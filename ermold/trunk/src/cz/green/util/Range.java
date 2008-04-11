package cz.green.util;

import java.awt.*;

class Range {
    private static final int X = 1;
    private static final int Y = 2;

    private int start = 0;
    private int size = 0;

    public Range() {
    }

    public Range(int start, int size) {
        this.start = start;
        this.size = size;
    }

    public Range(Rectangle rect, int dim) {
        if (dim == X) {
            start = rect.x;
            size = rect.width;
        }
        if (dim == Y) {
            start = rect.y;
            size = rect.height;
        }
    }
}
