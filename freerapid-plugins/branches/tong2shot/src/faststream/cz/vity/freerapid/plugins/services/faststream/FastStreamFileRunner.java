package cz.vity.freerapid.plugins.services.faststream;

import cz.vity.freerapid.plugins.services.xfileplayer.XFilePlayerRunner;

/**
 * Class which contains main code
 *
 * @author birchie
 */
class FastStreamFileRunner extends XFilePlayerRunner {
    @Override
    protected void correctURL() throws Exception {
        fileURL = fileURL.replaceFirst("faststream.in/", "fastvideo.in/");
    }
}