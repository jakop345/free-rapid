package net.wordrider.core.swing;

import net.wordrider.core.Lng;
import net.wordrider.utilities.Swinger;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * @author Vity
 */
public class GraphicMenuItem extends JLabel implements MouseListener {
    private String titleText;
    private String text;
    private final Action action;
    private final static String ITEM_STYLE = Lng.getLabel("gmenu.section");
    private final static String ITEM_STYLE_OVER = Lng.getLabel("gmenu.sectionOver");
    private final static Border emptyBorder = BorderFactory.createEmptyBorder(8, 10, 8, 10);

    public GraphicMenuItem(final String titleText, final String text, final Icon icon, final Action action) {
        super();
        this.titleText = titleText;
        this.text = text;
        this.action = action;
        this.setIcon(icon);
        this.setFocusable(true);
        this.setText(updateText(false));
        //this.setToolTipText(text);
        this.setBorder(emptyBorder);
        this.setOpaque(false);
        this.setBackground(null);
        this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        this.addMouseListener(this);
    }

    private String updateText(boolean over) {
        if (over)
            return String.format(ITEM_STYLE_OVER, this.titleText, this.text);
        else
            return String.format(ITEM_STYLE, this.titleText, this.text);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (hasFocus()) {
            final Rectangle rec = this.getBounds();
            g.setColor(Color.BLACK);
            g.drawRect(rec.x, rec.y, rec.width, rec.height);
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
}
