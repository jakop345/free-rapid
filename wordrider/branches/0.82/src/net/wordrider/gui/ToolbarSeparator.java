package net.wordrider.gui;

import javax.swing.*;

public final class ToolbarSeparator extends JToolBar.Separator {
    private static final String uiClassID = "ToolbarSeparatorUI";

    static {
        UIManager.getDefaults().put("ToolbarSeparatorUI", "net.wordrider.gui.ToolbarSeparatorUI");
    }

    public final String getUIClassID() {
        return uiClassID;
    }

    public final void updateUI() {
        this.setUI((ToolbarSeparatorUI) UIManager.getUI(this));
    }
}
