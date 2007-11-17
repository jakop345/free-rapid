package net.wordrider.core.managers;

import net.wordrider.core.MainApp;
import net.wordrider.core.managers.interfaces.IRiderManager;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;

import javax.swing.*;
import java.awt.*;

/**
 * @author Vity
 */
public final class ManagerDirector implements IRiderManager {
    private final Container rootContainer;
    private AreaManager areaManager;
    private MenuManager menuManager;
    private StatusbarManager statusbarManager;
    private PluginToolManager pluginsToolManager;
    private final JFrame mainFrame;
    private BackgroundManager backgroundManager;
    private TitleManager titleManager;
    private ToolbarManager toolbarManager;
    private MyDoggyToolWindowManager toolsManager;

    public ManagerDirector(final JFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.rootContainer = mainFrame.getContentPane();
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
        this.titleManager = new TitleManager(mainFrame);
        // Add myDoggyToolWindowManager to the frame. MyDoggyToolWindowManager is an extension of a JPanel
        //rootContainer.setLayout(new TableLayout(new double[][]{{0, -1, 0}, {0, -1, 0}}));
        rootContainer.setLayout(new BorderLayout());
        rootContainer.add(getDockingWindowManager(), BorderLayout.CENTER);

        //MainApp.makeProgress();
        //areaManager.getManagerComponent().setBackground(this.toolbarManager.getManagerComponent().getBackground());

        backgroundManager = new BackgroundManager(this);

        this.pluginsToolManager = new PluginToolManager(getDockingWindowManager());
        //MainApp.makeProgress();
        getStatusbarManager();
        rootContainer.add(this.toolbarManager.getManagerComponent(), BorderLayout.NORTH);
        //rootContainer.add(backgroundManager.getManagerComponent(), BorderLayout.CENTER);
        rootContainer.add(this.statusbarManager.getManagerComponent(), BorderLayout.SOUTH);
        areaManager.addAreaChangeListener(pluginsToolManager);
        areaManager.addAreaChangeListener(statusbarManager);
        areaManager.addAreaChangeListener(menuManager);
        areaManager.addAreaChangeListener(toolbarManager);
        areaManager.addAreaChangeListener(this.titleManager);
        areaManager.addFileChangeListener(toolbarManager);
        areaManager.addFileChangeListener(backgroundManager);
        //MainApp.makeProgress();
        //this.backgroundManager
        //statusbar
    }


    public BackgroundManager getDataDividerManager() {
        return backgroundManager;
    }

    public final PluginToolManager getPluginToolsManager() {
        return this.pluginsToolManager;
    }

    public AreaManager getAreaManager() {
        return areaManager;
    }

    public final MyDoggyToolWindowManager getDockingWindowManager() {
        return (this.toolsManager == null) ? this.toolsManager = new MyDoggyToolWindowManager(mainFrame) : this.toolsManager;
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
