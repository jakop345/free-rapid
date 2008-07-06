package cz.felk.cvut.erm.event;

/**
 * Implements the methods for creating resize boxes.
 */
public class ResizeRectangle extends java.awt.Rectangle {
    /**
     * This field says in which coordinate we can resize the item
     * by draging this point
     */
    public int direction = 0;

    /**
     * Creates <code>ResizeRectangle</code> in the specified point
     * (<code>x</code>, <code>y</code>), with size
     * (<code>width</code>, <code>height</code>) and
     * with <code>direction</code>. The other function is
     * to translate the left top point by
     * (<code>width/2</code>, <code>height/2</code>) to the rectangle by in the middle of ResizePoint.
     *
     * @param <code>x</code>         X coordinate.
     * @param <code>y</code>         Y coordinate.
     * @param <code>direction</code> Composition of the constant <code>ResizePoint.XCOORDINATE</code>
     *                               and <code>ResizePoint.YCOORDINATE</code> specifing the direction.
     * @see ResizePoint.XCOORDINATE
     * @see ResizePoint.YCOORDINATE
     * @see ResizePoint
     */
    public ResizeRectangle(int x, int y, int width, int height, int direction) {
        super(x, y, width, height);
        this.direction = direction;
        x -= width / 2;
        y -= height / 2;
    }
}
