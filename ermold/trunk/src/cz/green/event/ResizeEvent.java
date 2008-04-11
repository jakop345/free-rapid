package cz.green.event;

/**
 * Informs about end of resizing. Carry information about <code>ResizeRectangle</code>
 * which is the start of the resizing.
 *
 * @see ResizeRectangle
 */
public class ResizeEvent extends MoveEvent {
    /**
     * Which resize rectangle is used for resizing.
     *
     * @see ResizeRectangle
     */
    private ResizeRectangle resizeRect = null;

    /**
     * Constructs <code>ResizeEvent</code>.
     *
     * @param <code>r</code> <code>ResizeRectangle</code>, that is used to change size the item.
     * @see MoveEvent#MoveEvent(int, int, int, int, java.awt.Component)
     * @see ResizeRectangle
     */
    public ResizeEvent(int x, int y, int dx, int dy, ResizeRectangle r, java.awt.Component comp) {
        super(x, y, dx, dy, comp);
        this.resizeRect = r;
    }

    /**
     * Returns the resize rectangle used for resizing.
     *
     * @return This rectangle carry information about resizing.
     */
    public ResizeRectangle getResizeRect() {
        return resizeRect;
    }

    /**
     * This method determine whether the event will be passed to focused item or not.
     *
     * @see cz.green.event.interfaces.Event#passToFocused()
     */
    public boolean passToFocused() {
        return true;
    }

    /**
     * Sets the <code>resizeRect</code>.
     *
     * @see ResizeRectangle
     */
    public void setResizeRect(ResizeRectangle r) {
        resizeRect = r;
}
}
