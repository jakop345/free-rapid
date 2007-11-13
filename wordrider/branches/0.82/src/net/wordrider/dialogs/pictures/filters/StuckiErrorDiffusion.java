package net.wordrider.dialogs.pictures.filters;

/**
 * @author Vity
 */
public class StuckiErrorDiffusion extends DitherRaster {
    protected void performEffect() {

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                final int index = y * width + x;
                final int pixel = pixels[index];
                pixels[index] = pixels[index] <= threshold ? 0 : 255;
                final int k1 = pixel - pixels[index];
                final int i = (k1 << 2) / 21;
                final int j = i >> 1;
                final int k = i >> 2;
                final int l = i >> 3;
                if (x < width - 1)
                    pixels[index + 1] += i;
                if (x < width - 2)
                    pixels[index + 2] += j;
                if (y < height - 1) {
                    int l2 = (y + 1) * width + x;
                    if (x > 1)
                        pixels[l2 - 2] += k;
                    if (x > 0)
                        pixels[l2 - 1] += j;
                    pixels[l2] += i;
                    if (x < width - 1)
                        pixels[l2 + 1] += j;
                    if (x < width - 2)
                        pixels[l2 + 2] += k;
                }
                if (y < height - 2) {
                    int i3 = (y + 2) * width + x;
                    if (x > 1)
                        pixels[i3 - 2] += l;
                    if (x > 0)
                        pixels[i3 - 1] += k;
                    pixels[i3] += j;
                    if (x < width - 1)
                        pixels[i3 + 1] += k;
                    if (x < width - 2)
                        pixels[i3 + 2] += l;
                }
            }

        }
        quantize();
    }
}
