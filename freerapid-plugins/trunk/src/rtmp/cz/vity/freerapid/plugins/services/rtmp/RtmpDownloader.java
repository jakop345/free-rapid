package cz.vity.freerapid.plugins.services.rtmp;

import cz.vity.freerapid.plugins.exceptions.PluginImplementationException;
import cz.vity.freerapid.plugins.webclient.DownloadClient;
import cz.vity.freerapid.plugins.webclient.DownloadClientConsts;
import cz.vity.freerapid.plugins.webclient.DownloadState;
import cz.vity.freerapid.plugins.webclient.interfaces.HttpDownloadClient;
import cz.vity.freerapid.plugins.webclient.interfaces.HttpFile;
import cz.vity.freerapid.plugins.webclient.interfaces.HttpFileDownloadTask;
import cz.vity.freerapid.plugins.webclient.utils.HttpUtils;
import cz.vity.freerapid.plugins.webclient.utils.PlugUtils;
import cz.vity.freerapid.utilities.LogUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.util.logging.Logger;

/**
 * @author ntoskrnl
 * @author tong2shot
 */
public class RtmpDownloader {
    private final static Logger logger = Logger.getLogger(RtmpDownloader.class.getName());

    private HttpDownloadClient client;
    private HttpFileDownloadTask downloadTask;

    public RtmpDownloader(HttpDownloadClient client, HttpFileDownloadTask downloadTask) {
        this.client = client;
        this.downloadTask = downloadTask;
    }

    public boolean tryDownloadAndSaveFile(final RtmpSession rtmpSession) throws Exception {
        HttpFile httpFile = downloadTask.getDownloadFile();
        if (httpFile.getState() == DownloadState.PAUSED || httpFile.getState() == DownloadState.CANCELLED)
            return false;
        else
            httpFile.setState(DownloadState.GETTING);
        logger.info("Starting RTMP download");

        httpFile.getProperties().remove(DownloadClient.START_POSITION);
        httpFile.getProperties().remove(DownloadClient.SUPPOSE_TO_DOWNLOAD);
        httpFile.setResumeSupported(false);

        final String fn = httpFile.getFileName();
        if (fn == null || fn.isEmpty())
            throw new IOException("No defined file name");
        httpFile.setFileName(HttpUtils.replaceInvalidCharsForFileSystem(PlugUtils.unescapeHtml(fn), "_"));

        client.getHTTPClient().getParams().setBooleanParameter(DownloadClientConsts.NO_CONTENT_LENGTH_AVAILABLE, true);

        rtmpSession.setConnectionSettings(client.getSettings());//for proxy
        rtmpSession.setHttpFile(httpFile);//for size estimation

        RtmpClient rtmpClient = null;
        try {
            rtmpClient = new RtmpClient(rtmpSession);
            rtmpClient.connect();

            InputStream in = rtmpSession.getOutputWriter().getStream();

            if (in != null) {
                logger.info("Saving to file");
                downloadTask.saveToFile(in);
                return true;
            } else {
                logger.info("Saving file failed");
                return false;
            }
        } catch (InterruptedException e) {
            //ignore
        } catch (InterruptedIOException e) {
            //ignore
        } catch (Exception e) {
            LogUtils.processException(logger, e);
            Throwable t = e;
            while (t.getCause() != null) {
                t = t.getCause();
            }
            throw new PluginImplementationException("RTMP error - " + cz.vity.freerapid.utilities.Utils.getThrowableDescription(t));
        } finally {
            if (rtmpClient != null) {
                try {
                    rtmpClient.disconnect();
                } catch (Exception e) {
                    LogUtils.processException(logger, e);
                }
            }
            if (rtmpSession.isRedirected()) {
                RedirectHandler redirectHandler = rtmpSession.getRedirectHandler();
                if (redirectHandler != null) {
                    redirectHandler.handle(rtmpSession);
                }
            }
        }
        return true;
    }
}
