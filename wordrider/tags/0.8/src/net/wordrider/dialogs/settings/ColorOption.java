package net.wordrider.dialogs.settings;

import net.wordrider.area.ColorStyles;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Vity
 */
final class ColorOption extends JButton implements IOptionable<JButton> {
    //private final static Border border = BorderFactory.createLineBorder(Color.BLACK);
    private final Color initValue;
    private Color applyedValue;

    private Color selectedValue;
    private final OptionsGroupManager optionsGroupManager;

    private final static Dimension colorPanelDimension = new Dimension(34, 19);
    private static final ActionListener clickAction = new ButtonClick();
    private boolean wasChanged = false;
    private IOptionGroup group = null;
    private int colorCode;

    private static final class ButtonClick implements ActionListener {
        public final void actionPerformed(final ActionEvent e) {
            final ColorOption component = ((ColorOption) e.getSource());
            component.setSelectedValue(JColorChooser.showDialog(component.getParent(), "Choose color", component.getSelectedValue()));
        }
    }

    public ColorOption(final OptionsGroupManager optionsGroupManager, final int initColor, final IOptionGroup group) {
        this(optionsGroupManager, ColorStyles.getColor(initColor), group);
        this.colorCode = initColor;
    }

    private ColorOption(final OptionsGroupManager optionsGroupManager, final Color initValue, final IOptionGroup group) {
        super();
        this.group = group;
        this.optionsGroupManager = optionsGroupManager;
//        this.optionsGroupManager.registerOptionable(this);
        this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        this.addActionListener(clickAction);
        this.setOpaque(false);
        //this.setBackground(initValue);
        this.initValue = this.applyedValue = this.selectedValue = initValue;
        this.setFont(this.getFont().deriveFont(Font.PLAIN, 7));
        this.setText(" ");
        this.setRolloverEnabled(false);
        this.setFocusPainted(false);
        this.setFocusable(true);
        this.setMinimumSize(colorPanelDimension);
        this.setMaximumSize(colorPanelDimension);
        this.setPreferredSize(colorPanelDimension);
        //        this.setSize(colorPanelDimension);
    }

    public final void paint(final Graphics g) {
        super.paint(g);    //call to super
        final int offsetx = 4;
        final int offsety = 3;
        //      g.setXORMode(this.getParent().getBackground());
        g.setColor(Color.BLACK);
        g.drawRect(offsetx, offsety, this.getWidth() - (2 * offsetx) - 1, this.getHeight() - (2 * offsety) - 2);
        g.setColor(this.selectedValue);
        g.fillRect(offsetx + 1, offsety + 1, this.getWidth() - (2 * offsetx) - 2, this.getHeight() - (2 * offsety) - 3);
        //        if (this.isFocusOwner()) {
        //            g.setColor(this.selectedValue.brighter());
        //            g.drawRect(offsetx+3, offsety+3, this.getWidth() - (2 * offsetx) - 5, this.getHeight() - (2 * offsety) - 6);
        //        }
    }

    private void setSelectedValue(final Color newValue) {
        if (newValue != null && !this.selectedValue.equals(newValue)) {
            this.selectedValue = newValue;
            wasChanged = !applyedValue.equals(newValue);
            optionsGroupManager.makeChange(this);
            repaint();
        }
    }

    private Color getDefault() {
        return ColorStyles.getDefaultColor(colorCode);
    }

    public final void applyChange() {
        ColorStyles.setColor(colorCode, selectedValue);
        wasChanged = false;
        applyedValue = selectedValue;
    }

    public final void restorePrevious() {
        this.selectedValue = initValue;
    }

    public final IOptionGroup getOptionsGroup() {
        return group;  //implement - call to super class
    }

    public final void setDefault() {
        setSelectedValue(getDefault());
    }

    public final boolean wasChanged() {
        return wasChanged;  //implement - call to super class
    }

    private Color getSelectedValue() {
        return selectedValue;
    }


    public JButton getComponent() {
        return this;
    }
}

