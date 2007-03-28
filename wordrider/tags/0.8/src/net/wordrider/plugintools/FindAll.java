package net.wordrider.plugintools;

import info.clearthought.layout.TableLayout;
import net.wordrider.area.AlphaBetaKeyListener;
import net.wordrider.area.ColorStyles;
import net.wordrider.area.RiderEditorKit;
import net.wordrider.area.RiderStyles;
import net.wordrider.core.AppPrefs;
import net.wordrider.core.Lng;
import net.wordrider.core.MainApp;
import net.wordrider.core.actions.RiderSwingWorker;
import net.wordrider.core.managers.AreaManager;
import net.wordrider.core.managers.FileChangeEvent;
import net.wordrider.core.managers.FileInstance;
import net.wordrider.core.managers.interfaces.IFileChangeListener;
import net.wordrider.core.managers.interfaces.IFileInstance;
import net.wordrider.core.swing.ColorPainterFactory;
import net.wordrider.core.swing.CustomLayoutConstraints;
import net.wordrider.core.swing.RecentsComboModel;
import net.wordrider.core.swing.SwingUtils;
import net.wordrider.utilities.LogUtils;
import net.wordrider.utilities.Swinger;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.lang.reflect.InvocationTargetException;


/**
 * @author Vity
 */
public final class FindAll extends PluginTool implements TreeSelectionListener, IFileChangeListener {
    private JComboBox comboBox;
    private final JButton btnFindAll = Swinger.getButton("findall.btnFindAll");
    private final JToggleButton btnUnhighlight = Swinger.getToggleButton("findall.togglehighlight", false);

    private final static String PREFS_IGNORE_CASE = "findall_ignoreCase";
    private final static String PREFS_REGEXP = "findall_regexp";
    private final static String PREFS_HIGHLIGHT = "findall_highight";
    private final static String PREFS_INALLFILES = "findall_inAllFiles";
    private final static String PREFS_WHOLEWORDS = "findall_wholewords";

    private final JCheckBox checkIgnoreCase = Swinger.getCheckBox("findall.ignoreCase", PREFS_IGNORE_CASE, false);
    private final JCheckBox checkRegexp = Swinger.getCheckBox("findall.regexp", PREFS_REGEXP, false);
    private final JCheckBox checkHighlight = Swinger.getCheckBox("findall.highlight", PREFS_HIGHLIGHT, true);
    private final JCheckBox checkInAllFiles = Swinger.getCheckBox("findall.inAllFiles", PREFS_INALLFILES, true);
    private final JCheckBox checkWholeWords = Swinger.getCheckBox("findall.wholeWords", PREFS_WHOLEWORDS, false);


    private final static String PREFS_FINDALL = "findall_pref";
    private DefaultTreeModel treeModel = null;
    private DefaultMutableTreeNode rootNode = null;
    private JTree tree;
    private final static Logger logger = Logger.getLogger(FindAll.class.getName());

    private final static Highlighter.HighlightPainter underLinePainter = ColorPainterFactory.createJaggedUnderlinePainter(ColorStyles.COLOR_HIGHLIGHT_FOUND);

    public FindAll() {
        super();
        try {
            MyCellRenderer cellRenderer = new MyCellRenderer();
            cellRenderer.setLeafIcon(null);
            rootNode = new DefaultMutableTreeNode();
            treeModel = new DefaultTreeModel(rootNode);
            tree = new JTree(treeModel);
            tree.setShowsRootHandles(false);
            tree.setCellRenderer(cellRenderer);
            tree.setFont(RiderStyles.getAreaFont().deriveFont((float) RiderStyles.SIZE_MINI - 1));

            tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
            tree.addTreeSelectionListener(this);
            tree.addMouseListener(new MouseAdapter() {
                public void mouseClicked(final MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        AreaManager.getInstance().grabActiveFocus();
                        e.consume();
                    }
                }
            });

            final ClickButtonListener clickListener = new ClickButtonListener();
            btnFindAll.addActionListener(clickListener);
            btnUnhighlight.addActionListener(clickListener);
            checkRegexp.addActionListener(clickListener);
            final FocusListener focusListener = new Swinger.SelectAllOnFocusListener();
            comboBox = new JComboBox(Swinger.loadSearchUsedList(PREFS_FINDALL));
            comboBox.setEditable(true);
            final KeyListener keyListener = new ActionKeyListener();
            tree.addKeyListener(keyListener);
            Component editorComponent = comboBox.getEditor().getEditorComponent();
            if (editorComponent instanceof JTextComponent) {
                final JTextComponent component = (JTextComponent) editorComponent;
                component.setFont(RiderStyles.getAreaFont());
                component.addKeyListener(keyListener);
                component.getDocument().addDocumentListener(new InputFieldsMethodListener());
                component.addFocusListener(focusListener);
                Swinger.addKeyActions(component);
            }
            comboBox.setPreferredSize(new Dimension(170, 23));

            // become a listener on the document to update the List.
            final Container contentPane = getContentPane();
            contentPane.setFocusCycleRoot(true);
            // configure the panel and frame containing it.
            tree.setBorder(new EmptyBorder(2, 2, 2, 4));
            updateSearchingEnabled();
            updateUnhighlightEnabled(false);
            updateWholeWordsEnabled();
            final double f = TableLayout.FILL;
            final double p = TableLayout.PREFERRED;
            final TableLayout mgr = new TableLayout(new double[]{p, f}, new double[]{p, f, p, p, p, p, p, 2, p});
            mgr.setHGap(10);
            mgr.setVGap(2);
            contentPane.setLayout(mgr);
            contentPane.add(comboBox, new CustomLayoutConstraints(0, 0, 2, 1));
            contentPane.add(new JScrollPane(tree), new CustomLayoutConstraints(0, 1, 2, 1));
            contentPane.add(checkRegexp, new CustomLayoutConstraints(0, 2, 2, 1));
            contentPane.add(checkIgnoreCase, new CustomLayoutConstraints(0, 3, 2, 1));
            contentPane.add(checkWholeWords, new CustomLayoutConstraints(0, 4, 2, 1));
            contentPane.add(checkHighlight, new CustomLayoutConstraints(0, 5, 2, 1));
            contentPane.add(checkInAllFiles, new CustomLayoutConstraints(0, 6, 2, 1));
            contentPane.add(btnFindAll, new CustomLayoutConstraints(1, 8, 1, 1, TableLayout.RIGHT, TableLayout.CENTER));
            contentPane.add(btnUnhighlight, new CustomLayoutConstraints(0, 8, 1, 1, TableLayout.LEFT, TableLayout.CENTER));
            AreaManager.getInstance().addFileChangeListener(this);

            getComponent().setPreferredSize(new Dimension(280, 300));
        } catch (Exception e) {
            LogUtils.processException(logger, e);
        }
    }

    private void updateUnhighlightEnabled(boolean value) {
        if (btnUnhighlight.isEnabled() != value)
            btnUnhighlight.setEnabled(value);
    }

    private void updateSearchingEnabled() {
        boolean old = btnFindAll.isEnabled();
        boolean result = !Swinger.isEmpty(comboBox) && AreaManager.getInstance().getActiveInstance() != null;
        if (old != result)
            btnFindAll.setEnabled(result);
    }

    private final class InputFieldsMethodListener implements DocumentListener {

        public void changedUpdate(final DocumentEvent e) {
            updateSearchingEnabled();
        }

        public void insertUpdate(final DocumentEvent e) {
            updateSearchingEnabled();
        }

        public void removeUpdate(final DocumentEvent e) {
            updateSearchingEnabled();
        }
    }

    private void clearHistory(final String searchFor) {
        ((RecentsComboModel) comboBox.getModel()).addElement(searchFor);
        synchronized (tree.getTreeLock()) {
            for (int i = rootNode.getChildCount() - 1; i >= 0; i--) {
                removeHighlights((DefaultMutableTreeNode) rootNode.getChildAt(i), true);
                rootNode.remove(i);
            }
            updateRootCount(-1, searchFor);
            treeModel.reload();
        }
    }

    private static void removeHighlights(DefaultMutableTreeNode parent, final boolean removeFromTree) {
        final SearchResult result = (SearchResult) parent.getUserObject();
        if (result.isHighlighted()) {
            final JTextComponent textComponent = result.getFileInstance().getRiderArea();
            final Highlighter highlighter = textComponent.getHighlighter();
            for (int i = parent.getChildCount() - 1; i >= 0; i--) {
                final SearchResultItem o = (SearchResultItem) ((DefaultMutableTreeNode) parent.getChildAt(i)).getUserObject();
                o.unhighLight(highlighter);
                if (removeFromTree)
                    parent.remove(i);
            }

            if (!removeFromTree) {
                result.setHighLighted(false);
                AreaManager.getInstance().repaintActive();
                //   textComponent.repaint();
            }

        }
    }


    private final class GlobalSearch extends RiderSwingWorker {

        public GlobalSearch() {
            super(true);
        }

        public final Object construct() {
            final JProgressBar progress = dialogToClose.getProgressBar();
            progress.setIndeterminate(true);
            progress.setStringPainted(true);
            showInfoWhileLoading("findall.msgSearching");
            final boolean hasBtnFocus = btnFindAll.isFocusOwner();
            if (hasBtnFocus) {
                tree.grabFocus();
                tree.requestFocus();
            }
            btnFindAll.setEnabled(false);
            updateUnhighlightEnabled(false);
            btnUnhighlight.setSelected(false);
            tree.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            try {
                findAll();
            } catch (BadLocationException e) {
                LogUtils.processException(logger, e);
            } finally {
                tree.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                btnFindAll.setEnabled(true);
                final boolean found = rootNode.getChildCount() > 0;
                updateUnhighlightEnabled(found);
                btnUnhighlight.setSelected(found && checkHighlight.isSelected());
                if (!found)
                    btnFindAll.getToolkit().beep();
            }
            return null;
        }

    }

    private void doFindAll() {
        final RiderSwingWorker worker = new GlobalSearch();
        worker.init();
        worker.get();
    }


    private void findAll() throws BadLocationException {
        String searchFor = comboBox.getEditor().getItem().toString();
        if (searchFor == null || searchFor.length() == 0)
            return;
        final boolean isRegexp = checkRegexp.isSelected();
        final boolean caseInsensitive = checkIgnoreCase.isSelected();
        Pattern regexp = null;
        if (isRegexp) {
            try {
                regexp = Pattern.compile(searchFor, (caseInsensitive) ? Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.UNICODE_CASE : Pattern.MULTILINE | Pattern.UNICODE_CASE);
            } catch (PatternSyntaxException e) {
                getContentPane().getToolkit().beep();
                Swinger.showErrorDialog(MainApp.getInstance().getMainAppFrame(), SwingUtils.getMessage(e, "Invalid regexp:", "near index"));
                Swinger.inputFocus(comboBox);
                return;
            }
        }

        clearHistory(searchFor);
        //set cursor
        final Collection<FileInstance> collection;
        if (checkInAllFiles.isSelected())
            collection = AreaManager.getInstance().getOpenedInstances();
        else {
            collection = new ArrayList<FileInstance>(1);
            collection.add(AreaManager.getInstance().getActiveInstance());
        }
        final String searchString = searchFor;
        if (caseInsensitive && !isRegexp)
            searchFor = searchFor.toLowerCase();

        final int searchForLength = searchFor.length();
        int sumFound = 0;
        final int maxVisibleRows = tree.getVisibleRect().height / tree.getRowBounds(0).height;
        final int minVisibleFolders = 1;
        final boolean wholeWordsOnly = checkWholeWords.isSelected();
        for (Object aCollection : collection) {
            final IFileInstance instance = (IFileInstance) aCollection;
            final JTextComponent textComponent = instance.getRiderArea();
            final Document doc = textComponent.getDocument();
            final int textLength = doc.getLength();
            String searchIn = doc.getText(0, textLength);
            if (caseInsensitive && !isRegexp)
                searchIn = searchIn.toLowerCase();
            final DefaultMutableTreeNode parentNode = new DefaultMutableTreeNode();
            int count = 0;
            if (!isRegexp) {
                int result;
                int start = 0;
                while ((result = searchIn.indexOf(searchFor, start)) != -1) {
                    start = result + searchForLength;
                    if (!wholeWordsOnly || (wholeWordsOnly && wholeWordsCheck(result, start, searchIn, textLength))) {
                        ++count;
                        addSearchResult(instance, textComponent, doc, parentNode, result, start, searchString);
                    }
                }
            } else {
                final Matcher matcher = regexp.matcher(searchIn);
                while (matcher.find()) {
                    ++count;
                    addSearchResult(instance, textComponent, doc, parentNode, matcher.start(), matcher.end(), matcher.group());
                }
            }
            if (count != 0) {
                SearchResult searchResult = (SearchResult) parentNode.getUserObject();
                searchResult.updateFoundCount(count);
                treeModel.valueForPathChanged(new TreePath(parentNode.getPath()), searchResult);
                if (rootNode.getChildCount() <= minVisibleFolders || sumFound < maxVisibleRows) {
                    tree.expandPath(new TreePath(treeModel.getPathToRoot(parentNode)));
                }
                if (checkHighlight.isSelected() && textComponent.isVisible()) {
                    AreaManager.getInstance().repaintActive();
                }
            }
            sumFound += count;
        }
        final int sumFounded = sumFound; 
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    if (sumFounded > 0) {
                        final TreeNode node1 = rootNode.getChildAt(0);
                        DefaultMutableTreeNode node = (DefaultMutableTreeNode) node1.getChildAt(0);
                        tree.setSelectionPath(new TreePath(node.getPath()));
                    }
                    updateRootCount(sumFounded, searchString);
                }
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
            //
        } catch (InvocationTargetException e) {
            //
            e.printStackTrace();
        }

    }

    private static boolean wholeWordsCheck(int start, int end, String searchIn, int textLength) {
        return (start == 0 || RiderEditorKit.WORD_SEPARATORS.get(searchIn.charAt(start - 1))) && (end == textLength || RiderEditorKit.WORD_SEPARATORS.get(searchIn.charAt(end)));
    }

    private void updateRootCount(final int count, final String searchFor) {
        final String infoString = Lng.getLabel("findall.rootInfo", new Object[]{searchFor, (count < 0) ? "" : "{" + count + "}"});
        treeModel.valueForPathChanged(new TreePath(rootNode), infoString);
    }


    private void addSearchResult(final IFileInstance instance, final JTextComponent textComponent, final Document doc, final DefaultMutableTreeNode parentNode, final int resultStart, final int end, final String foundText) throws BadLocationException {
        final int lineStart = Utilities.getRowStart(textComponent, resultStart);
        final int lineEnd = Utilities.getRowEnd(textComponent, end);
        final String textBefore = (lineStart < resultStart) ? doc.getText(lineStart, resultStart - lineStart) : "";
        final String textAfter = (lineEnd > end) ? doc.getText(end, lineEnd - end) : "";
        final SearchResultItem searchResult = new SearchResultItem(resultStart, end, textBefore, foundText, textAfter);
        if (checkHighlight.isSelected()) {
            searchResult.highLight(textComponent.getHighlighter());
        }
        final MutableTreeNode node = new DefaultMutableTreeNode(searchResult, false);
        final int childCount = parentNode.getChildCount();
        if (childCount == 0) {
            parentNode.setUserObject(new SearchResult(instance, checkHighlight.isSelected()));
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    synchronized (tree.getTreeLock()) {
                        treeModel.insertNodeInto(parentNode, rootNode, rootNode.getChildCount());
                    }
                }
            });
        }
        treeModel.insertNodeInto(node, parentNode, childCount);
    }

    private final class ActionKeyListener extends AlphaBetaKeyListener {
        public void keyPressed(final KeyEvent e) {
            final boolean isEnter = e.getKeyCode() == KeyEvent.VK_ENTER;
            if (e.getSource() instanceof JTextComponent) {
                if (isEnter) {
                    e.consume();
                    if (btnFindAll.isEnabled())
                        btnFindAll.doClick();
                    else btnFindAll.getToolkit().beep();
                } else super.keyPressed(e);
            } else { //from tree
                if (isEnter) {
                    AreaManager.getInstance().grabActiveFocus();
                    e.consume();
                }
            }
        }
    }


    public final String getName() {
        return Lng.getLabel("findall.title");
    }

    public String getTabName() {
        return getName();
    }

    public final Icon getIcon() {
        return Swinger.getIcon("find.gif");
    }

    public final String getTip() {
        return Lng.getLabel("findall.tooltip");
    }

    public void activate() {
        Swinger.inputFocus(comboBox);
        logger.info("FindAll activated");
    }

    public void deactivate() {
        logger.info("FindAll deactivated");
    }

    public void updateData() {
//        if (editor != null && treeModel != null)
//            treeModel.updateDataList();
    }


    private static final class MyCellRenderer extends DefaultTreeCellRenderer {
//        public Component getListCellRendererComponent(final JList tree, final Object value, final int index, final boolean isSelected, final boolean cellHasFocus) {
//            return super.getListCellRendererComponent(tree, "", index, isSelected, cellHasFocus);    //call to super
//        }

        public final Dimension getPreferredSize() {
            final Dimension retValue = super.getPreferredSize();
            if (retValue != null)
                retValue.width += 5;
            return retValue;
        }
    }


    public void fileWasOpened(final FileChangeEvent event) {
        updateSearchingEnabled();
    }

    public void fileWasClosed(final FileChangeEvent event) {
        IFileInstance fileInstance = event.getFileInstance();
        for (int i = rootNode.getChildCount() - 1; i >= 0; i--) {
            final DefaultMutableTreeNode parent = (DefaultMutableTreeNode) rootNode.getChildAt(i);
            if (((SearchResult) parent.getUserObject()).getFileInstance().equals(fileInstance)) {
                removeHighlights(parent, true);
                parent.removeAllChildren();
                rootNode.remove(i);
                treeModel.reload();
                final boolean hasChildren = rootNode.getChildCount() > 0;
                updateUnhighlightEnabled(hasChildren);
                if (!hasChildren)
                    treeModel.valueForPathChanged(new TreePath(rootNode), null);
                break;
            }
        }
        updateSearchingEnabled();
    }

    public void valueChanged(TreeSelectionEvent e) {
        final DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                tree.getLastSelectedPathComponent();
        if (node == null) return;
        if (node.isLeaf()) {
            final DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();
            if (parent == null)
                return;
            final IFileInstance fileInstance = ((SearchResult) parent.getUserObject()).getFileInstance();
            final JTextComponent textComponent = fileInstance.getRiderArea();
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    ((SearchResultItem) node.getUserObject()).selectResult(textComponent);
                    AreaManager.getInstance().activateInstance(fileInstance);
                }
            });
        }
    }

    private static final class SearchResult {
        private final IFileInstance fileInstance;
        private String searchString;
        private boolean isHighlighted;

        public SearchResult(final IFileInstance fileInstance, final boolean isHighlighted) {
            this.fileInstance = fileInstance;
            updateFoundCount(-1);
            this.isHighlighted = isHighlighted;
        }

        public final IFileInstance getFileInstance() {
            return fileInstance;
        }

        public final void updateFoundCount(final int count) {
            this.searchString = Lng.getLabel("findall.usagesNode", new Object[]{fileInstance.getName(), (count < 0) ? "" : "{" + count + "}"});
        }

        public final boolean isHighlighted() {
            return isHighlighted;
        }

        public final void setHighLighted(boolean value) {
            isHighlighted = value;
        }

        public final String toString() {
            return searchString;
        }

    }

    private static final class SearchResultItem {
        private String searchString;
        private final int foundStart;
        private final int foundEnd;
        private Object highlightTag = null;


        public SearchResultItem(final int foundStart, final int foundEnd, final String beforeFound, final String foundText, final String afterFound) {
            super();
            this.foundStart = foundStart;
            this.foundEnd = foundEnd;
            updateSearchString(beforeFound, foundText, afterFound);
        }

        private void updateSearchString(final String beforeFound, final String foundText, final String afterFound) {
            this.searchString = Lng.getLabel("findall.usageLeaf", new Object[]{beforeFound, foundText, afterFound});
        }

        public final void highLight(Highlighter highlighter) {
            try {
                highlightTag = highlighter.addHighlight(foundStart, foundEnd, underLinePainter);
            } catch (BadLocationException e) {
                LogUtils.processException(logger, e);
            }
        }

        public final void unhighLight(Highlighter highlighter) {
            if (highlightTag != null) {
                highlighter.removeHighlight(highlightTag);
                highlightTag = null;
            }
        }

        public final void selectResult(JTextComponent editor) {
            final Rectangle r;
            try {
                r = editor.modelToView(foundStart);
            } catch (BadLocationException ex) {
                LogUtils.processException(logger, ex);
                return;
            }
            if (r == null)
                return;
            r.y += -20;
            if (r.y < 0)
                r.y = 0;
            r.height = editor.getVisibleRect().height;
            //  editor.select(selElement.getStartOffset(), selElement.getEndOffset());
            try {
                editor.setCaretPosition(foundStart);
                editor.moveCaretPosition(foundEnd);
            } catch (Exception e) {
                //bad not already existing position
                return;
            }
            editor.scrollRectToVisible(r);
        }

        public final String toString() {
            return searchString;
        }
    }

    public boolean closeSoft() {
        AppPrefs.storeProperty(PREFS_HIGHLIGHT, checkHighlight.isSelected());
        AppPrefs.storeProperty(PREFS_IGNORE_CASE, checkIgnoreCase.isSelected());
        AppPrefs.storeProperty(PREFS_INALLFILES, checkInAllFiles.isSelected());
        AppPrefs.storeProperty(PREFS_REGEXP, checkRegexp.isSelected());
        AppPrefs.storeProperty(PREFS_WHOLEWORDS, checkWholeWords.isSelected());
        Swinger.storeProperties(PREFS_FINDALL, comboBox);
        return true;
    }

    public void closeHard() {
    }

    private final class ClickButtonListener implements ActionListener {
        public final void actionPerformed(ActionEvent e) {
            final Object source = e.getSource();
            if (source.equals(btnFindAll) && btnFindAll.isEnabled()) {
                doFindAll();
            } else {
                if (source.equals(btnUnhighlight)) {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            if (!btnUnhighlight.isSelected()) {
                                for (int i = rootNode.getChildCount() - 1; i >= 0; i--) {
                                    removeHighlights((DefaultMutableTreeNode) rootNode.getChildAt(i), false);
                                }
                            } else {
                                for (int i = rootNode.getChildCount() - 1; i >= 0; i--) {
                                    addHighlights((DefaultMutableTreeNode) rootNode.getChildAt(i));
                                }
                            }
                        }
                    });
                } else updateWholeWordsEnabled();
            }
        }

    }

    private void updateWholeWordsEnabled() {
        checkWholeWords.setEnabled(!checkRegexp.isSelected());
    }

    private static void addHighlights(DefaultMutableTreeNode parent) {
        final SearchResult result = (SearchResult) parent.getUserObject();
        if (!result.isHighlighted()) {
            final JTextComponent textComponent = result.getFileInstance().getRiderArea();
            final Highlighter highlighter = textComponent.getHighlighter();
            for (int i = parent.getChildCount() - 1; i >= 0; i--) {
                final SearchResultItem o = (SearchResultItem) ((DefaultMutableTreeNode) parent.getChildAt(i)).getUserObject();
                o.highLight(highlighter);
            }
            AreaManager.getInstance().repaintActive();
        }
        result.setHighLighted(true);
    }

}
