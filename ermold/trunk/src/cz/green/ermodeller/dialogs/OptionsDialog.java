package cz.green.ermodeller.dialogs;

import cz.green.ermodeller.AppPrefs;
import cz.green.ermodeller.ConceptualConstruct;
import cz.green.ermodeller.Consts;
import cz.green.event.WindowItem;
import cz.omnicom.ermodeller.sql.SQLConnection;

import javax.swing.*;
import java.awt.*;

/**
 * Dialog for program's preferences
 */
public class OptionsDialog extends javax.swing.JDialog {
    private javax.swing.JPanel ivjJDialogContentPane = null;
    private javax.swing.JTextField ivjDriverField = null;
    private javax.swing.JComboBox ivjNotationField = null;
    private javax.swing.JPasswordField ivjPasswordField = null;
    private javax.swing.JTextField ivjURLField = null;
    private javax.swing.JComboBox ivjCodingField = null;
    private javax.swing.JComboBox ivjPkUMLField = null;
    private javax.swing.JComboBox ivjCardUMLField = null;
    private javax.swing.JTextField ivjUserField = null;

    private cz.omnicom.ermodeller.sql.SQLConnection connection = null;
    final IvjEventHandler ivjEventHandler = new IvjEventHandler();
    private java.lang.String Sql;

    private javax.swing.JButton cancelButton = null;
    private javax.swing.JButton foregroundColorButton = null;
    private javax.swing.JButton backgroundColorButton = null;
    private javax.swing.JButton objectBackgroundColorButton = null;
    private javax.swing.JButton selectedObjectColorButton = null;
    private javax.swing.JButton OKButton = null;
    private javax.swing.JButton ApplyButton = null;
    private javax.swing.JPanel ivjJPanel1 = null;
    private javax.swing.JLabel ivjDriverLabel = null;
    private javax.swing.JLabel ivjNotationLabel = null;
    private javax.swing.JLabel ivjCodingLabel = null;
    private javax.swing.JLabel ivjPkUMLLabel = null;
    private javax.swing.JLabel ivjCardUMLLabel = null;
    private javax.swing.JLabel ivjBackgroundLabel = null;
    private javax.swing.JLabel ivjObjectBackgroundLabel = null;
    private javax.swing.JLabel ivjSelectedObjectBackgroundLabel = null;
    private javax.swing.JLabel ivjForegroundLabel = null;

    private javax.swing.JLabel ivjURL6 = null;
    private javax.swing.JLabel ivjUserLabel = null;
    private javax.swing.JLabel ivjPasswordLabel = null;
    private javax.swing.JButton testButton = null;
    private javax.swing.JButton logButton = null;
    private javax.swing.JPanel ivjJPanel2 = null;
    private javax.swing.JPanel jButtonsPanel = null;

    private javax.swing.JColorChooser jForegroundChooser = null;


    private int defNot;
    private String encod;

    class IvjEventHandler implements java.awt.event.ActionListener {
        public void actionPerformed(java.awt.event.ActionEvent e) {
            if (e.getSource() == OptionsDialog.this.getCancelButton())
                connEtoC3(e);
            if (e.getSource() == OptionsDialog.this.getOKButton())
                connEtoC4(e);
            if (e.getSource() == OptionsDialog.this.getApplyButton())
                connEtoC11(e);
            if (e.getSource() == OptionsDialog.this.getTestButton())
                connEtoC5(e);
            if (e.getSource() == OptionsDialog.this.getLogButton())
                connEtoC6();
            if (e.getSource() == OptionsDialog.this.getForegroundButton())
                connEtoC7();
            if (e.getSource() == OptionsDialog.this.getBackgroundButton())
                connEtoC8();
            if (e.getSource() == OptionsDialog.this.getObjectBackgroundButton())
                connEtoC9();
            if (e.getSource() == OptionsDialog.this.getSelectedObjectButton())
                connEtoC10();
        }

    }

    //private java.lang.String defDir;
    JLabel AppNameLabel = new JLabel();
    JLabel VersionLabel = new JLabel();
    JLabel AuthorLabel = new JLabel();
    JLabel auth1Label = new JLabel();
    JLabel auth2Label = new JLabel();
    JLabel auth3Label = new JLabel();
    JLabel auth4Label = new JLabel();
    JLabel auth5Label = new JLabel();
    final JTabbedPane jTabbedPane1 = new JTabbedPane();
    JTabbedPane jTabbedPane2 = new JTabbedPane();
    GridBagLayout gridBagLayout1 = new GridBagLayout();
    cz.green.ermodeller.ERModeller erm = null;

    /**
     * OptionsDialog constructor comment.
     *
     * @param owner java.awt.Frame
     */
    public OptionsDialog(java.awt.Frame owner, cz.green.ermodeller.ERModeller erm) {
        super(owner);
        initialize();
        this.erm = erm;
    }


    /**
     * Set true values to the connection fields and close the dialog
     *
     * @param actionEvent
     */
    private void cancelButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
//        getDriverField().setText(connection.getDriver());
//        getURLField().setText(connection.getUrl());
//        getUserField().setText(connection.getUser());
//        getPasswordField().setText(connection.getPasswd());
        this.setVisible(false);
    }

    /**
     * connEtoC3:  (SendButton.action.actionPerformed(java.awt.event.ActionEvent) --> SendSQL.sendButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
     *
     * @param arg1 java.awt.event.ActionEvent
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private void connEtoC3(java.awt.event.ActionEvent arg1) {
        try {
            // user code begin {1}
            // user code end
            this.cancelButton_ActionPerformed(arg1);
            // user code begin {2}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connEtoC4:  (OKButton.action.actionPerformed(java.awt.event.ActionEvent) --> PreferencesPanel.oKButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
     *
     * @param arg1 java.awt.event.ActionEvent
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private void connEtoC4(java.awt.event.ActionEvent arg1) {
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
     * connEtoC4:  (OKButton.action.actionPerformed(java.awt.event.ActionEvent) --> PreferencesPanel.oKButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
     *
     * @param arg1 java.awt.event.ActionEvent
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private void connEtoC11(java.awt.event.ActionEvent arg1) {
        try {
            // user code begin {1}
            // user code end
            this.applyButton_ActionPerformed(arg1);
            // user code begin {2}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connEtoC5:  (JButton5.action.actionPerformed(java.awt.event.ActionEvent) --> PreferencesPanel.test()V)
     *
     * @param arg1 java.awt.event.ActionEvent
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private void connEtoC5(java.awt.event.ActionEvent arg1) {
        try {
            // user code begin {1}
            // user code end
            this.test();
            // user code begin {2}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connEtoC6:  (JButton6.action. --> PreferencesPanel.jButton6_ActionEvents()V)
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private void connEtoC6() {
        try {
            this.jButton6_ActionEvents();
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    /**
     * connEtoC6:  (JButton6.action. --> PreferencesPanel.jButton6_ActionEvents()V)
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private void connEtoC7() {
        try {
            this.ForegroundButton_ActionPerformed();
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    /**
     * connEtoC6:  (JButton6.action. --> PreferencesPanel.jButton6_ActionEvents()V)
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private void connEtoC8() {
        try {
            this.BackgroundButton_ActionPerformed();
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    /**
     * connEtoC6:  (JButton6.action. --> PreferencesPanel.jButton6_ActionEvents()V)
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private void connEtoC9() {
        try {
            this.objectBackgroundButton_ActionPerformed();
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    /**
     * connEtoC6:  (JButton6.action. --> PreferencesPanel.jButton6_ActionEvents()V)
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private void connEtoC10() {
        try {
            this.selectedObjectButton_ActionPerformed();
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    /**
     * Return the JTextField1 property value.
     *
     * @return javax.swing.JTextField
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JTextField getDriverField() {
        if (ivjDriverField == null) {
            try {
                ivjDriverField = new javax.swing.JTextField();
                ivjDriverField.setName("DriverField");
                ivjDriverField.setPreferredSize(new java.awt.Dimension(1, 20));
                ivjDriverField.setMinimumSize(new java.awt.Dimension(1, 20));
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjDriverField;
    }

    /**
     * Return the JLabel5 property value.
     *
     * @return javax.swing.JLabel
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JLabel getCodingLabel() {
        if (ivjCodingLabel == null) {
            try {
                ivjCodingLabel = new javax.swing.JLabel();
                ivjCodingLabel.setName("JCodingLabel");
                ivjCodingLabel.setText("Encoding");
                ivjCodingLabel.setToolTipText("Encoding which will be saved to the schema and used while loading.");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjCodingLabel;
    }

    /**
     * Return the JLabel5 property value.
     *
     * @return javax.swing.JLabel
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JLabel getPkUMLLabel() {
        if (ivjPkUMLLabel == null) {
            try {
                ivjPkUMLLabel = new javax.swing.JLabel();
                ivjPkUMLLabel.setName("JPkUMLLabel");
                ivjPkUMLLabel.setText("Diplay \"pk\" symbol in UML notation");
                ivjPkUMLLabel.setToolTipText("Symbol \"pk\" will / won't be shown in front of attribute which is member of primary key");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjPkUMLLabel;
    }

    /**
     * Return the JLabel5 property value.
     *
     * @return javax.swing.JLabel
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JLabel getCardUMLLabel() {
        if (ivjCardUMLLabel == null) {
            try {
                ivjCardUMLLabel = new javax.swing.JLabel();
                ivjCardUMLLabel.setName("JCardUMLLabel");
                ivjCardUMLLabel.setText("Display 0..N and 1..1 cardinality in UML as");
                ivjCardUMLLabel.setToolTipText("Show full or shorten cardinality in UML notation");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjCardUMLLabel;
    }

    /**
     * Return the JButton1 property value.
     *
     * @return javax.swing.JButton
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JButton getCancelButton() {
        if (cancelButton == null) {
            try {
                cancelButton = new javax.swing.JButton();
                cancelButton.setName("cancelButton");
                cancelButton.setText("Cancel");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return cancelButton;
    }

    /**
     * Return the JButton1 property value.
     *
     * @return javax.swing.JButton
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JButton getForegroundButton() {
        if (foregroundColorButton == null) {
            try {
                foregroundColorButton = new javax.swing.JButton();
                foregroundColorButton.setName("ForegroundColorButton");
                foregroundColorButton.setBackground(WindowItem.OBJECT_FOREGROUND_COLOR);
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return foregroundColorButton;
    }

    /**
     * Return the JButton1 property value.
     *
     * @return javax.swing.JButton
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JButton getBackgroundButton() {
        if (backgroundColorButton == null) {
            try {
                backgroundColorButton = new javax.swing.JButton();
                backgroundColorButton.setName("BackgroundColorButton");
                backgroundColorButton.setBackground(WindowItem.BACKGROUND_COLOR);
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return backgroundColorButton;
    }

    /**
     * Return the JButton1 property value.
     *
     * @return javax.swing.JButton
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JButton getObjectBackgroundButton() {
        if (objectBackgroundColorButton == null) {
            try {
                objectBackgroundColorButton = new javax.swing.JButton();
                objectBackgroundColorButton.setName("ObjectBackgroundColorButton");
                objectBackgroundColorButton.setBackground(WindowItem.OBJECT_BACKGROUND_COLOR);
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return objectBackgroundColorButton;
    }

    /**
     * Return the JButton1 property value.
     *
     * @return javax.swing.JButton
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JButton getSelectedObjectButton() {
        if (selectedObjectColorButton == null) {
            try {
                selectedObjectColorButton = new javax.swing.JButton();
                selectedObjectColorButton.setName("SelectedObjectBackgroundColorButton");
                selectedObjectColorButton.setBackground(WindowItem.SELECTED_OBJECT_BACKGROUND_COLOR);
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return selectedObjectColorButton;
    }

    /**
     * Return the OKButton property value.
     *
     * @return javax.swing.JButton
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JButton getOKButton() {
        if (OKButton == null) {
            try {
                OKButton = new javax.swing.JButton();
                OKButton.setName("OKButton");
                OKButton.setPreferredSize(new java.awt.Dimension(73, 25));
                OKButton.setText("OK");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return OKButton;
    }

    /**
     * Return the OKButton property value.
     *
     * @return javax.swing.JButton
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JButton getApplyButton() {
        if (ApplyButton == null) {
            try {
                ApplyButton = new javax.swing.JButton();
                ApplyButton.setName("AplyButton");
                ApplyButton.setPreferredSize(new java.awt.Dimension(73, 25));
                ApplyButton.setText("Apply");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ApplyButton;
    }

    /**
     * Return the testButton property value.
     *
     * @return javax.swing.JButton
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JButton getTestButton() {
        if (testButton == null) {
            try {
                testButton = new javax.swing.JButton();
                testButton.setName("testButton");
                testButton.setText("Test connection");
                testButton.setContentAreaFilled(false);
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return testButton;
    }

    /**
     * Return the logButton property value.
     *
     * @return javax.swing.JButton
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JButton getLogButton() {
        if (logButton == null) {
            try {
                logButton = new javax.swing.JButton();
                logButton.setName("logButton");
                logButton.setText("View log");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return logButton;
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
                ivjJDialogContentPane.setBorder(BorderFactory.createLineBorder(Color.black));
                ivjJDialogContentPane.setDebugGraphicsOptions(0);
                ivjJDialogContentPane.setMaximumSize(new Dimension(450, 260));
                ivjJDialogContentPane.setPreferredSize(new Dimension(450, 260));
                ivjJDialogContentPane.setMinimumSize(new Dimension(450, 260));
                ivjJDialogContentPane.setRequestFocusEnabled(true);
                ivjJDialogContentPane.setLayout(new BorderLayout());

                JPanel GenPanel = new JPanel();
                JPanel DBPanel = new JPanel();
                JPanel ColorPanel = new JPanel();
                jTabbedPane1.add(GenPanel, "General");
                jTabbedPane1.add(DBPanel, "DB Connection");
                jTabbedPane1.add(ColorPanel, "Colors");
//			PreferencesDialog prefDialog = new PreferencesDialog();
//			prefDialog.setVisible(true);
//			DBPanel.add(prefDialog);
                ivjJDialogContentPane.add(jTabbedPane1, BorderLayout.CENTER);
                ivjJDialogContentPane.add(getJPanel1(), BorderLayout.SOUTH);

                //----------------GenPanel---------------------------
                GenPanel.setLayout(new java.awt.GridBagLayout());

                java.awt.GridBagConstraints notationField = new java.awt.GridBagConstraints();
                notationField.gridx = 1;
                notationField.gridy = 0;
                notationField.gridwidth = 3;
                notationField.fill = java.awt.GridBagConstraints.HORIZONTAL;
                notationField.anchor = java.awt.GridBagConstraints.WEST;
                notationField.weightx = 1.0;
                notationField.ipadx = 297;
                notationField.insets = new java.awt.Insets(10, 11, 6, 8);
                GenPanel.add(getNotationCombo(), notationField);

                java.awt.GridBagConstraints codingField = new java.awt.GridBagConstraints();
                codingField.gridx = 1;
                codingField.gridy = 1;
                codingField.gridwidth = 3;
                codingField.fill = java.awt.GridBagConstraints.HORIZONTAL;
                codingField.anchor = java.awt.GridBagConstraints.WEST;
                codingField.weightx = 1.0;
                codingField.ipadx = 297;
                codingField.insets = new java.awt.Insets(4, 11, 5, 8);
                GenPanel.add(getCodingCombo(), codingField);

                java.awt.GridBagConstraints pkUMLField = new java.awt.GridBagConstraints();
                pkUMLField.gridx = 1;
                codingField.gridy = 2;
                pkUMLField.gridwidth = 3;
                pkUMLField.fill = java.awt.GridBagConstraints.HORIZONTAL;
                pkUMLField.anchor = java.awt.GridBagConstraints.WEST;
                pkUMLField.weightx = 1.0;
                pkUMLField.ipadx = 297;
                pkUMLField.insets = new java.awt.Insets(4, 11, 5, 8);
                GenPanel.add(getPkUMLCombo(), pkUMLField);

                java.awt.GridBagConstraints cardUMLField = new java.awt.GridBagConstraints();
                cardUMLField.gridx = 1;
                cardUMLField.gridy = 3;
                cardUMLField.gridwidth = 3;
                cardUMLField.fill = java.awt.GridBagConstraints.HORIZONTAL;
                cardUMLField.anchor = java.awt.GridBagConstraints.WEST;
                cardUMLField.weightx = 1.0;
                cardUMLField.ipadx = 297;
                cardUMLField.insets = new java.awt.Insets(4, 11, 5, 8);
                GenPanel.add(getCardUMLCombo(), cardUMLField);

                java.awt.GridBagConstraints NotationJLabel = new java.awt.GridBagConstraints();
                NotationJLabel.gridx = 0;
                NotationJLabel.gridy = 0;
                NotationJLabel.anchor = java.awt.GridBagConstraints.WEST;
                NotationJLabel.insets = new java.awt.Insets(4, 4, 4, 4);
                GenPanel.add(getNotationLabel(), NotationJLabel);

                java.awt.GridBagConstraints CodingJLabel = new java.awt.GridBagConstraints();
                CodingJLabel.gridx = 0;
                CodingJLabel.gridy = 1;
                CodingJLabel.anchor = java.awt.GridBagConstraints.WEST;
                CodingJLabel.insets = new java.awt.Insets(4, 4, 4, 4);
                GenPanel.add(getCodingLabel(), CodingJLabel);

                java.awt.GridBagConstraints CardUMLJLabel = new java.awt.GridBagConstraints();
                CardUMLJLabel.gridx = 0;
                CardUMLJLabel.gridy = 3;
                CardUMLJLabel.anchor = java.awt.GridBagConstraints.WEST;
                CardUMLJLabel.insets = new java.awt.Insets(4, 4, 4, 4);
                GenPanel.add(getCardUMLLabel(), CardUMLJLabel);


                java.awt.GridBagConstraints PkUMLJLabel = new java.awt.GridBagConstraints();
                PkUMLJLabel.gridx = 0;
                PkUMLJLabel.gridy = 2;
                PkUMLJLabel.anchor = java.awt.GridBagConstraints.WEST;
                PkUMLJLabel.insets = new java.awt.Insets(4, 4, 4, 4);
                GenPanel.add(getPkUMLLabel(), PkUMLJLabel);
                /*
                            java.awt.GridBagConstraints CardUMLJLabel = new java.awt.GridBagConstraints();
                            CardUMLJLabel.gridx = 0; CardUMLJLabel.gridy = 3;
                            CardUMLJLabel.anchor = java.awt.GridBagConstraints.WEST;
                            CardUMLJLabel.insets = new java.awt.Insets(4, 4, 4, 4);
                            GenPanel.add(getPkUMLLabel(), CardUMLJLabel);
                */

                //----------------DBPanel---------------------------
                DBPanel.setLayout(new java.awt.GridBagLayout());

                java.awt.GridBagConstraints constraintsDriverField = new java.awt.GridBagConstraints();
                constraintsDriverField.gridx = 1;
                constraintsDriverField.gridy = 0;
                constraintsDriverField.gridwidth = 3;
                constraintsDriverField.fill = java.awt.GridBagConstraints.HORIZONTAL;
                constraintsDriverField.anchor = java.awt.GridBagConstraints.WEST;
                constraintsDriverField.weightx = 1.0;
                constraintsDriverField.ipadx = 297;
                constraintsDriverField.insets = new java.awt.Insets(10, 11, 6, 8);
                DBPanel.add(getDriverField(), constraintsDriverField);

                java.awt.GridBagConstraints constraintsURLField = new java.awt.GridBagConstraints();
                constraintsURLField.gridx = 1;
                constraintsURLField.gridy = 1;
                constraintsURLField.gridwidth = 3;
                constraintsURLField.fill = java.awt.GridBagConstraints.HORIZONTAL;
                constraintsURLField.anchor = java.awt.GridBagConstraints.WEST;
                constraintsURLField.weightx = 1.0;
                constraintsURLField.ipadx = 297;
                constraintsURLField.insets = new java.awt.Insets(4, 11, 5, 8);
                DBPanel.add(getURLField(), constraintsURLField);

                java.awt.GridBagConstraints constraintsUserField = new java.awt.GridBagConstraints();
                constraintsUserField.gridx = 1;
                constraintsUserField.gridy = 2;
                constraintsUserField.fill = java.awt.GridBagConstraints.HORIZONTAL;
                constraintsUserField.anchor = java.awt.GridBagConstraints.WEST;
                constraintsUserField.weightx = 1.0;
                constraintsUserField.ipadx = 297;
                constraintsUserField.insets = new java.awt.Insets(5, 11, 5, 8);
                DBPanel.add(getUserField(), constraintsUserField);

                java.awt.GridBagConstraints constraintsPasswordField = new java.awt.GridBagConstraints();
                constraintsPasswordField.gridx = 1;
                constraintsPasswordField.gridy = 3;
                constraintsPasswordField.gridwidth = 3;
                constraintsPasswordField.fill = java.awt.GridBagConstraints.HORIZONTAL;
                constraintsPasswordField.anchor = java.awt.GridBagConstraints.WEST;
                constraintsPasswordField.weightx = 1.0;
                constraintsPasswordField.ipadx = 297;
                constraintsPasswordField.insets = new java.awt.Insets(5, 11, 5, 8);
                DBPanel.add(getPasswordField(), constraintsPasswordField);

                java.awt.GridBagConstraints constraintsJLabel5 = new java.awt.GridBagConstraints();
                constraintsJLabel5.gridx = 0;
                constraintsJLabel5.gridy = 0;
                constraintsJLabel5.anchor = java.awt.GridBagConstraints.WEST;
                constraintsJLabel5.insets = new java.awt.Insets(4, 4, 4, 4);
                DBPanel.add(getJLabel5(), constraintsJLabel5);

                java.awt.GridBagConstraints constraintsJLabel6 = new java.awt.GridBagConstraints();
                constraintsJLabel6.gridx = 0;
                constraintsJLabel6.gridy = 1;
                constraintsJLabel6.anchor = java.awt.GridBagConstraints.WEST;
                constraintsJLabel6.insets = new java.awt.Insets(4, 4, 4, 4);
                DBPanel.add(getJLabel6(), constraintsJLabel6);

                java.awt.GridBagConstraints constraintsJLabel7 = new java.awt.GridBagConstraints();
                constraintsJLabel7.gridx = 0;
                constraintsJLabel7.gridy = 2;
                constraintsJLabel7.anchor = java.awt.GridBagConstraints.WEST;
                constraintsJLabel7.insets = new java.awt.Insets(4, 4, 4, 4);
                DBPanel.add(getJLabel7(), constraintsJLabel7);

                java.awt.GridBagConstraints constraintsJLabel8 = new java.awt.GridBagConstraints();
                constraintsJLabel8.gridx = 0;
                constraintsJLabel8.gridy = 3;
                constraintsJLabel8.anchor = java.awt.GridBagConstraints.WEST;
                constraintsJLabel8.insets = new java.awt.Insets(4, 4, 4, 4);
                DBPanel.add(getJLabel8(), constraintsJLabel8);

                java.awt.GridBagConstraints constraintsJPanel2 = new java.awt.GridBagConstraints();
                constraintsJPanel2.gridx = 0;
                constraintsJPanel2.gridy = 4;
                constraintsJPanel2.gridwidth = 2;
                constraintsJPanel2.fill = java.awt.GridBagConstraints.BOTH;
                constraintsJPanel2.weightx = 1.0;
                constraintsJPanel2.weighty = 1.0;
                constraintsJPanel2.insets = new java.awt.Insets(4, 4, 4, 4);
                DBPanel.add(getJPanel2(), constraintsJPanel2);

                //----------------ColorsPanel---------------------------
                ColorPanel.setLayout(new java.awt.GridBagLayout());
/*
			java.awt.GridBagConstraints notationField = new java.awt.GridBagConstraints();
			notationField.gridx = 1; notationField.gridy = 0;
			notationField.gridwidth = 3;
			notationField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			notationField.anchor = java.awt.GridBagConstraints.WEST;
			notationField.weightx = 1.0;
			notationField.ipadx = 297;
			notationField.insets = new java.awt.Insets(10, 11, 6, 8);
			GenPanel.add(getNotationCombo(), notationField);

			java.awt.GridBagConstraints codingField = new java.awt.GridBagConstraints();
			codingField.gridx = 1; codingField.gridy = 1;
			codingField.gridwidth = 3;
			codingField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			codingField.anchor = java.awt.GridBagConstraints.WEST;
			codingField.weightx = 1.0;
			codingField.ipadx = 297;
			codingField.insets = new java.awt.Insets(4, 11, 5, 8);
			GenPanel.add(getCodingCombo(), codingField);

			java.awt.GridBagConstraints pkUMLField = new java.awt.GridBagConstraints();
			pkUMLField.gridx = 1; codingField.gridy = 2;
			pkUMLField.gridwidth = 3;
			pkUMLField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			pkUMLField.anchor = java.awt.GridBagConstraints.WEST;
			pkUMLField.weightx = 1.0;
			pkUMLField.ipadx = 297;
			pkUMLField.insets = new java.awt.Insets(4, 11, 5, 8);
			GenPanel.add(getPkUMLCombo(), pkUMLField);
*/
/*			java.awt.GridBagConstraints ForegroundField = new java.awt.GridBagConstraints();
			ForegroundField.gridx = 1; ForegroundField.gridy = 3;
			ForegroundField.gridwidth = 3;
			ForegroundField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			ForegroundField.anchor = java.awt.GridBagConstraints.WEST;
			ForegroundField.weightx = 1.0;
			ForegroundField.ipadx = 297;
			ForegroundField.insets = new java.awt.Insets(4, 11, 5, 8);
			ColorPanel.add(getForegroundChooser(), ForegroundField);
*/
                java.awt.GridBagConstraints ForegroundField = new java.awt.GridBagConstraints();
                ForegroundField.gridx = 1;
                ForegroundField.gridy = 3;
                ForegroundField.gridwidth = 3;
                ForegroundField.fill = java.awt.GridBagConstraints.HORIZONTAL;
                ForegroundField.anchor = java.awt.GridBagConstraints.WEST;
                ForegroundField.weightx = 1.0;
                ForegroundField.ipadx = 297;
                ForegroundField.insets = new java.awt.Insets(4, 11, 5, 8);
                ColorPanel.add(getForegroundButton(), ForegroundField);

                java.awt.GridBagConstraints SelectedObjectBackgroundField = new java.awt.GridBagConstraints();
                SelectedObjectBackgroundField.gridx = 1;
                SelectedObjectBackgroundField.gridy = 2;
                SelectedObjectBackgroundField.gridwidth = 3;
                SelectedObjectBackgroundField.fill = java.awt.GridBagConstraints.HORIZONTAL;
                SelectedObjectBackgroundField.anchor = java.awt.GridBagConstraints.WEST;
                SelectedObjectBackgroundField.weightx = 1.0;
                SelectedObjectBackgroundField.ipadx = 297;
                SelectedObjectBackgroundField.insets = new java.awt.Insets(4, 11, 5, 8);
                ColorPanel.add(getSelectedObjectButton(), SelectedObjectBackgroundField);

                java.awt.GridBagConstraints ObjectBackgroundField = new java.awt.GridBagConstraints();
                ObjectBackgroundField.gridx = 1;
                ObjectBackgroundField.gridy = 1;
                ObjectBackgroundField.gridwidth = 3;
                ObjectBackgroundField.fill = java.awt.GridBagConstraints.HORIZONTAL;
                ObjectBackgroundField.anchor = java.awt.GridBagConstraints.WEST;
                ObjectBackgroundField.weightx = 1.0;
                ObjectBackgroundField.ipadx = 297;
                ObjectBackgroundField.insets = new java.awt.Insets(4, 11, 5, 8);
                ColorPanel.add(getObjectBackgroundButton(), ObjectBackgroundField);

                java.awt.GridBagConstraints BackgroundField = new java.awt.GridBagConstraints();
                BackgroundField.gridx = 1;
                BackgroundField.gridy = 0;
                BackgroundField.gridwidth = 3;
                BackgroundField.fill = java.awt.GridBagConstraints.HORIZONTAL;
                BackgroundField.anchor = java.awt.GridBagConstraints.WEST;
                BackgroundField.weightx = 1.0;
                BackgroundField.ipadx = 297;
                BackgroundField.insets = new java.awt.Insets(4, 11, 5, 8);
                ColorPanel.add(getBackgroundButton(), BackgroundField);

                java.awt.GridBagConstraints BackgroundJLabel = new java.awt.GridBagConstraints();
                BackgroundJLabel.gridx = 0;
                BackgroundJLabel.gridy = 0;
                BackgroundJLabel.anchor = java.awt.GridBagConstraints.WEST;
                BackgroundJLabel.insets = new java.awt.Insets(4, 4, 4, 4);
                ColorPanel.add(getBackgroundLabel(), BackgroundJLabel);

                java.awt.GridBagConstraints ObjectBackgroundJLabel = new java.awt.GridBagConstraints();
                ObjectBackgroundJLabel.gridx = 0;
                ObjectBackgroundJLabel.gridy = 1;
                ObjectBackgroundJLabel.anchor = java.awt.GridBagConstraints.WEST;
                ObjectBackgroundJLabel.insets = new java.awt.Insets(4, 4, 4, 4);
                ColorPanel.add(getObjectBackgroundLabel(), ObjectBackgroundJLabel);

                java.awt.GridBagConstraints SelectedObjectBackgroundJLabel = new java.awt.GridBagConstraints();
                SelectedObjectBackgroundJLabel.gridx = 0;
                SelectedObjectBackgroundJLabel.gridy = 2;
                SelectedObjectBackgroundJLabel.anchor = java.awt.GridBagConstraints.WEST;
                SelectedObjectBackgroundJLabel.insets = new java.awt.Insets(4, 4, 4, 4);
                ColorPanel.add(getSelectedObjectBackgroundLabel(), SelectedObjectBackgroundJLabel);


                java.awt.GridBagConstraints ForegroundJLabel = new java.awt.GridBagConstraints();
                ForegroundJLabel.gridx = 0;
                ForegroundJLabel.gridy = 3;
                ForegroundJLabel.anchor = java.awt.GridBagConstraints.WEST;
                ForegroundJLabel.insets = new java.awt.Insets(4, 4, 4, 4);
                ColorPanel.add(getForegroundLabel(), ForegroundJLabel);

/*				            ivjJDialogContentPane.add(jTabbedPane2,     new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

                jTabbedPane1.add(AuthorLabel,             new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 110, 0, 0), 0, 0));
                jTabbedPane1.add(auth1Label,         new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 104), 0, 0));
                jTabbedPane1.add(auth2Label,          new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 86), 0, 0));
                jTabbedPane2.add(auth3Label,      new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 125), 0, 0));
                jTabbedPane2.add(auth4Label,      new GridBagConstraints(1, 5, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 106), 0, 0));
            ivjJDialogContentPane.add(auth5Label,      new GridBagConstraints(1, 6, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 42, 119), 0, 0));
            ImageIcon image = new ImageIcon(ClassLoader
                    .getSystemResource("img/preferences.gif"));
//	        ivjJDialogContentPane.add(imageIcon,     new GridBagConstraints(1, 9, 1, 1, 0.0, 0.0
//		            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
*/
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJDialogContentPane;
    }

    /**
     * Insert the method's description here.
     * Creation date: (16.3.2001 18:26:28)
     *
     * @return java.lang.String
     */
    java.lang.String getSql() {
        return Sql;
    }

    /**
     * Return the JLabel5 property value.
     *
     * @return javax.swing.JLabel
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JLabel getJLabel5() {
        if (ivjDriverLabel == null) {
            try {
                ivjDriverLabel = new javax.swing.JLabel();
                ivjDriverLabel.setName("JLabel5");
                ivjDriverLabel.setText("Driver");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjDriverLabel;
    }

    /**
     * Return the JTextField2 property value.
     *
     * @return javax.swing.JTextField
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JComboBox getCodingCombo() {
        if (ivjCodingField == null) {
            try {
                String[] allowedEncodings = {"Windows-1250", "ISO-8859-1",
                        "ISO-8859-2", "UTF-16", "UTF-8"};
                ivjCodingField = new javax.swing.JComboBox(allowedEncodings);
                ivjCodingField.setName("CodingCombo");
                ivjCodingField.setPreferredSize(new java.awt.Dimension(1, 20));
                ivjCodingField.setMinimumSize(new java.awt.Dimension(1, 20));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjCodingField;
    }

    /**
     * Return the JTextField2 property value.
     *
     * @return javax.swing.JTextField
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JComboBox getPkUMLCombo() {
        if (ivjPkUMLField == null) {
            try {
                String[] showStr = {"Don't show \"pk\"", "Show \"pk\""};
                ivjPkUMLField = new javax.swing.JComboBox(showStr);
                ivjPkUMLField.setName("PkUMLCombo");
                ivjPkUMLField.setPreferredSize(new java.awt.Dimension(1, 20));
                ivjPkUMLField.setMinimumSize(new java.awt.Dimension(1, 20));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjPkUMLField;
    }

    /**
     * Return the JTextField2 property value.
     *
     * @return javax.swing.JTextField
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JComboBox getCardUMLCombo() {
        if (ivjCardUMLField == null) {
            try {
                String[] showStr = {"0..* and 1..1 (full)", "* and 1 (simple)"};
                ivjCardUMLField = new javax.swing.JComboBox(showStr);
                ivjCardUMLField.setName("PkUMLCombo");
                ivjCardUMLField.setPreferredSize(new java.awt.Dimension(1, 20));
                ivjCardUMLField.setMinimumSize(new java.awt.Dimension(1, 20));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjCardUMLField;
    }

    /**
     * Return the JTextField2 property value.
     *
     * @return javax.swing.JTextField
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JColorChooser getForegroundChooser() {
        if (jForegroundChooser == null) {
            try {
                jForegroundChooser = new javax.swing.JColorChooser();
                jForegroundChooser.setName("PkUMLCombo");
//			jForegroundChooser.setPreferredSize(new java.awt.Dimension(1, 20));
//			jForegroundChooser.setMinimumSize(new java.awt.Dimension(1, 20));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return jForegroundChooser;
    }

    /**
     * Return the JTextField1 property value.
     *
     * @return javax.swing.JTextField
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JComboBox getNotationCombo() {
        if (ivjNotationField == null) {
            try {
                String[] notString = {"CHEN NOTATION", "BINARY NOTATION", "UML NOTATION"};
                ivjNotationField = new javax.swing.JComboBox(notString);
                ivjNotationField.setName("NotationCombo");
                ivjNotationField.setPreferredSize(new java.awt.Dimension(1, 20));
                ivjNotationField.setMinimumSize(new java.awt.Dimension(1, 20));
                ivjNotationField.setSelectedIndex(1);
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjNotationField;
    }

    /**
     * Return the JLabel5 property value.
     *
     * @return javax.swing.JLabel
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JLabel getNotationLabel() {
        if (ivjNotationLabel == null) {
            try {
                ivjNotationLabel = new javax.swing.JLabel();
                ivjNotationLabel.setName("JNotationLabel");
                ivjNotationLabel.setText("Default graphic notation");
                ivjNotationLabel.setToolTipText("Notation which will be used after application start and new schema create");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjNotationLabel;
    }

    /**
     * Return the JLabel5 property value.
     *
     * @return javax.swing.JLabel
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JLabel getBackgroundLabel() {
        if (ivjBackgroundLabel == null) {
            try {
                ivjBackgroundLabel = new javax.swing.JLabel();
                ivjBackgroundLabel.setName("JBackgroundLabel");
                ivjBackgroundLabel.setText("Background color");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjBackgroundLabel;
    }

    /**
     * Return the JLabel5 property value.
     *
     * @return javax.swing.JLabel
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JLabel getObjectBackgroundLabel() {
        if (ivjObjectBackgroundLabel == null) {
            try {
                ivjObjectBackgroundLabel = new javax.swing.JLabel();
                ivjObjectBackgroundLabel.setName("JObjectBackgroundLabel");
                ivjObjectBackgroundLabel.setText("Object background color");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjObjectBackgroundLabel;
    }

    /**
     * Return the JLabel5 property value.
     *
     * @return javax.swing.JLabel
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JLabel getSelectedObjectBackgroundLabel() {
        if (ivjSelectedObjectBackgroundLabel == null) {
            try {
                ivjSelectedObjectBackgroundLabel = new javax.swing.JLabel();
                ivjSelectedObjectBackgroundLabel.setName("JSelectedObjectBackgroundLabel");
                ivjSelectedObjectBackgroundLabel.setText("Selected object background color");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjSelectedObjectBackgroundLabel;
    }

    /**
     * Return the JLabel5 property value.
     *
     * @return javax.swing.JLabel
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JLabel getForegroundLabel() {
        if (ivjForegroundLabel == null) {
            try {
                ivjForegroundLabel = new javax.swing.JLabel();
                ivjForegroundLabel.setName("JForegroundLabel");
                ivjForegroundLabel.setText("Foreground color");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjForegroundLabel;
    }

    /**
     * Return the JLabel6 property value.
     *
     * @return javax.swing.JLabel
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JLabel getJLabel6() {
        if (ivjURL6 == null) {
            try {
                ivjURL6 = new javax.swing.JLabel();
                ivjURL6.setName("JLabel6");
                ivjURL6.setText("URL");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjURL6;
    }

    /**
     * Return the JLabel7 property value.
     *
     * @return javax.swing.JLabel
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JLabel getJLabel7() {
        if (ivjUserLabel == null) {
            try {
                ivjUserLabel = new javax.swing.JLabel();
                ivjUserLabel.setName("JLabel7");
                ivjUserLabel.setText("User");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjUserLabel;
    }

    /**
     * Return the JLabel8 property value.
     *
     * @return javax.swing.JLabel
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JLabel getJLabel8() {
        if (ivjPasswordLabel == null) {
            try {
                ivjPasswordLabel = new javax.swing.JLabel();
                ivjPasswordLabel.setName("JLabel8");
                ivjPasswordLabel.setText("Password");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjPasswordLabel;
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
                ivjJPanel1.setPreferredSize(new java.awt.Dimension(300, 35));
                ivjJPanel1.setLayout(getJPanel1FlowLayout());
                getJPanel1().add(getOKButton(), getOKButton().getName());
                getJPanel1().add(getApplyButton(), getApplyButton().getName());
                getJPanel1().add(getCancelButton(), getCancelButton().getName());
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
     * Return the JPanel1FlowLayout property value.
     *
     * @return java.awt.FlowLayout
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private java.awt.FlowLayout getJPanel1FlowLayout() {
        java.awt.FlowLayout ivjJPanel1FlowLayout = null;
        try {
            /* Create part */
            ivjJPanel1FlowLayout = new java.awt.FlowLayout();
            ivjJPanel1FlowLayout.setAlignment(java.awt.FlowLayout.RIGHT);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
        return ivjJPanel1FlowLayout;
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
                ivjJPanel2.setLayout(new java.awt.BorderLayout());
                getJPanel2().add(getLogButton(), "East");
                getJPanel2().add(getTestButton(), "West");
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
     * Return the JPasswordField1 property value.
     *
     * @return javax.swing.JPasswordField
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JPasswordField getPasswordField() {
        if (ivjPasswordField == null) {
            try {
                ivjPasswordField = new javax.swing.JPasswordField();
                ivjPasswordField.setName("PasswordField");
                ivjPasswordField.setPreferredSize(new java.awt.Dimension(1, 20));
                ivjPasswordField.setMinimumSize(new java.awt.Dimension(1, 20));
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjPasswordField;
    }

    /**
     * Insert the method's description here.
     * Creation date: (16.3.2001 18:26:28)
     *
     * @return java.lang.String
     */
    public SQLConnection getSQLConnection() {
        return connection;
    }

    /**
     * Return the JTextField2 property value.
     *
     * @return javax.swing.JTextField
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JTextField getURLField() {
        if (ivjURLField == null) {
            try {
                ivjURLField = new javax.swing.JTextField();
                ivjURLField.setName("URLField");
                ivjURLField.setPreferredSize(new java.awt.Dimension(1, 20));
                ivjURLField.setMinimumSize(new java.awt.Dimension(1, 20));
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjURLField;
    }

    /**
     * Return the JTextField3 property value.
     *
     * @return javax.swing.JTextField
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JTextField getUserField() {
        if (ivjUserField == null) {
            try {
                ivjUserField = new javax.swing.JTextField();
                ivjUserField.setName("UserField");
                ivjUserField.setPreferredSize(new java.awt.Dimension(1, 20));
                ivjUserField.setMinimumSize(new java.awt.Dimension(1, 20));
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjUserField;
    }

    private void handleException(java.lang.Throwable exception) {

        /* Uncomment the following lines to print uncaught exceptions to stdout */
        // System.out.println("--------- UNCAUGHT EXCEPTION ---------");
        exception.printStackTrace(System.out);
    }

    /**
     * Initializes connections
     *
     * @throws java.lang.Exception The exception description.
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private void initConnections() {
        // user code begin {1}
        // user code end
        getCancelButton().addActionListener(ivjEventHandler);
        getOKButton().addActionListener(ivjEventHandler);
        getApplyButton().addActionListener(ivjEventHandler);
        getTestButton().addActionListener(ivjEventHandler);
        getLogButton().addActionListener(ivjEventHandler);
        getForegroundButton().addActionListener(ivjEventHandler);
        getBackgroundButton().addActionListener(ivjEventHandler);
        getObjectBackgroundButton().addActionListener(ivjEventHandler);
        getSelectedObjectButton().addActionListener(ivjEventHandler);
    }

    /**
     * Initialize the class.
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private void initialize() {
        try {
            // user code begin {1}
            // user code end
            setName("Preferences");
            setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
            setResizable(true);
            setSize(350, 210);
            setTitle("Options");
            setContentPane(getJDialogContentPane());

            initConnections();
            //this.getContentPane().add(getJDialogContentPane(), null);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    /**
     * Comment
     */
    public void jButton6_ActionEvents() {
        connection.showLog();
    }

    public void setActualValues() {
        this.defNot = AppPrefs.getProperty(AppPrefs.GENERAL_DEFNOTATION, Consts.DEF_GENERAL_DEFNOTATION);
        this.encod = AppPrefs.getProperty(AppPrefs.ENCODING, Consts.DEF_ENCODING);
        ivjNotationField.setSelectedIndex(defNot);
        ivjCodingField.setSelectedItem(encod);
        ivjPkUMLField.setSelectedIndex(ConceptualConstruct.SHOW_PK_IN_UML);
        ivjCardUMLField.setSelectedIndex(ConceptualConstruct.SHOW_SHORTEN_CARD_IN_UML);
        foregroundColorButton.setBackground(WindowItem.OBJECT_FOREGROUND_COLOR);
        backgroundColorButton.setBackground(WindowItem.BACKGROUND_COLOR);
        objectBackgroundColorButton.setBackground(WindowItem.OBJECT_BACKGROUND_COLOR);
        selectedObjectColorButton.setBackground(WindowItem.SELECTED_OBJECT_BACKGROUND_COLOR);

    }

    /**
     * Insert the method's description here.
     * Creation date: (11.3.2001 18:35:21)
     */
    public void loadCfg(String driver, String url, String user) {
        if (driver == null)
            driver = "";
        if (url == null)
            url = "";
        if (user == null)
            user = "";
        getDriverField().setText(driver);
        getURLField().setText(url);
        getUserField().setText(user);
        connection = new SQLConnection();
        connection.setDriver(driver);
        connection.setUrl(url);
        connection.setUser(user);
        /*
      connection.setLog(con.getLog());
      connection.showLog();
      connection.hideLog();
      */
    }
/**
 * Starts the application.
 * @param args an array of command-line arguments
 */
    /**
     * Starts the application.
     *
     * @param args an array of command-line arguments
     */
    public static void main(java.lang.String[] args) {
        // Insert code to start the application here.
/*	try {
		OptionsDialog aSendSQL;
		aSendSQL = new OptionsDialog();
		aSendSQL.setModal(true);
		try {
			Class aCloserClass = Class.forName("com.ibm.uvm.abt.edit.WindowCloser");
			Class parmTypes[] = { java.awt.WindowItem.class };
			Object parms[] = { aSendSQL };
			java.lang.reflect.Constructor aCtor = aCloserClass.getConstructor(parmTypes);
			aCtor.newInstance(parms);
		} catch (java.lang.Throwable exc) {
		};
		aSendSQL.pack();
		aSendSQL.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JDialog");
		exception.printStackTrace(System.out);
	}*/
    }

    /**
     * Comment
     */
    private void oKButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
        applyButton_ActionPerformed(actionEvent);
        this.setVisible(false);
    }

    /**
     * Comment
     */
    private void applyButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
//        connection.setDriver(getDriverField().getText());
//        connection.setUrl(getURLField().getText());
//        connection.setUser(getUserField().getText());
//        connection.setPasswd(new String(getPasswordField().getPassword()));
        AppPrefs.storeProperty(AppPrefs.ENCODING, (String) ivjCodingField.getSelectedItem());
        AppPrefs.storeProperty(AppPrefs.GENERAL_DEFNOTATION, ivjNotationField.getSelectedIndex());
        ConceptualConstruct.SHOW_PK_IN_UML = ivjPkUMLField.getSelectedIndex();
        ConceptualConstruct.SHOW_SHORTEN_CARD_IN_UML = ivjCardUMLField.getSelectedIndex();
        WindowItem.OBJECT_FOREGROUND_COLOR = foregroundColorButton.getBackground();
        WindowItem.BACKGROUND_COLOR = backgroundColorButton.getBackground();
        WindowItem.OBJECT_BACKGROUND_COLOR = objectBackgroundColorButton.getBackground();
        WindowItem.SELECTED_OBJECT_BACKGROUND_COLOR = selectedObjectColorButton.getBackground();

        erm.repaint();
        //erm.writeUserConfigFile(connection.getDriver(), connection.getUrl(), connection.getUser());
        erm.writeUserConfigFile("", "", "");
    }

    /**
     * Comment
     */
    public void ForegroundButton_ActionPerformed() {
        Color newColor = JColorChooser.showDialog(
                OptionsDialog.this,
                "Choose Background Color",
                Color.BLACK);
        if (newColor != null) foregroundColorButton.setBackground(newColor);
    }

    /**
     * Comment
     */
    public void BackgroundButton_ActionPerformed() {
        Color newColor = JColorChooser.showDialog(
                OptionsDialog.this,
                "Choose Background Color",
                Color.BLACK);
        if (newColor != null) backgroundColorButton.setBackground(newColor);
    }

    /**
     * Comment
     */
    public void objectBackgroundButton_ActionPerformed() {
        Color newColor = JColorChooser.showDialog(
                OptionsDialog.this,
                "Choose Background Color",
                Color.BLACK);
        if (newColor != null) objectBackgroundColorButton.setBackground(newColor);
    }

    /**
     * Comment
     */
    public void selectedObjectButton_ActionPerformed() {
        Color newColor = JColorChooser.showDialog(
                OptionsDialog.this,
                "Choose Background Color",
                Color.BLACK);
        if (newColor != null) selectedObjectColorButton.setBackground(newColor);
    }
/**
 * Comment
 */
    /*
    public void saveButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
        ObjectOutputStream so;
        java.net.URL url;

        try {
            try {
                ObjectInputStream si = new ObjectInputStream(new FileInputStream(defDir+"\\"+FNAME_CFG));
                si.close();
                so = new ObjectOutputStream(new FileOutputStream(defDir+"\\"+FNAME_CFG));
            } catch (Exception e) {
                try {
                    url=ClassLoader.getSystemResource(FNAME_CFG);
                    so = new ObjectOutputStream(new FileOutputStream(url.getFile()));
                } catch (Exception e1) {
                    so = new ObjectOutputStream(new FileOutputStream(defDir+"\\"+FNAME_CFG));
                }
            }
            connection.setDriver(getDriverField().getText());
            connection.setUrl(getURLField().getText());
            connection.setUser(getUserField().getText());
            connection.setPasswd(new String(getPasswordField().getPassword()));
            so.writeObject(connection);
            so.flush();
            so.close();
        } catch (Exception e) {
            ShowException d=new ShowException(null,"Error in saving configuration",e,true);
        }
    }
    */
/**
 * Comment
 */
    public void sendButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println(getSql());
    }

    /**
     * Insert the method's description here.
     * Creation date: (16.3.2001 18:26:28)
     *
     * @param newSql java.lang.String
     */
    public void setSql(java.lang.String newSql) {
        Sql = newSql;
    }

    public void test() {
        connection.test(getDriverField().getText(), getURLField().getText(), getUserField().getText(), new String(getPasswordField().getPassword()));
    }
}