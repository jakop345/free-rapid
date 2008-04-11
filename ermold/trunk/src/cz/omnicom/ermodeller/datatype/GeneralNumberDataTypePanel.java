package cz.omnicom.ermodeller.datatype;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Panel to customize <code>GeneralNumberDataType</code>
 */
public class GeneralNumberDataTypePanel extends DataTypePanel implements FocusListener, ActionListener, KeyListener, MouseListener, PropertyChangeListener {

    /**
     * <code>TimerListener</code> gets events from <code>Timer</code>
     * and increase or decrease properties of <code>GeneralNumberDataType</code>.
     *
     * @see javax.swing.Timer
     */
    private class TimerListener implements ActionListener {
        public void actionPerformed(ActionEvent anEvent) {
            if (value == PRECISION) {
                if (direction == UP)
                    getGeneralNumberDataType().incPrecision();
                if (direction == DOWN)
                    getGeneralNumberDataType().decPrecision();
            }
            if (value == SCALE) {
                if (direction == UP)
                    getGeneralNumberDataType().incScale();
                if (direction == DOWN)
                    getGeneralNumberDataType().decScale();
            }
            //System.out.println("actionPerformed(ActionEvent anEvent)");
        }
    }

    /**
     * Timer helps to change properties of <code>GeneralNumberDataType</code> when
     * buttons Up or Down are pressed.
     *
     * @see javax.swing.Timer
     */
    private Timer timer = null;
    /**
     * Increase or decrease.
     */
    private int direction;
    /**
     * Change precision or scale.
     */
    private int value;
    private static final int DOWN = 1;
    private static final int UP = 2;
    private static final int PRECISION = 1;
    private static final int SCALE = 2;
    private JButton ivjPrecisionDownButton = null;
    private JLabel ivjPrecisionLabel = null;
    private JButton ivjPrecisionUpButton = null;
    private JButton ivjScaleDownButton = null;
    private JLabel ivjScaleLabel = null;
    protected JTextField ivjScaleTextField = null;
    private JButton ivjScaleUpButton = null;
    protected JTextField ivjPrecisionTextField = null;
    protected GeneralNumberDataType ivjGeneralNumberDataType = null;

    /**
     * Constructor
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    public GeneralNumberDataTypePanel() {
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
        ivjGeneralNumberDataType = (GeneralNumberDataType) getGeneralNumberDataType().clone();
        // user code end
        if (e.getSource() == getPrecisionDownButton())
            connEtoM1(e);
        if (e.getSource() == getPrecisionUpButton())
            connEtoM2(e);
        if (e.getSource() == getScaleUpButton())
            connEtoM3(e);
        if (e.getSource() == getScaleDownButton())
            connEtoM4(e);
        //System.out.println("actionPerformed(java.awt.event.ActionEvent e)");
        // user code begin {2}
        try {
            ((DataTypeEditor) getDataTypeEditor()).setValue(ivjGeneralNumberDataType);
        } catch (java.lang.NullPointerException npe) {
        }
        // user code end
    }

    /**
     * connEtoC10:  (ScaleDownButton.mouse.mouseReleased(java.awt.event.MouseEvent) --> GeneralNumberDataTypePanel.scaleDownButton_MouseReleased(Ljava.awt.event.MouseEvent;)V)
     *
     * @param arg1 java.awt.event.MouseEvent
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    void connEtoC10(java.awt.event.MouseEvent arg1) {
        try {
            // user code begin {1}
            // user code end
            this.scaleDownButton_MouseReleased(arg1);
            // user code begin {2}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connEtoC11:  (ScaleUpButton.mouse.mousePressed(java.awt.event.MouseEvent) --> GeneralNumberDataTypePanel.scaleUpButton_MousePressed(Ljava.awt.event.MouseEvent;)V)
     *
     * @param arg1 java.awt.event.MouseEvent
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    void connEtoC11(java.awt.event.MouseEvent arg1) {
        try {
            // user code begin {1}
            // user code end
            this.scaleUpButton_MousePressed(arg1);
            // user code begin {2}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connEtoC12:  (ScaleDownButton.mouse.mousePressed(java.awt.event.MouseEvent) --> GeneralNumberDataTypePanel.scaleDownButton_MousePressed(Ljava.awt.event.MouseEvent;)V)
     *
     * @param arg1 java.awt.event.MouseEvent
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    void connEtoC12(java.awt.event.MouseEvent arg1) {
        try {
            // user code begin {1}
            // user code end
            this.scaleDownButton_MousePressed(arg1);
            // user code begin {2}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connEtoC5:  (PrecisionDownButton.mouse.mousePressed(java.awt.event.MouseEvent) --> GeneralNumberDataTypePanel.precisionDownButton_MousePressed(Ljava.awt.event.MouseEvent;)V)
     *
     * @param arg1 java.awt.event.MouseEvent
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    void connEtoC5(java.awt.event.MouseEvent arg1) {
        try {
            // user code begin {1}
            // user code end
            this.precisionDownButton_MousePressed(arg1);
            // user code begin {2}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connEtoC6:  (PrecisionDownButton.mouse.mouseReleased(java.awt.event.MouseEvent) --> GeneralNumberDataTypePanel.precisionDownButton_MouseReleased(Ljava.awt.event.MouseEvent;)V)
     *
     * @param arg1 java.awt.event.MouseEvent
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    void connEtoC6(java.awt.event.MouseEvent arg1) {
        try {
            // user code begin {1}
            // user code end
            this.precisionDownButton_MouseReleased(arg1);
            // user code begin {2}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connEtoC7:  (PrecisionUpButton.mouse.mousePressed(java.awt.event.MouseEvent) --> GeneralNumberDataTypePanel.precisionUpButton_MousePressed(Ljava.awt.event.MouseEvent;)V)
     *
     * @param arg1 java.awt.event.MouseEvent
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    void connEtoC7(java.awt.event.MouseEvent arg1) {
        try {
            // user code begin {1}
            // user code end
            this.precisionUpButton_MousePressed(arg1);
            // user code begin {2}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connEtoC8:  (PrecisionUpButton.mouse.mouseReleased(java.awt.event.MouseEvent) --> GeneralNumberDataTypePanel.precisionUpButton_MouseReleased(Ljava.awt.event.MouseEvent;)V)
     *
     * @param arg1 java.awt.event.MouseEvent
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    void connEtoC8(java.awt.event.MouseEvent arg1) {
        try {
            // user code begin {1}
            // user code end
            this.precisionUpButton_MouseReleased(arg1);
            // user code begin {2}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connEtoC9:  (ScaleUpButton.mouse.mouseReleased(java.awt.event.MouseEvent) --> GeneralNumberDataTypePanel.scaleUpButton_MouseReleased(Ljava.awt.event.MouseEvent;)V)
     *
     * @param arg1 java.awt.event.MouseEvent
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    void connEtoC9(java.awt.event.MouseEvent arg1) {
        try {
            // user code begin {1}
            // user code end
            this.scaleUpButton_MouseReleased(arg1);
            // user code begin {2}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connEtoM1:  (PrecisionDownButton.action.actionPerformed(java.awt.event.ActionEvent) --> GeneralNumberDataType.decPrecision()V)
     *
     * @param arg1 java.awt.event.ActionEvent
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    protected void connEtoM1(java.awt.event.ActionEvent arg1) {
        try {
            // user code begin {1}
            // user code end
            getGeneralNumberDataType().decPrecision();
            // user code begin {2}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connEtoM2:  (PrecisionUpButton.action.actionPerformed(java.awt.event.ActionEvent) --> GeneralNumberDataType.incPrecision()V)
     *
     * @param arg1 java.awt.event.ActionEvent
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    protected void connEtoM2(java.awt.event.ActionEvent arg1) {
        try {
            // user code begin {1}
            // user code end
            getGeneralNumberDataType().incPrecision();
            // user code begin {2}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connEtoM3:  (ScaleUpButton.action.actionPerformed(java.awt.event.ActionEvent) --> GeneralNumberDataType.incScale()V)
     *
     * @param arg1 java.awt.event.ActionEvent
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    protected void connEtoM3(java.awt.event.ActionEvent arg1) {
        try {
            // user code begin {1}
            // user code end
            getGeneralNumberDataType().incScale();
            // user code begin {2}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connEtoM4:  (ScaleDownButton.action.actionPerformed(java.awt.event.ActionEvent) --> GeneralNumberDataType.decScale()V)
     *
     * @param arg1 java.awt.event.ActionEvent
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    protected void connEtoM4(java.awt.event.ActionEvent arg1) {
        try {
            // user code begin {1}
            // user code end
            getGeneralNumberDataType().decScale();
            // user code begin {2}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * Invoked when a component gains the keyboard focus.
     */
    public void focusGained(java.awt.event.FocusEvent e) {
/*	Integer in;

	in=new Integer(getGeneralNumberDataType().getPrecision());
	getPrecisionTextField().setText(in.toString());
	in=new Integer(getGeneralNumberDataType().getScale());
	getScaleTextField().setText(in.toString());*/
    }

    /**
     * Invoked when a component loses the keyboard focus.
     */
    public void focusLost(java.awt.event.FocusEvent e) {
        updateFields(e);
    }

    /**
     * Return the GeneralNumberDataType property value.
     *
     * @return cz.omnicom.ermodeller.datatype.GeneralNumberDataType
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    protected GeneralNumberDataType getGeneralNumberDataType() {
        if (ivjGeneralNumberDataType == null) {
            try {
                ivjGeneralNumberDataType = new cz.omnicom.ermodeller.datatype.GeneralNumberDataType();
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjGeneralNumberDataType;
    }

    /**
     * Return the PrecisionDownButton property value.
     *
     * @return javax.swing.JButton
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    protected javax.swing.JButton getPrecisionDownButton() {
        if (ivjPrecisionDownButton == null) {
            try {
                ivjPrecisionDownButton = new javax.swing.JButton();
                ivjPrecisionDownButton.setName("PrecisionDownButton");
//			ivjPrecisionDownButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/left.gif")));
                ivjPrecisionDownButton.setToolTipText("Count down");
                ivjPrecisionDownButton.setText("");
                ivjPrecisionDownButton.setBounds(70, 23, 20, 25);
                ivjPrecisionDownButton.setActionCommand("PrecisionDown");
                // user code begin {1}
                ivjPrecisionDownButton.setIcon(new ImageIcon(ClassLoader.getSystemResource("img/left.gif")));
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjPrecisionDownButton;
    }

    /**
     * Return the PrecisionLabel property value.
     *
     * @return javax.swing.JLabel
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    protected javax.swing.JLabel getPrecisionLabel() {
        if (ivjPrecisionLabel == null) {
            try {
                ivjPrecisionLabel = new javax.swing.JLabel();
                ivjPrecisionLabel.setName("PrecisionLabel");
                ivjPrecisionLabel.setText("Precision:");
                ivjPrecisionLabel.setBounds(5, 5, 61, 15);
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjPrecisionLabel;
    }

    /**
     * Return the JTextField1 property value.
     *
     * @return javax.swing.JTextField
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    protected javax.swing.JTextField getPrecisionTextField() {
        if (ivjPrecisionTextField == null) {
            try {
                ivjPrecisionTextField = new javax.swing.JTextField();
                ivjPrecisionTextField.setName("PrecisionTextField");
                ivjPrecisionTextField.setToolTipText("Number of digits");
                ivjPrecisionTextField.setBackground(java.awt.SystemColor.activeCaptionText);
                ivjPrecisionTextField.setBounds(5, 26, 63, 19);
                ivjPrecisionTextField.setEditable(true);
                // user code begin {1}
                ivjPrecisionTextField.addKeyListener(this);
                ivjPrecisionTextField.addFocusListener(this);
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjPrecisionTextField;
    }

    /**
     * Return the PrecisionUpButton property value.
     *
     * @return javax.swing.JButton
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    protected javax.swing.JButton getPrecisionUpButton() {
        if (ivjPrecisionUpButton == null) {
            try {
                ivjPrecisionUpButton = new javax.swing.JButton();
                ivjPrecisionUpButton.setName("PrecisionUpButton");
//			ivjPrecisionUpButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/right.gif")));
                ivjPrecisionUpButton.setToolTipText("Count up");
                ivjPrecisionUpButton.setText("");
                ivjPrecisionUpButton.setBounds(90, 23, 20, 25);
                ivjPrecisionUpButton.setActionCommand("PrecisionUp");
                // user code begin {1}
                ivjPrecisionUpButton.setIcon(new ImageIcon(ClassLoader.getSystemResource("img/right.gif")));
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjPrecisionUpButton;
    }

    /**
     * This method was created by Aleš Kopecký.
     *
     * @return java.awt.Dimension
     */
    public Dimension getPreferredSize() {
        return new Dimension(114, 109);
    }

    /**
     * Return the ScaleDownButton property value.
     *
     * @return javax.swing.JButton
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    protected javax.swing.JButton getScaleDownButton() {
        if (ivjScaleDownButton == null) {
            try {
                ivjScaleDownButton = new javax.swing.JButton();
                ivjScaleDownButton.setName("ScaleDownButton");
//			ivjScaleDownButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/left.gif")));
                ivjScaleDownButton.setToolTipText("Count down");
                ivjScaleDownButton.setText("");
                ivjScaleDownButton.setBounds(70, 79, 20, 25);
                ivjScaleDownButton.setActionCommand("ScaleDown");
                // user code begin {1}
                ivjScaleDownButton.setIcon(new ImageIcon(ClassLoader.getSystemResource("img/left.gif")));
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjScaleDownButton;
    }

    /**
     * Return the ScaleLabel property value.
     *
     * @return javax.swing.JLabel
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    protected javax.swing.JLabel getScaleLabel() {
        if (ivjScaleLabel == null) {
            try {
                ivjScaleLabel = new javax.swing.JLabel();
                ivjScaleLabel.setName("ScaleLabel");
                ivjScaleLabel.setText("Scale:");
                ivjScaleLabel.setBounds(5, 61, 45, 15);
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjScaleLabel;
    }

    /**
     * Return the JTextField2 property value.
     *
     * @return javax.swing.JTextField
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    protected javax.swing.JTextField getScaleTextField() {
        if (ivjScaleTextField == null) {
            try {
                ivjScaleTextField = new javax.swing.JTextField();
                ivjScaleTextField.setName("ScaleTextField");
                ivjScaleTextField.setToolTipText("Number of decimal places");
                ivjScaleTextField.setBackground(java.awt.SystemColor.activeCaptionText);
                ivjScaleTextField.setBounds(5, 82, 63, 19);
                ivjScaleTextField.setEditable(true);
                // user code begin {1}
                ivjScaleTextField.addKeyListener(this);
                ivjScaleTextField.addFocusListener(this);
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjScaleTextField;
    }

    /**
     * Return the ScaleUpButton property value.
     *
     * @return javax.swing.JButton
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    protected javax.swing.JButton getScaleUpButton() {
        if (ivjScaleUpButton == null) {
            try {
                ivjScaleUpButton = new javax.swing.JButton();
                ivjScaleUpButton.setName("ScaleUpButton");
//			ivjScaleUpButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/right.gif")));
                ivjScaleUpButton.setToolTipText("Count Up");
                ivjScaleUpButton.setText("");
                ivjScaleUpButton.setBounds(90, 79, 20, 25);
                ivjScaleUpButton.setActionCommand("ScaleUp");
                // user code begin {1}
                ivjScaleUpButton.setIcon(new ImageIcon(ClassLoader.getSystemResource("img/right.gif")));
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjScaleUpButton;
    }

    /**
     * This method was created by Aleš Kopecký.
     *
     * @return javax.swing.Timer
     */
    Timer getTimer() {
        if (timer == null) {
            timer = new Timer(100, new TimerListener());
            timer.setInitialDelay(500);
            timer.setRepeats(true);
            timer.addActionListener(new TimerListener());
        }
        return timer;
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
        // user code begin {1}
        // user code end
        getPrecisionDownButton().addMouseListener(this);
        getPrecisionUpButton().addMouseListener(this);
        getScaleUpButton().addMouseListener(this);
        getScaleDownButton().addMouseListener(this);
        getPrecisionDownButton().addActionListener(this);
        getPrecisionUpButton().addActionListener(this);
        getScaleUpButton().addActionListener(this);
        getScaleDownButton().addActionListener(this);
    }

    /**
     * Initialize the class.
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    protected void initialize() {
        try {
            // user code begin {1}
            // user code end
            setName("GeneralNumberDataTypePanel");
            setLayout(null);
            setSize(114, 109);
            add(getPrecisionTextField(), getPrecisionTextField().getName());
            add(getScaleTextField(), getScaleTextField().getName());
            add(getPrecisionLabel(), getPrecisionLabel().getName());
            add(getScaleLabel(), getScaleLabel().getName());
            add(getPrecisionDownButton(), getPrecisionDownButton().getName());
            add(getPrecisionUpButton(), getPrecisionUpButton().getName());
            add(getScaleDownButton(), getScaleDownButton().getName());
            add(getScaleUpButton(), getScaleUpButton().getName());
            initConnections();
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
        // user code begin {2}
        // user code end
    }

    /**
     * Invoked when a key has been pressed.
     */
    public void keyPressed(java.awt.event.KeyEvent e) {
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
            GeneralNumberDataTypePanel aGeneralNumberDataTypePanel;
            aGeneralNumberDataTypePanel = new GeneralNumberDataTypePanel();
            frame.add("Center", aGeneralNumberDataTypePanel);
            frame.setSize(aGeneralNumberDataTypePanel.getSize());
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
        // user code begin {1}
        // user code end
        // user code begin {2}
        // user code end
    }

    /**
     * Method to handle events for the MouseListener interface.
     *
     * @param e java.awt.event.MouseEvent
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    public void mouseEntered(java.awt.event.MouseEvent e) {
        // user code begin {1}
        // user code end
        // user code begin {2}
        // user code end
    }

    /**
     * Method to handle events for the MouseListener interface.
     *
     * @param e java.awt.event.MouseEvent
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    public void mouseExited(java.awt.event.MouseEvent e) {
        // user code begin {1}
        // user code end
        // user code begin {2}
        // user code end
    }

    /**
     * Method to handle events for the MouseListener interface.
     *
     * @param e java.awt.event.MouseEvent
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    public void mousePressed(java.awt.event.MouseEvent e) {
        // user code begin {1}
        // user code end
        if (e.getSource() == getPrecisionDownButton())
            connEtoC5(e);
        if (e.getSource() == getPrecisionUpButton())
            connEtoC7(e);
        if (e.getSource() == getScaleUpButton())
            connEtoC11(e);
        if (e.getSource() == getScaleDownButton())
            connEtoC12(e);
        // user code begin {2}
        // user code end
    }

    /**
     * Method to handle events for the MouseListener interface.
     *
     * @param e java.awt.event.MouseEvent
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    public void mouseReleased(java.awt.event.MouseEvent e) {
        // user code begin {1}
        // user code end
        if (e.getSource() == getPrecisionDownButton())
            connEtoC6(e);
        if (e.getSource() == getPrecisionUpButton())
            connEtoC8(e);
        if (e.getSource() == getScaleUpButton())
            connEtoC9(e);
        if (e.getSource() == getScaleDownButton())
            connEtoC10(e);
        // user code begin {2}
        // user code end
    }

    /**
     * Starts timer and decreasing of precision.
     *
     * @see #timer
     */
    void precisionDownButton_MousePressed(java.awt.event.MouseEvent mouseEvent) {
        direction = DOWN;
        value = PRECISION;
        getTimer().restart();
    }

    /**
     * Stops timer.
     *
     * @see #timer
     */
    void precisionDownButton_MouseReleased(java.awt.event.MouseEvent mouseEvent) {
        getTimer().stop();
    }

    /**
     * Starts timer and precision increasing.
     *
     * @see #timer
     */
    void precisionUpButton_MousePressed(java.awt.event.MouseEvent mouseEvent) {
        direction = UP;
        value = PRECISION;
        getTimer().restart();
    }

    /**
     * Stops timer.
     *
     * @see #timer
     */
    void precisionUpButton_MouseReleased(java.awt.event.MouseEvent mouseEvent) {
        getTimer().stop();
    }

    /**
     * Listens for <code>PropertyChange</code> events from customized datatype.
     *
     * @param anEvent java.beans.PropertyChangeEvent
     */
    public void propertyChange(PropertyChangeEvent anEvent) {
        if (anEvent.getPropertyName().equals(GeneralNumberDataType.PRECISION_PROPERTY_CHANGE)) {
            getPrecisionTextField().setText(anEvent.getNewValue().toString());
            return;
        }
        if (anEvent.getPropertyName().equals(GeneralNumberDataType.SCALE_PROPERTY_CHANGE)) {
            getScaleTextField().setText(anEvent.getNewValue().toString());
            return;
        }
        firePropertyChange(anEvent.getPropertyName(), anEvent.getOldValue(), anEvent.getNewValue());
    }

    /**
     * Starts timer and decreasing of scale.
     *
     * @see #timer
     */
    void scaleDownButton_MousePressed(java.awt.event.MouseEvent mouseEvent) {
        direction = DOWN;
        value = SCALE;
        getTimer().restart();
    }

    /**
     * Stops timer.
     *
     * @see #timer
     */
    void scaleDownButton_MouseReleased(java.awt.event.MouseEvent mouseEvent) {
        getTimer().stop();
    }

    /**
     * Starts timer and increasing of scale.
     *
     * @see #timer
     */
    void scaleUpButton_MousePressed(java.awt.event.MouseEvent mouseEvent) {
        direction = UP;
        value = SCALE;
        getTimer().restart();
    }

    /**
     * Stops timer.
     *
     * @see #timer
     */
    void scaleUpButton_MouseReleased(java.awt.event.MouseEvent mouseEvent) {
        getTimer().stop();
    }

    /**
     * Sets dataType and starts listening to <code>PropertyChange</code> events.
     *
     * @param aDataType cz.omnicom.ermodeller.datatype.GeneralNumberDataType
     */
    public void setGeneralNumberDataType(GeneralNumberDataType aDataType) {
        ivjGeneralNumberDataType = aDataType;
        getPrecisionTextField().setText(Integer.toString(ivjGeneralNumberDataType.getPrecision()));
        getScaleTextField().setText(Integer.toString(ivjGeneralNumberDataType.getScale()));
        ivjGeneralNumberDataType.addPropertyChangeListener(this);
    }

    /**
     * Invoked when a key has been pressed.
     */
    protected void updateFields(ComponentEvent e) {
        int i;
        Integer in;

        if (e.getSource() == ivjPrecisionTextField)
            try {
                i = Integer.parseInt(getPrecisionTextField().getText());
                if (getGeneralNumberDataType().evaluatePrecision(i)) {
                    ivjGeneralNumberDataType = (GeneralNumberDataType) getGeneralNumberDataType().clone();
                    getGeneralNumberDataType().setPrecision(i);
                    ((DataTypeEditor) getDataTypeEditor()).setValue(ivjGeneralNumberDataType);
                } else {
                    in = new Integer(getGeneralNumberDataType().getPrecision());
                    getPrecisionTextField().setText(in.toString());
                }
            }
            catch (Exception ex) {
                in = new Integer(getGeneralNumberDataType().getPrecision());
                getPrecisionTextField().setText(in.toString());
            }
        if (e.getSource() == ivjScaleTextField)
            try {
                i = Integer.parseInt(getScaleTextField().getText());
                if (getGeneralNumberDataType().evaluateScale(i)) {
                    ivjGeneralNumberDataType = (GeneralNumberDataType) getGeneralNumberDataType().clone();
                    getGeneralNumberDataType().setScale(i);
                    ((DataTypeEditor) getDataTypeEditor()).setValue(ivjGeneralNumberDataType);
                } else {
                    in = new Integer(getGeneralNumberDataType().getScale());
                    getScaleTextField().setText(in.toString());
                }
            }
            catch (Exception ex) {
                in = new Integer(getGeneralNumberDataType().getScale());
                getScaleTextField().setText(in.toString());
            }
    }
}