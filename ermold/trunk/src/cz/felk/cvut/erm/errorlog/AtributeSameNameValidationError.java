package cz.felk.cvut.erm.errorlog;

/**
 * Atributes cannot have same names.
 *
 * @see cz.felk.cvut.erm.conceptual.beans.Atribute
 */
public class AtributeSameNameValidationError extends ConceptualObjectVectorValidationError {
    /**
     * Returns the title associated with the tree node.
     *
     * @return java.lang.String
     */
    public String toString() {
        return "Attribute name duplication";
    }
}
