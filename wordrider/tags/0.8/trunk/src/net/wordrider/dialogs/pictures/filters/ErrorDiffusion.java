package net.wordrider.dialogs.pictures.filters;

/**
 * @author Vity
 */

public final class ErrorDiffusion extends DitherRaster {
//    private int[][] rerror;
    //    private int[][] gerror;
    private int[][] berror;

    public final void setQuantLevels(final int levels) {
        super.setQuantLevels(levels);
        zeroError();
    }

    protected final void init() {
//        rerror = new int[2][width];
//        gerror = new int[2][width];
        berror = new int[2][width];
        super.init();
    }

    private void zeroError() {
        for (int i = 0; i < width; i++) {
//            rerror[0][i] = 0;
//            rerror[1][i] = 0;
//            gerror[0][i] = 0;
//            gerror[1][i] = 0;
            berror[0][i] = 0;
            berror[1][i] = 0;
        }
    }

    protected final int getNoiseyPixel(final int x, final int y, int actualPixel) {
//        final int a = pix & 0xff000000;
//        int r = (pix >> 16) & 255;
//        int g = (pix >> 8) & 255;
        //int b = pix & 255;

        final int thisrow = y & 1;
        final int nextrow = (y + 1) & 1;
//        r = r + rerror[thisrow][x];
//        g = g + gerror[thisrow][x];
        actualPixel += berror[thisrow][x];

//        r = ((r & ~255) == 0) ? r : ((r < 0) ? 0 : 255);
//        g = ((g & ~255) == 0) ? g : ((g < 0) ? 0 : 255);
        actualPixel = ((actualPixel & ~255) == 0) ? actualPixel : ((actualPixel < 0) ? 0 : 255);

//        final int qr = quantize[r];
//        final int qg = quantize[g];
        final int qb = quantize[actualPixel];

//        rerror[thisrow][x] = 0;
//        gerror[thisrow][x] = 0;
        berror[thisrow][x] = 0;

//        r -= qr;
//        g -= qg;
        actualPixel -= qb;
//        rerror[nextrow][x] += (5 * r + 8) >> 4;
//        gerror[nextrow][x] += (5 * g + 8) >> 4;
        berror[nextrow][x] += (5 * actualPixel + 8) >> 4;

        if (x - 1 >= 0) {
//            rerror[nextrow][x - 1] += (3 * r + 8) >> 4;
//            gerror[nextrow][x - 1] += (3 * g + 8) >> 4;
            berror[nextrow][x - 1] += (3 * actualPixel + 8) >> 4;
        }

        if (x + 1 < width) {
//            rerror[thisrow][x + 1] += (7 * r + 8) >> 4;
//            rerror[nextrow][x + 1] += (r + 8) >> 4;
//            gerror[thisrow][x + 1] += (7 * g + 8) >> 4;
//            gerror[nextrow][x + 1] += (g + 8) >> 4;
            berror[thisrow][x + 1] += (7 * actualPixel + 8) >> 4;
            berror[nextrow][x + 1] += (actualPixel + 8) >> 4;
        }

//        r = qr << 16;
//        g = qg << 8;
//        b = qb;
        return (0xff000000 | qb << 16 | qb << 8 | qb);
    }
//      protected final int getNoiseyPixel(final int x, final int y) {
//        final int pix = getPixel(x, y);
//        final int a = pix & 0xff000000;
//        int r = (pix >> 16) & 255;
//        int g = (pix >> 8) & 255;
//        int b = pix & 255;
//
//        final int thisrow = y & 1;
//        final int nextrow = (y + 1) & 1;
//
//        r = r + rerror[thisrow][x];
//        g = g + gerror[thisrow][x];
//        b = b + berror[thisrow][x];
//
//        r = ((r & ~255) == 0) ? r : ((r < 0) ? 0 : 255);
//        g = ((g & ~255) == 0) ? g : ((g < 0) ? 0 : 255);
//        b = ((b & ~255) == 0) ? b : ((b < 0) ? 0 : 255);
//
//        final int qr = quantize[r];
//        final int qg = quantize[g];
//        final int qb = quantize[b];
//
//        rerror[thisrow][x] = 0;
//        gerror[thisrow][x] = 0;
//        berror[thisrow][x] = 0;
//
//        r -= qr;
//        g -= qg;
//        b -= qb;
//        rerror[nextrow][x] += (5 * r + 8) >> 4;
//        gerror[nextrow][x] += (5 * g + 8) >> 4;
//        berror[nextrow][x] += (5 * b + 8) >> 4;
//
//        if (x - 1 >= 0) {
//            rerror[nextrow][x - 1] += (3 * r + 8) >> 4;
//            gerror[nextrow][x - 1] += (3 * g + 8) >> 4;
//            berror[nextrow][x - 1] += (3 * b + 8) >> 4;
//        }
//
//        if (x + 1 < width) {
//            rerror[thisrow][x + 1] += (7 * r + 8) >> 4;
//            rerror[nextrow][x + 1] += (r + 8) >> 4;
//            gerror[thisrow][x + 1] += (7 * g + 8) >> 4;
//            gerror[nextrow][x + 1] += (g + 8) >> 4;
//            berror[thisrow][x + 1] += (7 * b + 8) >> 4;
//            berror[nextrow][x + 1] += (b + 8) >> 4;
//        }
//
//        r = qr << 16;
//        g = qg << 8;
//        b = qb;
//        return (a | r | g | b);
//    }
}