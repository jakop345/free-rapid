package cz.felk.cvut.erm.event;

/**
 * This event informs item about removing it from manager.
 */
public class RemoveEvent extends CoordinateEvent {
    /**
     * The default constructor for creating the event.
     *
     * @see CoordinateEvent#CoordinateEvent(int, int, java.awt.Component)
     */
    public RemoveEvent(int x, int y, java.awt.Component comp) {
        super(x, y, comp);
    }
}
