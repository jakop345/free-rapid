package cz.vity.freerapid.plugins.container.impl.proxy;

/**
 * @author tong2shot
 */
public class FileInfoProxy {
    private String fileUrl;
    private String fileName;
    private long fileSize = -1;
    private String description;
    private String saveToDirectory;

    public FileInfoProxy() {
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSaveToDirectory() {
        return saveToDirectory;
    }

    public void setSaveToDirectory(String saveToDirectory) {
        this.saveToDirectory = saveToDirectory;
    }
}
