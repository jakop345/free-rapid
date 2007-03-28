package net.wordrider.dialogs.pictures.filters;

/**
 * @author Vity
 */

import java.awt.image.ColorModel;

public abstract class DitherRaster extends EffectFilter {
    final int[] quantize = new int[256];
    int threshold = 128;

//    /**
//     * This constructor creates an uninitialized
//     * Raster Object of a given size (w x h).
//     */
//    public DitherRaster(int w, int h)
//    {
//        super();
//        width = w;
//        height = h;
//        pixels = new int[w * h];
//    }

    public final void setDimensions(final int w, final int h) {
        super.setDimensions(w, h);
        init();
    }

    public final void setPixels(final int x, final int y, final int w, int h,
                                final ColorModel model, final byte[] pixels, int offset, final int scansize) {
        int i;
        int j;
        final int delta;
        i = y * this.width + x;
        delta = this.width - w;
        while (h > 0) {
            for (j = 0; j < w; j++) {
                this.pixels[i++] = getGrayColor(model.getRGB(pixels[offset + j] & 255));
            }
            for (j = 0; j < delta; ++j) {
                this.pixels[i++] = 0xFF;
            }
            offset += scansize;
            //i += delta;
            --h;
        }
        //        // Process every row in the source array
        //        for (int i = 0; i < height; i++) {
        //            // Shortcuts to save some computation time
        //            int destLineOffset = (y + i) * width;
        //            int srcLineOffset = i * scansize + offset;
        //
        //            // Process every pixel in the row
        //            for (int j = 0; j < width; j++) {
        //
        //                // Get the pixel value, make sure it is unsigned (the &0xff does this)
        //                int pixel = pixels[srcLineOffset + j] & 0xff;
        //
        //                // Get the RGB value
        //                this.pixels[destLineOffset + x + j] = model.getRGB(pixel);
        //            }
        //        }
    }

    public final void setPixels(final int x, final int y, final int w, int h, final ColorModel cm, final int[] p, int offset, final int stride) {
        int i;
        int j;
        final int delta;
        i = y * width + x;
        delta = width - w;
        while (h > 0) {
            for (j = 0; j < w; j++) {
                pixels[i++] = getGrayColor(cm.getRGB(p[offset + j]));
            }
            for (j = 0; j < delta; ++j) {
                this.pixels[i++] = 0xFF;
            }
            offset += stride;
            //i += delta;
            --h;
        }
    }

    private static int getGrayColor(final int pix) {
        //final int a = (pix >> 24) & 0xff;
        //        int r = (pix >> 16) & 255;
        //        int g = (pix >> 8) & 255;
        //        int b = pix & 255;
        //        int gray = (int) (0.2989*r + 0.5870*g + 0.1140*b);  // NTSC formula
        //  System.out.println("i am here: a :" + a);
//        if (a != 0xFF) {
//            return 0xFF;
//        } else
        return ((((pix & 0xff0000) >> 16) +
                ((pix & 0x00ff00) >> 8) +
                (pix & 0x0000ff)) / 3) & 0xFF;
        //return gray;
        //return (((a & 0xFF) << 24) | ((gray & 0xFF) << 16) | ((gray & 0xFF) << 8) | ((gray & 0xFF)));
        // return ((pix & 0xff000000) + 0x010101 * gray);
    }

    /**
     * Converts Rasters to Images
     */
    //    public final Image toImage(Component root)
    //    {
    //        return root.createImage(new MemoryImageSource(width, height, pixel, 0, width));
    //    }
    void init() {
        this.setQuantLevels(2);
    }

    /**
     * Gets a color from a Raster
     */
//    protected final Color getColor(int x, int y)
//    {
//        return new Color(pixels[y * width + x]);
//    }
    void setQuantLevels(final int levels) {

        threshold = (510 + levels) / (2 * levels);
        for (int i = 0; i < 256; i++) {
            quantize[i] = Math.round(255 * Math.round(i * (levels - 1) / 255.0) / (levels - 1));
        }
    }

    int getNoiseyPixel(final int x, final int y, final int actualPixel) {
        return 0;
    }

    protected void performEffect() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                final int pix = getNoiseyPixel(x, y, pixels[y * width + x]);
                //  if ((pix & 0xff000000) != 0)
                this.setPixel(x, y, pix);
            }
        }
    }

    void quantize() {
        int counter = 0;
        //    boolean flag = false;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                //   int r = (pixels[l] >> 16) & 0xff;
//                pixels[counter++] = quantize[pixels[counter]];
                if (pixels[counter] > threshold)
                    pixels[counter++] = 0xFFFFFFFF;
                else
                    pixels[counter++] = 0xFF000000;
            }
        }
    }
    //    /**
    //     *  Sets a pixel to a given color
    //     */
    //     protected final boolean setColor(Color c, int x, int y)
    //     {
    //        pixel[y*width+x] = c.getRGB();
    //        return true;
    //     }
}
