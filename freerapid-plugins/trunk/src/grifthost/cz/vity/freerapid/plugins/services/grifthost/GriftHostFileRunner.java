package cz.vity.freerapid.plugins.services.grifthost;

import cz.vity.freerapid.plugins.exceptions.ErrorDuringDownloadingException;
import cz.vity.freerapid.plugins.services.xfileplayer.XFilePlayerRunner;
import cz.vity.freerapid.plugins.services.xfilesharing.nameandsize.FileNameHandler;
import cz.vity.freerapid.plugins.webclient.interfaces.HttpFile;
import cz.vity.freerapid.plugins.webclient.utils.PlugUtils;

import java.util.List;

/**
 * Class which contains main code
 *
 * @author birchie
 */
class GriftHostFileRunner extends XFilePlayerRunner {

    @Override
    protected List<FileNameHandler> getFileNameHandlers() {
        final List<FileNameHandler> fileNameHandlers = super.getFileNameHandlers();
        fileNameHandlers.add(0, new FileNameHandler() {
            @Override
            public void checkFileName(HttpFile httpFile, String content) throws ErrorDuringDownloadingException {
                PlugUtils.checkName(httpFile, content, "<Title>", "</Title>");
            }
        });
        fileNameHandlers.add(0, new FileNameHandler() {
            @Override
            public void checkFileName(HttpFile httpFile, String content) throws ErrorDuringDownloadingException {
                PlugUtils.checkName(httpFile, content, "Verifying Video Request :", "</b>");
            }
        });
        return fileNameHandlers;
    }

    @Override
    protected List<String> getDownloadLinkRegexes() {
        final List<String> downloadLinkRegexes = super.getDownloadLinkRegexes();
        downloadLinkRegexes.add(0, "<param name\\s*?=\\s*?['\"]src['\"]\\s*?value\\s*?=\\s*?['\"](http[^'\"]+?)['\"]");
        downloadLinkRegexes.add(0, "type\\s*?=\\s*?['\"]video/divx['\"]\\s*?src\\s*?=\\s*?['\"](http[^'\"]+?)['\"]");
        return downloadLinkRegexes;
    }

}