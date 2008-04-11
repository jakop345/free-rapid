package cz.green.eventtool.interfaces;

/**
 * This interface have to implement all items, that have to be connectable. This items
 * are used by all connection objects.
 *
 * @see Connection
 */
public interface Connectable {
    /**
     * Adds the new connection to the connectable window. After this is the connection
     * repainted when it is needful.
     *
     * @param c The new connection.
     */
    void addConnection(Connection c);

    /**
     * Returns the border point of the connectable window. It's the intersection of the
     * border and the line during two points - direction and center of <code>this</code>.
     *
     * @param direction The point determing the direction of the intersection line.
     * @return The border point.
     */
    java.awt.Point getBorder(java.awt.Point direction);

    /**
     * Returns the connectable center of the connectable window.
     *
     * @return The point where the connection have to point (but don't have to reach).
     */
    java.awt.Point getCenter();

    /**
     * Same as <code>getCenter</code> but used during painting fast.
     *
     * @param direction The point determing the direction of the intersection line.
     * @see #getBorder(java.awt.Point)
     */
    java.awt.Point getRealBorder(java.awt.Point direction);

    /**
     * Same as <code>getCenter</code> but used during moving the window.
     *
     * @return The point where the connection have to point (but don't have to reach).
     * @see #getCenter()
     */
    java.awt.Point getRealCenter();

    /**
     * Removes the connection from the connectable window. After this is not the connection
     * repainted.
     *
     * @param c The removed connection.
     */
    void removeConnection(Connection c);
}
