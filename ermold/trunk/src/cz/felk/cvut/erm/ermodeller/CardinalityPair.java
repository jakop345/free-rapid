package cz.felk.cvut.erm.ermodeller;

import cz.felk.cvut.erm.event.interfaces.Manager;


/**
 * This class exists to hold entity, which would like to participate on the relation (also held).
 * Used by creating <code>Cardinality</code> by container.
 *
 * @see DesktopContainer#addingCardinality(cz.felk.cvut.erm.ermodeller.CardinalityPair)
 */
public class CardinalityPair {
    private EntityConstruct entity = null;
    private RelationConstruct relation = null;

    /**
     * Construct empty cardinality pair.
     */
    public CardinalityPair() {
        super();
    }

    /**
     * Construct cardinality pair and sets the relation and the entity.
     */
    public CardinalityPair(EntityConstruct ent, RelationConstruct rel) {
        this();
        setEntity(ent);
        setRelation(rel);
    }

    /**
     * Calls the <code>createCardinality</code> method of the <code>Relation</code> class.
     *
     * @param manager The window group (or desktop) where to put the new cardinality.
     * @param left    The x coordinate of the left top point of the new atribute.
     * @param top     The y coordinate of the left top point of the new atribute.
     * @return The created cardinality.
     */
    public CardinalityConstruct create(Manager manager, int left, int top) {
        if ((getRelation() == null) || (getEntity() == null))
            return null;
        return getRelation().createCardinality(getEntity(), manager, left, top);
    }

    /**
     * Gets the entity stored in the cardinality pair.
     *
     * @return The entity.
     * @see #entity
     */
    public EntityConstruct getEntity() {
        return entity;
    }

    /**
     * Gets the relation stored in the cardinality pair.
     *
     * @return The relation.
     * @see #relation
     */
    public RelationConstruct getRelation() {
        return relation;
    }

    /**
     * Sets the entity stored in the cardinality pair.
     *
     * @param entity The entity.
     * @see #entity
     */
    public void setEntity(EntityConstruct entity) {
        this.entity = entity;
    }

    /**
     * Sets the relation stored in the cardinality pair.
     *
     * @param relation The relation.
     * @see #relation
     */
    public void setRelation(RelationConstruct relation) {
        this.relation = relation;
    }

    /**
     * Insert the method's description here.
     * Creation date: (6.4.2001 18:55:11)
     *
     * @param pw java.io.PrintWriter
     */
    public void write(java.io.PrintWriter pw) {
        pw.println("<cardinalitypair>");
        pw.println("</cardinalitypair>");
    }
/**
 * @return Returns the relationConnection.
 */
}
