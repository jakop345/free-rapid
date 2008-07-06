package cz.felk.cvut.erm.conc2obj.interfaces;

import cz.felk.cvut.erm.conc2obj.CheckRowObj;

/**
 * Object, which creates checks command
 * should implement this interface.
 *
 * @see cz.felk.cvut.erm.sql.CheckRow
 */
public interface CheckRowProducerObj {
    /**
     * @return cz.omnicom.ermodeller.sql.CheckRow
     */
    public CheckRowObj createCheckRow();
}
