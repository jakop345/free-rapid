package cz.vity.freerapid.plugins.services.confidentcaptcha;

import cz.vity.freerapid.plugins.exceptions.CaptchaEntryInputMismatchException;
import cz.vity.freerapid.plugins.webclient.DownloadClient;
import cz.vity.freerapid.plugins.webclient.MethodBuilder;
import cz.vity.freerapid.plugins.webclient.interfaces.DialogSupport;
import cz.vity.freerapid.plugins.webclient.interfaces.HttpDownloadClient;
import cz.vity.freerapid.plugins.webclient.utils.PlugUtils;
import org.apache.commons.httpclient.HttpMethod;

import java.awt.*;
import java.lang.reflect.Field;
import java.util.logging.Logger;

/**
 * @author birchie
 */
public class ConfidentCaptcha {
    private final static Logger logger = Logger.getLogger(ConfidentCaptcha.class.getName());
    private HttpDownloadClient client;
    private String coordinates;
    private String code;

    private static Object captchaLock = null;
    private final DialogSupport dialogSupport;

    public ConfidentCaptcha(final DialogSupport dialogSupport, final HttpDownloadClient client) {
        this.dialogSupport = dialogSupport;
        this.client = client;
        final HttpDownloadClient c = new DownloadClient();
        c.initClient(client.getSettings());
    }

    public void askForCaptcha(final String content) throws Exception {
        final String imagesUrl = PlugUtils.getStringBetween(content, "image_url:'", "'");
        final String sequence = PlugUtils.getStringBetween(content, "categories: [", "],").replace("\"","").replaceAll(",\\s*?", ", ");
        final int clicksNeeded = sequence.split(",").length;
        final ConfidentCaptchaPanel panel = new ConfidentCaptchaPanel(imagesUrl, sequence, clicksNeeded);
        synchronized (getCaptchaLock(dialogSupport)) {
            if (!dialogSupport.showOKCancelDialog(panel, "Captcha")) {
                throw new CaptchaEntryInputMismatchException();
            }

            final String letters = PlugUtils.getStringBetween(content, "letters:'", "'");
            final Dimension imageSize = new Dimension(PlugUtils.getNumberBetween(content, "image_width: Math.floor(", ")"), PlugUtils.getNumberBetween(content, "image_height: Math.floor(", ")"));
            final String tiles = PlugUtils.getStringBetween(content, "cache_key\" value=\"", "-");
            final Dimension imageTiles = new Dimension(Integer.parseInt(tiles.split("X")[0]), Integer.parseInt(tiles.split("X")[1]));

            Dimension actualSize = panel.getImageDimensions();
            double scaleW = imageSize.getWidth() / actualSize.getWidth();
            double scaleH = imageSize.getHeight() / actualSize.getHeight();

            coordinates = "";
            code = "";
            for (Point nextPoint : panel.getClickedPoints()) {
                if (!coordinates.equals(""))
                    coordinates += "|";
                coordinates += new Double(scaleW *nextPoint.getX()).intValue() + "," + new Double(scaleH *nextPoint.getY()).intValue();
                code += pointToLetter(actualSize, imageTiles, nextPoint, letters);
            }
        }
    }

    private String pointToLetter(final Dimension size, final Dimension tiles, final Point point, final String letters) {
        int value = 3 * (new Double(Math.floor(tiles.getHeight() * (point.getY()+1) / (size.getHeight()+2))).intValue())
                + 1 + (new Double(Math.floor(tiles.getWidth() * (point.getX()+1) / (size.getWidth()+2))).intValue());
        return "" + letters.toCharArray()[value-1];
    }

    public MethodBuilder modifyResponseMethod(MethodBuilder methodBuilder, String content) throws Exception {
        HttpMethod post = new MethodBuilder(client)
                .setBaseURL(methodBuilder.getReferer().substring(0, methodBuilder.getReferer().indexOf("/", 7)))
                .setAction(PlugUtils.getStringBetween(content, "callback_url: '", "'"))
                .setParameter("confidentcaptcha_captcha_id", PlugUtils.getStringBetween(content, "id=\"confidentcaptcha_id_captcha_id\" value=\"", "\""))
                .setParameter("confidentcaptcha_code", code)
                .setParameter("confidentcaptcha_click_coordinates", coordinates)
                .setParameter("endpoint", "verify_captcha")
                .setParameter("confidentcaptcha_auth_token", PlugUtils.getStringBetween(content, "id=\"confidentcaptcha_id_auth_token\" value=\"", "\""))
                .setParameter("confidentcaptcha_server_key", PlugUtils.getStringBetween(content, "id=\"confidentcaptcha_id_server_key\" value=\"", "\""))
                .setParameter("confidentcaptcha_cache_key", PlugUtils.getStringBetween(content, "id=\"confidentcaptcha_id_cache_key\" value=\"", "\""))
                .setAjax().toPostMethod();
        client.makeRequest(post, false);

        return methodBuilder.setParameter("confidentcaptcha_code", code)
                .setParameter("confidentcaptcha_click_coordinates", coordinates);
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
