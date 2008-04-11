package cz.omnicom.ermodeller.typeseditor;

import cz.omnicom.ermodeller.datatype.*;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class ObjectTypeEditor extends TypeEditor implements PropertyChangeListener, ItemListener, DocumentListener {

    //public static final String OK_PROPERTY_CHANGE="OK_prop_change";
    public static final String DATATYPE_PROPERTY_CHANGE = "datatype_prop_change";
    public static final String NAME_PROPERTY_CHANGE = "name_prop_change";
    public static final String CONFIRM_NAME_PROPERTY_CHANGE = "confirm_name_prop_change";
    protected static final char[] LEGAL_CHARS = {'_', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0'};
    /**
     * number of instances already created
     */
    private static int instances = 0;

    private JLabel typeLabel = null;
    private JLabel nameLabel = null;
    private JComboBox ivjJComboBox = null;
    private JTextField nameTextField = null;
    private JLabel duplicityLabel = null;
    //private JButton OKButton = null;

    private DataType dataType = null;
    private DataTypePanel dataTypePanel = null;
    private ObjectDataType objectDataType = null;
    private final String defaultItemName;

    public ObjectTypeEditor(UserTypeStorageVector utsv, ObjectDataType odt) {
        super();
        //setBackground(java.awt.Color.red);
        defaultItemName = "Item" + instances;
        instances++;
        initialize();
        objectDataType = odt;
        updateJComboBox(utsv);
    }

    protected void initialize() {
        setLayout(null);
        addItems();
        initConnections();
        integerSelected();
    }

    protected void addItems() {
        add(getNameLabel());
        add(getNameTextField());
        add(getJComboBox());
        add(getTypeLabel());
        add(getDuplicityLabel());
    }

    protected void initConnections() {
        getJComboBox().addItemListener(this);
        getNameTextField().getDocument().addDocumentListener(this);
    }

    public boolean confirmName() {
        String aName = getNameTextField().getText();
        if (aName.trim().equals("")) {
            Object[] options = {"OK"};
            String[] message = {"You have to specify item name!!"};
            JOptionPane.showOptionDialog(this, message, "ERROR", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, null, options, options[0]);
            return false;
        }
        for (int i = 0, j; i < aName.length(); i++) {
            boolean found = false;
            for (j = 0; j < LEGAL_CHARS.length && !found; j++) {
                found = (aName.charAt(i) == LEGAL_CHARS[j]);
            }
            if (!found) {
                Object[] options = {"OK"};
                String[] message = {"Illegal character '" + aName.charAt(i) + "' in item name!!"};
                JOptionPane.showOptionDialog(this, message, "ERROR", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, null, options, options[0]);
                return false;
            }
        }
        if (getDuplicityLabel().isVisible()) {
            Object[] options = {"OK"};
            String[] message = {"Item name duplicity in this object!!"};
            JOptionPane.showOptionDialog(this, message, "ERROR", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, null, options, options[0]);
        } else {
            getPropertyChange().firePropertyChange(NAME_PROPERTY_CHANGE, null, getNameTextField().getText());
            getPropertyChange().firePropertyChange(DATATYPE_PROPERTY_CHANGE, null, dataType);
            getPropertyChange().firePropertyChange(CONFIRM_NAME_PROPERTY_CHANGE, null, null);
        }
        return !getDuplicityLabel().isVisible();//true if name is correct
    }

    public String getName() {
        return getNameTextField().getText();
    }

    public String getTypeName() {
        return dataType.toString();
    }

    public void setContents(UserTypeStorage u) {
        DataType dt = u.getDataType();
        getNameTextField().setText(u.getTypeName());
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
            dataTypePanel.setBounds(5, 80, dimension.width, dimension.height);
            dataTypePanel.addPropertyChangeListener(this);
            add(dataTypePanel);
        } else if (dt instanceof Varchar2DataType) {
            getJComboBox().setSelectedItem("Varchar2");
            dataTypePanel = new LengthDataTypePanel3();
            ((LengthDataTypePanel3) dataTypePanel).setLengthDataType((LengthDataType) dt);
            dataTypePanel.setDataTypeEditor(this);
            Dimension dimension = dataTypePanel.getPreferredSize();
            dataTypePanel.setBounds(5, 80, dimension.width, dimension.height);
            dataTypePanel.addPropertyChangeListener(this);
            add(dataTypePanel);
        } else if (dt instanceof GeneralNumberDataType) {
            getJComboBox().setSelectedItem("General number");
            dataTypePanel = new GeneralNumberDataTypePanel3();
            ((GeneralNumberDataTypePanel3) dataTypePanel).setGeneralNumberDataType((GeneralNumberDataType) dt);
            dataTypePanel.setDataTypeEditor(this);
            Dimension dimension = dataTypePanel.getPreferredSize();
            dataTypePanel.setBounds(5, 80, dimension.width, dimension.height);
            dataTypePanel.addPropertyChangeListener(this);
            add(dataTypePanel);
        }
        getJComboBox().addItemListener(this);
        dataType = dt;
    }


    public Dimension getPreferredSize() {
        return new Dimension(200, 205);
    }

    protected JLabel getNameLabel() {
        if (nameLabel == null) {
            nameLabel = new JLabel("Name of item");
            nameLabel.setBounds(5, 0, 80, 15);
        }
        return nameLabel;
    }

    protected JLabel getDuplicityLabel() {
        if (duplicityLabel == null) {
            duplicityLabel = new JLabel("DUPLICITY !!!");
            duplicityLabel.setVisible(false);
            duplicityLabel.setBounds(112, 17, 80, 20);
            duplicityLabel.setForeground(Color.red);
        }
        return duplicityLabel;
    }

    protected JTextField getNameTextField() {
        if (nameTextField == null) {
            nameTextField = new JTextField();
            nameTextField.setBounds(5, 17, 105, 20);
            nameTextField.setText(defaultItemName);
        }
        return nameTextField;
    }

    protected JLabel getTypeLabel() {
        if (typeLabel == null) {
            typeLabel = new JLabel("Type of items");
            typeLabel.setBounds(5, 40, 105, 15);
        }
        return typeLabel;
    }

    protected JComboBox getJComboBox() {
        if (ivjJComboBox == null) {
            ivjJComboBox = new JComboBox();
            ivjJComboBox.setToolTipText("Select type");
            ivjJComboBox.setBounds(5, 57, 125, 20);
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

    public void showDuplicityWarning() {
        getDuplicityLabel().setVisible(true);
    }

    public void hideDuplicityWarning() {
        getDuplicityLabel().setVisible(false);
    }

    private void updateJComboBox(UserTypeStorageVector vector) {
        if (vector != null)
            for (int i = 0; i < vector.getSize(); i++) {
                if (objectDataType != vector.getTypeAt(i).getDataType())
                    getJComboBox().addItem(vector.getTypeAt(i).getTypeName());
            }
    }

    public void addToJComboBox(UserTypeStorage uts) {
        if (objectDataType != uts.getDataType())
            getJComboBox().addItem(uts.getTypeName());
    }

    public void removeFromJComboBox(UserTypeStorage uts) {
        if (getJComboBox().getSelectedItem().equals(uts.getTypeName())) {
            getJComboBox().setSelectedItem("Integer");
            integerSelected();
        }
        getJComboBox().removeItem(uts.getTypeName());
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
            dataType = (DataType) e.getNewValue();
            getPropertyChange().firePropertyChange(DATATYPE_PROPERTY_CHANGE, null, dataType);
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
        dataTypePanel.setBounds(5, 80, dimension.width, dimension.height);
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
        dataTypePanel.setBounds(5, 80, dimension.width, dimension.height);
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
        dataTypePanel.setBounds(5, 80, dimension.width, dimension.height);
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

    public void changedUpdate(DocumentEvent e) {
        getPropertyChange().firePropertyChange(NAME_PROPERTY_CHANGE, null, getNameTextField().getText());
    }

    public void insertUpdate(DocumentEvent e) {
        getPropertyChange().firePropertyChange(NAME_PROPERTY_CHANGE, null, getNameTextField().getText());
    }

    public void removeUpdate(DocumentEvent e) {
        getPropertyChange().firePropertyChange(NAME_PROPERTY_CHANGE, null, getNameTextField().getText());
    }
}