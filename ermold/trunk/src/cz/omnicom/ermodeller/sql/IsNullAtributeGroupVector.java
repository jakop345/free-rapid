package cz.omnicom.ermodeller.sql;

import javax.swing.*;

/**
 * Groups of columns, which should be forced to be NULL.
 *
 * @see cz.omnicom.ermodeller.sql.ColumnSQL
 */
public class IsNullAtributeGroupVector extends AtributeGroupVector {
    /**
     * Returns icon for representing the group in the SQL tree.
     *
     * @return javax.swing.Icon
     */
    public Icon getIcon() {
        return new ImageIcon(ClassLoader.getSystemResource("img/andcheck.gif"));
    }

    /**
     * Returns <code>false</code>.
     * Returns whether check is provided like:
     * "atr IS NOT NULL".
     */
    public boolean getIsNotNull() {
        return false;
    }

    /**
     * Returns string representation of group.
     * "Is Null group".
     *
     * @return java.lang.String
     */
    public String toString() {
        return "Is Null group";
    }
}
