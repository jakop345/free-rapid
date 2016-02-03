package cz.vity.freerapid.plugins.services.restfile;

import cz.vity.freerapid.plugins.services.xfilesharing.XFileSharingRunner;

/**
 * Class which contains main code
 *
 * @author tong2shot
 */
class RestFileFileRunner extends XFileSharingRunner {
    @Override
    protected void correctURL() throws Exception {
        fileURL = fileURL.replaceFirst("restfile\\.com/", "restfilee.com/");
    }

}