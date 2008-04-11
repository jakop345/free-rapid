package cz.green.ermodeller;

import cz.green.event.exceptions.ImpossibleNegativeValueException;
import cz.green.event.exceptions.ItemNotInsideManagerException;
import cz.green.event.interfaces.Item;
import cz.green.event.interfaces.Manager;
import cz.green.event.interfaces.PaintableManager;
import cz.green.eventtool.Connection;
import cz.green.eventtool.ConnectionLine;
import cz.green.swing.ShowException;

import javax.swing.*;
import java.awt.*;

/**
 * This type represents the participation on the relation. Holds the cardinality and parciality of the participation.
 * Adds the role name. Has the pointer to the model object.
 * <p/>
 * Its created by <code>Relation</code> method <code>createCardinality</code>.
 *
 * @see Relation#createCardinality(cz.green.ermodeller.Entity, cz.green.event.interfaces.Manager ,int,int)
 */
public class Cardinality extends ConceptualObject {
    /**
     * The model of the cardinality -- object from the Aleš Kopecký work
     */
    cz.omnicom.ermodeller.conceptual.Cardinality model = null;

    /**
     * Constructs new cardinality. Counts the size from the role name and integrit restrictions and also set as
     * <code>propertyChangeListener</code> to its model to know about changes of the <b>name</b>, <b>arbitrary</b> and
     * <b>multiCardinality</b> changes.
     *
     * @param car     The model of this cardinality.
     * @param manager The window group (or desktop) where to put the new cardinality.
     * @param left    The x coordinate of the left top point of the new cardinality.
     * @param top     The y coordinate of the left top point of the new cardinality.
     * @throws <code>java.lang.NullPointerException</code>
     *          Thrown by inherited constructor.
     * @throws <code>cz.green.event.ImpossibleNegativeValueException</code>
     *          Thrown by inherited constructor.
     * @see ConceptualObject#ConceptualObject(cz.green.event.interfaces.Manager ,int,int,int,int)
     */
    public Cardinality(cz.omnicom.ermodeller.conceptual.Cardinality car, Manager manager, int left, int top) throws NullPointerException, ImpossibleNegativeValueException {
        //inhereted constructor
        super(manager, left, top, 10, 10);
        //set as property change listener
        car.addPropertyChangeListener(this);
        model = car;
        //counts the size
        java.awt.Dimension dim = countSize();
        rect[0][1] = rect[0][0] + dim.width;
        rect[1][1] = rect[1][0] + dim.height;
    }

    /**
     * Counts the size from the role name and the integrit restrictions.
     *
     * @return The counted size needful for holding.
     */
    protected java.awt.Dimension countSize() {
        String name = model.getName();
        java.awt.FontMetrics fm;
        try {
            fm = ((FontManager) manager).getReferentFontMetrics();
            int w1 = fm.stringWidth(name), w2 = fm.stringWidth("N:N"), height = fm.getAscent();
            return new java.awt.Dimension(height + ((w1 > w2) ? w1 : w2), (int) (2.25 * height));
        } catch (ClassCastException e) {
            return new java.awt.Dimension(10, 10);
        }
    }

    /**
     * This method adds items to the context menu, which are specific to the atribute.
     *
     * @param menu  The popup menu where to add the new items.
     * @param event The event, which caused the context menu displaying. Is useful for determing targets of the methods
     *              call.
     * @return The filled menu.
     */
    protected JPopupMenu createMenu(JPopupMenu menu, PopupMenuEvent event) {
        super.createMenu(menu, event);
        if (model.getArbitrary()) {
            addMenuItem(menu, "Optional", "img/mNotMandatory.gif", getModel(), "setArbitrary", new Boolean(false), boolean.class);
        } else {
            addMenuItem(menu, "Mandatory", "img/mMandatory.gif", getModel(), "setArbitrary", new Boolean(true), boolean.class);
        }
        if (model.getMultiCardinality()) {
            addMenuItem(menu, "Unary cardinality", "img/mUnary.gif", getModel(), "setMultiCardinality", new Boolean(false), boolean.class);
        } else {
            addMenuItem(menu, "N-ary cardinality", "img/mMulti.gif", getModel(), "setMultiCardinality", new Boolean(true), boolean.class);
        }
        return menu;
    }

    /**
     * Get the entity that participation this object represents.
     */
    public Entity getEntity() {
        java.util.Enumeration e = connections.elements();
        while (e.hasMoreElements()) {
            Connection c = ((Connection) e.nextElement());
            if (c.getOne() instanceof Entity)
                return (Entity) (c.getOne());
            if (c.getTwo() instanceof Entity)
                return (Entity) (c.getTwo());
        }
        return null;
    }

    /**
     * Returns the model object.
     */
    public Object getModel() {
        return model;
    }

    /**
     * Get the relation that this object belongs to.
     */
    public Relation getRelation() {
        java.util.Enumeration e = connections.elements();
        while (e.hasMoreElements()) {
            Connection c = ((Connection) e.nextElement());
            if (c.getOne() instanceof Relation)
                return (Relation) (c.getOne());
            if (c.getTwo() instanceof Relation)
                return (Relation) (c.getTwo());
        }
        return null;
    }

    /**
     * Get the connection line ro relation that this object belongs to.
     */
    public ConnectionLine getRelationConnectionLine() {
        java.util.Enumeration e = connections.elements();
        while (e.hasMoreElements()) {
            Connection c = ((Connection) e.nextElement());
            if (c.getOne() instanceof Relation || c.getTwo() instanceof Relation)
                return ((ConnectionLine) c);
        }
        return null;
    }

    /**
     * We can't change the size of the cardinality, is counted automaticily -> return null.
     */
    public cz.green.event.ResizePoint[] getResizePoints() {
        return null;
    }

    /**
     * Handle event when soma element is dragging over. Can work only with <code>ConceptualConstruct</code> instances.
     */
    public void handleDragOverEvent(DragOverEvent event) {
        if (selected && event.getAdd())
            return;
        Item item = event.getItem();
        if (item instanceof ConceptualConstruct) {
            if (event.getAdd()) {
                ConceptualConstruct cc = (ConceptualConstruct) item;
                if (this.connectionTo(cc) == null) {
                    event.getComponent().setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
                    return;
                }
            }
        }
        event.getComponent().setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
    }

    /**
     * Handle event when soma element is dropping above. Can work only with <code>ConceptualConstruct</code> instances and
     * that action caused the reconnection to other entity or relation.
     */
    public void handleDropAboveEvent(DropAboveEvent event) {
        if (selected && event.getAdd())
            return;
        Item item = event.getItem();
        if (item instanceof ConceptualConstruct) {
            if (event.getAdd()) {
                ConceptualConstruct cc = (ConceptualConstruct) item;
                if (this.connectionTo(cc) == null) {
                    reconnect(cc);
                    event.setDropped(true);
                }
            }
        }
        event.getComponent().setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
    }

    /**
     * Handle moving event and adds restrictions to BIN and UML notation
     */
    public void handleExMovingEvent(ExMovingEvent event) {
        int dx, dy = 0;
        float scale = getManager().getScale();
        java.awt.Point cardinalityCenter = getCenter();
        java.awt.Rectangle er = getEntity().getBounds();
        java.awt.Rectangle r = getBounds();
        int eventX = (int) (event.getX() / scale);
        int eventY = (int) (event.getY() / scale);
        int eventDx = (int) (event.getDx());
        int eventDy = (int) (event.getDy());
//	int eventDx = (int) (event.getDx()/scale);
//	int eventDy = (int) (event.getDy()/scale);

        if (ACTUAL_NOTATION == BINARY || ACTUAL_NOTATION == UML) {
            if (eventX < er.x) {
                dx = (er.x - r.width / 2) - cardinalityCenter.x;
                if (cardinalityCenter.y + r.height / 2 + eventDy >= er.y
                        && cardinalityCenter.y - r.height / 2 + eventDy <= er.y + er.height)
                    dy = eventDy;
                else dy = 0;
                //|| cardinalityCenter.y > er.y + er.height)?0:eventDy;
                //dy = eventDy;
            } else if (eventX > (er.x + er.width)) {
                dx = (er.x + er.width + r.width / 2) - cardinalityCenter.x;
                if (cardinalityCenter.y + r.height / 2 + eventDy >= er.y
                        && cardinalityCenter.y - r.height / 2 + eventDy <= er.y + er.height)
                    dy = eventDy;
                else dy = 0;
            } else if (eventY < er.y) {
                dx = eventDx;
                dy = 0;
                dy = (er.y - r.height / 2) - cardinalityCenter.y;
//					dy = (int) (((er.y - r.height/2) - cardinalityCenter.y)/scale);
            } else if (cardinalityCenter.y > (er.y + er.height)) {
                dx = eventDx;
                dy = (er.y + er.height + r.height / 2) - cardinalityCenter.y;
            }/* Cardinality is inside Enity */ else {
                if (cardinalityCenter.x < er.x + er.width / 2)
                    dx = er.x - r.width / 2 - cardinalityCenter.x;
                else
                    dx = er.x + er.width + r.width / 2 - cardinalityCenter.x;
            }
        } else {
            dx = eventDx;
            dy = eventDy;
        }
        if (paintedFast) {
            ((PaintableManager) manager).repaintItemFast(this);
        } else {
            paintedFast = true;
            rectangle = getBounds();
        }
        try {
            move(dx, dy, false);
        } catch (ItemNotInsideManagerException e) {
        } finally {
            ((PaintableManager) manager).repaintItemFast(this);
        }
    }

    /**
     * Handle moving event and adds restrictions to BIN and UML notation
     */
    public void handleExMoveEvent(ExMoveEvent event) {
        int dx = 0, dy = 0;
        java.awt.Point cardinalityCenter = getCenter();
        java.awt.Rectangle er = getEntity().getBounds();
        java.awt.Rectangle r = getBounds();
/*	int eventX = (int) (event.getX()/scale);
	int eventY = (int) (event.getY()/scale);
	int eventDx = (int) (event.getDx());
	int eventDy = (int) (event.getDy());
*/
        if (ConceptualConstruct.ACTUAL_NOTATION == ConceptualConstruct.BINARY || ACTUAL_NOTATION == UML) {
            if (cardinalityCenter.x < er.x && cardinalityCenter.y < er.y) {
                dx = er.x - cardinalityCenter.x + r.height / 5;
                dy = er.y - cardinalityCenter.y - r.height / 2;
            } else if (cardinalityCenter.x > er.x + er.width && cardinalityCenter.y < er.y) {
                dx = er.x + er.width - cardinalityCenter.x - r.height / 5;
                dy = er.y - cardinalityCenter.y - r.height / 2;
            } else if (cardinalityCenter.x < er.x && cardinalityCenter.y > er.y + er.height) {
                dx = er.x - cardinalityCenter.x + r.height / 5;
                dy = er.y + er.height - cardinalityCenter.y + r.height / 2;
            } else if (cardinalityCenter.x > er.x + er.width && cardinalityCenter.y > er.y + er.height) {
                dx = er.x + er.width - cardinalityCenter.x - r.height / 5;
                dy = er.y + er.height - cardinalityCenter.y + r.height / 2;
            } else {
            }
        } else {
            dx = event.getDx();
            dy = event.getDy();
        }
        if (paintedFast) {
            ((PaintableManager) manager).repaintItemFast(this);
            paintedFast = false;
        } else {
            rectangle = getBounds();
        }
        try {
            move(dx, dy, true);
            move(event.getDx(), event.getDy(), true);
            if (rectangle != null) {
                r = rectangle;
                rectangle = null;
                ((PaintableManager) manager).repaintRectangle(r.x, r.y, r.width, r.height);
            }
            ((PaintableManager) manager).repaintItem(this);
        } catch (ItemNotInsideManagerException e) {
        }
    }

    /**
     * Handle remove event and adds only one functionality -> remove cardinality from the model's object.
     */
    public void handleRemoveEvent(cz.green.event.RemoveEvent event) {
        try {
            Relation rel = getRelation();
            rel.removeCardinality(this);
            super.handleRemoveEvent(event);
/*	neni mozno pouzit protoze pri decompose to chce mazat vztah, ktery je posleze smazan jinou metodou, ktera jej nenajde
  			if(ACTUAL_NOTATION != CHEN)
			if (rel.getConnections() == null || rel.getConnections().size() == 0)
				rel.handleRemoveEvent(new RemoveEvent(rel.getBounds().x, rel.getBounds().y, null));
*/
        } catch (Throwable x) {
            ShowException d = new ShowException(null, "Error", x, true);
        }
    }

    public void moveCardinality(ExMovingEvent event) {
        this.handleExMovingEvent(event);
        this.handleExMoveEvent((ExMoveEvent) event);
    }

    /**
     * Determine whether this cardinality is useful to find compactable entity.
     *
     * @return <code>true</code> if it is.
     */
    public boolean isCompactable() {
        return (model.getArbitrary() && !model.getMultiCardinality());
    }

    /**
     * Determine whether the entity has multi participation on the relation.
     *
     * @return <code>true</code> if it has.
     */
    public boolean isMultiCardinality() {
        return model.getMultiCardinality();
    }

    /**
     * Paints the cardinality - it means to draw integrit restrictions and role name.
     */
    public void paint(java.awt.Graphics g) {
        final Stroke stroke = updateStrokeWithAliasing(g);
        java.awt.Rectangle r = getBounds();
        String name = model.getName();
        boolean arbitrary = model.getArbitrary();
        boolean multiCard = model.getMultiCardinality();
        java.awt.FontMetrics fm = g.getFontMetrics();
        String ir;
        switch (ACTUAL_NOTATION) {
            case (CHEN):
                if (selected) {
                    g.setColor(getSelectedBackgroundColor());
                    g.fillRect(r.x, r.y, r.width, r.height);
                }
                g.setColor(getForegroundColor());
                ir = ((arbitrary) ? "1" : "0") + ":" + ((multiCard) ? "N" : "1");
                g.drawString(ir, r.x + (int) ((r.width - fm.stringWidth(ir)) / 2), r.y + fm.getAscent());
                g.drawString(name, r.x + (int) ((r.width - fm.stringWidth(name)) / 2), r.y + r.height);
                break;
            case (BINARY):
                if (selected) {
                    g.setColor(getSelectedBackgroundColor());
                    g.fillRect(r.x, r.y, r.width, r.height);
                }
                g.setColor(getForegroundColor());
                ir = ((arbitrary) ? "1" : "0") + ":" + ((multiCard) ? "N" : "1");
                java.awt.Point cardinalityCenter = getCenter();
                java.awt.Rectangle er = getEntity().getBounds();

                if (cardinalityCenter.x < er.x && !(cardinalityCenter.y < er.y || cardinalityCenter.y > er.y + er.height)) {
                    g.drawLine(r.x + r.width / 2, r.y + r.height / 2, r.x + r.width, r.y + r.height / 2);
                    if (multiCard) {
                        g.drawLine(r.x + r.width, r.y + r.height / 2 - r.height / 5, r.x + r.width - r.height / 2, r.y + r.height / 2);
                        g.drawLine(r.x + r.width, r.y + r.height / 2 + r.height / 5, r.x + r.width - r.height / 2, r.y + r.height / 2);
                    }
                    g.drawString(name, r.x, r.y + r.height);
                    paintLineToCardinality(g, false);
                } else
                if (cardinalityCenter.x > (er.x + er.width) && !(cardinalityCenter.y < er.y || cardinalityCenter.y > er.y + er.height)) {
                    g.drawLine(r.x, r.y + r.height / 2, r.x + r.width / 2, r.y + r.height / 2);
                    if (multiCard) {
                        g.drawLine(r.x, r.y + r.height / 2 - r.height / 5, r.x + r.height / 2, r.y + r.height / 2);
                        g.drawLine(r.x, r.y + r.height / 2 + r.height / 5, r.x + r.height / 2, r.y + r.height / 2);
                    }
                    g.drawString(name, r.x + (int) ((r.width - fm.stringWidth(name))), r.y + r.height);
                    paintLineToCardinality(g, false);
                } else if (cardinalityCenter.y < er.y) {
                    g.drawLine(r.x + r.width / 2, r.y + r.height / 2, r.x + r.width / 2, r.y + r.height);
                    if (multiCard) {
                        g.drawLine(r.x + r.width / 2 - r.height / 5, r.y + r.height, r.x + r.width / 2, r.y + r.height / 2 + 1);
                        g.drawLine(r.x + r.width / 2 + r.height / 5, r.y + r.height, r.x + r.width / 2, r.y + r.height / 2 + 1);
                    }
                    g.drawString(name, r.x + (int) ((r.width - fm.stringWidth(name)) / 2), r.y + fm.getAscent());
                    paintLineToCardinality(g, true);
                } else if (cardinalityCenter.y > (er.y + er.height)) {
                    g.drawLine(r.x + r.width / 2, r.y, r.x + r.width / 2, r.y + r.height / 2);
                    if (multiCard) {
                        g.drawLine(r.x + r.width / 2 - r.height / 5, r.y, r.x + r.width / 2, r.y + r.height / 2 - 1);
                        g.drawLine(r.x + r.width / 2 + r.height / 5, r.y, r.x + r.width / 2, r.y + r.height / 2 - 1);
                    }
                    g.drawString(name, r.x + (int) ((r.width - fm.stringWidth(name)) / 2), r.y + r.height);
                    paintLineToCardinality(g, false);
                } else {
                    g.drawRect(r.x, r.y, r.width, r.height);
                }
                break;
            case (UML):
                if (selected) {
                    g.setColor(getSelectedBackgroundColor());
                    g.fillRect(r.x, r.y, r.width, r.height);
                }
                g.setColor(getForegroundColor());
                ir = ((arbitrary) ? "1" : "0") + ".." + ((multiCard) ? "*" : "1");
                if (SHOW_SHORTEN_CARD_IN_UML == 1 && !arbitrary && multiCard) ir = "*";
                if (SHOW_SHORTEN_CARD_IN_UML == 1 && arbitrary && !multiCard) ir = "1";
                g.drawString(ir, r.x + (int) ((r.width - fm.stringWidth(ir)) / 2), r.y + fm.getAscent());
                g.drawString(name, r.x + (int) ((r.width - fm.stringWidth(name)) / 2), r.y + r.height - fm.getAscent() / 4);
                break;
        }
        updateBackupStroke(g, stroke);
    }

    private void paintLineToCardinality(java.awt.Graphics g, boolean UP) {
        java.awt.Rectangle r = getBounds();
        Point rcenter = getRelation().getRealCenter();
        Point ccenter = getRealCenter();
        int dx = rcenter.x - ccenter.x;
        int dy = ccenter.y - rcenter.y;
        float konst = (float) dy / (float) dx;

        Graphics2D g2 = (Graphics2D) g;
        float dash1[] = {8f};//default 4.5f
        float dash2[] = {333333.5f};
        if (!model.getArbitrary()) {
            BasicStroke roundStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f, dash1, 0.0f);
            g2.setStroke(roundStroke);
        }
        // na horni stenu
        if (dy > 0 && (((r.width / 2) * konst) > r.height / 2 || ((r.width / 2) * konst) < -r.height / 2)) {
            if (!UP)
                g.drawLine(r.x + r.width / 2, r.y + r.height / 2, (int) (r.x + r.width / 2 + (r.height / 2) / konst), r.y);
        } else if (dy < 0 && (((r.width / 2) * konst) > r.height / 2 || ((r.width / 2) * konst) < -r.height / 2)) {
            if (UP)
                g.drawLine(r.x + r.width / 2, r.y + r.height / 2, (int) (r.x + r.width / 2 - (r.height / 2) / konst), r.y + r.height);
        } else {
            if (dx > 0)
                g.drawLine(r.x + r.width / 2, r.y + r.height / 2, r.x + r.width, (int) (r.y + r.height / 2 - (r.width / 2) * konst));
            if (dx < 0)
                g.drawLine(r.x + r.width / 2, r.y + r.height / 2, r.x, (int) (r.y + r.height / 2 + (r.width / 2) * konst));
        }
        if (!model.getArbitrary()) {
            BasicStroke lineStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 100f, dash2, 0.0f);
            g2.setStroke(lineStroke);
        }
    }

    /**
     * Prints the cardinality on the printer graphics. Same as paint but don't changes the colors.
     */
    public void print(java.awt.Graphics g) {
        java.awt.Rectangle r = getBounds();
        String name = model.getName();
        boolean arbitrary = model.getArbitrary();
        boolean multiCard = model.getMultiCardinality();
        java.awt.FontMetrics fm = g.getFontMetrics();
        String ir;
        switch (ACTUAL_NOTATION) {
            case (CHEN):
                g.setColor(getForegroundColor());
                ir = ((arbitrary) ? "1" : "0") + ":" + ((multiCard) ? "N" : "1");
                g.drawString(ir, r.x + (int) ((r.width - fm.stringWidth(ir)) / 2), r.y + fm.getAscent());
                g.drawString(name, r.x + (int) ((r.width - fm.stringWidth(name)) / 2), r.y + r.height);
                break;
            case (BINARY):
                g.setColor(getForegroundColor());
                ir = ((arbitrary) ? "1" : "0") + ":" + ((multiCard) ? "N" : "1");
                java.awt.Point cardinalityCenter = getCenter();
                java.awt.Rectangle er = getEntity().getBounds();

                if (cardinalityCenter.x < er.x && !(cardinalityCenter.y < er.y || cardinalityCenter.y > er.y + er.height)) {
                    g.drawLine(r.x + r.width / 2, r.y + r.height / 2, r.x + r.width, r.y + r.height / 2);
                    if (multiCard) {
                        g.drawLine(r.x + r.width, r.y + r.height / 2 - r.height / 5, r.x + r.width - r.height / 2, r.y + r.height / 2);
                        g.drawLine(r.x + r.width, r.y + r.height / 2 + r.height / 5, r.x + r.width - r.height / 2, r.y + r.height / 2);
                    }
                    g.drawString(name, r.x, r.y + r.height);
                    paintLineToCardinality(g, false);
                } else
                if (cardinalityCenter.x > (er.x + er.width) && !(cardinalityCenter.y < er.y || cardinalityCenter.y > er.y + er.height)) {
                    g.drawLine(r.x, r.y + r.height / 2, r.x + r.width / 2, r.y + r.height / 2);
                    if (multiCard) {
                        g.drawLine(r.x, r.y + r.height / 2 - r.height / 5, r.x + r.height / 2, r.y + r.height / 2);
                        g.drawLine(r.x, r.y + r.height / 2 + r.height / 5, r.x + r.height / 2, r.y + r.height / 2);
                    }
                    g.drawString(name, r.x + (int) ((r.width - fm.stringWidth(name))), r.y + r.height);
                    paintLineToCardinality(g, false);
                } else if (cardinalityCenter.y < er.y) {
                    g.drawLine(r.x + r.width / 2, r.y + r.height / 2, r.x + r.width / 2, r.y + r.height);
                    if (multiCard) {
                        g.drawLine(r.x + r.width / 2 - r.height / 5, r.y + r.height, r.x + r.width / 2, r.y + r.height / 2 + 1);
                        g.drawLine(r.x + r.width / 2 + r.height / 5, r.y + r.height, r.x + r.width / 2, r.y + r.height / 2 + 1);
                    }
                    g.drawString(name, r.x + (int) ((r.width - fm.stringWidth(name)) / 2), r.y + fm.getAscent());
                    paintLineToCardinality(g, true);
                } else if (cardinalityCenter.y > (er.y + er.height)) {
                    g.drawLine(r.x + r.width / 2, r.y, r.x + r.width / 2, r.y + r.height / 2);
                    if (multiCard) {
                        g.drawLine(r.x + r.width / 2 - r.height / 5, r.y, r.x + r.width / 2, r.y + r.height / 2 - 1);
                        g.drawLine(r.x + r.width / 2 + r.height / 5, r.y, r.x + r.width / 2, r.y + r.height / 2 - 1);
                    }
                    g.drawString(name, r.x + (int) ((r.width - fm.stringWidth(name)) / 2), r.y + r.height);
                    paintLineToCardinality(g, false);
                } else {
                    g.drawRect(r.x, r.y, r.width, r.height);
                }
                break;
            case (UML):
                g.setColor(getForegroundColor());
                ir = ((arbitrary) ? "1" : "0") + ".." + ((multiCard) ? "*" : "1");
                if (SHOW_SHORTEN_CARD_IN_UML == 1 && !arbitrary && multiCard) ir = "*";
                if (SHOW_SHORTEN_CARD_IN_UML == 1 && arbitrary && !multiCard) ir = "1";
                g.drawString(ir, r.x + (int) ((r.width - fm.stringWidth(ir)) / 2), r.y + fm.getAscent());
                g.drawString(name, r.x + (int) ((r.width - fm.stringWidth(name)) / 2), r.y + r.height - fm.getAscent() / 4);
                break;
        }
        r = null;
    }

    /**
     * Invoked when some property changes its value. When the property is <b>name</b> (role name) then recounts the size and
     * always when invoken repaints the item.
     */
    public void propertyChange(java.beans.PropertyChangeEvent e) {
        ((cz.omnicom.ermodeller.conceptual.ConceptualObject) getModel()).setChanged(true);
        if (e.getPropertyName().equals("name")) {
            int[][] r = rect;
            java.awt.Dimension dim = countSize(), real = new java.awt.Dimension(r[0][1] - r[0][0], r[1][1] - r[1][0]);
            int dx = dim.width - real.width;
            int dy = dim.height - real.height;
            java.awt.Rectangle b = getBounds();
            try {
                resize(dx, dy, cz.green.event.ResizePoint.RIGHT | cz.green.event.ResizePoint.BOTTOM, true);
            } catch (ItemNotInsideManagerException ex) {
            }
            b = b.union(getBounds());
            ((PaintableManager) manager).repaintRectangle(b.x, b.y, b.width, b.height);
        }
        if (e.getPropertyName().equals("arbitrary")) {
            ConnectionLine conn = getRelationConnectionLine();
            conn.setConnectionMandatory(model.getArbitrary());
            if (ACTUAL_NOTATION == BINARY) {
                java.awt.Rectangle r = conn.getBounds();
                conn.getManager().repaintRectangle(r.x, r.y, r.width, r.height);
            }
        }
        java.awt.Rectangle b = getBounds();
        ((PaintableManager) manager).repaintRectangle(b.x, b.y, b.width, b.height);

    }

    /**
     * Is used to sets this instance as <code>propertyChangeListener</code> for its model. Invoked automaticly after
     * deserializaing of the instance the atribute.
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
     * Invoken when wants to change its relation (entity participation on other relation - <code>cc</code>) or entity
     * (participation other entity - <code>cc</code>).
     *
     * @param cc Whether instance of the <code>Entity</code> changes the entity otherwise (instance <code>Relation</code>)
     *           change the relation.
     */
    protected void reconnect(ConceptualConstruct cc) {
        if (cc instanceof Entity) {
            //changes the participating entity
            Entity old = getEntity();
            Connection conn = connectionTo(old);
            cz.omnicom.ermodeller.conceptual.Entity cEnt = (cz.omnicom.ermodeller.conceptual.Entity) cc.getModel();
            try {
                model.setEntity(cEnt);
                cc.getManager().add(this);
                //reconnect
                if (conn.getOne() == old) {
                    conn.setOne(cc);
                    return;
                }
                if (conn.getTwo() == old) {
                    conn.setTwo(cc);
                    return;
                }
            } catch (Throwable x) {
                ShowException d = new ShowException(null, "Error", x, true);
            }
        }
        if (cc instanceof Relation) {
            //change the relation on which participate
            Relation old = getRelation();
            Connection conn = connectionTo(old);
            cz.omnicom.ermodeller.conceptual.Relation cRel = (cz.omnicom.ermodeller.conceptual.Relation) cc.getModel();
            try {
                model.setRelation(cRel);
                if (conn.getOne() == old) {
                    conn.setOne(cc);
                    return;
                }
                if (conn.getTwo() == old) {
                    conn.setTwo(cc);
                }
            } catch (Throwable x) {
                ShowException d = new ShowException(null, "Error", x, true);
            }
        }
    }

    /**
     * This method was created by Jiri Mares
     */
    public void transformToRelation(Entity ent, Manager man) {
        //others cardinalities decompose as new relations
        java.awt.Point p = ent.getCenter(getEntity());
        Relation rel = Relation.createRelation(model.getSchema(), man, p.x, p.y);
        ((cz.omnicom.ermodeller.conceptual.Relation) rel.getModel()).setName(model.getName());
        model.setName("");
        reconnect(getRelation());
        p = ent.getCenter(getRelation());
        Cardinality car = rel.createCardinality(ent, manager, p.x, p.y);
        ((cz.omnicom.ermodeller.conceptual.Cardinality) car.getModel()).setArbitrary(true);
        ((cz.omnicom.ermodeller.conceptual.Cardinality) car.getModel()).setMultiCardinality(false);
    }

    /**
     * This method was created by Jiri Mares
     */
    public void transformToStrongAddiction(Entity son, Manager man) {
        //java.awt.Point p = son.getCenter(getEntity());
//	StrongAddiction.createStrongAddiction(getEntity(), son, son.getManager(), p.x, p.y);
        StrongAddiction.createStrongAddiction(getEntity(), son, son.getManager(), getBounds().x, getBounds().y);
    }

    /**
     * Writes data for cardinality into XML file
     *
     * @param pw java.io.PrintWriter
     */
    public void write(java.io.PrintWriter pw) {
        pw.println("\t<cardinality>");
        super.write(pw);
        pw.println("\t</cardinality>");
    }
}
