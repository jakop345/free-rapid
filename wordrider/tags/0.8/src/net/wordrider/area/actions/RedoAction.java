package net.wordrider.area.actions;

import net.wordrider.area.RiderArea;
import net.wordrider.utilities.LogUtils;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.UndoManager;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.logging.Logger;

/**
 * @author Vity
 */
public final class RedoAction extends TextAreaAction {
    private static final RedoAction instance = new RedoAction();
    private static final String CODE = "RedoAction";
    private final static Logger logger = Logger.getLogger(RedoAction.class.getName());
    public static RedoAction getInstance() {
        return instance;
    }

    private RedoAction() {
        super(CODE, KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK), "redo.gif");
        setEnabled(false);
    }

    public final void actionPerformed(final ActionEvent e) {
        final RiderArea area = (RiderArea) getTextComponent(e);
        if (area != null) {
            try {
                area.setBusy(true);                
                area.getUndoManager().redo();
            } catch (CannotRedoException ex) {
                LogUtils.processException(logger, ex);
            } finally{
                area.setBusy(false);
            }
            updateRedoState(area);
            UndoAction.getInstance().updateUndoState(area);
        }
    }

    public final void updateRedoState(final JTextComponent component) {
        final RiderArea area = (RiderArea) component;
        if (area != null) {
            final UndoManager undoManager = area.getUndoManager();
            if (undoManager != null)
                setEnabled(undoManager.canRedo());
            //            if (undoManager.canRedo()) {
            //                setEnabled(true);
            //                putValue(Action.NAME, undoManager.getRedoPresentationName());
            //            } else {
            //                setEnabled(false);
            //                putValue(Action.NAME, "Redo");
            //            }
        } else {
            setEnabled(false);
            //            putValue(Action.NAME, "Redo");
        }
    }
}

