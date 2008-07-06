package cz.felk.cvut.erm.conc2obj.interfaces;

import cz.felk.cvut.erm.conc2obj.ConstraintObj;

/**
 * Object, which creates constraint command
 * should implement this interface.
 *
 * @see cz.felk.cvut.erm.sql.ConstraintSQL
 */
public interface ObjConstraintProducer {
    /**
     * @return cz.omnicom.ermodeller.sql.ConstraintSQL
     */
    public ConstraintObj createConstraintSQL();
}
