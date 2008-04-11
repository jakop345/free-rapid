package cz.omnicom.ermodeller.conc2obj;

import cz.omnicom.ermodeller.conc2rela.AtributeC2R;
import cz.omnicom.ermodeller.icontree.IconNode;

import javax.swing.*;
import java.util.Vector;

/**
 * Column in the table - atribute.
 */
public class ColumnUniqueObj extends RowObj {
    /**
     * Atribute describing the column
     *
     * @see cz.omnicom.ermodeller.conc2rela.AtributeC2R
     */
    private Vector uniqueKeysC2R = new Vector();

    /**
     * Constructor.
     *
     * @param cz.omnicom.ermodeller.conc2rela.AtributeC2R
     *
     */
    public ColumnUniqueObj(Vector anAtribute) {
        uniqueKeysC2R = anAtribute;
    }

    /**
     * Creates string representation column (atribute)
     *
     * @param countTabs intendation from the left
     * @return java.lang.String
     * @see cz.omnicom.ermodeller.conc2rela.AtributeC2R#toString
     */
    public String createSubSQL(int countTabs) {
        return TabCreatorObj.getTabs(countTabs) + toString();
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
     * Returns icon for representing the column in the SQL tree.
     *
     * @return javax.swing.Icon
     */
    public Icon getIcon() {
        return new ImageIcon(ClassLoader.getSystemResource("img/unq.gif"));
    }

    /**
     * Returns name of atributes in UniqueKey
     *
     * @return java.lang.String
     */
    public String getNameString() {
        String pom = "";
        for (int i = 0; i < uniqueKeysC2R.size(); i++) {
            AtributeC2R a = (AtributeC2R) uniqueKeysC2R.get(i);
            pom = pom + a.getNameC2R();
            if (i < (uniqueKeysC2R.size() - 1)) pom = pom + ", ";
        }
        return pom;
    }

    /**
     * Returns string representation of the atribute.
     *
     * @return java.lang.String
     * @see #createSubSQL
     */
    public String toString() {
        return "Unique(" + getNameString() + ")";
    }
}
