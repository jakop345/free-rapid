package cz.felk.cvut.erm.sql;

import cz.felk.cvut.erm.icontree.IconNode;

import javax.swing.*;

/**
 * Check row which implements OR check.
 */
public class GlueCheckRow extends CheckRow {
    /**
     * Check implementing conditions after gluing relations.
     *
     * @see cz.felk.cvut.erm.sql.ORCheck
     */
    private ORCheck orCheck = null;

    /**
     * Constructor.
     *
     * @param aORCheck cz.omnicom.ermodeller.sql.ORCheck
     */
    public GlueCheckRow(ORCheck aORCheck) {
        orCheck = aORCheck;
    }

    /**
     * Creates string representation of the check -
     * "Check (OrCheck)".
     *
     * @param countTabs intendation from the left
     * @return java.lang.String
     * @see cz.felk.cvut.erm.sql.ORCheck#createSubSQL
     */
    public String createSubSQL(int countTabs) {
        String result = TabCreator.getTabs(countTabs) + "Check (\n";
        result += orCheck.createSubSQL(countTabs + 1);
        return result + "\n" + TabCreator.getTabs(countTabs) + ")\n";
    }

    /**
     * Creates subtree from nodes.
     *
     * @return cz.omnicom.ermodeller.errorlog.icontree.IconNode
     */
    public IconNode createSubTree() {
        IconNode top = new IconNode(this, true, getIcon());
        top.add(orCheck.createSubTree());
        return top;
    }

    /**
     * Returns icon for representing the check row in the SQL tree.
     *
     * @return javax.swing.Icon
     */
    public Icon getIcon() {
        return new ImageIcon(ClassLoader.getSystemResource("img/checksql.gif"));
    }

    /**
     * Returns several spaces "        ".
     *
     * @return java.lang.String
     */
    public String toString() {
        return "          ";
    }
}
