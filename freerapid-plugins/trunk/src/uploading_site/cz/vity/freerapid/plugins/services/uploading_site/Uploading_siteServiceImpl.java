package cz.vity.freerapid.plugins.services.uploading_site;

import cz.vity.freerapid.plugins.services.xfilesharing.XFileSharingServiceImpl;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginRunner;

/**
 * Class that provides basic info about plugin
 *
 * @author birchie
 */
public class Uploading_siteServiceImpl extends XFileSharingServiceImpl {

    @Override
    public String getServiceTitle() {
        return "Uploading.site";
    }

    @Override
    public String getName() {
        return "uploading.site";
    }

    @Override
    protected PluginRunner getPluginRunnerInstance() {
        return new Uploading_siteFileRunner();
    }

}