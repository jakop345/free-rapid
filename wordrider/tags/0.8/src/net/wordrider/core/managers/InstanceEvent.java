package net.wordrider.core.managers;

import net.wordrider.core.managers.interfaces.IFileInstance;

import javax.swing.event.ChangeEvent;

/**
 * @author Vity
 */
public class InstanceEvent extends ChangeEvent {

    public InstanceEvent(IFileInstance source) {
        super(source);
    }

    public IFileInstance getInstance() {
        return (IFileInstance) source;
    }
}
