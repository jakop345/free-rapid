package cz.vity.freerapid.plugins.services.one80upload;

import cz.vity.freerapid.plugins.exceptions.ErrorDuringDownloadingException;
import cz.vity.freerapid.plugins.exceptions.URLNotAvailableAnymoreException;
import cz.vity.freerapid.plugins.services.xfilesharing.XFileSharingRunner;
import cz.vity.freerapid.plugins.webclient.utils.PlugUtils;

import java.util.List;
import java.util.regex.Matcher;

/**
 * Class which contains main code
 *
 * @author birchie
 */
class one80UploadFileRunner extends XFileSharingRunner {

    @Override
    protected void correctURL() throws Exception {
        if (fileURL.contains("180upload.nl/"))
            fileURL = fileURL.replaceFirst("180upload.nl/", "180upload.com/");
    }

    @Override
    protected List<String> getDownloadPageMarkers() {
        final List<String> downloadPageMarkers = super.getDownloadPageMarkers();
        downloadPageMarkers.add("By downloading the file you agree to the TOS");
        return downloadPageMarkers;
    }

    @Override
    protected List<String> getDownloadLinkRegexes() {
        final List<String> downloadLinkRegexes = super.getDownloadLinkRegexes();
        downloadLinkRegexes.add(0, "var file_link\\s*?=\\s*?['\"](.+?)['\"]");
        return downloadLinkRegexes;
    }

    @Override    protected void checkFileProblems() throws ErrorDuringDownloadingException {
        final String content = getContentAsString();
        try {
            super.checkFileProblems();
        } catch (URLNotAvailableAnymoreException e) {
            final Matcher match = PlugUtils.matcher("style=\"color:\\s*?transparent;\"[^>]*?>\\s*?File Not Found", content);
            if (!match.find())  throw e;
        }
    }
}