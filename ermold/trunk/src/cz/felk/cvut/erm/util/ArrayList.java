package cz.felk.cvut.erm.util;

import java.util.NoSuchElementException;
import java.util.Vector;

/**
 * Implements dynamic array of items. In JDK 1.1 is based on java.util.Vector but in JDK 1.2
 * will be implemented by java.util.ArrayList.
 *
 * @see java.util.Vector
 */

public class ArrayList extends Vector {

    /**
     * Constructs an empty ArrayList.
     */
    public ArrayList() {
        super();
    }

    /**
     * Constructs an empty ArrayList with the specified initial capacity.
     *
     * @param initialCapacity the initial capacity of the ArrayList.
     */
    public ArrayList(int initialCapacity) {
        super(initialCapacity);
    }

    /**
     * Constructs an empty ArrayList with the specified initial capacity and
     * capacity increment.
     *
     * @param initialCapacity   the initial capacity of the ArrayList.
     * @param capacityIncrement the amount by which the capacity is
     *                          increased when the ArrayList overflows.
     */
    public ArrayList(int initialCapacity, int capacityIncrement) {
        super(initialCapacity, capacityIncrement);
    }

    /**
     * Inserts the specified <code>element</code> at the specified position
     * <code>index</code> in this ArrayList. Shifts the element currently
     * at that position (if any) and
     * any subsequent elements to the right (adds one to their indices).
     *
     * @param index   index at which the specified element is to be inserted.
     * @param element element to be inserted.
     * @throws ArrayIndexOutOfBoundsException index is out of range
     *                                        (index &lt; 0 || index &gt; size()).
     */
    public void add(int index, Object element) {
        if (index > elementCount)
            throw new ArrayIndexOutOfBoundsException(index);

        ensureCapacity(elementCount + 1);  // Increments modCount!!
        System.arraycopy(elementData, index, elementData, index + 1,
                elementCount - index);
        elementData[index] = element;
        elementCount++;
    }

    /**
     * Appends the specified element to the end of this ArrayList.
     *
     * @param o element to be appended to this ArrayList.
     * @return true (as per the general contract of Collection.add).
     */
    public boolean add(Object o) {
        addElement(o);
        return true;
    }

    /**
     * Removes all of the elements from this ArrayList.  The ArrayList will
     * be empty after this call returns, unless it throws an exception.
     *
     * @throws UnsupportedOperationException clear is not supported
     *                                       by this Set.
     */
    public void clear() {
        removeAllElements();
    }

    /**
     * Returns a shallow copy of this ArrayList.  (The elements themselves
     * are not copied.)
     *
     * @return a clone of this ArrayList.
     */
    public Object clone() {
        return super.clone();
    }

    /**
     * Returns the first element in Array.
     *
     * @return java.lang.Object
     * @throws NoSuchElementException If this vector has no components.
     */
    public Object first() throws NoSuchElementException {
        return firstElement();
    }
    // Positional Access Operations

    /**
     * Returns the element at the specified position in this ArrayList.
     *
     * @param index index of element to return.
     * @throws ArrayIndexOutOfBoundsException index is out of range (index
     *                                        &lt; 0 || index &gt;= size()).
     */
    public Object get(int index) {
        if (index >= elementCount)
            throw new ArrayIndexOutOfBoundsException(index);

        return elementData[index];
    }

    /**
     * Returns the last element in Array.
     *
     * @return java.lang.Object
     * @throws NoSuchElementException If this vector has no components.
     */
    public Object last() throws NoSuchElementException {
        return lastElement();
    }

    /**
     * Removes the element at the specified position in this ArrayList.
     * Shifts any subsequent elements to the left (subtracts one from their
     * indices).  Returns the element that was removed from the ArrayList.
     *
     * @param index the index of the element to removed.
     * @throws ArrayIndexOutOfBoundsException index out of range <code>(index
     *                                        &lt; 0 || index &gt;= size())</code>.
     */
    public Object remove(int index) {
        if (index >= elementCount)
            throw new ArrayIndexOutOfBoundsException(index);
        Object oldValue = elementData[index];

        int numMoved = elementCount - index - 1;
        if (numMoved > 0)
            System.arraycopy(elementData, index + 1, elementData, index,
                    numMoved);
        elementData[--elementCount] = null; // Let gc do its work

        return oldValue;
    }

    /**
     * Removes from this ArrayList all of the elements whose index is between
     * fromIndex, inclusive and toIndex, exclusive.  Shifts any succeeding
     * elements to the left (reduces their index).
     * This call shortens the ArrayList by (<code>toIndex</code> - <code>fromIndex</code>) elements.  (If
     * <code>toIndex == fromIndex</code>, this operation has no effect.)
     *
     * @param fromIndex index of first element to be removed.
     * @param fromIndex index after last element to be removed.
     * @throws ArrayIndexOutOfBoundsException fromIndex or toIndex out of
     *                                        range <code>(fromIndex &lt; 0 || fromIndex &gt;= size() || toIndex
     *                                        &gt; size() || toIndex &lt; fromIndex)</code>.
     */
    public void removeRange(int fromIndex, int toIndex) {
        if (fromIndex < 0 || fromIndex >= elementCount ||
                toIndex > elementCount || toIndex < fromIndex)
            throw new ArrayIndexOutOfBoundsException();

        int numMoved = elementCount - toIndex;
        if (numMoved > 0)
            System.arraycopy(elementData, toIndex, elementData, fromIndex,
                    numMoved);

        // Let gc do its work
        int newElementCount = elementCount - (toIndex - fromIndex);
        while (elementCount != newElementCount)
            elementData[--elementCount] = null;
    }

    /**
     * Replaces the element at the specified position in this ArrayList with
     * the specified element.
     *
     * @param index   index of element to replace.
     * @param element element to be stored at the specified position.
     * @return the element previously at the specified position.
     * @throws ArrayIndexOutOfBoundsException index out of range
     *                                        <code>(index &lt; 0 || index &gt;= size())</code>.
     * @throws IllegalArgumentException       <code>fromIndex &gt; toIndex</code>.
     */
    public Object set(int index, Object element) {
        if (index >= elementCount)
            throw new ArrayIndexOutOfBoundsException(index);

        Object oldValue = elementData[index];
        elementData[index] = element;
        return oldValue;
    }

    /**
     * Returns an array containing all of the elements in this ArrayList
     * in the correct order.
     */
    public Object[] toArray() {
        Object[] result = new Object[elementCount];
        System.arraycopy(elementData, 0, result, 0, elementCount);
        return result;
    }
}
