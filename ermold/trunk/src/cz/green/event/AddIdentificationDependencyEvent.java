package cz.green.event;

import cz.green.event.interfaces.Item;

/**
 * This event is send to the item when other item is dragging over it. Carry information
 * about the dragging item and whether this event is in add or remove event.
 * <p/>
 * Is send by handlers of the <code>ExMovingEvent</code> or by the <code>Container</code>
 * when its in the <code>removing</code> regime.
 */
public class AddIdentificationDependencyEvent extends cz.green.event.CoordinateEvent {
    /**
     * The item dragging over receiver of the event
     */
    private Item item = null;
    /**
     * The mode of this event
     */
    private boolean add = true;

    /**
     * Constructs the event as instance of the <code>CoordinateEvent</code> class, set the dragging item
     * and tells that it work in adding regime.
     *
     * @param item The dragging item over
     * @see cz.green.event.CoordinateEvent#CoordinateEvent(int, int, java.awt.Component)
     */
    private AddIdentificationDependencyEvent(int x, int y, Item item, java.awt.Component comp) {
        super(x, y, comp);
        this.item = item;
    }

    /**
     * Constructs the event as instance of the <code>CoordinateEvent</code> class, set the dragging item
     * and the reggime.
     *
     * @param item The dragging item over
     * @param add  Determines the regime of the event - if <ccode>tru</code> then the regime is adding.
     * @see cz.green.event.CoordinateEvent#CoordinateEvent(int, int, java.awt.Component)
     */
    public AddIdentificationDependencyEvent(int x, int y, Item item, java.awt.Component comp, boolean add) {
        this(x, y, item, comp);
        this.add = add;
    }

    /**
     * Constructs the event as instance of the <code>CoordinateEvent</code> class, set the dragging item
     * and tells that it work in adding regime. All needed values to construct the ancestor takes from the
     * <code>event</code> parameter.
     *
     * @param event The instance of the <code>CoordinateEvent</code> that holds all values needed to
     *              sets the atributes inherited from ancestor.
     * @param item  The dragging item over
     * @see cz.green.event.CoordinateEvent#CoordinateEvent(int, int, java.awt.Component)
     */
    public AddIdentificationDependencyEvent(cz.green.event.CoordinateEvent event, Item item) {
        this(event.getX(), event.getY(), item, event.getComponent());
    }

    /**
     * Constructs the event as instance of the <code>CoordinateEvent</code> class, set the dragging item
     * and regime. All needed values to construct the ancestor takes from the
     * <code>event</code> parameter.
     *
     * @param event The instance of the <code>CoordinateEvent</code> that holds all values needed to
     *              sets the atributes inherited from ancestor.
     * @param item  The dragging item over
     * @param add   Determines the regime of the event - if <ccode>tru</code> then the regime is adding.
     * @see cz.green.event.CoordinateEvent#CoordinateEvent(int, int, java.awt.Component)
     */
    public AddIdentificationDependencyEvent(cz.green.event.CoordinateEvent event, Item item, boolean add) {
        this(event.getX(), event.getY(), item, event.getComponent(), add);
    }

    /**
     * Gets the regime of the event.
     *
     * @return <code>true</code> whether the regime is adding.
     */
    public boolean getAdd() {
        return add;
    }

    /**
     * Gets the item that is dragging over the receiver of the event.
     *
     * @return The dragging item.
     */
    public Item getItem() {
        return item;
    }

    /**
     * Sets the regime of the event.
     *
     * @param add <code>true</code> whether the regime is adding.
     */
    public void setAdd(boolean add) {
        this.add = add;
    }

    /**
     * Sets the item that is dragging over the receiver of the event.
     *
     * @param item The dragging item.
     */
    public void setItem(Item item) {
        this.item = item;
    }

    /**
     * Converts this item to the string, representing all atributes.
     */
    public String toString() {
        StringBuffer message = new StringBuffer(getClass().getName());
        message.append("[").append(getX()).append(",").append(getY()).append("],add=").append(add);
        message.append(",component=").append(getComponent()).append(",item=").append(item);
        message.append(",@").append(Integer.toHexString(hashCode()));
        return new String(message);
}
}
