package cz.felk.cvut.erm.dialogs;

import cz.felk.cvut.erm.app.Consts;

import javax.swing.*;
import java.awt.*;

/**
 * Dialog for program's preferences
 */
public class AboutDialog extends javax.swing.JDialog {
    private javax.swing.JPanel ivjJDialogContentPane = null;
//    private cz.felk.cvut.erm.sql.gui.SQLConnection connection = null;

    final JLabel AppNameLabel = new JLabel();
    final JLabel VersionLabel = new JLabel();
    final JLabel AuthorLabel = new JLabel();
    final JLabel auth1Label = new JLabel();
    final JLabel auth2Label = new JLabel();
    final JLabel auth3Label = new JLabel();
    final JLabel auth4Label = new JLabel();
    final JLabel auth5Label = new JLabel();
    final JLabel auth6Label = new JLabel();
    JTabbedPane jTabbedPane1 = new JTabbedPane();
    final GridBagLayout gridBagLayout1 = new GridBagLayout();

    /**
     * AboutDialog constructor comment.
     */
    public AboutDialog() {
        super();
        initialize();
    }

    /**
     * AboutDialog constructor comment.
     *
     * @param owner java.awt.Frame
     */
    public AboutDialog(java.awt.Frame owner) {
        super(owner);
        initialize();
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
                ivjJDialogContentPane.setMaximumSize(new Dimension(250, 160));
                ivjJDialogContentPane.setPreferredSize(new Dimension(250, 160));
                ivjJDialogContentPane.setRequestFocusEnabled(true);
                ivjJDialogContentPane.setLayout(gridBagLayout1);
                ivjJDialogContentPane.setMinimumSize(new Dimension(250, 160));

                ivjJDialogContentPane.add(AppNameLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
                        , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(41, 85, 0, 0), 0, 0));
                ivjJDialogContentPane.add(VersionLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
                        , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 85, 0, 18), 0, 0));

                ivjJDialogContentPane.add(AuthorLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
                        , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 110, 0, 0), 0, 0));
                ivjJDialogContentPane.add(auth1Label, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0
                        , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 104), 0, 0));
                ivjJDialogContentPane.add(auth2Label, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0
                        , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 86), 0, 0));
                ivjJDialogContentPane.add(auth3Label, new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0
                        , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 125), 0, 0));
                ivjJDialogContentPane.add(auth4Label, new GridBagConstraints(1, 5, 1, 1, 0.0, 0.0
                        , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 106), 0, 0));
                ivjJDialogContentPane.add(auth5Label, new GridBagConstraints(1, 6, 1, 1, 0.0, 0.0
                        , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 145), 0, 0));
                ivjJDialogContentPane.add(auth6Label, new GridBagConstraints(1, 7, 1, 1, 0.0, 0.0

                        , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 42, 119), 0, 0));
//                ImageIcon image = new ImageIcon(ClassLoader
//                        .getSystemResource("img/preferences.gif"));
//	        ivjJDialogContentPane.add(imageIcon,     new GridBagConstraints(1, 9, 1, 1, 0.0, 0.0
//		            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

            } catch (java.lang.Throwable ivjExc) {
                ivjExc.printStackTrace();
                //handleException(ivjExc);
            }
        }
        return ivjJDialogContentPane;
    }

//    private void handleException(java.lang.Throwable exception) {
//
//        /* Uncomment the following lines to print uncaught exceptions to stdout */
//        // System.out.println("--------- UNCAUGHT EXCEPTION ---------");
//        // exception.printStackTrace(System.out);
//    }

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
            setTitle("About ERM");
            setContentPane(getJDialogContentPane());
            AppNameLabel.setFont(new java.awt.Font("Dialog", 1, 16));
            AppNameLabel.setText("ER Modeller");
            VersionLabel.setFont(new java.awt.Font("Dialog", 0, 14));
            VersionLabel.setMaximumSize(new Dimension(94, 14));
            VersionLabel.setMinimumSize(new Dimension(94, 14));
            VersionLabel.setText("version: " + Consts.APPVERSION);
            AuthorLabel.setText("Authors:");
            auth2Label.setText("Tomáš Kremláèek");
            auth1Label.setText("Aleš Kopecký");
            //     auth3Label.setVerifyInputWhenFocusTarget(true);
            auth3Label.setText("Jiøí Mareš");
            auth4Label.setText("Štìpán Ježek");
            auth5Label.setText("Petr Šitych");
            auth6Label.setText("Zbynìk Øíha");
            //   this.getContentPane().add(getJDialogContentPane());
        } catch (java.lang.Throwable ivjExc) {
            ivjExc.printStackTrace();
            //handleException(ivjExc);
        }
        // user code begin {2}
        //loadCfg();
        // user code end
    }

//    /**
//     * Starts the application.
//     * @param args an array of command-line arguments
//     */
//    public static void main(java.lang.String[] args) {
//        // Insert code to start the application here.
//        try {
//            AboutDialog aSendSQL;
//            aSendSQL = new AboutDialog();
//            aSendSQL.setModal(true);
//            try {
//                Class aCloserClass = Class.forName("com.ibm.uvm.abt.edit.WindowCloser");
//                Class parmTypes[] = {java.awt.WindowItem.class};
//                Object parms[] = {aSendSQL};
//                java.lang.reflect.Constructor aCtor = aCloserClass.getConstructor(parmTypes);
//                aCtor.newInstance(parms);
//            } catch (java.lang.Throwable exc) {
//                exc.printStackTrace();
//            }
//
//            aSendSQL.pack();
//            aSendSQL.setVisible(true);
//        } catch (Throwable exception) {
//            System.err.println("Exception occurred in main() of javax.swing.JDialog");
//            exception.printStackTrace(System.out);
//        }
//    }

}