package cz.vity.freerapid.gui.content.comparators;

import cz.vity.freerapid.model.DownloadFile;

import java.text.Collator;
import java.util.Comparator;

/**
 * @author Vity
 */
public final class NameColumnComparator implements Comparator<DownloadFile> {
    @Override
    public final int compare(DownloadFile o1, DownloadFile o2) {
        return Collator.getInstance().compare(getValue(o1), getValue(o2));
    }

    static String getValue(DownloadFile downloadFile) {
        final String fn = downloadFile.getFileName();

        if (fn != null && !fn.isEmpty()) {
            return fn;
        } else {
            return downloadFile.getFileUrl().toString();
        }
    }
}
