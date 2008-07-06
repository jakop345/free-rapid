package cz.felk.cvut.erm.sql;

import cz.felk.cvut.erm.sql.interfaces.SubSQLProducer;
import cz.felk.cvut.erm.sql.interfaces.SubTreeProducer;


/**
 * Superclass of SQL commands.
 */
public abstract class CommandSQL implements SubSQLProducer, SubTreeProducer {
    /**
     * @return javax.swing.Icon
     */
    public abstract javax.swing.Icon getIcon();
}
