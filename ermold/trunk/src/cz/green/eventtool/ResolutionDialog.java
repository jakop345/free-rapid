package cz.green.eventtool;

import cz.green.swing.ConstantConstraint;
import cz.green.swing.CountLayout;
import cz.green.swing.LinearConstraint;
import cz.green.swing.SimpleBoundsConstraint;
import cz.green.util.ActionAdapter;

import javax.swing.*;
import java.awt.event.KeyListener;

/**
 * Asks user for the resolution, used when saving image to a file.
 */
public class ResolutionDialog extends JDialog implements KeyListener {
    /**
     * The cancel button
     */
    private JButton cancelButton = null;
    /**
     * The ok button
     */
    private JButton okButton = null;
    /**
     * The label x
     */
    private JLabel xLabel = null;
    /**
     * The label y
     */
    private JLabel yLabel = null;
    /**
     * The input field fot the x resolution
     */
    private JTextField xText = null;
    /**
     * The input field fot the y resolution
     */
    private JTextField yText = null;
    /**
     * The property with the choosed resolution
     */
    protected java.awt.Dimension resolution = null;

    /**
     * Constructs the modal dialog.
     *
     * @see java.awt.Dialog#Dialog(java.awt.Frame parent)
     */
    public ResolutionDialog(java.awt.Frame parent) {
        super(parent);
        setName("ResolutionDialog");
        getContentPane().setLayout(new CountLayout());
        setTitle("Edit resolution");
        setModal(true);
        getContentPane().add(getCancelButton(), new SimpleBoundsConstraint(
                new LinearConstraint(-93, 0.5, 90, 0.0), new ConstantConstraint(44, 23)));
        getContentPane().add(getOkButton(), new SimpleBoundsConstraint(
                new LinearConstraint(3, 0.5, 90, 0.0), new ConstantConstraint(44, 23)));
        getContentPane().add(getXLabel(), new SimpleBoundsConstraint(
                new ConstantConstraint(7, 12), new ConstantConstraint(13, 23)));
        getContentPane().add(getYLabel(), new SimpleBoundsConstraint(
                new ConstantConstraint(128, 12), new ConstantConstraint(13, 23)));
        getContentPane().add(getXText(), new SimpleBoundsConstraint(
                new ConstantConstraint(26, 95), new ConstantConstraint(13, 23)));
        getContentPane().add(getYText(), new SimpleBoundsConstraint(
                new ConstantConstraint(146, 95), new ConstantConstraint(13, 23)));
        getRootPane().setDefaultButton(getOkButton());
        pack();
        setLocationRelativeTo(parent);
        getXText().addKeyListener(this);
        getYText().addKeyListener(this);
        try {
            getOkButton().addActionListener(new ActionAdapter(this, "okButtonAction"));
            getCancelButton().addActionListener(new ActionAdapter(this, "dispose"));
        } catch (NoSuchMethodException x) {
        }
    }

    /**
     * Return the button - user press it when it wants to cancel the edits.
     */
    private JButton getCancelButton() {
        if (cancelButton == null) {
            cancelButton = new JButton("Cancel");
            cancelButton.setPreferredSize(new java.awt.Dimension(71, 23));
        }
        return cancelButton;
    }

    /**
     * Return the button - user press it when it wants to confirm.
     */
    private JButton getOkButton() {
        if (okButton == null) {
            okButton = new JButton("OK");
            okButton.setPreferredSize(new java.awt.Dimension(71, 23));
        }
        return okButton;
    }

    /**
     * Return the value of the <b>resolution</b> property.
     *
     * @return java.awt.Dimension
     */
    public java.awt.Dimension getResolution() {
        return resolution;
    }

    /**
     * Return the label for the x resolution.
     */
    private JLabel getXLabel() {
        if (xLabel == null) {
            xLabel = new JLabel("X:");
        }
        return xLabel;
    }

    /**
     * Return the input field for the x resolution.
     */
    private JTextField getXText() {
        if (xText == null) {
            xText = new JTextField();
        }
        return xText;
    }

    /**
     * Return the label for the y resolution.
     */
    private JLabel getYLabel() {
        if (yLabel == null) {
            yLabel = new JLabel("Y:");
        }
        return yLabel;
    }

    /**
     * Return the input field for the x resolution.
     */
    private JTextField getYText() {
        if (yText == null) {
            yText = new JTextField();
        }
        return yText;
    }

    /**
     * Exists for implementing the interface KeyListener
     *
     * @see java.awt.event.KeyListener
     */
    public void keyPressed(java.awt.event.KeyEvent e) {
    }

    /**
     * In this methods we are notified about pressing the Enter key. We do the same action as when the ok button is pressed.
     * This method is the reason to implement interface KeyListener.
     *
     * @see java.awt.event.KeyListener
     */
    public void keyReleased(java.awt.event.KeyEvent e) {
        if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER)
            okButtonAction();
    }

    /**
     * Exists for implementing the interface KeyListener
     *
     * @see java.awt.event.KeyListener
     */
    public void keyTyped(java.awt.event.KeyEvent e) {
    }

    /**
     * Invoked when ok button pressed. If user pressed ok to confirms the filled resolution,
     * it tries to parse the values from the input fields. When it is possible set new values to the
     * <b>resolution</b> property and disposes the dialog.
     * Otherwise set to the input field the old values and finished.
     */
    public void okButtonAction() {
        try {
            if (resolution == null) {
                resolution = new java.awt.Dimension(0, 0);
            }
            resolution.height = Integer.parseInt(getYText().getText());
            resolution.width = Integer.parseInt(getXText().getText());
            dispose();
        } catch (NumberFormatException ex) {
            setResolution(getResolution());
        }
    }

    /**
     * Enabled to set the resolution value to the <b>resolution</b> property.
     * Also set the text into input fields.
     *
     * @param res The new resolution.
     */
    public void setResolution(java.awt.Dimension res) {
        this.resolution = res;
        getXText().setText(Integer.toString(res.width));
        getYText().setText(Integer.toString(res.height));
    }
}
