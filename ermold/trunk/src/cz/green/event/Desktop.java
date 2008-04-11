package cz.green.event;

import cz.green.event.interfaces.*;
import cz.green.event.interfaces.Event;
import cz.green.util.MoveArrayList;

import javax.swing.*;
import java.awt.*;

/**
 * This is class then can hold windows. Implements all basic functions
 * needed for every item manager inherites from its super class.
 * <p/>
 * This class as atribute some component is the best way to show all functionality.
 */
public class Desktop extends GroupWindow implements ContainerDesktop, java.io.Serializable {
    /**
     * Stores the last <code>SelectItemEvent</code> event. This event is stored because
     * when we clicked in the selected item and made no move, this item is deselected.
     * Whether the item is selected we knows from the last
     * <code>SelectItemEvent</code>, in which returns information whether the aim
     * of the event was selected before falling the event or not. If not this item is selected
     * and th event <code>SelectItemExEvent</code> makes no efect. If the item is selected
     * then when we fall <code>SelectItemExEvent</code> the handler cuse the desection of the item
     * or selecting only this item. It depends on the status of the control key.
     *
     * @see SelectItemEvent
     * @see SelectItemExEvent
     */
    transient protected SelectItemEvent preSelectEvent = null;
    /**
     * This property stores any previous event. It is useful, when we handle
     * <code>MovingEvent</code> and <code>ResizingEvent</code>, because we have to say, who is the
     * receiver of the event.
     *
     * @see MovingEvent
     * @see ResizingEvent
     * @see fallAndHandleEvent(int, int, cz.green.event.interfaces.Event )
     */
    transient protected cz.green.event.interfaces.Event preEvent = null;
    /**
     * The selected items. This items is draw by <code>paintSelected</code>
     * method. This object gives me functionality adding, removing items,
     * removing all items and getting the focused item.
     */
    transient protected SelectedItems selectedItems = null;
    /**
     * This rectangle contains the area that we hace to repaint according to the
     * last change of selected item(s). When we do first repainting (element or
     * rectangle) we repaint also this rectangle.
     *
     * @see selectItem( cz.green.event.interfaces.SelectableItem , boolean)
     * @see selectItemEx( cz.green.event.interfaces.SelectableItem , boolean)
     */
    transient protected Rectangle selRect = null;
    /**
     * In this property we stores the array of resize rectangles. When cursor moves accross,
     * we have to change the cursor to resize cursor. In this situation may user change
     * item size.
     *
     * @see ResizeRectangle
     * @see PaintableItem#getResizePoints()
     */
    transient protected ResizeRectangle[] rects;
    /**
     * This property stores index of the actual using resize rectangle. Using this property
     * and <code>rects</code> we can desided into which direction we change the size
     * of the specified item. It is <code>-1</code> when we use no resize rectangle.
     *
     * @see Desktop.rects
     */
    transient protected int resizing = -1;
    /**
     * This property stores the actual scale rate for displaying all components.
     * The value <code>1</code> indicates no scale. When scale goes up the components
     * are going to be smaler and vice versa.
     */
    protected float scale = 1;
    /**
     * Where is everythink placed.
     */
    transient private ContainerComponent paintPlace = null;

    /**
     * Same as default cobnstructor but sets the left top point and width and height of the desktop.
     *
     * @throws <code>cz.green.event.engine.ImpossibleNegativeValueException</code>
     *          If <code>left</code>, <code>top</code>, <code>width</code> or
     *          <code>height</code> is less than zero.
     */
    public Desktop(ContainerComponent place, int left, int top, int width, int height) {
        super();
        manager = this;
        paintPlace = place;
        wins = new MoveArrayList(getArraySize(), getIncrement());
        selectedItems = new SelectedItems();
//	if ((left < 0) || (top < 0) || (width < 0) || (height < 0))
//		throw new ImpossibleNegativeValueException();
        left = (left < 0) ? 0 : left;
        top = (top < 0) ? 0 : top;
        rect[0][0] = left;
        rect[0][1] = left + width;
        rect[1][0] = top;
        rect[1][1] = top + height;
    }

    /**
     * This method was created by Jiri Mares
     */
    public void deselect() {
        selected = false;
    }

    /**
     * This method serve all events passed to the desktop. For making this
     * uses other properties of the desktop.
     * <p/>
     * When comes <code>ResizingEvent</code> set the property
     * <code>ResizeRect</code>, which identify the resize rectangle by which
     * we resize the item. If passes first <code>ResizingEvent</code> set the
     * <code>receiver</code> the the focused selected item.
     * <p/>
     * When comes first <code>MovingEvent</code> set the <code>receiver</code>
     * to the <code>selected</code>. It caused that all selected items is moved.
     * <p/>
     * When comes <code>SelectItemExEvent</code> then looks to the last
     * <code>SelectItemEvent</code>, whether the item was selected or not.
     * If the item was selected then the <code>SelectItemExEvent</code>
     * ignores.
     * <p/>
     * The event <code>MouseMoveEvent</code> is directly handled by desktop.
     *
     * @see GroupWindow#fallAndHandleEvent(int, int, cz.green.event.interfaces.Event)
     * @see Desktop.preEvent
     * @see Desktop.preSelectEvent
     * @see Desktop.selected
     * @see ResizingEvent
     * @see ResizeRectangle
     * @see GroupWindow.receiver
     * @see SelectedItems#focusedElement()
     * @see MovingEvent
     * @see GroupWindow.receiver
     * @see SelectItemExEvent
     * @see Desktop.preSelectEvent
     * @see MouseMoveEvent
     */
    public boolean fallAndHandleEvent(int x, int y, Event event) {
        Event help = preEvent;
        preEvent = event;
        if (event.passToSelected()) {
            receiver = selectedItems;
            return invokeEventHandler(event);
        }
        if (event.passToFocused()) {
            receiver = selectedItems.focusedElement();
            return invokeEventHandler(event);
        }
        if (event instanceof SelectItemEvent) {
            preSelectEvent = (SelectItemEvent) event; //save it as last SelectItemEvent
        }
        if (event instanceof SelectItemExEvent)
            if ((preSelectEvent == null) || (preSelectEvent.getSelected()))
                //there's no previous SelectItemEvent or the item was selected
                return true;
        if (event instanceof MouseMoveEvent) {
            MouseMoveEvent e = ((MouseMoveEvent) event);
            if (changeResizeCursor(e.getX(), e.getY()))
                return true;
        }
        return isIn(x, y) && invokeEventHandler(event);
    }

    /**
     * This method set the desktop scale and position to can see all selected items.
     *
     * @see ContainerDesktop#fitSelected()
     */
    public void fitSelected() {
        Rectangle selRect = selectedItems.getBounds();
        if (selRect != null) {
            JScrollPane p;
            if (paintPlace.getParent() instanceof JViewport)
                p = (JScrollPane) (paintPlace.getParent().getParent());
            else
                return;
            Dimension dim = p.getSize();
            float scale = getScale();
            Insets ins = p.getInsets();
            dim.width -= ins.left + ins.right;
            dim.height -= ins.top + ins.bottom;
            float sx = ((float) selRect.width) / dim.width * scale;
            float sy = ((float) selRect.height) / dim.height * scale;
            float s = (sx > sy) ? sx : sy;
            if (s > scale) {
                setScale(s);
            }
            selRect = selectedItems.getBounds();
            p.getHorizontalScrollBar().setValue(selRect.x - (dim.width - selRect.width) / 2);
            p.getVerticalScrollBar().setValue(selRect.y - (dim.height - selRect.height) / 2);
        }
    }

    /**
     * This method tests if the event is in some resize rectangle.
     * Useful when we have to know, whether we have to do resizing or
     * moving. When we are over some resize point we have to do Resizing.
     *
     * @param <code>x</code> The x coordinate the mouse position.
     * @param <code>y</code> The y coordinate the mouse position.
     * @return The resize point, where the event is situated or <code>null</code>.
     */
    public ResizeRectangle getActualResizeRect(int x, int y) {
        if (rects != null) {
            ResizeRectangle[] r = rects;
            for (int i = r.length - 1; i >= 0; i--) {
                if (r[i].contains(x, y))
                    return r[resizing = i];
            }
        }
        return null;
    }

    /**
     * Returns the starting window array size.
     *
     * @return <code>int</code> The size.
     */
    private int getArraySize() {
        return 30;
    }

    /**
     * Returns the background color of the desktop.
     *
     * @return The color background color.
     */
    protected Color getBackgroundColor() {
        return Color.RED;
    }

    /**
     * Returns the increment value for window array. If the array is too small
     * to store all items, by this value is expanded.
     *
     * @return <code>int</code> The size.
     */
    private int getIncrement() {
        return 10;
    }

    /**
     * This method was created by Jiri Mares.
     *
     * @return cz.green.event.engine.Container
     */
    public ContainerComponent getPaintPlace() {
        return paintPlace;
    }

    /**
     * This method return all points where should have be placed resize point.
     * Desktop could not be resized visually, therefore returns <code>null</code>.
     *
     * @see ResizePoint
     * @see PaintableItem#getResizePoints()
     */
    public ResizePoint[] getResizePoints() {
        return null;
    }

    /**
     * Returns the size of rectangle that represents the resize point.
     *
     * @return The size (same in x and y coordinate).
     */
    private int getResizePointSize() {
        return 5;
    }

    /**
     * Returns actual scale.
     *
     * @see cz.green.event.interfaces.PaintableManager#getScale()
     */
    public float getScale() {
        return scale;
    }

    /**
     * Returns the background color of the desktop when is seleceted.
     *
     * @return The color background color.
     */
    protected Color getSelectedBackgroundColor() {
        return getBackgroundColor();
    }

    /**
     * Returns the color used for XORing. XOR mode of painting is used
     * when items are painted fast.
     *
     * @return The color for XOR mode.
     */
    public Color getXORColor() {
        return Color.black;
    }

    /**
     * This handler do nothing because the desktop cannot change
     * its z order
     */
    public void handleChangeZOrderEvent(ChangeZOrderEvent event) {
    }

    /**
     * Handle event that informs about moving mouse over.
     * The handler changes the cursor when we are moving over this window.
     *
     * @param <code>event</code> The event with position information.
     */
    public void handleMouseMoveEvent(MouseMoveEvent event) {
        Cursor cursor = new Cursor(Cursor.DEFAULT_CURSOR);
        event.getComponent().setCursor(cursor);
    }

    /**
     * When we try to select desktop, then desktop deselect everything.
     *
     * @param <code>event</code> Event with all needed values.
     */
    public void handleSelectItemEvent(SelectItemEvent event) {
        selectItemEx(null, false);
        rects = null;
        if (selRect != null) {
            Rectangle r = selRect;
            selRect = null;
            repaintRectangle(r.x, r.y, r.width + 1, r.height + 1);
        }
    }

    /**
     * When we try to select desktop, then desktop deselect everything.
     *
     * @param <code>event</code> Event with all needed values.
     */
    public void handleSelectItemExEvent(SelectItemExEvent event) {
        selectItemEx(null, false);
        rects = null;
        if (selRect != null) {
            Rectangle r = selRect;
            selRect = null;
            repaintRectangle(r.x, r.y, r.width + 1, r.height + 1);
        }
    }

    /**
     * Test whether the event is over resize rectangle and if it is, changes the cursor.
     *
     * @param x The x coordinate of the event.
     * @param y The y coordinate of the event.
     * @return Was the cursor changed?
     */
    public boolean changeResizeCursor(int x, int y) {
        int cursorStyle;
        ResizeRectangle r = getActualResizeRect(x, y);
        Cursor cursor;
        if (r == null)
            return false;
        else {
            cursorStyle = r.direction;
            switch (cursorStyle & (ResizePoint.LEFT | ResizePoint.RIGHT | ResizePoint.TOP | ResizePoint.BOTTOM)) {
                case ResizePoint.LEFT:
                    cursor = new Cursor(Cursor.W_RESIZE_CURSOR);
                    break;
                case ResizePoint.RIGHT:
                    cursor = new Cursor(Cursor.E_RESIZE_CURSOR);
                    break;
                case ResizePoint.TOP:
                    cursor = new Cursor(Cursor.N_RESIZE_CURSOR);
                    break;
                case ResizePoint.BOTTOM:
                    cursor = new Cursor(Cursor.S_RESIZE_CURSOR);
                    break;
                case ResizePoint.LEFT | ResizePoint.TOP:
                    cursor = new Cursor(Cursor.NW_RESIZE_CURSOR);
                    break;
                case ResizePoint.LEFT | ResizePoint.BOTTOM:
                    cursor = new Cursor(Cursor.SW_RESIZE_CURSOR);
                    break;
                case ResizePoint.RIGHT | ResizePoint.TOP:
                    cursor = new Cursor(Cursor.NE_RESIZE_CURSOR);
                    break;
                case ResizePoint.RIGHT | ResizePoint.BOTTOM:
                    cursor = new Cursor(Cursor.SE_RESIZE_CURSOR);
                    break;
                default:
                    cursor = Cursor.getDefaultCursor();
            }
            getPaintPlace().setCursor(cursor);
            return true;
        }
    }

    /**
     * This method set paintPlace property and sets property selected
     * to new object SelectedItems.
     *
     * @param c The container where this desktop will be painted.
     * @see Desktop.paintPlace
     * @see Desktop.selected
     * @see SelectedItems
     * @see ContainerComponent
     */
    public void init(ContainerComponent c) {
        paintPlace = c;
    }

    /**
     * Set the new managewr for this window. This method do nothing
     * because desktop cannot change its manager. It's manager to itself.
     */
    public Manager manager(Manager manager) {
        return this;
    }

    /**
     * Paints the desktop and all included groups and items. When everything is painted paints
     * all selected items.
     *
     * @see PaintableItem#paint(java.awt.Graphics)
     */
    public void paint(Graphics g) {
        super.paint(g);
        paintSelectedItem(g);
    }

    /**
     * If exist any selected items, calls <code>paintSelected</code> for this items and
     * draw all resize points. For painting all selected items uses method
     * <code>paintSelected</code> of the class <code>SelectedItems</code>.
     *
     * @param <code>g</code> Graphics where to draw selected item.
     */
    protected void paintSelectedItem(Graphics g) {
        if (!selectedItems.isEmpty()) { //is anythng selected
            //		selected.paintSelected(g); //draw it
            //for focused selected element draw all resize rectangles
            SelectableItem help = selectedItems.focusedElement();
            ResizePoint[] points = help.getResizePoints();
            if (points != null) {
                ResizeRectangle[] rr = rects = new ResizeRectangle[points.length];
                Rectangle r = help.getBounds();
                int size = getResizePointSize();
                for (int i = points.length - 1; i >= 0; i--) {
                    rr[i] = new ResizeRectangle(r.x + ((int) (points[i].x * r.width)) - size / 2, r.y + ((int) (points[i].y * r.height)) - size / 2, size, size, points[i].direction);
                    g.fillRect(rr[i].x, rr[i].y, rr[i].width, rr[i].height);
                }
            }
        }
    }

    /**
     * This method reads object from stream, and is called automaticily by serialization.
     * One more thing to do is to create SelectedItems.
     */
    private void readObject(java.io.ObjectInputStream s) throws java.io.IOException, ClassNotFoundException {
        s.defaultReadObject();
        //set yourself as property change listener
        selectedItems = new SelectedItems();
    }

    /**
     * This method calls the method paint for the <code>item</code>. When we have to repaint
     * a rectangle of selected items then we repaint it now.
     *
     * @see PaintableManager#repaintItem(PaintableItem)
     */
    public void repaintItem(PaintableItem item) {
        Graphics g = getPaintPlace().getGraphics();
        if (selRect != null) {
            //we have some rectangle to repaint
            Rectangle r;
            if (selectedItems.focusedElement() == item) {
                //repaint selRect and item bounds
                r = item.getBounds();
                int size = getResizePointSize();
                r.x -= size / 2;
                r.y -= size / 2;
                r.width += size;
                r.height += size;
                r = r.union(selRect);
            } else
                r = selRect.union(item.getBounds());
            getPaintPlace().repaint(r.x, r.y, r.width + 1, r.height + 1);
            selRect = null;
        } else {
            item.paint(g);
            paintSelectedItem(g);
        }
    }

    /**
     * This method calls the method paintFast for the <code>item</code>.
     */
    public void repaintItemFast(PaintableItem item) {
        Graphics g = getPaintPlace().getGraphics();
        g.setColor(getBackgroundColor());
        g.setXORMode(getXORColor());
        item.paintFast(g);
        g.setPaintMode();
    }

    /**
     * This method forces to repaint the specified rectangle. When we have to repaint
     * a rectangle from selected items we do that now.
     */
    public void repaintRectangle(int x, int y, int width, int height) {
        Rectangle r = new Rectangle(x, y, width + 1, height + 1);
        if (selRect != null) {
            r = r.union(selRect);
            selRect = null;
        }
        if (!selectedItems.isEmpty()) {
            int size = getResizePointSize();
            r.x -= size / 2;
            r.y -= size / 2;
            r.width += size;
            r.height += size;
        }
        getPaintPlace().repaint(r.x, r.y, r.width, r.height);
    }

    /**
     * This method was created by Jiri Mares
     */
    public void select() {
        selected = false;
    }

    /**
     * Selects the specified <code>item</code>.
     *
     * @see Desktop.selected
     */
    public boolean selectItem(SelectableItem item, boolean add) {
        if (selectedItems.containsElement(item)) { //item is already selected -> do nothing
            return false;
        } else {
            if (add) {
                selectedItems.addElement(item);
            } else {
                if (!selectedItems.isEmpty()) {
                    int size = getResizePointSize();
                    Rectangle r = selectedItems.getBounds();
                    r.x -= size / 2;
                    r.y -= size / 2;
                    r.width += size;
                    r.height += size;
                    selRect = r;
                }
                selectedItems.addFirstElement(item);
            }
            return true;
        }
    }

    /**
     * Selects the specified <code>item</code>.
     *
     * @see Desktop.selected
     */
    public void selectItemEx(SelectableItem item, boolean add) {
        boolean set = false;
        if (item == null) {
            if (!add) { //clear all items
                set = true;
                selRect = selectedItems.getBounds();
                selectedItems.addFirstElement(null); //clears the list
            }
        } else {
            if (add) {
                if (!selectedItems.addElement(item)) { //the item was removed
                    set = true;
                    selRect = item.getBounds();
                } /*else {
				paintSelectedItem();
			}*/
            } else { //select only this item
                set = true;
                selRect = selectedItems.getBounds();
                selectedItems.addFirstElement(item);
            }
        }
        if (set && (selRect != null)) {
            int size = getResizePointSize();
            Rectangle r = selRect;
            r.x -= size / 2;
            r.y -= size / 2;
            r.width += size;
            r.height += size;
            selRect = r;
        }
    }

    /**
     * Sets actual scale.
     *
     * @return The actual scale.
     */
    public float setScale(float scale) {
        return (this.scale = scale);
    }
}
