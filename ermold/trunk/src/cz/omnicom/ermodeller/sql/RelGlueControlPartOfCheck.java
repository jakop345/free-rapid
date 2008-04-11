package cz.omnicom.ermodeller.sql;

import cz.omnicom.ermodeller.conc2rela.RelC2R;

/**
 * Part of check which enforces constraints for corresponding
 * conceptual relation in glued relation.
 */
public class RelGlueControlPartOfCheck extends GlueControlPartOfCheck {
    /**
     * Corresponding relational relation.
     */
    private RelC2R relation = null;

    /**
     * Constructor.
     *
     * @param aRelC2R cz.omnicom.ermodeller.conc2rela.RelC2R
     */
    public RelGlueControlPartOfCheck(RelC2R aRelC2R) {
        relation = aRelC2R;
    }

    /**
     * Returns string representation of the part of check.
     * "Relation RRR present".
     *
     * @return java.lang.String
     */
    public String toString() {
        return "Relation " + relation.getNameC2R() + " present";
    }
}
