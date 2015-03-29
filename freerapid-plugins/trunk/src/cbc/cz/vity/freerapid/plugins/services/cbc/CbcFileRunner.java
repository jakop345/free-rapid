package cz.vity.freerapid.plugins.services.cbc;

import cz.vity.freerapid.plugins.exceptions.*;
import cz.vity.freerapid.plugins.services.rtmp.AbstractRtmpRunner;
import cz.vity.freerapid.plugins.services.rtmp.RtmpSession;
import cz.vity.freerapid.plugins.webclient.FileState;
import cz.vity.freerapid.plugins.webclient.utils.PlugUtils;
import cz.vity.freerapid.utilities.LogUtils;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Matcher;

/**
 * Class which contains main code
 *
 * @author tong2shot
 * @since 0.9u4
 */
class CbcFileRunner extends AbstractRtmpRunner {
    private final static Logger logger = Logger.getLogger(CbcFileRunner.class.getName());
    private static final String SWF_URL = "http://www.cbc.ca/video/swf/UberPlayer.swf";
    private SettingsConfig config;

    private void setConfig() throws Exception {
        CbcServiceImpl service = (CbcServiceImpl) getPluginService();
        config = service.getConfig();
    }

    @Override
    public void runCheck() throws Exception {
        super.runCheck();
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
        PlugUtils.checkName(httpFile, content, "<title>", "- CBC");
        httpFile.setFileName(httpFile.getFileName() + ".flv");
        httpFile.setFileState(FileState.CHECKED_AND_EXISTING);
    }

    @Override
    public void run() throws Exception {
        super.run();
        logger.info("Starting download in TASK " + fileURL);
        final GetMethod method = getGetMethod(fileURL);
        String mediaId = getMediaId();
        if (makeRedirectedRequest(method)) {
            checkProblems();
            checkNameAndSize(getContentAsString());

            HttpMethod httpMethod = getMethodBuilder()
                    .setReferer(fileURL)
                    .setAction("http://tpfeed.cbc.ca/f/h9dtGB/5akSXx4Ng_Zn")
                    .setParameter("range", "1-1")
                    .setParameter("byContent", "byReleases%3DbyId%253D" + mediaId)
                    .toGetMethod();
            if (!makeRedirectedRequest(httpMethod)) {
                checkProblems();
                throw new ServiceConnectionProblemException();
            }
            checkProblems();

            String pid;
            try {
                pid = PlugUtils.getStringBetween(getContentAsString(), "\"pid\":\"", "\"");
            } catch (PluginImplementationException e) {
                throw new PluginImplementationException("Error getting PID", e);
            }

            setFileStreamContentTypes(new String[0], new String[]{"application/smil"});
            httpMethod = getGetMethod("http://link.theplatform.com/s/h9dtGB/" + pid + "?format=SMIL&Tracking=true&mbr=true");
            if (!makeRedirectedRequest(httpMethod)) {
                checkProblems();
                throw new ServiceConnectionProblemException();
            }
            checkProblems();
            String releaseContent = getContentAsString();

            setConfig();
            String subtitleUrl = null;
            try {
                subtitleUrl = PlugUtils.getStringBetween(getContentAsString(), "<param name=\"ClosedCaptionURL\" value=\"", "\"");
            } catch (PluginImplementationException e) {
                logger.warning("Subtitle URL not found");
                LogUtils.processException(logger, e);
            }
            if (config.isDownloadSubtitles() && subtitleUrl != null && !subtitleUrl.isEmpty()) {
                SubtitleDownloader subtitleDownloader = new SubtitleDownloader();
                subtitleDownloader.downloadSubtitle(client, httpFile, subtitleUrl);
            }

            final RtmpSession rtmpSession = getRtmpSession(releaseContent);
            rtmpSession.getConnectParams().put("swfUrl", SWF_URL);
            rtmpSession.getConnectParams().put("pageUrl", fileURL);
            tryDownloadAndSaveFile(rtmpSession);
        } else {
            checkProblems();
            throw new ServiceConnectionProblemException();
        }
    }

    private void checkProblems() throws ErrorDuringDownloadingException {
        if (getContentAsString().contains("Not the page you were looking for")
                || getContentAsString().contains("Media is not available")) {
            throw new URLNotAvailableAnymoreException("File not found");
        }
        if (getContentAsString().contains("title=\"Unavailable\"")
                || getContentAsString().contains("This content is not available in your location")) {
            throw new NotRecoverableDownloadException("This content is not available in your location");
        }
    }

    private String getMediaId() throws ErrorDuringDownloadingException {
        Matcher matcher = PlugUtils.matcher("/ID/(\\d+)/", fileURL);
        if (!matcher.find()) {
            throw new PluginImplementationException("Media ID not found");
        }
        return matcher.group(1);
    }

    private RtmpSession getRtmpSession(String releaseContent) throws ErrorDuringDownloadingException {
        final String baseUrl = PlugUtils.replaceEntities(PlugUtils.getStringBetween(releaseContent, "<meta base=\"", "\""));
        final Set<Stream> streamSet = new HashSet<Stream>();
        final Matcher matcher = PlugUtils.matcher("<video src=\"(.+?)\" system\\-bitrate=\"(\\d+)\" height=\"(\\d+)\"", releaseContent);
        while (matcher.find()) {
            Stream stream = new Stream(PlugUtils.unescapeHtml(matcher.group(1)), Integer.parseInt(matcher.group(2)), Integer.parseInt(matcher.group(3)));
            streamSet.add(stream);
        }
        if (streamSet.isEmpty()) {
            throw new PluginImplementationException("No streams found");
        }
        for (Stream stream : streamSet) {
            logger.info("Found stream: " + stream);
        }

        //select quality
        Stream selectedStream = null;
        final int LOWER_QUALITY_PENALTY = 10;
        int weight = Integer.MAX_VALUE;
        for (Stream stream : streamSet) {
            int deltaQ = stream.quality - config.getVideoQuality().getQuality();
            int tempWeight = (deltaQ < 0 ? Math.abs(deltaQ) + LOWER_QUALITY_PENALTY : deltaQ);
            if (tempWeight < weight) {
                weight = tempWeight;
                selectedStream = stream;
            }
        }
        if (selectedStream == null) {
            throw new PluginImplementationException("Unable to select stream");
        }
        int selectedQuality = selectedStream.quality;

        //select the highest bitrate for the selected quality
        int selectedBitrate = Integer.MIN_VALUE;
        for (Stream stream : streamSet) {
            if ((stream.quality == selectedQuality) && (stream.bitrate > selectedBitrate)) {
                selectedBitrate = stream.bitrate;
                selectedStream = stream;
            }
        }

        logger.info("Settings config: " + config);
        logger.info("Selected stream: " + selectedStream);
        return new RtmpSession(baseUrl, selectedStream.getPlayName());
    }

    private static class Stream implements Comparable<Stream> {
        private final String playName;
        private final int bitrate;
        private final int quality; //height as quality

        public Stream(final String playName, final int bitrate, final int quality) {
            this.playName = playName;
            this.bitrate = bitrate;
            this.quality = quality;
        }

        public String getPlayName() {
            if (playName.contains(".mp4") && !playName.startsWith("mp4:")) {
                return "mp4:" + playName;
            } else {
                return playName;
            }
        }

        @SuppressWarnings("NullableProblems")
        @Override
        public int compareTo(final Stream that) {
            return Integer.valueOf(this.quality).compareTo(that.quality);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Stream stream = (Stream) o;
            return playName.equals(stream.playName);
        }

        @Override
        public int hashCode() {
            return playName.hashCode();
        }

        @Override
        public String toString() {
            return "Stream{" +
                    "playName='" + playName + '\'' +
                    ", bitrate=" + bitrate +
                    ", quality=" + quality +
                    '}';
        }
    }

}
