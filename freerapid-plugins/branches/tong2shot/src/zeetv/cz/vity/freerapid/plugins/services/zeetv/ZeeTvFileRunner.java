package cz.vity.freerapid.plugins.services.zeetv;

import cz.vity.freerapid.plugins.exceptions.ErrorDuringDownloadingException;
import cz.vity.freerapid.plugins.exceptions.PluginImplementationException;
import cz.vity.freerapid.plugins.exceptions.ServiceConnectionProblemException;
import cz.vity.freerapid.plugins.exceptions.URLNotAvailableAnymoreException;
import cz.vity.freerapid.plugins.services.applehls.AdjustableBitrateHlsDownloader;
import cz.vity.freerapid.plugins.webclient.AbstractRunner;
import cz.vity.freerapid.plugins.webclient.FileState;
import cz.vity.freerapid.plugins.webclient.utils.JsonMapper;
import cz.vity.freerapid.plugins.webclient.utils.PlugUtils;
import org.apache.commons.httpclient.methods.GetMethod;
import org.codehaus.jackson.JsonNode;

import java.io.IOException;
import java.util.logging.Logger;
import java.util.regex.Matcher;

/**
 * Class which contains main code
 *
 * @author tong2shot
 * @since 0.9u4
 */
class ZeeTvFileRunner extends AbstractRunner {
    private final static Logger logger = Logger.getLogger(ZeeTvFileRunner.class.getName());

    private SettingsConfig config;

    private void setConfig() throws Exception {
        ZeeTvServiceImpl service = (ZeeTvServiceImpl) getPluginService();
        config = service.getConfig();
    }

    @Override
    public void runCheck() throws Exception {
        super.runCheck();
        final GetMethod getMethod = getGetMethod(fileURL);
        if (makeRedirectedRequest(getMethod)) {
            isAtHomePage(getMethod.getURI().toString());
            checkProblems();
            checkNameAndSize(getContentAsString());
        } else {
            isAtHomePage(getMethod.getURI().toString());
            checkProblems();
            throw new ServiceConnectionProblemException();
        }
    }

    private void checkNameAndSize(String content) throws ErrorDuringDownloadingException {
        PlugUtils.checkName(httpFile, content, "\"og:title\" content=\"", "\"");
        httpFile.setFileName(httpFile.getFileName() + ".ts");
        httpFile.setFileState(FileState.CHECKED_AND_EXISTING);
    }

    @Override
    public void run() throws Exception {
        super.run();
        logger.info("Starting download in TASK " + fileURL);
        final GetMethod method = getGetMethod(fileURL);
        if (makeRedirectedRequest(method)) {
            isAtHomePage(method.getURI().toString());
            checkProblems();
            checkNameAndSize(getContentAsString());

            Matcher matcher = getMatcherAgainstContent("var subject\\s*?=\\s*?'(.+?)';");
            if (!matcher.find()) {
                throw new PluginImplementationException("Cipher playlist URL not found");
            }
            String subject = matcher.group(1);
            JsonNode subjectNode;
            try {
                subjectNode = new JsonMapper().getObjectMapper().readTree(subject);
            } catch (IOException e) {
                throw new PluginImplementationException("Error parsing cipher playlist URL (1)");
            }
            String cipherTextBase64 = subjectNode.findPath("ct").getTextValue();
            String saltHex = subjectNode.findPath("s").getTextValue();
            if ((cipherTextBase64 == null) || (saltHex == null)) {
                throw new PluginImplementationException("Error parsing cipher playlist URL (2)");
            }
            String playlistUrl;
            try {
                playlistUrl = Crypto.decrypt(cipherTextBase64, saltHex).replace("\"", "").replace("\\/", "/");
            } catch (Exception e) {
                throw new PluginImplementationException("Error decrypting playlist URL");
            }

            setConfig();
            logger.info("Config settings : " + config);
            AdjustableBitrateHlsDownloader downloader = new AdjustableBitrateHlsDownloader(client, httpFile, downloadTask, config.getVideoQuality().getBitrate());
            downloader.tryDownloadAndSaveFile(playlistUrl);
        } else {
            isAtHomePage(method.getURI().toString());
            checkProblems();
            throw new ServiceConnectionProblemException();
        }
    }

    private void checkProblems() throws ErrorDuringDownloadingException {
        final String contentAsString = getContentAsString();
        if (contentAsString.contains("This page is currently unavailable")) {
            throw new URLNotAvailableAnymoreException("File not found");
        }
    }

    private void isAtHomePage(String url) throws ErrorDuringDownloadingException {
        if (url.matches("https?://(www\\.)zeetv\\.com/shows/[^/]+?/video/?")) {
            throw new URLNotAvailableAnymoreException("File not found");
        }
    }
}
