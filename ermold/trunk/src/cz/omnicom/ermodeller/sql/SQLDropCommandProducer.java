package cz.omnicom.ermodeller.sql;

/**
 * Object, which creates drop table command
 * should implement this interface.
 *
 * @see cz.omnicom.ermodeller.sql.DropCommandSQL
 */
public interface SQLDropCommandProducer {
    /**
     * @return cz.omnicom.ermodeller.sql.DropCommandSQL
     */
    public DropCommandSQL createDropCommandSQL();
}
