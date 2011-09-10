package cz.vity.freerapid.plugins.services.ulozto;

import cz.vity.freerapid.plugins.dev.PluginDevApplication;
import cz.vity.freerapid.plugins.webclient.ConnectionSettings;
import cz.vity.freerapid.plugins.webclient.interfaces.HttpFile;
import org.jdesktop.application.Application;

import java.net.URL;

/**
 * @author Ladislav Vitasek & Tom� Proch�zka <to.m.p@atomsoft.cz>
 */
public class TestApp extends PluginDevApplication {
    protected void startup() {

        final HttpFile httpFile = getHttpFile();
        try {
            //httpFile.setNewURL(new URL("http://www.uloz.to/1579588/7-harry-potter-a-relikvie-smrti-kniha-mp3-svet-warez-cz-part2-rar"));
            httpFile.setNewURL(new URL("http://www.ulozto.cz/7098526/la-professoressa-lingur-italia-porno-pantyhose-incest-mature-gangbang-mpg"));
            testRun(new UlozToServiceImpl(), httpFile, new ConnectionSettings());
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.exit();
    }

    public static void main(String[] args) {
        Application.launch(TestApp.class, args);
    }
}
