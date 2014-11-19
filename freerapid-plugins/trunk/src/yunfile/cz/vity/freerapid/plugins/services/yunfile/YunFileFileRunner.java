package cz.vity.freerapid.plugins.services.yunfile;

import cz.vity.freerapid.plugins.exceptions.*;
import cz.vity.freerapid.plugins.webclient.AbstractRunner;
import cz.vity.freerapid.plugins.webclient.DownloadClientConsts;
import cz.vity.freerapid.plugins.webclient.FileState;
import cz.vity.freerapid.plugins.webclient.utils.PlugUtils;
import cz.vity.freerapid.utilities.LogUtils;
import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;

/**
 * Class which contains main code
 *
 * @author Stan
 * @author tong2shot
 */
class YunFileFileRunner extends AbstractRunner {
    private final static Logger logger = Logger.getLogger(YunFileFileRunner.class.getName());

    @Override
    public void runCheck() throws Exception {
        super.runCheck();
        checkFileURL();
        addCookie(new Cookie(".yunfile.com", "language", "en_us", "/", 86400, false));
        final GetMethod getMethod = getGetMethod(fileURL);
        if (makeRedirectedRequest(getMethod)) {
            checkProblems();
            checkNameAndSize(getContentAsString());
        } else {
            checkProblems();
            throw new ServiceConnectionProblemException();
        }
    }

    private void checkNameAndSize(String content) throws ErrorDuringDownloadingException {
        String filename = null;
        Matcher matcher = PlugUtils.matcher("^\\s*?(?!<[^<>]+?display:none[^<>]*?>)<h2[^<>]*?>(?:Downloading\\s*?:\\s*?)?.*?(.+?)</h2>", content);
        while (matcher.find()) {
            String group1 = matcher.group(1);
            if (!group1.contains("Please") || !group1.contains("premium")) {
                filename = PlugUtils.unescapeHtml(group1)
                        .replaceAll("</?[a-z0-9]{1,2}?>", "")
                        .replaceAll("<!--.*?-->", "")
                        .trim();
            }
        }
        if (filename == null) {
            throw new PluginImplementationException("File name not found");
        }
        httpFile.setFileName(filename);
        PlugUtils.checkFileSize(httpFile, content, "File Size: <b>", "</b>");
        httpFile.setFileState(FileState.CHECKED_AND_EXISTING);
    }

    @Override
    /*
    Actual browser test note : turn browser's referer setting on for this domain, otherwise it won't download.
     */
    public void run() throws Exception {
        super.run();
        checkFileURL();
        addCookie(new Cookie(".yunfile.com", "language", "en_us", "/", 86400, false));
        logger.info("Starting download in TASK " + fileURL);
        final GetMethod method = getGetMethod(fileURL);
        if (makeRedirectedRequest(method)) {
            final String contentAsString = getContentAsString();
            checkProblems();
            checkNameAndSize(contentAsString);
            String baseURL = "http://" + method.getURI().getAuthority();
            String referer = method.getURI().toString();
            HttpMethod httpMethod;
            do {
                Matcher matcher = getMatcherAgainstContent("Please wait <span.+?>(.+?)</span>");
                final int waitTime = !matcher.find() ? 30 : Integer.parseInt(matcher.group(1));

                matcher = getMatcherAgainstContent("<a (.*?id=\"downpage_link\"[^<>]+?)>");
                Matcher downpageHref = getMatcherAgainstContent("href=\"([^\"]+?)\"");
                if (!matcher.find()) {
                    throw new PluginImplementationException("Download page anchor tag not found");
                }
                downpageHref.region(matcher.start(1), matcher.end(1));
                if (!downpageHref.find()) {
                    throw new PluginImplementationException("Download page link not found");
                }
                String downloadPageLink = downpageHref.group(1).trim();

                downloadTask.sleep(waitTime + 1);
                if (getContentAsString().contains("vcode")) {
                    final BufferedImage captchaImg = getCaptchaImg(getContentAsString(), baseURL);
                    final String captcha = getCaptchaSupport().askForCaptcha(captchaImg);
                    if (captcha == null) {
                        throw new CaptchaEntryInputMismatchException();
                    }
                    downloadPageLink = downloadPageLink.replaceAll("\\.html$", "/" + captcha + ".html");
                }
                httpMethod = getMethodBuilder()
                        .setReferer(referer)
                        .setBaseURL(baseURL)
                        .setAction(downloadPageLink)
                        .toGetMethod();
                if (!makeRedirectedRequest(httpMethod)) {
                    checkProblems();
                    throw new ServiceConnectionProblemException();
                }
                checkProblems();
                baseURL = "http://" + httpMethod.getURI().getAuthority();
                referer = httpMethod.getURI().toString();
            } while (getContentAsString().contains("vcode"));
            final String downloadPageUrl = httpMethod.getURI().toString();
            boolean cookieVidSet = false;
            if (getContentAsString().contains("setCookie(\"vid1\", \"")) {
                final String vid1CookieValue = PlugUtils.getStringBetween(getContentAsString(), "setCookie(\"vid1\", \"", "\"");
                addCookie(new Cookie(".yunfile.com", "vid1", vid1CookieValue, "/", 86400, false));
                cookieVidSet = true;
            }
            if (getContentAsString().contains("setCookie(\"vid2\", \"")) {
                final String vid2CookieValue = PlugUtils.getStringBetween(getContentAsString(), "setCookie(\"vid2\", \"", "\"");
                addCookie(new Cookie(".yunfile.com", "vid2", vid2CookieValue, "/", 86400, false));
                cookieVidSet = true;
            }
            if (cookieVidSet) {
                httpMethod = getMethodBuilder()
                        .setReferer(referer)
                        .setActionFromFormByName("down_from", true)
                        .toPostMethod();
            } else {
                final String fileId;
                final String vid;
                final String varVid;
                Matcher matcher = getMatcherAgainstContent("fileId\\.value\\s*=\\s*[\"'](.+?)[\"']\\s*;");
                if (!matcher.find()) {
                    fileId = getFileIdFromUrl();
                } else {
                    fileId = matcher.group(1);
                }
                matcher = getMatcherAgainstContent("^(?!.*?//)(?:.+?)?vid\\.value\\s*=\\s*([\"']?.+?[\"']?)\\s*;");
                if (!matcher.find()) throw new PluginImplementationException("Vid value not found");
                if (matcher.group(1).contains("\"") || matcher.group(1).contains("'")) { //vid is string
                    vid = matcher.group(1).replace("\"", "").replace("'", "");
                } else { //vid param is stored in variable
                    varVid = matcher.group(1);
                    matcher = getMatcherAgainstContent(String.format("var %s\\s*=\\s*[\"'](.+?)[\"']\\s*;", varVid));
                    if (!matcher.find()) throw new PluginImplementationException("Error parsing var vid");
                    vid = matcher.group(1);
                }
                httpMethod = getMethodBuilder()
                        .setReferer(downloadPageUrl)
                        .setActionFromFormWhereTagContains("fileId", true)
                        .setParameter("fileId", fileId)
                        .setParameter("vid", vid)
                        .toPostMethod();
                addCookie(new Cookie(".yunfile.com", "referer", URLEncoder.encode(downloadPageUrl, "UTF-8"), "/", 86400, false));
            }
            setClientParameter(DownloadClientConsts.DONT_USE_HEADER_FILENAME, true); //they send non-standard filename attachment header
            if (!tryDownloadAndSaveFile(httpMethod)) {
                checkProblems();
                throw new ServiceConnectionProblemException("Error starting download");
            }
        } else {
            checkProblems();
            throw new ServiceConnectionProblemException();
        }
    }

    private BufferedImage getCaptchaImg(String content, String baseUrl) throws Exception {
        /*
        <script>
	        var cvimg2 = document.getElementById("cvimg2");
	        cvimg2.setAttribute("src","/verifyimg/getPcv"+"/754"+".html");
        </script>
         */
        Matcher matcher = PlugUtils.matcher("(?s)<body[^<>]*?>(.+?)</body>", content);
        if (!matcher.find()) {
            throw new PluginImplementationException("Body content not found");
        }
        String bodyContent = matcher.group(1);

        String buttonId = "cvimg2";
        List<BufferedImage> images = new LinkedList<BufferedImage>();
        matcher = PlugUtils.matcher("['\"](/verifyimg/getPcv/\\d+\\.html)['\"]",
                bodyContent.replaceAll("(?sm)(?:([\\s;])+//(?:.*)$)|(?:/\\*(?:[\\s\\S]*?)\\*/)", "").replaceAll("<!--.*?-->", ""));
        int totalWidth = 0, maxHeight = 0;
        int imageType = 0;
        while (matcher.find()) {
            BufferedImage tempImage = getCaptchaSupport().getCaptchaImage(baseUrl + matcher.group(1));
            int width = tempImage.getWidth();
            int height = tempImage.getHeight();
            if (isValidImage(tempImage, width, height)) {
                if (images.size() > 3) {
                    throw new PluginImplementationException("Error getting captcha image (1)");
                }
                imageType = tempImage.getType();
                totalWidth += width;
                maxHeight = Math.max(maxHeight, height);
                images.add(tempImage);
            }
        }

        matcher = PlugUtils.matcher("(?s)<script>(.+?)</script>", bodyContent);
        ScriptEngine engine = initScriptEngine();
        while (matcher.find()) {
            String script = matcher.group(1);
            logger.info("Evaluating script:\n" + script);
            try {
                engine.eval(script);
            } catch (ScriptException e) {
                LogUtils.processException(logger, e);
            }
        }
        try {
            String imgSrc = (String) engine.eval(buttonId + ".getAttribute(\"src\")");
            BufferedImage tempImage = getCaptchaSupport().getCaptchaImage(baseUrl + imgSrc);
            int width = tempImage.getWidth();
            int height = tempImage.getHeight();
            if (isValidImage(tempImage, width, height)) {
                if (images.size() > 3) {
                    throw new PluginImplementationException("Error getting captcha image (2)");
                }
                imageType = tempImage.getType();
                totalWidth += width;
                maxHeight = Math.max(maxHeight, height);
                images.add(tempImage);
            }
        } catch (ScriptException e) {
            LogUtils.processException(logger, e);
        }

        if (images.size() == 0) {
            throw new PluginImplementationException("Error getting captcha image (3)");
        }
        BufferedImage compositeImage = new BufferedImage(totalWidth, maxHeight, imageType);
        for (int i = 0, imageWidth = 0, imagesCount = images.size(); i < imagesCount; i++) {
            BufferedImage tempImage = images.get(i);
            compositeImage.createGraphics().drawImage(tempImage, imageWidth, 0, null);
            imageWidth += tempImage.getWidth();
        }
        return compositeImage;
    }

    private boolean isValidImage(BufferedImage tempImage, int width, int height) throws PluginImplementationException {
        long sum = 0;
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                sum += tempImage.getRGB(i, j);
            }
        }
        long avg = (sum / (width * height));
        return (avg > (Color.BLACK.getRGB() + 0x000ff));
    }

    private String getFileIdFromUrl() throws PluginImplementationException {
        final Matcher matcher = PlugUtils.matcher("/([^/]+)/?$", fileURL);
        if (!matcher.find()) throw new PluginImplementationException("Error parsing URL");
        return matcher.group(1);
    }

    private void checkProblems() throws ErrorDuringDownloadingException {
        final String contentAsString = getContentAsString();
        if (contentAsString.contains("\u6587\u4EF6\u4E0D\u5B58\u5728") || contentAsString.contains("Been deleted")) { // 文件不存在 {@see http://www.snible.org/java2/uni2java.html}
            throw new URLNotAvailableAnymoreException("File not found");
        }
        if (contentAsString.contains("down_interval")) {
            int wait = 10;
            final Matcher match = PlugUtils.matcher("down_interval_tag\"[^>]+?>(.+?)</span>", contentAsString);
            if (match.find())
                wait = Integer.parseInt(match.group(1));
            throw new YouHaveToWaitException("Waiting for next file.", wait * 60);
        }
        if (contentAsString.contains("Web Server may be down")) {
            throw new ServiceConnectionProblemException("A communication error occurred: \"Operation timed out\"");
        }
        if (contentAsString.contains("Access denied")) {
            throw new PluginImplementationException("Access denied");
        }
    }

    private void checkFileURL() {
        fileURL = fileURL.replaceFirst("yfdisk\\.com", "yunfile.com")
                .replaceFirst("filemarkets\\.com", "yunfile.com")
                .replaceFirst("www\\.yunfile\\.com", "yunfile.com"); //apparently they redirect www.yunfile.com to yunfile.com
    }

    @Override
    protected String getBaseURL() {
        return "http://yunfile.com";
    }

    private ScriptEngine initScriptEngine() throws Exception {
        final ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");
        if (engine == null) {
            throw new RuntimeException("JavaScript engine not found");
        }
        final Reader reader = new InputStreamReader(YunFileFileRunner.class.getResourceAsStream("/resources/yunfile.js"), "UTF-8");
        try {
            engine.eval(reader);
        } finally {
            reader.close();
        }
        return engine;
    }
}