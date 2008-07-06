package cz.felk.cvut.erm.util;

import java.util.NoSuchElementException;

/**
 * Special version of <code>ArrayList</code>. This object have another
 * 2 methods for moving objects inside the <code>ArrayList</code>.
 *
 * @see ArrayList
 */
public class MoveArrayList extends ArrayList {


    /**
     * @see ArrayList#ArrayList()
     */
    public MoveArrayList() {
        super();
    }

    /**
     * @see ArrayList#ArrayList(int)
     */
    public MoveArrayList(int initialCapacity) {
        super(initialCapacity);
    }

    /**
     * @see ArrayList#ArrayList(int,int)
     */
    public MoveArrayList(int initialCapacity, int capacityIncrement) {
        super(initialCapacity, capacityIncrement);
    }

    /**
     * This method moves object from index <code>from</code> to index <code>to</code>.
     * Move object from index <code>from+1</code> to index <code>to</code>
     * move one item left and then to position defined by index <code>to</code> places before all
     * saved item from index <code>from</code>.
     *
     * @param <code>from</code> which element to move
     * @param <code>to</code>   where element to move
     * @throws java.lang.ArrayIndexOutOfBoundsException
     *          If <code>from</code>
     *          or <code>to</code> are out of array range.
     */
    public void move(int from, int to) throws ArrayIndexOutOfBoundsException {
        if ((from >= elementCount) || (to >= elementCount))
            throw new ArrayIndexOutOfBoundsException("Move from " + from + " to " + to + ".");
        if (from == to) return; //the element stays at the same position
        Object help = elementData[from]; //save moving element
        if (from < to) { //move element backwards
            System.arraycopy(elementData, from + 1, elementData, from, to - from);
        } else { //move element forwards
            System.arraycopy(elementData, to, elementData, to + 1, from - to);
        }
        elementData[to] = help; //move element to its new position
    }

    /**
     * This method moves object <code>element</code> to index <code>to</code>.
     * First try to find object <code>element</code> and then gives his index
     * in the array. Then invokes method <code>move(from, to)</code>;
     *
     * @param <code>element</code> element to move
     * @param <code>to</code>      where element to move
     * @throws java.lang.ArrayIndexOutOfBoundsException
     *          If <code>to</code>
     *          is out of array range.
     * @see move(int, int)
     */
    public void move(Object element, int to) throws ArrayIndexOutOfBoundsException,
            NoSuchElementException {
        int from = indexOf(element);
        if (from == -1) throw new NoSuchElementException();
        move(from, to);
    }
}
