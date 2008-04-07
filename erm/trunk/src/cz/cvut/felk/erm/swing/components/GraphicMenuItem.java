package cz.cvut.felk.erm.swing.components;

import cz.cvut.felk.erm.gui.managers.BackgroundManager;
import cz.cvut.felk.erm.swing.Swinger;
import org.jdesktop.application.ResourceMap;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * @author Ladislav Vitasek
 */
public class GraphicMenuItem extends JLabel implements MouseListener {
    private String header;
    private String comment;
    private final Action action;
    private final static Border emptyBorder = BorderFactory.createEmptyBorder(8, 20, 8, 10);

    public GraphicMenuItem(final String name, final String header, final String comment, final Action action) {
        super();
        this.header = header;
        this.comment = comment;
        this.setName(name);
        this.action = action;
        this.setFocusable(false);
        this.setText(updateText(false));
        //this.setToolTipText(comment);
        this.setBorder(emptyBorder);
        this.setOpaque(false);
        this.setBackground(null);
        this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        this.addMouseListener(this);
    }

    private String updateText(boolean over) {
        final ResourceMap map = Swinger.getResourceMap(BackgroundManager.class);

        if (over)
            return String.format(map.getString("gmenu.section"), this.header, this.comment);
        else
            return String.format(map.getString("gmenu.sectionOver"), this.header, this.comment);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (hasFocus()) {
            final Rectangle rec = this.getBounds();
            if (rec.height > 3) {
                g.setColor(Color.BLACK);
                g.drawRect(rec.x, rec.y, rec.width, rec.height);
            }
        }
    }


    public void mouseClicked(MouseEvent e) {
        action.actionPerformed(new ActionEvent(this, 0, ""));
        e.consume();
    }

    public void mousePressed(MouseEvent e) {
        Swinger.inputFocus(this);
    }

    public void mouseReleased(MouseEvent e) {

    }

    public void mouseEntered(MouseEvent e) {
        this.setText(updateText(true));
    }

    public void mouseExited(MouseEvent e) {
        this.setText(updateText(false));
    }


    public void setHeader(String header) {
        this.header = header;
        updateText(false);
    }

    public void setComment(String comment) {
        this.comment = comment;
        updateText(false);
    }
}
