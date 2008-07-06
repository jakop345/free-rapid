package cz.felk.cvut.erm.sql;

import cz.felk.cvut.erm.conc2rela.UniqueKeyC2R;
import cz.felk.cvut.erm.icontree.IconNode;

import javax.swing.*;

/**
 * SQL unique key.
 */
public class UniqueKeySQL extends ConstraintSQL {
    /**
     * Relational unique key
     *
     * @see cz.felk.cvut.erm.conc2rela.UniqueKeyC2R
     */
    private UniqueKeyC2R uniqueKeyC2R = null;

    /**
     * Constructor.
     *
     * @param aUniqueKeyC2R cz.omnicom.ermodeller.conc2rela.UniqueKeyC2R
     */
    public UniqueKeySQL(UniqueKeyC2R aUniqueKeyC2R) {
        uniqueKeyC2R = aUniqueKeyC2R;
    }

    /**
     * Creates string representation of the unique key -
     * "constraint xxx unique key(atr1, atr2, ...)".
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
     * Returns icon for representing the unique key in the SQL tree.
     *
     * @return javax.swing.Icon
     */
    public Icon getIcon() {
        return new ImageIcon(ClassLoader.getSystemResource("img/unq.gif"));
    }

    /**
     * Returns string representation of the unique key.
     * "constraint xxx unique key(atr1, atr2, ...)".
     *
     * @return java.lang.String
     * @see #createSubSQL
     */
    public String toString() {
        if (uniqueKeyC2R.getNameC2R() == null)
            return uniqueKeyC2R.toString();
        else
            return "Constraint " + uniqueKeyC2R.getNameC2R() + " " + uniqueKeyC2R.toString();
    }
}
