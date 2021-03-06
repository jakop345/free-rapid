package cz.vity.freerapid.plugins.services.xtraupload;

import cz.vity.freerapid.plugins.webclient.AbstractFileShareService;
import cz.vity.freerapid.plugins.webclient.HttpFileDownloader;


import java.util.regex.Pattern;

/**
 * @author Ladislav Vitasek
 */
public class XtrauploadServiceImpl extends AbstractFileShareService {
    private static final String SERVICE_NAME = "xtraupload.de";
    private final static Pattern pattern = Pattern.compile("http://(www\\.)?xtraupload\\.de/.*", Pattern.CASE_INSENSITIVE);

    public String getName() {
        return SERVICE_NAME;
    }

    public int getMaxDownloadsFromOneIP() {
        return 4;
    }

    public boolean supportsURL(String url) {
        return pattern.matcher(url).matches();
    }

    public void run(HttpFileDownloader downloader) throws Exception {
        super.run(downloader);
        new XtrauploadRunner().run(downloader);
    }

}