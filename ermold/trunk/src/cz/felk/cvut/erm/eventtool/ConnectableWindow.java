package cz.felk.cvut.erm.eventtool;

import cz.felk.cvut.erm.event.exceptions.ImpossibleNegativeValueException;
import cz.felk.cvut.erm.event.exceptions.ItemNotInsideManagerException;
import cz.felk.cvut.erm.event.interfaces.Manager;
import cz.felk.cvut.erm.event.interfaces.PaintableManager;
import cz.felk.cvut.erm.eventtool.interfaces.Connectable;
import cz.felk.cvut.erm.eventtool.interfaces.Connection;

import java.awt.*;
import java.util.ArrayList;

/**
 * Creates all needed functionality for connectable windows. Invokes repainting function to all connections,
 * save bounds and invokes the method for repainting stored bounds.
 * When removing window, removes all connected connections.
 * <p/>
 * Another functionality is the sending the DragOverEvent and DropAboveEvent, when the ConnectableWindow is moving.
 * This methods caused needed creating only handlers for DragOverEvent and DropAboveEvent.
 */
public class ConnectableWindow extends Window implements Connectable {
    /**
     * This vector holds all connections connected to this window
     */
    protected java.util.List<Connection> connections = null;
    /**
     * Determine whether the connections are already painted using paintFast method
     */
    transient protected boolean connPaintedFast = false;

    /**
     * The same functionality as inhereted constructor and constructs the connections.
     *
     * @see cz.felk.cvut.erm.eventtool.Window#WindowItem(cz.felk.cvut.erm.event.interfaces.Manager , int, int, int, int)
     * @see java.util.Vector
     */
    public ConnectableWindow(Manager manager, int left, int top, int width, int height) throws NullPointerException, ImpossibleNegativeValueException {
        super(manager, left, top, width, height);
        connections = new ArrayList<Connection>(5);
    }

    /**
     * Add connection to all connected connections. This window is to it connected.
     *
     * @param conn The new connected connection.
     * @see Connectable#addConnection(cz.felk.cvut.erm.ermodeller.Connection)
     */
    public void addConnection(Connection conn) {
        connections.add(conn);
    }

    /**
     * Looks in all connected connections for connection to the specified object.
     *
     * @param object The object to which we look the connection.
     * @return Return the connection to the <code>object</code> or whether doesn't exist returns
     *         <code>null</code>.
     */
    public Connection connectionTo(Object object) {
        for (Connection c : connections) {
            if ((c.getOne() == object) || (c.getTwo() == object))
                return c;
        }
        return null;
    }

    /**
     * Disconnect all connection from this window. Invokes to all connections method <code>disconnect</code>.
     *
     * @see Connection#disconnect()
     */
    public void disconnectAll() {
        while (!connections.isEmpty()) {
            connections.get(0).disconnect();
        }
    }

    /**
     * Is invoken after moving or resizing this object. Determine whether only repaint (using
     * <code>paintFast</code>) all connections or repaint and also repaints stored bounds all
     * connections.
     *
     * @param recount If it is <code>true</code> then connection are repaint and are also
     *                repainted its stored bounds, if <code>false</code> thay are only repainted.
     */
    protected void finishConnFastRepaint(boolean recount) {
        PaintableManager m = manager;
        if (recount) {
            for (Connection c : connections) {
                connPaintedFast = false;
                c.repaintStoredBounds();
                m.repaintItem(c);
            }
        } else {
            for (Connection c : connections) {
                m.repaintItemFast(c);
            }
        }
    }

    /**
     * Counts border point from the point <code>direction</code> to the rectangle border.
     *
     * @param direction The center of the other connected element.
     * @return The border.
     * @see Connectable#getBorder(java.awt.Point)
     */
    public java.awt.Point getBorder(java.awt.Point direction) {
        java.awt.Point center = getCenter();
        java.awt.Rectangle r = getBounds();
        int x2 = r.width / 2;
        int y2 = r.height / 2;
        int x = direction.x - center.x;
        int y = direction.y - center.y;
        int rx, ry;
        if ((x != 0) && (y != 0)) {
            if (x < 0)
                x = -x;
            if (y < 0)
                y = -y;
            ry = (x2 * y) / x;
            if (ry > y2) {
                rx = (y2 * x) / y;
                ry = y2;
            } else
                rx = x2;
        } else {
            rx = (x == 0) ? 0 : x2;
            ry = (y == 0) ? 0 : y2;
        }
        center.x += (center.x > direction.x) ? -rx : rx + 1;
        center.y += (center.y > direction.y) ? -ry : ry + 1;
        return center;
    }

    /**
     * Counts the center point of the rectangle.
     *
     * @return The center.
     * @see cz.felk.cvut.erm.eventtool.interfaces.Connectable#getCenter()
     */
    public java.awt.Point getCenter() {
        java.awt.Rectangle r;
        if (hRect != null)
            r = getRealBounds();
        else
            r = getBounds();
        return new java.awt.Point(r.x + r.width / 2, r.y + r.height / 2);
    }

    /**
     * Counts border point from the point <code>direction</code> to the rectangle border.
     * Same as <code>getBorder</code> but uses method <code>getRealBound</code> instead
     * <code>getBound</code>.
     *
     * @param direction The center of the other connected element.
     * @return The real border.
     * @see cz.felk.cvut.erm.eventtool.interfaces.Connectable#getRealBorder(java.awt.Point)
     */
    public java.awt.Point getRealBorder(java.awt.Point direction) {
        java.awt.Point center = getRealCenter();
        java.awt.Rectangle r = getRealBounds();
        int x2 = r.width / 2;
        int y2 = r.height / 2;
        int x = direction.x - center.x;
        int y = direction.y - center.y;
        int rx, ry;
        if ((x != 0) && (y != 0)) {
            if (x < 0)
                x = -x;
            if (y < 0)
                y = -y;
            ry = (x2 * y) / x;
            if (ry > y2) {
                rx = (y2 * x) / y;
                ry = y2;
            } else
                rx = x2;
        } else {
            rx = (x == 0) ? 0 : x2;
            ry = (y == 0) ? 0 : y2;
        }
        center.x += (center.x > direction.x) ? -rx : rx + 1;
        center.y += (center.y > direction.y) ? -ry : ry + 1;
        return center;
    }

    /**
     * Counts the center point of the rectangle.
     * Same as <code>getCenter</code> but uses method <code>getRealBound</code> instead
     * <code>getBound</code>.
     *
     * @return The real center.
     * @see cz.felk.cvut.erm.eventtool.interfaces.Connectable#getRealCenter()
     */
    public java.awt.Point getRealCenter() {
        java.awt.Rectangle r = getRealBounds();
        return new java.awt.Point(r.x + r.width / 2, r.y + r.height / 2);
    }

    /**
     * When cursor is moved over set the default cursor.
     */
    public void handleMouseMoveEvent(cz.felk.cvut.erm.event.MouseMoveEvent event) {
        event.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    /**
     * Disconnects all connections and calls inherited handler.
     *
     * @see #disconnectAll()
     */
    public void handleRemoveEvent(cz.felk.cvut.erm.event.RemoveEvent event) {
        disconnectAll();
        super.handleRemoveEvent(event);
    }

    /**
     * When this window is moved by manager it also have to repaint, store bounds and repaint stored bounds
     * all connected connections. For this aim uses <code>startConnFastRepaint</code> and
     * <code>finishConnFastRepaint</code>
     *
     * @see #startConnFastRepaint()
     * @see #finishConnFastRepaint(boolean)
     */
    public void managerMoves(int dx, int dy, boolean recount) {
        startConnFastRepaint();
        super.managerMoves(dx, dy, recount);
        finishConnFastRepaint(recount);
    }

    /**
     * When this window is moved it also have to repaint, store bounds and repaint stored bounds
     * all connected connections. For this aim uses <code>startConnFastRepaint</code> and
     * <code>finishConnFastRepaint</code>
     *
     * @see #startConnFastRepaint()
     * @see #finishConnFastRepaint(boolean)
     */
    public void move(int dx, int dy, boolean recount) throws ItemNotInsideManagerException {
        startConnFastRepaint();
        super.move(dx, dy, recount);
        finishConnFastRepaint(recount);
    }

    /**
     * Removes connection from connected connections.
     *
     * @param conn The removed connection.
     * @see Connectable#removeConnection(Connection)
     */
    public void removeConnection(Connection conn) {
        connections.remove(conn);
    }

    /**
     * When this window is resized it also have to repaint, store bounds and repaint stored bounds
     * all connected connections. For this aim uses <code>startConnFastRepaint</code> and
     * <code>finishConnFastRepaint</code>
     *
     * @see #startConnFastRepaint()
     * @see #finishConnFastRepaint(boolean)
     */
    public void resize(int dx, int dy, int where, boolean recount) throws ItemNotInsideManagerException {
        startConnFastRepaint();
        super.resize(dx, dy, where, recount);
        finishConnFastRepaint(recount);
    }

    /**
     * Is invoken before moving or resizing this object. Determine whether only repaint (using
     * <code>paintFast</code>) all connections or repaint and also store bounds all
     * connections. This decision is made according to the value of the <code>connPaintedFast</code>.
     *
     * @see #connPaintedFast
     */
    protected void startConnFastRepaint() {
        PaintableManager m = manager;
        if (connPaintedFast) {
            for (Connection c : connections) {
                m.repaintItemFast(c);
            }
        } else {
            for (Connection c : connections) {
                c.saveBounds();
                connPaintedFast = true; //TODO nastavovani v cyklu
            }
        }
    }
}
