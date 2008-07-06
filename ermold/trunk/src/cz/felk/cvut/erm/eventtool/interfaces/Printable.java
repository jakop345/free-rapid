package cz.felk.cvut.erm.eventtool.interfaces;

/**
 * All classes which want to have the opportunity to be printed should
 * implement this interface.
 */
public interface Printable {
    /**
     * Return bounds where lies specified object.
     *
     * @return The bounds of the object.
     */
    java.awt.Rectangle getPrintBounds();

    /**
     * Prints the implementator at the specified graphic context <code>g</code>.
     *
     * @param g Where is printed.
     */
    void print(java.awt.Graphics g);
}
