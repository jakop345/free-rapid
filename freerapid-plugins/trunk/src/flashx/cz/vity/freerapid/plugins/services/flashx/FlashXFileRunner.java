package cz.vity.freerapid.plugins.services.flashx;

import cz.vity.freerapid.plugins.services.xfileplayer.XFilePlayerRunner;

import java.util.List;

/**
 * Class which contains main code
 *
 * @author ntoskrnl
 */
class FlashXFileRunner extends XFilePlayerRunner {

    @Override
    protected List<String> getDownloadLinkRegexes() {
        final List<String> downloadLinkRegexes = super.getDownloadLinkRegexes();
        downloadLinkRegexes.add(0, "['\"]?file['\"]?\\s*?:\\s*?['\"]([^'\"]+?/v[^'\"]+?)['\"]");
        return downloadLinkRegexes;
    }
}