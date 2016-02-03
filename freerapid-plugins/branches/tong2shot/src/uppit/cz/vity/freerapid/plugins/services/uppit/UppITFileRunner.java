package cz.vity.freerapid.plugins.services.uppit;

import cz.vity.freerapid.plugins.exceptions.ErrorDuringDownloadingException;
import cz.vity.freerapid.plugins.exceptions.URLNotAvailableAnymoreException;
import cz.vity.freerapid.plugins.services.xfilesharing.XFileSharingRunner;
import cz.vity.freerapid.plugins.services.xfilesharing.nameandsize.FileNameHandler;
import cz.vity.freerapid.plugins.services.xfilesharing.nameandsize.FileSizeHandler;
import cz.vity.freerapid.plugins.webclient.MethodBuilder;
import cz.vity.freerapid.plugins.webclient.interfaces.HttpFile;
import cz.vity.freerapid.plugins.webclient.utils.PlugUtils;
import org.apache.commons.httpclient.HttpMethod;

import java.util.List;

/**
 * @author Kajda
 * @author ntoskrnl
 * @since 0.82
 */
class UppITFileRunner extends XFileSharingRunner {
    @Override
    protected List<FileNameHandler> getFileNameHandlers() {
        final List<FileNameHandler> fileNameHandlers = super.getFileNameHandlers();
        fileNameHandlers.add(0, new FileNameHandler() {
            @Override
            public void checkFileName(HttpFile httpFile, String content) throws ErrorDuringDownloadingException {
                PlugUtils.checkName(httpFile, content, "File download:&nbsp;<strong>", "</");
            }
        });
        return fileNameHandlers;
    }

    @Override
    protected void correctURL() throws Exception {
        if (fileURL.contains("up.ht/")) {
            HttpMethod httpMethod = getGetMethod(fileURL);
            if (makeRedirectedRequest(httpMethod)) {
                fileURL = httpMethod.getURI().getURI();
            }
        }
    }

    @Override
    protected List<FileSizeHandler> getFileSizeHandlers() {
        final List<FileSizeHandler> fileSizeHandlers = super.getFileSizeHandlers();
        fileSizeHandlers.add(0, new FileSizeHandler() {
            @Override
            public void checkFileSize(HttpFile httpFile, String content) throws ErrorDuringDownloadingException {
                PlugUtils.checkName(httpFile, content, "Size:&nbsp;&nbsp;<strong>", "</");
            }
        });
        return fileSizeHandlers;
    }

    @Override
    protected List<String> getDownloadPageMarkers() {
        final List<String> downloadPageMarkers = super.getDownloadPageMarkers();
        downloadPageMarkers.add("Your download is starting");
        downloadPageMarkers.add("var downloadlink = ");
        return downloadPageMarkers;
    }

    @Override
    protected MethodBuilder getXFSMethodBuilder() throws Exception {
        if (checkDownloadPageMarker()) {
            return getMethodBuilder().setReferer(fileURL).setAction(fileURL);
        }
        return super.getXFSMethodBuilder();
    }

    @Override
    protected List<String> getDownloadLinkRegexes() {
        final List<String> downloadLinkRegexes = super.getDownloadLinkRegexes();
        downloadLinkRegexes.add(0, "var downloadlink\\s*?=\\s*?.*?[\"'](http.+?)[\"']");
        return downloadLinkRegexes;
    }

    @Override
    protected void checkFileProblems(final String content) throws ErrorDuringDownloadingException {
        if (content.contains("requested URL was not found")) {
            throw new URLNotAvailableAnymoreException("File not found");
        }
        super.checkFileProblems(content);
    }
}