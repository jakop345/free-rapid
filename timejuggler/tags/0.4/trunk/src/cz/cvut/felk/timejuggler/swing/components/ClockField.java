package cz.cvut.felk.timejuggler.swing.components;

/**
 * @author Vity
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Jednoduche hodiny
 */
public class ClockField extends JLabel implements ActionListener {

    private final static DateFormat FORMAT = new SimpleDateFormat("HH:mm");
    private Timer timer;

    public ClockField() {
        super();
        timer = new Timer(1000, this);
        final Dimension dimension = this.getPreferredSize();
        this.setPreferredSize(new Dimension(70, dimension.height));
        showClock();
    }

    public void showClock() {
        timer.start();
        this.setVisible(true);
    }

    public void hideClock() {
        timer.stop();
        this.setVisible(false);
    }

    public void actionPerformed(ActionEvent e) {
        final Date now = new Date();
        this.setText(FORMAT.format(now));
        this.setToolTipText(now.toString());
    }
}