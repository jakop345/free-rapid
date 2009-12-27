package cz.vity.freerapid.plugins.services.shareonline;

import cz.vity.freerapid.plugins.exceptions.*;
import cz.vity.freerapid.plugins.webclient.AbstractRunner;
import cz.vity.freerapid.plugins.webclient.FileState;
import cz.vity.freerapid.plugins.webclient.hoster.CaptchaSupport;
import cz.vity.freerapid.plugins.webclient.utils.PlugUtils;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.logging.Logger;
import java.util.regex.Matcher;

/**
 * @author Ladislav Vitasek, Ludek Zika
 */
class ShareonlineRunner extends AbstractRunner {
    private final static Logger logger = Logger.getLogger(ShareonlineRunner.class.getName());
    private ServicePluginContext context;
    private String initURL;
    private int captchaCounter = 1, captchaMax = 5;

    public ShareonlineRunner(ServicePluginContext context) {
        super();
        this.context = context;
    }

    public void runCheck() throws Exception {
        super.runCheck();
        final GetMethod getMethod = getGetMethod(fileURL);
        if (makeRequest(getMethod)) {
            checkNameAndSize(getContentAsString());
        } else
            throw new PluginImplementationException();
    }

    public void run() throws Exception {
        super.run();
        client = downloadTask.getClient();
        initURL = fileURL;
        logger.info("Starting download in TASK " + fileURL);
        final GetMethod getMethod = getGetMethod(fileURL);
        getMethod.setFollowRedirects(true);
        if (makeRequest(getMethod)) {

            checkNameAndSize(getContentAsString());

            do {
                checkProblems();
                if (!getContentAsString().contains("Please enter the number")) {
                    throw new PluginImplementationException("No captcha.\nCannot find requested page content");
                }
                stepCaptcha(getContentAsString());

            } while (getContentAsString().contains("Please enter the number"));
            logger.info("Captcha OK");

            checkProblems();

            Matcher matcher = getMatcherAgainstContent("decode\\(\"(.+?)\"");
            if (matcher.find()) {
                String s = decode(matcher.group(1));
                logger.info("Found File URL - " + s);

                final GetMethod method = getGetMethod(s);
                Date newDate = new Date();
                if (tryDownloadAndSaveFile(method)) setTicket(newDate);
                else {
                    checkProblems();
                    throw new IOException("File input stream is empty.");
                }
            } else {
                checkProblems();
                throw new PluginImplementationException();
            }

        } else
            throw new PluginImplementationException();
    }

    private void checkNameAndSize(String content) throws Exception {

        if (content.contains("Your requested file could not be found")) {
            throw new URLNotAvailableAnymoreException("Your requested file could not be found");
        }

        Matcher matcher = PlugUtils.matcher("\\(([0-9.]* .B)\\)", content);
        if (matcher.find()) {
            Long a = PlugUtils.getFileSizeFromString(matcher.group(1));
            logger.info("File size " + a);
            httpFile.setFileSize(a);
            httpFile.setFileState(FileState.CHECKED_AND_EXISTING);
        }
        matcher = PlugUtils.matcher("File name:((<[^>]*>)|\\s)*([^<]+)<", content);
        if (matcher.find()) {
            final String fn = matcher.group(matcher.groupCount());
            logger.info("File name " + fn);
            httpFile.setFileName(fn);
        } else logger.warning("File name was not found" + getContentAsString());
    }


    private String decode(String input) {

        final StringBuilder output = new StringBuilder();
        int chr1;
        int chr2;
        int chr3;
        int enc1;
        int enc2;
        int enc3;
        int enc4;
        int i = 0;
        final String _keyStr = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";

        input = input.replaceAll("[^A-Za-z0-9+/=]", "");

        while (i < input.length()) {

            enc1 = _keyStr.indexOf(input.charAt(i++));
            enc2 = _keyStr.indexOf(input.charAt(i++));
            enc3 = _keyStr.indexOf(input.charAt(i++));
            enc4 = _keyStr.indexOf(input.charAt(i++));

            chr1 = (enc1 << 2) | (enc2 >> 4);
            chr2 = ((enc2 & 15) << 4) | (enc3 >> 2);
            chr3 = ((enc3 & 3) << 6) | enc4;

            output.append((char) chr1);

            if (enc3 != 64) {
                output.append((char) chr2);
            }
            if (enc4 != 64) {
                output.append((char) chr3);
            }
        }


        try {
            return (new String(output.toString().getBytes(), "UTF-8"));

        } catch (UnsupportedEncodingException ex) {
            logger.warning("Unsupported encoding" + ex);
        }
        return "";

    }


    private boolean stepCaptcha(String contentAsString) throws Exception {
/*
        if (contentAsString.contains("Please enter the number")) {
            Matcher matcher = Pattern.compile("captcha", Pattern.MULTILINE).matcher(contentAsString);
            if (matcher.find()) {
                String s = "http://www.share-online.biz/captcha.php";
                String captcha = getCaptchaSupport().getCaptcha(s);
                if (captcha == null) {
                    throw new CaptchaEntryInputMismatchException();
                } else {
                    matcher = Pattern.compile("name=myform action\\=\"([^\"]*)\"", Pattern.MULTILINE).matcher(contentAsString);
                    if (!matcher.find()) {
                        throw new PluginImplementationException("Captcha form action was not found");
                    }
                    s = matcher.group(1);
                    client.setReferer(initURL);
                    final PostMethod postMethod = getPostMethod(s);

                    postMethod.addParameter("captchacode", captcha);

                    if (makeRequest(postMethod)) {
                        return true;
                    }
                }
            } else {
                logger.warning(contentAsString);
                throw new PluginImplementationException("Captcha picture was not found");
            }
        }
        return false;
*/
        if (getContentAsString().contains("Please enter the number")) {
            final CaptchaSupport captchaSupport = getCaptchaSupport();
            String captchaSrc = "http://www.share-online.biz/captcha.php";
            //logger.info("Captcha URL " + captchaSrc);

            String captcha;
            if (captchaCounter <= captchaMax) {
                captcha = PlugUtils.recognize(captchaSupport.getCaptchaImage(captchaSrc), "-d -1 -C 0-9");
                logger.info("OCR attempt " + captchaCounter + " of " + captchaMax + ", recognized " + captcha);
                captchaCounter++;
            } else {
                captcha = captchaSupport.getCaptcha(captchaSrc);
                if (captcha == null) throw new CaptchaEntryInputMismatchException();
            }

            final HttpMethod httpMethod = getMethodBuilder().setReferer(fileURL).setActionFromFormByName("myform", true).setParameter("captchacode", captcha).toPostMethod();
            if (makeRedirectedRequest(httpMethod)) return true;

        } else {
            throw new PluginImplementationException("Captcha picture not found");
        }
        return false;
    }

    private int getTimeToWait() {
        long startOfTicket = context.getStartOfTicket().getTime();
        long now = new Date().getTime();
        if ((now - startOfTicket) < 60 * 60 * 1000)
            return new Long(((startOfTicket + 1000 * 60 * 60) - now) / 1000).intValue();
        return 20 * 60;
    }

    private void setTicket(Date newTime) {
        long oldTime = context.getStartOfTicket().getTime();

        if ((newTime.getTime() - oldTime) > 60 * 60 * 1000)
            context.setStartOfTicket(newTime);
    }

    private void checkProblems() throws ServiceConnectionProblemException, YouHaveToWaitException, URLNotAvailableAnymoreException {
        final String contentAsString = getContentAsString();

        if (contentAsString.contains("You have got max allowed download sessions from the same IP")) {
            throw new YouHaveToWaitException("You have got max allowed download sessions from the same IP", 5 * 60);
        }
        if (contentAsString.contains("this download is too big for your")) {
            throw new YouHaveToWaitException("This download is too big for your remaining download volume per hour", getTimeToWait());
        }
        if (contentAsString.contains("Your requested file could not be found")) {
            throw new URLNotAvailableAnymoreException("Your requested file could not be found");
        }
        if (contentAsString.contains("no slots available")) {
            throw new YouHaveToWaitException("All download slots for free users are in use", 5 * 60);
        }

    }

}