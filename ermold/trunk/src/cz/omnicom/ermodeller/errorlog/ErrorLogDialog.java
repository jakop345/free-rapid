package cz.omnicom.ermodeller.errorlog;

import cz.omnicom.ermodeller.icontree.IconNode;
import cz.omnicom.ermodeller.icontree.IconNodeRenderer;

import javax.swing.*;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.*;
import java.awt.*;
import java.util.Enumeration;

/**
 * Dialog presenting list of errors.
 */
public class ErrorLogDialog extends JDialog implements java.awt.event.ActionListener, java.beans.PropertyChangeListener {
    private JButton ivjCloseButton = null;
    private cz.green.ermodeller.Desktop desktop;
    private JPanel ivjDownPanel = null;
    private ErrorLogList ivjErrorLogList = null;
    private JTree ivjErrorTree = null;
    private JPanel ivjJDialogContentPane = null;
    private JScrollPane ivjJScrollPane = null;
    private IconNode ivjRoot = null;
    private JButton ivjRefreshButton = null;

    /**
     * Constructor
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private ErrorLogDialog() {
        super();
        initialize();
    }

    /**
     * ErrorLogDialog constructor comment.
     *
     * @param owner java.awt.Frame
     */
    public ErrorLogDialog(Frame owner, ErrorLogList aErrorLogList) {
        super(owner);
        setErrorLogList(aErrorLogList);
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
        if (e.getSource() == getRefreshButton())
            connEtoM1(e);
        // user code begin {2}
        // user code end
    }

    /**
     * Hide the dialog.
     */
    void closeButton_ActionEvents() {
        setVisible(false);
    }

    /**
     * connEtoC1:  (CloseButton.action. --> ErrorLogDialog.closeButton_ActionEvents()V)
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
     * connEtoM1:  (RefreshButton.action.actionPerformed(java.awt.event.ActionEvent) --> ErrorLogDialog.refresh()V)
     *
     * @param arg1 java.awt.event.ActionEvent
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private void connEtoM1(java.awt.event.ActionEvent arg1) {
        try {
            // user code begin {1}
            // user code end
            this.refresh();
            // user code begin {2}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * Creates tree representing current error list.
     *
     * @param javax.swing.tree.DefaultMutableTreeNode
     *         top node of the tree
     */
    private void createTree(DefaultMutableTreeNode top) {
        ValidationError err;

        for (Enumeration errors = getErrorLogList().elements(); errors.hasMoreElements();) {
            err = (ValidationError) errors.nextElement();
            for (Enumeration objects = err.getObjects().elements(); objects.hasMoreElements();) {
                ((cz.omnicom.ermodeller.conceptual.ConceptualObject) objects.nextElement()).addPropertyChangeListener(this);
            }
            top.add(err.getSubTree());
        }

        ivjErrorTree = new JTree(top, true);
        // register for tree events
        for (Enumeration errors = getErrorLogList().elements(); errors.hasMoreElements();) {
            ivjErrorTree.addTreeSelectionListener((TreeSelectionListener) errors.nextElement());
        }
//	ivjErrorTree.setUI(new IconTreeUI());
        ivjErrorTree.setName("ErrorTree");
        ivjErrorTree.setAutoscrolls(true);
        ivjErrorTree.setOpaque(true);
//	ivjErrorTree.setBorder(new BasicFieldBorder());
//	ivjErrorTree.setBorder(javax.swing.plaf.basic.BasicBorders.FieldBorder);
        ivjErrorTree.setCellRenderer(new IconNodeRenderer());
        ivjErrorTree.setShowsRootHandles(false);
        ivjErrorTree.setRootVisible(true);
        ivjErrorTree.setVisibleRowCount(20);
        getJScrollPane().setViewportView(ivjErrorTree);
        // expand tree
        for (Enumeration topChildren = top.children(); topChildren.hasMoreElements();) {
            TreeNode child = (TreeNode) topChildren.nextElement();
            TreeModel model = ivjErrorTree.getModel();
            if (model instanceof DefaultTreeModel) {
                DefaultTreeModel defModel = (DefaultTreeModel) model;
                ivjErrorTree.expandPath(new TreePath(defModel.getPathToRoot(child)));
            }
        }
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
                getDownPanel().add(getCloseButton(), "East");
                getDownPanel().add(getRefreshButton(), "West");
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
     * Return the ErrorLogList property value.
     *
     * @return cz.omnicom.ermodeller.errorlog.ErrorLogList
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    ErrorLogList getErrorLogList() {
        if (ivjErrorLogList == null) {
            try {
                ivjErrorLogList = new cz.omnicom.ermodeller.errorlog.ErrorLogList();
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjErrorLogList;
    }

    /**
     * Return the ErrorTree property value.
     *
     * @return javax.swing.JTree
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JTree getErrorTree() {
        if (ivjErrorTree == null) {
            try {
                ivjErrorTree = new javax.swing.JTree();
                ivjErrorTree.setName("ErrorTree");
                ivjErrorTree.setAutoscrolls(true);
                ivjErrorTree.setOpaque(true);
                ivjErrorTree.setCellRenderer(new javax.swing.tree.DefaultTreeCellRenderer());
                ivjErrorTree.setMaximumSize(new java.awt.Dimension(76, 36));
                ivjErrorTree.setShowsRootHandles(false);
                ivjErrorTree.setPreferredSize(new java.awt.Dimension(639, 380));
                ivjErrorTree.setBounds(0, 0, 122, 94);
                ivjErrorTree.setRootVisible(true);
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjErrorTree;
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
                ivjJDialogContentPane.setPreferredSize(new java.awt.Dimension(300, 250));
                ivjJDialogContentPane.setBorder(javax.swing.BorderFactory.createEmptyBorder(6, 5, 5, 5));
                ivjJDialogContentPane.setLayout(new java.awt.BorderLayout());
                ivjJDialogContentPane.setMinimumSize(new java.awt.Dimension(200, 250));
                getJDialogContentPane().add(getDownPanel(), "South");
                getJDialogContentPane().add(getJScrollPane(), "Center");
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
     * Return the JScrollPane property value.
     *
     * @return javax.swing.JScrollPane
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JScrollPane getJScrollPane() {
        if (ivjJScrollPane == null) {
            try {
                ivjJScrollPane = new javax.swing.JScrollPane();
                ivjJScrollPane.setName("JScrollPane");
                getJScrollPane().setViewportView(getErrorTree());
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjJScrollPane;
    }

    /**
     * Return the RefreshButton property value.
     *
     * @return javax.swing.JButton
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JButton getRefreshButton() {
        if (ivjRefreshButton == null) {
            try {
                ivjRefreshButton = new javax.swing.JButton();
                ivjRefreshButton.setName("RefreshButton");
                ivjRefreshButton.setText("Refresh");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjRefreshButton;
    }

    /**
     * Return the Root property value.
     *
     * @return cz.omnicom.ermodeller.icontree.IconNode
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private cz.omnicom.ermodeller.icontree.IconNode getRoot() {
        if (ivjRoot == null) {
            try {
                ivjRoot = new cz.omnicom.ermodeller.icontree.IconNode();
                ivjRoot.setIcon(new javax.swing.ImageIcon(getClass().getResource("img//allerrors.gif")));
                ivjRoot.setUserObject("All errors in the schema");
                // user code begin {1}
                ivjRoot.setIcon(new ImageIcon(ClassLoader.getSystemResource("img/allerrors.gif")));
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
        getRefreshButton().addActionListener(this);
    }

    /**
     * Initialize the class.
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private void initialize() {
        try {
            // user code begin {1}
            // user code end
            setName("ErrorLogDialog");
            setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
            setSize(642, 408);
            setTitle("List of errors");
            setContentPane(getJDialogContentPane());
            initConnections();
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
        // user code begin {2}
        // user code end
    }

    /**
     * main entrypoint - starts the part when it is run as an application
     *
     * @param args java.lang.String[]
     */
    public static void main(java.lang.String[] args) {
        try {
            ErrorLogDialog aErrorLogDialog;
            aErrorLogDialog = new ErrorLogDialog();
            aErrorLogDialog.setModal(true);
            try {
                Class aCloserClass = Class.forName("com.ibm.uvm.abt.edit.WindowCloser");
                Class parmTypes[] = {java.awt.Window.class};
                Object parms[] = {aErrorLogDialog};
                java.lang.reflect.Constructor aCtor = aCloserClass.getConstructor(parmTypes);
                aCtor.newInstance(parms);
            } catch (java.lang.Throwable exc) {
            }
            aErrorLogDialog.setVisible(true);
        } catch (Throwable exception) {
            System.err.println("Exception occurred in main() of javax.swing.JDialog");
            exception.printStackTrace(System.out);
        }
    }

    /**
     * This method gets called when a bound property is changed.
     *
     * @param evt A PropertyChangeEvent object describing the event source
     *            and the property that has changed.
     */
    public void propertyChange(java.beans.PropertyChangeEvent evt) {
        getErrorTree().repaint();
    }

    void refresh() {
        try {
            setErrorLogList(((cz.omnicom.ermodeller.conceptual.Schema) desktop.getModel()).checkConsistency());
        }
        catch (Exception e) {
            System.out.println(e);
        }
    }

    public void setDesktop(cz.green.ermodeller.Desktop d) {
        desktop = d;
    }

    /**
     * Sets new error list, then creates new tree and displays it.
     *
     * @param aErrorLogList cz.omnicom.ermodeller.errorlog.ErrorLogList
     */
    public synchronized void setErrorLogList(ErrorLogList aErrorLogList) {
        // Empties the old error tree
        getRoot().removeAllChildren();
        // Sets errorloglist
        ivjErrorLogList = (aErrorLogList == null) ? new ErrorLogList() : aErrorLogList;
        // Creates and inserts new error tree
        createTree(getRoot());
    }
}
