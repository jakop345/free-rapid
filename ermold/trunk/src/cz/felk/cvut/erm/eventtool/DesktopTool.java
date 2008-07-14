package cz.felk.cvut.erm.eventtool;

import cz.felk.cvut.erm.event.ContainerComponent;
import cz.felk.cvut.erm.event.DesktopGroupWindow;
import cz.felk.cvut.erm.event.exceptions.ImpossibleNegativeValueException;
import cz.felk.cvut.erm.event.exceptions.ItemNotInsideManagerException;
import cz.felk.cvut.erm.event.exceptions.ValueOutOfRangeException;
import cz.felk.cvut.erm.event.interfaces.Item;
import cz.felk.cvut.erm.eventtool.interfaces.Connection;
import cz.felk.cvut.erm.eventtool.interfaces.ConnectionManager;
import cz.felk.cvut.erm.eventtool.interfaces.Printable;

/**
 * This class has the same functionality as predecessor. Adds only one methods, which is caused by implementing the
 * interface <code>Printable</code>. Implementing this interface caused the possibility to be printed.
 */
public class DesktopTool extends DesktopGroupWindow implements ConnectionManager, Printable {
    /**
     * Group with connections
     */
    protected ConnectionGroupTool connections = null;

    /**
     * Calls the derived constructor.
     *
     * @see cz.felk.cvut.erm.event.DesktopGroupWindow#DesktopGroupWindow(cz.felk.cvut.erm.event.ContainerComponent , int, int, int, int)
     */
    public DesktopTool(ContainerComponent place, int left, int top, int width, int height) {
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
            connections.addItem(conn);
        } catch (ItemNotInsideManagerException e) {
        }
    }

    /**
     * Adds new connection to the connection group. Calls <code>addConnection</code>.
     *
     * @param conn The added connection.
     * @see #addConnection(cz.felk.cvut.erm.ermodeller.Connection)
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
        float scale = (manager).getScale();
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
     * @see #removeConnection(cz.felk.cvut.erm.ermodeller.Connection)
     */
    public void removeConnectionFromMain(Connection conn) {
        removeConnection(conn);
    }
}
