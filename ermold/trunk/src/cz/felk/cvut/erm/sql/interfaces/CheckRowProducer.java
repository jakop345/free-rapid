package cz.felk.cvut.erm.sql.interfaces;

import cz.felk.cvut.erm.sql.CheckRow;

/**
 * Object, which creates checks command
 * should implement this interface.
 *
 * @see cz.felk.cvut.erm.sql.CheckRow
 */
public interface CheckRowProducer {
    /**
     * @return cz.omnicom.ermodeller.sql.CheckRow
     */
    public CheckRow createCheckRow();
}
