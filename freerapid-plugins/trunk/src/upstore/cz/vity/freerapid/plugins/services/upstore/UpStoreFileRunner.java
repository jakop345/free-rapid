package cz.vity.freerapid.plugins.services.upstore;

import cz.vity.freerapid.plugins.exceptions.*;
import cz.vity.freerapid.plugins.services.recaptcha.ReCaptcha;
import cz.vity.freerapid.plugins.webclient.AbstractRunner;
import cz.vity.freerapid.plugins.webclient.FileState;
import cz.vity.freerapid.plugins.webclient.MethodBuilder;
import cz.vity.freerapid.plugins.webclient.utils.PlugUtils;
import org.apache.commons.httpclient.Cookie;
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
class UpStoreFileRunner extends AbstractRunner {
    private final static Logger logger = Logger.getLogger(UpStoreFileRunner.class.getName());

    @Override
    public void runCheck() throws Exception { //this method validates file
        setCookieUrl();
        super.runCheck();
        doDDoSCheck();
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
        final Matcher match = PlugUtils.matcher("<h2[^>]*?>(.+?)</h2>\\s*?<div[^>]*?>\\s*?(.+?)\\s*?</div>", content);
        if (!match.find())
            throw new PluginImplementationException("File name/size not found");
        httpFile.setFileName(match.group(1));
        httpFile.setFileSize(PlugUtils.getFileSizeFromString(match.group(2)));
        httpFile.setFileState(FileState.CHECKED_AND_EXISTING);
    }

    private void setCookieUrl() {
        addCookie(new Cookie(".upstore.net", "lang", "en", "/", 86400, false));
        fileURL = fileURL.replaceFirst("upsto.re/", "upstore.net/");
        fileURL = fileURL.replaceFirst("https?://www\\.", "http://");
    }

    private void doDDoSCheck() throws Exception{
        makeRedirectedRequest(getGetMethod(fileURL));
        if (getContentAsString().contains("DDoS protection")) {
            logger.info("Found DDoS Protection");
            final Matcher matchT = PlugUtils.matcher("https?://(.+?)/", fileURL);
            if (!matchT.find()) throw new PluginImplementationException("DDoS Err 1");
            final String var_t = matchT.group(1);
            final Matcher matchScript = PlugUtils.matcher("(?s)var t,r,a,f,(.+?)a.value", getContentAsString());
            if (!matchScript.find()) throw new PluginImplementationException("DDoS Script not found");
            final String scriptA = matchScript.group(1).replaceFirst("(?s)t = document.create.+?'challenge-form'\\);", "");
            final String scriptB = PlugUtils.getStringBetween(getContentAsString(), "a.value = ", " + t.length");
            String result;
            try {
                ScriptEngineManager mgr = new ScriptEngineManager();
                ScriptEngine engine = mgr.getEngineByName("JavaScript");
                result = "" + engine.eval(scriptA + scriptB);
            } catch (Exception e) {
                throw new ErrorDuringDownloadingException(e.getMessage());
            }
            int var_aValue = Integer.parseInt(result.split("\\.")[0].split("\\.")[0].split("\\.")[0]);
            HttpMethod method = getMethodBuilder().setReferer(fileURL).setBaseURL("http://upstore.net/")
                    .setActionFromFormWhereActionContains("chk", true)
                    .setParameter("jschl_answer", "" + (var_aValue + var_t.length()) )
                    .toGetMethod();
            if (!makeRedirectedRequest(method)) {
                checkProblems();
                throw new ServiceConnectionProblemException("DDoS Protection avoidance failed");
            }

        }
    }

    @Override
    public void run() throws Exception {
        setCookieUrl();
        super.run();
        logger.info("Starting download in TASK " + fileURL);
        doDDoSCheck();
        final GetMethod method = getGetMethod(fileURL); //create GET request
        if (makeRedirectedRequest(method)) { //we make the main request
            final String contentAsString = getContentAsString();//check for response
            checkProblems();//check problems
            checkNameAndSize(contentAsString);//extract file name and size from the page

            final HttpMethod pMethod = getMethodBuilder()
                    .setActionFromFormWhereTagContains("Slow", true)
                    .setReferer(fileURL).setAction(fileURL)
                    .toPostMethod();
            if (!makeRedirectedRequest(pMethod)) {
                checkProblems();
                throw new ServiceConnectionProblemException();
            }
            checkProblems();
            do {
                final int wait = PlugUtils.getNumberBetween(getContentAsString(), "var sec = ", ", ");
                final int msec = PlugUtils.getNumberBetween(getContentAsString(), "setInterval(countDown,", ");");
                downloadTask.sleep((wait * msec) / 1000);
                final HttpMethod cMethod = doCaptcha(getMethodBuilder()
                        .setActionFromFormWhereTagContains("Get download", true)
                        .setReferer(fileURL).setAction(fileURL)
                        .setParameter("kpw", "spam")
                        .setParameter("antispam", "spam")
                ).toPostMethod();
                if (!makeRedirectedRequest(cMethod)) {
                    checkProblems();
                    throw new ServiceConnectionProblemException();
                }
                checkProblems();
            } while (getContentAsString().contains("Wrong captcha protection code"));

            final HttpMethod httpMethod = getMethodBuilder()
                    .setActionFromAHrefWhereATagContains("Download file")
                    .setReferer(fileURL)
                    .toGetMethod();
            if (!tryDownloadAndSaveFile(httpMethod)) {
                checkProblems();//if downloading failed
                throw new ServiceConnectionProblemException("Error starting download");//some unknown problem
            }
        } else {
            checkProblems();
            throw new ServiceConnectionProblemException();
        }
    }

    private void checkProblems() throws ErrorDuringDownloadingException {
        final String content = getContentAsString();
        if (content.contains("File not found") ||
                content.contains("File was deleted by owner")) {
            throw new URLNotAvailableAnymoreException("File not found"); //let to know user in FRD
        }
        if (content.contains("This file is available only for Premium users")) {
            throw new NotRecoverableDownloadException("File is only for Premium users"); //let to know user in FRD
        }
        if (content.contains("already downloaded another file recently from your IP")) {
            final Matcher match = PlugUtils.matcher("Please wait (.+?)\\s(.+?) before", content);
            int wait = 300;
            if (match.find()) {
                wait = Integer.parseInt(match.group(1));
                if (match.group(2).trim().matches("minute(s)?"))
                    wait = wait * 60;
            }
            throw new YouHaveToWaitException("You need to wait between downloads", wait);
        }
    }

    private MethodBuilder doCaptcha(MethodBuilder methodBuilder) throws Exception {
        final String reCaptchaKey = PlugUtils.getStringBetween(getContentAsString(), "Recaptcha.create('", "',");
        final ReCaptcha r = new ReCaptcha(reCaptchaKey, client);
        final String captcha = getCaptchaSupport().getCaptcha(r.getImageURL());
        if (captcha == null) {
            throw new CaptchaEntryInputMismatchException();
        }
        r.setRecognized(captcha);
        r.modifyResponseMethod(methodBuilder);
        return methodBuilder;
    }
}