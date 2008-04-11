package cz.green.swing;

import cz.green.util.ActionAdapter;

import javax.swing.*;
import java.awt.*;

/**
 * This type was created in VisualAge.
 */
public class ShowException extends JDialog {
    private JButton closeButton = null;
    private JButton detailButton = null;
    private boolean detailed = false;
    private JLabel message = null;
    protected Throwable th = null;
    private JTextArea detail = null;

    /**
     * ShowException constructor comment.
     *
     * @param owner java.awt.Frame
     * @param title java.lang.String
     */
    public ShowException(java.awt.Frame owner, String title, Throwable th) {
        super(owner, title, true);
        this.th = th;
        getContentPane().setLayout(new CountLayout());
        getContentPane().add(getMessage(), new SimpleBoundsConstraint(
                new LinearConstraint(5, 0.0, -10, 1.0), new ConstantConstraint(5, 25)));
        getContentPane().add(getDetailButton(), new SimpleBoundsConstraint(
                new LinearConstraint(-103, 0.5, 100, 0.0), new ConstantConstraint(35, 25)));
        getContentPane().add(getCloseButton(), new SimpleBoundsConstraint(
                new LinearConstraint(3, 0.5, 100, 0.0), new ConstantConstraint(35, 25)));
        getContentPane().add(getDetail(), new SimpleBoundsConstraint(
                new LinearConstraint(5, 0.0, -10, 1.0), new LinearConstraint(65, 0.0, -70, 1.0)));
        setDetailed(false);
        setLocationRelativeTo(owner);
    }

    /**
     * ShowException constructor comment.
     *
     * @param owner java.awt.Frame
     * @param title java.lang.String
     */
    public ShowException(java.awt.Frame owner, String title, Throwable th, boolean show) {
        this(owner, title, th);
        if (show)
            show();
    }

    public ShowException(Exception e) {
        this(Frame.getFrames()[0], "Error", e);
    }

    /**
     * This method was created in VisualAge.
     *
     * @return javax.swing.JButton
     */
    public JButton getCloseButton() {
        if (closeButton == null) {
            closeButton = new JButton("Close");
            closeButton.setMnemonic('c');
            getRootPane().setDefaultButton(closeButton);
            try {
                getCloseButton().addActionListener(new ActionAdapter(this, "dispose"));
            } catch (NoSuchMethodException e) {
            }
        }
        return closeButton;
    }

    /**
     * This method was created in VisualAge.
     *
     * @return javax.swing.JTextArea
     */
    public JTextArea getDetail() {
        if (detail == null) {
            java.io.StringWriter os = null;
            try {
                os = new java.io.StringWriter();
                java.io.PrintWriter pw = new java.io.PrintWriter(os);
                th.printStackTrace(pw);
            } catch (Throwable t) {
            }
            detail = new JTextArea(os.toString());
            detail.setPreferredSize(new java.awt.Dimension(200, 150));
        }
        return detail;
    }

    /**
     * This method was created in VisualAge.
     *
     * @return javax.swing.JButton
     */
    public JButton getDetailButton() {
        if (detailButton == null) {
            detailButton = new JButton("");
            try {
                getDetailButton().addActionListener(new ActionAdapter(this, "switchDetailed"));
            } catch (NoSuchMethodException e) {
            }
        }
        return detailButton;
    }

    /**
     * This method was created in VisualAge.
     *
     * @return boolean
     */
    public boolean getDetailed() {
        return detailed;
    }

    /**
     * This method was created in VisualAge.
     *
     * @return javax.swing.JLabel
     */
    public JLabel getMessage() {
        if (message == null) {
            message = new JLabel(th.getMessage());
//		message.setHorizontalTextPosition(JLabel.CENTER);
            message.setHorizontalAlignment(JLabel.CENTER);
        }
        return message;
    }

    /**
     * This method was created in VisualAge.
     *
     * @param newValue boolean
     */
    public void setDetailed(boolean detailed) {
        this.detailed = detailed;
        getDetailButton().setText(detailed ? "Hide more" : "Show more");
        getDetailButton().setMnemonic('m');
        getDetail().setVisible(detailed);
        validate();
        pack();
    }

    /**
     * This method was created in VisualAge.
     *
     * @param newValue boolean
     */
    public void switchDetailed() {
        setDetailed(!getDetailed());
}
}
