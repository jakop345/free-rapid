package cz.omnicom.ermodeller.typeseditor;

import cz.omnicom.ermodeller.datatype.GeneralNumberDataType;
import cz.omnicom.ermodeller.datatype.editor.GeneralNumberDataTypePanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * Panel to customize <code>GeneralNumberDataType</code> in defining user types.
 * The <code>GeneralNumberDataType</code> is a part of Varray, Object or NestedTable
 */
public class GeneralNumberDataTypePanel3 extends GeneralNumberDataTypePanel {

    public static final String TYPE_CHANGED = "number_type_changed";
    PropertyChangeSupport propertyChange = null;

    public PropertyChangeSupport getPropertyChange() {
        if (propertyChange == null)
            propertyChange = new PropertyChangeSupport(this);
        return propertyChange;
    }

    public synchronized void addPropertyChangeListener(PropertyChangeListener listener) {
        getPropertyChange().addPropertyChangeListener(listener);
    }

    final ActionListener al = new ActionListener() {
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
            getPropertyChange().firePropertyChange(TYPE_CHANGED, null, getGeneralNumberDataType());
            //((UserTypesEditorPanel)getDataTypeEditor()).getEditor().setActualType(ivjGeneralNumberDataType);
        }
    };

    public GeneralNumberDataTypePanel3() {
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
        getPropertyChange().firePropertyChange(TYPE_CHANGED, null, ivjGeneralNumberDataType);
        //((UserTypesEditorPanel)getDataTypeEditor()).getEditor().setActualType(ivjGeneralNumberDataType);
    }

    /**
     * Initialize the class.
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    protected void initialize() {
        try {
            // user code begin {1}
            // user code end
            setName("GeneralNumberDataTypePanel3");
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
    protected void initConnections() {
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

    public void updateFields(ComponentEvent e) {
        int i;
        Integer in;

        if (e.getSource() == ivjPrecisionTextField)
            try {
                i = Integer.parseInt(getPrecisionTextField().getText());
                if (getGeneralNumberDataType().evaluatePrecision(i)) {
                    ivjGeneralNumberDataType = (GeneralNumberDataType) getGeneralNumberDataType().clone();
                    if (ivjGeneralNumberDataType.getPrecision() != i) {
                        getGeneralNumberDataType().setPrecision(i);
                        getPropertyChange().firePropertyChange(TYPE_CHANGED, null, getGeneralNumberDataType());
                    }
                    //((UserTypesEditorPanel)getDataTypeEditor()).getEditor().setActualType(ivjGeneralNumberDataType);
                } else {
                    in = getGeneralNumberDataType().getPrecision();
                    getPrecisionTextField().setText(in.toString());
                }
            }
            catch (Exception ex) {
                in = getGeneralNumberDataType().getPrecision();
                getPrecisionTextField().setText(in.toString());
            }
        if (e.getSource() == ivjScaleTextField)
            try {
                i = Integer.parseInt(getScaleTextField().getText());
                if (getGeneralNumberDataType().evaluateScale(i)) {
                    ivjGeneralNumberDataType = (GeneralNumberDataType) getGeneralNumberDataType().clone();
                    if (ivjGeneralNumberDataType.getScale() != i) {
                        getGeneralNumberDataType().setScale(i);
                        getPropertyChange().firePropertyChange(TYPE_CHANGED, null, getGeneralNumberDataType());
                    }
                    //((UserTypesEditorPanel)getDataTypeEditor()).getEditor().setActualType(ivjGeneralNumberDataType);
                } else {
                    in = getGeneralNumberDataType().getScale();
                    getScaleTextField().setText(in.toString());
                }
            }
            catch (Exception ex) {
                in = getGeneralNumberDataType().getScale();
                getScaleTextField().setText(in.toString());
            }
    }
}