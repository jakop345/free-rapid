package cz.cvut.felk.timejuggler.gui;

import org.jdesktop.swingx.JXMultiSplitPane;
import org.jdesktop.swingx.MultiSplitLayout;

import javax.swing.*;
import java.awt.*;

/**
 * Sprava a vytvoreni hlavniho panelu
 * @author Vity
 */
public class MainPanelManager {
    private JPanel contentPanel = new JPanel(new BorderLayout());
    private static final String LEFT_TOP = "left.top";
    private static final String RIGHT_TOP = "right.top";
    private static final String LEFT_BOTTOM = "left.bottom";
    private static final String RIGHT_BOTTOM = "right.bottom";
    private final MenuManager menuManager = new MenuManager();
    private StatusBarManager statusBarManager;
    private ToolbarManager toolbarManager;


    public JXMultiSplitPane getMultiSplitPane() {
        return multiSplitPane;
    }

    private JXMultiSplitPane multiSplitPane;

    public MainPanelManager() {
        initComponents();
    }

    private void initComponents() {
        multiSplitPane = new JXMultiSplitPane();
        multiSplitPane.setDividerSize(4);
        multiSplitPane.setContinuousLayout(false);
        multiSplitPane.getMultiSplitLayout().setModel(new DefaultSplitPaneModel());
        multiSplitPane.add(new SmallCalendarManager().getComponent(), LEFT_TOP);
        multiSplitPane.add(getTaskList(), LEFT_BOTTOM);
        multiSplitPane.add(new EventsListManager().getComponent(), RIGHT_TOP);
        multiSplitPane.add(new JButton("Right Bottom"), RIGHT_BOTTOM);
        contentPanel.add(getToolbarManager().getComponent(), BorderLayout.NORTH);
        contentPanel.add(multiSplitPane, BorderLayout.CENTER);
        contentPanel.add(getStatusBarManager().getStatusBar(), BorderLayout.SOUTH);
    }

    private StatusBarManager getStatusBarManager() {
        if (statusBarManager == null)
            statusBarManager = new StatusBarManager();
        return statusBarManager;
    }

    public JComponent getComponent() {
        return contentPanel;
    }

    private JComponent getTaskList() {
        return new TaskListManager().getComponent();
    }

    public ToolbarManager getToolbarManager() {
        if (toolbarManager == null)
            toolbarManager = new ToolbarManager();
        return toolbarManager;

    }

    /**
     * A simplified SplitPaneLayout for common split pane needs. A common multi splitpane need is:
     */
    private static class DefaultSplitPaneModel extends MultiSplitLayout.Split {
        public DefaultSplitPaneModel() {
            MultiSplitLayout.Split col2 = new MultiSplitLayout.Split();
            MultiSplitLayout.Split col1 = new MultiSplitLayout.Split();
            col1.setRowLayout(false);
            //col1.setWeight(0.9);
            col2.setRowLayout(false);
            setChildren(col1, new MultiSplitLayout.Divider(), col2);
            col1.setChildren(new MultiSplitLayout.Leaf(LEFT_TOP), new MultiSplitLayout.Divider(), new MultiSplitLayout.Leaf(LEFT_BOTTOM));
            col2.setChildren(new MultiSplitLayout.Leaf(RIGHT_TOP), new MultiSplitLayout.Divider(), new MultiSplitLayout.Leaf(RIGHT_BOTTOM));
        }
    }

    public MenuManager getMenuManager() {
        return menuManager;
    }
}
