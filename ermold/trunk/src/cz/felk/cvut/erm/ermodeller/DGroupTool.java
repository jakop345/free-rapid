package cz.felk.cvut.erm.ermodeller;

import cz.felk.cvut.erm.ermodeller.interfaces.FontManager;
import cz.felk.cvut.erm.ermodeller.interfaces.ModelFinder;
import cz.felk.cvut.erm.ermodeller.interfaces.ViewController;
import cz.felk.cvut.erm.event.ExMoveEvent;
import cz.felk.cvut.erm.event.ExMovingEvent;
import cz.felk.cvut.erm.event.SelectItemExEvent;
import cz.felk.cvut.erm.event.exceptions.ImpossibleNegativeValueException;
import cz.felk.cvut.erm.event.interfaces.Item;
import cz.felk.cvut.erm.event.interfaces.Manager;

import java.util.List;

/**
 * This group has special functionality useful by relations and entities. It can't receive any events,
 * scales up and down to exactly fit all its childs. It means that adding items can cause increasing
 * the size and removing decreasing. Or when item is moving the size can be also changed.
 * <p/>
 * When size item (also new) require large item, then it asks its manager to be able to increase its size.
 * The manager can be also DGroup instance, that it can also increase and so on.
 * <p/>
 * Its interface is the same as the interface of the others groups. But as add-on it implements the
 * <code>ConnectionManager</code> iterface to be able to hold the connections. Therefore has also atribute
 * <code>connections</code>, where holds local connections.
 */
class DGroupTool extends cz.felk.cvut.erm.eventtool.DGroupTool implements FontManager, ModelFinder {
    /**
     * Simply calls the inherited constructor.
     *
     * @see cz.felk.cvut.erm.eventtool.GroupTool#GroupTool(cz.felk.cvut.erm.event.interfaces.Manager , int, int, int, int)
     */
    public DGroupTool(Manager manager, int left, int top, int width, int height) throws NullPointerException, ImpossibleNegativeValueException {
        super(manager, left, top, width, height);
    }

    /**
     * Returns <code>FontMetrics</code> of currently used font. Exist to implement <code>FontManager</code>.
     * Simply calls the same manager's method.
     *
     * @see FontMetrics#getReferentFontMetrics()
     */
    public java.awt.FontMetrics getReferentFontMetrics() {
        try {
            return ((FontManager) manager).getReferentFontMetrics();
        } catch (ClassCastException e) {
            return null;
        }
    }

    /**
     * Handles the <code>ExMoveEvent</code>. This object receives no events by standard way
     * used in this library, so this event handler can be invoked only by others object directly. This is
     * invoked by relation or entity when it's moving. Calls inherited <code>handleMoveEvent</code>.
     */
    public void handleExMoveEvent(ExMoveEvent event) {
        handleMoveEvent(event);
    }

    /**
     * Handles the <code>ExMovingEvent</code>. This object receives no events by standard way
     * used in this library, so this event handler can be invoked only by others object directly. This is
     * invoked by relation or entity when it's moving. Calls inherited <code>handleMovingEvent</code>.
     */
    public void handleExMovingEvent(ExMovingEvent event) {
        handleMovingEvent(event);
    }

    /**
     * This method was created by Jiri Mares
     *
     * @param elems Vector
     * @param event cz.green.event.SelectItemExEvent
     */
    public void isModelIn(List elems, SelectItemExEvent event) {
        int size = wins.size();
        ViewController vc;
        Object o;
        int index;
        for (int i = 0; i < size; i++) {
            if (elems.size() == 0)
                return;
            if ((o = wins.elementAt(i)) instanceof ViewController) {
                vc = (ViewController) o;
                if ((index = elems.indexOf(vc.getModel())) != -1) {
                    elems.remove(index);
                    ((Item) o).invokeEventHandler(event);
                }
            }
            if (o instanceof ModelFinder) {
                ((ModelFinder) o).isModelIn(elems, event);
            }
        }
    }
}