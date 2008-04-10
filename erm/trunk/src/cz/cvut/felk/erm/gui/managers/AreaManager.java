package cz.cvut.felk.erm.gui.managers;

import cz.cvut.felk.erm.gui.managers.interfaces.IAreaChangeListener;
import cz.cvut.felk.erm.gui.managers.interfaces.IFileChangeListener;
import cz.cvut.felk.erm.gui.managers.interfaces.IFileInstance;
import cz.cvut.felk.erm.gui.managers.interfaces.InstanceListener;
import cz.cvut.felk.erm.swing.Swinger;
import cz.cvut.felk.erm.utilities.LogUtils;
import org.noos.xing.mydoggy.Content;
import org.noos.xing.mydoggy.ContentManager;
import org.noos.xing.mydoggy.ContentManagerListener;
import org.noos.xing.mydoggy.ContentManagerUIListener;
import org.noos.xing.mydoggy.event.ContentManagerEvent;
import org.noos.xing.mydoggy.event.ContentManagerUIEvent;
import org.noos.xing.mydoggy.plaf.ui.content.MyDoggyDesktopContentManagerUI;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.io.File;
import java.util.*;
import java.util.logging.Logger;

/**
 * @author Ladislav Vitasek
 */
public final class AreaManager implements InstanceListener, PropertyChangeListener {
    private final static Logger logger = Logger.getLogger(AreaManager.class.getName());

    private final EventListenerList listenerList = new EventListenerList();
    private final RecentFilesManager recentFilesManager;

    final Collection<FileInstance> runningInstances = new HashSet<FileInstance>(4);
    final Map<Content, FileInstance> instances = new HashMap<Content, FileInstance>();

    private static int anIDCounter = 0;
    private ContentManager contentManager;
    private FileInstance activeInstance = null;


    public AreaManager(final ManagerDirector director) {
        super();
        //  this.director = director;
        recentFilesManager = new RecentFilesManager(director.getMenuManager());
        contentManager = director.getDockingManager().getToolManager().getContentManager();
        addFileChangeListener(recentFilesManager);
        Collections.synchronizedCollection(runningInstances);
        contentManager.getContentManagerUI().addContentManagerUIListener(new ContentManagerUIListener() {
            public boolean contentUIRemoving(ContentManagerUIEvent event) {
                final Content content = event.getContentUI().getContent();
                final FileInstance instance = getFileInstance(content);
                final boolean close = closeInstance(instance, false);
                if (close) {
                    if (activeInstance != null && activeInstance.equals(instance))
                        deactivateInstance(activeInstance);
                }
                return close;
            }

            public void contentUIDetached(ContentManagerUIEvent event) {

            }
        });
        contentManager.addContentManagerListener(new ContentManagerListener() {
            public void contentAdded(ContentManagerEvent event) {
                logger.info("Content added");
            }

            public void contentRemoved(ContentManagerEvent event) {
                logger.info("Content removed");
//                if (activeInstance != null && activeInstance.equals(getFileInstance(event.getContent())))
//                    deactivateInstance(activeInstance);
            }

            public void contentSelected(ContentManagerEvent event) {
                final Content content = event.getContent();

                if (content != null) {
                    final FileInstance instance = getFileInstance(content);
                    if (instance != null) { //pokud je tohle pridani tak je null {
                        activateInstance(instance);
                        logger.info("newly selected instance = " + content.getId());
                    }
                }
            }
        });
    }

    final public void openFileInstance(final FileInstance instance) {
        final Content content;
        instance.setInternalId(nextID());
        synchronized (this) {
            runningInstances.add(instance);
            content = contentManager.addContent(instance.getId(), instance.getTabName(), instance.getIcon(), instance.getComponent(), instance.getTip());
            instances.put(content, instance);
        }
        activateInstance(instance);
        content.addPropertyChangeListener(this);
        try {
            ((JInternalFrame) ((MyDoggyDesktopContentManagerUI) contentManager.getContentManagerUI()).getContentUI(content)).setMaximum(true);
        } catch (PropertyVetoException e) {
            LogUtils.processException(logger, e);
        }

        fireFileOpened(instance);
        instance.addInstanceListener(this);

    }


    private FileInstance getFileInstance(Content content) {
        assert content != null;
        final FileInstance instance = instances.get(content);
        //assert instance != null;
        return instance;
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

    final protected void deactivateInstance(final FileInstance instance) {
        this.activeInstance = null;
        instance.deactivate();
        fireAreaDeactivated(instance);
    }

    final protected void activateInstance(final FileInstance instance) {
        if (activeInstance != null && !activeInstance.equals(instance))
            deactivateInstance(activeInstance);
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


    public final void setActivateFileInstance(final FileInstance instance) {
        final Content content = getUIContent(instance);
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
        if (result) {
            fireFileClosed(fileInstance);
        }
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

//    public static AreaManager getInstance() {
//        return MainApp.getInstance().getMainAppFrame().getManagerDirector().getAreaManager();
//    }

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
                final Content activeContent = this.getUIContent(instance);
                if (activeContent != null)
                    instances.remove(activeContent);
                final Content nextContent = contentManager.getNextContent();
                if (nextContent != null && !nextContent.equals(activeContent)) {
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
        instances.remove(this.getUIContent(instance));
        runningInstances.remove(instance);

        removeUIInstance(instance);
    }

    private synchronized void removeUIInstance(final FileInstance instance) {
        contentManager.removeContent(getUIContent(instance));
    }

    private void setTabTitle(final FileInstance instance) {
        getUIContent(instance).setTitle(instance.getTabName());
    }

    private Content getUIContent(FileInstance instance) {
        assert instance != null;
        return contentManager.getContent(instance.getId());
    }


    public void propertyChange(PropertyChangeEvent evt) {
        final String s = evt.getPropertyName();
        final Object old = evt.getOldValue();
        final Object newValue = evt.getNewValue();
        logger.info("property = " + s + " old value = " + old + " new value=" + newValue);
    }
}
