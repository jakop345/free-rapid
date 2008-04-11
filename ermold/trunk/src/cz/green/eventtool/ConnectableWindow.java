package cz.green.eventtool;

import cz.green.event.exceptions.ImpossibleNegativeValueException;
import cz.green.event.exceptions.ItemNotInsideManagerException;
import cz.green.event.interfaces.Manager;
import cz.green.event.interfaces.PaintableManager;

import java.util.Vector;

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
    protected Vector connections = null;
    /**
     * Determine whether the connections are already painted using paintFast method
     */
    private transient boolean connPaintedFast = false;

    /**
     * The same functionality as inhereted constructor and constructs the connections.
     *
     * @see cz.green.eventtool.Window#Window(cz.green.event.interfaces.Manager , int, int, int, int)
     * @see java.util.Vector
     */
    protected ConnectableWindow(Manager manager, int left, int top, int width, int height) throws NullPointerException, ImpossibleNegativeValueException {
        super(manager, left, top, width, height);
        connections = new Vector(5, 3);
    }

    /**
     * Add connection to all connected connections. This window is to it connected.
     *
     * @param conn The new connected connection.
     * @see Connectable#addConnection(cz.green.ermodeller.Connection)
     */
    public void addConnection(Connection conn) {
        connections.addElement(conn);
    }

    /**
     * Looks in all connected connections for connection to the specified object.
     *
     * @param object The object to which we look the connection.
     * @return Return the connection to the <code>object</code> or whether doesn't exist returns
     *         <code>null</code>.
     */
    public Connection connectionTo(Object object) {
        java.util.Enumeration e = connections.elements();
        while (e.hasMoreElements()) {
            Connection c = ((Connection) e.nextElement());
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
    void disconnectAll() {
        while (connections.size() != 0) {
            ((Connection) connections.elementAt(0)).disconnect();
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
    void finishConnFastRepaint(boolean recount) {
        PaintableManager m = (PaintableManager) manager;
        java.util.Enumeration e;
        e = connections.elements();
        if (recount) {
            while (e.hasMoreElements()) {
                Connection c = ((Connection) e.nextElement());
                connPaintedFast = false;
                c.repaintStoredBounds();
                m.repaintItem(c);
            }
        } else {
            while (e.hasMoreElements()) {
                Connection c = ((Connection) e.nextElement());
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
     * @see Connectable#getCenter()
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
     * @see Connectable#getRealBorder(java.awt.Point)
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
     * @see Connectable#getRealCenter()
     */
    public java.awt.Point getRealCenter() {
        java.awt.Rectangle r = getRealBounds();
        return new java.awt.Point(r.x + r.width / 2, r.y + r.height / 2);
    }

    /**
     * When cursor is moved over set the default cursor.
     */
    public void handleMouseMoveEvent(cz.green.event.MouseMoveEvent event) {
        event.getComponent().setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
    }

    /**
     * Disconnects all connections and calls inherited handler.
     *
     * @see #disconnectAll()
     */
    public void handleRemoveEvent(cz.green.event.RemoveEvent event) {
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
        connections.removeElement(conn);
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
        PaintableManager m = (PaintableManager) manager;
        java.util.Enumeration e;
        e = connections.elements();
        if (connPaintedFast) {
            if (connPaintedFast)
                while (e.hasMoreElements()) {
                    m.repaintItemFast((Connection) e.nextElement());
                }
        } else {
            while (e.hasMoreElements()) {
                ((Connection) e.nextElement()).saveBounds();
                connPaintedFast = true;
		}
	}
}
}
