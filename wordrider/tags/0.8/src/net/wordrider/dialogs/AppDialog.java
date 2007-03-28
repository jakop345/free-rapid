package net.wordrider.dialogs;

//import net.wordrider.utilities.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

/**
 * @author Vity
 */
public abstract class AppDialog extends JDialog {
    public final static int RESULT_OK = 0;
    final static int RESULT_CANCEL = 1;
    int result = RESULT_CANCEL;

//    private final boolean closeOnCancel = true;

    public AppDialog(final Frame owner, final boolean modal) throws HeadlessException {
        super(owner, modal);
    }

    public final void doClose() {
        dispose();
    }

    // --Commented out by Inspection START (4.2.05 16:19):
    //    protected void setCloseOnCancel(final boolean closeOnCancel) {
    //        this.closeOnCancel = closeOnCancel;
    //    }
    // --Commented out by Inspection STOP (4.2.05 16:19)

    public final int getModalResult() {
        return result;
    }

    protected AbstractButton getCancelButton() {
        return null;
    }

    protected AbstractButton getOkButton() {
        return null;
    }

    protected final JRootPane createRootPane() {
        final ActionListener escapeActionListener = new ActionListener() {
            public void actionPerformed(final ActionEvent actionEvent) {
                //     if (closeOnCancel) {
                final AbstractButton button = getCancelButton();
                if (button != null) {
                    doButtonAction(button, actionEvent);
                }
                //doCancelButtonAction();
                //   }
            }
        };
        final ActionListener okButtonListener = new ActionListener() {

            public void actionPerformed(final ActionEvent actionEvent) {
                if (AppDialog.this.getFocusOwner() instanceof AbstractButton) {
                    final AbstractButton button = ((AbstractButton) AppDialog.this.getFocusOwner());
                    if (button instanceof JToggleButton) {
                        final JToggleButton toggleButton = (JToggleButton) button;
                        if (!toggleButton.isSelected()) {
                            doButtonAction(button, actionEvent);
                            return;
                        }
                    } else {
                        doButtonAction(button, actionEvent);
                        return;
                    }
                }
                final AbstractButton button = getOkButton();
                if (button != null) {
                    actionEvent.setSource(button);
                    doButtonAction(button, actionEvent);
                }
            }
        };
        final JRootPane rootPane = new JRootPane();
        KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        rootPane.registerKeyboardAction(escapeActionListener, stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
        stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
        rootPane.registerKeyboardAction(okButtonListener, stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
        return rootPane;
    }

    private static void doButtonAction(final AbstractButton button, final ActionEvent actionEvent) {
        button.doClick();
        final Action action = button.getAction();
        if (action != null)
            action.actionPerformed(actionEvent);
        //     else logger.severe("Button has no action!" + button.toString());
    }


    void setResult(int result) {
        this.result = result;
    }
}
