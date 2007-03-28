package net.wordrider.files.ti68kformat;

/**
 * @author Vity
 */
public class TIFileInfo {
    public final static byte STORE_RAM = 0;
    public final static byte STORE_RAM_LOCKED = 1;
    public final static byte STORE_ARCHIVE = 2;

    private int storeType = STORE_RAM;
    private String folderName = "";
    private String varName = "";
    private String comment = "";

    public TIFileInfo() {
    }

    public TIFileInfo(final String folderName, final String varName) {
        this.folderName = folderName;
        this.varName = varName;
    }

    public final String getFolderName() {
        return folderName;
    }

    public final void setFolderName(final String folderName) {
        this.folderName = (folderName == null) ? "" : folderName;
    }

    public final String getComment() {
        return comment;
    }

    public final void setComment(final String comment) {
        this.comment = (comment == null) ? "" : comment;
    }

    public final String getVarName() {
        return varName;
    }

    public final void setVarName(final String varName) {
        this.varName = (varName == null) ? "" : varName;
    }

    public final void setStoreType(final int type, final boolean process) {
        if (!process)
            this.storeType = type;
        else
            switch (type) {
                case STORE_ARCHIVE:
                case STORE_RAM:
                case STORE_RAM_LOCKED:
                    this.storeType = type;
                    break;
                default:
                    this.storeType = STORE_RAM;
                    break;
            }
    }

    public final int getStoreType() {
        return storeType;
    }
    

}
