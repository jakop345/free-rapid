package net.wordrider.area.actions;

import net.wordrider.area.RiderArea;
import net.wordrider.area.RiderStyles;

import javax.swing.*;
import javax.swing.text.Element;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

/**
 * @author Vity
 */
public final class ChangeMathStyleAction extends StyledAreaAction {
    private static final ChangeMathStyleAction instance = new ChangeMathStyleAction();
    private static final String CODE = "ChangeMathStyleAction";

    public static ChangeMathStyleAction getInstance() {
        return instance;
    }

    private ChangeMathStyleAction() {
        super(CODE, KeyStroke.getKeyStroke(KeyEvent.VK_M, InputEvent.CTRL_MASK), "math.gif");
    }

    public final void actionPerformed(final ActionEvent e) {
        super.actionPerformed(e);
        final RiderArea area = getRiderArea(e);
        if (area != null) {
            final Element paraElement = area.getStyledDocument().getParagraphElement(area.getCaretPosition());
            if (!RiderStyles.isReadonlySection(paraElement)) {
                area.getInputAttributes().removeAttributes(area.getInputAttributes());
                area.getInputAttributes().addAttributes(RiderStyles.normalStyle);
                area.getDoc().setPrettyPrint(paraElement);
            }
        }
    }


}
