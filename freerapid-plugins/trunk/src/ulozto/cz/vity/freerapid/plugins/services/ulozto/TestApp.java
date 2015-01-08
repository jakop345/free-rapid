package cz.vity.freerapid.plugins.services.ulozto;

import cz.vity.freerapid.plugins.dev.PluginDevApplication;
import cz.vity.freerapid.plugins.webclient.ConnectionSettings;
import cz.vity.freerapid.plugins.webclient.interfaces.HttpFile;
import org.jdesktop.application.Application;

import java.io.IOException;
import java.net.URL;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;

/**
 * @author Ladislav Vitasek & Tomáš Procházka <to.m.p@atomsoft.cz>
 */
public class TestApp extends PluginDevApplication {
    protected void startup() {

        final HttpFile httpFile = getHttpFile();
        try {
            //httpFile.setNewURL(new URL("http://www.ulozto.cz/xda1xMy/penthouse-sex-obsessed-xxx-dvdrip-xvid-qualitx-avi"));
            httpFile.setNewURL(new URL("http://uloz.to/xda1xMy/penthouse-sex-obsessed-xxx-dvdrip-xvid-qualitx-avi"));
            //httpFile.setNewURL(new URL("http://uloz.to/xrgawztg/sample-txt"));           //password: "1234"
            //httpFile.setNewURL(new URL("http://www.ulozto.cz/xFU8sN2G/test-docx")); //password : "password"
            //httpFile.setNewURL(new URL("http://www.ulozto.cz/xE6PNASA/hercule-poirot-smrt-v-oblacich-ts"));
            //httpFile.setNewURL(new URL("http://uloz.to/soubory/ladaber/mlp/s04/"));
            //httpFile.setNewURL(new URL("http://uloz.to/soubory/pesicestice/video/the-originals/2-serie/"));
            //httpFile.setNewURL(new URL("http://www.uloz.to/soubory/readykirken/box/"));
            //httpFile.setNewURL(new URL("http://uloz.to/soubory/readykirken/box/wav-pro-vypaleni/"));
            //httpFile.setNewURL(new URL("http://uloz.to/m/ladaber/mlp/s04/"));
            //httpFile.setNewURL(new URL("http://www.uloz.to/m/hkey/farmacia-lobing-a-ich-spinave-praktiky"));
            //httpFile.setNewURL(new URL("http://pornfile.uloz.to/xDA7JuU8/ceske-porno-darx-28-ceske-anal-oral-hardcore-film-teen-nice-young-czech-holky-old-busty-milf-new-cz-dabing-erotika-xxx-sex-porno-29-3gp"));
            //httpFile.setNewURL(new URL("http://pornfile.uloz.to/xa2BKatg/anal-teens-from-russia-5-xxx-scene1-xfactorplus-com-3gp"));
            final ConnectionSettings settings = new ConnectionSettings();
            //settings.setProxy("localhost", 8081);
            testRun(new UlozToServiceImpl(), httpFile, settings);
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.exit();
    }

    public static void main(String[] args) throws IOException {
        Handler fh = new FileHandler("./TestApp.xml");
        Logger.getLogger("").addHandler(fh);
        Application.launch(TestApp.class, args);
    }
}
