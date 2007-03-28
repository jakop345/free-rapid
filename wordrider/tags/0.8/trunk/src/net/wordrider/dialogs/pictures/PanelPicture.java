package net.wordrider.dialogs.pictures;

import net.wordrider.utilities.Swinger;

import javax.swing.*;
import java.awt.*;
import java.awt.image.VolatileImage;
import java.util.logging.Logger;

/**
 * @author Vity
 * @noinspection PointlessArithmeticExpression
 */
final class PicturePanel extends JComponent {
    private Image img = null;
    private final static int DX = 1;
    private final static int DY = 1;
    private boolean lcdScreen = false;
    private Image lcdImageScreen = null;
    private VolatileImage buffer = null;
    private final static Logger logger = Logger.getLogger(PicturePanel.class.getName());

    public PicturePanel() {
        super();
        this.setBackground(Color.lightGray);
        this.setPreferredSize(new Dimension(50, 50));
        this.setBorder(BorderFactory.createLineBorder(SystemColor.controlText, 1));
    }

    /**
     * java.lang.NullPointerException at net.wordrider.dialogs.pictures.PicturePanel.renderOffscreen(PanelPicture.java:34)
     * at net.wordrider.dialogs.pictures.PicturePanel.updateContent(PanelPicture.java:72) at
     * net.wordrider.dialogs.pictures.PicturePanel.setLCDBackground(PanelPicture.java:86) at
     * net.wordrider.dialogs.pictures.FilterDialog$OutputScreenListener.itemStateChanged(FilterDialog.java:166) at
     * javax.swing.AbstractButton.fireItemStateChanged(AbstractButton.java:1877) at
     * javax.swing.AbstractButton$Handler.itemStateChanged(AbstractButton.java:2176) at
     * javax.swing.DefaultButtonModel.fireItemStateChanged(DefaultButtonModel.java:477) at
     * javax.swing.JToggleButton$ToggleButtonModel.setSelected(JToggleButton.java:233) at
     * javax.swing.ButtonGroup.setSelected(ButtonGroup.java:139) at net.wordrider.dialogs.JButtonGroup.setSelected(JButtonGroup.java:125)
     * at javax.swing.JToggleButton$ToggleButtonModel.setSelected(JToggleButton.java:215) at
     * javax.swing.AbstractButton.setSelected(AbstractButton.java:274) at net.wordrider.dialogs.pictures.FilterDialog.initFilters(FilterDialog.java:414)
     * at net.wordrider.dialogs.pictures.FilterDialog.<init>(FilterDialog.java:124) at
     * net.wordrider.area.actions.InsertPictureAction$1.run(InsertPictureAction.java:122) at
     * java.awt.event.InvocationEvent.dispatch(InvocationEvent.java:209) at java.awt.EventQueue.dispatchEvent(EventQueue.java:461)
     * at net.wordrider.core.swing.MouseEventQueue.dispatchEvent(MouseEventQueue.java:16) at
     * java.awt.EventDispatchThread.pumpOneEventForHierarchy(EventDispatchThread.java:242) at
     * java.awt.EventDispatchThread.pumpEventsForHierarchy(EventDispatchThread.java:163) at
     * java.awt.EventDispatchThread.pumpEvents(EventDispatchThread.java:157) at java.awt.EventDispatchThread.pumpEvents(EventDispatchThread.java:149)
     * at java.awt.EventDispatchThread.run(EventDispatchThread.java:110) WordRider 0.76devel Error : null
     * java.lang.NullPointerException at net.wordrider.dialogs.pictures.PicturePanel.renderOffscreen(PanelPicture.java:34)
     * at net.wordrider.dialogs.pictures.PicturePanel.updateContent(PanelPicture.java:72) at
     * net.wordrider.dialogs.pictures.PicturePanel.setLCDBackground(PanelPicture.java:86) at
     * net.wordrider.dialogs.pictures.FilterDialog$OutputScreenListener.itemStateChanged(FilterDialog.java:166) at
     * javax.swing.AbstractButton.fireItemStateChanged(AbstractButton.java:1877) at
     * javax.swing.AbstractButton$Handler.itemStateChanged(AbstractButton.java:2176) at
     * javax.swing.DefaultButtonModel.fireItemStateChanged(DefaultButtonModel.java:477) at
     * javax.swing.JToggleButton$ToggleButtonModel.setSelected(JToggleButton.java:233) at
     * javax.swing.ButtonGroup.setSelected(ButtonGroup.java:139) at net.wordrider.dialogs.JButtonGroup.setSelected(JButtonGroup.java:125)
     * at javax.swing.JToggleButton$ToggleButtonModel.setSelected(JToggleButton.java:215) at
     * javax.swing.AbstractButton.setSelected(AbstractButton.java:274) at net.wordrider.dialogs.pictures.FilterDialog.initFilters(FilterDialog.java:414)
     * at net.wordrider.dialogs.pictures.FilterDialog.<init>(FilterDialog.java:124) at
     * net.wordrider.area.actions.InsertPictureAction$1.run(InsertPictureAction.java:122) at
     * java.awt.event.InvocationEvent.dispatch(InvocationEvent.java:209) at java.awt.EventQueue.dispatchEvent(EventQueue.java:461)
     * at net.wordrider.core.swing.MouseEventQueue.dispatchEvent(MouseEventQueue.java:16) at
     * java.awt.EventDispatchThread.pumpOneEventForHierarchy(EventDispatchThread.java:242) at
     * java.awt.EventDispatchThread.pumpEventsForHierarchy(EventDispatchThread.java:163) at
     * java.awt.EventDispatchThread.pumpEvents(EventDispatchThread.java:157) at java.awt.EventDispatchThread.pumpEvents(EventDispatchThread.java:149)
     * at java.awt.EventDispatchThread.run(EventDispatchThread.java:110) WordRider 0.76devel Error : null
     * java.lang.NullPointerException at net.wordrider.dialogs.pictures.PicturePanel.renderOffscreen(PanelPicture.java:34)
     * at net.wordrider.dialogs.pictures.PicturePanel.updateContent(PanelPicture.java:72) at
     * net.wordrider.dialogs.pictures.PicturePanel.setLCDBackground(PanelPicture.java:86) at
     * net.wordrider.dialogs.pictures.FilterDialog$OutputScreenListener.itemStateChanged(FilterDialog.java:166) at
     * javax.swing.AbstractButton.fireItemStateChanged(AbstractButton.java:1877) at
     * javax.swing.AbstractButton$Handler.itemStateChanged(AbstractButton.java:2176) at
     * javax.swing.DefaultButtonModel.fireItemStateChanged(DefaultButtonModel.java:477) at
     * javax.swing.JToggleButton$ToggleButtonModel.setSelected(JToggleButton.java:233) at
     * javax.swing.ButtonGroup.setSelected(ButtonGroup.java:139) at net.wordrider.dialogs.JButtonGroup.setSelected(JButtonGroup.java:125)
     * at javax.swing.JToggleButton$ToggleButtonModel.setSelected(JToggleButton.java:215) at
     * javax.swing.AbstractButton.setSelected(AbstractButton.java:274) at net.wordrider.dialogs.pictures.FilterDialog.initFilters(FilterDialog.java:414)
     * at net.wordrider.dialogs.pictures.FilterDialog.<init>(FilterDialog.java:124) at
     * net.wordrider.area.actions.InsertPictureAction$1.run(InsertPictureAction.java:122) at
     * java.awt.event.InvocationEvent.dispatch(InvocationEvent.java:209) at java.awt.EventQueue.dispatchEvent(EventQueue.java:461)
     * at net.wordrider.core.swing.MouseEventQueue.dispatchEvent(MouseEventQueue.java:16) at
     * java.awt.EventDispatchThread.pumpOneEventForHierarchy(EventDispatchThread.java:242) at
     * java.awt.EventDispatchThread.pumpEventsForHierarchy(EventDispatchThread.java:163) at
     * java.awt.EventDispatchThread.pumpEvents(EventDispatchThread.java:157) at java.awt.EventDispatchThread.pumpEvents(EventDispatchThread.java:149)
     * at java.awt.EventDispatchThread.run(EventDispatchThread.java:110)
     */
    private void renderOffscreen(boolean create) {
        do {
            final Dimension preferredSize = getPreferredSize();
            if (create || buffer == null ||
                    buffer.validate(getGraphicsConfiguration()) == VolatileImage.IMAGE_INCOMPATIBLE) {
                buffer = createVolatileImage(preferredSize.width - 2 * DX, preferredSize.height - 2 * DY);
                if (buffer == null) {
                    logger.info("Renderoffscreen null");
                    return; //patch;
                }

            }

            final Graphics2D g2 = buffer.createGraphics();
            if (lcdScreen && lcdImageScreen != null) {
                g2.drawImage(lcdImageScreen, 0, 0, null);
                g2.drawImage(img, 0, 0, null);
            } else {
                g2.setColor(Color.LIGHT_GRAY);
                g2.fillRect(0, 0, preferredSize.width - 2 * DX, preferredSize.height - 2 * DY);
                if (img != null)
                    g2.drawImage(img, 0, 0, Color.YELLOW, null);
            }
            g2.dispose();
        } while (buffer.contentsLost());
    }


    public final void paintComponent(final Graphics g) {
        if (isVisible()) {
            do {
                if (buffer == null) {
                    renderOffscreen(true);
                } else {
                    int returnCode = buffer.validate(getGraphicsConfiguration());
                    if (returnCode == VolatileImage.IMAGE_RESTORED) {
                        renderOffscreen(false);
                    } else if (returnCode == VolatileImage.IMAGE_INCOMPATIBLE) {
                        renderOffscreen(true);
                    }
                }
                g.drawImage(buffer, DX, DY, this);
            } while (buffer.contentsLost());
        }
    }

    public final Image getImg() {
        return img;
    }

    private void updateContent() {
        renderOffscreen(buffer == null);
        repaint();
    }

    public final void setLCDBackground(final boolean selected, final boolean ti92format) {
        this.lcdScreen = selected;
        if (selected && lcdImageScreen == null) {
            lcdImageScreen = Swinger.getIconImage((ti92format) ? "lcdscrn2.jpg" : "lcdscrn.png");
            updateContent();
        }
    }

    public final void setImg(final Image img) {
        this.img = img;
        if (img != null)
            updateContent();
    }

}
