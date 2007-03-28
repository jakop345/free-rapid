package net.wordrider.area;

import javax.swing.*;
import java.awt.*;
import java.awt.event.AWTEventListener;
import java.awt.event.MouseEvent;

/**
 * Created by Santhosh Kumar http://www.jroller.com/page/santhosh?entry=enhanced_scrolling_in_swing
 */
final class ScrollGestureRecognizer implements AWTEventListener {
    private static final ScrollGestureRecognizer instance = new ScrollGestureRecognizer();

    private ScrollGestureRecognizer() {
        start();
    }

    public static ScrollGestureRecognizer getInstance() {
        return instance;
    }

    final void start() {
        Toolkit.getDefaultToolkit().addAWTEventListener(this, AWTEvent.MOUSE_EVENT_MASK);
    }

    final void stop() {
        Toolkit.getDefaultToolkit().removeAWTEventListener(this);
    }

    public final void eventDispatched(AWTEvent event) {
        MouseEvent me = (MouseEvent) event;
        boolean isGesture = SwingUtilities.isMiddleMouseButton(me) && me.getID() == MouseEvent.MOUSE_PRESSED;
        if (!isGesture)
            return;

        JViewport viewPort = (JViewport) SwingUtilities.getAncestorOfClass(JViewport.class, me.getComponent());
        if (viewPort == null)
            return;
        JRootPane rootPane = SwingUtilities.getRootPane(viewPort);
        if (rootPane == null)
            return;

        Point location = SwingUtilities.convertPoint(me.getComponent(), me.getPoint(), rootPane.getGlassPane());
        ScrollGlassPane glassPane = new ScrollGlassPane(rootPane.getGlassPane(), viewPort, location);
        rootPane.setGlassPane(glassPane);
        rootPane.setCursor(Cursor.getDefaultCursor());
        glassPane.setVisible(true);
    }
}