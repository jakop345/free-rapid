package net.wordrider.dialogs.settings;

import net.wordrider.area.ColorStyles;
import net.wordrider.utilities.Swinger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * @author Vity
 */
final class ColorSettingsPanel extends SettingsPanel {
    public ColorSettingsPanel(final SettingsDialog dialog, final String labelCode) {
        super(dialog, labelCode);    //call to super
    }

    protected final void init() {
        this.setLayout(new GridBagLayout());

        final JLabel labelLineSeparators = Swinger.getLabel("settings.color.separators");
        final JLabel labelBorderLineSeparators = Swinger.getLabel("settings.color.border");
        final JLabel labelMathExpression = Swinger.getLabel("settings.color.math");
        final JLabel labelForeground = Swinger.getLabel("settings.color.fg");
        final JLabel labelBackground = Swinger.getLabel("settings.color.bg");
        final JLabel labelCurrentLine = Swinger.getLabel("settings.color.line");
        final JLabel labelBracketMatch = Swinger.getLabel("settings.color.matchbracket");
        final JLabel labelBracketMismatch = Swinger.getLabel("settings.color.mmbracket");
        final JLabel labelHighlightFound = Swinger.getLabel("settings.color.highlightFound");

        final ColorOptionsGroup group = new ColorOptionsGroup();
        final ColorOption colorOptionForeground = new ColorOption(manager, ColorStyles.COLOR_AREA_FG, group);
        final ColorOption colorOptionBackground = new ColorOption(manager, ColorStyles.COLOR_AREA_BG, group);
        final ColorOption colorOptionLineSeparators = new ColorOption(manager, ColorStyles.COLOR_LINE_COLOR, group);
        final ColorOption colorOptionBorderLineSeparators = new ColorOption(manager, ColorStyles.COLOR_BORDER_LINE_COLOR, group);
        final ColorOption colorOptionMathExpr = new ColorOption(manager, ColorStyles.COLOR_MATH_STYLE, group);
        final ColorOption colorOptionLine = new ColorOption(manager, ColorStyles.COLOR_HIGHLIGHT_LINE, group);
        final ColorOption colorOptionBracketMatch = new ColorOption(manager, ColorStyles.COLOR_BRACKET_MATCH, group);
        final ColorOption colorOptionBracketMMatch = new ColorOption(manager, ColorStyles.COLOR_BRACKET_MISMATCH, group);
        final ColorOption colorOptionHighlightFound = new ColorOption(manager, ColorStyles.COLOR_HIGHLIGHT_FOUND, group);

        labelBackground.setLabelFor(colorOptionBackground);
        labelMathExpression.setLabelFor(colorOptionMathExpr);
        labelForeground.setLabelFor(colorOptionForeground);
        labelLineSeparators.setLabelFor(colorOptionLineSeparators);
        labelCurrentLine.setLabelFor(colorOptionLine);
        labelBracketMatch.setLabelFor(colorOptionBracketMatch);
        labelBracketMismatch.setLabelFor(colorOptionBracketMMatch);
        labelHighlightFound.setLabelFor(colorOptionHighlightFound);

        final JButton defaultsButton = Swinger.getButton("settings.btn.default",
                new AbstractAction("") {
                    public void actionPerformed(final ActionEvent e) {
                        colorOptionForeground.setDefault();
                        colorOptionBackground.setDefault();
                        colorOptionLineSeparators.setDefault();
                        colorOptionMathExpr.setDefault();
                        colorOptionBorderLineSeparators.setDefault();
                        colorOptionLine.setDefault();
                        colorOptionBracketMatch.setDefault();
                        colorOptionBracketMMatch.setDefault();
                        colorOptionHighlightFound.setDefault();
                    }
                });


        this.add(labelForeground, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
                , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 4, 2, 4), 0, 0));
        this.add(labelBackground, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
                , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2, 4, 2, 4), 0, 0));
        this.add(labelBorderLineSeparators, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0
                , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2, 4, 2, 4), 0, 0));
        this.add(labelMathExpression, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
                , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2, 4, 2, 4), 0, 0));
        this.add(labelLineSeparators, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
                , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2, 4, 2, 4), 0, 0));
        this.add(labelBorderLineSeparators, new GridBagConstraints(0, 4, 1, 1, 1.0, 1.0
                , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2, 4, 0, 4), 0, 0));
        this.add(labelCurrentLine, new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0
                , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2, 4, 2, 4), 0, 0));
        this.add(labelBracketMatch, new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0
                , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2, 4, 2, 4), 0, 0));
        this.add(labelBracketMismatch, new GridBagConstraints(0, 7, 1, 1, 1.0, 1.0
                , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2, 4, 0, 4), 0, 0));
        this.add(labelHighlightFound, new GridBagConstraints(0, 8, 1, 1, 1.0, 1.0
                , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2, 4, 0, 4), 0, 0));

        this.add(colorOptionForeground, new GridBagConstraints(1, 0, 1, 1, 1.0, 1.0
                , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 4, 2, 22), 0, 0));
        this.add(colorOptionBackground, new GridBagConstraints(1, 1, 1, 1, 1.0, 1.0
                , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 4, 4, 22), 0, 0));
        this.add(colorOptionMathExpr, new GridBagConstraints(1, 2, 1, 1, 1.0, 1.0
                , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 4, 4, 22), 0, 0));

        this.add(colorOptionLineSeparators, new GridBagConstraints(1, 3, 1, 1, 1.0, 1.0
                , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 4, 4, 22), 0, 0));
        this.add(colorOptionBorderLineSeparators, new GridBagConstraints(1, 4, 1, 1, 1.0, 1.0
                , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 4, 0, 22), 0, 0));
        this.add(colorOptionLine, new GridBagConstraints(1, 5, 1, 1, 1.0, 1.0
                , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 4, 0, 22), 0, 0));
        this.add(colorOptionBracketMatch, new GridBagConstraints(1, 6, 1, 1, 1.0, 1.0
                , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 4, 0, 22), 0, 0));
        this.add(colorOptionBracketMMatch, new GridBagConstraints(1, 7, 1, 1, 1.0, 1.0
                , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 4, 0, 22), 0, 0));
        this.add(colorOptionHighlightFound, new GridBagConstraints(1, 8, 1, 1, 1.0, 1.0
                , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 4, 0, 22), 0, 0));


        this.add(defaultsButton, new GridBagConstraints(2, 9, 1, 1, 0.0, 0.0
                , GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, new Insets(0, 4, 4, 4), 0, 0));

//        this.add(new JSeparator(JSeparator.HORIZONTAL), new GridBagConstraints(0, 6, 3, 1, 0.0, 0.0
//            , GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 4, 2, 4), 300, 0));

        //     this.setPreferredSize(new Dimension(325, 260));
    }

}
