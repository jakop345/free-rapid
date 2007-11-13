package net.wordrider.dialogs.pictures.filters;

import java.awt.image.RGBImageFilter;

/**
 * @author Vity
 */
public final class BrightnessFilter extends RGBImageFilter {

    private int brightness = 0;

    public BrightnessFilter() {
        this.canFilterIndexColorModel = true;
    }

    public final void setBrightness(final int brightness) {
        this.brightness = brightness;
    }

    public final int filterRGB(final int x, final int y, final int rgb) {
        // Get the individual colors
        int r = (rgb >> 16) & 0xff;
        int g = (rgb >> 8) & 0xff;
        int b = (rgb) & 0xff;

        // Calculate the brightness
        r += (brightness * r) / 100;
        g += (brightness * g) / 100;
        b += (brightness * b) / 100;

        // Check the boundaries
        r = Math.min(Math.max(0, r), 255);
        g = Math.min(Math.max(0, g), 255);
        b = Math.min(Math.max(0, b), 255);

        // Return the result
        return (rgb & 0xff000000) | (r << 16) | (g << 8) | (b);
    }
}