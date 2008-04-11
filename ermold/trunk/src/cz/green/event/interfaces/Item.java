package cz.green.event;

/**
 * This interface methods have to implement all object in the event tree.
 * Contains methods need for inserting into event tree structure and other methods
 * specified by others interfaces.
 */
public interface Item extends SelectableItem, MovableItem {
    /**
     * Counts all links in event tree for this instance.
     *
     * @param <code>item</code> The next <code>item</code> in the actual manager order.
     */
    void countLinks(Item next);

    /**
     * This methods recounts links to objects <code>to</code>. It needs thenext object in the manager
     * (<code>item</code>).
     *
     * @param <code>item</code> The next <code>item</code> in the actual manager order.
     * @param <code>to</code>   links, which i have to recount are specified by pointing to object
     *                          <code>to</code>
     */
    void countLinksTo(Item next, Item to);

    /**
     * This method makes the falling of the event through event tree and return item,
     * where the event situated is.
     *
     * @param <code>x</code> x coordinate
     * @param <code>y</code> y coordinate
     * @return The item, where is situated by x and y. Is such item doesn't exists
     *         return <code>null</code>.
     */
    Item eventFall(int x, int y);

    /**
     * Looks if the <code>point</code> in the specified
     * <code>dimension</code> is in the window. Returns this item
     * if the <code>point</code> is in or return left (right) window
     * behind from event tree.
     *
     * @param <code>dimension</code> In which dimension we looks for position. <code>0</code>
     *                               (<code>1</code>) says we wonder to know in dimension
     *                               <code>x</code> (<code>y</code>).
     * @param <code>point</code>     This is one dimension of the event.
     * @return Returns this window if this window <code>point</code>
     *         contains, or the left (right) behind window. It can return also
     *         <code>null</code>, if the left (right) behind window
     *         doesn't exists.
     * @throws <code>cz.green.event.engine.ValueOutOfRangeException</code>
     *          If <code>dimension</code> is less then <code>0</code> or
     *          greater then <code>1</code>.
     */
    Item eventFallStep(int dimension, int point) throws ValueOutOfRangeException;

    /**
     * This methods asks instance if the event is in its shape.
     *
     * @param <code>x</code> x coordinate
     * @param <code>y</code> y coordinate
     * @return <code>boolean</code>
     *         Return <code>true</code> if the event is in the object. According to
     *         its shape.
     */
    boolean isIn(int x, int y);

    /**
     * Looks in the <code>dimension</code> of the event tree
     * for item that is situated in <code>where</code> according to the
     * <code>interval</code>. Searching starts from this item.
     * Additional information is <code>myPosition</code>. It enable
     * fast solving situation, that this window is the succesor.
     *
     * @param <code>dimension</code>  In which dimension we looks for position. <code>0</code>
     *                                (<code>1</code>) says we wonder to know in dimension
     *                                <code>x</code> (<code>y</code>).
     * @param <code>interval</code>   One dimensional array with 2 items, where
     *                                item <code>interval[0]</code> gives the beggining and item
     *                                <code>interval[1]</code> the end of the interval. According
     *                                to this interval, we says the position.
     * @param <code>where</code>      This value is one of constants <code>LEFT</code>,
     *                                <code>IN</code> or <code>RIGHT</code> from class
     *                                <code>cz.green.util.IntervalMethods</code>.
     * @param <code>myPosition</code> This item position according to <code>interval</code>.
     *                                Is composition of constants <code>LEFT</code>,
     *                                <code>IN</code> or <code>RIGHT</code> from class
     *                                <code>cz.green.util.IntervalMethods</code>.
     * @return Returns the specified item if exists or <code>null</code> if doesn't.
     * @throws <code>cz.green.util.BadDimensionException</code>
     *          If array <code>interval</code> has less than 2 items,
     *          on the other hand if the array <code>interval</code> has more
     *          items -> it doesn't matter.
     * @throws <code>cz.green.event.engine.ValueOutOfRangeException</code>
     *          If <code>dimension</code> is less then <code>0</code> or
     *          greater then <code>1</code>.
     * @see cz.green.util.IntervalMethods
     */
    Item itemIn(int dimension, int[] interval, int where, int myPosition) throws cz.green.util.BadDimensionException, ValueOutOfRangeException;

    /**
     * Returns the window, which lies left behind in the
     * <code>dimension</code>.
     *
     * @param <code>dimension</code> In which dimension we have to know left successor.
     * @return Window lieing left behind.
     * @throws <code>cz.green.event.engine.ValueOutOfRangeException</code>
     *          If <code>dimension</code> is less then <code>0</code> or
     *          greater then <code>1</code>.
     */
    Item leftSuccessor(int dimension) throws ValueOutOfRangeException;

    /**
     * This method set the new manager for this item.
     *
     * @param <code>manager</code> The new manager to set.
     * @return The set manager.
     * @throws <code>java.lang.NullPointerException</code>
     *          If <code>manager</code> is equal to <code>null</code>.
     */
    Manager manager(Manager manager) throws NullPointerException;

    /**
     * This method looks for the most left point except desktop in this
     * <code>dimension</code>. Is useful for groups to find its most lef
     * point in the dimension.
     *
     * @param <code>dimension</code> In which dimension we looks for position. <code>0</code>
     *                               (<code>1</code>) says we wonder to know in dimension
     *                               <code>x</code> (<code>y</code>).
     * @return The most left point int this window manager.
     * @throws <code>cz.green.event.engine.ValueOutOfRangeException</code>
     *          Thrown if <code>dimension</code> is less then <code>0</code> or
     *          greater then <code>1</code>.
     */
    int mostLeft(int dimension) throws ValueOutOfRangeException;

    /**
     * This method looks for the most rightuf point except desktop in this
     * <code>dimension</code>. Is useful for groups to find its most lef
     * point in the dimension.
     *
     * @param <code>dimension</code> In which dimension we looks for position. <code>0</code>
     *                               (<code>1</code>) says we wonder to know in dimension
     *                               <code>x</code> (<code>y</code>).
     * @return The most right point int this window manager.
     * @throws <code>cz.green.event.engine.ValueOutOfRangeException</code>
     *          Thrown if <code>dimension</code> is less then <code>0</code> or
     *          greater then <code>1</code>.
     */
    int mostRight(int dimension) throws ValueOutOfRangeException;

    /**
     * This method says where this window is in this <code>dimension</code>
     * according to the <code>interval</code>. Has similar function as
     * function <code>whereIs(interval1, interval2)</code> from class
     * <code>cz.green.util.IntervalMethods</code>.
     *
     * @param <code>dimension</code> In which dimension we looks for position. <code>0</code>
     *                               (<code>1</code>) says we wonder to know in dimension
     *                               <code>x</code> (<code>y</code>).
     * @param <code>interval</code>  One dimensional array with 2 items, where
     *                               item <code>interval[0]</code> gives the beggining and item
     *                               <code>interval[1]</code> the end of the interval. According
     *                               to this interval, we says the position.
     * @return <code>int</code>
     *         This value is composition of constants <code>LEFT</code>
     *         <code>IN</code> or <code>RIGHT</code> from class
     *         <code>cz.green.util.IntervalMethods</code>.
     * @throws <code>cz.green.util.BadDimensionException</code>
     *          If array <code>interval</code> has less than 2 items,
     *          on the other hand if the array <code>interval</code> has more
     *          items -> it doesn't matter.
     * @throws <code>cz.green.event.engine.ValueOutOfRangeException</code>
     *          If <code>dimension</code> is less then <code>0</code> or
     *          greater then <code>1</code>.
     * @see cz.green.util.IntervalMethods
     * @see cz.green.util.IntervalMethods#whereIs(int[], int[])
     */
    int position(int dimension, int[] interval) throws cz.green.util.BadDimensionException, ValueOutOfRangeException;

    /**
     * Returns the window, which lies right behind is the
     * <code>dimension</code>.
     *
     * @param <code>dimension</code> In which dimension we have to know right successor.
     * @return Window lieing right behind.
     * @throws <code>cz.green.event.engine.ValueOutOfRangeException</code>
     *          If <code>dimension</code> is less then <code>0</code> or
     *          greater then <code>1</code>.
     */
    Item rightSuccessor(int dimension) throws ValueOutOfRangeException;
}
