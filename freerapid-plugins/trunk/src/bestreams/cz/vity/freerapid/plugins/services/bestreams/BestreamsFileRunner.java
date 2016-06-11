package cz.vity.freerapid.plugins.services.bestreams;

import cz.vity.freerapid.plugins.exceptions.ErrorDuringDownloadingException;
import cz.vity.freerapid.plugins.exceptions.URLNotAvailableAnymoreException;
import cz.vity.freerapid.plugins.services.xfileplayer.XFilePlayerRunner;

/**
 * Class which contains main code
 *
 * @author birchie
 */
class BestreamsFileRunner extends XFilePlayerRunner {
    protected void checkFileProblems(final String content) throws ErrorDuringDownloadingException {
        super.checkFileProblems(content);
        if (content.contains("file was deleted") || content.contains("Reason for deletion")) {
            throw new URLNotAvailableAnymoreException("File not found");
        }
    }

}