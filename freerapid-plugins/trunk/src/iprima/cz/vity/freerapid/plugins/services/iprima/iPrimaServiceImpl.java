package cz.vity.freerapid.plugins.services.iprima;

import cz.vity.freerapid.plugins.webclient.AbstractFileShareService;
import cz.vity.freerapid.plugins.webclient.interfaces.ConfigurationStorageSupport;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginRunner;
import cz.vity.freerapid.utilities.LogUtils;

import java.util.logging.Logger;

/**
 * Class that provides basic info about plugin
 *
 * @author JPEXS
 */
public class iPrimaServiceImpl extends AbstractFileShareService {
    private static final String CONFIG_FILE = "iPrimaSettings.xml";
    private static final Logger logger = Logger.getLogger(iPrimaServiceImpl.class.getName());
    private volatile iPrimaSettingsConfig config;

    @Override
    public String getName() {
        return "iprima.cz";
    }

    @Override
    public boolean supportsRunCheck() {
        return true;
    }

    @Override
    protected PluginRunner getPluginRunnerInstance() {
        return new iPrimaFileRunner();
    }

    @Override
    public void showOptions() throws Exception {
        super.showOptions();

        if (getPluginContext().getDialogSupport().showOKCancelDialog(new iPrimaSettingsPanel(this), "iPrima.cz settings")) {
            getPluginContext().getConfigurationStorageSupport().storeConfigToFile(config, CONFIG_FILE);
        }
    }

    public iPrimaSettingsConfig getConfig() throws Exception {
        synchronized (iPrimaServiceImpl.class) {
            final ConfigurationStorageSupport storage = getPluginContext().getConfigurationStorageSupport();
            if (config == null) {
                if (!storage.configFileExists(CONFIG_FILE)) {
                    config = new iPrimaSettingsConfig();
                } else {
                    try {
                        config = storage.loadConfigFromFile(CONFIG_FILE, iPrimaSettingsConfig.class);
                    } catch (Exception e) {
                        LogUtils.processException(logger, e);
                        logger.warning("Broken plugin config file detected. Using default settings.");
                        config = new iPrimaSettingsConfig();
                    }
                }
            }
            return config;
        }
    }

    public void setConfig(final iPrimaSettingsConfig config) {
        synchronized (iPrimaServiceImpl.class) {
            this.config = config;
        }
    }

}