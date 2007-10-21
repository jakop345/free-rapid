package cz.cvut.felk.timejuggler.db.entity.interfaces;

/**
 * @author Vity
 */
public interface EntityElement extends Cloneable {
    void setChanged(boolean changed);

    boolean isChanged();
}
