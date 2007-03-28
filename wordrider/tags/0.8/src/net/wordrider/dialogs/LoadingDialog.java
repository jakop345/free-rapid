package net.wordrider.dialogs;

import net.wordrider.core.Lng;
import net.wordrider.utilities.LogUtils;
import net.wordrider.utilities.Swinger;

import javax.swing.*;
import java.awt.*;
import java.util.logging.Logger;

/**
 * @author Vity
 */
public final class LoadingDialog extends AppDialog {
    private final JProgressBar progressBar = new JProgressBar();
    private JPanel showPanel;
    private final static Logger logger = Logger.getLogger(LoadingDialog.class.getName());

    public LoadingDialog(final Frame owner) {
        super(owner, true);
        //this.frame = owner;
        try {
            init();
        } catch (Exception e) {
            LogUtils.processException(logger, e);
        }
        Swinger.centerDialog(owner, this);
        this.setResizable(false);
        this.setModal(true);
        //this.setTitle(AppPrefs.getLabel("dialog.close.title"));
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private void init() {
        //this.getRootPane().setWindowDecorationStyle(JRootPane.PLAIN_DIALOG);
        this.setUndecorated(true);
        final Container mainPanel = this.getContentPane();
        mainPanel.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        //mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(showPanel = new JPanel(new BorderLayout()));

        progressBar.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        showPanel.add(progressBar, BorderLayout.CENTER);
        showPanel.setBorder(BorderFactory.createEmptyBorder(5, 7, 5, 7));
        //mainPanel.add(Box.createVerticalStrut(15));
        //showPanel.setPreferredSize(new Dimension(200,50));
        showPanel.setMinimumSize(new Dimension(200, 30));
        progressBar.setValue(0);
        progressBar.setStringPainted(false);
        //setStatusText(" ");
        //this.setSize(200, 50);
    }


    public final JProgressBar getProgressBar() {
        return progressBar;
    }

    public final void setStatusText(final String text) {
        progressBar.setString(text);
        final int widthText = progressBar.getFontMetrics(progressBar.getFont()).stringWidth(text);
        showPanel.setPreferredSize(new Dimension(widthText + 30, 30));
        showPanel.revalidate();
        this.pack();
    }

    public final void setTitle(final String title) {
        showPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), Lng.getLabel(title)));
        this.pack();
    }

}
