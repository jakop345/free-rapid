package cz.green.swing;

import java.awt.*;

public class ResizingConstrain {
    public int left = 0;
    public int top = 0;
    public int width = 0;
    public int height = 0;
    public Rectangle bounds = null;

    public ResizingConstrain() {
        this(0, 0, 0, 0);
    }

    public ResizingConstrain(int left, int top) {
        this(left, top, 0, 0);
    }

    public ResizingConstrain(int left, int top, int width, int height) {
        this.left = left;
        this.top = top;
        this.width = width;
        this.height = height;
    }
}
