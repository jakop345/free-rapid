package cz.cvut.felk.timejuggler.utilities;

import cz.cvut.felk.timejuggler.swing.Swinger;

import java.awt.*;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Pomocna trida pro spousteni weboveho browseru nebo emailoveho klienta
 * @author Vity
 */
public class Browser {
    private final static Logger logger = Logger.getLogger(Browser.class.getName());

    private Browser() {
    }

    /**
     * Otevre browser nebo emailoveho klienta
     * @param mailOrUrl pokud hodnota zacina mailto, otevira se klient, jinak browser
     */
    public static void openBrowser(String mailOrUrl) {
        assert mailOrUrl != null;
        if (!(mailOrUrl.length() > 0 && Desktop.isDesktopSupported()))
            return;
        try {
            URI uri = new URI(mailOrUrl);
            if (!mailOrUrl.startsWith("mailto")) {
                Desktop.getDesktop().browse(uri);
            } else {
                Desktop.getDesktop().mail(uri);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Opening browser failed", e);
            Swinger.showErrorDialog(Swinger.getResourceMap().getString("errorOpeningBrowser", e.getMessage()));
        }

    }

}
