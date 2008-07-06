package cz.felk.cvut.erm.conc2obj.interfaces;

import cz.felk.cvut.erm.conc2obj.DropCommandObj;

/**
 * Object, which creates drop table command
 * should implement this interface.
 *
 * @see cz.felk.cvut.erm.sql.DropCommandSQL
 */
public interface ObjDropCommandProducer {
    /**
     * @return cz.omnicom.ermodeller.sql.DropCommandSQL
     */
    public DropCommandObj createDropCommandSQL();
}
