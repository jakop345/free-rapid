package cz.vity.freerapid.plugins.services.applehls;

import cz.vity.freerapid.plugins.exceptions.PluginImplementationException;
import cz.vity.freerapid.plugins.webclient.interfaces.HttpDownloadClient;
import cz.vity.freerapid.plugins.webclient.interfaces.HttpFile;
import cz.vity.freerapid.plugins.webclient.interfaces.HttpFileDownloadTask;

import java.util.List;

/**
 * @author tong2shot
 */
public class AdjustableBitrateHlsDownloader extends HlsDownloader {
    private final int configBitrate;

    public AdjustableBitrateHlsDownloader(HttpDownloadClient client, HttpFile httpFile, HttpFileDownloadTask downloadTask, int configBitrate) {
        super(client, httpFile, downloadTask);
        this.configBitrate = configBitrate;
    }

    @Override
    protected HlsMedia getSelectedMedia(List<HlsMedia> mediaList) throws Exception {
        HlsMedia selectedMedia = null;
        int weight = Integer.MAX_VALUE;
        for (HlsMedia media : mediaList) {
            int deltaBitrate = media.getBandwidth() - configBitrate;
            int tempWeight = (deltaBitrate < 0 ? Math.abs(deltaBitrate) + 1 : deltaBitrate);
            if (tempWeight < weight) {
                weight = tempWeight;
                selectedMedia = media;
            }
        }
        if (selectedMedia == null) {
            throw new PluginImplementationException("Unable to select media");
        }
        return selectedMedia;
    }
}
