package cz.felk.cvut.erm.ermodeller.interfaces;

import cz.felk.cvut.erm.ermodeller.CardinalityPair;
import cz.felk.cvut.erm.ermodeller.ConceptualConstructItem;
import cz.felk.cvut.erm.ermodeller.EntityConstruct;
import cz.felk.cvut.erm.event.interfaces.Item;

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
    public boolean addingAtribute(ConceptualConstructItem object);

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
    public boolean addingUniqueKey(ConceptualConstructItem object);

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
