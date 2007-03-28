package net.wordrider.core.managers;

import net.wordrider.core.managers.interfaces.IFileInstance;

import javax.swing.event.ChangeEvent;

/**
 * @author Vity
 */
public class FileChangeEvent extends ChangeEvent {
    private final IFileInstance fileInstance;

    public FileChangeEvent(final Object source, final IFileInstance fileInstance) {
        super(source);
        this.fileInstance = fileInstance;
    }

    public IFileInstance getFileInstance() {
        return fileInstance;
    }

}
