package cz.vity.freerapid.plugins.services.exload;

import cz.vity.freerapid.plugins.exceptions.ErrorDuringDownloadingException;
import cz.vity.freerapid.plugins.exceptions.PluginImplementationException;
import cz.vity.freerapid.plugins.exceptions.ServiceConnectionProblemException;
import cz.vity.freerapid.plugins.exceptions.YouHaveToWaitException;
import cz.vity.freerapid.plugins.services.xfilesharing.XFileSharingRunner;
import cz.vity.freerapid.plugins.services.xfilesharing.nameandsize.FileNameHandler;
import cz.vity.freerapid.plugins.webclient.MethodBuilder;
import cz.vity.freerapid.plugins.webclient.interfaces.HttpFile;
import cz.vity.freerapid.plugins.webclient.utils.PlugUtils;
import org.apache.commons.httpclient.HttpMethod;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;

/**
 * Class which contains main code
 *
 * @author birchie
 */
class ExLoadFileRunner extends XFileSharingRunner {

    @Override
    protected List<FileNameHandler> getFileNameHandlers() {
        final List<FileNameHandler> fileNameHandlers = super.getFileNameHandlers();
        fileNameHandlers.add(new FileNameHandler() {
            @Override
            public void checkFileName(HttpFile httpFile, String content) throws ErrorDuringDownloadingException {
                final Matcher match = PlugUtils.matcher("<h1.+?>([^<>]+?)</h1>", content);
                if (!match.find())
                    throw new PluginImplementationException("File name not found");
                httpFile.setFileName(match.group(1).trim());
            }
        });
        return fileNameHandlers;
    }

    @Override
    protected MethodBuilder getXFSMethodBuilder(final String content) throws Exception {
        return getXFSMethodBuilder(content, "download2");
    }



    @Override
    protected void checkDownloadProblems() throws ErrorDuringDownloadingException {
        try {
            super.checkDownloadProblems();
        } catch (YouHaveToWaitException x) {
            if (dlcGetLink(fileURL) != null) {
                if (!x.getMessage().startsWith("You have to wait")) {
                    throw x;
                }
            } else {
                throw x;
            }
        }
    }

    @Override
    protected boolean stepProcessFolder() throws Exception {
        final String downloadLink = dlcGetLink(fileURL);
        if (downloadLink != null) {
            try {
                super.doDownload(getGetMethod(downloadLink));
                return true;
            } catch (ServiceConnectionProblemException x) {
                if (x.getMessage().equals("Error starting download")) {
                    if (isErrorWithLongTimeAvailableLink()) {
                        dlcRemoveLink(fileURL);
                    } else
                        throw x;
                }
            }
            makeRedirectedRequest(getGetMethod(fileURL));
        }
        return false;
    }

    @Override
    protected void doDownload(final HttpMethod method) throws Exception {
        int linkAvailTime = getLongTimeAvailableLinkFromRegexes();
        if (linkAvailTime > 0)
            dlcAddLink(fileURL, method.getURI().getURI(), linkAvailTime);
        super.doDownload(method);
    }


    protected boolean isErrorWithLongTimeAvailableLink() {
        return getContentAsString().contains("Wrong IP");
    }

    protected List<String> getLongTimeAvailableLinkRegexes() {
        final List<String> longTimeAvailableLinkRegexes = new LinkedList<String>();
        longTimeAvailableLinkRegexes.add("direct link will be available for your IP next (.+)");
        return longTimeAvailableLinkRegexes;
    }

    protected int getLongTimeAvailableLinkFromRegexes() {
        for (final String longTimeAvailableLinkRegexes : getLongTimeAvailableLinkRegexes()) {
            final Matcher match = getMatcherAgainstContent(longTimeAvailableLinkRegexes);
            if (match.find()) {
                final Matcher matcher = PlugUtils.matcher("(?:(\\d+) hours?)?[,\\s]*(?:(\\d+) minutes?)?[,\\s]*(?:(\\d+) seconds?)?", match.group(1));
                if (matcher.find()) {
                    int waitHours = 0, waitMinutes = 0, waitSeconds = 0;
                    if (matcher.group(1) != null)
                        waitHours = Integer.parseInt(matcher.group(1));
                    if (matcher.group(2) != null)
                        waitMinutes = Integer.parseInt(matcher.group(2));
                    if (matcher.group(3) != null)
                        waitSeconds = Integer.parseInt(matcher.group(3));

                    return ((waitHours * 60 * 60) + (waitMinutes * 60) + waitSeconds);
                }
            }
        }
        return -1;
    }


    private static HashMap<String, linkData> downloadLinkCache = new HashMap<String, linkData>();

    protected void dlcAddLink(String fileUrl, String dlLink, int secAvailableFor) {
        long expiryTime = System.currentTimeMillis() + (1000 * secAvailableFor);
        downloadLinkCache.put(fileUrl, new linkData(dlLink, expiryTime));
    }

    protected String dlcGetLink(String fileUrl) {
        dlcRemoveExpiredLinks();
        if (downloadLinkCache.containsKey(fileUrl)) {
            return downloadLinkCache.get(fileUrl).getDlLink();
        }
        return null;
    }

    protected void dlcRemoveLink(String fileUrl) {
        downloadLinkCache.remove(fileUrl);
    }

    protected void dlcRemoveExpiredLinks() {
        for (String key : downloadLinkCache.keySet()) {
            if (downloadLinkCache.get(key).isExpired())
                dlcRemoveLink(key);
        }
    }

    private class linkData {
        private String dlLink;
        private long expires;

        linkData(String dlLink, long expires) {
            this.dlLink = dlLink;
            this.expires = expires;
        }
        public String getDlLink() {
            return dlLink;
        }
        public boolean isExpired() {
            return System.currentTimeMillis() >= expires;
        }
    }
}