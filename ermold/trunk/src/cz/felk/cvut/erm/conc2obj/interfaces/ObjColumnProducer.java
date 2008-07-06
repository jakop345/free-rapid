package cz.felk.cvut.erm.conc2obj.interfaces;

import cz.felk.cvut.erm.conc2obj.ColumnObj;

/**
 * Object, which creates column command
 * should implement this interface.
 *
 * @see cz.felk.cvut.erm.sql.ColumnSQL
 */
public interface ObjColumnProducer {
    /**
     * @return cz.omnicom.ermodeller.sql.ColumnSQL
     */
    ColumnObj createColumnSQL();
}
