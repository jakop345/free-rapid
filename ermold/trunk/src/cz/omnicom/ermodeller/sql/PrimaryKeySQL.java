package cz.omnicom.ermodeller.sql;

import cz.omnicom.ermodeller.conc2rela.PrimaryKeyC2R;
import cz.omnicom.ermodeller.icontree.IconNode;

import javax.swing.*;

/**
 * SQL primary key.
 */
public class PrimaryKeySQL extends ConstraintSQL {
    /**
     * Corresponding primary key in relational schema.
     *
     * @see cz.omnicom.ermodeller.conc2rela.PrimaryKeyC2R
     */
    private PrimaryKeyC2R primaryKeyC2R = null;

    /**
     * Constructor.
     *
     * @param aPrimaryKeyC2R cz.omnicom.ermodeller.conc2rela.PrimaryKeyC2R
     */
    public PrimaryKeySQL(PrimaryKeyC2R aPrimaryKeyC2R) {
        primaryKeyC2R = aPrimaryKeyC2R;
    }

    /**
     * Creates string representation of the unique key -
     * "constraint xxx primary key(atr1, atr2, ...)".
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
     * Returns icon for representing the primary key in the SQL tree.
     *
     * @return javax.swing.Icon
     */
    public Icon getIcon() {
        return new ImageIcon(ClassLoader.getSystemResource("img/pk.gif"));
    }

    /**
     * Returns string representation of the unique key.
     * "constraint xxx primary key(atr1, atr2, ...)".
     *
     * @return java.lang.String
     * @see #createSubSQL
     */
    public String toString() {
        if (primaryKeyC2R.getNameC2R() == null)
            return primaryKeyC2R.toString();
        else
            return "Constraint " + primaryKeyC2R.getNameC2R() + " " + primaryKeyC2R.toString();
}
}
