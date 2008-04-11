package cz.omnicom.ermodeller.sql.interfaces;

import cz.omnicom.ermodeller.sql.AlterAddCommandSQL;

/**
 * Object, which creates alter table add command
 * should implement this interface.
 *
 * @see cz.omnicom.ermodeller.sql.AlterAddCommandSQL
 */
public interface SQLAlterAddCommandProducer {
    /**
     * @return cz.omnicom.ermodeller.sql.AlterAddCommandSQL
     */
    public AlterAddCommandSQL createAlterAddCommandSQL();
}
