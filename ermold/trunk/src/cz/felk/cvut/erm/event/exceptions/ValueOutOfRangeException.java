package cz.felk.cvut.erm.event.exceptions;

/**
 * Some value is out of range says this exception.
 */
public class ValueOutOfRangeException extends EventFallEngineException {
    /**
     * Default constructor.
     */
    public ValueOutOfRangeException() {
        super();
    }

    /**
     * Constructor with message to the catcher.
     *
     * @param <code>s</code> The message.
     */
    public ValueOutOfRangeException(String s) {
        super(s);
    }
}
