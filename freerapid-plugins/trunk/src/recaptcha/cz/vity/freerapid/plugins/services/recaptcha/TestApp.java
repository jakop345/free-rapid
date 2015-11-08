package cz.vity.freerapid.plugins.services.recaptcha;

import cz.vity.freerapid.plugins.dev.PluginDevApplication;
import cz.vity.freerapid.plugins.webclient.DownloadClient;
import cz.vity.freerapid.plugins.webclient.interfaces.HttpDownloadClient;
import cz.vity.freerapid.plugins.webclient.interfaces.HttpFile;
import org.jdesktop.application.Application;

/**
 * @author Vity+Team
 */
public class TestApp extends PluginDevApplication {
    @Override
    protected void startup() {
        final HttpFile httpFile = getHttpFile(); //creates new test instance of HttpFile
        try {
            final HttpDownloadClient client = new DownloadClient();
            client.setReferer("http://nitroflare.com/view/D188DB7C09937FD/080703343X.epub/free");
            ReCaptchaSlimerJs reCaptcha = new ReCaptchaSlimerJs("6Lenx_USAAAAAF5L1pmTWvWcH73dipAEzNnmNLgy", client);
            System.out.println(reCaptcha.getChallenge());
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