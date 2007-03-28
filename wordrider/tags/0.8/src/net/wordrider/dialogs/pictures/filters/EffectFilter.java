package net.wordrider.dialogs.pictures.filters;

import java.awt.image.ColorModel;
import java.awt.image.ImageConsumer;
import java.awt.image.ImageFilter;

/**
 * @author Vity
 */
abstract class EffectFilter extends ImageFilter {
    // Storage area for image info
    int width = 0;
    int height = 0;
    int[] pixels;

    public EffectFilter() {
    }

    // Filter the COMPLETESCANLINES hint out of the hints. You know you won't be
    // presenting complete scan lines.

    public final void setHints(final int hints) {
        // Set new hints, but preserve SINGLEFRAME setting
        consumer.setHints(TOPDOWNLEFTRIGHT | COMPLETESCANLINES |
                SINGLEPASS | (hints & SINGLEFRAME));
    }

    // When you find out the dimensions of the image, you can create the holding
    // area for the pixels.

    public void setDimensions(final int width, final int height) {
        this.width = width;
        this.height = height;
        this.pixels = new int[width * height];

        consumer.setDimensions(width, height);
    }

    // An image filter has two different versions of setPixels. This one
    // takes an array of bytes as the pixel values. This implies that the
    // color model is an indexed color model. Because this filter needs pixels
    // in RGB format, you just get the RGB value from the color model and put
    // it into our array of pixels.

    public void setPixels(final int x, final int y, final int width, final int height,
                          final ColorModel model, final byte[] pixels, final int offset, final int scansize) {

        // Process every row in the source array
        for (int i = 0; i < height; i++) {

            // Shortcuts to save some computation time
            final int destLineOffset = (y + i) * width;
            final int srcLineOffset = i * scansize + offset;

            // Process every pixel in the row
            for (int j = 0; j < width; j++) {

                // Get the pixel value, make sure it is unsigned (the &0xff does this)
                final int pixel = pixels[srcLineOffset + j] & 0xff;

                // Get the RGB value
                this.pixels[destLineOffset + x + j] =
                        model.getRGB(pixel);
            }
        }
    }

    // You don't actually know if the color model here is the RGB color
    // model or not, so just treat it like it might be an indexed model.

    public void setPixels(final int x, final int y, final int width, final int height,
                          final ColorModel model, final int[] pixels, final int offset, final int scansize) {
        // Process every row in the source array
        for (int i = 0; i < height; i++) {

            // Shortcuts to save some computation time
            final int destLineOffset = (y + i) * width;
            final int srcLineOffset = i * scansize + offset;

            // Process every pixel in the row
            for (int j = 0; j < width; j++) {

                // Get the pixel value, make sure it is unsigned (the &0xff does this)
                final int pixel = pixels[srcLineOffset + j];

                // Get the RGB value
                this.pixels[destLineOffset + x + j] =
                        model.getRGB(pixel);
            }
        }
    }

    // When the image producer is finished sending us pixels it calls
    // imageComplete. You take this opportunity to perform the effect
    // and then send all the pixels to our consumer before passing on
    // the imageComplete call to the consumer. Up to this point the consumer
    // doesn't know anything about our pixels. It's about to learn!

    public final void imageComplete(final int status) {
        if ((status == IMAGEABORTED) || (status == IMAGEERROR)) {
            consumer.imageComplete(status);
        } else {
            performEffect();
            //            for (int j = 0, offset = 0; j < height; ++j) {
            //                consumer.setPixels(0, j, width, 1, cm, pixels, offset, width);
            //                offset += width;
            //            }
            //            consumer.setPixels(x, y, w, h, cm, pixel, offset, stride);
            deliverPixels();
            consumer.imageComplete(ImageConsumer.STATICIMAGEDONE);
        }
    }

    protected abstract void performEffect();

    // deliverPixels sends the whole array of pixels to the consumer in one shot

    private void deliverPixels() {
        consumer.setPixels(0, 0, this.width, this.height,
                ColorModel.getRGBdefault(),
                this.pixels, 0, this.width);
    }

    /**
     * Gets a pixel from a Raster
     */
//    final int getPixel(final int x, final int y)
//    {
//        return pixels[y * width + x];
//    }

    /**
     * Sets a pixel to a given value
     */
    final void setPixel(final int x, final int y, final int pix) {
        pixels[y * width + x] = pix;
    }
}