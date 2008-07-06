package cz.felk.cvut.erm.ermodeller;

import cz.felk.cvut.erm.conceptual.beans.Atribute;
import cz.felk.cvut.erm.conceptual.beans.UniqueKey;
import cz.felk.cvut.erm.event.exceptions.ImpossibleNegativeValueException;
import cz.felk.cvut.erm.event.interfaces.Item;
import cz.felk.cvut.erm.event.interfaces.Manager;
import cz.felk.cvut.erm.eventtool.ConnectionLine;
import cz.felk.cvut.erm.eventtool.interfaces.Connectable;
import cz.felk.cvut.erm.eventtool.interfaces.Connection;
import cz.felk.cvut.erm.eventtool.interfaces.ConnectionManager;
import cz.felk.cvut.erm.swing.ShowException;

import javax.swing.*;
import java.awt.*;
import java.util.Vector;

/**
 * This class represents the unique key. Every unikey key has its model - object
 * from the Aleš Kopecký work, owner - entity or relation. When the owner is
 * entity then the unique key can be primary.
 */
public class UniqueKeyConstruct extends ConceptualConstructObject {
    /**
     * The owner of the unique key
     */
    EntityConstruct ent = null;

    /**
     * The model object from the Aleš Kopecký work
     */
    UniqueKey model = null;

    /**
     * Determines whether the unique key is primary
     */
    protected boolean primary = false;

    /**
     * Creates new unique key - sets the model, owner and also set as
     * propertyChangeListener.
     *
     * @param uq      The model object - unique key.
     * @param cc      The owner of the unique key.
     * @param manager Manager into which is new atribute put.
     * @param left    The x coordinate of the left top point of the new unique key.
     * @param top     The y coordinate of the left top point of the new unique key.
     * @throws <code>java.lang.NullPointerException</code>
     *          Thrown by inherited
     *          constructor.
     * @throws <code>cz.green.event.ImpossibleNegativeValueException</code>
     *          Thrown by inherited constructor.
     * @see ConceptualConstructObject#ConceptualConstructObject(cz.felk.cvut.erm.event.interfaces.Manager , int, int,
     *      int, int)
     */
    public UniqueKeyConstruct(UniqueKey uq, EntityConstruct ent,
                              Manager manager, int left, int top)
            throws NullPointerException,
            ImpossibleNegativeValueException {
        super(manager, left, top, 10, 10);
        this.ent = ent;
        uq.addPropertyChangeListener(this);
        model = uq;
    }

    /**
     * Adds the atribute as the participant on this unique key. It means
     * creating the new connection between atribute and this unique key.
     *
     * @param atr The added atribute.
     */
    public void addAtribute(AttributeConstruct atr) {
        try {
            model.addAtribute((Atribute) (atr
                    .getModel()));
            Connection conn = new ConnectionLine(manager, getSchema(), this, atr);
            ((ConnectionManager) manager).addConnection(conn);
            (manager).repaintItem(conn);
        } catch (Throwable x) {
            new ShowException(null, "Error", x, true);
        }
    }

    /**
     * Adds the atribute as the participant on this unique key. It means
     * creating the new connection between atribute and this unique key.
     *
     * @param atr The added atribute.
     */

    public void addingAtribute(EntityConstruct ent) {
        try {
            // ((ContainerToolComponent)manager).addingAtribute(ent);
            WorkingDesktop d = (WorkingDesktop) ((DGroupTool) manager).getManager();
        } catch (Throwable x) {
            new ShowException(null, "Error", x, true);
        }
    }

    /**
     * Test whether there is other strong addiction parent than <code>ent</code>.
     * Used during compacting two entities.
     *
     * @param ent The entity.
     * @return <code>true</code> if there is at least one other strong
     *         addiction parent.
     */
    public boolean areOthersConnections(EntityConstruct ent) {
        StrongAddiction sa;
        int count = 0;
        for (int i = connections.size() - 1; i >= 0; i--) {
            if ((sa = (StrongAddiction) ((Connection) connections.elementAt(i))
                    .isConnectedTo(StrongAddiction.class)) != null) {
                if (sa.getEntity() != ent) {
                    return true;
                } else {
                    count++;
                }
            }
        }
        return (count != 1);
    }

    /**
     * This method adds items to the context menu, which are specific to the
     * unique key.
     *
     * @param menu  The popup menu where to add the new items.
     * @param event The event, which caused the context menu displaying. Is useful
     *              for determing targets of the methods call.
     * @return The filled menu.
     */
    protected JPopupMenu createMenu(JPopupMenu menu, PopupMenuEvent event) {
        super.createMenu(menu, event);
        if (hasAtribute()) {
            addMenuItem(menu, "Remove atribute", "img/mDisconnect.gif", event
                    .getComponent(), "removing", this,
                    Item.class);
        }
        if (getPrimary())
            addMenuItem(menu, "Reset primary", "img/mResetPrimary.gif", this,
                    "resetPrimary");
        else
            addMenuItem(menu, "Set primary", "img/mSetPrimary.gif", this,
                    "setPrimary");
        // addMenuItem(menu, "Add atribute", "mAtribute.gif", this,
        // "addingAtribute", getOwner(), cz.felk.cvut.erm.ermodeller.Entity.class);
        return menu;
    }

    /**
     * This method was created by Jiri Mares
     *
     * @return boolean
     */
    public Vector<Connectable> getAtributes() {
        Vector<Connectable> v = new java.util.Vector<Connectable>();

        for (int i = connections.size() - 1; i >= 0; i--) {
            if (((Connection) connections.elementAt(i))
                    .isConnectedTo(AttributeConstruct.class) != null) {
                v.add(((ConnectionLine) connections.elementAt(i)).getTwo());
            }
        }
        return v;
    }

    /**
     * Returns the model of the unique key.
     *
     * @return The model.
     */
    public Object getModel() {
        return model;
    }

    /**
     * Returns the owner of the unique key.
     *
     * @return The owner.
     */
    public EntityConstruct getOwner() {
        return ent;
    }

    /**
     * Returns the primary property.
     *
     * @return <code>true</code> if the unique key is primary.
     */
    public boolean getPrimary() {
        return primary;
    }

    /**
     * Unique key can't be resized therefore return <code>null</code>.
     */
    public cz.felk.cvut.erm.event.ResizePoint[] getResizePoints() {
        return null;
    }

    /**
     * Handle event when some other object is dragged over. Can work only with
     * atributes (adding to (removing from) the unique key) and entities (add
     * (remove) strong addiction).
     */
    public void handleDragOverEvent(DragOverEvent event) {
        if (selected && event.getAdd())
            return;
        Item item = event.getItem();
        if (item instanceof AttributeConstruct) {
            if (event.getAdd()) {
                AttributeConstruct atr = (AttributeConstruct) item;
                if ((atr.connectionTo(this) == null)
                        && (atr.getOwner() == getOwner())) {
                    event.getComponent().setCursor(
                            Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    return;
                }
            } else {
                if (connectionTo(item) != null) {
                    event.getComponent().setCursor(
                            Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    return;
                }
            }
        }
        if (item instanceof EntityConstruct) {
            if (event.getAdd()) {
                EntityConstruct ent = (EntityConstruct) item;
                if (getPrimary() && (connectionTo(ent) == null)) {
                    event.getComponent().setCursor(
                            Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    return;
                }
            }
        }
        if (item instanceof StrongAddiction) {
            StrongAddiction sa = (StrongAddiction) item;
            if (event.getAdd() && getPrimary() && (sa.getUniqueKey() != this)) {
                event.getComponent().setCursor(
                        Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                return;
            }
        }
        event.getComponent().setCursor(
                Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    /**
     * Handle event when some other object is dragged over. Can work only with
     * atributes (adding to (removing from) the unique key) and entities (add
     * (remove) strong addiction).
     */
    public void handleDropAboveEvent(DropAboveEvent event) {
        if (selected && event.getAdd())
            return;
        Item item = event.getItem();
        event.getComponent().setCursor(
                Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        if (item instanceof AttributeConstruct) {
            // over is atribute
            if (event.getAdd()) {
                // add to the unique key
                AttributeConstruct atr = (AttributeConstruct) item;
                if ((atr.connectionTo(this) == null)
                        && (atr.getOwner() == getOwner())) {
                    addAtribute((AttributeConstruct) item);
                    event.setDropped(true);
                }
            } else {
                // remove from unique key
                Connection conn = connectionTo(item);
                if (conn != null) {
                    try {
                        model
                                .removeAtribute((Atribute) (((AttributeConstruct) item)
                                        .getModel()));
                        conn.disconnect();
                    } catch (Throwable x) {
                        new ShowException(null, "Error", x,
                                true);
                    }
                }
            }
        }
        if (item instanceof EntityConstruct) {
            // over is entity
            EntityConstruct ent = (EntityConstruct) item;
            if (event.getAdd()) {
                // adding strong addiction parent
                if (getPrimary() && (connectionTo(ent) == null)) {
                    ((DesktopContainer) event.getComponent())
                            .addingStrongAddiction(new StrongAddictionPair(ent,
                                    this));
                    event.setDropped(true);
                }
            }
        }
        if (item instanceof StrongAddiction) {
            StrongAddiction sa = (StrongAddiction) item;
            // over is atribute
            if (event.getAdd() && getPrimary() && (sa.getUniqueKey() != this)) {
                // reconnect strong addiction
                sa.reconnectStrongAddictionChild(this);
                event.setDropped(true);
            }
        }
    }

    /**
     * When removing unique key, then we have to remove all strong addictions
     * (method <code>disposeStrongAddiction</code>), dispose unique key int
     * model objects and call inherited handling method.
     *
     * @see #disposeStrongAddiction()
     */
    public void handleRemoveEvent(cz.felk.cvut.erm.event.RemoveEvent event) {
        try {
            removeAllStrongAddiction(event);
//PŠ			ent.removeUniqueKey(this);
            super.handleRemoveEvent(event);
        } catch (Throwable x) {
            new ShowException(null, "Error", x, true);
        }
    }

    /**
     * This method was created by Jiri Mares
     *
     * @return boolean
     */
    protected boolean hasAtribute() {
        for (int i = connections.size() - 1; i >= 0; i--) {
            if (((Connection) connections.elementAt(i))
                    .isConnectedTo(AttributeConstruct.class) != null) {
                return true;
            }
        }
        return false;
    }

    /**
     * Paints the unique key as cube. When is primary then has painted two
     * crossed lines.
     */
    public void paint(java.awt.Graphics g) {
        if (ent.getModel().getSchema().getNotationType() == ConceptualConstructItem.BINARY)
            return;
        final Stroke stroke = updateStrokeWithAliasing(g);
        // paints the rectangle
        java.awt.Rectangle r = getBounds();
        if (selected)
            g.setColor(getSelectedBackgroundColor());
        else
            g.setColor(getBackgroundColor());
        g.fillRect(r.x, r.y, r.width, r.height);
        g.setColor(getForegroundColor());
        g.drawRect(r.x, r.y, r.width, r.height);
        // if primary draws two cross lines
        if (getPrimary()) {
            g.drawLine(r.x, r.y, r.x + r.width, r.y + r.height);
            g.drawLine(r.x + r.width, r.y, r.x, r.y + r.height);
        }
        r = null;
        updateBackupStroke(g, stroke);
    }

    /**
     * Prints the unique key as cube. When is primary then has printed two
     * crossed lines.
     */
    public void print(java.awt.Graphics g) {
        if (ent.getModel().getSchema().getNotationType() == ConceptualConstructItem.BINARY)
            return;
        java.awt.Rectangle r = getBounds();
        g.drawRect(r.x, r.y, r.width, r.height);
        if (getPrimary()) {
            g.drawLine(r.x, r.y, r.x + r.width, r.y + r.height);
            g.drawLine(r.x + r.width, r.y, r.x, r.y + r.height);
        }
        r = null;
    }

    /**
     * This method was created by Jiri Mares
     *
     * @param s java.io.ObjectInputStream
     * @throws java.io.IOException The exception description.
     * @throws java.lang.ClassNotFoundException
     *                             The exception description.
     */
    private void readObject(java.io.ObjectInputStream s)
            throws java.io.IOException, ClassNotFoundException {
        s.defaultReadObject();
        // set yourself as property change listener
        try {
            model.addPropertyChangeListener(this);
        } catch (NullPointerException e) {
        }
    }

    /**
     * This method removes all Cardinalities connected to this object.
     *
     * @param event This remove event is sent to all disposed cardinalities.
     */
    protected void removeAllStrongAddiction(cz.felk.cvut.erm.event.RemoveEvent event) {
        StrongAddiction sa;
        for (int i = connections.size() - 1; i >= 0; i--) {
            if ((sa = (StrongAddiction) (((Connection) connections.elementAt(i)))
                    .isConnectedTo(StrongAddiction.class)) != null)
                sa.handleRemoveEvent(event);
        }
    }

    /**
     * Removes atribute from the unique. This action is made by disconnecting
     * the connection.
     *
     * @param atr The removing atribute.
     */
    public void removeAtribute(AttributeConstruct atr) {
        try {
            model.addAtribute((Atribute) (atr
                    .getModel()));
            Connection conn = new ConnectionLine(manager, getSchema(), this, atr);
            ((ConnectionManager) manager).addConnection(conn);
            (manager).repaintItem(conn);
        } catch (Throwable x) {
            new ShowException(null, "Error", x, true);
        }
    }

    /**
     * Resets this unique key as primary atribute and sets the <code>uk</code>
     * as the new on.
     *
     * @param uk The new primary key. If its <code>null</code> there is no
     *           other primary key.
     */
    public void resetPrimary() {
        if (!primary)
            return;
        try {
            try {
//PŠ				ent.setPrimary((UniqueKey) null);
                this.primary = false;
                (manager).repaintItem(this);
            } catch (ClassCastException e) {
            }
        } catch (Throwable x) {
            new ShowException(null, "Error", x, true);
        }
    }

    /**
     * Resets this unique key as primary atribute and sets the <code>uk</code>
     * as the new on.
     *
     * @param uk The new primary key. If its <code>null</code> there is no
     *           other primary key.
     */
    public void resetPrimary(UniqueKeyConstruct uk) {
        if (!primary || (uk == null))
            return;
        StrongAddiction sa = null;
        Connection c;
        for (int i = connections.size() - 1; i >= 0; i--) {
            if ((sa = (StrongAddiction) ((c = ((Connection) connections
                    .elementAt(i))).isConnectedTo(StrongAddiction.class))) != null) {
                if (c.getOne() instanceof StrongAddiction) {
                    c.setTwo(uk);
                }
                if (c.getTwo() instanceof StrongAddiction) {
                    c.setOne(uk);
                }
            }
        }
        this.primary = false;
        (manager).repaintItem(this);
    }

    /**
     * Sets this unique key as primary. It have to tell the owner the it's
     * primary.
     */
    public void setPrimary() {
        try {
            if (primary)
                return;
            try {
//PŠ				ent.setPrimary(this);
                this.primary = true;
                manager.repaintItem(this);
            } catch (ClassCastException e) {
            }
        } catch (Throwable x) {
            new ShowException(null, "Error", x, true);
        }
    }

    /**
     * Writes data for unique key into XML file
     *
     * @param pw java.io.PrintWriter
     */
    public void write(java.io.PrintWriter pw) {
        pw.println("\t<unique>");
        super.write(pw);
        pw.println("\t\t<primary>" + getPrimary() + "</primary>");
        pw.println("\t</unique>");
    }
}