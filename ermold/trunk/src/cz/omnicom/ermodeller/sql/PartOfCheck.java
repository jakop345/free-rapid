package cz.omnicom.ermodeller.sql;


/**
 * PartOfCheck represents any condition closed in brackets in check.
 */
public abstract class PartOfCheck implements SubSQLProducer, SubTreeProducer {
    /**
     * @return javax.swing.Icon
     */
    public abstract javax.swing.Icon getIcon();
}
