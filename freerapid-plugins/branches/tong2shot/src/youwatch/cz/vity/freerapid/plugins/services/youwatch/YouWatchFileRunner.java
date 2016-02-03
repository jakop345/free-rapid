package cz.vity.freerapid.plugins.services.youwatch;

import cz.vity.freerapid.plugins.exceptions.ErrorDuringDownloadingException;
import cz.vity.freerapid.plugins.exceptions.PluginImplementationException;
import cz.vity.freerapid.plugins.exceptions.ServiceConnectionProblemException;
import cz.vity.freerapid.plugins.services.xfileplayer.XFilePlayerRunner;
import cz.vity.freerapid.plugins.services.xfilesharing.nameandsize.FileNameHandler;
import cz.vity.freerapid.plugins.webclient.interfaces.HttpFile;
import cz.vity.freerapid.plugins.webclient.utils.PlugUtils;
import org.apache.commons.httpclient.HttpMethod;

import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;

/**
 * Class which contains main code
 *
 * @author birchie
 */
class YouWatchFileRunner extends XFilePlayerRunner {

    @Override
    protected List<FileNameHandler> getFileNameHandlers() {
        final List<FileNameHandler> fileNameHandlers = super.getFileNameHandlers();
        fileNameHandlers.add(new FileNameHandler() {
            @Override
            public void checkFileName(HttpFile httpFile, String content) throws ErrorDuringDownloadingException {
                final Matcher match = PlugUtils.matcher("<h3[^<>]*>(.+?)<", content);
                if (!match.find())
                    throw new PluginImplementationException("File name not found");
                httpFile.setFileName(decodeName(match.group(1)) + ".mp4");
            }
        });
        return fileNameHandlers;
    }

    private String decodeName(String text) {
        String decoded = "";
        for (char c : text.toCharArray()) {
            char d = c;
            if (c >= 'a' && c <= 'm') {
                d = (char)(c + 13);
            } else if (c > 'm' && c <= 'z') {
                d = (char)(d - 13);
            } else if (c >= 'A' && c <= 'M') {
                d = (char)(c + 13);
            } else if (c > 'M' && c <= 'Z') {
                d = (char)(d - 13);
            }
            decoded = decoded + d;
        }
        return decoded;
    }

    @Override
    protected boolean stepProcessFolder() throws Exception {
        do {
            String url = getMethodBuilder().setBaseURL("http:/").setActionFromIFrameSrcWhereTagContains("embed").getEscapedURI();
            HttpMethod method = getGetMethod(url.replace("%0A", ""));
            if (!makeRedirectedRequest(method)) {
                checkFileProblems();
                throw new ServiceConnectionProblemException();
            }
        } while (getContentAsString().contains("<iframe"));
Logger.getLogger(YouWatchFileRunner.class.getName()).info("@@@@@@@@@@@@@@" + getContentAsString() + "@@@@@@@@@@@@@");
        if (checkDownloadPageMarker()) {
            //page containing download link
            final String downloadLink = getDownloadLinkFromRegexes();
            HttpMethod method = getMethodBuilder()
                    .setReferer(fileURL)
                    .setAction(downloadLink)
                    .toGetMethod();
            doDownload(method);
            return true;
        }
        return false;
    }

    @Override
    protected int getWaitTime() throws Exception {
        final Matcher matcher = getMatcherAgainstContent("Wait <span id=\".*?\">.*?(\\d+).*?</span");
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1)) + 1;
        }
        return 0;
    }
}