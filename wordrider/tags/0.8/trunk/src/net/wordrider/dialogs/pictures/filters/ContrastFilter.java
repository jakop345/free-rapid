package net.wordrider.dialogs.pictures.filters;

import java.awt.image.RGBImageFilter;

/**
 * @author Vity
 */
public final class ContrastFilter extends RGBImageFilter {
    private float gain = 1;

    public ContrastFilter() {
        super();    //call to super
        canFilterIndexColorModel = true;
    }

    public final void setGain(final int value) {
        this.gain = (float) ((value + 100) / 100.0);
    }

    private int cont(final int in) {
        return (int) ((in < 128) ? (in / gain) : in * gain);
//        if (in < 128)
//            return (int) ((gain > 1) ? in / gain : in * gain);
//        else
//            return (int) ((gain > 1) ? in * gain : in / gain);
    }

    public final int filterRGB(final int x, final int y, final int rgb) {
        int r = cont((rgb >> 16) & 0xff);
        int g = cont((rgb >> 8) & 0xff);
        int b = cont(rgb & 0xff);
        r = Math.min(Math.max(0, r), 255);
        g = Math.min(Math.max(0, g), 255);
        b = Math.min(Math.max(0, b), 255);
        return (rgb & 0xff000000 | r << 16 | g << 8 | b);
    }
}

