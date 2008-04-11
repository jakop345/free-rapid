package cz.omnicom.ermodeller.conc2obj;

import cz.omnicom.ermodeller.icontree.IconNode;

import javax.swing.*;
import java.util.Enumeration;
import java.util.Vector;

/**
 * Part of check - AND condition of groups.
 */
public class GlueControlPartOfCheckObj extends PartOfCheckObj {
    /**
     * Groups of controlled atributes.
     *
     * @see cz.omnicom.ermodeller.sql.AtributeGroupVector
     */
    private final Vector groups = new Vector();
    /**
     * Nested OR checks
     *
     * @see cz.omnicom.ermodeller.sql.ORCheck
     */
    private final Vector subORChecks = new Vector();

    /**
     * Adds group of atributes.
     *
     * @param aAtributes java.util.Vector
     */
    public void addGroupAtributes(AtributeGroupVectorObj aAtributes) {
        if (getGroups().contains(aAtributes)) {
            // throw
        }
        getGroups().addElement(aAtributes);
    }

    /**
     * Adds nested OR check.
     *
     * @param aAtributes java.util.Vector
     */
    public void addSubORChecks(ORCheckObj aORCheck) {
        if (getSubORChecks().contains(aORCheck)) {
            // throw
        }
        getSubORChecks().addElement(aORCheck);
    }

    /**
     * Creates string representation of the part of check -
     * "group AND group AND ... AND ORCheck AND ORCheck AND ...".
     *
     * @param countTabs intendation from the left
     * @return java.lang.String
     * @see cz.omnicom.ermodeller.sql.AtributeGroupVector#createSubSQL
     * @see cz.omnicom.ermodeller.sql.ORCheck#createSubSQL
     */
    public String createSubSQL(int countTabs) {
        int count = getGroups().size() + getSubORChecks().size();
        String result = (count < 2) ? "" : TabCreatorObj.getTabs(countTabs) + "(\n";
        for (Enumeration elements = getGroups().elements(); elements.hasMoreElements();) {
            AtributeGroupVectorObj group = (AtributeGroupVectorObj) elements.nextElement();
            result += group.createSubSQL(countTabs + 1);
            if (elements.hasMoreElements())
                result += "\n" + TabCreatorObj.getTabs(countTabs + 1) + "AND\n";
        }
        if (!getGroups().isEmpty() && !getSubORChecks().isEmpty())
            result += "\n" + TabCreatorObj.getTabs(countTabs + 1) + "AND\n";
        for (Enumeration elements = getSubORChecks().elements(); elements.hasMoreElements();) {
            ORCheckObj subCheck = (ORCheckObj) elements.nextElement();
            result += TabCreatorObj.getTabs(countTabs + 1) + "(\n";
            result += subCheck.createSubSQL(countTabs + 2);
            result += "\n" + TabCreatorObj.getTabs(countTabs + 1) + ")";
            if (elements.hasMoreElements())
                result += "\n" + TabCreatorObj.getTabs(countTabs + 1) + "AND\n";
        }
        return result + ((count < 2) ? "" : "\n" + TabCreatorObj.getTabs(countTabs) + ")");
    }

    /**
     * Creates subtree from nodes.
     *
     * @return cz.omnicom.ermodeller.errorlog.icontree.IconNode
     */
    public IconNode createSubTree() {
        IconNode top = new IconNode(this, true, getIcon());
        for (Enumeration elements = getGroups().elements(); elements.hasMoreElements();) {
            AtributeGroupVectorObj group = (AtributeGroupVectorObj) elements.nextElement();
            top.add(group.createSubTree());
        }
        for (Enumeration elements = getSubORChecks().elements(); elements.hasMoreElements();) {
            ORCheckObj subCheck = (ORCheckObj) elements.nextElement();
            top.add(subCheck.createSubTree());
        }
        return top;
    }

    /**
     * Returns all groups.
     *
     * @return java.util.Vector
     */
    public Vector getGroups() {
        return groups;
    }

    /**
     * Returns icon for representing the OR check in the SQL tree.
     *
     * @return javax.swing.Icon
     */
    public Icon getIcon() {
        return new ImageIcon(ClassLoader.getSystemResource("img/andcheck.gif"));
    }

    /**
     * Returns all nested OR checks.
     *
     * @return java.util.Vector
     */
    public Vector getSubORChecks() {
        return subORChecks;
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
