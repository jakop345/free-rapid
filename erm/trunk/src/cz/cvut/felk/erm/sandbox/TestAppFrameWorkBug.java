package cz.cvut.felk.erm.sandbox;

import org.jdesktop.swingx.JXFrame;

import javax.swing.*;
import java.awt.*;

/**
 * @author Ladislav Vitasek
 */
public class TestAppFrameWorkBug {


    private JDialog createBlockingDialog(Component dialogOwner) {
        JOptionPane optionPane = new JOptionPane();
        optionPane.setOptions(new Object[]{});
        //optionPane.setIcon();
//            if (getTask().getUserCanCancel()) {
//                JButton cancelButton = new JButton();
//                cancelButton.setName("BlockingDialog.cancelButton");
//                ActionListener doCancelTask = new ActionListener() {
//                        public void actionPerformed(ActionEvent ignore) {
//                            getTask().cancel(true);
//                        }
//                    };
//                cancelButton.addActionListener(doCancelTask);
//                optionPane.setOptions(new Object[]{cancelButton});
//            }
        //  Component dialogOwner = (Component)getTarget();

        String dialogTitle = "BlockingDialog";
        JDialog dialog = optionPane.createDialog(dialogOwner, dialogTitle);
        optionPane.getRootPane().setWindowDecorationStyle(JRootPane.NONE);
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        dialog.setModal(true);
        //dialog.pack();
        return dialog;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new TestAppFrameWorkBug().init();
            }
        });
    }

    private void init() {
        final JXFrame frame = new JXFrame("Test");
        frame.setSize(new Dimension(300, 200));
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        final JButton comp = new JButton("asdasd");
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(comp, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        //final JDialog dialog = createBlockingDialog(frame);
        //dialog.setVisible(true);
    }
}
