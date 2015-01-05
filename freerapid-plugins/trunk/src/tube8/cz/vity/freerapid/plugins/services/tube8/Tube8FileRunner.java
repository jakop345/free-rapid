package cz.vity.freerapid.plugins.services.tube8;

import cz.vity.freerapid.plugins.exceptions.ErrorDuringDownloadingException;
import cz.vity.freerapid.plugins.exceptions.PluginImplementationException;
import cz.vity.freerapid.plugins.exceptions.ServiceConnectionProblemException;
import cz.vity.freerapid.plugins.exceptions.URLNotAvailableAnymoreException;
import cz.vity.freerapid.plugins.webclient.AbstractRunner;
import cz.vity.freerapid.plugins.webclient.FileState;
import cz.vity.freerapid.plugins.webclient.utils.PlugUtils;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;

/**
 * Class which contains main code
 *
 * @author TommyTom
 * @author tong2shot
 */
class Tube8FileRunner extends AbstractRunner {
    private final static Logger logger = Logger.getLogger(Tube8FileRunner.class.getName());
    private SettingsConfig config;

    private void setConfig() throws Exception {
        Tube8ServiceImpl service = (Tube8ServiceImpl) getPluginService();
        config = service.getConfig();
    }

    @Override
    public void runCheck() throws Exception { //this method validates file
        super.runCheck();
        final GetMethod getMethod = getGetMethod(fileURL);//make first request
        if (makeRedirectedRequest(getMethod)) {
            checkProblems();
            checkNameAndSize(getContentAsString());//ok let's extract file name and size from the page
        } else {
            checkProblems();
            throw new ServiceConnectionProblemException();
        }
    }

    private void checkNameAndSize(String content) throws ErrorDuringDownloadingException {
        final Matcher match = PlugUtils.matcher("videotitle\\s*?=\\s*?['\"]([^'\"]+?)['\"]", content);
        if (!match.find())
            throw new PluginImplementationException("File name not found");
        //remove old extension if it exists
        String name = match.group(1).trim();
        final int pos = name.lastIndexOf('.');
        if (pos != -1) name = name.substring(0, pos);
        httpFile.setFileName(name + ".flv");
        httpFile.setFileState(FileState.CHECKED_AND_EXISTING);
    }

    @Override
    public void run() throws Exception {
        super.run();
        logger.info("Starting download in TASK " + fileURL);
        final GetMethod method = getGetMethod(fileURL); //create GET request
        if (makeRedirectedRequest(method)) { //we make the main request
            String content = getContentAsString();//check for response
            checkProblems();//check problems
            checkNameAndSize(content);//extract file name and size from the page
            fileURL = method.getURI().toString(); //sometimes redirected

            setConfig();
            Tube8Video selectedVideo = getSelectedVideo(content);
            final String encURL = URLDecoder.decode(selectedVideo.url, "UTF-8").replace(" ", "+");
            final String name = PlugUtils.getStringBetween(content, "\"video_title\":\"", "\"").replace('+', ' ');
            logger.info("Video title: " + name);
            logger.info("Config settings: " + config);
            logger.info("Selected video: " + selectedVideo);
            logger.info("Encrypted URL: " + encURL);
            String videoUrl = new Crypto().decrypt(encURL, name);
            logger.info("Decrypted URL: " + videoUrl);

            final HttpMethod httpMethod = getGetMethod(videoUrl);
            if (!tryDownloadAndSaveFile(httpMethod)) {
                checkProblems();//if downloading failed
                throw new ServiceConnectionProblemException("Error starting download");//some unknown problem
            }
        } else {
            checkProblems();
            throw new ServiceConnectionProblemException();
        }
    }

    private void checkProblems() throws ErrorDuringDownloadingException {
        final String contentAsString = getContentAsString();
        if (contentAsString.contains(">Newest Videos<")) {
            throw new URLNotAvailableAnymoreException("File not found");
        } else if (contentAsString.contains("This video is deleted") ||
                contentAsString.contains("video-removed-div")) {
            throw new URLNotAvailableAnymoreException("This video is deleted");
        }
    }

    private Tube8Video getSelectedVideo(String content) throws PluginImplementationException, UnsupportedEncodingException {
        List<Tube8Video> videoList = new LinkedList<Tube8Video>();
        for (VideoQuality videoQuality : VideoQuality.values()) {
            Matcher matcher = PlugUtils.matcher("\"" + videoQuality.getLabel() + "\":\"(.+?)\",", content);
            if (matcher.find()) {
                Tube8Video tube8Video = new Tube8Video(videoQuality, matcher.group(1));
                videoList.add(tube8Video);
                logger.info("Found video: " + tube8Video);
            }
        }
        if (videoList.isEmpty()) {
            throw new PluginImplementationException("No available videos");
        }
        return Collections.min(videoList);
    }

    private class Tube8Video implements Comparable<Tube8Video> {
        private final static int LOWER_QUALITY_PENALTY = 10;
        private final VideoQuality videoQuality;
        private final String url;
        private final int weight;

        public Tube8Video(final VideoQuality videoQuality, final String url) {
            this.videoQuality = videoQuality;
            this.url = url;
            this.weight = calcWeight();
        }

        private int calcWeight() {
            VideoQuality configQuality = config.getVideoQuality();
            int deltaQ = videoQuality.getQuality() - configQuality.getQuality();
            return (deltaQ < 0 ? Math.abs(deltaQ) + LOWER_QUALITY_PENALTY : deltaQ);
        }

        @SuppressWarnings("NullableProblems")
        @Override
        public int compareTo(final Tube8Video that) {
            return Integer.valueOf(this.weight).compareTo(that.weight);
        }

        @Override
        public String toString() {
            return "Tube8Video{" +
                    "videoQuality=" + videoQuality +
                    ", url='" + url + '\'' +
                    ", weight=" + weight +
                    '}';
        }
    }

}