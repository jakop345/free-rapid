package cz.vity.freerapid.plugins.services.watching;

import cz.vity.freerapid.plugins.services.xfileplayer.XFilePlayerRunner;

import java.util.List;

/**
 * Class which contains main code
 *
 * @author birchie
 */
class WatchingFileRunner extends XFilePlayerRunner {

    @Override
    protected int getWaitTime() throws Exception {
        return 11;
    }

    @Override
    protected List<String> getDownloadLinkRegexes() {
        final List<String> downloadLinkRegexes = super.getDownloadLinkRegexes();
        downloadLinkRegexes.clear();
        downloadLinkRegexes.add("file\\s*:\\s*\"([^\"]+?\\.mp4)\"");
        return downloadLinkRegexes;
    }
}