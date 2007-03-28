package net.wordrider.files.ti68kformat;

/**
 * User: Vity
 */
public final class TIImageFileInfo extends TIFileInfo {
    private boolean insertIntoDocument = true;

    public final boolean isInsertIntoDocument() {
        return insertIntoDocument;
    }


    public final void setInsertIntoDocument(final boolean insert) {
        this.insertIntoDocument = insert;
    }

}
