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
import cz.vity.freerapid.utilities.Utils;

import java.io.*;
import java.nio.channels.FileChannel;
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

        if ((httpFile.getStoreFile() == null) || (httpFile.getStoreFile().length() == 0)) {
            httpFile.getProperties().remove(RtmpConsts.FLV_HEADER_WRITTEN);
            httpFile.getProperties().remove(DownloadClient.START_POSITION);
        } else {
            Boolean flvHeaderWritten = (Boolean) httpFile.getProperties().get(RtmpConsts.FLV_HEADER_WRITTEN);
            if (flvHeaderWritten == null) {
                rtmpSession.setFlvHeaderWritten(false);
            } else {
                rtmpSession.setFlvHeaderWritten(flvHeaderWritten);
            }
            //resume
            logger.info("Getting keyframe info");
            KeyFrameInfo keyFrameInfo = getKeyFrameInfo(httpFile.getStoreFile()); //last downloaded key frame info
            logger.info("Keyframe timestamp: " + keyFrameInfo.timestamp);
            logger.info("Keyframe pos: " + keyFrameInfo.pos);
            rtmpSession.setPlayStart(keyFrameInfo.timestamp);
            rtmpSession.setPos(keyFrameInfo.pos);
            httpFile.getProperties().put(DownloadClient.START_POSITION, keyFrameInfo.pos);
        }

        httpFile.getProperties().remove(DownloadClient.SUPPOSE_TO_DOWNLOAD);
        httpFile.setResumeSupported(true);

        final String fn = httpFile.getFileName();
        if (fn == null || fn.isEmpty())
            throw new IOException("No defined file name");
        httpFile.setFileName(HttpUtils.replaceInvalidCharsForFileSystem(PlugUtils.unescapeHtml(fn), "_"));

        client.getHTTPClient().getParams().setBooleanParameter(DownloadClientConsts.NO_CONTENT_LENGTH_AVAILABLE, true);

        rtmpSession.setConnectionSettings(client.getSettings());//for proxy
        rtmpSession.setHttpFile(httpFile);//for size estimation
        rtmpSession.setOutputWriter(new FlvStreamWriter(rtmpSession.getPlayStart(), rtmpSession, rtmpSession.isFlvHeaderWritten(), rtmpSession.getPos()));

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
            throw new PluginImplementationException("RTMP error - " + Utils.getThrowableDescription(t));
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
                    downloadTask.sleep(5); //give rtmpClient a chance to disconnect gracefully
                    redirectHandler.handleRedirect(rtmpSession);
                }
            }
        }
        return true;
    }

    private class KeyFrameInfo {
        private final int timestamp;
        private final long pos;

        public KeyFrameInfo(int timestamp, long pos) {
            this.timestamp = timestamp;
            this.pos = pos;
        }
    }

    private KeyFrameInfo getKeyFrameInfo(File flvFile) throws Exception {
        FileInputStream fis = new FileInputStream(flvFile);
        DataInputStream dis = new DataInputStream(fis);
        FileChannel channel = fis.getChannel();

        //check header
        if ((dis.readInt() != 0x464c5601) //"FLV" signature and version
                || (dis.readByte() != 0x05) // always audio+video, from FlvStreamWriter
                || (dis.readInt() != 0x09) //header size
                || (dis.readInt() != 0x00)) //prev tagsize
        {
            throw new IOException("Invalid FLV header");
        }

        long pos = 0, tempPos;
        int timestamp = 0, size, tempTimestamp, type;
        do {
            try {
                type = dis.readByte();
                if (!((type == 0x08) || (type == 0x09) || (type == 0x12) || (type == 0x00))) {
                    break;
                }
                size = readInt24(dis);
                tempTimestamp = readInt24(dis);
                tempTimestamp |= (dis.read() & 0xff) << 24;
                readInt24(dis); //stream id
                tempPos = channel.position() + size + 4;
            } catch (Exception ex) {
                break;
            }
            if ((tempPos >= channel.size()) || ((tempTimestamp < timestamp) && (tempTimestamp != 0)) || (tempPos < pos)) { //yep tempTimestamp==0 is valid, don't ask why
                break;
            } else {
                if (((type == 0x08) || (type == 0x09)) && (tempTimestamp > timestamp) && (tempPos > pos)) {
                    timestamp = tempTimestamp;
                    pos = tempPos;
                }
                channel.position(tempPos);
            }
        } while (true);
        return new KeyFrameInfo(timestamp, pos);
    }

    private int readInt24(DataInputStream dis) throws IOException {
        int ch1 = dis.read();
        int ch2 = dis.read();
        int ch3 = dis.read();
        if ((ch1 | ch2 | ch3) < 0)
            throw new EOFException();
        return (ch1 << 16) | (ch2 << 8) | ch3;
    }
}
