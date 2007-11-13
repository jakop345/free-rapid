package net.wordrider.files.miscformat;

import net.wordrider.files.ImportableFileReader;
import net.wordrider.files.InvalidDataTypeException;
import net.wordrider.files.NotSupportedFileException;

import java.io.*;

/**
 * @author Vity
 */
public class PureTextReader implements ImportableFileReader {
    private String content = "";

    public boolean openFromFile(final File file) throws InvalidDataTypeException, NotSupportedFileException, IOException {
        if (!file.exists() && !file.createNewFile()) return false;
        if (file.length() > 100000) // too big file
            throw new NotSupportedFileException(file.getAbsolutePath());
        BufferedReader stream = null;
        StringBuilder buffer = new StringBuilder((int) file.length());
        try {
            stream = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            final char[] lines = new char[2000];
            int read;
            while ((read = stream.read(lines)) != -1) buffer.append(lines, 0, read);
            content = buffer.toString();
            return true;
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
    }


    public String getContent() {
        return content;
    }
}
