package cz.omnicom.ermodeller.conc2rela;

import cz.omnicom.ermodeller.conc2rela.exception.AlreadyContainsExceptionC2R;
import cz.omnicom.ermodeller.conceptual.Atribute;
import cz.omnicom.ermodeller.conceptual.EntityBean;

import java.util.Vector;

/**
 * Superclass of all relations created from conceptual entities.
 *
 * @see cz.omnicom.ermodeller.conceptual.EntityBean
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
     * @param aSchemaC2R            owner schema
     * @param aConceptualEntityBean corresponding conceptual entity
     * @throws cz.omnicom.ermodeller.conc2rela.WasNotFoundByConceptualExceptionC2R
     *
     * @throws cz.omnicom.ermodeller.conc2rela.AlreadyContainsExceptionC2R
     *
     * @see cz.omnicom.ermodeller.conceptual.EntityBean
     */
    public EntC2R(SchemaC2R aSchemaC2R, EntityBean aConceptualEntityBean) throws AlreadyContainsExceptionC2R {
        super(aSchemaC2R, aConceptualEntityBean);
        UniqueKeyC2R uniqueKeyC2R = new UniqueKeyC2R(aSchemaC2R, this, aConceptualEntityBean.getPrimaryKey(), aConceptualEntityBean.getAtributes(), true);
        try {
            addUniqueKeyC2R(uniqueKeyC2R);
        }
        catch (AlreadyContainsExceptionC2R e) {
        } // cannot be thrown

        boolean isUnique = false;
        Vector<Atribute> atributes = aConceptualEntityBean.getAtributes();
        for (Atribute a : atributes) {
            if (a.isUnique() && !a.isPrimary()) isUnique = true;
        }
        if (isUnique) {
            UniqueKeyC2R uniqueKeyC2R1 = new UniqueKeyC2R(aSchemaC2R, this, aConceptualEntityBean.getAtributes(), aConceptualEntityBean.getAtributes(), false);
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
    protected boolean alreadyAddedToSonGraph() {
        return getLevel() > -1;
    }

    /**
     * @return int
     */
    protected int getLevel() {
        return level;
    }

    /**
     * Set unspecified level.
     */
    protected void resetLevel() {
        this.level = DEFAULT_LEVEL;
    }

    /**
     * Sets level.
     *
     * @param newValue int
     */
    protected void setLevel(int newValue) {
        if (newValue < -1)
            this.level = DEFAULT_LEVEL;
        else
            this.level = newValue;
    }
}
