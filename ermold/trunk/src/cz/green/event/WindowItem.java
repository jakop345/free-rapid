package cz.green.event;

import cz.green.event.exceptions.ImpossibleNegativeValueException;
import cz.green.event.exceptions.ItemNotInsideManagerException;
import cz.green.event.exceptions.ValueOutOfRangeException;
import cz.green.event.interfaces.*;
import cz.green.util.BadDimensionException;
import cz.green.util.IntervalMethods;
import cz.green.util.IntervalMethodsException;

import java.awt.*;

/**
 * This class implements basic function, which have to have event tree item.
 * Add implementing some useful interfaces.
 * This class should be the superclass to all classes, that wants to be places
 * in the vent tree structure.
 *
 * @see Item
 */
public class WindowItem implements Item, java.io.Serializable {
    /**
     * Field <code>tree</code> contains links to other EventTree2DItems.
     * in the first dimension has two items (dimesion x and y) and in the
     * second dimension has two items. The window in left and right.
     */
    protected Item[][] tree = null;
    /**
     * Field <code>rect</code> holds the rectangle, where this window is situated.
     */
    protected int[][] rect = null;
    /**
     * Field <code>hRect</code> holds the rectangle, where this window is situated during moving (resizing).
     */
    transient protected int[][] hRect = null;
    /**
     * Field <code>manager</code> stores object that manage this window.
     */
    protected Manager manager = null;
    /**
     * Indicates if we already used <code>paintFast</code>.
     *
     * @see paintFast(java.awt.Graphics)
     */
    transient protected boolean paintedFast = false;
    /**
     * Tells us where lied this window before moving. Is used for
     * repainting area after moving.
     */
    transient protected Rectangle rectangle = null;
    /**
     * Determines whether the item is selected or not
     *
     * @see SelectableItem#select()
     * @see SelectableItem#deselect()
     */
    transient protected boolean selected = false;
    protected boolean selectable = true;

    /**
     * Background color
     */
    public static Color BACKGROUND_COLOR = Color.lightGray;

    /**
     * Object background color
     */
    public static Color OBJECT_BACKGROUND_COLOR = Color.white;
    /**
     * Selected object background color
     */
    public static Color SELECTED_OBJECT_BACKGROUND_COLOR = new Color(204, 204, 255);
    /**
     * Selected object foreground color
     */
    public static final Color SELECTED_OBJECT_FOREGROUND_COLOR = Color.blue;
    /**
     * Object foreground color
     */
    public static Color OBJECT_FOREGROUND_COLOR = Color.black;

    /**
     * Default constructor for EventTree2DWindow. Values in <code>tree</code>
     * sets to <code>null</code> and values in <code>rect</code> set to
     * <code>0</code>. The field <code>manager</code> is set to
     * <code>null</code>. This <code>protected</code> constructor is useful only
     * for desktops, because they don't need to know his manager, because they
     * are manager itselfs.
     */
    protected WindowItem() {
        super();
        tree = new Item[2][2];
        rect = new int[2][2];
        manager = null;
    }

    /**
     * This constructor sets size and left top point of this window.
     *
     * @param <code>width</code>
     * @param <code>height</code>
     * @throws <code>java.lang.NullPointerException</code>
     *          If <code>manager</code> is equal to <code>null</code>.
     * @throws <code>cz.green.event.engine.ImpossibleNegativeValueException</code>
     *          if one of <code>left</code>, <code>right</code>,
     *          <code>width</code> or <code>height</code> is negative.
     */
    public WindowItem(Manager manager, int left, int top, int width, int height) throws ImpossibleNegativeValueException, NullPointerException {
        this();
        if (manager == null)
            throw new NullPointerException();
        this.manager = manager;
        if ((left < 0) || (top < 0) || (width < 0) || (height < 0))
            throw new ImpossibleNegativeValueException();
        rect[0][0] = left;
        rect[0][1] = left + width;
        rect[1][0] = top;
        rect[1][1] = top + height;
    }

    /**
     * This method count links to field <code>tree</code>. Method knows
     * size this window and knows the next window in the sorted array. Used
     * already built event tree.
     *
     * @param <code>item</code> The next <code>item</code> in the actual manager order.
     * @see Item#countLinks(cz.green.event.interfaces.Item)
     */
    public void countLinks(Item item) {
        if (item == null) {
            tree[0][0] = tree[0][1] = tree[1][0] = tree[1][1] = null;
            return;
        }
        try {
            int where;
            for (int i = 0; i < 2; i++) {
                //asks where is the next window
                where = item.position(i, rect[i]);
                //set where is the next window
                tree[i][0] = item.itemIn(i, rect[i], IntervalMethods.LEFT, where);
                tree[i][1] = item.itemIn(i, rect[i], IntervalMethods.RIGHT, where);
            }
        } //if occurs exception, this code is wrong
        catch (ValueOutOfRangeException e) {
        } catch (BadDimensionException e) {
        }
    }

    /**
     * This method recounts only that links from tree, that links to
     * <code>to</code>. Counts starts from next <code>item</code>
     * from the list actual window manager. This method uses already
     * created event tree.
     *
     * @param <code>item</code> The next <code>item</code> in the actual manager order.
     * @param <code>to</code>   Specify which links have to be recount. If the link is equal to
     *                          <code>to</code> then is recaount.
     * @see Item#countLinksTo(cz.green.event.interfaces.Item , cz.green.event.interfaces.Item)
     */
    public void countLinksTo(Item item, Item to) {
        if (item == null) {
            tree[0][0] = tree[0][1] = tree[1][0] = tree[1][1] = null;
            return;
        }
        try {
            for (int i = 0; i < 2; i++) {
                int where = item.position(i, rect[i]);
                if (tree[i][0] == to)
                    tree[i][0] = item.itemIn(i, rect[i], IntervalMethods.LEFT, where);
                if (tree[i][1] == to)
                    tree[i][1] = item.itemIn(i, rect[i], IntervalMethods.RIGHT, where);
            }
        } //if occurs exception, this code is wrong
        catch (ValueOutOfRangeException e) {
        } catch (BadDimensionException e) {
        }
    }

    /**
     * This method was created by Jiri Mares
     */
    public void deselect() {
        selected = false;
    }

    /**
     * Looks for item in which is situated point.
     * Returns the item, that first has field <code>rect</code>
     * as large as necessary be to include the event.
     *
     * @return <code>cz.green.event.engine.EventTree2DItem</code>
     *         The item in which point situatedis. Return <code>null</code>
     *         if such item doesn't exists.
     * @see Item#eventFall(int,int)
     */
    public Item eventFall(int x, int y) {
        try {
            Item item = null, newItem, item2 = this;
            //fall in first dimension and than in second -> if it's a different
            //item than continue
            while (item != item2) {
                if ((item = item2) == null)
                    return null;
                while (((newItem = item.eventFallStep(0, x)) != item) && ((item = newItem) != null)) ;
                if ((item2 = item) == null)
                    return null;
                while (((newItem = item2.eventFallStep(1, y)) != item2) && ((item2 = newItem) != null)) ;
            }
            return item;
        } //if occurs exception, this code is wrong
        catch (ValueOutOfRangeException e) {
            return null;
        }
    }

    /**
     * Looks if the <code>point</code> in the specified
     * <code>dimension</code> is in the window.
     *
     * @return <code>cz.green.event.engine.EventTree2DItem</code>
     *         Returns this window if this window <code><b>point</b></code>
     *         contains, or the left (right) behind window.
     * @see Item#eventFallStep(int, int)
     */
    public Item eventFallStep(int dimension, int point) throws ValueOutOfRangeException {
        try {
            switch (IntervalMethods.whereIs(rect[dimension], point)) {
                case IntervalMethods.LEFT:
                    return tree[dimension][0];
                case IntervalMethods.IN:
                    return this;
                case IntervalMethods.RIGHT:
                    return tree[dimension][1];
                default:
                    return null;
            }
        } catch (IndexOutOfBoundsException e) {
            throw new ValueOutOfRangeException();
        } catch (BadDimensionException e) {
            //this situation should not appear
            return null;
        }
    }

    /**
     * Returns the background color of the window.
     *
     * @return The background color.
     */
    protected Color getBackgroundColor() {
        return OBJECT_BACKGROUND_COLOR;
    }

    /**
     * Returns rectangle which specifies the size of this window. This function asks
     * its manager for the scale and counts the bounds according to the actual scale.
     *
     * @return Rectangle, where this window situated is.
     * @see cz.green.event.interfaces.PaintableItem#getBounds()
     * @see PaintableManager#getScale()
     */
    public Rectangle getBounds() {
        int[][] r = rect;
        float scale = (manager).getScale();
        return new Rectangle((int) (r[0][0] / scale), (int) (r[1][0] / scale),
                (int) ((r[0][1] - r[0][0]) / scale), (int) ((r[1][1] - r[1][0]) / scale));
    }

    /**
     * Returns the foreground color of the window.
     *
     * @return The foreground color.
     */
    protected Color getForegroundColor() {
        return OBJECT_FOREGROUND_COLOR;
    }

    public int getL() {
        return rect[0][1];
    }

    public Manager getManager() {
        return manager;
    }

    /**
     * Returns rectangle which specifies the size of this window. This function asks
     * its manager for the scale and counts the bounds according to the actual scale.
     *
     * @return Rectangle, where this window situated is.
     * @see cz.green.event.interfaces.PaintableItem#getBounds()
     * @see PaintableManager#getScale()
     */
    public Rectangle getRealBounds() {
        int[][] r = hRect;
        if (r == null)
            r = rect;
        float scale = (manager).getScale();
        return new Rectangle((int) (r[0][0] / scale), (int) (r[1][0] / scale), (int) ((r[0][1] - r[0][0]) / scale), (int) ((r[1][1] - r[1][0]) / scale));
    }

    /**
     * This method return all points where should have be placed resize point.
     * The position of all resize points is specified by relative coordinates
     * according to the bounds of this item.
     *
     * @see ResizePoint
     * @see PaintableItem#getResizePoints()
     */
    public ResizePoint[] getResizePoints() {
        ResizePoint[] r = new ResizePoint[8];
        r[0] = new ResizePoint(0, 0, ResizePoint.LEFT | ResizePoint.TOP);
        r[1] = new ResizePoint(0, 1, ResizePoint.LEFT | ResizePoint.BOTTOM);
        r[2] = new ResizePoint(1, 1, ResizePoint.RIGHT | ResizePoint.BOTTOM);
        r[3] = new ResizePoint(1, 0, ResizePoint.RIGHT | ResizePoint.TOP);
        r[4] = new ResizePoint(0, 0.5, ResizePoint.LEFT);
        r[5] = new ResizePoint(1, 0.5, ResizePoint.RIGHT);
        r[6] = new ResizePoint(0.5, 0, ResizePoint.TOP);
        r[7] = new ResizePoint(0.5, 1, ResizePoint.BOTTOM);
        return r;
    }

    /**
     * Returns the background color of the selected window.
     *
     * @return The background color.
     */
    protected Color getSelectedBackgroundColor() {
        return Color.yellow;
    }

    public int getT() {
        return rect[1][1];
    }

    /**
     * This event handler calls <code>add</code> methods to my manager.
     */
    public void handleAddItemEvent(AddItemEvent event) {
        try {
            manager.add(event.getItem());
            (manager).selectItemEx(null, false);
            (manager).repaintItem(event.getItem());
        } catch (ItemNotInsideManagerException e) {
        }
    }

    /**
     * This handler call managers method <code>changeZOrder</code>
     * and the forces the reapinting.
     *
     * @param <code>event</code> Event with all needed values.
     * @see Manager#changeZOrder(cz.green.event.interfaces.Item , boolean)
     * @see ChangeZOrderEvent
     */
    public void handleChangeZOrderEvent(ChangeZOrderEvent event) {
        boolean top = event.getTop();
        manager.changeZOrder(this, top);
        if (top) {
            (manager).repaintItem(this);
        } else {
            Rectangle r = getBounds();
            (manager).repaintRectangle(r.x, r.y, r.width, r.height);
        }
    }

    /**
     * Handle event that informs about moving mouse over.
     * The handler changes the cursor when we are moving over this window.
     *
     * @param <code>event</code> The event with position information.
     */
    public void handleMouseMoveEvent(MouseMoveEvent event) {
        Cursor cursor = new Cursor(Cursor.HAND_CURSOR);
        event.getComponent().setCursor(cursor);
    }

    /**
     * This handler try to move item and repaint it. During moving paint the
     * item by XOR mode and uses <code>paintFast</code>.
     *
     * @param <code>event</code> Event with all needed information.
     * @see MovableItem#move(int, int, boolean)
     * @see paintFast(java.awt.Graphics)
     */
    public void handleMoveEvent(MoveEvent event) {
        if (paintedFast) {
            (manager).repaintItemFast(this);
            paintedFast = false;
        } else {
            rectangle = getBounds();
        }
        try {
            move(event.getDx(), event.getDy(), true);
            if (rectangle != null) {
                Rectangle r = rectangle;
                rectangle = null;
                (manager).repaintRectangle(r.x, r.y, r.width, r.height);
            }
            (manager).repaintItem(this);
        } catch (ItemNotInsideManagerException e) {
        }
    }

    /**
     * If some item is selected paints its during dragging using
     * method <code>paintFast</code> and XOR mode of painting.
     *
     * @param <code>event</code>
     * @see MovableItem#move(int, int, boolean)
     * @see paintFast(java.awt.Graphics)
     */
    public void handleMovingEvent(MovingEvent event) {
        if (paintedFast) {
            (manager).repaintItemFast(this);
        } else {
            paintedFast = true;
            rectangle = getBounds();
        }
        try {
            move(event.getDx(), event.getDy(), false);
        } catch (ItemNotInsideManagerException e) {
        } finally {
            (manager).repaintItemFast(this);
        }
    }

    /**
     * This handler call manager method <code>remove</code>,
     * which caused removing the item. Then forces to repaint
     * the rectangle, where the item was situated.
     *
     * @param <code>event</code> Cary no information.
     * @see Manager#remove(cz.green.event.interfaces.Item)
     */
    public void handleRemoveEvent(RemoveEvent event) {
        Rectangle r = getBounds();
        manager.remove(this);
        (manager).selectItemEx(null, false);
        (manager).repaintRectangle(r.x, r.y, r.width, r.height);
    }

    /**
     * Resize this item according to giving information. Which side to resize,
     * it tells <code>resizeRect</code>. How much resize tells <code>dx</code> and
     * <code>dy</code>.
     *
     * @param <code>event</code> The event.
     * @see ResizeEvent
     * @see ResizeEvent
     * @see cz.green.event.interfaces.MovableItem
     */
    public void handleResizeEvent(ResizeEvent event) {
        if (paintedFast) {
            (manager).repaintItemFast(this);
            paintedFast = false;
        } else {
            rectangle = getBounds();
        }
        try {
            resize(event.getDx(), event.getDy(), event.getResizeRect().direction, true);
            Rectangle r;
            if (rectangle != null) {
                r = rectangle.union(getBounds());
                rectangle = null;
            } else {
                r = getBounds();
            }
            (manager).repaintRectangle(r.x, r.y, r.width, r.height);
        } catch (ItemNotInsideManagerException e) {
        }
    }

    /**
     * Invokes <code>handleResizeEvent</code> fro the right
     * item. Resized can be only the selected item.
     *
     * @param <code>event</code> The event.
     * @see ResizingEvent
     */
    public void handleResizingEvent(ResizingEvent event) {
        if (paintedFast) {
            (manager).repaintItemFast(this);
        } else {
            paintedFast = true;
            rectangle = getBounds();
        }
        try {
            resize(event.getDx(), event.getDy(), event.getResizeRect().direction, false);
        } catch (ItemNotInsideManagerException e) {
        } finally {
            (manager).repaintItemFast(this);
        }
    }

    /**
     * This handler call managers method <code>selectItem</code>
     * and the forces the reapinting. If selects the item then calls
     * <code>changeZOrder</code>.
     *
     * @param <code>event</code> Event with all needed values.
     * @see SelectItemEvent
     * @see PaintableManager#selectItem(cz.green.event.interfaces.SelectableItem , boolean)
     * @see Manager#changeZOrder(cz.green.event.interfaces.Item , boolean)
     */
    public void handleSelectItemEvent(SelectItemEvent event) {
        if (!selectable)
            return;
        boolean selected = manager.selectItem(this, event.getAddItem());
        event.setSelected(selected);
        if (selected) {
            manager.changeZOrder(this, true);
            (manager).repaintItem(this);
        }
    }

    /**
     * This handler call managers method <code>selectItemEx</code>
     * and the forces the reapinting.
     *
     * @param <code>event</code> Event with all needed values.
     * @see SelectItemExEvent
     * @see PaintableManager#selectItemEx(cz.green.event.interfaces.SelectableItem , boolean)
     */
    public void handleSelectItemExEvent(SelectItemExEvent event) {
        (manager).selectItemEx(this, event.getAddItem());
        (manager).repaintItem(this);
    }

    /**
     * This methods looks for right event handler for the <code>event</code> and invokes it.
     * If this finish ok return <code>true</code> other way return
     * <code>false</code>.
     *
     * @param <code>event</code> Specify the event.
     * @return <code>boolean</code>
     *         If everything finish unrubly than returns <code>true</code>.
     *         Otherways, returns <code>false</code>.
     */
    public boolean invokeEventHandler(cz.green.event.interfaces.Event event) {
        java.lang.reflect.Method handler;
        Class[] parameterTypes = new Class[1];
        Object[] parameters = new Object[1];
        String methodName = event.getClass().getName(); //name of the event handler
        //we want olny class name, withou package
        String remainder = methodName.substring(methodName.lastIndexOf('.') + 1);
        //prefix "handle" + event class name
        methodName = "handle" + remainder;
        //prepare parameters types and values
        parameterTypes[0] = event.getClass();
        parameters[0] = event;
        Class cls = this.getClass();
        try {
            while (true) {
                try {
                    //try to find handler
                    handler = cls.getMethod(methodName, parameterTypes);
                    //try to invoke handler
                    handler.invoke(this, parameters);
                    return true;
                } catch (NoSuchMethodException e) {
                    //handler doesn't exist, try to look in super class
                    if ((cls = cls.getSuperclass()) == null) {
                        return false;
                    }
                }
            }
        } catch (java.lang.reflect.InvocationTargetException e) {
            //handler throws some exception
            System.out.println(e);
            return false;
        } catch (IllegalArgumentException e) {
            //wrong parameters
            return false;
        } catch (IllegalAccessException e) {
            //method isn't accessible
            return false;
        } catch (NullPointerException e) {
            //it shouldn't appear
            return false;
        } catch (SecurityException e) {
            //handler isn't accessible
            return false;
        }
    }

    /**
     * Is the event inside the shape. This method knows the event
     * coordinates (<code>x</code>, <code>y</code>)
     * and knows the shapes of this item. Has to decide if the
     * point is inside the shape.
     *
     * @param <code>x</code> <code>X</code> coordinate of the event.
     * @param <code>y</code> <code>Y</code> coordinate of the event.
     */
    public boolean isIn(int x, int y) {
        return true;
    }

    /**
     * Looks in the <code>dimension</code> of the event tree
     * for item that is situated in <code>where</code> according to the
     * <code>interval</code>.
     *
     * @see cz.green.event.interfaces.Item#itemIn(int, int[], int, int)
     */
    public Item itemIn(int dimension, int[] interval, int where, int myPosition) throws cz.green.util.BadDimensionException, ValueOutOfRangeException {
        Item item = this;
        try {
            while (true) {
                if ((where & myPosition) != 0) {
                    //this is the succesor in the event tree
                    return item;
                }
                //now i have to decide: looks left or right
                if (where > myPosition) { //to understand look to constant definition
                    //goes right
                    item = item.rightSuccessor(dimension);
                } else {
                    //goes left
                    item = item.leftSuccessor(dimension);
                }
                if (item == null)
                    return null;
                myPosition = item.position(dimension, interval);
            }
        } catch (IndexOutOfBoundsException e) {
            throw new ValueOutOfRangeException();
        }
    }

    /**
     * Returns the window, which lies left behind is the
     * <code>dimension</code>.
     *
     * @param <code>dimension</code> In which dimension we have to know left successor.
     * @return <code>cz.green.event.engine.EventTree2DItem</code>
     *         Window lieing left behind.
     * @throws <code>cz.green.event.engine.ValueOutOfRangeException</code>
     *          If <code>dimension</code> is less then <code>0</code> or
     *          greater then <code>1</code>.
     */
    public Item leftSuccessor(int dimension) throws ValueOutOfRangeException {
        try {
            return tree[dimension][0];
        } catch (IndexOutOfBoundsException e) {
            throw new ValueOutOfRangeException();
        }
    }

    /**
     * Set the new managewr for this window.
     *
     * @see Item#manager(Manager)
     */
    public Manager manager(Manager manager) throws NullPointerException {
        if (manager == null)
            throw new NullPointerException();
        return (this.manager = manager);
    }

    /**
     * Moves item by specified <code>dx</code> (<code>dy</code>)
     * in horizontal (vertical) direction.
     *
     * @see cz.green.event.interfaces.MovableItem#managerMoves(int, int, boolean)
     */
    public void managerMoves(int dx, int dy, boolean recount) {
        try {
            if (!recount && (hRect == null)) {
                hRect = new int[2][2];
                System.arraycopy(rect[0], 0, hRect[0], 0, 2);
                System.arraycopy(rect[1], 0, hRect[1], 0, 2);
            }
            int[][] r;
            if (hRect != null)
                r = hRect;
            else
                r = rect;
            //move
            IntervalMethods.moveBy(r[0], dx);
            IntervalMethods.moveBy(r[1], dy);
            if (recount) {
                System.arraycopy(r[0], 0, rect[0], 0, 2);
                System.arraycopy(r[1], 0, rect[1], 0, 2);
                hRect = null;
            }
        } catch (BadDimensionException e) {
            //this is very bad;
        }
    }

    /**
     * Makes something like event fall, but look only for the most left
     * item in the specified <code>dimension</code>.
     *
     * @see Item#mostLeft(int)
     */
    public int mostLeft(int dimension) throws ValueOutOfRangeException {
        if (tree[dimension][0] == null)
            return rect[dimension][0];
        else {
            Item step = this, help;
            while ((help = step.leftSuccessor(dimension)) != null)
                step = help;
            return step.mostLeft(dimension);
        }
    }

    /**
     * Makes something like event fall, but look only for the most right
     * item in the specified <code>dimension</code>.
     *
     * @see Item#mostRight(int)
     */
    public int mostRight(int dimension) throws ValueOutOfRangeException {
        if (tree[dimension][1] == null)
            return rect[dimension][1];
        else {
            Item step = this, help;
            while ((help = step.rightSuccessor(dimension)) != null)
                step = help;
            return step.mostRight(dimension);
        }
    }

    /**
     * Moves item by specified <code>dx</code> (<code>dy</code>)
     * in horizontal (vertical) direction.
     *
     * @see MovableItem#move(int, int, boolean)
     */
    public void move(int dx, int dy, boolean recount) throws ItemNotInsideManagerException {
        try {
            if (!recount && (hRect == null)) {
                hRect = new int[2][2];
                System.arraycopy(rect[0], 0, hRect[0], 0, 2);
                System.arraycopy(rect[1], 0, hRect[1], 0, 2);
            }
            int[][] source = (hRect != null) ? hRect : rect;
            int[][] r = new int[2][2];
            System.arraycopy(source[0], 0, r[0], 0, 2);
            System.arraycopy(source[1], 0, r[1], 0, 2);
            //move
            IntervalMethods.moveBy(r[0], dx);
            IntervalMethods.moveBy(r[1], dy);
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
        } catch (BadDimensionException e) {
            throw new ItemNotInsideManagerException();
        }
    }

    /**
     * This method paints this window.
     *
     * @see cz.green.event.interfaces.PaintableItem#paint(java.awt.Graphics)
     */
    public void paint(java.awt.Graphics g) {
        //paint item
        Rectangle r = getBounds();
        if (selected)
            g.setColor(getSelectedBackgroundColor());
        else
            g.setColor(getBackgroundColor());
        g.fillRect(r.x, r.y, r.width, r.height);
        g.setColor(getForegroundColor());
        g.drawRect(r.x, r.y, r.width, r.height);
        r = null;
    }

    /**
     * Paints window bud only board. Don't fills the entire window area.
     *
     * @see PaintableItem#paintFast(java.awt.Graphics)
     */
    public void paintFast(java.awt.Graphics g) {
        Rectangle r = getRealBounds();
        g.drawRect(r.x, r.y, r.width, r.height);
        r = null;
    }

    /**
     * This method says where lies this window in this <code>dimension</code> is
     * according to the <code>interval</code>.
     *
     * @see Item#position(int, int[])
     */
    public int position(int dimension, int[] interval) throws cz.green.util.BadDimensionException, ValueOutOfRangeException {
        try {
            return IntervalMethods.whereIs(interval, rect[dimension]);
        } catch (IndexOutOfBoundsException e) {
            throw new ValueOutOfRangeException();
        }
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
            }
            if (((where & ResizePoint.LEFT) == ResizePoint.LEFT) && (dx <= (source[0][1] - source[0][0]))) {
                IntervalMethods.resizeStartBy(r[0], dx);
            }
            if (((where & ResizePoint.RIGHT) == ResizePoint.RIGHT) && (dx >= (source[0][0] - source[0][1]))) {
                IntervalMethods.resizeEndBy(r[0], dx);
            }
            if (((where & ResizePoint.TOP) == ResizePoint.TOP) && (dy <= (source[1][1] - source[1][0]))) {
                IntervalMethods.resizeStartBy(r[1], dy);
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
     * Returns the window, which lies right behind is the
     * <code>dimension</code>.
     *
     * @param <code>dimension</code> In which dimension we have to know right successor.
     * @return Window lieing right behind.
     * @throws <code>cz.green.event.engine.ValueOutOfRangeException</code>
     *          If <code>dimension</code> is less then <code>0</code> or
     *          greater then <code>1</code>.
     */
    public Item rightSuccessor(int dimension) throws ValueOutOfRangeException {
        try {
            return tree[dimension][1];
        } catch (IndexOutOfBoundsException e) {
            throw new ValueOutOfRangeException();
        }
    }

    /**
     * This method was created by Jiri Mares
     */
    public void select() {
        selected = true;
    }

    /**
     * Returns a String that represents the value of this object.
     *
     * @return a string representation of the receiver
     */
    public String toString() {
        StringBuffer message = new StringBuffer(getClass().getName());
        message.append("[").append(rect[0][0]).append(",").append(rect[1][0]).append("],[");
        message.append(rect[0][1]).append(",").append(rect[1][1]);
        message.append("],@").append(Integer.toHexString(hashCode()));
        return new String(message);
    }

    public boolean isSelectable() {
        return selectable;
    }

    public void setSelectable(boolean selectable) {
        this.selectable = selectable;
    }
}
