package net.wordrider.gui;

/**
 * @author Vity
 */

import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.metal.DefaultMetalTheme;
import java.awt.*;

final class WordRiderMetalTheme extends DefaultMetalTheme {
    private final ColorUIResource color = new ColorUIResource(0, 0, 0);
    private final FontUIResource font = new FontUIResource("Dialog", Font.PLAIN, 11);

    public WordRiderMetalTheme() {
        super();
    }

    public final ColorUIResource getControlTextColor() {
        return color;
    }

    public final ColorUIResource getMenuTextColor() {
        return color;
    }

    public final ColorUIResource getSystemTextColor() {
        return color;
    }

    public final ColorUIResource getUserTextColor() {
        return color;
    }

    public final FontUIResource getControlTextFont() {
        return font;
    }

    public final FontUIResource getMenuTextFont() {
        return font;
    }

    public final FontUIResource getSystemTextFont() {
        return font;
    }

    public final FontUIResource getUserTextFont() {
        return font;
    }

    public final FontUIResource getWindowTitleFont() {
        return font;
    }

}
