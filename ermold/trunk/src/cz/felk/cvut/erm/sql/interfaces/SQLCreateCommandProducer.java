package cz.felk.cvut.erm.sql.interfaces;

import cz.felk.cvut.erm.sql.CreateCommandSQL;

/**
 * Object, which creates create table command
 * should implement this interface.
 *
 * @see cz.felk.cvut.erm.sql.CreateCommandSQL
 */
public interface SQLCreateCommandProducer {
    /**
     * @return cz.omnicom.ermodeller.sql.CommandSQL
     */
    public CreateCommandSQL createCreateCommandSQL();
}
