package cz.omnicom.ermodeller.conceptual.editors;

import java.beans.PropertyEditorSupport;

/**
 * This is property editor for the java.lang.Boolean.
 * Boolean value <code>true</code> is represented by String "Yes",
 * value <code>false</code> by "No".
 * <p/>
 * <code>BooleanEditor</code> extends <code>PropertyEditorSupport</code>.
 * <p/>
 * #see java.beans.PropertyEditorSupport
 */
public class BooleanEditor extends PropertyEditorSupport {
    /**
     * Tags showed in the properties window.
     */
    private static final String[] tags = {"Yes", "No"};

    /**
     * BooleanEditor constructor.
     */
    public BooleanEditor() {
        super();
    }

    /**
     * BooleanEditor constructor.
     *
     * @param source java.lang.Object
     */
    public BooleanEditor(Object source) {
        super(source);
    }

    /**
     * @return The property value of <code>Boolean</code> as a string suitable
     *         for presentation to a human to edit. <code>True</code> implies returning
     *         of "Yes", <code>false</code> "No".
     *         <p>   Returns "null" if the value can't be expressed as a string.
     *         <p>   If a non-null value is returned, then the PropertyEditor should
     *         be prepared to parse that string back in <code>setAsText()</code>.
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
     * Set the <code>Boolean</code> property value by parsing a given <code>text</code>.
     * May raise <code>java.lang.IllegalArgumentException</code> if either the
     * <code>text</code> is badly formatted or if the passed value is different
     * from tags values.
     *
     * @param text The string to be parsed.
     * @see tags
     */
/*public void setAsText(String text) throws IllegalArgumentException {
	Object v = getValue();
	if (v instanceof Boolean) {
		Boolean b;
		if (text.equals(tags[0])) {
			b = Boolean.TRUE;
		}
		else {
			if (text.equals(tags[1]))
				b = Boolean.FALSE;
			else
				throw new IllegalArgumentException(text);
		}
		setValue(b);
		return;
	}
	throw new IllegalArgumentException(text);
}*/
    public void setAsText(String text) throws IllegalArgumentException {
        Object v = getValue();
        if (v instanceof Boolean) {

            if (text.equals(tags[0])) {
                if (!(Boolean) v) {
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
