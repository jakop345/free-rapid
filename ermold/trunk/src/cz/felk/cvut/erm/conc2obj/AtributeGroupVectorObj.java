package cz.felk.cvut.erm.conc2obj;

import cz.felk.cvut.erm.conc2obj.interfaces.SubObjProducer;
import cz.felk.cvut.erm.conc2obj.interfaces.SubTreeProducerObj;
import cz.felk.cvut.erm.conc2rela.AtributeC2R;
import cz.felk.cvut.erm.icontree.IconNode;

import javax.swing.*;
import java.util.Enumeration;
import java.util.Vector;

/**
 * Superclass for groups of atributes with the same controlled property.
 * For examaple that each of them must be NULL.
 */
abstract class AtributeGroupVectorObj implements SubObjProducer, SubTreeProducerObj {

    /**
     * Holder af atribute.
     */
    protected class AtributeC2RHolder implements SubTreeProducerObj, SubObjProducer {
        /**
         * Held atribute
         *
         * @see cz.felk.cvut.erm.conc2rela.AtributeC2R
         */
        AtributeC2R atributeC2R = null;
        /**
         * Control for Not Null or for beeing Null.
         */
        final boolean isNotNull;

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
            return TabCreatorObj.getTabs(countTabs) + toString();
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
    private Vector<AtributeC2RHolder> atributes = new Vector<AtributeC2RHolder>();

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
     * @see cz.felk.cvut.erm.sql.AtributeGroupVector#AtributeC2RHolder#createSubSQL
     */
    public String createSubSQL(int countTabs) {
        String result = (getAtributes().size() < 2) ? "" : TabCreatorObj.getTabs(countTabs) + "(\n";
        for (Enumeration<AtributeC2RHolder> elements = getAtributes().elements(); elements.hasMoreElements();) {
            AtributeC2RHolder atributeHolder = elements.nextElement();
            result += atributeHolder.createSubSQL((getAtributes().size() < 2) ? countTabs : countTabs + 1);
            if (elements.hasMoreElements())
                result += " AND\n";
        }
        return result + ((getAtributes().size() < 2) ? "" : "\n" + TabCreatorObj.getTabs(countTabs) + ")");
    }

    /**
     * Creates subtree from nodes.
     *
     * @return cz.omnicom.ermodeller.errorlog.icontree.IconNode
     * @see cz.felk.cvut.erm.sql.AtributeGroupVector#AtributeC2RHolder#createSubTree
     */
    public IconNode createSubTree() {
        IconNode top = new IconNode(this, true, getIcon());
        for (Enumeration<AtributeC2RHolder> elements = getAtributes().elements(); elements.hasMoreElements();) {
            AtributeC2RHolder atributeHolder = elements.nextElement();
            top.add(atributeHolder.createSubTree());
        }
        return top;
    }

    /**
     * Returns atribute holders.
     *
     * @return java.util.Vector
     */
    public Vector<AtributeC2RHolder> getAtributes() {
        if (atributes == null)
            atributes = new Vector<AtributeC2RHolder>();
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
