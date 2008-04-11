package cz.omnicom.ermodeller.conc2obj;

import cz.omnicom.ermodeller.conc2rela.AtributeC2R;
import cz.omnicom.ermodeller.conc2rela.NameC2R;
import cz.omnicom.ermodeller.conc2rela.UniqueKeyC2R;
import cz.omnicom.ermodeller.icontree.IconNode;

import javax.swing.*;
import java.util.Enumeration;
import java.util.Vector;

/**
 * Foreign key from one relation to another relation.
 * Referenced atributes must be unique group.
 */
public class ForeignKeyObj extends ConstraintObj {
    /**
     * Group of atributes, which values must be from referenced group.
     *
     * @see cz.omnicom.ermodeller.conc2rela.AtributeC2R
     */
    private Vector fromAtributesC2R = null;
    /**
     * Referenced group of atributes.
     *
     * @see cz.omnicom.ermodeller.conc2rela.UniqueKeyC2R
     */
    private UniqueKeyC2R toUniqueKeyC2R = null;
    /**
     * Name.
     */
    private NameC2R name = null;

    /**
     * Constructor.
     *
     * @param fromUniqueKeyC2R group of foreign atributes
     * @param toUniqueKeyC2R   referenced group (unique key)
     */
    public ForeignKeyObj(Vector aFromAtributesC2R, UniqueKeyC2R aToUniqueKeyC2R, NameC2R aName) {
        name = new NameC2R(aName);
        fromAtributesC2R = aFromAtributesC2R;
        toUniqueKeyC2R = aToUniqueKeyC2R;
    }

    /**
     * Creates string representation of the check -
     * "constraint foreign key (col, col, ...) references TAB(col, col, ...)".
     *
     * @param countTabs intendation from the left
     * @return java.lang.String
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
     * Returns list of atributes' names in brackets.
     * "(name, name, ...)"
     *
     * @return java.lang.String
     */
    public String getAtributesString() {
        String result = "(";
        for (Enumeration elements = fromAtributesC2R.elements(); elements.hasMoreElements();) {
            result += ((AtributeC2R) elements.nextElement()).getNameC2R();
            if (elements.hasMoreElements())
                result += ", ";
        }
        result += ")";
        return result;
    }

    /**
     * Returns icon for representing the foreign key in the SQL tree.
     *
     * @return javax.swing.Icon
     */
    public Icon getIcon() {
        return new ImageIcon(ClassLoader.getSystemResource("img/fk.gif"));
    }

    /**
     * This method was created in VisualAge.
     *
     * @return cz.omnicom.ermodeller.conc2rela.NameC2R
     */
    public NameC2R getName() {
        return name;
    }

    /**
     * Returns string representation.
     * "foreign key (atributes names) references TAB(atributes names)"
     *
     * @return java.lang.String
     */
    public String toString() {
        return "Constraint " + getName() + " Foreign Key " + getAtributesString() + " References " + toUniqueKeyC2R.getRelationC2R().getNameC2R() + toUniqueKeyC2R.getGroupString();
    }
}
