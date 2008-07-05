package cz.cvut.felk.erm.gui.dialogs;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.*;
import cz.cvut.felk.erm.core.tasks.CoreTask;
import cz.cvut.felk.erm.db.ScriptRunner;
import cz.cvut.felk.erm.gui.dialogs.filechooser.OpenSaveDialogFactory;
import cz.cvut.felk.erm.swing.ComponentFactory;
import cz.cvut.felk.erm.swing.Swinger;
import cz.cvut.felk.erm.utilities.LogUtils;
import cz.cvut.felk.erm.utilities.Utils;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.Task;
import org.jdesktop.swinghelper.buttonpanel.JXButtonPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Ladislav Vitasek
 */
public class ShowLogDialog extends AppDialog implements ClipboardOwner, TreeSelectionListener {
    private final static Logger logger = Logger.getLogger(ShowLogDialog.class.getName());
    private List<ScriptRunner.SQLScriptError> errorList;
    private int index = 0;
    private boolean prevErrorEnabled;
    private boolean nextErrorEnabled;

    private static final String PREV_ERROR_ENABLED_PROPERTY = "prevErrorEnabled";
    private static final String NEXT_ERROR_ENABLED_PROPERTY = "nextErrorEnabled";
    private int prevIndex;
    private int nextIndex;

    public ShowLogDialog(Frame owner) throws Exception {
        super(owner, true);

        this.setName("ShowLogDialog");
        try {
            initComponents();
            build();
        } catch (Exception e) {
            doClose(); //dialog se pri fatalni chybe zavre
            throw e;
        }
    }


    @org.jdesktop.application.Action
    public void okBtnAction() {
        setResult(RESULT_OK);
        doClose();
    }


    @org.jdesktop.application.Action
    public void btnCopyToClipboardAction() {
        StringSelection stringSelection = new StringSelection(textArea.getText());
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, this);
    }

    @org.jdesktop.application.Action(block = Task.BlockingScope.NONE)
    public Task btnSaveLogAction() {
        final File logDialog = OpenSaveDialogFactory.getSaveLogDialog();
        if (logDialog == null)
            return null;
        return new CoreTask(getApp()) {
            protected Object doInBackground() throws Exception {
                this.setUserCanCancel(false);
                message("message.savingFile");
                FileWriter writer = null;
                try {
                    writer = new FileWriter(logDialog);
                    writer.write(textArea.getText());
                } finally {
                    if (writer != null)
                        writer.close();
                }
                return null;
            }

            @Override
            protected void failed(Throwable cause) {
                LogUtils.processException(logger, cause);
                Swinger.showErrorMessage(getResourceMap(), "message.error.savingFileFailed", Utils.getExceptionMessage(cause));
            }
        };
    }

    @org.jdesktop.application.Action(enabledProperty = PREV_ERROR_ENABLED_PROPERTY)
    public void btnPrevErrorAction() {
        if (prevIndex < 0)
            return;
        setSelectedIndex(prevIndex);
    }

    private void setSelectedIndex(final int index) {
        final TreeModel treeModel = errorTree.getModel();
        final Object child = treeModel.getChild(treeModel.getRoot(), index);
        final TreePath path = new TreePath(child);
        final TreePath old = errorTree.getSelectionPath();
        errorTree.setSelectionPath(path);
        valueChanged(new TreeSelectionEvent(errorTree, path, true, old, path));
    }

    @org.jdesktop.application.Action(enabledProperty = NEXT_ERROR_ENABLED_PROPERTY)
    public void btnNextErrorAction() {
        if (nextIndex == -1 || nextIndex >= errorList.size())
            return;
        setSelectedIndex(nextIndex);
    }

    public boolean isPrevErrorEnabled() {
        return prevErrorEnabled;
    }

    public void setPrevErrorEnabled(boolean prevErrorEnabled) {
        final boolean oldValue = this.prevErrorEnabled;
        this.prevErrorEnabled = prevErrorEnabled;
        firePropertyChange(ShowLogDialog.PREV_ERROR_ENABLED_PROPERTY, oldValue, prevErrorEnabled);
    }

    public boolean isNextErrorEnabled() {
        return nextErrorEnabled;
    }

    public void setNextErrorEnabled(boolean nextErrorEnabled) {
        final boolean oldValue = this.nextErrorEnabled;
        this.nextErrorEnabled = nextErrorEnabled;
        firePropertyChange(ShowLogDialog.NEXT_ERROR_ENABLED_PROPERTY, oldValue, nextErrorEnabled);
    }

    @Override
    public void doClose() {
        //errorTree.setModel(null);
        super.doClose();
    }

    @Override
    protected AbstractButton getBtnCancel() {
        return btnOK;
    }

    @Override
    protected AbstractButton getBtnOK() {
        return btnOK;
    }

    public void setLog(String value) {
        textArea.setText(value);
        textArea.setCaretPosition(0);
    }

    private void build() throws CloneNotSupportedException {
        inject();
        buildGUI();

        setAction(btnOK, "okBtnAction");
        setAction(btnCopyToClipboard, "btnCopyToClipboardAction");
        setAction(btnSaveLog, "btnSaveLogAction");

        final Action btnPrevErrorAction = setAction(btnPrevError, "btnPrevErrorAction");
        final Action btnNextErrorAction = setAction(btnNextError, "btnNextErrorAction");

        registerKeyboardAction(btnPrevErrorAction);
        registerKeyboardAction(btnPrevErrorAction, KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, KeyEvent.ALT_DOWN_MASK));
        registerKeyboardAction(btnNextErrorAction);
        registerKeyboardAction(btnNextErrorAction, KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, KeyEvent.ALT_DOWN_MASK));

        buildModels();

        setDefaultValues();

        pack();
        setResizable(true);
        locateOnOpticalScreenCenter(this);
    }

    private void setDefaultValues() {

    }

    private void buildGUI() {
        nextIndex = prevIndex = -1;
        ToolTipManager.sharedInstance().registerComponent(errorTree);
        textArea.setFont(textArea.getFont().deriveFont(Font.PLAIN, 8));
        errorTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        final ResourceMap map = getResourceMap();
        errorTree.setCellRenderer(new MyCellRenderer(map.getImageIcon("sqlErrorIcon"), map.getImageIcon("sqlErrorRootIcon")));
        errorTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() >= 2)
                    Swinger.inputFocus(textArea);
            }
        });
        errorTree.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER)
                    Swinger.inputFocus(textArea);
            }
        });

        errorTree.addTreeSelectionListener(this);
        errorTree.setExpandsSelectedPaths(true);
        textArea.addCaretListener(new CaretListener() {
            public void caretUpdate(CaretEvent e) {
                caretPositionChanged(e.getDot());
            }
        });
        btnPrevError.setFocusable(false);
        btnNextError.setFocusable(false);

        textArea.getCaret().setVisible(true);
        Swinger.inputFocus(textArea);
        Swinger.inputFocus(errorTree);
    }

    private void caretPositionChanged(final int position) {
        if (errorList == null)
            return;
        nextIndex = prevIndex = -1;
        int index = -1;
        for (ScriptRunner.SQLScriptError error : errorList) {
            final int start = error.getStartPosition();
            final int end = error.getEndPosition();
            ++index;
            if (position >= start && position <= end) {
                prevIndex = index - 1;
                nextIndex = index + 1; //Math.min(size, index + 1);
                break;
            } else if (position < start) {
                prevIndex = index - 1;
                nextIndex = index;
                break;
            } else {//konec - kurzor za poslednim
                prevIndex = index;
                nextIndex = -1;
            }
        }
        this.setNextErrorEnabled(nextIndex != -1 && nextIndex < errorList.size());
        this.setPrevErrorEnabled(prevIndex >= 0);
    }


    private void buildModels() throws CloneNotSupportedException {
        bindBasicComponents();
    }

    private void bindBasicComponents() {

    }

    public void setErrorList(List<ScriptRunner.SQLScriptError> errorList) {
        this.errorList = errorList;
        final DefaultTreeModel treeModel = (DefaultTreeModel) errorTree.getModel();
        final DefaultMutableTreeNode root = new DefaultMutableTreeNode(errorList.size() + " errors in total");
        int counter = 0;
        for (ScriptRunner.SQLScriptError error : errorList) {
            final MutableTreeNode node = new DefaultMutableTreeNode(new TreeItem("#" + (++counter), error));
            root.add(node);
        }

        treeModel.setRoot(root);
        errorTree.setSelectionPath(new TreePath(root.getFirstChild()));
    }

    public void valueChanged(TreeSelectionEvent e) {
        final DefaultMutableTreeNode node = (DefaultMutableTreeNode) errorTree.getLastSelectedPathComponent();

        if (node == null || !node.isLeaf())
            //Nothing is selected.
            return;
        index = node.getParent().getIndex(node);
        final TreeItem treeItem = (TreeItem) node.getUserObject();
        treeItem.selectResult();
    }

    private final class TreeItem {
        ScriptRunner.SQLScriptError error;
        private String label;
        private String tooltip;

        private TreeItem(String label, ScriptRunner.SQLScriptError error) {
            this.label = label;
            this.error = error;
            tooltip = getResourceMap().getString("tooltip", label, updateTooltip(error.getErrorText()));
        }

        private String updateTooltip(String s) {
            final char[] chars = s.toCharArray();
            final StringBuilder builder = new StringBuilder();
            boolean newLine = false;
            for (char c : chars) {
                switch (c) {
                    case '\n':
                        newLine = true;
                        builder.append("<br>");
                        break;
                    case ' ':
                        if (newLine)
                            builder.append("&nbsp;");
                        else
                            builder.append(' ');
                        break;
                    default:
                        newLine = false;
                        builder.append(c);
                        break;
                }
            }
            return builder.toString();
        }

        public final void selectResult() {
            ShowLogDialog.this.selectResult(error.getStartPosition(), error.getEndPosition() - 1);
        }

        @Override
        public String toString() {
            return label;
        }
    }

    private void selectResult(int startPosition, int endPosition) {
        final Rectangle r, r2;
        try {
            r = textArea.modelToView(startPosition);
            r2 = textArea.modelToView(endPosition);
        } catch (BadLocationException ex) {
            LogUtils.processException(logger, ex);
            return;
        }
        if (r == null || r2 == null)
            return;
        r.width = Math.max(1, r.width);//no negative width
        r2.width = Math.max(1, r2.width);//no negative width
        final Rectangle visibleRect = textArea.getVisibleRect();

        final boolean notVisible = !(visibleRect.contains(r) && visibleRect.contains(r2));
        if (notVisible) {
            r.y += -3; //budeme scrollovat
            if (r.y < 0)
                r.y = 0;
            r.height = visibleRect.height;
        }
        //  editor.select(selElement.getStartOffset(), selElement.getEndOffset());
        try {
            textArea.setCaretPosition(startPosition);
            textArea.moveCaretPosition(endPosition);
            //textArea.select(error.getStartPosition(), error.getEndPosition());
        } catch (Exception e) {
            //bad not already existing position
            return;
        }
        if (notVisible)
            textArea.scrollRectToVisible(r);

    }


    private static final class MyCellRenderer extends DefaultTreeCellRenderer {

        private final Icon listIcon;
        private Icon rootIcon;

        private MyCellRenderer(Icon listIcon, Icon rootIcon) {
            this.listIcon = listIcon;
            this.rootIcon = rootIcon;
            assert listIcon != null;
        }

        @Override
        public final Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            //sel = true;
            final Component rendererComponent = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
            if (leaf) {
                final DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
                final TreeItem treeItem = (TreeItem) node.getUserObject();
                this.setIcon(listIcon);
                this.setToolTipText(treeItem.tooltip);
            } else {
                this.setIcon(rootIcon);
            }
            return rendererComponent;
        }

        public final Dimension getPreferredSize() {
            final Dimension retValue = super.getPreferredSize();
            if (retValue != null) {
                retValue.width += 5;
                retValue.height += 2;
            }
            return retValue;
        }
    }


    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Open Source Project license - unknown
        ////ResourceBundle bundle = ResourceBundle.getBundle("ShowLogDialog");
        JPanel dialogPane = new JPanel();
        dialogPane.setName("dialogPane");
        JPanel contentPanel = new JPanel();
        contentPanel.setName("contentPanel");
        JSplitPane splitPane = new JSplitPane();
        splitPane.setName("splitPane");
        splitPane.setPreferredSize(new Dimension(650, 400));
        JScrollPane scrollPane2 = new JScrollPane();
        scrollPane2.setName("scrollPane2");
        errorTree = new JTree();
        errorTree.setName("errorTree");
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setName("scrollPane");
        textArea = ComponentFactory.getSQLArea();
        textArea.setName("errorTree");
        JXButtonPanel buttonBar = new JXButtonPanel();
        btnCopyToClipboard = new JButton();
        btnSaveLog = new JButton();
        btnOK = new JButton();
        JPanel toolbarPanel = new JPanel();
        btnPrevError = new JButton();
        btnNextError = new JButton();
        CellConstraints cc = new CellConstraints();

        //======== this ========
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== dialogPane ========
        {
            dialogPane.setBorder(Borders.DIALOG_BORDER);
            dialogPane.setLayout(new BorderLayout());

            //======== contentPanel ========
            {
                contentPanel.setLayout(new BorderLayout());

                //======== splitPane ========
                {
                    splitPane.setOneTouchExpandable(true);
                    splitPane.setResizeWeight(0.01);

                    //======== scrollPane2 ========
                    {

                        //---- errorTree ----
                        //errorTree.setPreferredSize(new Dimension(140, 64));
                        scrollPane2.setViewportView(errorTree);
                        //scrollPane2.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
                    }
                    splitPane.setLeftComponent(scrollPane2);

                    //======== scrollPane ========
                    {
                        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

                        //---- textArea ----
                        textArea.setEditable(false);
                        scrollPane.setViewportView(textArea);
                    }
                    splitPane.setRightComponent(scrollPane);
                }
                contentPanel.add(splitPane, BorderLayout.CENTER);
            }
            dialogPane.add(contentPanel, BorderLayout.CENTER);

            //======== buttonBar ========
            {
                buttonBar.setBorder(Borders.BUTTON_BAR_GAP_BORDER);

                //---- btnCopyToClipboard ----
                btnCopyToClipboard.setName("btnCopyToClipboard");

                //---- btnSaveLog ----
                btnSaveLog.setName("btnSaveLog");

                //---- btnOK ----
                btnOK.setName("btnOK");

                PanelBuilder buttonBarBuilder = new PanelBuilder(new FormLayout(
                        new ColumnSpec[]{
                                ComponentFactory.BUTTON_COLSPEC,
                                FormFactory.RELATED_GAP_COLSPEC,
                                ComponentFactory.BUTTON_COLSPEC,
                                FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                new ColumnSpec(ColumnSpec.FILL, Sizes.PREFERRED, FormSpec.DEFAULT_GROW),
                                ComponentFactory.BUTTON_COLSPEC
                        },
                        RowSpec.decodeSpecs("pref")), buttonBar);

                buttonBarBuilder.add(btnCopyToClipboard, cc.xy(1, 1));
                buttonBarBuilder.add(btnSaveLog, cc.xy(3, 1));
                buttonBarBuilder.add(btnOK, cc.xy(6, 1));
            }
            dialogPane.add(buttonBar, BorderLayout.SOUTH);

            //======== toolbarPanel ========
            {
                toolbarPanel.setBorder(new EmptyBorder(0, 5, 3, 5));

                //---- btnPrevError ----
                btnPrevError.setPreferredSize(new Dimension(26, 23));

                //---- btnNextError ----
                btnNextError.setPreferredSize(new Dimension(26, 23));

                PanelBuilder toolbarPanelBuilder = new PanelBuilder(new FormLayout(
                        new ColumnSpec[]{
                                FormFactory.DEFAULT_COLSPEC,
                                FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                FormFactory.DEFAULT_COLSPEC
                        },
                        RowSpec.decodeSpecs("default")), toolbarPanel);

                toolbarPanelBuilder.add(btnPrevError, cc.xy(1, 1));
                toolbarPanelBuilder.add(btnNextError, cc.xy(3, 1));
            }
            dialogPane.add(toolbarPanel, BorderLayout.NORTH);
        }
        contentPane.add(dialogPane, BorderLayout.CENTER);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner Open Source Project license - unknown
    private JTree errorTree;
    private JEditorPane textArea;
    private JButton btnCopyToClipboard;
    private JButton btnSaveLog;
    private JButton btnOK;
    private JButton btnPrevError;
    private JButton btnNextError;

    public void lostOwnership(Clipboard clipboard, Transferable contents) {

    }


}