package cz.felk.cvut.erm.conc2obj.interfaces;

import cz.felk.cvut.erm.conc2obj.CreateCommandObj;

/**
 * Object, which creates create table command
 * should implement this interface.
 *
 * @see cz.felk.cvut.erm.sql.CreateCommandSQL
 */
public interface ObjCreateCommandProducer {
    /**
     * @return cz.omnicom.ermodeller.sql.CommandSQL
     */
    public CreateCommandObj createCreateCommandSQL();
}
