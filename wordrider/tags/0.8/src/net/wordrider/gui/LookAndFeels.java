package net.wordrider.gui;

import net.wordrider.core.AppPrefs;
import net.wordrider.core.MainApp;
import net.wordrider.core.MainAppFrame;
import net.wordrider.utilities.Consts;
import net.wordrider.utilities.LogUtils;
import net.wordrider.utilities.Utils;

import javax.swing.*;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.MetalTheme;
import java.awt.*;
import java.io.File;
import java.io.FilenameFilter;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Vity
 */
public final class LookAndFeels {
    private static final String KUNSTSTOFF = "com.incors.plaf.kunststoff.KunststoffLookAndFeel";

    private static final String LOOK_AND_FEEL_SELECTED_KEY = "lookAndFeel";
    private static final String LOOK_AND_FEEL_OPAQUE_KEY = "lafOpaque";
    private static final String THEME_SELECTED_KEY = "theme";
    private LaF selectedLookAndFeel;
    private final static Logger logger = Logger.getLogger(LookAndFeels.class.getName());
    private static final LookAndFeels instance = new LookAndFeels();

    private ClassLoader classLoader = null;

    private Vector<LaF> availableLaFs = null;


    public static LookAndFeels getInstance() {
        return instance;
    }

    private LookAndFeels() {
        classLoader = initClassLoader();
        final String selectedLookAndFeelClassName = AppPrefs.getProperty(LOOK_AND_FEEL_SELECTED_KEY, KUNSTSTOFF);
        final boolean opaque = AppPrefs.getProperty(LOOK_AND_FEEL_OPAQUE_KEY, true);
        String selectedTheme = AppPrefs.getProperty(THEME_SELECTED_KEY, null);

        if (selectedTheme == null && selectedLookAndFeelClassName.equals(KUNSTSTOFF))
            selectedTheme = WordRiderMetalTheme.class.getName();
        selectedLookAndFeel = new LaF(selectedLookAndFeelClassName, "", selectedTheme, opaque);
    }

    private ClassLoader initClassLoader() {
        final String path = AppPrefs.getAppPath() + "lib";
        logger.info("Loading plugin path " + path);
        final File file = new File(path);
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try {
            if (file.exists()) {
                final File[] jars = file.listFiles(new FilenameFilter() {
                    public boolean accept(final File dir, final String name) {
                        return name.endsWith(".jar");
                    }
                });
                final int jarsCount = jars.length;
                final URL[] urls = new URL[jarsCount];
                final boolean isDebug = logger.isLoggable(Level.INFO);
                for (int i = 0; i < jarsCount; ++i) {
                    urls[i] = jars[i].toURL();
                    if (isDebug)
                        logger.info("Loading URL with a jar " + urls[i]);
                }
                classLoader = new URLClassLoader(urls, classLoader);
            }
            return classLoader;
        } catch (Exception e) {
            LogUtils.processException(logger, e);
            return classLoader;
        }
    }

    // --Commented out by Inspection START (26.2.05 17:31):
    //    public final boolean isSelectedLaF(final LaF laf) {
    //        return selectedLookAndFeel.equals(laf);
    //    }
    // --Commented out by Inspection STOP (26.2.05 17:31)

    public final Vector<LaF> getAvailableLookAndFeels() {
        if (availableLaFs == null) {
            availableLaFs = new Vector<LaF>(5);
            final Properties properties = Utils.loadProperties(Consts.LAFSDIRFILE, true);
            final String namePostfix = ".name", themePostfix = ".theme", opaquePostfix = ".opaque", alonePostfix = ".alone";
            final String lafPrefix = "laf";
            int counter = -1;
            String className, lafID, theme, nameLaF, themeCode, themeName;
            boolean opaque;
            while ((className = properties.getProperty(lafID = (lafPrefix + ++counter))) != null) {
                if (isPresent(className) != null) {
                    opaque = properties.getProperty(lafID + opaquePostfix, "true").equals("true");
                    int themeCounter = -1;
                    nameLaF = properties.getProperty(lafID + namePostfix, className);
                    if (properties.getProperty(lafID + alonePostfix, "true").equals("true"))
                        availableLaFs.add(new LaF(className, nameLaF, null, opaque));
                    while ((theme = properties.getProperty(themeCode = (lafID + themePostfix + ++themeCounter), null)) != null) {
                        if (isPresent(theme) != null) {
                            themeName = nameLaF + " - " + properties.getProperty(themeCode + namePostfix, " - theme");
                            availableLaFs.add(new LaF(className, themeName, theme, opaque));
                        }
                    }
                }
            }
        }
        return availableLaFs;
    }

    public final LaF getSelectedLaF() {
        return selectedLookAndFeel;
    }

    public final void storeSelectedLaF(final LaF laf) {
        AppPrefs.storeProperty(LOOK_AND_FEEL_SELECTED_KEY, laf.getClassName());
        AppPrefs.storeProperty(LOOK_AND_FEEL_OPAQUE_KEY, laf.isToolbarOpaque());
        if (laf.getThemeClass() == null) {
            AppPrefs.removeProperty(THEME_SELECTED_KEY);
        } else
            AppPrefs.storeProperty(THEME_SELECTED_KEY, laf.getThemeClass());
        selectedLookAndFeel = laf;
    }

    public final void loadLookAndFeelSettings() {
        loadLookAndFeel(selectedLookAndFeel, false);
    }

    private Class<?> isPresent(final String className) {
        try {
            return classLoader.loadClass(className);
        } catch (UnsupportedClassVersionError ex) {
            logger.info("Possible problem. Look and feel class/theme " + className + " cannot be instantied. Probably higher version of the JDK is required.");
            return null;
        } catch (Exception e) {
            if (logger.isLoggable(Level.INFO)) {
                logger.info("Possible problem.Look and feel class/theme " + className + " was not detected(not in /lib?).Check " + Consts.LAFSDIRFILE);
            }
            return null;
        }
    }

    public final boolean loadLookAndFeel(final LaF laf, final boolean updateTree) {
        final String lookAndFeelClassName = laf.getClassName();
        final String themeClassName = laf.getThemeClass();
        try {
            Thread.currentThread().setContextClassLoader(classLoader);
            if (lookAndFeelClassName.equals(KUNSTSTOFF))
                initKunststoff();
            else
                initLaf(lookAndFeelClassName, themeClassName);
        } catch (Exception e) {
            logger.warning(lookAndFeelClassName + " was not found.");
            if (!lookAndFeelClassName.equals(KUNSTSTOFF)) {
                if (logger.isLoggable(Level.INFO))
                    LogUtils.processException(logger, e);
                try {
                    initKunststoff();
                } catch (Exception ex) {
                    logger.warning("Kunststoff Look and Feel was not found. Using a default metal theme.");
                    return false;
                }
            }
            return false;
        } finally {
            if (updateTree) {
                final MainAppFrame mainFrame = MainApp.getInstance().getMainAppFrame();
                mainFrame.getManagerDirector().beforeLookAndFeelUpdate();
                updateAllUIs();
                UIManager.getLookAndFeel().getDefaults();
                //MainApp.getInstance().getMainAppFrame().pack();
                mainFrame.getManagerDirector().afterLookAndFeelUpdate();
                mainFrame.invalidate();
                mainFrame.validate();
                mainFrame.repaint();

                //  UIManager.getLookAndFeel().getDefaults();
            }
        }
        return true;
    }

    private void initKunststoff() throws Exception {
        initLafWithTheme(KUNSTSTOFF, new WordRiderMetalTheme());
    }

    @SuppressWarnings({"RedundantArrayCreation"})
    private void initLafWithTheme(final String lookAndFeelClassName, final MetalTheme metalTheme) throws Exception {
        final LookAndFeel laf = (LookAndFeel) classLoader.loadClass(lookAndFeelClassName).newInstance();
        //Object[] keys = UIManager.getDefaults().keySet().toArray();
        //for (int i=0 ; i < keys.length ; i++) {
        //  UIManager.put(keys[i],null);
        //}

        if (metalTheme != null && laf instanceof MetalLookAndFeel) {
            laf.getClass().getMethod("setCurrentTheme", new Class[]{MetalTheme.class}).invoke(laf, new Object[]{metalTheme});
        }
        // PlasticLookAndFeel.setMyCurrentTheme(new MyTheme());
        UIManager.put("ClassLoader", classLoader);
        UIManager.setLookAndFeel(laf);
        UIManager.put("ClassLoader", classLoader);
    }

    //    private static class MyTheme extends com.jgoodies.looks.plastic.theme.InvertedColorTheme {
    //        public MyTheme() {
    //            super();    //call to super
    //        }
    //    }

    private void initLaf(final String lookAndFeelClassName, final String themeClassName) throws Exception {
        if (themeClassName != null) {
            final Class<?> themeClass = isPresent(themeClassName);
            if (themeClass != null) {
                final Object instanceTheme = themeClass.newInstance();
                if (instanceTheme instanceof MetalTheme)
                    initLafWithTheme(lookAndFeelClassName, (MetalTheme) instanceTheme);
                else {
                    logger.warning("Theme " + instanceTheme.getClass().getName() + " cannot be set.Theme is not an instance of Metaltheme");
                    initLafWithTheme(lookAndFeelClassName, null);
                }
            }
        } else
            initLafWithTheme(lookAndFeelClassName, null);
    }

    /**
     * Method to attempt a dynamic update for any GUI accessible by this JVM. It will filter through all frames and
     * sub-components of the frames.
     */
    private static void updateAllUIs() {
        final Frame[] frames = Frame.getFrames();
        for (Frame frame : frames) {
            updateWindowUI(frame);
        }
    }

    /**
     * Method to attempt a dynamic update for all components of the given <code>Window</code>.
     * @param window The <code>Window</code> for which the look and feel update has to be performed against.
     */
    public static void updateWindowUI(final Window window) {
        try {
            updateComponentTreeUI(window);
        } catch (Exception exception) {
            //empty
        }

        final Window windows[] = window.getOwnedWindows();

        for (Window window1 : windows) updateWindowUI(window1);
    }

    /**
     * A simple minded look and feel change: ask each node in the tree to <code>updateUI()</code> -- that is, to
     * initialize its UI property with the current look and feel.
     * <p/>
     * Based on the Sun SwingUtilities.updateComponentTreeUI, but ensures that the update happens on the components of a
     * JToolbar before the JToolbar itself.
     */
    public static void updateComponentTreeUI(final Component c) {
        updateComponentTreeUI0(c);
        c.invalidate();
        c.validate();
        c.repaint();
    }

    private static void updateComponentTreeUI0(final Component c) {

        Component[] children = null;

        if (c instanceof JToolBar) {
            children = ((JToolBar) c).getComponents();

            if (children != null) {
                final boolean opaque = LookAndFeels.getInstance().getSelectedLaF().isToolbarOpaque();
                for (Component aChildren : children) {
                    updateComponentTreeUI0(aChildren);
                    if (aChildren instanceof JComponent)
                        ((JComponent) aChildren).setOpaque(!opaque);
                }
            }

            ((JComponent) c).updateUI();
        } else {
            if (c instanceof JComponent) {
                ((JComponent) c).updateUI();
            }

            if (c instanceof JMenu) {
                children = ((JMenu) c).getMenuComponents();
            } else if (c instanceof Container) {
                children = ((Container) c).getComponents();
            }

            if (children != null) {
                for (Component aChildren : children) {
                    updateComponentTreeUI0(aChildren);
                }
            }
        }
    }

}
