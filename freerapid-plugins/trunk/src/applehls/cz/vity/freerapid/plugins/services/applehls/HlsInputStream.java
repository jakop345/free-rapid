package cz.vity.freerapid.plugins.services.applehls;


import cz.vity.freerapid.plugins.webclient.DownloadClient;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author tong2shot
 */
public class HlsInputStream extends InputStream {

    private final SegmentRequester requester;
    private long pos;
    private InputStream currentStream = null;

    public HlsInputStream(final SegmentRequester requester) {
        this.requester = requester;
        Long startPos = (Long) requester.httpFile.getProperties().get(DownloadClient.START_POSITION);
        this.pos = (startPos == null ? 0 : startPos);
    }

    @Override
    public int read() throws IOException {
        int read = 0;
        if ((currentStream == null) || (read = currentStream.read()) == -1) {
            if (read == -1) {
                requester.httpFile.getProperties().put(HlsConsts.SEGMENT_LAST_POST, pos);
                //currentStream.close(); //org.apache.commons.httpclient.AutoCloseInputStream
            }
            InputStream stream = requester.nextSegment();
            if (stream == null) {
                return -1;
            }
            currentStream = stream;
            pos++;
            return currentStream.read();
        }
        pos++;
        return read;
    }
}
