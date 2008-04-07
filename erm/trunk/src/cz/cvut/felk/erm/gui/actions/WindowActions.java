package cz.cvut.felk.erm.gui.actions;

import cz.cvut.felk.erm.core.MainApp;
import org.jdesktop.application.Action;

import javax.swing.*;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Ladislav Vitasek
 */

public class WindowActions {
    private final static Logger logger = Logger.getLogger(WindowActions.class.getName());

    private MainApp app;


    public WindowActions() {
        app = MainApp.getInstance(MainApp.class);
    }

    @Action
    public void tile() {
        doTile();
    }

    @Action
    public void tileHorizontal() {
        tileHorizontally();
    }

    @Action
    public void tileVertical() {
        tileVertically();
    }

    @Action
    public void restore() {
        final JDesktopPane desktop = this.getDesktop();
        final JInternalFrame selectedFrame = desktop.getSelectedFrame();
        if (selectedFrame == null)
            return;
        try {
            if (desktop.getSelectedFrame().isIcon()) {
                desktop.getSelectedFrame().setIcon(false);
            } else if (desktop.getSelectedFrame().isMaximum()) {
                desktop.getSelectedFrame().setMaximum(false);
            }
        } catch (PropertyVetoException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Action
    public void cascade() {
        doCascade();
    }

    @Action
    public void restoreAll() {
        for (JInternalFrame frame : this.getDesktop().getAllFrames()) {
            try {
                if (frame.isIcon()) {
                    frame.setIcon(false);
                } else if (frame.isMaximum()) {
                    frame.setMaximum(false);
                }
            } catch (PropertyVetoException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    @Action
    public void minimize() {
        final JDesktopPane desktop = this.getDesktop();
        final JInternalFrame selectedFrame = desktop.getSelectedFrame();
        if (selectedFrame == null)
            return;
        if (!desktop.getSelectedFrame().isIcon()) {
            try {
                selectedFrame.setIcon(true);
            } catch (PropertyVetoException ex) {
                throw new RuntimeException(ex);
            }
        }
    }


    @Action
    public void minimizeAll() {
        for (JInternalFrame frame : this.getDesktop().getAllFrames()) {
            if (!frame.isIcon()) {
                try {
                    frame.setIcon(true);
                } catch (PropertyVetoException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
    }

    @Action
    public void maximize() {
        try {
            final JInternalFrame selectedFrame = this.getDesktop().getSelectedFrame();
            if (selectedFrame == null)
                return;
            selectedFrame.setMaximum(true);
        } catch (PropertyVetoException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Action
    public void maximizeAll() {
        for (JInternalFrame frame : this.getDesktop().getAllFrames()) {
            try {
                frame.setMaximum(true);
            } catch (PropertyVetoException ex) {
                throw new RuntimeException(ex);
            }
        }
    }


    //****************************-------------
    /**
     * Change the bounds of visible windows to tile them checkerboard-style on the desktop.
     */
    private void doTile() {
        List<JInternalFrame> frames = getAllVisibleFrames();
        if (frames.isEmpty()) {
            return;
        }

        double sqrt = Math.sqrt(frames.size());
        int numCols = (int) Math.floor(sqrt);
        int numRows = numCols;
        if ((numCols * numRows) < frames.size()) {
            numCols++;
            if ((numCols * numRows) < frames.size()) {
                numRows++;
            }
        }

        int newWidth = getDesktop().getWidth() / numCols;
        int newHeight = getDesktop().getHeight() / numRows;

        int y = 0;
        int x = 0;
        int frameIdx = 0;
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                if (frameIdx < frames.size()) {
                    JInternalFrame frame = frames.get(frameIdx++);
                    if (frame.isMaximum()) {
                        try {
                            frame.setMaximum(false);
                        } catch (PropertyVetoException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                    frame.reshape(x, y, newWidth, newHeight);
                    x += newWidth;
                }
            }
            x = 0;
            y += newHeight;
        }
    }

    /**
     * Change the bounds of visible windows to cascade them down from the top left of the desktop.
     */
    private void doCascade() {
        List<JInternalFrame> frames = getAllVisibleFrames();
        if (frames.isEmpty()) {
            return;
        }

        int newWidth = (int) (getDesktop().getWidth() * 0.6);
        int newHeight = (int) (getDesktop().getHeight() * 0.6);
        int x = 0;
        int y = 0;
        for (JInternalFrame frame : frames) {
            if (frame.isMaximum()) {
                try {
                    frame.setMaximum(false);
                } catch (PropertyVetoException ex) {
                    throw new RuntimeException(ex);
                }
            }
            frame.reshape(x, y, newWidth, newHeight);
            x += 25;
            y += 25;

            if ((x + newWidth) > getDesktop().getWidth()) {
                x = 0;
            }

            if ((y + newHeight) > getDesktop().getHeight()) {
                y = 0;
            }
        }
    }

    /**
     * @return A list of frames on the desktop which are not iconified and are visible.
     */
    private List<JInternalFrame> getAllVisibleFrames() {
        List<JInternalFrame> frames = new ArrayList<JInternalFrame>();
        for (JInternalFrame frame : getDesktop().getAllFrames()) {
            if (frame.isVisible() && !frame.isClosed() && !frame.isIcon()) {
                frames.add(frame);
            }
        }
        Collections.sort(frames, new FrameComparator());
        return frames;
    }

    /**
     * Change the bounds of visible windows to tile them vertically on the desktop.
     */
    private void tileVertically() {
        java.util.List<JInternalFrame> frames = getAllVisibleFrames();
        if (frames.isEmpty())
            return;
        int newWidth = getDesktop().getWidth() / frames.size();
        int newHeight = getDesktop().getHeight();

        int x = 0;
        for (JInternalFrame frame : frames) {
            if (frame.isMaximum()) {
                try {
                    frame.setMaximum(false); // Restore if maximized first
                } catch (PropertyVetoException ex) {
                    throw new RuntimeException(ex);
                }
            }
            frame.reshape(x, 0, newWidth, newHeight);
            x += newWidth;
        }
    }

    /**
     * Change the bounds of visible windows to tile them horizontally on the desktop.
     */
    private void tileHorizontally() {
        List<JInternalFrame> frames = getAllVisibleFrames();
        if (frames.isEmpty())
            return;
        int newWidth = getDesktop().getWidth();
        int newHeight = getDesktop().getHeight() / frames.size();

        int y = 0;
        for (JInternalFrame frame : frames) {
            if (frame.isMaximum()) {
                try {
                    frame.setMaximum(false); // Restore if maximized first
                } catch (PropertyVetoException ex) {
                    throw new RuntimeException(ex);
                }
            }
            frame.reshape(0, y, newWidth, newHeight);
            y += newHeight;
        }
    }

    /**
     * Used for sorting the frames in alphabetical order by title
     */
    public static class FrameComparator implements Comparator<JInternalFrame> {
        public int compare(JInternalFrame o1, JInternalFrame o2) {
            int ret = 0;
            if (o1 != null && o2 != null) {
                String t1 = o1.getTitle();
                String t2 = o2.getTitle();

                if (t1 != null && t2 != null) {
                    ret = t1.compareTo(t2);
                } else if (t1 == null && t2 != null) {
                    ret = -1;
                } else if (t1 != null) {
                    ret = 1;
                } else {
                    ret = 0;
                }
            }
            return (ret);
        }
    }

    private JDesktopPane getDesktop() {
        return app.getManagerDirector().getContentPane();
    }


}
