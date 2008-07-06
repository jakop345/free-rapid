package cz.felk.cvut.erm.sql;

import cz.felk.cvut.erm.datatype.DataType;
import cz.felk.cvut.erm.icontree.IconNode;

public class CreateTypeWithoutRowsSQL extends CreateTypeSQL {
    /**
     * Corresponding data type.
     */
    DataType dataType = null;
    /**
     * name of the type
     */
    String name = null;

    /**
     * Constructor.
     *
     * @param aRelation corresponding relation
     */
    public CreateTypeWithoutRowsSQL(DataType aType, String aName) {
        dataType = aType;
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
     * Returns string representation of group.
     * "create type TYPE as ...".
     *
     * @return java.lang.String
     */
    public String toString() {
        return "Create type " + name + " as " + dataType;
    }
}