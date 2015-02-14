package cz.vity.freerapid.plugins.services.datafile;

import cz.vity.freerapid.plugins.exceptions.*;
import cz.vity.freerapid.plugins.services.recaptcha.ReCaptcha;
import cz.vity.freerapid.plugins.webclient.AbstractRunner;
import cz.vity.freerapid.plugins.webclient.DownloadClientConsts;
import cz.vity.freerapid.plugins.webclient.FileState;
import cz.vity.freerapid.plugins.webclient.MethodBuilder;
import cz.vity.freerapid.plugins.webclient.utils.HttpUtils;
import cz.vity.freerapid.plugins.webclient.utils.PlugUtils;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.util.logging.Logger;
import java.util.regex.Matcher;

/**
 * Class which contains main code
 *
 * @author birchie
 */
class DataFileFileRunner extends AbstractRunner {
    private final static Logger logger = Logger.getLogger(DataFileFileRunner.class.getName());

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
        PlugUtils.checkName(httpFile, content, "file-name\">", "</div>");
        PlugUtils.checkFileSize(httpFile, content, "Filesize: <span class=\"lime\">", "</span>");
        httpFile.setFileState(FileState.CHECKED_AND_EXISTING);
    }

    @Override
    public void run() throws Exception {
        super.run();
        logger.info("Starting download in TASK " + fileURL);
        final GetMethod method = getGetMethod(fileURL); //create GET request
        if (makeRedirectedRequest(method)) { //we make the main request
            final String content = getContentAsString();//check for response
            checkProblems();//check problems
            checkNameAndSize(content);//extract file name and size from the page

            MethodBuilder builder = getMethodBuilder()
                    .setAjax()
                    .setAction("/files/ajax.html")
                    .setReferer(fileURL)
                    .setParameter("doaction", "validateCaptcha")
                    .setParameter("fileid", PlugUtils.getStringBetween(content, "getFileDownloadLink('", "'"));
            boolean captchaLoop = false;
            do {
                if (captchaLoop) {
                    if (!makeRedirectedRequest(method)) {
                        checkProblems();
                        throw new ServiceConnectionProblemException();
                    }
                    checkProblems();
                }
                final int waitTime = 1 + PlugUtils.getNumberBetween(getContentAsString(), "counter.contdownTimer('", "'");
                if (waitTime > 123) throw new YouHaveToWaitException("Wait between downloads", waitTime);
                final long startTime = System.currentTimeMillis();
                doCaptcha(builder);
                final long endTime = System.currentTimeMillis();
                downloadTask.sleep(waitTime - (int) ((endTime - startTime) / 1000));
                setFileStreamContentTypes(new String[0], new String[]{"application/x-www-form-urlencoded"});
                if (!makeRedirectedRequest(builder.toPostMethod())) {
                    checkProblems();
                    throw new ServiceConnectionProblemException();
                }
                checkProblems();
                captchaLoop = true;
            } while (!getContentAsString().contains("success\":1"));

            builder.setParameter("doaction", "getFileDownloadLink")
                    .setParameter("token", PlugUtils.getStringBetween(getContentAsString(), "token\":\"", "\""))
                    .setParameter("fileid", PlugUtils.getStringBetween(content, "getFileDownloadLink('", "'"));
            if (!makeRedirectedRequest(builder.toPostMethod())) {
                checkProblems();
                throw new ServiceConnectionProblemException();
            }
            checkProblems();
            if (getContentAsString().contains("success\":0")) {
                throw new NotRecoverableDownloadException("Plugin Broken: "+PlugUtils.getStringBetween(getContentAsString(), "msg\":\"", "\""));
            }
            final String url = PlugUtils.getStringBetween(getContentAsString(), "link\":\"", "\"").replace("\\/", "/");
            client.getHTTPClient().getParams().setBooleanParameter(DownloadClientConsts.DONT_USE_HEADER_FILENAME, true);
            final HttpMethod httpMethod = getGetMethod(url);
            if (!tryDownloadAndSaveFile(httpMethod)) {
                checkProblems();//if downloading failed
                throw new ServiceConnectionProblemException("Error starting download");//some unknown problem
            }
            // correctly get Header file name to avoid truncation of long file names
            String name = HttpUtils.getFileName(httpMethod);
            httpFile.setFileName(name.substring(0, name.indexOf("\"")));
        } else {
            checkProblems();
            throw new ServiceConnectionProblemException();
        }
    }

    private void checkProblems() throws Exception {
        final String contentAsString = getContentAsString();
        if (contentAsString.contains("File not found") || contentAsString.contains("ErrorCode 3: This file was deleted")) {
            throw new URLNotAvailableAnymoreException("File not found"); //let to know user in FRD
        }
        if (contentAsString.contains("This file can be downloaded only users with<br />Premium account")) {
            throw new NotRecoverableDownloadException("This file can be downloaded by premium users"); //let to know user in FRD
        }
        if (contentAsString.contains("You are downloading another file at this moment") ||
                contentAsString.contains("You can not download more than one file at a time")) {
            throw new YouHaveToWaitException("You can not download more than one file at a time", 5*60);
        }

        if (contentAsString.contains("\"JavaScript\">s=")) {
            final Matcher match = PlugUtils.matcher("<script language=\"JavaScript\">s=.+?eval\\(m\\);(.+?)</script>", contentAsString);
            if (match.find()) {
                final String newUrl = decodeRedirectUrl(match.group(1));
                if (!makeRedirectedRequest(getMethodBuilder().setReferer(fileURL).setAction(newUrl).toGetMethod())) {
                    checkProblems();
                    throw new ServiceConnectionProblemException();
                }
                checkProblems();
            }
        }
    }

    private String decodeRedirectUrl(String script) throws PluginImplementationException {
        try {
            final Matcher match = PlugUtils.matcher("eval\\((atob\\(\"(.+?)\"\\))\\);", script);
            if (!match.find()) throw new PluginImplementationException("JS evaluation error 1");
            final String replace = match.group(1);
            final String encBase64 = match.group(2);
            final String notBase64 = new String(Base64.decodeBase64(encBase64));
            script = script.replace(replace, "notBase64");
            script = script.replace("window.location.href", "OUTPUT");
            ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");
            engine.put("notBase64", notBase64);
            engine.eval(script);
            return engine.get("OUTPUT").toString();
        } catch (Exception e) {
            throw new PluginImplementationException("JS evaluation error 2 " + e.getLocalizedMessage());
        }
    }

    private void doCaptcha(MethodBuilder methodBuilder) throws Exception {
        final String reCaptchaKey = PlugUtils.getStringBetween(getContentAsString(), "recaptcha/api/challenge?k=", "\"");
        final ReCaptcha r = new ReCaptcha(reCaptchaKey, client);
        final String captcha = getCaptchaSupport().getCaptcha(r.getImageURL());
        if (captcha == null) {
            throw new CaptchaEntryInputMismatchException();
        }
        r.setRecognized(captcha);
        r.modifyResponseMethod(methodBuilder);
    }

}