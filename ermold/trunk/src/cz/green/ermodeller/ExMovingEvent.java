package cz.green.ermodeller;

/**
 * This class extends the <code>MovingEvent</code> from the package <code>cz.green.event</code>.
 * The reason for existence this class is in ER modeller. When many things are made
 * by dragging one item to other. This operation should be differentiate from normal items moving.
 */
class ExMovingEvent extends ExMoveEvent {
    /**
     * Only invokes the inherited constructor
     *
     * @see ExMoveEvent#ExMoveEvent(int, int, int, int, java.awt.Component, boolean)
     */
    public ExMovingEvent(int x, int y, int dx, int dy, java.awt.Component comp, boolean move) {
        super(x, y, dx, dy, comp, move);
    }
}
