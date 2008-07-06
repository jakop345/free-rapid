package cz.felk.cvut.erm.sql;

import cz.felk.cvut.erm.conc2rela.RelationC2R;
import cz.felk.cvut.erm.icontree.IconNode;

import java.util.Enumeration;
import java.util.Vector;

/**
 * Superclass of commands which include rows (columns, checks, constraints).
 */
public abstract class CommandWithRowsSQL extends CommandSQL {
    /**
     * All rows
     *
     * @see cz.felk.cvut.erm.sql.RowSQL
     */
    Vector rows = new Vector();
    /**
     * Corresponding relation.
     *
     * @see cz.felk.cvut.erm.conc2rela.RelationC2R
     */
    RelationC2R relation = null;

    /**
     * Constructor.
     *
     * @param aRelation corresponding relation
     */
    public CommandWithRowsSQL(RelationC2R aRelation) {
        relation = aRelation;
    }

    /**
     * Adds column to command.
     *
     * @param aColumn cz.omnicom.ermodeller.sql.ColumnSQL
     * @see #addRowSQL
     */
    public void addColumn(ColumnSQL aColumn) {
        addRowSQL(aColumn);
    }

    /**
     * Adds constraint to command.
     *
     * @param aConstraint cz.omnicom.ermodeller.sql.Constraint
     * @see #addRowSQL
     */
    public void addConstraint(ConstraintSQL aConstraint) {
        addRowSQL(aConstraint);
    }

    /**
     * Adds check.
     *
     * @param aCheck cz.omnicom.ermodeller.sql.CheckRow
     * @see #addRowSQL
     */
    public void addCheck(CheckRow aCheck) {
        addRowSQL(aCheck);
    }

    /**
     * Adds nested table storage clause.
     *
     * @param aCheck cz.omnicom.ermodeller.sql.CheckRow
     * @see #addRowSQL
     */
    public void addNestedTableStorageClause(NestedTableStorageSQL aNestedClause) {
        addRowSQL(aNestedClause);
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
     * Creates string representation of the command -
     * "command string {row, row, ...}".
     *
     * @param countTabs intendation from the left
     * @return java.lang.String
     * @see cz.felk.cvut.erm.sql.RowSQL#createSubSQL
     */
    public String createSubSQL(int countTabs) {
        String tail = "";
        String result = TabCreator.getTabs(countTabs) + toString() + " (\n";
        for (Enumeration elements = getRows().elements(); elements.hasMoreElements();) {
            RowSQL commandSQL = (RowSQL) elements.nextElement();
            if (commandSQL instanceof NestedTableStorageSQL)
                tail += ((tail.equals("")) ? "\n" : ",\n") + commandSQL.createSubSQL(countTabs);
            else
                result += commandSQL.createSubSQL(countTabs + 1) + (elements.hasMoreElements() ? "," : "") + "\n";
        }
        result += TabCreator.getTabs(countTabs) + ")" + tail;
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
            RowSQL commandSQL = (RowSQL) elements.nextElement();
            if (!(commandSQL instanceof NestedTableStorageSQL))
                top.add(commandSQL.createSubTree());
        }
        for (Enumeration elements = getRows().elements(); elements.hasMoreElements();) {
            RowSQL commandSQL = (RowSQL) elements.nextElement();
            if (commandSQL instanceof NestedTableStorageSQL)
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