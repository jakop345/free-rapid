package net.wordrider.dialogs.pictures.filters;

/**
 * @author Vity
 */
public class BurkeFilter extends DitherRaster {

    protected void performEffect() {

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                final int actualIndex = y * width + x;
                final int pixel = pixels[actualIndex];
                pixels[actualIndex] = pixels[actualIndex] <= threshold ? 0 : 255;
                final int k = pixel - pixels[actualIndex];
                final int l = k >> 2;
                final int i = k >> 3;
                final int j = k >> 4;
                if (x < width - 1)
                    pixels[actualIndex + 1] += l;
                if (x < width - 2)
                    pixels[actualIndex + 2] += i;
                if (y < height - 1) {
                    int k2 = (y + 1) * width + x;
                    if (x > 1)
                        pixels[k2 - 2] += j;
                    if (x > 0)
                        pixels[k2 - 1] += i;
                    pixels[k2] += l;
                    if (x < width - 1)
                        pixels[k2 + 1] += i;
                    if (x < width - 2)
                        pixels[k2 + 2] += j;
                }
            }

        }
        quantize();
    }

}
