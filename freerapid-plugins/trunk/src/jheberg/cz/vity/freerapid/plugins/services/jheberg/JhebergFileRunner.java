package cz.vity.freerapid.plugins.services.jheberg;

import cz.vity.freerapid.plugins.exceptions.ErrorDuringDownloadingException;
import cz.vity.freerapid.plugins.exceptions.PluginImplementationException;
import cz.vity.freerapid.plugins.exceptions.ServiceConnectionProblemException;
import cz.vity.freerapid.plugins.exceptions.URLNotAvailableAnymoreException;
import cz.vity.freerapid.plugins.webclient.AbstractRunner;
import cz.vity.freerapid.plugins.webclient.DownloadState;
import cz.vity.freerapid.plugins.webclient.FileState;
import cz.vity.freerapid.plugins.webclient.utils.PlugUtils;
import org.apache.commons.httpclient.HttpMethod;
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
class JhebergFileRunner extends AbstractRunner {
    private final static Logger logger = Logger.getLogger(JhebergFileRunner.class.getName());

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
        if (fileURL.contains("/redirect/")) {
            httpFile.setFileName("Extract>> " + fileURL.split("/redirect/")[1]);
        } else {
            PlugUtils.checkName(httpFile, content, "\"filename\">", "</");
            httpFile.setFileName("Extract>> " + httpFile.getFileName());
            final Matcher match = PlugUtils.matcher("Size\\s*?:\\s*?<[^>]+?>(.+?)</", content);
            if (!match.find()) throw new PluginImplementationException("File size not found");
            httpFile.setFileSize(PlugUtils.getFileSizeFromString(match.group(1).trim()));
        }
        httpFile.setFileState(FileState.CHECKED_AND_EXISTING);
    }

    @Override
    public void run() throws Exception {
        super.run();
        logger.info("Starting download in TASK " + fileURL);
        List<URI> list = new LinkedList<URI>();
        if (fileURL.contains("/redirect/")) {
            list.add(stepRedirectLink(fileURL));
        } else {
            final GetMethod method = getGetMethod(fileURL); //create GET request
            if (!makeRedirectedRequest(method)) {
                checkProblems();
                throw new ServiceConnectionProblemException();
            }
            final String contentAsString = getContentAsString();//check for response
            checkProblems();//check problems
            checkNameAndSize(contentAsString);//extract file name and size from the page
            final HttpMethod aMethod = getMethodBuilder().setReferer(fileURL)
                    .setActionFromAHrefWhereATagContains("Download your file").toGetMethod();
            if (!makeRedirectedRequest(aMethod)) {
                checkProblems();
                throw new ServiceConnectionProblemException();
            }
            checkProblems();

            final Matcher match = PlugUtils.matcher("<a.*?href=\"(.+?)\".*?title=\"Download on", getContentAsString());
            while (match.find()) {
                list.add(stepRedirectLink(getMethodBuilder().setReferer(fileURL).setAction(match.group(1)).getEscapedURI()));
            }
        }
        if (list.isEmpty()) throw new PluginImplementationException("No links found");
        getPluginService().getPluginContext().getQueueSupport().addLinksToQueue(httpFile, list);
        httpFile.setFileName("Link(s) Extracted !");
        httpFile.setState(DownloadState.COMPLETED);
        httpFile.getProperties().put("removeCompleted", true);
    }

    private URI stepRedirectLink(final String link) throws Exception {
        if (link.contains("/redirect/")) {
            final GetMethod method = getGetMethod(link);
            if (makeRedirectedRequest(method)) {
                final HttpMethod hMethod = getMethodBuilder().setReferer(fileURL).setBaseURL("http://www.jheberg.net")
                        .setActionFromTextBetween(".post('", "'")
                        .setParameter("slug", PlugUtils.getStringBetween(getContentAsString(), "slug': '", "'"))
                        .setParameter("hoster", PlugUtils.getStringBetween(getContentAsString(), "hoster': '", "'"))
                        .setAjax().toPostMethod();
                if (!makeRedirectedRequest(hMethod)) {
                    throw new PluginImplementationException("Error getting redirected link");
                }
                return new URI(getMethodBuilder().setActionFromTextBetween("url\": \"", "\"").getEscapedURI());
            } else {
                checkProblems();
                throw new ServiceConnectionProblemException();
            }
        } else
            return new URI(link);
    }

    private void checkProblems() throws ErrorDuringDownloadingException {
        final String contentAsString = getContentAsString();
        if (contentAsString.contains("404</title>")) {
            throw new URLNotAvailableAnymoreException("File not found"); //let to know user in FRD
        }
    }

}