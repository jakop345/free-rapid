package cz.vity.freerapid.plugins.services.pornhub;

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
            httpFile.setNewURL(new URL("http://www.pornhub.com/view_video.php?viewkey=1728190603"));
            //httpFile.setNewURL(new URL("http://www.pornhub.com/embed/1728190603"));
            //httpFile.setNewURL(new URL("http://www.pornhub.com/album/449722"));
            //httpFile.setNewURL(new URL("http://www.pornhub.com/photo/4797481"));
            //httpFile.setNewURL(new URL("http://www.pornhub.com/view_video.php?viewkey=ph56e128a099ea8")); //HD
            //the way we connect to the internet
            final ConnectionSettings connectionSettings = new ConnectionSettings();// creates default connection
            //connectionSettings.setProxy("localhost", 9040, Proxy.Type.SOCKS); //eg we can use local proxy to sniff HTTP communication
            //then we tries to download
            final PornHubServiceImpl service = new PornHubServiceImpl(); //instance of service - of our plugin
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