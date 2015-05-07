package cz.vity.freerapid.plugins.services.twitchtv;

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
            //httpFile.setNewURL(new URL("http://www.twitch.tv/paradoxinteractive/b/326746473"));
            //httpFile.setNewURL(new URL("http://media-cdn.twitch.tv/store44.media44/archives/2012-7-30/highlight_326746473.flv?title=Oops%21+Penalty%21"));
            //httpFile.setNewURL(new URL("http://www.twitch.tv/tsm_chaox/b/328855029"));
            //httpFile.setNewURL(new URL("http://en.twitch.tv/teamsp00ky/b/330898023")); //multiparts
            //httpFile.setNewURL(new URL("http://en.twitch.tv/teamsp00ky/b/384333611"));

            //httpFile.setNewURL(new URL("http://www.twitch.tv/saltthesalty/b/656119014")); //archive multiparts
            //httpFile.setNewURL(new URL("http://www.twitch.tv/saltthesalty/b/656119014?itempos=2&title=More+GTA+V+%2F%2F+More+peyote+hunting+%3AD-2")); //archive multiparts item
            //httpFile.setNewURL(new URL("http://www.twitch.tv/bradacus/c/6614432")); //chapter multiparts
            //httpFile.setNewURL(new URL("http://www.twitch.tv/bradacus/c/6614432?itempos=6&title=Trevor+Visits+A+Bar...-6")); //chapter multiparts item

            httpFile.setNewURL(new URL("http://www.twitch.tv/norddeutscherjunge/v/4484480")); //vod
            //httpFile.setNewURL(new URL("http://www.twitch.tv/wgleu/v/4404821")); //vod
            final ConnectionSettings connectionSettings = new ConnectionSettings();
            //connectionSettings.setProxy("localhost", 8081); //eg we can use local proxy to sniff HTTP communication
            final TwitchTvServiceImpl service = new TwitchTvServiceImpl();
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