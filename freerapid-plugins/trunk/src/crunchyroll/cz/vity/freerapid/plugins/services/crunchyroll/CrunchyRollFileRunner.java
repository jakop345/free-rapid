package cz.vity.freerapid.plugins.services.crunchyroll;

import cz.vity.freerapid.plugins.exceptions.*;
import cz.vity.freerapid.plugins.services.rtmp.AbstractRtmpRunner;
import cz.vity.freerapid.plugins.services.rtmp.RtmpSession;
import cz.vity.freerapid.plugins.services.rtmp.SwfVerificationHelper;
import cz.vity.freerapid.plugins.webclient.FileState;
import cz.vity.freerapid.plugins.webclient.utils.PlugUtils;
import org.apache.commons.httpclient.HttpMethod;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.logging.Logger;
import java.util.regex.Matcher;

/**
 * Class which contains main code
 *
 * @author ntoskrnl
 * @author tong2shot (subtitle)
 */
class CrunchyRollFileRunner extends AbstractRtmpRunner {
    private final static Logger logger = Logger.getLogger(CrunchyRollFileRunner.class.getName());
    private static SwfVerificationHelper helper = null;

    private SettingsConfig config;

    private void setConfig() throws Exception {
        final CrunchyRollServiceImpl service = (CrunchyRollServiceImpl) getPluginService();
        config = service.getConfig();
    }

    @Override
    public void runCheck() throws Exception {
        super.runCheck();
        final HttpMethod method = getGetMethod(fileURL);
        if (makeRedirectedRequest(method)) {
            checkProblems();
            checkName();
        } else {
            checkProblems();
            throw new ServiceConnectionProblemException();
        }
    }

    private void checkName() throws ErrorDuringDownloadingException {
        final Matcher matcher = getMatcherAgainstContent("<title>(?:Crunchyroll \\- )?(?:Watch |Music \\- )?(.+?)(?: \\- Crunchyroll)?</title>");
        if (!matcher.find()) {
            throw new PluginImplementationException("File name not found");
        }
        httpFile.setFileName(matcher.group(1) + ".flv");
        httpFile.setFileState(FileState.CHECKED_AND_EXISTING);
    }

    @Override
    public void run() throws Exception {
        super.run();
        logger.info("Starting download in TASK " + fileURL);
        HttpMethod method = getGetMethod(fileURL);
        if (makeRedirectedRequest(method)) {
            checkProblems();
            checkName();

            setConfig();
            QualityUrl selectedQualityUrl = getQualityUrl(getContentAsString());
            fileURL = new URI(fileURL).resolve(selectedQualityUrl.url).toString();
            method = getGetMethod(fileURL);
            if (!makeRedirectedRequest(method)) {
                checkProblems();
                throw new ServiceConnectionProblemException();
            }
            checkProblems();

            logger.info("Config settings : " + config);
            logger.info("Selected video : " + selectedQualityUrl);
            final String loaderSwfUrl = PlugUtils.getStringBetween(getContentAsString(), ".embedSWF(\"", "\"").replace("\\/", "/");
            final String configUrl = URLDecoder.decode(PlugUtils.getStringBetween(getContentAsString(), "\"config_url\":\"", "\""), "UTF-8");
            method = getMethodBuilder().setReferer(null).setAction(configUrl).setParameter("current_page", fileURL).toPostMethod();
            if (!makeRedirectedRequest(method)) {
                checkProblems();
                throw new ServiceConnectionProblemException();
            }
            checkProblems();

            final String host = PlugUtils.replaceEntities(PlugUtils.getStringBetween(getContentAsString(), "<host>", "</host>"));
            final String file = PlugUtils.replaceEntities(PlugUtils.getStringBetween(getContentAsString(), "<file>", "</file>"));
            final String playerSwfUrl = PlugUtils.replaceEntities(PlugUtils.getStringBetween(getContentAsString(), "<default:chromelessPlayerUrl>", "</default:chromelessPlayerUrl>"));
            final String swfUrl;
            try {
                swfUrl = new URI(loaderSwfUrl).resolve(new URI(playerSwfUrl)).toString();
            } catch (final URISyntaxException e) {
                throw new PluginImplementationException("Invalid SWF URL", e);
            }
            if (config.isDownloadSubtitle()) {
                new SubtitleDownloader().downloadSubtitle(httpFile, getContentAsString());
            }
            final RtmpSession rtmpSession = new RtmpSession(host, file);
            setSwfVerification(rtmpSession, swfUrl);
            tryDownloadAndSaveFile(rtmpSession);
        } else {
            checkProblems();
            throw new ServiceConnectionProblemException();
        }
    }

    private void checkProblems() throws ErrorDuringDownloadingException {
        if (getContentAsString().contains("No media found") || getContentAsString().contains("we were unable to find the page you were looking for")) {
            throw new URLNotAvailableAnymoreException("File not found");
        }
        if (getContentAsString().contains("This video is not available from this website")) {
            throw new URLNotAvailableAnymoreException("This video is not available from this website");
        }
        if (getContentAsString().contains("this video is not available in your region")) {
            throw new NotRecoverableDownloadException("This video is not available in your region");
        }
        if (getContentAsString().contains("You are watching a sample clip")) {
            throw new URLNotAvailableAnymoreException("Sample clips are not supported");
        }
        if (getContentAsString().contains("Media not available")) {
            throw new URLNotAvailableAnymoreException("Media not available");
        }
    }

    private void setSwfVerification(final RtmpSession session, final String swfUrl) throws Exception {
        synchronized (CrunchyRollFileRunner.class) {
            if (helper == null || !helper.getSwfURL().equals(swfUrl)) {
                logger.info("New SWF URL: " + swfUrl);
                helper = new SwfVerificationHelper(swfUrl);
            }
            helper.setSwfVerification(session, client);
        }
    }

    private QualityUrl getQualityUrl(String content) throws PluginImplementationException {
        Matcher matcher = PlugUtils.matcher("<a href=\"([^\"]+?)\".+?token=\"showmedia.(\\d+)p\"[^<>]+?>", content);
        int weight = Integer.MAX_VALUE;
        int LOWER_QUALITY_PENALTY = 1;
        QualityUrl selectedQualityUrl = null;
        while (matcher.find()) {
            int quality = Integer.parseInt(matcher.group(2));
            String url = matcher.group(1);
            if ((quality > 480) || (url.startsWith("/freetrial/"))) { // 720 and 1080 only for premium
                continue;
            }
            QualityUrl qualityUrl = new QualityUrl(quality, url);
            logger.info("Found video: " + qualityUrl);
            int deltaQ = quality - config.getVideoQuality().getQuality();
            int tempWeight = (deltaQ < 0 ? Math.abs(deltaQ) + LOWER_QUALITY_PENALTY : deltaQ);
            if (tempWeight < weight) {
                weight = tempWeight;
                selectedQualityUrl = qualityUrl;
            }
        }
        if (selectedQualityUrl == null) {
            throw new PluginImplementationException("No quality URL found");
        }
        return selectedQualityUrl;
    }

    private class QualityUrl {
        private final int quality;
        private final String url;

        private QualityUrl(int quality, String url) {
            this.quality = quality;
            this.url = url;
        }

        @Override
        public String toString() {
            return "QualityUrl{" +
                    "quality=" + quality +
                    ", url='" + url + '\'' +
                    '}';
        }
    }

}