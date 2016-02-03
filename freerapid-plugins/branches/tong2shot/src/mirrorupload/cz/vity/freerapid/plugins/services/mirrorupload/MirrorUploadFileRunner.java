package cz.vity.freerapid.plugins.services.mirrorupload;

import cz.vity.freerapid.plugins.exceptions.*;
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
class MirrorUploadFileRunner extends AbstractRunner {
    private final static Logger logger = Logger.getLogger(MirrorUploadFileRunner.class.getName());

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
        final Matcher match = PlugUtils.matcher(">(.+?) \\((.+?)\\)<", content);
        if (!match.find())
            throw new PluginImplementationException("File name/size not found");
        httpFile.setFileName("Extract Link(s) > " +match.group(1));
        httpFile.setFileSize(PlugUtils.getFileSizeFromString(match.group(2)));
        httpFile.setFileState(FileState.CHECKED_AND_EXISTING);
    }

    private URI getHostURI(String link) throws Exception {
        if (link.contains("/host-")) {
            final GetMethod method = getGetMethod(link);
            if (makeRedirectedRequest(method)) {
                return new URI(method.getURI().getURI());
            }
            return null;
        }
        return new URI(link);
    }

    @Override
    public void run() throws Exception {
        super.run();
        logger.info("Starting download in TASK " + fileURL);
        final GetMethod method = getGetMethod(fileURL); //create GET request
        if (makeRedirectedRequest(method)) { //we make the main request
            final String contentAsString = getContentAsString();//check for response
            checkProblems();//check problems
            checkNameAndSize(contentAsString);//extract file name and size from the page
            final HttpMethod httpMethod = getMethodBuilder()
                    .setActionFromFormWhereTagContains("download links", true)
                    .setReferer(fileURL).setAction(fileURL).toPostMethod();
            if (!makeRedirectedRequest(httpMethod)) {
                checkProblems();
                throw new ServiceConnectionProblemException();
            }
            checkProblems();

            if (fileURL.contains("/host-")) {
                URI link = getHostURI(fileURL);
                if (link == null)
                    throw new NotRecoverableDownloadException("Invalid redirection");
                httpFile.setNewURL(link.toURL());
                httpFile.setPluginID("");
                httpFile.setState(DownloadState.QUEUED);
                return;
            }

            final List<URI> list = new LinkedList<URI>();
            final Matcher match = getMatcherAgainstContent("target=\"_blank\"><b>(http[^<]+?)</b>");
            while (match.find()) {
                URI link = getHostURI(match.group(1));
                if (link != null)
                    list.add(link);
            }
            // add urls to queue
            if (list.isEmpty()) throw new PluginImplementationException("No links found");
            getPluginService().getPluginContext().getQueueSupport().addLinksToQueue(httpFile, list);
            httpFile.setFileName("Link(s) Extracted !");
            httpFile.setState(DownloadState.COMPLETED);
            httpFile.getProperties().put("removeCompleted", true);
        } else {
            checkProblems();
            throw new ServiceConnectionProblemException();
        }
    }

    private void checkProblems() throws ErrorDuringDownloadingException {
        final String contentAsString = getContentAsString();
        if (contentAsString.contains("File Not Found") || contentAsString.contains("Error 404") ||
                contentAsString.contains("you have just fallen on a page which does not exist")) {
            throw new URLNotAvailableAnymoreException("File not found"); //let to know user in FRD
        }
    }
}