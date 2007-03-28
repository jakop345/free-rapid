package net.wordrider.core.managers;

import net.wordrider.area.RiderArea;
import net.wordrider.area.RiderStyles;
import net.wordrider.area.actions.*;
import net.wordrider.core.AppPrefs;
import net.wordrider.core.actions.*;
import net.wordrider.core.managers.interfaces.*;
import net.wordrider.dialogs.LimitedPlainDocument;
import net.wordrider.gui.ToolbarSeparator;
import net.wordrider.utilities.Swinger;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Vity
 */
public final class ToolbarManager implements IRiderManager, CaretListener, IFileChangeListener, IAreaChangeListener, DocumentListener, PropertyChangeListener, IHidAble, InstanceListener {
    private final JPanel panel;
    private JToolBar toolbar;
    private JToggleButton leftItem, centerItem, rightItem, margin10Item, margin20Item, margin30Item, marginXItem, mathItem;
    private JToggleButton bookmarkItem;
    //  private JButton cutItem, copyItem, pasteItem, searchItem, separatorSingleItem, separatorDoubleItem, pictureItem;
    private JToggleButton size1Item, size2Item, size3Item, superScriptItem, underlineSingleItem, underlineDottedItem, strikeoutItem, invertItem, vectorItem, wordWrapItem, subScriptItem, conjugateItem;
    //private JButton newItem, openItem, saveItem, saveasItem, undoItem, redoItem;

    private final static Dimension buttonDimension = new Dimension(24, 23);
    private static final int STRUT_SIZE = 8;
    private IFileInstance instance;
    private boolean activeMath = false;
    private Element currentRunEl;
    private JComboBox comboMarginX;
    private final static Logger logger = Logger.getLogger(ToolbarManager.class.getName());

    public ToolbarManager() {
        panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 0));
        UIManager.addPropertyChangeListener(this);

        createToolbar();

        panel.setVisible(AppPrefs.getProperty("net.wordrider.gui.showToolbar", true));
    }

    private void initToolbar() {
        comboMarginX.setEnabled(instance != null);

    }


    public void updateHibviewButtons() {
        boolean visibleHibviewButtons = AppPrefs.getProperty(AppPrefs.HIBVIEW_BUTTONS, true);
        conjugateItem.setVisible(visibleHibviewButtons);
        subScriptItem.setVisible(visibleHibviewButtons);
        comboMarginX.setVisible(visibleHibviewButtons);
        //MarginXAction.getInstance().setEnabled();
    }

    private void createToolbar() {
        panel.add(toolbar = new JToolBar("Main Toolbar"));

        panel.setPreferredSize(new Dimension(400, 27));
        SwingUtilities.updateComponentTreeUI(toolbar);
        //toolbar.setLayout(new FlowLayout(FlowLayout.LEFT, 2, 0));
        //toolbar.setLayout(null);
        //        toolbar.setLayout(new BoxLayout(toolbar, BoxLayout.X_AXIS));
        //        toolbar.setLayout(new GridBagLayout());
        toolbar.setFocusable(false);
        toolbar.setFloatable(false);

        //        final UpdateActionListener updateListener = new UpdateActionListener();

        toolbar.add(getButton(CreateNewFileAction.getInstance()));
        toolbar.add(getButton(OpenFileAction.getInstance()));
        toolbar.add(Box.createHorizontalStrut(2));
        toolbar.add(getButton(SaveFileAction.getInstance()));
        toolbar.add(getButton(SaveAsFileAction.getInstance()));
        toolbar.add(new ToolbarSeparator());
        toolbar.add(getButton(UndoAction.getInstance()));
        toolbar.add(getButton(RedoAction.getInstance()));
        toolbar.add(new ToolbarSeparator());
//        toolbar.add(getButton(ShowFindReplaceDialogAction.getInstance()));
//        toolbar.add(new ToolbarSeparator());
        toolbar.add(getButton(CutAction.getInstance()));
        toolbar.add(getButton(CopyAction.getInstance()));
        toolbar.add(getButton(PasteAction.getInstance()));
        toolbar.add(new ToolbarSeparator());
        ButtonGroup group = new ButtonGroup();
        toolbar.add(size1Item = getToggleButton(ChangeFontSizeStyle1Action.getInstance()));
        group.add(size1Item);
        toolbar.add(size2Item = getToggleButton(ChangeFontSizeStyle2Action.getInstance()));
        group.add(size2Item);
        toolbar.add(size3Item = getToggleButton(ChangeFontSizeStyle3Action.getInstance()));

        group.add(size3Item);
        toolbar.add(Box.createHorizontalStrut(STRUT_SIZE));
        toolbar.add(superScriptItem = getToggleButton(ChangeExponentStyleAction.getInstance()));
        toolbar.add(subScriptItem = getToggleButton(ChangeSubscriptStyleAction.getInstance()));
        toolbar.add(new ToolbarSeparator());

        toolbar.add(underlineSingleItem = getToggleButton(ChangeUnderlineStyleAction.getInstance()));
        //underlineSingleItem.addActionListener(updateListener);//!!!!!!!!!!!!!!!
        toolbar.add(underlineDottedItem = getToggleButton(ChangeDottedStyleAction.getInstance()));
        toolbar.add(Box.createHorizontalStrut(STRUT_SIZE));

        toolbar.add(strikeoutItem = getToggleButton(ChangeStrikedStyleAction.getInstance()));

        toolbar.add(new ToolbarSeparator());

        toolbar.add(invertItem = getToggleButton(ChangeInvertStyleAction.getInstance()));
        toolbar.add(Box.createHorizontalStrut(STRUT_SIZE));
        toolbar.add(vectorItem = getToggleButton(ChangeVectorStyleAction.getInstance()));
        toolbar.add(conjugateItem = getToggleButton(ChangeConjugateStyleAction.getInstance()));
        toolbar.add(Box.createHorizontalStrut(STRUT_SIZE));
        toolbar.add(wordWrapItem = getToggleButton(ChangeWordWrapStyleAction.getInstance()));

        toolbar.add(new ToolbarSeparator());

        group = new ButtonGroup();

        group.add(leftItem = getToggleButton(AligmentLeftAction.getInstance()));
        toolbar.add(leftItem);

        group.add(centerItem = getToggleButton(AligmentCenterAction.getInstance()));
        toolbar.add(centerItem);

        toolbar.add(rightItem = getToggleButton(AligmentRightAction.getInstance()));
        group.add(rightItem);
        toolbar.add(Box.createHorizontalStrut(STRUT_SIZE));

        toolbar.add(margin10Item = getToggleButton(Margin10Action.getInstance()));
        group.add(margin10Item);

        toolbar.add(margin20Item = getToggleButton(Margin20Action.getInstance()));
        group.add(margin20Item);

        toolbar.add(margin30Item = getToggleButton(Margin30Action.getInstance()));
        group.add(margin30Item);

        toolbar.add(getMarginXCombo());
        toolbar.add(Box.createHorizontalStrut(STRUT_SIZE));
        toolbar.add(comboMarginX);
        toolbar.add(marginXItem = getToggleButton(MarginXAction.getInstance()));
        group.add(marginXItem);

        toolbar.add(Box.createHorizontalStrut(STRUT_SIZE));
        toolbar.add(mathItem = getToggleButton(ChangeMathStyleAction.getInstance()));
        toolbar.add(new ToolbarSeparator());

        toolbar.add(getButton(InsertPictureAction.getInstance()));
        toolbar.add(Box.createHorizontalStrut(STRUT_SIZE));
        toolbar.add(getButton(InsertSeparatorSingleAction.getInstance()));
        toolbar.add(getButton(InsertSeparatorDoubleAction.getInstance()));
        toolbar.add(Box.createHorizontalStrut(STRUT_SIZE));
        toolbar.add(bookmarkItem = getToggleButton(UpdateBreakpointAction.getInstance()));
        //setOpaqueComponents(LookAndFeels.getInstance().getSelectedLaF().getOpaque());
        //setOpaqueComponents(false);
        panel.validate();


        initToolbar();
    }

//    private void setOpaqueComponents(final boolean opaque) {
//        final Component[] components = toolbar.getComponents();
//        final int componentCount = components.length;
//        for (int i = 0; i < componentCount; i++) {
//            if (components[i] instanceof JComponent)
//                ((JComponent) components[i]).setOpaque(opaque);
//        }
//    }


    private static JToggleButton getToggleButton(final Action action) {
        final JToggleButton button = new JToggleButton(action);

        //button.setToolTipText(tooltip);
        button.setAction(action);
        button.setText(null);
        button.setMinimumSize(buttonDimension);
        button.setPreferredSize(buttonDimension);
        button.setMaximumSize(buttonDimension);
        button.setMnemonic(0);
        button.setFocusable(false);
        return button;
    }

    //    private class UpdateActionListener implements ActionListener {
    //        public void actionPerformed(ActionEvent e) {
    //            updateStatus(0, 0);
    //        }
    //    }


    public final void insertUpdate(final DocumentEvent e) {
        updateStatus(0, 0);
    }

    private void unregisterHooks() {
        if (this.instance == null)
            return;
        final JTextComponent editor = instance.getRiderArea();
        editor.getDocument().removeDocumentListener(this);
        editor.removePropertyChangeListener("document", this);
        editor.removeCaretListener(this);
        comboMarginX.setEnabled(false);
        this.instance.removeInstanceListener(this);
        this.instance = null;        
    }


    public void areaActivated(AreaChangeEvent event) {
        final IFileInstance instance = event.getFileInstance();
        areaActivated(instance);
    }

    private void areaActivated(IFileInstance instance) {
        if (isVisible()) {
            if (!instance.equals(this.instance)) {
                unregisterHooks(); //for sure
                registerHooks(instance);
            }
            updateModifiedStatus();
            updateButtonsStatuses();
            comboMarginX.setEnabled(true);
        }
    }

    public void areaDeactivated(AreaChangeEvent event) {
        areaDeactivated();
    }

    private void areaDeactivated() {
        unregisterHooks();
    }

    public final void updateToolbar() {
        if (logger.isLoggable(Level.INFO))
            logger.info("update toolbar buttons statuses");
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                updateButtonsStatuses();
            }
        });
    }

    private void updateButtonsStatuses() {
        if (instance == null)
            return;
        final RiderArea editor = (RiderArea) instance.getRiderArea();
        final MutableAttributeSet set = editor.getInputAttributes();
        final Element paraEl = editor.getDoc().getParagraphElement(editor.getCaretPosition());
        final boolean isNOTMath = !RiderStyles.isMath(paraEl);

        if (activeMath != isNOTMath) {
            ChangeFontSizeStyle1Action.getInstance().setEnabled(isNOTMath);
            ChangeFontSizeStyle2Action.getInstance().setEnabled(isNOTMath);
            ChangeFontSizeStyle3Action.getInstance().setEnabled(isNOTMath);
            AligmentLeftAction.getInstance().setEnabled(isNOTMath);
            AligmentCenterAction.getInstance().setEnabled(isNOTMath);
            AligmentRightAction.getInstance().setEnabled(isNOTMath);
            Margin10Action.getInstance().setEnabled(isNOTMath);
            Margin20Action.getInstance().setEnabled(isNOTMath);
            Margin30Action.getInstance().setEnabled(isNOTMath);
            ChangeInvertStyleAction.getInstance().setEnabled(isNOTMath);
            ChangeVectorStyleAction.getInstance().setEnabled(isNOTMath);
            ChangeConjugateStyleAction.getInstance().setEnabled(isNOTMath);
            ChangeWordWrapStyleAction.getInstance().setEnabled(isNOTMath);
            ChangeExponentStyleAction.getInstance().setEnabled(isNOTMath);
            ChangeSubscriptStyleAction.getInstance().setEnabled(isNOTMath);
            ChangeUnderlineStyleAction.getInstance().setEnabled(isNOTMath);
            ChangeDottedStyleAction.getInstance().setEnabled(isNOTMath);
            ChangeStrikedStyleAction.getInstance().setEnabled(isNOTMath);
            UpdateBreakpointAction.getInstance().setEnabled(isNOTMath);
            MarginXAction.getInstance().setEnabled(isNOTMath);
            comboMarginX.setEnabled(isNOTMath);
            activeMath = isNOTMath;
            mathItem.setSelected(!isNOTMath);
        }

        size1Item.setSelected(RiderStyles.isSize(set, RiderStyles.SIZE_MINI));
        size2Item.setSelected(RiderStyles.isSize(set, RiderStyles.SIZE_NORMAL));
        size3Item.setSelected(RiderStyles.isSize(set, RiderStyles.SIZE_MAXI));
        superScriptItem.setSelected(RiderStyles.isExposant(set));
        subScriptItem.setSelected(RiderStyles.isSubscript(set));

        if (RiderStyles.isUnderLineDotted(set)) {
            underlineSingleItem.setSelected(false);
            underlineDottedItem.setSelected(true);
        } else if (RiderStyles.isUnderLine(set)) {
            underlineSingleItem.setSelected(true);
            underlineDottedItem.setSelected(false);
        } else {
            underlineSingleItem.setSelected(false);
            underlineDottedItem.setSelected(false);
        }
        final int margin = RiderStyles.getMargin(set);
        switch (margin) {
            case RiderStyles.MARGIN_10:
                margin10Item.setSelected(true);
                break;
            case RiderStyles.MARGIN_20:
                margin20Item.setSelected(true);
                break;
            case RiderStyles.MARGIN_30:
                margin30Item.setSelected(true);
                break;
            case RiderStyles.MARGIN_0:
                switch (RiderStyles.getAlignment(set)) {
                    case RiderStyles.ALIGN_LEFT:
                        leftItem.setSelected(true);
                        break;
                    case RiderStyles.ALIGN_CENTER:
                        centerItem.setSelected(true);
                        break;
                    case RiderStyles.ALIGN_RIGHT:
                        rightItem.setSelected(true);
                        break;
                    default:
                        leftItem.setSelected(true);
                        break;
                }
                break;
            default:
                //comboMarginX.setSelectedItem(Integer.valueOf(margin));
                comboMarginX.setSelectedItem(margin);
                marginXItem.setSelected(true);
                break;
        }
        invertItem.setSelected(RiderStyles.isInvert(set));
        vectorItem.setSelected(RiderStyles.isVector(set));
        conjugateItem.setSelected(RiderStyles.isConjugate(set));
        wordWrapItem.setSelected(RiderStyles.isWordWrap(set));
        strikeoutItem.setSelected(RiderStyles.isStrikeOut(set));
        bookmarkItem.setSelected(RiderStyles.isBookmark(paraEl));

        updateCopyPasteButtons();
    }

    public final Component getManagerComponent() {
        return panel;  //implement - call to super class
    }

    private void updateModifiedStatus() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                SaveFileAction.getInstance().setEnabled((instance != null && instance.isModified()));
            }
        });
    }


    public void fileWasOpened(final FileChangeEvent event) {
        if (comboMarginX != null) {
            MarginXAction.updateComboData(comboMarginX);
        }
    }

    public void fileWasClosed(final FileChangeEvent event) {

    }

    private void updateStatus(final int dot, final int mark) {
        final RiderArea editor = (RiderArea) instance.getRiderArea();
        if (editor.isBusy())
            return;
        final int start = Math.min(dot, mark);
        // record current character attributes.
        final StyledDocument doc = editor.getDoc();
        // If nothing is selected, get the attributes from the character
        // before the start of the selection, otherwise get the attributes
        // from the character element at the start of the selection.

        Element currentParagraph = doc.getParagraphElement(start);
        final Element run;
        if (currentParagraph.getStartOffset() == start || dot != mark) {
            // Get the attributes from the character at the selection
            // if in a different paragrah!
            run = doc.getCharacterElement(start);
        } else {
            run = doc.getCharacterElement(Math.max(start - 1, 0));
        }
        if (!run.equals(currentRunEl)) {
            updateToolbar();
            currentRunEl = run;
        } else updateCopyPasteButtons();
    }

    private void updateCopyPasteButtons() {
        final JTextComponent editor = instance.getRiderArea();
        final boolean isSelectedText = editor.getSelectionStart() != editor.getSelectionEnd();
        CopyAction.getInstance().setEnabled(isSelectedText && editor.isEditable());
        CutAction.getInstance().setEnabled(isSelectedText && editor.isEditable());
        PasteAction.getInstance().setEnabled(editor.isEditable());
        ChangeImagePropertiesAction.getInstance().updateEnabled((RiderArea) editor);
    }

    public final void caretUpdate(final CaretEvent e) {
        updateStatus(e.getDot(), e.getMark());
    }

    public final void changedUpdate(final DocumentEvent e) {
        updateStatus(e.getOffset(), 0);
    }


    public final void removeUpdate(final DocumentEvent e) {
        changedUpdate(e);
    }

    public JComboBox getInputMarginXCombo() {
        return comboMarginX;
    }


    private JComboBox getMarginXCombo() {
        comboMarginX = new JComboBox();
        //comboMarginX.setFocusable(false);
        final Component editorComponent = comboMarginX.getEditor().getEditorComponent();
        if (editorComponent instanceof JTextComponent) {
            final JTextComponent editor = (JTextComponent) editorComponent;
            editor.addKeyListener(new ComboKeyListener());
            editor.setDocument(new LimitedPlainDocument("\\p{Digit}{0,3}"));
            editor.addFocusListener(new Swinger.SelectAllOnFocusListener());
        }

        //comboMarginX.addK
        comboMarginX.setEditable(true);
        //comboMarginX.addItemListener(new ComboItemListener());
        final Dimension size = new Dimension(50, 22);
        comboMarginX.setMinimumSize(size);
        comboMarginX.setPreferredSize(size);
        comboMarginX.setMaximumSize(size);
        return comboMarginX;
    }

    public void instanceModifiedStatusChanged(InstanceEvent e) {
        updateModifiedStatus();
    }

    public void fileAssigned(InstanceEvent e) {

    }

    private static final class ComboKeyListener extends KeyAdapter {
        public final void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                MarginXAction.getInstance().actionPerformed(null);
            }
        }
    }


    public final void propertyChange(final PropertyChangeEvent event) {
        if (instance != null && event.getSource().equals(instance.getRiderArea()) &&
                event.getPropertyName().equals("document")) {
            // Reset the DocumentListener
            ((Document) event.getOldValue()).removeDocumentListener(this);
            final Document newDoc = (Document) event.getNewValue();
            newDoc.addDocumentListener(this);
        } else if (event.getPropertyName().equals("lookAndFeel")) {
            panel.remove(toolbar);
            createToolbar();
            updateButtonsStatuses();
            //
            //            toolbar.invalidate();
            //            toolbar.validate();
            //            toolbar.repaint();
        }
    }

    private void registerHooks(final IFileInstance instance) {
        this.instance = instance;
        final JTextComponent editor = instance.getRiderArea();
        editor.addCaretListener(this);
        editor.getDocument().addDocumentListener(this);
        editor.addPropertyChangeListener("document", this);
        instance.addInstanceListener(this);
        //editor.addPropertyChangeListener(AreaManager.MODIFIED_PROPERTY, this);
    }

    private static JButton getButton(final Action action) {
        final JButton button = new JButton(action);
        button.setText("");
        button.setMnemonic(0);
        button.setMinimumSize(buttonDimension);
        button.setPreferredSize(buttonDimension);
        button.setMaximumSize(buttonDimension);
        button.setFocusable(false);
        return button;
    }

    public final boolean isVisible() {
        return panel.isVisible();
    }

    public final void setVisible(final boolean value) {
        if (value) {
            panel.setVisible(true);
            areaActivated(AreaManager.getInstance().getActiveInstance());
        } else {
            panel.setVisible(false);
            areaDeactivated();
        }
        AppPrefs.storeProperty("net.wordrider.gui.showToolbar", value);
    }


}
