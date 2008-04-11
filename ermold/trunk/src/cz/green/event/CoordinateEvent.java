package cz.green.event;

/**
 * This is abstract class for all events, that have to know
 * coordinates, where the event appeared.
 */
public abstract class CoordinateEvent implements Event {
    /**
     * The x coordinate of the event.
     */
    private int x = 0;
    /**
     * The y coordinate of the event.
     */
    private int y = 0;
    /**
     * Component, where we can change cursor or do something else.
     */
    private java.awt.Component component = null;

    /**
     * Constructs <code>CoordinateEvent</code>.
     *
     * @param <code>x</code> The X coordinate of the event.
     * @param <code>x</code> The Y coordinate of the event.
     * @param comp           The new value for component property.
     * @see #component
     */
    public CoordinateEvent(int x, int y, java.awt.Component comp) {
        super();
        this.x = x;
        this.y = y;
        this.component = comp;
    }

    /**
     * This method returns the stored component.
     *
     * @return The stored component.
     * @see MouseMoveEvent.component
     */
    public java.awt.Component getComponent() {
        return component;
    }

    /**
     * Returns the x event coordinate.
     *
     * @return The coordinate.
     * @see CoordinateEvent.x
     */
    public int getX() {
        return x;
    }

    /**
     * Returns the y event coordinate.
     *
     * @return The coordinate.
     * @see CoordinateEvent.y
     */
    public int getY() {
        return y;
    }

    /**
     * This method determine whether the event will be passed to focused item or not.
     *
     * @see Event#passToFocused()
     */
    public boolean passToFocused() {
        return false;
    }

    /**
     * This method determine whether the event will be passed to all selected items or not.
     *
     * @see Event#passToSelected()
     */
    public boolean passToSelected() {
        return false;
    }
}
