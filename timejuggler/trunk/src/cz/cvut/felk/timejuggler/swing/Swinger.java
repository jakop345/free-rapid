package cz.cvut.felk.timejuggler.swing;

import application.ApplicationContext;
import application.ResourceManager;
import application.ResourceMap;
import cz.cvut.felk.timejuggler.core.MainApp;

import javax.swing.*;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.MetalTheme;
import java.util.logging.Logger;

/**
 * Pomocna trida pro caste volani nekterych metod. Nastavuje vychozi Look&Feel.
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
     * @param metalTheme           theme pro MetalLook pokud existuje
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
     * Vrati obrazek podle key property v resourcu Nenajde-li se obrazek pod danym kodem, vypise WARNING pokud neni
     * obrazek nalezen
     * @param imagePropertyCode kod obrazku
     * @return obrazek
     */
    public static ImageIcon getIconImage(final String imagePropertyCode) {
        final ResourceMap map = getResourceMap();
        final ImageIcon imageIcon = map.getImageIcon(imagePropertyCode);
        if (imageIcon == null)
            logger.warning("Invalid image property code:" + imagePropertyCode);
        return imageIcon;
    }

    public static ResourceMap getResourceMap() {
        final ApplicationContext ac = MainApp.getInstance(MainApp.class).getContext();
        final ResourceManager rm = ac.getResourceManager();
        return rm.getResourceMap();
    }

    public static Action getAction(Object actionName) {
        final Action action = MainApp.getAContext().getActionMap().get(actionName);
        assert action != null;
        return action;
    }
}
