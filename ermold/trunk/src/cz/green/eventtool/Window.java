package cz.green.eventtool;

import cz.green.event.WindowItem;
import cz.green.event.exceptions.ImpossibleNegativeValueException;
import cz.green.event.interfaces.Manager;
import cz.green.eventtool.interfaces.Printable;

/**
 * This class has the same functionality as predecessor. Adds only one methods, which is caused by implementing the
 * interface <code>Printable</code>. Implementing this interface caused the possibility to be printed.
 */
public class Window extends WindowItem implements Printable {
    /**
     * Is needful for creating desktop.
     *
     * @see cz.green.event.WindowItem#WindowItem()
     */
    protected Window() {
        super();
    }

    /**
     * Calls the derived constructor.
     *
     * @see cz.green.event.WindowItem#WindowItem(cz.green.event.interfaces.Manager , int, int, int, int)
     */
    public Window(Manager manager, int left, int top, int width, int height) throws NullPointerException, ImpossibleNegativeValueException {
        super(manager, left, top, width, height);
    }

    /**
     * Returns rectangle which specifies the size of this window.
     *
     * @see PaintableItem#getBounds()
     */
    public java.awt.Rectangle getPrintBounds() {
        return getBounds();
    }

    /**
     * Prints the window. Exists because of implementing the interface Printable.
     *
     * @see cz.green.eventtool.interfaces.Printable#print(java.awt.Graphics)
     */
    public void print(java.awt.Graphics g) {
        //paint window
        java.awt.Rectangle r = getBounds();
        g.drawRect(r.x, r.y, r.width, r.height);
    }
}
