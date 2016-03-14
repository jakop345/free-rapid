package cz.vity.freerapid.plugins.services.clicknupload;

import cz.vity.freerapid.plugins.exceptions.ErrorDuringDownloadingException;
import cz.vity.freerapid.plugins.exceptions.PluginImplementationException;
import cz.vity.freerapid.plugins.services.xfilesharing.XFileSharingRunner;
import cz.vity.freerapid.plugins.services.xfilesharing.nameandsize.FileNameHandler;
import cz.vity.freerapid.plugins.webclient.interfaces.HttpFile;
import cz.vity.freerapid.plugins.webclient.utils.PlugUtils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class which contains main code
 *
 * @author birchie
 */
class ClicknUploadFileRunner extends XFileSharingRunner {

    @Override
    protected void correctURL() throws Exception {
        fileURL = fileURL.replaceFirst("clicknupload.com", "clicknupload.me");
        fileURL = fileURL.replaceFirst("clicknupload.me", "clicknupload.link");
    }

    @Override
    protected List<FileNameHandler> getFileNameHandlers() {
        final List<FileNameHandler> fileNameHandlers = super.getFileNameHandlers();
        fileNameHandlers.add(0, new FileNameHandler() {
            @Override
            public void checkFileName(HttpFile httpFile, String content) throws ErrorDuringDownloadingException {
                final Matcher match = PlugUtils.matcher("(?:\\[URL=|href=\").+?(?:\\]|\">)(.+?) - (.+?)[\\[<]", content);
                if (!match.find())
                    throw new PluginImplementationException("File name not found");
                httpFile.setFileName(PlugUtils.unescapeHtml(match.group(1)).trim());
            }
        });
        return fileNameHandlers;
    }

    @Override
    protected List<String> getDownloadLinkRegexes() {
        final List<String> downloadLinkRegexes = super.getDownloadLinkRegexes();
        downloadLinkRegexes.add("window.open\\([\"'](http.+?" + Pattern.quote(httpFile.getFileName()) + ")[\"']\\)");
        return downloadLinkRegexes;
    }
}