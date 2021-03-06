package cz.vity.freerapid.plugins.services.solvemediacaptcha;

import cz.vity.freerapid.plugins.dev.PluginDevApplication;
import cz.vity.freerapid.plugins.dev.plugimpl.DevDialogSupport;
import cz.vity.freerapid.plugins.webclient.ConnectionSettings;
import cz.vity.freerapid.plugins.webclient.DownloadClient;
import cz.vity.freerapid.plugins.webclient.hoster.CaptchaSupport;
import cz.vity.freerapid.plugins.webclient.interfaces.HttpDownloadClient;
import cz.vity.freerapid.plugins.webclient.interfaces.HttpFile;
import org.jdesktop.application.Application;

import java.util.logging.Logger;

/**
 * @author ntoskrnl
 */
public class TestApp extends PluginDevApplication {
    private final static Logger logger = Logger.getLogger(TestApp.class.getName());

    @Override
    protected void startup() {
        try {
            final HttpFile httpFile = getHttpFile();
            final HttpDownloadClient client = new DownloadClient();
            final ConnectionSettings settings = new ConnectionSettings();
            //settings.setProxy("89.106.14.236", 3128);
            client.initClient(settings);
            final CaptchaSupport captchaSupport = new CaptchaSupport(client, new DevDialogSupport(null));

            /*
            // see http://solvemedia.com/publishers/captcha-type-in
            final SolveMediaCaptcha captcha = new SolveMediaCaptcha("5rpD0bf9RBZ.lyWs.lIKdv8bohgdaCBW", client, captchaSupport);
            captcha.askForCaptcha();
            final MethodBuilder methodBuilder = new MethodBuilder(client).setAction("https://portal.solvemedia.com/portal/public/demo-1?fmt=jsonp&callback=human_update&demo_type=secure");
            captcha.modifyResponseMethod(methodBuilder);
            client.makeRequest(methodBuilder.toGetMethod(), true);
            logger.info(client.getContentAsString());
            */

            //final SolveMediaCaptcha captcha = new SolveMediaCaptcha("a9P0IXFCBW4I3MBD6jhOqaI2-tG7KEKl", client, captchaSupport, getHttpFileDownloader(httpFile,settings), false, "red"); //vidxden
            //final SolveMediaCaptcha captcha = new SolveMediaCaptcha("hfwE8LDc6hPLdz4l4YEntxhGqF.XDx7B", client, captchaSupport, getHttpFileDownloader(httpFile,settings), true, "custom");  //safelinking
            //final SolveMediaCaptcha captcha = new SolveMediaCaptcha("iEXF7zf8za89u9WFCdGzF.noOv34.L8S", client, captchaSupport, getHttpFileDownloader(httpFile,settings)); //ryushare
            //final SolveMediaCaptcha captcha = new SolveMediaCaptcha("oy3wKTaFP368dkJiGUqOVjBR2rOOR7GR", client, captchaSupport, getHttpFileDownloader(httpFile,settings)); //rapidgator
            final SolveMediaCaptcha captcha = new SolveMediaCaptcha("PmNHIzoabGnx1.a18HcKp2KaKlEKu38t", client, captchaSupport, getHttpFileDownloader(httpFile, settings)); //depositfiles
            captcha.askForCaptcha();
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Application.launch(TestApp.class, args);
    }
}
