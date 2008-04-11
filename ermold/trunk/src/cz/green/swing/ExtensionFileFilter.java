package cz.green.swing;

import javax.swing.filechooser.FileFilter;

/**
 * This type was created by Jiri Mares
 */
public class ExtensionFileFilter extends FileFilter {
    protected final String description;
    protected final String extension;

    /**
     * This method was created by Jiri Mares
     *
     * @param extension   java.lang.String
     * @param description java.lang.String
     */
    public ExtensionFileFilter(String extension) {
        this(extension, "Files *." + extension);
    }

    /**
     * This method was created by Jiri Mares
     *
     * @param extension   java.lang.String
     * @param description java.lang.String
     */
    public ExtensionFileFilter(String extension, String description) {
        this.extension = extension;
        this.description = description;
    }

    /**
     * accept method comment.
     */
    public boolean accept(java.io.File f) {
        if (f.isDirectory())
            return true;
        String name = f.getName();
        try {
            String ext = name.substring(name.lastIndexOf('.') + 1);
            return ext.equals(extension);
        } catch (Throwable x) {
            return false;
        }
    }

    /**
     * getDescription method comment.
     */
    public String getDescription() {
        return description;
    }

    /**
     * This method was created by Jiri Mares
     *
     * @return java.lang.String
     */
    public String getExtension() {
        return extension;
    }

    /**
     * This method was created by Jiri Mares
     *
     * @param file java.io.File
     * @return java.lang.String
     */
    public String getPath(java.io.File file) {
        if (file == null)
            return null;
        String path = file.getPath();
        if (!accept(file))
            path = path + "." + extension;
        return path;
    }
}
