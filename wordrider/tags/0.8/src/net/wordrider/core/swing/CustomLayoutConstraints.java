package net.wordrider.core.swing;

import info.clearthought.layout.TableLayoutConstraints;


/**
 * CustomLayoutConstraints binds components to their constraints.
 * @author Vity
 */

public class CustomLayoutConstraints extends TableLayoutConstraints {


    /**
     * Constructs an TableLayoutConstraints a set of constraints.
     * @param col column where the component is placed
     * @param row row where the component is placed
     */

    public CustomLayoutConstraints(int col, int row) {
        this(col, row, 1, 1, FULL, FULL); //hack
    }


    /**
     * Constructs an TableLayoutConstraints a set of constraints.
     * @param col1 column where upper-left cornor of the component is placed
     * @param row1 row where upper-left cornor of the component is placed
     * @param col2 column where lower-right cornor of the component is placed
     * @param row2 row where lower-right cornor of the component is placed
     */

    public CustomLayoutConstraints(int col1, int row1, int col2, int row2) {
        this(col1, row1, col2, row2, FULL, FULL);
    }


    /**
     * Constructs an TableLayoutConstraints a set of constraints.
     * @param col1   column where upper-left cornor of the component is placed
     * @param row1   row where upper-left cornor of the component is placed
     * @param col2   column where lower-right cornor of the component is placed
     * @param row2   row where lower-right cornor of the component is placed
     * @param hAlign horizontal justification of a component in a single cell
     * @param vAlign vertical justification of a component in a single cell
     */

    public CustomLayoutConstraints
            (int col1, int row1, int col2, int row2, int hAlign, int vAlign) {
        super(col1, row1, col1 + col2 - 1, row1 + row2 - 1, hAlign, vAlign);
    }
}
