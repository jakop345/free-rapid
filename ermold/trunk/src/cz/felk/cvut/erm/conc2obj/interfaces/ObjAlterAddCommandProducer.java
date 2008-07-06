package cz.felk.cvut.erm.conc2obj.interfaces;

import cz.felk.cvut.erm.conc2obj.AlterAddCommandObj;

/**
 * Object, which creates alter table add command
 * should implement this interface.
 *
 * @see cz.felk.cvut.erm.sql.AlterAddCommandSQL
 */
public interface ObjAlterAddCommandProducer {
    /**
     * @return cz.omnicom.ermodeller.sql.AlterAddCommandSQL
     */
    public AlterAddCommandObj createAlterAddCommandSQL();
}
