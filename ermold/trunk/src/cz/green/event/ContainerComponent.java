package cz.green.event;

import cz.green.event.interfaces.ContainerDesktop;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * This is panel, that holds object
 * <code>cz.green.Desktop</code>. We implements method
 * <code>paint</code> for drawing the desktop, methods for handling mouse events and changing the state.
 *
 * @see DesktopGroupWindow
 */
public class ContainerComponent extends JComponent implements MouseListener, MouseMotionListener, KeyListener {
    /**
     * The working state.
     */
    public final static int WORKING = 0;
    /**
     * The deleting state.
     */
    public final static int DELETING = 1;
    /**
     * The resizing state.
     */
    public final static int RESIZING = 2;
    /**
     * The moving state.
     */
    public final static int MOVING = 3;
    /**
     * The adding item state.
     */
    public final static int ADDING_WINDOW = 4;
    /**
     * The adding group state.
     */
    public final static int ADDING_GROUP = 5;


    /**
     * The inserted desktop.
     *
     * @see cz.green.event.interfaces.Manager
     */
    protected ContainerDesktop desktop = null;
    /**
     * Auxiliary x coordinate
     */
    protected int cooX = 0;
    /**
     * Auxiliary y coordinate
     */
    protected int cooY = 0;
    /**
     * The state of the Container
     */
    protected int workMode = WORKING;
    /**
     * The actual resize rectangle
     */
    protected ResizeRectangle actResizeRect = null;

    /**
     * Default constructor. Creates object the specified size.
     */
    public ContainerComponent(int width, int height) {
        super();
        Dimension dim = new Dimension(width, height);
        setSize(dim);
        setPreferredSize(dim);
//  	setBounds(0, 0, width, height);
        desktop = getDesktop();
        setLayout(null);
        addMouseListener(this);
        addMouseMotionListener(this);
        addKeyListener(this);

    }

    /**
     * Changes the state to adding group.
     */
    public boolean addingGroup() {
        if ((workMode == WORKING) || (workMode == DELETING) || (workMode == ADDING_WINDOW)) {
            setWorkMode(ADDING_GROUP);
            return true;
        }
        return false;
    }

    /**
     * Changes the state to adding window.
     */
    public boolean addingWindow() {
        if ((workMode == WORKING) || (workMode == DELETING) || (workMode == ADDING_GROUP)) {
            setWorkMode(ADDING_WINDOW);
            return true;
        }
        return false;
    }

    /**
     * Changes the state to deleting.
     */
    public boolean deleting() {
        if ((workMode == ADDING_WINDOW) || (workMode == WORKING) || (workMode == ADDING_GROUP)) {
            setWorkMode(DELETING);
            return true;
        }
        return false;
    }

    /**
     * This method sets the desktop scale and position to be able to see all selected items.
     *
     * @see ContainerDesktop#fitSelected()
     */
    public void fitSelected() {
        desktop.fitSelected();
        Rectangle r = desktop.getBounds();
        setSize(r.getSize());
    }

    /**
     * This method creates desktop. Is needed for creating desktop instances. When we need other instance than
     * <code>cz.green.event.Desktop</code>.
     *
     * @return cz.green.event.ContainerDesktop
     */
    public ContainerDesktop getDesktop() {
        Rectangle r = getBounds();
        desktop = new DesktopGroupWindow(this, r.x, r.y, r.width, r.height);

        return desktop;
    }

    public Graphics getGraphics() {
        Component c = getParent();
        if (c instanceof JViewport) {
            Rectangle r = ((JViewport) c).getViewRect();
            Graphics g = super.getGraphics();
            g.setClip(r.x, r.y, r.width, r.height);
            return g;
        } else
            return super.getGraphics();
    }

    /**
     * This method tests if the desktop is in resize mode.
     *
     * @param <code>>x</code> The x coordinate the mouse position.
     * @param <code>>y</code> The y coordinate the mouse position.
     * @return <code>true</code> (<code>false</code>) if the component
     *         is (not) in resize mode.
     * @see DesktopGroupWindow#getActualResizeRect(int, int)
     */
    public boolean isResizeMode(int x, int y) {
        return ((actResizeRect = desktop.getActualResizeRect(x, y)) != null);
    }

    /**
     * Method to handle events for the MouseListener interface. Do nothing.
     *
     * @param e The event.
     */
    public void mouseClicked(java.awt.event.MouseEvent e) {
    }

    /**
     * Method to handle events for the MouseListener interface.
     *
     * @param e The event.
     */
    public void mouseDragged(MouseEvent e) {
        float scale = desktop.getScale();
        int x = (int) (e.getX() * scale), y = (int) (e.getY() * scale);
        switch (workMode) {
            case WORKING:
                setWorkMode(MOVING);
            case MOVING:
                desktop.fallAndHandleEvent(cooX, cooX, new MovingEvent(cooX, cooY, x - cooX, y - cooY, this));
                cooX = x;
                cooY = y;
                break;
            case RESIZING:
                desktop.fallAndHandleEvent(cooX, cooY, new ResizingEvent(cooX, cooY, x - cooX, y - cooY, actResizeRect, this));
                cooX = x;
                cooY = y;
                break;
        }
    }

    /**
     * Method to handle events for the MouseListener interface. Do nothing.
     *
     * @param e The event.
     */
    public void mouseEntered(java.awt.event.MouseEvent e) {
    }

    /**
     * Method to handle events for the MouseListener interface. Do nothing.
     *
     * @param e The event.
     */
    public void mouseExited(java.awt.event.MouseEvent e) {
    }

    /**
     * Method to handle events for the MouseListener interface.
     *
     * @param e The event.
     */
    public void mouseMoved(MouseEvent e) {
        float scale = desktop.getScale();
        int x = e.getX(), y = e.getY();
        desktop.fallAndHandleEvent((int) (x * scale), (int) (y * scale), new MouseMoveEvent(x, y, this));
    }

    /**
     * Method to handle events for the MouseListener interface.
     *
     * @param e The event.
     */
    public void mousePressed(MouseEvent e) {
        float scale = desktop.getScale();
        cooX = (int) (e.getX() * scale);
        cooY = (int) (e.getY() * scale);
        switch (workMode) {
            case WORKING:
                if (isResizeMode(e.getX(), e.getY()))
                    setWorkMode(RESIZING);
                else {
                    desktop.fallAndHandleEvent(cooX, cooY, new SelectItemEvent(cooX, cooY, e.isControlDown(), this));
                }
                break;
            case DELETING:
                desktop.fallAndHandleEvent(cooX, cooY, new RemoveEvent(cooX, cooY, this));
                break;
        }
    }

    /**
     * Method to handle events for the MouseListener interface.
     *
     * @param e The event.
     */
    public void mouseReleased(MouseEvent e) {
        int x, y, pom;
        float scale = desktop.getScale();
        x = (int) (e.getX() * scale);
        y = (int) (e.getY() * scale);
        switch (workMode) {
            case ADDING_WINDOW:
            case ADDING_GROUP:
                if (cooX > x) {
                    pom = x;
                    x = cooX;
                    cooX = pom;
                }
                if (cooY > y) {
                    pom = y;
                    y = cooY;
                    cooY = pom;
                }
                try {
                    if (workMode == ADDING_GROUP)
                        desktop.fallAndHandleEvent(cooX, cooY, new AddItemEvent(cooX, cooY, new GroupWindowItem(desktop, cooX, cooY, x - cooX, y - cooY), this));
                    else
                        desktop.fallAndHandleEvent(cooX, cooY, new AddItemEvent(cooX, cooY, new WindowItem(desktop, cooX, cooY, x - cooX, y - cooY), this));
                } catch (Exception ex) {
                    return;
                }
                break;
            case WORKING:
                desktop.fallAndHandleEvent(cooX, cooY, new SelectItemExEvent(cooX, cooX, e.isControlDown(), this));
                break;
            case MOVING:
                setWorkMode(WORKING);
                desktop.fallAndHandleEvent(cooX, cooY, new MoveEvent(x, y, x - cooX, y - cooY, this));
                break;
            case RESIZING:
                setWorkMode(WORKING);
                desktop.fallAndHandleEvent(cooX, cooY, new ResizeEvent(x, y, x - cooX, y - cooY, actResizeRect, this));
                actResizeRect = null;
                break;
        }
    }

    /**
     * Paints the panel and the desktop.
     *
     * @see java.awt.Component#paint(java.awt.Graphics)
     */
    public void paint(java.awt.Graphics g) {
        super.paint(g);
        if (desktop != null)
            desktop.paint(g);
    }

    /**
     * Set the new scale.
     */
    public float setScale(float scale) {
        desktop.setScale(scale);
        Rectangle r = desktop.getBounds();
        setSize(r.getSize());
        setPreferredSize(r.getSize());
        return desktop.getScale();
    }

    /**
     * This method was created by Jiri Mares
     *
     * @param newV int
     */
    protected void setWorkMode(int newV) {
        workMode = newV;
    }

    /**
     * Changes the state to working.
     */
    public boolean working() {
        if ((workMode == ADDING_WINDOW) || (workMode == DELETING) || (workMode == ADDING_GROUP)) {
            setWorkMode(WORKING);
            return true;
        }
        return false;
    }

    /* (non-Javadoc)
      * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
      */
    public void keyTyped(KeyEvent e) {
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
      * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
      */
    public void keyPressed(KeyEvent e) {
        if (e.getKeyChar() == KeyEvent.VK_DELETE) {
            desktop.fallAndHandleEvent(cooX, cooY, new RemoveEvent(cooX, cooY, this));
        }
    }

    /* (non-Javadoc)
      * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
      */
    public void keyReleased(KeyEvent e) {
        // TODO Auto-generated method stub
    }
}