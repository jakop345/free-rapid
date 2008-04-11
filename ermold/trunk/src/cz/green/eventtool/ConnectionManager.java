package cz.green.eventtool;

/**
 * All used groups and desktops with schema using connection hould implements.
 */
public interface ConnectionManager {
    /**
     * Adds connection to the special connection group.
     *
     * @param conn The added connection.
     */
    void addConnection(Connection conn);

    /**
     * Adds connection to the special desctop connection group.
     *
     * @param conn The added connection.
     */
    void addConnectionToMain(Connection conn);

    /**
     * Removes connection from the special connection group.
     *
     * @param conn The removing connection.
     */
    void removeConnection(Connection conn);

    /**
     * Removes connection from the special desctop connection group.
     *
     * @param conn The removing connection.
     */
    void removeConnectionFromMain(Connection conn);
}
