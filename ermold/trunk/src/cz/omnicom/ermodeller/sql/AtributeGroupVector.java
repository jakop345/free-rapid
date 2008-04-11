package cz.omnicom.ermodeller.sql;

import cz.omnicom.ermodeller.conc2rela.AtributeC2R;
import cz.omnicom.ermodeller.icontree.IconNode;

import javax.swing.*;
import java.util.Enumeration;
import java.util.Vector;

/**
 * Superclass for groups of atributes with the same controlled property.
 * For examaple that each of them must be NULL.
 */
public abstract class AtributeGroupVector implements SubSQLProducer, SubTreeProducer {

    /**
     * Holder af atribute.
     */
    protected class AtributeC2RHolder implements SubTreeProducer, SubSQLProducer {
        /**
         * Held atribute
         *
         * @see cz.omnicom.ermodeller.conc2rela.AtributeC2R
         */
        AtributeC2R atributeC2R = null;
        /**
         * Control for Not Null or for beeing Null.
         */
        boolean isNotNull;

        public AtributeC2RHolder(AtributeC2R anAtributeC2R, boolean aIsNotNull) {
            this.atributeC2R = anAtributeC2R;
            this.isNotNull = aIsNotNull;
        }

        /**
         * Returns string representation of atribute condition
         * regarding to <code>isNotNull</code>.
         *
         * @return java.lang.String
         */
        public String toString() {
            return atributeC2R.getNameC2R() + (isNotNull ? " Is Not Null" : " Is Null");
        }

        public IconNode createSubTree() {
            return new IconNode(this, false, this.getIcon());
        }

        public String createSubSQL(int countTabs) {
            return TabCreator.getTabs(countTabs) + toString();
        }

        /**
         * Returns Icon regarding to type of control.
         *
         * @return javax.swing.Icon
         */
        public Icon getIcon() {
            if (isNotNull)
                return new ImageIcon(ClassLoader.getSystemResource("img/notnullcolumn.gif"));
            else
                return new ImageIcon(ClassLoader.getSystemResource("img/nullcolumn.gif"));
        }
    }

    /**
     * Atributes in group - holders of columns.
     */
    private Vector atributes = new Vector();

    /**
     * Adds antribute.
     *
     * @param anAtributeC2R added atribute
     */
    public void addAtributeC2R(AtributeC2R anAtributeC2R) {
        getAtributes().addElement(new AtributeC2RHolder(anAtributeC2R, getIsNotNull()));
    }

    /**
     * Creates string representation of the conditions upon columns -
     * "col AND col AND ...".
     *
     * @param countTabs intendation from the left
     * @return java.lang.String
     * @see cz.omnicom.ermodeller.sql.AtributeGroupVector#AtributeC2RHolder#createSubSQL
     */
    public String createSubSQL(int countTabs) {
        String result = (getAtributes().size() < 2) ? "" : TabCreator.getTabs(countTabs) + "(\n";
        for (Enumeration elements = getAtributes().elements(); elements.hasMoreElements();) {
            AtributeC2RHolder atributeHolder = (AtributeC2RHolder) elements.nextElement();
            result += atributeHolder.createSubSQL((getAtributes().size() < 2) ? countTabs : countTabs + 1);
            if (elements.hasMoreElements())
                result += " AND\n";
        }
        return result + ((getAtributes().size() < 2) ? "" : "\n" + TabCreator.getTabs(countTabs) + ")");
    }

    /**
     * Creates subtree from nodes.
     *
     * @return cz.omnicom.ermodeller.errorlog.icontree.IconNode
     * @see cz.omnicom.ermodeller.sql.AtributeGroupVector#AtributeC2RHolder#createSubTree
     */
    public IconNode createSubTree() {
        IconNode top = new IconNode(this, true, getIcon());
        for (Enumeration elements = getAtributes().elements(); elements.hasMoreElements();) {
            AtributeC2RHolder atributeHolder = (AtributeC2RHolder) elements.nextElement();
            top.add(atributeHolder.createSubTree());
        }
        return top;
    }

    /**
     * Returns atribute holders.
     *
     * @return java.util.Vector
     */
    public Vector getAtributes() {
        if (atributes == null)
            atributes = new Vector();
        return atributes;
    }

    /**
     * @return javax.swing.Icon
     */
    public abstract javax.swing.Icon getIcon();

    /**
     * Returns whether check is provided like:
     * "atr IS NOT NULL".
     */
    public abstract boolean getIsNotNull();

    /**
     * Returns whether vector is empty ot not.
     *
     * @return boolean
     */
    public boolean isEmpty() {
        return getAtributes().isEmpty();
    }

    /**
     * Returns string representation of group.
     * "abcd group"
     *
     * @return java.lang.String
     */
    public abstract String toString();
}
