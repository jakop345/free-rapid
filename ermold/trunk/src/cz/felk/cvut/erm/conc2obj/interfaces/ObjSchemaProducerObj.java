package cz.felk.cvut.erm.conc2obj.interfaces;

import cz.felk.cvut.erm.conc2obj.SchemaObjSQL;

/**
 * Object, which creates SQL schema (sql commands)
 * should implement this interface.
 *
 * @see cz.felk.cvut.erm.sql.SchemaSQL
 */
public interface ObjSchemaProducerObj {
    /**
     * This method was created in VisualAge.
     *
     * @return cz.omnicom.ermodeller.sql.SchemaSQL
     */
    public SchemaObjSQL createSchemaObj();
}
