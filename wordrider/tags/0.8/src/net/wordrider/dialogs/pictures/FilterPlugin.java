package net.wordrider.dialogs.pictures;

import java.awt.*;

/**
 * @author Vity
 */
abstract class FilterPlugin {
    Image generatedImage;

    public final Image getGeneratedImage() {
        return generatedImage;
    }

    final void freeActualImage() {
        if (generatedImage != null) {
            generatedImage.flush();
            generatedImage = null;
        }
    }

    public abstract Image updateFilter(Image inputImage, final int source);

}
