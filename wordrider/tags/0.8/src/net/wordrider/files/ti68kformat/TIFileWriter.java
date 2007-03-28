package net.wordrider.files.ti68kformat;

import java.io.*;

/**
 * @author Vity
 */
public abstract class TIFileWriter extends TIFile {
    public boolean saveToFile(final File file) throws IOException {
        DataOutputStream stream = null;
        try {
            if (!file.exists() && !file.createNewFile()) return false;
            stream = new DataOutputStream(new FileOutputStream(file));
            writeHeader(stream);
            writeContent(stream);
            writeChecksum(stream);
            return true;
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
    }

    public static void writeString(final DataOutput stream, final String text, final int length) throws IOException {
        final byte[] arrayOfBytes = new String(text.getBytes(), "ASCII").getBytes();
        final int arrayLength = arrayOfBytes.length;
        stream.write(arrayOfBytes, 0, (arrayLength > length) ? length : arrayLength);
        //fill by zeroes
        if ((arrayLength < length))
            for (int i = arrayLength; i < length; ++i) stream.writeByte(0x0);
    }

    abstract protected int getContentFileSize();

    protected abstract String getFileHeaderType();

    private void writeHeader(final DataOutputStream stream) throws IOException {
        stream.writeBytes(getFileHeaderType());
        stream.writeShort(0x1 << 8);
        writeString(stream, fileInfo.getFolderName(), 8);
        writeString(stream, fileInfo.getComment(), 40);
        stream.writeShort(0x1 << 8);//number of vars = 1
        stream.writeInt(0x52 << 24); //data address
        writeString(stream, fileInfo.getVarName(), 8);
        stream.writeByte(getDataType()); //data type - text
        stream.writeByte(fileInfo.getStoreType()); //store type
        stream.writeShort(0xFF << 8); //??
        final int filesize = stream.size() + getContentFileSize();
        stream.writeByte(filesize & 0xFF);
        stream.writeByte((filesize >> 8) & 0xFF);
        stream.writeByte((filesize >> 24) & 0xFF);
        stream.writeByte((filesize >> 16) & 0xFF);
        stream.writeShort(0xA55A); //2 bytes separator
        stream.writeShort(0x0);// 2 bytes zeroes
    }

    protected abstract byte getDataType();

    private void writeChecksum(final DataOutputStream stream) throws IOException {
        final int crcsum = getCheckSum();
        stream.writeByte(crcsum);// hi a low bytes of crc sum
        stream.writeByte(crcsum >> 8);
    }

    protected abstract char getCheckSum();

    protected abstract void writeContent(DataOutputStream stream) throws IOException;

}