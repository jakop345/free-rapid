package net.wordrider.core.actions;

import net.wordrider.area.AreaImage;
import net.wordrider.area.RiderStyles;
import net.wordrider.core.managers.interfaces.IFileInstance;
import net.wordrider.files.InvalidDataTypeException;
import net.wordrider.files.NotSupportedFileException;
import net.wordrider.files.ti68kformat.TIFileInfo;
import net.wordrider.files.ti68kformat.TIImageDecoder;
import net.wordrider.utilities.Swinger;
import net.wordrider.utilities.Utils;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;

/**
 * #                          #                    &
 * U : underline              1 : small font       L : Left alignement
 * V : vector                 2 : medium font      R : Right alignement
 * I : invert                 3 : big font         C : Centered
 * N : underline dotted                            \ : Margin 0 pixel
 * S : strikethrought                              , : Margin 10 pixels
 * E : exponent                                    ; : Margin 20 pixels
 * W : Word Wrap                                   . : margin 30 pixels
 *                                                 E : Pretty printed expression
 *                                                 - : line separator
 *                                                 = : double "-"
 * D : subscript                                   Mxxx : margin xxx pixels
 * J : conjugate
 */

/**
 * @author Vity
 */
final class BatchTextRead extends BatchRead {
    private boolean setBookmark;
    private Collection<Integer> bookmarkList;
    private int linesCount;
    private final boolean isHibviewFormat;
    private final boolean isTi92format;
    private final String dir;

    public BatchTextRead(final IFileInstance instance, final String content) {
        super(instance, content);
        final File openFile = instance.getFile();
        this.isTi92format = !Utils.getExtension(openFile).equals("89t");
        this.dir = openFile.getParent();
        this.isHibviewFormat = instance.getFileInfo().isHibviewFormat();
    }

    private void init() {
        setBookmark = false;
        linesCount = 0;
        bookmarkList = new LinkedList<Integer>();
    }

    public final Document process(final JProgressBar progressBar) throws BadLocationException {
        init();
        final int length = content.length();
        final char[] textArray = content.toCharArray();
        char ch;
        MutableAttributeSet temp;
        initProgressBar(progressBar, length);
        int delta = (length / 100) * 4;
        if (delta <= 0) delta = 1;
        int state = 1;
        StringBuilder marginXBuffer = new StringBuilder(3);
        for (int i = 0; i < length; ++i) {
            if (i % delta == 0)
                progressBar.setValue(i);
            ch = textArray[i];
            switch (state) {
                case 5: //G
                    switch (ch) {
                        case'\r':
                            state = 1;
                            break;
                        case'#':
                            state = 4;
                            break;
                        default:
                            appendChar(ch);
                            break;
                    }
                    break;
                case 1://B
                    switch (ch) {
                        case' ':
                            setBookmark = false;
                            state = 2;
                            break;
                        case'\f':
                            setBookmark = true;
                            state = 2;
                            break;
                        case'\r':
                            setBookmark = false;
                            break;
                        default:
                            setBookmark = true;
                            state = 2;
                            break;
                    }
                    break;
                case 2://C
                    switch (ch) {
                        case'#':
                            insertNewLine();
                            state = 4;
                            break;
                        case'&':
                            state = 3;
                            break;
                        case'\r':
                            insertNewLine();
                            state = 1;
                            break;
                        default:
                            insertNewLine();
                            appendChar(ch);
                            state = 5;
                            break;
                    }
                    break;
                case 4: //F
                    temp = null;
                    switch (ch) {
                        case'\r':
                            textBuffer.append('#');
                            state = 1;
                            break;
                        case'1':
                            temp = RiderStyles.miniStyle;
                            state = 5;
                            break;
                        case'2':
                            temp = RiderStyles.normalStyle;
                            state = 5;
                            break;
                        case'3':
                            temp = RiderStyles.maxiStyle;
                            state = 5;
                            break;
                        case'U':
                            temp = RiderStyles.updateUnderlineAttributes(attr);
                            state = 5;
                            break;
                        case'E':
                            temp = RiderStyles.updateExposantAttributes(attr);
                            state = 5;
                            break;
                        case'D':
                            if (isHibviewFormat) {
                                temp = RiderStyles.updateSubscriptAttributes(attr);
                                state = 5;
                                break;
                            }
                        case'N':
                            temp = RiderStyles.updateUnderlineDottedAttributes(attr);
                            state = 5;
                            break;
                        case'V':
                            temp = RiderStyles.updateVectorAttributes(attr);
                            state = 5;
                            break;
                        case'J':
                            if (isHibviewFormat) {
                                temp = RiderStyles.updateConjugateAttributes(attr);
                                state = 5;
                                break;
                            }
                        case'I':
                            temp = RiderStyles.updateInvertAttributes(attr);
                            state = 5;
                            break;
                        case'S':
                            temp = RiderStyles.updateStrikedAttributes(attr);
                            state = 5;
                            break;
                        case'W':
                            temp = RiderStyles.updateWordWrapAttributes(attr);
                            state = 5;
                            break;
                        case'#':
                            if (isHibviewFormat) {
                                textBuffer.append('#');
                                state = 5;
                                break;
                            }
                        default:
                            textBuffer.append('#');
                            appendChar(ch);
                            state = 5;
                            break;

                    }
                    if (temp != null) {
                        insertContent();
                        attr.addAttributes(temp);
                    }
                    break;
                case 6: //H
                    switch (ch) {
                        case'\r':
                            //attr = new SimpleAttributeSet();
                            state = 1;
                            break;
                        default:
                            appendChar(ch);
                            break;
                    }
                    break;
                case 7: //I
                    switch (ch) {
                        case'\r':
                            appendPicture(dir);
                            state = 1;
                            break;
                        default:
                            textBuffer.append(ch);  //file path don't need a conversion
                            break;
                    }
                    break;
                case 3://E
                    switch (ch) {
                        case'E':
                            insertNewLineWithAttr(RiderStyles.mathStyle);
                            state = 6;
                            break;
                        case'P':
                            insertNewLine();
                            state = 7;
                            break;
                        case'\r':
                            insertNewLine();
                            textBuffer.append('&');
                            state = 1;
                            break;
                        case'=':
                            appendSeparateLine(RiderStyles.DOUBLE_LINE);
                            state = 0;
                            break;
                        case'-':
                            appendSeparateLine(RiderStyles.SINGLE_LINE);
                            state = 0;
                            break;
                        case'L':
                        case'\\':
                            state = 5;
                            paraA = RiderStyles.alignmentLeftStyle;
                            insertNewLine();
                            break;
                        case'R':
                            state = 5;
                            paraA = RiderStyles.alignmentRightStyle;
                            insertNewLine();
                            break;
                        case'C':
                            state = 5;
                            paraA = RiderStyles.alignmentCenteredStyle;
                            insertNewLine();
                            break;
                        case',':
                            state = 5;
                            paraA = RiderStyles.margin10Style;
                            insertNewLine();
                            break;
                        case';':
                            state = 5;
                            paraA = RiderStyles.margin20Style;
                            insertNewLine();
                            break;
                        case'.':
                            state = 5;
                            paraA = RiderStyles.margin30Style;
                            insertNewLine();
                            break;
                        case'M':
                            if (isHibviewFormat) {
                                state = 8;

                                break;
                            }
                        case'&':
                            if (isHibviewFormat) {
                                insertNewLine();
                                textBuffer.append('&');
                                state = 5;
                                break;
                            }
                        default:
                            insertNewLine();
                            textBuffer.append('&');
                            appendChar(ch);
                            state = 5;
                            break;
                    }
                    break;
                case 0://A - ignore all chars to the end of line
                    if (ch == '\r')
                        state = 1;
                    break;
                case 8:
                    marginXBuffer.append(ch);
                    if (marginXBuffer.length() == 3) {
                        boolean parseOK = true;
                        try {
                            paraA = RiderStyles.getMarginXStyle(Integer.parseInt(marginXBuffer.toString()));
                        } catch (NumberFormatException e) {
                            parseOK = false;
                        } finally {
                            marginXBuffer = new StringBuilder(3);
                            insertNewLine();
                            if (!parseOK)
                                textBuffer.append("&M").append(marginXBuffer);
                            state = 5;
                        }
                    }
                    break;
            }
        }
        //determine end state
        switch (state) {
            case 5://G
            case 6://H
                insertContent();
                break;
            case 3://E
                insertNewLine();
                textBuffer.append('&');
                insertContent();
                break;
            case 4://F
                insertNewLine();
                textBuffer.append('#');
                insertContent();
                break;
            case 7://I
                appendPicture(dir);
                break;
            case 2://C
                insertNewLine();
                break;
            case 8:
                insertNewLine();
                textBuffer.append("&M").append(marginXBuffer);
                insertContent();
            default:
                break;
        }
        insertNewLine();//we make remove last line due to some JTextPane's bug
        doc.processBatchUpdates(0, true);
        if (!bookmarkList.isEmpty()) {
            final Element sectionElement = doc.getDefaultRootElement();
            Element el;
            for (int aBookmarkList : bookmarkList) {
                el = sectionElement.getElement(aBookmarkList - 1);
                if (el != null && el.getAttributes().getAttribute(RiderStyles.STYLE_BOOKMARK) != null) {
                    doc.setParagraphAttributes(el.getStartOffset(),
                            0, RiderStyles.updateBookmark(el), false);
                }
            }
        }
        progressBar.setValue(length);
        return doc;
    }

    protected void insertNewLine() {
        if (setBookmark) {
            final SimpleAttributeSet set = new SimpleAttributeSet(paraA);
            //set.copyAttributes()
            set.addAttribute(RiderStyles.STYLE_BOOKMARK, "");
            insertNewLineWithAttr(set);
            bookmarkList.add(linesCount);
        } else
            insertNewLineWithAttr(paraA);
    }

    void insertNewLineWithAttr(final AttributeSet paraAttributes) {
        super.insertNewLineWithAttr(paraAttributes);
        ++linesCount;
    }

    private static boolean checkFile(final File f) {
        final boolean result = (f.exists() && f.isFile());
        if (!result) {
            logger.info("Automatic attempt for loading image file failed " + f.getAbsolutePath());
        }
        return result;
    }

    private static File getPictureFile(String dir, final String fileName, final String folderName, final String variableName, final String extension) {
        if (!dir.endsWith(File.separator))
            dir += File.separator;
        final boolean endsWith89i = fileName.endsWith(extension);
        File f = new File(new StringBuilder(dir).append(folderName).append(File.separator).append(!endsWith89i ? variableName + extension : variableName).toString());
        if (checkFile(f))
            return f;
        else {
            f = new File(new StringBuilder(dir).append(folderName).append(File.separator).append(variableName).toString());
            if (checkFile(f))
                return f;
            else {
                f = new File(dir + (!endsWith89i ? variableName + extension : variableName));
                if (checkFile(f))
                    return f;
                else {
                    f = new File(dir + variableName);
                    if (checkFile(f))
                        return f;
                    else //last chance, no ticalcfolder separator
                    {
                        f = new File(dir + fileName);
                        if (checkFile(f))
                            return f;
                    }
                }
            }
        }
        return null;
    }

    private static Image loadPicture(final File f) {
        final TIImageDecoder ti = new TIImageDecoder();
        try {
            if (!ti.openFromFile(f))
                logger.warning("Image was loaded but the file might be corrupted (invalid checksum)");
            //    ti.setOriginalFile(f);
            //     ti.setOriginalFileDir(dir);
            return Toolkit.getDefaultToolkit().createImage(ti);
        } catch (InvalidDataTypeException e) {
            logger.warning(e.getMessage());
            return null;
        } catch (NotSupportedFileException e) {
            logger.warning(e.getMessage());
            return null;
        } catch (IOException e) {
            logger.warning("Cannot load image " + e.getMessage());
            return null;
        } catch (Exception e) {
            logger.severe("Fatal error exception " + e.getMessage());
            return null;
        }

    }

    private void appendPicture(final String dir) {
        if (textBuffer.length() > 0) {
            final String fileName = textBuffer.toString();
            final String[] names = fileName.split(CALC_FOLDER_SEPARATOR, 2);
            Image image = null;
            if (names.length >= 2) {
                File pictureFile = getPictureFile(dir, fileName, names[0], names[1], (isTi92format) ? ".92i" : ".89i");

                if (pictureFile == null) {
                    pictureFile = getPictureFile(dir, fileName, names[0], names[1], (isTi92format) ? ".89i" : ".92i");
                }
                if (pictureFile != null) {
                    image = loadPicture(pictureFile);
                }
            }
            final AreaImage areaImage;
            if (image == null) {
                final TIFileInfo fileInfo;
                fileInfo = (names.length >= 2) ? new TIFileInfo(names[0], names[1]) : new TIFileInfo("", fileName);
                areaImage = new AreaImage(Swinger.getIconImage("notfound.gif"), fileInfo);
            } else areaImage = new AreaImage(image);
            final MutableAttributeSet attr = new SimpleAttributeSet();
            StyleConstants.setIcon(attr, areaImage);
            //StyleConstants.setAlignment(attr, StyleConstants.ALIGN_CENTER);
            //inserts image in the document
            doc.appendBatchString(" ", attr);
            initNewBuffer();
        }
    }



}
