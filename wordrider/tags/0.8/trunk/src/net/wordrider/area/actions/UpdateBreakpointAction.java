package net.wordrider.area.actions;

import net.wordrider.area.RiderArea;
import net.wordrider.area.RiderDocument;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * @author Vity
 */
public final class UpdateBreakpointAction extends StyledAreaAction {
    private final static UpdateBreakpointAction instance = new UpdateBreakpointAction();
    private final static String CODE = "UpdateBreakpointAction";


    public static UpdateBreakpointAction getInstance() {
        return instance;
    }

    private UpdateBreakpointAction() {
        super(CODE, KeyStroke.getKeyStroke("F7"), "bookmark.gif");
    }

    public final void toggleBookmark(final RiderDocument doc, final int position) {
        super.actionPerformed(null);
        doc.toggleBookmark(position);
    }

    public final void actionPerformed(final ActionEvent e) {
        final RiderArea area = getRiderArea(e);
        if (area != null)
            toggleBookmark(area.getDoc(), area.getCaretPosition());
    }
}
