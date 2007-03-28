package net.wordrider.dialogs.pictures.filters;

import java.awt.*;

/**
 * @author Vity
 */
abstract class OrderedFilter extends DitherRaster {
    private final int[] pattern;
    private final int level;
    private final static int whiteRGB = Color.WHITE.getRGB();
    private final static int blackRGB = Color.BLACK.getRGB();
    //    protected final Color getColor(int x, int y)
//    {
//        return new Color(pixels[y * width + x]);
//    }

    public OrderedFilter(final int[] pattern, final int level) {
        this.pattern = pattern;
        this.level = level;
        initPattern();
    }

    private void initPattern() {
        final int patternSize = pattern.length;
        for (int i = 0; i < patternSize; i++)
            pattern[i] = (255 * pattern[i]) / patternSize;
    }

    protected void performEffect() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                final int index = y * width + x;
                final int pixelValue = pixels[index] & 0xff;
                pixels[index] = pixelValue != 255 && pattern[(y % level) * level + x % level] >= pixelValue ? blackRGB : whiteRGB;
            }
        }
    }

}