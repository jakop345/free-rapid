package cz.vity.freerapid.plugins.services.yourvideohost;

import cz.vity.freerapid.plugins.services.xfileplayer.XFilePlayerRunner;
import cz.vity.freerapid.plugins.webclient.MethodBuilder;

/**
 * Class which contains main code
 *
 * @author birchie
 */
class YourVideoHostFileRunner extends XFilePlayerRunner {
    @Override
    protected MethodBuilder getXFSMethodBuilder() throws Exception {
        return getXFSMethodBuilder(getContentAsString());
    }
}