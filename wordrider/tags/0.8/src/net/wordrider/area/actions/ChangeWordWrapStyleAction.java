package net.wordrider.area.actions;

import net.wordrider.area.RiderArea;
import net.wordrider.area.RiderStyles;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

/**
 * @author Vity
 */
public final class ChangeWordWrapStyleAction extends StyledAreaAction {
    private static final ChangeWordWrapStyleAction instance = new ChangeWordWrapStyleAction();
    private static final String CODE = "ChangeWordWrapStyleAction";

    public static ChangeWordWrapStyleAction getInstance() {
        return instance;
    }

    private ChangeWordWrapStyleAction() {
        super(CODE, KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, InputEvent.CTRL_MASK), "word_wrap.gif");
    }

    public final void actionPerformed(final ActionEvent e) {
        super.actionPerformed(e);
        final RiderArea area = getRiderArea(e);
        if (area != null)
            setCharacterAttributes(area, RiderStyles.updateWordWrapAttributes(area.getInputAttributes()), false);
    }


}
