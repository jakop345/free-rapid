package cz.vity.freerapid.plugins.services.vshare_io;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author birchie
 */
class VShare_ioSettingsPanel extends JPanel {
    private VShare_ioSettingsConfig config;

    public VShare_ioSettingsPanel(VShare_ioServiceImpl service) throws Exception {
        super();
        config = service.getConfig();
        initPanel();
    }

    private static final String[] qualityStrings = {"File", "Stream"};

    private void initPanel() {
        final JLabel qualityLabel = new JLabel("Preferred download source:");
        final JComboBox qualityList = new JComboBox(qualityStrings);
        qualityLabel.setLabelFor(qualityList);
        qualityLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        qualityList.setAlignmentX(Component.LEFT_ALIGNMENT);
        qualityList.setSelectedIndex(config.getVideoQuality());

        qualityList.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                config.setVideoQuality(qualityList.getSelectedIndex());
            }
        });

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(qualityLabel);
        add(qualityList);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    }
}
