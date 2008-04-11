package cz.omnicom.ermodeller.conc2obj.interfaces;

import cz.omnicom.ermodeller.conc2obj.DropCommandObj;

/**
 * Object, which creates drop table command
 * should implement this interface.
 *
 * @see cz.omnicom.ermodeller.sql.DropCommandSQL
 */
public interface ObjDropCommandProducer {
    /**
     * @return cz.omnicom.ermodeller.sql.DropCommandSQL
     */
    public DropCommandObj createDropCommandSQL();
}
