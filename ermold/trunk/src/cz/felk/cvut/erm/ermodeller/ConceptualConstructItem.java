package cz.felk.cvut.erm.ermodeller;

import cz.felk.cvut.erm.conceptual.NotationType;
import cz.felk.cvut.erm.conceptual.beans.Atribute;
import cz.felk.cvut.erm.conceptual.beans.ConceptualConstruct;
import cz.felk.cvut.erm.conceptual.exception.ParameterCannotBeNullException;
import cz.felk.cvut.erm.conceptual.exception.WasNotFoundException;
import cz.felk.cvut.erm.event.*;
import cz.felk.cvut.erm.event.exceptions.ImpossibleNegativeValueException;
import cz.felk.cvut.erm.event.interfaces.Invokable;
import cz.felk.cvut.erm.event.interfaces.Item;
import cz.felk.cvut.erm.event.interfaces.Manager;
import cz.felk.cvut.erm.eventtool.ConnectionLine;
import cz.felk.cvut.erm.eventtool.interfaces.Connection;
import cz.felk.cvut.erm.eventtool.interfaces.ConnectionManager;
import cz.felk.cvut.erm.swing.ShowException;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Vector;


/**
 * This type is ancestor of the classes Entity and Relation. Makes the common functions
 * of these two types. It means all function about atributes and unique keys.
 */
public class ConceptualConstructItem extends ConceptualConstructObject {
    /**
     * All attributes
     */
    protected List<AttributeConstruct> attribs = new java.util.ArrayList<AttributeConstruct>(3);


    /**
     * The same functionality as inhereted constructor.
     *
     * @see ConceptualConstructObject#ConceptualConstructObject(cz.felk.cvut.erm.event.interfaces.Manager , int, int, int, int)
     */
    public ConceptualConstructItem(Manager manager, int left, int top, int width, int height) throws NullPointerException, ImpossibleNegativeValueException {
        super(manager, left, top, width, height);
    }

    /**
     * Looks for instance the <code>Cardinality</code> class which has as the second
     * connection (first is <code>this</code>).
     *
     * @param cc The object to which we looks for the connection.
     * @return The instance of the class <code>Cardinality</code> or <code>null</code>.
     */
    protected CardinalityConstruct cardinalityWith(ConceptualConstructItem cc) {
        for (Connection c : connections) {
            CardinalityConstruct car = null;
            if (c.getOne() instanceof CardinalityConstruct)
                car = (CardinalityConstruct) c.getOne();
            if (c.getTwo() instanceof CardinalityConstruct)
                car = (CardinalityConstruct) c.getTwo();
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
    public AttributeConstruct createAtribute(int left, int top) {
        try {
            //create model - atribute
            ConceptualConstruct cCc = (ConceptualConstruct) getModel();
            Atribute cAtr = cCc.createAtribute();
            //create atribute (view-controller)
            AttributeConstruct atr = new AttributeConstruct(cAtr, this, manager, left, top);
            manager.addItem(atr);
            //repaint atribute
            manager.repaintItem(atr);
            //create connection
            Connection conn = new ConnectionLine(manager, getSchema(), atr, this);
            ((ConnectionManager) manager).addConnection(conn);
            manager.repaintItem(conn);
            assert attribs != null;
            attribs.add(atr);
            cAtr.setPosition(attribs.size());
            final NotationType type = atr.getNotationType();
            if (this instanceof EntityConstruct && type != CHEN) {
                ((EntityConstruct) this).recalculatePositionsOfAtributes();
            }
            return atr;
        } catch (Throwable x) {
            new ShowException(null, "Error", x, true);
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
    protected JPopupMenu createMenu(JPopupMenu menu, PopUpMenuEvent event) {
        super.createMenu(menu, event);
        return menu;
    }

    /**
     * Returns all atributes of this construct
     */
    public Vector<AttributeConstruct> getAtributes() {
        Vector<AttributeConstruct> v = new java.util.Vector<AttributeConstruct>();
        AttributeConstruct atr;
        for (int i = connections.size() - 1; i >= 0; i--) {
            if ((atr = (AttributeConstruct) (connections.get(i)).isConnectedTo(AttributeConstruct.class)) != null)
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
        if (item instanceof AttributeConstruct) {
            if (event.getAdd()) {
                AttributeConstruct atr = (AttributeConstruct) item;
                if (atr.getOwner() != this) {
                    event.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    return;
                }
            }
        }
        if (item instanceof CardinalityConstruct) {
            if (event.getAdd()) {
                CardinalityConstruct car = (CardinalityConstruct) item;
                if (car.connectionTo(this) == null) {
                    event.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    return;
                }
            }
        }
        event.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    /**
     * Handle event when some other object is dragged over.
     * Can work only with atributes (moving atributes between conceptual consatructs) and cardinalities
     * (reconnecting the cardinalities).
     *
     * @see #moveAtribute(AttributeConstruct)
     * @see CardinalityConstruct#reconnect(ConceptualConstructItem)
     */
    public void handleDropAboveEvent(DropAboveEvent event) {
        if (selected && event.getAdd())
            return;
//	event.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        Item item = event.getItem();
        if (item instanceof AttributeConstruct) {
            if (event.getAdd()) {
                //move atribute
                AttributeConstruct atr = (AttributeConstruct) item;
                if (atr.getOwner() != this) {
                    reconnectAtribute(atr);
                    final NotationType type = atr.getNotationType();
                    if (type != CHEN) {
                        if (this instanceof EntityConstruct) ((EntityConstruct) this).recalculatePositionsOfAtributes();
                        if (atr.getOwner() instanceof EntityConstruct)
                            ((EntityConstruct) atr.getOwner()).recalculatePositionsOfAtributes();
                    }
                    event.setDropped(true);
                }
            }
        }
        if (item instanceof CardinalityConstruct) {
            if (event.getAdd()) {
                //reconnect cardinality
                CardinalityConstruct car = (CardinalityConstruct) item;
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
     * @see DGroupTool#handleExMoveEvent(cz.felk.cvut.erm.event.ExMoveEvent)
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
        ((DGroupTool) manager).handleExMoveEvent(event);
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
     * @see DGroupTool#handleExMovingEvent(cz.felk.cvut.erm.event.ExMovingEvent)
     */
    public void handleExMovingEvent(ExMovingEvent event) {
        paintedFast = true;
        ((DGroupTool) manager).handleExMovingEvent(event);
        if (!event.getMove())
            manager.fallAndHandleEvent(event.getX() + event.getDx(), event.getY() + event.getDy(), new DragOverEvent(event, this));
        else
            event.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    /**
     * Do all needful fo destoying the conceptual construct, calls inhereted handler and passes the event to the manager.
     *
     * @see DGroupTool#handleRemoveEvent(cz.felk.cvut.erm.event.RemoveEvent)
     */
    public void handleRemoveEvent(cz.felk.cvut.erm.event.RemoveEvent event) {
        removeCardinalities(event);
        super.handleRemoveEvent(event);
        ((Invokable) manager).invokeEventHandler(event);
    }

    /**
     * Selectes this item and passes the event to the manager.
     *
     * @see cz.felk.cvut.erm.event.interfaces.PaintableManager#selectItem(cz.felk.cvut.erm.event.interfaces.SelectableItem , boolean)
     * @see DGroupTool#handleSelectItemEvent(cz.felk.cvut.erm.event.SelectItemEvent)
     */
    public void handleSelectItemEvent(cz.felk.cvut.erm.event.SelectItemEvent event) {
        boolean selected = manager.selectItem(this, event.getAddItem());
        event.setSelected(selected);
        if (selected) {
            ((DGroupTool) manager).handleSelectItemEvent(event);
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
     * @see #moveAtribute(AttributeConstruct)
     */
    protected void reconnectAllAtributes(ConceptualConstructItem cc) {
        AttributeConstruct atr;
        for (int i = connections.size() - 1; i >= 0; i--) {
            if ((atr = (AttributeConstruct) ((Connection) connections.get(i)).isConnectedTo(AttributeConstruct.class)) != null)
                cc.reconnectAtribute(atr);
            //cc.attribs.addElement(atr);
        }
    }

    /**
     * Reconnects all cardinalities to the new conceptual construct. Used during compacting two entities.
     *
     * @param cc The new participant of the cardinality.
     * @see EntityConstruct#compact(EntityConstruct , cz.felk.cvut.erm.event.CoordinateEvent)
     */
    protected void reconnectAllCardinalities(ConceptualConstructItem cc) {
        Connection c;
        for (int i = connections.size() - 1; i >= 0; i--) {
            c = connections.get(i);
            if (c.getOne() instanceof CardinalityConstruct)
                ((CardinalityConstruct) c.getOne()).reconnect(cc);
            if (c.getTwo() instanceof CardinalityConstruct)
                ((CardinalityConstruct) c.getTwo()).reconnect(cc);
        }
    }

    /**
     * This method moves atribute from one to the second ConceptualConstruct.
     * It means moving it the model, removing from the old construct's manager, putting to the
     * new construct manager and creating the connection to the new owner.
     */
    public void reconnectAtribute(AttributeConstruct atr) {
        try {
            //move model's atribute
            final NotationType type = atr.getNotationType();
            if (type != CHEN && this instanceof RelationConstruct) {
                javax.swing.JOptionPane
                        .showMessageDialog(
                                null,
                                "Refused, can't reconnect atribute to relationship in binary and UML notation",
                                "Reconnect Attribute",
                                javax.swing.JOptionPane.ERROR_MESSAGE);
                return;
            }
            ConceptualConstructItem ccFrom = atr.getOwner();
            ConceptualConstruct cCcFrom = (ConceptualConstruct) (ccFrom.getModel());
            Atribute cAtr = atr.getModel();
            if (ccFrom instanceof EntityConstruct)
//			if(((cz.felk.cvut.erm.conceptual.beans.Entity)((Entity) ccFrom).getModel()).isPrimary())
                if ((atr.getModel()).isPrimary())
                    atr.setPrimary(false);
            atr.moveEnd();
            ccFrom.attribs.remove(atr);
            ((ConceptualConstruct) getModel()).moveAtribute(cCcFrom, cAtr);
            //remove view-controller from the old manager
            atr.removeAtribute(new cz.felk.cvut.erm.event.RemoveEvent(0, 0, null));
//atr.handleRemoveEvent(new cz.felk.cvut.erm.event.RemoveEvent(0, 0, null));
            //sets new owner
            atr.setOwner(this);
            //puts into new manager
            manager.addItem(atr);
            manager.repaintItem(atr);
            //create new connection
            Connection conn = new ConnectionLine(manager, atr.getSchema(), atr, this);
            ((ConnectionManager) manager).addConnection(conn);
            manager.repaintItem(conn);
            attribs.add(atr);
            atr.setPosition(attribs.size());
        } catch (Throwable x) {
            new ShowException(null, "Error", x, true);
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
    public void removeAtribute(AttributeConstruct atr) throws ParameterCannotBeNullException, WasNotFoundException {
        ConceptualConstruct cCc = (ConceptualConstruct) getModel();
        cCc.disposeAtribute(atr.getModel());
    }

    /**
     * This method removes all Cardinalities connected to this object.
     *
     * @param event This remove event is sent to all disposed cardinalities.
     */
    protected void removeCardinalities(cz.felk.cvut.erm.event.RemoveEvent event) {
        CardinalityConstruct car;
        for (int i = connections.size() - 1; i >= 0; i--) {
            if ((car = (CardinalityConstruct) (connections.get(i)).isConnectedTo(CardinalityConstruct.class)) != null)
                car.handleRemoveEvent(event);
        }
    }

    /**
     * @param i
     */
    public AttributeConstruct findAttributeWithPosition(int position) {
        for (Object Attrib : attribs) {
            if (((AttributeConstruct) Attrib).getPosition() == position)
                return (AttributeConstruct) Attrib;
        }
        return null;
    }

    protected java.awt.Point getAbsoluteCenter(ConceptualConstructObject co) {
        return new java.awt.Point((getBounds().x + getBounds().width / 2 + co.getBounds().x + co.getBounds().width / 2) / 2,
                (getBounds().y + getBounds().height / 2 + co.getBounds().y + co.getBounds().height / 2) / 2);
    }

}
