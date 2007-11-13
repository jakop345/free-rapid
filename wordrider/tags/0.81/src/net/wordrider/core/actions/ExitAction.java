package net.wordrider.core.actions;

import net.wordrider.area.ColorStyles;
import net.wordrider.core.AppPrefs;
import net.wordrider.core.managers.AreaManager;
import net.wordrider.core.managers.PluginToolsManager;
import net.wordrider.utilities.LogUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.logging.Logger;

/**
 * @author Vity
 */
public final class ExitAction extends CoreAction {
    private static final ExitAction INSTANCE = new ExitAction();
    private static final String CODE = "ExitAction";
    private final static Logger logger = Logger.getLogger(ExitAction.class.getName());

    private ExitAction() {
        super(CODE, KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.ALT_MASK), "exit.gif");
    }

    public static ExitAction getInstance() {
        return INSTANCE;
    }

    private static void storeWindowPosition() {
        if (AppPrefs.getProperty(AppPrefs.WINDOWSPOSITION, false)) {
            final JFrame mainFrame = getMainFrame();
            AppPrefs.storeProperty(AppPrefs.WINDOWSPOSITION_MAXIMIZED, mainFrame.getExtendedState() == JFrame.MAXIMIZED_BOTH);
            AppPrefs.storeProperty(AppPrefs.WINDOWSPOSITION_HEIGHT, mainFrame.getHeight());
            AppPrefs.storeProperty(AppPrefs.WINDOWSPOSITION_WIDTH, mainFrame.getWidth());
            AppPrefs.storeProperty(AppPrefs.WINDOWSPOSITION_X, mainFrame.getX());
            AppPrefs.storeProperty(AppPrefs.WINDOWSPOSITION_Y, mainFrame.getY());
        } else {
            AppPrefs.removeProperty(AppPrefs.WINDOWSPOSITION_HEIGHT);
            AppPrefs.removeProperty(AppPrefs.WINDOWSPOSITION_WIDTH);
            AppPrefs.removeProperty(AppPrefs.WINDOWSPOSITION_X);
            AppPrefs.removeProperty(AppPrefs.WINDOWSPOSITION_MAXIMIZED);
            AppPrefs.removeProperty(AppPrefs.WINDOWSPOSITION_Y);
        }
    }

    @SuppressWarnings({"finally"})
    public final void actionPerformed(final ActionEvent e) {
        if (CloseAllAction.closeAll()) {
            try {
                AreaManager.getInstance().getRecentFilesManager().storeRecentFiles();
                PluginToolsManager.getInstance().closeSoftAllInstances(false);
                storeWindowPosition();
                ColorStyles.storeColors();
                AppPrefs.store();
            } catch (Exception ex) {
                LogUtils.processException(logger, ex);
            }
            System.exit(0);
        }
    }
}
