package cz.omnicom.ermodeller.conc2obj.interfaces;

import cz.omnicom.ermodeller.conc2obj.ConstraintObj;

/**
 * Object, which creates constraint command
 * should implement this interface.
 *
 * @see cz.omnicom.ermodeller.sql.ConstraintSQL
 */
public interface ObjConstraintProducer {
    /**
     * @return cz.omnicom.ermodeller.sql.ConstraintSQL
     */
    public ConstraintObj createConstraintSQL();
}
