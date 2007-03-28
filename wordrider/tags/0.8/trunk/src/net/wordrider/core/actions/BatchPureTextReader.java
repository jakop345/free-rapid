package net.wordrider.core.actions;

import net.wordrider.core.Lng;
import net.wordrider.core.managers.interfaces.IFileInstance;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Vity
 */
public class BatchPureTextReader extends BatchRead {

    private final static Map<Character, Character> conversion_table = getConversionTable();


    public BatchPureTextReader(final IFileInstance instance, final String content) {
        super(instance, content);
    }

    public Document process(JProgressBar progress) throws BadLocationException {
        final int length = content.length() - 1;
        final char[] textArray = content.toCharArray();
        initProgressBar(progress, length);
        char ch;
        int delta = (length / 100) * 4;
        if (delta <= 0) delta = 1;
        for (int i = 0; i < length; ++i) {
            if (i % delta == 0)
                progress.setValue(i);
            ch = textArray[i];
            if (ch == '\n')
                insertNewLine();
            else appendChar(ch);
        }
        insertContent(); //if state == 0 then insert
        insertNewLine();//we make remove last line due to some JTextPane's bug
        doc.processBatchUpdates(0, false);
        progress.setValue(length);
        return doc;
    }


    @Override
    void appendChar(final char ch) {
        textBuffer.append(getUnicodeFromText(ch));
    }

    private static char getUnicodeFromText(final char ch) {
        final Character tmp = conversion_table.get(ch);
        return (tmp != null) ? tmp : ch;
    }

    private static Map<Character, Character> getConversionTable() {
        if (conversion_table != null)
            return conversion_table;
        logger.info("Creating conversion static ascii table");
        Map<Character, Character> map = new HashMap<Character, Character>(56 + TI2UNICODE.size());
        map.put('\u03B1', '\u20AC');
        map.put('\u03B2', '\u0081');
        map.put('\u0393', '\u201A');
        map.put('\u03B3', '\u0192');
        map.put('\u0394', '\u201E');
        map.put('\u03B4', '\u2026');
        map.put('\u03B5', '\u2020');
        map.put('\u03B6', '\u2021');
        map.put('\u03B8', '\u02C6');
        map.put('\u03BB', '\u2030');
        map.put('\u03BE', '\u0160');
        map.put('\u03A0', '\u2039');
        map.put('\u03C0', '\u0152');
        map.put('\u03C1', '\u008D');
        map.put('\u03A3', '\u017D');
        map.put('\u03C3', '\u008F');
        map.put('\u03C4', '\u0090');
        map.put('\u03C6', '\u2018');
        map.put('\u03C8', '\u2019');
        map.put('\u2126', '\u201C');
        map.put('\u03C9', '\u201D');
        map.put('\u2264', '\u0153');
        map.put('\u2260', '\u009D');
        map.put('\u2265', '\u017E');
        map.put('\u2026', '\u00A1');
        map.put('\u00A1', '\u00A2');
        map.put('\u00A2', '\u00A3');
        map.put('\u00A3', '\u00A4');
        map.put('\u00A4', '\u00A5');
        map.put('\u00A5', '\u00A6');
        map.put('\u00A6', '\u007C');
        map.put('\u221A', '\u00A7');
        map.put('\u00AB', '\u00D7');
        map.put('\u2310', '\u00A9');
        map.put('\u2212', '\u00AA');
        map.put('\u00B0', '\u00AC');
        map.put('\u2022', '\u00AD');
        map.put('\u00B2', '\u00AE');
        map.put('\u00B3', '\u00AF');
        map.put('\u03BC', '\u00B1');
        map.put('\u2219', '\u00B2');
        map.put('\u00D7', '\u00B3');
        map.put('\u00B9', '\u00B4');
        map.put('\u00BA', '\u00B5');
        map.put('\u00BB', '\u00DE');
        map.put('\u222B', '\u00B7');
        map.put('\u2022', '\u00B2');
        map.put('\u221E', '\u00B8');
        map.put('\u00BF', '\u00B9');
        map.put('\u2202', '\u0040');
        map.put('\u25BA', '\u00D0');
        map.put('\u2192', '\u00BB');
        map.put('\u25CF', '-');
        map.put('\u2013', '-');
        map.put('\u0009', ' ');
        map.putAll(TI2UNICODE);
        if (Lng.localeLanguageCode != null && Lng.localeLanguageCode.equals("CS")) {
            map.put('\u201E', '\"');
            map.put('\u201C', '\"');
            map.put('\u00E9', 'e');
            map.put('\u00E1', 'a');
            map.put('\u00ED', 'i');
            map.put('\u00F3', 'o');
            map.put('\u00FA', 'u');
            map.put('\u016F', 'u');
            map.put('\u00DA', 'U');
            map.put('\u00C1', 'A');
            map.put('\u00C9', 'E');
            map.put('\u00D3', 'O');
            map.put('\u00CD', 'I');
            map.put('\u016E', 'U');
            map.put('\u00DD', 'Y');
            map.put('\u017E', 'z');
            map.put('\u017D', 'Z');
            map.put('\u0161', 's');
            map.put('\u0160', 'S');
            map.put('\u011B', 'e');
            map.put('\u00FD', 'y');
            map.put('\u011A', 'E');
            map.put('\u010D', 'c');
            map.put('\u010C', 'C');
            map.put('\u0159', 'r');
            map.put('\u0158', 'R');
            map.put('\u010E', 'D');
            map.put('\u0164', 'T');
            map.put('\u010F', 'd');
            map.put('\u0147', 'N');
            map.put('\u0148', 'n');
            map.put('\u0165', 't');
        }
        return map;
    }

    public static String importText(final String importString) {
        final char[] text = importString.toCharArray();
        final int length = text.length;
        for (int i = 0; i < length; ++i)
            text[i] = getUnicodeFromText(text[i]);
        return new String(text);
    }
}
