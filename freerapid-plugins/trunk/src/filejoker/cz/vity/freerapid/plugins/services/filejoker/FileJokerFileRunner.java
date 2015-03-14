package cz.vity.freerapid.plugins.services.filejoker;

import cz.vity.freerapid.plugins.exceptions.ErrorDuringDownloadingException;
import cz.vity.freerapid.plugins.exceptions.YouHaveToWaitException;
import cz.vity.freerapid.plugins.services.xfilesharing.XFileSharingRunner;

import java.util.List;
import java.util.regex.Matcher;

/**
 * Class which contains main code
 *
 * @author birchie
 */
class FileJokerFileRunner extends XFileSharingRunner {

    @Override
    protected List<String> getDownloadPageMarkers() {
        final List<String> downloadPageMarkers = super.getDownloadPageMarkers();
        downloadPageMarkers.add("This link will be available for ");
        return downloadPageMarkers;
    }

    @Override
    protected void checkDownloadProblems() throws ErrorDuringDownloadingException {
        super.checkDownloadProblems();
        final String content = getContentAsString();
        if (content.contains("until the next download")) {
            final Matcher matcher = getMatcherAgainstContent("(?:(\\d+) hours? )?(?:(\\d+) minutes? )?(?:(\\d+) seconds?)");
            int waitHours = 0, waitMinutes = 0, waitSeconds = 0;
            if (matcher.find()) {
                if (matcher.group(1) != null) {
                    waitHours = Integer.parseInt(matcher.group(1));
                }
                if (matcher.group(2) != null) {
                    waitMinutes = Integer.parseInt(matcher.group(2));
                }
                waitSeconds = Integer.parseInt(matcher.group(3));
            }
            final int waitTime = (waitHours * 60 * 60) + (waitMinutes * 60) + waitSeconds;
            throw new YouHaveToWaitException("You have to wait " + matcher.group(), waitTime);
        }
    }

}