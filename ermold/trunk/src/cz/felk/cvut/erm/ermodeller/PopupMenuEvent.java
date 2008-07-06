package cz.felk.cvut.erm.ermodeller;

import cz.felk.cvut.erm.event.CoordinateEvent;

import javax.swing.*;
import java.awt.*;

/**
 * This event informs about appearing the popup menu event. Has the point where the
 * event occurs and the instance of the <code>PopupMenu</code> class to adds to it
 * some items and display.
 */
public class PopupMenuEvent extends CoordinateEvent {
    /**
     * The x coordinate the popup event. This is the real display coordinate not
     * after translation using the current scale
     */
    protected int realX = 0;
    /**
     * The y coordinate the popup event. This is the real display coordinate not
     * after translation using the current scale
     */
    protected int realY = 0;
    /**
     * The menu into which to put items and display.
     */
    protected JPopupMenu menu = null;

    /**
     * Construct the <code>PopupMenuEvent</code> using the inherited constructor
     * and set value to the new atributes.
     *
     * @param realX The x coordinate the popup event. This is the real display coordinate not
     *              after translation using the current scale.
     * @param realY The x coordinate the popup event. This is the real display coordinate not
     *              after translation using the current scale.
     * @param menu  The popup menu where put new items and display.
     * @see cz.felk.cvut.erm.event.CoordinateEvent#CoordinateEvent(int, int, java.awt.Component)
     */
    public PopupMenuEvent(int x, int y, int realX, int realY, Component comp, JPopupMenu menu) {
        super(x, y, comp);
        this.realX = realX;
        this.realY = realY;
        this.menu = menu;
    }

    /**
     * Gets the value of the <b>menu</b> atribute.
     *
     * @return The value of the <b>menu</b> atribute.
     * @see #menu
     */
    public JPopupMenu getMenu() {
        return menu;
    }

    /**
     * Gets the value of the <b>realX</b> atribute.
     *
     * @return The value of the <b>realX</b> atribute.
     * @see #realX
     */
    public int getRealX() {
        return realX;
    }

    /**
     * Gets the value of the <b>realY</b> atribute.
     *
     * @return The value of the <b>realY</b> atribute.
     * @see #realY
     */
    public int getRealY() {
        return realY;
    }

    /**
     * Sets the value of the <b>realX</b> atribute.
     *
     * @param realX The value of the <b>realX</b> atribute.
     * @see #realX
     */
    public void setRealX(int realX) {
        this.realX = realX;
    }

    /**
     * Sets the value of the <b>realY</b> atribute.
     *
     * @param realY The value of the <b>realY</b> atribute.
     * @see #realY
     */
    public void setRealY(int realY) {
        this.realY = realY;
    }
}
