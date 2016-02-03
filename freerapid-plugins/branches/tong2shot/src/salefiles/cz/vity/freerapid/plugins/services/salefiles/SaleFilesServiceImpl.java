package cz.vity.freerapid.plugins.services.salefiles;

import cz.vity.freerapid.plugins.services.xfilesharing.XFileSharingServiceImpl;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginRunner;

/**
 * Class that provides basic info about plugin
 *
 * @author birchie
 */
public class SaleFilesServiceImpl extends XFileSharingServiceImpl {

    @Override
    public String getServiceTitle() {
        return "SaleFiles";
    }

    @Override
    public String getName() {
        return "salefiles.com";
    }

    @Override
    protected PluginRunner getPluginRunnerInstance() {
        return new SaleFilesFileRunner();
    }

}