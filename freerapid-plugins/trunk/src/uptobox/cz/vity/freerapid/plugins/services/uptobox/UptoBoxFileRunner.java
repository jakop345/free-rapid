package cz.vity.freerapid.plugins.services.uptobox;

import cz.vity.freerapid.plugins.exceptions.*;
import cz.vity.freerapid.plugins.services.xfilesharing.XFileSharingRunner;
import cz.vity.freerapid.plugins.services.xfilesharing.nameandsize.FileNameHandler;
import cz.vity.freerapid.plugins.webclient.hoster.PremiumAccount;
import cz.vity.freerapid.plugins.webclient.utils.PlugUtils;
import org.apache.commons.httpclient.HttpMethod;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class which contains main code
 *
 * @author tong2shot
 */
class UptoBoxFileRunner extends XFileSharingRunner {

    @Override
    protected List<FileNameHandler> getFileNameHandlers() {
        final List<FileNameHandler> fileNameHandlers = super.getFileNameHandlers();
        fileNameHandlers.add(0, new UptoBoxFileNameHandler());
        return fileNameHandlers;
    }

    @Override
    protected int getWaitTime() throws Exception {
        final Matcher matcher = getMatcherAgainstContent("[Ww]ait.*?<.+?\">.*?(\\d+).*?</span");
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1)) + 1;
        }
        return 0;
    }

    @Override
    protected void checkFileProblems() throws ErrorDuringDownloadingException {
        final String content = getContentAsString();
        if (content.contains("the file you want is not available")) {
            throw new URLNotAvailableAnymoreException("File not found");
        }
        super.checkFileProblems();
    }

    @Override
    protected void checkDownloadProblems() throws ErrorDuringDownloadingException {
        final String content = getContentAsString();
        if (content.contains("Vous ne pouvez pas t&eacute;l&eacute;charger des fichiers de taille sup&eacute;rieur &agrave")) {
            throw new NotRecoverableDownloadException(PlugUtils.getStringBetween(content, " class=\"err\">", "<br").replace("Vous ne pouvez pas t&eacute;l&eacute;charger des fichiers de taille sup&eacute;rieur &agrave", "You can not download file sizes greater than"));
        }
        try {
            super.checkDownloadProblems();
        } catch (PluginImplementationException x) {
            if (!x.getMessage().contains("Skipped countdown"))      // ignore error
                throw new PluginImplementationException(x.getMessage());
        }
    }

    @Override
    protected List<String> getDownloadPageMarkers() {
        final List<String> downloadPageMarkers = super.getDownloadPageMarkers();
        downloadPageMarkers.add("Click here to start your download");
        return downloadPageMarkers;
    }

    @Override
    protected List<String> getDownloadLinkRegexes() {
        final List<String> downloadLinkRegexes = super.getDownloadLinkRegexes();
        downloadLinkRegexes.add(0, "product_download_url\\s*?=\\s*?(http.+?" + Pattern.quote(httpFile.getFileName()) + ")[\"']");
        return downloadLinkRegexes;
    }

    @Override
    protected String getDownloadLinkFromRegexes() throws ErrorDuringDownloadingException {
        String ret = super.getDownloadLinkFromRegexes();
        if (fileURL.startsWith("https")) {
            ret = ret.replaceFirst("http://", "https://");
        }
        return ret;
    }

    @Override
    protected void doLogin(final PremiumAccount pa) throws Exception {
        HttpMethod method = getMethodBuilder()
                .setReferer(getBaseURL())
                .setAction("https://login.uptobox.com/")
                .toGetMethod();
        if (!makeRedirectedRequest(method)) {
            throw new ServiceConnectionProblemException();
        }
        method = getMethodBuilder()
                .setReferer("https://login.uptobox.com/")
                .setAction("https://login.uptobox.com/")
                .setActionFromFormByName("FL", true)
                .setParameter("login", pa.getUsername())
                .setParameter("password", pa.getPassword())
                .toPostMethod();
        if (!makeRedirectedRequest(method)) {
            throw new ServiceConnectionProblemException();
        }
        if (getContentAsString().contains("Incorrect Login or Password")) {
            throw new BadLoginException("Invalid account login information");
        }
    }
}