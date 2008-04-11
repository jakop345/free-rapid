package cz.omnicom.ermodeller.conc2obj.interfaces;

import cz.omnicom.ermodeller.conc2obj.SchemaObj;

/**
 * Object, which creates SQL schema (sql commands)
 * should implement this interface.
 *
 * @see cz.omnicom.ermodeller.sql.SchemaSQL
 */
public interface ObjSchemaProducerObj {
    /**
     * This method was created in VisualAge.
     *
     * @return cz.omnicom.ermodeller.sql.SchemaSQL
     */
    public SchemaObj createSchemaObj();
}
