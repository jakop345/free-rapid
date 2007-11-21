package net.wordrider.core.managers;

import net.wordrider.area.RiderArea;
import net.wordrider.core.AppPrefs;
import net.wordrider.core.MainApp;
import net.wordrider.core.managers.interfaces.IAreaChangeListener;
import net.wordrider.core.managers.interfaces.IFileChangeListener;
import net.wordrider.core.managers.interfaces.IFileInstance;
import net.wordrider.core.managers.interfaces.InstanceListener;
import net.wordrider.utilities.LogUtils;
import net.wordrider.utilities.Swinger;
import org.noos.xing.mydoggy.Content;
import org.noos.xing.mydoggy.ContentManager;
import org.noos.xing.mydoggy.ContentManagerListener;
import org.noos.xing.mydoggy.ContentManagerUIListener;
import org.noos.xing.mydoggy.event.ContentManagerEvent;
import org.noos.xing.mydoggy.event.ContentManagerUIEvent;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.logging.Logger;

/**
 * @author Vity
 */
public final class AreaManager implements InstanceListener, PropertyChangeListener {
    private final static Logger logger = Logger.getLogger(AreaManager.class.getName());

    private final EventListenerList listenerList = new EventListenerList();
    private final RecentFilesManager recentFilesManager;

    final Collection<FileInstance> runningInstances = new HashSet<FileInstance>(4);


    private static int anIDCounter = 0;
    private ContentManager contentManager;
    private FileInstance activeInstance = null;


    public AreaManager(final ManagerDirector director) {
        super();
        //  this.director = director;
        recentFilesManager = new RecentFilesManager(director.getMenuManager());
        contentManager = director.getDockingWindowManager().getContentManager();
        addFileChangeListener(recentFilesManager);
        Collections.synchronizedCollection(runningInstances);
        contentManager.getContentManagerUI().addContentManagerUIListener(new ContentManagerUIListener() {
            public boolean contentUIRemoving(ContentManagerUIEvent event) {
                final Content content = event.getContentUI().getContent();
                return closeInstance(getFileInstance(content), false);
            }

            public void contentUIDetached(ContentManagerUIEvent event) {

            }
        });
        contentManager.addContentManagerListener(new ContentManagerListener() {
            public void contentAdded(ContentManagerEvent event) {

            }

            public void contentRemoved(ContentManagerEvent event) {
                if (activeInstance != null && activeInstance.equals(getFileInstance(event.getContent())))
                    deactivateInstance(activeInstance);
            }

            public void contentSelected(ContentManagerEvent event) {
                final Content content = event.getContent();

                if (content != null) {
                    final FileInstance instance = getFileInstance(content);
                    if (activeInstance != null && !activeInstance.equals(instance))
                        deactivateInstance(activeInstance);
                    activateInstance(instance);
                    System.out.println("newly selected instance = " + content.getKey());
                }
            }
        });
    }

    private FileInstance getFileInstance(Content content) {
        return (FileInstance) content.getKey();
    }


    public final void activateInstance(IFileInstance instance) {
        if (instance.equals(getActiveInstance()))
            return;
        for (FileInstance fileInstance : runningInstances()) {
            if (fileInstance.equals(instance))
                setActivateFileInstance(fileInstance);
        }
    }

    public FileInstance getActiveInstance() {
        final Content content = contentManager.getSelectedContent();
        if (content != null)
            return getFileInstance(content);
        return null;
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
    }


    final public void openFileInstance(final FileInstance instance) {
        final Content content;
        instance.setInternalId(nextID());
        synchronized (this) {
            runningInstances.add(instance);
            content = contentManager.addContent(instance, instance.getTabName(), instance.getIcon(), instance.getComponent(), instance.getTip());
        }
        content.addPropertyChangeListener(this);
        try {
            ((JInternalFrame) ((MyDesktopContentManagerUI) contentManager.getContentManagerUI()).getContentUI(content)).setMaximum(true);
        } catch (PropertyVetoException e) {
            LogUtils.processException(logger, e);
        }


        fireFileOpened(instance);
        instance.addInstanceListener(this);

    }


    final protected void deactivateInstance(final FileInstance instance) {
        this.activeInstance = null;
        instance.deactivate();
        fireAreaDeactivated(instance);
    }

    final protected void activateInstance(final FileInstance instance) {
        this.activeInstance = instance;
        instance.activate();
        fireAreaActivated();
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
        final Content content = contentManager.getContent(instance);
        if (content != null)
            content.setSelected(true);
    }

    public final void closeInstanceHard(final FileInstance instance) {
        instance.removeInstanceListener(this);
        closeHard(instance);
        fireFileClosed(instance);
    }


    public final boolean hasModifiedInstances() {
        for (FileInstance fi : runningInstances()) {
            if (fi.isModified())
                return true;
        }
        return false;
    }


    public final void closeActiveInstance() {
        final FileInstance fileInstance = getActiveInstance();
        if (fileInstance != null) {
            closeInstance(fileInstance, true);
        }
    }

    private boolean closeInstance(FileInstance fileInstance, final boolean close) {
        final boolean result = closeSoft(fileInstance, close);
        if (result)
            fireFileClosed(fileInstance);
        return result;
    }

    public RecentFilesManager getRecentFilesManager() {
        return recentFilesManager;
    }

    public final int getOpenedInstanceCount() {
        return runningInstances.size();
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
//        tabbedPane.setTabLayoutPolicy(AppPrefs.getProperty(AppPrefs.SCROLL_LAYOUT, true) ? JTabbedPane.SCROLL_TAB_LAYOUT : JTabbedPane.WRAP_TAB_LAYOUT);
    }

    public static AreaManager getInstance() {
        return MainApp.getInstance().getMainAppFrame().getManagerDirector().getAreaManager();
    }

    public void instanceModifiedStatusChanged(InstanceEvent e) {
        fileAssigned(e);
    }

    public void fileAssigned(InstanceEvent e) {
        setTabTitle((FileInstance) e.getSource());
    }

    private static synchronized Integer nextID() {
        return ++anIDCounter;
    }

    public Collection<FileInstance> getOpenedInstances() {
        return runningInstances();
    }

    public final void getPrevTab() {
        final Content previousContent = contentManager.getPreviousContent();
        if (previousContent != null)
            previousContent.setSelected(true);
    }


    public final void getNextTab() {
        final Content nextContent = contentManager.getNextContent();
        if (nextContent != null)
            nextContent.setSelected(true);
    }


    private Collection<FileInstance> runningInstances() {
        final Content[] contents = contentManager.getContents();
        final Collection<FileInstance> runningIds = new ArrayList<FileInstance>(contents.length);
        for (Content content : contents)
            runningIds.add(getFileInstance(content));
        return runningIds;
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

    private void fireAreaDeactivated(IFileInstance instance) {
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

    final void addAreaChangeListener(final IAreaChangeListener listener) {
        listenerList.add(IAreaChangeListener.class, listener);
    }

    final public void addFileChangeListener(final IFileChangeListener listener) {
        listenerList.add(IFileChangeListener.class, listener);
    }

    final boolean closeSoft(final FileInstance instance, final boolean removeWindow) {
        if (instance == null || !runningInstances.contains(instance))
            return false;

        boolean result = false;
        try {
            if (result = instance.closeSoft()) {
                runningInstances.remove(instance);
                final Content nextContent = contentManager.getNextContent();
                if (nextContent != null) {
                    this.setActivateFileInstance(getFileInstance(nextContent));
                }
            }
        } catch (Throwable throwable) {
            LogUtils.processException(logger, throwable);
        }
        if (removeWindow)
            removeUIInstance(instance);

        return result;
    }

    final void closeHard(final FileInstance instance) {
        if (instance == null || !runningInstances.contains(instance))
            return;
        runningInstances.remove(instance);
        removeUIInstance(instance);
    }

    private synchronized void removeUIInstance(final FileInstance instance) {
        contentManager.removeContent(contentManager.getContent(instance));
    }

    private void setTabTitle(final FileInstance instance) {
        contentManager.getContent(instance).setTitle(instance.getTabName());
    }


    public void propertyChange(PropertyChangeEvent evt) {
        final String s = evt.getPropertyName();
        final Object old = evt.getOldValue();
        final Object newValue = evt.getNewValue();
        System.out.println("property = " + s + " old value = " + old + " new value=" + newValue);
    }
}
