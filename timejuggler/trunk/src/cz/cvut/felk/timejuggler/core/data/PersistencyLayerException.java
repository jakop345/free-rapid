package cz.cvut.felk.timejuggler.core.data;

/**
 * @author Vity
 */
public class PersistencyLayerException extends RuntimeException {
    public PersistencyLayerException(Throwable cause) {
        super(cause);
    }

    public PersistencyLayerException(String message, Throwable cause) {
        super(message, cause);
    }
}
