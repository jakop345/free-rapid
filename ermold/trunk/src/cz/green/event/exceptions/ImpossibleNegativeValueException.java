package cz.green.event;

/**
 * This exception gives info about not wanted negative value.
 */
public class ImpossibleNegativeValueException extends EventFallEngineException {
    /**
     * ImposibleNegativeValueException default constructor.
     */
    public ImpossibleNegativeValueException() {
        super();
    }

    /**
     * ImposibleNegativeValueException constructor, that passes message.
     *
     * @param <code>s</code> Passed message.
     */
    public ImpossibleNegativeValueException(String s) {
        super(s);
    }
}
