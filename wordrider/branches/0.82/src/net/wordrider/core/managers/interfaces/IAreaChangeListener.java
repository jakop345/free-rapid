package net.wordrider.core.managers.interfaces;

import net.wordrider.core.managers.AreaChangeEvent;

import java.util.EventListener;

/**
 * @author Vity
 */
public interface IAreaChangeListener extends EventListener {
    public void areaActivated(AreaChangeEvent event);
    public void areaDeactivated(AreaChangeEvent event);
}
