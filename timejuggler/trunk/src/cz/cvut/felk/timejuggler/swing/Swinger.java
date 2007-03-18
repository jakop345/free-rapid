package cz.cvut.felk.timejuggler.swing;

import application.ApplicationContext;
import application.ResourceManager;
import application.ResourceMap;

import javax.swing.*;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.MetalTheme;
import java.util.logging.Logger;

/**
 * Pomocna trida pro caste volani nekterych metod.
 * Nastavuje vychozi Look&Feel.
 * @author Vity
 */
public class Swinger {
    private static final Logger logger = Logger.getLogger(Swinger.class.getName());
    private static final String KUNSTSTOFF = "com.incors.plaf.kunststoff.KunststoffLookAndFeel";

    private Swinger() {
    }

    /**
     * Nastaveni look&feelu
     */
    public static void initLaF() {
        initLafWithTheme(KUNSTSTOFF, new WordRiderMetalTheme());
    }

    /**
     * Nastavi look&feel jako aktivni
     * @param lookAndFeelClassName jmeno tridy look&feelu
     * @param metalTheme theme pro MetalLook pokud existuje
     */
    private static void initLafWithTheme(final String lookAndFeelClassName, final MetalTheme metalTheme) {

        try {
            final LookAndFeel laf = (LookAndFeel) ClassLoader.getSystemClassLoader().loadClass(lookAndFeelClassName).newInstance();

            if (metalTheme != null && laf instanceof MetalLookAndFeel) {
                laf.getClass().getMethod("setCurrentTheme", new Class[]{MetalTheme.class}).invoke(laf, metalTheme);
            }

            UIManager.setLookAndFeel(laf);
        } catch (Exception e) {
            logger.warning("Couldn't set LookandFeel " + KUNSTSTOFF);
        }

    }

    /**
     * Vrati obrazek podle key property v resourcu
     * Nenajde-li se obrazek pod danym kodem, vraci NullPointerException
     * @param imagePropertyCode kod obrazku
     * @return obrazek
     */
    public static ImageIcon getIconImage(final String imagePropertyCode) {
        final ResourceMap map = getResourceMap();
        final ImageIcon imageIcon = map.getImageIcon(imagePropertyCode);
        if (imageIcon == null)
            throw new NullPointerException("Invalid image property code, Image cannot be null");
        return imageIcon;
    }


    public static ResourceMap getResourceMap() {
        final ApplicationContext ac = ApplicationContext.getInstance();
        final ResourceManager rm = ac.getResourceManager();
        return rm.getResourceMap();
    }
}
