package cz.vity.freerapid.plugins.services.sharerepo;

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
class ShareRepoFileRunner extends XFilePlayerRunner {


    @Override
    protected List<FileNameHandler> getFileNameHandlers() {
        final List<FileNameHandler> fileNameHandlers = super.getFileNameHandlers();
        fileNameHandlers.add(new FileNameHandler() {
            @Override
            public void checkFileName(HttpFile httpFile, String content) throws ErrorDuringDownloadingException {
                PlugUtils.checkName(httpFile, content, "class=\"title\">", "</");
            }
        });
        return fileNameHandlers;
    }

    @Override
    protected List<String> getDownloadPageMarkers() {
        final List<String> downloadPageMarkers = super.getDownloadPageMarkers();
        downloadPageMarkers.add("jwplayer('myElement')");
        downloadPageMarkers.add("jwplayer(\"myElement\").setup");
        return downloadPageMarkers;
    }

    @Override
    protected String getDownloadLinkFromRegexes() throws ErrorDuringDownloadingException {
        final String name = httpFile.getFileName();
        final String link = super.getDownloadLinkFromRegexes();
        if (link.substring(link.lastIndexOf(".")).length() > 4)
            if (!name.endsWith(".mp4"))
                httpFile.setFileName(name + ".mp4");
        return link;
    }
}