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
    private final static String CLIENT_ID = "b45b1aa10f1ac2941910a7f0d10f8e28";

    @Override
    public void runCheck() throws Exception {
        super.runCheck();
        checkNameAndSize(getTrackInfoNode(new JsonMapper().getObjectMapper()));
    }

    private void checkNameAndSize(JsonNode trackInfoNode) throws ErrorDuringDownloadingException {
        final String title = trackInfoNode.findPath("title").getTextValue();
        if (title == null) {
            throw new PluginImplementationException("File name not found");
        }
        httpFile.setFileName(title.trim() + (trackInfoNode.findPath("download_url").isMissingNode() ? ".flv" : ".mp3"));

        final int contentSize = trackInfoNode.findPath("original_content_size").getIntValue();
        httpFile.setFileSize(contentSize);
        httpFile.setFileState(FileState.CHECKED_AND_EXISTING);
    }

    @Override
    public void run() throws Exception {
        super.run();
        logger.info("Starting download in TASK " + fileURL);
        ObjectMapper mapper = new JsonMapper().getObjectMapper();
        JsonNode trackInfoNode = getTrackInfoNode(mapper);
        checkNameAndSize(trackInfoNode);

        HttpMethod method;
        String url = trackInfoNode.findPath("download_url").getTextValue();
        if (url != null) { //downloadable
            method = getMethodBuilder()
                    .setReferer(fileURL)
                    .setAction(url)
                    .setParameter("client_id", CLIENT_ID)
                    .toGetMethod();
            if (!tryDownloadAndSaveFile(method)) {
                checkProblems();
                throw new ServiceConnectionProblemException("Error starting download");
            }
        } else { //stream

            /*
            url = trackInfoNode.findPath("stream_url").getTextValue();
            if (url == null) {
                throw new PluginImplementationException("Download URL not found");
            }
            url.replace("/tracks/", "/i1/tracks/").replace("/stream", "/streams")
            */

            String id = trackInfoNode.findPath("id").getValueAsText();
            if (id == null) {
                throw new PluginImplementationException("Media ID not found");
            }
            method = getMethodBuilder()
                    .setReferer(fileURL)
                    .setAction(String.format("https://api.soundcloud.com/i1/tracks/%s/streams", id))
                    .setParameter("client_id", CLIENT_ID)
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
            String streamUrl = streamsNode.findPath("rtmp_mp3_128_url").getTextValue();
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

    private JsonNode getTrackInfoNode(ObjectMapper mapper) throws Exception {
        HttpMethod method = getGetMethod("https://api.sndcdn.com/resolve?url=" + fileURL.replace(":", "%3A") + "&_status_format=json&client_id=" + CLIENT_ID);
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