package cz.vity.freerapid.plugins.services.uploadocean;

import cz.vity.freerapid.plugins.exceptions.ErrorDuringDownloadingException;
import cz.vity.freerapid.plugins.exceptions.PluginImplementationException;
import cz.vity.freerapid.plugins.services.xfilesharing.XFileSharingRunner;
import cz.vity.freerapid.plugins.services.xfilesharing.nameandsize.FileNameHandler;
import cz.vity.freerapid.plugins.services.xfilesharing.nameandsize.FileSizeHandler;
import cz.vity.freerapid.plugins.webclient.interfaces.HttpFile;
import cz.vity.freerapid.plugins.webclient.utils.PlugUtils;

import java.util.List;
import java.util.regex.Matcher;

/**
 * Class which contains main code
 *
 * @author birchie
 */
class UploadOceanFileRunner extends XFileSharingRunner {

    @Override
    protected List<FileNameHandler> getFileNameHandlers() {
        final List<FileNameHandler> fileNameHandlers = super.getFileNameHandlers();
        fileNameHandlers.add(0, new FileNameHandler() {
            @Override
            public void checkFileName(HttpFile httpFile, String content) throws ErrorDuringDownloadingException {
                final Matcher matcher = PlugUtils.matcher("(?:href=\"[^>]+?\">)(.+?) - (\\d.+?)<", content);
                if (!matcher.find()) {
                    throw new PluginImplementationException("File name not found");
                }
                httpFile.setFileName(PlugUtils.unescapeHtml(matcher.group(1)).trim());
            }
        });
        return fileNameHandlers;
    }

    @Override
    protected List<FileSizeHandler> getFileSizeHandlers() {
        final List<FileSizeHandler> fileSizeHandlers = super.getFileSizeHandlers();
        fileSizeHandlers.add(0, new FileSizeHandler() {
            @Override
            public void checkFileSize(HttpFile httpFile, String content) throws ErrorDuringDownloadingException {
                Matcher matcher = PlugUtils.matcher(">Size(?:\\s|<[^>]+>)*([\\s\\d\\.,]+?(?:bytes|.B|.b))\\s*<", content);
                if (!matcher.find()) {
                    throw new PluginImplementationException("File size not found");
                }
                httpFile.setFileSize(PlugUtils.getFileSizeFromString(matcher.group(1)));
            }
        });
        return fileSizeHandlers;
    }
}