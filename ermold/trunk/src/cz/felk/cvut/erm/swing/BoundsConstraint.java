package cz.felk.cvut.erm.swing;

import java.awt.*;

public interface BoundsConstraint {
    Rectangle getBounds(Dimension parent);

    Dimension getParentSize(Dimension compSize);
}
