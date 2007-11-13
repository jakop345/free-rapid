package net.wordrider.files.ti68kformat;

/**
 * @author Vity
 */
interface TIFileConstants {
    final static String[] SUPPORTED_FORMATS_TEXT = {"**TI92P*", "**TI89**", "**TI92**"};
    final static byte DATATYPE_TEXT = 0x0B;
    final static byte DATATYPE_NOTEFOLIO = 0x1C;
    final static String[] SUPPORTED_FORMATS_IMAGE = {"**TI92P*", "**TI89**"};
    final static byte DATATYPE_IMAGE = 0x10;
}
