package cz.felk.cvut.erm.conc2rela;

/**
 * Name of relational object.
 */
public class NameC2R {
    private String name = null;

    /**
     * NameC2R constructor.
     */
    public NameC2R() {
    }

    /**
     * NameC2R constructor - cretaes equal name.
     */
    public NameC2R(NameC2R aNameC2R) {
        this.name = aNameC2R.getName();
    }

    /**
     * NameC2R constructor.
     */
    public NameC2R(String aName) {
        this.name = convertToValidString(aName);
    }

    /**
     * This method was created in VisualAge.
     *
     * @param aString java.lang.String
     * @return java.lang.String
     */
    protected String convertToValidString(String aString) {
        if (aString == null)
            return null;
        return aString.replace(' ', '_').replace('\t', '_');
    }

    /**
     * @return java.lang.String
     */
    public String getName() {
        return name;
    }

    /**
     * Returns string representation of the name.
     *
     * @return java.lang.String
     */
    public String toString() {
        return (name == null) ? "" : name;
    }
}
