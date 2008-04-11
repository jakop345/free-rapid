package cz.green.swing;

import java.awt.*;

class ResizingConstrain {
    private int left = 0;
    private int top = 0;
    private int width = 0;
    private int height = 0;
    public Rectangle bounds = null;

    public ResizingConstrain() {
        this(0, 0, 0, 0);
    }

    public ResizingConstrain(int left, int top) {
        this(left, top, 0, 0);
    }

    private ResizingConstrain(int left, int top, int width, int height) {
        this.left = left;
        this.top = top;
        this.width = width;
        this.height = height;
    }
}
