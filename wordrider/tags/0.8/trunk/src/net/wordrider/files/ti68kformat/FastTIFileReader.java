package net.wordrider.files.ti68kformat;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;

/**
 * @author Vity
 */
public abstract class FastTIFileReader extends TIFileReader {
    byte[] byteContent = null;
    private byte[] checksum = null;
    private File file = null;

    public byte[] getChecksum() {
        return checksum;
    }

    public byte[] getByteContent() {
        return byteContent;
    }

    public File getFile() {
        return file;
    }

    public void setByteContent(final byte[] byteContent) {
        this.byteContent = byteContent;
        updateChecksum();
    }

    private void updateChecksum() {
        final int size = byteContent.length;
        int sum = 0;
        for (int i = 0; i < size; ++i)
            sum += byteContent[i];
        char charSum = (char) sum;
        checksum[0] = (byte) charSum;
        checksum[1] = (byte) (charSum >> 8);
    }

    protected void storeFileInformation(File file) {
        this.file = file;
    }


    protected void readContent(DataInputStream stream) throws IOException {
        final int contentSize = (int) file.length() - 0x52 - 2;
        if (contentSize <= 0)
            throw new IOException("Invalid file size");
        byteContent = new byte[contentSize]; //-2 = checksum
        stream.readFully(byteContent);
        fileInfo.setVarName(fileInfo.getVarName());
        fileInfo.setFolderName(fileInfo.getFolderName());
    }

    protected char getCheckSum() {
        return 0;
    }

    protected char readChecksum(final DataInputStream stream) throws IOException {
        checksum = new byte[2]; //-2 = checksum
        stream.readFully(checksum);
        return 0;
    }
}
