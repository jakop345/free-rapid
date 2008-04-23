package cz.omnicom.ermodeller.typeseditor;

import cz.omnicom.ermodeller.datatype.*;
import cz.omnicom.ermodeller.datatype.editor.DataTypePanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class VarrayNestedTypeEditor extends TypeEditor implements PropertyChangeListener, ItemListener {

    public static final String DATATYPE_PROPERTY_CHANGE = "datatype_change";

    private JLabel typeLabel = null;
    private JComboBox ivjJComboBox = null;

    private DataType dataType = null;
    private DataTypePanel dataTypePanel = null;

    /*
     public VarrayNestedTypeEditor(){
         super();
         initialize();
     }
     */
    public VarrayNestedTypeEditor(UserTypeStorageVector utsv) {
        super();
        initialize();
        updateJComboBox(utsv);
        //System.out.println("VarrayNestedTypeEditor constructor 1");
    }

    public VarrayNestedTypeEditor(UserTypeStorageVector utsv, DataType dt) {
        super();
        initialize();
        updateJComboBox(utsv);
        setPanelContents(dt);
        //System.out.println("VarrayNestedTypeEditor constructor 2");
    }

    protected void initialize() {
        setLayout(null);
        //setBackground(java.awt.Color.red);
        add(getJComboBox());
        add(getTypeLabel());
        initConnections();
        integerSelected();
    }

    protected void initConnections() {
        getJComboBox().addItemListener(this);
    }

    private void setPanelContents(DataType dt) {
        getJComboBox().removeItemListener(this);
        if (dt instanceof UserDefinedDataType)
            getJComboBox().setSelectedItem(dt.toString());
        else if (dt instanceof IntegerDataType)
            getJComboBox().setSelectedItem("Integer");
        else if (dt instanceof DateDataType)
            getJComboBox().setSelectedItem("Date");
        else if (dt instanceof FloatDataType)
            getJComboBox().setSelectedItem("Float");
        else if (dt instanceof FixedCharDataType) {
            getJComboBox().setSelectedItem("Char");
            dataTypePanel = new LengthDataTypePanel3();
            ((LengthDataTypePanel3) dataTypePanel).setLengthDataType((LengthDataType) dt);
            dataTypePanel.setDataTypeEditor(this);
            Dimension dimension = dataTypePanel.getPreferredSize();
            dataTypePanel.setBounds(5, 50, dimension.width, dimension.height);
            dataTypePanel.addPropertyChangeListener(this);
            add(dataTypePanel);
        } else if (dt instanceof Varchar2DataType) {
            getJComboBox().setSelectedItem("Varchar2");
            dataTypePanel = new LengthDataTypePanel3();
            ((LengthDataTypePanel3) dataTypePanel).setLengthDataType((LengthDataType) dt);
            dataTypePanel.setDataTypeEditor(this);
            Dimension dimension = dataTypePanel.getPreferredSize();
            dataTypePanel.setBounds(5, 50, dimension.width, dimension.height);
            dataTypePanel.addPropertyChangeListener(this);
            add(dataTypePanel);
        } else if (dt instanceof GeneralNumberDataType) {
            getJComboBox().setSelectedItem("General number");
            dataTypePanel = new GeneralNumberDataTypePanel3();
            ((GeneralNumberDataTypePanel3) dataTypePanel).setGeneralNumberDataType((GeneralNumberDataType) dt);
            dataTypePanel.setDataTypeEditor(this);
            Dimension dimension = dataTypePanel.getPreferredSize();
            dataTypePanel.setBounds(5, 50, dimension.width, dimension.height);
            dataTypePanel.addPropertyChangeListener(this);
            add(dataTypePanel);
        }
        getJComboBox().addItemListener(this);
    }

    public Dimension getPreferredSize() {
        return new Dimension(200, 200);
    }

    protected JLabel getTypeLabel() {
        if (typeLabel == null) {
            typeLabel = new JLabel("Type of items");
            typeLabel.setBounds(5, 0, 105, 15);
        }
        return typeLabel;
    }

    protected JComboBox getJComboBox() {
        if (ivjJComboBox == null) {
            ivjJComboBox = new JComboBox();
            ivjJComboBox.setToolTipText("Select type");
            ivjJComboBox.setBounds(5, 18, 125, 25);
            ivjJComboBox.addItem("Integer");
            ivjJComboBox.addItem("Char");
            ivjJComboBox.addItem("Varchar2");
            ivjJComboBox.addItem("Float");
            ivjJComboBox.addItem("General number");
            ivjJComboBox.addItem("Date");
            ivjJComboBox.setMaximumRowCount(6);
        }
        return ivjJComboBox;
    }

    private void updateJComboBox(UserTypeStorageVector u) {
        DataType dt;
        for (int i = 0; i < u.getSize(); i++) {
            dt = u.getTypeAt(i).getDataType();
            if (!((dt instanceof NestedTableDataType) ||
                    (dt instanceof VarrayDataType) ||
                    ((dt instanceof ObjectDataType) &&
                            (((ObjectDataType) dt).containsNested() || ((ObjectDataType) dt).containsVarray()))))
                getJComboBox().addItem(u.getTypeAt(i).getTypeName());
        }
    }

    private void addToJComboBox(UserTypeStorage uts) {
        DataType dt = uts.getDataType();

        if (!((dt instanceof NestedTableDataType) ||
                (dt instanceof VarrayDataType) ||
                ((dt instanceof ObjectDataType) &&
                        (((ObjectDataType) dt).containsNested() || ((ObjectDataType) dt).containsVarray()))))
            getJComboBox().addItem(uts.getTypeName());
    }

    private void removeFromJComboBox(UserTypeStorage uts) {
        if (!(uts.getDataType() instanceof NestedTableDataType) &&
                !(uts.getDataType() instanceof VarrayDataType)) {
            if (getJComboBox().getSelectedItem().equals(uts.getTypeName())) {
                getJComboBox().setSelectedItem("Integer");
                integerSelected();
            }
            getJComboBox().removeItem(uts.getTypeName());
        }
    }

    public void itemStateChanged(ItemEvent e) {
        if (getJComboBox().getSelectedItem().equals("Integer"))
            integerSelected();
        else if (getJComboBox().getSelectedItem().equals("Date"))
            dateSelected();
        else if (getJComboBox().getSelectedItem().equals("Float"))
            floatSelected();
        else if (getJComboBox().getSelectedItem().equals("General number"))
            generalNumberSelected();
        else if (getJComboBox().getSelectedItem().equals("Varchar2"))
            varchar2Selected();
        else if (getJComboBox().getSelectedItem().equals("Char"))
            charSelected();
        else
            userDefinedSelected();
        repaint();
        getPropertyChange().firePropertyChange(DATATYPE_PROPERTY_CHANGE, null, dataType);
    }

    public void propertyChange(PropertyChangeEvent e) {
        if ((e.getPropertyName().equals(GeneralNumberDataTypePanel3.TYPE_CHANGED)) ||
                (e.getPropertyName().equals(LengthDataTypePanel3.LENGTH_TYPE_CHANGED))) {
            getPropertyChange().firePropertyChange(DATATYPE_PROPERTY_CHANGE, null, e.getNewValue());
            return;
        }
        if (e.getPropertyName().equals(UserTypeStorageVector.ADD_PROPERTYCHANGE)) {
            addToJComboBox((UserTypeStorage) e.getNewValue());
            return;
        }
        if (e.getPropertyName().equals(UserTypeStorageVector.REMOVE_PROPERTYCHANGE)) {
            removeFromJComboBox((UserTypeStorage) e.getOldValue());
        }
    }

    protected void generalNumberSelected() {
        dataType = new GeneralNumberDataType();
        if (dataTypePanel != null)
            remove(dataTypePanel);
        dataTypePanel = new GeneralNumberDataTypePanel3();
        ((GeneralNumberDataTypePanel3) dataTypePanel).setGeneralNumberDataType((GeneralNumberDataType) dataType);
        dataTypePanel.setDataTypeEditor(this);
        Dimension dimension = dataTypePanel.getPreferredSize();
        dataTypePanel.setBounds(5, 50, dimension.width, dimension.height);
        dataTypePanel.addPropertyChangeListener(this);
        add(dataTypePanel);
    }

    protected void charSelected() {
        dataType = new FixedCharDataType();
        if (dataTypePanel != null)
            remove(dataTypePanel);
        dataTypePanel = new LengthDataTypePanel3();
        ((LengthDataTypePanel3) dataTypePanel).setLengthDataType((LengthDataType) dataType);
        dataTypePanel.setDataTypeEditor(this);
        Dimension dimension = dataTypePanel.getPreferredSize();
        dataTypePanel.setBounds(5, 50, dimension.width, dimension.height);
        dataTypePanel.addPropertyChangeListener(this);
        add(dataTypePanel);
    }

    protected void varchar2Selected() {
        dataType = new Varchar2DataType();
        if (dataTypePanel != null)
            remove(dataTypePanel);
        dataTypePanel = new LengthDataTypePanel3();
        ((LengthDataTypePanel3) dataTypePanel).setLengthDataType((LengthDataType) dataType);
        dataTypePanel.setDataTypeEditor(this);
        Dimension dimension = dataTypePanel.getPreferredSize();
        dataTypePanel.setBounds(5, 50, dimension.width, dimension.height);
        dataTypePanel.addPropertyChangeListener(this);
        add(dataTypePanel);
    }

    protected void dateSelected() {
        dataType = new DateDataType();
        if (dataTypePanel != null)
            remove(dataTypePanel);
    }

    protected void floatSelected() {
        dataType = new FloatDataType();
        if (dataTypePanel != null)
            remove(dataTypePanel);
    }

    protected void userDefinedSelected() {
        dataType = new UserDefinedDataType((String) (getJComboBox().getSelectedItem()));
        if (dataTypePanel != null)
            remove(dataTypePanel);
    }

    protected void integerSelected() {
        dataType = new IntegerDataType();
        if (dataTypePanel != null)
            remove(dataTypePanel);
    }
}