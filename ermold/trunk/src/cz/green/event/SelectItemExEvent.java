package cz.green.event;

/**
 * Handle to this event caused selection only the item or its removing from selection list.
 */
public class SelectItemExEvent extends CoordinateEvent {
    /**
     * Determine, whether the receiver this event is removed from selected
     * items list or is selected only alone - creates the new list.
     */
    protected boolean addItem = false;

    /**
     * Default constructor.
     *
     * @param <code>addItem</code> Determine to remove item from all selected,
     *                             or select only this item.
     * @see CoordinateEvent#CoordinateEvent(int, int, java.awt.Component)
     */
    public SelectItemExEvent(int x, int y, boolean addItem, java.awt.Component comp) {
        super(x, y, comp);
        this.addItem = addItem;
    }

    /**
     * This method return valuo of propery addItem.
     *
     * @return The value of the property.
     * @see SelectItemExEvent.addItem
     */
    public boolean getAddItem() {
        return addItem;
}
}
