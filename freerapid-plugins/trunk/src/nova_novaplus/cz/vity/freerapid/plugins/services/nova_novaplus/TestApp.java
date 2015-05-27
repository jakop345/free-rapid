package cz.vity.freerapid.plugins.services.nova_novaplus;

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
            //httpFile.setNewURL(new URL("http://novaplus.nova.cz/porad/ulice/video/3783-exkluzivne-z-ulice-necekane-komplikace-oldovy-krusne-chvile-u-soudu"));  //serviceUrl and avodServiceUrl
            httpFile.setNewURL(new URL("http://novaplus.nova.cz/porad/televizni-noviny/video/5534-televizni-noviny-26-5-2015/"));
            //httpFile.setNewURL(new URL("http://novaplus.nova.cz/porad/televizni-noviny/video/5519-televizni-noviny-25-5-2015"));
            //httpFile.setNewURL(new URL("http://novaplus.nova.cz/porad/tele-tele/video/807-tele-tele-159-dil"));
            //httpFile.setNewURL(new URL("http://novaplus.nova.cz/porad/tele-tele/video/754-tele-tele-155-dil"));

            final ConnectionSettings connectionSettings = new ConnectionSettings();
            //connectionSettings.setProxy("localhost", 8081); //eg we can use local proxy to sniff HTTP communication
            final Nova_NovaPlusServiceImpl service = new Nova_NovaPlusServiceImpl();
            SettingsConfig config = new SettingsConfig();
            config.setVideoQuality(VideoQuality.HQ);
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