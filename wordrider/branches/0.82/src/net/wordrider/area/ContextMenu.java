package net.wordrider.area;

import net.wordrider.area.actions.*;
import net.wordrider.core.actions.ChangeImagePropertiesAction;
import net.wordrider.core.actions.CloseActiveAction;

import javax.swing.*;

/**
 * @author Vity
 */
final class ContextMenu extends JPopupMenu {
    private final static ContextMenu instance = new ContextMenu();

    private ContextMenu() {
        super();    //call to super
        add(CloseActiveAction.getInstance());
        addSeparator();
        add(CutAction.getInstance());
        add(CopyAction.getInstance());
        add(PasteAction.getInstance());
        add(SelectAllAction.getInstance());
        addSeparator();
        add(ShowFindReplaceDialogAction.getInstance());
        addSeparator();
        add(ChangeImagePropertiesAction.getInstance());
    }

    public static ContextMenu getInstance() {
        instance.updateUI();
        return instance;
    }
}
