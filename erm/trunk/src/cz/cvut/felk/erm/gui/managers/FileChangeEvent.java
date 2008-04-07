package cz.cvut.felk.erm.gui.managers;

import cz.cvut.felk.erm.gui.managers.interfaces.IFileInstance;

import javax.swing.event.ChangeEvent;

/**
 * @author Ladislav Vitasek
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
