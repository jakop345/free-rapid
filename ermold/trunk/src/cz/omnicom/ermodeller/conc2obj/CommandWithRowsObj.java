package cz.omnicom.ermodeller.conc2obj;

import cz.omnicom.ermodeller.conc2rela.RelationC2R;
import cz.omnicom.ermodeller.icontree.IconNode;

import java.util.Enumeration;
import java.util.Vector;

/**
 * Superclass of commands which include rows (columns, checks, constraints).
 */
public abstract class CommandWithRowsObj extends CommandObj {
    /**
     * All rows
     *
     * @see cz.omnicom.ermodeller.sql.RowSQL
     */
    Vector rows = new Vector();
    /**
     * Corresponding relation.
     *
     * @see cz.omnicom.ermodeller.conc2rela.RelationC2R
     */
    RelationC2R relation = null;

    /**
     * Constructor.
     *
     * @param aRelation corresponding relation
     */
    public CommandWithRowsObj(RelationC2R aRelation) {
        relation = aRelation;
    }

    /**
     * Adds column to command.
     *
     * @param aColumn cz.omnicom.ermodeller.sql.ColumnSQL
     * @see #addRowSQL
     */
    public void addColumn(ColumnObj aColumn) {
        addRowObj(aColumn);
    }

    /**
     * Adds column to command.
     *
     * @param aConstraint cz.omnicom.ermodeller.sql.Constraint
     * @see #addColumnObject
     */
    public void addColumnObject(ColumnObjectObj aColumn) {
        addRowObj(aColumn);
    }

    /**
     * Adds unique to command.
     *
     * @param aConstraint cz.omnicom.ermodeller.sql.Constraint
     * @see #addColumnUnique
     */
    public void addColumnUnique(Vector uniqueKeys) {
        addRowObj(new ColumnUniqueObj(uniqueKeys));
    }

    /**
     * Adds reference to command.
     *
     * @param aConstraint cz.omnicom.ermodeller.sql.Constraint
     * @see #addColumnReference
     */
    public void addColumnReference(ColumnReferenceObj aColumn) {
        addRowObj(aColumn);
    }

    /**
     * Adds constraint to command.
     *
     * @param aConstraint cz.omnicom.ermodeller.sql.Constraint
     * @see #addRowSQL
     */
    public void addConstraint(ConstraintObj aConstraint) {
        addRowObj(aConstraint);
    }

    /**
     * Adds check.
     *
     * @param aCheck cz.omnicom.ermodeller.sql.CheckRow
     * @see #addRowSQL
     */
    public void addCheck(CheckRowObj aCheck) {
        addRowObj(aCheck);
    }

    /**
     * Adds nested table storage clause.
     *
     * @param aCheck cz.omnicom.ermodeller.sql.CheckRow
     * @see #addRowSQL
     */
    public void addNestedTableStorageClauseObj(NestedTableStorageObj aNestedClause) {
        addRowObj(aNestedClause);
    }

    /**
     * Adds row.
     *
     * @param aRow cz.omnicom.ermodeller.sql.RowSQL
     */
    protected void addRowObj(RowObj aRow) {
        if (getRows().contains(aRow)) {
//		throw AlreadyContainsExceptionSQL();
        }
        getRows().addElement(aRow);
    }

    /**
     * Creates string representation of the command -
     * "command string {row, row, ...}".
     *
     * @param countTabs intendation from the left
     * @return java.lang.String
     * @see cz.omnicom.ermodeller.sql.RowSQL#createSubSQL
     */
    public String createSubSQL(int countTabs) {
        String tail = "";
        String result = TabCreatorObj.getTabs(countTabs) + toString() + " (\n";
        for (Enumeration elements = getRows().elements(); elements.hasMoreElements();) {
            RowObj commandSQL = (RowObj) elements.nextElement();
            if (commandSQL instanceof NestedTableStorageObj)
                tail += ((tail.equals("")) ? "\n" : ",\n") + commandSQL.createSubSQL(countTabs);
            else
                result += commandSQL.createSubSQL(countTabs + 1) + (elements.hasMoreElements() ? "," : "") + "\n";
        }
        result += TabCreatorObj.getTabs(countTabs) + ")" + tail;
        return result;
    }

    /**
     * Creates subtree from nodes.
     *
     * @return cz.omnicom.ermodeller.errorlog.icontree.IconNode
     */
    public IconNode createSubTree() {
        IconNode top = new IconNode(this, true, getIcon());
        for (Enumeration elements = getRows().elements(); elements.hasMoreElements();) {
            RowObj commandSQL = (RowObj) elements.nextElement();
            if (!(commandSQL instanceof NestedTableStorageObj))
                top.add(commandSQL.createSubTree());
        }
        for (Enumeration elements = getRows().elements(); elements.hasMoreElements();) {
            RowObj commandSQL = (RowObj) elements.nextElement();
            if (commandSQL instanceof NestedTableStorageObj)
                top.add(commandSQL.createSubTree());
        }
        return top;
    }

    /**
     * Returns rows of the command.
     *
     * @return java.util.Vector
     */
    protected Vector getRows() {
        if (rows == null)
            rows = new Vector();
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
}