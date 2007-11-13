package net.wordrider.area;

import net.wordrider.core.MainApp;
import net.wordrider.core.managers.interfaces.IFileInstance;
import net.wordrider.utilities.Swinger;

import javax.swing.*;
import java.awt.*;

/**
 * @author Vity
 */
public final class ColorStyles {
    public static final int COLOR_AREA_FG = 0;
    public static final int COLOR_AREA_BG = 1;
    public static final int COLOR_MATH_STYLE = 2;
    public static final int COLOR_LINE_COLOR = 3;
    public static final int COLOR_BORDER_LINE_COLOR = 4;
    public static final int COLOR_HIGHLIGHT_LINE = 5;
    public static final int COLOR_BRACKET_MATCH = 6;
    public static final int COLOR_BRACKET_MISMATCH = 7;
    public static final int COLOR_TEXT_NOTFOCUSED = 8;
    public static final int COLOR_HIGHLIGHT_FOUND = 9;

    private static final int COLORS_COUNT = 10;

    private static final String[] COLOR_CODES = {"color.fgArea", "color.bgArea", "color.fgMathstyle", "color.editorline", "color.borderline", "color.lineHighlight", "color.bracketMatch", "color.bracketMMatch", "color.inactivetext", "color.highlightFound"};

    private static final Color LINE_COLOR = new Color(255, 255, 215);
    private static final Color BRACKET_MATCH_COLOR = new Color(153, 204, 255);
    private static final Color BRACKET_MMATCH_COLOR = new Color(255, 220, 220);
    private static final Color NOTFOCUSED_TEXT_COLOR = new Color(230, 230, 210);

    private static final Color[] colors = initColors();
    private static Color borderColor = null;

    private ColorStyles() {
    }


    private static Color[] initColors() {
        final Color[] c = new Color[COLORS_COUNT];
        c[COLOR_AREA_FG] = Swinger.getColor(COLOR_CODES[COLOR_AREA_FG], Color.BLACK);
        c[COLOR_AREA_BG] = Swinger.getColor(COLOR_CODES[COLOR_AREA_BG], Color.WHITE);
        c[COLOR_MATH_STYLE] = Swinger.getColor(COLOR_CODES[COLOR_MATH_STYLE], Color.BLUE);
        c[COLOR_LINE_COLOR] = Swinger.getColor(COLOR_CODES[COLOR_LINE_COLOR], Color.GRAY);
        c[COLOR_BORDER_LINE_COLOR] = Swinger.getColor(COLOR_CODES[COLOR_BORDER_LINE_COLOR], Color.GRAY);
        c[COLOR_HIGHLIGHT_LINE] = Swinger.getColor(COLOR_CODES[COLOR_HIGHLIGHT_LINE], LINE_COLOR);
        c[COLOR_BRACKET_MATCH] = Swinger.getColor(COLOR_CODES[COLOR_BRACKET_MATCH], BRACKET_MATCH_COLOR);
        c[COLOR_BRACKET_MISMATCH] = Swinger.getColor(COLOR_CODES[COLOR_BRACKET_MISMATCH], BRACKET_MMATCH_COLOR);
        c[COLOR_TEXT_NOTFOCUSED] = Swinger.getColor(COLOR_CODES[COLOR_TEXT_NOTFOCUSED], NOTFOCUSED_TEXT_COLOR);
        c[COLOR_HIGHLIGHT_FOUND] = Swinger.getColor(COLOR_CODES[COLOR_HIGHLIGHT_FOUND], Color.YELLOW);
        return c;
    }

    public static Color getDefaultColor(final int colorSettings) {
        switch (colorSettings) {
            case COLOR_AREA_FG:
                return Color.BLACK;
            case COLOR_AREA_BG:
                return Color.WHITE;
            case COLOR_MATH_STYLE:
                return Color.BLUE;
            case COLOR_LINE_COLOR:
                return Color.GRAY;
            case COLOR_BORDER_LINE_COLOR:
                return Color.GRAY;
            case COLOR_HIGHLIGHT_LINE:
                return LINE_COLOR;
            case COLOR_BRACKET_MATCH:
                return BRACKET_MATCH_COLOR;
            case COLOR_BRACKET_MISMATCH:
                return BRACKET_MMATCH_COLOR;
            case COLOR_HIGHLIGHT_FOUND:
                return Color.YELLOW;
            default:
                throw new IllegalArgumentException("invalid color");
        }
    }

    public static void setColor(final int colorSettings, final Color value) {
        colors[colorSettings] = value;
    }


    public static Color getColor(final int colorSettings) {
        return colors[colorSettings];
    }

    public static void updateEditorColors() {
        for (Object o : MainApp.getInstance().getMainAppFrame().getManagerDirector().getAreaManager().getOpenedInstances()) {
            ((RiderDocument) ((IFileInstance) o).getRiderArea().getDocument()).refreshAll();
        }
    }

    public static void updateBorderColor() {
        borderColor = null;
    }

    public static Color getBorderColor() {
        return (borderColor == null) ? borderColor = Swinger.getColor("color.bgborder", Swinger.brighter(UIManager.getDefaults().getColor("control"))) : borderColor;
    }

    public static void storeColors() {
        for (int i = 0; i < COLORS_COUNT; ++i) {
            Swinger.setColor(COLOR_CODES[i], colors[i]);
        }
    }

}
