package cz.felk.cvut.erm.event;

import cz.felk.cvut.erm.event.interfaces.Item;

/**
 * This is event for adding item to the manager.
 */
public class AddItemEvent extends CoordinateEvent {
    /**
     * This item we have to add to the manager.
     */
    private Item item = null;

    /**
     * Constructor for createing the event, that can add item to the manager.
     *
     * @param <code>item</code> This item we have to add to the manager.
     * @see CoordinateEvent#CoordinateEvent(int, int, java.awt.Component)
     */
    public AddItemEvent(int x, int y, Item item, java.awt.Component comp) {
        super(x, y, comp);
        this.item = item;
    }

    /**
     * Constructor for createing the event, that can add item to the manager.
     *
     * @param <code>item</code> This item we have to add to the manager.
     * @see CoordinateEvent#CoordinateEvent(int, int, java.awt.Component)
     */
    public AddItemEvent(int x, int y, java.awt.Component comp, Item item) {
        super(x, y, comp);
        this.item = item;
    }

    /**
     * Returns the value of property <code>item</code>.
     *
     * @return The stored item.
     * @see AddItemEvent.item
     */
    public Item getItem() {
        return item;
    }
}
