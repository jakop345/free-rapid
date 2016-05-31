package cz.vity.freerapid.plugins.services.uploadboy;

import cz.vity.freerapid.plugins.exceptions.ErrorDuringDownloadingException;
import cz.vity.freerapid.plugins.exceptions.URLNotAvailableAnymoreException;
import cz.vity.freerapid.plugins.services.xfilesharing.XFileSharingRunner;
import cz.vity.freerapid.plugins.services.xfilesharing.nameandsize.FileNameHandler;

import java.util.List;

/**
 * Class which contains main code
 *
 * @author birchie
 */
class UploadBoyFileRunner extends XFileSharingRunner {
    @Override
    protected List<FileNameHandler> getFileNameHandlers() {
        final List<FileNameHandler> fileNameHandlers = super.getFileNameHandlers();
        fileNameHandlers.add(0, fileNameHandlers.remove(2));
        return fileNameHandlers;
    }

    @Override
    protected List<String> getDownloadPageMarkers() {
        final List<String> downloadPageMarkers = super.getDownloadPageMarkers();
        downloadPageMarkers.add("   Downloading");
        return downloadPageMarkers;
    }

    @Override
    protected void checkFileProblems(final String content) throws ErrorDuringDownloadingException {
        if (content.contains("file you were looking for could not be found")) {
            throw new URLNotAvailableAnymoreException("File not found");
        }
        super.checkFileProblems(content);
    }
}