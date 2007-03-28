package net.wordrider.area;

import javax.swing.event.DocumentEvent;
import javax.swing.text.*;
import java.awt.*;
import java.util.Collection;
import java.util.Hashtable;
import java.util.LinkedList;

/**
 * @author Vity
 */
public final class RiderDocument extends DefaultStyledDocument {
    /**
     * EOL tag that we re-use when creating ElementSpecs
     */
    private static final char[] EOL_ARRAY = {'\n'};

    private transient final static FontKey fontSearch = new FontKey(null, 0, 0);
    private transient final static Hashtable<FontKey, Font> fontTable = new Hashtable<FontKey, Font>();


    /**
     * Batched ElementSpecs
     */
    private Collection<ElementSpec> batch = null;

    private static final Element[] ELEMENT = new Element[0];

    public RiderDocument() {
        super(RiderStyles.getDefaultStyleContext());
        batch = new LinkedList<ElementSpec>();
        //       this.putProperty(PlainDocument.tabSizeAttribute, new Integer(CHARACTERS_PER_TAB));
    }

    public void appendBatchString(final char[] str,
                                  final AttributeSet a) {
        //     We could synchronize this if multiple threads
        //     would be in here. Since we're trying to boost speed,
        //     we'll leave it off for now.
        // Make a copy of the attributes, since we will hang onto
        // them indefinitely and the caller might change them
        // before they are processed.
        batch.add(new ElementSpec(a.copyAttributes(), ElementSpec.ContentType, str, 0, str.length));
    }

    /**
     * Adds a String (assumed to not contain linefeeds) for later batch insertion.
     */
    public void appendBatchString(final String str,
                                  final AttributeSet a) {
        // We could synchronize this if multiple threads
        // would be in here. Since we're trying to boost speed,
        // we'll leave it off for now.

        // Make a copy of the attributes, since we will hang onto
        // them indefinitely and the caller might change them
        // before they are processed.
        batch.add(new ElementSpec(a.copyAttributes(), ElementSpec.ContentType, str.toCharArray(), 0, str.length()));
    }

    /**
     * Adds a linefeed for later batch processing
     */
    public void appendBatchLineFeed(final AttributeSet a) {
        // See sync notes above. In the interest of speed, this
        // isn't synchronized.

        // Add a spec with the linefeed characters
        batch.add(new ElementSpec(
                a, ElementSpec.ContentType, EOL_ARRAY, 0, 1));

        // Then add attributes for element start/end tags. Ideally
        // we'd get the attributes for the current position, but we
        // don't know what those are yet if we have unprocessed
        // batch inserts. Alternatives would be to get the last
        // paragraph element (instead of the first), or to process
        // any batch changes when a linefeed is inserted.
        final Element paragraph = getParagraphElement(0);
        final AttributeSet pattr = paragraph.getAttributes();
        batch.add(new ElementSpec(null, ElementSpec.EndTagType));
        batch.add(new ElementSpec(pattr, ElementSpec.StartTagType));
    }

    /**
     * Adds a linefeed for later batch processing
     */
    public void appendBatchLineFeed(final AttributeSet inputAttributes, final AttributeSet paraAttributeSet) {
        // See sync notes above. In the interest of speed, this
        // isn't synchronized.
        // Add a spec with the linefeed characters

        //batch.add(new ElementSpec(inputAttributes, ElementSpec.ContentType, EOL_ARRAY, 0, 1));
        batch.add(new ElementSpec(inputAttributes.copyAttributes(), ElementSpec.ContentType, EOL_ARRAY, 0, 1));

        // Then add attributes for element start/end tags. Ideally
        // we'd get the attributes for the current position, but we
        // don't know what those are yet if we have unprocessed
        // batch inserts. Alternatives would be to get the last
        // paragraph element (instead of the first), or to process
        // any batch changes when a linefeed is inserted.
        //        Element paragraph = getParagraphElement(0);
        //        AttributeSet pattr = paragraph.getAttributes();
        batch.add(new ElementSpec(null, ElementSpec.EndTagType));
        batch.add(new ElementSpec(paraAttributeSet, ElementSpec.StartTagType));
    }


    public void processBatchUpdates(final int offs, final boolean additionalDelete) throws BadLocationException {
        // As with insertBatchString, this could be synchronized if
        // there was a chance multiple threads would be in here.
        final ElementSpec[] inserts = new ElementSpec[batch.size()];
        batch.toArray(inserts);
        batch = new LinkedList<ElementSpec>();
        // Process all of the inserts in bulk
        super.insert(offs, inserts);
        if (additionalDelete && this.getDefaultRootElement().getElementCount() > 2) {
            final AttributeSet attr = getParagraphElement(1).getAttributes().copyAttributes();
            remove(0, 1);
            setParagraphAttributes(0, getParagraphElement(0).getEndOffset(), attr, true);
        }
        remove(getLength() - 1, 1);
    }

    public final Font getFont(final AttributeSet attr) {
        // PENDING(prinz) add cache behavior
        int style = Font.PLAIN;
        if (StyleConstants.isBold(attr))
            style |= Font.BOLD;
        if (StyleConstants.isItalic(attr))
            style |= Font.ITALIC;
        final Integer aSize = (Integer) attr.getAttribute(StyleConstants.FontSize);
        int size = (aSize != null) ? aSize : RiderStyles.SIZE_NORMAL;

        /**
         * if either superscript or subscript is
         * is set, we need to reduce the font size
         * by 2.
         */
        if (StyleConstants.isSuperscript(attr) ||
                StyleConstants.isSubscript(attr)) {
            size -= 1;
        }
        return getFont(style, size);
    }

    private static Font getFont(int style, int size) {
        fontSearch.setValue(RiderStyles.FONT_FAMILY, style, size);
        Font f = fontTable.get(fontSearch);
        if (f == null) {
            // haven't seen this one yet.
            f = RiderStyles.getAreaFont().deriveFont(style, size);
            FontKey key = new FontKey(RiderStyles.FONT_FAMILY, style, size);
            fontTable.put(key, f);
        }
        return f;
    }

    public final void toggleBookmark(final int offset) {
        toggleBookmark(getParagraphElement(offset));
    }

// --Commented out by Inspection START (4.8.05 17:30):
//    public final void putBookmark(final int offset) {
//        setParagraphAttributes(offset,
//                0, RiderStyles.updateBookmark(getParagraphElement(offset)), false);
//    }
// --Commented out by Inspection STOP (4.8.05 17:30)

    public void removeBookmark(final Element el) {
        final AttributeSet set = getAttributeContext().removeAttributes(el.getAttributes(), RiderStyles.bookmarkStyle.getAttributeNames());
        setParagraphAttributes(el.getStartOffset(),
                0, set, true);
    }

    public final void toggleBookmark(final Element el) {
        if (RiderStyles.isMath(el) || RiderStyles.isReadonlySection(el))
            return;
        final int elLength = el.getEndOffset() - el.getStartOffset();
        if (RiderStyles.isBookmark(el)) {
            removeBookmark(el);
        } else
            setParagraphAttributes(el.getStartOffset(),
                    elLength, RiderStyles.updateBookmark(el), false);
        refresh(el.getStartOffset(), elLength);
    }

// --Commented out by Inspection START (4.8.05 17:30):
//    public void activatePrettyPrint(final int pos) {
//        final Element el = getParagraphElement(pos);
//        final int elLength = el.getEndOffset() - el.getStartOffset();
//        removeBookmark(el);
//        setCharacterAttributes(el.getStartOffset(), elLength, new SimpleAttributeSet(), true);
//        setParagraphAttributes(el.getStartOffset(),
//                elLength, RiderStyles.mathStyle, true);
//    }
// --Commented out by Inspection STOP (4.8.05 17:30)

    public void setPrettyPrint(final Element el) {
        final int elLength = el.getEndOffset() - el.getStartOffset();
        if (RiderStyles.isMath(el)) {
            final AttributeSet set = getAttributeContext().removeAttributes(el.getAttributes(), RiderStyles.mathStyle.getAttributeNames());
            setParagraphAttributes(el.getStartOffset(),
                    elLength, set, true);
        } else {
            removeBookmark(el);

            setCharacterAttributes(el.getStartOffset(), elLength, new SimpleAttributeSet(), true);
            setParagraphAttributes(el.getStartOffset(),
                    elLength, RiderStyles.mathStyle, true);
        }
        refresh(el.getStartOffset(), elLength);
    }

    public final void refresh(final int offset, final int len) {
        //        if (offset == 0)
        //            len = getLength();
        final DefaultDocumentEvent changes = new DefaultDocumentEvent(offset, len, DocumentEvent.EventType.CHANGE);
        final Element root = getDefaultRootElement();
        final Element[] removed = ELEMENT;
        final Element[] added = ELEMENT;
        changes.addEdit(new ElementEdit(root, 0, removed, added));
        changes.end();
        fireChangedUpdate(changes);
    }

    public final void refreshAll() {
        refresh(0, getLength());
    }

    //    public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
    //
    //        if (editor != null /*&& (offs == getLength()-1 || str.equals("\n")) */) {
    //            editor.getCaret().setVisible(false);
    //            super.insertString(offs, str, a);    //call to super
    //            SwingUtilities.invokeLater(new Runnable() {
    //                public void run() {
    //                    editor.getCaret().setVisible(true);
    //                }
    //            });
    //        } else super.insertString(offs, str, a);    //call to super
    //    }
    //
    //    public final void setEditor(final RiderArea editor) {
    //        this.editor = editor;
    //    }

    public final void insertPicture(final int offset, final AreaImage areaImage) throws BadLocationException {
        final MutableAttributeSet attr = new SimpleAttributeSet();
        StyleConstants.setIcon(attr, areaImage);
        //StyleConstants.setAlignment(attr, StyleConstants.ALIGN_CENTER);
        //inserts image in the document
        this.insertString(offset, " ", attr);
    }

    public final void insertPicture(final int offset, final Image image) throws BadLocationException {
        //        final BufferedImage bi = new BufferedImage(image.getWidth(null),
        //                image.getHeight(null),
        //                BufferedImage.TYPE_BYTE_BINARY);
        //        bi.createGraphics().drawImage(image, 0, 0, null);
        insertPicture(offset, new AreaImage(image));
    }

    public final void appendSeparateLine(final JTextComponent pane, final int lineType) {
        final MutableAttributeSet attr = new SimpleAttributeSet();
        StyleConstants.setComponent(attr, new SeparatorLine(pane, lineType));
        this.appendBatchString(" ", attr);
    }


    public final void insertSeparateLine(final JTextComponent pane, final int offset, final int lineType) throws BadLocationException {
        //AttributeSet set = getParagraphElement(offset).getAttributes();
        setParagraphAttributes(offset, 0, RiderStyles.alignmentLeftStyle, true);
        final MutableAttributeSet attr = new SimpleAttributeSet();
        StyleConstants.setComponent(attr, new SeparatorLine(pane, lineType));
        this.insertString(offset, " ", attr);
        //this.insertString(offset, "\nasd", null);
        //setParagraphAttributes(offset, 0, set, true);
    }

    /**
     * key for a font table
     */
    static class FontKey {

        private String family;
        private int style;
        private int size;

        /**
         * Constructs a font key.
         */
        public FontKey(String family, int style, int size) {
            setValue(family, style, size);
        }

        public void setValue(String family, int style, int size) {
            this.family = (family != null) ? family.intern() : null;
            this.style = style;
            this.size = size;
        }

        /**
         * Returns a hashcode for this font.
         * @return a hashcode value for this font.
         */
        public int hashCode() {
            int fhash = (family != null) ? family.hashCode() : 0;
            return fhash ^ style ^ size;
        }

        /**
         * Compares this object to the specifed object. The result is <code>true</code> if and only if the argument is
         * not <code>null</code> and is a <code>Font</code> object with the same name, style, and point size as this
         * font.
         * @param obj the object to compare this font with.
         * @return <code>true</code> if the objects are equal; <code>false</code> otherwise.
         */
        public boolean equals(Object obj) {
            if (obj instanceof FontKey) {
                FontKey font = (FontKey) obj;
                return (size == font.size) && (style == font.style) && (family == font.family);
            }
            return false;
        }

    }

}
