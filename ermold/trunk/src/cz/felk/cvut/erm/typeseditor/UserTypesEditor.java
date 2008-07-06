package cz.felk.cvut.erm.typeseditor;

import cz.felk.cvut.erm.datatype.*;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Enumeration;

public class UserTypesEditor extends JDialog implements ItemListener, PropertyChangeListener {

    protected JButton buttOK = null;
    protected JButton buttEdit = null;
    protected JButton buttDelete = null;
    protected JTextField nameTextField = null;
    protected JComboBox typeComboBox = null;
    protected JLabel nameLabel = null;
    protected JLabel editLabel = null;
    protected JRadioButton newRadioButton = null;
    protected JRadioButton editRadioButton = null;
    protected ButtonGroup buttonGroup = null;
    protected JCheckBox checkBox = null;

    private boolean creating = true;
    private boolean objectDuplicity = false;
    private boolean showDialog = true;
    private boolean changed = false;

    private UserTypesEditor editor = null; //used for JOptionPane
    /**
     * list of all user defined types
     */
    protected UserTypeStorageVector typesVector = null;
    protected UserTypesEditorPanel userTypesEditorPanel = null;

    protected JDialog chooseDialog = null;
    protected UserTypeStorage actualUserTypeStorage = null;
    protected DataType actualType = null;

    protected static final char[] LEGAL_CHARS = {'_', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0'};

    final ChangeListener newButtonListener = new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
            //System.out.println("new changed!");
            if (getNewRadioButton().isSelected() && !creating) {
                creating = true;
                getTypeComboBox().setEnabled(false);
                getButtDelete().setEnabled(false);
                getNameTextField().setEnabled(true);
                getNameTextField().setCaret(getNameTextField().getCaret());
                getContentPane().remove(getUserTypesEditorPanel());
                userTypesEditorPanel = null;
                getContentPane().add(getUserTypesEditorPanel());
                getUserTypesEditorPanel().setVisible(true);
                getUserTypesEditorPanel().repaint();
            }
        }
    };

    final ChangeListener editButtonListener = new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
            //System.out.println("edit changed!");
            if (getEditRadioButton().isSelected()) {
                creating = false;
                changed = true;
                getTypeComboBox().setEnabled(true);
                if (getTypeComboBox().getItemCount() > 0) {
                    getButtDelete().setEnabled(true);
                    actualUserTypeStorage = getTypesVector().getTypeAt(getTypeComboBox().getSelectedIndex());
                    setActualType(actualUserTypeStorage.getDataType());
                    getContentPane().remove(getUserTypesEditorPanel());
                    userTypesEditorPanel = actualUserTypeStorage.getPanel();
                    getContentPane().add(userTypesEditorPanel);
                    userTypesEditorPanel.setVisible(true);
                    userTypesEditorPanel.repaint();
                    userTypesEditorPanel.paintComponents(userTypesEditorPanel.getGraphics());
                    //repaint();
                }
            }
        }
    };

    final DocumentListener documentListener = new DocumentListener() {
        public void changedUpdate(DocumentEvent e) {
            if (getNameTextField().getText().length() == 0) {
                getButtOK().setEnabled(false);
            } else {
                getButtOK().setEnabled(true);
            }
        }

        public void insertUpdate(DocumentEvent e) {
            getButtOK().setEnabled(true);
        }

        public void removeUpdate(DocumentEvent e) {
            if (getNameTextField().getText().length() == 0) {
                getButtOK().setEnabled(false);
            } else {
                getButtOK().setEnabled(true);
            }
            //getDupNameLabel().setVisible(false);
        }
    };

    final ActionListener buttOKListener = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            String aName = getNameTextField().getText();
            if (actualType instanceof ObjectDataType) {
                if (!((ObjectDataType) actualType).hasItems()) {
                    String[] message3 = {"No items in this object !!"};
                    Object[] options3 = {"OK"};
                    JOptionPane.showOptionDialog(editor, message3, "ERROR", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, null, options3, options3[0]);
                    return;
                }
            }
            for (int i = 0, j; i < aName.length(); i++) {
                boolean found = false;
                for (j = 0; j < LEGAL_CHARS.length && !found; j++) {
                    found = (aName.charAt(i) == LEGAL_CHARS[j]);
                }
                if (!found) {
                    Object[] options = {"OK"};
                    String[] message = {"Illegal character '" + aName.charAt(i) + "' in type name!!"};
                    JOptionPane.showOptionDialog(editor, message, "ERROR", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, null, options, options[0]);
                    return;
                }
            }
            if (!getTypesVector().nameAlreadyExists(aName)) {
                //System.out.println("Actual type is "+actualType.toString());
                actualUserTypeStorage = new UserTypeStorage(aName, actualType, getUserTypesEditorPanel());
                getTypesVector().addType(actualUserTypeStorage);
                DataTypeManager.getInstance().addToTypeNames(aName);
                if (actualUserTypeStorage.getDataType() instanceof NestedTableDataType)
                    DataTypeManager.getInstance().addToNestedNames(aName);
                else if (actualUserTypeStorage.getDataType() instanceof VarrayDataType)
                    DataTypeManager.getInstance().addToVarrayNames(aName);
                else if (actualUserTypeStorage.getDataType() instanceof ObjectDataType)
                    DataTypeManager.getInstance().addToObjectNames(aName);

                getButtOK().setEnabled(false);
                getNameTextField().setText("");
                getContentPane().remove(getUserTypesEditorPanel());
                userTypesEditorPanel = null;
                getContentPane().add(getUserTypesEditorPanel());
                getUserTypesEditorPanel().repaint();
                addAllTypesToCombo();
                changed = true;
            } else {
                String[] message2 = {"Data type name duplicity !!"};
                Object[] options = {"OK"};
                JOptionPane.showOptionDialog(editor, message2, "ERROR", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, null, options, options[0]);
                //getDupNameLabel().setVisible(true);
            }
        }
    };

    final ActionListener buttDeleteListener = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            //System.out.println("Delete pressed!!!");
            int choice;
            Object[] options = {"OK", "CANCEL"};
            Object[] message = {"Deleted type will be replaced with Integer data type", "               in each user type it is used.", "                     Click OK to continue.", getCheckBox()};
            if (showDialog)
                choice = JOptionPane.showOptionDialog(editor, message, "Warning", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
            else
                choice = JOptionPane.OK_OPTION;
            if (choice == JOptionPane.OK_OPTION) {
                getTypesVector().removeTypeAt(getTypeComboBox().getSelectedIndex());
                DataTypeManager.getInstance().removeFromTypeNames(getTypeComboBox().getSelectedIndex());
                DataTypeManager.getInstance().removeFromNestedNames((String) getTypeComboBox().getSelectedItem());
                DataTypeManager.getInstance().removeFromVarrayNames((String) getTypeComboBox().getSelectedItem());
                DataTypeManager.getInstance().removeFromObjectNames((String) getTypeComboBox().getSelectedItem());
                addAllTypesToCombo();
                if (getTypeComboBox().getItemCount() == 0) {
                    getButtDelete().setEnabled(false);
                    getContentPane().remove(userTypesEditorPanel);
                    userTypesEditorPanel = null;
                    repaint();
                }
            }
            changed = true;
        }
    };

    final MouseListener textFieldMouseListener = new MouseListener() {
        public void mousePressed(MouseEvent e) {
            if (!getNewRadioButton().isSelected())
                getNewRadioButton().doClick();
        }

        public void mouseReleased(MouseEvent e) {
        }

        public void mouseClicked(MouseEvent e) {
        }

        public void mouseEntered(MouseEvent e) {
        }

        public void mouseExited(MouseEvent e) {
        }
    };

    public void itemStateChanged(ItemEvent e) {
        //System.out.println((String) getTypeComboBox().getSelectedItem());
        //System.out.println("itemstatechanged");
        if (!creating)
            getEditRadioButton().doClick();
    }

    public UserTypesEditor(JFrame owner) {
        super(owner, "User types editor");
        getContentPane().setLayout(null);
        setSize(600, 320);
        setVisible(false);
        setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
        editor = this;
        addItems();
        initConnections();
    }

    public void initConnections() {
        getNewRadioButton().addChangeListener(newButtonListener);
        getEditRadioButton().addChangeListener(editButtonListener);
        getTypeComboBox().addItemListener(this);
        getNameTextField().getDocument().addDocumentListener(documentListener);
        getNameTextField().addMouseListener(textFieldMouseListener);
        getButtOK().addActionListener(buttOKListener);
        getButtDelete().addActionListener(buttDeleteListener);
        //getButtEdit().addActionListener(buttEditListener);
    }

    public void addItems() {
        getContentPane().add(getButtOK());
        //getContentPane().add(getButtEdit());
        getContentPane().add(getButtDelete());
        getContentPane().add(getNameTextField());
        getContentPane().add(getNameLabel());
        getContentPane().add(getEditLabel());
        getContentPane().add(getEditRadioButton());
        getContentPane().add(getNewRadioButton());
        getButtonGroup();
        getContentPane().add(getTypeComboBox());
        getContentPane().add(getUserTypesEditorPanel());
        //getContentPane().add(getDupObjectLabel());
        //getContentPane().add(getDupNameLabel());
    }

    public void setTypesVector(UserTypeStorageVector v) {
        typesVector = v;
        UserTypesEditorPanel utep;
        DataTypeManager.getInstance().getTypeNames().removeAllElements();
        addAllTypesToCombo();
        for (Enumeration e = typesVector.elements(); e.hasMoreElements();) {
            UserTypeStorage u = (UserTypeStorage) e.nextElement();
            DataTypeManager.getInstance().addToTypeNames(u.getTypeName());
            /*let's make correct structure of dialogs and panels*/
            utep = new UserTypesEditorPanel(this, u.getDataType());
            java.awt.Dimension dimension = utep.getPreferredSize();
            utep.setBounds(270, 10, dimension.width, dimension.height);
            u.setPanel(utep);
        }
    }

    public void reset() {
        DataTypeManager.getInstance().getTypeNames().removeAllElements();
        DataTypeManager.getInstance().removeAllFromNestedNames();
        DataTypeManager.getInstance().removeAllFromVarrayNames();
        DataTypeManager.getInstance().removeAllFromObjectNames();
    }

    protected JCheckBox getCheckBox() {
        if (checkBox == null) {
            checkBox = new JCheckBox("Do not show this warning next time");
            checkBox.setVisible(true);
            checkBox.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    showDialog = !showDialog;
                }
            });
        }
        return checkBox;
    }

    public JButton getButtOK() {
        if (buttOK == null) {
            buttOK = new JButton("OK");
            buttOK.setBounds(40, 250, 70, 25);
            buttOK.setEnabled(false);
        }
        return buttOK;
    }

    /*
     public JButton getButtEdit(){
         if (buttEdit == null){
             buttEdit = new JButton("Edit");
             buttEdit.setSize(70,25);
             buttEdit.setEnabled(false);
             buttEdit.setLocation(90,250);
         }
         return buttEdit;
     }
     */
    public JButton getButtDelete() {
        if (buttDelete == null) {
            buttDelete = new JButton("Delete");
            buttDelete.setBounds(120, 250, 70, 25);
            buttDelete.setEnabled(false);
        }
        return buttDelete;
    }

    public JTextField getNameTextField() {
        if (nameTextField == null) {
            nameTextField = new JTextField();
            nameTextField.setBounds(40, 60, 150, 25);
        }
        return nameTextField;
    }

    public JLabel getNameLabel() {
        if (nameLabel == null) {
            nameLabel = new JLabel("New type name");
            nameLabel.setSize(100, 25);
            nameLabel.setLocation(40, 35);
        }
        return nameLabel;
    }

    public JLabel getEditLabel() {
        if (editLabel == null) {
            editLabel = new JLabel("Select type");
            editLabel.setSize(100, 25);
            editLabel.setLocation(40, 130);
        }
        return editLabel;
    }

    public JRadioButton getNewRadioButton() {
        if (newRadioButton == null) {
            newRadioButton = new JRadioButton("Add a new type", true);
            newRadioButton.setSize(150, 25);
            newRadioButton.setLocation(10, 10);
        }
        return newRadioButton;
    }

    public JRadioButton getEditRadioButton() {
        if (editRadioButton == null) {
            editRadioButton = new JRadioButton("Edit / delete existing type", false);
            editRadioButton.setSize(190, 25);
            editRadioButton.setLocation(10, 105);
        }
        return editRadioButton;
    }

    public ButtonGroup getButtonGroup() {
        if (buttonGroup == null) {
            buttonGroup = new ButtonGroup();
            buttonGroup.add(getEditRadioButton());
            buttonGroup.add(getNewRadioButton());
        }
        return buttonGroup;
    }

    public JComboBox getTypeComboBox() {
        if (typeComboBox == null) {
            typeComboBox = new JComboBox();
            typeComboBox.setSize(150, 25);
            typeComboBox.setLocation(40, 155);
            typeComboBox.setEnabled(false);
            addAllTypesToCombo();
        }
        return typeComboBox;
    }

    public UserTypesEditorPanel getUserTypesEditorPanel() {
        if (userTypesEditorPanel == null) {
            userTypesEditorPanel = new UserTypesEditorPanel(this);
            java.awt.Dimension dimension = userTypesEditorPanel.getPreferredSize();
            userTypesEditorPanel.setBounds(270, 10, dimension.width, dimension.height);
            userTypesEditorPanel.setVisible(true);
        }
        return userTypesEditorPanel;
    }

    /**
     * Return the DataTypePanelGroupBox property value.
     *
     * @return com.ibm.ivj.examples.vc.utilitybeans.GroupBox
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private Border getDataTypePanelGroupBox() {
//        com.ibm.ivj.examples.vc.utilitybeans.GroupBox ivjDataTypePanelGroupBox = null;
//        try {
//            /* Create part */
//            ivjDataTypePanelGroupBox = new com.ibm.ivj.examples.vc.utilitybeans.GroupBox();
//            ivjDataTypePanelGroupBox.setLineWidth(1);
//            ivjDataTypePanelGroupBox.setTitle("Data type");
//        } catch (java.lang.Throwable ivjExc) {
//            //handleException(ivjExc);
//        }
//        ;
        return BorderFactory.createTitledBorder("Data type");
    }

    public void addAllTypesToCombo() {
        getTypeComboBox().removeAllItems();
        for (int i = 0; i < getTypesVector().getSize(); i++) {
            getTypeComboBox().addItem(getTypesVector().getTypeAt(i).getTypeName());
        }
    }

    public UserTypeStorageVector getTypesVector() {
        if (typesVector == null) {
            typesVector = new UserTypeStorageVector();
        }
        return typesVector;
    }

    public void setActualTypeStorage(UserTypeStorage uts) {
        actualUserTypeStorage = uts;
    }

    public void setActualType(DataType type) {
        actualType = type;
        //System.out.println("Set actual type: type set to "+actualType.toString());
    }

    public DataType getActualType() {
        return actualType;
    }

    public UserTypeStorage getUserTypeStorageAt(int index) {
        if ((index < getTypesVector().getSize()) && (index >= 0))
            return getTypesVector().getTypeAt(index);
        else
            return null;
    }

    public boolean isChanged() {
        return changed;
    }

    public void setUnchanged() {
        changed = false;
    }

    public void propertyChange(PropertyChangeEvent e) {
        if (e.getPropertyName().equals(ObjectDataTypePanel2.DUPLICITY_CONFLICT)) {
            objectDuplicity = true;
            return;
        }
        if (e.getPropertyName().equals(ObjectDataTypePanel2.NO_CONFLICT)) {
            objectDuplicity = false;
            //getDupObjectLabel().setVisible(false);
        }
    }
}