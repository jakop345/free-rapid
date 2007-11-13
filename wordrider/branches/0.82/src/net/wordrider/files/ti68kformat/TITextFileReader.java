package net.wordrider.files.ti68kformat;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;

/**
 * @author Vity
 */
public class TITextFileReader extends TIFileReader {

    private String content = null;
    private char contentChecksum;

    public TITextFileReader() {
        this.fileInfo = new TITextFileInfo();
    }

    public final TITextFileInfo getTextFileInfo() {
        return (TITextFileInfo) fileInfo;
    }

    protected void readContent(final DataInputStream stream) throws IOException {
        stream.skipBytes(8); //4x zeroes + 2 content length + 0x00 + 0x1
        int character;
        final StringBuilder buffer = new StringBuilder();
        contentChecksum = 0;
        while (true) {
            character = stream.readUnsignedByte();
            if (character == 0 || character == -1)
                break;
            contentChecksum += character;
            buffer.append((char) character);
        }
        //if (character == 0 || character == -1)
        stream.skipBytes(1);//0xE0
        content = buffer.toString();
    }


    protected String[] getSupportedHeaders() {
        return TIFileConstants.SUPPORTED_FORMATS_TEXT;
    }

    public final String getContent() {
        return content;
    }

    public byte getDataType() {
        return TIFileConstants.DATATYPE_TEXT;
    }

    protected void storeFileInformation(final File file) {
    }

    protected final char getCheckSum() {
        if (content == null) return 0;
        final int length4 = content.length() + 0x4;
        char result = contentChecksum;
        return (char) (result + 0xE0 + 0x1 + ((length4 & 0xff) + ((length4 >> 8) & 0xff)));  //+0x20
    }
}
