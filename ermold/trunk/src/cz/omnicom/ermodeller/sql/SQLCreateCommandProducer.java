package cz.omnicom.ermodeller.sql;

/**
 * Object, which creates create table command
 * should implement this interface.
 *
 * @see cz.omnicom.ermodeller.sql.CreateCommandSQL
 */
public interface SQLCreateCommandProducer {
    /**
     * @return cz.omnicom.ermodeller.sql.CommandSQL
     */
    public CreateCommandSQL createCreateCommandSQL();
}
