package cz.omnicom.ermodeller.conc2obj;

/**
 * Object, which creates column command
 * should implement this interface.
 *
 * @see cz.omnicom.ermodeller.sql.ColumnSQL
 */
public interface ObjColumnProducer {
    /**
     * @return cz.omnicom.ermodeller.sql.ColumnSQL
     */
    ColumnObj createColumnSQL();
}
