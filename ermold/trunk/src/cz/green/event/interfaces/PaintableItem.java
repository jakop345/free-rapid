package cz.green.event.interfaces;

import cz.green.event.ResizePoint;

import java.awt.*;

/**
 * This interface should implement classes, that can be painted on display.
 * They can cooperate with the classes implementing <code>PaintableManager</code>,
 * which has many important function to display.
 *
 * @see PaintableManager
 */
public interface PaintableItem {
    /**
     * Returns where the items lies and how large is, during resing (moving) returns old position.
     * Old position according to the event tree state.
     *
     * @return The position and the size represented by
     *         <code>Rectangle</code>.
     * @see java.awt.Rectangle
     */
    Rectangle getBounds();

    /**
     * Returns where the items lies and how large is, and returns real position while moving (resizing).
     *
     * @return The position and the size represented by
     *         <code>Rectangle</code>.
     * @see java.awt.Rectangle
     */
    Rectangle getRealBounds();

    /**
     * This method return all points where should have be placed resize points.
     * By draging this point user resize the item. The position in item
     * rectangle is specified by relative logical coordinates. This coordinates has
     * interval from <code>0</code> to <code>1</code>.
     *
     * @return Array of all Recize points.
     * @see cz.green.event.ResizePoint
     */
    ResizePoint[] getResizePoints();

    /**
     * This method has to paint object on the specified graphics device
     * <code>g</code>.
     *
     * @param <code>g</code> Where to paint and how large.
     * @see java.awt.Graphics
     */
    void paint(Graphics g);

    /**
     * This method has to paint object on the specified graphics device
     * <code>g</code>, but this method is used only, when engine
     * expect often repainting this item. This methods should not
     * change color, because it caused wrong painting.
     *
     * @param <code>g</code> Where to paint and how large.
     * @see java.awt.Graphics
     */
    void paintFast(Graphics g);
}
