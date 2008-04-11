package cz.green.event;

/**
 * This interface methods have to implement object, that woulds like to
 * be a manager for <code>EventTree2DItem</code>s.
 *
 * @see PaintableManager
 * @see MovableManager
 */
public interface Manager extends PaintableManager, MovableManager {
    /**
     * Adds <code>item</code> to this manager at the top. Manager should test
     * whether the <code>item</code> is in the rectangle of the manager.
     *
     * @param <code>item</code> Item that we adds.
     * @throws <code>cz.green.event.engine.ItemNotInsideManagerException</code>
     *          If the <code>item</code> lies out of this manager.
     */
    void add(Item item) throws ItemNotInsideManagerException;

    /**
     * This methods recounts all links <code>item</code> and all items over
     * according to the z coordinate. This methods uses items after moving etc.
     *
     * @param <code>item</code> The item from which we counts the links.
     */
    void countAllLinksToTop(Item item);

    /**
     * This manager method calls items <code>eventFall</code>,
     * than <code>isIn</code> and finaly
     * <code>invokeEventHandler</code> to handle events. If the <code>isIn</code>
     * returns <code>false</code> them the method
     * <code>eventFall</code> is called again and so on.
     *
     * @param <code>x</code>     coordinate the event.
     * @param <code>y</code>     coordinate the event.
     * @param <code>event</code> The event to handle.
     * @return Returns <code>true</code> if all was execute unrubly,
     *         else is returns <code>false</code>.
     * @see Item#eventFall(int, int)
     * @see Item#isIn(int, int)
     * @see Invokeable#invokeEventHandler(Event)
     */
    boolean fallAndHandleEvent(int x, int y, Event event);

    /**
     * This method moves window to top or bottom in this manager. Should
     * recount all proper links in the event tree structure.
     *
     * @param <code>item</code> Item that which we change Z order.
     * @param <code>top</code>  If <code>true</code> then
     *                          <code>item</code> is moved to top, if <code>false</code>
     *                          then is moved to bottom.
     */
    void changeZOrder(Item item, boolean top);

    /**
     * Is <code>item</code> at the bottom in this manager.
     *
     * @param <code>item</code> Is this object at the bottom?
     * @return <code>True</code> if the <code>item</code>
     *         is at the bottom.
     */
    boolean isBottom(Item item);

    /**
     * Is <code>item</code> at the top in this manager.
     *
     * @param <code>item</code> Is this object at the top?
     * @return <code>boolean</code> <code>True</code> if the <code>item</code>
     *         is at the top.
     */
    boolean isTop(Item item);

    /**
     * Removes <code>item</code> from this manager. The manager should recount
     * all proper links in the event tree structure.
     *
     * @param <code><b>item</b></code> Item that i have to remove.
     */
    void remove(Item item);
}
