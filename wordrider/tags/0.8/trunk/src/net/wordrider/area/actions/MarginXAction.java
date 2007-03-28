package net.wordrider.area.actions;


import net.wordrider.area.RiderStyles;
import net.wordrider.core.MainApp;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

/**
 * @author Vity
 */
public final class MarginXAction extends ChangeParagraphStyleAction {
    private static final MarginXAction instance = new MarginXAction();
    private static final String CODE = "MarginXAction";

    public static MarginXAction getInstance() {
        return instance;
    }

    private MarginXAction() {
        super(CODE, KeyStroke.getKeyStroke(KeyEvent.VK_4, InputEvent.ALT_MASK), "marg_x.gif");    //call to super
    }


    public final void actionPerformed(final ActionEvent e) {
        final JComboBox combo = getMarginCombo();
        if (combo != null) {
            final ComboBoxEditor editor = combo.getEditor();
            final String item = editor.getItem().toString();
            if (item != null && item.length() > 0) {
                final int marginValue = Integer.parseInt(item);
                super.style = RiderStyles.getMarginXStyle(marginValue);
                updateComboData(combo);
                super.actionPerformed(e);
            } else {
                combo.getEditor().selectAll();
                StyledAreaAction.updateStatus();
            }
        }
    }

    private static JComboBox getMarginCombo() {
        return MainApp.getInstance().getMainAppFrame().getManagerDirector().getToolbarManager().getInputMarginXCombo();
    }

    public static void updateComboData(JComboBox comboMarginX) {
        ComboBoxModel model = comboMarginX.getModel();
        final int count = model.getSize();
        final Object[] objects = RiderStyles.getVariableMargins();
        if (count != objects.length)
            comboMarginX.setModel(new DefaultComboBoxModel(objects));
    }
}
