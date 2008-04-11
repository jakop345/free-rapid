package cz.green.event;

/**
 * This event informs that we are moving the item.
 */
public class MovingEvent extends MoveEvent {
    /**
     * Constructs the event;
     *
     * @see MoveEvent#MoveEvent(int, int, int, int, java.awt.Component)
     */
    public MovingEvent(int x, int y, int dx, int dy, java.awt.Component comp) {
        super(x, y, dx, dy, comp);
    }
}
