package net.wordrider.files.ti68kformat;

import net.wordrider.files.ImportableFileReader;
import net.wordrider.files.InvalidDataTypeException;
import net.wordrider.files.NotSupportedFileException;
import net.wordrider.utilities.LogUtils;

import java.io.*;
import java.util.logging.Logger;

/**
 * @author Vity
 */
public abstract class TIFileReader extends TIFile implements ImportableFileReader {
    private final static Logger logger = Logger.getLogger(TIFileReader.class.getName());

    public final boolean openFromFile(final File file) throws InvalidDataTypeException, NotSupportedFileException, IOException {
        DataInputStream stream = null;
        storeFileInformation(file);
        if (!file.exists() && !file.createNewFile()) return false;
        if (file.length() > 100000)
            throw new InvalidDataTypeException();
        try {
            stream = new DataInputStream(new FileInputStream(file));
            readHeader(stream);
            readContent(stream);
            final char read = readChecksum(stream);
            final char get = getCheckSum();
            if (read != get) {
                logger.warning("File '" + file.getPath() + "' was loaded but the file might be corrupted (CRC mismatch)");
                return false;
            }
            return true;
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
    }

    protected abstract void storeFileInformation(File file);

    private static String getString(final byte[] text) {
        final int size = text.length;
        int offset;
        for (offset = 0; offset < size; ++offset)
            if (text[offset] == 0)
                break;
        try {
            return new String(text, 0, offset, "ASCII");
        } catch (UnsupportedEncodingException e) {
            LogUtils.processException(logger, e);
            return "unknown";
        }
    }


    private void readHeader(final DataInputStream stream) throws NotSupportedFileException, InvalidDataTypeException, IOException {
        byte[] tempArray = new byte[8];
        stream.readFully(tempArray);
        final String temp = getString(tempArray);
        if (!isSupportedHeader(getSupportedHeaders(), temp))
            throw new NotSupportedFileException(temp);
        stream.skipBytes(2);//stream.readShort(); // 0x1 << 8
        stream.readFully(tempArray);
        fileInfo.setFolderName(getString(tempArray)); //writeString(stream,folderName,8);
        stream.readFully(tempArray = new byte[40]);
        fileInfo.setComment(getString(tempArray)); //writeString(stream,comment,40);
        stream.readShort(); //(0x1 << 8);//number of vars = 1
        stream.skipBytes(4); //(0x52 << 24); //data address
        stream.readFully(tempArray = new byte[8]);
        fileInfo.setVarName(getString(tempArray)); // variable name //writeString(stream,varName,8);
        final int dType = stream.readUnsignedByte();
        if (dType != getDataType()) {
            logger.warning("Invalid data type for file. Found:" + Integer.toHexString(dType) + " , but expected:" + Integer.toHexString(getDataType()));
            throw new InvalidDataTypeException();
        }
        fileInfo.setStoreType(stream.readUnsignedByte(), true);//attribute
        stream.skipBytes(8);//0xFF | 00 | 4 bytes file size | 0xA5 | 0x5A
    }

    char readChecksum(final DataInputStream stream) throws IOException {
        return (char) (stream.readUnsignedByte() + (stream.readUnsignedByte() << 8));
    }


    protected abstract void readContent(DataInputStream stream) throws IOException, InvalidDataTypeException;

    protected abstract String[] getSupportedHeaders();

    private static boolean isSupportedHeader(String[] supportedFormats, String header) {
        final int size = supportedFormats.length;
        for (int i = 0; i < size; ++i)
            if (supportedFormats[i].equals(header))
                return true;
        return false;  //implement - call to super class
    }

    protected abstract byte getDataType();

    protected abstract char getCheckSum();

}