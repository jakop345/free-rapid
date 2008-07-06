package cz.felk.cvut.erm.ermodeller.interfaces;

import cz.felk.cvut.erm.ermodeller.EntityConstruct;
import cz.felk.cvut.erm.ermodeller.RelationConstruct;

/**
 * This interface adds the functionality for adding the new relations and entities.
 */
public interface ISchema {
    /**
     * Creates the new Entity.
     *
     * @param x   X coordinate of the left top point the new entity.
     * @param y   Y coordinate of the left top point the new entity.
     * @param old The entity that invokes the creation of the new entity.
     *            Used by decomposition of the entities.
     * @return The new entity.
     */
    EntityConstruct createEntity(int x, int y, EntityConstruct old);

    /**
     * Creates the new Relation.
     *
     * @param x X coordinate of the left top point the new relation.
     * @param y Y coordinate of the left top point the new relation.
     * @return The new relation.
     */
    RelationConstruct createRelation(int x, int y);
}
