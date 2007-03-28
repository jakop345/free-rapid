package net.wordrider.dialogs;


import net.wordrider.utilities.Swinger;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.logging.Logger;

final class ImagePreview extends JComponent implements PropertyChangeListener {
    private ImageIcon thumbnail = null;
    private File file = null;
    private final static Logger logger = Logger.getLogger(ImagePreview.class.getName());

    ImagePreview(final JFileChooser fc) {
        setPreferredSize(new Dimension(100, 50));
        fc.addPropertyChangeListener(this);
    }

    private void loadImage() {
        freeResources();
        if (file == null) {
            return;
        }
        final Image tmpIcon;
        try {
            tmpIcon = Swinger.loadPicture(file);
        } catch (Exception e) {
            logger.warning("Couldn't read the image file for preview :" + file.getPath());
            return;
        }
        //Don't use createImageIcon (which is a wrapper for getResource)
        //because the image we're trying to load is probably not one
        //of this program's own resources.
        //ImageIcon tmpIcon = new ImageIcon(file.getPath());
        if (tmpIcon != null)
            thumbnail = new ImageIcon((tmpIcon.getWidth(this) > 90) ? tmpIcon.getScaledInstance(90, -1, Image.SCALE_FAST) : tmpIcon);
    }

    public final void propertyChange(final PropertyChangeEvent e) {
        boolean update = false;
        final String prop = e.getPropertyName();
        //If the directory changed, don't show an image.
        if (JFileChooser.DIRECTORY_CHANGED_PROPERTY.equals(prop)) {
            file = null;
            update = true;

            //If a file became selected, find out which one.
        } else if (JFileChooser.SELECTED_FILE_CHANGED_PROPERTY.equals(prop)) {
            file = (File) e.getNewValue();
            update = true;
        }

        //Update the preview accordingly.
        if (update) {
            freeResources();
            if (isShowing()) {
                loadImage();
                repaint();
            }
        }
    }

    protected final void paintComponent(final Graphics g) {
        if (thumbnail == null)
            loadImage();

        if (thumbnail != null) {
            int x = getWidth() / 2 - thumbnail.getIconWidth() / 2;
            int y = getHeight() / 2 - thumbnail.getIconHeight() / 2;
            if (y < 0)
                y = 0;
            if (x < 5)
                x = 5;
            thumbnail.paintIcon(this, g, x, y);
        }
    }

    void freeResources() {
        if (thumbnail != null) {
            thumbnail.getImage().flush();
            thumbnail = null;
        }
    }
}
