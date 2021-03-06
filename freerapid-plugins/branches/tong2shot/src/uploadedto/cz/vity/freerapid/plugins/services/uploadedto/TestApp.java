package cz.vity.freerapid.plugins.services.uploadedto;

import cz.vity.freerapid.plugins.dev.PluginDevApplication;
import cz.vity.freerapid.plugins.webclient.ConnectionSettings;
import cz.vity.freerapid.plugins.webclient.interfaces.HttpFile;
import org.jdesktop.application.Application;

import java.net.URL;

/**
 * Test application for uploaded.to
 *
 * @author Ladislav Vitasek
 */
public class TestApp extends PluginDevApplication {
    @Override
    protected void startup() {
        final HttpFile httpFile = getHttpFile(); //creates new test instance of HttpFile
        try {
            //we set file URL
            //httpFile.setNewURL(new URL("http://uploaded.net/file/5wnd0ouw/Scryed_Alteration.part31.rar"));
            //httpFile.setNewURL(new URL("http://ul.to/folder/aec6gu"));
            httpFile.setNewURL(new URL("http://ul.to/pf5noeb9/iraff5.mp4"));
            //the way we connect to the internet
            final ConnectionSettings connectionSettings = new ConnectionSettings();// creates default connection
            //connectionSettings.setProxy("localhost", 8118); //eg we can use local proxy to sniff HTTP communication
            //then we tries to download
            final UploadedToShareServiceImpl service = new UploadedToShareServiceImpl(); //instance of service - of our plugin
            /*
            final PremiumAccount config = new PremiumAccount();
            config.setUsername("*****");
            config.setPassword("*****");
            service.setConfig(config);
            //*/
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
