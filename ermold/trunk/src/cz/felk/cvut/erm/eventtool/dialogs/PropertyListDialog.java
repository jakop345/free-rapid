package cz.felk.cvut.erm.eventtool.dialogs;

import com.l2fprod.common.propertysheet.Property;
import com.l2fprod.common.propertysheet.PropertySheet;
import com.l2fprod.common.propertysheet.PropertySheetPanel;
import com.l2fprod.common.propertysheet.PropertySheetTable;
import cz.felk.cvut.erm.conceptual.beans.ConceptualObject;
import cz.felk.cvut.erm.swing.CountLayout;
import cz.felk.cvut.erm.swing.LinearConstraint;
import cz.felk.cvut.erm.swing.SimpleBoundsConstraint;

import javax.swing.*;
import java.awt.*;
import java.beans.*;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Dialog that shows the PropertyList and required controls.
 */
public class PropertyListDialog extends JDialog implements PropertyChangeListener {
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
    private JButton customizer = null;
    protected static PropertyListDialog dialog = null;
    protected Frame parent = null;
    private PropertySheetPanel sheet;
    private Object activeBean;

    /**
     * Creates the dialog and places in all components
     *
     * @param parent The frame where the dialog is sutuated.
     * @param title  The title of the dialog.
     */
    public PropertyListDialog(Frame parent, boolean modal) {
        super(parent, "", modal);
        this.parent = parent;
        getContentPane().setMaximumSize(new Dimension(200, 300));
        getContentPane().setLayout(new CountLayout(160, 300));
        //getContentPane().add(getScroller(), new SimpleBoundsConstraint(new LinearConstraint(5, 0.0, -10, 1.0), new LinearConstraint(5, 0.0, -70, 1.0)));
        //getContentPane().add(getExpertCheck(), new SimpleBoundsConstraint(new LinearConstraint(5, 0.0, -8, 0.5), new LinearConstraint(-30, 1.0, 25, 0.0)));
        //getContentPane().add(getHiddenCheck(), new SimpleBoundsConstraint(new LinearConstraint(3, 0.5, -8, 0.5), new LinearConstraint(-30, 1.0, 25, 0.0)));
        getContentPane().add(getCustomizer(), new SimpleBoundsConstraint(new LinearConstraint(5, 0.0, -10, 1.0), new LinearConstraint(-60, 1.0, 25, 0.0)));
        //getExpertCheck().addItemListener(this);
        //getHiddenCheck().addItemListener(this);
        sheet = new PropertySheetPanel();
        sheet.setPreferredSize(new Dimension(200, 300));
        getContentPane().add(sheet, new SimpleBoundsConstraint(new LinearConstraint(5, 0.0, -10, 1.0), new LinearConstraint(5, 0.0, -70, 1.0)));
        sheet.setName("PropertySheetEditor");
        sheet.setMode(PropertySheet.VIEW_AS_FLAT_LIST);
        sheet.setToolBarVisible(true);
        sheet.setDescriptionVisible(true);
        final PropertySheetTable sheetTable = sheet.getTable();
        sheetTable.setRowHeight(20);
        sheetTable.setFont(sheet.getFont().deriveFont(Font.PLAIN, (float) 16.0));

        // everytime a property change, update the button with it
        PropertyChangeListener listener = new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                Property prop = (Property) evt.getSource();
                try {
                    prop.writeToObject(activeBean);
                } catch (RuntimeException e) {
                    reloadProperties(activeBean);
                }
            }
        };
        sheet.addPropertySheetChangeListener(listener);

//        try {
//            customizer.addActionListener(new ActionAdapter(getProperties(), "showCustomizer"));
//        } catch (NoSuchMethodException x) {
//        }
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
        //setBeanAndEdit(bean, name);
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
        return customizer;
    }

    /**
     * Returns the checkbox for showing (hidding) the expert properties.
     */
    private JCheckBox getExpertCheck() {
        if (expertCheck == null) {
            expertCheck = new JCheckBox("Show expert features");
        }
        return expertCheck;
    }

    /**
     * Returns the checkbox for showing (hidding) the hidden properties.
     */
    private JCheckBox getHiddenCheck() {
        if (hiddenCheck == null) {
            hiddenCheck = new JCheckBox("Show hidden features");
        }
        return hiddenCheck;
    }

//    /**
//     * Returns the PropertyList component that shows the editable properties
//     * according to the state hidden and expert properties showing checkbox.
//     */
//    private PropertyList getProperties() {
//        if (properties == null) {
//            properties = new PropertyList(parent);
//        }
//        return properties;
//    }
//
//    /**
//     * Returns the instance of the VerticalScroller class. This instance is used for
//     * scrolling the PropewrtyList component.
//     */
//    private JScrollPane getScroller() {
//        if (scroller == null) {
//            scroller = new JScrollPane();
//            scroller.setViewportView(getProperties());
//            scroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
//        }
//        return scroller;
//    }

//    /**
//     * Invoked when the state of one checkbox changed. Set the right state to the in-places
//     * PropertyList component.
//     *
//     * @param e The event.
//     * @see java.awt.event.ItemListener
//     * @see PropertyList#setShowExpert(boolean)
//     * @see PropertyList#setShowHidden(boolean)
//     */
//    public void itemStateChanged(ItemEvent e) {
//        if (e.getSource() == getExpertCheck()) {
//            getProperties().setShowExpert(e.getStateChange() == ItemEvent.SELECTED);
//        }
//        if (e.getSource() == getHiddenCheck()) {
//            getProperties().setShowHidden(e.getStateChange() == ItemEvent.SELECTED);
//        }
//        doLayout();
//    }

//    /**
//     * Set the bean to edit its properties.
//     *
//     * @param aBean The bean to edit.
//     */
//    public void setBean(Object bean, String name) {
//        setTitle("Editing \"" + name + "\"");
//        getProperties().setBean(bean, name);
//        setCustomizeable(getProperties().isCustomizeable());
//    }


    /**
     * Set the bean to edit its properties. PŠ - same as setBean, because properties dialog in
     * no longer as alone window
     */
    static public void setBeanAndEdit(Object bean, String name) {
//	if (dialog != null) {
        if (dialog.activeBean != null)
            ((ConceptualObject) dialog.activeBean).removePropertyChangeListener(dialog);
        dialog.activeBean = bean;
        ((ConceptualObject) bean).addPropertyChangeListener(dialog);

        BeanInfo beanInfo = new SimpleBeanInfo();
        try {
            beanInfo = Introspector.getBeanInfo(bean.getClass());
        } catch (IntrospectionException e) {
            e.printStackTrace();
        }

        final PropertyDescriptor[] descriptors = beanInfo.getPropertyDescriptors();
        List<PropertyDescriptor> descriptorList = new ArrayList<PropertyDescriptor>();
        for (int i = 0, c = descriptors.length; i < c; i++) {
            if (descriptors[i].isHidden())
                continue;
            Method getter = descriptors[i].getReadMethod();
            Method setter = descriptors[i].getWriteMethod();
            // Only display read/write properties.
            if (getter == null || setter == null) {
                continue;
            }
            Class propEditor = descriptors[i].getPropertyEditorClass();
            Class type = descriptors[i].getPropertyType();
            PropertyEditor editor = null;
            if (propEditor != null) {
                try {
                    editor = (PropertyEditor) propEditor.newInstance();
                } catch (Exception e) {
                    System.out.println(e);
                    // Do nothing
                }
            }
            if (editor == null) {
                editor = PropertyEditorManager.findEditor(type);
            }
            if (editor == null) {
                // We can't edit this property.
                continue;
            }


            descriptorList.add(descriptors[i]);
        }

        PropertyDescriptor[] descArray = descriptorList.toArray(new PropertyDescriptor[descriptorList.size()]);
        dialog.sheet.setProperties(descArray);

        reloadProperties(bean);

/*		dialog.validate();
        if (show) {
            dialog.setVisible(true);
        }
    }
*/
    }

    public void propertyChange(PropertyChangeEvent evt) {
        reloadProperties(dialog.activeBean);
    }

    private static void reloadProperties(Object bean) {
        Property[] properties = dialog.sheet.getProperties();

        for (int i = 0, c = properties.length; i < c; i++) {
            try {
                properties[i].readFromObject(bean);
            } catch (Exception e) {
                //as
            }
        }
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