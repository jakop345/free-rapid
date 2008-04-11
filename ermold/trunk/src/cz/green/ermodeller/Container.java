package cz.green.ermodeller;

import cz.green.ermodeller.interfaces.FontManager;
import cz.green.ermodeller.interfaces.ISchema;
import cz.green.ermodeller.interfaces.ModeSwitcher;
import cz.green.ermodeller.interfaces.ModelFinder;
import cz.green.event.AddAsISAChildEvent;
import cz.green.event.AddConnectionEvent;
import cz.green.event.AddIdentificationDependencyEvent;
import cz.green.event.AddRelWithConnsEvent;
import cz.green.event.exceptions.ImpossibleNegativeValueException;
import cz.green.event.interfaces.ContainerDesktop;
import cz.green.event.interfaces.Item;
import cz.green.eventtool.ContainerTool;
import cz.green.eventtool.Window;
import cz.omnicom.ermodeller.errorlog.ShowErrorEvent;
import cz.omnicom.ermodeller.errorlog.interfaces.ShowErrorListener;

import javax.swing.*;
import java.awt.event.KeyEvent;

/**
 * This class has the same functionality as its predecessor. Adds many new work regimes, help functionality and font
 * management.
 */
public class Container extends ContainerTool implements ModeSwitcher, FontManager, ShowErrorListener {
    /**
     * The adding entity state.
     */
    public final static int ADDING_ENTITY = 6;
    /**
     * The adding group state.
     */
    public final static int ADDING_RELATION = 7;
    /**
     * The adding group state.
     */
    public final static int ADDING_RELATION_AND_CONNECTION = 15;
    /** The adding group state. */
    //	public final static int ADDING_RELATION_AND_CONNECTION_FROM_TOOLBAR = 16;
    /**
     * The adding group state.
     */
    public final static int ADDING_CONNECTION = 17;
    /**
     * The adding group state.
     */
    public final static int ADDING_AS_ISA_CHILD = 18;
    /**
     * The adding group state.
     */
    public final static int ADDING_IDENT_DEPENDENCY = 19;
    /**
     * The adding atribute state.
     */
    public final static int ADDING_ATRIBUTE = 8;
    /**
     * The adding atribute state.
     */
    public final static int COMPOSING_ENTITY = 13;
    /**
     * The adding atribute state.
     */
    public final static int COMPOSING_RELATION = 14;
    /**
     * The adding unique key state.
     */
    public final static int ADDING_UNIQUE_KEY = 9;
    /**
     * The adding cardinality state.
     */
    public final static int ADDING_CARDINALITY = 10;
    /**
     * The adding strong addiction state.
     */
    public final static int ADDING_STRONGADDICTION = 11;
    /**
     * The removing state.
     */
    public final static int REMOVING = 12;
    /**
     * Helpful for some work modes
     */
    protected Object object = null;
    /**
     * The shown popup menu
     */
    protected JPopupMenu menu = null;
    /**
     * Holds the font metrics the current font
     */
    private java.awt.FontMetrics fm = null;
    /**
     * Current font used to paint schema
     */
    private java.awt.Font f = null;
    /**
     * Need to implement property workMode
     */
    private transient java.beans.PropertyChangeSupport pcs = null;

    /**
     * Editor of Cosntraints
     */
    ConstraintsDialog constDialog;

    /**
     * Calls the inherited constructor, but has one more parameter.
     *
     * @param helpURL Specify the page which is open as help.
     * @see cz.green.eventtool.ContainerTool#Container(int,int,int,int)
     */
    public Container(int width, int height) {
        super(width, height);
        setFont(new java.awt.Font("sansserif", java.awt.Font.PLAIN, 12));
        menu = new JPopupMenu("MainMenu");
        pcs = new java.beans.PropertyChangeSupport(this);
        //setHelpURL(helpURL);
    }

    /**
     * Adds new desktop
     */
    public void addDesktop(Desktop d) {
        desktop = d;
    }

    /**
     * Set regime for adding atribute.
     *
     * @param object The owner of the new atribute.
     */
    public boolean addingAtribute(ConceptualConstruct object) {
        this.object = object;
        if (Window.ACTUAL_NOTATION == Window.CHEN) {
            setCursor(new java.awt.Cursor(java.awt.Cursor.CROSSHAIR_CURSOR));
            setWorkMode(ADDING_ATRIBUTE);
            return true;
        }
        if (Window.ACTUAL_NOTATION == Window.BINARY || Window.ACTUAL_NOTATION == Window.UML) {
            (object).createAtribute(object.getBounds().x, object.getBounds().y);
/*		int x=object.getBounds().x+5, y=object.getBounds().y+5;
		cz.green.event.ResizeRectangle rr = new cz.green.event.ResizeRectangle(
				0, 0, 0, 0, cz.green.event.ResizePoint.BOTTOM
						| cz.green.event.ResizePoint.RIGHT);
		((Entity) object).resizeEntity(new ResizeEvent(x, y, 0, 0, rr, this));
*/
            return true;
        }
        return false;
    }

    /**
     * Set regime for adding cardinality.
     *
     * @param object The entity and the realtion for the new cradinality.
     */
    public boolean addingCardinality(CardinalityPair object) {
        this.object = object;
        setCursor(new java.awt.Cursor(java.awt.Cursor.CROSSHAIR_CURSOR));
        setWorkMode(ADDING_CARDINALITY);
        return true;
    }

    /**
     * Set regime for adding entity.
     *
     * @param object When this adding is used for decomposition the entity, it is the former entity, otherwise it's
     *               <code>null</code>.
     */
    public boolean addingEntity(EntityConstruct ent) {
        object = ent;
        setCursor(new java.awt.Cursor(java.awt.Cursor.CROSSHAIR_CURSOR));
        setWorkMode(ADDING_ENTITY);
        return true;
    }

    /**
     * Set regime for adding group.
     */
    public boolean addingGroup() {
        setCursor(new java.awt.Cursor(java.awt.Cursor.CROSSHAIR_CURSOR));
        setWorkMode(ADDING_GROUP);
        return true;
    }

    /**
     * Set regime for adding relation.
     */
    public boolean addingRelation() {
        setCursor(new java.awt.Cursor(java.awt.Cursor.CROSSHAIR_CURSOR));
        setWorkMode(ADDING_RELATION);
        return true;
    }
/**
 * Set regime for adding relation from toolbar.
 */
    /*public boolean addingRelationToolbar() {
        setCursor(new java.awt.Cursor(java.awt.Cursor.CROSSHAIR_CURSOR));
        setWorkMode(ADDING_RELATION_AND_CONNECTION_FROM_TOOLBAR);
        return true;
    }
    */
/**
 * Set regime for adding relation with connection to 2 entities.
 */
    public boolean addingRelationCon(EntityConstruct object) {
        this.object = object;
        setCursor(new java.awt.Cursor(java.awt.Cursor.CROSSHAIR_CURSOR));
        setWorkMode(ADDING_RELATION_AND_CONNECTION);
        return true;
    }

    /**
     * Set regime for adding connection.
     */
    public boolean addingConnectionToRel(EntityConstruct object) {
        this.object = object;
        setCursor(new java.awt.Cursor(java.awt.Cursor.CROSSHAIR_CURSOR));
        setWorkMode(ADDING_CONNECTION);
        return true;
    }

    /**
     * Set regime for adding connection.
     */
    public boolean addingIdentDependency(EntityConstruct object) {
        this.object = object;
        setCursor(new java.awt.Cursor(java.awt.Cursor.CROSSHAIR_CURSOR));
        setWorkMode(ADDING_IDENT_DEPENDENCY);
        return true;
    }

    /**
     * Set regime for adding as ISA Child.
     */
    public boolean addingAsISAChild(EntityConstruct object) {
        this.object = object;
        setCursor(new java.awt.Cursor(java.awt.Cursor.CROSSHAIR_CURSOR));
        setWorkMode(ADDING_AS_ISA_CHILD);
        return true;
    }

    /**
     * Set regime for adding connection.
     */
    public boolean addingConnectionToEnt(Relation object) {
        this.object = object;
        setCursor(new java.awt.Cursor(java.awt.Cursor.CROSSHAIR_CURSOR));
        setWorkMode(ADDING_CONNECTION);
        return true;
    }

    /**
     * Set regime for adding cardinality.
     *
     * @param object The entity and the realtion for the new cradinality.
     */
    public boolean addingStrongAddiction(StrongAddictionPair object) {
        this.object = object;
        setCursor(new java.awt.Cursor(java.awt.Cursor.CROSSHAIR_CURSOR));
        setWorkMode(ADDING_STRONGADDICTION);
        return true;
    }

    /**
     * Set regime for adding unique key.
     *
     * @param object The owner of the new unique key.
     */
    public boolean addingUniqueKey(ConceptualConstruct object) {
        this.object = object;
        setCursor(new java.awt.Cursor(java.awt.Cursor.CROSSHAIR_CURSOR));
        setWorkMode(ADDING_UNIQUE_KEY);
        return true;
    }

    /**
     * Set regime for adding window.
     *
     * @param object The owner of the new window.
     */
    public boolean addingWindow() {
        setCursor(new java.awt.Cursor(java.awt.Cursor.CROSSHAIR_CURSOR));
        setWorkMode(ADDING_WINDOW);
        return true;
    }

    /**
     * Adds PropertyChangeListener that catch the changes of the workMode property.
     */
    public void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    /**
     * Removes old desktop
     */
    public void clearDesktop() {
        desktop = null;
    }

    /**
     * Set regime for deleting elements.
     */
    public boolean deleting() {
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setWorkMode(DELETING);
        return true;
    }

    /**
     * Returns new instance of the desktop.
     *
     * @return The desktop.
     */
    public ContainerDesktop getDesktop() {
        if (desktop == null) {
            try {
                java.awt.Rectangle r = getBounds();
                desktop = new Desktop(this, r.x, r.y, r.width, r.height);
                ((Desktop) desktop).addShowErrorListener(this);
//			propEditing(true);
            } catch (ImpossibleNegativeValueException e) {
                return null;
            }
        }
        return desktop;
    }

    /**
     * Returns referent font.
     */
    public java.awt.Font getReferentFont() {
        if (f == null) {
            f = getGraphics().getFont();
            fm = getGraphics().getFontMetrics();
        }
        return f;
    }

    /**
     * Returns the referent font metrics.
     */
    public java.awt.FontMetrics getReferentFontMetrics() {
        if (fm == null) {
            fm = getGraphics().getFontMetrics();
            f = getGraphics().getFont();
        }
        return fm;
    }

    /**
     * Display helpURL in browser using cz.green.uti.BrowserControl.
     *
     * @see cz.green.uti.BrowserControl
     */
    public void help() {
        //zkusil bych, co vrátí tohle:    
        cz.green.util.BrowserControl.displayURL(AppPrefs.getAppPath() + Consts.DEF_HELPPATH);
    }

    /**
     * Simply loads schema from a file. For loading uses Java Serialization. After loading initialize loaded desktop by
     * method <code>init</code>.
     *
     * @param fileName File from which to load schema.
     * @throws java.io.IOException If file doesn't exist or occur other file error.
     * @throws java.lang.ClassNotFoundException
     *                             If file contains unknow class. For example doesn't contains schema, but other
     *                             classes.
     * @see cz.green.event.Desktop#init(cz.green.event.Container)
     */
    public void loadFromFile(String fileName) throws java.io.IOException, ClassNotFoundException {
        super.loadFromFile(fileName);
        try {
            ((Desktop) getDesktop()).addShowErrorListener(this);
//		propEditing(true);
        } catch (ClassCastException x) {
        }
    }

    /**
     * Call dialog to edit Constraints.
     */
    public void editConstraints(EntityConstruct ent) {
        if (constDialog == null)
            constDialog = new ConstraintsDialog(((Desktop) getDesktop()).ERMFrame, (cz.omnicom.ermodeller.conceptual.Entity) ent.getModel());
        constDialog.setLocationRelativeTo(((Desktop) getDesktop()).ERMFrame);
        if (ent.getModel() != null) constDialog.setVisible(true);
    }

    /**
     * Override inherited method to send new events and support new work regime.
     */
    public void mouseDragged(java.awt.event.MouseEvent e) {
        requestFocus();
        float scale = desktop.getScale();
        int x = (int) (e.getX() * scale), y = (int) (e.getY() * scale);
        switch (workMode) {
            case WORKING:
                setWorkMode(MOVING);
            case MOVING:
//			move = ((e.getModifiers() & java.awt.event.MouseEvent.CTRL_MASK) == 0);
                desktop.fallAndHandleEvent(cooX, cooX, new ExMovingEvent(cooX, cooY, x - cooX, y - cooY, this, SwingUtilities.isLeftMouseButton(e)));
                cooX = x;
                cooY = y;
                break;
            default:
                super.mouseDragged(e);
        }
    }

    /**
     * Override inherited method to send new events and support new work regime.
     */
    public void mouseMoved(java.awt.event.MouseEvent e) {
        switch (workMode) {
            case WORKING:
            case DELETING:
                super.mouseMoved(e);
                break;
            case REMOVING:
                float scale = desktop.getScale();
                int x = (int) (e.getX() * scale);
                int y = (int) (e.getY() * scale);
                desktop.fallAndHandleEvent(x, y, new DragOverEvent(x, y, (Item) object, this, false));
                break;
        }
    }

    /**
     * Override inherited method to send new events and support new work regime.
     */
    public void mousePressed(java.awt.event.MouseEvent e) {
        requestFocus();
        if (e.isPopupTrigger()) {
            float scale = desktop.getScale();
            popupAction((int) (e.getX() * scale), (int) (e.getY() * scale), e.getX(), e.getY());
        } else
            super.mousePressed(e);
    }

    /*
    public void mouseDoubleClicked(java.awt.event.MouseEvent e) {
        System.out.println("Double click");
        propEditing(true);
    }

    public void mouseClicked(java.awt.event.MouseEvent e) {
        if (e.getClickCount() ==2) mouseDoubleClicked(e);
        //System.out.println("Double click");
    }
    */
/**
 * Override inherited method to send new events and support new work regime.
 */
    public void mouseReleased(java.awt.event.MouseEvent e) {
        requestFocus();
        int x, y, pom;
        float scale = desktop.getScale();
        x = (int) (e.getX() * scale);
        y = (int) (e.getY() * scale);
        if (e.isPopupTrigger() && (workMode != MOVING) && (workMode != RESIZING)) {
            popupAction(x, y, e.getX(), e.getY());
        } else {
            switch (workMode) {
                case COMPOSING_ENTITY:
                    setWorkMode(WORKING);
                    setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
                    desktop.fallAndHandleEvent(x, y, new DropAboveEvent(x, y, (Item) object, this, false));
//				((ConceptualConstruct) object).createAtribute(x, y);
                    break;
                case COMPOSING_RELATION:
                    setWorkMode(WORKING);
                    setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
//				((ConceptualConstruct) object).createAtribute(x, y);
                    break;
                case ADDING_ATRIBUTE:
                    setWorkMode(WORKING);
                    setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
                    ((ConceptualConstruct) object).createAtribute(x, y);
                    break;
                case ADDING_UNIQUE_KEY:
                    setWorkMode(WORKING);
                    setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
                    ((EntityConstruct) object).createUniqueKey(x, y);
                    break;
                case ADDING_CARDINALITY:
                    setWorkMode(WORKING);
                    setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
                    ((CardinalityPair) object).create(getDesktop(), x, y);
                    break;
                case ADDING_STRONGADDICTION:
                    setWorkMode(WORKING);
                    setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
                    ((StrongAddictionPair) object).create(getDesktop(), x, y);
                    break;
                case REMOVING:
                    setWorkMode(WORKING);
                    setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
                    desktop.fallAndHandleEvent(x, y, new DropAboveEvent(x, y, (Item) object, this, false));
                    break;
                case ADDING_RELATION:
                    setWorkMode(WORKING);
                    setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
                    ((ISchema) desktop).createRelation(x, y);
                    break;
/*			case ADDING_RELATION_AND_CONNECTION_FROM_TOOLBAR :
				setWorkMode(ADDING_RELATION_AND_CONNECTION);
				setCursor(new java.awt.Cursor(java.awt.Cursor.CROSSHAIR_CURSOR));
				((Schema) desktop).createRelation(x, y);
				desktop.fallAndHandleEvent(cooX, cooY, new SelectItemEvent(cooX, cooY, e.isControlDown(), this));
				break;
*/
                case ADDING_RELATION_AND_CONNECTION:
                    setWorkMode(WORKING);
                    setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
                    desktop.fallAndHandleEvent(x, y, new AddRelWithConnsEvent(x, y, (Item) object, this, false));
                    break;
                case ADDING_CONNECTION:
                    setWorkMode(WORKING);
                    setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
                    desktop.fallAndHandleEvent(x, y, new AddConnectionEvent(x, y, (Item) object, this, false));
                    break;
                case ADDING_IDENT_DEPENDENCY:
                    setWorkMode(WORKING);
                    setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
                    desktop.fallAndHandleEvent(x, y, new AddIdentificationDependencyEvent(x, y, (Item) object, this, false));
                    break;
                case ADDING_AS_ISA_CHILD:
                    setWorkMode(WORKING);
                    setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
                    desktop.fallAndHandleEvent(x, y, new AddAsISAChildEvent(x, y, (Item) object, this, false));
                    break;
                case ADDING_ENTITY:
                    setWorkMode(WORKING);
                    setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
                    ((ISchema) desktop).createEntity(x, y, (EntityConstruct) object);
                    break;
                case MOVING:
                    setWorkMode(WORKING);
                    //				move = ((e.getModifiers() & java.awt.event.MouseEvent.CTRL_MASK) == 0);
                    desktop.fallAndHandleEvent(cooX, cooY, new ExMoveEvent(x, y, x - cooX, y - cooY, this, SwingUtilities.isLeftMouseButton(e)));
                    break;
                default:
                    super.mouseReleased(e);
            }
        }
    }

    /* (non-Javadoc)
    * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
    */
    public void keyPressed(KeyEvent e) {
        super.keyPressed(e);
    }

    /* (non-Javadoc)
    * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
    */
    public void keyReleased(KeyEvent e) {
        // TODO Auto-generated method stub
    }

    /**
     * Invoken when press mouse button to show context menu - fall PopupMenuEvent.
     *
     * @param x     Event x coordinate (nature schema coordinates)
     * @param y     Event y coordinate (nature schema coordinates)
     * @param realX Real container x coordinate
     * @param realY Real container y coordinate
     * @see PopupMenuEvent
     */
    protected void popupAction(int x, int y, int realX, int realY) {
        desktop.fallAndHandleEvent(x, y, new PopupMenuEvent(x, y, realX, realY, this, menu));
    }

    /**
     * This method was created by Jiri Mares
     */
    public void propEditing(boolean show) {
        try {
            ((Desktop) getDesktop()).propEditing(show);
        } catch (ClassCastException x) {
        }
    }

    /**
     * Removess PropertyChangeListener that catched the changes of the workMode property.
     */
    public void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

    /**
     * Set regime for removing connections.
     *
     * @param item This item is sent as item with DragOverEvent and DropAboveEvent.
     */
    public boolean removing(Item item) {
        this.object = item;
        setWorkMode(REMOVING);
        return true;
    }


    /**
     * Set the new scale. Also set new font sizes according to the scale.
     */
    public float setScale(float scale) {
        java.awt.Font f = getReferentFont();
        java.awt.Font newF = new java.awt.Font(f.getName(), f.getStyle(), (int) (f.getSize() / scale));
        setFont(newF);
        return super.setScale(scale);
    }

    /**
     * Fires propertyChangeEvent and call inherited <code>setWorkMode</code>.
     */
    protected void setWorkMode(int newV) {
        int old = workMode;
        workMode = newV;
        pcs.firePropertyChange("workMode", new Integer(old), new Integer(workMode));
    }

    /**
     * This method was created by Jiri Mares
     *
     * @param event cz.omnicom.ermodeller.errorlog.ShowErrorEvent
     */
    public void showError(ShowErrorEvent event) {
        try {
            getDesktop().selectItemEx(null, false);
            ((ModelFinder) getDesktop()).isModelIn(event.getConceptualObjects(), new cz.green.event.SelectItemExEvent(0, 0, true, this));
            getDesktop().fitSelected();
        } catch (ClassCastException x) {
        }
    }

    /**
     * Set normal working regime.
     */
    public boolean working() {
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setWorkMode(WORKING);
        return true;
    }
}
