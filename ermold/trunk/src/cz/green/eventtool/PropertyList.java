package cz.green.eventtool;

import cz.green.swing.ConstantConstraint;
import cz.green.swing.CountLayout;
import cz.green.swing.LinearConstraint;
import cz.green.swing.SimpleBoundsConstraint;

import javax.swing.*;
import java.awt.*;
import java.beans.*;
import java.lang.reflect.Method;

/**
 * This is panel with all editable properties of one component.
 */
public class PropertyList extends JComponent implements Scrollable, PropertyChangeListener {
    /**
     * Adjustable class to scroll this panel
     */
    protected Adjustable scroll = null;
    /**
     * The bean to edit its properties
     */
    protected Object bean = null;
    /**
     * Property descriptors to all bean properties
     */
    protected PropertyDescriptor[] properties = null;
    /**
     * Property editors for each property that has its editor, others are <code>null</code>
     */
    protected PropertyEditor[] editors = null;
    /**
     * The values of all readable properties, others are <code>null</code>
     */
    protected Object[] values = null;
    /**
     * The instances ShowProperty for all readable, writable a editable (has its editor) properties
     * of editting bean
     *
     * @see ShowProperty
     */
    protected ShowProperty[] views = null;
    /**
     * The labels with names of all properties
     */
    protected JLabel[] labels = null;
    /**
     * Determines whether the panel consists also hidden properties
     */
    protected boolean showHidden = false;
    /**
     * Determines whether the panel consists also expert properties
     */
    protected boolean showExpert = false;
    protected int height = 0;
    protected boolean customizable = false;
    protected Class customizerClass = null;
    protected String beanName = "";
    protected Frame parent = null;

    /**
     * Construct the Property list with no associated scrollbar.
     * Do the same things as other constructor.
     *
     * @see PropertyList(java.awt.Adjustable)
     */
    public PropertyList(Frame parent) {
        this(parent, null);
    }

    /**
     * Creates PropertyList with the specified associated scrollbar
     *
     * @param scroller The associated scrollbar.
     */
    public PropertyList(Frame parent, Adjustable scroller) {
        super();
        setLayout(new CountLayout());
        scroll = scroller;
        this.parent = parent;
    }

    /**
     * Returns the bean which properties are editted.
     */
    public Object getBean() {
        return bean;
    }

    /**
     * getPreferredScrollableViewportSize method comment.
     */
    public java.awt.Dimension getPreferredScrollableViewportSize() {
        return getSize();
    }

    public Dimension getPreferredSize() {
        return getSize();
    }

    /**
     * getScrollableBlockIncrement method comment.
     */
    public int getScrollableBlockIncrement(java.awt.Rectangle visibleRect, int orientation, int direction) {
        if (orientation == SwingConstants.VERTICAL)
            return (visibleRect.height / height) * height - height;
        return visibleRect.width;
    }

    /**
     * getScrollableTracksViewportHeight method comment.
     */
    public boolean getScrollableTracksViewportHeight() {
        return false;
    }

    /**
     * getScrollableTracksViewportWidth method comment.
     */
    public boolean getScrollableTracksViewportWidth() {
        return true;
    }

    /**
     * getScrollableUnitIncrement method comment.
     */
    public int getScrollableUnitIncrement(java.awt.Rectangle visibleRect, int orientation, int direction) {
        if (orientation == SwingConstants.VERTICAL)
            return height;
        return 1;
    }

    /**
     * This method was created in VisualAge.
     *
     * @return boolean
     */
    public boolean isCustomizeable() {
        return customizable;
    }

    /**
     * This method was created by Jiri Mares
     */
    public void modifyPropertyChangeListener(Object bean, boolean add) {
        if (bean == null)
            return;
        Class cls = bean.getClass();
        Class[] paramTypes = {PropertyChangeListener.class};
        Method method = null;
        while (true) {
            try {
                //try to find handler
                method = cls.getMethod(((add) ? "addPropertyChangeListener" : "removePropertyChangeListener"), paramTypes);
                break;
            } catch (NoSuchMethodException e) {
                //handler doesn't exist, try to look in super class
                if ((cls = cls.getSuperclass()) == null)
                    break;
            }
        }
        if (method != null) {
            Object[] params = {this};
            try {
                method.invoke(bean, params);
            } catch (Throwable ex) {
            }
        }
    }

    /**
     * Sets the new value of the changed property. This method is invoked when the value of a
     * property changes in its editor. Change the value of the property and rereads the values
     * of all properties to find out which changed to repaint it.
     *
     * @param e The event with the source of the event.
     */
    public void propertyChange(PropertyChangeEvent e) {
        if (e.getSource() instanceof PropertyEditor) {
            PropertyEditor editor = (PropertyEditor) e.getSource();
            for (int i = editors.length - 1; i >= 0; i--) {
                if (editors[i] == editor) {
                    Object value = editor.getValue();
                    if (value != values[i]) {
                        Method setter = properties[i].getWriteMethod();
                        try {
                            Object args[] = new Object[1];
                            args[0] = value;
                            setter.invoke(bean, args);
                        } catch (Exception ex) {
                            System.out.println(ex);
                            editor.setValue(values[i]);
                            return;
                        }
                        values[i] = value;
                        views[i].propertyChange();
                    } else {
                        //the value is the same, there no reason to continue
                        return;
                    }
                    break;
                }
            }
        }
        // Now look for other changed properties
        for (int i = properties.length - 1; i >= 0; i--) {
            Object value;
            try {
                Method getter = properties[i].getReadMethod();
                Object args[] = {};
                value = getter.invoke(bean, args);
            } catch (Exception ex) {
                value = null;
            }
            if ((value == values[i]) || ((value != null) && (value.equals(values[i]))))
                continue;
            values[i] = value;
            if (editors[i] == null)
                continue;
            editors[i].setValue(value);
            if (views[i] != null) {
                views[i].propertyChange();
            }
        }
    }

    /**
     * Sets the bean to edit and makes the intospection.
     * Sets values of the attributes as properties, editors, values, views and labels.
     *
     * @param newValue The bean to edit.
     * @see #properties
     * @see #editors
     * @see #values
     * @see #views
     * @see #labels
     */
    public void setBean(Object bean, String beanName) {
        modifyPropertyChangeListener(this.bean, false);
        this.bean = bean;
        this.beanName = beanName;
        try {
            BeanInfo info = Introspector.getBeanInfo(bean.getClass());
            customizerClass = info.getBeanDescriptor().getCustomizerClass();
            customizable = (customizerClass != null);
            properties = info.getPropertyDescriptors();
            editors = new PropertyEditor[properties.length];
            values = new Object[properties.length];
            views = new ShowProperty[properties.length];
            labels = new JLabel[properties.length];
            // EditedAdaptor adaptor = new EditedAdaptor(frame);
            for (int i = properties.length - 1; i >= 0; i--) {
                String name = properties[i].getDisplayName();
                Class type = properties[i].getPropertyType();
                Method getter = properties[i].getReadMethod();
                Method setter = properties[i].getWriteMethod();
                // Only display read/write properties.
                if (getter == null || setter == null) {
                    continue;
                }
                Component view = null;
                Object value;
                try {
                    Object args[] = {};
                    value = getter.invoke(bean, args);
                    Class propEditor = properties[i].getPropertyEditorClass();
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
                    if (value == null) {
                        //We can't set null value;
                        continue;
                    }
                    editors[i] = editor;
                    editor.setValue(value);
                    values[i] = value;
                    editor.addPropertyChangeListener(this);
                    views[i] = new ShowProperty(parent, editor, name);
                } catch (Exception ex) { //We have to skip this property.
                    continue;
                }
                labels[i] = new JLabel(name);
                labels[i].setToolTipText(properties[i].getShortDescription());
            }
            setVisible(false);
            showProperties();
            doLayout();
            setVisible(true);
            modifyPropertyChangeListener(bean, true);
        } catch (IntrospectionException e) {
        }
    }

    /**
     * Set the attribute showExpert. Caused replacing all components for editing peoperties.
     *
     * @param value Determines where the expert properties are shown (<code>true</code>) or not
     *              (<code>false</code>).
     * @see #showExpert
     */
    public void setShowExpert(boolean value) {
        showExpert = value;
        setVisible(false);
        showProperties();
        doLayout();
        setVisible(true);
    }

    /**
     * Set the attribute showHidden. Caused replacing all components for editing peoperties.
     *
     * @param value Determines where the hidden properties are shown (<code>true</code>) or not
     *              (<code>false</code>).
     * @see #showHidden
     */
    public void setShowHidden(boolean value) {
        showHidden = value;
        setVisible(false);
        showProperties();
        doLayout();
        setVisible(true);
    }

    /**
     * This method was created in VisualAge.
     *
     * @return java.lang.Class
     */
    public void showCustomizer() {
        Component customizer;
        try {
            customizer = (Component) customizerClass.newInstance();
            ((java.beans.Customizer) customizer).setObject(bean);
//		((java.beans.Customizer) customizer).addPropertyChangeListener(this);
        } catch (Throwable th) {
            //error while instantiation new class
            return;
        }
        JDialog dialog = new JDialog(parent, "Customize \"" + beanName + "\"", true);
        dialog.getContentPane().add("Center", customizer);
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    /**
     * Removes all components and places all for visible properties of the edited bean.
     *
     * @see #showExpert
     * @see #showHidden
     */
    protected void showProperties() {
        removeAll();
        if (views == null || labels == null)
            return;
        Dimension size = getSize(), d;
        int w = 0, y = 0;
        height = 0;
        for (int i = 0, tmp; i < properties.length; i++) {
            if (labels[i] == null || views[i] == null)
                continue;
            if ((tmp = (d = labels[i].getPreferredSize()).width) > w)
                w = tmp;
            if ((tmp = views[i].getPreferredSize().height) > height)
                height = tmp;
            if ((tmp = d.height) > height)
                height = tmp;
        }
        ++height;
        for (int i = 0; i < properties.length; i++) {
            if ((labels[i] == null) || (views[i] == null))
                continue;
            if ((!showHidden && properties[i].isHidden()) || (!showExpert && properties[i].isExpert()))
                continue;
            add(labels[i], new SimpleBoundsConstraint(
                    new ConstantConstraint(5, w), new ConstantConstraint(y, height - 1)));
            add(views[i], new SimpleBoundsConstraint(
                    new LinearConstraint(w + 10, 0.0, -(w + 10), 1.0), new ConstantConstraint(y, height - 1)));
            y += height;
        }
        setSize(size.width, y);
    }
}
