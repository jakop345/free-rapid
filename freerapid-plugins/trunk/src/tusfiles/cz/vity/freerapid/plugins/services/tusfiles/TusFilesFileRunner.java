package cz.vity.freerapid.plugins.services.tusfiles;

import cz.vity.freerapid.plugins.exceptions.*;
import cz.vity.freerapid.plugins.services.xfilesharing.XFileSharingRunner;
import cz.vity.freerapid.plugins.services.xfilesharing.nameandsize.FileNameHandler;
import cz.vity.freerapid.plugins.services.xfilesharing.nameandsize.FileSizeHandler;
import cz.vity.freerapid.plugins.webclient.DownloadState;
import cz.vity.freerapid.plugins.webclient.hoster.PremiumAccount;
import cz.vity.freerapid.plugins.webclient.interfaces.HttpFile;
import cz.vity.freerapid.plugins.webclient.utils.PlugUtils;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpMethod;

import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;

/**
 * Class which contains main code
 *
 * @author birchie
 */
class TusFilesFileRunner extends XFileSharingRunner {

    @Override
    protected void correctURL() throws Exception {
        fileURL = fileURL.replaceFirst("https://", "http://");
    }

    @Override
    protected List<FileNameHandler> getFileNameHandlers() {
        final List<FileNameHandler> fileNameHandlers = super.getFileNameHandlers();
        fileNameHandlers.add(0, new FileNameHandler() {
            @Override
            public void checkFileName(HttpFile httpFile, String content) throws ErrorDuringDownloadingException {
                if (content.contains(">All Link")) {
                    httpFile.setFileName("Folder > ");
                    return;
                }
                final Matcher match = PlugUtils.matcher("[\\]>]([\\w].+?) - (\\d[\\d\\.,]+?\\s*?\\w+?)[\\[<]/", content);
                if (match.find())
                    httpFile.setFileName(match.group(1));
                else {
                    try {
                        httpFile.setFileName(PlugUtils.getStringBetween(content, ">Download", "</").trim().replaceAll("\\s", "."));
                    } catch (Exception ee) {
                        try {
                            PlugUtils.checkName(httpFile, content, "globalFileName = '", "';");
                        } catch (Exception e) {
                            PlugUtils.checkName(httpFile, content, "?q=", "\"");
                        }
                    }
                }
            }
        });
        return fileNameHandlers;
    }

    @Override
    protected List<FileSizeHandler> getFileSizeHandlers() {
        final List<FileSizeHandler> fileSizeHandlers = super.getFileSizeHandlers();
        fileSizeHandlers.add(0, new FileSizeHandler() {
            @Override
            public void checkFileSize(HttpFile httpFile, String content) throws ErrorDuringDownloadingException {
                if (content.contains(">All Link")) {
                    httpFile.setFileSize(PlugUtils.getFileSizeFromString(PlugUtils.getStringBetween(content, "<small>(", "total)</small>")));
                    return;
                }
                final Matcher match = PlugUtils.matcher(" - (\\d[\\d\\.,]+?\\s*?\\w+?)[\\[<]/", content);
                if (match.find())
                    httpFile.setFileSize(PlugUtils.getFileSizeFromString(match.group(1)));
                else {
                    final Matcher match2 = PlugUtils.matcher(">\\s*?Size\\s*?:\\s*?(.+?)\\s*?<", content);
                    if (match2.find())
                        httpFile.setFileSize(PlugUtils.getFileSizeFromString(match2.group(1)));
                    else
                        PlugUtils.checkFileSize(httpFile, content, "Size:", "| <");
                }
            }
        });
        return fileSizeHandlers;
    }

    @Override
    protected boolean stepProcessFolder() throws Exception {
        if (httpFile.getFileName().startsWith("Folder >")) {
            List<URI> list = new LinkedList<URI>();
            boolean morePages;
            do {
                morePages = false;
                final Matcher match = PlugUtils.matcher("<a href=\"(https?://(www\\.)?tusfiles\\.net/[^\"]+?)\"><img", getContentAsString());
                while (match.find()) {
                    list.add(new URI(match.group(1)));
                }
                if (getContentAsString().contains(">Next")) {
                    morePages = true;
                    final HttpMethod nextMethod = getMethodBuilder().setBaseURL("http://tusfiles.net").setActionFromAHrefWhereATagContains("Next").toGetMethod();
                    if (!makeRedirectedRequest(nextMethod)) {
                        checkFileProblems();
                        throw new ServiceConnectionProblemException();
                    }
                    checkFileProblems();
                    checkDownloadProblems();
                }
                Logger.getLogger(TusFilesFileRunner.class.getName()).info(list.size() + " Links found");
            } while (morePages);

            if (list.isEmpty()) throw new PluginImplementationException("No links found");
            getPluginService().getPluginContext().getQueueSupport().addLinksToQueue(httpFile, list);
            httpFile.setFileName("Link(s) Extracted !");
            httpFile.setState(DownloadState.COMPLETED);
            httpFile.getProperties().put("removeCompleted", true);
            return true;
        }
        return false;
    }

    @Override
    protected void checkFileProblems() throws ErrorDuringDownloadingException {
        final String content = getContentAsString();
        if (content.contains("The file you were looking for could not be found")
                || content.contains("The file was deleted")) {
            throw new URLNotAvailableAnymoreException("File not found");
        }
        super.checkFileProblems();
    }

    @Override
    protected HttpMethod redirectToLocation(final HttpMethod method) throws Exception {
        final Header locationHeader = method.getResponseHeader("Location");
        if (locationHeader == null)
            throw new PluginImplementationException("Invalid redirect");
        httpFile.setFileName(locationHeader.getValue().substring(locationHeader.getValue().lastIndexOf("/") + 1));
        return super.redirectToLocation(method);
    }

    @Override
    protected void doLogin(final PremiumAccount pa) throws Exception {
        HttpMethod method = getMethodBuilder()
                .setReferer(getBaseURL())
                .setAction(getBaseURL() + "/login.html")
                .toGetMethod();
        if (!makeRedirectedRequest(method)) {
            throw new ServiceConnectionProblemException();
        }
        method = getMethodBuilder()
                .setReferer(getBaseURL() + "/login.html")
                .setActionFromFormByName("FL", true)
                .setParameter("login", pa.getUsername())
                .setParameter("password", pa.getPassword())
                .setAction(getBaseURL())
                .toPostMethod();
        if (!makeRedirectedRequest(method)) {
            throw new ServiceConnectionProblemException();
        }
        if (getContentAsString().contains("Incorrect Login or Password") ||
                getContentAsString().contains("login user error") ||            // new error check
                getContentAsString().contains("login password error")) {        // new error check
            throw new BadLoginException("Invalid account login information");
        }
    }
}