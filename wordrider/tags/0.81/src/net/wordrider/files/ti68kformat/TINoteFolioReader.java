package net.wordrider.files.ti68kformat;

import net.wordrider.files.InvalidDataTypeException;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 * @author Vity
 */
public class TINoteFolioReader extends TIFileReader{
    private String content = null;
    private int notesCount = 1;
    public static final char NOTE_SEPARATOR = '\f';
    private char contentChecksum;
    private final static byte END_OF_FILE [] = {0x00, 0x46, 0x4C, 0x49,0x4F,0x00}; //00 FLIO 00 


    public TINoteFolioReader() {
        this.fileInfo = new TITextFileInfo();
    }

    public final TITextFileInfo getTextFileInfo() {
        return (TITextFileInfo) fileInfo;
    }


    @Override
    public byte getDataType() {
        return TIFileConstants.DATATYPE_NOTEFOLIO;
    }

    @Override
    protected void readContent(final DataInputStream stream) throws IOException, InvalidDataTypeException {
        stream.skipBytes(9); //4x zeroes + 2 content length + 0x00 +
        notesCount = stream.readUnsignedByte();
        int character;
        final StringBuilder buffer = new StringBuilder();
        int zeroCounter = 0;
        contentChecksum = 0;
        while (true) {
            character = stream.readUnsignedByte();
            if (character == -1)
                break;
            contentChecksum += character;
            if (character == 0) {
                if (++zeroCounter == notesCount) {
                  //  buffer.append(NOTE_SEPARATOR);
                    break;
                } else {
                    character = NOTE_SEPARATOR;
                }
            }
            buffer.append((char) character);
        }
        //stream.skipBytes(7);//0x00 + 0x46 + 0x4C + 0x49 + 0x4F + 0x00 + 0xF8
        final byte[] notefolioMark = new byte[6];
        final int read = stream.read(notefolioMark);
        if (read != END_OF_FILE.length || !Arrays.equals(notefolioMark, END_OF_FILE))
            throw new InvalidDataTypeException();
        stream.skipBytes(1);//0xF8
        content = buffer.toString();
    }

    protected String[] getSupportedHeaders() {
        return TIFileConstants.SUPPORTED_FORMATS_TEXT;
    }


    protected void storeFileInformation(File file) {

    }

    protected char getCheckSum() {
        if (content == null) return 0;
        final int length12 = content.length() + 0xC;
        char result = contentChecksum;
        return (char) (result + 0x46 + 0x4C + 0x49 + 0x4F + 0xF8 + notesCount + ((length12 & 0xff) + ((length12 >> 8) & 0xff)));  //+0x20
    }

    public final String getContent() {
        return content;
    }
}
