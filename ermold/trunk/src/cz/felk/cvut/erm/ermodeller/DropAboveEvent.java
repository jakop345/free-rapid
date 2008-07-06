package cz.felk.cvut.erm.ermodeller;

import cz.felk.cvut.erm.event.interfaces.Item;

/**
 * Has the same meaning as <code>DragOverEvent</code> but informs about dropping the item above
 * the receiver.
 * <p/>
 * To the invoker carry information about the dropping has been made or not.
 */
public class DropAboveEvent extends DragOverEvent {
    /**
     * When receiver do some opertion with drpped item then sets this
     * atribute to <code>true</code>
     */
    protected boolean dropped = false;

    /**
     * Same as inherited one.
     *
     * @see DragOverEvent#DragOverEvent(int, int, cz.felk.cvut.erm.event.interfaces.Item , java.awt.Component)
     */
    public DropAboveEvent(int x, int y, Item item, java.awt.Component comp) {
        super(x, y, item, comp);
    }

    /**
     * Same as inherited one.
     *
     * @see DragOverEvent#DragOverEvent(int, int, cz.felk.cvut.erm.event.interfaces.Item , java.awt.Component, boolean)
     */
    public DropAboveEvent(int x, int y, Item item, java.awt.Component comp, boolean add) {
        super(x, y, item, comp, add);
    }

    /**
     * Same as inherited one.
     *
     * @see DragOverEvent#DragOverEvent(cz.felk.cvut.erm.event.CoordinateEvent, cz.felk.cvut.erm.event.interfaces.Item)
     */
    public DropAboveEvent(cz.felk.cvut.erm.event.CoordinateEvent event, Item item) {
        super(event, item);
    }

    /**
     * Same as inherited one.
     *
     * @see DragOverEvent#DragOverEvent(cz.felk.cvut.erm.event.CoordinateEvent, cz.felk.cvut.erm.event.interfaces.Item)
     */
    public DropAboveEvent(cz.felk.cvut.erm.event.CoordinateEvent event, Item item, boolean add) {
        super(event, item, add);
    }

    /**
     * Gets the value of the <code>dropped</code> atribute.This method was created by Jiri Mares
     *
     * @return Determines, whether the drop item was used to do some action (<code>true</code>)
     *         or not (<code>false</code>).
     * @see #dropped
     */
    public boolean getDropped() {
        return dropped;
    }

    /**
     * Sets the value of the <code>dropped</code> atribute.This method was created by Jiri Mares
     *
     * @param dropped Whwnn receiver has made some action with item the set this atribute to
     *                <code>true</code>.
     * @see #dropped
     */
    public void setDropped(boolean dropped) {
        this.dropped = dropped;
    }

    /**
     * Converts this item to the string, representing all atributes.
     */
    public String toString() {
        StringBuffer message = new StringBuffer(getClass().getName());
        message.append("[").append(getX()).append(",").append(getY()).append("],add=").append(add);
        message.append(",dropped=").append(dropped);
        message.append(",component=").append(getComponent()).append(",item=").append(item);
        message.append(",@").append(Integer.toHexString(hashCode()));
        return new String(message);
    }
}
