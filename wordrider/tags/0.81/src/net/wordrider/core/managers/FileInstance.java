package net.wordrider.core.managers;

import net.wordrider.area.RiderArea;
import net.wordrider.core.Lng;
import net.wordrider.core.MainApp;
import net.wordrider.core.actions.CloseActiveAction;
import net.wordrider.core.actions.SaveFileAction;
import net.wordrider.core.managers.interfaces.IFileInstance;
import net.wordrider.core.managers.interfaces.IInformedTab;
import net.wordrider.core.managers.interfaces.InstanceListener;
import net.wordrider.files.ti68kformat.TITextFileInfo;
import net.wordrider.utilities.Swinger;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

/**
 * @author Vity
 */
public final class FileInstance implements IFileInstance<RiderArea>, IInformedTab, PropertyChangeListener {
    private File file = null;
    private static int untitledCount;
    private String name;
    private final RiderArea area = new RiderArea();
    private final JScrollPane scrollPane = new JScrollPane();
    private Integer internalId = 0;
    private final EventListenerList listenerList = new EventListenerList();

    private TITextFileInfo fileInfo;
    //    private Integer instanceID;
//    private ManagerDirector director;

//    public static final class TweakedViewport extends JViewport {
//        private int theTopMargin;
//        private int theBottomMargin;
//
//        // --Commented out by Inspection START (4.2.05 16:18):
//        //            public TweakedViewport(final int aTopMargin, final int aBottomMargin) {
//        //                super();
//        //                theTopMargin = aBottomMargin;
//        //                theBottomMargin = aTopMargin;
//        //            }
//        // --Commented out by Inspection STOP (4.2.05 16:18)
//
//        public final void scrollRectToVisible(final Rectangle aRectangle) {
//            final Dimension extentSize = getExtentSize();
//            aRectangle.y = aRectangle.y - theTopMargin;
//            aRectangle.x = aRectangle.x - 0;
//            aRectangle.height = Math.min(aRectangle.height + theTopMargin + theBottomMargin, extentSize.height);
//            super.scrollRectToVisible(aRectangle);
//        }
//    }


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
        area.addPropertyChangeListener(RiderArea.MODIFIED_PROPERTY, this);
        scrollPane.getViewport().add(area);
        scrollPane.setFocusable(false);
        scrollPane.setFocusCycleRoot(false);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        updateName();
        area.initBorder();
        //    this.director = MainApp.getInstance().getMainAppFrame().getManagerDirector();
    }

    private void updateName() {
        this.name = (this.file == null) ? Lng.getLabel("editor.untitled", String.valueOf(++untitledCount)) : this.file.getName();
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
        CloseActiveAction.getInstance().updateStatusName(this);
        //     director.getStatusbarManager().displayFilePath(file.getAbsolutePath());
        this.setModified(false);
    }

    public final RiderArea getRiderArea() {
        return this.area;
    }

    public final JComponent getComponent() {
        return scrollPane;
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
            switch (Swinger.getChoiceCancel(MainApp.getInstance().getMainAppFrame(), Lng.getLabel("message.confirm.changed"))) {
                case Swinger.RESULT_YES:
                    SaveFileAction.getInstance().actionPerformed(null);
                    break;
                case Swinger.RESULT_NO:
                    closeHard();
                    return true;
                default:
                    return false;
            }
        area.removePropertyChangeListener(RiderArea.MODIFIED_PROPERTY, this);
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
        if (evt.getPropertyName().equals(RiderArea.MODIFIED_PROPERTY)) {
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

