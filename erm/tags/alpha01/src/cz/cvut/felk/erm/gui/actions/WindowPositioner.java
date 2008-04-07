package cz.cvut.felk.erm.gui.actions;

import javax.swing.*;
import java.awt.*;

/**
 * Interface for classes which will be used to determine the position of windows as they are added to the desktop.
 * Useful if the type of windows you're adding require a custom layout.
 *
 * @see ca.guydavis.swing.desktop.CascadingWindowPositioner
 */
public interface WindowPositioner {

    /**
     * Determines the best position to place a new frame on the desktop based on the position of the existing windows.
     *
     * @param newFrame The new frame being added to the desktop.
     * @param frames   The list of existing, visible frames on the desktop.
     * @return The position in the container to place the new frame.
     */
    Point getPosition(JInternalFrame newFrame, java.util.List<JInternalFrame> frames);
}
