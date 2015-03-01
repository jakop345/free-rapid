package cz.vity.freerapid.plugins.services.streamcz;

import cz.vity.freerapid.plugins.dev.PluginDevApplication;
import cz.vity.freerapid.plugins.webclient.ConnectionSettings;
import cz.vity.freerapid.plugins.webclient.interfaces.HttpFile;
import org.jdesktop.application.Application;

import java.net.URL;

/**
 * @author Ladislav Vitasek
 */
public class TestApp extends PluginDevApplication {
    protected void startup() {

        final HttpFile httpFile = getHttpFile();
        try {
            //httpFile.setNewURL(new URL("http://www.stream.cz/video/265710-agata-a-v-i-p-ky-pipky-bozsky-leos-mares"));
            //httpFile.setNewURL(new URL("http://www.stream.cz/menudomu/769916-entrecote-kuskus"));
            //httpFile.setNewURL(new URL("http://www.stream.cz/fenomen/562584-apokalypsa"));
            //httpFile.setNewURL(new URL("https://www.stream.cz/jidlo-s-r-o/10004917-cukr-1-repny-cukrovar"));
            //httpFile.setNewURL(new URL("https://www.stream.cz/jidlo-s-r-o/10004829-chlazene-pastiky-2-malovyroba"));
            httpFile.setNewURL(new URL("https://www.stream.cz/pohadky/krasty-a-moucha/10004521-brusleni"));
            //httpFile.setNewURL(new URL("https://www.stream.cz/pohadky/krasty-a-moucha")); //show
            //httpFile.setNewURL(new URL("https://www.stream.cz/porady/menudomu")); //show
            StreamCzServiceImpl service = new StreamCzServiceImpl();
            SettingsConfig config = new SettingsConfig();
            //config.setVideoQuality(VideoQuality._1080);
            service.setConfig(config);
            testRun(service, httpFile, new ConnectionSettings());
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.exit();
    }

    public static void main(String[] args) {
        Application.launch(TestApp.class, args);
    }
}