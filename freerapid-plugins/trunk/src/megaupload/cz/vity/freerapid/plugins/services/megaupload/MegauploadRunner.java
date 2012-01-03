package cz.vity.freerapid.plugins.services.megaupload;

import cz.vity.freerapid.plugins.exceptions.*;
import cz.vity.freerapid.plugins.webclient.AbstractRunner;
import cz.vity.freerapid.plugins.webclient.FileState;
import cz.vity.freerapid.plugins.webclient.hoster.PremiumAccount;
import cz.vity.freerapid.plugins.webclient.utils.PlugUtils;
import cz.vity.freerapid.utilities.LogUtils;
import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.HttpMethod;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.regex.Matcher;

/**
 * @author Ladislav Vitasek, Ludek Zika, JPEXS, ntoskrnl
 */
class MegauploadRunner extends AbstractRunner {
    private final static Logger logger = Logger.getLogger(MegauploadRunner.class.getName());

    @Override
    public void runCheck() throws Exception {
        super.runCheck();
        checkURL();
        addCookie(new Cookie(".megaupload.com", "l", "en", "/", 86400, false));
        addCookie(new Cookie(".megaporn.com", "l", "en", "/", 86400, false));
        final HttpMethod getMethod = getGetMethod(fileURL);
        if (makeRedirectedRequest(getMethod)) {
            if (!isFolder()) {
                checkNameAndSize();
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
        logger.info("Starting download in TASK " + fileURL);
        addCookie(new Cookie(".megaupload.com", "l", "en", "/", 86400, false));
        addCookie(new Cookie(".megaporn.com", "l", "en", "/", 86400, false));

        final boolean loggedIn = login();

        HttpMethod method = getGetMethod(fileURL);
        if (makeRedirectedRequest(method)) {
            checkProblems();

            if (isFolder()) {
                stepFolder();
                return;
            }

            checkNameAndSize();

            if (loggedIn) {
                if (tryManagerDownload(fileURL)) {
                    return;
                }
            }

            if (getContentAsString().contains("download is password protected")) {
                stepPasswordPage();
            }

            final Matcher matcher = getMatcherAgainstContent("<a href=\"(http.+?)\" class=\"download_regular_usual\"");
            if (!matcher.find()) {
                if (loggedIn && makeRedirectedRequest(getGetMethod("/?c=account"))) {
                    if (getContentAsString().contains("class=\"account_txt\">(Premium)")) {
                        throw new NotRecoverableDownloadException("Premium account detected, please use premium plugin instead");
                    }
                }
                throw new PluginImplementationException("Download link not found");
            }
            final String url = matcher.group(1);

            final int index = url.lastIndexOf('/');
            if (index > 0) {
                final String name = url.substring(index + 1);
                httpFile.setFileName(PlugUtils.unescapeHtml(URLDecoder.decode(name, "UTF-8")));
            }

            method = getMethodBuilder().setReferer(fileURL).setAction(url).toGetMethod();
            if (!tryDownloadAndSaveFile(method)) {
                checkProblems();
                throw new ServiceConnectionProblemException("Error starting download");
            }
        } else {
            checkProblems();
            throw new ServiceConnectionProblemException();
        }
    }

    private void checkNameAndSize() throws Exception {
        if (getContentAsString().contains("link you have clicked is not available")) {
            throw new URLNotAvailableAnymoreException("The file is not available");
        }
        PlugUtils.checkName(httpFile, getContentAsString(), "<div class=\"download_file_name\">", "</div>");
        PlugUtils.checkFileSize(httpFile, getContentAsString(), "<div class=\"download_file_size\">", "</div>");
        httpFile.setFileState(FileState.CHECKED_AND_EXISTING);
    }


    private void checkProblems() throws ErrorDuringDownloadingException {
        final String content = getContentAsString();
        if (content.contains("trying to access is temporarily unavailable")) {
            throw new ServiceConnectionProblemException("The file you are trying to access is temporarily unavailable");
        }
        if (content.contains("then try your download again")) {
            final Matcher matcher = getMatcherAgainstContent("Your IP address [\\d\\.]+? has just downloaded \\d+? bytes. Please wait (\\d+?) minutes, then try your download again.");
            if (matcher.find()) {
                throw new YouHaveToWaitException(matcher.group(), 10 + 60 * Integer.parseInt(matcher.group(1)));
            } else {
                throw new PluginImplementationException("Download limit exceeded, but waiting time was not found");
            }
        }
        if (content.contains("Download limit exceeded")) {
            throw new ServiceConnectionProblemException("Download limit exceeded");
        }
        if (content.contains("All download slots")) {
            throw new ServiceConnectionProblemException("No free slot for your country.");
        }
        if (content.contains("to download is larger than") || content.contains("class=\"download_l_descr\"")) {
            throw new NotRecoverableDownloadException("Only premium users are entitled to download files larger than 1 GB from Megaupload.");
        }
        if (content.contains("the link you have clicked is not available")) {
            throw new URLNotAvailableAnymoreException("The file is not available");
        }
        if (content.contains("We have detected an elevated number of requests")) {
            final int wait = PlugUtils.getWaitTimeBetween(content, "check back in", "minute", TimeUnit.MINUTES);
            throw new YouHaveToWaitException("We have detected an elevated number of requests", wait);
        }
    }

    private void checkURL() {
        final String host = httpFile.getFileUrl().getHost();
        if (host.contains("megarotic") || host.contains("sexuploader") || host.contains("megaporn")) {
            fileURL = fileURL.replace("megarotic.com", "megaporn.com").replace("sexuploader.com", "megaporn.com");
        }
    }

    private void stepPasswordPage() throws Exception {
        while (getContentAsString().contains("Please enter the password")) {
            final String password = getDialogSupport().askForPassword("MegaUpload");
            if (password == null) {
                throw new NotRecoverableDownloadException("This file is secured with a password");
            }
            final HttpMethod method = getMethodBuilder()
                    .setReferer(fileURL)
                    .setAction(fileURL)
                    .setParameter("filepassword", password)
                    .toPostMethod();
            if (!makeRedirectedRequest(method)) {
                throw new ServiceConnectionProblemException();
            }
        }
    }

    private boolean isFolder() {
        return PlugUtils.find("[\\?&]f=", fileURL);
    }

    private void stepFolder() throws Exception {
        final List<URI> list = new LinkedList<URI>();
        for (int page = 1; ; page++) {
            final String url = fileURL + "&ajax=1&pa=" + page + "&so=name&di=asc&rnd=" + System.currentTimeMillis();
            final HttpMethod method = getMethodBuilder().setReferer(fileURL).setAction(url).toGetMethod();
            if (!makeRedirectedRequest(method)) {
                throw new ServiceConnectionProblemException();
            }
            final int total = getTotal();
            final int previousSize = list.size();
            final Matcher matcher = getMatcherAgainstContent("\"url\":\"(.+?)\"");
            while (matcher.find()) {
                try {
                    list.add(new URI(matcher.group(1).replace("\\/", "/")));
                } catch (final URISyntaxException e) {
                    LogUtils.processException(logger, e);
                }
            }
            if (list.size() >= total || list.size() <= previousSize) {
                break;
            }
        }
        if (list.isEmpty()) {
            throw new PluginImplementationException("No links found");
        }
        getPluginService().getPluginContext().getQueueSupport().addLinksToQueue(httpFile, list);
        httpFile.getProperties().put("removeCompleted", true);
    }

    private int getTotal() throws ErrorDuringDownloadingException {
        final Matcher matcher = getMatcherAgainstContent("\"total\":\"(\\d+)\"");
        if (!matcher.find()) {
            throw new PluginImplementationException("Total number of links not found");
        }
        return Integer.parseInt(matcher.group(1));
    }

    private boolean login() throws Exception {
        synchronized (MegauploadRunner.class) {
            final MegauploadShareServiceImpl service = (MegauploadShareServiceImpl) getPluginService();
            final PremiumAccount pa = service.getConfig();
            if (pa == null || !pa.isSet()) {
                logger.info("No account data set, skipping login");
                return false;
            }

            final HttpMethod httpMethod = getMethodBuilder()
                    .setAction("/?c=login")
                    .setParameter("login", "1")
                    .setParameter("username", pa.getUsername())
                    .setParameter("password", pa.getPassword())
                    .toPostMethod();
            if (!makeRedirectedRequest(httpMethod))
                throw new ServiceConnectionProblemException("Error posting login info");

            if (getContentAsString().contains("Username and password do not match"))
                throw new BadLoginException("Invalid MegaUpload account login information!");

            return true;
        }
    }

    private String getManagerURL(final String url) {
        final Cookie user = getCookieByName("user");
        if (user != null) {
            final Matcher matcher = PlugUtils.matcher("[\\?&][df]=([^&=]+)", url);
            if (matcher.find()) {
                return "/mgr_dl.php?d=" + matcher.group(1) + "&u=" + user.getValue();
            }
        }
        return url;
    }

    private void setManagerRequestHeaders(final HttpMethod method) {
        method.setRequestHeader("Accept", "text/plain,text/html,*/*;q=0.3");
        method.setRequestHeader("Accept-Encoding", "identity");
        method.setRequestHeader("TE", "trailers");
        method.setRequestHeader("Connection", "TE");
        method.setRequestHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; Trident/4.0)");
    }

    private boolean tryManagerDownload(final String url) throws Exception {
        HttpMethod method = getMethodBuilder().setAction(getManagerURL(url)).setReferer(null).toGetMethod();
        setManagerRequestHeaders(method);
        if (client.makeRequest(method, false) == 302) {
            final String downloadURL = method.getResponseHeader("Location").getValue();
            logger.info("Found redirect location " + downloadURL);
            if (downloadURL.contains("files")) {
                final int i = downloadURL.lastIndexOf('/');
                if (i > 0) {
                    final String toEncode = downloadURL.substring(i + 1);
                    httpFile.setFileName(PlugUtils.unescapeHtml(toEncode));
                }
                method = getMethodBuilder().setAction(downloadURL).setReferer(null).toGetMethod();
                try {
                    return tryDownloadAndSaveFile(method);
                } catch (Exception e) {
                    return false;
                }
            }
        }
        if (!makeRedirectedRequest(getGetMethod(fileURL))) {
            throw new ServiceConnectionProblemException();
        }
        return false;
    }

}
