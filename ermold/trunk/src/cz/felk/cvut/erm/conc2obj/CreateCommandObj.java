package cz.felk.cvut.erm.conc2obj;

import cz.felk.cvut.erm.conc2rela.RelationC2R;

import javax.swing.*;

/**
 * Commands representing create table command.
 */
public class CreateCommandObj extends CommandWithRowsObj {
    /**
     * CreateCommand constructor.
     *
     * @param aRelation corresponding relation
     */
    public CreateCommandObj(RelationC2R aRelation) {
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
        return "CREATE TABLE " + relation.getNameC2R() + "_obj OF " + relation.getNameC2R() + "_t";
    }
}
