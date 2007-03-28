package net.wordrider.dialogs.pictures.filters;

/**
 * @author Vity
 */
public class FloydSteinbergErrorDiffusion extends DitherRaster {
    protected void performEffect() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixelIndex = y * width + x;
                int pixel = pixels[pixelIndex];
                pixels[pixelIndex] = pixels[pixelIndex] <= threshold ? 0 : 255;
                int k1 = pixel - pixels[pixelIndex];
                int l2 = k1 >> 1;
                int l1 = 7 * l2 >> 3;
                int k2 = l2 - l1;
                l2 = k1 - l2;
                int i2 = 5 * l2 >> 3;
                int j2 = l2 - i2;
                if (x < width - 1)
                    pixels[pixelIndex + 1] += l1;
                if (y < height - 1) {
                    int i3 = (y + 1) * width + x;
                    if (x > 0)
                        pixels[i3 - 1] += j2;
                    pixels[i3] += i2;
                    if (x < width - 1)
                        pixels[i3 + 1] += k2;
                }
            }

        }
        quantize();
    }
}
