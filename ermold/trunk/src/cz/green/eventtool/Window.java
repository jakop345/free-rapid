package cz.green.eventtool;

/**
 * This class has the same functionality as predecessor. Adds only one methods, which is caused by implementing the
 * interface <code>Printable</code>. Implementing this interface caused the possibility to be printed.
 */
public class Window extends cz.green.event.Window implements cz.green.eventtool.Printable {
    /**
     * The level of details - show full details
     */
    public final static int LOD_FULL = 0;
    /**
     * The level of details - show entities with only primary keys
     */
    public final static int LOD_MEDIUM = 1;
    /**
     * The level of details - show entities without attributes
     */
    public final static int LOD_LOW = 2;
    /**
     * Actual level of details
     */
    public static int ACTUAL_LOD = 0;
    /**
     * Graphic notation - Chan
     */
    public final static int CHEN = 0;
    /**
     * Graphic notation - Binary
     */
    public final static int BINARY = 1;
    /**
     * Graphic notation - UML
     */
    public final static int UML = 2;
    /**
     * Actual graphic notation
     */
    public static int ACTUAL_NOTATION = 0;
    /**
     * Flag if the symbol "pk" is shown in front of Atribute in UML notation
     * 0 = don't show
     * 1 = show
     */
    public static int SHOW_PK_IN_UML = 1;
    /**
     * Flag to show shorten 0..N and 1..1 cardinalities
     * 0 = normal - 0..*; 1..1
     * 1 = shorten - *  ;  1
     */
    public static int SHOW_SHORTEN_CARD_IN_UML = 1;

    /**
     * Is needful for creating desktop.
     *
     * @see cz.green.event.Window#Window()
     */
    protected Window() {
        super();
    }

    /**
     * Calls the derived constructor.
     *
     * @see cz.green.event.Window#Window(cz.green.event.Manager, int, int, int, int)
     */
    public Window(cz.green.event.Manager manager, int left, int top, int width, int height) throws NullPointerException, cz.green.event.ImpossibleNegativeValueException {
        super(manager, left, top, width, height);
    }

    /**
     * Returns rectangle which specifies the size of this window.
     *
     * @see PaintableItem#getBounds()
     */
    public java.awt.Rectangle getPrintBounds() {
        return getBounds();
    }

    /**
     * Prints the window. Exists because of implementing the interface Printable.
     *
     * @see Printable#print(java.awt.Graphics)
     */
    public void print(java.awt.Graphics g) {
        //paint window
        java.awt.Rectangle r = getBounds();
	g.drawRect(r.x, r.y, r.width, r.height);
	r = null;
}
}
