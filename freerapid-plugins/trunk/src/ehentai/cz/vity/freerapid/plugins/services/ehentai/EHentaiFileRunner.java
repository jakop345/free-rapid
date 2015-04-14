package cz.vity.freerapid.plugins.services.ehentai;

import cz.vity.freerapid.plugins.exceptions.*;
import cz.vity.freerapid.plugins.webclient.AbstractRunner;
import cz.vity.freerapid.plugins.webclient.DownloadState;
import cz.vity.freerapid.plugins.webclient.FileState;
import cz.vity.freerapid.plugins.webclient.utils.PlugUtils;
import org.apache.commons.httpclient.methods.GetMethod;

import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;

/**
 * Class which contains main code
 *
 * @author birchie
 */
class EHentaiFileRunner extends AbstractRunner {
    private final static Logger logger = Logger.getLogger(EHentaiFileRunner.class.getName());

    @Override
    public void runCheck() throws Exception { //this method validates file
        super.runCheck();
        final GetMethod getMethod = getGetMethod(fileURL);//make first request
        if (makeRedirectedRequest(getMethod)) {
            checkProblems();
            checkNameAndSize(getContentAsString());//ok let's extract file name and size from the page
        } else {
            checkProblems();
            throw new ServiceConnectionProblemException();
        }
    }

    private void checkNameAndSize(String content) throws ErrorDuringDownloadingException {
        Matcher match = PlugUtils.matcher("<title>(.+?)( - E-Hentai.*?)?</title>", content);
        if (!match.find())
            throw new PluginImplementationException("File name not found");
        httpFile.setFileName(match.group(1).trim());
        if (fileURL.contains("/g/")) {
            httpFile.setFileName("Gallery >> " + httpFile.getFileName());
            PlugUtils.checkFileSize(httpFile, content, " of ", " images<");
        }
        else if (fileURL.contains("/s/")) {
            match = PlugUtils.matcher("<div>([^<>]+?)\\s*::.+?::\\s*(.+?)</div>", content);
            if (!match.find())
                throw new PluginImplementationException("File name & size not found");
            httpFile.setFileName(httpFile.getFileName() + " _ " + match.group(1).trim());
            httpFile.setFileSize(PlugUtils.getFileSizeFromString(match.group(2).trim()));
        }
        else
            throw new InvalidURLOrServiceProblemException("Unrecognised url");
        httpFile.setFileState(FileState.CHECKED_AND_EXISTING);
    }

    @Override
    public void run() throws Exception {
        super.run();
        logger.info("Starting download in TASK " + fileURL);
        final GetMethod method = getGetMethod(fileURL); //create GET request
        if (makeRedirectedRequest(method)) { //we make the main request
            checkProblems();//check problems
            checkNameAndSize(getContentAsString());//extract file name and size from the page
            if (fileURL.contains("/g/")) {
                final List<URI> list = new LinkedList<URI>();
                boolean more = true;
                do {
                    final Matcher match = PlugUtils.matcher("<a href=\"([^<>]+?)\"><img alt=", getContentAsString());
                    while (match.find()) {
                        final String imagePage = getMethodBuilder().setReferer(fileURL).setAction(match.group(1).trim()).getEscapedURI();
                        list.add(new URI(imagePage));
                    }
                    if (getContentAsString().contains("&gt;</a>")) {
                        final String nextPage = getMethodBuilder().setReferer(fileURL).setActionFromAHrefWhereATagContains("&gt;").getEscapedURI();
                        if (!makeRedirectedRequest(getGetMethod(nextPage))) {
                            throw new ServiceConnectionProblemException();
                        }
                    } else
                        more = false;
                } while (more);
                // add urls to queue
                if (list.isEmpty()) throw new PluginImplementationException("No links found");
                getPluginService().getPluginContext().getQueueSupport().addLinksToQueue(httpFile, list);
                httpFile.setFileName("Link(s) Extracted !");
                httpFile.setState(DownloadState.COMPLETED);
                httpFile.getProperties().put("removeCompleted", true);
            }
            else if (fileURL.contains("/s/")) {
                Matcher match = PlugUtils.matcher("</iframe><a[^<>]+?><img[^<>]*?src=\"(.+?)\"[^<>]*?></a><iframe", getContentAsString());
                if (!match.find())
                    throw new PluginImplementationException("Image not found");
                final String imageFile = getMethodBuilder().setReferer(fileURL).setAction(match.group(1).trim()).getEscapedURI();
                if (!tryDownloadAndSaveFile(getGetMethod(imageFile))) {
                    checkProblems();//if downloading failed
                    throw new ServiceConnectionProblemException("Error starting download");//some unknown problem
                }
            }
            else
                throw new InvalidURLOrServiceProblemException("Unrecognised url");
        } else {
            checkProblems();
            throw new ServiceConnectionProblemException();
        }
    }

    private void checkProblems() throws ErrorDuringDownloadingException {
        final String contentAsString = getContentAsString();
        if (contentAsString.contains("Invalid page") || contentAsString.contains("Gallery not found") ||
                contentAsString.contains("Key missing, or incorrect key provided") || contentAsString.contains("Not Found")) {
            throw new URLNotAvailableAnymoreException("File not found"); //let to know user in FRD
        }
    }

}