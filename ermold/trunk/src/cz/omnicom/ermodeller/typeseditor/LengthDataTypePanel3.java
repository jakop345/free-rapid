package cz.omnicom.ermodeller.typeseditor;

import cz.omnicom.ermodeller.datatype.LengthDataType;
import cz.omnicom.ermodeller.datatype.editor.LengthDataTypePanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * Panel to customize <code>LengthDataType</code> in defining user types
 */
public class LengthDataTypePanel3 extends LengthDataTypePanel {

    public static final String LENGTH_TYPE_CHANGED = "length_type_changed";
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
            ivjLengthDataType = (LengthDataType) getLengthDataType().clone();

            if (ae.getSource() == getUpButton())
                connEtoM1(ae);
            if (ae.getSource() == getdownButton())
                connEtoM2(ae);
            getPropertyChange().firePropertyChange(LENGTH_TYPE_CHANGED, null, ivjLengthDataType);
            //((UserTypesEditorPanel)getDataTypeEditor()).getEditor().setActualType(ivjLengthDataType);
        }
    };

    public LengthDataTypePanel3() {
        super();
        initialize();
    }

    /**
     * Initialize the class.
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    protected void initialize() {
        try {
            setName("CharDataTypePanel3");
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
                if (ivjLengthDataType.getLength() != i) {
                    getLengthDataType().setLength(i);
                    getPropertyChange().firePropertyChange(LENGTH_TYPE_CHANGED, null, getLengthDataType());
                }
                //((UserTypesEditorPanel)getDataTypeEditor()).getEditor().setActualType(ivjLengthDataType);
            } else {
                in = getLengthDataType().getLength();
                getLengthTextField().setText(in.toString());
            }
        }
        catch (Exception ex) {
            in = getLengthDataType().getLength();
            getLengthTextField().setText(in.toString());
        }
    }
}