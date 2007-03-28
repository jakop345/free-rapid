package net.wordrider.dialogs.settings;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * @author Vity
 */
class ComboBoxOption implements IOptionable<JComboBox>, ItemListener {
    private final Object initValue;
    private Object applyedValue;
    private final Object defaultValue = null;
    private boolean wasChanged = false;
    private final OptionsGroupManager optionsGroupManager;
    private IOptionGroup group = null;
    private JComboBox combo;

    public ComboBoxOption(final OptionsGroupManager optionsGroupManager, final ComboBoxModel comboBoxModel, final IOptionGroup group) {
        combo = new JComboBox(comboBoxModel);
        this.initValue = this.applyedValue = this.combo.getSelectedItem();
        this.group = group;
        this.combo.setSelectedItem(comboBoxModel.getSelectedItem());
        this.optionsGroupManager = optionsGroupManager;
//        this.optionsGroupManager.registerOptionable(this);
        this.combo.addItemListener(this);
    }

    private void updateValue() {
        wasChanged = !this.applyedValue.equals(this.combo.getSelectedItem());
        optionsGroupManager.makeChange(this);
    }

    public final void itemStateChanged(final ItemEvent e) {
        updateValue();
    }

    // --Commented out by Inspection START (26.2.05 18:27):
    //    public final void setDefaultValue(final Object defaultValue) {
    //        this.defaultValue = defaultValue;
    //    }
    // --Commented out by Inspection STOP (26.2.05 18:27)

    public final void setDefault() {
        if (this.defaultValue != null)
            this.combo.setSelectedItem(this.defaultValue);
    }

    public final void restorePrevious() {
        this.combo.setSelectedItem(initValue);
    }

    public final boolean wasChanged() {
        return wasChanged;  //implement - call to super class
    }

    public void applyChange() {
        wasChanged = false;
        applyedValue = this.combo.getSelectedItem();
    }

    public final IOptionGroup getOptionsGroup() {
        return group;  //implement - call to super class
    }


    public JComboBox getComponent() {
        return combo;
    }
}
