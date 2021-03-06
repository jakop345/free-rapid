package cz.vity.freerapid.plugins.services.cbc;

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
            //httpFile.setNewURL(new URL("http://www.cbc.ca/player/Radio/Canada%20Reads/ID/2659629901/"));
            //httpFile.setNewURL(new URL("http://www.cbc.ca/player/Shows/ID/2661057045/"));
            //httpFile.setNewURL(new URL("http://www.cbc.ca/player/News/ID/2660371774/?page=6&sort=MostRecent")); //srt
            httpFile.setNewURL(new URL("http://www.cbc.ca/player/News/ID/2661249760/?sort=MostRecent")); //dfxp 's' style
            final ConnectionSettings connectionSettings = new ConnectionSettings();
            //connectionSettings.setProxy("localhost", 8081); //eg we can use local proxy to sniff HTTP communication
            final CbcServiceImpl service = new CbcServiceImpl();
            SettingsConfig config = new SettingsConfig();
            config.setVideoQuality(VideoQuality._360);
            config.setDownloadSubtitles(true);
            service.setConfig(config);
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