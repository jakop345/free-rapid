package cz.vity.freerapid.plugins.services.played;

import cz.vity.freerapid.plugins.exceptions.ErrorDuringDownloadingException;
import cz.vity.freerapid.plugins.exceptions.URLNotAvailableAnymoreException;
import cz.vity.freerapid.plugins.services.xfileplayer.XFilePlayerRunner;
import cz.vity.freerapid.plugins.services.xfilesharing.nameandsize.FileNameHandler;
import cz.vity.freerapid.plugins.webclient.interfaces.HttpFile;
import cz.vity.freerapid.plugins.webclient.utils.PlugUtils;

import java.util.List;

/**
 * Class which contains main code
 *
 * @author birchie
 */
class PlayedFileRunner extends XFilePlayerRunner {
    @Override
    protected List<FileNameHandler> getFileNameHandlers() {
        final List<FileNameHandler> fileNameHandlers = super.getFileNameHandlers();
        fileNameHandlers.add(new FileNameHandler() {
            @Override
            public void checkFileName(HttpFile httpFile, String content) throws ErrorDuringDownloadingException {
                PlugUtils.checkName(httpFile, content, "<h1 class=\"pagename\">", "</h1>");
            }
        });
        return fileNameHandlers;
    }

    protected void checkFileProblems() throws ErrorDuringDownloadingException {
        super.checkFileProblems();
        final String content = getContentAsString();
        if (content.contains("Removed for copyright infringement")) {
            throw new URLNotAvailableAnymoreException("File not found");
        }
    }
}