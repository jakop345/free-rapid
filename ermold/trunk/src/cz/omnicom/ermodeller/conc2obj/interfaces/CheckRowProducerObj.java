package cz.omnicom.ermodeller.conc2obj.interfaces;

import cz.omnicom.ermodeller.conc2obj.CheckRowObj;

/**
 * Object, which creates checks command
 * should implement this interface.
 *
 * @see cz.omnicom.ermodeller.sql.CheckRow
 */
public interface CheckRowProducerObj {
    /**
     * @return cz.omnicom.ermodeller.sql.CheckRow
     */
    public CheckRowObj createCheckRow();
}
