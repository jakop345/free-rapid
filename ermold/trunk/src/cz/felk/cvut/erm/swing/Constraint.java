package cz.felk.cvut.erm.swing;

public interface Constraint {
    int getParentSize(int compSize);

    int getSize(int parent);

    int getStart(int parent);
}
