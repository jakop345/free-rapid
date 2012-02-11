package cz.vity.freerapid.gui.actions;

import cz.vity.freerapid.core.AppPrefs;
import cz.vity.freerapid.core.Consts;
import cz.vity.freerapid.core.MainApp;
import cz.vity.freerapid.core.UserProp;
import cz.vity.freerapid.core.tasks.CheckForNewVersionTask;
import cz.vity.freerapid.gui.dialogs.AboutDialog;
import cz.vity.freerapid.gui.managers.UpdateManager;
import cz.vity.freerapid.utilities.Browser;
import org.jdesktop.application.Action;

import java.awt.event.ActionEvent;

/**
 * @author Vity
 */

public class PluginsActions {

    private MainApp app;

    public PluginsActions() {
        app = MainApp.getInstance(MainApp.class);
    }

    @Action
    public void checkPluginStatuses() {
        Browser.openBrowser(AppPrefs.getProperty(UserProp.PLUGINSSTATUS_URL, Consts.PLUGINSSTATUS_URL));
    }

    @Action
    public void checkForNewPlugins() {
        final UpdateManager updateManager = app.getManagerDirector().getUpdateManager();
        updateManager.checkUpdate(false);
    }
}
