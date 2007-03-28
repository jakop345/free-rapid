package net.wordrider.core.managers;

import net.wordrider.core.managers.interfaces.IFileInstance;

import javax.swing.event.ChangeEvent;

/**
 * @author Vity
 */
public class AreaChangeEvent extends ChangeEvent {
    private final IFileInstance instance;

    public AreaChangeEvent(final Object source, final IFileInstance instance) {
        super(source);
        this.instance = instance;
    }

    public IFileInstance getFileInstance() {
        return instance;
    }

}
