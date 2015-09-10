package cz.vity.freerapid.plugins.services.uploadex;

import cz.vity.freerapid.plugins.exceptions.ErrorDuringDownloadingException;
import cz.vity.freerapid.plugins.exceptions.PluginImplementationException;
import cz.vity.freerapid.plugins.services.xfilesharing.XFileSharingRunner;
import cz.vity.freerapid.plugins.services.xfilesharing.nameandsize.FileNameHandler;
import cz.vity.freerapid.plugins.services.xfilesharing.nameandsize.FileSizeHandler;
import cz.vity.freerapid.plugins.webclient.interfaces.HttpFile;
import cz.vity.freerapid.plugins.webclient.utils.PlugUtils;
import org.apache.commons.httpclient.HttpMethod;

import java.util.List;
import java.util.regex.Matcher;

/**
 * Class which contains main code
 *
 * @author birchie
 */
class UploadExFileRunner extends XFileSharingRunner {

    @Override
    protected void correctURL() throws Exception {
        fileURL = fileURL.replaceFirst("https://", "http://");
    }

    @Override
    protected List<FileNameHandler> getFileNameHandlers() {
        final List<FileNameHandler> fileNameHandlers = super.getFileNameHandlers();
        fileNameHandlers.add(new FileNameHandler() {
            @Override
            public void checkFileName(HttpFile httpFile, String content) throws ErrorDuringDownloadingException {
                final Matcher match = PlugUtils.matcher("filename\">(.+?)<", content);
                if (!match.find())
                    throw  new PluginImplementationException("File name not found");
                httpFile.setFileName(match.group(1).trim());
            }
        });
        return fileNameHandlers;
    }

    @Override
    protected List<FileSizeHandler> getFileSizeHandlers() {
        final List<FileSizeHandler> fileSizeHandlers = super.getFileSizeHandlers();
        fileSizeHandlers.add(new FileSizeHandler() {
            @Override
            public void checkFileSize(HttpFile httpFile, String content) throws ErrorDuringDownloadingException {
                final Matcher match = PlugUtils.matcher(">(\\d[\\d.,]+?\\s\\w*?B(ytes)?)<", content);
                if (!match.find())
                    throw  new PluginImplementationException("File size not found");
                httpFile.setFileSize(PlugUtils.getFileSizeFromString(match.group(1).trim()));
            }
        });
        return fileSizeHandlers;
    }

    protected HttpMethod redirectToLocation(final HttpMethod method) throws Exception {
        final HttpMethod newMethod = super.redirectToLocation(method);
        final String link = newMethod.getURI().getURI();
        httpFile.setFileName(link.substring(1 + link.lastIndexOf("/")));
        return newMethod;
    }

}