package cz.cvut.felk.erm.gui.managers;

import cz.cvut.felk.erm.gui.managers.interfaces.IFileInstance;
import cz.cvut.felk.erm.gui.managers.interfaces.IInformedTab;
import cz.cvut.felk.erm.gui.managers.interfaces.InstanceListener;
import cz.cvut.felk.erm.swing.Swinger;
import org.jdesktop.application.ResourceMap;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

/**
 * @author Ladislav Vitasek
 */
public final class FileInstance implements IFileInstance<ContentArea>, IInformedTab, PropertyChangeListener {
    private File file = null;
    private static int untitledCount;
    private String name;
    private final ContentArea area = new ContentArea();
    private final JScrollPane scrollPane = new JScrollPane();
    private Integer internalId = 0;
    private final EventListenerList listenerList = new EventListenerList();

    private TITextFileInfo fileInfo;


    public FileInstance() {
        setFileInfo(new TITextFileInfo());
        init();
    }

    public FileInstance(final File file, final TITextFileInfo fileInfo) {
        setFileInfo(fileInfo);
        this.file = file;
        init();
    }

    private void init() {
        //      JViewport viewPort = new TweakedViewport(20, 20);
        //scrollPane.setViewport(viewPort);

        // scrollPane = new JScrollPane();
        area.addPropertyChangeListener(ContentArea.MODIFIED_PROPERTY, this);
        scrollPane.getViewport().add(area);
        scrollPane.setFocusable(false);
        scrollPane.setFocusCycleRoot(false);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        updateName();
        //    this.director = MainApp.getInstance().getMainAppFrame().getManagerDirector();
    }

    private void updateName() {
        final ResourceMap map = Swinger.getResourceMap();
        this.name = (this.file == null) ? map.getString("editor.untitled", String.valueOf(++untitledCount)) : this.file.getName();
    }


    public int compareTo(IFileInstance o) {
        return getInternalId().compareTo(((FileInstance) o).getInternalId());
    }

    public boolean hasAssignedFile() {
        return this.file != null;
    }

    public final File getFile() {
        return this.file;
    }

    public final String getName() {
        return name;
    }

    public final TITextFileInfo getFileInfo() {
        return fileInfo;
    }

    public final void setFileInfo(final TITextFileInfo fileInfo) {
        this.fileInfo = fileInfo;
    }

    public final void setFile(final File file) {
        this.file = file;
        updateName();
        fireFileAssigned();
//        director.getAreaManager().setTabTitle(this); //save as

        //CloseActiveAction.getInstance().updateStatusName(this);

        //     director.getStatusbarManager().displayFilePath(file.getAbsolutePath());
        this.setModified(false);
    }

    public final ContentArea getRiderArea() {
        return this.area;
    }

    public final JComponent getComponent() {
        return scrollPane;
    }

    public String getId() {
        return "FileInstance" + this.getInternalId().toString();
    }

    public String getTitle() {
        return this.getTabName();
    }


    public final Icon getIcon() {
        return null;
    }

    public final String getTip() {
        return (file != null) ? file.getAbsolutePath() : null;
    }

    public final void activate() {
        Swinger.inputFocus(area);
    }

    public String getTabName() {
        return (isModified() ? getName() + "*" : getName());
    }

    public boolean closeSoft() throws Throwable {
        if (this.isModified())
            switch (Swinger.getChoiceCancel("message.confirm.changed")) {
                case Swinger.RESULT_YES:
                    //SaveFileAction.getInstance().actionPerformed(null);
                    break;
                case Swinger.RESULT_NO:
                    closeHard();
                    return true;
                default:
                    return false;
            }
        area.removePropertyChangeListener(ContentArea.MODIFIED_PROPERTY, this);
        closeHard();
        return true;
    }

    public final String toString() {
        return getName();
    }

    public void closeHard() throws Throwable {
        area.freeUpResources();
        //area.finalize();
    }

    //    public final void setInstanceID(final Integer instanceID) {
    //        this.instanceID = instanceID;
    //    }
    public boolean isModified() {
        return area.isModified();
    }

    public final void deactivate() {
        //implement - call to super class
    }


    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(ContentArea.MODIFIED_PROPERTY)) {
            fireInstanceModified();
        }
    }


    void setInternalId(Integer internalId) {
        this.internalId = internalId;
    }


    private Integer getInternalId() {
        return internalId;
    }


    private void fireInstanceModified() {
        Object[] listeners = this.listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        InstanceEvent event = null;
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == InstanceListener.class) {
                // Lazily create the event:
                if (event == null)
                    event = new InstanceEvent(this);
                ((InstanceListener) listeners[i + 1]).instanceModifiedStatusChanged(event);
            }
        }
    }

    private void fireFileAssigned() {
        Object[] listeners = this.listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        InstanceEvent event = null;
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == InstanceListener.class) {
                // Lazily create the event:
                if (event == null)
                    event = new InstanceEvent(this);
                ((InstanceListener) listeners[i + 1]).fileAssigned(event);
            }
        }
    }


    public void addInstanceListener(final InstanceListener listener) {
        listenerList.add(InstanceListener.class, listener);
    }

    public void removeInstanceListener(InstanceListener listener) {
        listenerList.remove(InstanceListener.class, listener);
    }


    public void setModified(boolean modified) {
        area.setModified(modified);
    }
}

