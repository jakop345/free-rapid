package cz.vity.freerapid.plugins.services.powvideo;

import cz.vity.freerapid.plugins.exceptions.ErrorDuringDownloadingException;
import cz.vity.freerapid.plugins.exceptions.PluginImplementationException;
import cz.vity.freerapid.plugins.services.xfileplayer.XFilePlayerRunner;

import java.util.List;

/**
 * Class which contains main code
 *
 * @author birchie
 */
class PowVideoFileRunner extends XFilePlayerRunner {

    @Override
    protected List<String> getDownloadLinkRegexes() {
        final List<String> downloadLinkRegexes = super.getDownloadLinkRegexes();
        downloadLinkRegexes.add("src:['\"](http[^'\"]+?mp4)['\"]");
        return downloadLinkRegexes;
    }

    @Override
    protected void checkDownloadProblems() throws ErrorDuringDownloadingException {
        try {
            super.checkDownloadProblems();
        } catch (PluginImplementationException x) {
            if (!x.getMessage().contains("Skipped countdown"))      // ignore error
                throw new PluginImplementationException(x.getMessage());
        }
    }
}