package net.wordrider.dialogs.settings;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * @author Vity
 */
class SpinnerOption implements IOptionable<JSpinner>, ChangeListener {
    private final Object initValue;
    private Object applyedValue;
    private Object defaultValue = 10;
    private boolean wasChanged = false;
    private final OptionsGroupManager optionsGroupManager;
    private IOptionGroup group = null;
    private final JSpinner spinner;

    public SpinnerOption(final OptionsGroupManager optionsGroupManager, final SpinnerModel spinnerModel, final IOptionGroup group) {
        spinner = new JSpinner(spinnerModel);
        this.initValue = applyedValue = spinner.getValue();
        this.group = group;
        this.spinner.setValue(initValue);
        this.optionsGroupManager = optionsGroupManager;
//        this.optionsGroupManager.registerOptionable(this);
        this.spinner.addChangeListener(this);
    }

    private void updateValue() {
        wasChanged = !applyedValue.equals(this.spinner.getValue());
        optionsGroupManager.makeChange(this);
    }

    public final void stateChanged(final ChangeEvent e) {
        updateValue();
    }

    public final void setDefaultValue(final Object defaultValue) {
        this.defaultValue = defaultValue;
    }

    public final void setDefault() {
        this.spinner.setValue(this.defaultValue);
    }

    public final void restorePrevious() {
        this.spinner.setValue(initValue);
    }

    public final boolean wasChanged() {
        return wasChanged;  //implement - call to super class
    }

    public void applyChange() {
        wasChanged = false;
        applyedValue = this.spinner.getValue();
    }

    public JSpinner getComponent() {
        return spinner;
    }

// --Commented out by Inspection START (6.11.06 0:15):
//    public JSpinner getSpinner() {
//        return spinner;
//    }
// --Commented out by Inspection STOP (6.11.06 0:15)

    public final IOptionGroup getOptionsGroup() {
        return group;  //implement - call to super class
    }

}
