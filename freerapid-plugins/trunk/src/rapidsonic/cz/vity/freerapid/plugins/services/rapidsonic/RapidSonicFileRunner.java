package cz.vity.freerapid.plugins.services.rapidsonic;

import cz.vity.freerapid.plugins.exceptions.ErrorDuringDownloadingException;
import cz.vity.freerapid.plugins.exceptions.PluginImplementationException;
import cz.vity.freerapid.plugins.services.xfilesharing.XFileSharingRunner;
import cz.vity.freerapid.plugins.services.xfilesharing.nameandsize.FileSizeHandler;
import cz.vity.freerapid.plugins.services.xfilesharing.nameandsize.FileSizeHandlerNoSize;

import java.util.List;

/**
 * Class which contains main code
 *
 * @author birchie
 */
class RapidSonicFileRunner extends XFileSharingRunner {

    @Override
    protected List<FileSizeHandler> getFileSizeHandlers() {
        final List<FileSizeHandler> fileSizeHandlers = super.getFileSizeHandlers();
        fileSizeHandlers.add(new FileSizeHandlerNoSize());
        return fileSizeHandlers;
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