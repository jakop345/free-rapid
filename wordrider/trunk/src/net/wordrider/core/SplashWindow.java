package net.wordrider.core;

/**
 * @author Vity
 */

import java.awt.*;

final class SplashWindow extends Window {
    private final Image splashImage;

    /**
     * This attribute is set to true when method paint(Graphics) has been called at least once since the construction of
     * this window.
     */
    private boolean paintCalled = false;

    /**
     * Constructs a splash window and centers it on the screen.
     * @param owner       The frame owning the splash window.
     * @param splashImage The splashImage to be displayed.
     */
    private SplashWindow(final Frame owner, final Image splashImage) {
        super(owner);
        this.splashImage = splashImage;

        // Load the image
        final MediaTracker mt = new MediaTracker(this);
        mt.addImage(splashImage, 0);
        try {
            mt.waitForID(0);
        } catch (InterruptedException ie) {
            //
        }

        //Swinger.centerDialog(owner, this);
        // Center the window on the screen.
        final int imgWidth = splashImage.getWidth(this);
        final int imgHeight = splashImage.getHeight(this);

        setSize(imgWidth, imgHeight);
        final Dimension screenDim =

                Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((screenDim.width - imgWidth) / 2,
                (screenDim.height - imgHeight) / 2);
    }

    /**
     * Updates the display area of the window.
     */
    public final void update(final Graphics g) {
        // Note: Since the paint method is going to draw an
        // image that covers the complete net.wordrider.area of the component we
        // do not fill the component with its background color
        // here. This avoids flickering.

        g.setColor(getForeground());
        paint(g);
    }

    /**
     * Paints the image on the window.
     */
    public final void paint(final Graphics g) {
        g.drawImage(splashImage, 0, 0, this);
        // Notify method splash that the window
        // has been painted.
        if (!paintCalled) {
            paintCalled = true;
            synchronized (this) {
                notifyAll();
            }
        }
    }

    /**
     * Constructs and displays a SplashWindow.<p> This method is useful for startup splashs. Dispose the returned frame
     * to get rid of the splash window.<p>
     * @param splashImage The image to be displayed.
     * @return Returns the frame that owns the SplashWindow.
     */
    public static Frame splash(final Image splashImage) {
        final Frame f = new Frame();
        final SplashWindow w = new SplashWindow(f, splashImage);
        // Show the window.
        w.toFront();
        w.setVisible(true);

        // Note: To make sure the user gets a chance to see the
        // splash window we wait until its paint method has been
        // called at least once by the AWT event dispatcher thread.
        if (!EventQueue.isDispatchThread()) {
            synchronized (w) {
                //while (!w.paintCalled) {
                    try {
                        w.wait(3000);                                               
                    } catch (InterruptedException e) {
                        //
                    }
                //}
            }
        }        
        return f;
    }
}