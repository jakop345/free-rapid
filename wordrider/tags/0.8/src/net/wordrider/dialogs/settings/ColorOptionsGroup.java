package net.wordrider.dialogs.settings;

import net.wordrider.area.ColorStyles;
import net.wordrider.area.RiderStyles;

/**
 * @author Vity
 */
final class ColorOptionsGroup extends DefaultOptionsGroup {
    public final void doGroupChange() {
        super.doGroupChange();
        RiderStyles.updateColorsForStyles();
        ColorStyles.updateEditorColors();
    }
}
