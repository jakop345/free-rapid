package cz.vity.freerapid.plugins.services.youporn;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author birchie
 */
class YouPornSettingsPanel extends JPanel {
    private YouPornSettingsConfig config;

    public YouPornSettingsPanel(YouPornServiceImpl service) throws Exception {
        super();
        config = service.getConfig();
        initPanel();
    }

    private static final String[] qualityStrings = {"Small", "Medium", "Large", "MAXIMUM"};
    private static final String[] qualDescStrings = {"MP4 - For iPhone/iPod", "MP4 - For Windows 7/8, Mac and iPad", "MP4 HD - For Windows 7/8, Mac and iPad", "Largest Available"};
    private static final String[] qualSizeStrings = {"240p", "480p", "720p", "1080p"};
    private static final String[] qualTypeStrings = {".mp4", ".mp4", ".mp4", ".mp4"};

    public String getQualitySelection(final int quality) {
        return qualSizeStrings[quality];
    }

    public String getQualityType(final int quality) {
        return qualTypeStrings[quality];
    }

    private void initPanel() {
        final JLabel qualityLabel = new JLabel("Preferred video quality:");
        final JComboBox qualityList = new JComboBox(qualityStrings);
        final JLabel qualDescLabel = new JLabel("");
        qualityLabel.setLabelFor(qualityList);
        qualityLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        qualityList.setAlignmentX(Component.LEFT_ALIGNMENT);
        qualDescLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        qualityList.setSelectedIndex(config.getVideoQuality());
        qualDescLabel.setText(qualDescStrings[config.getVideoQuality()]);

        qualityList.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                qualDescLabel.setText(qualDescStrings[qualityList.getSelectedIndex()]);
                config.setVideoQuality(qualityList.getSelectedIndex());
            }
        });

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(qualityLabel);
        add(qualityList);
        add(new JLabel(" "));
        add(qualDescLabel);
        add(Box.createRigidArea(new Dimension(0, 15)));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    }
}
