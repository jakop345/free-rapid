package cz.vity.freerapid.plugins.services.canalbrasilglobo;

import cz.vity.freerapid.plugins.exceptions.ErrorDuringDownloadingException;
import cz.vity.freerapid.plugins.exceptions.PluginImplementationException;
import cz.vity.freerapid.plugins.exceptions.ServiceConnectionProblemException;
import cz.vity.freerapid.plugins.exceptions.URLNotAvailableAnymoreException;
import cz.vity.freerapid.plugins.webclient.AbstractRunner;
import cz.vity.freerapid.plugins.webclient.DownloadState;
import cz.vity.freerapid.plugins.webclient.FileState;
import cz.vity.freerapid.plugins.webclient.utils.HttpUtils;
import cz.vity.freerapid.plugins.webclient.utils.JsonMapper;
import cz.vity.freerapid.plugins.webclient.utils.PlugUtils;
import cz.vity.freerapid.utilities.LogUtils;
import jlibs.core.net.URLUtil;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.codehaus.jackson.JsonNode;

import java.io.IOException;
import java.net.*;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class which contains main code
 *
 * @author tong2shot
 * @since 0.9u4
 */
class CanalBrasilGloboFileRunner extends AbstractRunner {
    private final static Logger logger = Logger.getLogger(CanalBrasilGloboFileRunner.class.getName());
    private final static String RESOURCE_ID = "resourceId";
    private final static String FNAME = "fname";
    private final static String DEFAULT_EXT = ".mp4";
    private final Random random = new Random();
    private SettingsConfig config;
    private String resourceIdFromUrl = null;


    private void setConfig() throws Exception {
        CanalBrasilGloboServiceImpl service = (CanalBrasilGloboServiceImpl) getPluginService();
        config = service.getConfig();
    }

    @Override
    public void runCheck() throws Exception {
        super.runCheck();
        if (isMultiparts(fileURL)) {
            return;
        }
        final GetMethod getMethod = getGetMethod(getPlaylistUrl(getVideoId(fileURL)));
        if (makeRedirectedRequest(getMethod)) {
            checkProblems();
            checkNameAndSize(getPlaylistRootNode(getContentAsString()));
        } else {
            checkProblems();
            throw new ServiceConnectionProblemException();
        }
    }

    private void checkNameAndSize(JsonNode playlistRootNode) throws ErrorDuringDownloadingException {
        String title = playlistRootNode.findPath("title").getTextValue();
        if (title == null) {
            throw new PluginImplementationException("Title not found");
        }
        String program = playlistRootNode.findPath("program").getTextValue();
        String filename = (program == null ? title : program + " - " + title) + DEFAULT_EXT;
        httpFile.setFileName(filename);
        httpFile.setFileState(FileState.CHECKED_AND_EXISTING);
    }

    @Override
    public void run() throws Exception {
        super.run();
        logger.info("Starting download in TASK " + fileURL);
        if (isMultiparts(fileURL)) {
            processMultiparts();
        }
        String videoId = getVideoId(fileURL);
        final GetMethod method = getGetMethod(getPlaylistUrl(videoId));
        if (makeRedirectedRequest(method)) {
            checkProblems();
            JsonNode playlistRootNode = getPlaylistRootNode(getContentAsString());
            if (resourceIdFromUrl == null) { //not multipart
                checkNameAndSize(playlistRootNode);
            }

            setConfig();
            List<CanalBrasilGloboVideo> availableVideos = getAvailableVideos(playlistRootNode);
            List<CanalBrasilGloboVideo> candidateSelectedVideos = getCandidateSelectedVideos(availableVideos); //for 'composite resource id' and 'video index' purpose ,
            List<CanalBrasilGloboVideo> selectedVideos = getSelectedVideos(availableVideos);
            logger.info("Config settings : " + config);
            logger.info("Selected video  : " + selectedVideos);

            if (selectedVideos.size() > 1) {
                queueMultiparts(selectedVideos);
            } else {
                CanalBrasilGloboVideo selectedVideo = selectedVideos.get(0);
                HttpMethod httpMethod = getMethodBuilder()
                        .setReferer(fileURL)
                        .setAction(String.format("http://security.video.globo.com/videos/%s/hash", videoId))
                        .setParameter("player", "flash")
                        .setParameter("version", "2.9.9.89")
                        .setParameter("resource_id", generateCompositeResourceId(candidateSelectedVideos))
                        .setParameter("_" + generateRandomString(3), String.valueOf(System.currentTimeMillis()))
                        .toGetMethod();
                if (!makeRedirectedRequest(httpMethod)) {
                    checkProblems();
                    throw new ServiceConnectionProblemException();
                }
                checkProblems();

                String hashContent = getContentAsString();
                logger.info(hashContent);
                String hash = getHash(hashContent, getVideoIndex(candidateSelectedVideos, selectedVideo.resourceId));

                httpMethod = getMethodBuilder()
                        .setReferer(fileURL)
                        .setAction(selectedVideo.url)
                        .setParameter("h", new Crypto(hash).sign())
                        .setParameter("k", "flash")
                        .toGetMethod();
                if (!tryDownloadAndSaveFile(httpMethod)) {
                    checkProblems();
                    throw new ServiceConnectionProblemException("Error starting download");
                }
            }
        } else {
            checkProblems();
            throw new ServiceConnectionProblemException();
        }
    }

    private void checkProblems() throws ErrorDuringDownloadingException {
        final String contentAsString = getContentAsString();
        if (contentAsString.contains("página não encontrada")
                || contentAsString.contains("\"message\":\"Not found\"")) {
            throw new URLNotAvailableAnymoreException("File not found");
        }
    }

    private String getVideoId(String fileUrl) throws PluginImplementationException {
        Matcher matcher = PlugUtils.matcher("/(\\d+)(?:\\.html|/$|/\\?)", fileUrl);
        if (!matcher.find()) {
            throw new PluginImplementationException("Video ID not found");
        }
        String videoId = matcher.group(1);
        logger.info("Video ID: " + videoId);
        return videoId;
    }

    private String getPlaylistUrl(String videoId) {
        return String.format("http://api.globovideos.com/videos/%s/playlist", videoId);
    }

    private String generateRandomString(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append((char) (97 + random.nextInt(25)));
        }
        return sb.toString();
    }

    private String generateCompositeResourceId(List<CanalBrasilGloboVideo> candidateSelectedVideos) {
        StringBuilder sb = new StringBuilder();
        for (CanalBrasilGloboVideo video : candidateSelectedVideos) {
            sb.append(video.resourceId).append("|");
        }
        sb.deleteCharAt(sb.length() - 1); //delete the last '|'
        return sb.toString();
    }

    private int getVideoIndex(List<CanalBrasilGloboVideo> candidateSelectedVideos, String resourceId) throws PluginImplementationException {
        int ret = -1;
        int idx = 0;
        for (CanalBrasilGloboVideo video : candidateSelectedVideos) {
            if (video.resourceId.equals(resourceId)) {
                ret = idx;
                break;
            }
            idx++;
        }
        if (ret == -1) {
            throw new PluginImplementationException("Error getting video index");
        }
        return ret;
    }

    private String getHash(String hashContent, int videoIndex) throws PluginImplementationException {
        String selectedHash = null;
        JsonNode hashNode;
        try {
            hashNode = new JsonMapper().getObjectMapper().readTree(hashContent).findPath("hash");
            if (!hashNode.isMissingNode()) {
                if (hashNode.isArray()) { //contains more than 1 item
                    int idx = 0;
                    for (JsonNode hashItem : hashNode) {
                        if (idx == videoIndex) {
                            selectedHash = hashItem.getTextValue();
                            break;
                        }
                        idx++;
                    }
                } else { //only 1
                    selectedHash = hashNode.getTextValue();
                }
            }
        } catch (IOException e) {
            throw new PluginImplementationException("Error parsing hash content", e);
        }
        if (selectedHash == null) {
            throw new PluginImplementationException("Error getting hash");
        }
        return selectedHash;
    }

    private JsonNode getPlaylistRootNode(String playlistContent) throws PluginImplementationException {
        JsonNode rootNode;
        try {
            rootNode = new JsonMapper().getObjectMapper().readTree(playlistContent);
        } catch (IOException e) {
            throw new PluginImplementationException("Error getting playlist root node");
        }
        return rootNode;
    }

    private List<CanalBrasilGloboVideo> getAvailableVideos(JsonNode playlistRootNode) throws PluginImplementationException {
        List<CanalBrasilGloboVideo> videoList = new LinkedList<CanalBrasilGloboVideo>();
        try {
            List<JsonNode> resourcesNodes = playlistRootNode.findValues("resources"); //can be multiple nodes
            for (JsonNode resourcesNode : resourcesNodes) {
                for (JsonNode resourceItem : resourcesNode) {
                    JsonNode playersNodes = resourceItem.get("players");
                    if (playersNodes == null) { //skip non player resources, eg.thumbnail
                        continue;
                    }
                    boolean isFlash = false;
                    for (JsonNode playersNode : playersNodes) {
                        if (playersNode.getTextValue().equalsIgnoreCase("flash")) {
                            isFlash = true;
                            break;
                        }
                    }
                    if (!isFlash) { //only support flash
                        continue;
                    }
                    String resourceId = resourceItem.get("_id").getTextValue();
                    int height = resourceItem.get("height").getIntValue(); //height as quality
                    String url = resourceItem.get("url").getTextValue();
                    CanalBrasilGloboVideo video = new CanalBrasilGloboVideo(resourceId, height, url);
                    videoList.add(video);
                    logger.info("Found: " + video);
                }
            }
        } catch (Exception e) {
            throw new PluginImplementationException("Error parsing playlist");
        }
        if (videoList.isEmpty()) {
            throw new PluginImplementationException("No available video");
        }
        return videoList;
    }

    private List<CanalBrasilGloboVideo> getSelectedVideos(List<CanalBrasilGloboVideo> videoList) throws PluginImplementationException {
        CanalBrasilGloboVideo selectedVideo = Collections.min(videoList);
        int selectedQuality = selectedVideo.videoQuality;
        List<CanalBrasilGloboVideo> selectedVideos = new LinkedList<CanalBrasilGloboVideo>();
        for (CanalBrasilGloboVideo video : videoList) {
            String resourceId = video.resourceId;
            if ((resourceIdFromUrl != null) && (!resourceId.equals(resourceIdFromUrl))) {
                continue;
            }
            if (video.videoQuality == selectedQuality) {
                selectedVideos.add(video);
            }
        }
        return selectedVideos;
    }

    private List<CanalBrasilGloboVideo> getCandidateSelectedVideos(List<CanalBrasilGloboVideo> videoList) throws PluginImplementationException {
        CanalBrasilGloboVideo selectedVideo = Collections.min(videoList);
        int selectedQuality = selectedVideo.videoQuality;
        List<CanalBrasilGloboVideo> selectedVideos = new LinkedList<CanalBrasilGloboVideo>();
        for (CanalBrasilGloboVideo video : videoList) {
            if (video.videoQuality == selectedQuality) {
                selectedVideos.add(video);
            }
        }
        return selectedVideos;
    }

    private void queueMultiparts(List<CanalBrasilGloboVideo> videoList) throws Exception {
        List<URI> list = new LinkedList<URI>();
        for (int i = 0; i < videoList.size(); i++) {
            CanalBrasilGloboVideo video = videoList.get(i);
            String resourceIdParam = RESOURCE_ID + "=" + URLEncoder.encode(video.resourceId, "UTF-8");
            String fnameParam = "&" + FNAME + "=" + URLEncoder.encode(httpFile.getFileName().replaceFirst(Pattern.quote(DEFAULT_EXT) + "$", "-" + (i + 1)), "UTF-8");
            try {
                list.add(new URI(fileURL + "?" + resourceIdParam + fnameParam));
            } catch (final URISyntaxException e) {
                LogUtils.processException(logger, e);
            }
        }
        if (list.isEmpty()) {
            throw new PluginImplementationException("No videos available");
        }
        getPluginService().getPluginContext().getQueueSupport().addLinksToQueue(httpFile, list);
        httpFile.setState(DownloadState.COMPLETED);
        httpFile.getProperties().put("removeCompleted", true);
        logger.info(list.size() + " videos added");
    }

    private boolean isMultiparts(String fileUrl) {
        return fileUrl.contains("?" + RESOURCE_ID + "=");
    }

    private void processMultiparts() throws Exception {
        URL url = new URL(fileURL);
        String filename = null;
        try {
            filename = URLUtil.getQueryParams(url.toString(), "UTF-8").get(FNAME);
        } catch (Exception e) {
            //
        }
        if (filename == null) {
            throw new PluginImplementationException("File name not found");
        }
        httpFile.setFileName(HttpUtils.replaceInvalidCharsForFileSystem(URLDecoder.decode(filename, "UTF-8") + DEFAULT_EXT, "_"));
        httpFile.setFileState(FileState.CHECKED_AND_EXISTING);

        resourceIdFromUrl = null;
        try {
            resourceIdFromUrl = URLUtil.getQueryParams(fileURL, "UTF-8").get(RESOURCE_ID);
        } catch (Exception e) {
            //
        }
        if (resourceIdFromUrl == null) {
            throw new PluginImplementationException("Switch item ID param not found");
        }

        fileURL = fileURL.replaceFirst("\\?" + RESOURCE_ID + "=.+", "");
    }

    private class CanalBrasilGloboVideo implements Comparable<CanalBrasilGloboVideo> {
        private final static int LOWER_QUALITY_PENALTY = 10;
        private final String resourceId;
        private final int videoQuality;
        private final String url;
        private final int weight;

        public CanalBrasilGloboVideo(final String resourceId, final int videoQuality, final String url) {
            this.resourceId = resourceId;
            this.videoQuality = videoQuality;
            this.url = url;
            this.weight = calcWeight();
        }

        private int calcWeight() {
            VideoQuality configQuality = config.getVideoQuality();
            int deltaQ = videoQuality - configQuality.getQuality();
            return (deltaQ < 0 ? Math.abs(deltaQ) + LOWER_QUALITY_PENALTY : deltaQ);
        }

        @SuppressWarnings("NullableProblems")
        @Override
        public int compareTo(final CanalBrasilGloboVideo that) {
            return Integer.valueOf(this.weight).compareTo(that.weight);
        }

        @Override
        public String toString() {
            return "CanalBrasilGloboVideo{" +
                    "resourceId='" + resourceId + '\'' +
                    ", videoQuality=" + videoQuality +
                    ", url='" + url + '\'' +
                    ", weight=" + weight +
                    '}';
        }
    }

}
