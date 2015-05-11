package cz.vity.freerapid.plugins.services.uptobox;

import cz.vity.freerapid.plugins.dev.PluginDevApplication;
import cz.vity.freerapid.plugins.webclient.ConnectionSettings;
import cz.vity.freerapid.plugins.webclient.interfaces.HttpFile;
import org.jdesktop.application.Application;

import java.net.URL;

/**
 * @author tong2shot
 */
public class TestApp extends PluginDevApplication {
    @Override
    protected void startup() {
        final HttpFile httpFile = getHttpFile();
        try {
            //httpFile.setNewURL(new URL("http://uptobox.com/q8q90xhx6q2d"));
            //httpFile.setNewURL(new URL("http://uptobox.com/njep7t0s3oy6"));
            httpFile.setNewURL(new URL("https://uptobox.com/9xs4v2xapnxt"));
            final ConnectionSettings connectionSettings = new ConnectionSettings();
            //connectionSettings.setProxy("118.97.197.176", 80); //eg we can use local proxy to sniff HTTP communication
            final UptoBoxServiceImpl service = new UptoBoxServiceImpl();
            //PremiumAccount pa = new PremiumAccount();
            //pa.setUsername("***");
            //pa.setPassword("***");
            //service.setConfig(pa);
            testRun(service, httpFile, connectionSettings);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.exit();
    }

    /**
     * Main start method for running this application
     * Called from IDE
     *
     * @param args arguments for application
     */
    public static void main(String[] args) {
        Application.launch(TestApp.class, args);
    }
}