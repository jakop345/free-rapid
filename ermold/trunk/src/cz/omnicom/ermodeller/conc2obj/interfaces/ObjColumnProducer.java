package cz.omnicom.ermodeller.conc2obj.interfaces;

import cz.omnicom.ermodeller.conc2obj.ColumnObj;

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
