package net.wordrider.core.managers.interfaces;

import net.wordrider.core.managers.InstanceEvent;

import java.util.EventListener;

/**
 * @author Vity
 */
public interface InstanceListener extends EventListener {

    public void instanceModifiedStatusChanged(InstanceEvent e);

    public void fileAssigned(InstanceEvent e);
    
}
