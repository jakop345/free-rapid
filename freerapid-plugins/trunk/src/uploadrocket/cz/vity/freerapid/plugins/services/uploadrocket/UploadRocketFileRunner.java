package cz.vity.freerapid.plugins.services.uploadrocket;

import cz.vity.freerapid.plugins.exceptions.ErrorDuringDownloadingException;
import cz.vity.freerapid.plugins.exceptions.ServiceConnectionProblemException;
import cz.vity.freerapid.plugins.exceptions.URLNotAvailableAnymoreException;
import cz.vity.freerapid.plugins.services.xfilesharing.XFileSharingRunner;
import cz.vity.freerapid.plugins.services.xfilesharing.nameandsize.FileSizeHandler;
import cz.vity.freerapid.plugins.services.xfilesharing.nameandsize.FileSizeHandlerNoSize;
import cz.vity.freerapid.plugins.webclient.MethodBuilder;
import org.apache.commons.httpclient.HttpMethod;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Class which contains main code
 *
 * @author birchie
 */
class UploadRocketFileRunner extends XFileSharingRunner {

    @Override
    protected List<FileSizeHandler> getFileSizeHandlers() {
        final List<FileSizeHandler> fileSizeHandlers = super.getFileSizeHandlers();
        fileSizeHandlers.add(new FileSizeHandlerNoSize());
        return fileSizeHandlers;
    }

    @Override
    protected List<String> getDownloadLinkRegexes() {
        final List<String> downloadLinkRegexes = super.getDownloadLinkRegexes();
        downloadLinkRegexes.add("var download_url\\s*=\\s*'(http.+?" + Pattern.quote(httpFile.getFileName()) + ")'");
        return downloadLinkRegexes;
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
    protected void checkFileProblems() throws ErrorDuringDownloadingException {
        try {
            super.checkFileProblems();
        } catch (ErrorDuringDownloadingException e) {
            final String contentAsString = getContentAsString()
                    .replaceAll("<font[^<>]+?visibility:hidden.+?</font>", "")
                    .replaceAll("<font[^<>]+?font-size:0.+?</font>", "")
                    .replaceAll("(?s)<td[^<>]+?color:\\s*transparent.+?</td>", "")
                    .replaceAll("<h3[^<>]+?color:black.+?</h3>", "");
            if (contentAsString.contains("file was deleted by") ||
                    contentAsString.contains("The file was removed") ||
                    contentAsString.contains("Reason for deletion") ||
                    contentAsString.contains("fname\" value=\"\"")) {
                throw new URLNotAvailableAnymoreException("File not found");
            }
        }
    }
}