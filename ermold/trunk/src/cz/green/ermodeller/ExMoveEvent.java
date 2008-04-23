package cz.green.ermodeller;

import cz.green.event.MovingEvent;

/**
 * This class extends the <code>MoveEvent</code> from the package <code>cz.green.event</code>.
 * The reason for existence this class is in ER modeller. When many things are made
 * by dragging one item to other. This operation should be differentiate from normal items moving.
 */
public class ExMoveEvent extends MovingEvent {
    /**
     * Holds information that this event represents just moving (<code>true</code>) or
     * dragging (<code>false</code>)
     */
    protected boolean move = true;

    /**
     * Call the inherited conatructor and sets the new atribute.
     *
     * @param move Set that this events represent just moving (<code>true</code>)
     *             or dragging (<code>false</code>).
     * @see cz.green.event.MovingEvent#MovingEvent(int, int, int, int, java.awt.Component)
     */
    public ExMoveEvent(int x, int y, int dx, int dy, java.awt.Component comp, boolean move) {
        super(x, y, dx, dy, comp);
        this.move = move;
    }

    /**
     * Gets the value of the move <b>atribute</b>.
     *
     * @return The value of the move <b>atribute</b>.
     * @see #move
     */
    public boolean getMove() {
        return move;
    }

    /**
     * Sets the value of the move <b>atribute</b>.
     *
     * @param move The value of the move <b>atribute</b>.
     * @see #move
     */
    public void setMove(boolean move) {
        this.move = move;
    }
}
