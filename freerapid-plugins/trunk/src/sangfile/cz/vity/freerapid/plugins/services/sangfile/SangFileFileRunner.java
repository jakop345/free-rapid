package cz.vity.freerapid.plugins.services.sangfile;

import cz.vity.freerapid.plugins.exceptions.ErrorDuringDownloadingException;
import cz.vity.freerapid.plugins.exceptions.PluginImplementationException;
import cz.vity.freerapid.plugins.services.xfilesharing.XFileSharingRunner;
import cz.vity.freerapid.plugins.services.xfilesharing.nameandsize.FileSizeHandler;
import cz.vity.freerapid.plugins.webclient.interfaces.HttpFile;
import cz.vity.freerapid.plugins.webclient.utils.PlugUtils;
import org.apache.commons.httpclient.HttpMethod;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class which contains main code
 *
 * @author birchie
 */
class SangFileFileRunner extends XFileSharingRunner {

    @Override
    protected List<FileSizeHandler> getFileSizeHandlers() {
        final List<FileSizeHandler> fileSizeHandlers = super.getFileSizeHandlers();
        fileSizeHandlers.add(0, new FileSizeHandler() {
            @Override
            public void checkFileSize(HttpFile httpFile, String content) throws ErrorDuringDownloadingException {
                final Matcher match = PlugUtils.matcher(">\\s*?" + Pattern.quote(httpFile.getFileName()) + "\\s*?\\[\\s*?(.+?)\\s*?\\]\\s*?<", content);
                if (!match.find())
                    throw new PluginImplementationException("File size not found");
                httpFile.setFileSize(PlugUtils.getFileSizeFromString(match.group(1).trim()));
            }
        });
        return fileSizeHandlers;
    }

    @Override
    protected void correctURL() throws Exception {
        final HttpMethod method = getGetMethod(fileURL);
        if (makeRedirectedRequest(method)) {
            fileURL = method.getURI().getURI();
        }
    }

    @Override
    protected List<String> getDownloadPageMarkers() {
        final List<String> downloadPageMarkers = super.getDownloadPageMarkers();
        downloadPageMarkers.add("/images/download.png");
        return downloadPageMarkers;
    }

}