package cz.vity.freerapid.plugins.services.copy_com;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author tong2shot
 */
class SettingsPanel extends JPanel {
    private SettingsConfig config;

    private final JCheckBox checkAppendPath = new JCheckBox("Append path to file name");

    public SettingsPanel(Copy_comServiceImpl service) throws Exception {
        super();
        config = service.getConfig();
        initPanel();
    }

    private void initPanel() {
        checkAppendPath.setSelected(config.isAppendPathToFilename());
        checkAppendPath.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                config.setAppendPathToFilename(checkAppendPath.isSelected());
            }
        });
        setLayout(new BorderLayout());
        add(checkAppendPath, BorderLayout.CENTER);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    }
}
