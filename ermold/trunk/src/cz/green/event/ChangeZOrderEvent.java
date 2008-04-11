package cz.green.event;

/**
 * This is event for removing to the top (bottom) the window.
 */
public class ChangeZOrderEvent extends CoordinateEvent {
    /**
     * This property carry the information where to move the window.
     * If <code>true</code> (<code>false</code>) the event caused moving
     * to the top (bottom).
     */
    private boolean top = true;

    /**
     * Constructs the event with specified graphics and direction of the moving item.
     *
     * @param <code>top</code> Set information where to move the window in Z direction.
     * @see CoordinateEvent#CoordinateEvent(int, int, java.awt.Component)
     */
    public ChangeZOrderEvent(int x, int y, java.awt.Component comp, boolean top) {
        super(x, y, comp);
        this.top = top;
    }

    /**
     * Return information, where to move the window (in Z direction).
     *
     * @return The Z order information.
     * @see ChangeZOrderEvent.top
     */
    public boolean getTop() {
        return top;
    }
}
