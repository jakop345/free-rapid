package cz.cvut.felk.timejuggler.db.entity.interfaces;

import java.awt.*;

/**
 * @author Vity
 *
 * interface
 */
public interface CategoryEntity extends EntityElement {
    boolean hasAssignedColor();

    Color getColor();

    void setColor(Color color);

    String getName();

    void setName(String newVal);

    Object clone() throws CloneNotSupportedException;
}
