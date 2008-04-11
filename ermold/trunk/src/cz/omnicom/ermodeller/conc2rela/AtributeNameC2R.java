package cz.omnicom.ermodeller.conc2rela;

import java.util.Enumeration;
import java.util.Vector;

/**
 * Name for atributes
 * <p/>
 * prefix - from role - for foreign keys atributes,
 * subnumbers - parallel cardinalities,
 * name.
 */
public class AtributeNameC2R extends NameC2R {
    private Vector prefixes = new Vector();
    private Vector subNumbers = new Vector();

    /**
     * AtributeNameC2R constructor.
     *
     * @param aNameC2R cz.omnicom.ermodeller.conc2rela.NameC2R
     */
    public AtributeNameC2R(AtributeNameC2R anAtributeNameC2R) {
        super(anAtributeNameC2R);
        this.prefixes = (Vector) anAtributeNameC2R.getPrefixes().clone();
        this.subNumbers = (Vector) anAtributeNameC2R.getSubNumbers().clone();
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
        for (Enumeration elements = anAtributeNameC2R.getPrefixes().elements(); elements.hasMoreElements();) {
            this.addPrefix((String) elements.nextElement());
        }
    }

    /**
     * Adds all subnumbers from <code>anAtributeNameC2R</code>.
     *
     * @param aNameC2R cz.omnicom.ermodeller.conc2rela.NameC2R
     */
    protected void addAllSubNumbers(AtributeNameC2R anAtributeNameC2R) {
        for (Enumeration elements = anAtributeNameC2R.getSubNumbers().elements(); elements.hasMoreElements();) {
            this.addSubNumber(((Integer) elements.nextElement()).intValue());
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
        getSubNumbers().addElement(new Integer(aSubNumber));
    }

    /**
     * @return Vector
     */
    public Vector getPrefixes() {
        return prefixes;
    }

    /**
     * @return java.util.Vector
     */
    public Vector getSubNumbers() {
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
        for (Enumeration elements = getSubNumbers().elements(); elements.hasMoreElements();) {
            subNumbers += elements.nextElement().toString();
/*		if (elements.hasMoreElements())
			subNumbers += "_";*/
        }
        for (Enumeration elements = getPrefixes().elements(); elements.hasMoreElements();) {
            strPrefixes += elements.nextElement().toString();
            if (elements.hasMoreElements())
                strPrefixes += "_";
        }
        return ((getPrefixes().isEmpty()) ? "" : strPrefixes) + ((getSubNumbers().isEmpty()) ? "" : ((getPrefixes().isEmpty()) ? "" : "_") + subNumbers) + ((getSubNumbers().isEmpty() && getPrefixes().isEmpty()) ? "" : "_") + getName();
    }
}
