package cz.felk.cvut.erm.typeseditor;

import cz.felk.cvut.erm.datatype.*;
import cz.felk.cvut.erm.datatype.editor.DataTypePanel;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UserTypesEditorPanel extends JPanel implements ActionListener {

    private JRadioButton intRadioButton = null;
    private JRadioButton charRadioButton = null;
    private JRadioButton varchar2RadioButton = null;
    private JRadioButton numberRadioButton = null;
    private JRadioButton objectRadioButton = null;
    private JRadioButton varrayRadioButton = null;
    private JRadioButton dateRadioButton = null;
    private JRadioButton floatRadioButton = null;
    private JRadioButton nestedRadioButton = null;

    private ButtonGroup buttonGroup = null;
    private UserTypesEditor editor = null;
    private DataTypePanel dataTypePanel = null;
    private JPanel jPanel;

    protected EditorDialog editorDialog = null;

    //protected PropertyChangeSupport propertyChange = null;

    public UserTypesEditorPanel(JDialog owner) {
        super();
        editor = (UserTypesEditor) owner;
        setLayout(null);
        addItems();
        initConnections();
    }

    public UserTypesEditorPanel(JDialog owner, DataType d) {
        super();
        editor = (UserTypesEditor) owner;
        setLayout(null);
        addItems();
        initConnections();
        //System.out.println("UserTypesEditorPanel - typ "+d);
        setPanelContents(d);
    }

    public void setPanelContents(DataType d) {
        if (d instanceof IntegerDataType)
            getIntRadioButton().setSelected(true);
        else if (d instanceof DateDataType)
            getDateRadioButton().setSelected(true);
        else if (d instanceof FloatDataType)
            getFloatRadioButton().setSelected(true);
        else if (d instanceof FixedCharDataType) {
            getCharRadioButton().setSelected(true);
            dataTypePanel = new LengthDataTypePanel2();
            ((LengthDataTypePanel2) dataTypePanel).setLengthDataType((LengthDataType) d);
            dataTypePanel.setDataTypeEditor(this);
            Dimension dimension = dataTypePanel.getPreferredSize();
            dataTypePanel.setVisible(true);
            dataTypePanel.setLayout(null);
            dataTypePanel.setBounds(170, 10, dimension.width, dimension.height);
            add(dataTypePanel);
        } else if (d instanceof Varchar2DataType) {
            getVarchar2RadioButton().setSelected(true);
            dataTypePanel = new LengthDataTypePanel2();
            ((LengthDataTypePanel2) dataTypePanel).setLengthDataType((LengthDataType) d);
            dataTypePanel.setDataTypeEditor(this);
            Dimension dimension = dataTypePanel.getPreferredSize();
            dataTypePanel.setVisible(true);
            dataTypePanel.setLayout(null);
            dataTypePanel.setBounds(170, 10, dimension.width, dimension.height);
            add(dataTypePanel);
        } else if (d instanceof GeneralNumberDataType) {
            getNumberRadioButton().setSelected(true);
            dataTypePanel = new GeneralNumberDataTypePanel2();
            ((GeneralNumberDataTypePanel2) dataTypePanel).setGeneralNumberDataType((GeneralNumberDataType) d);
            dataTypePanel.setDataTypeEditor(this);
            Dimension dimension = dataTypePanel.getPreferredSize();
            dataTypePanel.setVisible(true);
            dataTypePanel.setLayout(null);
            dataTypePanel.setBounds(170, 10, dimension.width, dimension.height);
            add(dataTypePanel);
        } else if (d instanceof VarrayDataType) {
            getVarrayRadioButton().setSelected(true);
            dataTypePanel = new VarrayDataTypePanel2(getEditor().getTypesVector());
            ((VarrayDataTypePanel2) dataTypePanel).setVarrayDataType((VarrayDataType) d);
            dataTypePanel.setDataTypeEditor(this);
            getEditor().getTypesVector().addPropertyChangeListener((VarrayDataTypePanel2) dataTypePanel);
            Dimension dimension = dataTypePanel.getPreferredSize();
            dataTypePanel.setVisible(true);
            dataTypePanel.setLayout(null);
            dataTypePanel.setBounds(170, 10, dimension.width, dimension.height);
            add(dataTypePanel);
        } else if (d instanceof ObjectDataType) {
            getObjectRadioButton().setSelected(true);
            dataTypePanel = new ObjectDataTypePanel2(getEditor().getTypesVector());
            dataTypePanel.setDataTypeEditor(this);
            ((ObjectDataTypePanel2) dataTypePanel).setObjectDataType((ObjectDataType) d);
            dataTypePanel.addPropertyChangeListener(getEditor());
            getEditor().getTypesVector().addPropertyChangeListener((ObjectDataTypePanel2) dataTypePanel);
            Dimension dimension = dataTypePanel.getPreferredSize();
            dataTypePanel.setVisible(true);
            dataTypePanel.setLayout(null);
            dataTypePanel.setBounds(170, 10, dimension.width, dimension.height);
            add(dataTypePanel);
        } else if (d instanceof NestedTableDataType) {
            getNestedRadioButton().setSelected(true);
            dataTypePanel = new NestedTableDataTypePanel2(getEditor().getTypesVector());
            ((NestedTableDataTypePanel2) dataTypePanel).setNestedTableDataType((NestedTableDataType) d);
            dataTypePanel.setDataTypeEditor(this);
            getEditor().getTypesVector().addPropertyChangeListener((NestedTableDataTypePanel2) dataTypePanel);
            Dimension dimension = dataTypePanel.getPreferredSize();
            dataTypePanel.setVisible(true);
            dataTypePanel.setLayout(null);
            dataTypePanel.setBounds(170, 10, dimension.width, dimension.height);
            add(dataTypePanel);
        }
        /*
          else
              System.out.println("zadny typ nerozpoznan!");
              */
    }

    public void addItems() {
        getButtonGroup();
        add(getJPanel());
    }

    public JPanel getJPanel() {
        if (jPanel == null) {
            jPanel = new JPanel();
            jPanel.setLayout(null);
            jPanel.setVisible(true);
            jPanel.setBounds(0, 5, 135, 200);
            jPanel.setBorder(getDataTypePanelGroupBox());

            jPanel.add(getIntRadioButton());
            jPanel.add(getCharRadioButton());
            jPanel.add(getVarchar2RadioButton());
            jPanel.add(getFloatRadioButton());
            jPanel.add(getNumberRadioButton());
            jPanel.add(getDateRadioButton());
            jPanel.add(getObjectRadioButton());
            jPanel.add(getVarrayRadioButton());
            jPanel.add(getNestedRadioButton());
        }
        return jPanel;
    }

    public EditorDialog getEditorDialog() {
        if (editorDialog == null) {
            //System.out.println("EditorDialog byl null");
            editorDialog = new EditorDialog(getEditor());
            editorDialog.setVisible(true);
            editorDialog.setLocationRelativeTo(this);
            Dimension d = editorDialog.getPreferredSize();
            editorDialog.setBounds(450, 300, d.width, d.height);
        }
        return editorDialog;
    }

    public void initConnections() {
        getIntRadioButton().addActionListener(this);
        getCharRadioButton().addActionListener(this);
        getVarchar2RadioButton().addActionListener(this);
        getFloatRadioButton().addActionListener(this);
        getNumberRadioButton().addActionListener(this);
        getDateRadioButton().addActionListener(this);
        getObjectRadioButton().addActionListener(this);
        getVarrayRadioButton().addActionListener(this);
        getNestedRadioButton().addActionListener(this);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == getIntRadioButton())
            if (!(editor.getActualType() instanceof IntegerDataType)) {
                intSelected();
            }
        if (e.getSource() == getCharRadioButton())
            if (!(editor.getActualType() instanceof FixedCharDataType)) {
                charSelected();
            }
        if (e.getSource() == getVarchar2RadioButton())
            if (!(editor.getActualType() instanceof Varchar2DataType)) {
                varchar2Selected();
            }
        if (e.getSource() == getFloatRadioButton())
            if (!(editor.getActualType() instanceof FloatDataType)) {
                floatSelected();
            }
        if (e.getSource() == getNumberRadioButton())
            if (!(editor.getActualType() instanceof GeneralNumberDataType)) {
                numberSelected();
            }
        if (e.getSource() == getDateRadioButton())
            if (!(editor.getActualType() instanceof DateDataType)) {
                dateSelected();
            }
        if (e.getSource() == getObjectRadioButton())
            if (!(editor.getActualType() instanceof ObjectDataType)) {
                objectSelected();
            }
        if (e.getSource() == getVarrayRadioButton())
            if (!(editor.getActualType() instanceof VarrayDataType)) {
                varraySelected();
            }
        if (e.getSource() == getNestedRadioButton())
            if (!(editor.getActualType() instanceof NestedTableDataType)) {
                nestedSelected();
            }
        repaint();
        paintComponents(this.getGraphics());
    }

    public void intSelected() {
        if (dataTypePanel != null)
            remove(dataTypePanel);
        if (editorDialog != null) {
            editorDialog.removeAll();
            editorDialog.setVisible(false);
        }
        editor.setActualType(new IntegerDataType());
    }

    public void charSelected() {
        if (dataTypePanel != null)
            remove(dataTypePanel);
        if (editorDialog != null) {
            editorDialog.removeAll();
            editorDialog.setVisible(false);
        }
        editor.setActualType(new FixedCharDataType());
        dataTypePanel = new LengthDataTypePanel2();
        ((LengthDataTypePanel2) dataTypePanel).setLengthDataType((LengthDataType) editor.getActualType());
        dataTypePanel.setDataTypeEditor(this);
        Dimension dimension = dataTypePanel.getPreferredSize();
        dataTypePanel.setVisible(true);
        dataTypePanel.setLayout(null);
        dataTypePanel.setBounds(170, 10, dimension.width, dimension.height);
        add(dataTypePanel);
    }

    public void varchar2Selected() {
        if (dataTypePanel != null)
            remove(dataTypePanel);
        if (editorDialog != null) {
            editorDialog.removeAll();
            editorDialog.setVisible(false);
        }
        editor.setActualType(new Varchar2DataType());
        dataTypePanel = new LengthDataTypePanel2();
        ((LengthDataTypePanel2) dataTypePanel).setLengthDataType((LengthDataType) editor.getActualType());
        dataTypePanel.setDataTypeEditor(this);
        Dimension dimension = dataTypePanel.getPreferredSize();
        dataTypePanel.setVisible(true);
        dataTypePanel.setLayout(null);
        dataTypePanel.setBounds(170, 10, dimension.width, dimension.height);
        add(dataTypePanel);
    }

    public void floatSelected() {
        if (dataTypePanel != null)
            remove(dataTypePanel);
        if (editorDialog != null) {
            editorDialog.removeAll();
            editorDialog.setVisible(false);
        }
        editor.setActualType(new FloatDataType());
    }

    private void numberSelected() {
        if (dataTypePanel != null)
            remove(dataTypePanel);
        if (editorDialog != null) {
            editorDialog.removeAll();
            editorDialog.setVisible(false);
        }
        editor.setActualType(new GeneralNumberDataType());
        dataTypePanel = new GeneralNumberDataTypePanel2();
        ((GeneralNumberDataTypePanel2) dataTypePanel).setGeneralNumberDataType((GeneralNumberDataType) editor.getActualType());
        dataTypePanel.setDataTypeEditor(this);
        Dimension dimension = dataTypePanel.getPreferredSize();
        dataTypePanel.setVisible(true);
        dataTypePanel.setLayout(null);
        dataTypePanel.setBounds(170, 10, dimension.width, dimension.height);
        add(dataTypePanel);
    }

    public void dateSelected() {
        if (dataTypePanel != null)
            remove(dataTypePanel);
        if (editorDialog != null) {
            editorDialog.removeAll();
            editorDialog.setVisible(false);
        }
        editor.setActualType(new DateDataType());
    }

    public void objectSelected() {
        if (dataTypePanel != null)
            remove(dataTypePanel);
        if (editorDialog != null) {
            editorDialog.removeAll();
            editorDialog.setVisible(false);
        }
        editor.setActualType(new ObjectDataType(editor.getTypesVector()));
        dataTypePanel = new ObjectDataTypePanel2(getEditor().getTypesVector());
        ((ObjectDataTypePanel2) dataTypePanel).setObjectDataType((ObjectDataType) editor.getActualType());
        dataTypePanel.setDataTypeEditor(this);
        dataTypePanel.addPropertyChangeListener(getEditor());
        getEditor().getTypesVector().addPropertyChangeListener((ObjectDataTypePanel2) dataTypePanel);
        Dimension dimension = dataTypePanel.getPreferredSize();
        dataTypePanel.setVisible(true);
        dataTypePanel.setLayout(null);
        dataTypePanel.setBounds(170, 10, dimension.width, dimension.height);
        add(dataTypePanel);
    }

    public void varraySelected() {
        if (dataTypePanel != null)
            remove(dataTypePanel);
        if (editorDialog != null) {
            editorDialog.removeAll();
            editorDialog.setVisible(false);
        }
        editor.setActualType(new VarrayDataType());
        dataTypePanel = new VarrayDataTypePanel2(getEditor().getTypesVector());
        ((VarrayDataTypePanel2) dataTypePanel).setVarrayDataType((VarrayDataType) editor.getActualType());
        dataTypePanel.setDataTypeEditor(this);
        getEditor().getTypesVector().addPropertyChangeListener((VarrayDataTypePanel2) dataTypePanel);
        Dimension dimension = dataTypePanel.getPreferredSize();
        dataTypePanel.setVisible(true);
        dataTypePanel.setLayout(null);
        dataTypePanel.setBounds(170, 10, dimension.width, dimension.height);
        add(dataTypePanel);
    }

    public void nestedSelected() {
        if (dataTypePanel != null)
            remove(dataTypePanel);
        if (editorDialog != null) {
            editorDialog.removeAll();
            editorDialog.setVisible(false);
        }
        editor.setActualType(new NestedTableDataType());
        dataTypePanel = new NestedTableDataTypePanel2(getEditor().getTypesVector());
        ((NestedTableDataTypePanel2) dataTypePanel).setNestedTableDataType((NestedTableDataType) editor.getActualType());
        dataTypePanel.setDataTypeEditor(this);
        getEditor().getTypesVector().addPropertyChangeListener((NestedTableDataTypePanel2) dataTypePanel);
        Dimension dimension = dataTypePanel.getPreferredSize();
        dataTypePanel.setVisible(true);
        dataTypePanel.setLayout(null);
        dataTypePanel.setBounds(170, 10, dimension.width, dimension.height);
        add(dataTypePanel);
    }

    public JRadioButton getIntRadioButton() {
        if (intRadioButton == null) {
            intRadioButton = new JRadioButton("Integer", true);
            intRadioButton.setSize(90, 20);
            intRadioButton.setLocation(10, 15);
            intSelected();
        }
        return intRadioButton;
    }

    public JRadioButton getCharRadioButton() {
        if (charRadioButton == null) {
            charRadioButton = new JRadioButton("Char");
            charRadioButton.setSize(90, 20);
            charRadioButton.setLocation(10, 35);
        }
        return charRadioButton;
    }

    public JRadioButton getVarchar2RadioButton() {
        if (varchar2RadioButton == null) {
            varchar2RadioButton = new JRadioButton("Variable string");
            varchar2RadioButton.setSize(120, 20);
            varchar2RadioButton.setLocation(10, 55);
        }
        return varchar2RadioButton;
    }

    public JRadioButton getFloatRadioButton() {
        if (floatRadioButton == null) {
            floatRadioButton = new JRadioButton("Float");
            floatRadioButton.setSize(90, 20);
            floatRadioButton.setLocation(10, 75);
        }
        return floatRadioButton;
    }

    public JRadioButton getNumberRadioButton() {
        if (numberRadioButton == null) {
            numberRadioButton = new JRadioButton("General number");
            numberRadioButton.setSize(120, 20);
            numberRadioButton.setLocation(10, 95);
        }
        return numberRadioButton;
    }

    public JRadioButton getDateRadioButton() {
        if (dateRadioButton == null) {
            dateRadioButton = new JRadioButton("Date");
            dateRadioButton.setSize(90, 20);
            dateRadioButton.setLocation(10, 115);
        }
        return dateRadioButton;
    }

    public JRadioButton getObjectRadioButton() {
        if (objectRadioButton == null) {
            objectRadioButton = new JRadioButton("Object");
            objectRadioButton.setSize(90, 20);
            objectRadioButton.setLocation(10, 135);
        }
        return objectRadioButton;
    }

    public JRadioButton getVarrayRadioButton() {
        if (varrayRadioButton == null) {
            varrayRadioButton = new JRadioButton("Varray");
            varrayRadioButton.setSize(90, 20);
            varrayRadioButton.setLocation(10, 155);
        }
        return varrayRadioButton;
    }

    public JRadioButton getNestedRadioButton() {
        if (nestedRadioButton == null) {
            nestedRadioButton = new JRadioButton("Nested table");
            nestedRadioButton.setSize(120, 20);
            nestedRadioButton.setLocation(10, 175);
        }
        return nestedRadioButton;
    }

    public ButtonGroup getButtonGroup() {
        if (buttonGroup == null) {
            buttonGroup = new ButtonGroup();
            buttonGroup.add(getIntRadioButton());
            buttonGroup.add(getCharRadioButton());
            buttonGroup.add(getVarchar2RadioButton());
            buttonGroup.add(getFloatRadioButton());
            buttonGroup.add(getNumberRadioButton());
            buttonGroup.add(getDateRadioButton());
            buttonGroup.add(getObjectRadioButton());
            buttonGroup.add(getVarrayRadioButton());
            buttonGroup.add(getNestedRadioButton());
        }
        return buttonGroup;
    }

    public UserTypesEditor getEditor() {
        return editor;
    }

    public Dimension getPreferredSize() {
        return new Dimension(320, 210);
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
}