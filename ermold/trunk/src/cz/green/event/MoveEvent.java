package cz.green.event;

/**
 * This class has necessary properties for moving the window.
 */
public class MoveEvent extends CoordinateEvent {
    /**
     * The difference in x coordinate.
     */
    private int dx = 0;
    /**
     * The difference in y coordinate.
     */
    private int dy = 0;

    /**
     * Constructs the move event.
     *
     * @param <code>dx</code> How many move in the x coordinate.
     * @param <code>dy</code> How many move in the y coordinate.
     * @see CoordinateEvent#CoordinateEvent(int, int, java.awt.Component)
     */
    public MoveEvent(int x, int y, int dx, int dy, java.awt.Component comp) {
        super(x, y, comp);
        this.dx = dx;
        this.dy = dy;
    }

    /**
     * Returns haw many have to move the window in x coordinate.
     *
     * @return The difference in x coordinate.
     */
    public int getDx() {
        return dx;
    }

    /**
     * Returns haw many have to move the window in y coordinate.
     *
     * @return The difference in y coordinate.
     */
    public int getDy() {
        return dy;
    }

    /**
     * This method determine whether the event will be passed to all selected items or not.
     *
     * @see cz.green.event.interfaces.Event#passToSelected()
     */
    public boolean passToSelected() {
        return true;
    }

    /**
     * Sets haw many have to move the window in x coordinate.
     */
    public void setDx(int dx) {
        this.dx = dx;
    }

    /**
     * Sets haw many have to move the window in y coordinate.
     */
    public void setDy(int dy) {
        this.dy = dy;
    }

    /**
     * Returns a String that represents the value of this object.
     *
     * @return a string representation of the receiver
     */
    public String toString() {
        StringBuffer message = new StringBuffer(getClass().getName());
        message.append("[").append(getX()).append(",").append(getY()).append("],move=[");
        message.append(dx).append(",").append(dy).append("],component=").append(getComponent());
        message.append(",@").append(Integer.toHexString(hashCode()));
        return new String(message);
    }
}
