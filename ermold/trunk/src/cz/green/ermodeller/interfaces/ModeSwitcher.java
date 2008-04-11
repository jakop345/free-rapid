package cz.green.ermodeller.interfaces;

import cz.green.ermodeller.CardinalityPair;
import cz.green.ermodeller.ConceptualConstruct;
import cz.green.ermodeller.EntityConstruct;
import cz.green.event.interfaces.Item;

/**
 * This class has the same functionality as its predecessor. Adds many new work regimes, help functionality
 * and font management.
 */
public interface ModeSwitcher {
    /**
     * Set regime for adding atribute.
     *
     * @param object The owner of the new atribute.
     */
    public boolean addingAtribute(ConceptualConstruct object);

    /**
     * Set regime for adding cardinality.
     *
     * @param object The entity and the realtion for the new cradinality.
     */
    public boolean addingCardinality(CardinalityPair object);

    /**
     * Set regime for adding entity.
     *
     * @param object When this adding is used for decomposition the entity, it is
     *               the former entity, otherwise it's <code>null</code>.
     */
    public boolean addingEntity(EntityConstruct ent);

    /**
     * Set regime for adding group.
     */
    public boolean addingGroup();

    /**
     * Set regime for adding relation.
     */
    public boolean addingRelation();

    /**
     * Set regime for adding relation with connection to 2 entities.
     */
    public boolean addingRelationCon(EntityConstruct object);

    /**
     * Set regime for adding unique key.
     *
     * @param object The owner of the new unique key.
     */
    public boolean addingUniqueKey(ConceptualConstruct object);

    /**
     * Set regime for deleting elements.
     */
    public boolean deleting();

    /**
     * Display helpURL in browser using cz.green.uti.BrowserControl.
     *
     * @see cz.green.uti.BrowserControl
     */
    public void help();

    /**
     * Set regime for removing connections.
     *
     * @param item This item is sent as item with DragOverEvent and DropAboveEvent.
     */
    public boolean removing(Item item);

    /**
     * Set normal working regime.
     */
    public boolean working();
}
