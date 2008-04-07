package cz.cvut.felk.erm.gui.managers.interfaces;

import cz.cvut.felk.erm.gui.managers.FileChangeEvent;

import java.util.EventListener;

/**
 * @author Ladislav Vitasek
 */
public interface IFileChangeListener extends EventListener {
    public void fileWasOpened(final FileChangeEvent event);

    public void fileWasClosed(final FileChangeEvent event);
}
