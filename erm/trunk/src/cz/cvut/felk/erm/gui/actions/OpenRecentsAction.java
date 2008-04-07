package cz.cvut.felk.erm.gui.actions;

import cz.cvut.felk.erm.gui.managers.ManagerDirector;
import cz.cvut.felk.erm.gui.managers.MenuManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * @author Vity
 */
public class OpenRecentsAction extends CoreAction {

    public OpenRecentsAction() {
        super("OpenRecentsAction", null, null);
    }

    public void actionPerformed(ActionEvent e) {
        final JComponent source = (JComponent) e.getSource();
        final ManagerDirector managerDirector = getManagerDirector();
        final MenuManager menuManager = managerDirector.getMenuManager();
        final JMenu jMenu = menuManager.getRecentsMenu();
        final JPopupMenu popup = new JPopupMenu();

        final JPopupMenu menuPopmenu = jMenu.getPopupMenu();
        final Component[] components = menuPopmenu.getComponents();
        for (Component component : components) {
            popup.add((JMenuItem) component);
        }
        getManagerDirector().getAreaManager().getRecentFilesManager().buildRecentFileMenu();
        if (components.length == 0)
            return; //prazdne podmenu
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                showPopup(popup, source);
                //  popup.removeAll();
//              for (Component component : components) {
//                    menuPopmenu.add((JMenuItem) component);
//                }
            }
        });
    }

    private void showPopup(JPopupMenu popup, JComponent source) {
        popup.show(source.getParent(), source.getX(), source.getY() + source.getHeight() / 2);
    }
}
