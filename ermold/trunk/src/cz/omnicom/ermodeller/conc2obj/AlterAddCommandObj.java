package cz.omnicom.ermodeller.conc2obj;

import cz.omnicom.ermodeller.conc2rela.AtributeC2R;
import cz.omnicom.ermodeller.conc2rela.RelationC2R;
import cz.omnicom.ermodeller.icontree.IconNode;

import javax.swing.*;

/**
 * Commands representing alter table add command.
 */
public class AlterAddCommandObj extends CommandObj {

    private AtributeC2R atribute = null;

    private RelationC2R relation = null;

    /**
     * AlterCommand constructor.
     *
     * @param aRelation corresponding relation
     */
    public AlterAddCommandObj(RelationC2R aRelation, AtributeC2R a) {
        relation = aRelation;
        atribute = a;
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
        return "ALTER TABLE " + relation.getNameC2R() + "_obj ADD FOREIGN KEY (" + atribute.getNameC2R() + ") REFERENCES " + atribute.getConceptualAtribute().getConstruct().getName() + "_obj";
    }

    public String createSubSQL(int countTabs) {
        return TabCreatorObj.getTabs(countTabs) + toString();
    }

    public IconNode createSubTree() {
        return new IconNode(this, false, getIcon());
    }
}
