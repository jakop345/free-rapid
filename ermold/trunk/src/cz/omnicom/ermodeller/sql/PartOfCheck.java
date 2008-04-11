package cz.omnicom.ermodeller.sql;

import cz.omnicom.ermodeller.sql.interfaces.SubSQLProducer;
import cz.omnicom.ermodeller.sql.interfaces.SubTreeProducer;


/**
 * PartOfCheck represents any condition closed in brackets in check.
 */
public abstract class PartOfCheck implements SubSQLProducer, SubTreeProducer {
    /**
     * @return javax.swing.Icon
     */
    public abstract javax.swing.Icon getIcon();
}
