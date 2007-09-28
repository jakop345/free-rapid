package cz.cvut.felk.timejuggler.core.tasks;

import application.Application;
import application.ResourceMap;
import application.SingleFrameApplication;
import application.Task;
import org.jdesktop.swingx.JXFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Logger;

/**
 * @author Vity
 */
class ScreenInputBlocker extends Task.InputBlocker implements PropertyChangeListener {
    private final static Logger logger = Logger.getLogger(ScreenInputBlocker.class.getName());

    private JDialog modalDialog = null;
    private JOptionPane optionPane;

    public ScreenInputBlocker(Task task, Task.BlockingScope scope, Object target) {
        super(task, scope, target);
    }

    private void setActionTargetBlocked(boolean f) {
        javax.swing.Action action = (javax.swing.Action) getTarget();
        action.setEnabled(!f);
    }

    private void setComponentTargetBlocked(boolean f) {
        Component component = (Component) getTarget();
        component.setEnabled(!f);
    }

    private void setScreenBlocked(final boolean enabled) {
        final JXFrame frame = (JXFrame) ((SingleFrameApplication) Application.getInstance()).getMainFrame();
        frame.setWaiting(enabled);
    }

    /* Creates a dialog whose visuals are initialized from the
    * following task resources:
    * BlockingDialog.title
    * BlockingDialog.optionPane.icon
    * BlockingDialog.optionPane.message
    * BlockingDialog.cancelButton.text
    * BlockingDialog.cancelButton.icon
    */
    private JDialog createBlockingDialog() {
        optionPane = new JOptionPane();
        optionPane.setMessageType(JOptionPane.PLAIN_MESSAGE);
        final Task task = getTask();
        if (task.getUserCanCancel()) {
            JButton cancelButton = new JButton();
            cancelButton.setName("BlockingDialog.cancelButton");
            ActionListener doCancelTask = new ActionListener() {
                public void actionPerformed(ActionEvent ignore) {
                    task.cancel(true);
                }
            };
            cancelButton.addActionListener(doCancelTask);
            optionPane.setOptions(new Object[]{cancelButton});
            optionPane.setInitialSelectionValue(cancelButton);
        } else {
            optionPane.setOptions(new Object[]{});
        }
        optionPane.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        Component dialogOwner = (Component) getTarget();

        String taskTitle = task.getTitle();
        String dialogTitle = (taskTitle == null) ? "BlockingDialog" : taskTitle;
        JDialog dialog = optionPane.createDialog(dialogOwner, dialogTitle);
        dialog.setModal(true);
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        if (task.getUserCanCancel()) {
            dialog.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    task.cancel(true);
                }
            });
        }
        dialog.setName("BlockingDialog");
        optionPane.setName("BlockingDialog.optionPane");
        final ResourceMap resourceMap = ((CoreTask) task).getTaskResourceMap();
        if (resourceMap != null) {
            resourceMap.injectComponents(dialog);
        }
        dialog.pack();
        return dialog;
    }

    public void propertyChange(PropertyChangeEvent evt) {
        final String propertyName = evt.getPropertyName();
        if ("done".equals(propertyName)) {
            removeTaskListener();
        } else if ("message".equals(propertyName)) {
            optionPane.setMessage(evt.getNewValue());
        }
    }

    private void showBlockingDialog(boolean f) {
        if (f) {
            if (modalDialog != null) {
                String msg = String.format("unexpected InputBlocker state [%s] %s", f, this);
                logger.warning(msg);
                modalDialog.dispose();
            }
            modalDialog = createBlockingDialog();
            Runnable doShowDialog = new Runnable() {
                public void run() {
                    addTaskListener();
                    modalDialog.setVisible(true);
                }
            };
            EventQueue.invokeLater(doShowDialog);
        } else {
            if (modalDialog != null) {
                optionPane = null;
                modalDialog.dispose();
                modalDialog = null;
            } else {
                String msg = String.format("unexpected InputBlocker state [%s] %s", f, this);
                logger.warning(msg);
            }
        }
    }

    private void addTaskListener() {
        getTask().getContext().getTaskMonitor().addPropertyChangeListener(this);
    }

    private void removeTaskListener() {
        getTask().getContext().getTaskMonitor().removePropertyChangeListener(this);
    }


    @Override
    protected void block() {
        switch (getScope()) {
            case ACTION:
                setActionTargetBlocked(true);
                break;
            case COMPONENT:
                setComponentTargetBlocked(true);
                break;
            case WINDOW:
                setScreenBlocked(true);
                break;
            case APPLICATION:
                showBlockingDialog(true);
                break;
        }
    }

    @Override
    protected void unblock() {
        switch (getScope()) {
            case ACTION:
                setActionTargetBlocked(false);
                break;
            case COMPONENT:
                setComponentTargetBlocked(false);
                break;
            case WINDOW:
                setScreenBlocked(false);
                break;
            case APPLICATION:
                showBlockingDialog(false);
                break;
        }
    }
}
