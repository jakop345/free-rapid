package net.wordrider.dialogs.settings;


import net.wordrider.core.AppPrefs;
import net.wordrider.utilities.Swinger;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * @author Vity
 */
class CheckBoxOption implements IOptionable<JCheckBox>, ItemListener {
    private final boolean initValue;
    private boolean applyedValue;
    @SuppressWarnings({"FieldMayBeStatic"})
    private final boolean defaultValue = false;
    private boolean wasChanged = false;
    private final OptionsGroupManager optionsGroupManager;
    private IOptionGroup group = null;
    private String propertyName = null;
    private final JCheckBox check;

    public CheckBoxOption(final OptionsGroupManager optionsGroupManager, final String labelCode, final String propertyName, final boolean defaultValue, final IOptionGroup group) {
        this(optionsGroupManager, labelCode, AppPrefs.getProperty(propertyName, defaultValue), group);
        this.propertyName = propertyName;
    }

    public CheckBoxOption(final OptionsGroupManager optionsGroupManager, final String labelCode, final boolean initValue, final IOptionGroup group) {
        check = Swinger.getCheckBox(labelCode);
        this.initValue = applyedValue = initValue;
        this.group = group;
        this.check.setSelected(initValue);
        this.optionsGroupManager = optionsGroupManager;
//        this.optionsGroupManager.registerOptionable(this);
        this.check.addItemListener(this);
    }

    private void updateValue() {
        wasChanged = applyedValue != this.check.isSelected();
        optionsGroupManager.makeChange(this);
    }

    public void itemStateChanged(final ItemEvent e) {
        updateValue();
    }

    // --Commented out by Inspection START (26.2.05 18:26):
    //    public final void setDefaultValue(final boolean defaultValue) {
    //        this.defaultValue = defaultValue;
    //    }
    // --Commented out by Inspection STOP (26.2.05 18:26)

    public final void setDefault() {
        this.check.setSelected(this.defaultValue);
    }

    public final void restorePrevious() {
        this.check.setSelected(initValue);
    }

    public final boolean wasChanged() {
        return wasChanged;  //implement - call to super class
    }

    public void applyChange() {
        wasChanged = false;
        applyedValue = this.check.isSelected();
        if (propertyName != null)
            AppPrefs.storeProperty(propertyName, applyedValue);
    }


    public final IOptionGroup getOptionsGroup() {
        return group;  //implement - call to super class
    }


    public JCheckBox getComponent() {
        return check;
    }
}
