package cz.green.event.interfaces;

import cz.green.event.exceptions.ItemNotInsideManagerException;

/**
 * This interface specify methods for moving and resizing the item.
 * Manager for this items have to implement
 * <code>MoveableManager</code>.
 *
 * @see MovableManager
 */
public interface MovableItem {
    /**
     * Moves item by specified <code>dx</code> (<code>dy</code>)
     * in horizontal (vertical) direction. This method doesn't ask manager for permit
     * to move, because this method is called just by manager and it have to know, how much
     * can window move.
     *
     * @param <code>dx</code>      Difference in horizontal direction.
     * @param <code>dy</code>      Difference in vertical direction.
     * @param <code>recount</code> If <code>true</code> the help size (used during moving) is set as size.
     */
    void managerMoves(int dx, int dy, boolean recount);

    /**
     * Moves item by specified <code>dx</code> (<code>dy</code>)
     * in horizontal (vertical) direction. This method asks manager for permit
     * to move, because the window can't appear out of its manager.
     *
     * @param <code>dx</code>      Difference in horizontal direction.
     * @param <code>dy</code>      Difference in vertical direction.
     * @param <code>recount</code> If <code>true</code> the links in event tree are recounted, otherways not.
     * @throws <code>cz.green.event.engine.ItemNotInsideManagerException</code>
     *          If manager gives no permit to move.
     */
    void move(int dx, int dy, boolean recount) throws ItemNotInsideManagerException;

    /**
     * This method resides border specified by <code>where</code> by sizes
     * <code>dx</code> (<code>dy</code>).
     *
     * @param <code>dx</code>      By what have to move left or right border.
     * @param <code>dy</code>      By what have to move top or bottom border.
     * @param <code>where</code>   Determine which border should to be resized.
     *                             Composite of constants specified in class <code>ResizePoint</code>.
     * @param <code>recount</code> Determine, if you wanted to recoun links in event tree or not.
     * @throws <code>cz.green.event.engine.ItemNotInsideManagerException</code>
     *          If <code>true</code> the links in event tree are recounted, otherways not.
     * @see cz.green.event.ResizePoint
     */
    void resize(int dx, int dy, int where, boolean recount) throws ItemNotInsideManagerException;
}
