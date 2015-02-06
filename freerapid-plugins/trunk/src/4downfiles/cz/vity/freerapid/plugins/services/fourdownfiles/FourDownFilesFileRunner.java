package cz.vity.freerapid.plugins.services.fourdownfiles;

import cz.vity.freerapid.plugins.services.xfilesharing.XFileSharingRunner;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Class which contains main code
 *
 * @author birchie
 */
class FourDownFilesFileRunner extends XFileSharingRunner {

    @Override
    protected  List<String> getDownloadLinkRegexes() {
        final List<String> downloadLinkRegexes = super.getDownloadLinkRegexes();
        downloadLinkRegexes.add(0, "<a href\\s*?=\\s*?[\"'](http.+?" + Pattern.quote(httpFile.getFileName()) + ")[\"']>http");
        return downloadLinkRegexes;
    }
}