package cz.green.event.exceptions;

/**
 * Super class all exceptions thrown by classes from this package.
 */
public class EventFallEngineException extends Exception {
    /**
     * Inherited default constructor.
     */
    public EventFallEngineException() {
        super();
    }

    /**
     * Pass message to the catcher of this exception.
     *
     * @param <code>s</code> The passed message.
     */
    public EventFallEngineException(String s) {
        super(s);
    }
}
