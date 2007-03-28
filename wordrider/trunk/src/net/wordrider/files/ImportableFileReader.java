package net.wordrider.files;

import java.io.File;
import java.io.IOException;

/**
 * @author Vity
 */
public interface ImportableFileReader {
    public abstract boolean openFromFile(final File file) throws InvalidDataTypeException, NotSupportedFileException, IOException;

    String getContent();
}
