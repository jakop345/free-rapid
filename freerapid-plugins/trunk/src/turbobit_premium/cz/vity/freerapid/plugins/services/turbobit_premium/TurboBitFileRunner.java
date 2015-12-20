package cz.vity.freerapid.plugins.services.turbobit_premium;

import cz.vity.freerapid.plugins.exceptions.*;
import cz.vity.freerapid.plugins.services.recaptcha.ReCaptcha;
import cz.vity.freerapid.plugins.webclient.AbstractRunner;
import cz.vity.freerapid.plugins.webclient.FileState;
import cz.vity.freerapid.plugins.webclient.MethodBuilder;
import cz.vity.freerapid.plugins.webclient.hoster.CaptchaSupport;
import cz.vity.freerapid.plugins.webclient.hoster.PremiumAccount;
import cz.vity.freerapid.plugins.webclient.utils.PlugUtils;
import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.HttpMethod;

import java.util.logging.Logger;
import java.util.regex.Matcher;

/**
 * Class which contains main code
 *
 * @author ntoskrnl, birchie
 */
class TurboBitFileRunner extends AbstractRunner {
    private final static Logger logger = Logger.getLogger(TurboBitFileRunner.class.getName());

    @Override
    public void runCheck() throws Exception {
        super.runCheck();
        addCookie(new Cookie(".turbobit.net", "user_lang", "en", "/", 86400, false));
        fileURL = checkFileURL(fileURL);
        final HttpMethod method = getGetMethod(fileURL);
        if (makeRedirectedRequest(method)) {
            checkProblems();
            checkNameAndSize();
        } else {
            checkProblems();
            throw new ServiceConnectionProblemException();
        }
    }

    private String checkFileURL(final String fileURL) throws ErrorDuringDownloadingException {
        final Matcher matcher = PlugUtils.matcher("http://(?:www\\.)?turbobit\\.net/(?:download/free/)?([a-z0-9]+)(?:\\.html?)?", fileURL);
        if (!matcher.find()) {
            throw new PluginImplementationException("Error parsing download link");
        }
        return "http://turbobit.net/" + matcher.group(1) + ".html";
    }

    private void checkNameAndSize() throws ErrorDuringDownloadingException {
        final Matcher matcher = getMatcherAgainstContent("<span class.*?>(.+?)</span>\\s*\\((.+?)\\)");
        if (matcher.find()) {
            httpFile.setFileName(matcher.group(1));
            httpFile.setFileSize(PlugUtils.getFileSizeFromString(matcher.group(2)));
        } else {
            PlugUtils.checkName(httpFile, getContentAsString(), "file-title\">", "<");
            PlugUtils.checkFileSize(httpFile, getContentAsString(), "file-size\">", "</");
        }
        httpFile.setFileState(FileState.CHECKED_AND_EXISTING);
    }

    @Override
    public void run() throws Exception {
        super.run();
        logger.info("Starting download in TASK " + fileURL);
        addCookie(new Cookie(".turbobit.net", "user_lang", "en", "/", 86400, false));
        fileURL = checkFileURL(fileURL);
        login();
        HttpMethod method = getGetMethod(fileURL);
        if (makeRedirectedRequest(method)) {
            checkProblems();
            checkNameAndSize();
            method = getMethodBuilder().setActionFromAHrefWhereATagContains("<b>Download").toGetMethod();
            if (!tryDownloadAndSaveFile(method)) {
                checkProblems();
                throw new ServiceConnectionProblemException("Error starting download");
            }
        } else {
            checkProblems();
            throw new ServiceConnectionProblemException();
        }
    }

    private void checkProblems() throws ErrorDuringDownloadingException {
        if (getContentAsString().contains("File was not found")
                || getContentAsString().contains("Probably it was deleted")
                || getContentAsString().contains("It could possibly be deleted")) {
            throw new URLNotAvailableAnymoreException("File not found");
        }
        if (getContentAsString().contains("The site is temporarily unavailable")) {
            throw new ServiceConnectionProblemException("The site is temporarily unavailable");
        }
        if (getContentAsString().contains("Limit of login attempts exceeded for your account")) {
            throw new ServiceConnectionProblemException("Limit of login attempts exceeded for your account -- To restore access follow the link has been sent to your email");
        }
        if (getContentAsString().contains("<u>Turbo Access</u> denied")) {
            throw new BadLoginException("Turbo Access denied");
        }
    }

    private void login() throws Exception {
        synchronized (TurboBitFileRunner.class) {
            TurboBitServiceImpl service = (TurboBitServiceImpl) getPluginService();
            PremiumAccount pa = service.getConfig();
            if (!pa.isSet()) {
                pa = service.showConfigDialog();
                if (pa == null || !pa.isSet()) {
                    throw new BadLoginException("No TurboBit account login information!");
                }
            }
            if (!isLoginStale(pa)) {
                logger.info("Logging in ... with cookies");
                setLoginCookies();
            } else {
                logger.info("Logging in");
                final MethodBuilder builder = getMethodBuilder()
                        .setAction("http://turbobit.net/user/login")
                        .setReferer("http://turbobit.net/login")
                        .setParameter("user[login]", pa.getUsername())
                        .setParameter("user[pass]", pa.getPassword())
                        .setParameter("user[captcha_type]", "")
                        .setParameter("user[captcha_subtype]", "")
                        .setParameter("user[memory]", "on")
                        .setParameter("user[submit]", "Sign in");
                if (!makeRedirectedRequest(builder.toPostMethod())) {
                    checkProblems();
                    throw new ServiceConnectionProblemException("Error posting login info");
                }
                // possible additional security step
                while (getContentAsString().contains("enter the captcha")) {
                    final Matcher m = getMatcherAgainstContent("api.recaptcha.net/noscript\\?k=([^\"]+)\"");
                    if (!m.find()) throw new PluginImplementationException("ReCaptcha key not found");
                    final String reCaptchaKey = m.group(1);
                    final ReCaptcha r = new ReCaptcha(reCaptchaKey, client);
                    final String captchaURL = r.getImageURL();
                    logger.info("Captcha URL " + captchaURL);

                    final CaptchaSupport captchaSupport = getCaptchaSupport();
                    final String captcha = captchaSupport.getCaptcha(captchaURL);
                    if (captcha == null) throw new CaptchaEntryInputMismatchException();
                    r.setRecognized(captcha);

                    builder.setParameter("user[captcha_type]", "recaptcha");
                    r.modifyResponseMethod(builder);
                    if (!makeRedirectedRequest(builder.toPostMethod())) {
                        checkProblems();
                        throw new ServiceConnectionProblemException("Error re-posting login info");
                    }
                    checkProblems();
                }
                if (getContentAsString().contains("Incorrect login or password")) {
                    throw new BadLoginException("Invalid TurboBit account login information!");
                }
                storeLoginCookies(pa);
            }
        }
    }

    private static PremiumAccount PA;
    private final static long MAX_AGE = 6 * 3600000;  //6 hours
    private static long created = 0;
    private static Cookie LOGGED_IN_COOKIE = new Cookie();
    private static Cookie LOGIN_ID_COOKIE = new Cookie();

    public boolean isLoginStale(final PremiumAccount pa) {
        return (System.currentTimeMillis() - created > MAX_AGE) || (!PA.getUsername().matches(pa.getUsername())) || (!PA.getPassword().matches(pa.getPassword()));
    }

    private void storeLoginCookies(final PremiumAccount pa) {
        created = System.currentTimeMillis();
        PA = pa;
        LOGGED_IN_COOKIE = getCookieByName("user_isloggedin");
        LOGIN_ID_COOKIE = getCookieByName("sid");
    }

    private void setLoginCookies() {
        addCookie(LOGGED_IN_COOKIE);
        addCookie(LOGIN_ID_COOKIE);
    }

}