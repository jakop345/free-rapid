package cz.vity.freerapid.plugins.services.sweetcaptcha;

import cz.vity.freerapid.plugins.exceptions.CaptchaEntryInputMismatchException;
import cz.vity.freerapid.plugins.exceptions.PluginImplementationException;
import cz.vity.freerapid.plugins.webclient.DownloadClient;
import cz.vity.freerapid.plugins.webclient.MethodBuilder;
import cz.vity.freerapid.plugins.webclient.interfaces.DialogSupport;
import cz.vity.freerapid.plugins.webclient.interfaces.HttpDownloadClient;
import cz.vity.freerapid.plugins.webclient.utils.PlugUtils;
import org.apache.commons.httpclient.HttpMethod;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.regex.Matcher;

/**
 * @author birchie
 */
public class SweetCaptcha {
    private final static Logger logger = Logger.getLogger(SweetCaptcha.class.getName());
    private HttpDownloadClient client;
    private String response;
    private String scKey;

    private static Object captchaLock = null;
    private final DialogSupport dialogSupport;

    public SweetCaptcha(final DialogSupport dialogSupport, final HttpDownloadClient client) {
        this.dialogSupport = dialogSupport;
        this.client = client;
        final HttpDownloadClient c = new DownloadClient();
        c.initClient(client.getSettings());
    }

    public void askForCaptcha(final String content) throws Exception {
        synchronized (getCaptchaLock(dialogSupport)) {
            final Matcher jsMatch = PlugUtils.matcher("function n\\(e,n\\)\\{[^\\}]+?\\}", content);
            if (!jsMatch.find())
                throw new PluginImplementationException("Sweetcaptcha image url decoder not found");
            final String jsDecoder = jsMatch.group(0);

            final Matcher hashMatch = PlugUtils.matcher("hash\"\\)\\.substr\\((\\d+?),(\\d+?)\\)", content);
            if (!hashMatch.find())
                throw new PluginImplementationException("Sweetcaptcha hash splitter not found");
            final int hashStart = Integer.parseInt(hashMatch.group(1).trim());
            final int hashLength = Integer.parseInt(hashMatch.group(2).trim());

            final Matcher apiMatch = PlugUtils.matcher("(sweetcaptcha.com/api[^'\"]+?)['\"]", content);
            if (!apiMatch.find())
                throw new PluginImplementationException("Sweetcaptcha api not found");
            final String sweetCaptchaApiUrl = "http://" + apiMatch.group(1);

            final HttpMethod method = new MethodBuilder(client).setAction(sweetCaptchaApiUrl).setAjax().toGetMethod();
            client.makeRequest(method, true);
            final String apiContent = client.getContentAsString();

            scKey = PlugUtils.getStringBetween(apiContent, "\"k\":\"", "\"");

            final Matcher strMatch = PlugUtils.matcher("click\":\\{[^\\{\\}]*?\"verify\":\"(.+?)\"[^\\{\\}]*?\"challenge\":\"(.+?)\"", apiContent);
            if (!strMatch.find())
                throw new PluginImplementationException("Sweetcaptcha challenge strings not found");
            final String verify = strMatch.group(1);
            final String challenge = strMatch.group(2);

            final String simpleKey = PlugUtils.getStringBetween(apiContent, "simple_key\":\"", "\"");
            final ArrayList<String> imageItemHash = new ArrayList<String>();
            final ArrayList<String> imageItemUrls = new ArrayList<String>();
            final Matcher encImgMatch = PlugUtils.matcher("\"hash\":\"(.+?)\",\"src\":\"(.+?)\"", apiContent);
            while (encImgMatch.find()) {
                imageItemHash.add(encImgMatch.group(1));
                imageItemUrls.add("http:" + decodeUrl(jsDecoder, simpleKey, encImgMatch.group(2)));
            }
            final String imageTargetUrl = "http:" + decodeUrl(jsDecoder, simpleKey, PlugUtils.getStringBetween(apiContent, "\"q\":\"", "\""));


            final SweetCaptchaPanel panel = new SweetCaptchaPanel(verify, challenge, imageTargetUrl, imageItemUrls);
            if (!dialogSupport.showOKCancelDialog(panel, "Captcha")) {
                throw new CaptchaEntryInputMismatchException();
            }

            final int selected = panel.getSelected();
            if (selected < 0)
                throw new CaptchaEntryInputMismatchException("No option selected");

            response = imageItemHash.get(selected).substring(hashStart, hashStart + hashLength);
        }
    }

    private String decodeUrl(final String jsDecoder, final String key, final String encoded) throws Exception {
        try {
            final String function = " OUTPUT=n(\""+key+"\",\""+encoded+"\")";
            ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");
            return engine.eval(jsDecoder + function).toString();
        } catch (Exception e) {
            throw new PluginImplementationException("JS evaluation error " + e.getLocalizedMessage());
        }
    }

    public String getResponseKey() {
        return scKey;
    }

    public String getResponseValue() {
        return response;
    }

    public MethodBuilder modifyResponseMethod(MethodBuilder methodBuilder) throws Exception {
        methodBuilder.setParameter("sckey", scKey);
        methodBuilder.setParameter("scvalue", response);
        methodBuilder.setParameter("scvalue2", "0");
        return methodBuilder;
    }


    private static synchronized Object getCaptchaLock(final DialogSupport dialogSupport) {
        if (captchaLock == null) {
            try {
                final Field field = dialogSupport.getClass().getDeclaredField("captchaLock");
                field.setAccessible(true);
                captchaLock = field.get(null);
            } catch (final Exception e) {
                //ignore
            }
            if (captchaLock == null) {
                captchaLock = new Object();
            }
        }
        return captchaLock;
    }

}
