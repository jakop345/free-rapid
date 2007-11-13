package net.wordrider.area.actions;

import net.wordrider.area.RiderArea;
import net.wordrider.core.Lng;
import net.wordrider.core.MainApp;
import net.wordrider.utilities.LogUtils;
import net.wordrider.utilities.Swinger;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * @author Vity
 */
public final class InsertFromClipboardAction extends TextAreaAction {
    private static final InsertFromClipboardAction instance = new InsertFromClipboardAction();
    private static final String CODE = "InsertFromClipboardAction";
    private final static Logger logger = Logger.getLogger(InsertFromClipboardAction.class.getName());

    public static InsertFromClipboardAction getInstance() {
        return instance;
    }

    private InsertFromClipboardAction() {
        super(CODE, KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK), "image_btm.gif");
    }

    public final void actionPerformed(final ActionEvent e) {
        final RiderArea area = getRiderArea(e);
        if (area != null) {
            Transferable content;
            try {
                content = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(this);
            } catch (IllegalStateException e1) {
                Swinger.showErrorDialog(MainApp.getInstance().getMainAppFrame(), Lng.getLabel("message.error.clipboard"));
                return;
            }
            if (isFlavourSupported(content)) {
                Object inputImage = null;
                try {
                    inputImage = content.getTransferData(DataFlavor.imageFlavor);
                } catch (UnsupportedFlavorException ex1) {
                    LogUtils.processException(logger, ex1);
                } catch (IOException ex2) {
                    LogUtils.processException(logger, ex2);
                }
                if (inputImage instanceof Image)
                    InsertPictureAction.insertImageFromFilter(MainApp.getInstance().getMainAppFrame(), area, null, (Image) inputImage);
            }
        }
    }

    private static boolean isFlavourSupported(final Transferable clipboardContent) {
        return clipboardContent.isDataFlavorSupported(DataFlavor.imageFlavor);
    }

    public boolean isFlavourSupported() {
        return isFlavourSupported(Toolkit.getDefaultToolkit().getSystemClipboard().getContents(this));
    }
}