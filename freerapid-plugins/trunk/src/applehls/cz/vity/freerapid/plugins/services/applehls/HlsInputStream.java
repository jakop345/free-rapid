package cz.vity.freerapid.plugins.services.applehls;


import java.io.IOException;
import java.io.InputStream;

/**
 * @author tong2shot
 */
public class HlsInputStream extends InputStream {

    private final SegmentRequester requester;
    private InputStream currentStream = null;

    public HlsInputStream(final SegmentRequester requester) {
        this.requester = requester;
    }

    @Override
    public int read() throws IOException {
        final byte[] b = new byte[1];
        final int len = read(b, 0, 1);
        if (len == -1) {
            return -1;
        }
        return b[0] & 0xff;
    }

    @Override
    public int read(final byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    @Override
    public synchronized int read(final byte[] b, final int off, final int len) throws IOException {
        int read = 0;
        if ((currentStream == null) || ((read = currentStream.read(b, off, len)) == -1)) {
            if (read == -1) {
                currentStream.close();
            }
            InputStream stream = requester.nextFragment();
            if (stream == null) {
                return -1;
            }
            currentStream = stream;
            return currentStream.read(b, off, len);
        }
        return read;
    }

}
