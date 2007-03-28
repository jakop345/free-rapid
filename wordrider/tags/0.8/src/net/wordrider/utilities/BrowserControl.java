package net.wordrider.utilities;

import java.io.File;
import java.util.logging.Logger;

/**
 * @author Vity
 */
public final class BrowserControl {
    private static final String WIN_PATH = "rundll32";
    private static final String WIN_FLAG = "url.dll,FileProtocolHandler";
    private static final String UNIX_PATH = "netscape";
    private static final String UNIX_FLAG = "-remote openURL";
    private final static Logger logger = Logger.getLogger(BrowserControl.class.getName());

    private BrowserControl() {
    }

    public static void showURL(final String url) {
        final boolean windows = Utils.isWindows();
        String cmd = "";
        try {
            if (windows) {
                cmd = WIN_PATH + " " + WIN_FLAG + " " + url;
                Runtime.getRuntime().exec(cmd);
            } else {
                cmd = UNIX_PATH + " " + UNIX_FLAG + "(" + url + ")";
                final Process p = Runtime.getRuntime().exec(cmd);
                final int exitCode = p.waitFor();
                if (exitCode != 0) {
                    cmd = UNIX_PATH + " " + url;
                    Runtime.getRuntime().exec(cmd);
                }
            }
        } catch (Exception e) {
            logger.warning("Couldn't invoke browser, command=" + cmd);
            LogUtils.processException(logger, e);
        }
    }

    public static void openPDF(final File file) {
        final boolean windows = Utils.isWindows();
        String cmd = "";
        try {
            if (windows) {
                cmd = "rundll32 url.dll,FileProtocolHandler " + file.getPath();
            } else {
                cmd = "acroread " + file.getPath();
            }
            Runtime.getRuntime().exec(cmd);
        } catch (Exception e) {
            logger.warning("Couldn't invoke browser, command=" + cmd);
            LogUtils.processException(logger, e);
        }
    }

    public static void showHomepage() {
        showURL(Consts.WEBURL);
    }
}