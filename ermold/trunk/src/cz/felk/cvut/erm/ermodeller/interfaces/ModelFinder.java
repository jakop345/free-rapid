package cz.felk.cvut.erm.ermodeller.interfaces;

import cz.felk.cvut.erm.event.SelectItemExEvent;

import java.util.Vector;

/**
 * This type was created by Jiri Mares
 */
public interface ModelFinder {
    /**
     * This method was created by Jiri Mares
     *
     * @param elems java.util.Vector
     * @param event cz.green.event.SelectItemExEvent
     */
    void isModelIn(Vector elems, SelectItemExEvent event);
}
