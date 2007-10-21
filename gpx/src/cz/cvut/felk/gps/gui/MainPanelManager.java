package cz.cvut.felk.gps.gui;

import cz.cvut.felk.gps.swing.TextComponentContextMenuListener;
import org.jdesktop.application.ApplicationContext;

import javax.swing.*;
import java.awt.*;

/**
 * Sprava a vytvoreni hlavniho panelu
 * @author Vity
 */
public class MainPanelManager {
    private JPanel contentPanel = new JPanel(new BorderLayout());

    private final ApplicationContext context;

    public MainPanelManager(ApplicationContext context) {
        this.context = context;
//        this.menuManager = new MenuManager(context);
        initComponents();
    }




    private void initComponents() {
//        contentPanel.add(getToolbarManager().getComponent(), BorderLayout.NORTH);
        contentPanel.add(new MainPanel(), BorderLayout.CENTER);
//        contentPanel.add(getStatusBarManager().getStatusBar(), BorderLayout.SOUTH);
//
        Toolkit.getDefaultToolkit().addAWTEventListener(new TextComponentContextMenuListener(), AWTEvent.MOUSE_EVENT_MASK);
    }

    public JComponent getComponent() {
        return contentPanel;
    }

}
