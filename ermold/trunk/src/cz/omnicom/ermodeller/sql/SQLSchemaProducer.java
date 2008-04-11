package cz.omnicom.ermodeller.sql;

/**
 * Object, which creates SQL schema (sql commands)
 * should implement this interface.
 *
 * @see cz.omnicom.ermodeller.sql.SchemaSQL
 */
public interface SQLSchemaProducer {
    /**
     * This method was created in VisualAge.
     *
     * @return cz.omnicom.ermodeller.sql.SchemaSQL
     */
    public SchemaSQL createSchemaSQL();
}
