package cz.vity.freerapid.plugins.services.applehls;

import cz.vity.freerapid.plugins.exceptions.ServiceConnectionProblemException;
import cz.vity.freerapid.plugins.webclient.interfaces.HttpDownloadClient;
import cz.vity.freerapid.plugins.webclient.utils.PlugUtils;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;
import java.util.regex.Matcher;

/**
 * @author tong2shot
 */
class HlsPlaylist {

    private static final Logger logger = Logger.getLogger(HlsPlaylist.class.getName());

    private final List<HlsMedia> medias;
    private final boolean master;

    public HlsPlaylist(final HttpDownloadClient client, final String playlistUrl, final boolean master, final int bandwidth, final int quality) throws IOException {
        try {
            this.master = master;
            final List<HlsMedia> medias = getMedias(client, playlistUrl, bandwidth, quality);
            if (medias.isEmpty()) {
                throw new IOException("No medias found");
            }
            logger.info("Found " + (!master ? "segment " : "") + "medias: " + medias.toString());
            this.medias = Collections.unmodifiableList(medias);
        } catch (final Exception e) {
            throw new IOException("Failed to parse playlist", e);
        }
    }

    public HlsPlaylist(final HttpDownloadClient client, final String playlistUrl) throws IOException {
        this(client, playlistUrl, true, 0, 0);
    }


    private List<HlsMedia> getMedias(final HttpDownloadClient client, final String playlistUrl, int bandwidth, int quality) throws Exception {
        logger.info("Playlist URL: " + playlistUrl);
        final HttpMethod method = client.getGetMethod(playlistUrl);
        if (client.makeRequest(method, true) != HttpStatus.SC_OK) {
            throw new ServiceConnectionProblemException();
        }

        final String content = client.getContentAsString();
        final String baseUrl = getBaseUrl(playlistUrl);
        final Scanner scanner = new Scanner(content);
        final List<HlsMedia> medias = new ArrayList<HlsMedia>();
        Matcher matcher;
        while (scanner.hasNext()) {
            String line = scanner.nextLine();
            if (master) {
                if (line.contains("BANDWIDTH")) {
                    quality = 0;
                    matcher = PlugUtils.matcher("BANDWIDTH=(\\d+)", line);
                    if (!matcher.find()) {
                        throw new IOException("Error parsing bandwidth");
                    }
                    bandwidth = Integer.parseInt(matcher.group(1)) / 1000;
                    matcher = PlugUtils.matcher("RESOLUTION=(\\d+)x(\\d+)", line);
                    if (matcher.find()) {
                        quality = Integer.parseInt(matcher.group(2));
                    }

                    line = scanner.nextLine();
                    medias.add(new HlsMedia(getUrl(baseUrl, line), bandwidth, quality));
                }
            } else {
                if ((line.length() > 0) && (!line.startsWith("#"))) {
                    medias.add(new HlsMedia(getUrl(baseUrl, line), bandwidth, quality));
                }
            }
        }
        return medias;
    }

    private String getBaseUrl(final String urlStr) throws MalformedURLException {
        URL url = new URL(urlStr);
        return url.getProtocol() + "://" + url.getAuthority();
    }

    private String getUrl(final String baseUrl, final String url) throws Exception {
        return new URI(baseUrl).resolve(new URI(url)).toString();
    }

    public boolean isMaster() {
        return master;
    }

    public List<HlsMedia> getMedias() {
        return medias;
    }

}
