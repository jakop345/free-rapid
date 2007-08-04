package cz.cvut.felk.timejuggler.gui.dialogs;

import application.Application;
import application.ApplicationContext;
import application.ResourceMap;
import cz.cvut.felk.timejuggler.swing.NaiiveComboModel;
import cz.cvut.felk.timejuggler.swing.Swinger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * @author Vity
 */
abstract class AppDialog extends JDialog {
    public final static int RESULT_OK = 0;
    final static int RESULT_CANCEL = 1;
    int result = RESULT_CANCEL;

//    private final boolean closeOnCancel = true;

    public AppDialog(final Frame owner, final boolean modal) throws HeadlessException {
        super(owner, modal);
    }

    public void doClose() {
        dispose();
    }

    public final int getModalResult() {
        return result;
    }

    protected AbstractButton getBtnCancel() {
        return null;
    }

    protected AbstractButton getBtnOK() {
        return null;
    }

    protected void inject() {
        Application application = Application.getInstance(Application.class);
        ApplicationContext context = application.getContext();
        context.getResourceMap(getClass()).injectComponents(this);
//        ActionMap actionMap = context.getActionMap(this.getClass(),
//                this);
//        ApplicationAction action = (ApplicationAction) actionMap.get("ok");
////        ActionMap formMap = context.getActionMap(form.getClass(),
////                form);
////        javax.swing.Action delegate = formMap.get("apply");
////        action.setProxy(delegate);
//
//        okayButton.setAction(action);
//        cancelButton.setAction(actionMap.get("cancel"));
    }


    protected final JRootPane createRootPane() {


        final ActionListener escapeActionListener = new ActionListener() {
            public void actionPerformed(final ActionEvent actionEvent) {
                //     if (closeOnCancel) {
                doCancel(actionEvent);
                //doCancelButtonAction();
                //   }
            }
        };
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                windowIsClosing();
            }
        });

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
                final AbstractButton button = getBtnOK();
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
        stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.CTRL_MASK);
        rootPane.registerKeyboardAction(okButtonListener, stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
        return rootPane;
    }

    protected void windowIsClosing() {

    }

    private void doCancel(ActionEvent actionEvent) {
        final AbstractButton button = getBtnCancel();
        if (button != null) {
            doButtonAction(button, actionEvent);
        }
    }

    private static void doButtonAction(final AbstractButton button, final ActionEvent actionEvent) {
        button.doClick();
        final Action action = button.getAction();
        if (action != null && action.isEnabled())
            action.actionPerformed(actionEvent);
    }


    void setResult(int result) {
        this.result = result;
    }

    protected ResourceMap getResourceMap() {
        return Swinger.getResourceMap(this.getClass());
    }

    /**
     * Locates the given component on the screen's center.
     * @param component the component to be centered
     */
    static void locateOnOpticalScreenCenter(Component component) {
        Dimension paneSize = component.getSize();
        Dimension screenSize = component.getToolkit().getScreenSize();
        component.setLocation(
                (screenSize.width - paneSize.width) / 2,
                (int) ((screenSize.height - paneSize.height) * 0.45));
    }

    protected String[] getList(String key) {
        return (String[]) getResourceMap().getObject(key + "_list", String[].class);
    }

    protected void setComboModelFromResource(JComboBox comboBox) {
        final String name = comboBox.getName();
        assert name != null && name.length() > 0;
        comboBox.setModel(new NaiiveComboModel(getList(name)));
    }

}