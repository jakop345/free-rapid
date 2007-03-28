package net.wordrider.core.actions;

import net.wordrider.core.AppPrefs;
import net.wordrider.utilities.Consts;
import net.wordrider.utilities.LogUtils;
import net.wordrider.utilities.Utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.logging.Logger;

/**
 * @author Vity
 */
final class CheckVersion extends RiderSwingWorker {
    public static final int CONNECT_NEW_VERSION = 0;
    public static final int CONNECT_SAME_VERSION = 8;
    public static final int CONNECT_ERROR_EXCEPTION = 16;
    public static final int CONNECT_ERROR_INETCONNECTION_NOTAVAILABLE = 128;
    //   public static final int CONNECT_ERROR_AUTENTIFICATION = 256;
    private final static Logger logger = Logger.getLogger(CheckVersion.class.getName());
    private static final String PARAM_VERSION = "version";

    // --Commented out by Inspection START (4.2.05 16:17):
    //    public CheckVersion() {
    //        super(false);
    //    }
    // --Commented out by Inspection STOP (4.2.05 16:17)

    public CheckVersion(final boolean useLoadingDialog) {
        super(useLoadingDialog);
        if (useLoadingDialog) {
            dialogToClose.getProgressBar().setIndeterminate(true);
        }
    }

    public void finished() {
        super.finished();    //call to super
        logger.info("--Checking version end--");
    }

    public final Object construct() {
        logger.info("--Checking version start--");
        if (AppPrefs.getProperty(AppPrefs.PROXY_USE, false)) {
            System.setProperty("proxySet", "true");
            System.setProperty("https.proxyHost", AppPrefs.getProperty(AppPrefs.PROXY_URL, "localhost"));
            System.setProperty("https.proxyPort", AppPrefs.getProperty(AppPrefs.PROXY_PORT, "8080"));
            System.setProperty("proxyHost", AppPrefs.getProperty(AppPrefs.PROXY_URL, "localhost"));
            System.setProperty("proxyPort", AppPrefs.getProperty(AppPrefs.PROXY_PORT, "8080"));
            if (AppPrefs.getProperty(AppPrefs.PROXY_LOGIN, false)) {
                Authenticator.setDefault(new HttpAuthenticateProxy(AppPrefs.getProperty(AppPrefs.PROXY_USERNAME, ""), Utils.generateXorString(AppPrefs.getProperty(AppPrefs.PROXY_PASSWORD, ""))));
            }
        } else
            System.setProperty("proxySet", "false");
        return checkIt();
        //return null;
    }

    private int checkIt() {
        HttpURLConnection urlConn = null;
        showInfoWhileLoading("message.connect.status.connecting");
        try {
            urlConn = (HttpURLConnection) new URL(Consts.WEBURL_CHECKNEWVERSION).openConnection();
            urlConn.setDoOutput(true);
            urlConn.setDoInput(true);
            urlConn.setUseCaches(false);
            final DataOutputStream bufferOut = new DataOutputStream(urlConn.getOutputStream());
            logger.info("Connected to WordRider.net, Writing params");
            bufferOut.write(Utils.addParam("", PARAM_VERSION, Consts.APPVERSION).getBytes());
            bufferOut.close();
            logger.info("reading Response");
            showInfoWhileLoading("message.connect.status.checking");
            final BufferedReader br = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
            final String line = br.readLine();
            br.close();
            logger.info("disconnecting");
            showInfoWhileLoading("message.connect.status.disconnect");
            urlConn.disconnect();
            //if (line != null && line.startsWith("#") && line.endsWith("#") && !line.toLowerCase().equals("#" + Consts.APPVERSION.toLowerCase() + "#")) {
            if (line != null && line.toLowerCase().contains("yes"))
                 //   return CONNECT_SAME_VERSION;
                return CONNECT_NEW_VERSION;

        } catch (UnknownHostException e) {
            if (urlConn != null) urlConn.disconnect();
            return CONNECT_ERROR_INETCONNECTION_NOTAVAILABLE;
        } catch (IOException e) {
            errorMessage = e.getMessage();
            LogUtils.processException(logger, e);
            if (urlConn != null) urlConn.disconnect();
            return CONNECT_ERROR_EXCEPTION;
        }

        return CONNECT_SAME_VERSION;
    }

    private static final class HttpAuthenticateProxy extends Authenticator {
        private final String proxyUsername;
        private final String proxyPassword;

        public HttpAuthenticateProxy(final String userName, final String password) {
            super();
            this.proxyUsername = userName;
            this.proxyPassword = password;
        }

        protected final PasswordAuthentication getPasswordAuthentication() {
            // username, password
            // sets http authentication
            return new PasswordAuthentication(proxyUsername,
                    proxyPassword.toCharArray());
        }
    }

}
