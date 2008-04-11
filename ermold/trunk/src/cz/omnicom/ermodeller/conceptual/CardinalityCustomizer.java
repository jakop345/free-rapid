package cz.omnicom.ermodeller.conceptual;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.Customizer;
import java.beans.PropertyChangeSupport;

/**
 * Customizer for <code>Cardinality</code> bean.
 *
 * @see cz.omnicom.ermodeller.conceptual.Cardinality
 */
public class CardinalityCustomizer extends JPanel implements ActionListener, ItemListener, Customizer {
    private PropertyChangeSupport propertyChange = null;
    private JCheckBox ivjArbitraryCheckBox = null;
    private JPanel ivjCardinalityPanel = null;

    private JCheckBox ivjGlueCheckBox = null;
    private JRadioButton ivjN_aryRadioButton = null;
    private TextField ivjRoleField = null;
    private JLabel ivjRoleLabel = null;
    private JRadioButton ivjSimpleRadioButton = null;
    private Cardinality ivjCardinality = null;

    /**
     * Constructor
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    public CardinalityCustomizer() {
        super();
        initialize();
    }

    /**
     * Method to handle events for the ActionListener interface.
     *
     * @param e java.awt.event.ActionEvent
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    public void actionPerformed(java.awt.event.ActionEvent e) {
        // user code begin {1}
        // user code end
        if (e.getSource() == getRoleField())
            connEtoC5(e);
        // user code begin {2}
        // user code end
    }

    /**
     * The addPropertyChangeListener method was generated to support the propertyChange field.
     */
    public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
        getPropertyChange().addPropertyChangeListener(listener);
    }

    /**
     * Sets arbitrary property of the cardinality it customizes.
     *
     * @see cz.omnicom.ermodeller.conceptual.Cardinality
     */
    private void arbitraryCheckBox_ItemStateChanged(java.awt.event.ItemEvent anEvent) {

        getGlueCheckBox().setSelected(getSimpleRadioButton().isSelected());
        getGlueCheckBox().setEnabled(getSimpleRadioButton().isSelected());

        if (anEvent.getStateChange() == ItemEvent.DESELECTED)
            getCardinality().setArbitrary(false);
        else
            getCardinality().setArbitrary(true);
        getPropertyChange().firePropertyChange(Cardinality.ARBITRARY_PROPERTY_CHANGE, null, null);
    }

    /**
     * connEtoC1:  (ArbitraryCheckBox.item.itemStateChanged(java.awt.event.ItemEvent) --> CardinalityCustomizer.arbitraryCheckBox_ItemStateChanged(Ljava.awt.event.ItemEvent;)V)
     *
     * @param arg1 java.awt.event.ItemEvent
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private void connEtoC1(java.awt.event.ItemEvent arg1) {
        try {
            // user code begin {1}
            // user code end
            this.arbitraryCheckBox_ItemStateChanged(arg1);
            // user code begin {2}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connEtoC4:  (GlueCheckBox.item.itemStateChanged(java.awt.event.ItemEvent) --> CardinalityCustomizer.glueCheckBox_ItemStateChanged(Ljava.awt.event.ItemEvent;)V)
     *
     * @param arg1 java.awt.event.ItemEvent
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private void connEtoC4(java.awt.event.ItemEvent arg1) {
        try {
            // user code begin {1}
            // user code end
            this.glueCheckBox_ItemStateChanged(arg1);
            // user code begin {2}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connEtoC5:  (RoleField.action.actionPerformed(java.awt.event.ActionEvent) --> CardinalityCustomizer.roleField_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
     *
     * @param arg1 java.awt.event.ActionEvent
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private void connEtoC5(java.awt.event.ActionEvent arg1) {
        try {
            // user code begin {1}
            // user code end
            this.roleField_ActionPerformed(arg1);
            // user code begin {2}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connEtoC6:  (SimpleRadioButton.item.itemStateChanged(java.awt.event.ItemEvent) --> CardinalityCustomizer.simpleRadioButton_ItemStateChanged(Ljava.awt.event.ItemEvent;)V)
     *
     * @param arg1 java.awt.event.ItemEvent
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private void connEtoC6(java.awt.event.ItemEvent arg1) {
        try {
            // user code begin {1}
            // user code end
            this.simpleRadioButton_ItemStateChanged(arg1);
            // user code begin {2}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * The firePropertyChange method was generated to support the propertyChange field.
     */
    public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
    }

    /**
     * Return the ArbitraryChB property value.
     *
     * @return javax.swing.JCheckBox
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JCheckBox getArbitraryCheckBox() {
        if (ivjArbitraryCheckBox == null) {
            try {
                ivjArbitraryCheckBox = new javax.swing.JCheckBox();
                ivjArbitraryCheckBox.setName("ArbitraryCheckBox");
                ivjArbitraryCheckBox.setToolTipText("Mandatory participation of entity in relation");
                ivjArbitraryCheckBox.setText(" Mandatory");
                ivjArbitraryCheckBox.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
                ivjArbitraryCheckBox.setActionCommand("Arbitrary");
                ivjArbitraryCheckBox.setVerticalTextPosition(javax.swing.SwingConstants.CENTER);
                ivjArbitraryCheckBox.setVerticalAlignment(javax.swing.SwingConstants.CENTER);
                ivjArbitraryCheckBox.setBounds(6, 54, 104, 25);
                ivjArbitraryCheckBox.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjArbitraryCheckBox;
    }

    /**
     * Return the Cardinality1 property value.
     *
     * @return cz.omnicom.ermodeller.conceptual.Cardinality
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private Cardinality getCardinality() {
        if (ivjCardinality == null) {
            try {
                ivjCardinality = new cz.omnicom.ermodeller.conceptual.Cardinality();
                ivjCardinality.setMultiCardinality(true);
                ivjCardinality.setArbitrary(false);
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjCardinality;
    }

    /**
     * Return the JPanel1 property value.
     *
     * @return javax.swing.JPanel
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JPanel getCardinalityPanel() {
        if (ivjCardinalityPanel == null) {
            try {
                ivjCardinalityPanel = new javax.swing.JPanel();
                ivjCardinalityPanel.setName("CardinalityPanel");
                ivjCardinalityPanel.setOpaque(true);
                ivjCardinalityPanel.setBorder(getCardinalityPanelGroupBox());
                ivjCardinalityPanel.setLayout(null);
                ivjCardinalityPanel.setBackground(new java.awt.Color(204, 204, 204));
                ivjCardinalityPanel.setBounds(5, 91, 193, 83);
                getCardinalityPanel().add(getSimpleRadioButton(), getSimpleRadioButton().getName());
                getCardinalityPanel().add(getN_aryRadioButton(), getN_aryRadioButton().getName());
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjCardinalityPanel;
    }

    /**
     * Return the CardinalityPanelGroupBox property value.
     *
     * @return com.ibm.ivj.examples.vc.utilitybeans.GroupBox
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private Border getCardinalityPanelGroupBox() {
        return BorderFactory.createTitledBorder("Data type");
    }

    /**
     * Return the JCheckBox1 property value.
     *
     * @return javax.swing.JCheckBox
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JCheckBox getGlueCheckBox() {
        if (ivjGlueCheckBox == null) {
            try {
                ivjGlueCheckBox = new javax.swing.JCheckBox();
                ivjGlueCheckBox.setName("GlueCheckBox");
                ivjGlueCheckBox.setToolTipText("Merge entity with relation during generating relational schema and SQL");
                ivjGlueCheckBox.setText(" Merge");
                ivjGlueCheckBox.setActionCommand("Glue");
                ivjGlueCheckBox.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
                ivjGlueCheckBox.setBounds(131, 54, 83, 25);
                ivjGlueCheckBox.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjGlueCheckBox;
    }

    /**
     * Return the JRadioButton2 property value.
     *
     * @return javax.swing.JRadioButton
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JRadioButton getN_aryRadioButton() {
        if (ivjN_aryRadioButton == null) {
            try {
                ivjN_aryRadioButton = new javax.swing.JRadioButton();
                ivjN_aryRadioButton.setName("N_aryRadioButton");
                ivjN_aryRadioButton.setToolTipText("Multiple participation of entity in relation");
                ivjN_aryRadioButton.setText("N - ary");
                ivjN_aryRadioButton.setBounds(11, 50, 95, 25);
                ivjN_aryRadioButton.setActionCommand("N_ary");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjN_aryRadioButton;
    }

    /**
     * Gets the prefered size.
     *
     * @return Dimension
     */
    public Dimension getPreferredSize() {
        return new Dimension(220, 176);
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
     * Return the NameField property value.
     *
     * @return java.awt.TextField
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private java.awt.TextField getRoleField() {
        if (ivjRoleField == null) {
            try {
                ivjRoleField = new java.awt.TextField();
                ivjRoleField.setName("RoleField");
                ivjRoleField.setText("");
                ivjRoleField.setBackground(java.awt.SystemColor.activeCaptionText);
                ivjRoleField.setBounds(41, 17, 135, 23);
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjRoleField;
    }

    /**
     * Return the JLabel1 property value.
     *
     * @return javax.swing.JLabel
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JLabel getRoleLabel() {
        if (ivjRoleLabel == null) {
            try {
                ivjRoleLabel = new javax.swing.JLabel();
                ivjRoleLabel.setName("RoleLabel");
                ivjRoleLabel.setToolTipText("Mnemonic role of entity in relation");
                ivjRoleLabel.setText("Role:");
                ivjRoleLabel.setBounds(6, 22, 28, 15);
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjRoleLabel;
    }

    /**
     * Return the JRadioButton1 property value.
     *
     * @return javax.swing.JRadioButton
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JRadioButton getSimpleRadioButton() {
        if (ivjSimpleRadioButton == null) {
            try {
                ivjSimpleRadioButton = new javax.swing.JRadioButton();
                ivjSimpleRadioButton.setName("SimpleRadioButton");
                ivjSimpleRadioButton.setToolTipText("Unique participation of entity in relation");
                ivjSimpleRadioButton.setText("1 (unary)");
                ivjSimpleRadioButton.setActionCommand("Simple");
                ivjSimpleRadioButton.setSelected(true);
                ivjSimpleRadioButton.setBounds(11, 20, 95, 25);
                ivjSimpleRadioButton.setEnabled(true);
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjSimpleRadioButton;
    }

    /**
     * Sets the glue property of the customized cardinaltity.
     *
     * @see cz.omnicom.ermodeller.conceptual.Cardinality
     */
    private void glueCheckBox_ItemStateChanged(java.awt.event.ItemEvent anEvent) {
        if (anEvent.getStateChange() == ItemEvent.SELECTED)
            getCardinality().setGlue(true);
        else
            getCardinality().setGlue(false);
        getPropertyChange().firePropertyChange(Cardinality.GLUE_PROPERTY_CHANGE, null, null);
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
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private void initConnections() {
        // user code begin {1}
        // user code end
        getArbitraryCheckBox().addItemListener(this);
        getGlueCheckBox().addItemListener(this);
        getRoleField().addActionListener(this);
        getSimpleRadioButton().addItemListener(this);
    }

    /**
     * Initialize the class.
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private void initialize() {
        try {
            // user code begin {1}
            // user code end
            setName("CardinalityPropertiesEditor");
            setLayout(null);
            setBackground(new java.awt.Color(204, 204, 204));
            setSize(220, 176);
            add(getRoleLabel(), getRoleLabel().getName());
            add(getRoleField(), getRoleField().getName());
            add(getArbitraryCheckBox(), getArbitraryCheckBox().getName());
            add(getGlueCheckBox(), getGlueCheckBox().getName());
            add(getCardinalityPanel(), getCardinalityPanel().getName());
            initConnections();
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
        // user code begin {2}
        ButtonGroup group = new ButtonGroup();
        group.add(getSimpleRadioButton());
        group.add(getN_aryRadioButton());
        // user code end
    }

    /**
     * Method to handle events for the ItemListener interface.
     *
     * @param e java.awt.event.ItemEvent
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    public void itemStateChanged(java.awt.event.ItemEvent e) {
        // user code begin {1}
        // user code end
        if (e.getSource() == getArbitraryCheckBox())
            connEtoC1(e);
        if (e.getSource() == getGlueCheckBox())
            connEtoC4(e);
        if (e.getSource() == getSimpleRadioButton())
            connEtoC6(e);
        // user code begin {2}
        // user code end
    }

    /**
     * main entrypoint - starts the part when it is run as an application
     *
     * @param args java.lang.String[]
     */
    public static void main(java.lang.String[] args) {
        try {
            CardinalityCustomizer aCardinalityCustomizer;
            aCardinalityCustomizer = new CardinalityCustomizer();
            try {
                Class aCloserClass = Class.forName("com.ibm.uvm.abt.edit.WindowCloser");
                Class parmTypes[] = {java.awt.Window.class};
                Object parms[] = {aCardinalityCustomizer};
                java.lang.reflect.Constructor aCtor = aCloserClass.getConstructor(parmTypes);
                aCtor.newInstance(parms);
            } catch (java.lang.Throwable exc) {
            }
            aCardinalityCustomizer.setVisible(true);
        } catch (Throwable exception) {
            System.err.println("Exception occurred in main() of java.awt.Frame");
            exception.printStackTrace(System.out);
        }
    }

    /**
     * The removePropertyChangeListener method was generated to support the propertyChange field.
     */
    public synchronized void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
        getPropertyChange().removePropertyChangeListener(listener);
    }

    /**
     * Sets the name of the customized cardinality.
     *
     * @see cz.omnicom.ermodeller.conceptual.Cardinality
     */
    private void roleField_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
        getCardinality().setName(getRoleField().getText());
        getPropertyChange().firePropertyChange(ConceptualObject.NAME_PROPERTY_CHANGE, null, null);
    }

    /**
     * Sets object to be customized. It shoud be <code>Cardinality</code>.
     *
     * @param anObject java.lang.Object
     */
    public void setObject(Object anObject) {
        ivjCardinality = (Cardinality) anObject;
        getRoleField().setText(getCardinality().getName());
        boolean glue = getCardinality().getGlue();
        getArbitraryCheckBox().setSelected(getCardinality().getArbitrary());
        getGlueCheckBox().setEnabled(!getCardinality().getMultiCardinality());
        if (getCardinality().getMultiCardinality()) {
            getSimpleRadioButton().setSelected(false);
            getN_aryRadioButton().setSelected(true);
        } else {
            getSimpleRadioButton().setSelected(true);
            getN_aryRadioButton().setSelected(false);
        }
        getGlueCheckBox().setSelected(glue);
    }

    /**
     * Sets multiCardinality property of customized cardinality.
     *
     * @see cz.omnicom.ermodeller.conceptual.Cardinality
     */
    private void simpleRadioButton_ItemStateChanged(java.awt.event.ItemEvent itemEvent) {
        if (itemEvent.getStateChange() == ItemEvent.SELECTED) {

            getGlueCheckBox().setSelected(true);
            getGlueCheckBox().setEnabled(true);
            getCardinality().setMultiCardinality(false);
        } else {
            getGlueCheckBox().setSelected(false);
            getGlueCheckBox().setEnabled(false);
            getCardinality().setMultiCardinality(true);
        }
        getPropertyChange().firePropertyChange(Cardinality.MULTICARDINALITY_PROPERTY_CHANGE, null, null);
    }
}
