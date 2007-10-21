package cz.cvut.felk.gps.gui.dialogs.filechooser;

import cz.cvut.felk.gps.swing.Swinger;
import cz.cvut.felk.gps.utilities.Utils;

import javax.swing.filechooser.FileFilter;
import java.io.File;

/**
 * @author Vity
 */
final class EnhancedFileFilter extends FileFilter implements IFileType {
    private final Object[] extensions;
    private final String description;

    public EnhancedFileFilter(final String[] extensions, final String labelDescription) {
        this.extensions = extensions;
        final StringBuilder buffer = new StringBuilder();
        final int length = extensions.length;
        for (int i = 0; i < length; ++i) {
            buffer.append("*.").append(extensions[i]);
            if (i + 1 != length)
                buffer.append(',');
        }
        this.description = Swinger.getResourceMap(JAppFileChooser.class).getString(labelDescription, buffer.toString());
    }

    public final String getExtension() {
        return extensions[0].toString();
    }

    public final boolean accept(final File f) {
        if (f.isDirectory())
            return true;

        final String extension = Utils.getExtension(f);
        if (extension != null)
            for (int i = 0; i < extensions.length; ++i) {
                if (extension.equals(this.extensions[i])) {
                    return true;
                }
            }
        return false;
    }

    //The description of this filter
    public final String getDescription() {
        return description;
    }
}