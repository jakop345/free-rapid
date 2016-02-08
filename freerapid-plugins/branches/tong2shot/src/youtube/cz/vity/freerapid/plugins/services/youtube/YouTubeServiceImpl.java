package cz.vity.freerapid.plugins.services.youtube;

import cz.vity.freerapid.plugins.webclient.AbstractFileShareService;
import cz.vity.freerapid.plugins.webclient.interfaces.ConfigurationStorageSupport;
import cz.vity.freerapid.plugins.webclient.interfaces.HttpFile;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginRunner;

/**
 * @author Kajda
 */
public class YouTubeServiceImpl extends AbstractFileShareService {

    private static final String SERVICE_NAME = "youtube.com";
    private static final String CONFIG_FILE = "YouTubeSettings.xml";
    private volatile YouTubeSettingsConfig config; //global config

    @Override
    public String getName() {
        return SERVICE_NAME;
    }

    @Override
    public boolean supportsRunCheck() {
        return true;
    }

    @Override
    protected PluginRunner getPluginRunnerInstance() {
        return new YouTubeRunner();
    }

    @Override
    public void showOptions() throws Exception {
        super.showOptions();

        if (getPluginContext().getDialogSupport().showOKCancelDialog(new YouTubeSettingsPanel(this), "YouTube settings")) {
            getPluginContext().getConfigurationStorageSupport().storeConfigToFile(config, CONFIG_FILE);
        }
    }

    public YouTubeSettingsConfig getConfig() throws Exception {
        final ConfigurationStorageSupport storage = getPluginContext().getConfigurationStorageSupport();
        if (config == null) {
            if (!storage.configFileExists(CONFIG_FILE)) {
                config = new YouTubeSettingsConfig();
            } else {
                try {
                    config = storage.loadConfigFromFile(CONFIG_FILE, YouTubeSettingsConfig.class);
                } catch (Exception e) {
                    config = new YouTubeSettingsConfig();
                }
            }
        }
        return config;
    }

    public void setConfig(final YouTubeSettingsConfig config) {
        synchronized (YouTubeServiceImpl.class) {
            this.config = config;
        }
    }

    @Override
    public void showLocalOptions(HttpFile httpFile) throws Exception {
        YouTubeSettingsPanel YouTubeSettingsPanel = new YouTubeSettingsPanel(this, httpFile);
        if (getPluginContext().getDialogSupport().showOKCancelDialog(YouTubeSettingsPanel, "YouTube local settings")) {
            httpFile.setLocalPluginConfig(storeConfigToString(YouTubeSettingsPanel.getConfig()));
        }
    }

    /**
     * Clone global config
     *
     * @return global config clone
     * @throws Exception
     */
    private YouTubeSettingsConfig cloneConfig() throws Exception {
        return cloneConfig(getConfig(), YouTubeSettingsConfig.class);
    }

    public YouTubeSettingsConfig getLocalConfig(HttpFile httpFile) throws Exception {
        YouTubeSettingsConfig result;
        String configAsString = httpFile.getLocalPluginConfig();
        if (configAsString == null) {
            result = cloneConfig();
        } else {
            try {
                result = loadConfigFromString(configAsString, YouTubeSettingsConfig.class);
            } catch (Exception e) {
                result = cloneConfig();
            }
        }
        return result;
    }
}