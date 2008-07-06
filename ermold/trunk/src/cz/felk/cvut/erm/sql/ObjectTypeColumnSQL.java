package cz.felk.cvut.erm.sql;

import cz.felk.cvut.erm.icontree.IconNode;
import cz.felk.cvut.erm.typeseditor.UserTypeStorage;

import javax.swing.*;

/**
 * Column in the table - atribute.
 */
public class ObjectTypeColumnSQL extends RowSQL {

    private UserTypeStorage typeStorage = null;

    /**
     * Constructor.
     *
     * @param cz.omnicom.ermodeller.conc2rela.AtributeC2R
     *
     */
    public ObjectTypeColumnSQL(UserTypeStorage u) {
        typeStorage = u;
    }

    /**
     * Creates string representation column (atribute)
     *
     * @param countTabs intendation from the left
     * @return java.lang.String
     * @see cz.felk.cvut.erm.conc2rela.AtributeC2R#toString
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
     * Returns icon for representing the column in the SQL tree.
     *
     * @return javax.swing.Icon
     */
    public Icon getIcon() {
        return new ImageIcon(ClassLoader.getSystemResource("img/objectitem.gif"));
    }

    /**
     * Returns only name of the column, not definition.
     *
     * @return java.lang.String
     */
    public String getNameString() {
        return typeStorage.getTypeName();
    }

    /**
     * Returns string representation of the atribute.
     *
     * @return java.lang.String
     * @see #createSubSQL
     */
    public String toString() {
        return typeStorage.getTypeName() + " " + typeStorage.getDataType();
    }
}