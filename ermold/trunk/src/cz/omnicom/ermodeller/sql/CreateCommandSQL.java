package cz.omnicom.ermodeller.sql;

import cz.omnicom.ermodeller.conc2rela.RelationC2R;

import javax.swing.*;

/**
 * Commands representing create table command.
 */
public class CreateCommandSQL extends CommandWithRowsSQL {
    /**
     * CreateCommand constructor.
     *
     * @param aRelation corresponding relation
     */
    public CreateCommandSQL(RelationC2R aRelation) {
        super(aRelation);
    }

    /**
     * Returns icon for representing the command in the SQL tree.
     *
     * @return javax.swing.Icon
     */
    public Icon getIcon() {
        return new ImageIcon(ClassLoader.getSystemResource("img/createtable.gif"));
    }

    /**
     * Returns string representation of the command.
     * "Create table TAB".
     *
     * @return java.lang.String
     */
    public String toString() {
        return "Create table " + relation.getNameC2R();
    }
}
