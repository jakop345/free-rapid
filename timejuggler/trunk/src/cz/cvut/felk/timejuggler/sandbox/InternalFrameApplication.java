package cz.cvut.felk.timejuggler.sandbox;

/**
 * @author Vity
 */

import application.*;
import org.jdesktop.swingx.JXStatusBar;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InternalFrameApplication extends SingleFrameApplication {

    private static Logger logger
            = Logger.getLogger(InternalFrameApplication.class.getName());

    protected ResourceMap resource;

    public JDesktopPane maintDesktopPane;
    public DefaultDesktopManager maintDesktopManager;

    /**
     * Creates a new instance of Main
     */
    public InternalFrameApplication() {
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Application.launch(InternalFrameApplication.class, args);
    }

    @Override
    protected void initialize(String[] args) {
        // Initialization
        logger.log(Level.INFO, "starting inititalize()..");
        super.initialize(args);
        ResourceManager rMgr = getContext().getResourceManager();
        resource = rMgr.getResourceMap(InternalFrameApplication.class);

    }

    @Override
    protected void startup() {
        // Create Main View
        View view = getMainView();
        view.setComponent(createMainPanel());
        view.setToolBar(createToolBar());
        view.setMenuBar(createMenuBar());
        view.setStatusBar(createStatusBar());
        show(view);
    }

    /**
     *
     */
    private JComponent createMainPanel() {
        // JPanel p1 = new JPanel();
        maintDesktopPane = new JDesktopPane();
        // p1.add(maintDesktopPane);
        maintDesktopManager = new DefaultDesktopManager();
        maintDesktopPane.setDesktopManager(maintDesktopManager);
        return maintDesktopPane;
    }

    /**
     * Gets action from ApplicationContext by name
     */
    private javax.swing.Action getAction(String actionName) {
        return getContext().getActionMap().get(actionName);
    }

    /**
     *
     */
    public JToolBar createToolBar() {
        String[] toolbarActionNames = {
                "quit"
        };
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        Border border = new EmptyBorder(2, 9, 2, 9); // top, left, bottom, right
        for (String actionName : toolbarActionNames) {
            JButton button = new JButton();
            button.setBorder(border);
            button.setVerticalTextPosition(JButton.BOTTOM);
            button.setHorizontalTextPosition(JButton.CENTER);
            button.setAction(getAction(actionName));
            button.setFocusable(false);
            toolBar.add(button);
        }
        return toolBar;
    }

    /**
     *
     */
    public JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        String[] fileMenuActionNames = {
                "quit"
        };
        menuBar.add(createMenu("fileMenu", fileMenuActionNames));
        return menuBar;
    }

    /**
     *
     */
    private JMenu createMenu(String menuName, String[] actionNames) {
        JMenu menu = new JMenu();
        menu.setName(menuName);
        for (String actionName : actionNames) {
            if (actionName.equals("---")) {
                menu.add(new JSeparator());
            } else {
                JMenuItem menuItem = new JMenuItem();
                menuItem.setAction(getAction(actionName));
                menuItem.setIcon(null);
                menu.add(menuItem);
            }
        }
        return menu;
    }

    /**
     *
     */
    public JXStatusBar createStatusBar() {
        JXStatusBar statusBar = new JXStatusBar();
        return statusBar;
    }
}
