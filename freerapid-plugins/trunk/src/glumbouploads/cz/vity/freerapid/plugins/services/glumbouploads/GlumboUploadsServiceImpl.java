package cz.vity.freerapid.plugins.services.glumbouploads;

import cz.vity.freerapid.plugins.services.xfilesharing.XFileSharingServiceImpl;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginRunner;

/**
 * Class that provides basic info about plugin
 *
 * @author tong2shot
 */
public class GlumboUploadsServiceImpl extends XFileSharingServiceImpl {
    @Override
    public String getServiceTitle() {
        return "GlumboUploads";
    }

    @Override
    public String getName() {
        return "glumbouploads.com";
    }

    @Override
    protected PluginRunner getPluginRunnerInstance() {
        return new GlumboUploadsFileRunner();
    }

}