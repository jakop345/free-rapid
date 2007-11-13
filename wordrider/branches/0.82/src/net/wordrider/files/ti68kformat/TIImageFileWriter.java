package net.wordrider.files.ti68kformat;

/**
 *
 * @author Vity
 */

import java.awt.*;
import java.awt.image.ImageObserver;
import java.awt.image.PixelGrabber;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;

public final class TIImageFileWriter extends TIFileWriter {
    private static final String fileHeader = "**TI92P*"; //readable headers
    private int sum = 0;
    private final Image image;
    private int[] bitmap;

    public TIImageFileWriter(final Image image) {
        super();
        this.image = image;
    }

    public boolean saveToFile(final File file) throws IOException {
        final int width = image.getWidth(null);
        final int height = image.getHeight(null);
        bitmap = new int[width * height];
        final PixelGrabber pg = new PixelGrabber(image, 0, 0, width, height, bitmap, 0, width);

        try {
            pg.grabPixels();
        } catch (InterruptedException e) {
            throw new IOException(e.getMessage());
        }
        if ((pg.getStatus() & ImageObserver.ABORT) != 0) {
            throw new IOException("Fatal error image processing");
        }
        return super.saveToFile(file);    //call to super
    }

    protected final char getCheckSum() {
        return (char) (sum);
    }

    protected final byte getDataType() {
        return TIFileConstants.DATATYPE_IMAGE;
    }

    protected final String getFileHeaderType() {
        return fileHeader;
    }

    protected int getContentFileSize() {
        return 0x13 + bitmap.length / 8;
    }

//00FFFFFF
//00000000
//FFFFFFFF

    //FF000000
    protected final void writeContent(final DataOutputStream stream) throws IOException {
        final int width = image.getWidth(null);
        final int height = image.getHeight(null);
        sum = 0xDF + (width & 0xff) + ((width >> 8) & 0xff) + (height & 0xff) + ((height >> 8) & 0xff);//2xheight + 2x width + content + 0xDF = length in bytes

        final int contentLength = bitmap.length;
        final int contentLength8 = contentLength / 8;
        final int readingBytes = contentLength8 + 5;
        sum += (readingBytes & 0xff) + ((readingBytes >> 8) & 0xff);
        stream.writeInt((char) readingBytes);
        stream.writeByte((height >> 8) & 0xff);
        stream.writeByte(height & 0xff);
        stream.writeByte((width >> 8) & 0xff);
        stream.writeByte(width & 0xff);

        int resultByte;
        for (int i = 0; i < contentLength; i += 8) {
            resultByte = ((bitmap[i] == 0xFF000000) ? 0x80 : 0);
            resultByte |= ((bitmap[i + 1] == 0xFF000000) ? 0x40 : 0);
            resultByte |= ((bitmap[i + 2] == 0xFF000000) ? 0x20 : 0);
            resultByte |= ((bitmap[i + 3] == 0xFF000000) ? 0x10 : 0);
            resultByte |= ((bitmap[i + 4] == 0xFF000000) ? 0x8 : 0);
            resultByte |= ((bitmap[i + 5] == 0xFF000000) ? 0x4 : 0);
            resultByte |= ((bitmap[i + 6] == 0xFF000000) ? 0x2 : 0);
            resultByte |= ((bitmap[i + 7] == 0xFF000000) ? 0x1 : 0);
            //  resultByte = (resultByte) & 0xff;
            sum += resultByte;
            stream.writeByte(resultByte);
        }
        stream.writeByte(0xDF);//ende
//        System.out.println("width " + width);
//        System.out.println("height " + height);
//        System.out.println("length " + bitmap.length);
//        for (int i = 0; i < contentLength; i++) {
//            System.out.println(bitmap[i]);
//        }

    }
}
