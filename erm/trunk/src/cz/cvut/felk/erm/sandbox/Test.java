package cz.cvut.felk.erm.sandbox;

import cz.cvut.felk.erm.swing.components.FindBar;
import org.jdesktop.swingx.JXEditorPane;

import javax.swing.*;
import java.awt.*;


public class Test {

    public static void main(String[] args) {
        final JFrame frame = new JFrame("Test window");
        final Container container = frame.getContentPane();
        container.setLayout(new BorderLayout());
        final JXEditorPane pane = new JXEditorPane();
        final FindBar comp = new FindBar(pane);

        container.add(comp, BorderLayout.NORTH);
        container.add(new JScrollPane(pane), BorderLayout.CENTER);

        frame.setSize(400, 250);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
