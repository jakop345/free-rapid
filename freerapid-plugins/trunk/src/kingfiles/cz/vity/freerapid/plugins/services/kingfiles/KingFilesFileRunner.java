package cz.vity.freerapid.plugins.services.kingfiles;

import cz.vity.freerapid.plugins.exceptions.ErrorDuringDownloadingException;
import cz.vity.freerapid.plugins.exceptions.InvalidURLOrServiceProblemException;
import cz.vity.freerapid.plugins.exceptions.ServiceConnectionProblemException;
import cz.vity.freerapid.plugins.exceptions.URLNotAvailableAnymoreException;
import cz.vity.freerapid.plugins.services.xfilesharing.XFileSharingRunner;
import cz.vity.freerapid.plugins.services.xfilesharing.nameandsize.FileSizeHandler;
import cz.vity.freerapid.plugins.services.xfilesharing.nameandsize.FileSizeHandlerNoSize;
import cz.vity.freerapid.plugins.webclient.utils.PlugUtils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class which contains main code
 *
 * @author birchie
 */
class KingFilesFileRunner extends XFileSharingRunner {

    @Override
    protected void correctURL() throws Exception {
        final Matcher match = PlugUtils.matcher("kingfiles\\.net/([\\w\\d]+)", fileURL);
        if (!match.find())
            throw new InvalidURLOrServiceProblemException("File ID missing from URL");
        fileURL = "http://www.kingfiles.net/" + match.group(1);
    }

    @Override
    protected List<FileSizeHandler> getFileSizeHandlers() {
        final List<FileSizeHandler> fileSizeHandlers = super.getFileSizeHandlers();
        fileSizeHandlers.add(new FileSizeHandlerNoSize());
        return fileSizeHandlers;
    }

    @Override
    protected List<String> getDownloadPageMarkers() {
        final List<String> downloadPageMarkers = super.getDownloadPageMarkers();
        downloadPageMarkers.add("var download_url =");
        return downloadPageMarkers;
    }

    @Override
    protected List<String> getDownloadLinkRegexes() {
        final List<String> downloadLinkRegexes = super.getDownloadLinkRegexes();
        downloadLinkRegexes.add(0, "download_url\\s*?=\\s*?['\"](http.+?" + Pattern.quote(httpFile.getFileName()) + ")['\"]");
        return downloadLinkRegexes;
    }

    @Override
    protected void checkFileProblems() throws ErrorDuringDownloadingException {
        final String content = getContentAsString();
        try {
            super.checkFileProblems();
        } catch (URLNotAvailableAnymoreException e) {
            final Matcher match = PlugUtils.matcher("(visibility:hidden|font-size:0).+?>(File Not Found|The file was removed|Reason for deletion)", content);
            if (!match.find())  throw e;
        } catch (ServiceConnectionProblemException e) {
            final Matcher match = PlugUtils.matcher("(visibility:hidden|font-size:0).+?>(This server is in maintenance mode)", content);
            if (!match.find())  throw e;
        }
    }
}