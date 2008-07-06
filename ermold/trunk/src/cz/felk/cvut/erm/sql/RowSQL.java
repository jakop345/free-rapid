package cz.felk.cvut.erm.sql;

import cz.felk.cvut.erm.sql.interfaces.SubSQLProducer;
import cz.felk.cvut.erm.sql.interfaces.SubTreeProducer;


/**
 * Row is element of command. Commands contain only rows.
 *
 * @see cz.felk.cvut.erm.sql.CommandSQL
 */
public abstract class RowSQL implements SubSQLProducer, SubTreeProducer {
    /**
     * @return javax.swing.Icon
     */
    public abstract javax.swing.Icon getIcon();
}
