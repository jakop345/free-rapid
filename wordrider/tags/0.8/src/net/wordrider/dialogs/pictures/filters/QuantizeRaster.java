package net.wordrider.dialogs.pictures.filters;

/**
 * @author Vity
 */
public final class QuantizeRaster extends DitherRaster {
    public final int getNoiseyPixel(final int i, final int j, final int actualPixel) {
//        final int actualPixel = getPixel(i, j);
////        int l = k & 0xff000000;
////        int i1 = k >> 16 & 0xff;
////        int j1 = k >> 8 & 0xff;
////        int k1 = k & 0xff;
////        i1 = quantize[i1] << 16;
////        j1 = quantize[j1] << 8;
////        k1 = quantize[k1];
//        return l | i1 | j1 | k1;
        final int b = quantize[actualPixel];
        return (0xff000000 | b << 16 | b << 8 | b);
    }
}
