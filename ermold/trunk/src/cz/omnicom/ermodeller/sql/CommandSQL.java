package cz.omnicom.ermodeller.sql;


/**
 * Superclass of SQL commands.
 */
public abstract class CommandSQL implements SubSQLProducer, SubTreeProducer {
    /**
     * @return javax.swing.Icon
     */
    protected abstract javax.swing.Icon getIcon();
}
