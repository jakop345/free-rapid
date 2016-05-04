package cz.vity.freerapid.plugins.services.barrandov;

import cz.vity.freerapid.plugins.dev.PluginDevApplication;
import cz.vity.freerapid.plugins.webclient.ConnectionSettings;
import cz.vity.freerapid.plugins.webclient.hoster.PremiumAccount;
import cz.vity.freerapid.plugins.webclient.interfaces.HttpFile;
import org.jdesktop.application.Application;

import java.net.URL;

/**
 * @author JPEXS
 */
public class TestApp extends PluginDevApplication {
    @Override
    protected void startup() {
        final HttpFile httpFile = getHttpFile(); //creates new test instance of HttpFile
        try {
            //we set file URL
            //httpFile.setNewURL(new URL("http://barrandov.tv/64226-pelisky-slavnych-ochutnavka-marketa-konvickova"));
            httpFile.setNewURL(new URL("http://www.barrandov.tv/video/64723-nase-zpravy-25-4-2016"));
            //httpFile.setNewURL(new URL("http://www.barrandov.tv/video/64725-kurna-co-to-je-25-4-2016")); // Premium acct needed
            //the way we connect to the internet
            final ConnectionSettings connectionSettings = new ConnectionSettings();// creates default connection
            //connectionSettings.setProxy("localhost", 8081); //eg we can use local proxy to sniff HTTP communication
            //then we tries to download
            final BarrandovServiceImpl service = new BarrandovServiceImpl(); //instance of service - of our plugin
            BarrandovSettingsConfig config = new BarrandovSettingsConfig();
            config.setQualitySetting(1);
            service.setConfig(config);
            /*
            final PremiumAccount account = new PremiumAccount();
            account.setUsername("****");
            account.setPassword("****");
            service.setAccount(account);
            //*/
            //runcheck makes the validation
            testRun(service, httpFile, connectionSettings);//download file with service and its Runner
            //all output goes to the console
        } catch (Exception e) {//catch possible exception
            e.printStackTrace(); //writes error output - stack trace to console
        }
        this.exit();//exit application
    }

    /**
     * Main start method for running this application
     * Called from IDE
     *
     * @param args arguments for application
     */
    public static void main(String[] args) {
        Application.launch(TestApp.class, args);//starts the application - calls startup() internally
    }
}