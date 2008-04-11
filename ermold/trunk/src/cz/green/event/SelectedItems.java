package cz.green.event;

import cz.green.event.interfaces.Invokable;
import cz.green.event.interfaces.PaintableItem;
import cz.green.event.interfaces.SelectableItem;
import cz.green.util.MoveArrayList;

import java.awt.*;

/**
 * This class encapsulates the array of items. Is used for selecting more
 * than one item. Also returns one item as focused. This item can change its size.
 */
public class SelectedItems implements SelectableItem {
    /**
     * Stores all selected items
     */
    private MoveArrayList elems = null;

    /**
     * Creates empty list of selected items.
     */
    public SelectedItems() {
        super();
        elems = new MoveArrayList(getArraySize(), getIncrement());
    }

    /**
     * This method add element <code>elem</code> into the element list
     * to the end or removes the element if it already is in the list.
     *
     * @param <code>elem</code> Adding element.
     * @return <code>true</code> if the element was added and
     *         <code>false</code> if the element was removed
     */
    public boolean addElement(SelectableItem elem) {
        if (elem == null)
            return false;
        int index = elems.indexOf(elem);
        if (index == -1) { //element will be selected
            elems.add(elem);
            elem.select();
            return true;
        } else { //element is going to be deselected
            elems.remove(index);
            elem.deselect();
            return false;
        }
    }

    /**
     * Empties the element list and add the element <code>elem</code>
     * as the first element in the list
     *
     * @param <code>elem</code> The first element in the new selection.
     */
    public void addFirstElement(SelectableItem elem) {
        if (!elems.isEmpty()) {
            java.util.Enumeration e = elems.elements();
            while (e.hasMoreElements()) {
                ((SelectableItem) e.nextElement()).deselect();
            }
            elems.clear();
        }
        if (elem != null) {
            elems.add(elem);
            elem.select();
        }
    }

    /**
     * Test whether the element list contains the element <code>elem</code>.
     *
     * @param <code>elem</code> The first element in the new selection.
     * @return <code>true</code> if the list contains the element <code>elem</code>;
     *         <cide>false</code> other ways
     */
    public boolean containsElement(SelectableItem elem) {
        int index = elems.indexOf(elem);
        return (index != -1);
    }

    /**
     * This method was created by Jiri Mares
     */
    public void deselect() {
    }

    /**
     * Returns focused item in the selected items list.
     *
     * @see java.util.Vector#isEmpty()
     */
    public SelectableItem focusedElement() {
        if (elems.isEmpty())
            return null;
        return ((SelectableItem) elems.first());
    }

    /**
     * Returns the starting window array size.
     *
     * @return The size.
     */
    private int getArraySize() {
        return 10;
    }

    /**
     * Returns rectangle, that is union of all selected items rectangles.
     */
    public java.awt.Rectangle getBounds() {
        java.util.Enumeration e = elems.elements();
        Rectangle rect = null;
        if (e.hasMoreElements()) {
            rect = ((SelectableItem) e.nextElement()).getBounds();
            while (e.hasMoreElements()) {
                rect = rect.union(((SelectableItem) e.nextElement()).getBounds());
            }
        }
        return rect;
    }

    /**
     * Returns the increment value for window array. If the array is too small
     * to store all items, by this value is expanded.
     *
     * @return The size.
     */
    private int getIncrement() {
        return 5;
    }

    /**
     * Returns rectangle, that is union of all selected items rectangles.
     */
    public java.awt.Rectangle getRealBounds() {
        java.util.Enumeration e = elems.elements();
        Rectangle rect = null;
        if (e.hasMoreElements()) {
            rect = ((SelectableItem) e.nextElement()).getRealBounds();
            while (e.hasMoreElements()) {
                rect = rect.union(((SelectableItem) e.nextElement()).getRealBounds());
            }
        }
        return rect;
    }

    /**
     * This method return resize points the first item in the list.
     * If there is more than one item in the list, it returns null.
     *
     * @see ResizePoint
     * @see PaintableItem#getResizePoints()
     */
    public ResizePoint[] getResizePoints() {
        SelectableItem help = focusedElement();
        if (help != null)
            return help.getResizePoints();
        else
            return null;
    }

    /**
     * Invokes invokeEventHandler for all items in the list.
     *
     * @see Invokable#invokeEventHandler(cz.green.event.interfaces.Event)
     */
    public boolean invokeEventHandler(cz.green.event.interfaces.Event event) {
        java.util.Vector v = (java.util.Vector) elems.clone();
        java.util.Enumeration e = v.elements();
        while (e.hasMoreElements()) {
            ((SelectableItem) e.nextElement()).invokeEventHandler(event);
        }
        return true;
    }

    /**
     * Returns whether the list is empty.
     *
     * @see java.util.Vector#isEmpty()
     */
    public boolean isEmpty() {
        return elems.isEmpty();
    }

    /**
     * Paint all items in the list. The first inserted item is painted first.
     *
     * @see cz.green.event.interfaces.PaintableItem#paint(java.awt.Graphics)
     */
    public void paint(java.awt.Graphics g) {
        java.util.Enumeration e = elems.elements();
        while (e.hasMoreElements()) {
            ((SelectableItem) e.nextElement()).paint(g);
        }
    }

    /**
     * Invokes <code>paintFast</code> all items in the list.
     * The first inserted item is painted first.
     *
     * @see PaintableItem#paintFast(java.awt.Graphics)
     */
    public void paintFast(java.awt.Graphics g) {
        java.util.Enumeration e = elems.elements();
        while (e.hasMoreElements()) {
            ((SelectableItem) e.nextElement()).paintFast(g);
        }
    }

    /**
     * Removes the specified element <code>elem</code> from the selected
     * items list. If this <code>elem</code> is not in the list then do nothing.
     *
     * @param <code>elem</code> The first element in the new selection.
     * @return <code>true</code> if the list contained the element <code>elem</code>;
     *         <cide>false</code> other ways
     */
    public boolean removeElement(SelectableItem elem) {
        int index = elems.indexOf(elem);
        if (index == -1) { //element is will be selected
            return false;
        }
        //element is going to be deselected
        elems.remove(index);
        elem.deselect();
        return true;
    }

    /**
     * This method was created by Jiri Mares
     */
    public void select() {
}
}
