package cz.vity.freerapid.plugins.services.mediahd;

import cz.vity.freerapid.plugins.exceptions.ErrorDuringDownloadingException;
import cz.vity.freerapid.plugins.services.xfilesharing.XFileSharingRunner;

/**
 * Class which contains main code
 *
 * @author birchie
 */
class MediaHDFileRunner extends XFileSharingRunner {

    @Override
    protected void checkDownloadProblems() throws ErrorDuringDownloadingException {
        final String content = getContentAsString();
        if (!content.contains("Skipped countdown"))
            super.checkDownloadProblems();
    }
}