package cz.green.ermodeller;

import cz.green.ermodeller.interfaces.FontManager;
import cz.green.event.*;
import cz.green.event.exceptions.ImpossibleNegativeValueException;
import cz.green.event.exceptions.ItemNotInsideManagerException;
import cz.green.event.interfaces.Item;
import cz.green.event.interfaces.Manager;
import cz.green.eventtool.ConnectableWindow;
import cz.green.eventtool.ConnectionLine;
import cz.green.eventtool.interfaces.Connection;
import cz.green.eventtool.interfaces.ConnectionManager;
import cz.green.swing.ShowException;
import cz.omnicom.ermodeller.conceptual.beans.*;
import cz.omnicom.ermodeller.conceptual.exception.MustHave2ConnectionsException;
import cz.omnicom.ermodeller.conceptual.exception.ParameterCannotBeNullException;

import javax.swing.*;
import java.awt.*;
import java.util.Vector;

/**
 * Represents the relation construct. Relation object is always inserted to the manager
 * (instance of the class DGroup) and it caused the moving atributes and unique key together
 * with its relation.
 *
 * @see DGroupTool
 */
public class RelationConstruct extends ConceptualConstructItem {
    /**
     * The model object - entity from Aleš Kopecký work
     */
    protected Relation model = null;

    /**
     * Creates relation, counts size to fit the name of the relation.
     *
     * @param rel     The model object - relation from the Aleš Kopecký work.
     * @param manager The window group where to put the new relation. Instance of the
     *                DGroup class.
     * @param left    The x coordinate of the left top point of the new relation.
     * @param top     The y coordinate of the left top point of the new relation.
     * @throws <code>java.lang.NullPointerException</code>
     *          Thrown by inherited constructor.
     * @throws <code>cz.green.event.ImpossibleNegativeValueException</code>
     *          Thrown by inherited constructor.
     * @see ConceptualConstructItem#ConceptualConstructItem(cz.green.event.interfaces.Manager , int, int, int, int)
     */
    protected RelationConstruct(Relation rel, Manager manager, int left, int top) throws NullPointerException, ImpossibleNegativeValueException {
        super(manager, left, top, 0, 0);
        rel.addPropertyChangeListener(this);
        String name = (model = rel).getName();
        java.awt.FontMetrics fm;
        try {
            fm = ((FontManager) manager).getReferentFontMetrics();
            int width = fm.stringWidth(name), height = fm.getAscent();
            if (getNotationType() == CHEN) {
//			rect[0][1] = rect[0][0] + 2 * width + height;
                rect[0][1] = rect[0][0] + 9 * height;
                rect[1][1] = rect[1][0] + 3 * height;
            } else {
                rect[0][1] = rect[0][0] + 7;
                rect[1][1] = rect[1][0] + 7;
            }
        } catch (ClassCastException e) {
            fm = null;
        }
    }

    /**
     * Returns <code>true</code> if it is compactable relation between to entities e1 and e2.
     * It means that on this relation partiicipates only two entities (e1 and e2) and both cardinalities
     * are also compactable.
     *
     * @param e1 The first entity.
     * @param e2 The second entity.
     * @return <code>true</code> if it is compactable relation.
     * @see CardinalityConstruct#isCompactable()
     */
    public boolean compactConnection(EntityConstruct e1, EntityConstruct e2) {
        Object o;
        Connection c;
        CardinalityConstruct car;
        //go through all connections
        for (int i = connections.size() - 1; i >= 0; i--) {
            if ((car = (CardinalityConstruct) ((Connection) connections.elementAt(i)).isConnectedTo(CardinalityConstruct.class)) != null) {
                //it's connection to the cardinality
                if (car.isCompactable()) {
                    EntityConstruct e = car.getEntity();
                    if (e == e1) {
                        e1 = null;
                    } else {
                        if (e == e2) {
                            e2 = null;
                        } else {
                            return false;
                        }
                    }
                } else
                    return false;
            }
        }
        //connections only to two entities - is it e1 and e2
        return ((e1 == null) && (e2 == null));
    }

    public void composeRelation(RelationConstruct rel, CoordinateEvent event) {
        //System.out.println(rel);

        this.reconnectAllAtributes(rel);
        this.reconnectAllCardinalities(rel);
        this.handleRemoveEvent(new RemoveEvent(event.getX(), event.getY(), event.getComponent()));
    }

    /**
     * Creates new cardinality object. Creates model object - cardinality,
     * new view-controller and connectes it to the entity and relation.
     *
     * @param ent     The entity that should be participant in this relation.
     * @param manager The window group (or desktop) where this cardinaliti should be put into.
     * @param left    The x coordinate of the left top point of the new cardinality.
     * @param top     The y coordinate of the left top point of the new cardinality.
     * @return The new cradinality.
     */
    public CardinalityConstruct createCardinality(EntityConstruct ent, Manager manager, int left, int top) {
        CardinalityConstruct car;
        try {
            //creates new model - cardinality
            Relation cRel = model;
            Entity cEnt = (ent.getModel());
            Cardinality cCar = cRel.createCardinality(cEnt);
            //creates new view controller - cardinality
/*		Changed manager from desktop to ENTITY!	
  		Cardinality car = new Cardinality(cCar, manager, left, top);
		manager.add(car);
*/
            if (getNotationType() != CHEN) {
                java.util.Enumeration e = connections.elements();
                int counter = 0;
                while (e.hasMoreElements()) {
                    Connection c = ((Connection) e.nextElement());
                    CardinalityConstruct exCar = null;
                    if (c.getOne() instanceof CardinalityConstruct)
                        exCar = (CardinalityConstruct) (c.getOne());
                    if (c.getTwo() instanceof CardinalityConstruct)
                        exCar = (CardinalityConstruct) (c.getTwo());
                    if (exCar != null) counter++;
                }
                if (counter >= 2) throw new MustHave2ConnectionsException(this.model);
            }
            car = new CardinalityConstruct(cCar, ent.getManager(), left, top);
            ent.getManager().add(car);
            cCar.setName(cEnt.getName());
            //connects it to the relation
            Connection conn = new ConnectionLine(manager, getSchema(), car, this);
            ((ConnectionManager) manager).addConnection(conn);
            (manager).repaintItem(conn);
            //connects it to the entity
            conn = new ConnectionLine(manager, getSchema(), car, ent);
            ((ConnectionManager) manager).addConnection(conn);
            (manager).repaintItem(conn);
            return car;
        } catch (Throwable x) {
            new ShowException(null, "Error", x, true);
            car = null;
        }
        return car;
    }

    /*public Cardinality createCardinality(Entity ent, cz.green.event.interfaces.Manager manager, int left, int top, boolean toTheMiddle) throws ItemNotInsideManagerException {
        Cardinality car = createCardinality(ent, manager, left, top);
        car.move(-car.getBounds().width/2, -car.getBounds().height/2, false);
        return null;
    }
    */
/**
 * This method adds items to the context menu, which are specific to the atribute.
 *
 * @param menu  The popup menu where to add the new items.
 * @param event The event, which caused the context menu displaying. Is useful for determing targets of the
 *              methods call.
 * @return The filled menu.
 */
    protected JPopupMenu createMenu(JPopupMenu menu, PopupMenuEvent event) {
        super.createMenu(menu, event);
        if (getNotationType() == CHEN)
            addMenuItem(menu, "Add atribute", "img/mAtribute.gif", event.getComponent(), "addingAtribute", this, ConceptualConstructItem.class);
        addMenuItem(menu, "Add Connection to Entity ...", "img/mAddConnection.gif", event.getComponent(),
                "addingConnectionToEnt", this,
                RelationConstruct.class);
        addMenuItem(menu, "Decompose", "img/mDRelation.gif", this, "decompose", event, CoordinateEvent.class);
        //addMenuItem(menu, "Compose with relation", "mCompEntity.gif", event.getComponent(), "composingRelation", this, cz.green.ermodeller.Relation.class);
        if (getNotationType() == CHEN)
            addMenuItem(menu, "Compose with", "img/mCompEntity.gif", event.getComponent(), "removing", this, Item.class);
        if (getNotationType() == CHEN)
            addMenuItem(menu, "Readjust size", "img/mReadjustSizeRel.gif", this, "minimizeSize", event, CoordinateEvent.class);
        return menu;
    }

    public void minimizeSize(CoordinateEvent e) {
        FontMetrics fm = ((FontManager) this.getManager()).getReferentFontMetrics();
        int width = fm.stringWidth(this.model.getName()), height = fm.getAscent();
        ResizeRectangle rr = new ResizeRectangle(
                0, 0, 0, 0, ResizePoint.BOTTOM
                | ResizePoint.RIGHT);
        minimizeRelation(new ResizeEvent(getBounds().x, getBounds().y, 0, 0, rr, null));
    }

    /**
     * Is called to create new relation
     *
     * @param schema  Is needful because model object relation creates model object schema.
     * @param manager Where to put the new Dgroup instance created to put into new Realtion instance.
     * @param left    The x coordinate of the left top point of the new relation.
     * @param top     The y coordinate of the left top point of the new relation.
     * @return The new created relation.
     */
    static public RelationConstruct createRelation(Schema schema, Manager manager, int left, int right) {
        try {
            //creates the new DGroup instance
            DGroupTool group = new DGroupTool(manager, left, right, 0, 0);
            Relation cRel = schema.createRelation();
            //creates new relation
            RelationConstruct rel = new RelationConstruct(cRel, manager, left, right);
            group.add(rel);
            //adds group to the manager
            manager.add(group);
            manager.repaintItem(group);
            return rel;
        } catch (Throwable x) {
            new ShowException(null, "Error", x, true);
        }
        return null;
    }

    /**
     * Is called to create new relation
     *
     * @param schema  Is needful because model object relation creates model object schema.
     * @param manager Where to put the new Dgroup instance created to put into new Realtion instance.
     * @param left    The x coordinate of the left top point of the new relation.
     * @param top     The y coordinate of the left top point of the new relation.
     * @return The new created relation.
     */
    static public RelationConstruct createRelation(Schema schema, Manager manager, int left, int top, int width, int height) {
        RelationConstruct rel;
        rel = createRelation(schema, manager, left, top);
        java.awt.Rectangle r = rel.getBounds();
        try {
            rel.resize((left + width) - (r.x + r.width), (top + height) - (r.y + r.height),
                    (ResizePoint.RIGHT | ResizePoint.BOTTOM), true);
        } catch (ItemNotInsideManagerException e1) {
            e1.printStackTrace();
        }
        return rel;
    }

    /**
     * Determine whether the relation is decomposable.
     *
     * @return <code>true</code> if this relation is decomposable.
     */
    protected boolean decomposable() {
        Vector<EntityConstruct> v = new java.util.Vector<EntityConstruct>(connections.size());
        Object o;
        CardinalityConstruct car;
        Connection c;
        boolean recursive = false;
        //go through all connections
        for (int i = connections.size() - 1; i >= 0; i--) {
            if ((car = (CardinalityConstruct) ((Connection) (connections.elementAt(i))).isConnectedTo(CardinalityConstruct.class)) != null) {
                if (!car.isMultiCardinality())
                    return true;
                //is there already connection to the entity
                if (v.indexOf(car.getEntity()) != -1)
                    recursive = true;
                v.addElement(car.getEntity());
            }
        }
        return !recursive;
    }

    /**
     * Decompose the entity.
     *
     * @param event Useful for sending the remove event to some objects.
     */
    public void decompose(CoordinateEvent event) {
//	if (!decomposable()) return;

        Manager man = ((Container) event.getComponent()).getDesktop();
        try {
            EntityConstruct ent = transformToEntity(man);
            boolean create = false;
            CardinalityConstruct car, primary = null, first = null;
            //find entity with single cardinality or take the first
            for (int i = connections.size() - 1; i >= 0; i--) {
                if ((car = (CardinalityConstruct) ((Connection) (connections.elementAt(i))).isConnectedTo(CardinalityConstruct.class)) != null) {
                    if (primary == null)
                        primary = car;
                    if ((!car.isMultiCardinality()) && !create) {
                        create = true;
                        primary = car;
                    }
                }
            }
            //if there is some cardinality object, create primary key and add strong addiction
            if (primary != null) {
                java.awt.Point p = ent.getCenter(primary.getEntity());
                primary.transformToStrongAddiction(ent, man);
            }
            for (int i = connections.size() - 1; i >= 0; i--) {
                if ((car = (CardinalityConstruct) ((Connection) (connections.elementAt(i))).isConnectedTo(CardinalityConstruct.class)) != null) {
                    if (car != primary) {
                        if (create) {
                            //others cardinalities decompose as new relations
                            car.transformToRelation(ent, man);
                        } else {
                            //others cardinalities decompose as strong addiction
                            car.transformToStrongAddiction(ent, man);
                        }
                    }
                }
            }
            if (!decomposable()) {
                StrongAddiction sa;
                for (int i = connections.size() - 1; i >= 0; i--) {
                    if ((sa = (StrongAddiction) ((Connection) (connections.elementAt(i))).isConnectedTo(StrongAddiction.class)) != null) {
                        sa.move(i * 10, i * 10, false);
                    }
                }
            }
            //delete this relation
            handleRemoveEvent(new RemoveEvent(event.getX(), event.getY(), event.getComponent()));
        } catch (Throwable x) {
            new ShowException(null, "Error", x, true);
        }
    }

    /**
     * Counts new border point according to the relation schape.
     *
     * @param direction The center of the other connected element.
     * @return The border.
     * @see ConnectableWindow#getBorder(java.awt.Point)
     */
    public java.awt.Point getBorder(java.awt.Point direction) {
        java.awt.Point center = getCenter();
        java.awt.Rectangle r = getBounds();
        int x2 = r.width / 2;
        int y2 = r.height / 2;
        int x = direction.x - center.x;
        int y = direction.y - center.y;
        if (x < 0)
            x = -x;
        if (y < 0)
            y = -y;
        int ry;
        if (x != 0) {
            float a = ((float) x2) / y2;
            ry = (int) (((float) x2) / (((float) x) / y + a));
            int rx = (int) (a * ry);
            center.x += (center.x > direction.x) ? -x2 + rx : x2 - rx;
            center.y += (center.y > direction.y) ? -ry : ry;
        } else {
            center.y += (center.y > direction.y) ? -y2 : y2;
        }
        return center;
    }

    /**
     * Returns the model relation from the Aleš Kopecký work.
     *
     * @see ConceptualConstructObject#getModel()
     */
    public Object getModel() {
        return model;
    }


    /**
     * Returns the border point (same as <code>getBorder</code>) but according to the position
     * during moving or resizing - it's the real position. Returns the real borde.
     *
     * @see #getBorder(java.awt.Point)
     * @see #getRealCenter()
     */
    public java.awt.Point getRealBorder(java.awt.Point direction) {
        java.awt.Point center = getRealCenter();
        java.awt.Rectangle r = getRealBounds();
        int x2 = r.width / 2;
        int y2 = r.height / 2;
        int x = direction.x - center.x;
        int y = direction.y - center.y;
        if (x < 0)
            x = -x;
        if (y < 0)
            y = -y;
        int ry;
        if (x != 0) {
            float a = ((float) x2) / y2;
            ry = (int) (((float) x2) / (((float) x) / y + a));
            int rx = (int) (a * ry);
            center.x += (center.x > direction.x) ? -x2 + rx : x2 - rx;
            center.y += (center.y > direction.y) ? -ry : ry;
        } else {
            center.y += (center.y > direction.y) ? -y2 : y2;
        }
        return center;
    }

    /**
     * Because the relation  has different shape, it has other resize points.
     */
    public ResizePoint[] getResizePoints() {
        ResizePoint[] r;
        if (getNotationType() == ConceptualConstructItem.CHEN) {
            r = new ResizePoint[8];
            r[0] = new ResizePoint(0, 0.5, ResizePoint.LEFT);
            r[1] = new ResizePoint(1, 0.5, ResizePoint.RIGHT);
            r[2] = new ResizePoint(0.5, 0, ResizePoint.TOP);
            r[3] = new ResizePoint(0.5, 1, ResizePoint.BOTTOM);
            r[4] = new ResizePoint(0.25, 0.25, ResizePoint.LEFT | ResizePoint.TOP);
            r[5] = new ResizePoint(0.25, 0.75, ResizePoint.LEFT | ResizePoint.BOTTOM);
            r[6] = new ResizePoint(0.75, 0.75, ResizePoint.RIGHT | ResizePoint.BOTTOM);
            r[7] = new ResizePoint(0.75, 0.25, ResizePoint.RIGHT | ResizePoint.TOP);
            return r;
        } else return null;
    }

    /**
     * Handle event when some other object is dragged over.
     * Can work only with entity and it means to create the new caedinality.
     * Otherwise invokes inherited handler.
     */
    public void handleDragOverEvent(DragOverEvent event) {
        if (selected && event.getAdd())
            return;
        Item item = event.getItem();
        if (item instanceof EntityConstruct) {
            if (event.getAdd()) {
                event.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                return;
            }
        }
        if (item instanceof RelationConstruct) {
            if (!event.getAdd()) {
                event.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                return;
            }
        }
        super.handleDragOverEvent(event);
    }

    public void handleAddConnectionEvent(AddConnectionEvent event) {
        if (selected && event.getAdd())
            return;
        Item item = event.getItem();
        if (item instanceof EntityConstruct) {
            String name = "";
            if (getNotationType() != ConceptualConstructItem.CHEN) {
                java.util.Enumeration e = getConnections().elements();
                CardinalityConstruct car1;
                while (e.hasMoreElements()) {
                    Connection c = ((Connection) e.nextElement());
                    if (c.getOne() instanceof CardinalityConstruct) {
                        car1 = ((CardinalityConstruct) c.getOne());
                        name = (car1.getEntity().getModel()).getName();
                    }
                    if (c.getTwo() instanceof CardinalityConstruct) {
                        car1 = ((CardinalityConstruct) c.getTwo());
                        name = (car1.getEntity().getModel()).getName();
                    }
                }
            }
            java.awt.Point p = ((EntityConstruct) item).getAbsoluteCenter(this);
            CardinalityConstruct car = createCardinality((EntityConstruct) item, ((EntityConstruct) item).getManager(), p.x, p.y);
            car.handleMoveEvent(new MoveEvent(car.getBounds().x, car.getBounds().y, -car.getBounds().width / 2, -car.getBounds().height / 2, null));
            if (getNotationType() != ConceptualConstructItem.CHEN) {
                car.model.setName(this.model.getName());
                if (name.length() > 0) car.model.setName(name);
                car.moveCardinality(new ExMovingEvent(p.x, p.y, 0, 0, null, false));
            }
        }
    }

    /**
     * Handle event when some other object is dragged over.
     * Can work only with entity and it means to create the new caedinality.
     * Otherwise invokes inherited handler.
     */
    public void handleDropAboveEvent(DropAboveEvent event) {
        if (selected && event.getAdd())
            return;
        Item item = event.getItem();
        if (item instanceof EntityConstruct) {
            if (event.getAdd()) {
                EntityConstruct ent = (EntityConstruct) item;
                try {
                    if (getNotationType() == CHEN)
                        ((Container) event.getComponent()).addingCardinality(new CardinalityPair(ent, this));
                    else {
                        CardinalityConstruct car = createCardinality((EntityConstruct) item, ((EntityConstruct) item).getManager(), getBounds().x, getBounds().y);
                        car.model.setName(this.model.getName());
                    }
                    event.setDropped(true);
                    return;
                } catch (Throwable x) {
                    new ShowException(null, "Error", x, true);
                }
            }
        }
        if (item instanceof RelationConstruct && !event.getAdd())
            composeRelation((RelationConstruct) item, event);
        super.handleDropAboveEvent(event);
    }

    public void handleExMoveEvent(ExMoveEvent event) {
        super.handleExMoveEvent(event);
        java.util.Enumeration e = connections.elements();
        while (e.hasMoreElements()) {
            Connection c = ((Connection) e.nextElement());
            CardinalityConstruct car = null;
            if (c.getOne() instanceof CardinalityConstruct)
                car = (CardinalityConstruct) (c.getOne());
            if (c.getTwo() instanceof CardinalityConstruct)
                car = (CardinalityConstruct) (c.getTwo());
            if (car != null) {
                java.awt.Rectangle r = car.getBounds();
                car.getManager().repaintRectangle(r.x, r.y, r.width, r.height);
            }
        }

    }

    /**
     * Handle remove event and adds only one functionality -> remove relation from the model's object.
     */
    public void handleRemoveEvent(RemoveEvent event) {
        try {
            model.getSchema().disposeRelation(model);
            super.handleRemoveEvent(event);
        } catch (Throwable x) {
            new ShowException(null, "Error", x, true);
        }
    }

    public void handleResizeEvent(ResizeEvent event) {
        if (getNotationType() == CHEN)
            resizeRelation(event);
    }

    public void handleResizingEvent(ResizingEvent event) {
        if (getNotationType() == CHEN)
            resizingRelation(event);
    }

    /**
     * Determines whether the resizing specified by <code>ev</code> is
     * possible . If its not changes the dx and dy
     * properties the event to be the resize maximal possible.
     *
     * @param ev The resize event, which will be handled.
     */
    protected void countMinSize(ResizeEvent ev) {
        int r[][];
        int width = 0;
        int height = 0;
        java.awt.FontMetrics fm;
        switch (getNotationType()) {
            case CHEN:
                fm = ((FontManager) manager).getReferentFontMetrics();
                int nameWidth = fm.stringWidth(((Relation) getModel()).getName());
                int nameHeight = fm.getAscent();
                width = 2 * nameWidth + nameHeight;
                height = 3 * nameHeight;
                break;
            case BINARY:
            case UML:
                width = 7;
                height = 7;
                break;
        }
        r = getRect();
        // test whether dx is possible, if not have to change
        int dx = width - r[0][1] + r[0][0];
        int real = ((ev.getResizeRect().direction & ResizePoint.LEFT) == ResizePoint.LEFT) ? -ev
                .getDx()
                : ev.getDx();
        if (real < dx) {
            ev.setDx(((ev.getResizeRect().direction & ResizePoint.LEFT) == ResizePoint.LEFT) ? -dx
                    : dx);
        }
        // count max dy
        int dy = height - r[1][1] + r[1][0];
        real = ((ev.getResizeRect().direction & ResizePoint.TOP) == ResizePoint.TOP) ? -ev
                .getDy() : ev.getDy();
        // if event dy is greater - change it
        if (real < dy) {
            ev.setDy(((ev.getResizeRect().direction & ResizePoint.TOP) == ResizePoint.TOP) ? -dy
                    : dy);
        }
    }

    /**
     * Overrides the method because has different shape.
     *
     * @see interfaces.Item#isIn(int, int)
     */
    public boolean isIn(int x, int y) {
        if (super.isIn(x, y)) {
            int[][] r = rect;
            float p = ((float) (r[0][1] - r[0][0])) / ((float) (r[1][1] - r[1][0]));
            float x2 = ((float) (r[0][1] + r[0][0])) / 2, y2 = ((float) (r[1][1] + r[1][0])) / 2;
            if (y > y2)
                p *= (r[1][1] - y);
            else
                p *= (y - r[1][0]);
            if (x > x2)
                return (x <= x2 + p);
            else
                return (x >= x2 - p);
        }
        return false;
    }

    /**
     * Paints the relation.
     */
    public void paint(java.awt.Graphics g) {
        final Stroke stroke = updateStrokeWithAliasing(g);
        java.awt.Rectangle r = getBounds();
        int x = r.x + r.width / 2;
        int y = r.y + r.height / 2;
        int xpoints[] = {x, r.x, x, r.x + r.width};
        int ypoints[] = {r.y, y, r.y + r.height, y};
        java.awt.Polygon p = new java.awt.Polygon(xpoints, ypoints, 4);
        switch (getNotationType()) {
            case CHEN:
                if (selected)
                    g.setColor(getSelectedBackgroundColor());
                else
                    g.setColor(getBackgroundColor());
                g.fillPolygon(p);
                g.setColor(getForegroundColor());
                g.drawPolygon(p);
                java.awt.FontMetrics fm = g.getFontMetrics();
                String name = model.getName();
                g.drawString(name, r.x + (r.width - fm.stringWidth(name)) / 2, r.y + (r.height + fm.getAscent()) / 2);
                break;
            case BINARY:
            case UML:
                g.fillPolygon(p);
                g.setColor(getForegroundColor());
                g.drawPolygon(p);
                break;
        }
        updateBackupStroke(g, stroke);
    }

    /**
     * Paints the relation.
     */
    public void paintFast(java.awt.Graphics g) {
        java.awt.Rectangle r = getRealBounds();
        int x = r.x + r.width / 2, y = r.y + r.height / 2;
        int xpoints[] = {x, r.x, x, r.x + r.width}, ypoints[] = {r.y, y, r.y + r.height, y};
        java.awt.Polygon p = new java.awt.Polygon(xpoints, ypoints, 4);
        g.drawPolygon(p);
    }

    /**
     * Prints the relation.
     */
    public void print(java.awt.Graphics g) {
        java.awt.Rectangle r = getBounds();
        int x = r.x + r.width / 2;
        int y = r.y + r.height / 2;
        int xpoints[] = {x, r.x, x, r.x + r.width};
        int ypoints[] = {r.y, y, r.y + r.height, y};
        java.awt.Polygon p = new java.awt.Polygon(xpoints, ypoints, 4);

        switch (getNotationType()) {
            case CHEN:
                g.drawPolygon(p);
                java.awt.FontMetrics fm = g.getFontMetrics();
                String name = model.getName();
                g.drawString(name, r.x + (r.width - fm.stringWidth(name)) / 2, r.y + (r.height + fm.getAscent()) / 2);
                break;
            case BINARY:
            case UML:
                g.fillOval(x - 2, y - 2, 4, 4);
                break;
        }
//        p = null;
//        r = null;
    }

    /**
     * Invoked when some model's property change. Catch changes of the <b>name</b>
     * and <b>strongAddictionParents</b> property.
     *
     * @see ConceptualConstructItem#propertyCHENge(java.beans.PropertyCHENgeEvent);
     */
    public void propertyChange(java.beans.PropertyChangeEvent e) {
        ((ConceptualObject) getModel())
                .setChanged(true);
        java.awt.Rectangle r = getBounds();
        if (e.getPropertyName().equals("name")) {
            if (getNotationType() == CHEN) {
                ResizeRectangle rr = new ResizeRectangle(
                        0, 0, 0, 0, ResizePoint.BOTTOM
                        | ResizePoint.RIGHT);
                this.resizeRelation(new ResizeEvent(0, 0, 0, 0, rr, null));
                (manager).repaintRectangle(r.x,
                        r.y, r.width, r.height);
            }
        }
    }

    /**
     * Do real resize of the relation.
     *
     * @see #countMinSize(ResizeEvent)
     */
    protected void resizeRelation(ResizeEvent event) {
        countMinSize(event);
        super.handleResizeEvent(event);
    }

    /**
     * Do real resizing of the relation.
     *
     * @see #countMinSize(ResizeEvent)
     */
    protected void resizingRelation(ResizingEvent event) {
        countMinSize(event);
        super.handleResizingEvent(event);
    }

    /**
     * Do real minimize of the relation. Calls countMinSize to tests the resize
     * requirement.
     *
     * @see #countMinSize(ResizeEvent)
     */
    protected void minimizeRelation(ResizeEvent event) {
        event.setDx(-1000);
        event.setDy(-1000);
        countMinSize(event);
        super.handleResizeEvent(event);
    }

    /**
     * This method was created by Jiri Mares
     *
     * @param s java.io.ObjectInputStream
     * @throws java.io.IOException The exception description.
     * @throws java.lang.ClassNotFoundException
     *                             The exception description.
     */
    private void readObject(java.io.ObjectInputStream s) throws java.io.IOException, ClassNotFoundException {
        s.defaultReadObject();
        //set yourself as property change listener
        try {
            model.addPropertyChangeListener(this);
        } catch (NullPointerException e) {
        }
    }

    /**
     * Disposes the cradinality.
     *
     * @param car the desposing cardinality.
     * @throws cz.omnicom.ermodeller.conceptual.ParameterCannotBeNull
     *          Thrown by model objects.
     */
    public void removeCardinality(CardinalityConstruct car) throws ParameterCannotBeNullException {
        model.disposeCardinality((Cardinality) car.getModel());
    }

    /**
     * Decompose the entity.
     *
     * @param event Useful for sending the remove event to some objects.
     */
    protected EntityConstruct transformToEntity(Manager man) {
        int[][] r = getRect();
        EntityConstruct ent = EntityConstruct.createEntity(model.getSchema(), man, r[0][0], r[1][0], null);
        (ent.getModel()).setName(model.getName());
        reconnectAllAtributes(ent);
        return ent;
    }

    /**
     * @return Returns the connections.
     */
    public Vector getConnections() {
        return connections;
    }

    /**
     * Writes data for relation into XML file
     *
     * @param pw java.io.PrintWriter
     */
    public void write(java.io.PrintWriter pw) {
        pw.println("\t<relation>");
        super.write(pw);
        pw.println("\t</relation>");
    }
}