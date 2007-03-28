package net.wordrider.dialogs;

import javax.swing.*;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Vity
 */
public final class JButtonGroup extends ButtonGroup {
    //    /**
    //     * Used to deselect all buttons in the group
    //     */
    //    private static final AbstractButton dummyButton = new JButton();
    /**
     * Stores a reference to the currently selected button in the group
     */
    private AbstractButton selectedButton;

    /**
     * Creates an empty <code>JButtonGroup</code>
     */
    public JButtonGroup() {
        super();
        //add(dummyButton);
    }

    // --Commented out by Inspection START (26.2.05 17:39):
    //    /**
    //     * Creates a <code>JButtonGroup</code> object from an array of buttons and adds the buttons to the group
    //     * No button will be selected initially.
    //     *
    //     * @param buttons an array of <code>AbstractButton</code>s
    //     */
    //    public JButtonGroup(final AbstractButton[] buttons) {
    //        add(buttons);
    //        //add(dummyButton);
    //    }
    // --Commented out by Inspection STOP (26.2.05 17:39)

    /**
     * Adds a button to the group
     * @param button an <code>AbstractButton</code> reference
     */
    public void add(final AbstractButton button) {
        if (button == null || buttons.contains(button)) {
            return;
        }
        super.add(button);
        if (getSelection() != null && getSelection().equals(button.getModel())) {
            selectedButton = button;
        }
    }

    /**
     * Adds an array of buttons to the group
     * @param buttons an array of <code>AbstractButton</code>s
     */
    private void add(final AbstractButton[] buttons) {
        if (buttons == null) {
            return;
        }
        for (AbstractButton button : buttons) {
            add(buttons);
        }
    }

    /**
     * Removes a button from the group
     * @param button the button to be removed
     */
    public void remove(final AbstractButton button) {
        if (button != null) {
            if (selectedButton.equals(button)) {
                selectedButton = null;
            }
            super.remove(button);
        }
    }

    /**
     * Removes all the buttons in the array from the group
     * @param buttons an array of <code>AbstractButton</code>s
     */
    private void remove(final AbstractButton[] buttons) {
        if (buttons == null) {
            return;
        }
        for (AbstractButton button : buttons) {
            remove(buttons);
        }
    }

    /**
     * Sets the selected button in the group Only one button in the group can be selected
     * @param button   an <code>AbstractButton</code> reference
     * @param selected an <code>boolean</code> representing the selection state of the button
     */
    public void setSelected(final AbstractButton button, final boolean selected) {
        if (button != null && buttons.contains(button)) {
            setSelected(button.getModel(), selected);
            if (getSelection().equals(button.getModel())) {
                selectedButton = button;
            }
        }
    }

    /**
     * Sets the selected button model in the group
     * @param model    a <code>ButtonModel</code> reference
     * @param selected an <code>boolean</code> representing the selection state of the button
     */
    public void setSelected(final ButtonModel model, final boolean selected) {
        final AbstractButton button = getButton(model);
        if (buttons.contains(button)) {
            super.setSelected(model, selected);
            selectedButton = button;
            //            if (model != dummyButton.getModel()) {
            //
            //            }
        }
    }

    /**
     * Returns the <code>AbstractButton</code> whose <code>ButtonModel</code> is given. If the model does not belong to
     * a button in the group, returns null.
     * @param model a <code>ButtonModel</code> that should belong to a button in the group
     * @return an <code>AbstractButton</code> reference whose model is <code>model</code> if the button belongs to the
     *         group, <code>null</code>otherwise
     */
    private AbstractButton getButton(final ButtonModel model) {
        for (final AbstractButton ab : buttons) {
            if (ab.getModel().equals(model)) {
                return ab;
            }
        }
        return null;
    }

    /**
     * Returns the selected button in the group.
     * @return a reference to the currently selected button in the group or <code>null</code> if no button is selected
     */
    public AbstractButton getSelected() {
        return selectedButton;

        //        if (selectedButton == dummyButton) {
        //            return null;
        //        } else {
        //            return selectedButton;
        //        }
    }

    //    public ButtonModel getSelection() {
    //        if (selectedButton == dummyButton) {
    //            return null;
    //        } else {
    //            return super.getSelection();
    //        }
    //    }

    /**
     * Returns whether the button is selected
     * @param button an <code>AbstractButton</code> reference
     * @return <code>true</code> if the button is selected, <code>false</code> otherwise
     */
    public boolean isSelected(final AbstractButton button) {
        //        if (button == dummyButton) {
        //            return false;
        //        }
        return button.equals(selectedButton);
    }

    /**
     * Returns the buttons in the group as a <code>List</code>
     * @return a <code>List</code> containing the buttons in the group, in the order they were added to the group
     */
    public List<AbstractButton> getButtons() {
        final List<AbstractButton> allButtons = new LinkedList<AbstractButton>(buttons);
        //allButtons.remove(dummyButton);
        return Collections.unmodifiableList(allButtons);
    }

    /**
     * Checks whether the group contains the given button
     * @return <code>true</code> if the button is contained in the group, <code>false</code> otherwise
     */
    public boolean contains(final AbstractButton button) {
        //        if (button == dummyButton) {
        //            return false;
        //        }
        return buttons.contains(button);
    }

    //    /**
    //     * unselects all buttons
    //     */
    //    public void unselectAll() {
    //        setSelected(dummyButton, true);
    //    }
}
