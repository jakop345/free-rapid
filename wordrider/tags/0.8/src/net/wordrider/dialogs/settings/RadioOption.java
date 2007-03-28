package net.wordrider.dialogs.settings;

import net.wordrider.core.AppPrefs;
import net.wordrider.utilities.Swinger;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * @author Vity
 */
class RadioOption implements IOptionable<JRadioButton>, ItemListener {
    private final int itemValue;
    private boolean applyedValue;
    private boolean wasChanged = false;
    private final OptionsGroupManager optionsGroupManager;
    private IOptionGroup group = null;
    private String propertyName = null;
    private final JRadioButton radio;

    public RadioOption(final OptionsGroupManager optionsGroupManager, final String labelCode, final String propertyName, final int itemValue, final int defaultValue, final IOptionGroup group) {
        this.propertyName = propertyName;
        radio = Swinger.getRadio(labelCode);
        this.itemValue = itemValue;
        this.group = group;
        this.applyedValue = itemValue == AppPrefs.getProperty(propertyName, defaultValue);

        this.radio.setSelected(this.applyedValue);
        this.optionsGroupManager = optionsGroupManager;
        this.radio.addItemListener(this);
        updateValue();
    }

    void updateValue() {
        wasChanged = applyedValue != this.radio.isSelected();
        optionsGroupManager.makeChange(this);
    }

    public void itemStateChanged(final ItemEvent e) {
        updateValue();
    }

    public final void setDefault() {
        this.radio.setSelected(this.applyedValue);
    }

    public final void restorePrevious() {
        if (applyedValue != this.radio.isSelected())
            this.radio.setSelected(this.applyedValue);
    }

    public final boolean wasChanged() {
        return wasChanged;  //implement - call to super class
    }

    public void applyChange() {
        wasChanged = false;
        if (this.radio.isSelected())
            AppPrefs.storeProperty(propertyName, itemValue);
    }


    public final IOptionGroup getOptionsGroup() {
        return group;  //implement - call to super class
    }


    public JRadioButton getComponent() {
        return radio;
    }
}
