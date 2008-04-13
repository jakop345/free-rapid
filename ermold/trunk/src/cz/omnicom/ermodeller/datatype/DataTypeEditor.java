package cz.omnicom.ermodeller.datatype;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyEditor;

/**
 * Property editor for the <code>DataType</code> beans.
 */
public class DataTypeEditor extends JPanel implements java.awt.event.ActionListener, PropertyEditor {
    /**
     * Datatype to be edited.
     *
     * @see cz.omnicom.ermodeller.datatype.DataType
     */
    private DataType dataType = null;
    /**
     * Panel which customizes the <code>dataType</code>.
     *
     * @see cz.omnicom.ermodeller.datatype.DataTypePanel
     */
    private DataTypePanel dataTypePanel = null;
    protected transient PropertyChangeSupport propertyChange;
    private JPanel ivjDataTypePanel = null;
    private JRadioButton ivjDateRadioButton = null;
    private JRadioButton ivjFixedStringRadioButton = null;
    private JRadioButton ivjFloatRadioButton = null;
    private JRadioButton ivjGeneralNumberRadioButton = null;
    private JRadioButton ivjIntegerRadioButton = null;
    private JRadioButton ivjVariableStringRadioButton = null;
    //Steve
    private JRadioButton ivjUserTypeRadioButton = null;

    /**
     * Constructor
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    public DataTypeEditor() {
        super();
        initialize();
    }

    /**
     * DataTypeEditor constructor comment.
     *
     * @param layout java.awt.LayoutManager
     */
    public DataTypeEditor(java.awt.LayoutManager layout) {
        super(layout);
    }

    /**
     * DataTypeEditor constructor comment.
     *
     * @param layout           java.awt.LayoutManager
     * @param isDoubleBuffered boolean
     */
    public DataTypeEditor(java.awt.LayoutManager layout, boolean isDoubleBuffered) {
        super(layout, isDoubleBuffered);
    }

    /**
     * DataTypeEditor constructor comment.
     *
     * @param isDoubleBuffered boolean
     */
    public DataTypeEditor(boolean isDoubleBuffered) {
        super(isDoubleBuffered);
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
        if (e.getSource() == getFixedStringRadioButton())
            connEtoC1(e);
        if (e.getSource() == getVariableStringRadioButton())
            connEtoC2();
        if (e.getSource() == getIntegerRadioButton())
            connEtoC3();
        if (e.getSource() == getFloatRadioButton())
            connEtoC4();
        if (e.getSource() == getGeneralNumberRadioButton())
            connEtoC5();
        if (e.getSource() == getDateRadioButton())
            connEtoC6();
        //Steve
        if (e.getSource() == getUserTypeRadioButton())
            connEtoC7();
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
     * connEtoC1:  (FixedStringRadioButton.action.actionPerformed(java.awt.event.ActionEvent) -->
     * DataTypeEditor.fixedStringRadioButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
     *
     * @param arg1 java.awt.event.ActionEvent
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private void connEtoC1(java.awt.event.ActionEvent arg1) {
        try {
            // user code begin {1}
            // user code end
            this.fixedStringRadioButton_ActionPerformed(arg1);
            // user code begin {2}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connEtoC2:  (VariableStringRadioButton.action. --> DataTypeEditor.variableStringRadioButton_ActionEvents()V)
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private void connEtoC2() {
        try {
            // user code begin {1}
            // user code end
            this.variableStringRadioButton_ActionEvents();
            // user code begin {2}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connEtoC3:  (IntegerRadioButton.action. --> DataTypeEditor.integerRadioButton_ActionEvents()V)
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private void connEtoC3() {
        try {
            // user code begin {1}
            // user code end
            this.integerRadioButton_ActionEvents();
            // user code begin {2}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connEtoC4:  (FloatRadioButton.action. --> DataTypeEditor.floatRadioButton_ActionEvents()V)
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private void connEtoC4() {
        try {
            // user code begin {1}
            // user code end
            this.floatRadioButton_ActionEvents();
            // user code begin {2}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connEtoC5:  (GeneralNumberRadioButton.action. --> DataTypeEditor.generalNumberRadioButton_ActionEvents()V)
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private void connEtoC5() {
        try {
            // user code begin {1}
            // user code end
            this.generalNumberRadioButton_ActionEvents();
            // user code begin {2}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connEtoC6:  (DateRadioButton.action. --> DataTypeEditor.dateRadioButton_ActionEvents()V)
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private void connEtoC6() {
        try {
            // user code begin {1}
            // user code end
            this.dateRadioButton_ActionEvents();
            // user code begin {2}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    //Steve
/**
 * connEtoC7:  (UserTypeRadioButton.action. --> DataTypeEditor.userTypeRadioButton_ActionEvents()V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private void connEtoC7() {
        try {
            // user code begin {1}
            // user code end
            this.userTypeRadioButton_ActionEvents();
            // user code begin {2}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * Change datatype to <code>DateDataType</code>.
     *
     * @see cz.omnicom.ermodeller.datatype.DateDataType
     */
    private void dateRadioButton_ActionEvents() {
        if (!(dataType instanceof DateDataType)) {
            dataType = new DateDataType();
            changeDataType(dataType);
        }
    }

    /**
     * Change datatype to <code>Varchar2DataType</code>.
     *
     * @see cz.omnicom.ermodeller.datatype.Varchar2DataType
     */
    private void variableStringRadioButton_ActionEvents() {
        if (!(dataType instanceof Varchar2DataType)) {
            dataType = new Varchar2DataType();
            changeDataType(dataType);
        }
    }

    /**
     * Change datatype to <code>FixedCharDataType</code>.
     *
     * @see cz.omnicom.ermodeller.datatype.FixedCharDataType
     */
    private void fixedStringRadioButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
        if (!(dataType instanceof FixedCharDataType)) {
            dataType = new FixedCharDataType();
            changeDataType(dataType);
        }
    }

    /**
     * Change datatype to <code>FloatDataType</code>.
     *
     * @see cz.omnicom.ermodeller.datatype.FloatDataType
     */
    private void floatRadioButton_ActionEvents() {
        if (!(dataType instanceof FloatDataType)) {
            dataType = new FloatDataType();
            changeDataType(dataType);
        }
    }

    /**
     * Change datatype to <code>IntegerDataType</code>.
     *
     * @see cz.omnicom.ermodeller.datatype.IntegerDataType
     */
    private void integerRadioButton_ActionEvents() {
        if (!(dataType instanceof IntegerDataType)) {
            dataType = new IntegerDataType();
            changeDataType(dataType);
        }
    }

    /**
     * Change datatype to <code>GeneralNumberDataType</code>.
     *
     * @see cz.omnicom.ermodeller.datatype.GeneralNumberDataType
     */
    private void generalNumberRadioButton_ActionEvents() {
        if (!(dataType instanceof GeneralNumberDataType)) {
            dataType = new GeneralNumberDataType();
            changeDataType(dataType);
        }
    }

    //Steve
/**
 * Change datatype to some UserDefinedDataType.
 *
 * @see cz.omnicom.ermodeller.datatype.UserDefinedDataType
 */
    private void userTypeRadioButton_ActionEvents() {
        if (!(dataType instanceof UserDefinedDataType)) {
            dataType = new UserDefinedDataType();
            changeDataType(dataType);
        }
    }

    /**
     * The firePropertyChange method was generated to support the propertyChange field.
     */
    public void firePropertyChange() {
        getPropertyChange().firePropertyChange("", null, null);
    }

    /**
     * Returns string representation of the current datatype.
     *
     * @see cz.omnicom.ermodeller.datatype.DataType#toString
     */
    public String getAsText() {
        return ((DataType) getValue()).toDescriptionString();
    }

    /**
     * Returns this editor as a <code>Component</code>.
     *
     * @returns java.awt.Component
     */
    public Component getCustomEditor() {
        return this;
    }

    /**
     * Return the DataTypePanel property value.
     *
     * @return javax.swing.JPanel
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JPanel getDataTypePanel() {
        if (ivjDataTypePanel == null) {
            try {
                ivjDataTypePanel = new javax.swing.JPanel();
                ivjDataTypePanel.setName("DataTypePanel");
                ivjDataTypePanel.setOpaque(true);
                ivjDataTypePanel.setBorder(getDataTypePanelGroupBox());
                ivjDataTypePanel.setLayout(null);
                ivjDataTypePanel.setBackground(new java.awt.Color(204, 204, 204));
                ivjDataTypePanel.setBounds(5, 5, 151, 223);
                ivjDataTypePanel.setEnabled(true);
                getDataTypePanel().add(getFixedStringRadioButton(), getFixedStringRadioButton().getName());
                getDataTypePanel().add(getVariableStringRadioButton(), getVariableStringRadioButton().getName());
                getDataTypePanel().add(getIntegerRadioButton(), getIntegerRadioButton().getName());
                getDataTypePanel().add(getFloatRadioButton(), getFloatRadioButton().getName());
                getDataTypePanel().add(getGeneralNumberRadioButton(), getGeneralNumberRadioButton().getName());
                getDataTypePanel().add(getDateRadioButton(), getDateRadioButton().getName());
                getDataTypePanel().add(getUserTypeRadioButton(), getUserTypeRadioButton().getName());
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjDataTypePanel;
    }

    /**
     * Return the DataTypePanelGroupBox property value.
     *
     * @return com.ibm.ivj.examples.vc.utilitybeans.GroupBox
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private Border getDataTypePanelGroupBox() {
        return BorderFactory.createTitledBorder("Data type");
    }

    /**
     * Return the DateRadioButton property value.
     *
     * @return javax.swing.JRadioButton
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JRadioButton getDateRadioButton() {
        if (ivjDateRadioButton == null) {
            try {
                ivjDateRadioButton = new javax.swing.JRadioButton();
                ivjDateRadioButton.setName("DateRadioButton");
                ivjDateRadioButton.setToolTipText("Date value");
                ivjDateRadioButton.setText(" Date");
                ivjDateRadioButton.setBounds(14, 158, 120, 25);
                ivjDateRadioButton.setActionCommand("Date");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjDateRadioButton;
    }

    /**
     * Return the FixedStringRadioButton property value.
     *
     * @return javax.swing.JRadioButton
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JRadioButton getFixedStringRadioButton() {
        if (ivjFixedStringRadioButton == null) {
            try {
                ivjFixedStringRadioButton = new javax.swing.JRadioButton();
                ivjFixedStringRadioButton.setName("FixedStringRadioButton");
                ivjFixedStringRadioButton.setToolTipText("String data type with fixed length ");
                ivjFixedStringRadioButton.setText(" Fixed string");
                ivjFixedStringRadioButton.setBounds(14, 23, 120, 25);
                ivjFixedStringRadioButton.setActionCommand("Fixed string");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjFixedStringRadioButton;
    }

    /**
     * Return the FloatRadioButton property value.
     *
     * @return javax.swing.JRadioButton
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JRadioButton getFloatRadioButton() {
        if (ivjFloatRadioButton == null) {
            try {
                ivjFloatRadioButton = new javax.swing.JRadioButton();
                ivjFloatRadioButton.setName("FloatRadioButton");
                ivjFloatRadioButton.setToolTipText("Floating point number");
                ivjFloatRadioButton.setText(" Float");
                ivjFloatRadioButton.setBounds(14, 104, 120, 25);
                ivjFloatRadioButton.setActionCommand("Float");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjFloatRadioButton;
    }

    /**
     * Return the GeneralNumberRadioButton property value.
     *
     * @return javax.swing.JRadioButton
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JRadioButton getGeneralNumberRadioButton() {
        if (ivjGeneralNumberRadioButton == null) {
            try {
                ivjGeneralNumberRadioButton = new javax.swing.JRadioButton();
                ivjGeneralNumberRadioButton.setName("GeneralNumberRadioButton");
                ivjGeneralNumberRadioButton.setToolTipText("Number with precission and scale");
                ivjGeneralNumberRadioButton.setText(" General number");
                ivjGeneralNumberRadioButton.setBounds(14, 131, 120, 25);
                ivjGeneralNumberRadioButton.setActionCommand("General number");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjGeneralNumberRadioButton;
    }

    /**
     * Return the IntegerRadioButton property value.
     *
     * @return javax.swing.JRadioButton
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JRadioButton getIntegerRadioButton() {
        if (ivjIntegerRadioButton == null) {
            try {
                ivjIntegerRadioButton = new javax.swing.JRadioButton();
                ivjIntegerRadioButton.setName("IntegerRadioButton");
                ivjIntegerRadioButton.setToolTipText("Integer number");
                ivjIntegerRadioButton.setText(" Integer");
                ivjIntegerRadioButton.setBounds(14, 77, 120, 25);
                ivjIntegerRadioButton.setActionCommand("Integer");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjIntegerRadioButton;
    }

    /**
     * Return the VariableStringRadioButton property value.
     *
     * @return javax.swing.JRadioButton
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JRadioButton getVariableStringRadioButton() {
        if (ivjVariableStringRadioButton == null) {
            try {
                ivjVariableStringRadioButton = new javax.swing.JRadioButton();
                ivjVariableStringRadioButton.setName("VariableStringRadioButton");
                ivjVariableStringRadioButton.setToolTipText("String data type with variable length from 1 up to defined length");
                ivjVariableStringRadioButton.setText(" Variable string");
                ivjVariableStringRadioButton.setBounds(14, 50, 120, 25);
                ivjVariableStringRadioButton.setActionCommand("Variable string");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjVariableStringRadioButton;
    }

    /**
     * Return the UserTypeRadioButton property value.
     *
     * @return javax.swing.JRadioButton
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JRadioButton getUserTypeRadioButton() {
        if (ivjUserTypeRadioButton == null) {
            try {
                ivjUserTypeRadioButton = new javax.swing.JRadioButton();
                ivjUserTypeRadioButton.setName("UserTypeRadioButton");
                ivjUserTypeRadioButton.setToolTipText("User defined data type");
                ivjUserTypeRadioButton.setText(" User defined");
                ivjUserTypeRadioButton.setBounds(14, 185, 120, 25);
                ivjUserTypeRadioButton.setActionCommand("User defined");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjUserTypeRadioButton;
    }

    /**
     * Not implemented.
     *
     * @return null
     */
    public String getJavaInitializationString() {
        return null;
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
     * Property editor does not implement tags.
     *
     * @return null
     */
    public String[] getTags() {
        return null;
    }

    /**
     * Gets current datatype.
     */
    public Object getValue() {
        return dataType;
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
     * Sets <code>aDataType</code> to be edited. This implies to remove current datatype panel and to create and add new
     * one, then repaint.
     *
     * @param aDataType cz.omnicom.ermodeller.datatype.DataType
     */
    private void changeDataType(DataType aDataType) {
        //System.out.println("hlasi se changeDataType!");
        synchronized (this) {
            // clear
            if (dataTypePanel != null)
                remove(dataTypePanel);
            if (dataType != null && dataTypePanel != null)
                dataType.removePropertyChangeListener((PropertyChangeListener) dataTypePanel);

            // set
            dataType = aDataType;
            dataTypePanel = dataType.getPanel();
            if (dataTypePanel != null) {
                dataTypePanel.setDataTypeEditor(this);
                dataTypePanel.setLayout(null);
                Dimension dimension = dataTypePanel.getPreferredSize();
                dataTypePanel.setBounds(156, 10, dimension.width, dimension.height);
                dataTypePanel.setEnabled(true);
                add(dataTypePanel);
                dataTypePanel.paintComponents(dataTypePanel.getGraphics());
            }
            repaint();
        }
        // propertyChange
        firePropertyChange();
    }

    /**
     * Initializes connections
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private void initConnections() {
        // user code begin {1}
        // user code end
        getFixedStringRadioButton().addActionListener(this);
        getVariableStringRadioButton().addActionListener(this);
        getIntegerRadioButton().addActionListener(this);
        getFloatRadioButton().addActionListener(this);
        getGeneralNumberRadioButton().addActionListener(this);
        getDateRadioButton().addActionListener(this);
        //Steve
        getUserTypeRadioButton().addActionListener(this);
    }

    /**
     * Initialize the class.
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private void initialize() {
        try {
            setName("DataTypeEditor");
            setPreferredSize(new java.awt.Dimension(271, 238));
            setLayout(null);
            setSize(271, 238);
            add(getDataTypePanel(), getDataTypePanel().getName());
/*		closeButton = new JButton("Close");
        closeButton.setBounds(new Rectangle(173, 202, 83, 25));
        closeButton.addActionListener(new ActionListener(){
    		public void actionPerformed(ActionEvent e) {
    			setVisible(false);
    		}});
       add(closeButton, closeButton.getName());
*/
            initConnections();
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
        // user code begin {2}
        ButtonGroup group = new ButtonGroup();
        group.add(getDateRadioButton());
        group.add(getFixedStringRadioButton());
        group.add(getVariableStringRadioButton());
        group.add(getFloatRadioButton());
        group.add(getIntegerRadioButton());
        group.add(getGeneralNumberRadioButton());
        group.add(getUserTypeRadioButton());
        // user code end
    }

    /**
     * @return false.
     */
    public boolean isPaintable() {
        return false;
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
            DataTypeEditor aDataTypeEditor;
            aDataTypeEditor = new DataTypeEditor();
            frame.add("Center", aDataTypeEditor);
            frame.setSize(300, 300);
            //frame.setSize(aDataTypeEditor.getSize());
            frame.setVisible(true);
            frame.paintComponents(frame.getGraphics());
        } catch (Throwable exception) {
            System.err.println("Exception occurred in main() of javax.swing.JPanel");
            exception.printStackTrace(System.out);
        }
    }

    /**
     * Do noithing (is not paintable).
     *
     * @see #isPaintable
     */
    public void paintValue(java.awt.Graphics gfx, java.awt.Rectangle box) {
    }

    /**
     * The removePropertyChangeListener method was generated to support the propertyChange field.
     */
    public synchronized void removePropertyChangeListener(PropertyChangeListener listener) {
        getPropertyChange().removePropertyChangeListener(listener);
    }

    /**
     * Do nothing - does not support setting as text.
     */
    public void setAsText(String text) throws IllegalArgumentException {
    }

    /**
     * Set initial state of radio buttons of the editor regarding to current datatype.
     */
    private void setInitRadioButtonsState() {
        if (getValue() instanceof FixedCharDataType) {
            getFixedStringRadioButton().setSelected(true);
            return;
        }
        if (getValue() instanceof Varchar2DataType) {
            getVariableStringRadioButton().setSelected(true);
            return;
        }
        if (getValue() instanceof IntegerDataType) {
            getIntegerRadioButton().setSelected(true);
            return;
        }
        if (getValue() instanceof FloatDataType) {
            getFloatRadioButton().setSelected(true);
            return;
        }
        if (getValue() instanceof GeneralNumberDataType) {
            getGeneralNumberRadioButton().setSelected(true);
            return;
        }
        if (getValue() instanceof DateDataType) {
            getDateRadioButton().setSelected(true);
            return;
        }
        //Steve
        if (getValue() instanceof UserDefinedDataType) {
            getUserTypeRadioButton().setSelected(true);
            /*tady asi pridat zvoleni spravne polozky do comba*/
        }
    }

    /**
     * Sets the datatype to be edited.
     *
     * @param java.lang.Object value
     */
    public synchronized void setValue(Object value) {
        Object oldValue = getValue();
        changeDataType((DataType) value);
        if (oldValue == null)
            setInitRadioButtonsState();
    }

    /**
     * supportsCustomEditor method comment.
     */
    public boolean supportsCustomEditor() {
        return true;
    }

}