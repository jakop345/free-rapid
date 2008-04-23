package cz.omnicom.ermodeller.datatype;

import cz.omnicom.ermodeller.datatype.editor.DataTypePanel;
import cz.omnicom.ermodeller.datatype.editor.GeneralNumberDataTypePanel;

/**
 * General number datatype with precision and scale.
 */
public class GeneralNumberDataType extends DataType {
    /**
     * Precision of the number.
     */
    private int fieldPrecision;
    /**
     * Scale of the number.
     */
    private int fieldScale;

    static final public int MINIMUM_PRECISION = 1;
    static final public int MAXIMUM_PRECISION = 38;
    static final public int DEFAULT_PRECISION = MAXIMUM_PRECISION;
    GeneralNumberDataTypePanel panel;
    static final public int MINIMUM_SCALE = -84;
    static final public int MAXIMUM_SCALE = 128;
    static final public int DEFAULT_SCALE = 0;

    public static final String PRECISION_PROPERTY_CHANGE = "precision";
    public static final String SCALE_PROPERTY_CHANGE = "scale";

    /**
     * GeneralNumberDataType constructor.
     * Sets precision to <code>DEFAULT_PRECISION</code>and scale to <code>DEFAULT_SCALE</code>.
     */
    public GeneralNumberDataType() {
        setPrecision(DEFAULT_PRECISION);
        setScale(DEFAULT_SCALE);
    }

    /**
     * Decrease precision.
     *
     * @see #setPrecision
     */
    public void decPrecision() {
        setPrecision(fieldPrecision - 1);
    }

    /**
     * Decrease scale.
     *
     * @see #setScale
     */
    public void decScale() {
        setScale(fieldScale - 1);
    }

    /**
     * Returns whether <code>obj</code> represents the same datatype as the <code>GeneralNumberDataType</code>.
     *
     * @param java.lang.Object
     * @return boolean
     */
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj instanceof GeneralNumberDataType) {
            GeneralNumberDataType dataType = (GeneralNumberDataType) obj;
            return (this.getPrecision() == dataType.getPrecision()) && (this.getScale() == dataType.getScale());
        }
        return false;
    }

    /**
     * Return whether <code>aPrecision</code> is valid value or not.
     *
     * @param aLength int
     * @return boolean
     */
    public boolean evaluatePrecision(int aPrecision) {
        return aPrecision >= MINIMUM_PRECISION && aPrecision <= MAXIMUM_PRECISION;
    }

    /**
     * Return whether <code>aScale</code> is valid value or not.
     *
     * @param aLength int
     * @return boolean
     */
    public boolean evaluateScale(int aScale) {
        return aScale >= MINIMUM_SCALE && aScale <= MAXIMUM_SCALE;
    }

    /**
     * Returns the new instance of <code>GeneralNumberDataTypePanel</code>.
     *
     * @return cz.omnicom.ermodeller.datatype.DataTypePanel
     */
    public DataTypePanel getPanel() {
        if (panel == null)
            panel = new GeneralNumberDataTypePanel();
        panel.setGeneralNumberDataType(this);
        return panel;
    }

    /**
     * Gets the precision property (int) value.
     *
     * @return The precision property value.
     * @see #setPrecision
     */
    public int getPrecision() {
        return fieldPrecision;
    }

    /**
     * Gets the scale property (int) value.
     *
     * @return The scale property value.
     * @see #setScale
     */
    public int getScale() {
        return fieldScale;
    }

    /**
     * Increase precision.
     *
     * @see #setPrecision
     */
    public void incPrecision() {
        setPrecision(fieldPrecision + 1);
    }

    /**
     * Increase scale.
     *
     * @see #setScale
     */
    public void incScale() {
        setScale(fieldScale + 1);
    }

    /**
     * Sets the precision property (int) value.
     *
     * @param precision The new value for the property.
     * @see #getPrecision
     * @see #evaluatePrecision
     */
    public void setPrecision(int precision) {
        int oldValue;
        getPrecision();
        synchronized (this) {
            if (!evaluatePrecision(precision))
                return;
            oldValue = getPrecision();
            fieldPrecision = precision;
        }
        firePropertyChange(PRECISION_PROPERTY_CHANGE, oldValue, precision);
    }

    /**
     * Sets the scale property (int) value.
     *
     * @param scale The new value for the property.
     * @see #getScale
     * @see #evaluateScale
     */
    public void setScale(int scale) {
        int oldValue;
        synchronized (this) {
            if (!evaluateScale(scale))
                return;
            oldValue = fieldScale;
            fieldScale = scale;
        }
        firePropertyChange(SCALE_PROPERTY_CHANGE, oldValue, scale);
    }

    /**
     * String representation of the datatype in property list.
     *
     * @return java.lang.String
     */
    public String toDescriptionString() {
        return "General Number (" + getPrecision() + ", " + getScale() + ")";
    }

    /**
     * String representation of the datatype.
     *
     * @return java.lang.String
     */
    public String toString() {
        return "Number(" + getPrecision() + ", " + getScale() + ")";
    }
}