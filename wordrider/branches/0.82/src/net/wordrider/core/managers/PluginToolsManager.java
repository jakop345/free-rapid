package net.wordrider.core.managers;

import net.wordrider.core.MainApp;
import net.wordrider.core.managers.interfaces.IAreaChangeListener;
import net.wordrider.core.managers.interfaces.IFileInstance;
import net.wordrider.plugintools.PluginTool;

import javax.swing.*;

/**
 * @author Vity
 */
public final class PluginToolsManager extends TabManager<PluginTool> implements IAreaChangeListener {

    public PluginToolsManager() {
        super(JTabbedPane.LEFT);    //call to super
    }


    public final void addPluginTool(final PluginTool pluginTool) {
        registerNewOne(pluginTool, false);
    }


    public void areaActivated(AreaChangeEvent event) {
        updateFileInstance(event.getFileInstance());
    }

    public void areaDeactivated(AreaChangeEvent event) {
        updateFileInstance(null);
    }


    private void updateFileInstance(IFileInstance instance) {
        final PluginTool tool = getActiveInstance();
        if (tool != null)
            tool.setFileInstance(instance);
    }

    public final void selectPluginTool(final PluginTool pluginTool) {
        tabbedPane.setSelectedComponent(pluginTool.getComponent());
    }

    public static PluginToolsManager getInstance() {
        return MainApp.getInstance().getMainAppFrame().getManagerDirector().getPluginToolsManager();
    }
}
