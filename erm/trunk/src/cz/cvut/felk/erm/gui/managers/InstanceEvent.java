package cz.cvut.felk.erm.gui.managers;

import cz.cvut.felk.erm.gui.managers.interfaces.IFileInstance;

import javax.swing.event.ChangeEvent;

/**
 * @author Ladislav Vitasek
 */
public class InstanceEvent extends ChangeEvent {

    public InstanceEvent(IFileInstance source) {
        super(source);
    }

    public IFileInstance getInstance() {
        return (IFileInstance) source;
    }
}
