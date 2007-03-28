package net.wordrider.core.actions;

import net.wordrider.area.AreaImage;
import net.wordrider.area.RiderStyles;
import net.wordrider.files.ti68kformat.TIFileInfo;
import net.wordrider.files.ti68kformat.TITextFileInfo;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.StyledDocument;
import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * @author Vity
 */

/**
 * #                          #                    & U : underline              1 : small font       L : Left alignement
 * V : vector                 2 : medium font      R : Right alignement I : invert                 3 : big font
 * C : Centered N : underline dotted                            \ : Margin 0 pixel S : strikethrought
 *           , : Margin 10 pixels E : exposant                                    ; : Margin 20 pixels W : Word Wrap
 *                               . : margin 30 pixels - : line separator D : suffix
 * = : double "-" J: conjugate                                    E : Pretty printed expression M : variable margin
 */
final class TextRiderProcess {
    //  private static final String[] txtriderTags = {"#U", "#V", "#I", "#N", "#S", "#E", "#W"};
    private static final String[] txtriderParagraphTags = {"#1", "#2", "#3", "&L", "&R", "&C", "&\\", "&,", "&;", "&.", "&-", "&=", "&E", "&P", "&M"};
    private final MatchedTag[] matchedTags = {new MatchedTag(RiderStyles.P_UNDERLINE, "#U"), new MatchedTag(RiderStyles.P_VECTOR, "#V"), new MatchedTag(RiderStyles.P_INVERT, "#I"), new MatchedTag(RiderStyles.P_UNDERLINE_DOTTED, "#N"), new MatchedTag(RiderStyles.P_STRIKETHROUGHT, "#S"), new MatchedTag(RiderStyles.P_EXPOSANT, "#E"), new MatchedTag(RiderStyles.P_WORDWRAP, "#W"), new MatchedTag(RiderStyles.P_CONJUGATE, "#J"), new MatchedTag(RiderStyles.P_SUBSCRIPT, "#D")};
    private final short[] attributesMore = new short[3];

    private static final byte P_ALIGNMENT = 0;
    private static final byte P_SIZE = 1;
    private static final byte P_MARGIN = 2;

    private static final byte P_SIZEMINI = 0;
    private static final byte P_SIZENORMAL = 1;
    private static final byte P_SIZEMAXI = 2;
    private static final byte P_ALIGNLEFT = 3;
    private static final byte P_ALIGNRIGHT = 4;
    private static final byte P_ALIGNCENTER = 5;
    private static final byte P_MARGIN0 = 6;
    private static final byte P_MARGIN10 = 7;
    private static final byte P_MARGIN20 = 8;
    private static final byte P_MARGIN30 = 9;
    private static final byte P_LINESEPARATOR = 10;
    private static final byte P_DOUBLELINE = 11;
    private static final byte P_PRETTYPRINT = 12;
    private static final byte P_PICTURE = 13;
    private static final byte P_CUSTOMMARGIN = 14;

    private static final String LINE_SEPARATOR_TEXT = "\n";
    private static final String LINE_SEPARATOR_FILE = "\r ";
    private static final String tabulator = "   ";
    private String activeLineSeparator = LINE_SEPARATOR_TEXT;
    private StringBuilder builder;
    private boolean prettyPrint = false;
    private TITextFileInfo textFileInfo;
    private FastStack firstStack;
    private boolean hibviewFormat;
    private final static String LINE_SEPARATOR1 = "\n";
    private final static String LINE_SEPARATOR2 = "\n\r";
    private Pattern replaceSpecialPattern = Pattern.compile("#");


    private void initProcess() {
        attributesMore[P_ALIGNMENT] = P_ALIGNLEFT;
        attributesMore[P_SIZE] = P_SIZENORMAL;
        attributesMore[P_MARGIN] = P_MARGIN0;
        firstStack = new FastStack();
    }

    //    public final String getTxtRiderTextForSelection(final StyledDocument document, final int startElemenIndex, final int endElemenIndex, final int startOffset, final int endOffset) throws BadLocationException {
    //        final Element sectionElem = document.getDefaultRootElement();
    //        activeLineSeparator = LINE_SEPARATOR_FILE;
    //        buffer = new StringBuilder(endOffset - startOffset + 50);
    //        initProcess();
    //        int elCount;
    //        String text;
    //        Element element;
    //        boolean wasSetComponent;
    //        for (int i = startElemenIndex; i <= endElemenIndex; i++) {
    //            element = sectionElem.getElement(i);
    //            updateBookmark(element);
    //            updateParagraphProperties(element);
    //            elCount = element.getElementCount();
    //            wasSetComponent = false;
    //            for (int j = 0; j < elCount; j++) {
    //                element = element.getElement(j);
    //                if (updateComponents(element, j == 0))
    //                    wasSetComponent = true;
    //                else {
    //                    final int rangeStart = element.getStartOffset();
    //                    final int rangeEnd = element.getEndOffset();
    //                    text = document.getText(rangeStart, rangeEnd - rangeStart);
    //                    if (!text.equals("\n") && !text.equals("\n\r") && wasSetComponent)
    //                        buffer.append(activeLineSeparator);
    //                    if (!prettyPrint)
    //                        updateCharacterAttributes(element.getAttributes());
    //                    //after adding attributes!
    //                    buffer.append(text);
    //                }
    //            }
    //        }
    //        return updateResult(true);
    //    }


    public final String getTxtRiderTextForDocument(final StyledDocument document, final TITextFileInfo info, final boolean fileOutput) throws BadLocationException {
        final Element sectionElem = document.getDefaultRootElement();
        final int paraCount = sectionElem.getElementCount();
        this.hibviewFormat = info.isHibviewFormat();
        activeLineSeparator = (fileOutput) ? LINE_SEPARATOR_FILE : LINE_SEPARATOR_TEXT;
        builder = new StringBuilder(document.getLength() + 50);
        this.textFileInfo = info;
        initProcess();
        int elCount;
        String text;
        Element element, paraEl;
        boolean wasSetComponent;
        int writtenChars;
        for (int i = 0; i < paraCount; i++) {
            paraEl = sectionElem.getElement(i);
            if (fileOutput)
                updateBookmark(paraEl);
            writtenChars = builder.length();
            if (!RiderStyles.isReadonlySection(paraEl))
                updateParagraphProperties(paraEl);
            elCount = paraEl.getElementCount();
            wasSetComponent = false;
            boolean newline;
            for (int j = 0; j < elCount; j++) {
                element = paraEl.getElement(j);
                newline = j == 0;
                if (updateComponents(element, newline))
                    wasSetComponent = true;
                else {
                    final int rangeStart = element.getStartOffset();
                    final int rangeEnd = element.getEndOffset();
                    text = document.getText(rangeStart, rangeEnd - rangeStart);

                    final boolean isLineSeparator = text.equals(LINE_SEPARATOR1) || text.equals(LINE_SEPARATOR2);
                    if (wasSetComponent && !isLineSeparator) {
                        builder.append(activeLineSeparator);
                        writtenChars = builder.length();
                        newline = true;
                    }
                    if (hibviewFormat)
                        text = replaceSpecialPattern.matcher(text).replaceAll("##");                    
                    if (!prettyPrint && !wasSetComponent && !(isLineSeparator && elCount > 1))
                        //if (!prettyPrint && !wasSetComponent)
                        updateCharacterAttributes(element.getAttributes());
                    //after adding attributes!
                    if (newline && hibviewFormat && builder.length() == writtenChars && text.startsWith("&"))
                        text = "&" + text;
                    builder.append(text);
                }
            }
        }
        firstStack.clear();
        return updateResult(fileOutput);
    }

    private boolean updateComponents(final Element element, final boolean isFirstInOrder) {
        return updateSingleLine(element, isFirstInOrder) || updateDoubleLine(element, isFirstInOrder) || updateImageComponent(element, isFirstInOrder);
    }


    private void updateCharacterAttributes(final AttributeSet attr) {
        for (int i = firstStack.size() - 1; i >= 0; --i) {
            matchedTags[((Integer) firstStack.get(i))].updateTagFirst(attr);
        }
        updateFontSize(attr);
        matchedTags[RiderStyles.P_EXPOSANT].updateTag(attr);
        if (hibviewFormat) {
            matchedTags[RiderStyles.P_CONJUGATE].updateTag(attr);
            matchedTags[RiderStyles.P_SUBSCRIPT].updateTag(attr);
        }
        matchedTags[RiderStyles.P_WORDWRAP].updateTag(attr);
        matchedTags[RiderStyles.P_UNDERLINE].updateTag(attr);
        matchedTags[RiderStyles.P_INVERT].updateTag(attr);
        matchedTags[RiderStyles.P_UNDERLINE_DOTTED].updateTag(attr);
        matchedTags[RiderStyles.P_STRIKETHROUGHT].updateTag(attr);
        matchedTags[RiderStyles.P_VECTOR].updateTag(attr);
    }


    private String updateResult(final boolean fileOutput) {
        final String result;
        if (fileOutput) {
            builder.deleteCharAt(builder.length() - 1); //the last line separator delete
            if (builder.charAt(builder.length() - 1) == '\n') builder.append(' ');//space after the last \r if any
            result = builder.toString().replaceAll(LINE_SEPARATOR2, "\r ").replaceAll("\t", tabulator);
            builder = null;
            return result;
        }
        result = builder.toString();
        //builder.setLength(0);
        builder = null;//free memory
        return result;
    }

    private void updateBookmark(final Element el) {
        builder.append((RiderStyles.isBookmark(el)) ? '\f' : ' ');
    }

    private boolean updatePrettyPrint(final Element el) {
        final boolean isMath = RiderStyles.isMath(el);
        if (isMath) {
            builder.append(txtriderParagraphTags[P_PRETTYPRINT]);
            resetMarginToLeft();
        }
        return isMath;
    }

    private void updateParagraphProperties(final Element element) {
        if (!(prettyPrint = updatePrettyPrint(element))) {
            AttributeSet attr = element.getAttributes();
            final int bufferLength = builder.length();
            updateMargin(attr);
            if (bufferLength == builder.length())
                updateAlignment(attr);
        }
    }

    private void updateMargin(final AttributeSet attributeSet) {
        short margin;
        boolean customMargin = false;
        switch (margin = RiderStyles.getMargin(attributeSet)) {
            case RiderStyles.MARGIN_0:
                margin = P_MARGIN0;
                break;
            case RiderStyles.MARGIN_10:
                margin = P_MARGIN10;
                break;
            case RiderStyles.MARGIN_20:
                margin = P_MARGIN20;
                break;
            case RiderStyles.MARGIN_30:
                margin = P_MARGIN30;
                break;
            default:
                if (hibviewFormat) {
                    customMargin = true;
                } else margin = P_MARGIN0;
                break;
        }
        if (attributesMore[P_MARGIN] != margin) {
            attributesMore[P_MARGIN] = margin;
            //if (buffer.length() != 1) //bookmark at the beginning = 1
            //    buffer.append(activeLineSeparator);
            if (customMargin) {
                builder.append(txtriderParagraphTags[P_CUSTOMMARGIN]);
                if (margin >= 10 && margin < 100)
                    builder.append("0");
                else if (margin < 10) builder.append("00");
                builder.append(String.valueOf(margin));
            } else
                builder.append(txtriderParagraphTags[margin]);
        }
    }

    private void updateAlignment(final AttributeSet attributeSet) {
        final short align;
        switch (RiderStyles.getAlignment(attributeSet)) {
            case RiderStyles.ALIGN_LEFT:
                align = P_ALIGNLEFT;
                break;
            case RiderStyles.ALIGN_CENTER:
                align = P_ALIGNCENTER;
                break;
            case RiderStyles.ALIGN_RIGHT:
                align = P_ALIGNRIGHT;
                break;
            default:
                align = P_ALIGNLEFT;
                break;
        }
        if (attributesMore[P_ALIGNMENT] != align) {
            attributesMore[P_ALIGNMENT] = align;
            //            if (buffer.length() != 1) //space at the beginning
            //                buffer.append(activeLineSeparator);
            builder.append(txtriderParagraphTags[align]);
        }
    }

    private void updateFontSize(final AttributeSet attributeSet) {
        final short size;
        switch (RiderStyles.getFontSize(attributeSet)) {
            case RiderStyles.SIZE_NORMAL:
                size = P_SIZENORMAL;
                break;
            case RiderStyles.SIZE_MAXI:
                size = P_SIZEMAXI;
                break;
            case RiderStyles.SIZE_MINI:
                size = P_SIZEMINI;
                break;
            default:
                size = P_SIZENORMAL;
                break;
        }
        if (attributesMore[P_SIZE] != size) {
            attributesMore[P_SIZE] = size;
            builder.append(txtriderParagraphTags[size]);
        }
    }

    private void resetMarginToLeft() {
        attributesMore[P_MARGIN] = P_MARGIN0;
    }

    private boolean updateSingleLine(final Element el, final boolean isFirstInOrder) {
        if (RiderStyles.isComponentLine(el, RiderStyles.SINGLE_LINE)) {
            if (!isFirstInOrder)
                builder.append(activeLineSeparator);
            resetMarginToLeft();
            builder.append(txtriderParagraphTags[P_LINESEPARATOR]);
            return true;
        }
        return false;
    }

    private boolean updateDoubleLine(final Element el, final boolean isFirstInOrder) {
        if (RiderStyles.isComponentLine(el, RiderStyles.DOUBLE_LINE)) {
            if (!isFirstInOrder)
                builder.append(activeLineSeparator);
            builder.append(txtriderParagraphTags[P_DOUBLELINE]);
            resetMarginToLeft();
            return true;
        }
        return false;
    }

    private String generateImageDesc(final String imagePath, final TIFileInfo imageInfo) {
        switch (textFileInfo.getPictureProcessingType()) {
            case TITextFileInfo.PICTURE_FOLDER_DONTCHANGE:
//                System.out.println(imageInfo.getFolderName());
//                System.out.println(imageInfo.getVarName());
                return imageInfo.getFolderName() + "\\" + imageInfo.getVarName();
            case TITextFileInfo.PICTURE_FOLDER_USELAST:
                final int lastIndex = imagePath.lastIndexOf('\\') + 1;
                if (lastIndex != 0) {
                    return imagePath.substring(lastIndex) + "\\" + imageInfo.getVarName();
                } else
                    return imageInfo.getFolderName() + "\\" + imageInfo.getVarName();//not found picture
            case TITextFileInfo.PICTURE_FOLDER_USEOWN:
                return textFileInfo.getPictureFolder() + "\\" + imageInfo.getVarName();
            case TITextFileInfo.PICTURE_USESAMEASFORDOCUMENT:
                return textFileInfo.getFolderName() + "\\" + imageInfo.getVarName();
            default:// don't save
                return null;
        }
    }

    private boolean updateImageComponent(final Element el, final boolean isFirstInOrder) {
        if (RiderStyles.isImage(el)) {
            final AreaImage areaImage = RiderStyles.getImage(el);

            final String imageDesc = generateImageDesc(areaImage.getImagePath(), areaImage.getTIFileInfo());
            if (imageDesc != null) {
                if (!isFirstInOrder)
                    builder.append(activeLineSeparator);
                resetMarginToLeft();
                builder.append(txtriderParagraphTags[P_PICTURE]).append(imageDesc);
                return true;
            }
        }
        return false;
    }


    private final class MatchedTag {
        private boolean isSet = false;
        private final String tag;
        private final byte propertyCode;
        private boolean alreadyWas = false;

        public MatchedTag(final byte propertyCode, final String tag) {
            this.tag = tag;
            this.propertyCode = propertyCode;
        }

        public final void updateTag(final AttributeSet attributes) {
            if (alreadyWas) {
                alreadyWas = false;
                return;
            }
            final boolean set = RiderStyles.isSet(attributes, propertyCode);
            if (set ^ isSet) {
                isSet = set;
                if (firstStack.peek() == propertyCode && !isSet)
                    firstStack.pop();
                else firstStack.push(propertyCode);
                builder.append(tag);
            }
        }

        public final void updateTagFirst(AttributeSet attr) {
            updateTag(attr);
            alreadyWas = true;
        }
    }

    private final static class FastStack extends ArrayList<Object> {
        public FastStack() {
            super(5);
        }

        public void push(int item) {
            add(item);
        }

        public int pop() {
            return (Integer) remove(size() - 1);
        }

        public int peek() {
            int len = size();
            if (len == 0)
                return -1;
            return (Integer) get(len - 1);
        }
    }
}
