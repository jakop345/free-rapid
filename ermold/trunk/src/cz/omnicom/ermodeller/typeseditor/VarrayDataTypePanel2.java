package cz.omnicom.ermodeller.typeseditor;

import cz.omnicom.ermodeller.datatype.VarrayDataType;
import cz.omnicom.ermodeller.datatype.editor.DataTypePanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * This type was created by Aleš Kopecký.
 */
public class VarrayDataTypePanel2 extends DataTypePanel implements FocusListener, ActionListener, MouseListener, PropertyChangeListener, KeyListener {

    protected class TimerListener implements ActionListener {
        public void actionPerformed(ActionEvent anEvent) {
            if (direction == UP)
                getVarrayDataType().incLength();
            if (direction == DOWN)
                getVarrayDataType().decLength();
        }
    }

    protected JTextField ivjLengthTextField = null;
    protected JButton ivjdownButton = null;
    protected JButton ivjUpButton = null;
    protected Timer timer = null;
    protected int direction;
    protected static final int DOWN = 1;
    protected static final int UP = 2;
    protected VarrayDataType ivjVarrayDataType = null;
    protected JLabel ivjLengthLabel = null;
    protected JLabel ivjTypeLabel = null;
    protected JComboBox ivjJComboBox = null;
    protected JButton typeButton = null;
    private VarrayNestedTypeEditor varrayTypeEditor = null;
    private UserTypeStorageVector userTypeStorageVector = null;

    /**
     * Constructor
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    public VarrayDataTypePanel2() {
        super();
        initialize();
    }

    public VarrayDataTypePanel2(UserTypeStorageVector vector) {
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
        ivjVarrayDataType = getVarrayDataType();//.clone();

        if (e.getSource() == getUpButton())
            connEtoM1(e);
        if (e.getSource() == getdownButton())
            connEtoM2(e);
        if (e.getSource() == getTypeButton())
            connEtoM3(e);
        ((UserTypesEditorPanel) getDataTypeEditor()).getEditor().setActualType(ivjVarrayDataType);
    }

    /**
     * connEtoC3:  (downButton.mouse.mouseReleased(java.awt.event.MouseEvent) --> UniCharDataTypePanel.downButton_MouseReleased(Ljava.awt.event.MouseEvent;)V)
     *
     * @param arg1 java.awt.event.MouseEvent
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    protected void connEtoC3(java.awt.event.MouseEvent arg1) {
        try {
            this.downButton_MousePressed(arg1);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    /**
     * connEtoC4:  (downButton.mouse.mouseReleased(java.awt.event.MouseEvent) --> UniCharDataTypePanel.downButton_MouseReleased1(Ljava.awt.event.MouseEvent;)V)
     *
     * @param arg1 java.awt.event.MouseEvent
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    protected void connEtoC4(java.awt.event.MouseEvent arg1) {
        try {
            this.downButton_MouseReleased(arg1);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    /**
     * connEtoC5:  (UpButton.mouse.mousePressed(java.awt.event.MouseEvent) --> UniCharDataTypePanel.upButton_MousePressed(Ljava.awt.event.MouseEvent;)V)
     *
     * @param arg1 java.awt.event.MouseEvent
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    protected void connEtoC5(java.awt.event.MouseEvent arg1) {
        try {
            this.upButton_MousePressed(arg1);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    /**
     * connEtoC6:  (UpButton.mouse.mouseReleased(java.awt.event.MouseEvent) --> UniCharDataTypePanel.upButton_MouseReleased(Ljava.awt.event.MouseEvent;)V)
     *
     * @param arg1 java.awt.event.MouseEvent
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    protected void connEtoC6(java.awt.event.MouseEvent arg1) {
        try {
            this.upButton_MouseReleased(arg1);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    /**
     * connEtoM1:  (UpButton.action.actionPerformed(java.awt.event.ActionEvent) --> LengthDataType.incLength()V)
     *
     * @param arg1 java.awt.event.ActionEvent
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    protected void connEtoM1(java.awt.event.ActionEvent arg1) {
        try {
            getVarrayDataType().incLength();
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    /**
     * connEtoM2:  (downButton.action.actionPerformed(java.awt.event.ActionEvent) --> LengthDataType.decLength()V)
     *
     * @param arg1 java.awt.event.ActionEvent
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    protected void connEtoM2(java.awt.event.ActionEvent arg1) {
        try {
            getVarrayDataType().decLength();
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    protected void connEtoM3(java.awt.event.ActionEvent arg1) {
        EditorDialog d = ((UserTypesEditorPanel) getDataTypeEditor()).getEditorDialog();
        d.setOKButtonVisible(false);
        d.setVisible(true);
        d.getContentPane().add(getVarrayTypeEditor());
        d.paintComponents(d.getGraphics());
        //getVarrayTypeEditor().updateJComboBox(userTypeStorageVector);
    }

    /**
     * Comment
     */
    protected void downButton_MousePressed(java.awt.event.MouseEvent mouseEvent) {
        direction = DOWN;
        getTimer().restart();
    }

    /**
     * Comment
     */
    protected void downButton_MouseReleased(java.awt.event.MouseEvent mouseEvent) {
        getTimer().stop();
    }

    /**
     * Invoked when a component gains the keyboard focus.
     */
    public void focusGained(java.awt.event.FocusEvent e) {
    }

    /**
     * Invoked when a component loses the keyboard focus.
     */
    public void focusLost(java.awt.event.FocusEvent e) {
        updateFields(e);
    }

    protected VarrayNestedTypeEditor getVarrayTypeEditor() {
        if (varrayTypeEditor == null) {
            //System.out.println("VarrayTypeEditor byl null");
            varrayTypeEditor = new VarrayNestedTypeEditor(userTypeStorageVector);
            ((UserTypesEditorPanel) getDataTypeEditor()).getEditor().getTypesVector().addPropertyChangeListener(varrayTypeEditor);
            varrayTypeEditor.addPropertyChangeListener(this);
            varrayTypeEditor.addPropertyChangeListener(getVarrayDataType());
            Dimension dimension = varrayTypeEditor.getPreferredSize();
            varrayTypeEditor.setBounds(0, 0, dimension.width, dimension.height);
        }
        return varrayTypeEditor;
    }

    /**
     * Return the downButton property value.
     *
     * @return javax.swing.JButton
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    protected javax.swing.JButton getdownButton() {
        if (ivjdownButton == null) {
            try {
                ivjdownButton = new javax.swing.JButton();
                ivjdownButton.setName("downButton");
                ivjdownButton.setToolTipText("Count down");
                ivjdownButton.setText("");
                ivjdownButton.setBounds(70, 23, 20, 25);
                ivjdownButton.setActionCommand("down");
                // user code begin {1}
                ivjdownButton.setIcon(new ImageIcon(ClassLoader.getSystemResource("img/left.gif")));
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjdownButton;
    }

    /**
     * Return the LengthDataType property value.
     *
     * @return cz.omnicom.ermodeller.datatype.LengthDataType
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    protected VarrayDataType getVarrayDataType() {
        if (ivjVarrayDataType == null) {
            try {
                ivjVarrayDataType = new cz.omnicom.ermodeller.datatype.VarrayDataType();
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjVarrayDataType;
    }

    /**
     * Return the lengthLabel property value.
     *
     * @return javax.swing.JLabel
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    protected javax.swing.JLabel getLengthLabel() {
        if (ivjLengthLabel == null) {
            try {
                ivjLengthLabel = new javax.swing.JLabel();
                ivjLengthLabel.setName("LengthLabel");
                ivjLengthLabel.setText("Length:");
                ivjLengthLabel.setBounds(5, 5, 45, 15);
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjLengthLabel;
    }

    protected javax.swing.JLabel getTypeLabel() {
        if (ivjTypeLabel == null) {
            try {
                ivjTypeLabel = new javax.swing.JLabel();
                ivjTypeLabel.setName("TypeLabel");
                ivjTypeLabel.setText("Type of items:");
                ivjTypeLabel.setBounds(5, 60, 85, 15);
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjTypeLabel;
    }

    /**
     * Return the JTextField1 property value.
     *
     * @return javax.swing.JTextField
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    protected javax.swing.JTextField getLengthTextField() {
        if (ivjLengthTextField == null) {
            try {
                ivjLengthTextField = new javax.swing.JTextField();
                ivjLengthTextField.setName("LengthTextField");
                ivjLengthTextField.setToolTipText("Max number of items in the varray");
                ivjLengthTextField.setBackground(java.awt.SystemColor.activeCaptionText);
                ivjLengthTextField.setBounds(5, 26, 63, 19);
                ivjLengthTextField.setEditable(true);
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjLengthTextField;
    }

    /**
     * This method was created by Aleš Kopecký.
     *
     * @return java.awt.Dimension
     */
    public Dimension getPreferredSize() {
        return new Dimension(140, 110);
    }

    /**
     * This method was created by Aleš Kopecký.
     *
     * @return javax.swing.Timer
     */
    protected Timer getTimer() {
        if (timer == null) {
            timer = new Timer(100, new TimerListener());
            timer.setInitialDelay(500);
            timer.setRepeats(true);
            timer.addActionListener(new TimerListener());
        }
        return timer;
    }

    /**
     * Return the UpButton property value.
     *
     * @return javax.swing.JButton
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    protected javax.swing.JButton getUpButton() {
        if (ivjUpButton == null) {
            try {
                ivjUpButton = new javax.swing.JButton();
                ivjUpButton.setName("UpButton");
                ivjUpButton.setToolTipText("Count up");
                ivjUpButton.setText("");
                ivjUpButton.setBounds(90, 23, 20, 25);
                ivjUpButton.setActionCommand("up");
                ivjUpButton.setIcon(new ImageIcon(ClassLoader.getSystemResource("img/right.gif")));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjUpButton;
    }

    protected JButton getTypeButton() {
        if (typeButton == null) {
            typeButton = new JButton();
            typeButton.setBounds(5, 81, 130, 25);
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
        getLengthTextField().addKeyListener(this);
        getdownButton().addMouseListener(this);
        getUpButton().addMouseListener(this);
        getUpButton().addActionListener(this);
        getdownButton().addActionListener(this);
        getTypeButton().addActionListener(this);
    }

    /**
     * Initialize the class.
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    protected void initialize() {
        try {
            setName("VarrayDataTypePanel");
            setLayout(null);
            setBackground(new java.awt.Color(204, 204, 204));
            add(getLengthTextField(), getLengthTextField().getName());
            add(getdownButton(), getdownButton().getName());
            add(getUpButton(), getUpButton().getName());
            add(getLengthLabel(), getLengthLabel().getName());
            add(getTypeLabel(), getTypeLabel().getName());
            add(getTypeButton(), getTypeButton().getName());
            initConnections();
        } catch (java.lang.Throwable ivjExc) {
            //System.out.println("exception!!");
            System.out.println(ivjExc.getMessage());
            ivjExc.printStackTrace();
            handleException(ivjExc);
        }
        // user code begin {2}
        getLengthTextField().addFocusListener(this);
        // user code end
    }

    /**
     * Invoked when a key has been pressed.
     */
    public void keyPressed(java.awt.event.KeyEvent e) {
        int i = 1;
        if (e.getKeyCode() == KeyEvent.VK_ENTER)
            updateFields(e);
    }

    /**
     * Invoked when a key has been released.
     */
    public void keyReleased(java.awt.event.KeyEvent e) {
    }

    /**
     * Invoked when a key has been typed.
     * This event occurs when a key press is followed by a key release.
     */
    public void keyTyped(java.awt.event.KeyEvent e) {
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
            VarrayDataTypePanel2 aVarrayDataTypePanel;
            aVarrayDataTypePanel = new VarrayDataTypePanel2();
            frame.add("Center", aVarrayDataTypePanel);
            frame.setSize(aVarrayDataTypePanel.getSize());
            frame.setVisible(true);
        } catch (Throwable exception) {
            System.err.println("Exception occurred in main() of javax.swing.JPanel");
            exception.printStackTrace(System.out);
        }
    }

    /**
     * Method to handle events for the MouseListener interface.
     *
     * @param e java.awt.event.MouseEvent
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    public void mouseClicked(java.awt.event.MouseEvent e) {
    }

    /**
     * Method to handle events for the MouseListener interface.
     *
     * @param e java.awt.event.MouseEvent
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    public void mouseEntered(java.awt.event.MouseEvent e) {
    }

    /**
     * Method to handle events for the MouseListener interface.
     *
     * @param e java.awt.event.MouseEvent
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    public void mouseExited(java.awt.event.MouseEvent e) {
    }

    /**
     * Method to handle events for the MouseListener interface.
     *
     * @param e java.awt.event.MouseEvent
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    public void mousePressed(java.awt.event.MouseEvent e) {
        if (e.getSource() == getdownButton())
            connEtoC3(e);
        if (e.getSource() == getUpButton())
            connEtoC5(e);
    }

    /**
     * Method to handle events for the MouseListener interface.
     *
     * @param e java.awt.event.MouseEvent
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    public void mouseReleased(java.awt.event.MouseEvent e) {
        if (e.getSource() == getdownButton())
            connEtoC4(e);
        if (e.getSource() == getUpButton())
            connEtoC6(e);
    }

    /**
     * This method was created in VisualAge.
     *
     * @param anEvent java.beans.PropertyChangeEvent
     */
    public void propertyChange(PropertyChangeEvent anEvent) {
        //System.out.println("propertyChange "+ anEvent.getPropertyName());
        if (anEvent.getPropertyName().equals(VarrayDataType.LENGTH_PROPERTY_CHANGE)) {
            getLengthTextField().setText(anEvent.getNewValue().toString());
            return;
        }
        if (anEvent.getPropertyName().equals(VarrayNestedTypeEditor.DATATYPE_PROPERTY_CHANGE)) {
            //	System.out.println("type changed to "+((DataType)anEvent.getNewValue()).toString());
            getTypeButton().setText(anEvent.getNewValue().toString());
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
    public void setVarrayDataType(VarrayDataType aDataType) {
        ivjVarrayDataType = aDataType;
        getLengthTextField().setText(Integer.toString(ivjVarrayDataType.getLength()));
        ivjVarrayDataType.addPropertyChangeListener(this);
        getTypeButton().setText(ivjVarrayDataType.getType().toString());
        varrayTypeEditor = new VarrayNestedTypeEditor(userTypeStorageVector, ivjVarrayDataType.getType());
        userTypeStorageVector.addPropertyChangeListener(varrayTypeEditor);
        varrayTypeEditor.addPropertyChangeListener(this);
        varrayTypeEditor.addPropertyChangeListener(ivjVarrayDataType);
        Dimension dimension = varrayTypeEditor.getPreferredSize();
        varrayTypeEditor.setBounds(0, 0, dimension.width, dimension.height);
    }

    /**
     * Comment
     */
    protected void upButton_MousePressed(java.awt.event.MouseEvent mouseEvent) {
        direction = UP;
        getTimer().restart();
    }

    /**
     * Comment
     */
    protected void upButton_MouseReleased(java.awt.event.MouseEvent mouseEvent) {
        getTimer().stop();
    }

    public void updateFields(ComponentEvent e) {
        int i;
        Integer in;

        try {
            i = Integer.parseInt(getLengthTextField().getText());
            if (getVarrayDataType().evaluateLength(i)) {
                ivjVarrayDataType = getVarrayDataType();//.clone();
                getVarrayDataType().setLength(i);
                ((UserTypesEditorPanel) getDataTypeEditor()).getEditor().setActualType(ivjVarrayDataType);
            } else {
                in = getVarrayDataType().getLength();
                getLengthTextField().setText(in.toString());
            }
        }
        catch (Exception ex) {
            in = getVarrayDataType().getLength();
            getLengthTextField().setText(in.toString());
        }
    }
}