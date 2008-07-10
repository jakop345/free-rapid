package cz.felk.cvut.erm.dialogs;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * This type was created in VisualAge.
 */
public class GenerateDialog extends JDialog implements ActionListener, ItemListener {
    private JCheckBox ivjdropCheckBox = null;
    private JCheckBox ivjGlueCheckBox = null;
    private JPanel ivjJDialogContentPane = null;
    private JPanel ivjJPanel1 = null;
    private JCheckBox ivjshortenCheckBox = null;
    private boolean result = GENDIALOG_CANCEL;
    public static final boolean GENDIALOG_CANCEL = false;
    public static final boolean GENDIALOG_OK = true;
    private JPanel ivjDownPanel = null;
    private JPanel ivjJPanel11 = null;
    private GridLayout ivjJPanel11GridLayout = null;
    private boolean defaultGlue = false;
    private boolean genDrop = true;
    private boolean shortenPrefixes = true;
    private JButton ivjCancelButton = null;
    private JButton ivjOKButton = null;

    /**
     * Constructor
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    public GenerateDialog() {
        super();
        initialize();
    }

    /**
     * GenerateDialog constructor comment.
     *
     * @param owner java.awt.Frame
     */
    public GenerateDialog(java.awt.Frame owner) {
        super(owner);
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
        if (e.getSource() == getOKButton())
            connEtoC2(e);
        if (e.getSource() == getCancelButton())
            connEtoC1(e);
        // user code begin {2}
        // user code end
    }

    /**
     * Comment
     */
    public void cancelButton_ActionPerformed1(java.awt.event.ActionEvent actionEvent) {
        result = GENDIALOG_CANCEL;
        dispose();
    }

    /**
     * connEtoC1:  (cancelButton.action.actionPerformed(java.awt.event.ActionEvent) --> GenerateDialog.cancelButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
     *
     * @param arg1 java.awt.event.ActionEvent
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private void connEtoC1(java.awt.event.ActionEvent arg1) {
        try {
            // user code begin {1}
            // user code end
            this.cancelButton_ActionPerformed1(arg1);
            // user code begin {2}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connEtoC2:  (okButton.action.actionPerformed(java.awt.event.ActionEvent) --> GenerateDialog.okButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
     *
     * @param arg1 java.awt.event.ActionEvent
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private void connEtoC2(java.awt.event.ActionEvent arg1) {
        try {
            // user code begin {1}
            // user code end
            this.oKButton_ActionPerformed(arg1);
            // user code begin {2}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connEtoC3:  (GlueCheckBox.item.itemStateChanged(java.awt.event.ItemEvent) --> GenerateDialog.glueCheckBox_ItemStateChanged(Ljava.awt.event.ItemEvent;)V)
     *
     * @param arg1 java.awt.event.ItemEvent
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private void connEtoC3(java.awt.event.ItemEvent arg1) {
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
     * connEtoC4:  (shortenCheckBox.item.itemStateChanged(java.awt.event.ItemEvent) --> GenerateDialog.shortenCheckBox_ItemStateChanged(Ljava.awt.event.ItemEvent;)V)
     *
     * @param arg1 java.awt.event.ItemEvent
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private void connEtoC4(java.awt.event.ItemEvent arg1) {
        try {
            // user code begin {1}
            // user code end
            this.shortenCheckBox_ItemStateChanged(arg1);
            // user code begin {2}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connEtoC5:  (dropCheckBox.item.itemStateChanged(java.awt.event.ItemEvent) --> GenerateDialog.dropCheckBox_ItemStateChanged(Ljava.awt.event.ItemEvent;)V)
     *
     * @param arg1 java.awt.event.ItemEvent
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private void connEtoC5(java.awt.event.ItemEvent arg1) {
        try {
            // user code begin {1}
            // user code end
            this.dropCheckBox_ItemStateChanged(arg1);
            // user code begin {2}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * Comment
     */
    public void dropCheckBox_ItemStateChanged(java.awt.event.ItemEvent itemEvent) {
        setGenDrop(itemEvent.getStateChange() == ItemEvent.SELECTED);
    }

    /**
     * Return the okButton property value.
     *
     * @return javax.swing.JButton
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JButton getCancelButton() {
        if (ivjCancelButton == null) {
            try {
                ivjCancelButton = new javax.swing.JButton();
                ivjCancelButton.setName("CancelButton");
                ivjCancelButton.setToolTipText("Cancel generating.");
                ivjCancelButton.setSelected(true);
                ivjCancelButton.setText("Cancel");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjCancelButton;
    }

    /**
     * @return boolean
     */
    public boolean getDefaultGlue() {
        return defaultGlue;
    }

    /**
     * Return the DownPanel property value.
     *
     * @return javax.swing.JPanel
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JPanel getDownPanel() {
        if (ivjDownPanel == null) {
            try {
                ivjDownPanel = new javax.swing.JPanel();
                ivjDownPanel.setName("DownPanel");
                ivjDownPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(6, 0, 0, 0));
                ivjDownPanel.setLayout(new java.awt.BorderLayout());
                getDownPanel().add(getJPanel11(), "East");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjDownPanel;
    }

    /**
     * Return the dropCheckBox property value.
     *
     * @return javax.swing.JCheckBox
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JCheckBox getdropCheckBox() {
        if (ivjdropCheckBox == null) {
            try {
                ivjdropCheckBox = new javax.swing.JCheckBox();
                ivjdropCheckBox.setName("dropCheckBox");
                ivjdropCheckBox.setToolTipText("Generate drop clauses in final SQL");
                ivjdropCheckBox.setSelected(true);
                ivjdropCheckBox.setText("Generate drop clauses");
                ivjdropCheckBox.setBounds(5, 5, 168, 25);
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjdropCheckBox;
    }

    /**
     * This method was created in VisualAge.
     *
     * @return boolean
     */
    public boolean getGenDrop() {
        return genDrop;
    }

    /**
     * Return the GlueCheckBox property value.
     *
     * @return javax.swing.JCheckBox
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JCheckBox getGlueCheckBox() {
        if (ivjGlueCheckBox == null) {
            try {
                ivjGlueCheckBox = new javax.swing.JCheckBox();
                ivjGlueCheckBox.setName("GlueCheckBox");
                ivjGlueCheckBox.setToolTipText("Uncheck to supress merging set by user");
                ivjGlueCheckBox.setSelected(true);
                ivjGlueCheckBox.setText("User merging");
                ivjGlueCheckBox.setBounds(5, 59, 127, 25);
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
     * Return the JDialogContentPane property value.
     *
     * @return javax.swing.JPanel
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JPanel getJDialogContentPane() {
        if (ivjJDialogContentPane == null) {
            try {
                ivjJDialogContentPane = new javax.swing.JPanel();
                ivjJDialogContentPane.setName("JDialogContentPane");
                ivjJDialogContentPane.setPreferredSize(new java.awt.Dimension(0, 0));
                ivjJDialogContentPane.setBorder(javax.swing.BorderFactory.createEmptyBorder(6, 5, 5, 5));
                ivjJDialogContentPane.setLayout(new java.awt.BorderLayout());
                ivjJDialogContentPane.setMinimumSize(new java.awt.Dimension(0, 0));
                getJDialogContentPane().add(getDownPanel(), "South");
                getJDialogContentPane().add(getJPanel1(), "Center");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjJDialogContentPane;
    }

    /**
     * Return the JPanel1 property value.
     *
     * @return javax.swing.JPanel
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JPanel getJPanel1() {
        if (ivjJPanel1 == null) {
            try {
                ivjJPanel1 = new javax.swing.JPanel();
                ivjJPanel1.setName("JPanel1");
                ivjJPanel1.setLayout(null);
                getJPanel1().add(getdropCheckBox(), getdropCheckBox().getName());
                getJPanel1().add(getshortenCheckBox(), getshortenCheckBox().getName());
                getJPanel1().add(getGlueCheckBox(), getGlueCheckBox().getName());
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjJPanel1;
    }

    /**
     * Return the JPanel11 property value.
     *
     * @return javax.swing.JPanel
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JPanel getJPanel11() {
        if (ivjJPanel11 == null) {
            try {
                ivjJPanel11 = new javax.swing.JPanel();
                ivjJPanel11.setName("JPanel11");
                ivjJPanel11.setLayout(getJPanel11GridLayout());
                getJPanel11().add(getCancelButton(), getCancelButton().getName());
                getJPanel11().add(getOKButton(), getOKButton().getName());
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjJPanel11;
    }

    /**
     * Return the JPanel11GridLayout property value.
     *
     * @return java.awt.GridLayout
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private java.awt.GridLayout getJPanel11GridLayout() {
        java.awt.GridLayout ivjJPanel11GridLayout = null;
        try {
            /* Create part */
            ivjJPanel11GridLayout = new java.awt.GridLayout();
            ivjJPanel11GridLayout.setVgap(5);
            ivjJPanel11GridLayout.setHgap(5);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
        return ivjJPanel11GridLayout;
    }

    /**
     * Return the cancelButton property value.
     *
     * @return javax.swing.JButton
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JButton getOKButton() {
        if (ivjOKButton == null) {
            try {
                ivjOKButton = new javax.swing.JButton();
                ivjOKButton.setName("OKButton");
                ivjOKButton.setToolTipText("Proceed");
                ivjOKButton.setPreferredSize(new java.awt.Dimension(89, 25));
                ivjOKButton.setText("OK");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjOKButton;
    }

    /**
     * This method was created in VisualAge.
     *
     * @return boolean
     */
    public boolean getResult() {
        return result;
    }

    /**
     * Return the shortenCheckBox property value.
     *
     * @return javax.swing.JCheckBox
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JCheckBox getshortenCheckBox() {
        if (ivjshortenCheckBox == null) {
            try {
                ivjshortenCheckBox = new javax.swing.JCheckBox();
                ivjshortenCheckBox.setName("shortenCheckBox");
                ivjshortenCheckBox.setToolTipText("Check to shorten prefixes added to atributes during generating");
                ivjshortenCheckBox.setSelected(true);
                ivjshortenCheckBox.setText("Shorten prefixes of atributes");
                ivjshortenCheckBox.setBounds(5, 32, 205, 25);
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjshortenCheckBox;
    }

    /**
     * This method was created in VisualAge.
     *
     * @return boolean
     */
    public boolean getShortenPrefixes() {
        return shortenPrefixes;
    }

    /**
     * Comment
     */
    public void glueCheckBox_ItemStateChanged(java.awt.event.ItemEvent itemEvent) {
        setDefaultGlue(itemEvent.getStateChange() == ItemEvent.DESELECTED);
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
        getGlueCheckBox().addItemListener(this);
        getshortenCheckBox().addItemListener(this);
        getdropCheckBox().addItemListener(this);
        getOKButton().addActionListener(this);
        getCancelButton().addActionListener(this);
    }

    /**
     * Initialize the class.
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private void initialize() {
        try {
            // user code begin {1}
            // user code end
            setName("GenerateDialog");
            setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
            setResizable(false);
            setVisible(false);
            setModal(true);
            setSize(299, 196);
            setTitle("Generate options");
            setContentPane(getJDialogContentPane());
            initConnections();
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
        // user code begin {2}
        getGlueCheckBox().setSelected(true);
        getdropCheckBox().setSelected(true);
        getshortenCheckBox().setSelected(true);
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
        if (e.getSource() == getGlueCheckBox())
            connEtoC3(e);
        if (e.getSource() == getshortenCheckBox())
            connEtoC4(e);
        if (e.getSource() == getdropCheckBox())
            connEtoC5(e);
        // user code begin {2}
        // user code end
    }

    /**
     * Comment
     */
    public void jButton1_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
        result = GENDIALOG_CANCEL;
        dispose();
    }

    /**
     * main entrypoint - starts the part when it is run as an application
     *
     * @param args java.lang.String[]
     */
    public static void main(java.lang.String[] args) {
        try {
            GenerateDialog aGenerateDialog;
            aGenerateDialog = new GenerateDialog();
            aGenerateDialog.setModal(true);
            try {
                Class aCloserClass = Class.forName("com.ibm.uvm.abt.edit.WindowCloser");
                Class parmTypes[] = {java.awt.Window.class};
                Object parms[] = {aGenerateDialog};
                java.lang.reflect.Constructor aCtor = aCloserClass.getConstructor(parmTypes);
                aCtor.newInstance(parms);
            } catch (java.lang.Throwable exc) {
            }
            aGenerateDialog.setVisible(true);
        } catch (Throwable exception) {
            System.err.println("Exception occurred in main() of javax.swing.JDialog");
            exception.printStackTrace(System.out);
        }
    }

    /**
     * Comment
     */
    public void oKButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
        result = GENDIALOG_OK;
        dispose();
    }

    /**
     * @param newValue boolean
     */
    private void setDefaultGlue(boolean newValue) {
        this.defaultGlue = newValue;
    }

    /**
     * This method was created in VisualAge.
     *
     * @param newValue boolean
     */
    private void setGenDrop(boolean newValue) {
        this.genDrop = newValue;
    }

    /**
     * This method was created in VisualAge.
     *
     * @param newValue boolean
     */
    private void setShortenPrefixes(boolean newValue) {
        this.shortenPrefixes = newValue;
    }

    /**
     * Comment
     */
    public void shortenCheckBox_ItemStateChanged(java.awt.event.ItemEvent itemEvent) {
        setShortenPrefixes(itemEvent.getStateChange() == ItemEvent.SELECTED);
    }
}
