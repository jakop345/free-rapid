package cz.omnicom.ermodeller.sql;

import cz.omnicom.ermodeller.conc2rela.RelationC2R;

import javax.swing.*;

/**
 * Commands representing alter table add command.
 */
public class AlterAddCommandSQL extends CommandWithRowsSQL {
    /**
     * AlterCommand constructor.
     *
     * @param aRelation corresponding relation
     */
    public AlterAddCommandSQL(RelationC2R aRelation) {
        super(aRelation);
    }

    /**
     * Returns icon for representing the command in the SQL tree.
     *
     * @return javax.swing.Icon
     */
    public Icon getIcon() {
        return new ImageIcon(ClassLoader.getSystemResource("img/altertable.gif"));
    }

    /**
     * Returns string representation of the command.
     * "Alter table TAB add".
     *
     * @return java.lang.String
     */
    public String toString() {
        return "Alter table " + relation.getNameC2R() + " add";
    }
}
