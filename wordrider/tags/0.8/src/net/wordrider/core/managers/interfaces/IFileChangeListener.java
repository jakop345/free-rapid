package net.wordrider.core.managers.interfaces;

import net.wordrider.core.managers.FileChangeEvent;

import java.util.EventListener;

/**
 * @author Vity
 */
public interface IFileChangeListener extends EventListener {
    public void fileWasOpened(final FileChangeEvent event);

    public void fileWasClosed(final FileChangeEvent event);
}
