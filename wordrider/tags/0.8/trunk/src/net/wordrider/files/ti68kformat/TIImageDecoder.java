package net.wordrider.files.ti68kformat;

import net.wordrider.utilities.LogUtils;

import java.awt.image.ColorModel;
import java.awt.image.ImageConsumer;
import java.awt.image.ImageProducer;
import java.awt.image.IndexColorModel;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.logging.Logger;

/**
 * @author Vity
 */
public final class TIImageDecoder extends TIFileReader implements ImageProducer {
    private final static Logger logger = Logger.getLogger(TIImageDecoder.class.getName());
    //    private static final Object TI_COMMENT = "TI_COMMENT";
    //    private static final Object TI_VARIABLENAME = "TI_VARIABLENAME";
    //    private static final Object TI_FOLDERNAME = "TI_FOLDERNAME";
    private static final String TI_ORIGFILENAME = "TI_ORIGFILENAME";
    //   private static final Object TI_ORIGFILEDIR = "TI_ORIGFILEDIR";
    //   private static final Object TI_IMAGEPATH = "TI_IMAGEPATH";

    /* Since done in-memory, only one consumer */
    private ImageConsumer consumer;
    //   private final boolean loadError = false;
    private int width = -1;
    private int height = -1;
    private byte[] store;
    private final Hashtable<String, File> props = new Hashtable<String, File>(4);
    private char sum;
    private int widthExtension = 0;

    //    public final void setFolderName(final String folderName) {
    //        props.put(TI_FOLDERNAME, folderName);
    //    }

    // --Commented out by Inspection START (26.2.05 17:32):
    //    public final String getOriginalFileDir() {
    //        return (String) props.get(TI_ORIGFILEDIR);
    //    }
    // --Commented out by Inspection STOP (26.2.05 17:32)

    public final File getOriginalFile() {
        return props.get(TI_ORIGFILENAME);
    }

    public final void setOriginalFile(final File fileName) {
        props.put(TI_ORIGFILENAME, fileName);
    }

//    public final void setOriginalFileDir(final String origFileDir) {
//        props.put(TI_ORIGFILEDIR, origFileDir);
//    }

//    public final void setImagePath(final String imagePath) {
//        props.put(TI_IMAGEPATH, imagePath);
//    }

    protected void storeFileInformation(final File file) {
        setOriginalFile(file);
        //this.setImagePath(file.getParent());
    }

    protected final void readContent(final DataInputStream stream) throws IOException {
        stream.skip(4); //4x zeroes
        sum = (char) (stream.readUnsignedByte() + stream.readUnsignedByte()); // bytes for reading
        int tempByte1 = stream.readUnsignedByte();//height
        int tempByte2 = stream.readUnsignedByte();
        height = (tempByte1 << 8) + tempByte2;
        sum += tempByte1 + tempByte2;
        tempByte1 = stream.readUnsignedByte();
        tempByte2 = stream.readUnsignedByte();
        width = (tempByte1 << 8) + tempByte2;
        sum += tempByte1 + tempByte2;
        widthExtension = (width % 8 != 0) ? width + (8 - (width % 8)) : width;

        final byte[] pixels = new byte[height * (widthExtension / 8)];
        stream.readFully(pixels);
        final int length = pixels.length;
        store = new byte[length * 8];
        byte bitByte;
        int k;
        for (int i = 0, index = 0; i < length; ++i) {
            bitByte = pixels[i];
            sum += (char) (bitByte & 0xFF);//crc counter
            k = 7;
            do {
                store[index++] = ((bitByte & (1 << k)) != 0) ? (byte) 0xFF : 0;
            } while (--k >= 0);
        }
        sum += stream.readUnsignedByte(); //0xDF
    }

    protected String[] getSupportedHeaders() {
        return TIFileConstants.SUPPORTED_FORMATS_TEXT;
    }

    public final byte getDataType() {
        return TIFileConstants.DATATYPE_IMAGE;
    }

    public final char getCheckSum() {
        return sum;  //implement - call to super class
    }

    /* Format of Ppm file is single pass/frame, w/ complete scan lines in order */
    private static final int hints = (ImageConsumer.TOPDOWNLEFTRIGHT |
            ImageConsumer.COMPLETESCANLINES |
            ImageConsumer.SINGLEPASS |
            ImageConsumer.SINGLEFRAME);


    public final synchronized void addConsumer(final ImageConsumer ic) {
        consumer = ic;
        try {
            produce();
        } catch (Exception e) {
            LogUtils.processException(logger, e);
            if (consumer != null)
                consumer.imageComplete(ImageConsumer.IMAGEERROR);
        }
        consumer = null;
    }

    /* If consumer passed to routine is single consumer, return true, else false. */
    public final synchronized boolean isConsumer(final ImageConsumer ic) {
        return (ic.equals(consumer));
    }

    /* Disables consumer if currently consuming. */
    public final synchronized void removeConsumer(final ImageConsumer ic) {
        if (consumer != null && consumer.equals(ic))
            consumer = null;
    }

    /* Production is done by adding consumer. */
    public final void startProduction(final ImageConsumer ic) {
        addConsumer(ic);
    }

    public final void requestTopDownLeftRightResend(final ImageConsumer ic) {
        // Not needed.  The data is always in this format.
    }

    private void produce() {
        final byte[] bw = {(byte) 0xff, (byte) 0};
        final ColorModel cm = new IndexColorModel(1, 2, bw, bw, bw);
        if (consumer != null) {
//            if (loadError) {
//                consumer.imageComplete(ImageConsumer.IMAGEERROR);
//            } else {
            consumer.setDimensions(width, height);
            consumer.setColorModel(cm);
            consumer.setHints(hints);
            // allocate pixels
            //consumer.setPixels(0, 0, this.width, this.height,cm, store, 0, this.width);
            for (int j = 0, offset = 0; j < height; ++j) {
                consumer.setPixels(0, j, width, 1, cm, store, offset, width);
                offset += widthExtension;
            }
            consumer.setProperties(props);
            consumer.imageComplete(ImageConsumer.STATICIMAGEDONE);
            //  store = null;
//            }
        }
    }

    public String getContent() {
        return null;
    }
}
