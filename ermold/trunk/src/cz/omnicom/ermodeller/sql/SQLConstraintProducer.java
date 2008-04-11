package cz.omnicom.ermodeller.sql;

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
