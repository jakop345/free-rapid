package net.wordrider.core.actions;

import net.wordrider.area.RiderStyles;
import net.wordrider.core.AppPrefs;
import net.wordrider.core.managers.interfaces.IFileInstance;
import net.wordrider.files.ti68kformat.TINoteFolioReader;

import javax.swing.*;
import javax.swing.text.*;
import java.util.Collection;
import java.util.LinkedList;

/**
 * @author Vity
 */
final class BatchNotefolioRead extends BatchRead {

    private Collection<Integer> bookmarkList;
    private boolean setBookmark;
    private int linesCount;
    private boolean addBookmarks;

    public BatchNotefolioRead(final IFileInstance instance, final String content) {
        super(instance, content);
    }

    public final Document process(final JProgressBar progress) throws BadLocationException {
        bookmarkList = new LinkedList<Integer>();
        setBookmark = true;
        linesCount = 0;
        final int length = content.length() - 1;
        final char[] textArray = content.toCharArray();
        char ch;
        //MutableAttributeSet temp;
        initProgressBar(progress, length);
        int delta = (length / 100) * 4;
        if (delta <= 0) delta = 1;
        int state = 0;
        final int separator = AppPrefs.getProperty(AppPrefs.NOTEFOLIO_SEPARATOR, RiderStyles.DOUBLE_LINE);
        this.addBookmarks = AppPrefs.getProperty(AppPrefs.NOTEFOLIO_BREAKPOINT, true);
        final boolean isNewLineAsSeparator = (separator == RiderStyles.EMPTY_LINE);
        insertNewLine();
        for (int i = 0; i < length; ++i) {
            if (i % delta == 0)
                progress.setValue(i);
            ch = textArray[i];
            switch (state) {
                case 0: //S
                    switch (ch) {
                        case'\r':
                            insertNewLine();
                            state = 2;
                            break;
                        case TINoteFolioReader.NOTE_SEPARATOR:
                            if (isNewLineAsSeparator) {
                                insertNewLine();//end of text
                                setBookmark = true;
                                insertNewLine();
                                state = 2;
                            } else {
                                appendSeparateLine(separator);
                                state = 1;
                                setBookmark = true;
                            }
                            break;
                        default:
                            appendChar(ch);
                            break;
                    }
                    break;
                case 2: //R
                    switch (ch) {
                        case'\r':
                            insertNewLine();
                            break;
                        case TINoteFolioReader.NOTE_SEPARATOR:
                            if (isNewLineAsSeparator) {
                                setBookmark = true;
                                insertNewLine();                                
                            } else {
                                appendSeparateLine2(separator);
                                state = 1;
                                setBookmark = true;
                            }
                            break;
                        default:
                            appendChar(ch);
                            state = 0;
                            break;
                    }
                    break;
                case 1: //F -- never happen when inserting empty lines
                    switch (ch) {
                        case'\r':
                            insertNewLine();
                            state = 2;
                            break;
                        case TINoteFolioReader.NOTE_SEPARATOR:
                            appendSeparateLine(separator);
                            setBookmark = true;
                            break;
                        default:
                            insertNewLine();
                            appendChar(ch);
                            state = 0;
                            break;
                    }
                    break;
                default:
                    assert false;
                    break;
            }
        }
        insertContent(); //if state == 0 then insert
        insertNewLine();//we make remove last line due to some JTextPane's bug
        doc.processBatchUpdates(0, true);
        if (!bookmarkList.isEmpty()) {
            final Element sectionElement = doc.getDefaultRootElement();
            Element el;
            for (int line : bookmarkList) {
                el = sectionElement.getElement(line - 1);
                if (el != null && el.getAttributes().getAttribute(RiderStyles.STYLE_BOOKMARK) != null) {
                    doc.setParagraphAttributes(el.getStartOffset(),
                            0, RiderStyles.updateBookmark(el), false);
                }
            }
        }
        progress.setValue(length);
        return doc;
    }

    @Override
    void insertNewLineWithAttr(final AttributeSet paraAttributes) {
        super.insertNewLineWithAttr(paraAttributes);
        ++linesCount;
    }

    @Override
    protected void insertNewLine() {
        //++linesCount;
        if (addBookmarks && setBookmark) {
            final SimpleAttributeSet set = new SimpleAttributeSet(paraA);
            //set.copyAttributes()
            set.addAttribute(RiderStyles.STYLE_BOOKMARK, "");
            insertNewLineWithAttr(set);
            bookmarkList.add(linesCount);
            setBookmark = false;

        } else {
            insertNewLineWithAttr(paraA);
        }
//        ++linesCount;
    }

}
