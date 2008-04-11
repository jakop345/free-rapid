package cz.green.util;

import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class WindowCloser implements WindowListener {
    private Window win = null;
    private boolean exit = false;

    public WindowCloser(Window win) {
        this.win = win;
    }

    public WindowCloser(Window win, boolean exit) {
        this.win = win;
        this.exit = exit;
    }

    public void windowActivated(WindowEvent e) {
    }

    public void windowClosed(WindowEvent e) {
    }

    public void windowClosing(WindowEvent e) {
        win.dispose();
        if (exit)
            System.exit(0);
    }

    public void windowDeactivated(WindowEvent e) {
    }

    public void windowDeiconified(WindowEvent e) {
    }

    public void windowIconified(WindowEvent e) {
    }

    public void windowOpened(WindowEvent e) {
    }
}
