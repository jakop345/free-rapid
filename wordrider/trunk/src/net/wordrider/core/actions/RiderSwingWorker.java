package net.wordrider.core.actions;

import net.wordrider.core.Lng;
import net.wordrider.core.MainApp;
import net.wordrider.core.MainAppFrame;
import net.wordrider.dialogs.LoadingDialog;
import net.wordrider.utilities.SwingWorker;
import net.wordrider.utilities.LogUtils;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Logger;
import java.awt.*;

/**
 * @author Vity
 */
public abstract class RiderSwingWorker extends SwingWorker {
    private final static Logger logger = Logger.getLogger(RiderSwingWorker.class.getName());
    protected LoadingDialog dialogToClose = null;
    private final boolean showLoading;
    String errorMessage = "";
    private MainAppFrame frame;

    public RiderSwingWorker(final boolean useLoadingDialog) {
        super();
        frame = MainApp.getInstance().getMainAppFrame();
        if (useLoadingDialog) {
            this.dialogToClose = new LoadingDialog(frame);
        }
        this.showLoading = useLoadingDialog;
    }

    public final void init() {
        start();
        if (showLoading) {
            dialogToClose.setVisible(true);
        }
    }

    public void finished() {
        super.finished();
        if (showLoading)
            dialogToClose.dispose();
    }

    protected final void showInfoWhileLoading(final String code) {
        if (showLoading)
            dialogToClose.setStatusText(Lng.getLabel(code));
    }

    public final String getErrorMessage() {
        return errorMessage;
    }


    protected void setWaitCursor(final boolean waitCursor) {
        invokeAndWait(new Runnable() {
            public void run() {
                if (frame != null) {
                   Cursor cursor = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
                   if (!waitCursor) cursor = null;
                   frame.setCursor(cursor);
                   //if (glassPane != null) glassPane.setVisible(waitCursor);
                }
            }
        });
    }

    private void invokeAndWait(final Runnable runnable) {
        try {
            SwingUtilities.invokeAndWait(runnable);
        } catch (InvocationTargetException e) {
            LogUtils.processException(logger, e);
        } catch (InterruptedException e) {
            LogUtils.processException(logger, e);
        }
    }
}
