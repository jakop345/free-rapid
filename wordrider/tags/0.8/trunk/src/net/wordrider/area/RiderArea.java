package net.wordrider.area;

/**
 * @author Vity
 */

import net.wordrider.area.actions.*;
import net.wordrider.core.AppPrefs;
import net.wordrider.core.actions.BatchPureTextReader;
import net.wordrider.core.actions.ChangeImagePropertiesAction;
import net.wordrider.core.swing.AutoscrollSupport;
import net.wordrider.utilities.LogUtils;
import net.wordrider.utilities.Swinger;

import javax.swing.*;
import javax.swing.plaf.TextUI;
import javax.swing.text.*;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.*;
import java.awt.event.*;
import java.io.File;
import java.util.Collection;
import java.util.LinkedList;
import java.util.logging.Logger;

public final class RiderArea extends JTextPane implements DropTargetListener, DragSourceListener,
        DragGestureListener, Autoscroll {
    public static final int LEFT_BORDER_WIDTH = 30;
    private AutoscrollSupport scrollSupport = new AutoscrollSupport(this, new Insets(10, 10, 10, 10));
    private boolean isModified = false;
    private static boolean isOvertypeMode;
    private static InputMap defaultInputMap = null;
    private Caret defaultCaret = new RiderCaret();
    private Caret overtypeCaret = new OvertypeCaret();

    private static final ClipboardOwner defaultClipboardOwner = new ClipboardObserver();
    private AreaUndoManager undoManager = null;
    private final static Logger logger = Logger.getLogger(RiderArea.class.getName());
    /**
     * a data flavor for transferables processed by this component
     */
    public final static DataFlavor df =
            new DataFlavor(StyledContent.class, "StyledContent");

    //final private DropTarget dropTarget = new DropTarget(this, this);

    final private DragSource dragSource = new DragSource();

    private int lastSelStart = 0;

    private int lastSelEnd = 0;

    private int dndEventLocation = 0;

    private boolean dragStartedHere = false;


    private static int maxTI89FontWidth = 0;
    private static int maxTI92FontWidth = 0;

    public static final int FULLVIEWBORDER = 0;
    public static final int TI89VIEWBORDER = 1;
    public static final int TI92VIEWBORDER = 2;

    private int borderType = FULLVIEWBORDER;
    private int maxRightWidth = -1;
    //    private boolean noModifyChange = false;
    private final static KeyListener alfaBetaKeyListener = new AlphaBetaKeyListener();
    private boolean tooltipSet = false;

    private static int max1CharWidth = 0;
    private boolean busy = false;
    public static final String MODIFIED_PROPERTY = "modified";


    static {
        ScrollGestureRecognizer.getInstance();
    }

    public RiderArea() {
        super();
        init();
    }

    public final RiderDocument getDoc() {
        return (RiderDocument) this.getDocument();
    }

// --Commented out by Inspection START (4.8.05 17:31):
//    public final RiderEditorKit getKit() {
//        return (RiderEditorKit) this.getEditorKit();
//    }
// --Commented out by Inspection STOP (4.8.05 17:31)

    // private void updateRightInset() {
    //  ((AreaBorder) this.getBorder()).setRightInset(getWidth() - getTI89MaxTextWidth() - LEFT_BORDER_WIDTH);
    //revalidate();
    //repaint();
    //        SwingUtilities.invokeLater(new Runnable() {
    //            public void run() {
    //                repaint();
    //            }
    //        });
    //  this.setBorder(new AreaBorder(2, LEFT_BORDER_WIDTH, 0, getWidth() - getTI89MaxTextWidth() - LEFT_BORDER_WIDTH));
    //}


    public Font getFont() {
        return RiderStyles.getAreaFont();
    }

    public Color getBackground() {
        return ColorStyles.getColor(ColorStyles.COLOR_AREA_BG);
    }

    private void init() {
        new DropTarget(this, this);
        if (maxTI89FontWidth == 0)
            maxTI89FontWidth = this.getFontMetrics(RiderStyles.getAreaFont()).stringWidth(RiderStyles.TI89MAXLENGTHSTRING) + 1;
        if (maxTI92FontWidth == 0)
            maxTI92FontWidth = this.getFontMetrics(RiderStyles.getAreaFont()).stringWidth(RiderStyles.TI92MAXLENGTHSTRING) + 1;
        if (max1CharWidth == 0)
            max1CharWidth = this.getFontMetrics(RiderStyles.getAreaBigFont()).stringWidth("M") + 1;
        dragSource.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_MOVE, this);
        defaultCaret.setBlinkRate(getCaret().getBlinkRate());
        this.setCaret(defaultCaret);
        createUndoManager();
        //undoManager = new AreaUndoManager(this); // after setting of document
        if (defaultInputMap == null) {
            Swinger.addKeyActions(this);
            defaultInputMap = initActions(this.getInputMap());
        }
        this.setInputMap(JComponent.WHEN_FOCUSED, defaultInputMap);
        final MouseListener[] listeners = this.getMouseListeners();
        for (MouseListener listener : listeners) this.removeMouseListener(listener);
        this.addMouseMotionListener(new MouseMoveAdapter());
        this.addMouseListener(new MouseClickAdapter());
        for (MouseListener listener1 : listeners) this.addMouseListener(listener1);
        this.addKeyListener(alfaBetaKeyListener);
        overtypeCaret.setBlinkRate(defaultCaret.getBlinkRate());
        setOvertypeMode(false);
        if (AppPrefs.getProperty(AppPrefs.HIGHLIGHT_LINE, true))
            CurrentLineHighlighter.install(this);
        if (AppPrefs.getProperty(AppPrefs.MATCH_BRACKETS, true))
            BracketMatcher.install(this);
        //   setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        //this.addCaretListener(this);
        //this.add(ContextMenu.getInstance());
    }

    public void initBorder() {
        setViewBorder(AppPrefs.getProperty(AppPrefs.USE_EMULATION_CODE, FULLVIEWBORDER), false);
        final AreaBorder border = new AreaBorder(2, LEFT_BORDER_WIDTH, 500, 4, this);
        this.setBorder(border);
        UIManager.addPropertyChangeListener(border);
    }


    boolean showRightBorder() {
        return borderType != FULLVIEWBORDER;
    }

    boolean limitRightBorder() {
        return borderType != FULLVIEWBORDER;
    }

    public final UndoManager getUndoManager() {
        return undoManager;
    }

    public final int getViewBorder() {
        return borderType;
    }

    //private static final int TOP_MARGIN = 0;
    private static final int BOTTOM_MARGIN = 15;

    public void scrollRectToVisible(final Rectangle aRect) {
        final Dimension extentSize = getVisibleRect().getSize();
        /* aRect.y = aRect.y - TOP_MARGIN; */
        //aRect.x = aRect.x;
        aRect.height = Math.min(aRect.height + /*TOP_MARGIN +*/ BOTTOM_MARGIN, extentSize.height);
        super.scrollRectToVisible(aRect);
        //        if (!getCaret().isVisible()) {
        //            getCaret().setVisible(true);
        //        }
    }

    public final void setViewBorder(final int type, final boolean update) {
        switch (type) {
            case FULLVIEWBORDER:
                maxRightWidth = -1;
                break;
            case TI89VIEWBORDER:
                maxRightWidth = RiderArea.maxTI89FontWidth;
                break;
            case TI92VIEWBORDER:
                maxRightWidth = RiderArea.maxTI92FontWidth;
                break;
            default:
                return;
        }
        borderType = type;
        //        final Component component = this;
        if (update)
            getDoc().refreshAll();
        //        SwingUtilities.invokeLater(new Runnable() {
        //            public void run() {
        //                component.validate();
        //                component.repaint();
        //            }
        //        });
    }

    /*
	 *	Set the caretPosition to use depending on overtype/insert mode
	 */
    private void setOvertypeMode(final boolean overtype) {
        isOvertypeMode = overtype;
        final int pos = getCaretPosition();
        this.putClientProperty("overtype", overtype);
        this.firePropertyChange("overtype", overtype, overtype);
        if (overtype)
            setCaret(overtypeCaret);
        else {
            setCaret(defaultCaret);
            getDoc().refresh(pos, 1);
        }
        setCaretPosition(pos);
    }

    int getMaxTextWidth() {
        return maxRightWidth;
    }

    public void freeUpResources() {
        this.getUndoManager().discardAllEdits();
        CurrentLineHighlighter.uninstall(this);
        BracketMatcher.uninstall(this);
        this.removeAll();
        UIManager.removePropertyChangeListener((AreaBorder) this.getBorder());
        undoManager.freeUpResources();
        undoManager = null;
        defaultInputMap = null;
        defaultCaret = null;
        overtypeCaret = null;
    }

    public AreaImage getSelectedImage() {
        final int selStart = this.getSelectionStart();
        if (this.getSelectionEnd() - selStart == 1) {
            return RiderStyles.getImage(getDoc().getCharacterElement(selStart));
        }
        return null;
    }

    public void setBracketMatching(final boolean enable) {
        if (enable)
            BracketMatcher.install(this);
        else
            BracketMatcher.uninstall(this);
    }

    public void setCurrentLineHighlight(final boolean enable) {
        if (enable)
            CurrentLineHighlighter.install(this);
        else
            CurrentLineHighlighter.uninstall(this);
    }

    public Insets getAutoscrollInsets() {
        return scrollSupport.getAutoscrollInsets();
    }

    public void autoscroll(Point cursorLocn) {
        scrollSupport.autoscroll(cursorLocn);
    }

    public Collection<File> getAllPictureFilePaths() {
        final Collection<File> result = new LinkedList<File>();
        final Element sectionElem = getDoc().getDefaultRootElement();
        final int paraCount = sectionElem.getElementCount();
        Element el, paraEl;
        int elCount;
        for (int i = 0; i < paraCount; i++) {
            paraEl = sectionElem.getElement(i);
            elCount = paraEl.getElementCount();
            for (int j = 0; j < elCount; j++) {
                el = paraEl.getElement(j);
                if (RiderStyles.isImage(el)) {
                    final AreaImage areaImage = RiderStyles.getImage(el);
                    final File originalFile = areaImage.getOriginalFile();
                    if (originalFile != null)
                        result.add(originalFile);
                }
            }
        }
        return result;
    }


    private final class MouseClickAdapter extends MouseAdapter {
        private boolean isImage;
        private boolean inLeftBorder;

        public final void mouseReleased(final MouseEvent e) {
            lastSelStart = getSelectionStart();
            lastSelEnd = getSelectionEnd();

        }

        public void mousePressed(MouseEvent e) {
            final JTextPane area = (JTextPane) e.getSource();
            isImage = false;
            inLeftBorder = isInLeftBorder(e);
            if (!inLeftBorder) {
                final int position = area.viewToModel(e.getPoint());
                final Element el = area.getStyledDocument().getCharacterElement(position);
                if (RiderStyles.isImage(el)) {
                    try {
                        final Rectangle rect = area.modelToView(position);
                        if (rect.x <= e.getX()) {
                            isImage = true;
                            area.select(el.getStartOffset(), el.getEndOffset());
                        }
                        e.consume();
                    } catch (BadLocationException ex) {
                        LogUtils.processException(logger, ex);
                    }
                }
            }
        }

        public final void mouseClicked(final MouseEvent e) {
            final RiderArea area = (RiderArea) e.getSource();
            if (inLeftBorder) {
                final int pos = area.getCaretPosition();
                final Element el = area.getBookmarkElement(e, area.viewToModel(e.getPoint()));
                if (el != null) {
                    UpdateBreakpointAction.getInstance().toggleBookmark(area.getDoc(), el.getStartOffset());
                    area.setCaretPosition(pos);
                }
                e.consume();
            } else {
                if (isImage)
                    area.select(lastSelStart, lastSelEnd);
                if (SwingUtilities.isRightMouseButton(e)) {
                    ContextMenu.getInstance().show(area, e.getX(), e.getY());
                    e.consume();
                } else {
                    if (e.getClickCount() > 1) {
                        if (isImage) {
                            SwingUtilities.invokeLater(new Runnable() {
                                public void run() {
                                    ChangeImagePropertiesAction.getInstance().actionPerformed(null);

                                }
                            });
                            e.consume();
                        }
                        lastSelStart = getSelectionStart();
                        lastSelEnd = getSelectionEnd();
                    }
                }
            }
        }
    }

    public final void setDocument(final Document doc) {
        //        if (this.getDocument() != null)
        //            this.getDocument().removeDocumentListener(this);
        //        ((RiderDocument) doc).setEditor(this);
        super.setDocument(doc);    //call to super
        //        doc.addDocumentListener(this);
        setModified(false);
        //createUndoManager();
    }

    private void createUndoManager() {
        undoManager = new AreaUndoManager(this);
    }

    public final void setModified(final boolean modified) {
        if (isModified != modified) {
//            if (noModifyChange)
//                return;
            this.isModified = modified;//must be first
            this.putClientProperty(MODIFIED_PROPERTY, modified);
            //this.firePropertyChange("modified", !modified, modified);
        }
    }

    public final boolean isModified() {
        return this.isModified;
    }
    //    public final void setAttributeSet(final AttributeSet attr,
    //                                      final boolean setParagraphAttributes) {
    //        final int selStart = this.getSelectionStart();
    //        final int selEnd = this.getSelectionEnd();
    //
    //        if (setParagraphAttributes) {
    //            document.setParagraphAttributes(selStart,
    //                    selEnd - selStart, attr, false);
    //        } else if (selStart != selEnd)
    //            document.setCharacterAttributes(selStart,
    //                    selEnd - selStart, attr, false);
    //        else {
    //            final MutableAttributeSet inputAttributes =
    //                    this.getInputAttributes();
    //            inputAttributes.addAttributes(attr);
    //        }
    //    }


    private void replaceSelection(final StyledContent content) {
        final Caret caret = getCaret();
        try {
            final int p0 = Math.min(caret.getDot(), caret.getMark());
            final int p1 = Math.max(caret.getDot(), caret.getMark());
            if (p0 != p1) {
                this.getDocument().remove(p0, p1 - p0);
            }
            if (content != null) {
                content.insert(this, p0);
            }
        } catch (Exception e) {
            LogUtils.processException(logger, e);
        }
    }

    /**
     * Enables to group changes to undo as one
     * @param state if true - starts to create a group of change, false ends it
     */
    public void makeGroupChange(final boolean state) {
        setBusy(state);
        firePropertyChange("undoredo", !state, state);
    }

// --Commented out by Inspection START (4.8.05 17:30):
//    public final void noModifyUpdate(final boolean update) {
//        this.noModifyChange = update;
//    }
// --Commented out by Inspection STOP (4.8.05 17:30)

    /*
	 *  Override method from JComponent
	 */


    public void replaceSelection(final String text) {
        //  Implement overtype mode by selecting the character at the current
        //  caretPosition position

        if (isOvertypeMode()) {
            final int pos = getCaretPosition();
            if (getSelectedText() == null && pos < getDocument().getLength())
                moveCaretPosition(pos + 1);

        }
        super.replaceSelection(text);
    }

    private static boolean isOvertypeMode() {
        return isOvertypeMode;
    }

    protected final EditorKit createDefaultEditorKit() {
        return new RiderEditorKit();
    }

    public final void cut() {
        if (isEditable() && isEnabled()) {
            copy();
            replaceSelection("");
        }
    }

    public final void copy() {
        if (getSelectionStart() != getSelectionEnd()) {
            try {
                final StyledContent st = new StyledContent(this);
                final StyledContentSelection contents = new StyledContentSelection(st);
                final Clipboard clipboard = getToolkit().getSystemClipboard();
                clipboard.setContents(contents, defaultClipboardOwner);
            } catch (Exception e) {
                LogUtils.processException(logger, e);
            }
        }
    }

    public final void paste() {
        //this.firePropertyChange("undoredo",false, true);
        final Clipboard clipboard = getToolkit().getSystemClipboard();
        final Transferable content = clipboard.getContents(this);
//TODO
/*
WordRider 0.76devel Error : null
java.lang.NullPointerException
	at net.wordrider.dialogs.pictures.PicturePanel.renderOffscreen(PanelPicture.java:113)
	at net.wordrider.dialogs.pictures.PicturePanel.updateContent(PanelPicture.java:151)
	at net.wordrider.dialogs.pictures.PicturePanel.setLCDBackground(PanelPicture.java:159)
	at net.wordrider.dialogs.pictures.FilterDialog$OutputScreenListener.itemStateChanged(FilterDialog.java:166)
	at javax.swing.AbstractButton.fireItemStateChanged(Unknown Source)
	at javax.swing.AbstractButton$Handler.itemStateChanged(Unknown Source)
	at javax.swing.DefaultButtonModel.fireItemStateChanged(Unknown Source)
	at javax.swing.JToggleButton$ToggleButtonModel.setSelected(Unknown Source)
	at javax.swing.ButtonGroup.setSelected(Unknown Source)
	at net.wordrider.dialogs.JButtonGroup.setSelected(JButtonGroup.java:125)
	at javax.swing.JToggleButton$ToggleButtonModel.setSelected(Unknown Source)
	at javax.swing.AbstractButton.setSelected(Unknown Source)
	at net.wordrider.dialogs.pictures.FilterDialog.initFilters(FilterDialog.java:414)
	at net.wordrider.dialogs.pictures.FilterDialog.<init>(FilterDialog.java:124)
	at net.wordrider.area.actions.InsertPictureAction$1.run(InsertPictureAction.java:122)
	at java.awt.event.InvocationEvent.dispatch(Unknown Source)
	at java.awt.EventQueue.dispatchEvent(Unknown Source)
	at net.wordrider.core.swing.MouseEventQueue.dispatchEvent(MouseEventQueue.java:16)
	at java.awt.EventDispatchThread.pumpOneEventForHierarchy(Unknown Source)
	at java.awt.EventDispatchThread.pumpEventsForHierarchy(Unknown Source)
	at java.awt.EventDispatchThread.pumpEvents(Unknown Source)
	at java.awt.EventDispatchThread.pumpEvents(Unknown Source)
	at java.awt.EventDispatchThread.run(Unknown Source)
*/
        if (content != null) {
            try {
                makeGroupChange(true);
                if (content.isDataFlavorSupported(df))
                    replaceSelection((StyledContent) content.getTransferData(df));
                else if (content.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                    String clipbString = content.getTransferData(DataFlavor.stringFlavor).toString();
                    replaceSelection(BatchPureTextReader.importText(clipbString));
                }
            } catch (Exception e) {
                LogUtils.processException(logger, e);
            } finally {
                makeGroupChange(false);
            }

        }
        //this.firePropertyChange("undoredo",true, false);
    }

    //    public final int getTI89MaxTextWidth() {
    //        return maxTI89FontWidth;
    //    }

    //    public final boolean processKeyBinding(final KeyStroke ks, final KeyEvent e, final int condition, final boolean pressed) {
    //        final int caretPosition = this.getCaretPosition();
    //        if (ks.getKeyEventType() == KeyEvent.KEY_PRESSED) {
    //            if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
    //                if (caretPosition > 0) {
    //                    final Element sectionElem = document.getDefaultRootElement();
    //                    final Element paragraph = sectionElem.getElement(sectionElem.getElementIndex(caretPosition));
    //                    //final int elCount = paragraph.getElementCount();
    //                    if (paragraph.getStartOffset() == caretPosition && paragraph.getElementCount() > 0) {
    //                        final Element el = paragraph.getElement(0);
    //                        if (el.getName().equals(StyleConstants.ComponentElementName)) {
    //                            //StyleConstants.setComponent();
    //                        }
    //                    }
    //                }
    //            } else {
    //                if (RiderStyles.isReadonlySection(document.getParagraphElement(caretPosition))) {
    //                    //System.out.println("it's read only section LASTOPENFOLDER_KEY code " + e.getKeyCode());
    //                    if (!e.isControlDown() && e.getKeyCode() != KeyEvent.VK_DELETE && Character.isUnicodeIdentifierPart(e.getKeyChar())) {
    //                        //System.out.println("stopping a LASTOPENFOLDER_KEY");
    //                        e.setKeyCode(0);
    //                        return false;
    //                    }
    //                }
    //            }
    //        }
    //        return super.processKeyBinding(ks, e, condition, pressed);
    //    }

    public final Color getCaretColor() {
        if (this.hasFocus()) {
            final int caretPos = this.getCaretPosition();
            try {
                if (RiderStyles.isInvert(getInputAttributes()) && Utilities.getRowEnd(this, caretPos) != caretPos)
                    return getBackground();
            } catch (BadLocationException e) {
                LogUtils.processException(logger, e);
            }
        }
        return super.getCaretColor();
    }


    private static InputMap initActions(final InputMap map) {
        map.put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_MASK), RedoAction.getInstance());
        map.put(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK), RedoAction.getInstance());
        map.put(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, InputEvent.ALT_MASK), UndoAction.getInstance());
        map.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, InputEvent.CTRL_MASK), new DeleteWordAction(RiderEditorKit.DIRECTION_NEXT));
        map.put(KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.CTRL_MASK), new SelectWordAction());
        map.put(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, InputEvent.CTRL_MASK), new DeleteWordAction(RiderEditorKit.DIRECTION_PREVIOUS));
        map.put(KeyStroke.getKeyStroke(KeyEvent.VK_0, InputEvent.ALT_MASK), AligmentLeftAction.getInstance());
        map.put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, InputEvent.SHIFT_MASK), new RiderEditorKit.RemoveTabAction());
        map.put(KeyStroke.getKeyStroke("F4"), RotateAligmentStyleAction.getInstance());
        map.put(KeyStroke.getKeyStroke("F5"), RotateMarginStyleAction.getInstance());
        map.put(GetNextTabAction.getInstance().getKeyStroke(), GetNextTabAction.getInstance());
        map.put(GetPrevTabAction.getInstance().getKeyStroke(), GetPrevTabAction.getInstance());
        return map;
    }

    final private class MouseMoveAdapter extends MouseMotionAdapter {
        public void mouseMoved(final MouseEvent e) {
            areaMouseMoved(e);
        }

    }

    private static boolean isInLeftBorder(final MouseEvent e) {
        return e.getX() < LEFT_BORDER_WIDTH - (LEFT_BORDER_WIDTH - AreaBorder.SHADOW_BAR_WIDTH);
    }

//    private AreaImage getMoveableComponent(final int pos) {
//        if (pos != -1) {
//            final Element el = this.getStyledDocument().getParagraphElement(pos);
//            return RiderStyles.isMoveableComponent(el);
//        }
//        return null;
//    }

    private Element getBookmarkElement(final MouseEvent e, final int pos) {
        if (pos != -1) {
            final Element el = this.getStyledDocument().getParagraphElement(pos);
            if (RiderStyles.isReadonlySection(el) || RiderStyles.isMath(el))
                return null;
            final Rectangle rect;
            try {
                rect = this.modelToView(el.getStartOffset());
            } catch (BadLocationException ex) {
                LogUtils.processException(logger, ex);
                return null;
            }
            if (e.getY() >= rect.y + 2 && e.getY() <= rect.height + rect.y - 2)
                return el;
        }
        return null;
    }

    private static final class ClipboardObserver implements ClipboardOwner {
        public final void lostOwnership(final Clipboard clipboard, final Transferable contents) {
        }
    }

    public void switchOverTypeMode() {
        setOvertypeMode(!isOvertypeMode());
    }

//    public final void removeUpdate(final DocumentEvent e) {
//        setModified(true);
//
//    }


    public final String getToolTipText(final MouseEvent event) {
        final TextUI ui = getUI();
        if (ui != null) {
            return ui.getToolTipText(this, new Point(event.getX(),
                    event.getY()));
        }
        return null;
    }

    private void areaMouseMoved(final MouseEvent e) {
         final RiderArea area = (RiderArea) e.getSource();
        if (isInLeftBorder(e)) {
            final int position = area.viewToModel(e.getPoint());
            if (area.getBookmarkElement(e, position) != null) {
                if (area.getCursor().getType() != Cursor.HAND_CURSOR)
                    area.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            } else if (area.getCursor().getType() != Cursor.DEFAULT_CURSOR)
                area.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        } else {
            if (!tooltipSet) {
                final int type = area.getCursor().getType();
                if (type != Cursor.TEXT_CURSOR && type != Cursor.WAIT_CURSOR)
                    area.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
            }
        }
        final String tooltip = area.getToolTipText(e);
        if (tooltip != null) {
            if (!tooltipSet) {
                area.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                area.setToolTipText(tooltip);
                tooltipSet = true;
            }
        } else if (tooltipSet) {
            area.setToolTipText("");
            tooltipSet = false;
            if (area.getCursor().getType() != Cursor.TEXT_CURSOR)
                area.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
        }
    }

    public void selectReverse(int selectionStart, int selectionEnd) {

        final int docLength = getDocument().getLength();

        if (selectionStart < 0)
            selectionStart = 0;

        if (selectionEnd < 0)
            selectionStart = 0;

        if (selectionStart > docLength)
            selectionStart = docLength;

        if (selectionEnd > docLength)
            selectionEnd = docLength;

        setCaretPosition(selectionStart);
        moveCaretPosition(selectionEnd);
    }

/* ------ start of drag and drop implementation -------------------------
       (partly using example code of the Java Tutorial)
*/

    /**
     * a drag gesture has been initiated
     */
    public void dragGestureRecognized(final DragGestureEvent event) {
        if (AppPrefs.getProperty(AppPrefs.DRAG_AND_DROP, true)) {
            final int selStart = getSelectionStart();
//       int selEnd = getSelectionEnd();
            try {
                if ((lastSelEnd > lastSelStart) &&
                        (selStart >= lastSelStart) &&
                        (selStart < lastSelEnd)) {
                    dragStartedHere = true;
                    select(lastSelStart, lastSelEnd);
                    final StyledContent text = new StyledContent(this);
                    final StyledContentSelection trans = new StyledContentSelection(text);
                    dragSource.startDrag(event, DragSource.DefaultMoveDrop, trans, this);
                }
            } catch (Exception e) {
                LogUtils.processException(logger, e);
            }
        } else event.getTriggerEvent().consume();
    }

    /**
     * this message goes to DragSourceListener, informing it that the dragging has entered the DropSite
     */
    public void dragEnter(final DragSourceDragEvent event) {
    }

    /**
     * this message goes to DragSourceListener, informing it that the dragging has exited the DropSite
     */
    public void dragExit(final DragSourceEvent event) {
    }

    /**
     * this message goes to DragSourceListener, informing it that the dragging is currently ocurring over the DropSite
     */
    public void dragOver(final DragSourceDragEvent event) {
    }

    /**
     * this message goes to DragSourceListener, informing it that the dragging has ended
     */
    public void dragDropEnd(final DragSourceDropEvent event) {
        dragStartedHere = false;
    }

    /**
     * is invoked when the user changes the dropAction
     */
    public void dropActionChanged(final DragSourceDragEvent event) {
    }

    /**
     * is invoked when you are dragging over the DropSite
     */
    public void dragEnter(final DropTargetDragEvent event) {
    }

    /**
     * is invoked when you are exit the DropSite without dropping
     */
    public void dragExit(final DropTargetEvent event) {
    }

    /**
     * is invoked when a drag operation is going on
     */
    public void dragOver(final DropTargetDragEvent event) {
        dndEventLocation = viewToModel(event.getLocation());
        try {
            setCaretPosition(dndEventLocation);
            CurrentLineHighlighter.updateDnD(this);
        } catch (Exception e) {
            LogUtils.processException(logger, e);
        }
    }

    public void drop(final DropTargetDropEvent event) {
        dndEventLocation = viewToModel(event.getLocation());
        if ((dndEventLocation >= lastSelStart) &&
                (dndEventLocation <= lastSelEnd)) {
            event.rejectDrop();
            select(lastSelStart, lastSelEnd);
        } else {
            try {
                final Transferable transferable = event.getTransferable();
                if (transferable.isDataFlavorSupported(df)) {
                    final StyledContent s = (StyledContent) transferable.getTransferData(df);
                    doDrop(event, s);
                } else if (transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                    final String s = (String) transferable.getTransferData(DataFlavor.stringFlavor);
                    doDrop(event, s);
                } else {
                    event.rejectDrop();
                }
            } catch (Exception e) {
                LogUtils.processException(logger, e);
                event.rejectDrop();
            }
        }
    }

    /**
     * do the drop operation consisting of adding the dragged element and necessarily removing the dragged element from
     * the original position
     */
    private void doDrop(final DropTargetDropEvent event, final Object s) {
        try {
            this.firePropertyChange("undoredo", false, true);
            int removeOffset = 0;
            int moveOffset = 0;
            final int newSelStart;
            final int newSelEnd;
            event.acceptDrop(DnDConstants.ACTION_MOVE);
            addElement(s);
            if (dndEventLocation < lastSelStart) {
                removeOffset = s.toString().length();
            } else {
                moveOffset = s.toString().length();
            }
            newSelEnd = dndEventLocation + (lastSelEnd - lastSelStart) - moveOffset;
            newSelStart = dndEventLocation - moveOffset;
            if (dragStartedHere) {
                lastSelStart += removeOffset;
                lastSelEnd += removeOffset;
                removeElement();
            }
            lastSelEnd = newSelEnd;
            lastSelStart = newSelStart;
            select(lastSelStart, lastSelEnd);
            event.getDropTargetContext().dropComplete(true);
        } finally {
            this.firePropertyChange("undoredo", true, false);
        }

    }

    /**
     * is invoked if the user modifies the current drop gesture
     */
    public void dropActionChanged(final DropTargetDragEvent event) {
    }

    private void addElement(final Object s) {
        try {
            setCaretPosition(dndEventLocation);
            if (s instanceof StyledContent) {
                replaceSelection((StyledContent) s);
            } else if (s instanceof String) {
                replaceSelection((String) s);
            }
        } catch (Exception e) {
            LogUtils.processException(logger, e);
        }
    }

    private void removeElement() {
        select(lastSelStart, lastSelEnd);
        replaceSelection("");
    }

    public synchronized void setBusy(final boolean busy) {
        this.busy = busy;
        getCaret().setVisible(!busy);
    }


    public synchronized boolean isBusy() {
        return busy;
    }

    public int getMaxMarginWidth() {
        return ((borderType == FULLVIEWBORDER) ? getWidth() : getMaxTextWidth()) - max1CharWidth;
    }

    // ------ end of drag and drop implementation ----------------------------
}
