package cz.felk.cvut.erm.sql;

import cz.felk.cvut.erm.conc2rela.RelationC2R;
import cz.felk.cvut.erm.icontree.IconNode;

import javax.swing.*;

/**
 * Commands representing drop table command.
 */
public class DropCommandSQL extends CommandSQL {
    /**
     * Corresponding relation.
     */
    RelationC2R relation = null;

    /**
     * Constructor.
     *
     * @param aRelation corresponding relation
     */
    public DropCommandSQL(RelationC2R aRelationC2R) {
        this.relation = aRelationC2R;
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
        return new ImageIcon(ClassLoader.getSystemResource("img/droptable.gif"));
    }

    /**
     * Returns string representation of group.
     * "drop table TAB cascade constraints".
     *
     * @return java.lang.String
     */
    public String toString() {
        return "DROP TABLE " + relation.getNameC2R() + " CASCADE CONSTRAINTS";
    }
}
