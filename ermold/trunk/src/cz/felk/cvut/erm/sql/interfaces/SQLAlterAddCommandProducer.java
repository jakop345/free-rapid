package cz.felk.cvut.erm.sql.interfaces;

import cz.felk.cvut.erm.sql.AlterAddCommandSQL;

/**
 * Object, which creates alter table add command
 * should implement this interface.
 *
 * @see cz.felk.cvut.erm.sql.AlterAddCommandSQL
 */
public interface SQLAlterAddCommandProducer {
    /**
     * @return cz.omnicom.ermodeller.sql.AlterAddCommandSQL
     */
    public AlterAddCommandSQL createAlterAddCommandSQL();
}
