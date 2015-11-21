package cz.vity.freerapid.plugins.services.itv;

import cz.vity.freerapid.plugins.exceptions.ErrorDuringDownloadingException;
import cz.vity.freerapid.plugins.exceptions.PluginImplementationException;
import cz.vity.freerapid.plugins.exceptions.ServiceConnectionProblemException;
import cz.vity.freerapid.plugins.exceptions.URLNotAvailableAnymoreException;
import cz.vity.freerapid.plugins.services.rtmp.AbstractRtmpRunner;
import cz.vity.freerapid.plugins.services.rtmp.RtmpSession;
import cz.vity.freerapid.plugins.services.rtmp.SwfVerificationHelper;
import cz.vity.freerapid.plugins.services.tor.TorProxyClient;
import cz.vity.freerapid.plugins.webclient.FileState;
import cz.vity.freerapid.plugins.webclient.utils.PlugUtils;
import cz.vity.freerapid.utilities.LogUtils;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;
import java.util.regex.Matcher;

/**
 * Class which contains main code
 *
 * @author ntoskrnl
 * @author tong2shot
 */
class ItvFileRunner extends AbstractRtmpRunner {
    private final static Logger logger = Logger.getLogger(ItvFileRunner.class.getName());

    private final static String SWF_URL = "https://www.itv.com/mediaplayer/ITVMediaPlayer.swf";
    private final static SwfVerificationHelper helper = new SwfVerificationHelper(SWF_URL);
    private SettingsConfig config;

    private void setConfig() throws Exception {
        final ItvServiceImpl service = (ItvServiceImpl) getPluginService();
        config = service.getConfig();
    }

    @Override
    public void runCheck() throws Exception {
        super.runCheck();
        final HttpMethod method = getGetMethod(fileURL);
        if (makeRedirectedRequest(method)) {
            checkProblems();
            checkNameAndSize();
        } else {
            checkProblems();
            throw new ServiceConnectionProblemException();
        }
    }

    private void checkNameAndSize() throws Exception {
        Matcher matcher = getMatcherAgainstContent("<h1[^<>]*?>(.+?)</h1");
        if (!matcher.find()) {
            throw new PluginImplementationException("Programme title not found");
        }
        String name = matcher.group(1).trim();

        matcher = getMatcherAgainstContent("\"episode-info__series\">Series (\\d+) [^<>]*?Episode (\\d+)");
        if (matcher.find()) {
            name = String.format("%s - S%02dE%02d", name, Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)));
        }
        httpFile.setFileName(name + ".flv");
        httpFile.setFileState(FileState.CHECKED_AND_EXISTING);
    }

    @Override
    public void run() throws Exception {
        super.run();
        logger.info("Starting download in TASK " + fileURL);
        HttpMethod method = getGetMethod(fileURL);
        if (makeRedirectedRequest(method)) {
            checkProblems();
            checkNameAndSize();
            String productionId;
            try {
                productionId = PlugUtils.getStringBetween(getContentAsString(), "\"productionId\":\"", "\"").replace("\\/", "/");
            } catch (PluginImplementationException e) {
                productionId = PlugUtils.getStringBetween(getContentAsString(), "data-video-id=\"", "\"").replace("\\/", "/");
            }
            method = getMethodBuilder()
                    .setReferer(fileURL)
                    .setAction("http://mercury.itv.com/PlaylistService.svc?wsdl")
                    .setHeader("SOAPAction", "http://tempuri.org/PlaylistService/GetPlaylist")
                    .toPostMethod();
            ((PostMethod) method).setRequestEntity(new StringRequestEntity(
                    String.format(PLAYLIST_REQUEST_BASE, productionId, getRandomGuid()), "text/xml", "utf-8"));
            final TorProxyClient torClient = TorProxyClient.forCountry("gb", client, getPluginService().getPluginContext().getConfigurationStorageSupport());
            if (!torClient.makeRequest(method)) {
                checkProblems();
                throw new ServiceConnectionProblemException();
            }
            checkProblems();
            setConfig();
            logger.info("Config settings: " + config);
            final RtmpSession rtmpSession = getRtmpSession();
            tryDownloadAndSaveFile(rtmpSession);
        } else {
            checkProblems();
            throw new ServiceConnectionProblemException();
        }
    }

    private void checkProblems() throws ErrorDuringDownloadingException {
        if (getContentAsString().contains("Page not found")
                || getContentAsString().contains("ContentUnavailable")) {
            throw new URLNotAvailableAnymoreException("File not found");
        }
        if (getContentAsString().contains("InvalidGeoRegion")) {
            throw new URLNotAvailableAnymoreException("This video is not available in your region");
        }
    }

    private RtmpSession getRtmpSession() throws Exception {
        Matcher matcher = getMatcherAgainstContent("(?s)<Video timecode=[^<>]*?>(.+?)</Video>");
        if (!matcher.find()) {
            throw new PluginImplementationException("'Video' tag not found in playlist");
        }
        final String video = matcher.group(1);

        if (config.isDownloadSubtitles()) {
            matcher = PlugUtils.matcher("<URL>(?:<!\\[CDATA\\[)?(http://subtitles\\.[^\\]<>]+?)(?:\\]\\]>)?</URL>", video);
            if (matcher.find()) {
                String subtitleUrl = matcher.group(1);
                try {
                    new SubtitleDownloader().downloadSubtitle(client, httpFile, subtitleUrl);
                } catch (Exception e) {
                    LogUtils.processException(logger, e);
                }
            } else {
                logger.warning("Subtitle not found");
            }
        }

        matcher = PlugUtils.matcher("(?s)<MediaFiles base=\"(rtmp.+?)\"", video);
        if (!matcher.find()) {
            throw new PluginImplementationException("URL not found in playlist");
        }
        final String url = PlugUtils.replaceEntities(matcher.group(1));

        List<ItvVideo> videoList = new ArrayList<ItvVideo>();
        Matcher mediaFileMatcher = PlugUtils.matcher("(?s)<MediaFile[^<>]*?bitrate\\s*?=\\s*?\"(\\d+?)\"[^<>]*?>(.+?)</MediaFile>", video);
        while (mediaFileMatcher.find()) {
            matcher = PlugUtils.matcher("(mp4:.+?\\.mp4)", mediaFileMatcher.group(2));
            if (matcher.find()) {
                ItvVideo itvVideo = new ItvVideo(Integer.parseInt(mediaFileMatcher.group(1)) / 1000, matcher.group(1));
                videoList.add(itvVideo);
                logger.info("Found video: " + itvVideo);
            }
        }
        if (videoList.isEmpty()) {
            throw new PluginImplementationException("No available videos");
        }
        ItvVideo selectedVideo = Collections.min(videoList);
        logger.info("Config settings : " + config);
        logger.info("Selected video  : " + selectedVideo);

        final RtmpSession rtmpSession = new RtmpSession(url, selectedVideo.play);
        rtmpSession.getConnectParams().put("pageUrl", fileURL);
        rtmpSession.getConnectParams().put("swfUrl", SWF_URL);
        helper.setSwfVerification(rtmpSession, client);
        return rtmpSession;
    }

    private static String getRandomGuid() {
        //returns a string like this: 6D3D963A-B6C7-0A3E-D1E0-A0A1611A2B86
        final byte[] b = new byte[18];
        new Random().nextBytes(b);
        final char[] c = Hex.encodeHex(b, false);
        c[8] = '-';
        c[13] = '-';
        c[18] = '-';
        c[23] = '-';
        return new String(c);
    }

    private class ItvVideo implements Comparable<ItvVideo> {
        private final static int LOWER_QUALITY_PENALTY = 10;
        private final int bitrate; //kbps
        private final String play;
        private int weight;

        public ItvVideo(final int bitrate, final String play) {
            this.bitrate = bitrate;
            this.play = play;
            this.weight = calcWeight();
        }

        private int calcWeight() {
            VideoQuality configQuality = config.getVideoQuality();
            int deltaQ = bitrate - configQuality.getBitrate();
            return (deltaQ < 0 ? Math.abs(deltaQ) + LOWER_QUALITY_PENALTY : deltaQ);
        }

        @SuppressWarnings("NullableProblems")
        @Override
        public int compareTo(final ItvVideo that) {
            return Integer.valueOf(this.weight).compareTo(that.weight);
        }

        @Override
        public String toString() {
            return "ItvVideo{" +
                    "bitrate=" + bitrate +
                    ", play='" + play + '\'' +
                    ", weight=" + weight +
                    '}';
        }
    }

    private final static String PLAYLIST_REQUEST_BASE =
            "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:tem=\"http://tempuri.org/\" xmlns:itv=\"http://schemas.datacontract.org/2004/07/Itv.BB.Mercury.Common.Types\" xmlns:com=\"http://schemas.itv.com/2009/05/Common\">\n" +
                    "  <soapenv:Header/>\n" +
                    "  <soapenv:Body>\n" +
                    "    <tem:GetPlaylist>\n" +
                    "      <tem:request>\n" +
                    "        <itv:ProductionId>%s</itv:ProductionId>\n" +
                    "        <itv:RequestGuid>%s</itv:RequestGuid>\n" +
                    "        <itv:Vodcrid>\n" +
                    "          <com:Id/>\n" +
                    "          <com:Partition>itv.com</com:Partition>\n" +
                    "        </itv:Vodcrid>\n" +
                    "      </tem:request>\n" +
                    "      <tem:userInfo>\n" +
                    "        <itv:Broadcaster>Itv</itv:Broadcaster>\n" +
                    "        <itv:GeoLocationToken>\n" +
                    "          <itv:Token/>\n" +
                    "        </itv:GeoLocationToken>\n" +
                    "        <itv:RevenueScienceValue>ITVPLAYER.12.18.4</itv:RevenueScienceValue>\n" +
                    "        <itv:SessionId/>\n" +
                    "        <itv:SsoToken/>\n" +
                    "        <itv:UserToken/>\n" +
                    "      </tem:userInfo>\n" +
                    "      <tem:siteInfo>\n" +
                    "        <itv:AdvertisingRestriction>None</itv:AdvertisingRestriction>\n" +
                    "        <itv:AdvertisingSite>ITV</itv:AdvertisingSite>\n" +
                    "        <itv:AdvertisingType>Any</itv:AdvertisingType>\n" +
                    "        <itv:Area>ITVPLAYER.VIDEO</itv:Area>\n" +
                    "        <itv:Category/>\n" +
                    "        <itv:Platform>DotCom</itv:Platform>\n" +
                    "        <itv:Site>ItvCom</itv:Site>\n" +
                    "      </tem:siteInfo>\n" +
                    "      <tem:deviceInfo>\n" +
                    "        <itv:ScreenSize>Big</itv:ScreenSize>\n" +
                    "      </tem:deviceInfo>\n" +
                    "      <tem:playerInfo>\n" +
                    "        <itv:Version>2</itv:Version>\n" +
                    "      </tem:playerInfo>\n" +
                    "    </tem:GetPlaylist>\n" +
                    "  </soapenv:Body>\n" +
                    "</soapenv:Envelope>";

}