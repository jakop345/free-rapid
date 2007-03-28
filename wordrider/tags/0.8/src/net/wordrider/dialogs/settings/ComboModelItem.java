package net.wordrider.dialogs.settings;

/**
 * @author Vity
*/
abstract class ComboModelItem {
    private final Integer value;
    private final String label;

    public ComboModelItem(final int value) {
        this.value = value;
        this.label = getLabelByValue(value);
    }

    public final boolean equals(final Object obj) {
        return obj instanceof ComboModelItem && value.equals(((ComboModelItem) obj).value);
    }

    public Integer getValue() {
        return value;
    }

    public final String toString() {
        return label;
    }

    abstract String getLabelByValue(final int value);
}
