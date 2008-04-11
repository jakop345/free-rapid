package cz.omnicom.ermodeller.conc2obj;

/**
 * Insert the type's description here.
 * Creation date: (17.4.2001 18:02:26)
 *
 * @author:
 */
public class LogSQLObj extends javax.swing.JDialog {

    class IvjEventHandler implements java.awt.event.ActionListener {
        public void actionPerformed(java.awt.event.ActionEvent e) {
            if (e.getSource() == LogSQLObj.this.getClearButton())
                connEtoC1(e);
        }

    }

    private javax.swing.JButton ivjClearButton = null;
    final IvjEventHandler ivjEventHandler = new IvjEventHandler();
    private javax.swing.JPanel ivjJDialogContentPane = null;
    private javax.swing.JScrollPane ivjJScrollPane1 = null;
    private javax.swing.JTextArea ivjJTextArea1 = null;

    /**
     * LogSQL constructor comment.
     */
    public LogSQLObj() {
        super();
        initialize();
    }

    /**
     * LogSQL constructor comment.
     *
     * @param owner java.awt.Dialog
     */
    public LogSQLObj(java.awt.Dialog owner) {
        super(owner);
    }

    /**
     * LogSQL constructor comment.
     *
     * @param owner java.awt.Dialog
     * @param title java.lang.String
     */
    public LogSQLObj(java.awt.Dialog owner, String title) {
        super(owner, title);
    }

    /**
     * LogSQL constructor comment.
     *
     * @param owner java.awt.Dialog
     * @param title java.lang.String
     * @param modal boolean
     */
    public LogSQLObj(java.awt.Dialog owner, String title, boolean modal) {
        super(owner, title, modal);
    }

    /**
     * LogSQL constructor comment.
     *
     * @param owner java.awt.Dialog
     * @param modal boolean
     */
    public LogSQLObj(java.awt.Dialog owner, boolean modal) {
        super(owner, modal);
    }

    /**
     * LogSQL constructor comment.
     *
     * @param owner java.awt.Frame
     */
    public LogSQLObj(java.awt.Frame owner) {
        super(owner);
    }

    /**
     * LogSQL constructor comment.
     *
     * @param owner java.awt.Frame
     * @param title java.lang.String
     */
    public LogSQLObj(java.awt.Frame owner, String title) {
        super(owner, title);
    }

    /**
     * LogSQL constructor comment.
     *
     * @param owner java.awt.Frame
     * @param title java.lang.String
     * @param modal boolean
     */
    public LogSQLObj(java.awt.Frame owner, String title, boolean modal) {
        super(owner, title, modal);
    }

    /**
     * LogSQL constructor comment.
     *
     * @param owner java.awt.Frame
     * @param modal boolean
     */
    public LogSQLObj(java.awt.Frame owner, boolean modal) {
        super(owner, modal);
    }

    public void append(String s) {
        getJTextArea1().append(s);
        getJTextArea1().revalidate();
    }

    public void clear() {
        getJTextArea1().setText("");
    }

    /**
     * Comment
     */
    public void clearButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
        clear();
    }

    /**
     * connEtoC1:  (ClearButton.action.actionPerformed(java.awt.event.ActionEvent) --> LogSQL.clearButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
     *
     * @param arg1 java.awt.event.ActionEvent
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private void connEtoC1(java.awt.event.ActionEvent arg1) {
        try {
            // user code begin {1}
            // user code end
            this.clearButton_ActionPerformed(arg1);
            // user code begin {2}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * Return the ClearButton property value.
     *
     * @return javax.swing.JButton
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JButton getClearButton() {
        if (ivjClearButton == null) {
            try {
                ivjClearButton = new javax.swing.JButton();
                ivjClearButton.setName("ClearButton");
                ivjClearButton.setText("Clear");
                ivjClearButton.setContentAreaFilled(true);
                ivjClearButton.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjClearButton;
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
                ivjJDialogContentPane.setLayout(new java.awt.BorderLayout());
                getJDialogContentPane().add(getJScrollPane1(), "Center");
                getJDialogContentPane().add(getClearButton(), "South");
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
                getJScrollPane1().setViewportView(getJTextArea1());
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
                ivjJTextArea1.setBounds(0, 0, 160, 120);
                // user code begin {1}
                ivjJTextArea1.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 12));
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
    private void initConnections() {
        // user code begin {1}
        // user code end
        getClearButton().addActionListener(ivjEventHandler);
    }

    /**
     * Initialize the class.
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private void initialize() {
        try {
            // user code begin {1}
            // user code end
            setName("LogSQL");
            setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
            setSize(420, 377);
            setTitle("Sql log");
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
            LogSQLObj aLogSQL;
            aLogSQL = new LogSQLObj();
            aLogSQL.setModal(true);
            aLogSQL.addWindowListener(new java.awt.event.WindowAdapter() {
                public void windowClosing(java.awt.event.WindowEvent e) {
                    System.exit(0);
                }

            });
            aLogSQL.setVisible(true);
            java.awt.Insets insets = aLogSQL.getInsets();
            aLogSQL.setSize(aLogSQL.getWidth() + insets.left + insets.right, aLogSQL.getHeight() + insets.top + insets.bottom);
            aLogSQL.setVisible(true);
        } catch (Throwable exception) {
            System.err.println("Exception occurred in main() of javax.swing.JDialog");
            exception.printStackTrace(System.out);
        }
    }
}
