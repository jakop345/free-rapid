package net.wordrider.dialogs.pictures.filters;

/**
 * @author Vity
 */
public final class OrderedDither extends DitherRaster {
    private final int[] pattern = new int[16];

    public final void setQuantLevels(final int levels) {
        super.setQuantLevels(levels);
        initPattern();
    }

    private void initPattern() {
        pattern[0] = (15 * threshold) / 32;
        pattern[1] = (-1 * threshold) / 32;
        pattern[2] = (11 * threshold) / 32;
        pattern[3] = (-5 * threshold) / 32;
        pattern[4] = (-9 * threshold) / 32;
        pattern[5] = (7 * threshold) / 32;
        pattern[6] = (-13 * threshold) / 32;
        pattern[7] = (3 * threshold) / 32;
        pattern[8] = (9 * threshold) / 32;
        pattern[9] = (-7 * threshold) / 32;
        pattern[10] = (13 * threshold) / 32;
        pattern[11] = (-3 * threshold) / 32;
        pattern[12] = (-15 * threshold) / 32;
        pattern[13] = (threshold) / 32;//1 * quantize
        pattern[14] = (-11 * threshold) / 32;
        pattern[15] = (5 * threshold) / 32;
    }

    public final int getNoiseyPixel(final int x, final int y, int actualPixel) {
        //   final int a = pix & 0xff000000;
        //    int b = pix & 255;
//        int g = (pix >> 8) & 255;
//        int b = pix & 255;
        //   System.out.println("pix:" + pix);
        final int i = 4 * (y & 3) + (x & 3);
        actualPixel += pattern[i];
//        g = g + pattern[i];
//        b = b + pattern[i];

        actualPixel = ((actualPixel & ~255) == 0) ? actualPixel : ((actualPixel < 0) ? 0 : 255);
//        g = ((g & ~255) == 0) ? g : ((g < 0) ? 0 : 255);
//        b = ((b & ~255) == 0) ? b : ((b < 0) ? 0 : 255);

        actualPixel = quantize[actualPixel];
//        g = quantize[g] << 8;
//        b = quantize[b];
        return (0xff000000 | actualPixel << 16 | actualPixel << 8 | actualPixel);
//        final int pix = getPixel(x, y);
//        final int a = pix & 0xff000000;
//        int r = (pix >> 16) & 255;
//        int g = (pix >> 8) & 255;
//        int b = pix & 255;
//
//        final int i = 4 * (y & 3) + (x & 3);
//        r = r + pattern[i];
//        g = g + pattern[i];
//        b = b + pattern[i];
//
//        r = ((r & ~255) == 0) ? r : ((r < 0) ? 0 : 255);
//        g = ((g & ~255) == 0) ? g : ((g < 0) ? 0 : 255);
//        b = ((b & ~255) == 0) ? b : ((b < 0) ? 0 : 255);
//
//        r = quantize[r] << 16;
//        g = quantize[g] << 8;
//        b = quantize[b];
//        return (a | r | g | b);
    }
}