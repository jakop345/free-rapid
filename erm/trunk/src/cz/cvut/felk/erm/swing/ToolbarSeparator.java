package cz.cvut.felk.erm.swing;

import javax.swing.*;

/**
 * Toolbar Separator UI
 * @author Ladislav Vitasek
 */
public final class ToolbarSeparator extends JToolBar.Separator {
    private static final String uiClassID = "ToolbarSeparatorUI";

    static {
        UIManager.getDefaults().put("ToolbarSeparatorUI", ToolbarSeparatorUI.class.getName());
    }

    public final String getUIClassID() {
        return uiClassID;
    }

    public final void updateUI() {
        this.setUI((ToolbarSeparatorUI) UIManager.getUI(this));
    }
}
