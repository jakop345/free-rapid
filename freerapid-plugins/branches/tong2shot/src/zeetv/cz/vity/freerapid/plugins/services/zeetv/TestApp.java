package cz.vity.freerapid.plugins.services.zeetv;

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
            //httpFile.setNewURL(new URL("http://www.zeetv.com/shows/kumkum-bhagya/video/kumkum-bhagya-episode-216-february-6-2015-full-episode.html"));
            httpFile.setNewURL(new URL("http://www.zeetv.com/shows/sa-re-ga-ma-pa-lil-champs-season-5/videos/sa-re-ga-ma-pa-lil-champs-5-episode-11-january-31-2015-full-episode.html"));
            final ConnectionSettings connectionSettings = new ConnectionSettings();
            //connectionSettings.setProxy("localhost", 8081); //eg we can use local proxy to sniff HTTP communication
            final ZeeTvServiceImpl service = new ZeeTvServiceImpl();
            SettingsConfig config = new SettingsConfig();
            config.setVideoQuality(VideoQuality._360);
            service.setConfig(config);
            //setUseTempFiles(true);
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