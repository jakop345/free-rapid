package net.wordrider.dialogs.pictures.filters;

/**
 * @author Vity
 */
public class Shiau1ErrorDiffusion extends DitherRaster {
    protected void performEffect() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                final int actualIndex = y * width + x;
                final int pixel = pixels[actualIndex];
                pixels[actualIndex] = pixels[actualIndex] <= threshold ? 0 : 255;
                int k1 = pixel - pixels[actualIndex];
                if (x < width - 1)
                    pixels[actualIndex + 1] += k1 >> 1;
                if (y < height - 1) {
                    int l1 = k1 >> 4;
                    int i2 = (y + 1) * width + x;
                    if (x > 0)
                        pixels[i2 - 1] += k1 >> 3;
                    if (x - 1 > 0)
                        pixels[i2 - 2] += l1;
                    if (x - 2 > 0)
                        pixels[i2 - 3] += l1;
                    pixels[i2] += k1 >> 2;
                }
            }
        }
        quantize();
    }
}
