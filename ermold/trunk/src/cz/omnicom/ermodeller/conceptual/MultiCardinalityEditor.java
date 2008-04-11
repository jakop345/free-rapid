package cz.omnicom.ermodeller.conceptual;

import java.beans.PropertyEditorSupport;

/**
 * This is property editor for the cz.omnicom.ermodeller.Boolean.
 * Boolean value true is represented by String "Unary", value false by "N-ary".
 */
public class MultiCardinalityEditor extends PropertyEditorSupport {
    /**
     * Tags showed in the properties window.
     */
    private static final String[] tags = {"N-ary", "Unary"};

    /**
     * MultiCardinalityEditor constructor.
     */
    public MultiCardinalityEditor() {
        super();
    }

    /**
     * MultiCardinalityEditor constructor.
     *
     * @param source java.lang.Object
     */
    public MultiCardinalityEditor(Object source) {
        super(source);
    }

    /**
     * @return The property value of Boolean as a string suitable for presentation
     *         to a human to edit. True implies returning of "N-ary", false "Unary".
     *         <p>   Returns "null" is the value can't be expressed as a string.
     *         <p>   If a non-null value is returned, then the PropertyEditor should
     *         be prepared to parse that string back in setAsText().
     */
    public String getAsText() {
        Object v = getValue();
        if (v instanceof Boolean) {
            if ((Boolean) v)
                return tags[0];
            else
                return tags[1];
        }
        return "" + v;
    }

    /**
     * Gets possible tags (it uses for example property editor of a builder tool).
     *
     * @return java.lang.String[]
     */
    public String[] getTags() {
        return tags;
    }

    /**
     * Set the MultiCardinality property value by parsing a given String.  May raise
     * java.lang.IllegalArgumentException if either the String is
     * badly formatted or if the passed value is different from tags values.
     *
     * @param text The string to be parsed.
     * @see tags
     */
    public void setAsText(String text) throws IllegalArgumentException {
        Object v = getValue();
        if (v instanceof Boolean) {
            if (text.equals(tags[0])) {
                if (!((Boolean) v)) {
                    setValue(Boolean.TRUE);
                }

            } else {
                if (text.equals(tags[1])) {
                    if ((Boolean) v) {
                        setValue(Boolean.FALSE);
                    }
                } else
                    throw new IllegalArgumentException(text);
            }

            return;
        }
        throw new IllegalArgumentException(text);
    }
}
