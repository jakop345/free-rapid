package cz.omnicom.ermodeller.sql;

/**
 * Object, which creates checks command
 * should implement this interface.
 *
 * @see cz.omnicom.ermodeller.sql.CheckRow
 */
public interface CheckRowProducer {
    /**
     * @return cz.omnicom.ermodeller.sql.CheckRow
     */
    public CheckRow createCheckRow();
}
