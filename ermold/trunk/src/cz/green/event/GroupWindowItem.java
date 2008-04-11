package cz.green.event;

import cz.green.event.exceptions.ImpossibleNegativeValueException;
import cz.green.event.exceptions.ItemNotInsideManagerException;
import cz.green.event.exceptions.ValueOutOfRangeException;
import cz.green.event.interfaces.*;
import cz.green.util.BadDimensionException;
import cz.green.util.IntervalMethods;
import cz.green.util.IntervalMethodsException;
import cz.green.util.MoveArrayList;

import java.awt.*;

/**
 * This class implements the functionality of the <code>EventTree2DItem</code>
 * and <code>EventTree2DManager</code>. Its means that it is for its items manager
 * and for its manager items.
 * <p/>
 * This class should be the superclass for all classes that have to implements
 * the some kinds of window's groups.
 *
 * @see Item
 * @see Manager
 */
public class GroupWindowItem extends WindowItem implements Manager, java.io.Serializable {
    /**
     * Array of all item stored in this manager. The first in this array is the most under and
     * vice versa. It is caused by need to redraw all items from the under to the top.
     *
     * @see cz.green.util.MoveArrayList
     * @see Item
     */
    protected MoveArrayList wins = null;
    /**
     * This item stores pointer to the receiver of the event. It means, the
     * item, that is selected as the potencial window, where the event
     * situated is.This property is set during method <code>isIn</code>
     * invocation.
     *
     * @see SelectableItem
     * @see isIn(int, int)
     */
    transient protected SelectableItem receiver = null;
    /**
     * This rectangle stores the rectangle, that should be repainted as soon as possible.
     *
     * @see #repaintItem(Item)
     */
    transient protected Rectangle repaint = null;

    /**
     * This parameter less constructor is needed for desktop creating.
     */
    protected GroupWindowItem() {
        super();
        wins = new MoveArrayList(getArraySize(), getIncrement());
    }

    /**
     * Creates group with specified <code>manager</code>, position (by <code>left</code>
     * and <code>right</code> parameters) and size (by <code>width</code> and <code>height</code>
     * parameters).
     *
     * @see WindowItem#WindowItem(cz.green.event.interfaces.Manager , int, int, int, int)
     */
    public GroupWindowItem(Manager manager, int left, int top, int width, int height) throws NullPointerException, ImpossibleNegativeValueException {
        super(manager, left, top, width, height);
        wins = new MoveArrayList(getArraySize(), getIncrement());
    }

    /**
     * Adds <code>item</code> to the top of this group (manager).
     *
     * @see Manager#add(cz.green.event.interfaces.Item)
     */
    public void add(Item item) throws ItemNotInsideManagerException {
        try {
            if ((item.position(0, rect[0]) != IntervalMethods.IN) || (item.position(1, rect[1]) != IntervalMethods.IN))
                throw new ItemNotInsideManagerException();
            int size = wins.size();
            item.manager(this);
            wins.add(size, item);
            if (isBottom(item))
                item.countLinks(null);
            else
                item.countLinks((Item) wins.get(size - 1));
        } //this could not apper, else it is critical situation
        catch (ValueOutOfRangeException e) {
        } catch (BadDimensionException e) {
        }
    }

    /**
     * This methods recounts all links from the <code>item</code> to the top.
     *
     * @see Manager#countAllLinksToTop(cz.green.event.interfaces.Item)
     */
    public void countAllLinksToTop(Item item) {
        if (item == null)
            return;
        //you have to know, where is and the size
        int position = wins.indexOf(item);
        if (position == -1)

            //element isn't in the array ->do nothing
            return;
        int size = wins.size() - 1;
        Item next;
        //recount links to item, to build correct event tree
        if (position == 0)
            next = null;
        else
            next = (Item) wins.get(position - 1);
        while (position++ < size) {
            item.countLinks(next);
            next = item;
            item = (Item) wins.get(position);
        }
        item.countLinks(next);
    }

    /**
     * This method process all needed for handling the event. It means that it asks whether the
     * event is really situated in this group (method <code>isIn</code>) and if is, then
     * invoces the <code>invokeEventHandler</code>.
     *
     * @see Manager#fallAndHandleEvent(int, int, cz.green.event.interfaces.Event)
     * @see isIn(int,int)
     * @see invokeEventHandler( cz.green.event.interfaces.Event )
     */
    public boolean fallAndHandleEvent(int x, int y, cz.green.event.interfaces.Event event) {
        return manager.fallAndHandleEvent(x, y, event);
    }

    /**
     * Returns the starting window array size.
     *
     * @return The size.
     * @see GroupWindowItem.wins
     */
    private int getArraySize() {
        return 50;
    }

    /**
     * Returns the background color of the window.
     *
     * @return The background color.
     */
    protected Color getBackgroundColor() {
        return BACKGROUND_COLOR;
    }

    /**
     * Returns the increment value for window array. If the array is too small
     * to store all items, by this value is expanded.
     *
     * @return The size.
     * @see GroupWindowItem.wins
     */
    private int getIncrement() {
        return 5;
    }

    /**
     * Insert the method's description here.
     * Creation date: (6.4.2001 19:59:30)
     *
     * @return int
     */
    public Item getItem(int i) {
        return (Item) wins.get(i);
    }

    /**
     * Insert the method's description here.
     * Creation date: (6.4.2001 19:59:30)
     *
     * @return int
     */
    public int getItemCount() {
        return wins.size();
    }

    /**
     * Returns the scale for painting its items. For the scale value should ask its manager.
     *
     * @see PaintableManager#getScale()
     */
    public float getScale() {
        return manager.getScale();
    }

    /**
     * Returns the background color of the selected window group.
     *
     * @return The color background color.
     */
    protected Color getSelectedBackgroundColor() {
        return Color.green;
    }

    /**
     * This event handler adds the item stored in the <code>event</code>
     * to this manager, displays it and clears the selected items list.
     *
     * @param <code>event</code> Event with all needed properties.
     * @see Manager#add(cz.green.event.interfaces.Item)
     * @see PaintableManager#repaintItem(cz.green.event.interfaces.PaintableItem)
     */
    public void handleAddItemEvent(AddItemEvent event) {
        try {
            add(event.getItem());
            selectItemEx(null, false);
            repaintItem(event.getItem());
        } catch (ItemNotInsideManagerException e) {
        }
    }

    /**
     * Moves <code>item</code> to the top (bottom) in this group
     * (manager). Where to place decides the <code>top</code> parameter.
     *
     * @see Manager#changeZOrder(cz.green.event.interfaces.Item , boolean)
     * @see GroupWindowItem.wins
     */
    public void changeZOrder(Item item, boolean top) {
        if (item == null)
            return;
        //you have to know, where is and the size
        int position = wins.indexOf(item);
        if (position == -1)
            //element isn't in the array ->do nothing
            return;
        int size = wins.size() - 1;
        Item help, next;
        if (top) {
            if (!isTop(item)) {
                //move window to the top - it means the last in array
                wins.move(position, size);
                //recount links to item, to build correct event tree
                if (position == 0)
                    next = null;
                else
                    next = (Item) wins.get(position - 1);
                for (; position < size; position++) {
                    (help = (Item) wins.get(position)).countLinksTo(next, item);
                    next = help;
                }
                //count his links, all have to recount
                item.countLinks(next);
            }
        } else {
            if (!isBottom(item)) {
                //move window to bottom, to first position in array
                wins.move(item, 0);
                //count all links the moved array
                item.countLinks(null);
                //from 1 to position, we have to count links to manager
                next = item;
                for (int i = 1; i <= position; i++) {
                    (help = (Item) wins.get(i)).countLinksTo(next, null);
                    next = help;
                }
                //from position to top we counts links to item and manager
                for (; position <= size; position++) {
                    (help = (Item) wins.get(position)).countLinksTo(next, item);
                    help.countLinksTo(next, null);
                    next = help;
                }
            }
        }
        if (manager != this) {
            manager.changeZOrder(this, top);
            if (repaint != null)
                repaint.union(getBounds());
            else
                repaint = getBounds();
        }
    }

    /**
     * This methods invokes event handler for specified <code>receiver</code>.
     *
     * @see Invokable#invokeEventHandler(cz.green.event.interfaces.Event)
     * @see GroupWindowItem.receiver
     */
    public boolean invokeEventHandler(cz.green.event.interfaces.Event event) {
        Invokable i = receiver;
        receiver = null;
        if ((i == null) || (i == this))
            return super.invokeEventHandler(event);
        else
            return i.invokeEventHandler(event);
    }

    /**
     * Is the specified <code>item</code> at the bottom of all items?
     *
     * @see Manager#isBottom(cz.green.event.interfaces.Item)
     * @see GroupWindowItem.wins
     */
    public boolean isBottom(Item item) {
        return (item.equals(wins.firstElement()));
    }

    /**
     * Is the event inside the group shape. In the window group
     * this method perform event fall inside the group and looks
     * for item, which can handle the event. If there is no such item,
     * then receives itself the event.
     *
     * @see WindowItem#isIn(int, int)
     * @see WindowItem#eventFall(int, int)
     */
    public boolean isIn(int x, int y) {
        if (wins.size() == 0)
            receiver = this;
        else {
            Item h = (Item) wins.lastElement();
            while (true) {
                if (((h = h.eventFall(x, y)) == null) || (h == this))
                    break;
                if (h.isIn(x, y))
                    break;
                //			try {
                int position = wins.indexOf(h);
                if (position <= 0) {
                    h = null;
                    break;
                }
                h = (Item) wins.get(position - 1);
                /*				if ((h = h.behindSuccessor(0)) == null) {
                break;
                }
                } catch (ValueOutOfRangeException e) {
                h = null;
                break;
                }*/
            }
            if (h == null) {
                receiver = this;
            } else {
                receiver = h;
            }
        }
        return true;
    }

    /**
     * Looks for the most left item in the specified
     * <code>dimension</code> and tests whether the new size (specified by
     * <code>interval</code>) contains this item. This method is used during resizing
     * the group for testing whether all items are still in the group.
     *
     * @param <code>dimension</code> In which dimension to test.
     * @throws <code>cz.green.event.engine.ItemNotInsideManagerException</code>
     *          If the items lies out of the group manager.
     */
    protected void isMostLeftItemIn(int dimension, int[] interval) throws ItemNotInsideManagerException {
        if (wins.size() == 0) //empty group -> all is ok
            return;
        try {
            if (IntervalMethods.whereIs(interval, ((Item) wins.lastElement()).mostLeft(dimension)) != IntervalMethods.IN)
                //tests the item
                throw new ItemNotInsideManagerException();
        } catch (ValueOutOfRangeException e) {
            throw new ItemNotInsideManagerException();
        } catch (BadDimensionException e) {
            throw new ItemNotInsideManagerException();
        }
    }

    /**
     * Looks for the most right item in the specified
     * <code>dimension</code> and tests whether the new size (specified by
     * <code>interval</code>) contains this item.
     *
     * @param <code>dimension</code> In which dimension to test.
     * @throws <code>cz.green.event.engine.ItemNotInsideManagerException</code>
     *          If the items lies out of the group manager.
     */
    protected void isMostRightItemIn(int dimension, int[] interval) throws ItemNotInsideManagerException {
        if (wins.size() == 0) //empty group -> all is ok
            return;
        try {
            if (IntervalMethods.whereIs(interval, ((Item) wins.lastElement()).mostRight(dimension)) != IntervalMethods.IN)
                //tests the item
                throw new ItemNotInsideManagerException();
        } catch (ValueOutOfRangeException e) {
            throw new ItemNotInsideManagerException();
        } catch (BadDimensionException e) {
            throw new ItemNotInsideManagerException();
        }
    }

    /**
     * Is the specified <code>item</code> at the top of all items?
     *
     * @see Manager#isTop(cz.green.event.interfaces.Item)
     * @see GroupWindowItem.wins
     */
    public boolean isTop(Item item) {
        return (item.equals(wins.lastElement()));
    }

    /**
     * Window manager test if the new position its item is in. This method is called during
     * moving or resizing the item to its manager to tests whether the item is stil inside its
     * manager.
     *
     * @see MovableManager#itemMoveDimension(int, int[])
     */
    public void itemMoveDimension(int dimension, int[] interval) throws ItemNotInsideManagerException {
//	try {
//		if (IntervalMethods.whereIs(rect[dimension], interval) != IntervalMethods.IN)
//			throw new ItemNotInsideManagerException();
//	} catch (IndexOutOfBoundsException e) {
//		throw new ItemNotInsideManagerException();
//	} catch (BadDimensionException e) {
//		throw new ItemNotInsideManagerException();
        //}
    }

    /**
     * Moves item by specified <code>dx</code> (<code>dy</code>)
     * in horizontal (vertical) direction. Used when group is moving. Because we used absolut
     * coordinates, we have to move all included items during group moving.
     *
     * @see MovableItem#managerMoves(int, int, boolean)
     */
    public void managerMoves(int dx, int dy, boolean recount) {
        super.managerMoves(dx, dy, recount);
        moveItems(dx, dy, recount);
    }

    /**
     * Moves by specified <code>dx</code> (<code>dy</code>)
     * in horizontal (vertical) direction. More moves all item in this manager,
     * to stay relatively at same position (caused by absolut coordinates).
     *
     * @see WindowItem#move(int, int, boolean)
     * @see moveItems(int, int, boolean)
     */
    public void move(int dx, int dy, boolean recount) throws ItemNotInsideManagerException {
        super.move(dx, dy, recount);
        moveItems(dx, dy, recount);
    }

    /**
     * This method moves all items stored in this group. It is used
     * when we move group and items have to move too.
     *
     * @param <code>dx</code>      Difference in x coordinate
     * @param <code>dy</code>      Difference in y coordinate
     * @param <code>recount</code> Specifies, whether we have to reconstruct the event tree struction.
     * @see move(int, int, boolean)
     */
    protected void moveItems(int dx, int dy, boolean recount) {
        java.util.Enumeration e = wins.elements();
        while (e.hasMoreElements()) {
            ((Item) e.nextElement()).managerMoves(dx, dy, recount);
        }
    }

    /**
     * Paints the group and all included items.
     *
     * @see PaintableItem#paint(java.awt.Graphics)
     */
    public void paint(Graphics g) {
        //paint group
        Rectangle r = getBounds();
        if (selected)
            g.setColor(getSelectedBackgroundColor());
        else
            g.setColor(getBackgroundColor());
        g.fillRect(r.x, r.y, r.width, r.height);
        g.setColor(getForegroundColor());
        g.drawRect(r.x, r.y, r.width, r.height);
        //paint all included element
        java.util.Enumeration e = wins.elements();
        while (e.hasMoreElements()) {
            ((PaintableItem) e.nextElement()).paint(g);
        }
    }

    /**
     * Paints specified group item with XOR mode painting.
     *
     * @see PaintableItem#paintFast(java.awt.Graphics)
     */
    public void paintFast(java.awt.Graphics g) {
        Rectangle r = getRealBounds();
        g.drawRect(r.x, r.y, r.width, r.height);
    }

    /**
     * Removes the specified <code>item</code> from this group (manager). Also reconstruts the
     * event tree struction.
     *
     * @see cz.green.event.interfaces.Manager#remove(cz.green.event.interfaces.Item)
     */
    public void remove(Item item) {
        if (item == null)
            return;
        //you have to know, where is and the size
        int position = wins.indexOf(item);
        if (position == -1)
            return;
        int size = wins.size() - 1;
        Item help, next;
        //remove window from array
        wins.remove(position);
        //recount links to item, to build correct event tree
        if (position != 0) {
            next = (Item) wins.get(position - 1);
            for (; position < size; position++) {
                (help = (Item) wins.get(position)).countLinksTo(next, item);
                next = help;
            }
        }
    }

    /**
     * This method calls the same manager method. If there is some rectangle to repaint stored
     * in atribute repaint, it repaints its also. The manager paints the <code>item</code>.
     *
     * @see PaintableManager#repaintItem(cz.green.event.interfaces.PaintableItem)
     * @see #repaint
     */
    public void repaintItem(PaintableItem item) {
        if (repaint != null) {
            Rectangle r = repaint;
            manager.repaintRectangle(r.x, r.y, r.width, r.height);
            repaint = null;
        }
        manager.repaintItem(item);
    }

    /**
     * This method calls the manager method paintFast to paint fast the <code>item</code>.
     *
     * @see PaintableManager#repaintItemFast(cz.green.event.interfaces.PaintableItem)
     */
    public void repaintItemFast(PaintableItem item) {
        manager.repaintItemFast(item);
    }

    /**
     * This method invokes the same manager method to repaint the specified rectangle.
     * If there is some rectangle to repaint stored in atribute repaint, it repaints its also.
     *
     * @see PaintableManager#repaintRectangle(int, int, int, int)
     * @see #repaint
     */
    public void repaintRectangle(int x, int y, int width, int height) {
        if (repaint != null) {
            Rectangle r = repaint;
            manager.repaintRectangle(r.x, r.y, r.width, r.height);
            repaint = null;
        }
        manager.repaintRectangle(x, y, width, height);
    }

    /**
     * Moves bottom border of the window.
     *
     * @see MovableItem#resizeBottom(int, boolean)
     */
    public void resize(int dx, int dy, int where, boolean recount) throws ItemNotInsideManagerException {
        try {
            if (!recount && (hRect == null)) {
                hRect = new int[2][2];
                System.arraycopy(rect[0], 0, hRect[0], 0, 2);
                System.arraycopy(rect[1], 0, hRect[1], 0, 2);
            }
            int[][] source;
            if (hRect != null)
                source = hRect;
            else
                source = rect;
            int[][] r = new int[2][2];
            System.arraycopy(source[0], 0, r[0], 0, 2);
            System.arraycopy(source[1], 0, r[1], 0, 2);
            //resize
            if (((where & ResizePoint.BOTTOM) == ResizePoint.BOTTOM) && (dy >= (source[1][0] - source[1][1]))) {
                IntervalMethods.resizeEndBy(r[1], dy);
                isMostRightItemIn(1, r[1]);
            }
            if (((where & ResizePoint.LEFT) == ResizePoint.LEFT) && (dx <= (source[0][1] - source[0][0]))) {
                IntervalMethods.resizeStartBy(r[0], dx);
                isMostLeftItemIn(0, r[0]);
            }
            if (((where & ResizePoint.RIGHT) == ResizePoint.RIGHT) && (dx >= (source[0][0] - source[0][1]))) {
                IntervalMethods.resizeEndBy(r[0], dx);
                isMostRightItemIn(0, r[0]);
            }
            if (((where & ResizePoint.TOP) == ResizePoint.TOP) && (dy <= (source[1][1] - source[1][0]))) {
                IntervalMethods.resizeStartBy(r[1], dy);
                isMostLeftItemIn(1, r[1]);
            }
            //ask for permition
            manager.itemMoveDimension(0, r[0]);
            manager.itemMoveDimension(1, r[1]);
            if (recount) {
                System.arraycopy(r[0], 0, rect[0], 0, 2);
                System.arraycopy(r[1], 0, rect[1], 0, 2);
                hRect = null;
                manager.countAllLinksToTop(this);
            } else {
                System.arraycopy(r[0], 0, source[0], 0, 2);
                System.arraycopy(r[1], 0, source[1], 0, 2);
            }
        } catch (IntervalMethodsException e) {
            throw new ItemNotInsideManagerException();
        }
    }

    /**
     * This methods invokes manager <code>selectItem</code> method
     * to set selected a <code>item</code>.
     *
     * @see PaintableManager#selectItem(cz.green.event.interfaces.SelectableItem , boolean)
     */
    public boolean selectItem(SelectableItem item, boolean add) {
        return (manager.selectItem(item, add));
    }

    /**
     * This methods invokes manager <code>selectItemEx</code> method
     * to set selected a <code>item</code>.
     *
     * @see PaintableManager#selectItemEx(cz.green.event.interfaces.SelectableItem , boolean)
     */
    public void selectItemEx(SelectableItem item, boolean add) {
        manager.selectItemEx(item, add);
    }
}
