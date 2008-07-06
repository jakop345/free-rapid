package cz.felk.cvut.erm.conc2obj;

import javax.swing.filechooser.FileFilter;
import java.io.File;

/**
 * File filter for text files *.sql.
 *
 * @see javax.swing.filechooser.FileFilter;
 */
public class ObjFileFilter extends FileFilter {
    final static String SQL = "sql";

    /**
     * Accept all directories and all *.sql files.
     *
     * @param java.io.File file to be accepted or refused
     * @return boolean
     */
    public boolean accept(File f) {
        if (f.isDirectory())
            return true;

        String s = f.getName();
        int i = s.lastIndexOf('.');
        if (i > 0 && i < s.length() - 1) {
            String extension = s.substring(i + 1).toLowerCase();
            if (SQL.equals(extension))
                return true;
        }
        return false;
    }

    /**
     * Returns description of the filter.
     *
     * @return java.lang.String
     */
    public String getDescription() {
        return "SQL batch files";
    }
}
