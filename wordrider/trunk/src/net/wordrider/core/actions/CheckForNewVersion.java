package net.wordrider.core.actions;

import net.wordrider.core.Lng;
import net.wordrider.dialogs.AppDialog;
import net.wordrider.dialogs.ConnectDialog;
import net.wordrider.utilities.BrowserControl;
import net.wordrider.utilities.Swinger;

import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * @author Vity
 */
public final class CheckForNewVersion extends CoreAction {
    private final static CheckForNewVersion INSTANCE = new CheckForNewVersion();
    private final static String CODE = "CheckForNewVersion";

    public static CheckForNewVersion getInstance() {
        return INSTANCE;
    }

    private CheckForNewVersion() {
        super(CODE, null, null);    //call to super
    }

    public static void check(final boolean showInfoMessages) {
        final Frame mainFrame = getMainFrame();
        final CheckVersion worker;

        worker = new CheckVersion(showInfoMessages);
        worker.init();

        //loadingDialog.setVisible(true);
        switch ((Integer) worker.get()) {
            case CheckVersion.CONNECT_NEW_VERSION:
                final int result = Swinger.getChoice(mainFrame, Lng.getLabel("message.connect.newVersion"));
                if (result == Swinger.RESULT_YES)
                    BrowserControl.showHomepage();
                break;
            case CheckVersion.CONNECT_ERROR_EXCEPTION:
                if (showInfoMessages)
                    Swinger.showErrorDialog(mainFrame, Lng.getLabel("message.connect.exception", worker.getErrorMessage()));
                break;
            case CheckVersion.CONNECT_ERROR_INETCONNECTION_NOTAVAILABLE:
                if (showInfoMessages)
                    Swinger.showErrorDialog(mainFrame, Lng.getLabel("message.connect.noInetAvailable"));
                break;
            case CheckVersion.CONNECT_SAME_VERSION:
            default:
                if (showInfoMessages)
                    Swinger.showInformationDialog(mainFrame, Lng.getLabel("message.connect.sameVersion"));
                break;
        }

        //Swinger.showInformationDialog(mainFrame, AppPrefs.getLabel("message.information.savedSuccess", f.getAbsolutePath()));

    }

    public final void actionPerformed(final ActionEvent e) {
        final Frame mainFrame = getMainFrame();
        final int modalResult = new ConnectDialog(mainFrame).getModalResult();
        if (modalResult == AppDialog.RESULT_OK)
            check(true);
    }
}
