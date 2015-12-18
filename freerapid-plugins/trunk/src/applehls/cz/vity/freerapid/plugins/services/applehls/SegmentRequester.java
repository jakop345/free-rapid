package cz.vity.freerapid.plugins.services.applehls;

import cz.vity.freerapid.plugins.webclient.interfaces.HttpDownloadClient;
import cz.vity.freerapid.plugins.webclient.interfaces.HttpFile;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

/**
 * @author tong2shot
 */
public class SegmentRequester {
    private final static Logger logger = Logger.getLogger(SegmentRequester.class.getName());

    protected final HttpFile httpFile;
    protected final HttpDownloadClient client;
    protected final List<HlsMedia> mediaList;
    private int currentSegment;
    private long totalSegmentsSize;

    public SegmentRequester(final HttpFile httpFile, final HttpDownloadClient client, final List<HlsMedia> mediaList) {
        this.httpFile = httpFile;
        this.client = client;
        this.mediaList = mediaList;
        Long segmentLastPos = (Long) httpFile.getProperties().get(HlsConsts.SEGMENT_LAST_POST);
        this.totalSegmentsSize = (segmentLastPos == null ? 0 : segmentLastPos);
        Integer currentSegment = (Integer) httpFile.getProperties().get(HlsConsts.CURRENT_SEGMENT);
        this.currentSegment = (currentSegment == null ? 1 : currentSegment);
    }

    public InputStream nextSegment() throws IOException {
        if (currentSegment > mediaList.size()) {
            return null;
        }
        final String url = mediaList.get(currentSegment - 1).getUrl();
        logger.info("Downloading: " + url);
        HttpMethod method = client.getGetMethod(url);
        InputStream in = makeRequestForFile(method);

        final int MAX_REDIRECT = 5;
        int i = 0;
        while (in == null && (i++ < MAX_REDIRECT)) {
            Header header = method.getResponseHeader("location");
            if (header != null) {
                String newuri = header.getValue();
                if ((newuri == null) || ("".equals(newuri))) {
                    newuri = "/";
                }
                if (!newuri.matches("(?i)https?://.+")) {
                    if (!newuri.startsWith("/")) {
                        newuri = "/" + newuri;
                    }
                    newuri = method.getURI().getScheme() + "://" + method.getURI().getHost() + newuri;
                }
                logger.info("Redirect target: " + newuri);
                method.abort();
                method.releaseConnection();
                method = client.getGetMethod(newuri);
                in = makeRequestForFile(method);
            } else {
                break;
            }
        }
        if (in == null) {
            throw new IOException("Failed to request segment " + currentSegment);
        }

        final Header header = method.getResponseHeader("Content-Length");
        if (header != null) {
            final long segmentSize = Long.parseLong(header.getValue());
            totalSegmentsSize += segmentSize;
            httpFile.setFileSize((totalSegmentsSize / currentSegment) * mediaList.size());//estimate
        }
        httpFile.getProperties().put(HlsConsts.CURRENT_SEGMENT, currentSegment);
        currentSegment++;
        return in;
    }

    private void processHttpMethod(HttpMethod method) throws IOException {
        if (client.getHTTPClient().getHostConfiguration().getProtocol() != null) {
            client.getHTTPClient().getHostConfiguration().setHost(method.getURI().getHost(), 80, client.getHTTPClient().getHostConfiguration().getProtocol());
        }
        client.getHTTPClient().executeMethod(method);
    }

    public InputStream makeRequestForFile(HttpMethod method) throws IOException {
        processHttpMethod(method);
        int statuscode = method.getStatusCode();
        if (statuscode == HttpStatus.SC_OK) {
            Header hce = method.getResponseHeader("Content-Encoding");
            if (null != hce && !hce.getValue().isEmpty()) {
                if ("gzip".equalsIgnoreCase(hce.getValue())) {
                    logger.info("Found GZIP stream");
                    return new GZIPInputStream(method.getResponseBodyAsStream());
                } else {
                    logger.warning("Unknown Content-Encoding: " + hce.getValue());
                }
            }
            return method.getResponseBodyAsStream();
        }
        return null;
    }

}
