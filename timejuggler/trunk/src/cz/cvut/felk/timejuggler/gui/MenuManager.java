package cz.cvut.felk.timejuggler.gui;

import application.ApplicationActionMap;
import application.ApplicationContext;
import cz.cvut.felk.timejuggler.gui.actions.*;
import cz.cvut.felk.timejuggler.swing.Swinger;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


/**
 * Sprava a vytvoreni hlavniho menu
 * @author Vity
 */
public class MenuManager {
    private JMenuBar menuBar;
    private final ApplicationContext context;
    private static final String SELECTED_TEXT_PROPERTY = "selectedText";
    private static final String MENU_SEPARATOR = "---";

    public MenuManager(final ApplicationContext context) {
        super();
        this.context = context;
        initActions(new FileActions());
        initActions(new EditActions());
        initActions(new GoActions());
        initActions(new ViewActions());
        initActions(new HelpActions());
    }

    private void init() {

        final Object[] fileMenuActionNames = {
                "newEvent",
                "newTask",
                "newCalendar",
                "openCalendarFile",
                MENU_SEPARATOR,
                "importCalendar",
                "exportSelection",
                "exportCalendar",
                MENU_SEPARATOR,
                "pageSetup",
                "print",
                MENU_SEPARATOR,
                "quit"
        };
        final Object[] editMenuActionNames = {
                "cut",
                "copy",
                "paste",
                "editSelection",
                MENU_SEPARATOR,
                "options",
        };
        final Object[] goMenuActionNames = {
                "goToday",
                "goToDate",
                MENU_SEPARATOR,
                "previousDate",
                "nextDate",
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
        menuBar.add(createMenu("goMenu", goMenuActionNames));
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
        jMenu.add(new JCheckBoxMenuItem(Swinger.getAction("showSearchBar")));
        jMenu.add(new JCheckBoxMenuItem(Swinger.getAction("showStatusBar")));
        jMenu.addSeparator();
        final ButtonGroup buttonGroup = new ButtonGroup();
        JRadioButtonMenuItem item = new JRadioButtonMenuItem(Swinger.getAction("dayView"));
        buttonGroup.add(item);
        jMenu.add(item);
        item = new JRadioButtonMenuItem(Swinger.getAction("weekView"));
        buttonGroup.add(item);
        jMenu.add(item);
        item = new JRadioButtonMenuItem(Swinger.getAction("multiWeekView"));
        buttonGroup.add(item);
        jMenu.add(item);
        item = new JRadioButtonMenuItem(Swinger.getAction("monthView"));
        buttonGroup.add(item);
        jMenu.add(item);
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

}
