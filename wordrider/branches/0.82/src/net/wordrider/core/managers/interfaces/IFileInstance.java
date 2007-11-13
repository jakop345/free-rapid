package net.wordrider.core.managers.interfaces;

import net.wordrider.files.ti68kformat.TITextFileInfo;

import javax.swing.text.JTextComponent;
import java.io.File;

/**
 * @author Vity
 */
public interface IFileInstance<C extends JTextComponent> extends Comparable<IFileInstance> {
    public C getRiderArea();

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
