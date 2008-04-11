package cz.omnicom.ermodeller.conc2obj;

import cz.omnicom.ermodeller.conc2rela.AtributeC2R;
import cz.omnicom.ermodeller.icontree.IconNode;

import javax.swing.*;

/**
 * Commands representing alter table add command.
 */
public class AlterReferenceType extends CommandObj {

    private AtributeC2R atribute = null;


    /**
     * AlterCommand constructor.
     *
     * @param aRelation corresponding relation
     */
    public AlterReferenceType(AtributeC2R a) {

        atribute = a;
    }

    /**
     * Returns icon for representing the command in the SQL tree.
     *
     * @return javax.swing.Icon
     */
    public Icon getIcon() {
        return new ImageIcon(ClassLoader.getSystemResource("img/reference.gif"));
    }

    /**
     * Returns string representation of the command.
     * "Alter table TAB add".
     *
     * @return java.lang.String
     */
    public String toString() {
        return atribute.toString(3);
    }

    public String createSubSQL(int countTabs) {
        return TabCreatorObj.getTabs(countTabs) + toString();
    }

    public IconNode createSubTree() {
        return new IconNode(this, false, getIcon());
    }
}
