package net.wordrider.dialogs.pictures.filters;

import java.awt.image.ColorModel;
import java.awt.image.ImageFilter;

/**
 * @author Vity
 */

public class RotateFlipFilter extends ImageFilter {

    public static final int FLIP_HORIZONTALY = 1;
    private static final int FLIP_VERTICALY = 2;
    private static final int FLIP_HORIZ_VERT = 3;
    private static final int FLIP_90CW = 4;
    private static final int FLIP_90CCW = 5;
    private static final int FLIP_180 = 6;
    private int operation;
    private int width;
    private int height;
    private int newWidth;
    private int newHeight;


    public RotateFlipFilter(int i) {
        this.operation = i;
    }

    public void setHints(int i) {
        i &= 0xfffffff;
        consumer.setHints(i);
    }

    public void setDimensions(int i, int j) {
        width = i;
        height = j;
        switch (operation) {
            case FLIP_HORIZONTALY:
            case FLIP_VERTICALY:
            case FLIP_180:
                newWidth = i;
                newHeight = j;
                break;
            case FLIP_HORIZ_VERT:
            case FLIP_90CW:
            case FLIP_90CCW:
                newWidth = j;
                newHeight = i;
                break;
        }
        consumer.setDimensions(newWidth, newHeight);
    }

    public void setPixels(int x, int y, int w, int h, ColorModel model, byte pixels[], int offset,
                          int scansize) {
        int newX = x;
        int newY = y;
        int newW = w;
        int newH = h;
        switch (operation) {
            case FLIP_HORIZONTALY:
                newX = width - (x + w);
                break;

            case FLIP_VERTICALY:
                newY = height - (y + h);
                break;

            case FLIP_HORIZ_VERT:
                newW = h;
                newH = w;
                newX = y;
                newY = x;
                break;

            case FLIP_90CW:
                newW = h;
                newH = w;
                newX = height - (y + h);
                newY = x;
                break;

            case FLIP_90CCW:
                newW = h;
                newH = w;
                newX = y;
                newY = width - (x + w);
                break;

            case FLIP_180:
                newX = width - (x + w);
                newY = height - (y + h);
                break;
        }
        byte pixelLine[] = new byte[newW * newH];
        for (int i = 0; i < h; i++) {
            for (int l2 = 0; l2 < w; l2++) {
                int i3 = i * scansize + offset + l2;
                int j3 = i;
                int k3 = l2;
                switch (operation) {
                    case FLIP_HORIZONTALY:
                        k3 = w - l2 - 1;
                        break;

                    case FLIP_VERTICALY:
                        j3 = h - i - 1;
                        break;

                    case FLIP_90CW:
                        j3 = l2;
                        k3 = h - i - 1;
                        break;

                    case FLIP_90CCW:
                        j3 = w - l2 - 1;
                        k3 = i;
                        break;

                    case FLIP_180:
                        j3 = h - i - 1;
                        k3 = w - l2 - 1;
                        break;

                    case FLIP_HORIZ_VERT:
                        j3 = l2;
                        k3 = i;
                        break;

                }
                int pixelPosition = j3 * newW + k3;
                pixelLine[pixelPosition] = pixels[i3];
            }

        }

        consumer.setPixels(newX, newY, newW, newH, model, pixelLine, 0, newW);
    }

    public void setPixels(int x, int y, int w, int h, ColorModel colormodel, int pixels[], int offset,
                          int scansize) {
        int newX = x;
        int newY = y;
        int newW = w;
        int newH = h;
        switch (operation) {
            case FLIP_HORIZONTALY:
                newX = width - (x + w);
                break;

            case FLIP_VERTICALY:
                newY = height - (y + h);
                break;

            case FLIP_HORIZ_VERT:
                newW = h;
                newH = w;
                newX = y;
                newY = x;
                break;

            case FLIP_90CW:
                newW = h;
                newH = w;
                newX = height - (y + h);
                newY = x;
                break;

            case FLIP_90CCW:
                newW = h;
                newH = w;
                newX = y;
                newY = width - (x + w);
                break;

            case FLIP_180:
                newX = width - (x + w);
                newY = height - (y + h);
                break;
        }
        int pixelLine[] = new int[newW * newH];
        for (int i = 0; i < h; i++) {
            for (int l2 = 0; l2 < w; l2++) {
                int i3 = i * scansize + offset + l2;
                int j3 = i;
                int k3 = l2;
                switch (operation) {
                    case FLIP_HORIZONTALY:
                        k3 = w - l2 - 1;
                        break;

                    case FLIP_VERTICALY:
                        j3 = h - i - 1;
                        break;


                    case FLIP_90CW:
                        j3 = l2;
                        k3 = h - i - 1;
                        break;

                    case FLIP_90CCW:
                        j3 = w - l2 - 1;
                        k3 = i;
                        break;

                    case FLIP_180:
                        j3 = h - i - 1;
                        k3 = w - l2 - 1;
                        break;

                    case FLIP_HORIZ_VERT:
                        j3 = l2;
                        k3 = i;
                        break;

                }
                int pixelPosition = j3 * newW + k3;
                pixelLine[pixelPosition] = pixels[i3];
            }

        }

        consumer.setPixels(newX, newY, newW, newH, colormodel, pixelLine, 0, newW);
    }

}
