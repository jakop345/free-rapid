package net.wordrider.files.ti68kformat;

import net.wordrider.utilities.LogUtils;
import net.wordrider.utilities.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.logging.Logger;

/**
 * User: Vity
 */
public final class TIImageUpdater {
    private final TIFileInfo fileInfo;
    private final File f;
    private final static Logger logger = Logger.getLogger(TIImageUpdater.class.getName());
    private final static int MIN_LENGHT = 85;

    public TIImageUpdater(final TIFileInfo fileInfo, final File f) {
        this.fileInfo = fileInfo;
        this.f = f;
    }

    public final File doUpdate(final boolean renameFile) throws IOException, IllegalAccessException {
        if (!f.exists())
            throw new FileNotFoundException();
        RandomAccessFile stream = null;
        try {
            stream = new RandomAccessFile(f, "rw");
            if (stream.length() <= MIN_LENGHT)
                throw new IllegalAccessException();
            updateData(stream);
            stream.close();
            stream = null;
            if (renameFile) {
                final String extension = Utils.getExtension(f);
                final String fileName = new StringBuilder(Utils.addFileSeparator(f.getParent())).append(fileInfo.getVarName()).append(".").append((extension != null) ? extension : "").toString();
                File newFile = new File(fileName);
                f.renameTo(newFile);
                return newFile;
            }
            return f;
        } finally {
            if (stream != null)
                try {
                    stream.close();
                } catch (IOException e) {
                    LogUtils.processException(logger, e);
                }
        }
    }

    private void updateData(final RandomAccessFile stream) throws IOException {
        stream.seek(0xA);
        TIFileWriter.writeString(stream, fileInfo.getFolderName(), 8);
        TIFileWriter.writeString(stream, fileInfo.getComment(), 40);
        stream.skipBytes(6);
//        writeShort(0x1 << 8);//number of vars = 1
//        stream.writeInt(0x52 << 24); //data address
        TIFileWriter.writeString(stream, fileInfo.getVarName(), 8);
        stream.skipBytes(1);
        stream.writeByte(fileInfo.getStoreType()); //store type
    }

}
