package cz.green.eventtool;

import cz.green.ermodeller.*;
import cz.green.event.ResizePoint;
import cz.green.event.exceptions.ImpossibleNegativeValueException;
import cz.green.event.interfaces.Manager;
import cz.green.eventtool.interfaces.Connectable;
import cz.green.eventtool.interfaces.Connection;

import java.awt.*;

/**
 * The class implemented the interface Connection. Do nothing more only paint
 * the line before connected window.
 */
public class ConnectionLine extends cz.green.eventtool.Window implements
        Connection {
    /**
     * The first connected element
     */
    protected Connectable one = null;

    /**
     * The second connected element
     */
    protected Connectable two = null;

    /**
     * Center of the first connected element
     */
    transient protected java.awt.Point centerOne = null;

    /**
     * Center of the second connected element
     */
    transient protected java.awt.Point centerTwo = null;

    /**
     * Border of the first connected element
     */
    transient protected java.awt.Point borderOne = null;

    /**
     * Border of the second connected element
     */
    transient protected java.awt.Point borderTwo = null;

    /**
     * Real enter of the first connected element
     */
    transient protected java.awt.Point realCenterOne = null;

    /**
     * Real enter of the second connected element
     */
    transient protected java.awt.Point realCenterTwo = null;

    /**
     * Real border of the first connected element
     */
    transient protected java.awt.Point realBorderOne = null;

    /**
     * Real border of the second connected element
     */
    transient protected java.awt.Point realBorderTwo = null;

    /**
     * Mandatory of cardinality which is connect through this connection line
     */
    private boolean connectionMandatory = false;

    /**
     * Connection line to strong addiction
     */
    private boolean strongAddicted = false;

    /**
     * Flag if the connection is to strong addiction child
     */
    protected boolean strongAddictionChild = false;

    /**
     * Calls the inhereted constructor and fills all properties of this object.
     */
    public ConnectionLine(Manager manager, Connectable one, Connectable two)
            throws ImpossibleNegativeValueException, NullPointerException {
        super(manager, 0, 0, 0, 0);
        if ((one == null) || (two == null))
            throw new NullPointerException();
        this.one = one;
        this.two = two;
        // connects to the windows
        one.addConnection(this);
        two.addConnection(this);
        // counts the center
        borderTwo = two.getBorder(centerOne = one.getCenter());
        borderOne = one.getBorder(centerTwo = two.getCenter());
        int[][] r = rect;
        if (centerOne.x > centerTwo.x) {
            r[0][0] = centerTwo.x;
            r[0][1] = centerOne.x;
        } else {
            r[0][1] = centerTwo.x;
            r[0][0] = centerOne.x;
        }
        if (centerOne.y > centerTwo.y) {
            r[1][0] = centerTwo.y;
            r[1][1] = centerOne.y;
        } else {
            r[1][1] = centerTwo.y;
            r[1][0] = centerOne.y;
        }
    }

    /**
     * Disconnect the connection and disconnect also from the connected window.
     *
     * @see Connection#disconnect()
     */
    public void disconnect() {
        one.removeConnection(this);
        two.removeConnection(this);
        one = null;
        two = null;
        java.awt.Rectangle r = getBounds();
        manager.remove(this);
        manager.selectItemEx(null, false);
        manager.repaintRectangle(r.x, r.y, r.width,
                r.height);
    }

    /**
     * Counts the bounds to contain both border points.
     */
    public java.awt.Rectangle getBounds() {
        java.awt.Rectangle r = new java.awt.Rectangle(borderOne);
        r.add(borderTwo);
        return r;
    }

    /**
     * Gets the first connected connectable window.
     *
     * @return The connectable window.
     * @see cz.green.eventtool.interfaces.Connection#getOne()
     */
    public Connectable getOne() {
        return one;
    }

    /**
     * Same as <code>getBounds</code> but uses the real border.
     *
     * @see #getBounds()
     */
    public java.awt.Rectangle getRealBounds() {
        java.awt.Rectangle r = new java.awt.Rectangle(realBorderOne);
        r.add(realBorderTwo);
        return r;
    }

    /**
     * Can't be resized, therefore returns <code>nul</code>.
     */
    public ResizePoint[] getResizePoints() {
        return null;
    }

    /**
     * Gets the second connected connectable window.
     *
     * @return The connectable window.
     * @see Connection#getTwo()
     */
    public Connectable getTwo() {
        return two;
    }

    /**
     * This method was created by Jiri Mares
     *
     * @param cls java.lang.Class
     * @return java.lang.Object
     */
    public Object isConnectedTo(Class cls) {
        Object o;
        if (cls.isInstance(o = getOne()))
            return o;
        if (cls.isInstance(o = getTwo()))
            return o;
        return null;
    }

    /**
     * Always returns <code>false</code> to avoid being the receiver of the
     * vent.
     */
    public boolean isIn(int x, int y) {
        return false;
    }

    /**
     * Is invoken before very repainting, and test whether teh connected window
     * doesn't change its border points. If they have it copunts new border
     * poinrs.
     */
    protected void moves() {
        java.awt.Point p = one.getCenter();
        boolean rp = false;
        if (!p.equals(centerOne)) {
            centerOne = p;
            rp = true;
        }
        p = two.getCenter();
        if (!p.equals(centerTwo)) {
            centerTwo = p;
            rp = true;
        }
        if (rp) {
            borderOne = one.getBorder(centerTwo);
            borderTwo = two.getBorder(centerOne);
        }
    }

    /**
     * Paint as simple line before border points. Before painting calls
     * <code>move</code> method.
     *
     * @see #moves()
     */
    public void paint(java.awt.Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        moves();
        if (ACTUAL_NOTATION == ConceptualConstruct.BINARY) {
            if ((AtributeConstruct.class.equals(one.getClass()))
                    || (AtributeConstruct.class.equals(two
                    .getClass())))
                return;
            if (((CardinalityConstruct.class.equals(one.getClass()))
                    && (EntityConstruct.class.equals(two.getClass())))
                    ||
                    ((CardinalityConstruct.class.equals(two.getClass()))
                            && (EntityConstruct.class.equals(one.getClass()))))
                return;
            float dash1[] = {6f};//default 4.5f
            float dash2[] = {333333.5f};
            if (strongAddicted && !strongAddictionChild) {
                g.drawLine(borderOne.x, borderOne.y, (borderTwo.x + borderOne.x) / 2, (borderTwo.y + borderOne.y) / 2);
                BasicStroke roundStroke = new BasicStroke(Consts.STROKE_WIDTH, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f, dash1, 0.0f);
                g2.setStroke(roundStroke);
                g.drawLine(borderTwo.x, borderTwo.y, (borderTwo.x + borderOne.x) / 2, (borderTwo.y + borderOne.y) / 2);
                BasicStroke lineStroke = new BasicStroke(Consts.STROKE_WIDTH, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 100f, dash2, 0.0f);
                g2.setStroke(lineStroke);
                //g.drawLine(borderOne.x, borderOne.y, borderTwo.x, borderTwo.y);
            } else {
                if (connectionMandatory || strongAddicted)
                    g.drawLine(borderOne.x, borderOne.y, borderTwo.x, borderTwo.y);
                else {
                    BasicStroke roundStroke = new BasicStroke(Consts.STROKE_WIDTH, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f, dash1, 0.0f);
                    g2.setStroke(roundStroke);
                    g2.drawLine(borderOne.x, borderOne.y, borderTwo.x, borderTwo.y);
                    BasicStroke lineStroke = new BasicStroke(Consts.STROKE_WIDTH, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 100f, dash2, 0.0f);
                    g2.setStroke(lineStroke);
                }
            }
        }
        final Stroke stroke = updateStrokeWithAliasing(g);
        if (ACTUAL_NOTATION == ConceptualConstruct.UML) {
            if ((AtributeConstruct.class.equals(one.getClass()
            ))
                    || (AtributeConstruct.class.equals(two
                    .getClass())))
                return;
            if (((CardinalityConstruct.class.equals(one.getClass()))
                    && (EntityConstruct.class.equals(two.getClass())))
                    ||
                    ((CardinalityConstruct.class.equals(two.getClass()))
                            && (EntityConstruct.class.equals(one.getClass()))))
                return;
            g.drawLine(borderOne.x, borderOne.y, borderTwo.x, borderTwo.y);
        }
        if (ACTUAL_NOTATION == ConceptualConstruct.CHEN) {

            switch (ACTUAL_LOD) {
                case (LOD_FULL):
                    g.drawLine(borderOne.x, borderOne.y, borderTwo.x, borderTwo.y);
                case (LOD_MEDIUM):
                    /* There are no lines to atributes */
                    if (!(one.getClass().equals(
                            AtributeConstruct.class) || two.getClass()
                            .equals(AtributeConstruct.class)))
                        g.drawLine(borderOne.x, borderOne.y, borderTwo.x, borderTwo.y);
                        /* There is one end of line to atribute, so we test it hte atribute is primary */
                    else {
                        if ((one.getClass().equals(AtributeConstruct.class)))
                            if (((AtributeConstruct) getOne()).isPrimary())
                                g.drawLine(borderOne.x, borderOne.y, borderTwo.x, borderTwo.y);
                        if ((two.getClass().equals(AtributeConstruct.class)))
                            if (((AtributeConstruct) getTwo()).isPrimary())
                                g.drawLine(borderOne.x, borderOne.y, borderTwo.x, borderTwo.y);
                    }
                case (LOD_LOW):
                    if (!(one.getClass().equals(
                            AtributeConstruct.class) || two.getClass()
                            .equals(AtributeConstruct.class)))
                        g.drawLine(borderOne.x, borderOne.y, borderTwo.x, borderTwo.y);
            }
        }
        updateBackupStroke(g, stroke);
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
     * Paint as simple line before border points. Before painting calls
     * <code>realMove</code> method.
     *
     * @see #realMoves()
     */
    public void paintFast(java.awt.Graphics g) {
        realMoves();
        if (ACTUAL_NOTATION == ConceptualConstruct.BINARY || ACTUAL_NOTATION == ConceptualConstruct.UML)
            if ((AtributeConstruct.class.equals(one.getClass()))
                    || (AtributeConstruct.class.equals(two.getClass()))
                    || (UniqueKeyConstruct.class.equals(two.getClass()))
                    || (UniqueKeyConstruct.class.equals(one.getClass())))
                return;

        g.drawLine(realBorderOne.x, realBorderOne.y, realBorderTwo.x,
                realBorderTwo.y);
    }

    /**
     * Prints as simple line before border points. Before painting calls
     * <code>move</code> method.
     *
     * @see #move()
     */
    public void print(java.awt.Graphics g) {
        moves();
        if (ACTUAL_NOTATION == ConceptualConstruct.BINARY) {
            if ((AtributeConstruct.class.equals(one.getClass()))
                    || (AtributeConstruct.class.equals(two.getClass())))
                return;
            if (((CardinalityConstruct.class.equals(one.getClass()))
                    && (EntityConstruct.class.equals(two.getClass())))
                    ||
                    ((CardinalityConstruct.class.equals(two.getClass()))
                            && (EntityConstruct.class.equals(one.getClass()))))
                return;
            Graphics2D g2 = (Graphics2D) g;
            float dash1[] = {6f};//default 4.5f
            float dash2[] = {333333.5f};
            if (strongAddicted && !strongAddictionChild) {
                g.drawLine(borderOne.x, borderOne.y, (borderTwo.x + borderOne.x) / 2, (borderTwo.y + borderOne.y) / 2);
                BasicStroke roundStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f, dash1, 0.0f);
                g2.setStroke(roundStroke);
                g.drawLine(borderTwo.x, borderTwo.y, (borderTwo.x + borderOne.x) / 2, (borderTwo.y + borderOne.y) / 2);
                BasicStroke lineStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 100f, dash2, 0.0f);
                g2.setStroke(lineStroke);
                //g.drawLine(borderOne.x, borderOne.y, borderTwo.x, borderTwo.y);
            } else {
                if (connectionMandatory || strongAddicted)
                    g.drawLine(borderOne.x, borderOne.y, borderTwo.x, borderTwo.y);
                else {
                    BasicStroke roundStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f, dash1, 0.0f);
                    g2.setStroke(roundStroke);
                    g2.drawLine(borderOne.x, borderOne.y, borderTwo.x, borderTwo.y);
                    BasicStroke lineStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 100f, dash2, 0.0f);
                    g2.setStroke(lineStroke);
                }
            }
        }
        if (ACTUAL_NOTATION == ConceptualConstruct.UML) {
            if ((AtributeConstruct.class.equals(one.getClass()))
                    || (AtributeConstruct.class.equals(two.getClass())))
                return;
            if (((CardinalityConstruct.class.equals(one.getClass()))
                    && (EntityConstruct.class.equals(two.getClass())))
                    ||
                    ((CardinalityConstruct.class.equals(two.getClass()))
                            && (EntityConstruct.class.equals(one.getClass()))))
                return;
            g.drawLine(borderOne.x, borderOne.y, borderTwo.x, borderTwo.y);
        }
        if (ACTUAL_NOTATION == ConceptualConstruct.CHEN) {
            switch (ACTUAL_LOD) {
                case (LOD_FULL):
                    g.drawLine(borderOne.x, borderOne.y, borderTwo.x, borderTwo.y);
                case (LOD_MEDIUM):
                    /* There are no lines to atributes */
                    if (!(one.getClass().equals(
                            AtributeConstruct.class) || two.getClass()
                            .equals(AtributeConstruct.class)))
                        g.drawLine(borderOne.x, borderOne.y, borderTwo.x, borderTwo.y);
                        /* There is one end of line to atribute, so we test it hte atribute is primary */
                    else {
                        if ((one.getClass().equals(AtributeConstruct.class)))
                            if (((AtributeConstruct) getOne()).isPrimary())
                                g.drawLine(borderOne.x, borderOne.y, borderTwo.x, borderTwo.y);
                        if ((two.getClass().equals(AtributeConstruct.class)))
                            if (((AtributeConstruct) getTwo()).isPrimary())
                                g.drawLine(borderOne.x, borderOne.y, borderTwo.x, borderTwo.y);
                    }
                case (LOD_LOW):
                    if (!(one.getClass().equals(
                            AtributeConstruct.class) || two.getClass()
                            .equals(AtributeConstruct.class)))
                        g.drawLine(borderOne.x, borderOne.y, borderTwo.x, borderTwo.y);
            }
        }

    }

    /**
     * Same as <code>move</code> but used when repainted fast and therefore
     * uses real position.
     *
     * @see #move()
     */
    public void realMoves() {
        java.awt.Point p = one.getRealCenter();
        boolean rp = false;
        if (!p.equals(realCenterOne)) {
            realCenterOne = p;
            rp = true;
        }
        p = two.getRealCenter();
        if (!p.equals(realCenterTwo)) {
            realCenterTwo = p;
            rp = true;
        }
        if (rp) {
            realBorderOne = one.getRealBorder(realCenterTwo);
            realBorderTwo = two.getRealBorder(realCenterOne);
        }
    }

    /**
     * If there is stored bounds, then repaint it.
     *
     * @see cz.green.event.interfaces.PaintableManager#repaintRectangle(int, int, int, int)
     */
    public void repaintStoredBounds() {
        if (rectangle != null) {
            java.awt.Rectangle r = rectangle;
            rectangle = null;
            manager.repaintRectangle(r.x, r.y, r.width,
                    r.height);
        }
    }

    /**
     * Stores actual bounds. If there are stored bounds, then repaints it using
     * <code>repaintStoredBounds</code> and the stores the bounds.
     *
     * @see #repaintStoredBounds
     */
    public void saveBounds() {
        repaintStoredBounds();
        rectangle = getBounds();
    }

    /**
     * Sets the first connected connectable window.
     *
     * @return The connectable window.
     * @see Connection#setOne()
     */
    public void setOne(Connectable newValue) {
        if (newValue == null)
            throw new NullPointerException();
        one.removeConnection(this);
        this.one = newValue;
        one.addConnection(this);
        java.awt.Rectangle rep = new java.awt.Rectangle(borderOne);
        rep.add(borderTwo);
        moves();
        rep.add(borderOne);
        rep.add(borderTwo);
        manager.repaintRectangle(rep.x, rep.y, rep.width, rep.height);
    }

    /**
     * Sets the second connected connectable window.
     *
     * @return The connectable window.
     * @see cz.green.eventtool.interfaces.Connection#setTwo()
     */
    public void setTwo(Connectable newValue) {
        if (newValue == null)
            throw new NullPointerException();
        two.removeConnection(this);
        two = newValue;
        two.addConnection(this);
        java.awt.Rectangle rep = new java.awt.Rectangle(borderOne);
        rep.add(borderTwo);
        moves();
        rep.add(borderOne);
        rep.add(borderTwo);
        manager.repaintRectangle(rep.x, rep.y, rep.width, rep.height);
    }

    /**
     * Cast to String to display.
     */
    public String toString() {
        StringBuffer message = new StringBuffer(getClass().getName());
        message.append("[").append(one).append("-").append(two).append("]");
        message.append(",@").append(Integer.toHexString(hashCode()));
        return new String(message);
    }

    /**
     * @return Returns the connectionMandatory.
     */
    public boolean isConnectionMandatory() {
        return connectionMandatory;
    }

    /**
     * @param connectionMandatory The connectionMandatory to set.
     */
    public void setConnectionMandatory(boolean connectionMandatory) {
        this.connectionMandatory = connectionMandatory;
    }

    public boolean isStrongAddicted() {
        return strongAddicted;
    }

    public void setStrongAddicted(boolean strongAddicted) {
        this.strongAddicted = strongAddicted;
    }
}
