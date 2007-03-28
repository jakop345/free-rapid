package net.wordrider.area;

import net.wordrider.utilities.Swinger;

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

/**
 * Created by Santhosh Kumar http://www.jroller.com/page/santhosh?entry=enhanced_scrolling_in_swing
 */

final class ScrollGlassPane extends JComponent implements ActionListener, MouseInputListener, SwingConstants {
    private static final Image img = Swinger.getIconImage("mouse.gif");

    private Component oldGlassPane = null;
    private Point location = null;

    private final Timer movingTimer;
    private Point mouseLocation;
    private final JViewport viewport;
    private final Rectangle imageRect;

    public ScrollGlassPane(Component oldGlassPane, JViewport viewport, Point location) {
        this.oldGlassPane = oldGlassPane;
        this.viewport = viewport;
        this.location = mouseLocation = location;

        setOpaque(false);
        ScrollGestureRecognizer.getInstance().stop();
        addMouseListener(this);
        this.setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
        addMouseMotionListener(this);
        imageRect = new Rectangle(location.x - 15, location.y - 15, img.getWidth(null), img.getHeight(null));
        movingTimer = new Timer(100, this);
        movingTimer.start();
    }

    protected final void paintComponent(Graphics g) {
//        g.setXORMode(Color.BLACK);
//        g.setColor(Color.white);
//        g.drawLine(location.x - 15, location.y - 15,location.x - 55, location.y - 55);
        g.drawImage(img, location.x - 15, location.y - 15, this);
        //g.drawImage(img, getWidth() - 15, getHeight() - 15, this);
    }

    /*-------------------------------------------------[ ActionListener ]---------------------------------------------------*/

    public final void actionPerformed(ActionEvent e) {
        int deltax = (mouseLocation.x - location.x) / 4;
        int deltay = (mouseLocation.y - location.y) / 4;


        Point p = viewport.getViewPosition();
        p.translate(deltax, deltay);

        if (p.x < 0)
            p.x = 0;
        else if (p.x >= viewport.getView().getWidth() - viewport.getWidth())
            p.x = viewport.getView().getWidth() - viewport.getWidth();

        if (p.y < 0)
            p.y = 0;
        else if (p.y >= viewport.getView().getHeight() - viewport.getHeight())
            p.y = viewport.getView().getHeight() - viewport.getHeight();

        viewport.setViewPosition(p);
    }

    /*-------------------------------------------------[ MouseListener ]---------------------------------------------------*/

    public final void mousePressed(MouseEvent e) {
        movingTimer.stop();
        setVisible(false);
        JRootPane rootPane = SwingUtilities.getRootPane(this);
        rootPane.setGlassPane(oldGlassPane);
        ScrollGestureRecognizer.getInstance().start();
        //     System.out.println("bounds" + this.getBounds());
    }

    public final void mouseClicked(MouseEvent e) {
        setVisible(false);
        JRootPane rootPane = SwingUtilities.getRootPane(this);
        rootPane.setGlassPane(oldGlassPane);
        ScrollGestureRecognizer.getInstance().start();
    }

    public final void mouseMoved(MouseEvent e) {
        mouseLocation = e.getPoint();
        if (imageRect.contains(mouseLocation)) {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
        } else if (this.getCursor().getType() != Cursor.DEFAULT_CURSOR)
            this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    public final void mouseDragged(MouseEvent e) {
    }

    public final void mouseEntered(MouseEvent e) {
    }

    public final void mouseExited(MouseEvent e) {
    }

    public final void mouseReleased(MouseEvent e) {
    }
}
