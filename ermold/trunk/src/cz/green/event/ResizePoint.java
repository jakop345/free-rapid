package cz.green.event;

/**
 * This class gives information about resize points places in item.
 * By draging this points (most drawn as little black rectangle)
 * user can resize the item.
 */
public class ResizePoint {
    /**
     * The relative x coordinate the resize point.
     */
    public double x = 0;
    /**
     * The relative y coordinate the resize point.
     */
    public double y = 0;
    /**
     * This field says in which coordinate we can resize the item
     * by draging this point
     */
    public int direction = 0;
    /**
     * This constant is used for defining the <code>direction</code>.
     */
    public final static int XCOORDINATE = 1;
    /**
     * This constant is used for defining the <code>direction</code>.
     */
    public final static int YCOORDINATE = 2;
    /**
     * This constant is used for moving left border.
     */
    public final static int LEFT = 4 | XCOORDINATE;
    /**
     * This constant is used for moving right border.
     */
    public final static int RIGHT = 8 | XCOORDINATE;
    /**
     * This constant is used for moving top border.
     */
    public final static int TOP = 16 | YCOORDINATE;
    /**
     * This constant is used for moving bottom border.
     */
    public final static int BOTTOM = 32 | YCOORDINATE;

    /**
     * Creates <code>ResizePoint</code> in the specified point
     * (<code>x</code>, <code>y</code>) and
     * with <code>direction</code>.
     *
     * @param <code>x</code>         X coordinate.
     * @param <code>y</code>         Y coordinate.
     * @param <code>direction</code> Composition of the constant <code>XCOORDINATE</code>
     *                               and <code>YCOORDINATE</code> specifing the direction.
     * @see ResizePoint.direction
     */
    public ResizePoint(double x, double y, int direction) {
        this.x = x;
        this.y = y;
        this.direction = direction;
}
}
