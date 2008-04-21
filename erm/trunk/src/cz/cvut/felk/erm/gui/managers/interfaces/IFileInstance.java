package cz.cvut.felk.erm.gui.managers.interfaces;

import cz.cvut.felk.erm.gui.managers.TITextFileInfo;

import javax.swing.*;
import java.io.File;

/**
 * @author Ladislav Vitasek
 */
public interface IFileInstance<C extends JComponent> extends Comparable<IFileInstance> {
    public C getContentArea();

    public TITextFileInfo getFileInfo();

    public void setFileInfo(final TITextFileInfo fileInfo);

    public File getFile();

    public void setFile(final File file);

    public boolean isModified();

    public String getName();

    public boolean hasAssignedFile();

    public void addInstanceListener(InstanceListener listener);

    public void removeInstanceListener(InstanceListener listener);

    void setModified(boolean b);
}
