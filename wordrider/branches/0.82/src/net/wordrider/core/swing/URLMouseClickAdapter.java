package net.wordrider.core.swing;

import net.wordrider.utilities.BrowserControl;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @author Vity
 */
public final class URLMouseClickAdapter extends MouseAdapter {
    private String url = "";

    public URLMouseClickAdapter(final String url) {
        super();
        this.url = url;
    }

    public final void mouseClicked(final MouseEvent e) {
        BrowserControl.showURL(url);
    }

    public final void mouseExited(final MouseEvent e) {
        ((Component) e.getSource()).setCursor(Cursor.getDefaultCursor());
    }

    public final void mouseEntered(final MouseEvent e) {
        ((Component) e.getSource()).setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }
}
