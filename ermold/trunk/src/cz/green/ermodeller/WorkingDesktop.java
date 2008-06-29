package cz.green.ermodeller;

import cz.green.ermodeller.interfaces.FontManager;
import cz.green.ermodeller.interfaces.ISchema;
import cz.green.ermodeller.interfaces.ModelFinder;
import cz.green.ermodeller.interfaces.ViewController;
import cz.green.event.ContainerComponent;
import cz.green.event.RemoveEvent;
import cz.green.event.SelectItemEvent;
import cz.green.event.SelectItemExEvent;
import cz.green.event.interfaces.Item;
import cz.green.event.interfaces.PaintableItem;
import cz.green.eventtool.DesktopTool;
import cz.green.eventtool.dialogs.PropertyListDialog;
import cz.green.eventtool.interfaces.Connection;
import cz.green.util.ActionAdapter;
import cz.green.util.ParamActionAdapter;
import cz.omnicom.ermodeller.conceptual.NotationType;
import cz.omnicom.ermodeller.conceptual.beans.*;
import cz.omnicom.ermodeller.errorlog.interfaces.ShowErrorListener;
import cz.omnicom.ermodeller.typeseditor.UserTypeStorage;
import cz.omnicom.ermodeller.typeseditor.UserTypeStorageVector;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Vector;

/**
 * Adds new needful functionality for realiying the ER modeller.
 */
public class WorkingDesktop extends DesktopTool implements FontManager,
        ISchema, ViewController, ModelFinder {
    /**
     * The model - object from Aleš Kopecký work
     */
    protected Schema model = null;
    public JFrame ERMFrame;

    private transient java.beans.PropertyChangeSupport pcs = null;

    /**
     * Call inhereted constructor and creates the model - schema.
     *
     * @see cz.green.eventtool.DesktopTool#DesktopGroupWindow(cz.green.event.ContainerComponent , int,
     *      int, int, int)
     */
    public WorkingDesktop(ContainerComponent place, int left, int top,
                          int width, int height) {
        super(place, left, top, width, height);
        model = new Schema();
        pcs = new java.beans.PropertyChangeSupport(this);
        this.addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                System.out.println("evt = v Desktop = " + evt.getPropertyName());
            }
        });

    }

    /**
     * Adds PropertyChangeListener that catch the changes of the workMode
     * property.
     */
    public void addPropertyChangeListener(
            java.beans.PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    /**
     * This method was created by Jiri Mares
     *
     * @param lst cz.omnicom.ermodeller.errorlog.ShowErrorListener
     */
    public void addShowErrorListener(ShowErrorListener lst) {
        model.addShowErrorListener(lst);
    }

    /**
     * Compose entity with..
     */
    public void composeEntity(Entity eM1,
                              Entity eM2) {
        EntityConstruct e1 = getEntity(eM1.getID()), e2 = getEntity(eM2.getID());

        e2.composeEntity(e1, new cz.green.ermodeller.DragOverEvent(0, 0,
                e1, getPaintPlace()));
    }

    /**
     */
    public void composeEntityOld(Entity e1,
                                 Entity e2) {
        Vector<Atribute> v1 = e1.getAtributes();
        Vector<Atribute> v2 = e2.getAtributes();
        int i, j;
        Atribute atrM1, atrM2;
        AttributeConstruct atr2, atr1;
        EntityConstruct ent2 = getEntity(e2.getID()), ent1 = getEntity(e1.getID());

        for (i = 0; i < v2.size(); i++) {
            atrM2 = v2.get(i);
            atr2 = getAtribute(atrM2.getID());
            atr1 = null;
            for (j = 0; j < v1.size(); j++) {
                atrM1 = v1.get(i);
                if (atrM1.getName().equals(atrM2.getName())) {
                    atr1 = getAtribute(atrM1.getID());
                }
            }
            if (atr1 == null) {
                ent1.reconnectAtribute(atr2);
            }
        }
        ent2.handleRemoveEvent(new cz.green.event.RemoveEvent(0, 0,
                getPaintPlace()));
    }

    /**
     * Compose Relation with
     */
    public void composeRelation(Relation rM1,
                                Relation rM2) {
        RelationConstruct r1 = getRelation(rM1.getID()), r2 = getRelation(rM2.getID());

        r2.composeRelation(r1, new cz.green.ermodeller.DragOverEvent(0, 0,
                r1, getPaintPlace()));
    }

    /**
     * Creates the new entity with exact size. Invokes the static entity's method
     * <code>createEntity</code>.
     *
     * @see cz.green.ermodeller.interfaces.ISchema#createEntity(int, int, EntityConstruct)
     */
    public EntityConstruct createEntity(int x, int y, int width, int height, EntityConstruct old) {
        return EntityConstruct.createEntity(model, this, x, y, width, height, old);
    }

    /**
     * Creates the new entity. Invokes the static entity's method
     * <code>createEntity</code>.
     *
     * @see cz.green.ermodeller.interfaces.ISchema#createEntity(int, int, EntityConstruct)
     */
    public EntityConstruct createEntity(int x, int y, EntityConstruct old) {
        return EntityConstruct.createEntity(model, this, x, y, old);
    }

    /**
     * Creates the new relation. Invokes the static relation's method
     * <code>createRelation</code>.
     *
     * @see cz.green.ermodeller.interfaces.ISchema#createRelation(int, int)
     */
    public RelationConstruct createRelation(int x, int y, int width, int height) {
        return RelationConstruct.createRelation(model, this, x, y, width, height);
    }

    /**
     * Creates the new relation. Invokes the static relation's method
     * <code>createRelation</code>.
     *
     * @see cz.green.ermodeller.interfaces.ISchema#createRelation(int, int)
     */
    public RelationConstruct createRelation(int x, int y) {
        return RelationConstruct.createRelation(model, this, x, y);
    }

    /**
     * Returns atribute with ID
     */
    public AttributeConstruct getAtribute(int aid) {
        return (AttributeConstruct) getConceptualObject(aid);
    }

    private AttributeConstruct getAtribute(DGroupTool dg, int aid) {
        int cnt = dg.getItemCount(), id;
        Item item;
        AttributeConstruct atr;

        for (int i = 0; i < cnt; i++) {
            item = dg.getItem(i);
            if (item instanceof DGroupTool) {
                if ((atr = getAtribute((DGroupTool) item, aid)) != null)
                    return atr;
            } else {
                id = ((ConceptualObject) ((ConceptualConstructObject) item)
                        .getModel()).getID();
                if (item instanceof AttributeConstruct && aid == id)
                    return (AttributeConstruct) item;
            }
        }
        return null;
    }

    /**
     * Returns the background color of the desktop.
     *
     * @return The color background color.
     */
    protected java.awt.Color getBackgroundColor() {
        return BACKGROUND_COLOR;
    }

    /**
     * Returns conceptual object with ID
     */
    public ConceptualConstructObject getConceptualObject(int id) {
        int cnt = getItemCount();
        Item item;
        ConceptualConstructObject co = null;

        for (int i = 0; i < cnt; i++) {
            item = getItem(i);
            if (item instanceof DGroupTool) {
                if ((co = getConceptualObject((DGroupTool) item, id)) != null)
                    return co;
            } else if (!(item instanceof StrongAddiction)
                    && id == (((ConceptualObject) ((ConceptualConstructObject) item)
                    .getModel()).getID()))
                return (ConceptualConstructObject) item;
        }
        return co;
    }

    /**
     * Returns conceptual object with ID
     */
    private ConceptualConstructObject getConceptualObject(DGroupTool dg, int id) {
        int cnt = dg.getItemCount();
        Item item;
        ConceptualConstructObject co = null;

        for (int i = 0; i < cnt; i++) {
            item = dg.getItem(i);
            if (item instanceof DGroupTool) {
                if ((co = getConceptualObject((DGroupTool) item, id)) != null)
                    return co;
            } else if (!(item instanceof StrongAddiction)
                    && id == (((ConceptualObject) ((ConceptualConstructObject) item)
                    .getModel()).getID()))
                return (ConceptualConstructObject) item;
        }
        return co;
    }

    /**
     * Returns vector of all entities in the schema
     */
    private Vector<Item> getAllEntities(DGroupTool dg) {
        int cnt;
        if (dg == null)
            cnt = getItemCount();
        else
            cnt = dg.getItemCount();

        Vector<Item> retval = new Vector<Item>();
        Item item;
        for (int i = 0; i < cnt; i++) {
            if (dg == null)
                item = getItem(i);
            else
                item = dg.getItem(i);
            if (item instanceof DGroupTool) {
                Vector<Item> sub;
                if ((sub = getAllEntities((DGroupTool) item)).size() != 0) {
                    retval.addAll(sub);
                }
            } else if (item instanceof EntityConstruct)
                retval.add(item);
        }
        return retval;
    }

    /**
     * Returns vector of all entities in the schema
     */
    public Vector<Item> getAllEntities() {
        return getAllEntities(null);
    }

    /**
     * Returns vector of all relationships in the schema
     */
    private Vector<Item> getAllRelations(DGroupTool dg) {
        int cnt;
        if (dg == null)
            cnt = getItemCount();
        else
            cnt = dg.getItemCount();

        Vector<Item> retval = new Vector<Item>();
        Item item;
        for (int i = 0; i < cnt; i++) {
            if (dg == null)
                item = getItem(i);
            else
                item = dg.getItem(i);
            if (item instanceof DGroupTool) {
                Vector<Item> sub;
                if ((sub = getAllRelations((DGroupTool) item)).size() != 0) {
                    retval.addAll(sub);
                }
            } else if (item instanceof RelationConstruct)
                retval.add(item);
        }
        return retval;
    }

    /**
     * Returns vector of all relationships in the schema
     */
    public Vector<Item> getAllRelations() {
        return getAllRelations(null);
    }

    /**
     * Returns vector of all relationships which have at least one atribute
     */
    public Vector<Serializable> getRelationsWithAttribute(boolean namesOnly) {
        Vector<Item> rels = getAllRelations();
        Vector<Serializable> relsWithAtributes = new Vector<Serializable>(3, 2);
        for (Object rel1 : rels) {
            RelationConstruct rel = (RelationConstruct) rel1;
            /*Check for aributes*/
            if (!rel.getAtributes().isEmpty())
                if (namesOnly)
                    relsWithAtributes.add(((Relation) rel.getModel()).getName());
                else relsWithAtributes.add(rel);
        }
        return relsWithAtributes;
    }

    /**
     * Returns vector of all relationships which have less than two connections to entities
     */
    public Vector<Serializable> getRelationsWithoutConnection(boolean namesOnly) {
        Vector<Item> rels = getAllRelations();
        Vector<Serializable> relsNoConn = new Vector<Serializable>(3, 2);
        int connCounter;
        for (Object rel1 : rels) {
            RelationConstruct rel = (RelationConstruct) rel1;
            connCounter = 0;
            Enumeration e = rel.getConnections().elements();
            while (e.hasMoreElements()) {
                Connection c = ((Connection) e.nextElement());
                CardinalityConstruct car = null;
                if (c.getOne() instanceof CardinalityConstruct)
                    car = (CardinalityConstruct) c.getOne();
                if (c.getTwo() instanceof CardinalityConstruct)
                    car = (CardinalityConstruct) c.getTwo();
                if (car != null) {
                    if (car.getEntity() != null) {
                        connCounter++;
                    }
                }
            }
            if (connCounter <= 1) {
//				System.out.println("Stored");
                if (namesOnly) relsNoConn.add(((Relation) rel.getModel()).getName());
                else relsNoConn.add(rel);
            }

        }
        return relsNoConn;
    }

    /**
     * Returns vector of all relationships which have more than two connections to entities
     */
    public Vector<Serializable> getTernaryRelations(boolean namesOnly) {
        Vector<Item> rels = getAllRelations();
        Vector<Serializable> relsTernary = new Vector<Serializable>(3, 2);
        int connCounter;
        for (Object rel1 : rels) {
            RelationConstruct rel = (RelationConstruct) rel1;
            connCounter = 0;
            Enumeration e = rel.getConnections().elements();
            while (e.hasMoreElements()) {
                Connection c = ((Connection) e.nextElement());
                CardinalityConstruct car = null;
                if (c.getOne() instanceof CardinalityConstruct)
                    car = (CardinalityConstruct) c.getOne();
                if (c.getTwo() instanceof CardinalityConstruct)
                    car = (CardinalityConstruct) c.getTwo();
                if (car != null) {
                    if (car.getEntity() != null) {
                        connCounter++;
                    }
                }
            }
            if (connCounter > 2) {
                if (namesOnly) relsTernary.add(((Relation) rel.getModel()).getName());
                else relsTernary.add(rel);
            }

        }
        return relsTernary;
    }

    /**
     * Delete all relationships which have less than two connections to entities
     */
    public void delRelsWithoutConnection() {
        Vector<Serializable> RC = getRelationsWithoutConnection(false);
        for (Object aRC : RC) {
            RelationConstruct rel = (RelationConstruct) aRC;
            rel.handleRemoveEvent(new RemoveEvent(rel.getBounds().x, rel.getBounds().y, null));
        }
    }

    /**
     * Decompose all relationships which have atribute
     */
    public void decomposeRelsWithAtributes(DesktopContainer place) {
        Vector<Serializable> RA = getRelationsWithAttribute(false);
        for (Object aRA : RA) {
            RelationConstruct rel = (RelationConstruct) aRA;
            rel.decompose(new SelectItemEvent(rel.getBounds().x, rel.getBounds().x, false, place));
        }
    }

    /**
     * Decompose all ternary relationships
     */
    public void decomposeTernaryRels(DesktopContainer place) {
        Vector<Serializable> TR = getTernaryRelations(false);
        for (Object aTR : TR) {
            RelationConstruct rel = (RelationConstruct) aTR;
            rel.decompose(new SelectItemEvent(rel.getBounds().x, rel.getBounds().x, false, place));
        }
    }

    /**
     * Switch all connetions to the other side of relationship
     */
    public void switchAllRConnectionsCard(DesktopContainer place) {
        Vector<Item> R = getAllRelations();
        Cardinality car1M, car2M;
        for (Object aR : R) {
            RelationConstruct rel = (RelationConstruct) aR;

            Enumeration e = rel.getConnections().elements();
            java.util.List<CardinalityConstruct> cards = new Vector<CardinalityConstruct>();
            while (e.hasMoreElements()) {
                Connection c = ((Connection) e.nextElement());
                if (c.getOne() instanceof CardinalityConstruct)
                    cards.add((CardinalityConstruct) c.getOne());
                if (c.getTwo() instanceof CardinalityConstruct)
                    cards.add((CardinalityConstruct) c.getTwo());
            }
            if (cards.size() != 2) break;
            car1M = (Cardinality) ((CardinalityConstruct) cards.get(0)).getModel();
            car2M = (Cardinality) ((CardinalityConstruct) cards.get(1)).getModel();
            String name = car1M.getName();
            String comment = car1M.getComment();
            //boolean arb = car1M.getArbitrary();
            boolean multi = car1M.getMultiCardinality();
            //boolean glue=car1M.getGlue();
            car1M.setName(car2M.getName());
            car1M.setComment(car2M.getComment());
            //car1M.setArbitrary(car2M.getArbitrary());
            car1M.setMultiCardinality(car2M.getMultiCardinality());
            //car1M.setGlue(car2M.getGlue());
            car2M.setName(name);
            car2M.setComment(comment);
            //car2M.setArbitrary(arb);
            car2M.setMultiCardinality(multi);
            //car2M.setGlue(glue);
        }
    }

    /**
     * Switch all connetions to the other side of relationship
     */
    public void switchAllRConnectionsBoth(DesktopContainer place) {
        Vector<Item> R = getAllRelations();
        Cardinality car1M, car2M;
        for (Object aR : R) {
            RelationConstruct rel = (RelationConstruct) aR;

            Enumeration e = rel.getConnections().elements();
            java.util.List<CardinalityConstruct> cards = new Vector<CardinalityConstruct>();
            while (e.hasMoreElements()) {
                Connection c = ((Connection) e.nextElement());
                if (c.getOne() instanceof CardinalityConstruct)
                    cards.add((CardinalityConstruct) c.getOne());
                if (c.getTwo() instanceof CardinalityConstruct)
                    cards.add((CardinalityConstruct) c.getTwo());
            }
            if (cards.size() != 2) break;
            car1M = (Cardinality) ((CardinalityConstruct) cards.get(0)).getModel();
            car2M = (Cardinality) ((CardinalityConstruct) cards.get(1)).getModel();
            String name = car1M.getName();
            String comment = car1M.getComment();
            boolean arb = car1M.getArbitrary();
            boolean multi = car1M.getMultiCardinality();
            boolean glue = car1M.getGlue();
            car1M.setName(car2M.getName());
            car1M.setComment(car2M.getComment());
            car1M.setArbitrary(car2M.getArbitrary());
            car1M.setMultiCardinality(car2M.getMultiCardinality());
            car1M.setGlue(car2M.getGlue());
            car2M.setName(name);
            car2M.setComment(comment);
            car2M.setArbitrary(arb);
            car2M.setMultiCardinality(multi);
            car2M.setGlue(glue);
        }
    }


    /**
     * Switch all connetions to the other side of relationship
     */
    public void switchAllRConnectionsArb(DesktopContainer place) {
        Vector<Item> allRelations = getAllRelations();
        Cardinality car1M, car2M;
        for (Object allRelation : allRelations) {
            RelationConstruct rel = (RelationConstruct) allRelation;

            java.util.Enumeration e = rel.getConnections().elements();
            java.util.List<CardinalityConstruct> cards = new Vector<CardinalityConstruct>();
            while (e.hasMoreElements()) {
                Connection c = ((Connection) e.nextElement());
                if (c.getOne() instanceof CardinalityConstruct)
                    cards.add((CardinalityConstruct) c.getOne());
                if (c.getTwo() instanceof CardinalityConstruct)
                    cards.add((CardinalityConstruct) c.getTwo());
            }
            if (cards.size() != 2) break;
            car1M = (Cardinality) ((CardinalityConstruct) cards.get(0)).getModel();
            car2M = (Cardinality) ((CardinalityConstruct) cards.get(1)).getModel();
            String name = car1M.getName();
            String comment = car1M.getComment();
            boolean arb = car1M.getArbitrary();
            //boolean multi = car1M.getMultiCardinality();

            boolean glue = car1M.getGlue();
            car1M.setGlue(car2M.getGlue());
            car2M.setGlue(glue);


            car1M.setName(car2M.getName());
            car1M.setComment(car2M.getComment());
            car1M.setArbitrary(car2M.getArbitrary());
            //car1M.setMultiCardinality(car2M.getMultiCardinality());
            car2M.setName(name);
            car2M.setComment(comment);
            car2M.setArbitrary(arb);
            //car2M.setMultiCardinality(multi);
        }
    }

    /**
     * Returns entity with ID
     */
    public EntityConstruct getEntity(int id) {
        return (EntityConstruct) getConceptualObject(id);
    }

    /**
     * Returns entity
     */
    private EntityConstruct getEntity(DGroupTool dg, int id) {
        int cnt = dg.getItemCount();
        Item item;
        EntityConstruct ent = null;

        for (int i = 0; i < cnt; i++) {
            item = dg.getItem(i);
            if (item instanceof DGroupTool) {
                if ((ent = getEntity((DGroupTool) item, id)) != null)
                    return ent;
            } else if (item instanceof EntityConstruct
                    && id == ((((EntityConstruct) item)
                    .getModel()).getID()))
                return (EntityConstruct) item;
        }
        return ent;
    }

    /**
     * Returns highest coordinates in the schema
     */
    public int[] getHighestRect() {
        int rect[] = {0, 0}, r[] = {0, 0};
        for (int i = 0; i < getItemCount(); i++) {
            Item item = getItem(i);
            if (item instanceof DGroupTool) {
                r = getHighestRect((DGroupTool) item);
                if (r[0] > rect[0])
                    rect[0] = r[0];
                if (r[1] > rect[1])
                    rect[1] = r[1];
            } else {
                if ((r[0] = ((ConceptualConstructObject) item).getL()) > rect[0])
                    rect[0] = r[0];
                if ((r[1] = ((ConceptualConstructObject) item).getT()) > rect[1])
                    rect[1] = r[1];
            }
        }
        return rect;
    }

    private int[] getHighestRect(DGroupTool dg) {
        int rect[] = {0, 0}, r[] = {0, 0};
        for (int i = 0; i < dg.getItemCount(); i++) {
            Item item = dg.getItem(i);
            if (item instanceof DGroupTool) {
                r = getHighestRect((DGroupTool) item);
                if (r[0] > rect[0])
                    rect[0] = r[0];
                if (r[1] > rect[1])
                    rect[1] = r[1];
            } else {
                if ((r[0] = ((ConceptualConstructObject) item).getL()) > rect[0])
                    rect[0] = r[0];
                if ((r[1] = ((ConceptualConstructObject) item).getT()) > rect[1])
                    rect[1] = r[1];
            }
        }
        return rect;
    }

    /**
     * Returns the model object - from work Aleš kopecký.
     *
     * @return The model.
     * @see ViewController#getModel()
     */
    public Object getModel() {
        return model;
    }

    /**
     * According to the implementation of the <code>FontManager</code> its
     * returns the metrics of the font, that is used to draw string at 100%
     * scale.
     *
     * @return The font metrics.
     * @see FontManager#getReferentFontMetrics()
     */
    public java.awt.FontMetrics getReferentFontMetrics() {
        try {
            return ((FontManager) getPaintPlace()).getReferentFontMetrics();
        } catch (ClassCastException e) {
            return null;
        }
    }

    /**
     * Returns relation with ID
     */
    public RelationConstruct getRelation(int id) {
        return (RelationConstruct) getConceptualObject(id);
    }

    /**
     * Simply sets the default mouse cursor because nothing can be drop on the
     * desktop.
     */
    public void handleDragOverEvent(DragOverEvent event) {
        event.getComponent().setCursor(
                Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    /**
     * Simply sets the default mouse cursor.
     */
    public void handleMouseMoveEvent(cz.green.event.MouseMoveEvent event) {
        event.getComponent().setCursor(
                Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    /**
     * Shows the context menu for the desktop. Removes all items from the menu,
     * add new a display.
     */
    public void handlePopupMenuEvent(PopupMenuEvent event) {
        try {
            JPopupMenu menu = event.getMenu();
            menu.removeAll();
            JMenuItem item;
            /*  		item = new JMenuItem("Properties", new ImageIcon(
                                ClassLoader.getSystemResource("mProperty.gif")));
                        item.setHorizontalTextPosition(JMenuItem.RIGHT);
                        item.addActionListener(new ParamActionAdapter(this, "propEditing",
                                new Boolean(true), boolean.class));
                        menu.add(item);
            */
            item = new JMenuItem("Add entity", new ImageIcon(ClassLoader
                    .getSystemResource("img/mEntity.gif")));
            item.setHorizontalTextPosition(JMenuItem.RIGHT);
            item.addActionListener(new ParamActionAdapter(getPaintPlace(),
                    "addingEntity", null, EntityConstruct.class));
            menu.add(item);

            if (model.getNotationType() == NotationType.CHEN) {
                item = new JMenuItem("Add relationship", new ImageIcon(ClassLoader
                        .getSystemResource("img/mRelation.gif")));
                item.setHorizontalTextPosition(JMenuItem.RIGHT);
                item.addActionListener(new ActionAdapter(getPaintPlace(),
                        "addingRelation"));
                menu.add(item);
            }
            menu.show(event.getComponent(), event.getRealX(), event.getRealY());
        } catch (NoSuchMethodException e) {
        }
    }

    /**
     * This handler call managers method <code>selectItem</code> and the
     * forces the reapinting. If selects the item then calls
     * <code>changeZOrder</code>.
     *
     * @param <code>event</code> Event with all needed values.
     * @see SelectItemEvent
     * @see cz.green.event.interfaces.PaintableManager#selectItem(cz.green.event.interfaces.SelectableItem , boolean)
     * @see cz.green.event.interfaces.Manager#changeZOrder(cz.green.event.interfaces.Item , boolean)
     */
    public void handleSelectItemEvent(SelectItemEvent event) {
        super.handleSelectItemEvent(event);
        if (!event.getAddItem()) {
            propEditing(false);
        }
    }

    /**
     * This method was created by Jiri Mares
     *
     * @param elems Vector
     * @param event cz.green.event.SelectItemExEvent
     */
    public void isModelIn(Vector elems, SelectItemExEvent event) {
        int size = wins.size();
        ViewController vc;
        Object o;
        int index;
        for (int i = 0; i < size; i++) {
            if (elems.size() == 0)
                return;
            if ((o = wins.elementAt(i)) instanceof ViewController) {
                vc = (ViewController) o;
                if ((index = elems.indexOf(vc.getModel())) != -1) {
                    elems.removeElementAt(index);
                    ((Item) o).invokeEventHandler(event);
                }
            }
            if (o instanceof ModelFinder) {
                ((ModelFinder) o).isModelIn(elems, event);
            }
        }
    }

    /**
     * Paints the desktop - invokes inherited method and paints the connections.
     */
    public void paint(java.awt.Graphics g) {
        super.paint(g);
        if (connections != null)
            connections.paint(g);
    }

    /**
     * Prints the desktop and and connections.
     *
     * @see #connections
     */
    public void print(java.awt.Graphics g) {
        super.print(g);
        if (connections != null)
            connections.print(g);
    }

    /**
     * This method was created by Jiri Mares
     */
    public void propEditing(boolean show) {
        PropertyListDialog.setBeanAndEdit(getModel(), ((ConceptualObject) getModel()).getName());
    }

    /**
     * Is used to sets this instance as <code>propertyChangeListener</code>
     * for its model. Invoked automaticly after deserializaing of the instance
     * the atribute.
     */
    private void readObject(java.io.ObjectInputStream s)
            throws java.io.IOException, ClassNotFoundException {
        s.defaultReadObject();
        pcs = new java.beans.PropertyChangeSupport(this);
    }

    /**
     * Removess PropertyChangeListener that catched the changes of the workMode
     * property.
     */
    public void removePropertyChangeListener(
            java.beans.PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

    /**
     * This method was created by Jiri Mares
     *
     * @param lst cz.omnicom.ermodeller.errorlog.ShowErrorListener
     */
    public void removeShowErrorListener(ShowErrorListener lst) {
        model.removeShowErrorListener(lst);
    }

    /**
     * Same as inherited but repaints also all connections.
     *
     * @see #connections
     */
    public void repaintItem(PaintableItem item) {
        super.repaintItem(item);
        if (connections != null)
            connections.paint(getPaintPlace().getGraphics());
    }

    /**
     * Sets actual scale.
     *
     * @return The actual scale.
     */
    public float setScale(float scale) {
        float old = getScale();
        float ret = super.setScale(scale);
        pcs.firePropertyChange("scale", old, scale);
        return ret;
    }

    /**
     * Writes data for schema into XML file
     *
     * @param pw java.io.PrintWriter
     */
    public void write(String encoding, UserTypeStorageVector typesVector,
                      java.io.PrintWriter pw) {
        Schema schema = (Schema) getModel();
        float sc = getScale();
        int cnt = getItemCount();

        pw.println("<?xml version=\"1.0\" encoding=\"" + encoding + "\"?>"); // encoding=\"UTF-16\"
        pw.println("<!-- ER Modeller version 4.0 schema -->");
        pw.println("<!DOCTYPE schema [");
        pw
                .println("<!ELEMENT schema (scale,left,top,width,height,id,name,notation,comment,usertype*,(entity*,atribute*,relation*,unique*,cardinality*,strong*)*)>");
        pw.println("<!ELEMENT scale (#PCDATA)>");
        pw.println("<!ELEMENT left (#PCDATA)>");
        pw.println("<!ELEMENT top (#PCDATA)>");
        pw.println("<!ELEMENT width (#PCDATA)>");
        pw.println("<!ELEMENT height (#PCDATA)>");
        pw.println("<!ELEMENT id (#PCDATA)>");
        pw.println("<!ELEMENT name (#PCDATA)>");
        pw.println("<!ELEMENT comment (#PCDATA)>");
        pw.println("<!ELEMENT usertype (typename, datatypedef)>");
        pw.println("<!ELEMENT entity (left,top,width,height,id,name,comment)>");
        pw
                .println("<!ELEMENT atribute (left,top,width,height,id,name,comment,datatype,arbitrary,(ent|rel))>");
        pw
                .println("<!ELEMENT relation (left,top,width,height,id,name,comment)>");
        pw
                .println("<!ELEMENT unique (left,top,width,height,id,name,comment,ent,atr*,primary)>");
        pw
                .println("<!ELEMENT cardinality (left,top,width,height,id,name,comment,ent,rel,arbitrary,multi,glue)>");
        pw.println("<!ELEMENT strong (left,top,width,height,ent,uni)>");
        pw.println("<!ELEMENT datatype (#PCDATA)>");
        pw.println("<!ELEMENT typename (#PCDATA)>");
        pw.println("<!ELEMENT datatypedef (datatype, item*)>");
        pw.println("<!ELEMENT item (itemname, datatype)>");
        pw.println("<!ELEMENT itemname (#PCDATA)>");
        pw.println("<!ELEMENT arbitrary (#PCDATA)>");
        pw.println("<!ELEMENT ent (#PCDATA)>");
        pw.println("<!ELEMENT rel (#PCDATA)>");
        pw.println("<!ELEMENT atr (#PCDATA)>");
        pw.println("<!ELEMENT primary (#PCDATA)>");
        pw.println("<!ELEMENT multi (#PCDATA)>");
        pw.println("<!ELEMENT glue (#PCDATA)>");
        pw.println("<!ELEMENT uni (#PCDATA)>");
        pw.println("]>");
        pw.println("<schema>");
        pw.print("\t<scale>");
        pw.print(sc);
        pw.println("</scale>");
        pw.print("\t<left>");
        pw.print(rect[0][0]);
        pw.println("</left>");
        pw.print("\t<top>");
        pw.print(rect[1][0]);
        pw.println("</top>");
        pw.print("\t<width>");
        pw.print(rect[0][1] - rect[0][0]);
        pw.println("</width>");
        pw.print("\t<height>");
        pw.print(rect[1][1] - rect[1][0]);
        pw.println("</height>");
        schema.write(pw);
        for (Enumeration enu = typesVector.elements(); enu.hasMoreElements();) {
            ((UserTypeStorage) enu.nextElement()).write(pw);
        }
        for (int i = 0; i < cnt; i++) {
            Item item = getItem(i);
            if (item instanceof DGroupTool)
                writeItem((DGroupTool) item, pw);
            else
                ((ConceptualConstructObject) getItem(i)).write(pw);
        }
        pw.println("</schema>");
    }

    private void writeItem(DGroupTool dg, java.io.PrintWriter pw) {
        Item item;
        for (int j = 0; j < dg.getItemCount(); j++) {
            item = dg.getItem(j);
            if (item instanceof DGroupTool)
                writeItem((DGroupTool) item, pw);
            else
                ((ConceptualConstructObject) item).write(pw);
        }
    }
}