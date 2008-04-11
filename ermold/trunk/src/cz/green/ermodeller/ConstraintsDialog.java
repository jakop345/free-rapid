package cz.green.ermodeller;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Property editor for the <code>DataType</code> beans.
 */
public class ConstraintsDialog extends JDialog implements java.awt.event.ActionListener {
    /**
     * Datatype to be edited.
     * @see cz.omnicom.ermodeller.datatype.DataType
     */
    /**
     * Panel which customizes the <code>dataType</code>.
     *
     * @see cz.omnicom.ermodeller.datatype.DataTypePanel
     */
    private JTextArea ivjTextArea = null;
    private cz.omnicom.ermodeller.conceptual.Entity Cent = null;
    final JButton OKbutton = new JButton();
    final JButton CancelButton = new JButton();
    final JButton ApplyButton = new JButton();

    /**
     * Constructor
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    public ConstraintsDialog(JFrame owner, cz.omnicom.ermodeller.conceptual.Entity ent) {
        super(owner, "Constraints Editor");
        initialize();
        this.Cent = ent;
    }

    public void actionPerformed(java.awt.event.ActionEvent e) {
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
            setName("ConstraintsDialog");
            this.setName("AA");
            setPreferredSize(new java.awt.Dimension(280, 270));
            setLayout(null);
            setSize(new Dimension(280, 270));
            OKbutton.setBounds(new Rectangle(13, 204, 73, 25));
            OKbutton.setText("OK");
            OKbutton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    setValue(ivjTextArea.getText());
                    setVisible(false);
                }
            });
            CancelButton.setBounds(new Rectangle(178, 204, 73, 25));
            CancelButton.setText("Cancel");
            CancelButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    setVisible(false);
                }
            });
            ApplyButton.setBounds(new Rectangle(96, 204, 73, 25));
            ApplyButton.setText("Apply");
            ApplyButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    setValue(ivjTextArea.getText());
                }
            });
            this.add(getTextArea(), getTextArea().getName());
            this.getContentPane().add(OKbutton, null);
            this.getContentPane().add(CancelButton, null);
            this.getContentPane().add(ApplyButton, null);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    /**
     * Return the DataTypePanel property value.
     *
     * @return javax.swing.JPanel
     */
    private javax.swing.JTextArea getTextArea() {
        if (ivjTextArea == null) {
            try {
                ivjTextArea = new javax.swing.JTextArea();
                ivjTextArea.setName("DataTypePanel");
                ivjTextArea.setOpaque(true);
                ivjTextArea.setLayout(null);
                ivjTextArea.setBounds(new Rectangle(9, 5, 254, 191));
                ivjTextArea.setBackground(Color.white);
                ivjTextArea.setEnabled(true);
                ivjTextArea.setText(getValue());
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjTextArea;
    }

    /**
     * @return false.
     */
    public boolean isPaintable() {
        return false;
    }

    /**
     * Sets the datatype to be edited.
     *
     * @param java.lang.Object value
     */
    public synchronized void setValue(Object value) {
        Cent.setConstraints((String) value);
    }

    public String getValue() {
        // TODO Auto-generated method stub
        //value = getValue();
        return Cent.getConstraints();
}

}