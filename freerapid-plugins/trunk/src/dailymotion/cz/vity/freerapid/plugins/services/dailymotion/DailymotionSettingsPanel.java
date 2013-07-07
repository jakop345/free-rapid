package cz.vity.freerapid.plugins.services.dailymotion;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author tong2shot
 */
public class DailymotionSettingsPanel extends JPanel {
    private DailymotionSettingsConfig config;

    public DailymotionSettingsPanel(DailymotionServiceImpl service) throws Exception {
        super();
        config = service.getConfig();
        initPanel();
    }

    private void initPanel() {
        final String[] qualityStrings = {"1080p (HD)", "720p (HD)", "480p (HQ)", "384p (SD)", "240p (LD)"};
        final int[] qualityIndexMap = {4, 3, 2, 1, 0};

        final JLabel qualityLabel = new JLabel("Preferred quality level:");
        final JComboBox qualityList = new JComboBox(qualityStrings);
        final JCheckBox subtitleCheck = new JCheckBox("Download subtitle");

        qualityLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        qualityList.setAlignmentX(Component.LEFT_ALIGNMENT);
        subtitleCheck.setAlignmentX(Component.LEFT_ALIGNMENT);
        int qs = config.getQualitySetting();
        for (int i = 0; i < qualityIndexMap.length; i++) {
            if (qualityIndexMap[i] == qs) {
                qualityList.setSelectedIndex(i);
                break;
            }
        }
        subtitleCheck.setSelected(config.isSubtitleDownload());

        qualityList.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                config.setQualitySetting(qualityIndexMap[qualityList.getSelectedIndex()]);
            }
        });

        subtitleCheck.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                config.setSubtitleDownload(subtitleCheck.isSelected());
            }
        });
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(qualityLabel);
        add(qualityList);
        add(subtitleCheck);
        add(Box.createRigidArea(new Dimension(0, 15)));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    }

}
