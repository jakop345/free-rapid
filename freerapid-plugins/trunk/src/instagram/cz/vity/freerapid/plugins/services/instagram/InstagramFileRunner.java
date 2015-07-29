package cz.vity.freerapid.plugins.services.instagram;

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
class InstagramFileRunner extends AbstractRunner {
    private final static Logger logger = Logger.getLogger(InstagramFileRunner.class.getName());

    @Override
    public void runCheck() throws Exception { //this method validates file
        super.runCheck();
        final GetMethod getMethod = getGetMethod(fileURL);//make first request
        if (makeRedirectedRequest(getMethod)) {
            checkProblems();
            checkNameAndSize(getDownloadLink(getContentAsString()));//ok let's extract file name and size from the page
        } else {
            checkProblems();
            throw new ServiceConnectionProblemException();
        }
    }

    private void checkNameAndSize(String downloadLink) throws ErrorDuringDownloadingException {
        if (fileURL.contains("/p/")) {
            String filename;
            try {
                filename = PlugUtils.suggestFilename(downloadLink);
            } catch (PluginImplementationException e) {
                throw new PluginImplementationException("File name not found");
            }
            logger.info("File name: " + filename);
            httpFile.setFileName(filename);
        } else {
            final String content = getContentAsString();
            final Matcher match = PlugUtils.matcher("content=\"(.+?)\\s.\\sInstagram", content);
            if (!match.find())
                throw new PluginImplementationException("User's name not found");
            httpFile.setFileName("User: " + match.group(1));
            PlugUtils.checkFileSize(httpFile, content, "media\":{\"count\":", ",");
        }
        httpFile.setFileState(FileState.CHECKED_AND_EXISTING);
    }

    @Override
    public void run() throws Exception {
        super.run();
        logger.info("Starting download in TASK " + fileURL);
        final GetMethod method = getGetMethod(fileURL); //create GET request
        if (makeRedirectedRequest(method)) { //we make the main request
            checkProblems();//check problems
            if (fileURL.contains("/p/")) {  // post
                String downloadLink = getDownloadLink(getContentAsString());
                checkNameAndSize(downloadLink);
                if (!tryDownloadAndSaveFile(getGetMethod(downloadLink))) {
                    checkProblems();//if downloading failed
                    throw new ServiceConnectionProblemException("Error starting download");//some unknown problem
                }
            } else {  // user
                List<URI> list = new LinkedList<URI>();
                final String userID = PlugUtils.getStringBetween(getContentAsString(), "owner\":{\"id\":\"", "\"");
                final String csrfToken = PlugUtils.getStringBetween(getContentAsString(), "\"csrf_token\":\"", "\"");
                boolean nextPage;
                do {
                    nextPage = false;
                    String content = getContentAsString();
                    final Matcher match = PlugUtils.matcher("\"code\":\"(.+?)\"", content);
                    while (match.find()) {
                        list.add(new URI("https://instagram.com/p/" + match.group(1)));
                    }
                    if (content.contains("\"has_next_page\":true")) {
                        nextPage = true;
                        final String lastPost = PlugUtils.getStringBetween(content, "end_cursor\":\"", "\"");
                        final HttpMethod nextPageMethod = getMethodBuilder(content).setReferer(fileURL)
                                .setAction("https://instagram.com/query/")
                                .setParameter("q", "ig_user(" + userID + ") { media.after(" + lastPost + ", 24) {\n  count,\n  nodes {\n    caption,\n    code,\n    comments {\n      count\n    },\n    date,\n    display_src,\n    id,\n    is_video,\n    likes {\n      count\n    },\n    owner {\n      id\n    }\n  },\n  page_info\n}\n }")
                                .setParameter("ref", "users::show")
                                .setAjax().toPostMethod();
                        nextPageMethod.setRequestHeader("X-CSRFToken", csrfToken);
                        nextPageMethod.setRequestHeader("X-Instagram-AJAX", "1");
                        if (!makeRedirectedRequest(nextPageMethod)) {
                            checkProblems();
                            throw new ServiceConnectionProblemException();
                        }
                    }
                    logger.info(list.size() + " Links found");
                } while (nextPage);
                if (list.isEmpty()) throw new PluginImplementationException("No posts found");
                getPluginService().getPluginContext().getQueueSupport().addLinksToQueue(httpFile, list);
                httpFile.setFileName("Post(s) Extracted !");
                httpFile.setState(DownloadState.COMPLETED);
                httpFile.getProperties().put("removeCompleted", true);
            }
        } else {
            checkProblems();
            throw new ServiceConnectionProblemException();
        }
    }

    private void checkProblems() throws ErrorDuringDownloadingException {
        final String contentAsString = getContentAsString();
        if (contentAsString.contains("Page Not Found")) {
            throw new URLNotAvailableAnymoreException("File not found"); //let to know user in FRD
        }
    }

    private String getDownloadLink(String content) throws PluginImplementationException {
        if (content.contains("\"video_url\"")) {
            return PlugUtils.getStringBetween(content, "\"video_url\":\"", "\"").replace("\\/", "/");
        } else if (content.contains("\"display_src\"")) {
            return PlugUtils.getStringBetween(content, "\"display_src\":\"", "\"").replace("\\/", "/");
        } else {
            throw new PluginImplementationException("Download link not found");
        }
    }

}