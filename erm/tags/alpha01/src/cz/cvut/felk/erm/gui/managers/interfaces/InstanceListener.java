package cz.cvut.felk.erm.gui.managers.interfaces;

import cz.cvut.felk.erm.gui.managers.InstanceEvent;

import java.util.EventListener;

/**
 * @author Ladislav Vitasek
 */
public interface InstanceListener extends EventListener {

    public void instanceModifiedStatusChanged(InstanceEvent e);

    public void fileAssigned(InstanceEvent e);
    
}
