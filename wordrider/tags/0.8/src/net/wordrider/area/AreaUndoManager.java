package net.wordrider.area;

import net.wordrider.area.actions.RedoAction;
import net.wordrider.area.actions.UndoAction;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.UndoableEditEvent;
import javax.swing.text.AbstractDocument;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.undo.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @author Vity
 */
final class AreaUndoManager extends UndoManager implements PropertyChangeListener {
    private final RiderArea editor;
    private CompoundEdit compoundEdit;
    private int lastOffset;
    private boolean multipleUndo = false;
    private boolean multipleUndoFirstTime = false;


    public AreaUndoManager(final RiderArea editor) {
        this.editor = editor;        
        editor.getDocument().addUndoableEditListener(this);
        editor.addPropertyChangeListener("document", this);
        editor.addPropertyChangeListener("undoredo", this);
    }

    /*
    **  Listen for Undoable Edits
    */
    //    public final void xxxundoableEditHappened(final UndoableEditEvent e) {
    //        addEdit(e.getEdit());
    //        getUndoAction().updateUndoState();
    //        getRedoAction().updateRedoState();
    //    }

    public final boolean addEdit(final UndoableEdit edit) {
        final boolean result = super.addEdit(edit);

        updateStatuses();
        return result;
    }

    private void updateStatuses() {
        UndoAction.getInstance().updateUndoState(editor);
        editor.setModified(this.canUndoOrRedo());
        RedoAction.getInstance().updateRedoState(editor);
    }


    /*
**  Whenever an UndoableEdit happens the edit will either be absorbed
**  by the current compound edit or a new compound edit will be started
*/
    public final void undoableEditHappened(final UndoableEditEvent e) {
        // Start a new compound edit
//        if (multipleUndoFirstTime) {
//            initMultipleUndoRedo(e);
//            return;
//        } else if (compoundEdit == null) {
//            compoundEdit = startCompoundEdit(e.getEdit());
//            return;
//        }
        if (multipleUndoFirstTime || compoundEdit == null) {
            initMultipleUndoRedo(e);
            return;
        }
        //  Check for an incremental edit or backspace

        final int diff = editor.getCaretPosition() - lastOffset;
        if (Math.abs(diff) == 1 || multipleUndo) {
            compoundEdit.addEdit(e.getEdit());
            updateStatuses(); //problem paste when openened empty
            lastOffset += diff;
            return;
        }

        //  Not incremental edit, end previous edit and start a new one

        compoundEdit.end();
        compoundEdit = startCompoundEdit(e.getEdit());
    }

    private void initMultipleUndoRedo(UndoableEditEvent e) {
        if (compoundEdit != null) compoundEdit.end();
        compoundEdit = startCompoundEdit(e == null ? null : e.getEdit());
        multipleUndoFirstTime = false;
    }

    /*
    **  Each CompoundEdit will store a group of related incremental edit
    **  (ie. each character typed or backspaced is an incremental edit)
    */
    private CompoundEdit startCompoundEdit(final UndoableEdit anEdit) {
        //  Track the starting offset of this compound edit

        lastOffset = editor.getCaretPosition();

        //  The compound edit is used to store incremental edits

        if (anEdit != null) {
            final AbstractDocument.DefaultDocumentEvent event = (AbstractDocument.DefaultDocumentEvent) anEdit;
            final boolean accurate = (event.getType() == DocumentEvent.EventType.CHANGE);
            compoundEdit = new MyCompoundEdit(editor, accurate);
            compoundEdit.addEdit(anEdit);
        } else {
            compoundEdit = new MyCompoundEdit(editor, true);
        }

        //  The compound edit is added to the UndoManager. All incremental
        //  edits stored in the compound edit will be undone/redone at once

        addEdit(compoundEdit);
        return compoundEdit;
    }

    public synchronized void discardAllEdits() {
        super.discardAllEdits();    //call to super
        UndoAction.getInstance().updateUndoState(editor);
        RedoAction.getInstance().updateRedoState(editor);
    }

    private final class MyCompoundEdit extends CompoundEdit {
        private final int realSelStart;
        private final int realSelEnd;
        private final boolean accurate;

        public MyCompoundEdit(RiderArea editor, final boolean accurate) {
            super();    //call to super
            this.realSelStart = editor.getCaret().getMark();
            this.realSelEnd = editor.getCaret().getDot();
            this.accurate = accurate;
        }

        public final boolean isInProgress() {
            //  in order for the canUndo() and canRedo() methods to work
            //  assume that the compound edit is never in progress

            return false;
        }

        public final void undo() throws CannotUndoException {
            //  End the edit so future edits don't get absorbed by this edit

            if (compoundEdit != null)
                compoundEdit.end();
            editor.putClientProperty("makingundo", Boolean.TRUE);
            super.undo();
            editor.putClientProperty("makingundo", null);
            updateEditor();
            //  Always start a new compound edit after an undo

            compoundEdit = null;
        }

        private void updateEditor() {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    if (realSelStart == realSelEnd)
                        editor.setCaretPosition(Math.max(Math.min(realSelStart + ((accurate) ? 0 : -1), editor.getDocument().getLength()), 0));
                    else
                        editor.selectReverse(realSelStart, realSelEnd);
                   // editor.revalidate();
                    editor.validate();
                    editor.repaint();
                }
            });
        }

        public void redo() throws CannotRedoException {
            super.redo();    //call to super
            updateEditor();
        }

    }


    /**
     * This method gets called when a bound property is changed.
     * @param event A PropertyChangeEvent object describing the event source and the property that has changed.
     */
    public final void propertyChange(final PropertyChangeEvent event) {
        if (event.getSource().equals(getEditor())) {
            if (event.getPropertyName().equals("document")) {

                // Reset the UndoableEditListener
                ((Document) event.getOldValue()).removeUndoableEditListener(this);
                final Document newDoc = (Document) event.getNewValue();
                newDoc.addUndoableEditListener(this);
                this.discardAllEdits();
            } else if (event.getPropertyName().equals("undoredo")) {
                this.multipleUndo = this.multipleUndoFirstTime = (Boolean) event.getNewValue();
                if (this.multipleUndoFirstTime) {
                    initMultipleUndoRedo(null);
                }
            }
        }
    }

    public void freeUpResources() {
        editor.getDocument().removeUndoableEditListener(this);
        editor.removePropertyChangeListener("document", this);
        editor.removePropertyChangeListener("undoredo", this);
        //editor = null;
    }


    private JTextComponent getEditor() {
        return editor;
    }

}
