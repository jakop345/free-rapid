package cz.green.eventtool;

import cz.green.event.exceptions.ImpossibleNegativeValueException;
import cz.green.event.exceptions.ItemNotInsideManagerException;
import cz.green.event.interfaces.Item;
import cz.green.event.interfaces.Manager;
import cz.green.event.interfaces.PaintableItem;
import cz.green.eventtool.interfaces.Printable;

/**
 * This group holds connections and all others elements that don't want to handle
 * event. According to the event fall mechanism it is not sets as reciver the events.
 * Everytimes it says that it is the potencional receiver, but the method <code>isIn</code>
 * return always <code>false</code>. It caused the event falling fron succesor according to the
 * z coordinate.
 * <p/>
 * All added elements hasn't to be in this group (according to the x and y coordinate),
 * because there is no event falling. It caused that adding method doesn't detect the
 * overlapping of this group by the added item.
 */
public class ConnectionGroupTool extends GroupTool {
    /**
     * Simply calls only inherited constructor.
     *
     * @see GroupTool#GroupTool(cz.green.event.interfaces.Manager , int, int, int, int)
     */
    public ConnectionGroupTool(Manager manager, int left, int top, int width, int height) throws NullPointerException, ImpossibleNegativeValueException {
        super(manager, left, top, width, height);
    }

    /**
     * Adds <code>item</code> to the top of this group (manager). Doesn't detect the overlapping this group
     * by added window.
     */
    public void add(Item item) throws ItemNotInsideManagerException {
        int size = wins.size();
        item.manager(this);
        wins.add(size, item);
    }

    /**
     * This group can't be resized = returns <code>null</code>.
     */
    public cz.green.event.ResizePoint[] getResizePoints() {
        return null;
    }

    /**
     * Always returns <code>false</code> to avoid being the receiver of the vent.
     */
    public boolean isIn(int x, int y) {
        return false;
    }

    /**
     * Paints all added elements, not itself.
     */
    public void paint(java.awt.Graphics g) {
        //paint all included element
        java.util.Enumeration e = wins.elements();
        while (e.hasMoreElements()) {
            ((PaintableItem) e.nextElement()).paint(g);
        }
    }

    /**
     * Paints all added elements, not itself.
     */
    public void paintFast(java.awt.Graphics g) {
        //paint all included element
        java.util.Enumeration e = wins.elements();
        while (e.hasMoreElements()) {
            ((PaintableItem) e.nextElement()).paintFast(g);
        }
    }

    /**
     * Prints all added elements, not itself.
     */
    public void print(java.awt.Graphics g) {
        //paint all included element
        java.util.Enumeration e = wins.elements();
        while (e.hasMoreElements()) {
            ((Printable) e.nextElement()).print(g);
        }
    }
}
