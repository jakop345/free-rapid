package cz.felk.cvut.erm.ermodeller.interfaces;

/**
 * All classes that has its models should implements this interface.
 * Can only return its model.
 */
public interface ViewController {
    /**
     * Returns the model of the object.
     *
     * @return The model.
     */
    Object getModel();
}
