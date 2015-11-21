package cz.vity.freerapid.plugins.services.uploadrocket;

import cz.vity.freerapid.plugins.exceptions.ErrorDuringDownloadingException;
import cz.vity.freerapid.plugins.exceptions.ServiceConnectionProblemException;
import cz.vity.freerapid.plugins.services.xfilesharing.XFileSharingRunner;
import cz.vity.freerapid.plugins.services.xfilesharing.nameandsize.FileNameHandler;
import cz.vity.freerapid.plugins.services.xfilesharing.nameandsize.FileSizeHandler;
import cz.vity.freerapid.plugins.services.xfilesharing.nameandsize.FileSizeHandlerNoSize;
import cz.vity.freerapid.plugins.webclient.MethodBuilder;
import cz.vity.freerapid.plugins.webclient.interfaces.HttpFile;
import org.apache.commons.httpclient.HttpMethod;

import java.util.LinkedList;
import java.util.List;

/**
 * Class which contains main code
 *
 * @author birchie
 */
class UploadRocketFileRunner extends XFileSharingRunner {

    @Override
    protected List<FileNameHandler> getFileNameHandlers() {
        final List<FileNameHandler> fileNameHandlers = super.getFileNameHandlers();
        fileNameHandlers.add(new FileNameHandler() {
            @Override
            public void checkFileName(HttpFile httpFile, String content) throws ErrorDuringDownloadingException {
                // no file name displayed
            }
        });
        return fileNameHandlers;
    }

    @Override
    protected List<FileSizeHandler> getFileSizeHandlers() {
        final List<FileSizeHandler> fileSizeHandlers = super.getFileSizeHandlers();
        fileSizeHandlers.add(new FileSizeHandlerNoSize());
        return fileSizeHandlers;
    }

    @Override
    protected List<String> getDownloadLinkRegexes() {
        final List<String> downloadLinkRegexes =  new LinkedList<String>();
        downloadLinkRegexes.add("<a[^<>]*href=\"(.+?)\"[^<>]*>[^<>]*Download Link");
        return downloadLinkRegexes;
    }

    @Override
    protected String getDownloadLinkFromRegexes() throws ErrorDuringDownloadingException {
        final String link = super.getDownloadLinkFromRegexes();
        httpFile.setFileName(link.substring(1 + link.lastIndexOf("/")));
        return link;
    }

    @Override
    protected MethodBuilder getXFSMethodBuilder() throws Exception {
        final MethodBuilder methodBuilder = super.getXFSMethodBuilder();
        if ((methodBuilder.getParameters().get("method_isfree") != null) && (!methodBuilder.getParameters().get("method_isfree").isEmpty())) {
            methodBuilder.removeParameter("method_ispremium");
        }
        return methodBuilder;
    }

    @Override
    protected boolean handleDirectDownload(final HttpMethod method) throws Exception {
        fileURL = method.getResponseHeader("Location").getValue();
        if (!makeRedirectedRequest(redirectToLocation(method))) {
            checkFileProblems();
            throw new ServiceConnectionProblemException();
        }
        return false;
    }

    @Override
    protected List<String> getFalseProblemRegexes() {
        final List<String> falseProblemRegexes = super.getFalseProblemRegexes();
        falseProblemRegexes.add("<h3[^<>]+?color:black.+?</h3>");
        return falseProblemRegexes;
    }
}