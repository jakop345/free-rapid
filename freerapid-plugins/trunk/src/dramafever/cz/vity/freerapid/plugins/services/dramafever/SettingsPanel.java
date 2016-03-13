package cz.vity.freerapid.plugins.services.dramafever;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author tong2shot
 */
class SettingsPanel extends JPanel {
    private SettingsConfig config;

    public SettingsPanel(DramaFeverServiceImpl service) throws Exception {
        super();
        config = service.getConfig();
        initPanel();
    }

    private void initPanel() {
        final JLabel qualityLabel = new JLabel("Preferred quality level:");
        final JComboBox<VideoQuality> qualityList = new JComboBox<VideoQuality>(VideoQuality.getItems());
        final JCheckBox checkSubtitle = new JCheckBox("Download subtitle", config.isDownloadSubtitle());
        final JCheckBox checkTor = new JCheckBox("<html>Enable Tor <small><sup>**)</sup></small></html>", config.isEnableTor());
        final JLabel lblTorNote = new JLabel("<html><small>**) Doesn't affect US and proxy users," +
                "<br>US and proxy users won't use Tor.</small></html>");

        qualityLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        qualityList.setAlignmentX(Component.LEFT_ALIGNMENT);
        checkSubtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        checkTor.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblTorNote.setAlignmentX(Component.LEFT_ALIGNMENT);

        qualityList.setSelectedItem(config.getVideoQuality());

        qualityList.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                config.setVideoQuality((VideoQuality) qualityList.getSelectedItem());
            }
        });
        checkSubtitle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                config.setDownloadSubtitle(checkSubtitle.isSelected());
            }
        });
        checkTor.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                config.setEnableTor(checkTor.isSelected());
            }
        });


        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(qualityLabel);
        add(qualityList);
        add(Box.createRigidArea(new Dimension(0, 5)));
        add(checkSubtitle);
        add(Box.createRigidArea(new Dimension(0, 5)));
        add(checkTor);
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(lblTorNote);
        add(Box.createRigidArea(new Dimension(0, 15)));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    }

}
