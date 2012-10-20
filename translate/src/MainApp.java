import Consts.Consts;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;
import utilities.LogUtils;
import utilities.OSDesktop;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Vity
 */
public class MainApp extends SingleFrameApplication {
    public static void main(String[] args) {
        Application.launch(MainApp.class, args);
    }


    @Override
    protected void initialize(String[] args) {
        super.initialize(args);
    }


    @Override
    protected void startup() {
        try {
            LogUtils.initLogging(Consts.LOGDEBUG);//logovani nejdrive
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, e.getMessage());
        }

        final MainFrame mainFrame = new MainFrame();
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setSize(new Dimension(400, 300));

        this.setMainFrame(mainFrame);
        mainFrame.getBtnAnalyze().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                analyze(mainFrame);
            }
        });
        show(mainFrame);

    }

    private void analyze(MainFrame mainFrame) {

        final String path = mainFrame.getFieldSourcePath().getText();
        if (!new File(path).exists()) {
            JOptionPane.showMessageDialog(mainFrame, "Path " + path + " does not exist!!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        final TranslateProcessor processor = new TranslateProcessor(path, mainFrame.getFieldSourceLangCode().getText(), mainFrame.getFieldTargetLangCode().getText(), mainFrame.getCheckCommentOut().isSelected());
        try {
            final File run = processor.run();
            if (mainFrame.getCheckRunInBrowser().isSelected()) {
                OSDesktop.openFile(run);
            } else {
                JOptionPane.showMessageDialog(mainFrame, "Information was succesfuly generated in " + run.getAbsolutePath());
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(mainFrame, "Error during processing: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            LogUtils.processException(getLogger(), e);
        }

    }

    private Logger getLogger() {
        return Logger.getLogger(MainApp.class.getName());
    }

}
