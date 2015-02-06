package cz.vity.freerapid.plugins.services.vshare_io;

import cz.vity.freerapid.plugins.webclient.AbstractFileShareService;
import cz.vity.freerapid.plugins.webclient.interfaces.ConfigurationStorageSupport;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginRunner;

/**
 * Class that provides basic info about plugin
 *
 * @author birchie
 */
public class VShare_ioServiceImpl extends AbstractFileShareService {
    private static final String CONFIG_FILE = "plugin_VShare_io.xml";
    private volatile VShare_ioSettingsConfig config;

    @Override
    public String getName() {
        return "vshare.io";
    }

    @Override
    public boolean supportsRunCheck() {
        return true;
    }

    @Override
    protected PluginRunner getPluginRunnerInstance() {
        return new VShare_ioFileRunner();
    }


    @Override
    public void showOptions() throws Exception {
        super.showOptions();
        if (getPluginContext().getDialogSupport().showOKCancelDialog(new VShare_ioSettingsPanel(this), "YouPorn settings")) {
            getPluginContext().getConfigurationStorageSupport().storeConfigToFile(config, CONFIG_FILE);
        }
    }

    VShare_ioSettingsConfig getConfig() throws Exception {
        synchronized (VShare_ioServiceImpl.class) {
            final ConfigurationStorageSupport storage = getPluginContext().getConfigurationStorageSupport();
            if (config == null) {
                if (!storage.configFileExists(CONFIG_FILE)) {
                    config = new VShare_ioSettingsConfig();
                } else {
                    config = storage.loadConfigFromFile(CONFIG_FILE, VShare_ioSettingsConfig.class);
                }
            }
            return config;
        }
    }
}