package cz.cvut.felk.erm.sandbox;
/*
Code revised from Desktop Java Live:
http://www.sourcebeat.com/downloads/
*/

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.adapter.ToggleButtonAdapter;
import com.jgoodies.binding.beans.BeanAdapter;
import com.jgoodies.binding.beans.Model;
import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class ToggleButtonAdapterExample extends JPanel {
    public ToggleButtonAdapterExample() {
        DefaultFormBuilder defaultFormBuilder = new DefaultFormBuilder(new FormLayout("p, 2dlu, p"));
        defaultFormBuilder.setDefaultDialogBorder();

        ToggleChangeListener toggleChangeListener = new ToggleChangeListener();

        BooleanBean booleanBean = new BooleanBean();

        BeanAdapter booleanBeanAdapter = new BeanAdapter(booleanBean, true);
        booleanBeanAdapter.addBeanPropertyChangeListener(toggleChangeListener);
        ValueModel booleanValueModel = booleanBeanAdapter.getValueModel("enabled");

        JCheckBox checkBox = BasicComponentFactory.createCheckBox(booleanValueModel, "Enabled");

        JToggleButton booleanToggleButton = new JToggleButton();
        booleanToggleButton.setPreferredSize(new Dimension(20, 20));
        booleanToggleButton.setModel(new ToggleButtonAdapter(booleanValueModel));

        defaultFormBuilder.append("Check Box:", checkBox);
        defaultFormBuilder.append("Toggle Button:", booleanToggleButton);

        StopAndGoBean stopAndGoBean = new StopAndGoBean();
        BeanAdapter stopAndGoBeanAdapter = new BeanAdapter(stopAndGoBean);
        stopAndGoBeanAdapter.addBeanPropertyChangeListener(toggleChangeListener);

        ValueModel stopAndGoModel = stopAndGoBeanAdapter.getValueModel("state");

        JToggleButton stopAndGoToggleButton = new JToggleButton();
        stopAndGoToggleButton.setPreferredSize(new Dimension(20, 20));
        stopAndGoToggleButton.setModel(new ToggleButtonAdapter(stopAndGoModel, "stop", "go"));

        defaultFormBuilder.append("Stop/Go Button:", stopAndGoToggleButton);

        add(defaultFormBuilder.getPanel());
    }

    private class ToggleChangeListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            JOptionPane.showMessageDialog(null, "Property " + evt.getPropertyName() + " was changed to " + evt.getNewValue());
        }
    }

    public class BooleanBean extends Model {
        public final static String ENABLED_PROPERTY = "enabled";
        private Boolean enabled = Boolean.TRUE;

        public Boolean getEnabled() {
            return enabled;
        }

        public void setEnabled(Boolean enabled) {
            Boolean oldValue = this.enabled;
            this.enabled = enabled;
            firePropertyChange(ENABLED_PROPERTY, oldValue, this.enabled);
        }
    }

    public class StopAndGoBean extends Model {
        public final static String STATE_PROPERTY = "state";
        private String state = "stop";

        public String getState() {
            return state;
        }

        public void setState(String state) {
            String oldState = this.state;
            this.state = state;
            firePropertyChange(STATE_PROPERTY, oldState, this.state);
        }
    }


    public static void main(String[] a) {
        JFrame f = new JFrame("ToggleButtonAdapter Example");
        f.setDefaultCloseOperation(2);
        f.add(new ToggleButtonAdapterExample());
        f.pack();
        f.setVisible(true);
    }
}