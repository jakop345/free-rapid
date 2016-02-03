package cz.vity.freerapid.plugins.services.canalbrasilglobo;

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
            //httpFile.setNewURL(new URL("http://canalbrasil.globo.com/programas/larica-total/videos/1902870.html"));
            //httpFile.setNewURL(new URL("http://canalbrasil.globo.com/programas/302/videos/3667306.html"));
            //httpFile.setNewURL(new URL("http://canalbrasil.globo.com/programas/pornolandia/videos/3670071.html"));
            //httpFile.setNewURL(new URL("http://canalbrasil.globo.com/programas/matador-de-passarinho/videos/3630343.html"));
            //httpFile.setNewURL(new URL("http://globotv.globo.com/rede-globo/programa-do-jo/v/clovis-de-barros-lanca-o-livro-a-filosofia-explica-as-grandes-questoes-da-humanidade/2854196/")); //multipart
            //httpFile.setNewURL(new URL("http://globotv.globo.com/rede-globo/programa-do-jo/v/clovis-de-barros-lanca-o-livro-a-filosofia-explica-as-grandes-questoes-da-humanidade/2854196/?resourceId=5246e680dd23810e0b001b63&fname=Programa+do+J%C3%B4+-+Cl%C3%B3vis+de+Barros+lan%C3%A7a+o+livro+%22A+filosofia+explica+as+grandes+quest%C3%B5es+da+humanidade%27%27-1"));
            httpFile.setNewURL(new URL("http://globotv.globo.com/rede-globo/programa-do-jo/v/clovis-de-barros-lanca-o-livro-a-filosofia-explica-as-grandes-questoes-da-humanidade/2854196/?resourceId=5246edabdd23810e1e0017d3&fname=Programa+do+J%C3%B4+-+Cl%C3%B3vis+de+Barros+lan%C3%A7a+o+livro+%22A+filosofia+explica+as+grandes+quest%C3%B5es+da+humanidade%27%27-2"));
            final ConnectionSettings connectionSettings = new ConnectionSettings();
            //connectionSettings.setProxy("localhost", 8081); //eg we can use local proxy to sniff HTTP communication
            final CanalBrasilGloboServiceImpl service = new CanalBrasilGloboServiceImpl();
            SettingsConfig config = new SettingsConfig();
            config.setVideoQuality(VideoQuality._360);
            service.setConfig(config);
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