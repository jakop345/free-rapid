package net.wordrider.dialogs;

import net.wordrider.core.Lng;
import net.wordrider.core.swing.URLMouseClickAdapter;
import net.wordrider.utilities.Consts;
import net.wordrider.utilities.LogUtils;
import net.wordrider.utilities.Sound;
import net.wordrider.utilities.Swinger;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

/**
 * @author Vity
 */
public final class AboutDialog extends AppDialog {
    private JButton btnClose;
    private final JLabel splash = new JLabel();
    private final JLabel info = new JLabel();
    private final static Logger logger = Logger.getLogger(AboutDialog.class.getName());

    public AboutDialog(final Frame owner) {
        super(owner, true);
        //this.frame = owner;
        try {
            init();
        } catch (Exception e) {
            LogUtils.processException(logger, e);
        }
        Swinger.centerDialog(owner, this);
        this.setModal(true);
        this.setTitle(Lng.getLabel("dialog.about.title", Consts.APPVERSION));
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Sound.playSound("sound.wav");
            }
        });
        this.setVisible(true);
    }

    private void doCancelButtonAction() {
        doClose();
    }

    protected AbstractButton getCancelButton() {
        return btnClose;
    }

    private void init() {
        this.setResizable(false);
        final Container mainPanel = this.getContentPane();
        final JPanel btnPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(splash, BorderLayout.CENTER);

        final ImageIcon image = Swinger.getIcon("splash.gif");

        splash.setIcon(image);
        if (image != null) {
            splash.setPreferredSize(new Dimension(image.getIconWidth(), image.getIconHeight()));
        }

        //info.setOpaque(false);
        splash.setLayout(new BoxLayout(splash, BoxLayout.Y_AXIS));
        info.setBorder(new EmptyBorder(87, 25, 0, 0));
        info.setText(Lng.getLabel("dialog.about.text"));
        final JLabel link1 = new JLabel(Lng.getLabel("dialog.about.weblink", Consts.WEBURL));

        final JLabel link2 = new JLabel(Lng.getLabel("dialog.about.maillink", Consts.WEBMAIL));
        final Border leftBorder = new EmptyBorder(0, 25, 0, 0);
        link1.setSize(50, 20);
        link2.setSize(50, 20);
        link1.setBorder(leftBorder);
        link2.setBorder(leftBorder);
        link1.addMouseListener(new URLMouseClickAdapter(Consts.WEBURL));
        link2.addMouseListener(new URLMouseClickAdapter(Consts.WEBMAILCMD));
        splash.add(info);
        splash.add(link1);
        splash.add(link2);
        splash.setBorder(new LineBorder(Color.BLACK, 2, true));
        mainPanel.add(btnPanel, BorderLayout.SOUTH);
        btnPanel.setLayout(new GridBagLayout());
        final Dimension buttonSize = new Dimension(80, 25);

        btnClose = Swinger.getButton("dialog.about.btnClose");
        btnClose.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                doCancelButtonAction();
            }
        });
        btnClose.setMinimumSize(buttonSize);
//        btnClose.setPreferredSize(buttonSize);
//        btnClose.setMaximumSize(buttonSize);

        btnPanel.add(btnClose, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0
                , GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(4, 23, 4, 5), 0, 0));
        //this.setSize(350, 210);
        this.pack();
    }

}
