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
        final JLabel lblProtocol = new JLabel("Preferred protocol:");
        final JComboBox<Protocol> cbbProtocol = new JComboBox<Protocol>(Protocol.values());
        final JLabel lblQuality = new JLabel("Preferred quality level:");
        final JComboBox<VideoQuality> cbbQuality = new JComboBox<VideoQuality>(VideoQuality.values());
        final JLabel lblPort = new JLabel("Preferred RTMP port:");
        final JComboBox cbbPort = new JComboBox<Port>(Port.values());

        lblProtocol.setAlignmentX(Component.LEFT_ALIGNMENT);
        cbbProtocol.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblQuality.setAlignmentX(Component.LEFT_ALIGNMENT);
        cbbQuality.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblPort.setAlignmentX(Component.LEFT_ALIGNMENT);
        cbbPort.setAlignmentX(Component.LEFT_ALIGNMENT);

        cbbProtocol.setSelectedItem(config.getProtocol());
        cbbQuality.setSelectedItem(config.getVideoQuality());
        cbbPort.setSelectedItem(config.getPort());

        cbbPort.setEnabled(config.getProtocol() == Protocol.RTMP);

        cbbProtocol.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Protocol selectedProtocol = (Protocol) cbbProtocol.getSelectedItem();
                config.setProtocol(selectedProtocol);
                cbbPort.setEnabled(selectedProtocol == Protocol.RTMP);
            }
        });
        cbbQuality.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                config.setVideoQuality((VideoQuality) cbbQuality.getSelectedItem());
            }
        });
        cbbPort.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                config.setPort((Port) cbbPort.getSelectedItem());
            }
        });

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(lblProtocol);
        add(cbbProtocol);
        add(Box.createRigidArea(new Dimension(0, 5)));
        add(lblQuality);
        add(cbbQuality);
        add(Box.createRigidArea(new Dimension(0, 5)));
        add(lblPort);
        add(cbbPort);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    }

}