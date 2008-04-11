package cz.green.ermodeller;

import cz.omnicom.ermodeller.sql.SQLConnection;

/**
 * Dialog for program's preferences
 */
public class PreferencesPanel extends javax.swing.JPanel {
    private javax.swing.JPanel ivjJDialogContentPane = null;
    private javax.swing.JTextField ivjDriverField = null;
    private javax.swing.JPasswordField ivjPasswordField = null;
    private javax.swing.JTextField ivjURLField = null;
    private javax.swing.JTextField ivjUserField = null;
    private cz.omnicom.ermodeller.sql.SQLConnection connection = null;
    IvjEventHandler ivjEventHandler = new IvjEventHandler();
    private java.lang.String Sql;
    private javax.swing.JButton cancelButton = null;
    private javax.swing.JButton OKButton = null;
    private javax.swing.JPanel ivjJPanel1 = null;
    private java.awt.FlowLayout ivjJPanel1FlowLayout = null;
    private javax.swing.JLabel ivjDriverLabel = null;
    private javax.swing.JLabel ivjURL6 = null;
    private javax.swing.JLabel ivjUserLabel = null;
    private javax.swing.JLabel ivjPasswordLabel = null;
    private javax.swing.JButton testButton = null;
    private javax.swing.JButton logButton = null;
    private javax.swing.JPanel ivjJPanel2 = null;

    class IvjEventHandler implements java.awt.event.ActionListener {
        public void actionPerformed(java.awt.event.ActionEvent e) {
            if (e.getSource() == PreferencesPanel.this.getCancelButton())
                connEtoC3(e);
            if (e.getSource() == PreferencesPanel.this.getOKButton())
                connEtoC4(e);
            if (e.getSource() == PreferencesPanel.this.getTestButton())
                connEtoC5(e);
            if (e.getSource() == PreferencesPanel.this.getLogButton())
                connEtoC6();
        }

        ;
    }

    ;
    private java.lang.String defDir;

    /**
     * SendSQL constructor comment.
     */
    public PreferencesPanel() {
        super();
        initialize();
    }

    /**
     * Comment
     */
    public void cancelButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
        getDriverField().setText(connection.getDriver());
        getURLField().setText(connection.getUrl());
        getUserField().setText(connection.getUser());
        getPasswordField().setText(connection.getPasswd());
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
            // user code begin {1}
            // user code end
            this.jButton6_ActionEvents();
            // user code begin {2}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * Insert the method's description here.
     * Creation date: (1.6.2001 12:20:14)
     *
     * @return java.lang.String
     */
    java.lang.String getDefDir() {
        return defDir;
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
                ivjJDialogContentPane.setPreferredSize(new java.awt.Dimension(350, 210));
                ivjJDialogContentPane.setLayout(new java.awt.GridBagLayout());
                ivjJDialogContentPane.setMinimumSize(new java.awt.Dimension(350, 185));

                java.awt.GridBagConstraints constraintsDriverField = new java.awt.GridBagConstraints();
                constraintsDriverField.gridx = 1;
                constraintsDriverField.gridy = 0;
                constraintsDriverField.gridwidth = 3;
                constraintsDriverField.fill = java.awt.GridBagConstraints.HORIZONTAL;
                constraintsDriverField.anchor = java.awt.GridBagConstraints.WEST;
                constraintsDriverField.weightx = 1.0;
                constraintsDriverField.ipadx = 297;
                constraintsDriverField.insets = new java.awt.Insets(10, 11, 6, 8);
                getJDialogContentPane().add(getDriverField(), constraintsDriverField);

                java.awt.GridBagConstraints constraintsURLField = new java.awt.GridBagConstraints();
                constraintsURLField.gridx = 1;
                constraintsURLField.gridy = 1;
                constraintsURLField.gridwidth = 3;
                constraintsURLField.fill = java.awt.GridBagConstraints.HORIZONTAL;
                constraintsURLField.anchor = java.awt.GridBagConstraints.WEST;
                constraintsURLField.weightx = 1.0;
                constraintsURLField.ipadx = 297;
                constraintsURLField.insets = new java.awt.Insets(4, 11, 5, 8);
                getJDialogContentPane().add(getURLField(), constraintsURLField);

                java.awt.GridBagConstraints constraintsUserField = new java.awt.GridBagConstraints();
                constraintsUserField.gridx = 1;
                constraintsUserField.gridy = 2;
                constraintsUserField.fill = java.awt.GridBagConstraints.HORIZONTAL;
                constraintsUserField.anchor = java.awt.GridBagConstraints.WEST;
                constraintsUserField.weightx = 1.0;
                constraintsUserField.ipadx = 297;
                constraintsUserField.insets = new java.awt.Insets(5, 11, 5, 8);
                getJDialogContentPane().add(getUserField(), constraintsUserField);

                java.awt.GridBagConstraints constraintsPasswordField = new java.awt.GridBagConstraints();
                constraintsPasswordField.gridx = 1;
                constraintsPasswordField.gridy = 3;
                constraintsPasswordField.gridwidth = 3;
                constraintsPasswordField.fill = java.awt.GridBagConstraints.HORIZONTAL;
                constraintsPasswordField.anchor = java.awt.GridBagConstraints.WEST;
                constraintsPasswordField.weightx = 1.0;
                constraintsPasswordField.ipadx = 297;
                constraintsPasswordField.insets = new java.awt.Insets(5, 11, 5, 8);
                getJDialogContentPane().add(getPasswordField(), constraintsPasswordField);

                java.awt.GridBagConstraints constraintsJPanel1 = new java.awt.GridBagConstraints();
                constraintsJPanel1.gridx = 0;
                constraintsJPanel1.gridy = 5;
                constraintsJPanel1.gridwidth = 2;
                constraintsJPanel1.fill = java.awt.GridBagConstraints.HORIZONTAL;
                constraintsJPanel1.weightx = 1.0;
                constraintsJPanel1.weighty = 1.0;
                constraintsJPanel1.insets = new java.awt.Insets(4, 4, 4, 4);
                getJDialogContentPane().add(getJPanel1(), constraintsJPanel1);

                java.awt.GridBagConstraints constraintsJLabel5 = new java.awt.GridBagConstraints();
                constraintsJLabel5.gridx = 0;
                constraintsJLabel5.gridy = 0;
                constraintsJLabel5.anchor = java.awt.GridBagConstraints.WEST;
                constraintsJLabel5.insets = new java.awt.Insets(4, 4, 4, 4);
                getJDialogContentPane().add(getJLabel5(), constraintsJLabel5);

                java.awt.GridBagConstraints constraintsJLabel6 = new java.awt.GridBagConstraints();
                constraintsJLabel6.gridx = 0;
                constraintsJLabel6.gridy = 1;
                constraintsJLabel6.anchor = java.awt.GridBagConstraints.WEST;
                constraintsJLabel6.insets = new java.awt.Insets(4, 4, 4, 4);
                getJDialogContentPane().add(getJLabel6(), constraintsJLabel6);

                java.awt.GridBagConstraints constraintsJLabel7 = new java.awt.GridBagConstraints();
                constraintsJLabel7.gridx = 0;
                constraintsJLabel7.gridy = 2;
                constraintsJLabel7.anchor = java.awt.GridBagConstraints.WEST;
                constraintsJLabel7.insets = new java.awt.Insets(4, 4, 4, 4);
                getJDialogContentPane().add(getJLabel7(), constraintsJLabel7);

                java.awt.GridBagConstraints constraintsJLabel8 = new java.awt.GridBagConstraints();
                constraintsJLabel8.gridx = 0;
                constraintsJLabel8.gridy = 3;
                constraintsJLabel8.anchor = java.awt.GridBagConstraints.WEST;
                constraintsJLabel8.insets = new java.awt.Insets(4, 4, 4, 4);
                getJDialogContentPane().add(getJLabel8(), constraintsJLabel8);

                java.awt.GridBagConstraints constraintsJPanel2 = new java.awt.GridBagConstraints();
                constraintsJPanel2.gridx = 0;
                constraintsJPanel2.gridy = 4;
                constraintsJPanel2.gridwidth = 2;
                constraintsJPanel2.fill = java.awt.GridBagConstraints.BOTH;
                constraintsJPanel2.weightx = 1.0;
                constraintsJPanel2.weighty = 1.0;
                constraintsJPanel2.insets = new java.awt.Insets(4, 4, 4, 4);
                getJDialogContentPane().add(getJPanel2(), constraintsJPanel2);
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
            ivjJPanel1FlowLayout.setAlignment(java.awt.FlowLayout.CENTER);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
        ;
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
    java.lang.String getSql() {
        return Sql;
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

    /**
     * Called whenever the part throws an exception.
     *
     * @param exception java.lang.Throwable
     */
    private void handleException(java.lang.Throwable exception) {

        /* Uncomment the following lines to print uncaught exceptions to stdout */
        // System.out.println("--------- UNCAUGHT EXCEPTION ---------");
        // exception.printStackTrace(System.out);
    }

    /**
     * Initializes connections
     *
     * @throws java.lang.Exception The exception description.
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private void initConnections() throws java.lang.Exception {
        // user code begin {1}
        // user code end
        getCancelButton().addActionListener(ivjEventHandler);
        getOKButton().addActionListener(ivjEventHandler);
        getTestButton().addActionListener(ivjEventHandler);
        getLogButton().addActionListener(ivjEventHandler);
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
            setSize(350, 210);
            getJDialogContentPane();
            add(ivjJDialogContentPane);
            initConnections();
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
        // user code begin {2}
        //loadCfg();
        // user code end
    }

    /**
     * Comment
     */
    public void jButton6_ActionEvents() {
        connection.showLog();
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
     *
     * @param args an array of command-line arguments
     */
    public static void main(java.lang.String[] args) {
        // Insert code to start the application here.
        try {
            PreferencesPanel aSendSQL;
            aSendSQL = new PreferencesPanel();
//		aSendSQL.setModal(true);
            try {
                Class aCloserClass = Class.forName("com.ibm.uvm.abt.edit.WindowCloser");
                Class parmTypes[] = {java.awt.Window.class};
                Object parms[] = {aSendSQL};
                java.lang.reflect.Constructor aCtor = aCloserClass.getConstructor(parmTypes);
                aCtor.newInstance(parms);
            } catch (java.lang.Throwable exc) {
            }
            ;
//		aSendSQL.pack();
            aSendSQL.setVisible(true);
        } catch (Throwable exception) {
            System.err.println("Exception occurred in main() of javax.swing.JDialog");
            exception.printStackTrace(System.out);
        }
    }

    /**
     * Comment
     */
    public void oKButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
        connection.setDriver(getDriverField().getText());
        connection.setUrl(getURLField().getText());
        connection.setUser(getUserField().getText());
        connection.setPasswd(new String(getPasswordField().getPassword()));
        this.setVisible(false);
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
        return;
    }

    /**
     * Insert the method's description here.
     * Creation date: (1.6.2001 12:20:14)
     *
     * @param newDefDir java.lang.String
     */
    public void setDefDir(java.lang.String newDefDir) {
        defDir = newDefDir;
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