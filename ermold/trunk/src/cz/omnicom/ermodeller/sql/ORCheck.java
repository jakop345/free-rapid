package cz.omnicom.ermodeller.sql;

import cz.omnicom.ermodeller.icontree.IconNode;
import cz.omnicom.ermodeller.sql.interfaces.SubSQLProducer;
import cz.omnicom.ermodeller.sql.interfaces.SubTreeProducer;

import javax.swing.*;
import java.util.Enumeration;
import java.util.Vector;

/**
 * Type of check which compose parts by OR.
 */
public class ORCheck implements SubSQLProducer, SubTreeProducer {
    /**
     * Parts of check.
     *
     * @see cz.omnicom.ermodeller.sql.PartOfCheck
     */
    private final Vector<PartOfCheck> orParts = new Vector<PartOfCheck>();

    /**
     * Adds new part of check.
     *
     * @param aPart cz.omnicom.ermodeller.sql.PartOfCheck
     */
    public void addORPartOfCheck(PartOfCheck aPart) {
        if (getORParts().contains(aPart)) {
            // throw AlreadyContains
        }
        getORParts().addElement(aPart);
    }

    /**
     * Creates string representation of the check -
     * "part OR part OR part OR ...".
     *
     * @param countTabs intendation from the left
     * @return java.lang.String
     * @see cz.omnicom.ermodeller.sql.PartOfCheck#createSubSQL
     */
    public String createSubSQL(int countTabs) {
        String result = "";
        for (Enumeration<PartOfCheck> elements = getORParts().elements(); elements.hasMoreElements();) {
            result += (elements.nextElement()).createSubSQL(countTabs);
            if (elements.hasMoreElements())
                result += "\n" + TabCreator.getTabs(countTabs) + "OR\n";
        }
        return result;
    }

    /**
     * Creates subtree from nodes.
     *
     * @return cz.omnicom.ermodeller.errorlog.icontree.IconNode
     */
    public IconNode createSubTree() {
        IconNode top = new IconNode(this, true, getIcon());
        for (Enumeration<PartOfCheck> elements = getORParts().elements(); elements.hasMoreElements();) {
            top.add((elements.nextElement()).createSubTree());
        }
        return top;
    }

    /**
     * Returns icon for representing the OR check in the SQL tree.
     *
     * @return javax.swing.Icon
     */
    public Icon getIcon() {
        return new ImageIcon(ClassLoader.getSystemResource("img/orcheck.gif"));
    }

    /**
     * Returns all parts of the check.
     *
     * @return java.util.Vector
     */
    public Vector<PartOfCheck> getORParts() {
        return orParts;
    }

    /**
     * Returns several spaces "        ".
     *
     * @return java.lang.String
     */
    public String toString() {
        return "          ";
    }
}
