package cz.green.ermodeller;

import cz.green.swing.ShowException;
import cz.green.event.interfaces.Manager;

/**
 * This class exists to hold entity, which would like to participate on the relation (also held).
 * Used by creating <code>Cardinality</code> by container.
 *
 * @see Container#addingCardinality(cz.green.ermodeller.CardinalityPair)
 */
public class StrongAddictionPair {
    private Entity entity = null;
    private UniqueKey uniqueKey = null;

    /**
     * Construct empty cardinality pair.
     */
    public StrongAddictionPair() {
        super();
    }

    /**
     * Construct cardinality pair and sets the relation and the entity.
     */
    public StrongAddictionPair(Entity ent, UniqueKey uk) {
        this();
        setEntity(ent);
        setUniqueKey(uk);
    }

    /**
     * Calls the <code>createCardinality</code> method of the <code>Relation</code> class.
     *
     * @param manager The window group (or desktop) where to put the new cardinality.
     * @param left    The x coordinate of the left top point of the new atribute.
     * @param top     The y coordinate of the left top point of the new atribute.
     * @return The created cardinality.
     */
    public StrongAddiction create(Manager manager, int left, int top) {
        try {
            if ((getUniqueKey() == null) || (getEntity() == null))
                return null;
            //create strong addiction
//OPRAVIT!!!!
            return StrongAddiction.createStrongAddiction(getEntity(), getEntity(), manager, left, top);
        } catch (Throwable x) {
            ShowException d = new ShowException(null, "Error", x, true);
        }
        return null;
    }

    /**
     * Gets the entity stored in the cardinality pair.
     *
     * @return The entity.
     * @see #entity
     */
    public Entity getEntity() {
        return entity;
    }

    /**
     * Gets the relation stored in the cardinality pair.
     *
     * @return The relation.
     * @see #relation
     */
    public UniqueKey getUniqueKey() {
        return uniqueKey;
    }

    /**
     * Sets the entity stored in the cardinality pair.
     *
     * @param entity The entity.
     * @see #entity
     */
    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    /**
     * Sets the relation stored in the cardinality pair.
     *
     * @param relation The relation.
     * @see #relation
     */
    public void setUniqueKey(UniqueKey uniqueKey) {
        this.uniqueKey = uniqueKey;
    }
}
