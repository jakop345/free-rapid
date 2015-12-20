package cz.vity.freerapid.plugins.services.onefichier;

import cz.vity.freerapid.plugins.dev.PluginDevApplication;
import cz.vity.freerapid.plugins.webclient.ConnectionSettings;
import cz.vity.freerapid.plugins.webclient.interfaces.HttpFile;
import org.jdesktop.application.Application;

import java.net.URL;

/**
 * @author birchie
 */
public class TestApp extends PluginDevApplication {
    @Override
    protected void startup() {
        final HttpFile httpFile = getHttpFile(); //creates new test instance of HttpFile
        try {
            //we set file URL
            httpFile.setNewURL(new URL("https://fayr7wrmkh.1fichier.com/yify.info_-_the.hungr.games.catchng.fire.2013.rar"));
            //httpFile.setNewURL(new URL("http://jiqrn4nr9e.1fichier.com/%22")); //direct download
            //httpFile.setNewURL(new URL("https://1fichier.com/dir/vFx5uE46"));  // folder
            //the way we connect to the internet
            final ConnectionSettings connectionSettings = new ConnectionSettings();// creates default connection
            //connectionSettings.setProxy("localhost", 8081); //eg we can use local proxy to sniff HTTP communication
            //then we tries to download
            final OneFichierServiceImpl service = new OneFichierServiceImpl(); //instance of service - of our plugin
            /*
            final PremiumAccount config = new PremiumAccount();
            config.setUsername("****");
            config.setPassword("****");
            service.setConfig(config);
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