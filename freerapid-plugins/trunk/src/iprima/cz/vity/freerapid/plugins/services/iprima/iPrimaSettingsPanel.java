package cz.vity.freerapid.plugins.services.iprima;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author JPEXS
 * @author ntoskrnl
 */
class iPrimaSettingsPanel extends JPanel {
    private iPrimaSettingsConfig config;

    public iPrimaSettingsPanel(iPrimaServiceImpl service) throws Exception {
        super();
        config = service.getConfig();
        initPanel();
    }

    private void initPanel() {
        final JLabel lblQuality = new JLabel("Preferred quality level:");
        final JComboBox<VideoQuality> cbbQuality = new JComboBox<VideoQuality>(VideoQuality.values());

        lblQuality.setAlignmentX(Component.LEFT_ALIGNMENT);
        cbbQuality.setAlignmentX(Component.LEFT_ALIGNMENT);

        cbbQuality.setSelectedItem(config.getVideoQuality());

        cbbQuality.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                config.setVideoQuality((VideoQuality) cbbQuality.getSelectedItem());
            }
        });

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(lblQuality);
        add(cbbQuality);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    }

}