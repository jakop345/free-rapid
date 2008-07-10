package cz.felk.cvut.erm.typeseditor;

import cz.felk.cvut.erm.datatype.DataType;
import cz.felk.cvut.erm.datatype.IntegerDataType;
import cz.felk.cvut.erm.datatype.ObjectDataType;
import cz.felk.cvut.erm.datatype.editor.DataTypePanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Enumeration;
import java.util.Vector;

/**
 * This type was created by Aleš Kopecký.
 */
public class ObjectDataTypePanel2 extends DataTypePanel implements ActionListener, MouseListener, PropertyChangeListener, KeyListener {

    public static final String NO_CONFLICT = "no_conflict_in_object";
    public static final String DUPLICITY_CONFLICT = "duplicity_conflict_in_object";

    protected ObjectDataType ivjObjectDataType = null;
    protected JList itemList = null;
    protected DefaultListModel listModel = null;
    protected JLabel itemLabel = null;
    protected JScrollPane scrollPane = null;

    private Vector<ObjectTypeEditor> objectTypeEditorVector = null;
    private UserTypeStorageVector userTypeStorageVector = null;

    private DataType dataType = null;
    private String name;
    private String type;
    private int selectedItemPos = 0;
    private boolean deleteReleased = true;

    /**
     * Constructor
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    public ObjectDataTypePanel2() {
        super();
        initialize();
    }

    public ObjectDataTypePanel2(UserTypeStorageVector vector) {
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
        ivjObjectDataType = (ObjectDataType) getObjectDataType().clone();

        ((UserTypesEditorPanel) getDataTypeEditor()).getEditor().setActualType(ivjObjectDataType);
    }

    protected ObjectTypeEditor getObjectTypeEditor(int index) {
        //System.out.println("getObjectTypeEditor "+index);
        if (objectTypeEditorVector == null)
            objectTypeEditorVector = new Vector<ObjectTypeEditor>();
        //System.out.println("objectTypeEditorVector.size()= "+objectTypeEditorVector.size());
        if (objectTypeEditorVector.size() == index) {
            ObjectTypeEditor ote = new ObjectTypeEditor(userTypeStorageVector, getObjectDataType());
            objectTypeEditorVector.addElement(ote);
            ((UserTypesEditorPanel) getDataTypeEditor()).getEditor().getTypesVector().addPropertyChangeListener(ote);
            //ote.addPropertyChangeListener(getObjectDataType());
            ote.addPropertyChangeListener(this);
            Dimension dimension = ote.getPreferredSize();
            ote.setBounds(0, 0, dimension.width, dimension.height);
        }
        return objectTypeEditorVector.get(index);
    }

    /**
     * Return the ObjectDataType property value.
     *
     * @return cz.omnicom.ermodeller.datatype.LengthDataType
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    protected ObjectDataType getObjectDataType() {
        if (ivjObjectDataType == null) {
            try {
                ivjObjectDataType = new cz.felk.cvut.erm.datatype.ObjectDataType(userTypeStorageVector);
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjObjectDataType;
    }

    protected JLabel getItemLabel() {
        if (itemLabel == null) {
            itemLabel = new JLabel();
            itemLabel.setText("Items in Object:");
            itemLabel.setBounds(0, 0, 120, 15);
        }
        return itemLabel;
    }

    protected JList getItemList() {
        if (itemList == null) {
            itemList = new JList();
            listModel = new DefaultListModel();
            itemList.setModel(listModel);
            listModel.addElement("< new >");
            itemList.setBounds(0, 18, 150, 177);
            itemList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        }
        return itemList;
    }

    protected JScrollPane getScrollPane() {
        if (scrollPane == null) {
            scrollPane = new JScrollPane(getItemList());
            scrollPane.setVisible(true);
            scrollPane.setBounds(0, 18, 150, 177);
        }
        return scrollPane;
    }

    /**
     * This method was created by Aleš Kopecký.
     *
     * @return java.awt.Dimension
     */
    public Dimension getPreferredSize() {
        return new Dimension(150, 205);
    }

    /**
     * Called whenever the part throws an exception.
     *
     * @param exception java.lang.Throwable
     */
    protected void handleException(Throwable exception) {

        /* Uncomment the following lines to print uncaught exceptions to stdout */
        // System.out.println("--------- UNCAUGHT EXCEPTION ---------");
        // exception.printStackTrace(System.out);
    }

    /**
     * Initializes connections
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    protected void initConnections() {
        //System.out.println("initConnections()");
        getItemList().addMouseListener(this);
        getItemList().addKeyListener(this);
    }

    /**
     * Initialize the class.
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    protected void initialize() {
        try {
            setName("ObjectDataTypePanel");
            setLayout(null);
            setBackground(new java.awt.Color(204, 204, 204));//204
            add(getItemLabel());
            add(getItemList());
            add(getScrollPane());
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
                frame = (Frame) aFrameClass.newInstance();
            } catch (java.lang.Throwable ivjExc) {
                frame = new java.awt.Frame();
            }
            ObjectDataTypePanel2 aObjectDataTypePanel;
            aObjectDataTypePanel = new ObjectDataTypePanel2();
            frame.add("Center", aObjectDataTypePanel);
            frame.setSize(aObjectDataTypePanel.getSize());
            frame.setVisible(true);
        } catch (Throwable exception) {
            System.err.println("Exception occurred in main() of javax.swing.JPanel");
            exception.printStackTrace(System.out);
        }
    }

    protected void newClicked() {
        //zalozit ObjectTypeEditor
        EditorDialog d = ((UserTypesEditorPanel) getDataTypeEditor()).getEditorDialog();
        d.setVisible(true);
        d.removeAll();
        d.setOKButtonVisible(true);
        d.repaint();
        d.getContentPane().add(getObjectTypeEditor(listModel.size() - 1));
        d.paintComponents(getObjectTypeEditor(listModel.size() - 1).getGraphics());
        //getObjectTypeEditor(listModel.size()-1).updateJComboBox(userTypeStorageVector);
        type = getObjectTypeEditor(listModel.size() - 1).getTypeName();
        name = getObjectTypeEditor(listModel.size() - 1).getName();
        dataType = new IntegerDataType();
        //listModel.addElement(name+" : "+type);
        //getObjectDataType().addItem(new UserTypeStorage(name, new IntegerDataType(), null));
        checkForNameDuplicities();
    }

    protected void itemListClicked(int index) {
        EditorDialog d = ((UserTypesEditorPanel) getDataTypeEditor()).getEditorDialog();
        d.setVisible(true);
        d.removeAll();
        d.setOKButtonVisible(true);
        d.repaint();
        d.getContentPane().add(getObjectTypeEditor(index - 1));
        d.paintComponents(getObjectTypeEditor(index - 1).getGraphics());
    }

    /**
     * Method to handle events for the MouseListener interface.
     *
     * @param e java.awt.event.MouseEvent
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
            int indexClicked = getItemList().locationToIndex(e.getPoint());
            if (indexClicked > 0) {
                //System.out.println("Clicked in item list");
                selectedItemPos = indexClicked;
                itemListClicked(indexClicked);
            } else if (indexClicked == 0) {
                //System.out.println("Clicked in new");
                selectedItemPos = listModel.size();
                newClicked();
            }
        }
    }

    public void keyPressed(KeyEvent e) {
        if ((e.getKeyCode() == KeyEvent.VK_DELETE) && (deleteReleased) && (getItemList().getSelectedIndex() > 0)) {
            getObjectDataType().removeItemAt(getItemList().getSelectedIndex() - 1);
            objectTypeEditorVector.removeElementAt(getItemList().getSelectedIndex() - 1);
            listModel.removeElementAt(getItemList().getSelectedIndex());
            deleteReleased = false;
        }
    }

    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_DELETE)
            deleteReleased = true;
    }

    public void keyTyped(KeyEvent e) {
    }

    /**
     * Method to handle events for the MouseListener interface.
     *
     * @param e java.awt.event.MouseEvent
     */
    public void mouseEntered(java.awt.event.MouseEvent e) {
    }

    /**
     * Method to handle events for the MouseListener interface.
     *
     * @param e java.awt.event.MouseEvent
     */
    public void mouseExited(java.awt.event.MouseEvent e) {
    }

    /**
     * Method to handle events for the MouseListener interface.
     *
     * @param e java.awt.event.MouseEvent
     */
    public void mousePressed(java.awt.event.MouseEvent e) {
    }

    /**
     * Method to handle events for the MouseListener interface.
     *
     * @param e java.awt.event.MouseEvent
     */
    public void mouseReleased(java.awt.event.MouseEvent e) {
    }

    /**
     * This method was created in VisualAge.
     *
     * @param anEvent java.beans.PropertyChangeEvent
     */
    public void propertyChange(PropertyChangeEvent anEvent) {
        //System.out.println("propertyChange "+ anEvent.getPropertyName());
        if (anEvent.getPropertyName().equals(ObjectTypeEditor.DATATYPE_PROPERTY_CHANGE)) {
            type = anEvent.getNewValue().toString();
            dataType = (DataType) anEvent.getNewValue();
            //listModel.set(selectedItemPos, name +" : "+type);
            //getObjectDataType().getItemAt(selectedItemPos-1).setDataType((DataType)anEvent.getNewValue());
            return;
        }
        if (anEvent.getPropertyName().equals(ObjectTypeEditor.NAME_PROPERTY_CHANGE)) {
            name = (String) anEvent.getNewValue();
            //listModel.set(selectedItemPos, name +" : "+type);
            checkForNameDuplicities();
            //getObjectDataType().getItemAt(selectedItemPos-1).setTypeName((String)anEvent.getNewValue());
            return;
        }
        if (anEvent.getPropertyName().equals(ObjectTypeEditor.CONFIRM_NAME_PROPERTY_CHANGE)) {
            //name = (String)anEvent.getNewValue();
            if (selectedItemPos == listModel.size()) {
                //System.out.println("dava se nova polozka");
                listModel.addElement(name + " : " + type);
                getObjectDataType().addItem(new UserTypeStorage(name, dataType, null));
            } else {
                listModel.set(selectedItemPos, name + " : " + type);
                getObjectDataType().getItemAt(selectedItemPos - 1).setTypeName(name);
                getObjectDataType().getItemAt(selectedItemPos - 1).setDataType(dataType);
            }
            return;
        }
        // call inherited method
        firePropertyChange(anEvent.getPropertyName(), anEvent.getOldValue(), anEvent.getNewValue());
    }

    protected void checkForNameDuplicities() {
        firePropertyChange(NO_CONFLICT, null, null);
        for (int k = 1; k < listModel.size(); k++)
            getObjectTypeEditor(k - 1).hideDuplicityWarning();
        getObjectTypeEditor(selectedItemPos - 1).hideDuplicityWarning();
        for (int i = 1; i < listModel.size(); i++) {
            if ((i != selectedItemPos) && (((String) listModel.get(i)).indexOf(name + " : ") == 0)) {
                getObjectTypeEditor(selectedItemPos - 1).showDuplicityWarning();
                getObjectTypeEditor(i - 1).showDuplicityWarning();
                firePropertyChange(DUPLICITY_CONFLICT, null, null);
            }
        }
    }

    protected void checkForNameDuplicities2() {
        String actName;
        for (int k = 1; k < listModel.size(); k++)
            getObjectTypeEditor(k - 1).hideDuplicityWarning();
        firePropertyChange(NO_CONFLICT, null, null);
        for (int i = 1; i < listModel.size(); i++) {
            actName = ((String) listModel.get(i));
            actName = actName.substring(0, actName.indexOf(": "));
            //System.out.println("actName je"+actName+".");
            for (int j = i + 1; j < listModel.size(); j++) {
                if (((String) listModel.get(j)).indexOf(actName) == 0) {
                    getObjectTypeEditor(j - 1).showDuplicityWarning();
                    getObjectTypeEditor(i - 1).showDuplicityWarning();
                    firePropertyChange(DUPLICITY_CONFLICT, null, null);
                }
            }
        }
    }

    /**
     * This method was created by Aleš Kopecký.
     *
     * @param aDataType cz.omnicom.ermodeller.datatype.CharDataType
     */
    public void setObjectDataType(ObjectDataType aDataType) {
        ivjObjectDataType = aDataType;
        ivjObjectDataType.addPropertyChangeListener(this);
        for (Enumeration<UserTypeStorage> e = ivjObjectDataType.getItemVector().elements(); e.hasMoreElements();) {
            UserTypeStorage u = e.nextElement();
            selectedItemPos = listModel.size();
            getObjectTypeEditor(listModel.size() - 1).setContents(u);
            type = u.getDataType().toString();
            name = u.getTypeName();
            listModel.addElement(name + " : " + type);
        }
    }
}