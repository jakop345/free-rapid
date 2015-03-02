package cz.vity.freerapid.plugins.services.crunchyroll;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author tong2shot
 */
class SettingsPanel extends JPanel {
    private SettingsConfig config;

    public SettingsPanel(CrunchyRollServiceImpl service) throws Exception {
        super();
        config = service.getConfig();
        initPanel();
    }

    private void initPanel() {
        final JLabel lblQuality = new JLabel("Preferred quality level:");
        final JComboBox<VideoQuality> cbbVideoQuality = new JComboBox<VideoQuality>(VideoQuality.getItems());
        final JCheckBox checkSubtitles = new JCheckBox("Download subtitle", config.isDownloadSubtitle());

        lblQuality.setAlignmentX(Component.LEFT_ALIGNMENT);
        cbbVideoQuality.setAlignmentX(Component.LEFT_ALIGNMENT);
        checkSubtitles.setAlignmentX(Component.LEFT_ALIGNMENT);

        cbbVideoQuality.setSelectedItem(config.getVideoQuality());

        cbbVideoQuality.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                config.setVideoQuality((VideoQuality) cbbVideoQuality.getSelectedItem());
            }
        });
        checkSubtitles.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                config.setDownloadSubtitle(checkSubtitles.isSelected());
            }
        });

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(lblQuality);
        add(cbbVideoQuality);
        add(Box.createRigidArea(new Dimension(0, 5)));
        add(checkSubtitles);
        setBorder(BorderFactory.createEmptyBorder(5, 15, 15, 15));
    }

}
