package cz.cvut.felk.erm.gui.managers;

import cz.cvut.felk.erm.gui.managers.interfaces.IFileInstance;

import javax.swing.event.ChangeEvent;

/**
 * @author Ladislav Vitasek
 */
public class AreaChangeEvent extends ChangeEvent {
    private final IFileInstance instance;

    public AreaChangeEvent(final cz.cvut.felk.erm.gui.managers.AreaManager source, final IFileInstance instance) {
        super(source);
        this.instance = instance;
    }

    public IFileInstance getFileInstance() {
        return instance;
    }

    public AreaManager getAreaManager() {
        return (AreaManager) source;
    }

}
