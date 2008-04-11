package cz.omnicom.ermodeller.sql;

import cz.omnicom.ermodeller.icontree.IconNode;

import javax.swing.*;

/**
 * Commands representing drop table command.
 */
public class DropTypeSQL extends CommandSQL {
    /**
     * name of the type
     */
    private String name = null;

    /**
     * Constructor.
     *
     * @param aRelation corresponding relation
     */
    public DropTypeSQL(String aName) {
        name = aName;
    }

    /**
     * Creates string representation of the command -
     * "drop TAB with constraints".
     *
     * @param countTabs intendation from the left
     * @return java.lang.String
     */
    public String createSubSQL(int countTabs) {
        return TabCreator.getTabs(countTabs) + toString();
    }

    /**
     * Creates subtree from nodes.
     *
     * @return cz.omnicom.ermodeller.errorlog.icontree.IconNode
     */
    public IconNode createSubTree() {
        return new IconNode(this, false, getIcon());
    }

    /**
     * Returns icon for representing the drop command in the SQL tree.
     *
     * @return javax.swing.Icon
     */
    public Icon getIcon() {
        return new ImageIcon(ClassLoader.getSystemResource("img/droptype2.gif"));
    }

    /**
     * Returns string representation of group.
     * "drop table TAB cascade constraints".
     *
     * @return java.lang.String
     */
    public String toString() {
        return "Drop type " + name + " FORCE";
}
}