package cz.vity.freerapid.plugins.services.sibit;

import cz.vity.freerapid.plugins.exceptions.*;
import cz.vity.freerapid.plugins.services.turbobit.TurboBitFileRunner;
import cz.vity.freerapid.plugins.webclient.FileState;
import cz.vity.freerapid.plugins.webclient.utils.PlugUtils;

import java.util.regex.Matcher;

/**
 * Class which contains main code
 *
 * @author birchie
 */
class SiBitFileRunner extends TurboBitFileRunner {

    @Override
    protected void checkNameAndSize() throws ErrorDuringDownloadingException {
        Matcher matcher = getMatcherAgainstContent("Download file:\\s*(?:<br>\\s*)?<span[^<>]+?>(.+?)</span>");
        if (!matcher.find()) {
            throw new PluginImplementationException("File name not found");
        }
        httpFile.setFileName(matcher.group(1));
        matcher = getMatcherAgainstContent("\\((\\d.+?)\\)");
        if (!matcher.find()) {
            throw new PluginImplementationException("File size not found");
        }
        httpFile.setFileSize(PlugUtils.getFileSizeFromString(matcher.group(1).replaceAll("б", "B")));
        httpFile.setFileState(FileState.CHECKED_AND_EXISTING);
    }
}