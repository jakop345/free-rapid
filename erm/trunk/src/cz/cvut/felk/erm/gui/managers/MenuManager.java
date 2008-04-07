package cz.cvut.felk.erm.gui.managers;

import cz.cvut.felk.erm.gui.actions.*;
import cz.cvut.felk.erm.gui.managers.interfaces.IAreaChangeListener;
import cz.cvut.felk.erm.gui.managers.interfaces.IFileChangeListener;
import cz.cvut.felk.erm.swing.Swinger;
import org.jdesktop.application.ApplicationActionMap;
import org.jdesktop.application.ApplicationContext;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.beans.PropertyVetoException;
import java.io.File;
import java.util.*;
import java.util.List;


/**
 * Sprava a vytvoreni hlavniho menu
 *
 * @author Ladislav Vitasek
 */
public class MenuManager implements IFileChangeListener, IAreaChangeListener {
    private JMenuBar menuBar;
    private final ApplicationContext context;
    private final ManagerDirector director;
    private static final String SELECTED_TEXT_PROPERTY = "selectedText";
    private static final String MENU_SEPARATOR = "---";


    public MenuManager(final ApplicationContext context, ManagerDirector director) {
        super();
        this.context = context;
        this.director = director;
        initActions(new FileActions());
        initActions(new EditActions());
        initActions(new ViewActions());
        initActions(new WindowActions());
        initActions(new HelpActions());
    }

    private void init() {

        final Object[] fileMenuActionNames = {
                "newScheme",
                "openScheme",
                MENU_SEPARATOR,
                "saveScheme",
                "saveAsScheme",
                MENU_SEPARATOR,
                "closeActiveScheme",
                "closeAllSchemes",
                MENU_SEPARATOR,
//                "pageSetup",
//                "print",
//                MENU_SEPARATOR,
                "quit"
        };
        final Object[] editMenuActionNames = {
                "cut",
                "copy",
                "paste",
                MENU_SEPARATOR,
                "options",
        };
        final Object[] helpMenuActionNames = {
                "help",
                MENU_SEPARATOR,
                "about"
        };

        MenuSelectionManager.defaultManager().addChangeListener(
                new ChangeListener() {
                    public void stateChanged(ChangeEvent evt) {
                        // Get the selected menu or menu item
                        MenuSelectionManager msm = (MenuSelectionManager) evt.getSource();
                        MenuElement[] path = msm.getSelectedPath();
                        // To interpret path, see
                        // e813 Getting the Currently Selected Menu or Menu Item
                        final StringBuilder builder = new StringBuilder();
                        for (MenuElement menuElement : path) {
                            if (!(menuElement.getComponent() instanceof JMenuItem))
                                continue;

                            JMenuItem menuItem = (JMenuItem) menuElement.getComponent();
                            final Action action = menuItem.getAction();
                            if (action == null)
                                continue;
                            final String longDescription = (String) action.getValue(Action.LONG_DESCRIPTION);
                            if (longDescription != null) {
                                if (builder.length() > 0)
                                    builder.append(" - ");
                                builder.append(longDescription);
                            } else {
                                final Object shortDescription = action.getValue(Action.SHORT_DESCRIPTION);
                                if (shortDescription != null) {
                                    if (builder.length() > 0)
                                        builder.append(" - ");
                                    builder.append(shortDescription);
                                }
                            }
                        }
                        menuBar.putClientProperty(SELECTED_TEXT_PROPERTY, builder.toString());
                    }
                }
        );
        menuBar.add(createMenu("fileMenu", fileMenuActionNames));
        menuBar.add(createMenu("editMenu", editMenuActionNames));
        menuBar.add(createViewMenu());
        menuBar.add(createWindowMenu());
        menuBar.add(createMenu("helpMenu", helpMenuActionNames));
        menuBar.putClientProperty(SELECTED_TEXT_PROPERTY, "");


    }

    private JMenu createWindowMenu() {
        final Object[] windowMenuActionNames = {
                "cascade",
                "tile",
                "tileHorizontal",
                "tileVertical",
                MENU_SEPARATOR,
                "restore",
                "minimize",
                "maximize",
                MENU_SEPARATOR,
                "restoreAll",
                "minimizeAll",
                "maximizeAll",
                MENU_SEPARATOR,
        };


        final JMenu winMenu = createMenu("windowMenu", windowMenuActionNames);
        new JWindowsMenu(winMenu, director.getContentPane());

        return winMenu;
    }

    private void initActions(Object actionsObject) {
        final ApplicationActionMap globalMap = context.getActionMap();
        final ApplicationActionMap actionMap = context.getActionMap(actionsObject);
        for (Object key : actionMap.keys()) {
            globalMap.put(key, actionMap.get(key));
        }
    }

    private JMenu createViewMenu() {
        final JMenu jMenu = new JMenu();
        jMenu.setName("viewMenu");
        jMenu.add(new JCheckBoxMenuItem(Swinger.getAction("showToolbar")));
        jMenu.add(new JCheckBoxMenuItem(Swinger.getAction("showStatusBar")));
        return jMenu;
    }

    public JMenuBar getMenuBar() {
        if (menuBar == null) {
            this.menuBar = new JMenuBar();
            //final ApplicationActionMap map = new ApplicationActionMap();
//            map.setParent(ApplicationContext.getInstance().getActionMap());
//            map.
            init();
        }
        return menuBar;
    }

    private static JMenu createMenu(String menuName, Object[] actionNames) {
        JMenu menu = new JMenu();
        return processMenu(menu, menuName, actionNames);
    }

    private static JMenu processMenu(JMenu menu, String menuName, Object[] actionNames) {
        menu.setName(menuName);
        for (Object actionName : actionNames) {
            if (MENU_SEPARATOR.equals(actionName)) {
                menu.addSeparator();
            } else {
                JMenuItem menuItem = new JMenuItem();
                menuItem.setAction(Swinger.getAction(actionName));
                menuItem.setToolTipText("");//showed in statusbar
                menu.add(menuItem);
            }
        }
        return menu;
    }

    public static JPopupMenu processMenu(JPopupMenu menu, String menuName, Object[] actionNames) {
        menu.setName(menuName);
        for (Object actionName : actionNames) {
            if (MENU_SEPARATOR.equals(actionName)) {
                menu.addSeparator();
            } else {
                JMenuItem menuItem = new JMenuItem();
                menuItem.setAction(Swinger.getAction(actionName));
                menu.add(menuItem);
            }
        }
        return menu;
    }

    public void updateRecentMenu(Stack<File> recentFilesList) {

    }

    public void fileWasOpened(FileChangeEvent event) {

    }

    public void fileWasClosed(FileChangeEvent event) {

    }


    public void areaActivated(AreaChangeEvent event) {

    }

    public void areaDeactivated(AreaChangeEvent event) {

    }


    private class JWindowsMenu implements ContainerListener {

        /**
         * The desktop whose windows are being monitored
         */
        private JDesktopPane desktop;

        /**
         * Used to retrieve the menu item corresponding to a given frame
         */
        private Map<JInternalFrame, JCheckBoxMenuItem> menusForFrames;

        /**
         * An optional helper class which governs the position of new windows
         */
        private WindowPositioner windowPositioner;
        private final JMenu windowMenu;

        /**
         * Create the "Windows" menu for a MDI view using the given title and menu items.
         *
         * @param windowTitle The title of the window to display.
         * @param desktop     The desktop to monitor.
         * @param items       A variable length argument indicating which menu items to display in the menu.
         */
        public JWindowsMenu(JMenu windowMenu, JDesktopPane desktop) {
            this.windowMenu = windowMenu;

            this.desktop = desktop;

            this.menusForFrames = new HashMap<JInternalFrame, JCheckBoxMenuItem>();
            desktop.addContainerListener(this);
            desktop.setDesktopManager(new CustomDesktopManager());
            updateWindowsList(); // Setup list for any existing windows
        }


        /**
         * Records the addition of a window to the desktop.
         *
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
         *
         * @see java.awt.event.ContainerListener#componentRemoved(java.awt.event.ContainerEvent)
         */
        public void componentRemoved(ContainerEvent e) {
            updateWindowsList();
        }

        /**
         * Invoked to regenerate the dynamic window listing menu items at the bottom of the menu.
         */
        private void updateWindowsList() {

            java.util.List<JInternalFrame> frames = new ArrayList<JInternalFrame>();
            frames.addAll(Arrays.asList(getDesktop().getAllFrames()));
            Collections.sort(frames, new WindowActions.FrameComparator());

            for (Component menu : this.windowMenu.getMenuComponents()) {
                if (menu instanceof JCheckBoxMenuItem) {
                    this.windowMenu.remove(menu);
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
                this.windowMenu.add(item);
                i++;
            }
        }

        /**
         * Toggle the enabled state of the static menu items depending on the selected frame.
         */
        private void updateStaticMenuItems() {
            JInternalFrame selectedFrame = getDesktop().getSelectedFrame();
            final Action minimizeAction = Swinger.getAction("minimize");
            final Action maximizeAction = Swinger.getAction("maximize");
            final Action restoreAction = Swinger.getAction("restore");

            for (JCheckBoxMenuItem item : menusForFrames.values()) {
                item.setSelected(false);
            }

            if (selectedFrame == null) {
                restoreAction.setEnabled(false);
                maximizeAction.setEnabled(false);
                minimizeAction.setEnabled(false);
            } else if (selectedFrame.isIcon()) {
                restoreAction.setEnabled(true);
                maximizeAction.setEnabled(selectedFrame.isMaximizable());
                minimizeAction.setEnabled(false);
                menusForFrames.get(selectedFrame).setSelected(true);
            } else if (selectedFrame.isMaximum()) {
                restoreAction.setEnabled(true);
                maximizeAction.setEnabled(false);
                minimizeAction.setEnabled(selectedFrame.isIconifiable());
                menusForFrames.get(selectedFrame).setSelected(true);
            } else { // Window in regular position
                restoreAction.setEnabled(false);
                maximizeAction.setEnabled(selectedFrame.isMaximizable());
                minimizeAction.setEnabled(selectedFrame.isIconifiable());
                menusForFrames.get(selectedFrame).setSelected(true);
            }
        }

        /**
         * @return A list of frames on the desktop which are not iconified and are visible.
         */
        private List<JInternalFrame> getAllVisibleFrames() {
            List<JInternalFrame> frames = new ArrayList<JInternalFrame>();
            for (JInternalFrame frame : getDesktop().getAllFrames()) {
                if (frame.isVisible() && !frame.isClosed() && !frame.isIcon()) {
                    frames.add(frame);
                }
            }
            Collections.sort(frames, new WindowActions.FrameComparator());
            return frames;
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
         *
         * @param windowPositioner
         */
        public void setWindowPositioner(WindowPositioner windowPositioner) {
            this.windowPositioner = windowPositioner;
        }

        private JDesktopPane getDesktop() {
            return this.desktop;
        }
    }


}
