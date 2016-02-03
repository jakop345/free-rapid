package cz.vity.freerapid.plugins.services.rapidvideo;

import cz.vity.freerapid.plugins.exceptions.ErrorDuringDownloadingException;
import cz.vity.freerapid.plugins.services.xfileplayer.XFilePlayerRunner;
import cz.vity.freerapid.plugins.webclient.utils.PlugUtils;
import cz.vity.freerapid.utilities.LogUtils;

import java.util.logging.Logger;
import java.util.regex.Matcher;

/**
 * Class which contains main code
 *
 * @author birchie
 */
class RapidVideoFileRunner extends XFilePlayerRunner {
    private final static Logger logger = Logger.getLogger(RapidVideoFileRunner.class.getName());

    @Override
    protected String getDownloadLinkFromRegexes() throws ErrorDuringDownloadingException {
        String downloadLink = super.getDownloadLinkFromRegexes();
        String jsText = unPackJavaScript();
        Matcher matcher = PlugUtils.matcher("['\"]?files['\"]?\\s*?:\\s*?['\"](http[^'\"]+?\\.srt)['\"],", jsText);
        if (!matcher.find()) {
            logger.info("Subtitle not found");
        } else {
            String subtitleUrl = matcher.group(1);
            logger.info("Subtitle URL: " + subtitleUrl);
            SubtitleDownloader subtitleDownloader = new SubtitleDownloader();
            try {
                subtitleDownloader.downloadSubtitle(client, httpFile, subtitleUrl);
            } catch (Exception e) {
                LogUtils.processException(logger, e);
            }
        }
        return downloadLink;
    }
}