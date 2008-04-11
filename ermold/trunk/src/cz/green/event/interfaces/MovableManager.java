package cz.green.event.interfaces;

import cz.green.event.exceptions.ItemNotInsideManagerException;

/**
 * This mathods have to implement manager which have to store
 * <code>MoveableItem</code>s. It contains only method for testing,
 * whether item will be in manager after moving or resizing.
 *
 * @see MovableItem
 */
public interface MovableManager {
    /**
     * Window manager have to try if the new position its item is in.
     * If isn't the exception is thrown.
     *
     * @param <code>dimension</code> In which dimension we looks for position. <code>0</code>
     *                               (<code>1</code>) says we wonder to know in dimension
     *                               <code>x</code> (<code>y</code>).
     * @param <code>interval</code>  This is the new position the item.
     *                               One dimensional array with 2 items, where
     *                               item <code>interval[0]</code> gives the begining and item
     *                               <code>interval[1]</code> the end of the interval. According
     *                               to this interval, we says the position.
     * @throws <code>cz.green.event.engine.ItemNotInsideManagerException</code>
     *          If this window goes by moving out of its window manager.
     */
    void itemMoveDimension(int dimension, int[] interval) throws ItemNotInsideManagerException;
}
