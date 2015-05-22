package cz.vity.freerapid.plugins.services.vshare;

import cz.vity.freerapid.plugins.exceptions.ErrorDuringDownloadingException;
import cz.vity.freerapid.plugins.exceptions.PluginImplementationException;
import cz.vity.freerapid.plugins.exceptions.URLNotAvailableAnymoreException;
import cz.vity.freerapid.plugins.services.xfilesharing.XFileSharingRunner;
import cz.vity.freerapid.plugins.services.xfilesharing.nameandsize.FileSizeHandler;
import cz.vity.freerapid.plugins.services.xfilesharing.nameandsize.FileSizeHandlerNoSize;

import java.util.List;

/**
 * Class which contains main code
 *
 * @author birchie
 */
class VShareFileRunner extends XFileSharingRunner {

    @Override
    protected List<FileSizeHandler> getFileSizeHandlers() {
        final List<FileSizeHandler> fileSizeHandlers = super.getFileSizeHandlers();
        fileSizeHandlers.add(new FileSizeHandlerNoSize());
        return fileSizeHandlers;
    }

    @Override
    protected void checkFileProblems() throws ErrorDuringDownloadingException {
        try {
            final String content = getContentAsString();
            if (content.contains("File is no longer available!<") ||
                    content.contains("file you were looking for could not be found")) {
                throw new URLNotAvailableAnymoreException("File not found");
            }
            super.checkFileProblems();
        } catch (URLNotAvailableAnymoreException x) {
            if (getContentAsString().contains("hidden\">File is no longer available")) {
                String content = getContentAsString().replace("hidden\">File is no longer available", "\">");
                if (content.contains("File is no longer available") ||
                        content.contains("file you were looking for could not be found"))
                    throw new URLNotAvailableAnymoreException("File not found");
                if (content.contains("File Not Found")
                        || content.contains("file was removed")
                        || content.contains("file has been removed")) {
                    throw new URLNotAvailableAnymoreException("File not found");
                }
            } else
                throw new PluginImplementationException(x.getMessage());
        }
    }

}