package net.wordrider.dialogs.settings;

import javax.swing.*;

/**
 * @author Vity
 */
interface IOptionable<C extends JComponent> {

    // --Commented out by Inspection (6.11.06 0:15):public C getComponent();

    public void setDefault();

    public void restorePrevious();

    public boolean wasChanged();

    public void applyChange();

    public IOptionGroup getOptionsGroup();
}
