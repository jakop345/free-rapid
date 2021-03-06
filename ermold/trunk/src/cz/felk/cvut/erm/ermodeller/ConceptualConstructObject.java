package cz.felk.cvut.erm.ermodeller;

import cz.felk.cvut.erm.app.Consts;
import cz.felk.cvut.erm.conceptual.NotationType;
import cz.felk.cvut.erm.conceptual.beans.ConceptualObject;
import cz.felk.cvut.erm.conceptual.beans.Schema;
import cz.felk.cvut.erm.dialogs.PropertyListDialog;
import cz.felk.cvut.erm.ermodeller.interfaces.ViewController;
import cz.felk.cvut.erm.event.*;
import cz.felk.cvut.erm.event.exceptions.ImpossibleNegativeValueException;
import cz.felk.cvut.erm.event.interfaces.Manager;
import cz.felk.cvut.erm.event.interfaces.PaintableManager;
import cz.felk.cvut.erm.eventtool.ConnectableWindow;
import cz.felk.cvut.erm.util.ActionAdapter;
import cz.felk.cvut.erm.util.ParamActionAdapter;

import javax.swing.*;
import java.awt.*;

/**
 * The common ancestor to all conceptual elements of the ER schema.
 * <p/>
 * Creates the common functionality as context menu, implements <code>PropertyChangeListener</code> interface and
 * creates the scelet of the <code>getModel</code> method.
 */
public class ConceptualConstructObject extends ConnectableWindow implements ViewController, java.beans.PropertyChangeListener {

    /**
     * Graphic notation - Chan
     */
    public final static NotationType CHEN = NotationType.CHEN;
    /**
     * Graphic notation - Binary
     */
    public final static NotationType BINARY = NotationType.BINARY;
    /**
     * Graphic notation - UML
     */
    public final static NotationType UML = NotationType.UML;

    /**
     * The same functionality as inhereted constructor.
     *
     * @see ConnectableWindow#ConnectableWindow(cz.felk.cvut.erm.event.interfaces.Manager ,int,int,int,int)
     */
    public ConceptualConstructObject(Manager manager, int left, int top, int width, int height) throws NullPointerException, ImpossibleNegativeValueException {
        super(manager, left, top, width, height);
    }

    /**
     * This method was created in VisualAge.
     *
     * @param menu        javax.swing.JPopupMenu
     * @param name        java.lang.String
     * @param destination java.lang.Object
     * @param handlerName java.lang.String
     * @param param       java.lang.Object
     * @param paramType   java.lang.Class
     */
    protected void addMenuItem(JPopupMenu menu, String name, String icon, Object destination, String handlerName) {
        try {
            JMenuItem item = new JMenuItem(name, new ImageIcon(ClassLoader.getSystemResource(icon)));
//		JMenuItem item = new JMenuItem(name, new ImageIcon(icon));
            item.setHorizontalTextPosition(JMenuItem.RIGHT);
            item.addActionListener(new ActionAdapter(destination, handlerName));
            menu.add(item);
        } catch (NoSuchMethodException e) {
            //asdasdas
        }
    }

    /**
     * Help method for painting objects. Sets thinner stroke and sets antialiasing On.
     *
     * @param g graphics context
     * @return previous stroke
     */
    protected Stroke updateStrokeWithAliasing(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        final Stroke strokeBackup = g2.getStroke();
        g2.setStroke(new BasicStroke(Consts.STROKE_WIDTH));
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        return strokeBackup;
    }

    /**
     * Returns graphical object to previous state updated by updateStrokeWithAliasing method
     *
     * @param g            graphics context
     * @param strokeBackup previous stroke value
     * @see updateStrokeWithAliasing
     */
    protected void updateBackupStroke(Graphics g, Stroke strokeBackup) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(strokeBackup);
        // Disable antialiasing for shapes
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_OFF);
    }


    /**
     * This method was created in VisualAge.
     *
     * @param menu        javax.swing.JPopupMenu
     * @param name        java.lang.String
     * @param destination java.lang.Object
     * @param handlerName java.lang.String
     * @param param       java.lang.Object
     * @param paramType   java.lang.Class
     */
    protected void addMenuItem(JPopupMenu menu, String name, String icon, Object destination, String handlerName, Object param, Class paramType) {
        try {
            JMenuItem item = new JMenuItem(name, new ImageIcon(ClassLoader.getSystemResource(icon)));
//		JMenuItem item = new JMenuItem(name, new ImageIcon(icon));
            item.setHorizontalTextPosition(JMenuItem.RIGHT);
            item.addActionListener(new ParamActionAdapter(destination, handlerName, param, paramType));
            menu.add(item);
        } catch (NoSuchMethodException e) {
        }
    }

    /**
     * This method adds items to the context menu, which are specific to the atribute. If the childs wants to display other
     * items in the context menu the best way is to override this methods. Creates only one item - Properties, common to all
     * objects.
     *
     * @param menu  The popup menu where to add the new items.
     * @param event The event, which caused the context menu displaying. Is useful for determing targets of the methods
     *              call.
     * @return The filled menu.
     */
    protected JPopupMenu createMenu(JPopupMenu menu, PopUpMenuEvent event) {
        menu.removeAll();
        //addMenuItem(menu, "Properties", "mProperty.gif", this, "propEditing", new Boolean(true), boolean.class);
        return menu;
    }

    /**
     * This method counts the center between two <code>ConceptualConstructs</code>. Is used during decomposition.
     *
     * @param cc The second conceptual construct (first is <code>this</code>).
     * @return The center point between <code>this</code> and <code>cc</code>
     * @see RelationConstruct#decompose(cz.felk.cvut.erm.event.CoordinateEvent)
     * @see EntityConstruct#decompose(EntityConstruct , cz.felk.cvut.erm.event.interfaces.Manager)
     */
    protected java.awt.Point getCenter(ConceptualConstructObject co) {
        int[][] r1 = getRect(), r2 = co.getRect();
        return new java.awt.Point((r1[0][0] + r2[0][0]) / 2, (r1[1][0] + r2[1][0]) / 2);
    }

    /**
     * Returns the conceptual object's ID
     */
    public int getID() {
        return ((ConceptualObject) getModel()).getID();
    }

    /**
     * Should be overriden to get model object
     *
     * @return <code>nul</code>
     * @see cz.felk.cvut.erm.ermodeller.interfaces.ViewController#getModel()
     */
    public Object getModel() {
        return null;
    }

    /**
     * Return the position in the "real" coordinates, the native coordinates of the schema. is used to count center between
     * two conceptual construct and to layout the ISA childs in ISA parent.
     *
     * @return The real native position and size
     * @see #getCenter(ConceptualConstructItem)
     * @see cz.felk.cvut.erm.event#rect
     * @see cz.felk.cvut.erm.event#hRect
     */
    protected int[][] getRect() {
        return (hRect != null) ? hRect : rect;
    }

    /**
     * Returns the selected background color of all elements in the ER schema.
     */
    protected java.awt.Color getSelectedBackgroundColor() {
        return SELECTED_OBJECT_BACKGROUND_COLOR;
    }

    /**
     * Returns the foreground color  of all elements in the ER schema
     */
    protected java.awt.Color getSelectedForegroundColor() {
        return SELECTED_OBJECT_FOREGROUND_COLOR;
    }

    /**
     * Same as <code>handleMoveEvent</code> but adds functionality falling <code>DropAboveEvent</code>. When the dropping is
     * made, then counts move dx to move element to the original place. At the end restores dx and dy before this handler.
     */
    public void handleExMoveEvent(ExMoveEvent event) {
        DropAboveEvent ev = null;
        int dx = event.getDx(), dy = event.getDy();
        if (!event.getMove()) {
            //fall drop above event
            ev = new DropAboveEvent(event, this);
            manager.fallAndHandleEvent(event.getX() + event.getDx(), event.getY() + event.getDy(), ev);
            //when dropped then move back to the original place
            if (ev.getDropped() && hRect != null) {
                event.setDx(rect[0][0] - hRect[0][0]);
                event.setDy(rect[1][0] - hRect[1][0]);
            }
        }
        //handle move
        super.handleMoveEvent(event);
        //restores original dx and dy
        if ((ev != null) && (ev.getDropped())) {
            event.setDx(dx);
            event.setDy(dy);
        }
    }

    /**
     * Same as <code>handleMovingEvent</code> but adds functionality falling <code>DragOverEvent</code>.
     */
    public void handleExMovingEvent(ExMovingEvent event) {
        super.handleMovingEvent(event);
        if (!event.getMove())
            manager.fallAndHandleEvent(event.getX() + event.getDx(), event.getY() + event.getDy(), new DragOverEvent(event, this));
        else
            event.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    /**
     * Handle popup menu event to create menu by method <code>createMenu</code>, adds at the end of the menu item to delete
     * element and shows the menu.
     *
     * @see #createMenu(java.awt.PopupMenu, PopUpMenuEvent)
     */
    public void handlePopUpMenuEvent(PopUpMenuEvent event) {
        JPopupMenu menu = event.getMenu();
        //construct the menu
        createMenu(menu, event);
        //adds the delete item
        addMenuItem(menu, "Delete", "img/mDelete.gif", this, "handleRemoveEvent", new RemoveEvent(event.getX(), event.getY(), event.getComponent()), RemoveEvent.class);
        //show menu
        menu.show(event.getComponent(), event.getRealX(), event.getRealY());
    }

    /**
     * This handler call managers method <code>selectItem</code> and the forces the reapinting. If selects the item then
     * calls <code>changeZOrder</code>.
     *
     * @param <code>event</code> Event with all needed values.
     * @see SelectItemEvent
     * @see PaintableManager#selectItem(cz.felk.cvut.erm.event.interfaces.SelectableItem ,boolean)
     * @see Manager#changeZOrder(cz.felk.cvut.erm.event.interfaces.Item ,boolean)
     */
    public void handleSelectItemEvent(cz.felk.cvut.erm.event.SelectItemEvent event) {
        super.handleSelectItemEvent(event);
        if (!event.getAddItem()) {
            propEditing(false);
        }
    }

    /**
     * Caused the showing the dialog to edit properties of the model object. Model object is got by <code>getModel</code>.
     * This method is invoken when the item Properties is selected.
     *
     * @see #getModel()
     */
    public void propEditing(boolean show) {
        if (getModel() != null)
            PropertyListDialog.setBeanAndEdit(getModel(), ((ConceptualObject) getModel()).getName());
    }

    /**
     * Invoken when some propertie of the model object is changed and caused repainting of the object.
     */
    public void propertyChange(java.beans.PropertyChangeEvent e) {
        java.awt.Rectangle b = getBounds();
        (manager).repaintRectangle(b.x, b.y, b.width, b.height);
    }

    /**
     * Sets conceptual object's ID
     */
    public void setID(int id) {
        ((ConceptualObject) getModel()).setID(id);
    }

    /**
     * Writes data for conceptual object into XML file
     *
     * @param pw java.io.PrintWriter
     */
    public void write(java.io.PrintWriter pw) {
        pw.print("\t\t<left>");
        pw.print(rect[0][0]);
        pw.println("</left>");
        pw.print("\t\t<top>");
        pw.print(rect[1][0]);
        pw.println("</top>");
        pw.print("\t\t<width>");
        pw.print(rect[0][1] - rect[0][0]);
        pw.println("</width>");
        pw.print("\t\t<height>");
        pw.print(rect[1][1] - rect[1][0]);
        pw.println("</height>");
        ConceptualObject co = (ConceptualObject) getModel();
        co.write(pw);
    }

    public Schema getSchema() {
        return ((ConceptualObject) this.getModel()).getSchema();
    }

    public NotationType getNotationType() {
        return ((ConceptualObject) this.getModel()).getSchema().getNotationType();
    }

    public int getLevelOfDetails() {
        return ((ConceptualObject) this.getModel()).getSchema().getLevelOfDetails();
    }
}
