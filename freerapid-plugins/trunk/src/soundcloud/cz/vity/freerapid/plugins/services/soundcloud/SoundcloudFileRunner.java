package cz.vity.freerapid.plugins.services.soundcloud;

import cz.vity.freerapid.plugins.exceptions.ErrorDuringDownloadingException;
import cz.vity.freerapid.plugins.exceptions.PluginImplementationException;
import cz.vity.freerapid.plugins.exceptions.ServiceConnectionProblemException;
import cz.vity.freerapid.plugins.exceptions.URLNotAvailableAnymoreException;
import cz.vity.freerapid.plugins.services.rtmp.RtmpDownloader;
import cz.vity.freerapid.plugins.services.rtmp.RtmpSession;
import cz.vity.freerapid.plugins.webclient.AbstractRunner;
import cz.vity.freerapid.plugins.webclient.FileState;
import cz.vity.freerapid.plugins.webclient.utils.JsonMapper;
import cz.vity.freerapid.plugins.webclient.utils.PlugUtils;
import org.apache.commons.httpclient.HttpMethod;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.logging.Logger;
import java.util.regex.Matcher;

/**
 * Class which contains main code
 *
 * @author Vity
 * @author ntoskrnl
 * @author Abinash Bishoyi
 * @author tong2shot
 */
class SoundcloudFileRunner extends AbstractRunner {
    private final static Logger logger = Logger.getLogger(SoundcloudFileRunner.class.getName());
    private final static String CLIENT_ID = "02gUJC0hH2ct1EGOcYXQIzRFU91c72Ea";
    private final static String APP_VERSION = "f3cc0b3";

    @Override
    public void runCheck() throws Exception {
        super.runCheck();
        checkNameAndSize(getTrackInfoNode(new JsonMapper().getObjectMapper(), getMediaId()));
    }

    private void checkNameAndSize(JsonNode trackInfoNode) throws ErrorDuringDownloadingException {
        final String title = trackInfoNode.findPath("title").getTextValue();
        if (title == null) {
            throw new PluginImplementationException("File name not found");
        }
        httpFile.setFileName(title.trim() + ".flv");

        final int contentSize = trackInfoNode.findPath("original_content_size").getIntValue();
        httpFile.setFileSize(contentSize);
        httpFile.setFileState(FileState.CHECKED_AND_EXISTING);
    }

    @Override
    public void run() throws Exception {
        super.run();
        logger.info("Starting download in TASK " + fileURL);

        String mediaId = getMediaId();
        ObjectMapper mapper = new JsonMapper().getObjectMapper();
        JsonNode trackInfoNode = getTrackInfoNode(mapper, mediaId);
        checkNameAndSize(trackInfoNode);

        HttpMethod method = getMethodBuilder()
                .setReferer(fileURL)
                .setAction(String.format("https://api.soundcloud.com/i1/tracks/%s/streams", mediaId))
                .setParameter("client_id", CLIENT_ID)
                .setParameter("app_version", APP_VERSION)
                .toGetMethod();
        if (!makeRedirectedRequest(method)) {
            checkProblems();
            throw new ServiceConnectionProblemException();
        }
        checkProblems();

        JsonNode streamsNode;
        try {
            streamsNode = mapper.readTree(getContentAsString());
        } catch (IOException e) {
            throw new PluginImplementationException("Error parsing streams info", e);
        }
        String streamUrl = streamsNode.findPath("http_mp3_128_url").getTextValue();
        if (streamUrl != null) {
            httpFile.setFileName(httpFile.getFileName().replaceFirst("\\..{3,4}$", ".mp3"));
            method = getMethodBuilder()
                    .setReferer(fileURL)
                    .setAction(streamUrl)
                    .toGetMethod();
            if (!tryDownloadAndSaveFile(method)) {
                checkProblems();
                throw new ServiceConnectionProblemException("Error starting download");
            }
        } else {
            streamUrl = streamsNode.findPath("rtmp_mp3_128_url").getTextValue();
            if (streamUrl == null) {
                throw new PluginImplementationException("Stream URL not found");
            }
            Matcher matcher = PlugUtils.matcher("rtmp://([^/]+)/(.+)", streamUrl);
            if (!matcher.find()) {
                throw new PluginImplementationException("Invalid RTMP URL");
            }
            RtmpSession session = new RtmpSession(matcher.group(1), 1935, "", matcher.group(2));
            new RtmpDownloader(client, downloadTask).tryDownloadAndSaveFile(session);
        }
    }

    private void checkProblems() throws ErrorDuringDownloadingException {
        if (getContentAsString().contains("404 - Not Found")) {
            throw new URLNotAvailableAnymoreException("File not found");
        }
    }

    private String getMediaId() throws IOException, ErrorDuringDownloadingException {
        HttpMethod method = getGetMethod(fileURL);
        if (!makeRedirectedRequest(method)) {
            checkProblems();
            throw new ServiceConnectionProblemException();
        }
        checkProblems();

        Matcher matcher = getMatcherAgainstContent("\"urn\":\"soundcloud:tracks:(\\d+)");
        if (!matcher.find()) {
            throw new PluginImplementationException("Media ID not found");
        }
        return matcher.group(1);
    }

    private JsonNode getTrackInfoNode(ObjectMapper mapper, String mediaId) throws Exception {
        HttpMethod method = getMethodBuilder()
                .setReferer(fileURL)
                .setAction("https://api-v2.soundcloud.com/tracks")
                .setParameter("urns", "soundcloud%3Atracks%3A" + mediaId)
                .setParameter("client_id", CLIENT_ID)
                .setParameter("app_version", APP_VERSION)
                .toGetMethod();
        if (!makeRedirectedRequest(method)) {
            checkProblems();
            throw new ServiceConnectionProblemException();
        }
        checkProblems();

        JsonNode trackInfoNode;
        try {
            trackInfoNode = mapper.readTree(getContentAsString());
        } catch (IOException e) {
            throw new PluginImplementationException("Error parsing track info", e);
        }
        return trackInfoNode;
    }

}