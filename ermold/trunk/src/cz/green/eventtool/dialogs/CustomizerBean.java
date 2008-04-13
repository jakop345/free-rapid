package cz.green.eventtool.dialogs;

import cz.green.util.WindowCloser;

import javax.swing.*;
import java.awt.*;
import java.beans.BeanInfo;
import java.beans.Introspector;

/**
 * This dialog present the JavaBeans Customiser to user.
 */
public class CustomizerBean extends JDialog {
    /**
     * This is the customizer as component.
     */
    private Component customizer = null;

    /**
     * Creates dialog, test whether existed customizzer for JavaBeans <code>bean</code> and
     * this customizer places into itself.
     *
     * @param parent Frame, where the dialog places is.
     * @param bean   JavaBeans, which customizer should show.
     * @param name   The name of the bean. Is use for title creating.
     */
    public CustomizerBean(java.awt.Frame parent, Object bean, String name) {
        super(parent, "Edit bean \"" + name + "\"", true);
        setLayout(null);
        setCustomizer(bean);
        if (customizer != null) {
            ((java.beans.Customizer) customizer).setObject(bean);
            add(customizer);
        } else {
            setTitle("Error - no customizer for bean \"" + name + "\"");
        }
        addWindowListener(new WindowCloser(this, false));
    }

    /**
     * Ask the customizer for its preffered size, places it to the dialog and sets the dialog size
     * to view whole customizer.
     */
    public void doLayout() {
        Insets insets = getInsets();
        if (customizer != null) {
            Dimension dim = customizer.getPreferredSize();
            //place components
            customizer.setBounds(2 + insets.left, 2 + insets.top, dim.width + 1, dim.height + 1);
            //set size
            setSize(dim.width + 5 + insets.left + insets.right, dim.height + 5 + insets.top + insets.bottom);
        } else
            setSize(300, 50);
    }

    /**
     * Gets the BeanInfo for the <code>bean</code> and tries to get customizer. If customizer exists,
     * tries to instantied it and set the property <code>customizer</code> to this new instance. If occurs some error
     * or problem, then sets <code>customizer</code> to <code>null</code>.
     */
    protected void setCustomizer(Object bean) {
        try {
            BeanInfo info = Introspector.getBeanInfo(bean.getClass());
            Class customizerClass = info.getBeanDescriptor().getCustomizerClass();
            if (customizerClass == null)
                return;
            try {
                customizer = (Component) customizerClass.newInstance();
            } catch (Exception e) {
                //error while instantiation new class
                customizer = null;
                return;
            }
        } catch (java.beans.IntrospectionException e) {
            customizer = null;
            return;
        }
        if (customizer instanceof java.beans.Customizer)
            return;
        customizer = null;
    }

}
