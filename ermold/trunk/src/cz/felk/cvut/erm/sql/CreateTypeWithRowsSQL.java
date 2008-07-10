package cz.felk.cvut.erm.sql;

import cz.felk.cvut.erm.datatype.ObjectDataType;
import cz.felk.cvut.erm.icontree.IconNode;
import cz.felk.cvut.erm.typeseditor.UserTypeStorage;

import java.util.Enumeration;
import java.util.Vector;

public class CreateTypeWithRowsSQL extends CreateTypeSQL {
    /**
     * Corresponding data type.
     */
    private ObjectDataType objectDataType = null;
    /**
     * name of the type
     */
    private String name = null;

    private Vector<RowSQL> rows = null;

    /**
     * Constructor.
     *
     * @param aRelation corresponding relation
     */
    public CreateTypeWithRowsSQL(ObjectDataType aType, String aName) {
        objectDataType = aType;
        name = aName;
        fillRows();
    }

    private void fillRows() {
        for (UserTypeStorage column : objectDataType.getItemVector().getUserTypeStorageVector()) {
            addColumn(new ObjectTypeColumnSQL(column));
        }
    }

    /**
     * Creates string representation of the command -
     * "drop TAB with constraints".
     *
     * @param countTabs intendation from the left
     * @return java.lang.String
     */
    public String createSubSQL(int countTabs) {
        String result = TabCreator.getTabs(countTabs) + toString() + " (\n";
        for (Enumeration<RowSQL> elements = getRows().elements(); elements.hasMoreElements();) {
            RowSQL commandSQL = elements.nextElement();
            result += commandSQL.createSubSQL(countTabs + 1) + (elements.hasMoreElements() ? "," : "") + "\n";
        }
        return result + TabCreator.getTabs(countTabs) + ")";
    }

    /**
     * Creates subtree from nodes.
     *
     * @return cz.omnicom.ermodeller.errorlog.icontree.IconNode
     */
    public IconNode createSubTree() {
        IconNode top = new IconNode(this, true, getIcon());
        for (Enumeration<RowSQL> elements = getRows().elements(); elements.hasMoreElements();) {
            RowSQL commandSQL = elements.nextElement();
            top.add(commandSQL.createSubTree());
        }
        return top;
    }

    /**
     * Returns rows of the command.
     *
     * @return java.util.Vector
     */
    protected Vector<RowSQL> getRows() {
        if (rows == null)
            rows = new Vector<RowSQL>();
        return rows;
    }

    /**
     * Returns whether command has some rows
     *
     * @return boolean
     */
    public boolean isEmpty() {
        return getRows().isEmpty();
    }

    public void addColumn(ObjectTypeColumnSQL aColumn) {
        addRowSQL(aColumn);
    }

    /**
     * Adds row.
     *
     * @param aRow cz.omnicom.ermodeller.sql.RowSQL
     */
    protected void addRowSQL(RowSQL aRow) {
        if (getRows().contains(aRow)) {
//		throw AlreadyContainsExceptionSQL();
        }
        getRows().addElement(aRow);
    }

    /**
     * Returns string representation of group.
     * "create type TYPE as ...".
     *
     * @return java.lang.String
     */
    public String toString() {
        return "CREATE TYPE " + name + " AS " + objectDataType.toString();
    }
}