package cz.felk.cvut.erm.sql;

import cz.felk.cvut.erm.icontree.IconNode;

import javax.swing.*;
import java.util.Enumeration;
import java.util.Vector;

/**
 * Part of check - AND condition of groups.
 */
public class GlueControlPartOfCheck extends PartOfCheck {
    /**
     * Groups of controlled atributes.
     *
     * @see cz.felk.cvut.erm.sql.AtributeGroupVector
     */
    private final Vector<AtributeGroupVector> groups = new Vector<AtributeGroupVector>();
    /**
     * Nested OR checks
     *
     * @see cz.felk.cvut.erm.sql.ORCheck
     */
    private final Vector<ORCheck> subORChecks = new Vector<ORCheck>();

    /**
     * Adds group of atributes.
     *
     * @param aAtributes java.util.Vector
     */
    public void addGroupAtributes(AtributeGroupVector aAtributes) {
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
    public void addSubORChecks(ORCheck aORCheck) {
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
     * @see cz.felk.cvut.erm.sql.AtributeGroupVector#createSubSQL
     * @see cz.felk.cvut.erm.sql.ORCheck#createSubSQL
     */
    public String createSubSQL(int countTabs) {
        int count = getGroups().size() + getSubORChecks().size();
        String result = (count < 2) ? "" : TabCreator.getTabs(countTabs) + "(\n";
        for (Enumeration<AtributeGroupVector> elements = getGroups().elements(); elements.hasMoreElements();) {
            AtributeGroupVector group = elements.nextElement();
            result += group.createSubSQL(countTabs + 1);
            if (elements.hasMoreElements())
                result += "\n" + TabCreator.getTabs(countTabs + 1) + "AND\n";
        }
        if (!getGroups().isEmpty() && !getSubORChecks().isEmpty())
            result += "\n" + TabCreator.getTabs(countTabs + 1) + "AND\n";
        for (Enumeration<ORCheck> elements = getSubORChecks().elements(); elements.hasMoreElements();) {
            ORCheck subCheck = elements.nextElement();
            result += TabCreator.getTabs(countTabs + 1) + "(\n";
            result += subCheck.createSubSQL(countTabs + 2);
            result += "\n" + TabCreator.getTabs(countTabs + 1) + ")";
            if (elements.hasMoreElements())
                result += "\n" + TabCreator.getTabs(countTabs + 1) + "AND\n";
        }
        return result + ((count < 2) ? "" : "\n" + TabCreator.getTabs(countTabs) + ")");
    }

    /**
     * Creates subtree from nodes.
     *
     * @return cz.omnicom.ermodeller.errorlog.icontree.IconNode
     */
    public IconNode createSubTree() {
        IconNode top = new IconNode(this, true, getIcon());
        for (Enumeration<AtributeGroupVector> elements = getGroups().elements(); elements.hasMoreElements();) {
            AtributeGroupVector group = elements.nextElement();
            top.add(group.createSubTree());
        }
        for (Enumeration<ORCheck> elements = getSubORChecks().elements(); elements.hasMoreElements();) {
            ORCheck subCheck = elements.nextElement();
            top.add(subCheck.createSubTree());
        }
        return top;
    }

    /**
     * Returns all groups.
     *
     * @return java.util.Vector
     */
    public Vector<AtributeGroupVector> getGroups() {
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
    public Vector<ORCheck> getSubORChecks() {
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
