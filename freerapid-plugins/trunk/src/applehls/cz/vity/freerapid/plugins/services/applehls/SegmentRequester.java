package cz.vity.freerapid.plugins.services.applehls;

import cz.vity.freerapid.plugins.webclient.interfaces.HttpDownloadClient;
import cz.vity.freerapid.plugins.webclient.interfaces.HttpFile;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpMethod;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author tong2shot
 */
public class SegmentRequester {
    private final static Logger logger = Logger.getLogger(SegmentRequester.class.getName());

    protected final HttpFile httpFile;
    protected final HttpDownloadClient client;
    protected final List<HlsMedia> medias;
    private int currentSegment = 1;
    private long totalSegmentsSize;

    public SegmentRequester(final HttpFile httpFile, final HttpDownloadClient client, final List<HlsMedia> medias) {
        this.httpFile = httpFile;
        this.client = client;
        this.medias = medias;
    }

    public InputStream nextFragment() throws IOException {
        if (currentSegment > medias.size()) {
            return null;
        }
        final String url = medias.get(currentSegment - 1).getUrl();
        logger.info("Downloading: " + url);
        final HttpMethod method = client.getGetMethod(url);
        final InputStream in = client.makeRequestForFile(method);
        if (in == null) {
            throw new IOException("Failed to request fragment " + currentSegment);
        }
        final Header header = method.getResponseHeader("Content-Length");
        if (header != null) {
            final long fragmentSize = Long.parseLong(header.getValue());
            totalSegmentsSize += fragmentSize;
            httpFile.setFileSize((totalSegmentsSize / currentSegment) * medias.size());//estimate
        }
        currentSegment++;
        return in;
    }

}
