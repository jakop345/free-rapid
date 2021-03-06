package cz.vity.freerapid.plugins.services.xfilesharing.nameandsize;

import cz.vity.freerapid.plugins.exceptions.ErrorDuringDownloadingException;
import cz.vity.freerapid.plugins.webclient.interfaces.HttpFile;

/**
 * @author ntoskrnl
 */
public interface FileNameHandler {

    public void checkFileName(final HttpFile httpFile, final String content) throws ErrorDuringDownloadingException;

}
