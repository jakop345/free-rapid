package cz.green.eventtool;

import cz.green.event.exceptions.ImpossibleNegativeValueException;
import cz.green.event.exceptions.ItemNotInsideManagerException;
import cz.green.event.exceptions.ValueOutOfRangeException;
import cz.green.event.interfaces.Item;
import cz.green.event.interfaces.PaintableManager;

/**
 * This class has the same functionality as predecessor. Adds only one methods, which is caused by implementing the
 * interface <code>Printable</code>. Implementing this interface caused the possibility to be printed.
 */
public class Desktop extends cz.green.event.Desktop implements ConnectionManager, Printable {
    /**
     * Group with connections
     */
    protected ConnectionGroupTool connections = null;

    /**
     * Calls the derived constructor.
     *
     * @see cz.green.event.Desktop#Desktop(cz.green.event.Container, int, int, int, int)
     */
    public Desktop(cz.green.event.Container place, int left, int top, int width, int height) {
        super(place, left, top, width, height);
    }

    /**
     * Adds new connection to the connection group.
     *
     * @param conn The added connection.
     */
    public void addConnection(Connection conn) {
        if (connections == null) {
            try {
                connections = new ConnectionGroupTool(manager, 0, 0, 0, 0);
            } catch (ImpossibleNegativeValueException e) {
                //when execution is here -> it is very bad
                return;
            }
        }
        try {
            connections.add(conn);
        } catch (ItemNotInsideManagerException e) {
        }
    }

    /**
     * Adds new connection to the connection group. Calls <code>addConnection</code>.
     *
     * @param conn The added connection.
     * @see #addConnection(cz.green.ermodeller.Connection)
     */
    public void addConnectionToMain(Connection conn) {
        addConnection(conn);
    }

    /**
     * Returns rectangle which specifies the size where lies all elements.
     *
     * @see PaintableItem#getBounds()
     */
    public java.awt.Rectangle getPrintBounds() {
        int[][] r = rect;
        if (wins.size() <= 0) {
            return new java.awt.Rectangle((int) (r[0][0] / scale), (int) (r[1][0] / scale), (int) ((r[0][0] + 1) / scale), (int) ((r[1][0] + 1) / scale));
        }
        Item i = (Item) wins.last();
        int right = rect[0][1], bottom = rect[1][1];
        try {
            right = i.mostRight(0);
            bottom = i.mostRight(1);
        } catch (ValueOutOfRangeException e) {
        }
        float scale = ((PaintableManager) manager).getScale();
        return new java.awt.Rectangle((int) (r[0][0] / scale), (int) (r[1][0] / scale), (int) ((right - r[0][0]) / scale), (int) ((bottom - r[1][0]) / scale));
    }

    /**
     * Prints the desktop and all inserted groups and windows. Exists because of implementing the interface Printable.
     *
     * @see Printable#print(java.awt.Graphics)
     */
    public void print(java.awt.Graphics g) {
        //paint all included element
        java.util.Enumeration e = wins.elements();
        while (e.hasMoreElements()) {
            try {
                ((Printable) e.nextElement()).print(g);
            } catch (Exception ex) {
            }
        }
    }

    /**
     * Removes connection from the connection group.
     *
     * @param conn The removing connection.
     */
    public void removeConnection(Connection conn) {
        if (connections != null) {
            connections.remove(conn);
        }
    }

    /**
     * Removes connection from the connection group. Calls <code>removeConnection</code>.
     *
     * @param conn The removing connection.
     * @see #removeConnection(cz.green.ermodeller.Connection)
     */
    public void removeConnectionFromMain(Connection conn) {
        removeConnection(conn);
    }
}
