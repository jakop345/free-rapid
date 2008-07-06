package cz.felk.cvut.erm.sql;

import cz.felk.cvut.erm.datatype.DataType;
import cz.felk.cvut.erm.icontree.IconNode;

import javax.swing.*;

public class CreateIncompleteTypeSQL extends CreateTypeSQL {
    /**
     * Corresponding data type. In this case it is set to null, because
     * data type is not necessary
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
    public CreateIncompleteTypeSQL(String aName) {
        name = aName;
    }

    public Icon getIcon() {
        return new ImageIcon(ClassLoader.getSystemResource("img/createtype3.gif"));
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
        return "Create type " + name;
    }
}