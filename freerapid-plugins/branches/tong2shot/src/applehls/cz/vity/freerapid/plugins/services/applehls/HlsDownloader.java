package cz.vity.freerapid.plugins.services.applehls;

import cz.vity.freerapid.plugins.webclient.DefaultFileStreamRecognizer;
import cz.vity.freerapid.plugins.webclient.DownloadClient;
import cz.vity.freerapid.plugins.webclient.DownloadClientConsts;
import cz.vity.freerapid.plugins.webclient.DownloadState;
import cz.vity.freerapid.plugins.webclient.interfaces.HttpDownloadClient;
import cz.vity.freerapid.plugins.webclient.interfaces.HttpFile;
import cz.vity.freerapid.plugins.webclient.interfaces.HttpFileDownloadTask;
import cz.vity.freerapid.plugins.webclient.utils.HttpUtils;
import cz.vity.freerapid.plugins.webclient.utils.PlugUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author tong2shot
 */
public class HlsDownloader {
    /*
     * Useful resources:
     *
     * http://en.wikipedia.org/wiki/HTTP_Live_Streaming
     * http://en.wikipedia.org/wiki/M3U
     */

    private static final Logger logger = Logger.getLogger(HlsDownloader.class.getName());

    protected final HttpDownloadClient client;
    protected final HttpFile httpFile;
    protected final HttpFileDownloadTask downloadTask;

    public HlsDownloader(final HttpDownloadClient client, final HttpFile httpFile, final HttpFileDownloadTask downloadTask) {
        this.client = client;
        this.httpFile = httpFile;
        this.downloadTask = downloadTask;
        client.getHTTPClient().getParams().setParameter(DownloadClientConsts.FILE_STREAM_RECOGNIZER, new DefaultFileStreamRecognizer(new String[0], new String[]{"mpegurl"}, false));
    }

    public void tryDownloadAndSaveFile(final String playlistUrl) throws Exception {
        HlsPlaylist hlsPlaylist = new HlsPlaylist(client, playlistUrl); //master playlist
        HlsMedia media = getSelectedMedia(hlsPlaylist.getMediaList());
        logger.info("Downloading media: " + media);
        tryDownloadAndSaveFile(media.url, media.getBandwidth(), media.getQuality());
    }

    public void tryDownloadAndSaveFile(final String playlistUrl, final int bandwidth, final int quality) throws Exception {
        HlsPlaylist hlsPlaylist = new HlsPlaylist(client, playlistUrl, false, bandwidth, quality); //segments playlist

        httpFile.setState(DownloadState.GETTING);
        logger.info("Starting HLS download");

        if ((httpFile.getStoreFile() == null) || (httpFile.getStoreFile().length() == 0)) {  //cancelled
            httpFile.getProperties().remove(HlsConsts.SEGMENT_LAST_POST);
            httpFile.getProperties().remove(HlsConsts.CURRENT_SEGMENT);
        }

        Long segmentLastPos = (Long) httpFile.getProperties().get(HlsConsts.SEGMENT_LAST_POST);
        if (segmentLastPos == null) {
            httpFile.getProperties().remove(DownloadClient.START_POSITION);
        } else {
            httpFile.getProperties().put(DownloadClient.START_POSITION, segmentLastPos);
        }
        httpFile.getProperties().remove(DownloadClient.SUPPOSE_TO_DOWNLOAD);
        httpFile.setResumeSupported(true);

        final String fn = httpFile.getFileName();
        if (fn == null || fn.isEmpty())
            throw new IOException("No defined file name");
        httpFile.setFileName(HttpUtils.replaceInvalidCharsForFileSystem(PlugUtils.unescapeHtml(fn), "_"));

        client.getHTTPClient().getParams().setBooleanParameter(DownloadClientConsts.NO_CONTENT_LENGTH_AVAILABLE, true);

        final SegmentRequester requester = getFragmentRequester(hlsPlaylist.getMediaList());
        InputStream in = new HlsInputStream(requester);
        try {
            downloadTask.saveToFile(in);
        } finally {
            in.close();
        }
    }

    protected HlsMedia getSelectedMedia(List<HlsMedia> mediaList) throws Exception {
        return Collections.max(mediaList);
    }

    protected SegmentRequester getFragmentRequester(List<HlsMedia> mediaList) {
        return new SegmentRequester(httpFile, client, mediaList);
    }

}
