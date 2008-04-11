package cz.green.event.interfaces;

/**
 * This interface determine the methods needed for manager of
 * <code>PaintableItem</code>s.
 */
public interface PaintableManager extends PaintableItem {
    /**
     * This method returns the actual scale. By value returned by
     * this method is divided the size of the item.
     *
     * @return Actual scale.
     * @see PaintableItem#getBounds()
     */
    float getScale();

    /**
     * This method set the item to repaint and call repaint method.
     * When comes event to repaint the manager repaints only this item.
     * Other way to do this is to call paint for this item without
     * calling repaint.
     *
     * @param <code>item</code> Item for repainting.
     */
    void repaintItem(PaintableItem item);

    /**
     * This method set the item to repaint fast and call repaint method.
     * When comes event to repaint the manager repaints only this item.
     * Other way to do this is to call paint for this item without
     * calling repaint.
     *
     * @param <code>item</code> Item for repainting.
     */
    void repaintItemFast(PaintableItem item);

    /**
     * This method forces to repaint the specified rectangle.
     * It could be used for example, when the item is moved to the bottom
     * in the all items to repaint item area.
     *
     * @param <code>x</code>      The x coordinate of the left top point of the rectangle to repaint.
     * @param <code>y</code>      The y coordinate of the left top point of the rectangle to repaint.
     * @param <code>width</code>  The width of the rectangle to repaint.
     * @param <code>height</code> The height of the rectangle to repaint.
     */
    void repaintRectangle(int x, int y, int width, int height);

    /**
     * This method try to select the specified <code>item</code>. If the
     * <code>item</code> is already selected do nothing.
     *
     * @param <code>item</code> The item to select.
     * @param <code>add</code>  Determines whether we add the item to selected items (<code>add</code>
     *                          is <code>true</code>) or the items creates the new selected items
     *                          list (<code>add</code> is <code>true</code>).
     * @return <code>true</code> if the item was selected;
     *         <code>false</code> otherways
     */
    boolean selectItem(SelectableItem item, boolean add);

    /**
     * Select only <code>item</code> or deselected it. It dependes on parameter <code>add</code>.
     *
     * @param <code>item</code> The selected item.
     * @param <code>add</code>  Determine whether the item is removed (<code>false</code>) from
     *                          list selected items or the <code>item</code> is added to the list.
     */
    void selectItemEx(SelectableItem item, boolean add);
}
