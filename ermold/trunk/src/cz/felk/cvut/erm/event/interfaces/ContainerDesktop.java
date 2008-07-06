package cz.felk.cvut.erm.event.interfaces;

import cz.felk.cvut.erm.event.ResizeRectangle;

/**
 * This method set the desktop scale and position to can see all selected items.
 */
public interface ContainerDesktop extends Manager {
    /**
     * This method set the desktop scale and position to can see all selected items.
     */
    void fitSelected();

    /**
     * This method tests if the event is in some resize rectangle.
     * Useful when we have to know, whether we have to do resizing or
     * moving. When we are over some resize point we have to do Resizing.
     *
     * @param <code>x</code> The x coordinate the mouse position.
     * @param <code>y</code> The y coordinate the mouse position.
     * @return The resize point, where the event is situated or <code>null</code>.
     */
    ResizeRectangle getActualResizeRect(int x, int y);

    /**
     * This method set the new actual scale and returns it.
     *
     * @param <code>scale</code> The new actual scale.
     * @return Actual scale.
     * @see PaintableManager#getScale()
     */
    float setScale(float scale);

}
