package cz.felk.cvut.erm.event.interfaces;

/**
 * This is the superclass for all event handled by this event fall mechanism.
 */
public interface Event {
    /**
     * This method determine whether the event will be passed to focused item or not.
     *
     * @return <code>True</code> (<code>false</code>) if the event should (not) be passed to focused item.
     */
    boolean passToFocused();

    /**
     * This method determine whether the event will be passed to all selected items or not.
     *
     * @return <code>True</code> (<code>false</code>) if the event should (not) be passed to all selected items.
     */
    boolean passToSelected();
}
