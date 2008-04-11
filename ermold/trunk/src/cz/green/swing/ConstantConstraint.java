package cz.green.swing;

public class ConstantConstraint implements Constraint {
    int start = 0;
    int size = 0;

    ConstantConstraint() {
    }

    public ConstantConstraint(int start, int size) {
        this.start = start;
        this.size = size;
    }

    public int getParentSize(int compSize) {
        return start + size;
    }

    public int getSize(int parent) {
        return size;
    }

    public int getStart(int parent) {
        return start;
    }
}
