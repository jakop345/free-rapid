package cz.cvut.felk.erm.sandbox;

import org.jdesktop.swingx.JXLoginDialog;
import org.jdesktop.swingx.JXLoginPane;

import javax.swing.*;

/**
 * @author Ladislav Vitasek
 */
public class Test {
    public static void main(String[] args) {

        final JFrame jFrame = new JFrame();
        final JXLoginDialog dialog = new JXLoginDialog(jFrame);
        final JXLoginPane jxLoginPane = new JXLoginPane(null);
        dialog.setPanel(jxLoginPane);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                dialog.setVisible(true);
            }
        });
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setVisible(true);
    }
}
