package cz.vity.freerapid.plugins.services.cliphunter;

import cz.vity.freerapid.plugins.exceptions.ErrorDuringDownloadingException;
import cz.vity.freerapid.plugins.exceptions.PluginImplementationException;
import cz.vity.freerapid.plugins.exceptions.ServiceConnectionProblemException;
import cz.vity.freerapid.plugins.exceptions.URLNotAvailableAnymoreException;
import cz.vity.freerapid.plugins.webclient.AbstractRunner;
import cz.vity.freerapid.plugins.webclient.FileState;
import cz.vity.freerapid.plugins.webclient.utils.JsonMapper;
import cz.vity.freerapid.plugins.webclient.utils.PlugUtils;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class which contains main code
 *
 * @author tong2shot
 * @since 0.9u4
 */
class ClipHunterFileRunner extends AbstractRunner {
    private final static Logger logger = Logger.getLogger(ClipHunterFileRunner.class.getName());
    private SettingsConfig config;
    private final static String DEFAULT_EXT = ".mp4";

    private void setConfig() throws Exception {
        ClipHunterServiceImpl service = (ClipHunterServiceImpl) getPluginService();
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
        PlugUtils.checkName(httpFile, content, "var mediaTitle = \"", "\"");
        httpFile.setFileName(httpFile.getFileName() + DEFAULT_EXT);
        httpFile.setFileState(FileState.CHECKED_AND_EXISTING);
    }

    @Override
    public void run() throws Exception {
        super.run();
        logger.info("Starting download in TASK " + fileURL);
        final GetMethod method = getGetMethod(fileURL);
        if (makeRedirectedRequest(method)) {
            final String contentAsString = getContentAsString();
            checkProblems();
            checkNameAndSize(contentAsString);

            Matcher matcher = getMatcherAgainstContent("src=\"(http://s\\.gexo[^\"]+?player(?:_new|_old)?\\.js)\"");
            if (!matcher.find()) {
                throw new PluginImplementationException("Player JS not found");
            }
            String jsPlayer = matcher.group(1).trim();
            logger.info("JS player: " + jsPlayer);
            HttpMethod httpMethod = getMethodBuilder().setReferer(fileURL).setAction(jsPlayer).toGetMethod();
            if (!makeRedirectedRequest(httpMethod)) {
                checkProblems();
                throw new ServiceConnectionProblemException();
            }
            checkProblems();
            String jsPlayerContent = getContentAsString();
            matcher = PlugUtils.matcher(",decrypt:function([^,]+?}),", jsPlayerContent);
            String decryptFunc;
            if (!matcher.find()) {
                throw new PluginImplementationException("Decrypt func not found");
            } else {
                decryptFunc = "function decrypt" + matcher.group(1) + ";";
            }

            setConfig();
            ClipHunterVideo selectedVideo = getSelectedVideo(contentAsString, decryptFunc);
            String suggestedFilename = PlugUtils.suggestFilename(selectedVideo.url);
            String ext = suggestedFilename.contains(".") ? suggestedFilename.substring(suggestedFilename.lastIndexOf(".")) : null;
            if (ext != null) {
                httpFile.setFileName(httpFile.getFileName().replaceFirst(Pattern.quote(DEFAULT_EXT) + "$", ext));
            }
            httpMethod = getMethodBuilder().setReferer(fileURL).setAction(selectedVideo.url).toGetMethod();
            if (!tryDownloadAndSaveFile(httpMethod)) {
                checkProblems();
                throw new ServiceConnectionProblemException("Error starting download");
            }
        } else {
            checkProblems();
            throw new ServiceConnectionProblemException();
        }
    }

    private void checkProblems() throws ErrorDuringDownloadingException {
        final String contentAsString = getContentAsString();
        if (contentAsString.contains("This Video is not available")
                || contentAsString.contains("This video was removed")) {
            throw new URLNotAvailableAnymoreException("File not found");
        }
    }

    private static String decryptUrl(final ScriptEngine engine, final String func, final String toDecrypt) {
        Object result;
        final Invocable inv = (Invocable) engine;
        try {
            engine.eval(func);
            result = inv.invokeFunction("decrypt", toDecrypt);
        } catch (final Throwable e) {
            return null;
        }
        return result != null ? result.toString() : null;
    }


    private ClipHunterVideo getSelectedVideo(String content, String decryptFunc) throws Exception {
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("javascript");
        ObjectMapper objectMapper = new JsonMapper().getObjectMapper();

        Matcher matcher = PlugUtils.matcher("var player_btns\\s*?=\\s*?(.+?);", content);
        if (!matcher.find()) {
            throw new PluginImplementationException("Player buttons content not found");
        }
        JsonNode playerButtonsRootNode;
        try {
            playerButtonsRootNode = objectMapper.readTree(matcher.group(1));
        } catch (IOException e) {
            throw new PluginImplementationException("Error parsing player buttons");
        }
        Map<String, Integer> qualityMap = new HashMap<String, Integer>(); //k=filename v=height
        Iterator<Map.Entry<String, JsonNode>> playerButtonsIter = playerButtonsRootNode.getFields();
        while (playerButtonsIter.hasNext()) {
            Map.Entry<String, JsonNode> next = playerButtonsIter.next();
            qualityMap.put(next.getKey(), next.getValue().get("h").getIntValue());
        }
        if (qualityMap.isEmpty()) {
            throw new PluginImplementationException("Video quality is empty");
        }

        matcher = PlugUtils.matcher("var mp4json\\s*?=\\s*?(\\[.+?\\]);", content);
        if (!matcher.find()) {
            throw new PluginImplementationException("MP4 json not found");
        }
        String mp4json = matcher.group(1);
        JsonNode mp4jsonRootNode;
        try {
            mp4jsonRootNode = objectMapper.readTree(mp4json);
        } catch (IOException e) {
            throw new PluginImplementationException("Error parsing MP4 json");
        }

        List<ClipHunterVideo> videoList = new LinkedList<ClipHunterVideo>();
        for (JsonNode item : mp4jsonRootNode) {
            String fname = item.findPath("fname").getTextValue();
            String url = item.findPath("sUrl").getTextValue();
            if ((fname == null) || (url == null) || !qualityMap.containsKey(fname)) {
                throw new PluginImplementationException("Error parsing MP4 json (2)");
            }
            ClipHunterVideo video = new ClipHunterVideo(qualityMap.get(fname), decryptUrl(engine, decryptFunc, url));
            logger.info("Found video: " + video);
            videoList.add(video);
        }
        if (videoList.isEmpty()) {
            throw new PluginImplementationException("No available video");
        }
        ClipHunterVideo selectedVideo = Collections.min(videoList);
        logger.info("Config settings : " + config);
        logger.info("Selected video  : " + selectedVideo);
        return selectedVideo;
    }

    private class ClipHunterVideo implements Comparable<ClipHunterVideo> {
        private final static int NEAREST_LOWER_PENALTY = 10;
        private final int videoQuality;
        private final String url;
        private int weight;

        public ClipHunterVideo(final int videoQuality, final String url) {
            this.videoQuality = videoQuality;
            this.url = url;
            this.weight = calcWeight();
        }

        private int calcWeight() {
            VideoQuality configQuality = config.getVideoQuality();
            int deltaQ = videoQuality - configQuality.getQuality();
            return (deltaQ < 0 ? Math.abs(deltaQ) + NEAREST_LOWER_PENALTY : deltaQ); //prefer nearest better if the same quality doesn't exist
        }

        @SuppressWarnings("NullableProblems")
        @Override
        public int compareTo(final ClipHunterVideo that) {
            return Integer.valueOf(this.weight).compareTo(that.weight);
        }

        @Override
        public boolean equals(Object obj) {
            return obj != null && obj instanceof ClipHunterVideo && ((ClipHunterVideo) obj).url.equals(this.url);
        }

        @Override
        public String toString() {
            return "ClipHunterVideo{" +
                    "videoQuality=" + videoQuality +
                    ", url='" + url + '\'' +
                    ", weight=" + weight +
                    '}';
        }
    }

}
