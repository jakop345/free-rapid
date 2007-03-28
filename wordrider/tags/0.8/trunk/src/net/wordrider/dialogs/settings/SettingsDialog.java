package net.wordrider.dialogs.settings;

import net.wordrider.core.Lng;
import net.wordrider.dialogs.AppDialog;
import net.wordrider.utilities.LogUtils;
import net.wordrider.utilities.Swinger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.util.logging.Logger;

/**
 * @author Vity
 */
@SuppressWarnings({"FieldCanBeLocal"})
public final class SettingsDialog extends AppDialog {
    //    private JSpinner recentMaxFileCount;
    private final static Dimension buttonDimension = new Dimension(145, 40);
    private ButtonPanel buttonPanel = null;
    private SelectablePanel selectablePanel;
    private static final int PANEL_COLORS = 0;
    private static final int PANEL_GENERAL = 1;
    private static final int PANEL_EDITOR = 2;
    private static final int PANEL_MISC = 3;
    private static final int PANEL_APPEARANCE = 4;
    private static final int PANEL_SENDMETHOD = 5;
    private JPanel panelGeneralSettings = null;
    private JPanel panelEditorSettings = null;
    private JPanel panelColorSettings = null;
    private JPanel panelMiscSettings = null;
    private JPanel panelSendMethod = null;
    private JPanel panelAppearanceSettings = null;
    private AbstractButton toolbarBtnColorPanel, toolbarBtnGeneralPanel, toolbarBtnEditorPanel, toolbarBtnMiscPanel, toolbarBtnSendMethodPanel, toolbarBtnAppPanel;
    private final static Logger logger = Logger.getLogger(SettingsDialog.class.getName());

    public SettingsDialog(final Frame owner) {
        this(owner, false);
    }

    public SettingsDialog(final Frame owner, final boolean showSelectSendMethodPanel) {
        super(owner, true);
        try {
            init(showSelectSendMethodPanel);
        } catch (Exception e) {
            LogUtils.processException(logger, e);
        }
        this.pack();
        Swinger.centerDialog(owner, this);
        this.setModal(true);
        this.setTitle(Lng.getLabel("settings.title"));
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setVisible(true);
    }

    private static AbstractButton getButton(final Action action) {
        final JToggleButton button = new JToggleButton(action);
        //button.setText("");
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setMinimumSize(buttonDimension);
        button.setPreferredSize(buttonDimension);
        //button.setMaximumSize(buttonDimension);
        button.setRolloverEnabled(true);
        button.setSize(buttonDimension);
        button.setFocusable(true);
        return button;
    }

    protected final AbstractButton getCancelButton() {
        return getButtonPanel().getCancelButton();
    }

    protected final AbstractButton getOkButton() {
        return getButtonPanel().getOkButton();
    }

    private JToolBar getToolBar() {
        final JToolBar toolbar = new JToolBar(JToolBar.VERTICAL);
        toolbar.setLayout(new GridBagLayout());
        toolbar.setPreferredSize(new Dimension(160, 285));

        toolbar.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3), BorderFactory.createLineBorder(Color.BLACK, 1)), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        toolbar.setFloatable(false);
        final ButtonGroup group = new ButtonGroup();
        group.add(toolbarBtnColorPanel = getButton(new SelectSettingsClickAction(PANEL_COLORS, "settings.btn.colors", null)));
        group.add(toolbarBtnGeneralPanel = getButton(new SelectSettingsClickAction(PANEL_GENERAL, "settings.btn.general", null)));
        group.add(toolbarBtnEditorPanel = getButton(new SelectSettingsClickAction(PANEL_EDITOR, "settings.btn.editor", null)));
        group.add(toolbarBtnSendMethodPanel = getButton(new SelectSettingsClickAction(PANEL_SENDMETHOD, "settings.btn.sendMethod", null)));
        group.add(toolbarBtnMiscPanel = getButton(new SelectSettingsClickAction(PANEL_MISC, "settings.btn.misc", null)));
        group.add(toolbarBtnAppPanel = getButton(new SelectSettingsClickAction(PANEL_APPEARANCE, "settings.btn.appearance", null)));
        toolbarBtnGeneralPanel.setIcon(Swinger.getIcon("general.gif"));
        toolbarBtnEditorPanel.setIcon(Swinger.getIcon("editor.gif"));
        toolbarBtnSendMethodPanel.setIcon(Swinger.getIcon("sending_big.gif"));
        toolbarBtnColorPanel.setIcon(Swinger.getIcon("colors.gif"));
        toolbarBtnMiscPanel.setIcon(Swinger.getIcon("miscellanous.gif"));
        toolbarBtnAppPanel.setIcon(Swinger.getIcon("look-feel.gif"));
        toolbar.add(toolbarBtnGeneralPanel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
                , GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        toolbar.add(toolbarBtnColorPanel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
                , GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(4, 0, 0, 0), 0, 0));
        toolbar.add(toolbarBtnAppPanel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
                , GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(4, 0, 0, 0), 0, 0));
        toolbar.add(toolbarBtnEditorPanel, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
                , GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(4, 0, 0, 0), 0, 0));
        toolbar.add(toolbarBtnSendMethodPanel, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0
                , GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(4, 0, 0, 0), 0, 0));
        toolbar.add(toolbarBtnMiscPanel, new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0
                , GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(4, 0, 0, 0), 0, 0));
        toolbar.add(Box.createVerticalStrut(4), new GridBagConstraints(0, 5, 1, 1, 0.0, 1.0
                , GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 150, 0), 0, 0));
        return toolbar;
    }

    public final ButtonPanel getButtonPanel() {
        return (buttonPanel == null) ? buttonPanel = new ButtonPanel(this) : buttonPanel;
    }

    private JPanel getColorSettingsPanel() {
        return (panelColorSettings == null) ? panelColorSettings = new ColorSettingsPanel(this, Lng.getLabel("settings.colors.title")) : panelColorSettings;
    }

    private JPanel getAppearanceSettingsPanel() {
        return (panelAppearanceSettings == null) ? panelAppearanceSettings = new AppearanceSettingsPanel(this, Lng.getLabel("settings.appearance.title")) : panelAppearanceSettings;
    }

    private JPanel getGeneralSettingsPanel() {
        return (panelGeneralSettings == null) ? panelGeneralSettings = new GeneralSettingsPanel(this, Lng.getLabel("settings.general.title")) : panelGeneralSettings;
    }

    private JPanel getEditorSettingsPanel() {
        return (panelEditorSettings == null) ? panelEditorSettings = new EditorSettingsPanel(this, Lng.getLabel("settings.editor.title")) : panelEditorSettings;
    }

    private JPanel getSendMethodPanelPanel() {
        return (panelSendMethod == null) ? panelSendMethod = new SendMethodSettingsPanel(this, Lng.getLabel("settings.sendMethod.title")) : panelSendMethod;
    }

    private JPanel getMiscSettingsPanel() {
        return (panelMiscSettings == null) ? panelMiscSettings = new MiscSettingsPanel(this, Lng.getLabel("settings.misc.title")) : panelMiscSettings;
    }


    private void init(boolean showSelectSendMethodPanel) {
        final Container mainPanel = this.getContentPane();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(getToolBar(), BorderLayout.WEST);
        final JPanel rightPanel = new JPanel(new BorderLayout());
        selectablePanel = new SelectablePanel();
        rightPanel.add(selectablePanel, BorderLayout.CENTER);
        rightPanel.add(getButtonPanel(), BorderLayout.SOUTH);
        mainPanel.add(rightPanel, BorderLayout.CENTER);
        if (!showSelectSendMethodPanel) {
            selectablePanel.showCard(getGeneralSettingsPanel());
            toolbarBtnGeneralPanel.setSelected(true);
        } else {
            selectablePanel.showCard(getSendMethodPanelPanel());
            toolbarBtnSendMethodPanel.setSelected(true);
            Swinger.inputFocus(toolbarBtnSendMethodPanel);
        }
    }

    public final SettingsPanel getActiveSettingsPanel() {
        return (SettingsPanel) selectablePanel.getActiveCard();
    }

    private final class SelectSettingsClickAction extends AbstractAction {
        private final int actionCode;


        public SelectSettingsClickAction(final int actionCode, final String labelCode, final String smallIcon) {
            super(labelCode);
            this.actionCode = actionCode;
            putValue(Action.NAME, Lng.getLabel(labelCode));
            //putValue(Action.SHORT_DESCRIPTION, Lng.getLabel(labelCode));
            putValue(Action.MNEMONIC_KEY, new Integer(Lng.getMnemonic(labelCode)));
            if (smallIcon != null)
                putValue(Action.SMALL_ICON, Swinger.getIcon(smallIcon));
        }

        public final void actionPerformed(final ActionEvent e) {
            final IOptionsManager manager = getActiveSettingsPanel().getOptionsManager();
            final JFrame frame = (JFrame) SettingsDialog.this.getParent();
            if (manager.wasChanged()) {
                final int result = Swinger.getChoice(frame, Lng.getLabel("settings.confirm"));
                if (result == Swinger.RESULT_YES) {
                    manager.applyChanges();
                } else manager.restoreChanged();
            }
            switch (actionCode) {
                case PANEL_GENERAL:
                    selectablePanel.showCard(getGeneralSettingsPanel());
                    break;
                case PANEL_COLORS:
                    selectablePanel.showCard(getColorSettingsPanel());
                    break;
                case PANEL_APPEARANCE:
                    selectablePanel.showCard(getAppearanceSettingsPanel());
                    break;
                case PANEL_EDITOR:
                    selectablePanel.showCard(getEditorSettingsPanel());
                    break;
                case PANEL_SENDMETHOD:
                    selectablePanel.showCard(getSendMethodPanelPanel());
                    break;
                case PANEL_MISC:
                    selectablePanel.showCard(getMiscSettingsPanel());
                    break;
                default:
                    return;
            }
            getActiveSettingsPanel().getOptionsManager().resetChanges();
            getActiveSettingsPanel().focusFirstComponent();
            //  SettingsDialog.this.validate();
            updateSize();
        }
    }


    private void updateSize() {
        //Dimension size = this.getPreferredSize();
        //this.setSize(size.width, this.getSize().height);
        this.pack();
    }

    protected final void processWindowEvent(final WindowEvent e) {
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            getButtonPanel().getCancelButton().getAction().actionPerformed(null);
        }
    }

}