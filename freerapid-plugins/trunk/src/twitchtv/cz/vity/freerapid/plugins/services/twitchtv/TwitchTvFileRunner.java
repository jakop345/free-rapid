package cz.vity.freerapid.plugins.services.twitchtv;

import cz.vity.freerapid.plugins.exceptions.ErrorDuringDownloadingException;
import cz.vity.freerapid.plugins.exceptions.PluginImplementationException;
import cz.vity.freerapid.plugins.exceptions.ServiceConnectionProblemException;
import cz.vity.freerapid.plugins.exceptions.URLNotAvailableAnymoreException;
import cz.vity.freerapid.plugins.services.applehls.HlsDownloader;
import cz.vity.freerapid.plugins.webclient.AbstractRunner;
import cz.vity.freerapid.plugins.webclient.DownloadClientConsts;
import cz.vity.freerapid.plugins.webclient.DownloadState;
import cz.vity.freerapid.plugins.webclient.FileState;
import cz.vity.freerapid.plugins.webclient.utils.HttpUtils;
import cz.vity.freerapid.plugins.webclient.utils.JsonMapper;
import cz.vity.freerapid.plugins.webclient.utils.PlugUtils;
import cz.vity.freerapid.utilities.LogUtils;
import jlibs.core.net.URLUtil;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.codehaus.jackson.JsonNode;

import java.io.IOException;
import java.net.*;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;

/**
 * Class which contains main code
 *
 * @author tong2shot
 */
class TwitchTvFileRunner extends AbstractRunner {
    private final static Logger logger = Logger.getLogger(TwitchTvFileRunner.class.getName());
    private final static String TITLE = "title";
    private final static String ITEM_POS = "itempos";
    private String itemPosFromUrl = null;

    @Override
    public void runCheck() throws Exception {
        super.runCheck();
        if (isVideoUrl(fileURL)) {
            return;
        }
        if (isMultipartsItem(fileURL)) {
            return;
        }
        final GetMethod getMethod = getGetMethod(fileURL);
        if (makeRedirectedRequest(getMethod)) {
            isAtHomePage(getMethod);
            checkProblems();
            checkNameAndSize(getVideoInfoNode(getVideoId(), isVod()));
        } else {
            checkProblems();
            throw new ServiceConnectionProblemException();
        }
    }

    private void checkNameAndSize(JsonNode videoInfoNode) throws ErrorDuringDownloadingException {
        String title = videoInfoNode.findPath("title").getTextValue();
        if (title == null) {
            throw new PluginImplementationException("File name not found");
        }
        httpFile.setFileName(PlugUtils.unescapeHtml(title.trim()).replaceAll("\\s", " "));
        httpFile.setFileState(FileState.CHECKED_AND_EXISTING);
    }

    @Override
    public void run() throws Exception {
        super.run();
        logger.info("Starting download in TASK " + fileURL);
        if (isVideoUrl(fileURL)) {
            processVideoUrl();
            return;
        }
        if (isMultipartsItem(fileURL)) {
            processMultipartsItem();
        }
        final GetMethod method = getGetMethod(fileURL);
        if (makeRedirectedRequest(method)) {
            isAtHomePage(method);
            checkProblems();
            String videoId = getVideoId();
            boolean isVod = isVod();
            if (itemPosFromUrl == null) { //not multipart
                checkNameAndSize(getVideoInfoNode(videoId, isVod));
            }

            if (!isVod) {
                HttpMethod httpMethod = getGetMethod(String.format("https://api.twitch.tv/api/videos/%s?as3=t", videoId));
                if (!makeRedirectedRequest(httpMethod)) {
                    checkProblems();
                    throw new ServiceConnectionProblemException();
                }
                checkProblems();
                List<TwitchTvVideo> selectedVideos = getSelectedVideos(getContentAsString());
                if (selectedVideos.size() > 1) {
                    queueMultiparts(selectedVideos);
                } else {
                    TwitchTvVideo selectedVideo = selectedVideos.get(0);
                    logger.info("Selected video: " + selectedVideo);
                    String fileNameFromUrl = PlugUtils.suggestFilename(selectedVideo.url);
                    String fileExt = fileNameFromUrl.substring(fileNameFromUrl.lastIndexOf("."));
                    httpFile.setFileName(httpFile.getFileName() + fileExt);
                    httpFile.setResumeSupported(true); //force resume
                    setClientParameter(DownloadClientConsts.IGNORE_ACCEPT_RANGES, true);
                    httpMethod = getMethodBuilder().setReferer(fileURL).setAction(selectedVideo.url).toGetMethod();
                    if (!tryDownloadAndSaveFile(httpMethod)) {
                        checkProblems();
                        throw new ServiceConnectionProblemException("Error starting download");
                    }
                }
            } else {
                HttpMethod httpMethod = getGetMethod(String.format("https://api.twitch.tv/api/vods/%s/access_token?as3=t", videoId));
                if (!makeRedirectedRequest(httpMethod)) {
                    checkProblems();
                    throw new ServiceConnectionProblemException();
                }
                checkProblems();

                JsonNode rootNode;
                try {
                    rootNode = new JsonMapper().getObjectMapper().readTree(getContentAsString().replaceAll("[\\n\\r]", ""));
                } catch (IOException e) {
                    throw new PluginImplementationException("Error getting VOD root node");
                }
                JsonNode tokenNode = rootNode.findPath("token");
                JsonNode sigNode = rootNode.findPath("sig");
                if (tokenNode.isMissingNode() || sigNode.isMissingNode()) {
                    throw new PluginImplementationException("Error getting 'token' or 'sig' node");
                }

                httpMethod = getMethodBuilder()
                        .setReferer(fileURL)
                        .setAction("http://usher.justin.tv/vod/" + videoId)
                        .setAndEncodeParameter("nauth", tokenNode.getTextValue())
                        .setParameter("nauthsig", sigNode.getTextValue())
                        .toGetMethod();
                httpFile.setFileName(httpFile.getFileName() + ".ts");
                HlsDownloader hlsDownloader = new HlsDownloader(client, httpFile, downloadTask);
                hlsDownloader.tryDownloadAndSaveFile(httpMethod.getURI().toString());
            }
        } else {
            checkProblems();
            throw new ServiceConnectionProblemException();
        }
    }

    private String getVideoId() throws PluginImplementationException {
        Matcher matcher = PlugUtils.matcher("/([^/])/(\\d+)$", fileURL);
        if (!matcher.find()) {
            throw new PluginImplementationException("Video ID not found");
        }
        String group1 = matcher.group(1);
        return (group1.equals("b") ? "a" : (group1.equals("v") ? "" : "c")) + matcher.group(2);
    }

    private boolean isVod() throws PluginImplementationException {
        Matcher matcher = PlugUtils.matcher("/([^/])/(\\d+)$", fileURL);
        if (!matcher.find()) {
            throw new PluginImplementationException("Video ID not found");
        }
        return matcher.group(1).equals("v");
    }

    private JsonNode getVideoInfoNode(String videoId, boolean isVod) throws Exception {
        GetMethod getMethod = getGetMethod(String.format("http://api.twitch.tv/kraken/videos/%s?on_site=1", (isVod ? "v" : "") + videoId));
        if (!makeRedirectedRequest(getMethod)) {
            checkProblems();
            throw new ServiceConnectionProblemException();
        }
        checkProblems();
        JsonNode rootNode;
        try {
            rootNode = new JsonMapper().getObjectMapper().readTree(getContentAsString());
        } catch (IOException e) {
            throw new PluginImplementationException("Error parsing video info");
        }
        return rootNode;
    }

    private void checkProblems() throws ErrorDuringDownloadingException {
        final String contentAsString = getContentAsString();
        if (contentAsString.contains("I'm sorry, that page is in another castle")) {
            throw new URLNotAvailableAnymoreException("File not found");
        }
    }

    private void isAtHomePage(final HttpMethod method) throws URLNotAvailableAnymoreException, URIException {
        if (method.getURI().toString().matches("http://(?:.+?\\.)?twitch\\.tv/.+?/videos/?")) {
            throw new URLNotAvailableAnymoreException("File not found");
        }
    }

    private List<TwitchTvVideo> getSelectedVideos(String playlistContent) throws PluginImplementationException {
        String title = httpFile.getFileName();
        List<TwitchTvVideo> videoList = new LinkedList<TwitchTvVideo>();
        JsonNode rootNode;
        try {
            rootNode = new JsonMapper().getObjectMapper().readTree(playlistContent);
        } catch (IOException e) {
            throw new PluginImplementationException("Error getting video root node");
        }
        try {
            JsonNode chunksNode = rootNode.get("chunks");
            //for multi quality, pick non-live, as live sometimes cannot be downloaded.
            //only support 480p for non-live, can't find sample for other qualities.
            JsonNode mediaNodes = (chunksNode.get("480p") != null ? chunksNode.get("480p") : chunksNode.get("live"));
            int itemPos = 0;
            for (JsonNode mediaNode : mediaNodes) {
                itemPos++;
                String strItemPos = String.valueOf(itemPos);
                String url = mediaNode.get("url").getTextValue();
                if ((itemPosFromUrl != null) && (!strItemPos.equals(itemPosFromUrl))) {
                    continue;
                }
                videoList.add(new TwitchTvVideo(strItemPos, title, url));
            }
        } catch (Exception e) {
            throw new PluginImplementationException("Error parsing JSON content", e);
        }
        if (videoList.isEmpty()) {
            throw new PluginImplementationException("No videos found");
        }
        return videoList;
    }

    private void queueMultiparts(List<TwitchTvVideo> videoList) throws Exception {
        List<URI> list = new LinkedList<URI>();
        for (int i = 0; i < videoList.size(); i++) {
            TwitchTvVideo video = videoList.get(i);
            String itemIdParam = ITEM_POS + "=" + URLEncoder.encode(video.itemPos, "UTF-8");
            String titleParam = "&" + TITLE + "=" + URLEncoder.encode(httpFile.getFileName() + "-" + (i + 1), "UTF-8");
            try {
                list.add(new URI(fileURL + "?" + itemIdParam + titleParam));
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

    private boolean isMultipartsItem(String fileUrl) {
        return fileUrl.contains("?" + ITEM_POS + "=");
    }

    private void processMultipartsItem() throws Exception {
        URL url = new URL(fileURL);
        String filename = null;
        try {
            filename = URLUtil.getQueryParams(url.toString(), "UTF-8").get(TITLE);
        } catch (Exception e) {
            //
        }
        if (filename == null) {
            throw new PluginImplementationException("File name not found");
        }
        httpFile.setFileName(HttpUtils.replaceInvalidCharsForFileSystem(URLDecoder.decode(filename, "UTF-8"), "_"));
        httpFile.setFileState(FileState.CHECKED_AND_EXISTING);

        itemPosFromUrl = null;
        try {
            itemPosFromUrl = URLUtil.getQueryParams(fileURL, "UTF-8").get(ITEM_POS);
        } catch (Exception e) {
            //
        }
        if (itemPosFromUrl == null) {
            throw new PluginImplementationException("Item pos param not found");
        }

        fileURL = fileURL.replaceFirst("\\?" + ITEM_POS + "=.+", "");
    }

    //for backward compatibility purpose, version < 1.2.0.
    private boolean isVideoUrl(final String url) {
        return url.matches("http://media-cdn\\.twitch\\.tv/[^/]+?/archives/.+?/.+?\\..{3}.*");
    }

    //for backward compatibility purpose, version < 1.2.0.
    private void processVideoUrl() throws Exception {
        //http://media-cdn.twitch.tv/store48.media48/archives/2012-9-3/format_480p_330898023.flv -> original videoUrl
        //http://media-cdn.twitch.tv/store48.media48/archives/2012-9-3/format_480p_330898023.flv?title=Kings of Poverty for RAINN!-Mystery Tournament_2 -> title+"_"+counter added
        URL url = new URL(fileURL);
        String title = null;
        try {
            title = URLUtil.getQueryParams(fileURL, "UTF-8").get(TITLE);
        } catch (Exception e) {
            //
        }
        fileURL = url.getProtocol() + "://" + url.getAuthority() + url.getPath();
        final String extension = fileURL.substring(fileURL.lastIndexOf("."));
        final String filename = (title == null ? PlugUtils.suggestFilename(fileURL) : URLDecoder.decode(title + extension, "UTF-8"));
        httpFile.setFileName(HttpUtils.replaceInvalidCharsForFileSystem(filename, "_"));
        client.setReferer(httpFile.getFileUrl().getProtocol() + "://" + httpFile.getFileUrl().getAuthority());
        final GetMethod method = getGetMethod(fileURL);
        if (!tryDownloadAndSaveFile(method)) {
            checkProblems();
            throw new ServiceConnectionProblemException("Error starting download");
        }
    }

    private class TwitchTvVideo {
        private final String itemPos;
        private final String title;
        private final String url;

        public TwitchTvVideo(final String itemPos, final String title, final String url) {
            this.itemPos = itemPos;
            this.title = title;
            this.url = url;
        }

        @Override
        public String toString() {
            return "TwitchTvVideo{" +
                    "itemPos='" + itemPos + '\'' +
                    ", title='" + title + '\'' +
                    ", url='" + url + '\'' +
                    '}';
        }
    }

}