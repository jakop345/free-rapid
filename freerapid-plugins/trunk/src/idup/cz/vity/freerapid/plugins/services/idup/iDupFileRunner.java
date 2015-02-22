package cz.vity.freerapid.plugins.services.idup;

import cz.vity.freerapid.plugins.services.xfilesharing.XFileSharingRunner;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Class which contains main code
 *
 * @author birchie
 */
class iDupFileRunner extends XFileSharingRunner {

    protected List<String> getDownloadLinkRegexes() {
        final List<String> downloadLinkRegexes = super.getDownloadLinkRegexes();
        downloadLinkRegexes.add(0, "<a[^<>]+?href\\s?=\\s?[\"'](http.+?idup.+?" + Pattern.quote(httpFile.getFileName()) + ")[\"']");
        return downloadLinkRegexes;
    }

}