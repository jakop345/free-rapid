package cz.green.ermodeller;

import cz.green.event.MoveEvent;
import cz.green.event.ResizeEvent;
import cz.green.eventtool.Connection;
import cz.green.eventtool.ConnectionLine;
import cz.green.eventtool.ConnectionManager;
import cz.green.swing.ShowException;
import cz.omnicom.ermodeller.datatype.DataType;

import javax.swing.*;
import java.awt.*;

/**
 * This type is designed to show atributes. Has its owner to which is connected and can participate on the unique keys.
 * From its model shows his <b>name</b> and <b>arbitrary</b> properties.
 * <p/>
 * To create atribute, there is a method <code>createAtribute</code> of the <code>ConceptualConstruct</code> class.
 *
 * @see ConceptualConstruct#createAtribute(int,int)
 */
public class Atribute extends ConceptualObject {
    /**
     * The owner of the atribute
     */
    ConceptualConstruct cc = null;
    /**
     * The model of the atribute - object from the Aleš Kopecký work.
     */
    cz.omnicom.ermodeller.conceptual.Atribute model = null;
    /**
     * Flag; true - Atribute is first member of Primary key
     */
    boolean PKfirst = false;

    /**
     * Constructs atribute for the model. It means to count the needed size and set yourself as
     * <code>PropertyChangeListener</code> to the model.
     *
     * @param atr     It's the model of the new view and controller. To this JavaBeans is this class
     *                <code>PropertyChangeListener</code>.
     * @param cc      The <code>ConceptualConstruct</code> - it means entity or relation which is this the atribute. We
     *                say the owner of the new atribute.
     * @param manager The window group wherer to put the new atribute. It's the owner's group.
     * @param left    The x coordinate of the left top point of the new atribute.
     * @param top     The y coordinate of the left top point of the new atribute.
     * @throws <code>java.lang.NullPointerException</code>
     *          Thrown by inherited constructor.
     * @throws <code>cz.green.event.ImpossibleNegativeValueException</code>
     *          Thrown by inherited constructor.
     * @see ConceptualObject#ConceptualObject(cz.green.event.Manager,int,int,int,int)
     */
    public Atribute(cz.omnicom.ermodeller.conceptual.Atribute atr, ConceptualConstruct cc, cz.green.event.Manager manager, int left, int top) throws NullPointerException, cz.green.event.ImpossibleNegativeValueException {
        super(manager, left, top, 50, 50);
        //set model
        this.cc = cc;
        atr.addPropertyChangeListener(this);
        model = atr;
        //count the size
        java.awt.Dimension dim = countSize();
        rect[0][1] = rect[0][0] + dim.width;
        rect[1][1] = rect[1][0] + dim.height;
    }

    /**
     * This methods add the new connection to the atribute. Call inhereted method but add one important functionality.
     * When there is only one connection ant this connection is to the owner, it means that the atribute participate on
     * no unique key and the adding connection is to the first unique key. It means we have to remove the existing
     * connection to the owner and adds the connection to the first unique key.
     */
    public void addConnection(Connection conn) {
        Object removeElement = null;
        if (connections.size() == 1) {
            //there is only connection to relation or entity
            removeElement = connections.elementAt(0);
            Connection c = (Connection) removeElement;
            if ((c.getOne() != cc) && (c.getTwo() != cc))
                removeElement = null;
        }
        super.addConnection(conn);
        if (removeElement != null) {
            //there's connection to owner --> we have to remove it
            ((Connection) removeElement).disconnect();
            removeElement = null;
        }
    }

    /**
     * Counts the size of the atribute according to its name and choosen font to display elements of the schema.
     *
     * @return The counted size.
     */
    protected java.awt.Dimension countSize() {
//	String name = (ACTUAL_NOTATION != ConceptualConstruct.UML) ? model.getName() : (model.getName() + ": " + model.getDataType().toDescriptionString());
        String name = model.getName();
        java.awt.FontMetrics fm = null;
        try {
            fm = ((FontManager) manager).getReferentFontMetrics();
            int width = fm.stringWidth(name), height = fm.getAscent();
            return new java.awt.Dimension((int) (1.5 * height) + width, height);
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
        if (getOwner() instanceof Entity) {
            if (((Entity) cc).ISAParent == null) {
                if (model.isPrimary()) {
                    addMenuItem(menu, "Reset Primary", "img/mResetPrimary.gif", this, "setPrimary", new Boolean(false), boolean.class);
                } else {
                    addMenuItem(menu, "Set Primary", "img/mSetPrimary.gif", this, "setPrimary", new Boolean(true), boolean.class);
                }
            }
            if (model.isUnique() && !model.isPrimary()) {
                addMenuItem(menu, "Reset Unique", "img/mResetUnique.gif", getModel(), "setUnique", new Boolean(false), boolean.class);
            } else {
                if (!model.isPrimary())
                    addMenuItem(menu, "Set Unique", "img/mSetUnique.gif", getModel(), "setUnique", new Boolean(true), boolean.class);
            }
        }
        if (!model.isPrimary()) {
            if (model.getArbitrary()) {
                addMenuItem(menu, "Optional", "img/mNotMandatory.gif", getModel(), "setArbitrary", new Boolean(false), boolean.class);
            } else {
                addMenuItem(menu, "Mandatory", "img/mMandatory.gif", getModel(), "setArbitrary", new Boolean(true), boolean.class);
            }
        }
/*PŠif (isInUniqueKey())
		addMenuItem(menu, "Remove from unique key", "mDisconnect.gif", event.getComponent(), "removing", this, cz.green.event.Item.class);
	else
		if (getOwner() instanceof Entity)
		addMenuItem(menu, "Add to unique key", "mUKey.gif", this, "createUnique", this, Atribute.class);
*/
        if (ACTUAL_NOTATION != CHEN) {
//		if (((cz.omnicom.ermodeller.conceptual.Atribute) getModel()).getPosition()>1) 
            {
                addMenuItem(menu, "Move to top", "img/mMoveTop.gif", this, "moveTop");
                addMenuItem(menu, "Move up", "img/mMoveUp.gif", this, "moveUp");
            }
//		if (((cz.omnicom.ermodeller.conceptual.Atribute) getModel()).getPosition() < ((Entity) getOwner()).Attribs.size()) 
            {
                addMenuItem(menu, "Move down", "img/mMoveDown.gif", this, "moveDown");
                addMenuItem(menu, "Move to end", "img/mMoveEnd.gif", this, "moveEnd");
            }
        }
        return menu;
    }

    /**
     * Moves atribute 1 position up
     * <p/>
     * It is used to chnge odrer of atributes in binary and UML notation
     */
    public void moveUp() {
        if (ACTUAL_NOTATION == BINARY && (getOwner() instanceof Entity) &&
                getPosition() == ((Entity) cc).PKmembers.size() + 1)
            return;
        int actualPosition = getPosition();
        Atribute upperAttr = cc.findAttributeWithPosition(actualPosition - 1);
        upperAttr.setPosition(actualPosition);
        this.setPosition(actualPosition - 1);
        if (getOwner() instanceof Entity) ((Entity) cc).recalculatePositionsOfAtributes();
    }

    /**
     * Moves atribute 1 position down
     * <p/>
     * It is used to chnge odrer of atributes in binary and UML notation
     */
    public void moveDown() {
        if (ACTUAL_NOTATION == BINARY && (getOwner() instanceof Entity) &&
                getPosition() == ((Entity) cc).PKmembers.size())
            return;
        int actualPosition = getPosition();
        Atribute upperAttr = cc.findAttributeWithPosition(actualPosition + 1);
        upperAttr.setPosition(actualPosition);
        this.setPosition(actualPosition + 1);
        if (getOwner() instanceof Entity) ((Entity) cc).recalculatePositionsOfAtributes();
    }

    /**
     * Moves atribute to first position
     * <p/>
     * It is used to chnge odrer of atributes in binary and UML notation
     */
    public void moveTop() {
        if (ACTUAL_NOTATION == BINARY && (getOwner() instanceof Entity)) {
            if (getPosition() > ((Entity) cc).PKmembers.size())
                moveToPosition(((Entity) cc).PKmembers.size() + 1);
            else moveToPosition(1);
        } else moveToPosition(1);
    }

    /**
     * Moves atribute to last position in entity
     * <p/>
     * It is used to chnge odrer of atributes in binary and UML notation
     */
    public void moveEnd() {
        if (ACTUAL_NOTATION == BINARY && (getOwner() instanceof Entity)) {
            if (getPosition() <= ((Entity) cc).PKmembers.size())
                moveToPosition(((Entity) cc).PKmembers.size());
            else moveToPosition(cc.Attribs.size());
        } else moveToPosition(cc.Attribs.size());
    }

    /**
     * Moves atribute to specific position
     * <p/>
     * It is used to chnge odrer of atributes in binary and UML notation
     */
    public void moveToPosition(int newPosition) {
        int actualPosition = getPosition();
        if (newPosition == actualPosition) return;
        if (newPosition > actualPosition) {
            for (int i = 0; i < cc.Attribs.size(); i++) {
                Atribute a = (cz.green.ermodeller.Atribute) cc.Attribs.get(i);
                if (a.getPosition() > actualPosition && a.getPosition() <= newPosition)
                    a.setPosition(a.getPosition() - 1);
            }
        } else
            for (int i = 0; i < cc.Attribs.size(); i++) {
                Atribute a = (cz.green.ermodeller.Atribute) cc.Attribs.get(i);
                if (a.getPosition() >= newPosition && a.getPosition() < actualPosition)
                    a.setPosition(a.getPosition() + 1);
            }

        this.setPosition(newPosition);
        if (getOwner() instanceof Entity) ((Entity) cc).recalculatePositionsOfAtributes();
    }

    /**
     * Returns the position of atribute in entity
     */
    public int getPosition() {
        return ((cz.omnicom.ermodeller.conceptual.Atribute) getModel()).getPosition();
    }

    /**
     * Sets the position of atribute in entity
     */
    public void setPosition(int position) {
        ((cz.omnicom.ermodeller.conceptual.Atribute) getModel()).setPosition(position);
    }

    /**
     * Creates new unique key, then adds atribute to them
     *
     * @param atr <code>cz.green.ermodeller.Atribute</code> added atribute
     */
    public void createUnique(Atribute atr) {
        int l = rect[0][0], t = rect[1][0];
        Entity ent = (Entity) atr.getOwner();
        int[][] r = ent.getRect();
        if (l < r[0][0])
            l += 15;
        else
            l -= 15;
        if (t > r[1][0]) t -= 15;
        else t += 15;
        UniqueKey uni = ent.createUniqueKey(l, t);
        uni.addAtribute(atr);
    }


    public void setPrimary(boolean primary) {
        try {
            model.setPrimary(primary);
        } catch (Throwable x) {
            ShowException d = new ShowException(null, "Error", x, true);
        }
/*	for (int i = 0; i< ((Entity) getOwner()).PKmembers.size(); i++) {
		System.out.println(((Entity) getOwner()).PKmembers.get(i));
		System.out.println( ((Atribute) ((Entity) getOwner()).PKmembers.get(i)).PKfirst);
	}*/
    }

    /**
     * Returns the value if the atribute is member of primary key or not
     */
    public boolean isPrimary() {
        return model.isPrimary();
    }

    /**
     * Exists to counts the point, where to starts (ends) connections according to the shape of the atribute. For the
     * atribute it's its center.
     *
     * @see #getCenter()
     */
    public java.awt.Point getBorder(java.awt.Point direction) {
        return getCenter();
    }

    /**
     * Returns the center point to which all connections has to point. For the atribute it's the center of the circle in
     * front of the <b>name</b>.
     */
    public java.awt.Point getCenter() {
        java.awt.Rectangle r;
        if (hRect != null)
            r = getRealBounds();
        else
            r = getBounds();
        int h = r.height / 2;
        return new java.awt.Point(r.x + h, r.y + h);
    }

    /**
     * Returns the model atribute from the Aleš Kopecký work.
     */
    public Object getModel() {
        return model;
    }

    /**
     * Returns the <code>ConceptualConstruct</code> (means entity or relation) the owns this atribute.
     */
    public ConceptualConstruct getOwner() {
        return cc;
    }

    /**
     * Returns the border point (same as <code>getBorder</code>) but according to the position during moving or resizing
     * - it's the real position. Returns the real border.
     *
     * @see #getBorder(java.awt.Point)
     * @see #getRealCenter()
     */
    public java.awt.Point getRealBorder(java.awt.Point direction) {
        return getRealCenter();
    }

    /**
     * The real position of the center the circle in front of the <b>name</b>. Used during moving and resizing.
     */
    public java.awt.Point getRealCenter() {
        java.awt.Rectangle r = getRealBounds();
        int h = r.height / 2;
        return new java.awt.Point(r.x + h, r.y + h);
    }

    /**
     * Overrides this function to say that Atribute can't be resized - returns null.
     */
    public cz.green.event.ResizePoint[] getResizePoints() {
        return null;
    }

    /**
     * Handle event when some other object is dragged over. Can work only with unique key and it means to add this
     * atribute to the unique key or remove it.
     */
    public void handleDragOverEvent(DragOverEvent event) {
        if (selected && event.getAdd())
            return;
        cz.green.event.Item item = event.getItem();
        if (item instanceof UniqueKey) {
            //over me is unique key
            if (event.getAdd()) {
                //to add the atribute
                UniqueKey uk = (UniqueKey) item;
                if ((uk.connectionTo(this) == null) && (uk.getOwner() == getOwner())) {
                    event.getComponent().setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
                    return;
                }
            } else {
                //to remove the atribute
                if (connectionTo(item) != null) {
                    event.getComponent().setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
                    return;
                }
            }
        }
        event.getComponent().setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
    }

    /**
     * Handle event when other object is drop above me and i have to do some action when it's possible. Can work only
     * with unique key - adds or removes the atributes to (from).
     */
    public void handleDropAboveEvent(DropAboveEvent event) {
        if (selected && event.getAdd())
            return;
        cz.green.event.Item item = event.getItem();
        if (item instanceof UniqueKey) {
            //above is unique key
            if (event.getAdd()) {
                //add atribute to the unique key
                UniqueKey uk = (UniqueKey) item;
                if ((uk.connectionTo(this) == null) && (uk.getOwner() == getOwner())) {
                    ((UniqueKey) item).addAtribute(this);
                    event.setDropped(true);
                }
            } else {
                //remove atribute from the unique key
                Connection conn = connectionTo(item);
                if (conn != null) {
                    try {
                        ((cz.omnicom.ermodeller.conceptual.UniqueKey) (((UniqueKey) item).getModel())).removeAtribute(model);
                        conn.disconnect();
                    } catch (Throwable x) {
                        ShowException d = new ShowException(null, "Error", x, true);
                    }
                }
            }
        }
        event.getComponent().setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
    }

    /**
     * Same as <code>handleMovingEvent</code> but remove functionality for BINARY and UML notation.
     */
    public void handleExMovingEvent(ExMovingEvent event) {
        if (ConceptualConstruct.ACTUAL_NOTATION == ConceptualConstruct.CHEN)
            super.handleMovingEvent(event);
    }

    /**
     * Handle remove event - the only one added function is to remove atribute from the model's objects.
     */
    public void handleRemoveEvent(cz.green.event.RemoveEvent event) {
        if (cc instanceof Entity)
            if (isPrimary())
                setPrimary(false);
        moveEnd();
        cc.Attribs.remove(this);
        try {
            cc.removeAtribute(this);
            super.handleRemoveEvent(event);
        } catch (Throwable x) {
            ShowException d = new ShowException(null, "Error", x, true);
        }
//	this.setPosition(cc.Attribs.size());

        if (ConceptualConstruct.ACTUAL_NOTATION != ConceptualConstruct.CHEN)
            if (cc instanceof Entity) {
                Entity ent = (Entity) cc;
                ent.recalculatePositionsOfAtributes();
                if (ent.getISAChilds() != null && ent.getISAChilds().size() > 0)
                    ent.moveChilds(new MoveEvent(ent.getBounds().x, ent.getBounds().y, 0, 0, null));
                ent.getManager().repaintRectangle(ent.getBounds().x, ent.getBounds().y, ent.getBounds().width, ent.getBounds().height);
            }
    }

    /**
     * This method was created by Jiri Mares
     *
     * @return boolean
     */
    protected boolean isInUniqueKey() {
        for (int i = connections.size() - 1; i >= 0; i--) {
            if (((Connection) connections.elementAt(i)).isConnectedTo(UniqueKey.class) != null)
                return true;
        }
        return false;
    }

    /**
     * Paints the atribute. Read <b>name</b> and the <b>arbitrary</b> properties from the model and paints the
     * atribute.
     */
    public void paint(java.awt.Graphics g) {
        java.awt.Rectangle r = getBounds();
        String name = model.getName();
        java.awt.FontMetrics fm = g.getFontMetrics();
        final Stroke stroke = updateStrokeWithAliasing(g);
        // Enable antialiasing for shapes
        if (ACTUAL_NOTATION == CHEN) {
            switch (ACTUAL_LOD) {
                case (LOD_LOW):
                    break;
                case (LOD_MEDIUM):
                    if (model.isPrimary()) {
                        if (selected) {
                            g.setColor(getSelectedBackgroundColor());
                            g.fillRect(r.x, r.y, r.width, r.height);
                        }
                        g.setColor(getForegroundColor());
                        g.fillOval(r.x, r.y, r.height, r.height);
                        if (model.getDataType() instanceof cz.omnicom.ermodeller.datatype.UserDefinedDataType) {
                            if (DataType.isInNestedNames(model.getDataType().toString()) ||
                                    DataType.isInObjectNames(model.getDataType().toString()) ||
                                    DataType.isInVarrayNames(model.getDataType().toString())) {
                                if (model.getArbitrary()) {
                                    g.fillOval(r.x + fm.getAscent() / 2, r.y, r.height, r.height);
                                } else {
                                    g.drawOval(r.x + fm.getAscent() / 2, r.y, r.height, r.height);
                                }
                            }
                        }
                        g.drawString(name, r.x + (int) (1.5 * r.height), r.y + r.height - 2);
                        g.drawLine(r.x + r.height, r.y + r.height, r.x + r.width, r.y + r.height);
                        r = null;
                    }
                    break;
                case (LOD_FULL):
                    if (selected) {
                        g.setColor(getSelectedBackgroundColor());
                        g.fillRect(r.x, r.y, r.width, r.height);
                    }
                    //determime circle drawing
                    g.setColor(getForegroundColor());
                    if (model.getArbitrary()) {
                        g.fillOval(r.x, r.y, r.height, r.height);
                    } else {
                        g.drawOval(r.x, r.y, r.height, r.height);
                    }

                    if (model.getDataType() instanceof cz.omnicom.ermodeller.datatype.UserDefinedDataType) {
                        if (DataType.isInNestedNames(model.getDataType().toString()) ||
                                DataType.isInObjectNames(model.getDataType().toString()) ||
                                DataType.isInVarrayNames(model.getDataType().toString())) {
                            if (model.getArbitrary()) {
                                g.fillOval(r.x + fm.getAscent() / 2, r.y, r.height, r.height);
                            } else {
                                g.drawOval(r.x + fm.getAscent() / 2, r.y, r.height, r.height);
                            }
                        }
                    }

                    if (model.isPrimary())
                        g.drawLine(r.x + r.height, r.y + r.height, r.x + r.width, r.y + r.height);
                    g.drawString(name, r.x + (int) (1.5 * r.height), r.y + r.height - 2);
                    r = null;
            }
        }
        if (ACTUAL_NOTATION == BINARY) {

            //System.out.println("Draw binary");
            switch (ACTUAL_LOD) {
                case (LOD_LOW):
                    break;
                case (LOD_MEDIUM):
                    if (model.isPrimary()) {
                        if (selected) {
                            g.setColor(getSelectedBackgroundColor());
                            g.fillRect(r.x, r.y, r.width, r.height);
                        }
                        g.setColor(getForegroundColor());
                        if (!PKfirst) g.drawString(" ,", r.x, r.y + r.height - 2);
                        g.drawString(name, r.x + (int) (1.5 * r.height), r.y + r.height - 2);
                    }
                    r = null;
                    break;
                case (LOD_FULL):
                    if (selected) {
                        g.setColor(getSelectedBackgroundColor());
                        g.fillRect(r.x, r.y, r.width, r.height);
                    }
                    g.setColor(getForegroundColor());
                    if (model.isPrimary()) {
                        if (!PKfirst) g.drawString(" ,", r.x, r.y + r.height - 2);
                    } else {
                        if (model.isUnique()) {
                            g.drawString(" #", r.x, r.y + r.height - 2);
                        } else if (model.getArbitrary()) {
                            g.drawString(" *", r.x, r.y + r.height);
                        }
                    }
                    g.drawString(name, r.x + (int) (1.5 * r.height), r.y + r.height - 2);
                    r = null;
            }
        }
        if (ACTUAL_NOTATION == UML) {
            String nameUML = model.getName() + ": " + model.getDataType().toDescriptionString();
            switch (ACTUAL_LOD) {
                case (LOD_LOW):
                    break;
                case (LOD_MEDIUM):
                    if (model.isPrimary()) {
                        if (selected) {
                            g.setColor(getSelectedBackgroundColor());
                            g.fillRect(r.x, r.y, r.width, r.height);
                        }
                        if (SHOW_PK_IN_UML == 1) g.drawString("pk", r.x, r.y + r.height - 2);
                        g.drawString(nameUML, r.x + (int) (1.5 * r.height), r.y + r.height - 2);
                    }
                    break;
                case (LOD_FULL):
                    if (selected) {
                        g.setColor(getSelectedBackgroundColor());
                        g.fillRect(r.x, r.y, r.width, r.height);
                    }
                    if (selected) {
                        g.setColor(getSelectedBackgroundColor());
                        g.fillRect(r.x, r.y, r.width, r.height);
                    }
                    //determime circle drawing
                    g.setColor(getForegroundColor());
                    //g.setColor(Color.blue);
                    if (model.isPrimary()) {
                        if (SHOW_PK_IN_UML == 1) g.drawString("pk", r.x, r.y + r.height - 2);
                    }
                    g.drawString(nameUML, r.x + (int) (1.5 * r.height), r.y + r.height - 2);
                    r = null;
            }
        }
        updateBackupStroke(g, stroke);
    }
/**
 * Paints atribute as rectangle -- used during moving.
 */
    /*public void paintFast(java.awt.Graphics g) {
        java.awt.Rectangle r = getRealBounds();
    //	g.drawRect(r.x + r.height / 2, r.y, r.width, r.height);
        g.drawRect(r.x, r.y, r.width, r.height);
        r = null;
    }
    */
/**
 * Prints the atribute on the printer graphics. Same as paint but don't sets the colors.
 */
    public void print(java.awt.Graphics g) {
/*	java.awt.Rectangle r = getBounds();
	//draw circle
	if (model.getArbitrary()) {
		g.fillOval(r.x, r.y, r.height, r.height);
	} else {
		g.drawOval(r.x, r.y, r.height, r.height);
	}
	//show the atribute name
	String name = model.getName();
	g.drawString(name, r.x + (int) (1.5 * r.height), r.y + r.height);
	r = null;
//////////////////////////////////////////////////
*/
        java.awt.Rectangle r = getBounds();
        String name = model.getName();
        java.awt.FontMetrics fm = g.getFontMetrics();

        if (ACTUAL_NOTATION == CHEN) {
            switch (ACTUAL_LOD) {
                case (LOD_LOW):
                    break;
                case (LOD_MEDIUM):
                    if (model.isPrimary()) {
                        g.fillOval(r.x, r.y, r.height, r.height);
                        if (model.getDataType() instanceof cz.omnicom.ermodeller.datatype.UserDefinedDataType) {
                            if (DataType.isInNestedNames(model.getDataType().toString()) ||
                                    DataType.isInObjectNames(model.getDataType().toString()) ||
                                    DataType.isInVarrayNames(model.getDataType().toString())) {
                                if (model.getArbitrary()) {
                                    g.fillOval(r.x + fm.getAscent() / 2, r.y, r.height, r.height);
                                } else {
                                    g.drawOval(r.x + fm.getAscent() / 2, r.y, r.height, r.height);
                                }
                            }
                        }
                        g.drawString(name, r.x + (int) (1.5 * r.height), r.y + r.height - 2);
                        g.drawLine(r.x + r.height, r.y + r.height, r.x + r.width, r.y + r.height);
                        r = null;
                    }
                    break;
                case (LOD_FULL):
                    if (model.getArbitrary()) {
                        g.fillOval(r.x, r.y, r.height, r.height);
                    } else {
                        g.drawOval(r.x, r.y, r.height, r.height);
                    }

                    if (model.getDataType() instanceof cz.omnicom.ermodeller.datatype.UserDefinedDataType) {
                        if (DataType.isInNestedNames(model.getDataType().toString()) ||
                                DataType.isInObjectNames(model.getDataType().toString()) ||
                                DataType.isInVarrayNames(model.getDataType().toString())) {
                            if (model.getArbitrary()) {
                                g.fillOval(r.x + fm.getAscent() / 2, r.y, r.height, r.height);
                            } else {
                                g.drawOval(r.x + fm.getAscent() / 2, r.y, r.height, r.height);
                            }
                        }
                    }

                    if (model.isPrimary())
                        g.drawLine(r.x + r.height, r.y + r.height, r.x + r.width, r.y + r.height);
                    g.drawString(name, r.x + (int) (1.5 * r.height), r.y + r.height - 2);
                    r = null;
            }
        }
        if (ACTUAL_NOTATION == BINARY) {
            switch (ACTUAL_LOD) {
                case (LOD_LOW):
                    break;
                case (LOD_MEDIUM):
                    if (model.isPrimary()) {
                        if (!PKfirst) g.drawString(" ,", r.x, r.y + r.height - 2);
                        g.drawString(name, r.x + (int) (1.5 * r.height), r.y + r.height - 2);
                    }
                    r = null;
                    break;
                case (LOD_FULL):
                    if (model.isPrimary()) {
                        if (!PKfirst) g.drawString(" ,", r.x, r.y + r.height - 2);
                    } else {
                        if (model.isUnique()) {
                            g.drawString(" #", r.x, r.y + r.height - 2);
                        } else if (model.getArbitrary()) {
                            g.drawString(" *", r.x, r.y + r.height);
                        }
                    }
                    g.drawString(name, r.x + (int) (1.5 * r.height), r.y + r.height - 2);
                    r = null;
            }
        }
        if (ACTUAL_NOTATION == UML) {
            String nameUML = model.getName() + ": " + model.getDataType().toDescriptionString();
            switch (ACTUAL_LOD) {
                case (LOD_LOW):
                    break;
                case (LOD_MEDIUM):
                    if (model.isPrimary()) {
                        g.drawString("pk", r.x, r.y + r.height - 2);
                        g.drawString(nameUML, r.x + (int) (1.5 * r.height), r.y + r.height - 2);
                    }
                    break;
                case (LOD_FULL):
                    g.setColor(getForegroundColor());
                    if (model.isPrimary()) {
                        g.drawString("pk", r.x, r.y + r.height - 2);
                    }
                    g.drawString(nameUML, r.x + (int) (1.5 * r.height), r.y + r.height - 2);
                    r = null;
            }
        }
    }


    /**
     * Invoked when some model's property change. Catch changes of the <b>name</b> and <b>arbitrary</b> property.
     */
    public void propertyChange(java.beans.PropertyChangeEvent e) {
        ((cz.omnicom.ermodeller.conceptual.ConceptualObject) getModel()).setChanged(true);
        if (e.getPropertyName().equals("dataType")) {
            if (getOwner() instanceof Entity) {
                cz.green.event.ResizeRectangle rr = new cz.green.event.ResizeRectangle(
                        0, 0, 0, 0, cz.green.event.ResizePoint.BOTTOM
                        | cz.green.event.ResizePoint.RIGHT);
                ((Entity) getOwner()).resizeEntity(new ResizeEvent(0, 0, 0, 0, rr, null));
                ((cz.green.event.PaintableManager) manager).repaintItem(this);
            }
        }
        if (e.getPropertyName().equals("primary")) {
            if (isPrimary()) {
                if (!((Entity) getOwner()).PKmembers.contains(this))
                    ((Entity) getOwner()).PKmembers.addElement(this);
                if (ACTUAL_NOTATION == BINARY)
                    moveToPosition(((Entity) getOwner()).PKmembers.size());
            } else {
                ((Entity) getOwner()).PKmembers.removeElement(this);
                if (ACTUAL_NOTATION == BINARY)
                    moveToPosition(((Entity) getOwner()).PKmembers.size() + 1);
            }
            if (ConceptualConstruct.ACTUAL_NOTATION != ConceptualConstruct.CHEN)
                ((Entity) getOwner()).recalculatePositionsOfAtributes();

        }
        if (e.getPropertyName().equals("name")) {
            //the name changed --> counts new size and resize the atribute
            int[][] r = rect;
            java.awt.Dimension dim = countSize(), real = new java.awt.Dimension(r[0][1] - r[0][0], r[1][1] - r[1][0]);
            int dx = dim.width - real.width;
            int dy = dim.height - real.height;
            java.awt.Rectangle b = getBounds();
            try {
                if (dx != 0)
                    resize(dx, 0, cz.green.event.ResizePoint.RIGHT, true);
                if (dy != 0)
                    resize(0, dy, cz.green.event.ResizePoint.BOTTOM, true);
            } catch (cz.green.event.ItemNotInsideManagerException ex) {
            }
            b = b.union(getBounds());
            ((cz.green.event.PaintableManager) manager).repaintRectangle(b.x, b.y, b.width, b.height);
            if (ACTUAL_NOTATION == UML || ACTUAL_NOTATION == BINARY) {
                if (getOwner() instanceof Entity) {
                    cz.green.event.ResizeRectangle rr = new cz.green.event.ResizeRectangle(
                            0, 0, 0, 0, cz.green.event.ResizePoint.BOTTOM
                            | cz.green.event.ResizePoint.RIGHT);
                    ((Entity) getOwner()).resizeEntity(new ResizeEvent(0, 0, 0, 0, rr, null));
                }
            }
        } else {
            //jinak ho prekresli
            java.awt.Rectangle b = getBounds();
            ((cz.green.event.PaintableManager) manager).repaintItem(this);
        }
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
     * Call inherited handleRemoveEvent method.
     */
    public void removeAtribute(cz.green.event.RemoveEvent ev) {
/*	if (cc instanceof Entity)
		if(isPrimary())
			setPrimary(false);
	moveEnd();
	cc.Attribs.remove(this);
*/
        startConnFastRepaint();
        super.handleRemoveEvent(ev);
    }

    /**
     * This method has the opposed functionaliti to the <code>addConnection</code> method. It means that when removing
     * last connection and this connection is to the unique key, then we have to add one connection to the owner.
     *
     * @see addConnection(cz.green.ermodeller.Connection)
     */
    public void removeConnection(Connection conn) {
        boolean add = false;
        if (connections.size() == 1) {
            //there is only connection to relation, entity or unique key
            Connection c = (Connection) (connections.elementAt(0));
            if ((c.getOne() != cc) && (c.getTwo() != cc)) {
                //the last connection is not to the owner -- adds such connection
                add = true;
            }
        }
        super.removeConnection(conn);
        if (add) {
            //there are no connections -- add connection to the owner
            try {
                Connection c = new ConnectionLine(manager, this, cc);
                ((ConnectionManager) manager).addConnection(c);
                ((cz.green.event.PaintableManager) manager).repaintItem(c);
            } catch (cz.green.event.ImpossibleNegativeValueException e) {
            }
        }
    }

    /**
     * Set new owner. Is useful for moving atributed among owners.
     *
     * @param cc The new owner of the atribute - entity or relation.
     */
    public void setOwner(ConceptualConstruct cc) {
        this.cc = cc;
    }

    /**
     * Writes data for atribute into XML file
     *
     * @param pw java.io.PrintWriter
     */
    public void write(java.io.PrintWriter pw) {
        pw.println("\t<atribute>");
        super.write(pw);
        pw.println("\t</atribute>");
    }
}
