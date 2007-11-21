package net.wordrider.core.managers;

import net.wordrider.area.RiderArea;
import net.wordrider.area.actions.*;
import net.wordrider.core.Lng;
import net.wordrider.core.actions.*;
import net.wordrider.core.managers.interfaces.IAreaChangeListener;
import net.wordrider.core.managers.interfaces.IFileInstance;
import net.wordrider.core.managers.interfaces.WindowPositioner;
import net.wordrider.dialogs.JButtonGroup;
import net.wordrider.utilities.Swinger;

import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyVetoException;
import java.io.File;
import java.util.*;
import java.util.List;

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
        final MyDesktopContentManagerUI ui = (MyDesktopContentManagerUI) director.getDockingWindowManager().getContentManager().getContentManagerUI();
        menuBar.add(new JWindowsMenu((JDesktopPane) ui.getContainer()));

        menuBar.add(getWordRiderMenu());

    }

    /**
     * The possible static menu items that can be added to the Windows menu above the dynamic listing of open windows.
     */
    enum MenuItem {
        /**
         * Cascade windows from top left down
         */
        CASCADE,
        /**
         * Checkerboard style tile of windows
         */
        TILE,
        /**
         * Tile from top down
         */
        TILE_HORIZ,
        /**
         * Tile from left to right
         */
        TILE_VERT,
        /**
         * Restore the currently selected window to original size
         */
        RESTORE,
        /**
         * Restore all windows to their original size
         */
        RESTORE_ALL,
        /**
         * Minimize the currently selected window to original size
         */
        MINIMIZE,
        /**
         * Minimize all windows to their original size
         */
        MINIMIZE_ALL,
        /**
         * Maximize the currently selected window to original size
         */
        MAXIMIZE,
        /**
         * Maximize all windows to their original size
         */
        MAXIMIZE_ALL,
        /**
         * Indicates a menu separator should be placed in the menu
         */
        SEPARATOR
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

    public class JWindowsMenu extends JMenu implements ContainerListener {

        /**
         * The desktop whose windows are being monitored
         */
        private JDesktopPane desktop;

        /**
         * Used to retrieve the menu item corresponding to a given frame
         */
        private Map<JInternalFrame, JCheckBoxMenuItem> menusForFrames;

        /**
         * Used for sorting the frames in alphabetical order by title
         */
        private Comparator<JInternalFrame> frameComparator;

        /**
         * The static menus for each chosen MenuItem type
         */
        private Map<MenuItem, JMenuItem> staticMenus;

        /**
         * An optional helper class which governs the position of new windows
         */
        private WindowPositioner windowPositioner;

        /**
         * Create the "Windows" menu for a MDI view using default title and menu choices.
         * @param desktop The desktop to monitor.
         */
        public JWindowsMenu(JDesktopPane desktop) {
            this("Window", desktop);
        }

        /**
         * Create the "Windows" menu for a MDI view using the given title and default menu choices.
         * @param windowTitle The title of the window to display.
         * @param desktop     The desktop to monitor.
         */
        public JWindowsMenu(String windowTitle, JDesktopPane desktop) {
            this(windowTitle, desktop, MenuItem.CASCADE, MenuItem.TILE,
                    MenuItem.TILE_HORIZ, MenuItem.TILE_VERT, MenuItem.SEPARATOR,
                    MenuItem.RESTORE, MenuItem.MINIMIZE, MenuItem.MAXIMIZE,
                    MenuItem.SEPARATOR, MenuItem.RESTORE_ALL,
                    MenuItem.MINIMIZE_ALL, MenuItem.MAXIMIZE_ALL);
        }

        /**
         * Create the "Windows" menu for a MDI view using the given title and menu items.
         * @param windowTitle The title of the window to display.
         * @param desktop     The desktop to monitor.
         * @param items       A variable length argument indicating which menu items to display in the menu.
         */
        public JWindowsMenu(String windowTitle, JDesktopPane desktop,
                            MenuItem... items) {

            this.desktop = desktop;
            this.staticMenus = new HashMap<MenuItem, JMenuItem>();
            setText(windowTitle);

            for (MenuItem item : items) {
                addMenuItem(item);
            }

            // Add a final separator if the user forgot to include it
            if (items[items.length - 1] != MenuItem.SEPARATOR) {
                addMenuItem(MenuItem.SEPARATOR);
            }

            // Sort frames by title alphabetically
            this.frameComparator = new Comparator<JInternalFrame>() {
                public int compare(JInternalFrame o1, JInternalFrame o2) {
                    int ret = 0;
                    if (o1 != null && o2 != null) {
                        String t1 = o1.getTitle();
                        String t2 = o2.getTitle();

                        if (t1 != null && t2 != null) {
                            ret = t1.compareTo(t2);
                        } else if (t1 == null && t2 != null) {
                            ret = -1;
                        } else if (t1 != null) {
                            ret = 1;
                        } else {
                            ret = 0;
                        }
                    }
                    return (ret);
                }
            };

            this.menusForFrames = new HashMap<JInternalFrame, JCheckBoxMenuItem>();
            this.desktop.addContainerListener(this);
            this.desktop.setDesktopManager(new CustomDesktopManager());
            updateWindowsList(); // Setup list for any existing windows
        }

        /**
         * Creates a static menu item with mnemonic and action listener.
         * @param item The type of menu item to add.
         */
        private void addMenuItem(MenuItem item) {
            String name = null;
            int mnemonic = 0;
            ActionListener listener = null;

            switch (item) {
                case CASCADE:
                    name = "Cascade";
                    mnemonic = KeyEvent.VK_C;
                    listener = new ActionListener() {
                        public void actionPerformed(@SuppressWarnings("unused")
                        ActionEvent e) {
                            cascade();
                        }
                    };
                    break;
                case TILE:
                    name = "Tile";
                    mnemonic = KeyEvent.VK_T;
                    listener = new ActionListener() {
                        public void actionPerformed(@SuppressWarnings("unused")
                        ActionEvent e) {
                            tile();
                        }
                    };
                    break;
                case TILE_HORIZ:
                    name = "Tile Horizontally";
                    mnemonic = KeyEvent.VK_H;
                    listener = new ActionListener() {
                        public void actionPerformed(@SuppressWarnings("unused")
                        ActionEvent e) {
                            tileHorizontally();
                        }
                    };
                    break;
                case TILE_VERT:
                    name = "Tile Vertically";
                    mnemonic = KeyEvent.VK_V;
                    listener = new ActionListener() {
                        public void actionPerformed(@SuppressWarnings("unused")
                        ActionEvent e) {
                            tileVertically();
                        }
                    };
                    break;
                case RESTORE:
                    name = "Restore";
                    mnemonic = KeyEvent.VK_R;
                    listener = new ActionListener() {
                        public void actionPerformed(@SuppressWarnings("unused")
                        ActionEvent e) {
                            try {
                                if (desktop.getSelectedFrame().isIcon()) {
                                    desktop.getSelectedFrame().setIcon(false);
                                } else if (desktop.getSelectedFrame().isMaximum()) {
                                    desktop.getSelectedFrame().setMaximum(false);
                                }
                            } catch (PropertyVetoException ex) {
                                throw new RuntimeException(ex);
                            }
                        }
                    };
                    break;
                case RESTORE_ALL:
                    name = "Restore All";
                    mnemonic = KeyEvent.VK_E;
                    listener = new ActionListener() {
                        public void actionPerformed(@SuppressWarnings("unused")
                        ActionEvent e) {
                            for (JInternalFrame frame : desktop.getAllFrames()) {
                                try {
                                    if (frame.isIcon()) {
                                        frame.setIcon(false);
                                    } else if (frame.isMaximum()) {
                                        frame.setMaximum(false);
                                    }
                                } catch (PropertyVetoException ex) {
                                    throw new RuntimeException(ex);
                                }
                            }
                        }
                    };
                    break;
                case MINIMIZE:
                    name = "Minimize";
                    mnemonic = KeyEvent.VK_M;
                    listener = new ActionListener() {
                        public void actionPerformed(@SuppressWarnings("unused")
                        ActionEvent e) {
                            if (!desktop.getSelectedFrame().isIcon()) {
                                try {
                                    desktop.getSelectedFrame().setIcon(true);
                                } catch (PropertyVetoException ex) {
                                    throw new RuntimeException(ex);
                                }
                            }
                        }
                    };
                    break;
                case MINIMIZE_ALL:
                    name = "Minimize All";
                    mnemonic = KeyEvent.VK_I;
                    listener = new ActionListener() {
                        public void actionPerformed(@SuppressWarnings("unused")
                        ActionEvent e) {
                            for (JInternalFrame frame : desktop.getAllFrames()) {
                                if (!frame.isIcon()) {
                                    try {
                                        frame.setIcon(true);
                                    } catch (PropertyVetoException ex) {
                                        throw new RuntimeException(ex);
                                    }
                                }
                            }
                        }
                    };
                    break;
                case MAXIMIZE:
                    name = "Maximize";
                    mnemonic = KeyEvent.VK_A;
                    listener = new ActionListener() {
                        public void actionPerformed(@SuppressWarnings("unused")
                        ActionEvent e) {
                            try {
                                desktop.getSelectedFrame().setMaximum(true);
                            } catch (PropertyVetoException ex) {
                                throw new RuntimeException(ex);
                            }
                        }
                    };
                    break;
                case MAXIMIZE_ALL:
                    name = "Maximize All";
                    mnemonic = KeyEvent.VK_X;
                    listener = new ActionListener() {
                        public void actionPerformed(@SuppressWarnings("unused")
                        ActionEvent e) {
                            for (JInternalFrame frame : desktop.getAllFrames()) {
                                try {
                                    frame.setMaximum(true);
                                } catch (PropertyVetoException ex) {
                                    throw new RuntimeException(ex);
                                }
                            }
                        }
                    };
                    break;
                case SEPARATOR:
                    addSeparator(); // Create a menu separator
            }

            // Now create a menu item with the given name, mnemonic and listener
            if (name != null) {
                JMenuItem menuItem = new JMenuItem(name);
                menuItem.setMnemonic(mnemonic);
                menuItem.addActionListener(listener);
                add(menuItem); // Add to the main menu
                staticMenus.put(item, menuItem);
            }
        }

        /**
         * @return A list of frames on the desktop which are not iconified and are visible.
         */
        private List<JInternalFrame> getAllVisibleFrames() {
            List<JInternalFrame> frames = new ArrayList<JInternalFrame>();
            for (JInternalFrame frame : this.desktop.getAllFrames()) {
                if (frame.isVisible() && !frame.isClosed() && !frame.isIcon()) {
                    frames.add(frame);
                }
            }
            Collections.sort(frames, this.frameComparator);
            return frames;
        }

        /**
         * Change the bounds of visible windows to tile them vertically on the desktop.
         */
        protected void tileVertically() {
            java.util.List<JInternalFrame> frames = getAllVisibleFrames();
            int newWidth = this.desktop.getWidth() / frames.size();
            int newHeight = this.desktop.getHeight();

            int x = 0;
            for (JInternalFrame frame : frames) {
                if (frame.isMaximum()) {
                    try {
                        frame.setMaximum(false); // Restore if maximized first
                    } catch (PropertyVetoException ex) {
                        throw new RuntimeException(ex);
                    }
                }
                frame.reshape(x, 0, newWidth, newHeight);
                x += newWidth;
            }
        }

        /**
         * Change the bounds of visible windows to tile them horizontally on the desktop.
         */
        protected void tileHorizontally() {
            List<JInternalFrame> frames = getAllVisibleFrames();
            int newWidth = this.desktop.getWidth();
            int newHeight = this.desktop.getHeight() / frames.size();

            int y = 0;
            for (JInternalFrame frame : frames) {
                if (frame.isMaximum()) {
                    try {
                        frame.setMaximum(false); // Restore if maximized first
                    } catch (PropertyVetoException ex) {
                        throw new RuntimeException(ex);
                    }
                }
                frame.reshape(0, y, newWidth, newHeight);
                y += newHeight;
            }
        }

        /**
         * Change the bounds of visible windows to tile them checkerboard-style on the desktop.
         */
        protected void tile() {
            List<JInternalFrame> frames = getAllVisibleFrames();
            if (frames.isEmpty()) {
                return;
            }

            double sqrt = Math.sqrt(frames.size());
            int numCols = (int) Math.floor(sqrt);
            int numRows = numCols;
            if ((numCols * numRows) < frames.size()) {
                numCols++;
                if ((numCols * numRows) < frames.size()) {
                    numRows++;
                }
            }

            int newWidth = this.desktop.getWidth() / numCols;
            int newHeight = this.desktop.getHeight() / numRows;

            int y = 0;
            int x = 0;
            int frameIdx = 0;
            for (int i = 0; i < numRows; i++) {
                for (int j = 0; j < numCols; j++) {
                    if (frameIdx < frames.size()) {
                        JInternalFrame frame = frames.get(frameIdx++);
                        if (frame.isMaximum()) {
                            try {
                                frame.setMaximum(false);
                            } catch (PropertyVetoException ex) {
                                throw new RuntimeException(ex);
                            }
                        }
                        frame.reshape(x, y, newWidth, newHeight);
                        x += newWidth;
                    }
                }
                x = 0;
                y += newHeight;
            }
        }

        /**
         * Change the bounds of visible windows to cascade them down from the top left of the desktop.
         */
        protected void cascade() {
            List<JInternalFrame> frames = getAllVisibleFrames();
            if (frames.isEmpty()) {
                return;
            }

            int newWidth = (int) (this.desktop.getWidth() * 0.6);
            int newHeight = (int) (this.desktop.getHeight() * 0.6);
            int x = 0;
            int y = 0;
            for (JInternalFrame frame : frames) {
                if (frame.isMaximum()) {
                    try {
                        frame.setMaximum(false);
                    } catch (PropertyVetoException ex) {
                        throw new RuntimeException(ex);
                    }
                }
                frame.reshape(x, y, newWidth, newHeight);
                x += 25;
                y += 25;

                if ((x + newWidth) > this.desktop.getWidth()) {
                    x = 0;
                }

                if ((y + newHeight) > this.desktop.getHeight()) {
                    y = 0;
                }
            }
        }

        /**
         * Records the addition of a window to the desktop.
         * @see java.awt.event.ContainerListener#componentAdded(java.awt.event.ContainerEvent)
         */
        public void componentAdded(ContainerEvent e) {
            if ((this.windowPositioner != null)
                    && (e.getChild() instanceof JInternalFrame)) {
                JInternalFrame frame = (JInternalFrame) e.getChild();
                Point position = this.windowPositioner.getPosition(frame,
                        getAllVisibleFrames());
                frame.setLocation(position);
            }
            updateWindowsList();
        }

        /**
         * Records the removal of a window from the desktop.
         * @see java.awt.event.ContainerListener#componentRemoved(java.awt.event.ContainerEvent)
         */
        public void componentRemoved(ContainerEvent e) {
            updateWindowsList();
        }

        /**
         * Invoked to regenerate the dynamic window listing menu items at the bottom of the menu.
         */
        private void updateWindowsList() {

            List<JInternalFrame> frames = new ArrayList<JInternalFrame>();
            frames.addAll(Arrays.asList(this.desktop.getAllFrames()));
            Collections.sort(frames, this.frameComparator);

            for (Component menu : this.getMenuComponents()) {
                if (menu instanceof JCheckBoxMenuItem) {
                    this.remove(menu);
                }
            }

            this.menusForFrames.clear();

            int i = 1;
            ButtonGroup group = new ButtonGroup();
            for (final JInternalFrame frame : frames) {
                JCheckBoxMenuItem item = new JCheckBoxMenuItem(i + " "
                        + frame.getTitle());

                if (frame.isIcon()) {
                    item.setSelected(false);
                }

                if (frame.isSelected()) {
                    item.setState(true);
                }
                group.add(item);
                item.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        try {
                            if (frame.isIcon()) {
                                frame.setIcon(false);
                            }

                            if (!frame.isSelected()) {
                                frame.setSelected(true);
                                frame.toFront();
                            }
                        } catch (PropertyVetoException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                });
                this.menusForFrames.put(frame, item);
                add(item);
                i++;
            }
        }

        /**
         * Toggle the enabled state of the static menu items depending on the selected frame.
         */
        private void updateStaticMenuItems() {
            JInternalFrame selectedFrame = this.desktop.getSelectedFrame();
            JMenuItem minimizeItem = this.staticMenus.get(MenuItem.MINIMIZE);
            JMenuItem maximizeItem = this.staticMenus.get(MenuItem.MAXIMIZE);
            JMenuItem restoreItem = this.staticMenus.get(MenuItem.RESTORE);

            for (JCheckBoxMenuItem item : menusForFrames.values()) {
                item.setSelected(false);
            }

            if (selectedFrame == null) {
                restoreItem.setEnabled(false);
                maximizeItem.setEnabled(false);
                minimizeItem.setEnabled(false);
            } else if (selectedFrame.isIcon()) {
                restoreItem.setEnabled(true);
                maximizeItem.setEnabled(selectedFrame.isMaximizable());
                minimizeItem.setEnabled(false);
                menusForFrames.get(selectedFrame).setSelected(true);
            } else if (selectedFrame.isMaximum()) {
                restoreItem.setEnabled(true);
                maximizeItem.setEnabled(false);
                minimizeItem.setEnabled(selectedFrame.isIconifiable());
                menusForFrames.get(selectedFrame).setSelected(true);
            } else { // Window in regular position
                restoreItem.setEnabled(false);
                maximizeItem.setEnabled(selectedFrame.isMaximizable());
                minimizeItem.setEnabled(selectedFrame.isIconifiable());
                menusForFrames.get(selectedFrame).setSelected(true);
            }
        }

        /**
         * A desktop manager for listening to window-related events on the desktop.
         */
        private class CustomDesktopManager extends DefaultDesktopManager {

            /**
             * @see javax.swing.DefaultDesktopManager#activateFrame(javax.swing.JInternalFrame)
             */
            @Override
            public void activateFrame(JInternalFrame f) {
                super.activateFrame(f);
                updateStaticMenuItems();
            }

            /**
             * @see javax.swing.DefaultDesktopManager#deactivateFrame(javax.swing.JInternalFrame)
             */
            @Override
            public void deactivateFrame(JInternalFrame f) {
                super.deactivateFrame(f);
                updateStaticMenuItems();
            }

            /**
             * @see javax.swing.DefaultDesktopManager#deiconifyFrame(javax.swing.JInternalFrame)
             */
            @Override
            public void deiconifyFrame(JInternalFrame f) {
                super.deiconifyFrame(f);
                updateStaticMenuItems();
            }

            /**
             * @see javax.swing.DefaultDesktopManager#iconifyFrame(javax.swing.JInternalFrame)
             */
            @Override
            public void iconifyFrame(JInternalFrame f) {
                super.iconifyFrame(f);
                updateStaticMenuItems();
            }

            /**
             * @see javax.swing.DefaultDesktopManager#maximizeFrame(javax.swing.JInternalFrame)
             */
            @Override
            public void maximizeFrame(JInternalFrame f) {
                super.maximizeFrame(f);
                updateStaticMenuItems();
            }

            /**
             * @see javax.swing.DefaultDesktopManager#minimizeFrame(javax.swing.JInternalFrame)
             */
            @Override
            public void minimizeFrame(JInternalFrame f) {
                super.minimizeFrame(f);
                updateStaticMenuItems();
            }
        }

        /**
         * Use this window positioner to position (<code>setLocation()</code>) of new windows added to the desktop.
         * @param windowPositioner
         */
        public void setWindowPositioner(WindowPositioner windowPositioner) {
            this.windowPositioner = windowPositioner;
        }
    }


}
