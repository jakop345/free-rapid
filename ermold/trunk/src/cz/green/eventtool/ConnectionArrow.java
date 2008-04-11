package cz.green.eventtool;

import cz.green.event.exceptions.ImpossibleNegativeValueException;
import cz.green.event.interfaces.Manager;
import cz.green.event.interfaces.PaintableManager;

/**
 * Has the same functionalitz as ancestor, but adds one important thing. At the and (by first
 * connected window) paints the arrow.
 */
public class ConnectionArrow extends ConnectionLine {
    /**
     * The size of the painted arrow
     */
    static final private int LENGTH = 10;
    /**
     * The size of the painted diamond
     */
    static final private int DLENGTH = 12;
    /**
     * Polygon, that held the arrow shape
     */
    transient protected java.awt.Polygon arrow = null;
    /**
     * Polygon, that held the diamond shape
     */
    transient protected java.awt.Polygon diamond = null;

    /**
     * Same functionality as inhereted.
     *
     * @see ConnectionLine#ConnectionLine(cz.green.event.interfaces.Manager , cz.green.ermodeller.Connectable, cz.green.ermodeller.Connectable)
     */
    public ConnectionArrow(Manager manager, Connectable one, Connectable two) throws NullPointerException, ImpossibleNegativeValueException {
        super(manager, one, two);
    }

    /**
     * Same functionality as inhereted but as add-on counts the arrow size, location and direction.
     *
     * @see #arrow
     */
    public void moves() {
        java.awt.Point p = one.getCenter();
        boolean rp = false;
        boolean rd = false;
        int[][] r = rect;
        if (arrow == null) {
            arrow = new java.awt.Polygon(new int[3], new int[3], 3);
            rp = true;
        }
        if (diamond == null) {
            diamond = new java.awt.Polygon(new int[4], new int[4], 4);
            rp = true;
        }
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
            arrow.xpoints[0] = borderOne.x;
            arrow.ypoints[0] = borderOne.y;
            double alfa = Math.atan((double) (borderOne.y - borderTwo.y) / ((double) (borderOne.x - borderTwo.x)));
            float scale = ((PaintableManager) manager).getScale();
            if (borderOne.x < borderTwo.x)
                alfa += Math.PI;
            alfa += Math.PI / 6;
            arrow.xpoints[1] = borderOne.x - (int) (LENGTH * Math.cos(alfa) / scale);
            arrow.ypoints[1] = borderOne.y - (int) (LENGTH * Math.sin(alfa) / scale);
            alfa -= Math.PI / 3;
            arrow.xpoints[2] = borderOne.x - (int) (LENGTH * Math.cos(alfa) / scale);
            arrow.ypoints[2] = borderOne.y - (int) (LENGTH * Math.sin(alfa) / scale);

            alfa = Math.atan((double) (borderOne.y - borderTwo.y) / ((double) (borderOne.x - borderTwo.x)));
            diamond.xpoints[0] = borderOne.x;
            diamond.ypoints[0] = borderOne.y;
            if (borderOne.x < borderTwo.x)
                alfa += Math.PI;
            alfa += Math.PI / 8;

            diamond.xpoints[1] = borderOne.x - (int) (DLENGTH * Math.cos(alfa) / scale);
            diamond.ypoints[1] = borderOne.y - (int) (DLENGTH * Math.sin(alfa) / scale);

            alfa -= Math.PI / 8;
            diamond.xpoints[2] = borderOne.x - (int) ((2 * DLENGTH) * Math.cos(alfa) / scale);
            diamond.ypoints[2] = borderOne.y - (int) ((2 * DLENGTH) * Math.sin(alfa) / scale);

            alfa -= Math.PI / 8;
            diamond.xpoints[3] = borderOne.x - (int) (DLENGTH * Math.cos(alfa) / scale);
            diamond.ypoints[3] = borderOne.y - (int) (DLENGTH * Math.sin(alfa) / scale);
        }
    }

    /**
     * Same as inhereted, but add painting the arrow.
     */
    public void paint(java.awt.Graphics g) {
        super.paint(g);
        g.setColor(getForegroundColor());
        switch (ACTUAL_NOTATION) {
            case (CHEN):
                if (!isStrongAddictionChild()) g.fillPolygon(arrow);
                break;
            case (BINARY):
                break;
            case (UML):
                if (isStrongAddictionChild()) g.fillPolygon(diamond);
                break;
        }
    }

    /**
     * Same as inhereted and adds printing the arrow.
     */
    public void print(java.awt.Graphics g) {
        super.paint(g);
        g.setColor(getForegroundColor());
        switch (ACTUAL_NOTATION) {
            case (CHEN):
                if (!isStrongAddictionChild()) g.fillPolygon(arrow);
                break;
            case (BINARY):
                break;
            case (UML):
                if (isStrongAddictionChild()) g.fillPolygon(diamond);
                break;
        }
    }

    public boolean isStrongAddictionChild() {
        return strongAddictionChild;
    }

    public void setStrongAddictionChild(boolean strongAddictionChild) {
        this.strongAddictionChild = strongAddictionChild;
    }
}
