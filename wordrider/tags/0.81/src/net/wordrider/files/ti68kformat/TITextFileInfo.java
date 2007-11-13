package net.wordrider.files.ti68kformat;

/**
 * @author Vity
 */
public final class TITextFileInfo extends TIFileInfo {
    public static final int PICTURE_FOLDER_DONTCHANGE = 0;
    public static final int PICTURE_FOLDER_USELAST = 1;
    public static final int PICTURE_FOLDER_USEOWN = 2;
    public static final int PICTURE_DONTSAVE = 3;
    public static final int PICTURE_USESAMEASFORDOCUMENT = 4;

    public static final int OUTPUT_FORMAT_HIBVIEW = 0;
    public static final int OUTPUT_FORMAT_TXTRIDER = 1;

    private String pictureFolder = "";

    private int outputFormat = -1;

    private int pictureProcessingType = PICTURE_FOLDER_DONTCHANGE;

    public boolean isHibviewFormat() {
        return outputFormat == 0 || outputFormat == -1;
    }

    final public int getPictureProcessingType() {
        return pictureProcessingType;
    }

    public int getOutputFormat() {
        return outputFormat;
    }


    public final void setOutputFormat(final int outputFormat) {
        this.outputFormat = outputFormat;
    }

    final public void setPictureProcessingType(final int pictureProcessingType) {
        this.pictureProcessingType = pictureProcessingType;
    }

    final public String getPictureFolder() {
        return pictureFolder;
    }

    final public void setPictureFolder(final String pictureFolder) {
        this.pictureFolder = pictureFolder;
        pictureProcessingType = PICTURE_FOLDER_USEOWN;
    }

}
