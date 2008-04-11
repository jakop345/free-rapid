package cz.green.event.exceptions;

/**
 * This class informs about situation, that somebody tries to add
 * (move, resize) out of the manager.
 */
public class ItemNotInsideManagerException extends EventFallEngineException {
    /**
     * ItemNotInsideManagerException default constructor.
     */
    public ItemNotInsideManagerException() {
        super();
    }

    /**
     * ItemNotInsideManagerException constructor, that passes message.
     *
     * @param <code>s</code> Passed message.
     */
    public ItemNotInsideManagerException(String s) {
        super(s);
    }
}
