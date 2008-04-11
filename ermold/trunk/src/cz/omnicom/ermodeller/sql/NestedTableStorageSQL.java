package cz.omnicom.ermodeller.sql;

import cz.omnicom.ermodeller.icontree.IconNode;

import javax.swing.*;

/**
 * Nested table storage clause
 */
public class NestedTableStorageSQL extends RowSQL {
    /**
     * Atribute describing the column of NestedTable data type
     *
     * @see cz.omnicom.ermodeller.conc2rela.AtributeC2R
     */
    private String atribute = null;
    private String entityName;
    /**
     * tells whether nested table storage clause is being created from
     * atribute of nested table data type or from atribute of object data type
     * which contains some item of nested table data type
     */
    private boolean fromObject = false;

    /**
     * Constructor.
     *
     * @param cz.omnicom.ermodeller.conc2rela.AtributeC2R
     *
     */
    public NestedTableStorageSQL(String anAtribute, String entName) {
        atribute = anAtribute;
        entityName = entName;
    }

    /**
     * Creates string representation column (atribute)
     *
     * @param countTabs intendation from the left
     * @return java.lang.String
     * @see cz.omnicom.ermodeller.conc2rela.AtributeC2R#toString
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
        return new ImageIcon(ClassLoader.getSystemResource("img/nested.gif"));
    }

    /**
     * Returns only name of the column, not definition.
     *
     * @return java.lang.String
     */
    public String getNameString() {
        return atribute;
    }

    /**
     * Returns string representation of the atribute.
     *
     * @return java.lang.String
     * @see #createSubSQL
     */
    public String toString() {
        return "nested table " + getNameString() + " store as " + entityName + "_" + getNameString();
}
}