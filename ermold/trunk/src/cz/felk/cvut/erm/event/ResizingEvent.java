package cz.felk.cvut.erm.event;

/**
 * This is event passed during resizing.
 */
public class ResizingEvent extends ResizeEvent {
    /**
     * Constructs the resizing event.
     *
     * @see ResizeEvent#ResizeEvent(int, int, int, int, cz.felk.cvut.erm.event.ResizeRectangle, java.awt.Component)
     */
    public ResizingEvent(int x, int y, int dx, int dy, ResizeRectangle r, java.awt.Component comp) {
        super(x, y, dx, dy, r, comp);
    }
}
