package cz.green.ermodeller;

import cz.green.event.SelectItemExEvent;

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
