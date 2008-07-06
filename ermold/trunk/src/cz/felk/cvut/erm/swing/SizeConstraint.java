package cz.felk.cvut.erm.swing;

public interface SizeConstraint {
    int getMinSize();

    int getPrefferredSize();

    int getSize(int parent);
}
