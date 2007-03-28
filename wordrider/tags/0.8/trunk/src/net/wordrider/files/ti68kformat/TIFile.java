package net.wordrider.files.ti68kformat;

/**
 * @author Vity
 */
public abstract class TIFile {
    TIFileInfo fileInfo;

    public TIFile() {
        fileInfo = new TIFileInfo();
    }

    public final TIFileInfo getFileInfo() {
        return fileInfo;
    }

    public final void setFileInfo(final TIFileInfo fileInfo) {
        this.fileInfo = fileInfo;
    }

}
