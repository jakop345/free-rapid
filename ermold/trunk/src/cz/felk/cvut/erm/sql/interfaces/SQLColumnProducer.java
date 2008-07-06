package cz.felk.cvut.erm.sql.interfaces;

import cz.felk.cvut.erm.sql.ColumnSQL;

/**
 * Object, which creates column command
 * should implement this interface.
 *
 * @see cz.felk.cvut.erm.sql.ColumnSQL
 */
public interface SQLColumnProducer {
    /**
     * @return cz.omnicom.ermodeller.sql.ColumnSQL
     */
    ColumnSQL createColumnSQL();
}
