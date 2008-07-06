package cz.felk.cvut.erm.event;

/**
 * Informs about moving the mouse over.
 */
public class MouseMoveEvent extends CoordinateEvent {
    /**
     * Constructs the event, with all specified information.
     *
     * @see CoordinateEvent#CoordinateEvent(int, int, java.awt.Component)
     */
    public MouseMoveEvent(int x, int y, java.awt.Component comp) {
        super(x, y, comp);
    }
}
