package cz.green.swing;

public interface SizeConstraint {
    int getMinSize();

    int getPrefferredSize();

    int getSize(int parent);
}
