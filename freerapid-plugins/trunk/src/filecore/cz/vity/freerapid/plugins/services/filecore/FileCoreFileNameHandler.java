package cz.vity.freerapid.plugins.services.filecore;

import cz.vity.freerapid.plugins.exceptions.ErrorDuringDownloadingException;
import cz.vity.freerapid.plugins.services.xfilesharing.nameandsize.FileNameHandler;
import cz.vity.freerapid.plugins.webclient.interfaces.HttpFile;
import cz.vity.freerapid.plugins.webclient.utils.PlugUtils;

import java.util.regex.Matcher;

public class FileCoreFileNameHandler implements FileNameHandler {
    @Override
    public void checkFileName(HttpFile httpFile, String content) throws ErrorDuringDownloadingException {
        Matcher match = PlugUtils.matcher("<td colspan=\"2\"><b>(.+?)</b>", content);
        if (match.find())
            httpFile.setFileName(match.group(1).trim());
        else
            httpFile.setFileName(PlugUtils.getStringBetween(content, "\"Download File ", "\"").trim().replaceAll("\\s", "."));
    }
}
