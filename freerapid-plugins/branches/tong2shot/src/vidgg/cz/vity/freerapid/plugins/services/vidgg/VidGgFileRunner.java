package cz.vity.freerapid.plugins.services.vidgg;

import cz.vity.freerapid.plugins.exceptions.ErrorDuringDownloadingException;
import cz.vity.freerapid.plugins.exceptions.PluginImplementationException;
import cz.vity.freerapid.plugins.exceptions.ServiceConnectionProblemException;
import cz.vity.freerapid.plugins.exceptions.URLNotAvailableAnymoreException;
import cz.vity.freerapid.plugins.webclient.AbstractRunner;
import cz.vity.freerapid.plugins.webclient.FileState;
import cz.vity.freerapid.plugins.webclient.MethodBuilder;
import cz.vity.freerapid.plugins.webclient.utils.PlugUtils;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.util.URIUtil;

import java.util.logging.Logger;
import java.util.regex.Matcher;

/**
 * Class which contains main code
 *
 * @author birchie
 */
class VidGgFileRunner extends AbstractRunner {
    private final static Logger logger = Logger.getLogger(VidGgFileRunner.class.getName());

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
        final Matcher match = PlugUtils.matcher("id=\"video-title\">\\s*(?:<[^<>]+>)*(.+?)<", content);
        if (!match.find())
            throw new PluginImplementationException("File name not found");
        httpFile.setFileName(match.group(1).trim() + ".flv");
        httpFile.setFileState(FileState.CHECKED_AND_EXISTING);
    }

    @Override
    public void run() throws Exception {
        super.run();
        fileURL = fileURL.replaceFirst("vid\\.gg", "vidgg.to");
        logger.info("Starting download in TASK " + fileURL);
        final GetMethod method = getGetMethod(fileURL); //create GET request
        if (makeRedirectedRequest(method)) { //we make the main request
            final String content = getContentAsString();//check for response
            checkProblems();//check problems
            checkNameAndSize(content);//extract file name and size from the page

            String cid  = PlugUtils.getStringBetween(content, "cid=\"", "\"");
            String cid2;
            try { cid2 = PlugUtils.getStringBetween(content, "cid2=\"", "\"");
            } catch(Exception x) { cid2 = "undefined"; }
            String file = PlugUtils.getStringBetween(content, "file=\"", "\"");
            String key  = PlugUtils.getStringBetween(content, "filekey=\"", "\"");
            int errCount = 0;
            MethodBuilder builder = getMethodBuilder()
                    .setAction("http://www.vidgg.to/api/player.api.php")
                    .setParameter("numOfErrors", "" + errCount)
                    .setParameter("user", "undefined")
                    .setParameter("pass", "undefined")
                    .setParameter("file", file)
                    .setParameter("key", key)
                    .setParameter("cid", cid)
                    .setParameter("cid2", cid2)
                    .setParameter("cid3", "undefined")
                    .setAjax();
            boolean errLoop;
            do {
                errLoop = false;
                if (!makeRedirectedRequest(builder.toGetMethod())) {
                    checkProblems();
                    throw new ServiceConnectionProblemException();
                }
                final HttpMethod httpMethod = getMethodBuilder()
                        .setActionFromTextBetween("url=", "&title")
                        .toGetMethod();
                if (!tryDownloadAndSaveFile(httpMethod)) {
                    checkProblems();//if downloading failed
                    if (getContentAsString().contains("403 - Forbidden")) {
                        errLoop = true;
                        errCount++;
                        builder.setParameter("numOfErrors", "" + errCount);
                        builder.setParameter("errorCode", "404");
                        builder.setParameter("errorUrl", URIUtil.encodeAll(httpMethod.getURI().getURI()));
                    } else
                        throw new ServiceConnectionProblemException("Error starting download");//some unknown problem
                }
            } while (errLoop);
        } else {
            checkProblems();
            throw new ServiceConnectionProblemException();
        }
    }

    private void checkProblems() throws ErrorDuringDownloadingException {
        final String contentAsString = getContentAsString();
        if (contentAsString.contains("This file no longer exists") ||
                contentAsString.contains("The video file was removed")) {
            throw new URLNotAvailableAnymoreException("File not found"); //let to know user in FRD
        }

    }

}