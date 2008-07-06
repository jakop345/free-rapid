package cz.felk.cvut.erm.conc2obj;

import cz.felk.cvut.erm.conc2obj.interfaces.SubObjProducer;
import cz.felk.cvut.erm.ermodeller.AppPrefs;
import cz.felk.cvut.erm.icontree.IconNodeRenderer;
import cz.felk.cvut.erm.sql.gui.SQLConnection;
import cz.felk.cvut.erm.swing.ExtensionFileFilter;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.StringTokenizer;

/**
 * Dialog presenting SQL command in the tree.
 */
public class ObjDialog extends JDialog implements java.awt.event.ActionListener, javax.swing.event.TreeSelectionListener {
    private JPanel ivjJDialogContentPane = null;
    private JScrollPane ivjJScrollPane1 = null;
    private JSplitPane ivjJSplitPane1 = null;
    private JScrollPane ivjJScrollPane2 = null;
    private JButton ivjCloseButton = null;
    private JPanel ivjDownPanel = null;
    private SchemaObjSQL ivjSchemaObjSQL = null;
    private JTree ivjSQLTree = null;
    private JTextArea ivjJTextArea1 = null;
    private JPanel ivjJPanel1 = null;
    private JButton ivjSaveButton = null;
    private JFileChooser ivjJFileChooser2 = null;
    private JDialog ivjJDialog1 = null;
    private JPanel ivjJDialogContentPane1 = null;
    private DefaultMutableTreeNode ivjRoot = null;
    private String fileName = null;
    private cz.felk.cvut.erm.sql.gui.SQLConnection connection = null;
    private JButton ivjSendButton = null;
    private JButton ivjViewButton = null;
    private JPanel ivjSVPanel = null;

    /**
     * Constructor
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    public ObjDialog() {
        super();
        initialize();
    }

    /**
     * SQLDialog constructor comment.
     *
     * @param owner java.awt.Frame
     */
    public ObjDialog(Frame owner, SchemaObjSQL aSchemaObjSQL) {
        super(owner);
        setSchemaObj(aSchemaObjSQL);
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
        if (e.getSource() == getCloseButton())
            connEtoC1();
        if (e.getSource() == getSaveButton())
            connEtoC3(e);
        if (e.getSource() == getSendButton())
            connEtoC4(e);
        if (e.getSource() == getViewButton())
            connEtoC5();
        // user code begin {2}
        // user code end
    }

    /**
     * Closes and disposes the dialog.
     */
    public void closeButton_ActionEvents() {
        setVisible(false);
    }

    /**
     * connEtoC1:  (CloseButton.action. --> SQLDialog.closeButton_ActionEvents()V)
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private void connEtoC1() {
        try {
            // user code begin {1}
            // user code end
            this.closeButton_ActionEvents();
            // user code begin {2}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connEtoC2:  (SQLTree.treeSelection.valueChanged(javax.swing.event.TreeSelectionEvent) --> SQLDialog.sQLTree_ValueChanged(Ljavax.swing.event.TreeSelectionEvent;)V)
     *
     * @param arg1 javax.swing.event.TreeSelectionEvent
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private void connEtoC2(javax.swing.event.TreeSelectionEvent arg1) {
        try {
            // user code begin {1}
            // user code end
            this.objTree_ValueChanged(arg1);
            // user code begin {2}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connEtoC3:  (SaveButton.action.actionPerformed(java.awt.event.ActionEvent) --> SQLDialog.saveButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
     *
     * @param arg1 java.awt.event.ActionEvent
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private void connEtoC3(java.awt.event.ActionEvent arg1) {
        try {
            // user code begin {1}
            // user code end
            this.saveButton_ActionPerformed(arg1);
            // user code begin {2}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connEtoC4:  (SendButton.action.actionPerformed(java.awt.event.ActionEvent) --> SQLDialog.sendButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
     *
     * @param arg1 java.awt.event.ActionEvent
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private void connEtoC4(java.awt.event.ActionEvent arg1) {
        try {
            // user code begin {1}
            // user code end
            this.sendButton_ActionPerformed(arg1);
            // user code begin {2}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connEtoC5:  (ViewButton.action. --> SQLDialog.viewButton_ActionEvents()V)
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private void connEtoC5() {
        try {
            // user code begin {1}
            // user code end
            this.viewButton_ActionEvents();
            // user code begin {2}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * Creates new tree and shows it.
     *
     * @see cz.felk.cvut.erm.sql.SchemaSQL#createTree
     */
    private void createTree() {
        ivjRoot = getSchemaObj().createTree();
        ivjSQLTree = new JTree(ivjRoot, true);

        ivjSQLTree.setUI(new javax.swing.plaf.basic.BasicTreeUI());
        ivjSQLTree.setName("SQLTree");
        ivjSQLTree.setAutoscrolls(true);
        ivjSQLTree.setOpaque(true);
//	ivjSQLTree.setBorder(new BasicFieldBorder());
        ivjSQLTree.setCellRenderer(new IconNodeRenderer());
        ivjSQLTree.setShowsRootHandles(false);
        ivjSQLTree.setRootVisible(true);
        ivjSQLTree.setVisibleRowCount(20);
        getJScrollPane1().setViewportView(ivjSQLTree);
        ivjSQLTree.addTreeSelectionListener(this);
        getJTextArea1().setText("");
        if (!isVisible())
            getJSplitPane1().setDividerLocation(400);
    }

    /**
     * Return the CloseButton property value.
     *
     * @return javax.swing.JButton
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JButton getCloseButton() {
        if (ivjCloseButton == null) {
            try {
                ivjCloseButton = new javax.swing.JButton();
                ivjCloseButton.setName("CloseButton");
                ivjCloseButton.setText("Close");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjCloseButton;
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
                getDownPanel().add(getJPanel1(), "East");
                getDownPanel().add(getSVPanel(), "West");
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
     * Return the JDialog1 property value.
     *
     * @return javax.swing.JDialog
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JDialog getJDialog1() {
        if (ivjJDialog1 == null) {
            try {
                ivjJDialog1 = new javax.swing.JDialog();
                ivjJDialog1.setName("JDialog1");
                ivjJDialog1.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
                ivjJDialog1.setBounds(143, 546, 435, 252);
                ivjJDialog1.setModal(true);
                ivjJDialog1.setTitle("Save SQL");
                getJDialog1().setContentPane(getJDialogContentPane1());
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjJDialog1;
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
                ivjJDialogContentPane.setBorder(javax.swing.BorderFactory.createEmptyBorder(6, 5, 5, 5));
                ivjJDialogContentPane.setLayout(getJDialogContentPaneBorderLayout());
                getJDialogContentPane().add(getDownPanel(), "South");
                getJDialogContentPane().add(getJSplitPane1(), "Center");
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
     * Return the JDialogContentPane1 property value.
     *
     * @return javax.swing.JPanel
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JPanel getJDialogContentPane1() {
        if (ivjJDialogContentPane1 == null) {
            try {
                ivjJDialogContentPane1 = new javax.swing.JPanel();
                ivjJDialogContentPane1.setName("JDialogContentPane1");
                ivjJDialogContentPane1.setLayout(new javax.swing.BoxLayout(getJDialogContentPane1(), javax.swing.BoxLayout.X_AXIS));
                getJDialogContentPane1().add(getJFileChooser2(), getJFileChooser2().getName());
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjJDialogContentPane1;
    }

    /**
     * Return the JDialogContentPaneBorderLayout property value.
     *
     * @return java.awt.BorderLayout
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private java.awt.BorderLayout getJDialogContentPaneBorderLayout() {
        java.awt.BorderLayout ivjJDialogContentPaneBorderLayout = null;
        try {
            /* Create part */
            ivjJDialogContentPaneBorderLayout = new java.awt.BorderLayout();
            ivjJDialogContentPaneBorderLayout.setVgap(0);
            ivjJDialogContentPaneBorderLayout.setHgap(0);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
        return ivjJDialogContentPaneBorderLayout;
    }

    /**
     * Return the JFileChooser2 property value.
     *
     * @return javax.swing.JFileChooser
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JFileChooser getJFileChooser2() {
        if (ivjJFileChooser2 == null) {
            try {
                ivjJFileChooser2 = new javax.swing.JFileChooser();
                ivjJFileChooser2.setName("JFileChooser2");
                // user code begin {1}
                ExtensionFileFilter ff = new ExtensionFileFilter("sql", "SQL batch files (*.sql)");
                ivjJFileChooser2.addChoosableFileFilter(ff);
                ivjJFileChooser2.setFileFilter(ff);
                ff = new ExtensionFileFilter("txt", "Text files (*.txt)");
                ivjJFileChooser2.addChoosableFileFilter(ff);
                ivjJFileChooser2.addChoosableFileFilter(ivjJFileChooser2.getAcceptAllFileFilter());
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjJFileChooser2;
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
                ivjJPanel1.setLayout(getJPanel1GridLayout());
                getJPanel1().add(getSaveButton(), getSaveButton().getName());
                getJPanel1().add(getCloseButton(), getCloseButton().getName());
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
     * Return the JPanel1GridLayout property value.
     *
     * @return java.awt.GridLayout
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private java.awt.GridLayout getJPanel1GridLayout() {
        java.awt.GridLayout ivjJPanel1GridLayout = null;
        try {
            /* Create part */
            ivjJPanel1GridLayout = new java.awt.GridLayout();
            ivjJPanel1GridLayout.setVgap(5);
            ivjJPanel1GridLayout.setHgap(5);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
        return ivjJPanel1GridLayout;
    }

    /**
     * Return the JScrollPane1 property value.
     *
     * @return javax.swing.JScrollPane
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JScrollPane getJScrollPane1() {
        if (ivjJScrollPane1 == null) {
            try {
                ivjJScrollPane1 = new javax.swing.JScrollPane();
                ivjJScrollPane1.setName("JScrollPane1");
                getJScrollPane1().setViewportView(getSQLTree());
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjJScrollPane1;
    }

    /**
     * Return the JScrollPane2 property value.
     *
     * @return javax.swing.JScrollPane
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JScrollPane getJScrollPane2() {
        if (ivjJScrollPane2 == null) {
            try {
                ivjJScrollPane2 = new javax.swing.JScrollPane();
                ivjJScrollPane2.setName("JScrollPane2");
                getJScrollPane2().setViewportView(getJTextArea1());
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjJScrollPane2;
    }

    /**
     * Return the JSplitPane1 property value.
     *
     * @return javax.swing.JSplitPane
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JSplitPane getJSplitPane1() {
        if (ivjJSplitPane1 == null) {
            try {
                ivjJSplitPane1 = new javax.swing.JSplitPane(javax.swing.JSplitPane.HORIZONTAL_SPLIT);
                ivjJSplitPane1.setName("JSplitPane1");
                ivjJSplitPane1.setDividerSize(5);
                ivjJSplitPane1.setLastDividerLocation(450);
                ivjJSplitPane1.setDividerLocation(450);
                getJSplitPane1().add(getJScrollPane1(), "left");
                getJSplitPane1().add(getJScrollPane2(), "right");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjJSplitPane1;
    }

    /**
     * Return the JTextArea1 property value.
     *
     * @return javax.swing.JTextArea
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JTextArea getJTextArea1() {
        if (ivjJTextArea1 == null) {
            try {
                ivjJTextArea1 = new javax.swing.JTextArea();
                ivjJTextArea1.setName("JTextArea1");
                ivjJTextArea1.setBounds(0, 0, 164, 109);
                ivjJTextArea1.setEditable(false);
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjJTextArea1;
    }

    /**
     * Return the Root property value.
     *
     * @return javax.swing.tree.DefaultMutableTreeNode
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.tree.DefaultMutableTreeNode getRoot() {
        if (ivjRoot == null) {
            try {
                ivjRoot = new javax.swing.tree.DefaultMutableTreeNode();
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjRoot;
    }

    /**
     * Return the JButton1 property value.
     *
     * @return javax.swing.JButton
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JButton getSaveButton() {
        if (ivjSaveButton == null) {
            try {
                ivjSaveButton = new javax.swing.JButton();
                ivjSaveButton.setName("SaveButton");
                ivjSaveButton.setPreferredSize(new java.awt.Dimension(89, 25));
                ivjSaveButton.setText("Save SQL");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjSaveButton;
    }

    /**
     * Return the SendButton property value.
     *
     * @return javax.swing.JButton
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JButton getSendButton() {
        if (ivjSendButton == null) {
            try {
                ivjSendButton = new javax.swing.JButton();
                ivjSendButton.setName("SendButton");
                ivjSendButton.setText("Send");
                ivjSendButton.setActionCommand("Send");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjSendButton;
    }

    /**
     * Return the SchemaSQL property value.
     *
     * @return cz.omnicom.ermodeller.sql.SchemaSQL
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    public SchemaObjSQL getSchemaObj() {
        if (ivjSchemaObjSQL == null) {
            try {
                ivjSchemaObjSQL = new SchemaObjSQL();
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjSchemaObjSQL;
    }

    /**
     * Return the JTree1 property value.
     *
     * @return javax.swing.JTree
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JTree getSQLTree() {
        if (ivjSQLTree == null) {
            try {
                ivjSQLTree = new javax.swing.JTree();
                ivjSQLTree.setName("SQLTree");
                ivjSQLTree.setBounds(0, 0, 76, 36);
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjSQLTree;
    }

    /**
     * Return the JPanel11 property value.
     *
     * @return javax.swing.JPanel
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JPanel getSVPanel() {
        if (ivjSVPanel == null) {
            try {
                ivjSVPanel = new javax.swing.JPanel();
                ivjSVPanel.setName("SVPanel");
                ivjSVPanel.setLayout(getSVPanelGridLayout());
                ivjSVPanel.add(getSendButton());
                getSVPanel().add(getViewButton(), getViewButton().getName());
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjSVPanel;
    }

    /**
     * Return the SVPanelGridLayout property value.
     *
     * @return java.awt.GridLayout
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private java.awt.GridLayout getSVPanelGridLayout() {
        java.awt.GridLayout ivjSVPanelGridLayout = null;
        try {
            /* Create part */
            ivjSVPanelGridLayout = new java.awt.GridLayout();
            ivjSVPanelGridLayout.setVgap(5);
            ivjSVPanelGridLayout.setHgap(5);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
        return ivjSVPanelGridLayout;
    }

    /**
     * Return the ViewButton property value.
     *
     * @return javax.swing.JButton
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JButton getViewButton() {
        if (ivjViewButton == null) {
            try {
                ivjViewButton = new javax.swing.JButton();
                ivjViewButton.setName("ViewButton");
                ivjViewButton.setText("View log");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjViewButton;
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
        getCloseButton().addActionListener(this);
        getSQLTree().addTreeSelectionListener(this);
        getSaveButton().addActionListener(this);
        getSendButton().addActionListener(this);
        getViewButton().addActionListener(this);
    }

    /**
     * Initialize the class.
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private void initialize() {
        try {
            // user code begin {1}
            // user code end
            setName("SQLDialog");
            setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
            setSize(800, 467);
            setTitle("Object relation commands");
            setContentPane(getJDialogContentPane());
            initConnections();
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
        // user code begin {2}
        getJSplitPane1().setDividerLocation(400);
        // user code end
    }

    /**
     * main entrypoint - starts the part when it is run as an application
     *
     * @param args java.lang.String[]
     */
    public static void main(java.lang.String[] args) {
        try {
            ObjDialog aSQLDialog;
            aSQLDialog = new ObjDialog();
            aSQLDialog.setModal(true);
            try {
                Class aCloserClass = Class.forName("com.ibm.uvm.abt.edit.WindowCloser");
                Class parmTypes[] = {java.awt.Window.class};
                Object parms[] = {aSQLDialog};
                java.lang.reflect.Constructor aCtor = aCloserClass.getConstructor(parmTypes);
                aCtor.newInstance(parms);
            } catch (java.lang.Throwable exc) {
            }
            aSQLDialog.setVisible(true);
        } catch (Throwable exception) {
            System.err.println("Exception occurred in main() of javax.swing.JDialog");
            exception.printStackTrace(System.out);
        }
    }

    /**
     * Opens save dialog and saves the SQL script.
     */
    public void saveButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) throws java.io.IOException {
        FileWriter fileOutStream;
        PrintWriter dataOutStream;

        Object object = getRoot().getUserObject();
        SchemaObjSQL schemaSQLSQL;
        String sql;
        if (object instanceof SchemaObjSQL) {
            schemaSQLSQL = (SchemaObjSQL) object;
            sql = schemaSQLSQL.createSQL();
            if (sql.length() <= 0) {
                String[] options = {"Yes", "No"};
                int option;
                option = JOptionPane.showOptionDialog(this, "There are no SQL commands. Save anyway?", "Empty SQL", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
                switch (option) {
                    case 0:
                        break;
                    case 1:
                        return;
                }
            }
        } else
            return;
        if (fileName != null) {
            java.io.File f = new java.io.File(fileName);
            int i = fileName.lastIndexOf(f.getName());
            if (i > -1) {
                String dir = fileName.substring(0, i);
                getJFileChooser2().setCurrentDirectory(new File(dir));
                getJFileChooser2().setSelectedFile(f);
            }
        } else {
            getJFileChooser2().setCurrentDirectory(new File(AppPrefs.getProperty(AppPrefs.LOAD_STORE_DIR, "")));
            String name = getSchemaObj().getName();
            String extension;
            try {
                extension = ((ExtensionFileFilter) getJFileChooser2().getFileFilter()).getExtension();
                extension = "";
            }
            catch (ClassCastException x) {
                extension = ".sql";
            }
            name = name + extension;
            getJFileChooser2().setSelectedFile(new java.io.File(name));
        }
        if (getJFileChooser2().showSaveDialog(getJDialog1()) == JFileChooser.APPROVE_OPTION) {
            File file = getJFileChooser2().getSelectedFile();
            if (file != null)
                AppPrefs.storeProperty(AppPrefs.LOAD_STORE_DIR, file.getAbsolutePath(), true);
            String path;
            try {
                path = ((ExtensionFileFilter) getJFileChooser2().getFileFilter()).getPath(file);
            }
            catch (ClassCastException x) {
                path = file.getPath();
            }
            file = new File(path);
            fileOutStream = new FileWriter(file);
            dataOutStream = new PrintWriter(fileOutStream);
            String crlf = System.getProperties().getProperty("line.separator");
            StringTokenizer tokenizer = new StringTokenizer(sql, "\n");
            for (; tokenizer.hasMoreTokens();) {
                String token = tokenizer.nextToken();
                dataOutStream.write(token + crlf);
            }
            fileOutStream.close();
            dataOutStream.close();
        }
        getJFileChooser2().rescanCurrentDirectory();
    }

    /**
     * Comment
     */
    public void sendButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
        Object object = getRoot().getUserObject();
        SchemaObjSQL schemaSQLSQL;
        String sql;
        if (object instanceof SchemaObjSQL) {
            schemaSQLSQL = (SchemaObjSQL) object;
            sql = schemaSQLSQL.createSQL();
            connection.send(sql);
        }
    }

    /**
     * This method was created in VisualAge.
     *
     * @param newValue java.lang.String
     */
    public void setFileName(String newValue) {
        int index = newValue.length();
        if (newValue != null) {
            index = newValue.lastIndexOf(".cts");
            if (index < 0)
                index = newValue.lastIndexOf(".xml");
        }
        String extension;
        try {
            extension = ((ExtensionFileFilter) getJFileChooser2().getFileFilter()).getExtension();
            extension = "";
        }
        catch (ClassCastException x) {
            extension = ".sql";
        }
        this.fileName = newValue.substring(0, index) + extension;
    }

    /**
     * Sets the SQL schema and creates new tree.
     *
     * @param aErrorLogList cz.omnicom.ermodeller.errorlog.ErrorLogList
     * @see #createTree
     */
    public synchronized void setSchemaObj(SchemaObjSQL aSchemaObjSQL) {
        // Sets errorloglist
        ivjSchemaObjSQL = (aSchemaObjSQL == null) ? new SchemaObjSQL() : aSchemaObjSQL;
        // Creates and inserts new error tree
        createTree();
    }

    /**
     * This method was created in VisualAge.
     *
     * @param newValue java.lang.String
     */
    public void setObjConnection(SQLConnection con) {
        connection = con;
    }

    /**
     * Listenes for selection change in the tree and displays regarding SQL command.
     */
    public void objTree_ValueChanged(javax.swing.event.TreeSelectionEvent treeSelectionEvent) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) (treeSelectionEvent.getPath().getLastPathComponent());
        Object o = node.getUserObject();
        if (o instanceof SubObjProducer)
            getJTextArea1().setText(((SubObjProducer) o).createSubSQL(0));
        else
            getJTextArea1().setText("Bad data in SQL tree!");
    }

    /**
     * Method to handle events for the TreeSelectionListener interface.
     *
     * @param e javax.swing.event.TreeSelectionEvent
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    public void valueChanged(javax.swing.event.TreeSelectionEvent e) {
        // user code begin {1}
        // user code end
        if (e.getSource() == getSQLTree())
            connEtoC2(e);
        // user code begin {2}
        // user code end
    }

    /**
     * Comment
     */
    public void viewButton_ActionEvents() {
        connection.showLog();
    }
}