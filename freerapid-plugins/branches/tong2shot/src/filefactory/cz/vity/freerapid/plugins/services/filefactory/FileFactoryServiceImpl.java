package cz.vity.freerapid.plugins.services.filefactory;

import cz.vity.freerapid.plugins.webclient.AbstractFileShareService;
import cz.vity.freerapid.plugins.webclient.hoster.PremiumAccount;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginRunner;

/**
 * @author Kajda
 */
public class FileFactoryServiceImpl extends AbstractFileShareService {
    private static final String SERVICE_NAME = "filefactory.com";
    private static final String PLUGIN_CONFIG_FILE = "plugin_FileFactory.xml";
    private volatile PremiumAccount config;

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
        return new FileFactoryRunner();
    }

    @Override
    public void showOptions() throws Exception {
        PremiumAccount pa = showConfigDialog();
        if (pa != null) config = pa;
    }

    public PremiumAccount showConfigDialog() throws Exception {
        return showAccountDialog(getConfig(), "FileFactory", PLUGIN_CONFIG_FILE);
    }

    PremiumAccount getConfig() throws Exception {
        if (config == null) {
            synchronized (FileFactoryServiceImpl.class) {
                config = getAccountConfigFromFile(PLUGIN_CONFIG_FILE);
            }
        }

        return config;
    }

    public void setConfig(PremiumAccount config) {
        this.config = config;
    }
}