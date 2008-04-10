package cz.cvut.felk.erm.gui.managers;

import cz.cvut.felk.erm.core.MainApp;
import cz.cvut.felk.erm.gui.actions.FileTransferHandlerImpl;
import cz.cvut.felk.erm.gui.managers.interfaces.IFileChangeListener;
import cz.cvut.felk.erm.swing.TextComponentContextMenuListener;
import org.jdesktop.application.ApplicationContext;

import javax.swing.*;
import java.awt.*;

/**
 * Sprava a vytvoreni hlavniho panelu
 *
 * @author Ladislav Vitasek
 */
public class ManagerDirector implements IFileChangeListener {
    private final ApplicationContext context;
    private final MenuManager menuManager;
    private StatusBarManager statusBarManager;
    private ToolbarManager toolbarManager;
    private PluginToolsManager pluginToolsManager;
    private AreaManager areaManager;
    private BackgroundManager backgroundManager;
    private TitleManager titleManager;
    private DockingManager dockingManager;
    private Container rootContainer;
    private JFrame mainFrame;


    public ManagerDirector(ApplicationContext context) {
        this.context = context;
        this.menuManager = new MenuManager(context, this);
        initComponents();
    }


    private void initComponents() {
        mainFrame = ((MainApp) context.getApplication()).getMainFrame();

        mainFrame.getRootPane().setTransferHandler(new FileTransferHandlerImpl());

        this.rootContainer = new JPanel();
        this.rootContainer.setPreferredSize(new Dimension(700, 550));

        rootContainer.setLayout(new BorderLayout());

        this.dockingManager = new DockingManager(this, mainFrame);

        this.areaManager = new AreaManager(this);


        this.backgroundManager = new BackgroundManager(this);
        this.pluginToolsManager = new PluginToolsManager(this);
        this.titleManager = new TitleManager(this);

        rootContainer.add(getToolbarManager().getComponent(), BorderLayout.NORTH);
        rootContainer.add(getDockingManager().getToolManager(), BorderLayout.CENTER);
        rootContainer.add(getStatusBarManager().getStatusBar(), BorderLayout.SOUTH);

        areaManager.addAreaChangeListener(pluginToolsManager);
        areaManager.addAreaChangeListener(statusBarManager);
        areaManager.addAreaChangeListener(menuManager);
        areaManager.addAreaChangeListener(toolbarManager);
        areaManager.addAreaChangeListener(this.titleManager);

        areaManager.addFileChangeListener(toolbarManager);
        areaManager.addFileChangeListener(backgroundManager);


        Toolkit.getDefaultToolkit().addAWTEventListener(new TextComponentContextMenuListener(), AWTEvent.MOUSE_EVENT_MASK);
    }

    private StatusBarManager getStatusBarManager() {
        if (statusBarManager == null)
            statusBarManager = new StatusBarManager(this, context);
        return statusBarManager;
    }

    public Container getComponent() {
        return rootContainer;
    }


    public ToolbarManager getToolbarManager() {
        if (toolbarManager == null)
            toolbarManager = new ToolbarManager(context);
        return toolbarManager;

    }


    public void beforeLookAndFeelUpdate() {
        getToolbarManager().updateToolbar();
    }

    public void afterLookAndFeelUpdate() {
        getPluginToolsManager().lookAndFeelChanged();
    }

    public MenuManager getMenuManager() {
        return menuManager;
    }

    public AreaManager getAreaManager() {
        return areaManager;
    }


    public Container getRootContainer() {
        return rootContainer;
    }

    public void fileWasOpened(FileChangeEvent event) {

    }

    public void fileWasClosed(FileChangeEvent event) {

    }

    public JFrame getMainFrame() {
        return mainFrame;
    }

    public PluginToolsManager getPluginToolsManager() {
        return pluginToolsManager;
    }


    public BackgroundManager getBackgroundManager() {
        return backgroundManager;
    }

    public DockingManager getDockingManager() {
        return dockingManager;
    }


    public ApplicationContext getContext() {
        return context;
    }
}
