package cz.omnicom.ermodeller.conc2obj;

import cz.omnicom.ermodeller.icontree.IconNode;

import javax.swing.*;

/**
 * Commands representing drop table command.
 */
public class Intro extends CommandObj {

/**
 * Constructor.
 *
 * @param aRelation corresponding relation
 */
    /**
     * Creates string representation of the command -
     * "drop TAB with constraints".
     *
     * @param countTabs intendation from the left
     * @return java.lang.String
     */
    public String createSubSQL(int countTabs) {
        return TabCreatorObj.getTabs(countTabs) + toString();
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
        return new ImageIcon(ClassLoader.getSystemResource("img/intro.gif"));
    }

    /**
     * Returns string representation of group.
     * "drop table TAB cascade constraints".
     *
     * @return java.lang.String
     */
    public String toString() {
        return "Set Echo on\nPurge Recyclebin;\n/* používáte-li verzi nižší než Oracle 10g,\n zakomentujte pøíkaz PURGE */\n";
    }
}
