package net.wordrider.dialogs.pictures.filters;

/**
 * @author Vity
 */

import java.util.Random;

public final class RandomRaster extends DitherRaster {
    private final Random whiteNoise = new Random();
    private int random;
    private int rcount = 0;

    public final int getNoiseyPixel(final int x, final int y, int actualPixel) {
        //   final int a = pix & 0xff000000;
//        int r = (pix >> 16) & 255;
//        int g = (pix >> 8) & 255;
//        int b = pix & 255;
//        int actualPixel = getPixel(x, y);

        if (rcount == 0) {
            random = whiteNoise.nextInt();
            rcount = 4;
        }

        int noise = (random & 255) - 128;
        noise = (threshold * noise) >> 8;
        random >>= 8;
        rcount -= 1;

//        r += noise;
//        g += noise;
        actualPixel += noise;

//        r = ((r & ~255) == 0) ? r : ((r < 0) ? 0 : 255);
//        g = ((g & ~255) == 0) ? g : ((g < 0) ? 0 : 255);
        actualPixel = ((actualPixel & ~255) == 0) ? actualPixel : ((actualPixel < 0) ? 0 : 255);

//        r = quantize[r] << 16;
//        g = quantize[g] << 8;
        actualPixel = quantize[actualPixel];
        return (0xff000000 | actualPixel << 16 | actualPixel << 8 | actualPixel);
    }
}