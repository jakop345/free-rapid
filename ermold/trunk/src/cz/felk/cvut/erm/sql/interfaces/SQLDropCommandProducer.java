package cz.felk.cvut.erm.sql.interfaces;

import cz.felk.cvut.erm.sql.DropCommandSQL;

/**
 * Object, which creates drop table command
 * should implement this interface.
 *
 * @see cz.felk.cvut.erm.sql.DropCommandSQL
 */
public interface SQLDropCommandProducer {
    /**
     * @return cz.omnicom.ermodeller.sql.DropCommandSQL
     */
    public DropCommandSQL createDropCommandSQL();
}
