package cz.omnicom.ermodeller.typeseditor;

import cz.omnicom.ermodeller.datatype.GeneralNumberDataType;
import cz.omnicom.ermodeller.datatype.GeneralNumberDataTypePanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;

/**
 * Panel to customize <code>GeneralNumberDataType</code> in defining user types
 */
public class GeneralNumberDataTypePanel2 extends GeneralNumberDataTypePanel {

    ActionListener al = new ActionListener() {
        public void actionPerformed(ActionEvent ae) {
            ivjGeneralNumberDataType = (GeneralNumberDataType) getGeneralNumberDataType().clone();

            if (ae.getSource() == getPrecisionDownButton())
                connEtoM1(ae);
            if (ae.getSource() == getPrecisionUpButton())
                connEtoM2(ae);
            if (ae.getSource() == getScaleUpButton())
                connEtoM3(ae);
            if (ae.getSource() == getScaleDownButton())
                connEtoM4(ae);
            //System.out.println("actionPerformed(java.awt.event.ActionEvent e) 2 "+ae.paramString());
            ((UserTypesEditorPanel) getDataTypeEditor()).getEditor().setActualType(ivjGeneralNumberDataType);
        }
    };

    public GeneralNumberDataTypePanel2() {
        super();
        initialize();
    }

    /**
     * Method to handle events for the ActionListener interface.
     *
     * @param e java.awt.event.ActionEvent
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    public void actionPerformed(java.awt.event.ActionEvent e) {
        ivjGeneralNumberDataType = (GeneralNumberDataType) getGeneralNumberDataType().clone();

        if (e.getSource() == getPrecisionDownButton())
            connEtoM1(e);
        if (e.getSource() == getPrecisionUpButton())
            connEtoM2(e);
        if (e.getSource() == getScaleUpButton())
            connEtoM3(e);
        if (e.getSource() == getScaleDownButton())
            connEtoM4(e);
        //System.out.println("actionPerformed(java.awt.event.ActionEvent e) 2 "+e.paramString());
        ((UserTypesEditorPanel) getDataTypeEditor()).getEditor().setActualType(ivjGeneralNumberDataType);
    }

    /**
     * Initialize the class.
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    protected void initialize() {
        try {
            // user code begin {1}
            // user code end
            setName("GeneralNumberDataTypePanel2");
            setLayout(null);
            setSize(114, 109);
            add(getPrecisionTextField(), getPrecisionTextField().getName());
            add(getScaleTextField(), getScaleTextField().getName());
            add(getPrecisionLabel(), getPrecisionLabel().getName());
            add(getScaleLabel(), getScaleLabel().getName());
            add(getPrecisionDownButton(), getPrecisionDownButton().getName());
            add(getPrecisionUpButton(), getPrecisionUpButton().getName());
            add(getScaleDownButton(), getScaleDownButton().getName());
            add(getScaleUpButton(), getScaleUpButton().getName());
            initConnections();
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
        // user code begin {2}
        // user code end
    }

    /**
     * Initializes connections
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    protected void initConnections() throws java.lang.Exception {
        // user code begin {1}
        // user code end
        getPrecisionDownButton().addMouseListener(this);
        getPrecisionUpButton().addMouseListener(this);
        getScaleUpButton().addMouseListener(this);
        getScaleDownButton().addMouseListener(this);
        getPrecisionDownButton().addActionListener(al);
        getPrecisionUpButton().addActionListener(al);
        getScaleUpButton().addActionListener(al);
        getScaleDownButton().addActionListener(al);
    }

    /*
    public void propertyChange(PropertyChangeEvent anEvent) {
        if (anEvent.getPropertyName().equals(GeneralNumberDataType.PRECISION_PROPERTY_CHANGE)) {
            getPrecisionTextField().setText(anEvent.getNewValue().toString());
            return;
        }
        if (anEvent.getPropertyName().equals(GeneralNumberDataType.SCALE_PROPERTY_CHANGE)) {
            getScaleTextField().setText(anEvent.getNewValue().toString());
            return;
        }
        firePropertyChange(anEvent.getPropertyName(), anEvent.getOldValue(), anEvent.getNewValue());
    }
    */
    public void updateFields(ComponentEvent e) {
        int i = 1;
        Integer in;

        if (e.getSource() == ivjPrecisionTextField)
            try {
                i = new Integer(getPrecisionTextField().getText()).intValue();
                if (getGeneralNumberDataType().evaluatePrecision(i)) {
                    ivjGeneralNumberDataType = (GeneralNumberDataType) getGeneralNumberDataType().clone();
                    getGeneralNumberDataType().setPrecision(i);
                    ((UserTypesEditorPanel) getDataTypeEditor()).getEditor().setActualType(ivjGeneralNumberDataType);
                } else {
                    in = new Integer(getGeneralNumberDataType().getPrecision());
                    getPrecisionTextField().setText(in.toString());
                }
            }
            catch (Exception ex) {
                in = new Integer(getGeneralNumberDataType().getPrecision());
                getPrecisionTextField().setText(in.toString());
            }
        if (e.getSource() == ivjScaleTextField)
            try {
                i = new Integer(getScaleTextField().getText()).intValue();
                if (getGeneralNumberDataType().evaluateScale(i)) {
                    ivjGeneralNumberDataType = (GeneralNumberDataType) getGeneralNumberDataType().clone();
                    getGeneralNumberDataType().setScale(i);
                    ((UserTypesEditorPanel) getDataTypeEditor()).getEditor().setActualType(ivjGeneralNumberDataType);
                } else {
                    in = new Integer(getGeneralNumberDataType().getScale());
                    getScaleTextField().setText(in.toString());
                }
            }
            catch (Exception ex) {
                in = new Integer(getGeneralNumberDataType().getScale());
                getScaleTextField().setText(in.toString());
            }
    }
}