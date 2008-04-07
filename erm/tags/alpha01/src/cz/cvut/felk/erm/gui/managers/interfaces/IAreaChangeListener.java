package cz.cvut.felk.erm.gui.managers.interfaces;

import cz.cvut.felk.erm.gui.managers.AreaChangeEvent;

import java.util.EventListener;

/**
 * @author Ladislav Vitasek
 */
public interface IAreaChangeListener extends EventListener {
    public void areaActivated(AreaChangeEvent event);
    public void areaDeactivated(AreaChangeEvent event);
}
