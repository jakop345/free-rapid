package cz.vity.freerapid.plugins.services.skymiga;

import cz.vity.freerapid.plugins.exceptions.ErrorDuringDownloadingException;
import cz.vity.freerapid.plugins.exceptions.PluginImplementationException;
import cz.vity.freerapid.plugins.exceptions.URLNotAvailableAnymoreException;
import cz.vity.freerapid.plugins.services.xfilesharing.XFileSharingRunner;
import cz.vity.freerapid.plugins.services.xfilesharing.nameandsize.FileSizeHandler;
import cz.vity.freerapid.plugins.webclient.MethodBuilder;
import cz.vity.freerapid.plugins.webclient.interfaces.HttpFile;
import cz.vity.freerapid.plugins.webclient.utils.PlugUtils;

import java.util.List;
import java.util.regex.Matcher;

/**
 * Class which contains main code
 *
 * @author birchie
 */
class SkyMigaFileRunner extends XFileSharingRunner {

    @Override
    protected List<FileSizeHandler> getFileSizeHandlers() {
        final List<FileSizeHandler> fileSizeHandlers = super.getFileSizeHandlers();
        fileSizeHandlers.add(new FileSizeHandler() {
            @Override
            public void checkFileSize(HttpFile httpFile, String content) throws ErrorDuringDownloadingException {
                final Matcher match = PlugUtils.matcher("Size</td>\\s*<td>(.+?)<", content);
                if (!match.find())
                    throw new PluginImplementationException("File name not found");
                httpFile.setFileSize(PlugUtils.getFileSizeFromString(match.group(1)));
            }
        });
        return fileSizeHandlers;
    }

    @Override
    protected void stepPassword(final MethodBuilder methodBuilder) throws Exception {
    }

    @Override
    protected int getWaitTime() throws Exception {
        final Matcher matcher = getMatcherAgainstContent("id=\"countdown\".*?<span.*?\">.*?(\\d+).*?</span");
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1)) + 1;
        }
        return 0;
    }

    @Override
    protected void checkFileProblems(String content) throws ErrorDuringDownloadingException {
        super.checkFileProblems(content);
        if (content.contains("server can not find the requested page")
                || content.contains("file you were looking for could not be found")
                || content.contains("file expired")
                || content.contains("file was deleted")) {
            throw new URLNotAvailableAnymoreException("File not found");
        }
    }

    @Override
    protected void checkDownloadProblems() throws ErrorDuringDownloadingException {
        try {
            super.checkDownloadProblems();
        } catch (PluginImplementationException x) {
            if (!x.getMessage().contains("Skipped countdown"))      // ignore error
                throw new PluginImplementationException(x.getMessage());
        }
    }
}