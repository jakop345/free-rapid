package cz.omnicom.ermodeller.sql;

import cz.omnicom.ermodeller.sql.interfaces.SubSQLProducer;
import cz.omnicom.ermodeller.sql.interfaces.SubTreeProducer;


/**
 * Superclass of SQL commands.
 */
public abstract class CommandSQL implements SubSQLProducer, SubTreeProducer {
    /**
     * @return javax.swing.Icon
     */
    public abstract javax.swing.Icon getIcon();
}
