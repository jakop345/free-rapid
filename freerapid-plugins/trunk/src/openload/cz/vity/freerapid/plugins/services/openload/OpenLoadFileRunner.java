package cz.vity.freerapid.plugins.services.openload;

import cz.vity.freerapid.plugins.exceptions.*;
import cz.vity.freerapid.plugins.webclient.AbstractRunner;
import cz.vity.freerapid.plugins.webclient.FileState;
import cz.vity.freerapid.plugins.webclient.utils.PlugUtils;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.logging.Logger;
import java.util.regex.Matcher;

/**
 * Class which contains main code
 *
 * @author birchie
 */
class OpenLoadFileRunner extends AbstractRunner {
    private final static Logger logger = Logger.getLogger(OpenLoadFileRunner.class.getName());
    private final static String OPENLOAD_API_URL = "https://api.openload.co/1";
    private final static String OPENLOAD_API_TICKET = "/file/dlticket?file=";
    private final static String OPENLOAD_API_DOWNLOAD = "/file/dl?file=%s&ticket=%s"; //&captcha_response=

    @Override
    public void runCheck() throws Exception { //this method validates file
        super.runCheck();
        fixUrl();
        final GetMethod getMethod = getGetMethod(fileURL);//make first request
        if (makeRedirectedRequest(getMethod)) {
            checkProblems();
            checkNameAndSize(getContentAsString());//ok let's extract file name and size from the page
        } else {
            checkProblems();
            throw new ServiceConnectionProblemException();
        }
    }

    private void fixUrl() {
        fileURL = fileURL.replaceFirst("/embed/", "/f/");
    }

    private void checkNameAndSize(String content) throws ErrorDuringDownloadingException {
        final Matcher match = PlugUtils.matcher("<h3[^<>]*title[^<>]*>(.+?)<", content);
        if (!match.find())
            PlugUtils.checkName(httpFile, content, "filename\">", "<");
        httpFile.setFileName(match.group(1).trim());
        PlugUtils.checkFileSize(httpFile, content, "File size:", "<");
        httpFile.setFileState(FileState.CHECKED_AND_EXISTING);
    }

    @Override
    public void run() throws Exception {
        super.run();
        fixUrl();
        logger.info("Starting download in TASK " + fileURL);
        final GetMethod method = getGetMethod(fileURL); //create GET request
        if (makeRedirectedRequest(method)) { //we make the main request
            final String contentAsString = getContentAsString();//check for response
            checkProblems();//check problems
            checkNameAndSize(contentAsString);//extract file name and size from the page

            final int wait = PlugUtils.getNumberBetween(contentAsString, "secondsdl = ", ";");
            if (wait > 0)
                downloadTask.sleep(1 + wait);
            String decodedText = "";
            int loop = 1;
            do {
                try {
                    HttpMethod httpMethod = getGetMethod(method.getURI().getURI());
                    // Workaround for 0.9u4 bug, can be removed when next version is released
                    httpMethod.removeRequestHeader("Accept-Encoding");
                    if (!makeRedirectedRequest(httpMethod)) {
                        checkProblems();
                        throw new ServiceConnectionProblemException();
                    }
                    checkProblems();
                    final Matcher match = PlugUtils.matcher("(?s)Download.+?\\s*<script type=\"text/javascript\">([^<]+?)<", getContentAsString());
                    if (!match.find())
                        throw new PluginImplementationException("Script not found");
                    String srcScript = match.group(1);
                    logger.info("Src script: " + srcScript);
                    decodedText = new AADecoder().decode(srcScript);
                    loop = -1;
                } catch (PluginImplementationException e) {
                    if (loop++ > 10) {
                        //throw new PluginImplementationException("JavaScript eval failed");
                        logger.warning("JavaScript eval failed - HTML Source : " + getContentAsString());
                        downloadWithAPI();
                        return;
                    }
                }
            } while (loop > 0);
            logger.info("Decoded text: " + decodedText);
            try {
                decodedText = decodeNewScript(decodedText);
            } catch (ScriptException e) {
                //throw new PluginImplementationException("JavaScript eval-2 failed");
                logger.warning("JavaScript eval-2 failed - HTML Source : " + getContentAsString());
                downloadWithAPI();
                return;
            }
            logger.info("Decoded-2 text: " + decodedText);
            final String dlUrl = decodedText;
            HttpMethod httpMethod = getGetMethod(dlUrl);
            if (!tryDownloadAndSaveFile(httpMethod)) {
                checkProblems();//if downloading failed
                throw new ServiceConnectionProblemException("Error starting download");//some unknown problem
            }
        } else {
            checkProblems();
            throw new ServiceConnectionProblemException();
        }
    }

    private void downloadWithAPI() throws Exception {
        logger.info("Downloading using API");
        final Matcher match = PlugUtils.matcher("/f/([^/]+)", fileURL);
        if (!match.find()) throw new InvalidURLOrServiceProblemException("Unable to find fileID in url");
        final String fileID = match.group(1);

        HttpMethod httpMethod = getMethodBuilder().setReferer(fileURL)
                .setAction(OPENLOAD_API_URL + OPENLOAD_API_TICKET + fileID)
                .setAjax().toGetMethod();
        if (!makeRedirectedRequest(httpMethod)) {
            checkAPIProblems();
            throw new ServiceConnectionProblemException();
        }
        checkAPIProblems();

        final String ticket = PlugUtils.getStringBetween(getContentAsString(), "\"ticket\":\"", "\",");
        final int wait = PlugUtils.getNumberBetween(getContentAsString(), "\"wait_time\":", ",");
        if (wait > 0)
            downloadTask.sleep(1 + wait);
        String captchaImg = "";
        if (getContentAsString().contains("\"captcha_url\":\"")) {
            captchaImg = PlugUtils.getStringBetween(getContentAsString(), "\"captcha_url\":\"", "\",").replace("\\/", "/");
        }
        do {
            httpMethod = getMethodBuilder().setReferer(fileURL)
                    .setAction(OPENLOAD_API_URL + String.format(OPENLOAD_API_DOWNLOAD, fileID, ticket))
                    .setParameter("captcha_response", doCaptcha(captchaImg))
                    .setAjax().toGetMethod();
            if (!makeRedirectedRequest(httpMethod)) {
                checkAPIProblems();
                throw new ServiceConnectionProblemException();
            }
        } while (getContentAsString().contains("Captcha not solved correctly"));
        checkAPIProblems();

        final String dlUrl = PlugUtils.getStringBetween(getContentAsString(), "\"url\":\"", "\",").replace("\\/", "/");
        httpMethod = getGetMethod(dlUrl);
        if (!tryDownloadAndSaveFile(httpMethod)) {
            checkProblems();//if downloading failed
            throw new ServiceConnectionProblemException("Error starting download");//some unknown problem
        }
    }

    private void checkProblems() throws ErrorDuringDownloadingException {
        final String content = getContentAsString();
        if (content.contains("We can't find the file you are looking for") ||
                content.contains("title>Error 404") || content.contains("class=\"text-404")) {
            throw new URLNotAvailableAnymoreException("File not found"); //let to know user in FRD
        }
    }

    private void checkAPIProblems() throws ErrorDuringDownloadingException {
        final String content = getContentAsString();
        if (!content.contains("\"status\":200,")) {
            final String msg = PlugUtils.getStringBetween(content, "\"msg\":\"", "\",");
            throw new ServiceConnectionProblemException(msg);
        }
    }

    private String doCaptcha(final String img) throws Exception {
        if (img.equals("")) return img;
        final String captchaTxt = getCaptchaSupport().getCaptcha(img);
        if (captchaTxt == null)
            throw new CaptchaEntryInputMismatchException();
        return captchaTxt;
    }

    private String decodeNewScript(final String encoded) throws Exception {
        final String functMatch = "\\{function ([^\\(]+?)([^\\}]+?\\})";
        Matcher match = PlugUtils.matcher(functMatch, encoded);
        if (!match.find()) throw new PluginImplementationException("Script Err 1");
        final String functScr = match.group(1) + " = function" + match.group(2) + ";";
        final String dataMatch = "}return (.+?)}";
        match = PlugUtils.matcher(dataMatch, encoded);
        if (!match.find()) throw new PluginImplementationException("Script Err 2");
        final String dataScr = match.group(1);
        final ScriptEngineManager manager = new ScriptEngineManager();
        final ScriptEngine engine = manager.getEngineByName("javascript");
        return (String) engine.eval(functScr + dataScr);
    }

}