package cz.vity.freerapid.plugins.services.barrandov;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Kajda, JPEXS, ntoskrnl
 */
public class BarrandovSettingsPanel extends JPanel {
    private BarrandovSettingsConfig config;
    private BarrandovServiceImpl service;

    public BarrandovSettingsPanel(BarrandovServiceImpl service) throws Exception {
        super();
        this.service = service;
        config = service.getConfig();
        initPanel();
    }

    private void initPanel() {
        final String[] qualityStrings = {"360p SD","720p HD"};

        final JLabel qualityLabel = new JLabel("Preferred quality level:");
        final JComboBox qualityList = new JComboBox(qualityStrings);
        final JButton accountButton = new JButton("Premium Account");
        qualityLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        qualityList.setAlignmentX(Component.LEFT_ALIGNMENT);
        accountButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        int qs = config.getQualitySetting();
        qualityList.setSelectedIndex(qs);
        qualityList.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                config.setQualitySetting(qualityList.getSelectedIndex());
            }
        });
        accountButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                service.showAccount();
            }
        });
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(qualityLabel);
        add(qualityList);
        add(new JLabel(" "));
        add(accountButton);
        //add(Box.createRigidArea(new Dimension(0, 15)));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    }

}