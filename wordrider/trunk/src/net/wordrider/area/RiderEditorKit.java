package net.wordrider.area;

import net.wordrider.area.actions.NextPrevWordAction;
import net.wordrider.area.views.RiderViewFactory;
import net.wordrider.core.AppPrefs;
import net.wordrider.utilities.Consts;
import net.wordrider.utilities.LogUtils;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.event.ActionEvent;
import java.util.BitSet;
import java.util.logging.Logger;

/**
 * @author Vity
 */
public final class RiderEditorKit extends StyledEditorKit {
    public static final int DIRECTION_NEXT = 0;
    public static final int DIRECTION_PREVIOUS = 1;
    private final static Logger logger = Logger.getLogger(RiderEditorKit.class.getName());
    private static final Action[] defaultActions = {new DefaultKeyTypedAction(), new InsertTabAction(), new NextPrevWordAction(DefaultEditorKit.nextWordAction, false, DIRECTION_NEXT), new NextPrevWordAction(DefaultEditorKit.selectionNextWordAction, true, DIRECTION_NEXT), new NextPrevWordAction(DefaultEditorKit.previousWordAction, false, DIRECTION_PREVIOUS), new NextPrevWordAction(DefaultEditorKit.selectionPreviousWordAction, true, DIRECTION_PREVIOUS)};

    public final static BitSet WORD_SEPARATORS;

    static {
        WORD_SEPARATORS = new BitSet(256);
        WORD_SEPARATORS.set(' ');
        WORD_SEPARATORS.set('\n');
        WORD_SEPARATORS.set('\r');
        WORD_SEPARATORS.set('\f');
        WORD_SEPARATORS.set('.');
        WORD_SEPARATORS.set(',');
        WORD_SEPARATORS.set(':');
        WORD_SEPARATORS.set('(');
        WORD_SEPARATORS.set(')');
        WORD_SEPARATORS.set('[');
        WORD_SEPARATORS.set(']');
        WORD_SEPARATORS.set('{');
        WORD_SEPARATORS.set('}');
        WORD_SEPARATORS.set('<');
        WORD_SEPARATORS.set('>');
        WORD_SEPARATORS.set('\\');
        WORD_SEPARATORS.set('@');
        WORD_SEPARATORS.set('?');
        WORD_SEPARATORS.set('!');
        WORD_SEPARATORS.set(';');
        WORD_SEPARATORS.set('*');
        WORD_SEPARATORS.set('+');
        WORD_SEPARATORS.set('-');
        WORD_SEPARATORS.set('\"');
        WORD_SEPARATORS.set('\'');
        WORD_SEPARATORS.set('$');
        WORD_SEPARATORS.set('=');
        WORD_SEPARATORS.set('&');
        WORD_SEPARATORS.set('/');
        WORD_SEPARATORS.set('~');
    }


    public final ViewFactory getViewFactory() {
        return new RiderViewFactory();
    }

    public RiderEditorKit() {
        super();    //call to super
    }

    public Action[] getActions() {
        return TextAction.augmentList(super.getActions(), defaultActions);
    }

//    private final void initDefaultStyles() {
//        getInputAttributes().addAttributes(RiderStyles.normalStyle);
//    }

//    public Object setTextRiderText(final JTextComponent area, final String text, final String dir, final JProgressBar progress) throws BadLocationException {
//        final RiderDocument doc = (RiderDocument) createDefaultDocument();
//        initDefaultStyles();
//        char ch;
//        boolean startLine = true, bookmark = true;
//        MutableAttributeSet temp;
//        int offset = 0;
//        StringBuffer buffer = new StringBuffer();
//        final List bookmarkList = new LinkedList(), mathList = new LinkedList();
//        final int length = text.length();
//        final char[] textArray = text.toCharArray();
//        progress.setMinimum(0);
//        progress.setMaximum(length);
//        progress.setValue(0);
//        int delta = (length / 100) * 4;
//        if (delta <= 0) delta = 1;
//        for (int i = 0; i < length; ++i) {
//            if (i % delta == 0)
//                progress.setValue(i);
//            //progress.repaint();
//            ch = textArray[i];
//            switch (ch) {
//                case ' ':
//                    if (startLine)
//                        bookmark = false;
//                    else
//                        buffer.append(' ');
//                    break;
//                case '\r':
//                    buffer.append('\n');
//                    startLine = bookmark = true;
//                    break;
//                case '#':
//                    if (i + 1 != length) {
//                        switch (textArray[++i]) {
//                            case '1':
//                                temp = RiderStyles.miniStyle;
//                                break;
//                            case '2':
//                                temp = RiderStyles.normalStyle;
//                                break;
//                            case '3':
//                                temp = RiderStyles.maxiStyle;
//                                break;
//                            case 'U':
//                                temp = RiderStyles.updateUnderlineAttributes(getInputAttributes());
//                                break;
//                            case 'V':
//                                temp = RiderStyles.updateVectorAttributes(getInputAttributes());
//                                break;
//                            case 'I':
//                                temp = RiderStyles.updateInvertAttributes(getInputAttributes());
//                                break;
//                            case 'N':
//                                temp = RiderStyles.updateUnderlineDottedAttributes(getInputAttributes());
//                                break;
//                            case 'S':
//                                temp = RiderStyles.updateStrikedAttributes(getInputAttributes());
//                                break;
//                            case 'E':
//                                temp = RiderStyles.updateExposantAttributes(getInputAttributes());
//                                break;
//                            case 'W':
//                                temp = RiderStyles.updateWordWrapAttributes(getInputAttributes());
//                                break;
//                            default :
//                                --i;
//                                temp = null;
//                                buffer.append('#');
//                                break;
//                        }
//                        if (temp != null) {
//                            if (buffer.length() != 0) {
//                                doc.insertString(offset, buffer.toString(), getInputAttributes());
//                                offset += buffer.length();
//                                buffer = new StringBuffer();
//                            }
//                            getInputAttributes().addAttributes(temp);
//                        }
//                    } else
//                        buffer.append('#');
//                    break;
//                case '&':
//                    if (startLine && !bookmark && i + 1 != length) {
//                        boolean keyword = true;
//                        int component = -1;
//                        Image image = null;
//                        temp = null;
//                        switch (text.charAt(++i)) {
//                            case '-':
//                                component = RiderStyles.SINGLE_LINE;
//                                break;
//                            case '=':
//                                component = RiderStyles.DOUBLE_LINE;
//                                break;
//                            case 'L':
//                            case '\\':
//                                temp = RiderStyles.aligmentLeftStyle;
//                                break;
//                            case 'R':
//                                temp = RiderStyles.aligmentRightStyle;
//                                break;
//                            case 'C':
//                                temp = RiderStyles.aligmentCenteredStyle;
//                                break;
//                            case ',':
//                                temp = RiderStyles.margin10Style;
//                                break;
//                            case ';':
//                                temp = RiderStyles.margin20Style;
//                                break;
//                            case '.':
//                                temp = RiderStyles.margin30Style;
//                                break;
//                            case 'E':
//                                // temp = RiderStyles.mathStyle;
//                                mathList.add(new Integer(doc.getLength() + buffer.length()));
//                                break;
//                            case 'P': //picture
//                                final String fileName = getPictureFileName(textArray, i + 1);
//                                if (fileName != null) {
//                                    i += fileName.length();
//                                    final File f = getPictureFile(dir, fileName);
//                                    if (f != null)
//                                        image = loadPicture(f, dir, fileName);
//                                } else
//                                    logger.warning("Picture filename couldn't be extracted");
//                                break;
//                            default :
//                                --i;
//                                //buffer.append(getUnicodeFromTIChar(ch));
//                                keyword = false;
//                                break;
//                        }
//                        if (keyword) {
//                            if (buffer.length() != 0) {
//                                doc.insertString(offset, buffer.toString(), getInputAttributes());
//                                offset += buffer.length();
//                                buffer = new StringBuffer();
//                            }
//                            if (temp != null)
//                                doc.setParagraphAttributes(offset, 0, temp, true);
//                            else {
//                                if (component != -1) {
//                                    doc.insertSeparateLine(area, offset, component);
//                                    ++offset;
//                                } else if (image != null) {
//                                    doc.insertPicture(offset, image);
//                                    ++offset;
//                                }
//                            }
//                        }
//                    } else
//                        buffer.append('&');
//                    startLine = false;
//                    break;
//                case '\f':
//                    if (startLine) {
//                        bookmarkList.add(new Integer(doc.getLength() + buffer.length()));
//                        bookmark = false;
//                        break;
//                    }        //else default
//                default:
//                    buffer.append(getUnicodeFromTIChar(ch));
//                    startLine = false;
//                    break;
//            }
//        }
//        progress.setValue(length);
//
//        if (buffer.length() > 0)
//            doc.insertString(offset, buffer.toString(), getInputAttributes());
//        //int paraEl;
//        //RiderStyles.setProperty(new SimpleAttributeSet(),RiderStyles.STYLE_WORDWRAP, false);
//        for (final Iterator iterator = bookmarkList.iterator(); iterator.hasNext();)
//            doc.putBookmark(((Integer) iterator.next()).intValue());
//
//        for (final Iterator iterator = mathList.iterator(); iterator.hasNext();) {
//            doc.activatePrettyPrint(((Integer) iterator.next()).intValue());
//            //   paraEl = ((Integer) iterator.next()).intValue();
//            //paraElement = doc.getParagraphElement(paraEl);
//            //doc.setParagraphAttributes(paraEl, 0, RiderStyles.mathStyle, true);
//            //doc.setCharacterAttributes(paraEl, paraEl, );
//            //doc.setLogicalStyle(((Integer) iterator.next()).intValue(), RiderStyles.mathStyle);
//        }
//
//        return doc;
//
//    }
//
//    private static final String getPictureFileName(final char[] textArray, final int offset) {
//        final int arrayLength = textArray.length;
//        int i = offset;
//        for (; i < arrayLength && textArray[i] != '\r'; ++i) ; // to the end of file or new line
//        return (i != offset) ? new String(textArray, offset, i - offset) : null;
//    }

    public final Document createDefaultDocument() {
        //this.getInputAttributes()
        return new RiderDocument();
    }


    private static String getTab(String tab) {
        final int tabSize = AppPrefs.getProperty(AppPrefs.TABSIZE, Consts.DEFAULT_TAB_SIZE);

        if (tab.length() != tabSize) {
            final char[] spaces = new char[tabSize];
            for (int i = 0; i < tabSize; ++i)
                spaces[i] = ' ';
            tab = new String(spaces);
        }
        return tab;
    }

    public static final class RemoveTabAction extends TextAction {
        private static String tab = "    ";

        public RemoveTabAction() {
            super("RemoveTabAction");    //call to super
        }

        public void actionPerformed(final ActionEvent e) {
            final JTextComponent target = getTextComponent(e);
            if (target == null)
                return;
            if ((!target.isEditable()) || (!target.isEnabled())) {
                UIManager.getLookAndFeel().provideErrorFeedback(target);
                return;
            }
            final int selStart = target.getSelectionStart();
            final int selEnd = target.getSelectionEnd();
            final Element sectionElement = target.getDocument().getDefaultRootElement();
            final int endElementIndex = sectionElement.getElementIndex(selEnd);
            final RiderArea area = (RiderArea) target;
            area.setSelectionEnd(selStart);
            area.makeGroupChange(true);
            int tempElementIndex = sectionElement.getElementIndex(selStart);
            int counter = 0;
            tab = getTab(tab);
            Element paragraphElement;
            do {
                paragraphElement = sectionElement.getElement(tempElementIndex);
                if (RiderStyles.isImage(paragraphElement.getElement(0))) {
                    counter += removeTabAtPosition(area.getDocument(), paragraphElement.getStartOffset(), paragraphElement.getEndOffset());
                }
            } while (endElementIndex != tempElementIndex++);
            area.makeGroupChange(false);
            area.setSelectionEnd(selEnd - counter);
        }

        private int removeTabAtPosition(final Document doc, final int startOffset, final int endOffset) {
            int i = 0;
            try {
                final String removeText = doc.getText(startOffset, endOffset - startOffset);
                final int stringLength = removeText.length();
                for (; i < stringLength && Character.isSpaceChar(removeText.charAt(i)) && i < tab.length(); ++i) {
                }
                if (i != 0)
                    doc.remove(startOffset, i);
            } catch (BadLocationException e) {
                LogUtils.processException(logger, e);
            }
            return i;
        }
    }


    private static final class InsertTabAction extends DefaultEditorKit.InsertTabAction {
        private static String tab = "    ";

        private void insertTabAtPosition(final JTextComponent target, final int offset) {
            try {
                target.getDocument().insertString(offset, tab, null);
            } catch (BadLocationException ex) {
                LogUtils.processException(logger, ex);
            }
        }

        public void actionPerformed(final ActionEvent e) {
            final JTextComponent target = getTextComponent(e);
            if (target == null)
                return;
            if ((!target.isEditable()) || (!target.isEnabled())) {
                UIManager.getLookAndFeel().provideErrorFeedback(target);
                return;
            }
            tab = getTab(tab);
            final int selStart = target.getSelectionStart();
            final int selEnd = target.getSelectionEnd();
            if (selEnd != selStart) {
                final Element sectionElement = target.getDocument().getDefaultRootElement();
                final int endElementIndex = sectionElement.getElementIndex(selEnd);
                final RiderArea area = (RiderArea) target;
                area.setSelectionEnd(selStart);
                area.makeGroupChange(true);
                int tempElementIndex = sectionElement.getElementIndex(selStart);
                int counter = 0;
                do {
                    //  int position = sectionElement.getElement(tempElementIndex).getStartOffset();
                    //System.out.println("tempElementIndex: " + tempElementIndex + " position:" + position);
                    insertTabAtPosition(area, sectionElement.getElement(tempElementIndex).getStartOffset());
                    ++counter;
                } while (endElementIndex != tempElementIndex++);
                area.makeGroupChange(false);
                area.setSelectionEnd(selEnd + counter * tab.length());
            } else insertTabAtPosition(target, target.getCaretPosition());
            //target.replaceSelection(tab);
        }
    }

    //    int(...x,xa)  (   a....    asdasd asdasd asdasd asdasd    .....
}
