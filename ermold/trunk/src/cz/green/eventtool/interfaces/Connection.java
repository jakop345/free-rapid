package cz.green.eventtool.interfaces;

import cz.green.event.interfaces.Item;

/**
 * Interface for connections. The object that are connected to two instances of the interface
 * <code>Connectable</code>. Gives many important functions to the connectable windows.
 *
 * @see Connectable
 */
public interface Connection extends Item, Printable {
    /**
     * Disconnects itself from connectable window and before it disconnects both
     * connectable windows from this connection using method <code>removeConnection</code>.
     *
     * @see Connectable#removeConnection(Connection)
     */
    void disconnect();

    /**
     * Gets the first connected connectable window.
     *
     * @return The connectable window.
     */
    Connectable getOne();

    /**
     * Gets the second connected connectable window.
     *
     * @return The connectable window.
     */
    Connectable getTwo();

    /**
     * This method was created by Jiri Mares
     *
     * @param cls java.lang.Class
     * @return java.lang.Object
     */
    Object isConnectedTo(Class cls);

    /**
     * This method repaints the stored bounds. This bounds occupy this connection
     * before moving or resizing one of the connected window.
     *
     * @see #saveBounds()
     */
    void repaintStoredBounds();

    /**
     * Save current bounds to subsequent repainting.
     *
     * @see #repaintStoredBounds()
     */
    void saveBounds();

    /**
     * Sets the first connected connectable window. It also contain disconnecting
     * the old first window and recounting all properties.
     *
     * @param one The connectable window.
     */
    void setOne(Connectable one);

    /**
     * Sets the second connected connectable window. It also contain disconnecting
     * the old second window and recounting all properties.
     *
     * @param two The connectable window.
     */
    void setTwo(Connectable two);
}
