package cz.omnicom.ermodeller.conc2rela;

import cz.omnicom.ermodeller.conc2rela.exception.AlreadyContainsExceptionC2R;
import cz.omnicom.ermodeller.conceptual.Atribute;
import cz.omnicom.ermodeller.conceptual.Entity;

import java.util.Vector;

/**
 * Superclass of all relations created from conceptual entities.
 *
 * @see cz.omnicom.ermodeller.conceptual.Entity
 */
public abstract class EntC2R extends RelationC2R {
    /**
     * Used when creating primary keys.
     * See cz.omnicom.ermodeller.conc2rela.SchemaC2R#createPrimaryKeysC2R
     */
    private int level = DEFAULT_LEVEL;

    private static final int DEFAULT_LEVEL = -1;

    /**
     * EntC2R constructor.
     *
     * @param aSchemaC2R        owner schema
     * @param aConceptualEntity corresponding conceptual entity
     * @throws cz.omnicom.ermodeller.conc2rela.WasNotFoundByConceptualExceptionC2R
     *
     * @throws cz.omnicom.ermodeller.conc2rela.AlreadyContainsExceptionC2R
     *
     * @see cz.omnicom.ermodeller.conceptual.Entity
     */
    EntC2R(SchemaC2R aSchemaC2R, Entity aConceptualEntity) throws AlreadyContainsExceptionC2R {
        super(aSchemaC2R, aConceptualEntity);
        UniqueKeyC2R uniqueKeyC2R = new UniqueKeyC2R(aSchemaC2R, this, aConceptualEntity.getPrimaryKey(), aConceptualEntity.getAtributes(), true);
        try {
            addUniqueKeyC2R(uniqueKeyC2R);
        }
        catch (AlreadyContainsExceptionC2R e) {
        } // cannot be thrown

        boolean isUnique = false;
        Vector<Atribute> atributes = aConceptualEntity.getAtributes();
        for (Atribute a : atributes) {
            if (a.isUnique() && !a.isPrimary()) isUnique = true;
        }
        if (isUnique) {
            UniqueKeyC2R uniqueKeyC2R1 = new UniqueKeyC2R(aSchemaC2R, this, aConceptualEntity.getAtributes(), aConceptualEntity.getAtributes(), false);
            try {
                addUniqueKeyC2R(uniqueKeyC2R1);
            }
            catch (AlreadyContainsExceptionC2R e) {
            }
        }
        /*for (Enumeration elements = aConceptualEntity.getUniqueKeys().elements(); elements.hasMoreElements();) {
          UniqueKeyC2R uniqueKeyC2R = new UniqueKeyC2R(aSchemaC2R, this, (UniqueKey) elements.nextElement());
              // can throw AlreadyContains, WasNotFound
          try {
              addUniqueKeyC2R(uniqueKeyC2R);
          }
          catch (AlreadyContainsExceptionC2R e) {} // cannot be thrown
      }*/
    }

    /**
     * Level already specified.
     *
     * @return boolean
     */
    boolean alreadyAddedToSonGraph() {
        return getLevel() > -1;
    }

    /**
     * @return int
     */
    int getLevel() {
        return level;
    }

    /**
     * Set unspecified level.
     */
    void resetLevel() {
        this.level = DEFAULT_LEVEL;
    }

    /**
     * Sets level.
     *
     * @param newValue int
     */
    void setLevel(int newValue) {
        if (newValue < -1)
            this.level = DEFAULT_LEVEL;
        else
            this.level = newValue;
    }
}
