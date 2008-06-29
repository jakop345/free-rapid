package cz.omnicom.ermodeller.conc2obj;

import cz.omnicom.ermodeller.conc2obj.interfaces.SubObjProducer;
import cz.omnicom.ermodeller.conc2obj.interfaces.SubTreeProducerObj;
import cz.omnicom.ermodeller.icontree.IconNode;

import javax.swing.*;
import java.util.Enumeration;
import java.util.Vector;

/**
 * Type of check which compose parts by OR.
 */
public class ORCheckObj implements SubObjProducer, SubTreeProducerObj {
    /**
     * Parts of check.
     *
     * @see cz.omnicom.ermodeller.sql.PartOfCheck
     */
    private final Vector<PartOfCheckObj> orParts = new Vector<PartOfCheckObj>();

    /**
     * Adds new part of check.
     *
     * @param aPart cz.omnicom.ermodeller.sql.PartOfCheck
     */
    public void addORPartOfCheck(PartOfCheckObj aPart) {
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
        for (Enumeration<PartOfCheckObj> elements = getORParts().elements(); elements.hasMoreElements();) {
            result += (elements.nextElement()).createSubSQL(countTabs);
            if (elements.hasMoreElements())
                result += "\n" + TabCreatorObj.getTabs(countTabs) + "OR\n";
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
        for (Enumeration<PartOfCheckObj> elements = getORParts().elements(); elements.hasMoreElements();) {
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
    public Vector<PartOfCheckObj> getORParts() {
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
