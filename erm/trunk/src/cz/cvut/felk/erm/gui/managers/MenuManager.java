package cz.cvut.felk.erm.gui.managers;

import cz.cvut.felk.erm.gui.actions.EditActions;
import cz.cvut.felk.erm.gui.actions.FileActions;
import cz.cvut.felk.erm.gui.actions.HelpActions;
import cz.cvut.felk.erm.gui.actions.ViewActions;
import cz.cvut.felk.erm.gui.managers.interfaces.IAreaChangeListener;
import cz.cvut.felk.erm.gui.managers.interfaces.IFileChangeListener;
import cz.cvut.felk.erm.swing.Swinger;
import org.jdesktop.application.ApplicationActionMap;
import org.jdesktop.application.ApplicationContext;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.io.File;
import java.util.Stack;


/**
 * Sprava a vytvoreni hlavniho menu
 * @author Ladislav Vitasek
 */
public class MenuManager implements IFileChangeListener, IAreaChangeListener {
    private JMenuBar menuBar;
    private final ApplicationContext context;
    private static final String SELECTED_TEXT_PROPERTY = "selectedText";
    private static final String MENU_SEPARATOR = "---";

    public MenuManager(final ApplicationContext context) {
        super();
        this.context = context;
        initActions(new FileActions());
        initActions(new EditActions());
        initActions(new ViewActions());
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
        menuBar.add(createMenu("helpMenu", helpMenuActionNames));
        menuBar.putClientProperty(SELECTED_TEXT_PROPERTY, "");


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
}
