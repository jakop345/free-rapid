package cz.omnicom.ermodeller.conc2obj;

import javax.swing.filechooser.FileFilter;
import java.io.File;

/**
 * File filter for text files *.txt.
 *
 * @see javax.swing.filechooser.FileFilter;
 */
public class TXTFileFilterObj extends FileFilter {
    final static String TXT = "txt";

    /**
     * Accept all directories and all *.txt files.
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
            if (TXT.equals(extension))
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
        return "Text files";
    }
}
