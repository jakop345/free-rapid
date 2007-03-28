package net.wordrider.core.swing;

import net.wordrider.area.RiderArea;
import net.wordrider.area.actions.*;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.AWTEventListener;
import java.awt.event.MouseEvent;

/**
 * User: Vity
 */
public final class TextComponentContextMenuListener implements AWTEventListener {

    public void eventDispatched(AWTEvent event) {
        final MouseEvent me = (MouseEvent) event;

        // interested only in popuptriggers
        if (!me.isPopupTrigger())
            return;

        // me.getComponent(...) retunrs the heavy weight component on which event occured
        final Component comp = SwingUtilities.getDeepestComponentAt(me.getComponent(), me.getX(), me.getY());

        // interested only in textcomponents
//        if (!(comp instanceof JTextComponent)) {
//            if (comp instanceof JComboBox) {
//                comp =
//            }
//        }

        // no popup shown by user code
        if (MenuSelectionManager.defaultManager().getSelectedPath().length > 0)
            return;
        if (!(comp instanceof JTextComponent))
            return;
        if (comp instanceof RiderArea)
            return;
        // create popup menu and show
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                showPopmenu(comp, me);
            }
        });

    }

    private void showPopmenu(Component comp, MouseEvent me) {
        final JTextComponent tc = (JTextComponent) comp;
        //Swinger.inputFocus(tc);
        tc.grabFocus();
        final JPopupMenu menu = new JPopupMenu();
        menu.add(CutAction.getInstance());
        menu.add(CopyAction.getInstance());
        menu.add(PasteAction.getInstance());
        menu.add(new DeleteAction(tc));
        menu.addSeparator();
        menu.add(SelectAllAction.getInstance());
        final Point pt = SwingUtilities.convertPoint(me.getComponent(), me.getPoint(), tc);
        menu.show(tc, pt.x, pt.y);
        //  PopupFactory.getSharedInstance().getPopup()
    }
}