package cz.felk.cvut.erm.dialogs;

import cz.felk.cvut.erm.conceptual.beans.*;
import cz.felk.cvut.erm.ermodeller.AttributeConstruct;
import cz.felk.cvut.erm.ermodeller.ConceptualConstructItem;
import cz.felk.cvut.erm.ermodeller.ConceptualConstructObject;
import cz.felk.cvut.erm.ermodeller.WorkingDesktop;
import cz.felk.cvut.erm.errorlog.ConceptualObjectVectorValidationError;
import cz.felk.cvut.erm.errorlog.ErrorLogList;
import cz.felk.cvut.erm.errorlog.ValidationError;
import cz.felk.cvut.erm.icontree.IconNode;
import cz.felk.cvut.erm.icontree.IconNodeRenderer;

import javax.swing.*;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.*;
import java.awt.*;
import java.util.Enumeration;
import java.util.Vector;

/**
 * Dialog presenting list of conflicts.
 */
public class ConflictsDialog extends JDialog implements java.awt.event.ActionListener, java.beans.PropertyChangeListener, TreeSelectionListener {
    private JButton ivjCloseButton = null;
    private String prefix = null;
    private ConceptualObject conceptualObject;
    private WorkingDesktop desktop;
    private int id = 0;
    private CardLayout cardL;
    private JPanel ivjDownPanel = null;
    private ErrorLogList ivjErrorLogList = null;
    private JTree ivjErrorTree = null;
    private JPanel ivjJDialogContentPane = null;
    private JScrollPane ivjJScrollPane = null;
    private IconNode ivjRoot = null;
    private JTextField ivjPrefixField = null;
    private JLabel ivjPrefixLabel = null;
    private JTextField ivjRenameField = null;
    private JLabel ivjRenameLabel = null;
    private JLabel ivjComposeLabel = null;
    private JButton ivjAcceptButton = null;
    private JComboBox ivjComposeList = null;
    private JButton ivjRemoveButton = null;
    private JButton ivjRefreshButton = null;
    private DefaultComboBoxModel ivjComposeModel = null;
    private JPanel ivjJPanel1 = null;
    private JPanel ivjJPanel2 = null;
    private JPanel ivjConceptualConstructPanel = null;
    private JPanel ivjJPanel21 = null;
    private JTextField ivjPrefixField1 = null;
    private JLabel ivjPrefixLabel1 = null;
    private JButton ivjRenameAllButton = null;
    private JPanel ivjSchemaPanel = null;
    private JButton ivjAcceptButton1 = null;
    private JPanel ivjAtributePanel = null;
    private JPanel ivjJPanel22 = null;
    private JTextField ivjPrefixField2 = null;
    private JLabel ivjPrefixLabel2 = null;
    private JButton ivjRemoveButton1 = null;
    private JTextField ivjRenameField1 = null;
    private JLabel ivjRenameLabel1 = null;
    private JPanel ivjAPanel = null;
    private JPanel ivjCPanel = null;
    private JPanel ivjSPanel = null;
    private JSplitPane ivjJSplitPane1 = null;
    private JLabel ivjJLabel1 = null;
    private JPanel ivjNPanel = null;

    /**
     * Constructor
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    public ConflictsDialog() {
        super();
        initialize();
    }

    /**
     * ErrorLogDialog constructor comment.
     *
     * @param owner java.awt.Frame
     */
    public ConflictsDialog(Frame owner, ErrorLogList aErrorLogList) {
        super(owner);
        initialize();
        setErrorLogList(aErrorLogList);
    }

    /**
     * Comment
     */
    public void acceptButtonAction() {
        //EntityConstruct ent;
        if (conceptualObject instanceof ConceptualConstruct)
            if (getComposeModel().getSelectedItem().equals("none")) {
                conceptualObject.setName(getRenameField().getText());
            } else {
                Object o = getComposeModel().getSelectedItem();
                if (conceptualObject instanceof Entity) {
                    //   ent = desktop.getEntity(((ConceptualConstruct) o).getID());
                    //ent.composeEntity(desktop.getEntity(i),new cz.felk.cvut.erm.event.DragOverEvent(0,0,(cz.felk.cvut.erm.event.interfaces.Item)desktop.getEntity(i),desktop.getPaintPlace()));
                    desktop.composeEntity((Entity) conceptualObject, (Entity) o);
                } else {
                    //rel = desktop.getRelation(((ConceptualConstruct) o).getID());
                    desktop.composeRelation((Relation) conceptualObject, (Relation) o);
                }
            }
        if (conceptualObject instanceof Atribute)
            conceptualObject.setName(getRenameField1().getText());
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
            connEtoC2();
        // user code begin {2}
        if (e.getActionCommand().equals("Accept"))
            acceptButtonAction();
        if (e.getActionCommand().equals("Rename all"))
            renameAll();
        if (e.getActionCommand().equals("Remove"))
            removeConstruct();
        // user code end
    }

    public void clear() {
        for (Enumeration errors = getErrorLogList().elements(); errors.hasMoreElements();)
            ivjErrorTree.removeTreeSelectionListener((TreeSelectionListener) errors.nextElement());
    }

    /**
     * Hide the dialog.
     */
    public void closeButton_ActionEvents() {
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
     * connEtoC2:  (RefreshButton.action. --> ConflictsDialog.refreshButton_ActionEvents()V)
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private void connEtoC2() {
        try {
            // user code begin {1}
            // user code end
            this.refreshButton_ActionEvents();
            // user code begin {2}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connPtoP1SetTarget:  (ComposeModel.this <--> ComposeList.model)
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private void connPtoP1SetTarget() {
        /* Set the target from the source */
        try {
            getComposeList().setModel(getComposeModel());
            // user code begin {1}
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
                ((ConceptualObject) objects.nextElement()).addPropertyChangeListener(this);
            }
            top.add(err.getSubTree());
        }

        ivjErrorTree = new JTree(top, true);
        // register for tree events
        for (Enumeration errors = getErrorLogList().elements(); errors.hasMoreElements();)
            ivjErrorTree.addTreeSelectionListener((TreeSelectionListener) errors.nextElement());
        ivjErrorTree.addTreeSelectionListener(this);
        ivjErrorTree.setUI(new javax.swing.plaf.basic.BasicTreeUI());
        ivjErrorTree.setName("ErrorTree");
        ivjErrorTree.setAutoscrolls(true);
        ivjErrorTree.setOpaque(true);
        ivjErrorTree.setCellRenderer(new IconNodeRenderer());
        //ivjErrorTree.setBorder(null);
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
     * Return the AcceptButton property value.
     *
     * @return javax.swing.JButton
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JButton getAcceptButton() {
        if (ivjAcceptButton == null) {
            try {
                ivjAcceptButton = new javax.swing.JButton();
                ivjAcceptButton.setName("AcceptButton");
                ivjAcceptButton.setText("Accept");
                ivjAcceptButton.setContentAreaFilled(false);
                ivjAcceptButton.setEnabled(true);
                ivjAcceptButton.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
                // user code begin {1}
                ivjAcceptButton.setActionCommand("Accept");
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjAcceptButton;
    }

    /**
     * Return the AcceptButton1 property value.
     *
     * @return javax.swing.JButton
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JButton getAcceptButton1() {
        if (ivjAcceptButton1 == null) {
            try {
                ivjAcceptButton1 = new javax.swing.JButton();
                ivjAcceptButton1.setName("AcceptButton1");
                ivjAcceptButton1.setText("Accept");
                ivjAcceptButton1.setContentAreaFilled(false);
                ivjAcceptButton1.setEnabled(true);
                ivjAcceptButton1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
                // user code begin {1}
                ivjAcceptButton1.setActionCommand("Accept");
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjAcceptButton1;
    }

    /**
     * Return the APanel property value.
     *
     * @return javax.swing.JPanel
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JPanel getAPanel() {
        if (ivjAPanel == null) {
            try {
                ivjAPanel = new javax.swing.JPanel();
                ivjAPanel.setName("APanel");
                ivjAPanel.setLayout(new java.awt.BorderLayout());
                getAPanel().add(getAtributePanel(), "North");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjAPanel;
    }

    /**
     * Return the AtributePanel property value.
     *
     * @return javax.swing.JPanel
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JPanel getAtributePanel() {
        if (ivjAtributePanel == null) {
            try {
                ivjAtributePanel = new javax.swing.JPanel();
                ivjAtributePanel.setName("AtributePanel");
                ivjAtributePanel.setLayout(new java.awt.GridBagLayout());
                ivjAtributePanel.setVisible(true);
                ivjAtributePanel.setPreferredSize(new java.awt.Dimension(280, 90));
                ivjAtributePanel.setMinimumSize(new java.awt.Dimension(0, 0));

                java.awt.GridBagConstraints constraintsPrefixLabel2 = new java.awt.GridBagConstraints();
                constraintsPrefixLabel2.gridx = 0;
                constraintsPrefixLabel2.gridy = 0;
                constraintsPrefixLabel2.anchor = java.awt.GridBagConstraints.WEST;
                constraintsPrefixLabel2.insets = new java.awt.Insets(4, 4, 4, 4);
                getAtributePanel().add(getPrefixLabel2(), constraintsPrefixLabel2);

                java.awt.GridBagConstraints constraintsPrefixField2 = new java.awt.GridBagConstraints();
                constraintsPrefixField2.gridx = 1;
                constraintsPrefixField2.gridy = 0;
                constraintsPrefixField2.fill = java.awt.GridBagConstraints.HORIZONTAL;
                constraintsPrefixField2.weightx = 1.0;
                constraintsPrefixField2.insets = new java.awt.Insets(4, 4, 4, 4);
                getAtributePanel().add(getPrefixField2(), constraintsPrefixField2);

                java.awt.GridBagConstraints constraintsRenameLabel1 = new java.awt.GridBagConstraints();
                constraintsRenameLabel1.gridx = 0;
                constraintsRenameLabel1.gridy = 1;
                constraintsRenameLabel1.anchor = java.awt.GridBagConstraints.WEST;
                constraintsRenameLabel1.insets = new java.awt.Insets(4, 4, 4, 4);
                getAtributePanel().add(getRenameLabel1(), constraintsRenameLabel1);

                java.awt.GridBagConstraints constraintsRenameField1 = new java.awt.GridBagConstraints();
                constraintsRenameField1.gridx = 1;
                constraintsRenameField1.gridy = 1;
                constraintsRenameField1.fill = java.awt.GridBagConstraints.HORIZONTAL;
                constraintsRenameField1.weightx = 1.0;
                constraintsRenameField1.insets = new java.awt.Insets(4, 4, 4, 4);
                getAtributePanel().add(getRenameField1(), constraintsRenameField1);

                java.awt.GridBagConstraints constraintsJPanel22 = new java.awt.GridBagConstraints();
                constraintsJPanel22.gridx = 0;
                constraintsJPanel22.gridy = 2;
                constraintsJPanel22.gridwidth = 2;
                constraintsJPanel22.fill = java.awt.GridBagConstraints.BOTH;
                constraintsJPanel22.weightx = 1.0;
                constraintsJPanel22.weighty = 1.0;
                constraintsJPanel22.insets = new java.awt.Insets(4, 4, 4, 4);
                getAtributePanel().add(getJPanel22(), constraintsJPanel22);
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjAtributePanel;
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
     * Return the ComposeLabel property value.
     *
     * @return javax.swing.JLabel
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JLabel getComposeLabel() {
        if (ivjComposeLabel == null) {
            try {
                ivjComposeLabel = new javax.swing.JLabel();
                ivjComposeLabel.setName("ComposeLabel");
                ivjComposeLabel.setText("Compose");
                ivjComposeLabel.setEnabled(true);
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjComposeLabel;
    }

    /**
     * Return the ComposeList property value.
     *
     * @return javax.swing.JComboBox
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JComboBox getComposeList() {
        if (ivjComposeList == null) {
            try {
                ivjComposeList = new javax.swing.JComboBox();
                ivjComposeList.setName("ComposeList");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjComposeList;
    }

    /**
     * Return the ComposeModel property value.
     *
     * @return javax.swing.DefaultComboBoxModel
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.DefaultComboBoxModel getComposeModel() {
        if (ivjComposeModel == null) {
            try {
                ivjComposeModel = new javax.swing.DefaultComboBoxModel();
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjComposeModel;
    }

    /**
     * Return the PropPanel property value.
     *
     * @return javax.swing.JPanel
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JPanel getConceptualConstructPanel() {
        if (ivjConceptualConstructPanel == null) {
            try {
                ivjConceptualConstructPanel = new javax.swing.JPanel();
                ivjConceptualConstructPanel.setName("ConceptualConstructPanel");
                ivjConceptualConstructPanel.setPreferredSize(new java.awt.Dimension(280, 120));
                ivjConceptualConstructPanel.setLayout(new java.awt.GridBagLayout());
                ivjConceptualConstructPanel.setMinimumSize(new java.awt.Dimension(0, 0));
                ivjConceptualConstructPanel.setVisible(true);

                java.awt.GridBagConstraints constraintsPrefixLabel = new java.awt.GridBagConstraints();
                constraintsPrefixLabel.gridx = 0;
                constraintsPrefixLabel.gridy = 0;
                constraintsPrefixLabel.anchor = java.awt.GridBagConstraints.WEST;
                constraintsPrefixLabel.insets = new java.awt.Insets(4, 4, 4, 4);
                getConceptualConstructPanel().add(getPrefixLabel(), constraintsPrefixLabel);

                java.awt.GridBagConstraints constraintsPrefixField = new java.awt.GridBagConstraints();
                constraintsPrefixField.gridx = 1;
                constraintsPrefixField.gridy = 0;
                constraintsPrefixField.fill = java.awt.GridBagConstraints.HORIZONTAL;
                constraintsPrefixField.weightx = 1.0;
                constraintsPrefixField.insets = new java.awt.Insets(4, 4, 4, 4);
                getConceptualConstructPanel().add(getPrefixField(), constraintsPrefixField);

                java.awt.GridBagConstraints constraintsRenameLabel = new java.awt.GridBagConstraints();
                constraintsRenameLabel.gridx = 0;
                constraintsRenameLabel.gridy = 1;
                constraintsRenameLabel.anchor = java.awt.GridBagConstraints.WEST;
                constraintsRenameLabel.insets = new java.awt.Insets(4, 4, 4, 4);
                getConceptualConstructPanel().add(getRenameLabel(), constraintsRenameLabel);

                java.awt.GridBagConstraints constraintsRenameField = new java.awt.GridBagConstraints();
                constraintsRenameField.gridx = 1;
                constraintsRenameField.gridy = 1;
                constraintsRenameField.fill = java.awt.GridBagConstraints.HORIZONTAL;
                constraintsRenameField.weightx = 1.0;
                constraintsRenameField.insets = new java.awt.Insets(4, 4, 4, 4);
                getConceptualConstructPanel().add(getRenameField(), constraintsRenameField);

                java.awt.GridBagConstraints constraintsComposeLabel = new java.awt.GridBagConstraints();
                constraintsComposeLabel.gridx = 0;
                constraintsComposeLabel.gridy = 2;
                constraintsComposeLabel.anchor = java.awt.GridBagConstraints.WEST;
                constraintsComposeLabel.insets = new java.awt.Insets(4, 4, 4, 4);
                getConceptualConstructPanel().add(getComposeLabel(), constraintsComposeLabel);

                java.awt.GridBagConstraints constraintsComposeList = new java.awt.GridBagConstraints();
                constraintsComposeList.gridx = 1;
                constraintsComposeList.gridy = 2;
                constraintsComposeList.fill = java.awt.GridBagConstraints.HORIZONTAL;
                constraintsComposeList.weightx = 1.0;
                constraintsComposeList.insets = new java.awt.Insets(4, 4, 4, 4);
                getConceptualConstructPanel().add(getComposeList(), constraintsComposeList);

                java.awt.GridBagConstraints constraintsJPanel2 = new java.awt.GridBagConstraints();
                constraintsJPanel2.gridx = 0;
                constraintsJPanel2.gridy = 3;
                constraintsJPanel2.gridwidth = 2;
                constraintsJPanel2.fill = java.awt.GridBagConstraints.BOTH;
                constraintsJPanel2.weightx = 1.0;
                constraintsJPanel2.weighty = 1.0;
                constraintsJPanel2.insets = new java.awt.Insets(4, 4, 4, 4);
                getConceptualConstructPanel().add(getJPanel2(), constraintsJPanel2);
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjConceptualConstructPanel;
    }

    /**
     * Return the CPanel property value.
     *
     * @return javax.swing.JPanel
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JPanel getCPanel() {
        if (ivjCPanel == null) {
            try {
                ivjCPanel = new javax.swing.JPanel();
                ivjCPanel.setName("CPanel");
                ivjCPanel.setLayout(new java.awt.BorderLayout());
                getCPanel().add(getConceptualConstructPanel(), "North");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjCPanel;
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
    public ErrorLogList getErrorLogList() {
        if (ivjErrorLogList == null) {
            try {
                ivjErrorLogList = new cz.felk.cvut.erm.errorlog.ErrorLogList();
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
                ivjErrorTree.setPreferredSize(new java.awt.Dimension(340, 22));
                ivjErrorTree.setBounds(0, 0, 639, 365);
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
                ivjJDialogContentPane.setBorder(javax.swing.BorderFactory.createEmptyBorder(6, 5, 5, 5));
                ivjJDialogContentPane.setLayout(new java.awt.BorderLayout());
                ivjJDialogContentPane.setPreferredSize(new java.awt.Dimension(300, 250));
                ivjJDialogContentPane.setMinimumSize(new java.awt.Dimension(200, 250));
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
     * Return the JLabel1 property value.
     *
     * @return javax.swing.JLabel
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JLabel getJLabel1() {
        if (ivjJLabel1 == null) {
            try {
                ivjJLabel1 = new javax.swing.JLabel();
                ivjJLabel1.setName("JLabel1");
                ivjJLabel1.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
                ivjJLabel1.setText("Click to the node to view action");
                ivjJLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjJLabel1;
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
                ivjJPanel1.setLayout(new java.awt.CardLayout());
                getJPanel1().add(getSPanel(), getSPanel().getName());
                getJPanel1().add(getCPanel(), getCPanel().getName());
                getJPanel1().add(getAPanel(), getAPanel().getName());
                getJPanel1().add(getNPanel(), getNPanel().getName());
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
     * Return the JPanel2 property value.
     *
     * @return javax.swing.JPanel
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JPanel getJPanel2() {
        if (ivjJPanel2 == null) {
            try {
                ivjJPanel2 = new javax.swing.JPanel();
                ivjJPanel2.setName("JPanel2");
                ivjJPanel2.setPreferredSize(new java.awt.Dimension(156, 20));
                ivjJPanel2.setLayout(new java.awt.BorderLayout());
                ivjJPanel2.setMinimumSize(new java.awt.Dimension(156, 20));
                getJPanel2().add(getRemoveButton(), "East");
                getJPanel2().add(getAcceptButton(), "West");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjJPanel2;
    }

    /**
     * Return the JPanel21 property value.
     *
     * @return javax.swing.JPanel
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JPanel getJPanel21() {
        if (ivjJPanel21 == null) {
            try {
                ivjJPanel21 = new javax.swing.JPanel();
                ivjJPanel21.setName("JPanel21");
                ivjJPanel21.setPreferredSize(new java.awt.Dimension(156, 20));
                ivjJPanel21.setLayout(new java.awt.FlowLayout());
                ivjJPanel21.setMinimumSize(new java.awt.Dimension(156, 20));
                getJPanel21().add(getRenameAllButton(), getRenameAllButton().getName());
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjJPanel21;
    }

    /**
     * Return the JPanel22 property value.
     *
     * @return javax.swing.JPanel
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JPanel getJPanel22() {
        if (ivjJPanel22 == null) {
            try {
                ivjJPanel22 = new javax.swing.JPanel();
                ivjJPanel22.setName("JPanel22");
                ivjJPanel22.setPreferredSize(new java.awt.Dimension(156, 20));
                ivjJPanel22.setLayout(new java.awt.BorderLayout());
                ivjJPanel22.setMinimumSize(new java.awt.Dimension(156, 20));
                getJPanel22().add(getRemoveButton1(), "East");
                getJPanel22().add(getAcceptButton1(), "West");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjJPanel22;
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
                ivjJScrollPane.setPreferredSize(new java.awt.Dimension(340, 22));
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
                ivjJSplitPane1.setDividerSize(6);
                ivjJSplitPane1.setOneTouchExpandable(true);
                ivjJSplitPane1.setDividerLocation(200);
                getJSplitPane1().add(getJScrollPane(), "left");
                getJSplitPane1().add(getJPanel1(), "right");
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
     * Return the NPanel property value.
     *
     * @return javax.swing.JPanel
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JPanel getNPanel() {
        if (ivjNPanel == null) {
            try {
                ivjNPanel = new javax.swing.JPanel();
                ivjNPanel.setName("NPanel");
                ivjNPanel.setLayout(new java.awt.BorderLayout());
                getNPanel().add(getJLabel1(), "North");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjNPanel;
    }

    /**
     * Return the PrefixField property value.
     *
     * @return javax.swing.JTextField
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JTextField getPrefixField() {
        if (ivjPrefixField == null) {
            try {
                ivjPrefixField = new javax.swing.JTextField();
                ivjPrefixField.setName("PrefixField");
                ivjPrefixField.setEnabled(false);
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjPrefixField;
    }

    /**
     * Return the PrefixField1 property value.
     *
     * @return javax.swing.JTextField
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JTextField getPrefixField1() {
        if (ivjPrefixField1 == null) {
            try {
                ivjPrefixField1 = new javax.swing.JTextField();
                ivjPrefixField1.setName("PrefixField1");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjPrefixField1;
    }

    /**
     * Return the PrefixField2 property value.
     *
     * @return javax.swing.JTextField
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JTextField getPrefixField2() {
        if (ivjPrefixField2 == null) {
            try {
                ivjPrefixField2 = new javax.swing.JTextField();
                ivjPrefixField2.setName("PrefixField2");
                ivjPrefixField2.setEnabled(false);
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjPrefixField2;
    }

    /**
     * Return the PrefixLabel property value.
     *
     * @return javax.swing.JLabel
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JLabel getPrefixLabel() {
        if (ivjPrefixLabel == null) {
            try {
                ivjPrefixLabel = new javax.swing.JLabel();
                ivjPrefixLabel.setName("PrefixLabel");
                ivjPrefixLabel.setText("Prefix");
                ivjPrefixLabel.setVerticalAlignment(javax.swing.SwingConstants.CENTER);
                ivjPrefixLabel.setEnabled(false);
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjPrefixLabel;
    }

    /**
     * Return the PrefixLabel1 property value.
     *
     * @return javax.swing.JLabel
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JLabel getPrefixLabel1() {
        if (ivjPrefixLabel1 == null) {
            try {
                ivjPrefixLabel1 = new javax.swing.JLabel();
                ivjPrefixLabel1.setName("PrefixLabel1");
                ivjPrefixLabel1.setText("Prefix");
                ivjPrefixLabel1.setVerticalAlignment(javax.swing.SwingConstants.CENTER);
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjPrefixLabel1;
    }

    /**
     * Return the PrefixLabel2 property value.
     *
     * @return javax.swing.JLabel
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JLabel getPrefixLabel2() {
        if (ivjPrefixLabel2 == null) {
            try {
                ivjPrefixLabel2 = new javax.swing.JLabel();
                ivjPrefixLabel2.setName("PrefixLabel2");
                ivjPrefixLabel2.setText("Prefix");
                ivjPrefixLabel2.setVerticalAlignment(javax.swing.SwingConstants.CENTER);
                ivjPrefixLabel2.setEnabled(false);
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjPrefixLabel2;
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
     * Return the RemoveButton property value.
     *
     * @return javax.swing.JButton
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JButton getRemoveButton() {
        if (ivjRemoveButton == null) {
            try {
                ivjRemoveButton = new javax.swing.JButton();
                ivjRemoveButton.setName("RemoveButton");
                ivjRemoveButton.setText("Remove");
                ivjRemoveButton.setEnabled(true);
                ivjRemoveButton.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
                // user code begin {1}
                ivjRemoveButton.setActionCommand("Remove");
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjRemoveButton;
    }

    /**
     * Return the RemoveButton1 property value.
     *
     * @return javax.swing.JButton
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JButton getRemoveButton1() {
        if (ivjRemoveButton1 == null) {
            try {
                ivjRemoveButton1 = new javax.swing.JButton();
                ivjRemoveButton1.setName("RemoveButton1");
                ivjRemoveButton1.setText("Remove");
                ivjRemoveButton1.setEnabled(true);
                ivjRemoveButton1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
                // user code begin {1}
                ivjRemoveButton1.setActionCommand("Remove");
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjRemoveButton1;
    }

    /**
     * Return the RenameAllButton property value.
     *
     * @return javax.swing.JButton
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JButton getRenameAllButton() {
        if (ivjRenameAllButton == null) {
            try {
                ivjRenameAllButton = new javax.swing.JButton();
                ivjRenameAllButton.setName("RenameAllButton");
                ivjRenameAllButton.setText("Rename all");
                // user code begin {1}
                ivjRenameAllButton.setActionCommand("Rename all");
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjRenameAllButton;
    }

    /**
     * Return the RenameField property value.
     *
     * @return javax.swing.JTextField
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JTextField getRenameField() {
        if (ivjRenameField == null) {
            try {
                ivjRenameField = new javax.swing.JTextField();
                ivjRenameField.setName("RenameField");
                ivjRenameField.setEnabled(true);
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjRenameField;
    }

    /**
     * Return the RenameField1 property value.
     *
     * @return javax.swing.JTextField
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JTextField getRenameField1() {
        if (ivjRenameField1 == null) {
            try {
                ivjRenameField1 = new javax.swing.JTextField();
                ivjRenameField1.setName("RenameField1");
                ivjRenameField1.setEnabled(true);
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjRenameField1;
    }

    /**
     * Return the RenameLabel property value.
     *
     * @return javax.swing.JLabel
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JLabel getRenameLabel() {
        if (ivjRenameLabel == null) {
            try {
                ivjRenameLabel = new javax.swing.JLabel();
                ivjRenameLabel.setName("RenameLabel");
                ivjRenameLabel.setText("Rename");
                ivjRenameLabel.setEnabled(true);
                ivjRenameLabel.setVisible(true);
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjRenameLabel;
    }

    /**
     * Return the RenameLabel1 property value.
     *
     * @return javax.swing.JLabel
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JLabel getRenameLabel1() {
        if (ivjRenameLabel1 == null) {
            try {
                ivjRenameLabel1 = new javax.swing.JLabel();
                ivjRenameLabel1.setName("RenameLabel1");
                ivjRenameLabel1.setText("Rename");
                ivjRenameLabel1.setEnabled(true);
                ivjRenameLabel1.setVisible(true);
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjRenameLabel1;
    }

    /**
     * Return the Root property value.
     *
     * @return cz.omnicom.ermodeller.icontree.IconNode
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private cz.felk.cvut.erm.icontree.IconNode getRoot() {
        if (ivjRoot == null) {
            try {
                ivjRoot = new cz.felk.cvut.erm.icontree.IconNode();
                ivjRoot.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/allerrors.gif")));
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
     * Return the SchemaPanel property value.
     *
     * @return javax.swing.JPanel
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JPanel getSchemaPanel() {
        if (ivjSchemaPanel == null) {
            try {
                ivjSchemaPanel = new javax.swing.JPanel();
                ivjSchemaPanel.setName("SchemaPanel");
                ivjSchemaPanel.setPreferredSize(new java.awt.Dimension(80, 70));
                ivjSchemaPanel.setLayout(new java.awt.GridBagLayout());
                ivjSchemaPanel.setMinimumSize(new java.awt.Dimension(0, 0));
                ivjSchemaPanel.setVisible(true);

                java.awt.GridBagConstraints constraintsPrefixLabel1 = new java.awt.GridBagConstraints();
                constraintsPrefixLabel1.gridx = 0;
                constraintsPrefixLabel1.gridy = 0;
                constraintsPrefixLabel1.anchor = java.awt.GridBagConstraints.WEST;
                constraintsPrefixLabel1.insets = new java.awt.Insets(4, 4, 4, 4);
                getSchemaPanel().add(getPrefixLabel1(), constraintsPrefixLabel1);

                java.awt.GridBagConstraints constraintsPrefixField1 = new java.awt.GridBagConstraints();
                constraintsPrefixField1.gridx = 1;
                constraintsPrefixField1.gridy = 0;
                constraintsPrefixField1.fill = java.awt.GridBagConstraints.HORIZONTAL;
                constraintsPrefixField1.weightx = 1.0;
                constraintsPrefixField1.insets = new java.awt.Insets(4, 4, 4, 4);
                getSchemaPanel().add(getPrefixField1(), constraintsPrefixField1);

                java.awt.GridBagConstraints constraintsJPanel21 = new java.awt.GridBagConstraints();
                constraintsJPanel21.gridx = 0;
                constraintsJPanel21.gridy = 1;
                constraintsJPanel21.gridwidth = 2;
                constraintsJPanel21.fill = java.awt.GridBagConstraints.BOTH;
                constraintsJPanel21.weightx = 1.0;
                constraintsJPanel21.weighty = 1.0;
                constraintsJPanel21.insets = new java.awt.Insets(4, 4, 4, 4);
                getSchemaPanel().add(getJPanel21(), constraintsJPanel21);
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjSchemaPanel;
    }

    /**
     * Return the SPanel property value.
     *
     * @return javax.swing.JPanel
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JPanel getSPanel() {
        if (ivjSPanel == null) {
            try {
                ivjSPanel = new javax.swing.JPanel();
                ivjSPanel.setName("SPanel");
                ivjSPanel.setLayout(new java.awt.BorderLayout());
                getSPanel().add(getSchemaPanel(), "North");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjSPanel;
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
        getAcceptButton().addActionListener(this);
        getAcceptButton1().addActionListener(this);
        getRemoveButton().addActionListener(this);
        getRemoveButton1().addActionListener(this);
        getRenameAllButton().addActionListener(this);
        // user code end
        getCloseButton().addActionListener(this);
        getRefreshButton().addActionListener(this);
        connPtoP1SetTarget();
    }

    /**
     * Initialize the class.
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private void initialize() {
        try {
            // user code begin {1}
            // user code end
            setName("ConflictsDialog");
            setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
            setSize(642, 408);
            setTitle("List of errors");
            setContentPane(getJDialogContentPane());
            initConnections();
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
        // user code begin {2}
        cardL = (CardLayout) (getJPanel1().getLayout());
        cardL.show(getJPanel1(), getNPanel().getName());
        // user code end
    }

    /**
     * main entrypoint - starts the part when it is run as an application
     *
     * @param args java.lang.String[]
     */
    public static void main(java.lang.String[] args) {
        try {
            ConflictsDialog aErrorLogDialog;
            aErrorLogDialog = new ConflictsDialog();
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

    /**
     * Comment
     */
    public void refreshButton_ActionEvents() {
        if (desktop == null)
            System.out.println("desktop je null");
        try {
            setErrorLogList(((Schema) desktop.getModel()).checkConsistency());
        }
        catch (Exception e) {
            System.out.println(e);
            //e.printStackTrace();
        }
    }

    /**
     * Comment
     */
    public void removeConstruct() {
        int i;
        i = conceptualObject.getID();
        ConceptualConstructItem cc;
        ConceptualConstructObject co;

        try {
            if (conceptualObject instanceof Entity) {
                cc = desktop.getEntity(i);
                cc.handleRemoveEvent(new cz.felk.cvut.erm.event.RemoveEvent(0, 0, desktop.getPaintPlace()));
            }
            if (conceptualObject instanceof Relation) {
                cc = desktop.getRelation(i);
                cc.handleRemoveEvent(new cz.felk.cvut.erm.event.RemoveEvent(0, 0, desktop.getPaintPlace()));
            }
            if (conceptualObject instanceof Cardinality) {
                co = desktop.getConceptualObject(i);
                co.handleRemoveEvent(new cz.felk.cvut.erm.event.RemoveEvent(0, 0, desktop.getPaintPlace()));
            }
            if (conceptualObject instanceof Atribute) {
                AttributeConstruct atr = desktop.getAtribute(i);
                cc = atr.getOwner();
                atr.removeAtribute(new cz.felk.cvut.erm.event.RemoveEvent(0, 0, desktop.getPaintPlace()));
                cc.removeAtribute(atr);
            }
        }
        catch (Exception e) {
            System.out.println(e);
        }
    }

    public void renameAll() {
        ConceptualObjectVectorValidationError errV;
        ValidationError err;
        Vector objList;
        ConceptualObject co;
        int i, j, objID;
        String name;
        Object obj;
        int cnt = ivjErrorLogList.size();

        prefix = ivjPrefixField.getText();
        for (i = 0; i < cnt; i++) {
            obj = ivjErrorLogList.get(i);
//		if (obj instanceof CardinalitySameNameValidationError || obj instanceof ConceptualConstructSameNameValidationError) {
            err = (ValidationError) obj;
            objList = err.getObjects();
            for (j = 0; j < objList.size(); j++) {
                co = (ConceptualObject) objList.get(j);
                objID = co.getID();
                if (objID >= id) {
                    name = co.getName();
                    co.setName(prefix + name);
                }
            }
//		}
        }
    }

    public void setDesktop(WorkingDesktop d) {
        desktop = d;
    }

    /**
     * Sets new error list, then creates new tree and displays it.
     *
     * @param aErrorLogList cz.omnicom.ermodeller.errorlog.ErrorLogList
     */
    public synchronized void setErrorLogList(ErrorLogList aErrorLogList) {
        ValidationError err;

        //java.util.Vector objList;
        int cnt = aErrorLogList.size(), i, j;
        //ConceptualObject co;

        // Empties the old error tree
        getRoot().removeAllChildren();
        // Sets errorloglist
        ivjErrorLogList = new ErrorLogList();

        for (i = 0; i < cnt; i++) {
            err = (aErrorLogList.get(i));
//            objList = err.getObjects();
            //compErr = false;
//            for (j = 0; j < objList.size(); j++) {
//                co = (ConceptualObject) objList.get(j);
//                if (co.getID() >= id)
            //compErr = true;
//            }
//			if (compErr) {
            ivjErrorLogList.add(err);
//			}
        }
        // Creates and inserts new error tree
        createTree(getRoot());
        cardL.show(getJPanel1(), "NPanel");
    }

    public void setID(int i) {
        id = i;
    }

    public void setPrefix(String s) {
        prefix = s;
        getPrefixField().setText(prefix);
        getPrefixField1().setText(prefix);
        getPrefixField2().setText(prefix);
    }

    /**
     * Called whenever the value of the selection changes.
     *
     * @param e the event that characterizes the change.
     */
    public void valueChanged(javax.swing.event.TreeSelectionEvent e) {
        TreePath tp = e.getPath();
        Object o = tp.getLastPathComponent();
        o = ((DefaultMutableTreeNode) o).getUserObject();
        int cnt = tp.getPathCount(), i;
        Schema schema;
        ConceptualObject co;
        Vector v;

        ivjComposeModel.removeAllElements();
        ivjComposeModel.addElement("none");
        switch (cnt) {
            case 1:
                cardL.show(getJPanel1(), "SPanel");
                break;
            case 2:
                cardL.show(getJPanel1(), "NPanel");
                break;
            case 3:
                cardL.show(getJPanel1(), "NPanel");
                conceptualObject = (ConceptualObject) o;
                if (conceptualObject instanceof Cardinality || conceptualObject instanceof Atribute) {
                    cardL.show(getJPanel1(), "APanel");
                }
                schema = conceptualObject.getSchema();
                if (conceptualObject instanceof Entity) {
                    cardL.show(getJPanel1(), "CPanel");
                    v = schema.getEntities();
                    for (i = 0; i < v.size(); i++) {
                        co = (ConceptualObject) v.get(i);
                        if (co.getID() != conceptualObject.getID())
                            ivjComposeModel.addElement(co);
                    }
                }
                if (conceptualObject instanceof Relation) {
                    cardL.show(getJPanel1(), "CPanel");
                    v = schema.getRelations();
                    for (i = 0; i < v.size(); i++) {
                        co = (ConceptualObject) v.get(i);
                        if (co.getID() != conceptualObject.getID())
                            ivjComposeModel.addElement(co);
                    }
                }
                if (conceptualObject.getID() >= id) {
                    getRenameField().setText(prefix + conceptualObject.getName());
                    getRenameField1().setText(prefix + conceptualObject.getName());
                } else {
                    getRenameField().setText(conceptualObject.getName());
                    getRenameField1().setText(conceptualObject.getName());
                }
        }
        ivjJPanel1.repaint();
    }
}