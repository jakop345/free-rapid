package cz.vity.freerapid.plugins.services.gboxes;

import cz.vity.freerapid.plugins.services.xfilesharing.XFileSharingRunner;

import java.util.regex.Matcher;

/**
 * Class which contains main code
 *
 * @author birchie
 */
class GBoxesFileRunner extends XFileSharingRunner {

    @Override
    protected int getWaitTime() throws Exception {
        final Matcher matcher = getMatcherAgainstContent("id=\"countdown\".*?<span.*?>.*?(\\d+).*?</span");
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1)) + 1;
        }
        return 0;
    }
}