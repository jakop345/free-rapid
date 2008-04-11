package cz.omnicom.ermodeller.conc2obj;

import cz.omnicom.ermodeller.icontree.IconNode;
import cz.omnicom.ermodeller.typeseditor.UserTypeStorage;

import javax.swing.*;

/**
 * Column in the table - atribute.
 */
public class ObjectTypeColumnObj extends RowObj {

    private UserTypeStorage typeStorage = null;

    /**
     * Constructor.
     *
     * @param cz.omnicom.ermodeller.conc2rela.AtributeC2R
     *
     */
    public ObjectTypeColumnObj(UserTypeStorage u) {
        typeStorage = u;
    }

    /**
     * Creates string representation column (atribute)
     *
     * @param countTabs intendation from the left
     * @return java.lang.String
     * @see cz.omnicom.ermodeller.conc2rela.AtributeC2R#toString
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