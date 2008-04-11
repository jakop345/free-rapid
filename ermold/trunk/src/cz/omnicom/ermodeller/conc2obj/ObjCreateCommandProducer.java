package cz.omnicom.ermodeller.conc2obj;

/**
 * Object, which creates create table command
 * should implement this interface.
 *
 * @see cz.omnicom.ermodeller.sql.CreateCommandSQL
 */
public interface ObjCreateCommandProducer {
    /**
     * @return cz.omnicom.ermodeller.sql.CommandSQL
     */
    public CreateCommandObj createCreateCommandSQL();
}
