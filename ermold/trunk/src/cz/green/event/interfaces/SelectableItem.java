package cz.green.event;

/**
 * All classes, that can be selected, must implement this interface.
 *
 * @see Invokable
 * @see PaintableItem
 */
public interface SelectableItem extends Invokable, PaintableItem {
    /**
     * This method was created by Jiri Mares
     */
    void deselect();

    /**
     * This method was created by Jiri Mares
     */
    void select();
}
