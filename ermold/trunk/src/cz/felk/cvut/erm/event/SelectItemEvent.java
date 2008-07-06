package cz.felk.cvut.erm.event;

/**
 * Handle to this event caused the selection of the item or adding it to selection list.
 */
public class SelectItemEvent extends SelectItemExEvent {
    /**
     * Gives information whether the item was selected or not. Useful
     * when we process the <code>SelectItemExEvent</code>
     *
     * @see SelectItemExEvent
     */
    protected boolean selected = false;

    /**
     * Default constructor.
     *
     * @see CoordinateEvent#CoordinateEvent(int, int, java.awt.Component)
     */
    public SelectItemEvent(int x, int y, boolean add, java.awt.Component comp) {
        super(x, y, add, comp);
    }

    /**
     * Returns the value of property selected.
     *
     * @return The value of the property.
     * @see SelectItemEvent.selected
     */
    public boolean getSelected() {
        return selected;
    }

    /**
     * This method set the new value the property selected.
     *
     * @param sel The new value for the property.
     * @see SelectItemEvent.selected
     */
    public void setSelected(boolean sel) {
        this.selected = sel;
    }
}
