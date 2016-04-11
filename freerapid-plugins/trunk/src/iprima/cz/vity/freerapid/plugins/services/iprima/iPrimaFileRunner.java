package cz.vity.freerapid.plugins.services.iprima;

import cz.vity.freerapid.plugins.exceptions.*;
import cz.vity.freerapid.plugins.services.applehls.AdjustableBitrateHlsDownloader;
import cz.vity.freerapid.plugins.services.applehls.HlsDownloader;
import cz.vity.freerapid.plugins.services.tor.TorProxyClient;
import cz.vity.freerapid.plugins.webclient.AbstractRunner;
import cz.vity.freerapid.plugins.webclient.FileState;
import cz.vity.freerapid.plugins.webclient.utils.PlugUtils;
import org.apache.commons.httpclient.HttpMethod;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.logging.Logger;
import java.util.regex.Matcher;

/**
 * Class which contains main code
 *
 * @author JPEXS
 * @author ntoskrnl
 * @author tong2shot
 */
class iPrimaFileRunner extends AbstractRunner {
    private final static Logger logger = Logger.getLogger(iPrimaFileRunner.class.getName());
    private final static String DEFAULT_EXT = ".ts";
    private iPrimaSettingsConfig config;

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

    private void setConfig() throws Exception {
        final iPrimaServiceImpl service = (iPrimaServiceImpl) getPluginService();
        config = service.getConfig();
    }

    private void checkNameAndSize() throws Exception {
        final String name = PlugUtils.getStringBetween(getContentAsString(), "<meta property=\"og:title\" content=\"", "\"")
                .replaceFirst("\\|[^\\|]+$", "").trim();
        httpFile.setFileName(name + DEFAULT_EXT);
        httpFile.setFileState(FileState.CHECKED_AND_EXISTING);
    }

    @Override
    public void run() throws Exception {
        super.run();
        logger.info("Starting download in TASK " + fileURL);
        setConfig();
        HttpMethod method = getGetMethod(fileURL);
        if (makeRedirectedRequest(method)) {
            checkProblems();
            checkNameAndSize();
            if (getContentAsString().contains("http://flash.stream.cz/")) {
                final String id = PlugUtils.getStringBetween(getContentAsString(), "&cdnID=", "&");
                method = getGetMethod("http://cdn-dispatcher.stream.cz/?id=" + id);
                if (!tryDownloadAndSaveFile(method)) {
                    checkProblems();
                    throw new ServiceConnectionProblemException("Error starting download");
                }
            } else {
                boolean iPrimaPlay = isIPrimaPlay();
                String productId;
                try {
                    productId = (iPrimaPlay ? PlugUtils.getStringBetween(getContentAsString(), "data-product=\"", "\"") :
                            PlugUtils.getStringBetween(getContentAsString(), "prehravac/embedded?id=", "\""));
                } catch (PluginImplementationException e) {
                    throw new PluginImplementationException("Video ID not found");
                }

                method = getMediaSelectorMethod(productId, iPrimaPlay);
                try {
                    if (!makeRedirectedRequest(method)) {
                        checkLocationProblems();
                    }
                    checkLocationProblems();
                } catch (ErrorDuringDownloadingException e) {
                    TorProxyClient torClient = TorProxyClient.forCountry("cz", client, getPluginService().getPluginContext().getConfigurationStorageSupport());
                    if (!torClient.makeRequest(method)) {
                        checkLocationProblems();
                        throw new ServiceConnectionProblemException();
                    }
                    checkLocationProblems();
                }

                IPrimaVideo selectedVideo = getSelectedVideo(getContentAsString());
                logger.info("Settings config: " + config);
                logger.info("Selected video: " + selectedVideo);
                HlsDownloader hlsDownloader = new AdjustableBitrateHlsDownloader(client, httpFile, downloadTask, config.getVideoQuality().getBitrate());
                hlsDownloader.tryDownloadAndSaveFile(selectedVideo.playlist);
            }
        } else {
            checkProblems();
            throw new ServiceConnectionProblemException();
        }
    }

    private HttpMethod getMediaSelectorMethod(String productId, boolean iPrimaPlay) throws BuildMethodException {
        return (iPrimaPlay ? getMethodBuilder()
                .setReferer(fileURL)
                .setAction("http://play.iprima.cz/prehravac/init")
                .setParameter("_infuse", "1")
                .setParameter("_ts", String.valueOf(System.currentTimeMillis()))
                .setParameter("productId", productId)
                .toGetMethod()

                : getMethodBuilder()
                .setReferer(fileURL)
                .setAction("http://api.play-backend.iprima.cz/prehravac/init")
                .setParameter("_infuse", "1")
                .setParameter("_ts", String.valueOf(System.currentTimeMillis()))
                .setParameter("embed", "true")
                .setParameter("productId", productId)
                .toGetMethod());
    }

    private boolean isIPrimaPlay() {
        return fileURL.contains("://play.iprima.cz");
    }

    private IPrimaVideo getSelectedVideo(String videoPlayerContent) throws ErrorDuringDownloadingException {
        Matcher matcher = PlugUtils.matcher("(?s)'HLS':.+?'src'\\s*?:\\s*?'([^']+?)'", videoPlayerContent);
        if (!matcher.find()) {
            throw new PluginImplementationException("HLS playlist not found");
        }
        String sdPlaylist = matcher.group(1); //assume SD
        String hd720Playlist = sdPlaylist.replaceFirst("/cze-[^/]*?sd[^/]+?\\.smil/", "/cze-hd1.smil/");
        String hd1080Playlist = sdPlaylist.replaceFirst("/cze-[^/]*?sd[^/]+?\\.smil/", "/cze-hd2.smil/");

        List<IPrimaVideo> iPrimaVideoList = new ArrayList<IPrimaVideo>();
        iPrimaVideoList.add(new IPrimaVideo(VideoQuality._400_1600, sdPlaylist));
        iPrimaVideoList.add(new IPrimaVideo(VideoQuality._720, hd720Playlist));
        iPrimaVideoList.add(new IPrimaVideo(VideoQuality._1080, hd1080Playlist));

        setTextContentTypes("application/vnd.apple.mpegurl");
        ListIterator<IPrimaVideo> iter = iPrimaVideoList.listIterator();
        iter.next(); //assume SD always exists
        while (iter.hasNext()) {
            IPrimaVideo iPrimaVideo = iter.next();
            try {
                if (!makeRedirectedRequest(getGetMethod(iPrimaVideo.playlist))) {
                    checkProblems();
                    throw new ServiceConnectionProblemException();
                }
                checkProblems();
            } catch (Exception e) {
                logger.warning("Error getting playlist for: " + iPrimaVideo.videoQuality);
                iter.remove(); //remove unavailable video
            }
        }
        logger.info("Found videos: " + iPrimaVideoList);
        return Collections.min(iPrimaVideoList);
    }

    private void checkProblems() throws ErrorDuringDownloadingException {
        String content = getContentAsString();
        if (content.contains("Video bylo odstraněno")
                || content.contains("Požadovaná stránka nebyla nalezena")) {
            throw new URLNotAvailableAnymoreException("File not found");
        }
    }

    private void checkLocationProblems() throws ErrorDuringDownloadingException {
        String content = getContentAsString();
        if (content.contains("tento pořad nelze z licenčních důvodů přehrávat mimo Českou republiku")) {
            throw new PluginImplementationException("This show can not be played outside the Czech Republic due to licensing reasons");
        }
    }

    private class IPrimaVideo implements Comparable<IPrimaVideo> {
        private final static int LOWER_QUALITY_PENALTY = 10;
        private final VideoQuality videoQuality;
        private final String playlist;
        private final int weight;

        public IPrimaVideo(final VideoQuality videoQuality, final String playlist) {
            this.videoQuality = videoQuality;
            this.playlist = playlist;
            this.weight = calcWeight();
        }

        private int calcWeight() {
            VideoQuality configQuality = config.getVideoQuality();
            int deltaQ = videoQuality.getBitrate() - configQuality.getBitrate();
            return (deltaQ < 0 ? Math.abs(deltaQ) + LOWER_QUALITY_PENALTY : deltaQ);
        }

        @SuppressWarnings("NullableProblems")
        @Override
        public int compareTo(final IPrimaVideo that) {
            return Integer.valueOf(this.weight).compareTo(that.weight);
        }

        @Override
        public String toString() {
            return "IPrimaVideo{" +
                    "videoQuality=" + videoQuality +
                    ", playlist='" + playlist + '\'' +
                    ", weight=" + weight +
                    '}';
        }
    }

}