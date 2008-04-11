package cz.omnicom.ermodeller.conc2obj;

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
