package cz.omnicom.ermodeller.typeseditor;

import cz.omnicom.ermodeller.datatype.DataType;
import cz.omnicom.ermodeller.datatype.DataTypePanel;
import cz.omnicom.ermodeller.datatype.NestedTableDataType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * This type was created by Aleš Kopecký.
 */
public class NestedTableDataTypePanel2 extends DataTypePanel implements ActionListener, PropertyChangeListener {

    private NestedTableDataType ivjNestedTableDataType = null;
    private JLabel ivjTypeLabel = null;
    private JButton typeButton = null;
    private VarrayNestedTypeEditor nestedTypeEditor = null;
    private UserTypeStorageVector userTypeStorageVector = null;

    /**
     * Constructor
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private NestedTableDataTypePanel2() {
        super();
        initialize();
    }

    public NestedTableDataTypePanel2(UserTypeStorageVector vector) {
        super();
        userTypeStorageVector = vector;
        initialize();
    }

    /**
     * Method to handle events for the ActionListener interface.
     *
     * @param e java.awt.event.ActionEvent
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    public void actionPerformed(java.awt.event.ActionEvent e) {
        if (e.getSource() == getTypeButton())
            connEtoM3(e);
        ((UserTypesEditorPanel) getDataTypeEditor()).getEditor().setActualType(getNestedTableDataType());
    }

    void connEtoM3(java.awt.event.ActionEvent arg1) {
        EditorDialog d = ((UserTypesEditorPanel) getDataTypeEditor()).getEditorDialog();
        d.setOKButtonVisible(false);
        d.setVisible(true);
        d.getContentPane().add(getNestedTypeEditor());
        d.paintComponents(d.getGraphics());
        //getNestedTypeEditor().updateJComboBox(userTypeStorageVector);
    }

    VarrayNestedTypeEditor getNestedTypeEditor() {
        if (nestedTypeEditor == null) {
            //System.out.println("VarrayTypeEditor byl null");
            nestedTypeEditor = new VarrayNestedTypeEditor(userTypeStorageVector);
            ((UserTypesEditorPanel) getDataTypeEditor()).getEditor().getTypesVector().addPropertyChangeListener(nestedTypeEditor);
            nestedTypeEditor.addPropertyChangeListener(this);
            nestedTypeEditor.addPropertyChangeListener(getNestedTableDataType());
            Dimension dimension = nestedTypeEditor.getPreferredSize();
            nestedTypeEditor.setBounds(0, 0, dimension.width, dimension.height);
        }
        return nestedTypeEditor;
    }

    /**
     * Return the LengthDataType property value.
     *
     * @return cz.omnicom.ermodeller.datatype.LengthDataType
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    NestedTableDataType getNestedTableDataType() {
        if (ivjNestedTableDataType == null) {
            try {
                ivjNestedTableDataType = new cz.omnicom.ermodeller.datatype.NestedTableDataType();
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjNestedTableDataType;
    }

    javax.swing.JLabel getTypeLabel() {
        if (ivjTypeLabel == null) {
            try {
                ivjTypeLabel = new javax.swing.JLabel();
                ivjTypeLabel.setName("TypeLabel");
                ivjTypeLabel.setText("Type of items:");
                ivjTypeLabel.setBounds(5, 5, 85, 15);
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjTypeLabel;
    }

    /**
     * This method was created by Aleš Kopecký.
     *
     * @return java.awt.Dimension
     */
    public Dimension getPreferredSize() {
        return new Dimension(140, 110);
    }

    JButton getTypeButton() {
        if (typeButton == null) {
            typeButton = new JButton();
            typeButton.setBounds(5, 26, 130, 25);
            typeButton.setText("Integer");
            typeButton.setToolTipText("Click to change datatype");
        }
        return typeButton;
    }

    /**
     * Called whenever the part throws an exception.
     *
     * @param exception java.lang.Throwable
     */
    void handleException(Throwable exception) {

        /* Uncomment the following lines to print uncaught exceptions to stdout */
        // System.out.println("--------- UNCAUGHT EXCEPTION ---------");
        // exception.printStackTrace(System.out);
    }

    /**
     * Initializes connections
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    void initConnections() {
        //System.out.println("initConnections()");
        getTypeButton().addActionListener(this);
    }

    /**
     * Initialize the class.
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    void initialize() {
        try {
            setName("NestedTableDataTypePanel");
            setLayout(null);
            setBackground(new java.awt.Color(204, 204, 204));
            add(getTypeLabel(), getTypeLabel().getName());
            add(getTypeButton(), getTypeButton().getName());
            initConnections();
        } catch (java.lang.Throwable ivjExc) {
            //System.out.println("exception!!");
            System.out.println(ivjExc.getMessage());
            ivjExc.printStackTrace();
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
            NestedTableDataTypePanel2 aNestedTableDataTypePanel;
            aNestedTableDataTypePanel = new NestedTableDataTypePanel2();
            frame.add("Center", aNestedTableDataTypePanel);
            frame.setSize(aNestedTableDataTypePanel.getSize());
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
        //System.out.println("propertyChange "+ anEvent.getPropertyName());
        if (anEvent.getPropertyName().equals(VarrayNestedTypeEditor.DATATYPE_PROPERTY_CHANGE)) {
            //	System.out.println("type changed to "+((DataType)anEvent.getNewValue()).toString());
            getTypeButton().setText(((DataType) anEvent.getNewValue()).toString());
            return;
        }
        // call inherited method
        firePropertyChange(anEvent.getPropertyName(), anEvent.getOldValue(), anEvent.getNewValue());
    }

    /**
     * This method was created by Aleš Kopecký.
     *
     * @param aDataType cz.omnicom.ermodeller.datatype.CharDataType
     */
    public void setNestedTableDataType(NestedTableDataType aDataType) {
        ivjNestedTableDataType = aDataType;
        ivjNestedTableDataType.addPropertyChangeListener(this);
        getTypeButton().setText(ivjNestedTableDataType.getType().toString());
        nestedTypeEditor = new VarrayNestedTypeEditor(userTypeStorageVector, ivjNestedTableDataType.getType());
        userTypeStorageVector.addPropertyChangeListener(nestedTypeEditor);
        nestedTypeEditor.addPropertyChangeListener(this);
        nestedTypeEditor.addPropertyChangeListener(ivjNestedTableDataType);
        Dimension dimension = nestedTypeEditor.getPreferredSize();
        nestedTypeEditor.setBounds(0, 0, dimension.width, dimension.height);
    }
}