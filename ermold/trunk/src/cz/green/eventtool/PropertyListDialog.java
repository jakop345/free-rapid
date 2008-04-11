package cz.green.eventtool;

import cz.green.swing.CountLayout;
import cz.green.swing.LinearConstraint;
import cz.green.swing.SimpleBoundsConstraint;
import cz.green.util.ActionAdapter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * Dialog that shows the PropertyList and required controls.
 */
public class PropertyListDialog extends JDialog implements ItemListener {
    /**
     * Checkbox for determining whether the expert property is shown
     */
    private JCheckBox expertCheck = null;
    /**
     * Checkbox for determining whether the hidden property is shown
     */
    private JCheckBox hiddenCheck = null;
    /**
     * The panel with all properties of the edited bean
     */
    private PropertyList properties = null;
    /**
     * Scrollbar for scrolling the PropertyList
     */
    private JScrollPane scroller = null;
    private JButton customizer = null;
    protected static PropertyListDialog dialog = null;
    protected Frame parent = null;

    /**
     * Creates the dialog and places in all components
     *
     * @param parent The frame where the dialog is sutuated.
     * @param title  The title of the dialog.
     */
    public PropertyListDialog(Frame parent, boolean modal) {
        super(parent, "", modal);
        this.parent = parent;
        getContentPane().setMaximumSize(new Dimension(160, 300));
        getContentPane().setLayout(new CountLayout(160, 300));
        getContentPane().add(getScroller(), new SimpleBoundsConstraint(new LinearConstraint(5, 0.0, -10, 1.0), new LinearConstraint(5, 0.0, -70, 1.0)));
        //getContentPane().add(getExpertCheck(), new SimpleBoundsConstraint(new LinearConstraint(5, 0.0, -8, 0.5), new LinearConstraint(-30, 1.0, 25, 0.0)));
        //getContentPane().add(getHiddenCheck(), new SimpleBoundsConstraint(new LinearConstraint(3, 0.5, -8, 0.5), new LinearConstraint(-30, 1.0, 25, 0.0)));
        getContentPane().add(getCustomizer(), new SimpleBoundsConstraint(new LinearConstraint(5, 0.0, -10, 1.0), new LinearConstraint(-60, 1.0, 25, 0.0)));
        //getExpertCheck().addItemListener(this);
        //getHiddenCheck().addItemListener(this);
        try {
            customizer.addActionListener(new ActionAdapter(getProperties(), "showCustomizer"));
        } catch (NoSuchMethodException x) {
        }
        pack();
//	setLocationRelativeTo(parent);
    }

    /**
     * Creates the dialog and places in all components
     *
     * @param parent The frame where the dialog is sutuated.
     * @param title  The title of the dialog.
     */
    public PropertyListDialog(Frame parent, boolean modal, Object bean, String name) {
        this(parent, modal);
        setBean(bean, name);
    }

    /**
     * This method was created in VisualAge.
     *
     * @param parent java.awt.Frame
     * @param modal  boolean
     */
    public static PropertyListDialog createListDialog(Frame parent, boolean modal) {
        dialog = new PropertyListDialog(parent, modal);
        dialog.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
        //dialog.setSize(300, 250);
        return dialog;

    }

    /**
     * This method was created in VisualAge.
     *
     * @param parent java.awt.Frame
     * @param modal  boolean
     */
    public static PropertyListDialog createListDialog(Frame parent, boolean modal, Object bean, String name) {
        dialog = new PropertyListDialog(parent, modal, bean, name);
        dialog.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
        //dialog.setSize(300, 250);
        return dialog;

    }

    /**
     * Returns the checkbox for showing (hidding) the expert properties.
     */
    private JButton getCustomizer() {
        if (customizer == null) {
            customizer = new JButton("Customize ...");
        }
        ;
        return customizer;
    }

    /**
     * Returns the checkbox for showing (hidding) the expert properties.
     */
    private JCheckBox getExpertCheck() {
        if (expertCheck == null) {
            expertCheck = new JCheckBox("Show expert features");
        }
        ;
        return expertCheck;
    }

    /**
     * Returns the checkbox for showing (hidding) the hidden properties.
     */
    private JCheckBox getHiddenCheck() {
        if (hiddenCheck == null) {
            hiddenCheck = new JCheckBox("Show hidden features");
        }
        ;
        return hiddenCheck;
    }

    /**
     * Returns the PropertyList component that shows the editable properties
     * according to the state hidden and expert properties showing checkbox.
     */
    private PropertyList getProperties() {
        if (properties == null) {
            properties = new PropertyList(parent);
        }
        ;
        return properties;
    }

    /**
     * Returns the instance of the VerticalScroller class. This instance is used for
     * scrolling the PropewrtyList component.
     */
    private JScrollPane getScroller() {
        if (scroller == null) {
            scroller = new JScrollPane();
            scroller.setViewportView(getProperties());
            scroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        }
        ;
        return scroller;
    }

    /**
     * Invoked when the state of one checkbox changed. Set the right state to the in-places
     * PropertyList component.
     *
     * @param e The event.
     * @see java.awt.event.ItemListener
     * @see PropertyList#setShowExpert(boolean)
     * @see PropertyList#setShowHidden(boolean)
     */
    public void itemStateChanged(ItemEvent e) {
        if (e.getSource() == getExpertCheck()) {
            getProperties().setShowExpert(e.getStateChange() == ItemEvent.SELECTED);
        }
        if (e.getSource() == getHiddenCheck()) {
            getProperties().setShowHidden(e.getStateChange() == ItemEvent.SELECTED);
        }
        doLayout();
    }

    /**
     * Set the bean to edit its properties.
     *
     * @param aBean The bean to edit.
     */
    public void setBean(Object bean, String name) {
        setTitle("Editing \"" + name + "\"");
        getProperties().setBean(bean, name);
        setCustomizeable(getProperties().isCustomizeable());
    }

    /**
     * Set the bean to edit its properties. PŠ - same as setBean, because properties dialog in
     * no longer as alone window
     *
     * @param aBean The bean to edit.
     */
    static public void setBeanAndEdit(Object bean, String name, boolean show) {
//	if (dialog != null) {
        dialog.setBean(bean, name);
/*		dialog.validate();
		if (show) {
			dialog.setVisible(true);
		}
	}
*/
    }

    /**
     * This method was created in VisualAge.
     *
     * @param custom boolean
     */
    public void setCustomizeable(boolean custom) {
        getCustomizer().setEnabled(custom);
}
}