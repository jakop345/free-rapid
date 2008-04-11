package cz.green.swing;

import java.awt.*;

public interface BoundsConstraint {
    Rectangle getBounds(Dimension parent);

    Dimension getParentSize(Dimension compSize);
}
