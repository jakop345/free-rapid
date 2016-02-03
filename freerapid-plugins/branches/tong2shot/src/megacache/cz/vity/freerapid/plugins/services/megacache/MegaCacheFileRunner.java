package cz.vity.freerapid.plugins.services.megacache;

import cz.vity.freerapid.plugins.services.xfilesharing.XFileSharingRunner;
import cz.vity.freerapid.plugins.webclient.MethodBuilder;

/**
 * Class which contains main code
 *
 * @author birchie
 */
class MegaCacheFileRunner extends XFileSharingRunner {

    @Override
    protected void stepPassword(final MethodBuilder methodBuilder) throws Exception {
    }

    @Override
    protected MethodBuilder getXFSMethodBuilder() throws Exception {
        return getXFSMethodBuilder(getContentAsString().replaceAll("(?s)<!--.+?-->", ""));
    }
}