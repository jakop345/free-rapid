package cz.vity.freerapid.plugins.services.twentyfouruploading;

import cz.vity.freerapid.plugins.exceptions.ServiceConnectionProblemException;
import cz.vity.freerapid.plugins.services.xfilesharing.XFileSharingRunner;
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
class TwentyFourUploadingFileRunner extends XFileSharingRunner {

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