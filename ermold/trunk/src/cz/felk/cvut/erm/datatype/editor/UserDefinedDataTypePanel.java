package cz.felk.cvut.erm.datatype.editor;

import cz.felk.cvut.erm.datatype.DataTypeManager;
import cz.felk.cvut.erm.datatype.UserDefinedDataType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class UserDefinedDataTypePanel extends DataTypePanel implements ActionListener, ItemListener, PropertyChangeListener {

    private UserDefinedDataType ivjUserDefinedDataType = null;
    private JComboBox jComboBox = null;
    private JLabel jLabel = null;
    private JButton refreshButton = null;
    private PropertyChangeSupport propertyChange = null;


    final ActionListener refreshActionListener = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            String oldItem = "";

            if (getDataTypeManager().getTypeNamesChanged())
                getDataTypeManager().setTypeNamesChanged(false);
            if (getJComboBox().getItemCount() > 0)
                oldItem = (String) getJComboBox().getSelectedItem();
            getJComboBox().removeAllItems();
            for (int i = 0; i < getDataTypeManager().getTypeNames().size(); i++) {
                getJComboBox().addItem(getDataTypeManager().getTypeNames().get(i));
                if ((getDataTypeManager().getTypeNames().get(i)).compareTo(oldItem) == 0)
                    getJComboBox().setSelectedIndex(i);
            }
        }
    };

    private DataTypeManager getDataTypeManager() {
        return null;
    }

    /**
     * Constructor
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    public UserDefinedDataTypePanel() {
        super();
        initialize();
    }

    /**
     * Accessor for the propertyChange field.
     */
    protected PropertyChangeSupport getPropertyChange() {
        if (propertyChange == null)
            propertyChange = new PropertyChangeSupport(this);
        return propertyChange;
    }

    /**
     * The addPropertyChangeListener method was generated to support the propertyChange field.
     */
    public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
        getPropertyChange().addPropertyChangeListener(listener);
    }

    /**
     * The removePropertyChangeListener method was generated to support the propertyChange field.
     */
    public synchronized void removePropertyChangeListener(PropertyChangeListener listener) {
        getPropertyChange().removePropertyChangeListener(listener);
    }

    public void firePropertyChange() {
        getPropertyChange().firePropertyChange("", null, null);
    }

    /**
     * Return the jLabel property value.
     *
     * @return javax.swing.JLabel
     */
    private javax.swing.JLabel getJLabel() {
        if (jLabel == null) {
            try {
                jLabel = new JLabel();
                jLabel.setName("jLabel");
                jLabel.setText("User types:");
                jLabel.setBounds(5, 5, 71, 15);
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return jLabel;
    }

    /**
     * Return the jComboBox property value.
     *
     * @return javax.swing.JComboBox
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    public JComboBox getJComboBox() {
        String oldItem = "";

        if (jComboBox == null) {
            try {
                jComboBox = new JComboBox();
                getDataTypeManager().setTypeNamesChanged(true);
                jComboBox.setName("JComboBox");
                jComboBox.setToolTipText("Select your own type");
                jComboBox.setBounds(5, 25, 87, 25);
                //System.out.println("vytvarim jComboBox...");
            } catch (java.lang.Throwable ivjExc) {
                //System.out.println("getJComboBox() exception");
                handleException(ivjExc);
            }
        }
        if (getDataTypeManager().getTypeNamesChanged()) {
            getDataTypeManager().setTypeNamesChanged(false);
            if (jComboBox.getItemCount() > 0) {
                oldItem = (String) jComboBox.getSelectedItem();
                //System.out.println("old item = "+ (String)jComboBox.getSelectedItem());
            }
            jComboBox.removeAllItems();
            //System.out.println("items removed...");
            for (int i = 0; i < getDataTypeManager().getTypeNames().size(); i++) {
                jComboBox.addItem(getDataTypeManager().getTypeNames().get(i));
                //System.out.println(DataType.getTypeNames().get(i)+" added to combobox");
                if ((getDataTypeManager().getTypeNames().get(i)).compareTo(oldItem) == 0)
                    jComboBox.setSelectedIndex(i);
            }
        }
        //System.out.println("getJComboBox() "+ jComboBox.getSelectedItem());
        return jComboBox;
    }

    protected JButton getRefreshButton() {
        if (refreshButton == null) {
            refreshButton = new JButton();
            refreshButton.setIcon(new ImageIcon(ClassLoader.getSystemResource("img/refresh2.gif")));
            refreshButton.setToolTipText("Refresh user types list");
            refreshButton.setBounds(90, 25, 25, 25);
            refreshButton.setEnabled(true);
        }
        return refreshButton;
    }

    /**
     * Method to handle events for the ItemListener interface.
     *
     * @param e java.awt.event.ActionEvent
     */
    public void itemStateChanged(ItemEvent e) {
        //System.out.println((String) jComboBox.getSelectedItem());
        getUserDefinedDataType().setName((String) getJComboBox().getSelectedItem());
    }

    /**
     * Method to handle events for the ActionListener interface.
     *
     * @param e java.awt.event.ActionEvent
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    public void actionPerformed(java.awt.event.ActionEvent e) {
        ivjUserDefinedDataType = (UserDefinedDataType) getUserDefinedDataType().clone();
        ((DataTypeEditor) getDataTypeEditor()).setValue(ivjUserDefinedDataType);
    }

    /**
     * Return the LengthDataType property value.
     *
     * @return cz.omnicom.ermodeller.datatype.LengthDataType
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private UserDefinedDataType getUserDefinedDataType() {
        if (ivjUserDefinedDataType == null) {
            try {
                ivjUserDefinedDataType = new UserDefinedDataType();
            } catch (java.lang.Throwable ivjExc) {
                //System.out.println("getUserDefinedDataType() exception");
                handleException(ivjExc);
            }
        }
        return ivjUserDefinedDataType;
    }

    /**
     * This method was created by Aleš Kopecký.
     *
     * @return java.awt.Dimension
     */
    public Dimension getPreferredSize() {
        return new Dimension(114, 93);
    }

    /**
     * Called whenever the part throws an exception.
     *
     * @param exception java.lang.Throwable
     */
    private void handleException(Throwable exception) {

        /* Uncomment the following lines to print uncaught exceptions to stdout */
        // System.out.println("--------- UNCAUGHT EXCEPTION ---------");
        // exception.printStackTrace(System.out);
    }

    /**
     * Initializes connections
     */
    private void initConnections() {
        getJComboBox().addItemListener(this);
        getJComboBox().addActionListener(this);
        getRefreshButton().addActionListener(refreshActionListener);
    }

    /**
     * Initialize the class.
     */
    private void initialize() {
        try {
            setName("UserDefinedDataTypePanel");
            setLayout(null);
            add(getJComboBox(), getJComboBox().getName());
            setBackground(new java.awt.Color(204, 204, 204));//204
            add(getJLabel(), getJLabel().getName());
            add(getRefreshButton());
            initConnections();
            if (getJComboBox().getItemCount() > 0)
                getUserDefinedDataType().setName((String) getJComboBox().getSelectedItem());
            else
                getUserDefinedDataType().setName("");
            //System.out.println(getUserDefinedDataType().toDescriptionString());
            //firePropertyChange();
        } catch (java.lang.Throwable ivjExc) {
            //System.out.println("initialize() exception");
            handleException(ivjExc);
        }
    }

    /**
     * main entrypoint - starts the part when it is run as an application
     *
     * @param args java.lang.String[]
     */
    public static void main(java.lang.String[] args) {
        try {
            java.awt.Frame frame;
            try {
                Class aFrameClass = Class.forName("com.ibm.uvm.abt.edit.TestFrame");
                frame = (java.awt.Frame) aFrameClass.newInstance();
            } catch (java.lang.Throwable ivjExc) {
                frame = new java.awt.Frame();
            }
            UserDefinedDataTypePanel aUserDefinedDataTypePanel;
            aUserDefinedDataTypePanel = new UserDefinedDataTypePanel();
            frame.add("Center", aUserDefinedDataTypePanel);
            frame.setSize(200, 200);
            //frame.setSize(aUserDefinedDataTypePanel.getSize());
            frame.setVisible(true);
        } catch (Throwable exception) {
            System.err.println("Exception occurred in main() of javax.swing.JPanel");
            exception.printStackTrace(System.out);
        }
    }

    /**
     * This method was created in VisualAge.
     *
     * @param anEvent java.beans.PropertyChangeEvent
     */
    public void propertyChange(PropertyChangeEvent anEvent) {
        /*
      if (anEvent.getPropertyName().equals(UserDefinedDataType.LENGTH_PROPERTY_CHANGE)) {
          getLengthTextField().setText(anEvent.getNewValue().toString());
          return;
      }
      */
        // call inherited method
        //System.out.println("property change");
        //firePropertyChange(anEvent.getPropertyName(), anEvent.getOldValue(), anEvent.getNewValue());

    }

    /**
     * This method was created by Aleš Kopecký.
     *
     * @param aDataType cz.omnicom.ermodeller.datatype.CharDataType
     */
    public void setUserDefinedDataType(UserDefinedDataType aDataType) {
        ivjUserDefinedDataType = aDataType;
        ivjUserDefinedDataType.addPropertyChangeListener(this);
    }
}