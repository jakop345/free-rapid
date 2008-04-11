package cz.green.ermodeller;

import cz.green.eventtool.Connection;
import cz.green.eventtool.ConnectionLine;
import cz.green.eventtool.ConnectionManager;
import cz.green.swing.ShowException;
import cz.green.event.interfaces.Invokable;
import cz.green.event.interfaces.PaintableManager;
import cz.green.event.interfaces.Item;
import cz.green.event.interfaces.Manager;
import cz.green.event.exceptions.ImpossibleNegativeValueException;
import cz.omnicom.ermodeller.conceptual.exception.ParameterCannotBeNullException;
import cz.omnicom.ermodeller.conceptual.exception.WasNotFoundException;

import javax.swing.*;
import java.util.Vector;


/**
 * This type is ancestor of the classes Entity and Relation. Makes the common functions
 * of these two types. It means all function about atributes and unique keys.
 */
public class ConceptualConstruct extends ConceptualObject {
    /**
     * All attributes
     */
    protected java.util.Vector Attribs = new java.util.Vector(3, 2);


    /**
     * The same functionality as inhereted constructor.
     *
     * @see ConceptualObject#ConceptualObject(cz.green.event.interfaces.Manager , int, int, int, int)
     */
    public ConceptualConstruct(Manager manager, int left, int top, int width, int height) throws NullPointerException, ImpossibleNegativeValueException {
        super(manager, left, top, width, height);
    }

    /**
     * Looks for instance the <code>Cardinality</code> class which has as the second
     * connection (first is <code>this</code>).
     *
     * @param cc The object to which we looks for the connection.
     * @return The instance of the class <code>Cardinality</code> or <code>null</code>.
     */
    protected Cardinality cardinalityWith(ConceptualConstruct cc) {
        java.util.Enumeration e = connections.elements();
        while (e.hasMoreElements()) {
            Connection c = ((Connection) e.nextElement());
            Cardinality car = null;
            if (c.getOne() instanceof Cardinality)
                car = (Cardinality) c.getOne();
            if (c.getTwo() instanceof Cardinality)
                car = (Cardinality) c.getTwo();
            if (car != null) {
                if (car.connectionTo(cc) != null)
                    return car;
            }
        }
        return null;
    }

    /**
     * Creates the new atribute to this object. It includes the model and connection creation.
     *
     * @param left The x coordinate of the left top point of the new atribute.
     * @param top  The y coordinate of the left top point of the new atribute.
     * @return The new created atribute.
     */
    public Atribute createAtribute(int left, int top) {
        try {
            //create model - atribute
            cz.omnicom.ermodeller.conceptual.ConceptualConstruct cCc = (cz.omnicom.ermodeller.conceptual.ConceptualConstruct) getModel();
            cz.omnicom.ermodeller.conceptual.Atribute cAtr = cCc.createAtribute();
            //create atribute (view-controller)
            Atribute atr = new Atribute(cAtr, this, manager, left, top);
            manager.add(atr);
            //repaint atribute
            ((PaintableManager) manager).repaintItem(atr);
            //create connection
            Connection conn = new ConnectionLine(manager, atr, this);
            ((ConnectionManager) manager).addConnection(conn);
            ((PaintableManager) manager).repaintItem(conn);
            if (Attribs == null)
                Attribs = new java.util.Vector(3, 2);
            Attribs.addElement(atr);
            cAtr.setPosition(Attribs.size());
            if (this instanceof Entity && ConceptualConstruct.ACTUAL_NOTATION != ConceptualConstruct.CHEN) {
                ((Entity) this).recalculatePositionsOfAtributes();
            }
            return atr;
        } catch (Throwable x) {
            ShowException d = new ShowException(null, "Error", x, true);
        }
        return null;
    }

    /**
     * This method adds items to the context menu, which are specific to the both entities and relations.
     *
     * @param menu  The popup menu where to add the new items.
     * @param event The event, which caused the context menu displaying. Is useful for determing targets of the
     *              methods call.
     * @return The filled menu.
     */
    protected JPopupMenu createMenu(JPopupMenu menu, PopupMenuEvent event) {
        super.createMenu(menu, event);
        return menu;
    }

    /**
     * Returns all atributes of this construct
     */
    public Vector<Atribute> getAtributes() {
        Vector<Atribute> v = new java.util.Vector<Atribute>();
        Atribute atr;
        for (int i = connections.size() - 1; i >= 0; i--) {
            if ((atr = (Atribute) ((Connection) connections.elementAt(i)).isConnectedTo(Atribute.class)) != null)
                v.add(atr);
        }
        return v;
    }

    /**
     * Handle event when some other object is dragged over.
     * Can work only with atributes (moving atributes between conceptual consatructs) and cardinalities
     * (reconnecting the cardinalities).
     */
    public void handleDragOverEvent(DragOverEvent event) {
        if (selected && event.getAdd())
            return;
        Item item = event.getItem();
        if (item instanceof Atribute) {
            if (event.getAdd()) {
                Atribute atr = (Atribute) item;
                if (atr.getOwner() != this) {
                    event.getComponent().setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
                    return;
                }
            }
        }
        if (item instanceof Cardinality) {
            if (event.getAdd()) {
                Cardinality car = (Cardinality) item;
                if (car.connectionTo(this) == null) {
                    event.getComponent().setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
                    return;
                }
            }
        }
        event.getComponent().setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
    }

    /**
     * Handle event when some other object is dragged over.
     * Can work only with atributes (moving atributes between conceptual consatructs) and cardinalities
     * (reconnecting the cardinalities).
     *
     * @see #moveAtribute(cz.green.ermodeller.Atribute)
     * @see Cardinality#reconnect(cz.green.ermodeller.ConceptualConstruct)
     */
    public void handleDropAboveEvent(DropAboveEvent event) {
        if (selected && event.getAdd())
            return;
//	event.getComponent().setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        Item item = event.getItem();
        if (item instanceof Atribute) {
            if (event.getAdd()) {
                //move atribute
                Atribute atr = (Atribute) item;
                if (atr.getOwner() != this) {
                    reconnectAtribute(atr);
                    if (ACTUAL_NOTATION != CHEN) {
                        if (this instanceof Entity) ((Entity) this).recalculatePositionsOfAtributes();
                        if (atr.getOwner() instanceof Entity)
                            ((Entity) atr.getOwner()).recalculatePositionsOfAtributes();
                    }
                    event.setDropped(true);
                }
            }
        }
        if (item instanceof Cardinality) {
            if (event.getAdd()) {
                //reconnect cardinality
                Cardinality car = (Cardinality) item;
                if (car.connectionTo(this) == null) {
                    car.reconnect(this);
                    event.setDropped(true);
                }
            }
        }
    }

    /**
     * The same handling as by ConnectableWindow but the moving is passed to the manager
     *
     * @see DGroup#handleExMoveEvent(cz.green.ermodeller.ExMoveEvent)
     */
    public void handleExMoveEvent(ExMoveEvent event) {
        DropAboveEvent ev = null;
        int dx = event.getDx(), dy = event.getDy();
        if (!event.getMove()) {
            //pass drop event
            ev = new DropAboveEvent(event, this);
            manager.fallAndHandleEvent(event.getX() + event.getDx(), event.getY() + event.getDy(), ev);
            if (ev.getDropped() && hRect != null) {
                //if dropped --> count dx to move back to the original position
                event.setDx(rect[0][0] - hRect[0][0]);
                event.setDy(rect[1][0] - hRect[1][0]);
            }
        }
        //passed event to the manager
        ((DGroup) manager).handleExMoveEvent(event);
        //get back the original dx
        paintedFast = false;
        if ((ev != null) && (ev.getDropped())) {
            event.setDx(dx);
            event.setDy(dy);
        }
    }

    /**
     * The same handling as by ConnectableWindow but the moving is passed to the manager
     *
     * @see DGroup#handleExMovingEvent(cz.green.ermodeller.ExMovingEvent)
     */
    public void handleExMovingEvent(ExMovingEvent event) {
        paintedFast = true;
        ((DGroup) manager).handleExMovingEvent(event);
        if (!event.getMove())
            manager.fallAndHandleEvent(event.getX() + event.getDx(), event.getY() + event.getDy(), new DragOverEvent(event, this));
        else
            event.getComponent().setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
    }

    /**
     * Do all needful fo destoying the conceptual construct, calls inhereted handler and passes the event to the manager.
     *
     * @see DGroup#handleRemoveEvent(cz.green.event.RemoveEvent)
     */
    public void handleRemoveEvent(cz.green.event.RemoveEvent event) {
        removeCardinalities(event);
        super.handleRemoveEvent(event);
        ((Invokable) manager).invokeEventHandler(event);
    }

    /**
     * Selectes this item and passes the event to the manager.
     *
     * @see cz.green.event.interfaces.PaintableManager#selectItem(cz.green.event.interfaces.SelectableItem , boolean)
     * @see DGroup#handleSelectItemEvent(cz.green.event.SelectItemEvent)
     */
    public void handleSelectItemEvent(cz.green.event.SelectItemEvent event) {
        boolean selected = manager.selectItem(this, event.getAddItem());
        event.setSelected(selected);
        if (selected) {
            ((DGroup) manager).handleSelectItemEvent(event);
        }
        if (!event.getAddItem()) {
            propEditing(false);
        }
    }

    /**
     * Moves all atributes from <code>this</code> to the new owner - <code>cc</code>.
     * Each atribute is moved by <code>moveAtribute</code>.
     *
     * @param cc The new owner of all atributes.
     * @see #moveAtribute(Atribute)
     */
    protected void reconnectAllAtributes(ConceptualConstruct cc) {
        Atribute atr;
        for (int i = connections.size() - 1; i >= 0; i--) {
            if ((atr = (Atribute) ((Connection) connections.elementAt(i)).isConnectedTo(Atribute.class)) != null)
                cc.reconnectAtribute(atr);
            //cc.Attribs.addElement(atr);
        }
    }

    /**
     * Reconnects all cardinalities to the new conceptual construct. Used during compacting two entities.
     *
     * @param cc The new participant of the cardinality.
     * @see Entity#compact(cz.green.ermodeller.Entity, cz.green.event.CoordinateEvent)
     */
    protected void reconnectAllCardinalities(ConceptualConstruct cc) {
        Connection c;
        for (int i = connections.size() - 1; i >= 0; i--) {
            c = (Connection) connections.elementAt(i);
            if (c.getOne() instanceof Cardinality)
                ((Cardinality) c.getOne()).reconnect(cc);
            if (c.getTwo() instanceof Cardinality)
                ((Cardinality) c.getTwo()).reconnect(cc);
        }
    }

    /**
     * This method moves atribute from one to the second ConceptualConstruct.
     * It means moving it the model, removing from the old construct's manager, putting to the
     * new construct manager and creating the connection to the new owner.
     */
    public void reconnectAtribute(Atribute atr) {
        try {
            //move model's atribute
            if (ACTUAL_NOTATION != CHEN && this instanceof Relation) {
                javax.swing.JOptionPane
                        .showMessageDialog(
                                null,
                                "Refused, can't reconnect atribute to relationship in binary and UML notation",
                                "Reconnect Attribute",
                                javax.swing.JOptionPane.ERROR_MESSAGE);
                return;
            }
            ConceptualConstruct ccFrom = atr.getOwner();
            cz.omnicom.ermodeller.conceptual.ConceptualConstruct cCcFrom = (cz.omnicom.ermodeller.conceptual.ConceptualConstruct) (ccFrom.getModel());
            cz.omnicom.ermodeller.conceptual.Atribute cAtr = (cz.omnicom.ermodeller.conceptual.Atribute) (atr.getModel());
            if (ccFrom instanceof Entity)
//			if(((cz.omnicom.ermodeller.conceptual.Entity)((Entity) ccFrom).getModel()).isPrimary())
                if (((cz.omnicom.ermodeller.conceptual.Atribute) atr.getModel()).isPrimary())
                    atr.setPrimary(false);
            atr.moveEnd();
            ccFrom.Attribs.removeElement(atr);
            ((cz.omnicom.ermodeller.conceptual.ConceptualConstruct) getModel()).moveAtribute(cCcFrom, cAtr);
            //remove view-controller from the old manager
            atr.removeAtribute(new cz.green.event.RemoveEvent(0, 0, null));
//atr.handleRemoveEvent(new cz.green.event.RemoveEvent(0, 0, null));
            //sets new owner
            atr.setOwner(this);
            //puts into new manager
            manager.add(atr);
            ((PaintableManager) manager).repaintItem(atr);
            //create new connection
            Connection conn = new ConnectionLine(manager, atr, this);
            ((ConnectionManager) manager).addConnection(conn);
            ((PaintableManager) manager).repaintItem(conn);
            Attribs.addElement(atr);
            atr.setPosition(Attribs.size());
        } catch (Throwable x) {
            ShowException d = new ShowException(null, "Error", x, true);
        }
    }

    /**
     * Disposes the existed atribute of this <code>conceptualConstruct</code>. Also disposes the
     * atribute in the model objects.
     *
     * @param atr The disposed atribute.
     * @throws <code>cz.omnicom.ermodeller.conceptual.ParameterCannotBeNull</code>
     *          Thrown by model object.
     * @throws <code>cz.omnicom.ermodeller.conceptual.WasNotFound</code>
     *          Thrown by model object.
     */
    public void removeAtribute(Atribute atr) throws ParameterCannotBeNullException, WasNotFoundException {
        cz.omnicom.ermodeller.conceptual.ConceptualConstruct cCc = (cz.omnicom.ermodeller.conceptual.ConceptualConstruct) getModel();
        cCc.disposeAtribute((cz.omnicom.ermodeller.conceptual.Atribute) atr.getModel());
    }

    /**
     * This method removes all Cardinalities connected to this object.
     *
     * @param event This remove event is sent to all disposed cardinalities.
     */
    protected void removeCardinalities(cz.green.event.RemoveEvent event) {
        Cardinality car;
        for (int i = connections.size() - 1; i >= 0; i--) {
            if ((car = (Cardinality) (((Connection) connections.elementAt(i))).isConnectedTo(Cardinality.class)) != null)
                car.handleRemoveEvent(event);
        }
    }

    /**
     * @param i
     */
    public Atribute findAttributeWithPosition(int position) {
        for (int i = 0; i < Attribs.size(); i++) {
            if (((Atribute) Attribs.get(i)).getPosition() == position)
                return (Atribute) Attribs.get(i);
        }
        return null;
    }

    protected java.awt.Point getAbsoluteCenter(ConceptualObject co) {
        return new java.awt.Point((getBounds().x + getBounds().width / 2 + co.getBounds().x + co.getBounds().width / 2) / 2,
                (getBounds().y + getBounds().height / 2 + co.getBounds().y + co.getBounds().height / 2) / 2);
    }

}
