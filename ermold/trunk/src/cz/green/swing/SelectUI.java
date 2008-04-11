package cz.green.swing;

import cz.green.util.ActionAdapter;
import cz.green.util.ParamActionAdapter;

import javax.swing.*;
import java.awt.*;

/**
 * This type was created by Jiri Mares
 */
public class SelectUI extends JPanel {
    private JList ivjFeels = null;
    private JButton ivjOkButton = null;
    private JScrollPane ivjScrollPane = null;
    private BorderLayout ivjSelectUIBorderLayout = null;
    private JPanel ivjButtonPanel = null;
    private FlowLayout ivjButtonPanelFlowLayout = null;
    private JButton ivjCancelButton = null;
    protected Component root = null;

    /**
     * Constructor
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    public SelectUI() {
        super();
        initialize();
    }

    /**
     * Return the ButtonPanel property value.
     *
     * @return javax.swing.JPanel
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JPanel getButtonPanel() {
        if (ivjButtonPanel == null) {
            try {
                ivjButtonPanel = new javax.swing.JPanel();
                ivjButtonPanel.setName("ButtonPanel");
                ivjButtonPanel.setPreferredSize(new java.awt.Dimension(0, 25));
                ivjButtonPanel.setLayout(getButtonPanelFlowLayout());
                getButtonPanel().add(getOkButton(), getOkButton().getName());
                getButtonPanel().add(getCancelButton(), getCancelButton().getName());
                // user code begin {1}
                ivjButtonPanel.getRootPane().setDefaultButton(getOkButton());
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjButtonPanel;
    }

    /**
     * Return the ButtonPanelFlowLayout property value.
     *
     * @return java.awt.FlowLayout
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private java.awt.FlowLayout getButtonPanelFlowLayout() {
        java.awt.FlowLayout ivjButtonPanelFlowLayout = null;
        try {
            /* Create part */
            ivjButtonPanelFlowLayout = new java.awt.FlowLayout();
            ivjButtonPanelFlowLayout.setVgap(0);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
        ;
        return ivjButtonPanelFlowLayout;
    }

    /**
     * Return the JButton2 property value.
     *
     * @return javax.swing.JButton
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JButton getCancelButton() {
        if (ivjCancelButton == null) {
            try {
                ivjCancelButton = new javax.swing.JButton();
                ivjCancelButton.setName("CancelButton");
                ivjCancelButton.setText("Cancel");
                ivjCancelButton.setActionCommand("CancelButton");
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
     * Return the Feels property value.
     *
     * @return javax.swing.JList
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JList getFeels() {
        if (ivjFeels == null) {
            try {
                ivjFeels = new javax.swing.JList();
                ivjFeels.setName("Feels");
                ivjFeels.setBounds(0, 0, 160, 120);
                // user code begin {1}
                UIManager.LookAndFeelInfo[] feels = UIManager.getInstalledLookAndFeels();
                String[] names = new String[feels.length];
                for (int i = feels.length - 1; i >= 0; i--)
                    names[i] = feels[i].getName();
                ivjFeels.setListData(names);
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjFeels;
    }

    /**
     * Return the OkButton property value.
     *
     * @return javax.swing.JButton
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JButton getOkButton() {
        if (ivjOkButton == null) {
            try {
                ivjOkButton = new javax.swing.JButton();
                ivjOkButton.setName("OkButton");
                ivjOkButton.setText("OK");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjOkButton;
    }

    /**
     * Return the ScrollPane property value.
     *
     * @return javax.swing.JScrollPane
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JScrollPane getScrollPane() {
        if (ivjScrollPane == null) {
            try {
                ivjScrollPane = new javax.swing.JScrollPane();
                ivjScrollPane.setName("ScrollPane");
                getScrollPane().setViewportView(getFeels());
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjScrollPane;
    }

    /**
     * This method was created by Jiri Mares
     *
     * @return java.lang.String
     */
    public String getSelectedLookAndFeel() {
        return (UIManager.getInstalledLookAndFeels()[getFeels().getSelectedIndex()]).getClassName();
    }

    /**
     * Return the SelectUIBorderLayout property value.
     *
     * @return java.awt.BorderLayout
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private java.awt.BorderLayout getSelectUIBorderLayout() {
        java.awt.BorderLayout ivjSelectUIBorderLayout = null;
        try {
            /* Create part */
            ivjSelectUIBorderLayout = new java.awt.BorderLayout();
            ivjSelectUIBorderLayout.setVgap(5);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
        ;
        return ivjSelectUIBorderLayout;
    }

    /**
     * This method was created by Jiri Mares
     *
     * @return javax.swing.JDialog
     */
    public static JDialog getSelectUIDialog(Component root) {
        JDialog dialog = new JDialog((Frame) null, "Select new UI", true);
        SelectUI sui = new SelectUI();
        dialog.getContentPane().add(sui, "Center");
        sui.root = root;
        try {
            sui.getCancelButton().addActionListener(new ActionAdapter(dialog, "dispose"));
            sui.getOkButton().addActionListener(new ParamActionAdapter(sui, "switchUI", dialog, JDialog.class));
        } catch (NoSuchMethodException x) {
        }
        dialog.pack();
        return dialog;
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
     * Initialize the class.
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private void initialize() {
        try {
            // user code begin {1}
            // user code end
            setName("SelectUI");
            setLayout(getSelectUIBorderLayout());
            setSize(200, 150);
            add(getScrollPane(), "Center");
            add(getButtonPanel(), "South");
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
        // user code begin {2}
        setPreferredSize(new Dimension(200, 150));
        // user code end
    }

    /**
     * main entrypoint - starts the part when it is run as an application
     *
     * @param args java.lang.String[]
     */
    public static void main(java.lang.String[] args) {
        try {
            JFrame frame = new JFrame();
            SelectUI aSelectUI = new SelectUI();
            frame.add("Center", aSelectUI);
            frame.setSize(aSelectUI.getSize());
            frame.setVisible(true);
        } catch (Throwable exception) {
            System.err.println("Exception occurred in main() of javax.swing.JPanel");
            exception.printStackTrace(System.out);
        }
    }

    /**
     * This method was created by Jiri Mares
     */
    public void switchUI(JDialog dialog) {
        try {
            UIManager.setLookAndFeel(getSelectedLookAndFeel());
            if (root != null) {
                SwingUtilities.updateComponentTreeUI(root);
            }
        } catch (Throwable t) {
            ShowException x = new ShowException(null, "switching UI", t);
        }
        if (dialog != null) {
            dialog.dispose();
        }
    }
}
