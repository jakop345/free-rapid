package cz.omnicom.ermodeller.sql.interfaces;

import cz.omnicom.ermodeller.sql.DropCommandSQL;

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
