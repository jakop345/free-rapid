package net.wordrider.dialogs.pictures;

import javax.swing.*;

/**
 * @author Vity
 */
final class WidthSpinnerModel extends SpinnerNumberModel {

    public static Integer getNextHigh(int value, final int max) {
        value += 8 - (value % 8);
        if (max < value)
            value = max;
        return value;
    }

    public static int getNextHigh(int value) {
        value += 8 - (value % 8);
        return value;
    }

    private Integer getNextHigh(final Object nextHigh) {
        return getNextHigh(((Number) nextHigh).intValue(), ((Number) getMaximum()).intValue());
    }

    public final Object getNextValue() {
        return getNextHigh(getValue());
    }

    public final Object getPreviousValue() {
        int value = ((Number) getValue()).intValue();
        final Number min = ((Number) getMinimum());

        value -= 8 - (value % 8);
        if (min != null && min.intValue() > value)
            value = min.intValue();
        return value;
    }
}
