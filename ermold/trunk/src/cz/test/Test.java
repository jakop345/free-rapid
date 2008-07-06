package cz.test;

import cz.green.ermodeller.DesktopContainer;

import javax.swing.*;
import java.awt.*;

/**
 * @author Ladislav Vitasek
 */
public class Test {
    public static void main(String[] args) {
        final JFrame frame = new JFrame("Test frame");
        final Container contentPane = frame.getContentPane();
        contentPane.add(new DesktopContainer(2500, 2500));
        frame.setLocationRelativeTo(null);
        frame.setSize(400, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
