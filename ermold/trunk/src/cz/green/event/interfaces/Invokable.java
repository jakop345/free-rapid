package cz.green.event.interfaces;

/**
 * This interface is used for items, that can handle event. Has only one methods, that looks in
 * objects hierarchy tree for event handler.
 */
public interface Invokable {
    /**
     * This method looks for handler for <code>event</code> in this
     * instance and if exist then invokes it to handle the <code>event</code>.
     *
     * @param <code>event</code> Event for which to invoke the handler.
     * @return Says if the event handler exists or not. If <code>true</code> it exists.
     */
    boolean invokeEventHandler(Event event);
}
