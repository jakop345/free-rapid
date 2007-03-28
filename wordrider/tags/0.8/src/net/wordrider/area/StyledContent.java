package net.wordrider.area;

import javax.swing.*;
import javax.swing.text.*;
import java.util.List;
import java.util.Vector;

/**
 * @author Vity
 */
final class StyledContent {
    private final List<StyledCopyContent> clipContent = new Vector<StyledCopyContent>(10);

    private static final class StyledCopyContent {
        public static final int CONTENT_TEXT = 0;
        public static final int CONTENT_SEPARATOR = 1;
        public static final int CONTENT_IMAGE = 2;

        private final Object content;
        private AttributeSet attr;
        private final int contentType;


        public final Object getContent() {
            return content;
        }

        public final AttributeSet getAttr() {
            return attr;
        }

        public final int getContentType() {
            return contentType;
        }

        public StyledCopyContent(final String text, final AttributeSet attr) {
            this.content = text;
            this.attr = attr;
            this.contentType = CONTENT_TEXT;
        }

        public StyledCopyContent(final AreaImage icon) {
            this.content = icon.clone();
            this.contentType = CONTENT_IMAGE;
        }

        public StyledCopyContent(final int separatorLineType) {
            this.content = separatorLineType;
            this.contentType = CONTENT_SEPARATOR;
        }

        public final String toString() {
            switch (contentType) {
                case CONTENT_TEXT:
                    return content.toString();
                case CONTENT_SEPARATOR:
                    return SeparatorLine.getStringRepresentation((Integer) content);
                case CONTENT_IMAGE:
                    return content.toString();
                default:
                    return content.toString();
            }

        }
    }

    /**
     * construct a new <code>StyledContent</code> object and load it with an already selected portion of text from a
     * <code>StyledEditorPane</code>
     * @param src - StyledEditorPane that has selected the text portion to be taken
     */
    public StyledContent(final RiderArea src) throws BadLocationException {
        final Document doc = src.getDocument();
        final int selStart = src.getSelectionStart();
        final int selEnd = src.getSelectionEnd();
        final int max = Math.max(selStart, selEnd);
        final SelectedElementsIterator eli = new SelectedElementsIterator(doc.getDefaultRootElement(), Math.min(selStart, selEnd), max, SelectedElementsIterator.FORWARD_DIRECTION);
        int eStart, eEnd;
        Element elem;
        while ((elem = eli.next()) != null) {
            eStart = elem.getStartOffset();
            eEnd = elem.getEndOffset();
            if (elem.getName().equals(AbstractDocument.ContentElementName)) {
                if (eli.isFirstLeafElement()) {
                    clipContent.add(new StyledCopyContent(src.getText(selStart, (eli.isLastLeafElement()) ? selEnd - selStart : eEnd - selStart), elem.getAttributes()));
                } else {
                    clipContent.add(new StyledCopyContent(src.getText(eStart, (eli.isLastLeafElement()) ? selEnd - eStart : eEnd - eStart), elem.getAttributes()));
                }
            } else if (RiderStyles.isSupportedComponent(elem)) {
                clipContent.add(new StyledCopyContent(((SeparatorLine) (elem.getAttributes().getAttribute(StyleConstants.ComponentAttribute))).getLineType()));
            } else if (RiderStyles.isImage(elem)) {
                clipContent.add(new StyledCopyContent(RiderStyles.getImage(elem)));
            }

        }
    }

    // --Commented out by Inspection START (4.2.05 16:15):
    //    private static int findWordSeparator(final String text) {
    //        final char[] array = text.toCharArray();
    //        final int lenght = array.length;
    //        for (int i = 0; i < lenght; ++i)
    //            if (RiderEditorKit.WORD_SEPARATORS.get(array[i]))
    //                return i;
    //        return -1;
    //    }
    // --Commented out by Inspection STOP (4.2.05 16:15)

    // --Commented out by Inspection START (4.2.05 16:15):
    //    public static final int findNextWord(final RiderArea src) throws BadLocationException {
    //        final Document doc = src.getDocument();
    //        int index = src.getCaretPosition();
    //        final int selStart = index;
    //        final int selEnd = doc.getLength() - 1;
    //        if (index == selEnd)
    //            return index;
    //        int eStart, eEnd;
    //        final SelectedElementsIterator eli = new SelectedElementsIterator(doc.getDefaultRootElement(), index, doc.getLength() - 1, SelectedElementsIterator.FORWARD_DIRECTION);
    //        Element elem;
    //        String text;
    //        int result;
    //        while ((elem = eli.next()) != null) {
    //            eStart = elem.getStartOffset();
    //            eEnd = elem.getEndOffset();
    //            if (elem.getName().equals(AbstractDocument.ContentElementName)) {
    //                if (eli.isFirstLeafElement()) {
    //                    text = src.getText(selStart, (eli.isLastLeafElement()) ? selEnd - selStart : eEnd - selStart);
    //                } else {
    //                    text = src.getText(eStart, (eli.isLastLeafElement()) ? selEnd - eStart : eEnd - eStart);
    //                }
    //                if (!text.equals("")) {
    //                    if (eli.isFirstLeafElement() && text.length() > 2 && text.charAt(0) == ' ') {
    //                        text = text.substring(1);
    //                        ++index;
    //                    }
    //                    if ((result = findWordSeparator(text)) != -1)
    //                        return index += result;
    //                    else {
    //                        index += text.length();
    //                    }
    //                }
    //            } else if (RiderStyles.isSupportedComponent(elem)) {
    //                ++index;
    //                break;
    //            }
    //
    //        }
    //        return index + 1;
    //
    //    }
    // --Commented out by Inspection STOP (4.2.05 16:15)


    /**
     * insert this <code>StyledContent</code> into a <code>Document</code>
     * @param insertPos the position within the document to insert at
     */
    public final void insert(final JTextPane area, int insertPos) throws BadLocationException {
        String text;
        StyledCopyContent content;
        final RiderDocument doc = (RiderDocument) area.getDocument();

        for (StyledCopyContent aClipContent : clipContent) {
            content = aClipContent;
            switch (content.getContentType()) {
                case StyledCopyContent.CONTENT_TEXT:
                    text = (String) content.getContent();
                    if (!text.equals("")) {
                        if (RiderStyles.isReadonlySection(doc.getParagraphElement(insertPos))) {
                            doc.insertString(insertPos, "\n", null);
                            if (Utilities.getRowStart(area, insertPos) != insertPos) ++insertPos;
                        }
                        doc.insertString(insertPos, text, content.getAttr());
                        insertPos += text.length();
                    }
                    break;
                case StyledCopyContent.CONTENT_SEPARATOR:
                case StyledCopyContent.CONTENT_IMAGE:
                    if (RiderStyles.isReadonlySection(doc.getParagraphElement(insertPos))) {
                        doc.insertString(insertPos, "\n", null);
                        if (Utilities.getRowStart(area, insertPos) != insertPos) ++insertPos;
                    } else if (insertPos - 1 > 0 && !doc.getText(insertPos - 1, 1).equals("\n"))
                        doc.insertString(insertPos++, "\n", null);
                    if (content.getContentType() == StyledCopyContent.CONTENT_SEPARATOR)
                        doc.insertSeparateLine(area, insertPos++, (Integer) content.getContent());
                    else
                        doc.insertPicture(insertPos++, (AreaImage) content.getContent());
                    break;
                default:
                    break;
            }
        }
    }

    public final String toString() {
        final StringBuilder buffer = new StringBuilder();
        for (StyledCopyContent aClipContent : clipContent) buffer.append(aClipContent.toString());
        return buffer.toString();
    }

}