package cz.vity.freerapid.plugins.services.twodrive;

import cz.vity.freerapid.plugins.services.xfilesharing.XFileSharingRunner;

import java.util.regex.Matcher;

/**
 * Class which contains main code
 *
 * @author birchie
 */
class TwoDriveFileRunner extends XFileSharingRunner {

    @Override  protected int getWaitTime() throws Exception {
        final Matcher matcher = getMatcherAgainstContent("Wait <[^>]+?>(\\d+?)</[^>]+?> seconds");
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1)) + 1;
        }
        return 0;
    }
}