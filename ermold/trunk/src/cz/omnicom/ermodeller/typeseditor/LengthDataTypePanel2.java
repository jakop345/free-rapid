package cz.omnicom.ermodeller.typeseditor;

import cz.omnicom.ermodeller.datatype.LengthDataType;
import cz.omnicom.ermodeller.datatype.LengthDataTypePanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;

/**
 * Panel to customize <code>LengthDataType</code> in defining user types
 */
public class LengthDataTypePanel2 extends LengthDataTypePanel {

    private final ActionListener al = new ActionListener() {
        public void actionPerformed(ActionEvent ae) {
            ivjLengthDataType = (LengthDataType) getLengthDataType().clone();

            if (ae.getSource() == getUpButton())
                connEtoM1(ae);
            if (ae.getSource() == getdownButton())
                connEtoM2(ae);

            //System.out.println("actionPerformed(java.awt.event.ActionEvent e) 2 "+ae.paramString());
            ((UserTypesEditorPanel) getDataTypeEditor()).getEditor().setActualType(ivjLengthDataType);
        }
    };

    public LengthDataTypePanel2() {
        super();
        initialize();
    }

    /**
     * Initialize the class.
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    protected void initialize() {
        try {
            setName("CharDataTypePanel2");
            setLayout(null);
            setBackground(new java.awt.Color(204, 204, 204));
            setSize(114, 53);
            add(getLengthTextField(), getLengthTextField().getName());
            add(getdownButton(), getdownButton().getName());
            add(getUpButton(), getUpButton().getName());
            add(getLengthLabel(), getLengthLabel().getName());
            initConnections();
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }

        getLengthTextField().addFocusListener(this);
    }

    /**
     * Initializes connections
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    protected void initConnections() {
        getLengthTextField().addKeyListener(this);
        getdownButton().addMouseListener(this);
        getUpButton().addMouseListener(this);
        getUpButton().addActionListener(al);
        getdownButton().addActionListener(al);
    }

    /*
    public void propertyChange(PropertyChangeEvent anEvent) {
        if (anEvent.getPropertyName().equals(LengthDataType.LENGTH_PROPERTY_CHANGE)) {
            getLengthTextField().setText(anEvent.getNewValue().toString());
            return;
        }
        firePropertyChange(anEvent.getPropertyName(), anEvent.getOldValue(), anEvent.getNewValue());
    }
    */
    public void updateFields(ComponentEvent e) {
        int i;
        Integer in;

        try {
            i = Integer.parseInt(getLengthTextField().getText());
            if (getLengthDataType().evaluateLength(i)) {
                ivjLengthDataType = (LengthDataType) getLengthDataType().clone();
                getLengthDataType().setLength(i);
                ((UserTypesEditorPanel) getDataTypeEditor()).getEditor().setActualType(ivjLengthDataType);
            } else {
                in = new Integer(getLengthDataType().getLength());
                getLengthTextField().setText(in.toString());
            }
        }
        catch (Exception ex) {
            in = new Integer(getLengthDataType().getLength());
            getLengthTextField().setText(in.toString());
        }
    }
}