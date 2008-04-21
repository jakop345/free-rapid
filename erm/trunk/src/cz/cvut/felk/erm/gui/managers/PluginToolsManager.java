package cz.cvut.felk.erm.gui.managers;

import cz.cvut.felk.erm.gui.managers.interfaces.IAreaChangeListener;
import cz.cvut.felk.erm.gui.managers.interfaces.IFileInstance;
import cz.cvut.felk.erm.utilities.LogUtils;
import org.noos.xing.mydoggy.*;

import javax.swing.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

/**
 * @author Ladislav Vitasek
 */
public class PluginToolsManager implements IAreaChangeListener {
    private ToolWindowManager toolWindowManager;
    private ToolWindowGroup pluginGroup;
    private static final String PLUGINS_GROUP = "plugins";
    private List<PluginTool> tools = new Vector<PluginTool>(3);
    private final static Logger logger = Logger.getLogger(PluginToolsManager.class.getName());

    public PluginToolsManager(ManagerDirector director) {
        this.toolWindowManager = director.getDockingManager().getToolManager();
        this.pluginGroup = toolWindowManager.getToolWindowGroup(PLUGINS_GROUP);
        this.pluginGroup.setImplicit(false);
        this.pluginGroup.setVisible(false);
    }

    public final void addPluginTool(final PluginTool pluginTool) {
        registerNewOne(pluginTool, true);
    }

    private void registerNewOne(PluginTool pluginTool, final boolean activate) {
        final ToolWindow toolWindow = toolWindowManager.registerToolWindow(pluginTool.getName(),  // Id
                pluginTool.getTabName(),      // Title
                pluginTool.getIcon(),         // Icon
                pluginTool.getComponent(),    // Component
                ToolWindowAnchor.LEFT);       // Anchor
        setupTool(toolWindow);
        tools.add(pluginTool);
        this.pluginGroup.addToolWindow(toolWindow);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                toolWindow.setVisible(false);
                toolWindow.setAvailable(false);
                toolWindow.setActive(false);
            }
        });
    }


    public void areaActivated(AreaChangeEvent event) {
        updateFileInstance(event.getFileInstance());
        if (event.getAreaManager().hasOpenedInstance()) {
            setToolsAvailable(true);
        }
    }

    private void setToolsAvailable(final boolean available) {
        final ToolWindow[] toolWindows = toolWindowManager.getToolWindows();
        for (ToolWindow toolWindow : toolWindows) {
            toolWindow.setAvailable(available);
            toolWindow.setVisible(available);
        }
    }

    public void areaDeactivated(AreaChangeEvent event) {
        updateFileInstance(null);
        if (!event.getAreaManager().hasOpenedInstance()) {
            setToolsAvailable(false);
        }
    }


    private void updateFileInstance(IFileInstance instance) {
        for (PluginTool tool : new LinkedList<PluginTool>(tools)) {
            tool.setFileInstance(instance);
        }
    }

    public void closeSoftAllInstances(final boolean removeTabs) {
        for (PluginTool tool : new LinkedList<PluginTool>(tools)) {
            closeSoft(tool, removeTabs);
        }
    }

    final void closeSoft(final PluginTool tool, final boolean removeTab) {
        if (tool == null || !tools.contains(tool))
            return;

        boolean result = false;
        try {
            result = tool.closeSoft();
        } catch (Throwable throwable) {
            LogUtils.processException(logger, throwable);
        }
        if (result && removeTab)
            removeInstance(tool);
    }

    final void closeHard(final PluginTool anID) {
        if (anID == null || !tools.contains(anID))
            return;
        removeInstance(anID);
    }

    private synchronized void removeInstance(final PluginTool tool) {
        toolWindowManager.unregisterToolWindow(tool.getName());
        tools.remove(tool);
    }

    public final void selectPluginTool(final PluginTool pluginTool) {
        final ToolWindow window = toolWindowManager.getToolWindow(pluginTool.getName());
        window.setActive(true);
    }


    protected void setupTool(ToolWindow tool) {

        DockedTypeDescriptor dockedTypeDescriptor = (DockedTypeDescriptor) tool.getTypeDescriptor(ToolWindowType.DOCKED);

        dockedTypeDescriptor.setDockLength(300);
        dockedTypeDescriptor.setPopupMenuEnabled(true);

//        JMenu toolsMenu = dockedTypeDescriptor.getToolsMenu();
//        toolsMenu.add(new AbstractAction("Hello World!!!") {
//            public void actionPerformed(ActionEvent e) {
//                JOptionPane.showMessageDialog(frame, "Hello World!!!");
//            }
//        });

//        dockedTypeDescriptor.setToolWindowActionHandler(new ToolWindowActionHandler() {
//            public void onHideButtonClick(ToolWindow toolWindow) {
//                JOptionPane.showMessageDialog(frame, "Hiding...");
//                toolWindow.setVisible(false);
//            }
//        });
        dockedTypeDescriptor.setAnimating(false);
        dockedTypeDescriptor.setPreviewEnabled(true);
        dockedTypeDescriptor.setPreviewDelay(1500);
        dockedTypeDescriptor.setPreviewTransparentRatio(0.4f);

        SlidingTypeDescriptor slidingTypeDescriptor = (SlidingTypeDescriptor) tool.getTypeDescriptor(ToolWindowType.SLIDING);
        slidingTypeDescriptor.setEnabled(false);
        slidingTypeDescriptor.setTransparentMode(true);
        slidingTypeDescriptor.setTransparentRatio(0.8f);
        slidingTypeDescriptor.setTransparentDelay(0);
        slidingTypeDescriptor.setAnimating(false);

        FloatingTypeDescriptor floatingTypeDescriptor = (FloatingTypeDescriptor) tool.getTypeDescriptor(ToolWindowType.FLOATING);
        floatingTypeDescriptor.setEnabled(true);
        floatingTypeDescriptor.setLocation(150, 200);
        floatingTypeDescriptor.setSize(320, 200);
        floatingTypeDescriptor.setModal(false);
        floatingTypeDescriptor.setTransparentMode(true);
        floatingTypeDescriptor.setTransparentRatio(0.2f);
        floatingTypeDescriptor.setTransparentDelay(1000);
        floatingTypeDescriptor.setAnimating(false);

        FloatingTypeDescriptor descriptor = (FloatingTypeDescriptor) tool.getTypeDescriptor(ToolWindowType.FLOATING_FREE);
        descriptor.setEnabled(false);
//        descriptor.setLocation(150, 200);
//        descriptor.setSize(320, 200);
//        descriptor.setModal(false);
//        descriptor.setTransparentMode(true);
//        descriptor.setTransparentRatio(0.2f);
//        descriptor.setTransparentDelay(1000);
//        descriptor.setAnimating(true);

    }

    public void lookAndFeelChanged() {

    }

    public void updateData() {
        for (PluginTool tool : new LinkedList<PluginTool>(tools)) {
            tool.updateData();
        }
    }


    public void setPluginToolsVisible(boolean b) {
        setToolsAvailable(b);
    }
}
