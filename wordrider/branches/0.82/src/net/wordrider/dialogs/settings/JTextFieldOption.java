package net.wordrider.dialogs.settings;

import net.wordrider.core.AppPrefs;
import net.wordrider.utilities.Swinger;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;


/**
 * @author Vity
 */
class JTextFieldOption implements IOptionable<JTextField>, DocumentListener {
    private final String initValue;
    @SuppressWarnings({"fieldMayBeStatic"})
    private String defaultValue;
    private boolean wasChanged = false;
    private final OptionsGroupManager optionsGroupManager;
    private IOptionGroup group = null;
    private String propertyName = null;
    private JTextField field;


    public JTextFieldOption(final OptionsGroupManager optionsGroupManager, Document document, final String propertyName, final String defaultValue, final IOptionGroup group) {
        this(optionsGroupManager, document, AppPrefs.getProperty(propertyName, defaultValue), group);
        this.propertyName = propertyName;
        this.defaultValue = defaultValue;
    }

    private JTextFieldOption(final OptionsGroupManager optionsGroupManager, Document document, final String initValue, final IOptionGroup group) {
        field = new JTextField(document = (document == null ? new PlainDocument() : document), initValue, 0);
        this.initValue = initValue;
        this.group = group;
        this.optionsGroupManager = optionsGroupManager;
        document.addDocumentListener(this);
        Swinger.addKeyActions(this.field);
    }

    private void updateValue() {
        wasChanged = !(initValue.equals(this.field.getText()));
        optionsGroupManager.makeChange(this);
    }

    public void insertUpdate(DocumentEvent e) {
        updateValue();
    }

    public void removeUpdate(DocumentEvent e) {
        updateValue();
    }

    public void changedUpdate(DocumentEvent e) {
        updateValue();
    }


    public final void setDefault() {
        this.field.setText(this.defaultValue);
    }

    public final void restorePrevious() {
        this.field.setText(initValue);
    }

    public final boolean wasChanged() {
        return wasChanged;  //implement - call to super class
    }

    public void applyChange() {
        wasChanged = false;
        //applyedValue = this.getText();
        if (propertyName != null)
            AppPrefs.storeProperty(propertyName, this.field.getText());
    }


    public final IOptionGroup getOptionsGroup() {
        return group;  //implement - call to super class
    }

    public JTextField getComponent() {
        return field;
    }
}
