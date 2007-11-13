package net.wordrider.files.ti68kformat;

import net.wordrider.files.NotSupportedFileException;
import net.wordrider.files.InvalidDataTypeException;

import java.io.File;
import java.io.IOException;

/**
 * @author Vity
 */
public class PureTextFileReader extends TIFile {

    public PureTextFileReader() {
        super();
    }


    public boolean openFromFile(final File file) throws InvalidDataTypeException, NotSupportedFileException, IOException {
        return false;
    }
}
