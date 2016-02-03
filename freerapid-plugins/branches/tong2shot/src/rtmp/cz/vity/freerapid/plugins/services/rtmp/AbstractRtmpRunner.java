package cz.vity.freerapid.plugins.services.rtmp;

import cz.vity.freerapid.plugins.webclient.AbstractRunner;
import cz.vity.freerapid.plugins.webclient.DownloadClientConsts;

/**
 * Subclass this for support for RTMP downloads.
 *
 * @author ntoskrnl
 * @see #tryDownloadAndSaveFile(RtmpSession)
 */
public abstract class AbstractRtmpRunner extends AbstractRunner {

    /**
     * Method uses given RtmpSession parameter to connect to the server and tries to download.<br />
     * Download state of HttpFile is updated automatically - sets <code>DownloadState.GETTING</code> and then <code>DownloadState.DOWNLOADING</code>.
     * The DownloadClient parameter {@link DownloadClientConsts#NO_CONTENT_LENGTH_AVAILABLE NO_CONTENT_LENGTH_AVAILABLE} is also set.
     *
     * @param rtmpSession RtmpSession to use for downloading
     * @return true if file was successfully downloaded, false otherwise
     * @throws Exception if something goes horribly wrong
     * @see RtmpSession
     */
    protected boolean tryDownloadAndSaveFile(final RtmpSession rtmpSession) throws Exception {
        return new RtmpDownloader(client, downloadTask).tryDownloadAndSaveFile(rtmpSession);
    }

}
