package cz.omnicom.ermodeller.sql;

/**
 * Object, which creates column command
 * should implement this interface.
 *
 * @see cz.omnicom.ermodeller.sql.ColumnSQL
 */
public interface SQLColumnProducer {
    /**
     * @return cz.omnicom.ermodeller.sql.ColumnSQL
     */
    ColumnSQL createColumnSQL();
}
