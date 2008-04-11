package cz.omnicom.ermodeller.sql.interfaces;

import cz.omnicom.ermodeller.sql.ConstraintSQL;

/**
 * Object, which creates constraint command
 * should implement this interface.
 *
 * @see cz.omnicom.ermodeller.sql.ConstraintSQL
 */
public interface SQLConstraintProducer {
    /**
     * @return cz.omnicom.ermodeller.sql.ConstraintSQL
     */
    public ConstraintSQL createConstraintSQL();
}
