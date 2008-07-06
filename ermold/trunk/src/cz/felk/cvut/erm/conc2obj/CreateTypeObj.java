package cz.felk.cvut.erm.conc2obj;

import cz.felk.cvut.erm.datatype.DataType;
import cz.felk.cvut.erm.icontree.IconNode;

import javax.swing.*;

/**
 * Commands representing drop table command.
 */
abstract class CreateTypeObj extends CommandObj {
    /**
     * Corresponding data type.
     */
    DataType dataType = null;
    /**
     * name of the type
     */
    String name = null;

    /**
     * Creates string representation of the command -
     * "drop TAB with constraints".
     *
     * @param countTabs intendation from the left
     * @return java.lang.String
     */
    public abstract String createSubSQL(int countTabs);

    /**
     * Creates subtree from nodes.
     *
     * @return cz.omnicom.ermodeller.errorlog.icontree.IconNode
     */
    public abstract IconNode createSubTree();

    /**
     * Returns icon for representing the drop command in the SQL tree.
     *
     * @return javax.swing.Icon
     */
    public Icon getIcon() {
        return new ImageIcon(ClassLoader.getSystemResource("img/createtype2.gif"));
    }

    /**
     * Returns string representation of group.
     * "create type TYPE as ...".
     *
     * @return java.lang.String
     */
    public abstract String toString();
}