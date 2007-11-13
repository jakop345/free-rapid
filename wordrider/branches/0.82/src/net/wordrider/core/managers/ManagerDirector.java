package net.wordrider.core.managers;

import net.wordrider.core.MainApp;
import net.wordrider.core.managers.interfaces.IRiderManager;

import javax.swing.*;
import java.awt.*;

/**
 * @author Vity
 */
public final class ManagerDirector implements IRiderManager {
    private final JPanel rootContainer;
    private AreaManager areaManager;
    private MenuManager menuManager;
    private StatusbarManager statusbarManager;
    private PluginToolsManager pluginsToolManager;
    private final JFrame mainFrame;
    private ToolbarManager toolbarManager;
    private DataDividerManager dataDividerManager;
    private TitleManager titleManager;

    public ManagerDirector(final JFrame mainFrame, final JPanel rootContainer) {
        this.mainFrame = mainFrame;
        this.rootContainer = rootContainer;
        init();
    }

    private void init() {
        //menu
        MainApp.makeProgress();
        getMenuManager();
        //toolbar

        //content
        //MainApp.makeProgress();
        this.areaManager = new AreaManager(this);
        //MainApp.makeProgress();
        //areaManager.newFile();
        //MainApp.makeProgress();
        this.toolbarManager = new ToolbarManager();
        this.pluginsToolManager = new PluginToolsManager();
        this.titleManager = new TitleManager(mainFrame);
        //MainApp.makeProgress();
        //areaManager.getManagerComponent().setBackground(this.toolbarManager.getManagerComponent().getBackground());
        dataDividerManager = new DataDividerManager(rootContainer, pluginsToolManager.getManagerComponent(), areaManager.getManagerComponent());
        //MainApp.makeProgress();
        getStatusbarManager();
        rootContainer.add(this.toolbarManager.getManagerComponent(), BorderLayout.NORTH);
        rootContainer.add(dataDividerManager.getManagerComponent(), BorderLayout.CENTER);
        rootContainer.add(this.statusbarManager.getManagerComponent(), BorderLayout.SOUTH);
        areaManager.addAreaChangeListener(pluginsToolManager);
        areaManager.addAreaChangeListener(statusbarManager);
        areaManager.addAreaChangeListener(menuManager);
        areaManager.addAreaChangeListener(toolbarManager);
        areaManager.addAreaChangeListener(this.titleManager);
        areaManager.addFileChangeListener(toolbarManager);
        areaManager.addFileChangeListener(dataDividerManager);
        //MainApp.makeProgress();
        //this.dataDividerManager
        //statusbar
    }


    public DataDividerManager getDataDividerManager() {
        return dataDividerManager;
    }

    public final PluginToolsManager getPluginToolsManager() {
        return this.pluginsToolManager;
    }

    public final AreaManager getAreaManager() {
        return areaManager;
    }

    public MenuManager getMenuManager() {
        return (this.menuManager == null) ? this.menuManager = new MenuManager(this) : this.menuManager;
    }

    public final JFrame getMainFrame() {
        return mainFrame;
    }


    public final ToolbarManager getToolbarManager() {
        return (this.toolbarManager == null) ? this.toolbarManager = new ToolbarManager() : this.toolbarManager;
    }

    public final StatusbarManager getStatusbarManager() {
        return (this.statusbarManager == null) ? this.statusbarManager = new StatusbarManager() : this.statusbarManager;
    }

    public final TitleManager getTitleChanger() {
        return this.titleManager;
    }

    public final Component getManagerComponent() {
        return rootContainer;
    }

    public void beforeLookAndFeelUpdate() {
        getToolbarManager().updateToolbar();
    }

    public void afterLookAndFeelUpdate() {
        getPluginToolsManager().lookAndFeelChanged();
    }

}
