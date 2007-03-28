package net.wordrider.core.managers;

import net.wordrider.area.RiderArea;
import net.wordrider.area.actions.*;
import net.wordrider.core.Lng;
import net.wordrider.core.actions.*;
import net.wordrider.core.managers.interfaces.IAreaChangeListener;
import net.wordrider.core.managers.interfaces.IFileInstance;
import net.wordrider.dialogs.JButtonGroup;
import net.wordrider.utilities.Swinger;

import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import java.awt.*;
import java.io.File;
import java.util.Collection;

/**
 * @author Vity
 */
public final class MenuManager implements IAreaChangeListener {
    private final JMenuBar menuBar = new JMenuBar();
    private JMenu fileMenu,
            editMenu,
            insertMenu,
            formatMenu,
            paragraphMenu,
            viewMenu,
            wordRiderMenu,
            widthViewMenu,
            recentMenu,
            functionsMenu;
    private JRadioButtonMenuItem fullViewItem,
            ti89ViewItem,
            ti92ViewItem;
    //   private MenuListener mainMenuListener = new SelectedMenuListener();
    private static final String LANG_NONAME_ICON = "blank.gif";
    private JMenuItem insertFromClipboardMenu;

    public MenuManager(final ManagerDirector director) {
        super();    //call to super
        //this.director = director;
        director.getMainFrame().setJMenuBar(menuBar);
        selectedAreaChanged(false, null);
        menuBar.add(getFileMenu());
        menuBar.add(getEditMenu());
        menuBar.add(getFormatMenu());
        menuBar.add(getParagraphMenu());
        menuBar.add(getInsertMenu());
        menuBar.add(getViewMenu());
        menuBar.add(getWordRiderMenu());

    }

    private static JMenu initMenu(final String code) {
        final JMenu menu = new JMenu(Lng.getLabel("menu." + code));
        menu.setMnemonic(Lng.getMnemonic("menu." + code));
        //menu.addMenuListener(this.mainMenuListener);
        return menu;
    }

    private Component getViewMenu() {
        if (viewMenu == null) {
            viewMenu = initMenu("view");
            JCheckBoxMenuItem viewToolbarItem;
            Action action = ToggleToolbarAction.getInstance(viewToolbarItem = new JCheckBoxMenuItem());
            viewToolbarItem.setAction(action);
            viewMenu.add(viewToolbarItem);
            JCheckBoxMenuItem viewStatusBarItem;
            action = ToggleStatusBarAction.getInstance(viewStatusBarItem = new JCheckBoxMenuItem());
            viewStatusBarItem.setAction(action);
            viewMenu.add(viewStatusBarItem);
            viewMenu.addSeparator();
            //            action = ToggleAntialisingAreaAction.getInstance(viewAntialiased = new JCheckBoxMenuItem());
            //            viewMenu.add(viewAntialiased);
            //viewAntialiased.setAction(action);
            //            viewMenu.addSeparator();
            viewMenu.add(getWidthViewMenu());
        }
        return viewMenu;
    }

    private JMenu getWidthViewMenu() {
        if (widthViewMenu != null)
            return widthViewMenu;
        widthViewMenu = initMenu("view.emulation");
        final JButtonGroup buttonGroup = new JButtonGroup();
        buttonGroup.add(fullViewItem = new JRadioButtonMenuItem(ToggleFullViewAreaAction.getInstance()));
        buttonGroup.add(ti89ViewItem = new JRadioButtonMenuItem(Toggle89ViewAreaAction.getInstance()));
        buttonGroup.add(ti92ViewItem = new JRadioButtonMenuItem(Toggle92ViewAreaAction.getInstance()));
        fullViewItem.setSelected(true);
        widthViewMenu.add(fullViewItem);
        widthViewMenu.add(ti89ViewItem);
        widthViewMenu.add(ti92ViewItem);
        return widthViewMenu;
    }

    private JMenu getFunctionsFormatMenu() {
        if (functionsMenu != null)
            return functionsMenu;
        functionsMenu = initMenu("format.functions");
        functionsMenu.add(new JMenuItem(TrimTrailingSpacesAction.getInstance()));
        functionsMenu.addSeparator();
        functionsMenu.add(new JMenuItem(TextToUpperCaseAction.getInstance()));
        functionsMenu.add(new JMenuItem(TextToLowerCaseAction.getInstance()));
        functionsMenu.add(new JMenuItem(CapitalizeAction.getInstance()));
        functionsMenu.add(new JMenuItem(InvertCaseAction.getInstance()));
        return functionsMenu;
    }


    private Component getWordRiderMenu() {
        if (wordRiderMenu == null) {
            wordRiderMenu = initMenu("wordrider");
            final JMenu language = initMenu("ChangeLanguageAction");
            //final JMenu lafMenu = initMenu("ChangeLookAndFeelAction");
            wordRiderMenu.add(language);
            //wordRiderMenu.add(lafMenu);
            initLanguageMenu(language);
            //lafMenu.setMnemonic(Lng.getMnemonic("ChangeLookAndFeelAction"));
            language.setMnemonic(Lng.getMnemonic("ChangeLanguageAction"));
            //initLookAndFeelMenu(lafMenu);
            wordRiderMenu.addSeparator();
            wordRiderMenu.add(new JMenuItem(OnlineDocumentationAction.getInstance()));
            wordRiderMenu.add(new JMenuItem(OpenKeymapAction.getInstance()));
            wordRiderMenu.addSeparator();
            wordRiderMenu.add(new JMenuItem(CheckForNewVersion.getInstance()));
            wordRiderMenu.add(new JMenuItem(VisitHomepageAction.getInstance()));
//            wordRiderMenu.addSeparator();
//            wordRiderMenu.add(new JMenuItem(RunGCAction.getInstance()));
            wordRiderMenu.addSeparator();
            wordRiderMenu.add(new JMenuItem(ShowAboutAction.getInstance()));
        }
        return wordRiderMenu;
    }

    private static void initLanguageMenu(final JMenu langMenu) {
        langMenu.setIcon(Swinger.getIcon(LANG_NONAME_ICON));

        final String selectedLanguage = Lng.getSelectedLanguageCode();
        JRadioButtonMenuItem menuItem;
        SupportedLanguage supportedLanguage;
        final JButtonGroup buttonGroup = new JButtonGroup();
        for (SupportedLanguage supportedLanguage1 : Lng.getSupportedLanguages()) {
            supportedLanguage = supportedLanguage1;
            buttonGroup.add(menuItem = new JRadioButtonMenuItem(new ChangeLanguageAction(supportedLanguage)));
            langMenu.add(menuItem);
            menuItem.setSelected(selectedLanguage.equals(supportedLanguage.getLanguageCode()));
        }
    }

    private Component getParagraphMenu() {
        if (paragraphMenu == null) {
            paragraphMenu = initMenu("paragraph");
            paragraphMenu.add(new JMenuItem(AligmentLeftAction.getInstance()));
            paragraphMenu.add(new JMenuItem(AligmentRightAction.getInstance()));
            paragraphMenu.add(new JMenuItem(AligmentCenterAction.getInstance()));
            paragraphMenu.addSeparator();
            paragraphMenu.add(new JMenuItem(Margin10Action.getInstance()));
            paragraphMenu.add(new JMenuItem(Margin20Action.getInstance()));
            paragraphMenu.add(new JMenuItem(Margin30Action.getInstance()));
            paragraphMenu.add(new JMenuItem(MarginXAction.getInstance()));
            paragraphMenu.addSeparator();
            paragraphMenu.add(new JMenuItem(ChangeMathStyleAction.getInstance()));

        }
        return paragraphMenu;
    }

    private Component getFormatMenu() {
        if (formatMenu == null) {
            formatMenu = initMenu("format");
            formatMenu.add(new JMenuItem(ChangeFontSizeStyle1Action.getInstance()));
            formatMenu.add(new JMenuItem(ChangeFontSizeStyle2Action.getInstance()));
            formatMenu.add(new JMenuItem(ChangeFontSizeStyle3Action.getInstance()));
            formatMenu.addSeparator();
            formatMenu.add(new JMenuItem(ChangeExponentStyleAction.getInstance()));
            formatMenu.add(new JMenuItem(ChangeSubscriptStyleAction.getInstance()));
            formatMenu.addSeparator();
            formatMenu.add(new JMenuItem(ChangeUnderlineStyleAction.getInstance()));
            formatMenu.add(new JMenuItem(ChangeDottedStyleAction.getInstance()));
            formatMenu.add(new JMenuItem(ChangeStrikedStyleAction.getInstance()));
            formatMenu.addSeparator();
            formatMenu.add(new JMenuItem(ChangeInvertStyleAction.getInstance()));
            formatMenu.add(new JMenuItem(ChangeVectorStyleAction.getInstance()));
            formatMenu.add(new JMenuItem(ChangeConjugateStyleAction.getInstance()));
            formatMenu.add(new JMenuItem(ChangeWordWrapStyleAction.getInstance()));
            formatMenu.addSeparator();
            formatMenu.add(getFunctionsFormatMenu());
        }
        return formatMenu;
    }


    private Component getInsertMenu() {
        if (insertMenu == null) {
            insertMenu = initMenu("insert");
            insertMenu.addMenuListener(new SelectedMenuListener());
            insertMenu.add(new JMenuItem(InsertPictureAction.getInstance()));
            insertMenu.add(insertFromClipboardMenu = new JMenuItem(InsertFromClipboardAction.getInstance()));
            insertMenu.addSeparator();
            insertMenu.add(new JMenuItem(InsertSeparatorSingleAction.getInstance()));
            insertMenu.add(new JMenuItem(InsertSeparatorDoubleAction.getInstance()));
            insertMenu.addSeparator();
            insertMenu.add(new JMenuItem(UpdateBreakpointAction.getInstance()));
        }
        return insertMenu;
    }

    private Component getFileMenu() {
        if (fileMenu == null) {
            fileMenu = initMenu("file");
            fileMenu.add(new JMenuItem(CreateNewFileAction.getInstance()));
            fileMenu.add(new JMenuItem(OpenFileAction.getInstance()));
            fileMenu.addSeparator();
            fileMenu.add(new JMenuItem(SaveFileAction.getInstance()));
            fileMenu.add(new JMenuItem(SaveAsFileAction.getInstance()));
            fileMenu.addSeparator();
            fileMenu.add(new JMenuItem(CloseActiveAction.getInstance()));
            fileMenu.add(new JMenuItem(CloseAllAction.getInstance()));
            fileMenu.addSeparator();
            fileMenu.add(new JMenuItem(SendToCalcAction.getInstance()));
            fileMenu.addSeparator();
            fileMenu.add(recentMenu = initMenu("file.recentFiles"));
            recentMenu.setEnabled(false);
            fileMenu.addSeparator();
            fileMenu.add(new JMenuItem(ExitAction.getInstance()));
        }
        return fileMenu;
    }

    private Component getEditMenu() {
        if (editMenu == null) {
            editMenu = initMenu("edit");
            editMenu.addMenuListener(new EditMenuListener());
            editMenu.add(new JMenuItem(UndoAction.getInstance()));
            editMenu.add(new JMenuItem(RedoAction.getInstance()));
            editMenu.add(new JSeparator());
            editMenu.add(new JMenuItem(CutAction.getInstance()));
            editMenu.add(new JMenuItem(CopyAction.getInstance()));
            editMenu.add(new JMenuItem(PasteAction.getInstance()));
            editMenu.add(new JMenuItem(SelectAllAction.getInstance()));
            editMenu.addSeparator();
            editMenu.add(new JMenuItem(ShowFindReplaceDialogAction.getInstance()));
            editMenu.addSeparator();
            editMenu.add(new JMenuItem(ChangeImagePropertiesAction.getInstance()));
            editMenu.addSeparator();
            editMenu.add(new JMenuItem(ShowUserSettings.getInstance()));
        }
        return editMenu;
    }

    public JMenu getRecentsMenu() {
        return recentMenu;
    }


    //    private void enableActions(final JTextComponent component) {
    //        //parapraph
    //
    //
    //    }

    private final class SelectedMenuListener implements MenuListener {
        public void menuCanceled(final MenuEvent e) {
        }

        public void menuSelected(final MenuEvent e) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    insertFromClipboardMenu.setEnabled(InsertFromClipboardAction.getInstance().isFlavourSupported());
                }
            });
        }

        public void menuDeselected(final MenuEvent e) {
        }
    }

    private static final class EditMenuListener implements MenuListener {
        public void menuCanceled(final MenuEvent e) {
        }

        public void menuSelected(final MenuEvent e) {
            ChangeImagePropertiesAction.getInstance().updateEnabled();
        }

        public void menuDeselected(final MenuEvent e) {
        }
    }


    public void areaActivated(AreaChangeEvent event) {
        selectedAreaChanged(true, event.getFileInstance());
    }

    public void areaDeactivated(AreaChangeEvent event) {
        selectedAreaChanged(false, event.getFileInstance());
    }

    private void selectedAreaChanged(final boolean isEnabled, IFileInstance instance) {
        RiderArea editor = (isEnabled) ? (RiderArea) instance.getRiderArea() : null;
        RedoAction.getInstance().updateRedoState(editor);
        UndoAction.getInstance().updateUndoState(editor);

        ChangeFontSizeStyle1Action.getInstance().setEnabled(isEnabled);
        ChangeFontSizeStyle2Action.getInstance().setEnabled(isEnabled);
        ChangeFontSizeStyle3Action.getInstance().setEnabled(isEnabled);
        AligmentLeftAction.getInstance().setEnabled(isEnabled);
        AligmentCenterAction.getInstance().setEnabled(isEnabled);
        AligmentRightAction.getInstance().setEnabled(isEnabled);
        Margin10Action.getInstance().setEnabled(isEnabled);
        Margin20Action.getInstance().setEnabled(isEnabled);
        Margin30Action.getInstance().setEnabled(isEnabled);
        ChangeInvertStyleAction.getInstance().setEnabled(isEnabled);
        ChangeVectorStyleAction.getInstance().setEnabled(isEnabled);
        ChangeWordWrapStyleAction.getInstance().setEnabled(isEnabled);
        ChangeExponentStyleAction.getInstance().setEnabled(isEnabled);
        ChangeUnderlineStyleAction.getInstance().setEnabled(isEnabled);
        ChangeDottedStyleAction.getInstance().setEnabled(isEnabled);
        ChangeStrikedStyleAction.getInstance().setEnabled(isEnabled);
        UpdateBreakpointAction.getInstance().setEnabled(isEnabled);
        InsertPictureAction.getInstance().setEnabled(isEnabled);
        InsertSeparatorDoubleAction.getInstance().setEnabled(isEnabled);
        InsertSeparatorSingleAction.getInstance().setEnabled(isEnabled);
        ChangeMathStyleAction.getInstance().setEnabled(isEnabled);
        MarginXAction.getInstance().setEnabled(isEnabled);
        ChangeConjugateStyleAction.getInstance().setEnabled(isEnabled);
        ChangeSubscriptStyleAction.getInstance().setEnabled(isEnabled);

        ChangeImagePropertiesAction.getInstance().setEnabled(isEnabled);
        //paragraphMenu.getActionMap().
        //paragraphMenu.setEnabled(isEnabled);
        //        insertMenu.setEnabled(isEnabled);
        //        formatMenu.setEnabled(isEnabled);
        if (editor != null) {
            final boolean isSelectedText = editor.getSelectionStart() != editor.getSelectionEnd();
            CopyAction.getInstance().setEnabled(isSelectedText && editor.isEditable());
            CutAction.getInstance().setEnabled(isSelectedText && editor.isEditable());
            PasteAction.getInstance().setEnabled(editor.isEditable());
            switch (editor.getViewBorder()) {
                case RiderArea.FULLVIEWBORDER:
                    fullViewItem.setSelected(true);
                    break;
                case RiderArea.TI89VIEWBORDER:
                    ti89ViewItem.setSelected(true);
                    break;
                case RiderArea.TI92VIEWBORDER:
                    ti92ViewItem.setSelected(true);
                    break;
                default:
                    fullViewItem.setSelected(true);
                    break;
            }
        } else {
            CopyAction.getInstance().setEnabled(isEnabled);
            CutAction.getInstance().setEnabled(isEnabled);
            CopyAction.getInstance().setEnabled(isEnabled);
            PasteAction.getInstance().setEnabled(isEnabled);
        }

        getWidthViewMenu().setEnabled(isEnabled);
        SelectAllAction.getInstance().setEnabled(isEnabled);
        ShowFindReplaceDialogAction.getInstance().setEnabled(isEnabled);
        SaveFileAction.getInstance().setEnabled(isEnabled && editor != null && instance.isModified());
        SaveAsFileAction.getInstance().setEnabled(isEnabled);
        SendToCalcAction.getInstance().setEnabled(isEnabled);
        CloseActiveAction.getInstance().setEnabled(isEnabled);
        CloseActiveAction.getInstance().updateStatusName(instance);
        CloseAllAction.getInstance().setEnabled(isEnabled);

        TrimTrailingSpacesAction.getInstance().setEnabled(isEnabled);
        TextToUpperCaseAction.getInstance().setEnabled(isEnabled);
        TextToLowerCaseAction.getInstance().setEnabled(isEnabled);
        CapitalizeAction.getInstance().setEnabled(isEnabled);
        InvertCaseAction.getInstance().setEnabled(isEnabled);
        getFunctionsFormatMenu().setEnabled(isEnabled);
    }

    public void updateRecentMenu(final Collection<File> recentFiles) {
        recentMenu.removeAll();
        int position = 0;
        for (File file : recentFiles)
            recentMenu.add(new JMenuItem(new OpenRecentFileAction(file, getMnemonicByPosition(position++))));
        recentMenu.setEnabled(!recentFiles.isEmpty());
    }

    private static char getMnemonicByPosition(final int position) {
        return (char) ((position > 9) ? 'A' + position - 10 : '0' + position);
    }

}
