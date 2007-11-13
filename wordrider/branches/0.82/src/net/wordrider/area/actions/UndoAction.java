package net.wordrider.area.actions;

import net.wordrider.area.RiderArea;
import net.wordrider.utilities.LogUtils;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.logging.Logger;

/**
 * @author Vity
 */
public final class UndoAction extends TextAreaAction {
    private static final UndoAction instance = new UndoAction();
    private final static String CODE = "UndoAction";
    private final static Logger logger = Logger.getLogger(UndoAction.class.getName());
    public static UndoAction getInstance() {
        return instance;
    }

    private UndoAction() {
        super(CODE, KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_MASK), "undo.gif");
        setEnabled(false);
    }

    public final void actionPerformed(final ActionEvent e) {
        final RiderArea area = getRiderArea(e);
        if (area != null) {
            final UndoManager undoManager = area.getUndoManager();
            try {
                area.setBusy(true);                
                undoManager.undo();
            } catch (CannotUndoException ex) {
                //  System.out.println("Unable to undo: " + ex);
                LogUtils.processException(logger, ex);
            } finally{
                area.getCaret().setVisible(true);
                area.setBusy(false);
            }
            updateUndoState(area);
            RedoAction.getInstance().updateRedoState(area);
            StyledAreaAction.updateStatus();
        }
    }

    public final void updateUndoState(final JTextComponent component) {
        final RiderArea area = (RiderArea) component;
        if (area != null) {
            final UndoManager undoManager = area.getUndoManager();
            if (undoManager != null)
                setEnabled(undoManager.canUndo());
            //            if (undoManager.canUndo()) {
            //                setEnabled(true);
            //                putValue(Action.NAME, undoManager.getUndoPresentationName());
            //            } else {
            //                setEnabled(false);
            //                putValue(Action.NAME, "Undo");
            //            }
        } else {
            setEnabled(false);
            //putValue(Action.NAME, "Redo");
        }

    }
}
