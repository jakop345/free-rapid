package cz.felk.cvut.erm.sql.interfaces;

import cz.felk.cvut.erm.sql.SchemaSQL;

/**
 * Object, which creates SQL schema (sql commands)
 * should implement this interface.
 *
 * @see cz.felk.cvut.erm.sql.SchemaSQL
 */
public interface SQLSchemaProducer {
    /**
     * This method was created in VisualAge.
     *
     * @return cz.omnicom.ermodeller.sql.SchemaSQL
     */
    public SchemaSQL createSchemaSQL();
}
