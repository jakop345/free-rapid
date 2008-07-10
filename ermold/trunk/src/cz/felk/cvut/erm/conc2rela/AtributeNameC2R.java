package cz.felk.cvut.erm.conc2rela;

import java.util.Enumeration;
import java.util.Vector;

/**
 * Name for atributes
 * <p/>
 * prefix - from role - for foreign keys atributes,
 * subnumbers - parallel cardinalities,
 * name.
 */
class AtributeNameC2R extends NameC2R {
    private Vector<String> prefixes = new Vector<String>();
    private Vector<Integer> subNumbers = new Vector<Integer>();

    /**
     * AtributeNameC2R constructor.
     *
     * @param aNameC2R cz.omnicom.ermodeller.conc2rela.NameC2R
     */
    public AtributeNameC2R(AtributeNameC2R anAtributeNameC2R) {
        super(anAtributeNameC2R);
        this.prefixes = (Vector<String>) anAtributeNameC2R.getPrefixes().clone();
        this.subNumbers = (Vector<Integer>) anAtributeNameC2R.getSubNumbers().clone();
    }

    /**
     * AtributeNameC2R constructor.
     *
     * @param aPrefix java.lang.String
     * @param aName   java.lang.String
     */
    public AtributeNameC2R(String aPrefix, String aName) {
        super(aName);
        addPrefix(aPrefix);
    }

    /**
     * Adds all prefixes from <code>anAtributeNameC2R</code>.
     *
     * @param aNameC2R cz.omnicom.ermodeller.conc2rela.NameC2R
     */
    protected void addAllPrefixes(AtributeNameC2R anAtributeNameC2R) {
        for (Enumeration<String> elements = anAtributeNameC2R.getPrefixes().elements(); elements.hasMoreElements();) {
            this.addPrefix(elements.nextElement());
        }
    }

    /**
     * Adds all subnumbers from <code>anAtributeNameC2R</code>.
     *
     * @param aNameC2R cz.omnicom.ermodeller.conc2rela.NameC2R
     */
    protected void addAllSubNumbers(AtributeNameC2R anAtributeNameC2R) {
        for (Enumeration<Integer> elements = anAtributeNameC2R.getSubNumbers().elements(); elements.hasMoreElements();) {
            this.addSubNumber(elements.nextElement());
        }
    }

    /**
     * Adds prefix.
     *
     * @return String
     */
    public void addPrefix(String aPrefix) {
        if (aPrefix != null)
            this.prefixes.addElement(aPrefix);
    }

    /**
     * Adds one subnumber.
     *
     * @param aSubNumber int
     */
    public void addSubNumber(int aSubNumber) {
        getSubNumbers().addElement(aSubNumber);
    }

    /**
     * @return Vector
     */
    public Vector<String> getPrefixes() {
        return prefixes;
    }

    /**
     * @return java.util.Vector
     */
    public Vector<Integer> getSubNumbers() {
        return subNumbers;
    }

    /**
     * Returns string representation of name.
     *
     * @return java.lang.String
     */
    public String toString() {
        String subNumbers = "";
        String strPrefixes = "";
        for (Enumeration<Integer> elements = getSubNumbers().elements(); elements.hasMoreElements();) {
            subNumbers += elements.nextElement().toString();
/*		if (elements.hasMoreElements())
			subNumbers += "_";*/
        }
        for (Enumeration<String> elements = getPrefixes().elements(); elements.hasMoreElements();) {
            strPrefixes += elements.nextElement().toString();
            if (elements.hasMoreElements())
                strPrefixes += "_";
        }
        return ((getPrefixes().isEmpty()) ? "" : strPrefixes) + ((getSubNumbers().isEmpty()) ? "" : ((getPrefixes().isEmpty()) ? "" : "_") + subNumbers) + ((getSubNumbers().isEmpty() && getPrefixes().isEmpty()) ? "" : "_") + getName();
    }
}
