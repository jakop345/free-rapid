package net.wordrider.core.actions;

import java.util.HashSet;

/**
 * @author Vity
 */
public class GroupFileOptions {
    private static final int FOLDER_DONT_CHANGE = 0;
    public static final int FOLDER_MOVE_TO_SPECIFIC = 1;
    public static final int FOLDER_PROJECT_FOLDERS = 2;
    public static final int FOLDER_PARENT_DOCUMENT = 3;
    private int textFileFolder = FOLDER_DONT_CHANGE;
    private int pictureFolder = FOLDER_DONT_CHANGE;

    private String specificTextFileFolder = null;
    private String specificPictureFolder = null;
    private String comments = null;
    private HashSet projectFolders = null;

    public int getTextFileFolder() {
        return textFileFolder;
    }

    public void setTextFileFolder(int textFileFolder) {
        this.textFileFolder = textFileFolder;
    }

    public int getPictureFolder() {
        return pictureFolder;
    }

    public void setPictureFolder(int pictureFolder) {
        this.pictureFolder = pictureFolder;
    }

    public String getSpecificTextFileFolder() {
        return specificTextFileFolder;
    }

    public void setSpecificTextFileFolder(String specificTextFileFolder) {
        this.specificTextFileFolder = specificTextFileFolder;
    }

    public String getSpecificPictureFolder() {
        return specificPictureFolder;
    }

    public void setSpecificPictureFolder(String specificPictureFolder) {
        this.specificPictureFolder = specificPictureFolder;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public HashSet getProjectFolders() {
        return projectFolders;
    }

    public void setProjectFolders(HashSet projectFolders) {
        this.projectFolders = projectFolders;
    }

}
