package cz.felk.cvut.erm.swing;

public class ConstantConstraint implements Constraint {
    protected int start = 0;
    protected int size = 0;

    public ConstantConstraint() {
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
