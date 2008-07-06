package cz.felk.cvut.erm.swing;


public class MinLinearConstraint extends LinearConstraint {
    protected int min = 0;

    public MinLinearConstraint() {
        super();
    }

    public MinLinearConstraint(double startMultiplier, double sizeMultiplier, int min) {
        super(startMultiplier, sizeMultiplier);
        this.min = min;
    }

    public MinLinearConstraint(int min) {
        super();
        this.min = min;
    }

    public MinLinearConstraint(int startConstant, double startMultiplier,
                               int sizeConstant, double sizeMultiplier, int min) {
        super(startConstant, startMultiplier, sizeConstant, sizeMultiplier);
        this.min = min;
    }

    public MinLinearConstraint(int startConstant, int sizeConstant) {
        super(startConstant, sizeConstant);
    }

    public int getMinSize() {
        return size + min;
    }

    public int getPrefferredSize() {
        return size + min;
    }

    public int getSize(int parent) {
        int c = ((int) (sizeMultiplier * parent)) + size;
        return (c > min) ? c : min;
    }
}
