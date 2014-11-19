package cz.vity.freerapid.plugins.services.copy_com;

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
            //httpFile.setNewURL(new URL("https://copy.com/0JMAsqGSaU9L"));                      // root folder
            //httpFile.setNewURL(new URL("https://copy.com/s/0JMAsqGSaU9L/002"));                // sub folder
            //httpFile.setNewURL(new URL("https://copy.com/0JMAsqGSaU9L/002/torneo.part1.rev")); // file
            //httpFile.setNewURL(new URL("https://www.copy.com/s/VKi8D4z6fiuwsWS8"));
            //httpFile.setNewURL(new URL("https://copy.com/VKi8D4z6fiuwsWS8/Free%20Trackz"));
            httpFile.setNewURL(new URL("https://copy.com/VKi8D4z6fiuwsWS8/Free%20Trackz/More%20Free%20Tracks.zip"));
            //the way we connect to the internet
            final ConnectionSettings connectionSettings = new ConnectionSettings();// creates default connection
            //connectionSettings.setProxy("localhost", 8081); //eg we can use local proxy to sniff HTTP communication
            //then we tries to download
            final Copy_comServiceImpl service = new Copy_comServiceImpl(); //instance of service - of our plugin
            SettingsConfig config = new SettingsConfig();
            config.setAppendPathToFilename(true);
            service.setConfig(config);
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