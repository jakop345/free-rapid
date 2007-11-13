package net.wordrider.dialogs.pictures.filters;

/**
 * @author Vity
 */
public class JarvisErrorDiffusion extends DitherRaster {
    protected void performEffect() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int actualIndex = y * width + x;
                int i1 = pixels[actualIndex];
                pixels[actualIndex] = pixels[actualIndex] <= threshold ? 0 : 255;
                int m = i1 - pixels[actualIndex];
                int i = (m * 7 >> 4) / 3;
                int j = (m * 5 >> 4) / 3;
                int k = m >> 4;
                int l = k / 3;
                if (x < width - 1)
                    pixels[actualIndex + 1] += i;
                if (x < width - 2)
                    pixels[actualIndex + 2] += j;
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
