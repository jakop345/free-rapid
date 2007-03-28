package net.wordrider.core.swing;

import net.wordrider.utilities.Consts;

import javax.swing.*;
import java.util.Collection;
import java.util.Stack;

/**
 * @author Vity
 */
public final class RecentsComboModel extends DefaultComboBoxModel {

    private final Stack stack;

    public RecentsComboModel(final Stack v) {
        super(v);    //call to super
        this.stack = v;
    }

    public final void addElement(final Object anObject) {
        if (!anObject.equals("") && getIndexOf(anObject) < 0) {
            super.insertElementAt(anObject, 0);
            if (stack.size() > Consts.MAX_RECENT_PHRASES_COUNT) {
                this.remove(Consts.MAX_RECENT_PHRASES_COUNT - 1);
            }
        }
    }


    private void remove(int index) {
        setSelectedItem(getElementAt(0));
        stack.removeElementAt(index);
        fireIntervalRemoved(this, index, index);
    }

    public final Collection getList() {
        return stack;
    }
}
