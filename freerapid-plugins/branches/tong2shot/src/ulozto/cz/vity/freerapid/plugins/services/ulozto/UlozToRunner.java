package cz.vity.freerapid.plugins.services.ulozto;

import cz.vity.freerapid.plugins.exceptions.*;
import cz.vity.freerapid.plugins.services.ulozto_captcha.SoundReader;
import cz.vity.freerapid.plugins.webclient.AbstractRunner;
import cz.vity.freerapid.plugins.webclient.DownloadClientConsts;
import cz.vity.freerapid.plugins.webclient.FileState;
import cz.vity.freerapid.plugins.webclient.MethodBuilder;
import cz.vity.freerapid.plugins.webclient.hoster.CaptchaSupport;
import cz.vity.freerapid.plugins.webclient.utils.PlugUtils;
import cz.vity.freerapid.utilities.LogUtils;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Ladislav Vitasek
 * @author Ludek Zika
 * @author JPEXS (captcha)
 * @author birchie
 * @author tong2shot
 */
class UlozToRunner extends AbstractRunner {
    private final static Logger logger = Logger.getLogger(UlozToRunner.class.getName());
    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:33.0) Gecko/20100101 Firefox/33.0";
    private int captchaCount = 0;
    private final Random random = new Random();

    private void ageCheck(String content) throws Exception {
        if (content.contains("confirmContent")) { //eroticky obsah vyzaduje potvruemo
            final PostMethod confirmMethod = (PostMethod) getMethodBuilder()
                    .setActionFromFormWhereTagContains("frm-porn", true)
                    .setReferer(fileURL)
                    .removeParameter("disagree")
                    .toPostMethod();
            makeRedirectedRequest(confirmMethod);
            if (getContentAsString().contains("confirmContent")) {
                throw new PluginImplementationException("Cannot confirm age");
            }
            fileURL = fileURL.replaceFirst("^http://uloz\\.to", "http://pornfile.cz"); //porn redirected to pornfile, explicit because of POST
        }
    }

    private boolean isPasswordProtected() {
        return getContentAsString().contains("passwordProtectedFile");
    }

    private void passwordProtectedCheck() throws Exception {
        while (getContentAsString().contains("passwordProtectedFile")) {
            final String password = getDialogSupport().askForPassword("Ulozto password protected file");
            if (password == null) {
                throw new PluginImplementationException("This file is secured with a password");
            }
            HttpMethod hm = getMethodBuilder()
                    .setActionFromFormWhereTagContains("passwordProtected", true)
                    .setReferer(fileURL)
                    .setParameter("password", password)
                    .toPostMethod();
            if (!makeRedirectedRequest(hm)) {
                checkProblems();
                throw new ServiceConnectionProblemException();
            }
        }
    }

    @Override
    public void runCheck() throws Exception {
        super.runCheck();
        checkURL();
        setClientParameter(DownloadClientConsts.USER_AGENT, USER_AGENT);
        if (isFolder(fileURL)) {
            return;
        }
        final GetMethod getMethod = getGetMethod(fileURL);
        if (makeRedirectedRequest(getMethod)) {
            checkProblems();
            if (!isPasswordProtected()) {
                ageCheck(getContentAsString());
                checkNameAndSize(getContentAsString());
            }
        } else {
            checkProblems();
            throw new ServiceConnectionProblemException();
        }
    }

    @Override
    public void run() throws Exception {
        super.run();
        checkURL();
        setClientParameter(DownloadClientConsts.USER_AGENT, USER_AGENT);
        final GetMethod getMethod = getGetMethod(fileURL);
        if (makeRedirectedRequest(getMethod)) {
            checkProblems();
            passwordProtectedCheck();
            fileURL = getMethod.getURI().toString(); // '/m/' folder redirected to '/soubory/'
            ageCheck(getContentAsString());
            if (isFolder(fileURL)) {
                parseFolder();
            } else {
                checkNameAndSize(getContentAsString());
                captchaCount = 0;
                HttpMethod method = null;
                while (getContentAsString().contains("captchaContainer") || getContentAsString().contains("?captcha=no")) {
                    //client.getHTTPClient().getParams().setIntParameter(HttpClientParams.MAX_REDIRECTS, 8);
                    method = stepCaptcha();
                    downloadTask.sleep(new Random().nextInt(4) + new Random().nextInt(3));
                    makeRequest(method);
                    if ((method.getStatusCode() == 302) || (method.getStatusCode() == 303)) {
                        String nextUrl = method.getResponseHeader("Location").getValue();
                        method = getMethodBuilder().setReferer(fileURL).setAction(nextUrl).toGetMethod();
                        //downloadTask.sleep(new Random().nextInt(15) + new Random().nextInt(3));
                        logger.info("Download file location : " + nextUrl);
                        break;
                    }
                    checkProblems();
                }
                setFileStreamContentTypes("text/plain", "text/texmacs");
                if (!tryDownloadAndSaveFile(method)) {
                    checkProblems();
                    throw new ServiceConnectionProblemException("Error starting download");
                }
            }
        } else {
            checkProblems();
            throw new ServiceConnectionProblemException();
        }
    }

    private boolean isPornFile() {
        return fileURL.contains("pornfile.uloz") || fileURL.contains("pornfile.cz");
    }

    private void checkURL() {
        fileURL = fileURL.replaceFirst("(ulozto\\.net|ulozto\\.cz|ulozto\\.sk)", "uloz.to").replaceFirst("http://(m|www)\\.uloz\\.to", "http://uloz.to");
    }

    @Override
    protected String getBaseURL() {
        return !isPornFile() ? "http://uloz.to" : "http://pornfile.cz";
    }

    private void checkNameAndSize(String content) throws Exception {
        if (!content.contains("uloz.to")) {
            logger.warning(getContentAsString());
            throw new InvalidURLOrServiceProblemException("Invalid URL or unindentified service");
        }
        if (getContentAsString().contains("soubor nebyl nalezen")) {
            throw new URLNotAvailableAnymoreException("Pozadovany soubor nebyl nalezen");
        }
        PlugUtils.checkName(httpFile, content, "<title>", !isPornFile() ? " | Ulo" : " | PORNfile");
        String size;
        try {
            //tady nema byt id=, jinak to prestane fungovat
            size = PlugUtils.getStringBetween(content, "<span id=\"fileSize\">", "</span>");
            if (size.contains("|")) {
                size = size.substring(size.indexOf("|") + 1).trim();
            }
            httpFile.setFileSize(PlugUtils.getFileSizeFromString(size));
        } catch (PluginImplementationException ex) {
            //u online videi neni velikost
            //throw new PluginImplementationException("File size not found");
        }
        httpFile.setFileState(FileState.CHECKED_AND_EXISTING);
    }

    private HttpMethod stepCaptcha() throws Exception {
        if (getContentAsString().contains("Please click here to continue")) {
            logger.info("Using HTML redirect");
            return getMethodBuilder().setReferer(fileURL).setActionFromAHrefWhereATagContains("Please click here to continue").toGetMethod();
        }
        final MethodBuilder sendForm = getMethodBuilder()
                .setReferer(fileURL)
                .setActionFromFormWhereTagContains("freeDownloadForm", true);

        HttpMethod method = getMethodBuilder()
                .setReferer(fileURL)
                .setAjax()
                .setAction("/reloadXapca.php?rnd=" + System.currentTimeMillis())
                .toGetMethod();
        if (!makeRedirectedRequest(method)) {
            checkProblems();
            throw new PluginImplementationException("Error requesting captcha");
        }
        checkProblems();

        Matcher matcher;
        String captchaImg;
        String timestamp;
        String salt;
        String hash;
        try {
            captchaImg = PlugUtils.getStringBetween(getContentAsString(), "\"image\":\"", "\"").replace("\\/", "/").replaceFirst("^//", "http://");
        } catch (PluginImplementationException e) {
            throw new PluginImplementationException("Captcha image not found");
        }
        matcher = getMatcherAgainstContent("\"timestamp\":(\\d+)");
        if (!matcher.find()) {
            throw new PluginImplementationException("Timestamp not found");
        }
        timestamp = matcher.group(1);
        try {
            hash = PlugUtils.getStringBetween(getContentAsString(), "\"hash\":\"", "\"");
        } catch (PluginImplementationException e) {
            throw new PluginImplementationException("Hash not found");
        }
        matcher = getMatcherAgainstContent("\"salt\":(\\d+)");
        if (!matcher.find()) {
            throw new PluginImplementationException("Salt not found");
        }
        salt = matcher.group(1);

        sendForm.setParameter("timestamp", timestamp)
                .setParameter("hash", hash)
                .setParameter("salt", salt);

        final String captchaSnd = captchaImg.replace("image.gif", "sound.wav");
        final CaptchaSupport captchaSupport = getCaptchaSupport();
        String captchaTxt;
        //captchaCount = 9; //for test purpose
        if (captchaCount++ < 8) {
            logger.info("captcha url: " + captchaSnd);
            final SoundReader captchaReader = new SoundReader(); //load fingerprint from file to test, don't forget to change it back
            final HttpMethod methodSound = getMethodBuilder()
                    .setReferer(fileURL)
                    .setAction(captchaSnd)
                    .toGetMethod();
            try {
                captchaTxt = captchaReader.parse(client.makeRequestForFile(methodSound));
                logger.info("Auto recog attempt : " + captchaCount);
                logger.info("Captcha recognized : " + captchaTxt);
            } catch (Exception e) {
                LogUtils.processException(logger, e);
                final StringBuilder captchaTxtBuilder = new StringBuilder(4);
                for (int i = 0; i < 4; i++) {
                    captchaTxtBuilder.append(Character.toChars(random.nextInt(26) + 97)); //throw random chars
                }
                captchaTxt = captchaTxtBuilder.toString();
                logger.info("Generated random captcha : " + captchaTxt);
            } finally {
                methodSound.releaseConnection();
            }
        } else {
            captchaTxt = captchaSupport.getCaptcha(captchaImg);
        }
        if (captchaTxt == null) {
            throw new CaptchaEntryInputMismatchException();
        }
        sendForm.setParameter("captcha_value", captchaTxt);
        return sendForm.toPostMethod();
    }

    //"Prekro�en pocet FREE slotu, pouzijte VIP download
    private void checkProblems() throws Exception {
        final String content = getContentAsString();
        if (content.contains("Soubor byl sma") || content.contains("Soubor byl zak")) {
            throw new URLNotAvailableAnymoreException("Soubor byl smazan");
        }
        if (content.contains("soubor nebyl nalezen")) {
            throw new URLNotAvailableAnymoreException("Pozadovany soubor nebyl nalezen");
        }
        if (content.contains("Stránka nenalezena")) {
            throw new URLNotAvailableAnymoreException("Stránka nenalezena - Page not found");
        }
        if (content.contains("stahovat pouze jeden soubor")) {
            throw new ServiceConnectionProblemException("Muzete stahovat pouze jeden soubor naraz");
        }
        if (content.contains("Majitel souboru si nepřeje soubor zveřejnit a označil jej jako soukromý")) {
            throw new NotRecoverableDownloadException("Majitel souboru si nepřeje soubor zveřejnit a označil jej jako soukromý");
        }
        if (content.contains("et FREE slot") && content.contains("ijte VIP download")) {
            logger.warning(getContentAsString());
            throw new YouHaveToWaitException("Nejsou dostupne FREE sloty", 40);
        }
    }

    private boolean isFolder(String fileUrl) {
        return fileUrl.contains("uloz.to/soubory/") || fileUrl.contains("uloz.to/m/");
    }

    private void parseFolder() throws Exception {
        Pattern pattern = Pattern.compile("<script>(?s)(.*?var kn\\s*?=.+?)</script>");
        Matcher matcher = pattern.matcher(getContentAsString());
        if (!matcher.find()) {
            HttpMethod getMethod = getMethodBuilder().setReferer(fileURL).setAction(fileURL).setAjax().toGetMethod();
            if (!makeRedirectedRequest(getMethod)) {
                checkProblems();
                throw new ServiceConnectionProblemException();
            }
            checkProblems();
            matcher = pattern.matcher(getContentAsString());
            if (!matcher.find()) {
                throw new PluginImplementationException("Error getting 'script vars'");
            }
        }
        String scriptVars = matcher.group(1);
        ScriptEngine engine = initScriptEngine();
        try {
            engine.eval(scriptVars);
        } catch (ScriptException e) {
            throw new PluginImplementationException("Script execution failed (1)", e);
        }
        List<URI> uriList = new LinkedList<URI>();
        matcher = PlugUtils.matcher("data-icon=\"([a-z0-9]+)\"", getContentAsString());
        while (matcher.find()) {
            String dataIcon = matcher.group(1);
            try {
                engine.eval("var decrypted = ad.decrypt(kn[\"" + dataIcon + "\"]);");
            } catch (ScriptException e) {
                throw new PluginImplementationException("Script execution failed (2)", e);
            }
            String decrypted = (String) engine.get("decrypted");
            Matcher correspondMatcher = PlugUtils.matcher("<a [^<>]*?(?:data-icon|href)\\s*?=\\s*?\"([a-z0-9]+?|/soubory/[^\"]+?)\"[^<>]*?>", decrypted);
            if (!correspondMatcher.find()) {
                logger.warning(decrypted);
                throw new PluginImplementationException("Error getting corresponding data-icon for: " + dataIcon);
            }
            String matchedStr = correspondMatcher.group(1);
            if (!matchedStr.contains("/soubory/")) { //file
                try {
                    engine.eval("var decrypted = ad.decrypt(kn[\"" + matchedStr + "\"]);");
                } catch (ScriptException e) {
                    throw new PluginImplementationException("Script execution failed (3)", e);
                }
                decrypted = (String) engine.get("decrypted");
            } else { //folder
                decrypted = matchedStr;
            }
            uriList.add(new URI(getBaseURL() + decrypted.replace("\u0000", "")));
        }
        if (uriList.isEmpty()) {
            throw new PluginImplementationException("No links found");
        }
        getPluginService().getPluginContext().getQueueSupport().addLinksToQueue(httpFile, uriList);
        httpFile.getProperties().put("removeCompleted", true);
        logger.info(uriList.size() + " links added");
    }


    private ScriptEngine initScriptEngine() throws Exception {
        final ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");
        if (engine == null) {
            throw new RuntimeException("JavaScript engine not found");
        }
        final Reader reader = new InputStreamReader(UlozToRunner.class.getResourceAsStream("/resources/crypto.js"), "UTF-8");
        try {
            engine.eval(reader);
        } finally {
            reader.close();
        }
        return engine;
    }
}
