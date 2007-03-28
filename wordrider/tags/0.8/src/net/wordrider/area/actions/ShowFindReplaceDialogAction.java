package net.wordrider.area.actions;

import net.wordrider.core.MainApp;
import net.wordrider.core.managers.interfaces.IFileInstance;
import net.wordrider.dialogs.FindReplaceDialog;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

/**
 * @author Vity
 */
public final class ShowFindReplaceDialogAction extends TextAreaAction {
    private static final ShowFindReplaceDialogAction instance = new ShowFindReplaceDialogAction();
    private final static String CODE = "ShowFindReplaceDialogAction";


    public static ShowFindReplaceDialogAction getInstance() {
        return instance;
    }

    private ShowFindReplaceDialogAction() {
        super(CODE, KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_MASK), "find_replace.gif");
    }

    public final void actionPerformed(final ActionEvent e) {
        final IFileInstance instance = getAreaManager().getActiveInstance();
        if (instance != null) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    new FindReplaceDialog(MainApp.getInstance().getMainAppFrame(), (JEditorPane) instance.getRiderArea());
                }
            });
        }
    }
}
