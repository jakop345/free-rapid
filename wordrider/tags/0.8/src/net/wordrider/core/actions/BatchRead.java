package net.wordrider.core.actions;

import net.wordrider.area.RiderDocument;
import net.wordrider.area.RiderStyles;
import net.wordrider.core.managers.interfaces.IFileInstance;
import net.wordrider.plugintools.CharacterList;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.JTextComponent;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * @author Vity
 */
abstract class BatchRead implements DocumentProducer  {
    protected static final Map<Character, Character> TI2UNICODE;
    static final String CALC_FOLDER_SEPARATOR = "\\\\";
    private final JTextComponent area;
    final static Logger logger = Logger.getLogger(BatchTextRead.class.getName());
    final String content;
    StringBuilder textBuffer;
    RiderDocument doc;
    MutableAttributeSet paraA;
    MutableAttributeSet attr;



    static {
        TI2UNICODE = new HashMap<Character, Character>(7);
        TI2UNICODE.put((char) 18, '\u00D0');
        TI2UNICODE.put((char) 22, '\u00BB');
        TI2UNICODE.put((char) 28, '\u00BC');
        TI2UNICODE.put((char) 29, '\u00BD');
        TI2UNICODE.put((char) 30, '\u00BE');
        TI2UNICODE.put((char) 31, '\u00BF');
        TI2UNICODE.put((char) 0x10, '\u005F');
    }

    public BatchRead(final IFileInstance instance, final String content) {
        //this.instance = instance;
        this.content = content;
        this.area = instance.getRiderArea();
        textBuffer = new StringBuilder();
        doc = new RiderDocument();
        attr = new SimpleAttributeSet(RiderStyles.normalStyle);
        paraA = RiderStyles.alignmentLeftStyle;
    }

    private static char getUnicodeFromTIChar(final char ch) {
        if (ch >= 128 && ch <= 0xFF)
            return CharacterList.TI92PC_UNICODE_FONT[ch - 128];
        if (ch < 32) {      //speed optimization
            final Character tmp = TI2UNICODE.get(ch);
            return (tmp != null) ? tmp : ch;
        } else
            return ch;
    }

    static void initProgressBar(JProgressBar progressBar, int length) {
        progressBar.setMinimum(0);
        progressBar.setMaximum(length);
        progressBar.setValue(0);
    }

    void appendChar(final char ch) {
        textBuffer.append(getUnicodeFromTIChar(ch));
    }

    void insertContent() {
        if (textBuffer.length() > 0) {
            doc.appendBatchString(textBuffer.toString().toCharArray(), attr);
            initNewBuffer();
        }
    }

    void initNewBuffer() {
        textBuffer.setLength(0);
    }

    void insertNewLineWithAttr(final AttributeSet paraAttributes) {
        insertContent();
        doc.appendBatchLineFeed(attr, paraAttributes);
    }

    void appendSeparateLine(final int lineType) {
        insertNewLineWithAttr(RiderStyles.alignmentLeftStyle);
        doc.appendSeparateLine(area, lineType);
    }

    void appendSeparateLine2(final int lineType) {
        doc.appendSeparateLine(area, lineType);
    }

    protected void insertNewLine() {
//        if (setBookmark) {
//            final SimpleAttributeSet set = new SimpleAttributeSet(paraA);
//            //set.copyAttributes()
//            set.addAttribute(RiderStyles.STYLE_BOOKMARK, "");
//            insertNewLine(set);
//            bookmarkList.add(linesCount);
//        } else
        insertNewLineWithAttr(paraA);
    }


}
