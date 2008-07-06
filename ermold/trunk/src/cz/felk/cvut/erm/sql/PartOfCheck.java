package cz.felk.cvut.erm.sql;

import cz.felk.cvut.erm.sql.interfaces.SubSQLProducer;
import cz.felk.cvut.erm.sql.interfaces.SubTreeProducer;


/**
 * PartOfCheck represents any condition closed in brackets in check.
 */
public abstract class PartOfCheck implements SubSQLProducer, SubTreeProducer {
    /**
     * @return javax.swing.Icon
     */
    public abstract javax.swing.Icon getIcon();
}
