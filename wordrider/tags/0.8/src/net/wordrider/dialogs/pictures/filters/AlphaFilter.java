package net.wordrider.dialogs.pictures.filters;

import java.awt.image.RGBImageFilter;

/**
 * @author Vity
 */
public final class AlphaFilter extends RGBImageFilter {
    public AlphaFilter() {
        super();
        this.canFilterIndexColorModel = true;
    }

    public final int filterRGB(final int x, final int y, final int rgb) {
        if ((rgb & 0xFF) > 0 || rgb == 0)
            return 0x00FFFFFF;
        else
            return rgb;
        //return (rgb << 24) ^ rgb;
        // return rgb;
        //return (rgb & 0x00FFFFFF) | (((rgb & 0xff) ^ 0xFF) << 16);
    }
}
