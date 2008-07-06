package cz.felk.cvut.erm.sql.interfaces;

import cz.felk.cvut.erm.sql.ConstraintSQL;

/**
 * Object, which creates constraint command
 * should implement this interface.
 *
 * @see cz.felk.cvut.erm.sql.ConstraintSQL
 */
public interface SQLConstraintProducer {
    /**
     * @return cz.omnicom.ermodeller.sql.ConstraintSQL
     */
    public ConstraintSQL createConstraintSQL();
}
