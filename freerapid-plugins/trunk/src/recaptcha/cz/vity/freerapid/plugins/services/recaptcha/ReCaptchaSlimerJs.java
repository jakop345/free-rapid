package cz.vity.freerapid.plugins.services.recaptcha;

import cz.vity.freerapid.plugins.webclient.interfaces.HttpDownloadClient;
import cz.vity.freerapid.plugins.webclient.utils.PlugUtils;
import cz.vity.freerapid.utilities.LogUtils;
import cz.vity.freerapid.utilities.Utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Logger;
import java.util.regex.Matcher;

/**
 * @author tong2shot
 */
public class ReCaptchaSlimerJs {
    private final static Logger logger = Logger.getLogger(ReCaptchaSlimerJs.class.getName());

    public final static String PATH_WINDOWS = "tools\\slimerjs\\slimerjs.bat";
    public final static String PATH_LINUX = "tools/slimerjs/slimerjs";
    public final static String PATH_XULRUNNER_WINDOWS = "tools\\slimerjs\\xulrunner\\xulrunner.exe";
    private final static String PATH_APP_INI_WINDOWS = "tools\\slimerjs\\application.ini";
    private final static String PATH_APP_INI_LINUX = "tools/slimerjs/application.ini";

    private final String publicKey;
    private final HttpDownloadClient client;


    static {
        //For lightweight nightly version (0.10.0pre).
        //Bump maxversion from 4x to 6x, we're safe for another week :)
        String appIni;
        if (Utils.isWindows()) {
            appIni = Utils.addFileSeparator(Utils.getAppPath()) + PATH_APP_INI_WINDOWS;
        } else {
            appIni = Utils.addFileSeparator(Utils.getAppPath()) + PATH_APP_INI_LINUX;
        }
        File appIniFile = new File(appIni);
        if (appIniFile.exists()) {
            String appIniContent = Utils.loadFile(appIniFile, "UTF-8");
            Matcher matcher = PlugUtils.matcher("MaxVersion\\s*?=\\s*?4", appIniContent);
            if (matcher.find()) {
                appIniContent = appIniContent.replaceFirst("MaxVersion\\s*?=\\s*?4", "MaxVersion=6");
                try {
                    BufferedWriter bw = new BufferedWriter(new FileWriter(appIniFile));
                    bw.write(appIniContent);
                    bw.close();
                } catch (IOException e) {
                    LogUtils.processException(logger, e);
                }
            }
        }
    }

    public ReCaptchaSlimerJs(String publicKey, HttpDownloadClient c) {
        this.publicKey = publicKey;
        this.client = c;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public String getChallenge() throws IOException {

        final String command;
        if (Utils.isWindows()) {
            command = Utils.addFileSeparator(Utils.getAppPath()) + PATH_WINDOWS;
        } else {
            command = Utils.addFileSeparator(Utils.getAppPath()) + PATH_LINUX;
        }

        if (!new File(command).exists()) {
            throw new IOException("SlimerJS not found");
        }

        String referer = (client.getReferer() == null || client.getReferer().isEmpty() ? "https://www.bing.com" : client.getReferer());
        try {
            referer = new URI(referer).toString();
        } catch (URISyntaxException e) {
            referer = "https://www.bing.com";
        }

        String jsContent =
                "var page = require(\"webpage\").create();\n" +
                        "page.onResourceRequested = function(requestData, networkRequest) {\n" +
                        "    if (requestData.url.indexOf(\"/recaptcha/api/image?c=\")>-1) {\n" +
                        "        console.log(requestData.url);\n" +
                        "    }\n" +
                        "};\n" +
                        "var htmlContent = \"<script src='http://ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js'></script>" +
                        "<script>var RecaptchaOptions = { theme : 'clean' };</script>" +
                        "<script type='text/javascript' src='http://www.google.com/recaptcha/api/challenge?k=" + publicKey + "'></script>\";\n" +
                        "page.setContent(htmlContent,\"" + referer + "\", function(status){\n" +
                        "})\n" +
                        "page.close();\n" +
                        "slimer.exit();";
        String tempFileName = "recaptcha_" + System.currentTimeMillis() + new Random().nextInt();
        File tempFile = File.createTempFile(tempFileName, ".js");
        BufferedWriter bw = new BufferedWriter(new FileWriter(tempFile));
        bw.write(jsContent);
        bw.close();

        logger.info("Temp recaptcha script file location: " + tempFile.getCanonicalPath());
        //logger.info(jsContent);

        Scanner scanner = null;
        try {
            final ProcessBuilder processBuilder = new ProcessBuilder(command, tempFile.getCanonicalPath());
            if (Utils.isWindows()) { //windows batch (processor) bug workaround
                processBuilder.environment().put("SLIMERJSLAUNCHER", Utils.addFileSeparator(Utils.getAppPath()) + PATH_XULRUNNER_WINDOWS);
            }
            final Process process = processBuilder.start();
            scanner = new Scanner(process.getInputStream());
            StringBuilder builder = new StringBuilder();
            final String s;
            while (scanner.hasNext()) {
                builder.append(scanner.next());
            }
            s = builder.toString();
            logger.info(s);
            if (s.isEmpty())
                throw new IllegalStateException("No SlimerJS output");
            process.waitFor();
            if (process.exitValue() != 0)
                throw new IOException("SlimerJS process exited abnormally");
            Matcher matcher = PlugUtils.matcher("/recaptcha/api/image\\?c=(.+?)(?:&|\\[\\{)", s);
            if (!matcher.find())
                throw new IOException("ReCaptcha (SlimerJS) challenge not found");
            return matcher.group(1);
        } catch (Exception e) {
            LogUtils.processException(logger, e);
            throw new IOException(e);
        } finally {
            if (scanner != null)
                try {
                    scanner.close();
                } catch (Exception e) {
                    LogUtils.processException(logger, e);
                }
            tempFile.delete();
        }
    }

}
