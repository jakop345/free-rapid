package net.wordrider.core.managers;

import net.wordrider.area.RiderArea;
import net.wordrider.area.actions.GetNextTabAction;
import net.wordrider.area.actions.GetPrevTabAction;
import net.wordrider.core.AppPrefs;
import net.wordrider.core.MainApp;
import net.wordrider.core.actions.*;
import net.wordrider.core.managers.interfaces.*;
import net.wordrider.utilities.Swinger;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Vity
 */
public final class AreaManager extends TabManager<FileInstance> implements InstanceListener {
    //private final ManagerDirector director;
    private final EventListenerList listenerList = new EventListenerList();
//    private final Collection<IAreaChangeListener> areaChangelisteners = new ArrayList<IAreaChangeListener>(4);
    //    private final Collection<IFileChangeListener> fileStatusListeners = new HashSet<IFileChangeListener>(2);
    private final RecentFilesManager recentFilesManager;    

    public AreaManager(final ManagerDirector director) {
        super();    //call to super
        //this.director = director;
        recentFilesManager = new RecentFilesManager(director.getMenuManager());
        addFileChangeListener(recentFilesManager);
        tabbedPane.setFocusable(true);//must be
        applyTabLayout();
//        tabbedPane.setFocusCycleRoot(true);
//        tabbedPane.setFocusTraversalPolicy(new LayoutFocusTraversalPolicy() {
//            public Component getDefaultComponent(Container cont) {
//                final IFileInstance instance = getActiveInstance();
//                if (instance != null)
//                    return instance.getRiderArea();
//                else return null;
//            }
//        });
//        tabbedPane.addMouseListener(new MouseAdapter() {
//            public void mousePressed(final MouseEvent e) {
//                System.out.println("click 3x!");
//            }
//
//            public void mouseReleased(final MouseEvent e) {
//                super.mouseReleased(e);    //call to super
//                System.out.println("released");
//            }
//
//            public void mouseClicked(final MouseEvent e) {
//                //                if (e.getClickCount() > 1) {
//                System.out.println("click 2x!");
//                //                }
//            }
//        });
        tabbedPane.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {

                if ((SwingUtilities.isMiddleMouseButton(e) || SwingUtilities.isLeftMouseButton(e)) && e.getClickCount() >= 2) {
                    e.consume();
                    JTabbedPane tabbedPane = (JTabbedPane)e.getSource();
                    int tabIdx = tabbedPane.getUI().tabForCoordinate(tabbedPane, e.getX(), e.getY());
                    if (tabIdx == -1)
                        CreateNewFileAction.getInstance().actionPerformed(null);
                    else
                        CloseActiveAction.getInstance().actionPerformed(null);
                } else if (SwingUtilities.isRightMouseButton(e)) {
                    final JPopupMenu popup = new JPopupMenu("popup");
                    if (getOpenedInstanceCount() > 0) {
                        popup.add(CloseActiveAction.getInstance());
                        popup.add(CloseAllButThisAction.getInstance());
                        popup.add(CloseAllAction.getInstance());
                        popup.add(CloseAllUnmodifiedAction.getInstance());
                        popup.addSeparator();
                        popup.add(GetNextTabAction.getInstance());
                        popup.add(GetPrevTabAction.getInstance());
                    } else {
                        popup.add(CreateNewFileAction.getInstance());
                    }
                    popup.show(tabbedPane, e.getX(), e.getY());
                }
            }
        });

    }


    public final void activateInstance(IFileInstance instance) {
        if (instance.equals(getActiveInstance()))
            return;
        for (FileInstance fileInstance : runningInstances()) {
            if (fileInstance.equals(instance))
                setActivateFileInstance(fileInstance);
        }
    }

    final public IFileInstance isFileAlreadyOpened(final File f) {
        for (IFileInstance instance : runningInstances()) {
            if (f.equals(instance.getFile()))
                return instance;
        }
        return null;
    }

    final public void openFileInstance() {
        openFileInstance(new FileInstance());
        //  activateInstance(id, instance);
        // MainApp.getMainApp().getMainAppFrame().setTitle();
        //tabbedPane.validate();
        //        tabbedPane.repaint();
    }

    final public void openFileInstance(final FileInstance fileInstance) {
        final Integer id = registerNewOne(fileInstance);
        fileInstance.setInternalId(id);
        fireFileOpened(fileInstance);
        fileInstance.addInstanceListener(this);
//        for (IFileChangeListener fileStatusListener : fileStatusListeners)
//            (fileStatusListener).fileWasOpened(fileInstance);
        //        fileInstance.getRiderArea().grabFocus();
        //        SwingUtilities.invokeLater(
        //                new Runnable() {
        //                    public void run() {
        //                        fileInstance.getRiderArea().grabFocus();
        //                    }
        //                }
        //        );
        //  activateInstance(id, instance);
        // MainApp.getMainApp().getMainAppFrame().setTitle();
        //tabbedPane.validate();
        //        tabbedPane.repaint();
    }



    final protected void deactivateInstance(final Object anID) {
        super.deactivateInstance(anID);    //call to super
        //director.getPluginToolsManager().selectedAreaChanged(null);
        fireAreaDeactivated();
    }

    final protected void activateInstance(final Object anID) {
        super.activateInstance(anID);    //call to super
        fireAreaActivated();
    }

    private void fireAreaActivated() {
        final IFileInstance instance = getActiveInstance();
        if (instance == null)
            return;
        Object[] listeners = this.listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        AreaChangeEvent event = null;
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == IAreaChangeListener.class) {
                // Lazily create the event:
                if (event == null)
                    event = new AreaChangeEvent(this, instance);
                ((IAreaChangeListener) listeners[i + 1]).areaActivated(event);
            }
        }
    }

    private void fireFileOpened(final IFileInstance fileInstance) {
        Object[] listeners = this.listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        FileChangeEvent event = null;
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == IFileChangeListener.class) {
                // Lazily create the event:
                if (event == null)
                    event = new FileChangeEvent(this, fileInstance);
                ((IFileChangeListener) listeners[i + 1]).fileWasOpened(event);
            }
        }
    }

    private void fireAreaDeactivated() {
        final IFileInstance instance = getActiveInstance();
        assert instance != null;
        Object[] listeners = this.listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        AreaChangeEvent event = null;
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == IAreaChangeListener.class) {
                // Lazily create the event:
                if (event == null)
                    event = new AreaChangeEvent(this, instance);
                ((IAreaChangeListener) listeners[i + 1]).areaDeactivated(event);
            }
        }
    }

    private void setTabTitle(final IInformedTab informedPlugin) {
        final int index = tabbedPane.indexOfComponent(informedPlugin.getComponent());
        tabbedPane.setTitleAt(index, informedPlugin.getTabName());
    }

    final void addAreaChangeListener(final IAreaChangeListener listener) {
        listenerList.add(IAreaChangeListener.class, listener);
    }

    final public void addFileChangeListener(final IFileChangeListener listener) {
        listenerList.add(IFileChangeListener.class, listener);
    }

    public final Collection<FileInstance> getModifiedInstances() {
        final Collection<FileInstance> list = new ArrayList<FileInstance>();
        for (FileInstance fi : runningInstances()) {
            if (fi.isModified())
                list.add(fi);
        }
        return list;
    }

    public void updateHighlightCurrentLine() {
        final boolean property = AppPrefs.getProperty(AppPrefs.HIGHLIGHT_LINE, true);
        for (IFileInstance instance : runningInstances()) {
            ((RiderArea) instance.getRiderArea()).setCurrentLineHighlight(property);
        }
        repaintActive();
    }

    public void repaintActive() {
        final IFileInstance instance = getActiveInstance();
        if (instance != null) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    instance.getRiderArea().repaint();
                }
            });
        }
    }

    public void updateBracketMatching() {
        final boolean property = AppPrefs.getProperty(AppPrefs.MATCH_BRACKETS, true);
        for (IFileInstance<RiderArea> instance : runningInstances()) {
            (instance.getRiderArea()).setBracketMatching(property);
        }
        repaintActive();
    }


    public final void setActivateFileInstance(final FileInstance instance) {
        tabbedPane.setSelectedComponent(instance.getComponent());
    }

    public final void closeInstanceHard(final FileInstance instance) {
        instance.removeInstanceListener(this);
        closeHard(runningTabs.get(instance.getComponent()));
        fireFileClosed(instance);
    }

    private void fireFileClosed(IFileInstance instance) {
        Object[] listeners = this.listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        FileChangeEvent event = null;
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == IFileChangeListener.class) {
                // Lazily create the event:
                if (event == null)
                    event = new FileChangeEvent(this, instance);
                ((IFileChangeListener) listeners[i + 1]).fileWasClosed(event);
            }
        }
    }

    public final boolean hasModifiedInstances() {
        for (FileInstance fi : runningInstances()) {
            if (fi.isModified())
                return true;
        }
        return false;
    }


    public final void closeActiveInstance() {
        final IFileInstance fileInstance = getActiveInstance();
        if (fileInstance != null) {
            closeSoft(activeInstanceID, true);
            fireFileClosed(fileInstance);
        }
    }

    public RecentFilesManager getRecentFilesManager() {
        return recentFilesManager;
    }

    public final int getOpenedInstanceCount() {
        return runningInstancesIDs.size();
    }

    public boolean hasOpenedInstance() {
        return this.getOpenedInstanceCount() > 0;
    }


    public void grabActiveFocus() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                final IFileInstance instance = getActiveInstance();
                if (instance != null)
                    grabActiveFocus(instance);
            }
        });
    }

    public static void grabActiveFocus(final IFileInstance instance) {
        Swinger.inputFocus(instance.getRiderArea());
    }


    public void applyTabLayout() {
        tabbedPane.setTabLayoutPolicy(AppPrefs.getProperty(AppPrefs.SCROLL_LAYOUT, true) ? JTabbedPane.SCROLL_TAB_LAYOUT : JTabbedPane.WRAP_TAB_LAYOUT);
    }

    public static AreaManager getInstance() {
        return MainApp.getInstance().getMainAppFrame().getManagerDirector().getAreaManager();
    }

    public void instanceModifiedStatusChanged(InstanceEvent e) {
        fileAssigned(e);
    }

    public void fileAssigned(InstanceEvent e) {
        setTabTitle((IInformedTab) e.getSource());
    }
}
