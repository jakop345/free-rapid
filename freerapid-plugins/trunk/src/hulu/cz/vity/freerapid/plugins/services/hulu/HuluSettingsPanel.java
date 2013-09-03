package cz.vity.freerapid.plugins.services.hulu;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

/**
 * @author tong2shot
 */
public class HuluSettingsPanel extends JPanel {

    private HuluSettingsConfig config;
    private final static String[] qualityStrings = {"Highest available", "480p", "360p", "240p", "Lowest available"};
    private final static int[] qualityIndexMap = {HuluSettingsConfig.MAX_HEIGHT_INDEX, 3, 2, 1, HuluSettingsConfig.MIN_HEIGHT_INDEX}; //to anticipate higher quality (576,720,1080,2160,4320,etc) in the future
    private final static String[] videoFormatStrings = {"Any video format", "H264", "VP6"};
    private final static String[] cdnStrings = {"Akamai", "Limelight", "Level3"};
    private final static String[] portStrings = {"1935", "80"};

    public HuluSettingsPanel(HuluServiceImpl service) throws Exception {
        super();
        config = service.getConfig();
        initPanel();
    }

    private void initPanel() {
        final JLabel lblUsername = new JLabel("Email address:");
        final JTextField txtfldUsername = new JTextField(40);
        final JLabel lblPassword = new JLabel("Password:");
        final JPasswordField pswdfldPassword = new JPasswordField(40);
        final JLabel lblQuality = new JLabel("Preferred quality level:");
        final JComboBox<String> cbbQuality = new JComboBox<String>(qualityStrings);
        final JLabel lblVideoFormat = new JLabel("Preferred video format:");
        final JComboBox<String> cbbVideoFormat = new JComboBox<String>(videoFormatStrings);
        final JLabel lblCdn = new JLabel("Preferred CDN:");
        final JComboBox<String> cbbCdn = new JComboBox<String>(cdnStrings);
        final JLabel lblPort = new JLabel("RTMP port:");
        final JComboBox<String> cbbPort = new JComboBox<String>(portStrings);
        final JCheckBox checkSubtitles = new JCheckBox("Download subtitles", config.isDownloadSubtitles());
        final JCheckBox checkTunlr = new JCheckBox("Enable Tunlr *)", config.isTunlrEnabled());
        final JLabel lblTunlrDesc = new JLabel("*) Unless you are US user or using US proxy, you should enable Tunlr.");

        lblUsername.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtfldUsername.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblPassword.setAlignmentX(Component.LEFT_ALIGNMENT);
        pswdfldPassword.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblQuality.setAlignmentX(Component.LEFT_ALIGNMENT);
        cbbQuality.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblVideoFormat.setAlignmentX(Component.LEFT_ALIGNMENT);
        cbbVideoFormat.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblCdn.setAlignmentX(Component.LEFT_ALIGNMENT);
        cbbCdn.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblPort.setAlignmentX(Component.LEFT_ALIGNMENT);
        cbbPort.setAlignmentX(Component.LEFT_ALIGNMENT);
        checkSubtitles.setAlignmentX(Component.LEFT_ALIGNMENT);
        checkTunlr.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblTunlrDesc.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtfldUsername.setText(config.getUsername());
        pswdfldPassword.setText(config.getPassword());
        int qs = config.getQualityHeightIndex();
        for (int i = 0; i < qualityIndexMap.length; i++) {
            if (qualityIndexMap[i] == qs) {
                cbbQuality.setSelectedIndex(i);
                break;
            }
        }
        cbbVideoFormat.setSelectedIndex(config.getVideoFormatIndex());
        cbbCdn.setSelectedIndex(config.getCdnIndex());
        cbbPort.setSelectedIndex(config.getPortIndex());

        txtfldUsername.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                //
            }

            @Override
            public void focusLost(FocusEvent e) {
                config.setUsername(txtfldUsername.getText().trim().isEmpty() ? null : txtfldUsername.getText());
            }
        });

        pswdfldPassword.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                //
            }

            @Override
            public void focusLost(FocusEvent e) {
                config.setPassword((pswdfldPassword.getPassword().length == 0) ? null : new String(pswdfldPassword.getPassword()));
            }
        });

        cbbQuality.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                config.setQualityHeightIndex(qualityIndexMap[cbbQuality.getSelectedIndex()]);
            }
        });

        cbbVideoFormat.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                config.setVideoFormatIndex(cbbVideoFormat.getSelectedIndex());
            }
        });

        cbbCdn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                config.setCdnIndex(cbbCdn.getSelectedIndex());
            }
        });

        cbbPort.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                config.setPortIndex(cbbPort.getSelectedIndex());
            }
        });

        checkSubtitles.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                config.setDownloadSubtitles(checkSubtitles.isSelected());
            }
        });

        checkTunlr.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                config.setTunlrEnabled(checkTunlr.isSelected());
            }
        });

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(lblUsername);
        add(txtfldUsername);
        add(lblPassword);
        add(pswdfldPassword);
        add(lblQuality);
        add(cbbQuality);
        add(lblVideoFormat);
        add(cbbVideoFormat);
        add(lblCdn);
        add(cbbCdn);
        add(lblPort);
        add(cbbPort);
        add(Box.createRigidArea(new Dimension(0, 5)));
        add(checkSubtitles);
        add(checkTunlr);
        add(Box.createRigidArea(new Dimension(0, 15)));
        add(lblTunlrDesc);
        add(Box.createRigidArea(new Dimension(0, 15)));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    }

}
