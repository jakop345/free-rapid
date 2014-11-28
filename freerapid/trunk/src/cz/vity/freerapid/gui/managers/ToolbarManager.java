package cz.vity.freerapid.gui.managers;

import com.jgoodies.binding.adapter.Bindings;
import com.jgoodies.binding.beans.PropertyConnector;
import com.jgoodies.binding.value.ValueModel;
import cz.vity.freerapid.core.AppPrefs;
import cz.vity.freerapid.core.UserProp;
import cz.vity.freerapid.gui.SearchField;
import cz.vity.freerapid.gui.actions.DownloadsActions;
import cz.vity.freerapid.gui.dialogs.CompoundUndoManager;
import cz.vity.freerapid.gui.managers.search.SearchItem;
import cz.vity.freerapid.plugins.webclient.ConnectionSettings;
import cz.vity.freerapid.swing.SwingUtils;
import cz.vity.freerapid.swing.Swinger;
import cz.vity.freerapid.swing.ToolbarSeparator;
import cz.vity.freerapid.swing.binding.BindUtils;
import org.jdesktop.application.ApplicationContext;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;

/**
 * Sprava toolbaru Vytvari a ovlada cely toolbar
 *
 * @author Vity
 */
public class ToolbarManager implements PropertyChangeListener {
    /**
     * hlavni panel v kterem je toolbar umisten
     */
    private final JPanel toolbarPanel = new JPanel(new BorderLayout());
    /**
     * button dimension with text
     */
    private final static Dimension buttonDimensionWithText = new Dimension(74, 68);
    /**
     * button dimension without text
     */
    private final static Dimension buttonWithoutWithoutTextDimension = new Dimension(40, 38);
    /**
     * velikost mezery mezi buttony ruzneho typu
     */
    //private static final int STRUT_SIZE = 8;

    /**
     * samotny toolbar
     */
    private JToolBar toolbar = new JToolBar("mainToolbar");
    //private JXBusyLabel labelWorkingProgress;

    private float fontSize;
    private SearchField searchField;
    private final ManagerDirector directorManager;
    private final ApplicationContext context;

    /**
     * Konstruktor - naplni toolbar buttony
     */

    public ToolbarManager(ManagerDirector directorManager, ApplicationContext context) {
        this.directorManager = directorManager;
        this.context = context;
        toolbarPanel.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 0));
        final Action action = context.getActionMap().get("showToolbar");

        final ValueModel valueModel = BindUtils.getPrefsValueModel(UserProp.SHOW_TOOLBAR, UserProp.SHOW_TOOLBAR_DEFAULT);
        action.putValue(Action.SELECTED_KEY, valueModel.getValue());
        PropertyConnector.connectAndUpdate(valueModel, toolbarPanel, "visible");


        fontSize = context.getResourceMap().getFloat("buttonBarFontSize");
        createToolbar();
    }

    public void propertyChange(PropertyChangeEvent evt) {
        final String propertyName = evt.getPropertyName();
        if ("done".equals(propertyName)) {
            //setWorkingProgress(false);
        } else if ("started".equals(propertyName)) {
            //setWorkingProgress(true);
        }
    }

    private JButton forceDownloadButton = new JButton();
    private JPopupMenu forceDownloadButtonMenu = new JPopupMenu();

    private void initForceDownloadButton() {
        final ForceDownloadButtonAction forceButtonAction = new ForceDownloadButtonAction(
                context.getResourceMap().getString("forceDownloadMenu.text"), 
                context.getResourceMap().getIcon("forceDownloadMenu_largeIcon"));
        forceDownloadButton.setAction(forceButtonAction);
        setButtonProperties(forceDownloadButton, forceButtonAction);

        directorManager.getContentManager().getContentPanel().addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                forceDownloadButton.setEnabled(getForceEnabled());
                if (!forceDownloadButton.isEnabled()) forceDownloadButtonMenu.setVisible(false);
            }
        } );
        forceDownloadButton.setEnabled(false);
    }

    private boolean getForceEnabled() {
        return directorManager.getContentManager().getContentPanel().isSelectedEnabled() &&
                directorManager.getDataManager().hasDownloadFilesStates(directorManager.getContentManager().getContentPanel().getSelectedRows(), DownloadsActions.forceEnabledStates);
    }

    private void updateForceDownloadButtonMenu() {
        boolean forceEnabled = getForceEnabled();
        forceDownloadButton.setEnabled(forceEnabled);
        if (forceEnabled) {
            forceDownloadButtonMenu.removeAll();
            final java.util.List<ConnectionSettings> connectionSettingses = directorManager.getClientManager().getAvailableConnections();
            boolean anyEnabled = false;
            for (ConnectionSettings settings : connectionSettingses) {
                final ForceDownloadOptionAction action = new ForceDownloadOptionAction(settings);
                forceDownloadButtonMenu.add(action);
                action.setEnabled(forceEnabled);
                if (settings.isEnabled())
                    anyEnabled = true;
            }
            forceDownloadButton.setEnabled(anyEnabled);
        }
    }

    private class ForceDownloadButtonAction extends AbstractAction {
        public ForceDownloadButtonAction(String name, Icon largeIcon) {
            this.putValue(Action.NAME, name.replace("&", ""));
            this.putValue(Action.SHORT_DESCRIPTION, name.replace("&", ""));
            this.putValue(Action.LARGE_ICON_KEY, largeIcon);
        }

        public void actionPerformed(ActionEvent e) {
            updateForceDownloadButtonMenu();
            if (forceDownloadButton.isEnabled())
                forceDownloadButtonMenu.show((JComponent) e.getSource(), forceDownloadButtonMenu.getX(), forceDownloadButtonMenu.getY() + (int)(forceDownloadButton.getPreferredSize().getHeight()*2/3));
        }
    }

    private class ForceDownloadOptionAction extends AbstractAction {
        private final ConnectionSettings settings;

        public ForceDownloadOptionAction(ConnectionSettings settings) {
            this.settings = settings;
            this.putValue(NAME, settings.toString());
        }

        public void actionPerformed(ActionEvent e) {
            directorManager.getDataManager().forceDownload(settings, directorManager.getContentManager().getContentPanel().getSelectedRows());
        }
    }

    private void addToolbarButtons() {
        final String customButtons = AppPrefs.getProperty(UserProp.CUSTOM_TOOLBAR_BUTTONS, UserProp.CUSTOM_TOOLBAR_BUTTONS_DEFAULT);
        for (char button : customButtons.toUpperCase().toCharArray()) {
            switch (button) {
                case '|' :
                case '-' :
                case '_' :
                    toolbar.add(new ToolbarSeparator());
                    break;
                case 'A' :
                    toolbar.add(getButton(Swinger.getAction("addNewLinksAction")));
                    break;
                case 'B' :
                    toolbar.add(getButton(Swinger.getAction("resumeAction")));
                    break;
                case 'C' :
                    toolbar.add(getButton(Swinger.getAction("pauseAction")));
                    break;
                case 'D' :
                    toolbar.add(getButton(Swinger.getAction("cancelAction")));
                    break;
                case 'E' :
                    toolbar.add(getButton(Swinger.getAction("topAction")));
                    break;
                case 'F' :
                    toolbar.add(getButton(Swinger.getAction("upAction")));
                    break;
                case 'G' :
                    toolbar.add(getButton(Swinger.getAction("downAction")));
                    break;
                case 'H' :
                    toolbar.add(getButton(Swinger.getAction("bottomAction")));
                    break;
                case 'I' :
                    toolbar.add(getButton(Swinger.getAction("downloadInformationAction")));
                    break;
                case 'J' :
                    toolbar.add(getButton(Swinger.getAction("copyContent")));
                    break;
                case 'K' :
                    toolbar.add(getButton(Swinger.getAction("openLogFile")));
                    break;
                case 'L' :
                    toolbar.add(getButton(Swinger.getAction("browseToLogFile")));
                    break;
                case 'M' :
                    toolbar.add(getButton(Swinger.getAction("checkForNewPlugins")));
                    break;
                case 'N' :
                    toolbar.add(getButton(Swinger.getAction("checkForNewVersion")));
                    break;
                case 'O' :
                    toolbar.add(getButton(Swinger.getAction("openInBrowser")));
                    break;
                case 'P' :
                    toolbar.add(getButton(Swinger.getAction("options")));
                    break;
                case 'Q' :
                    toolbar.add(getButton(Swinger.getAction("showDownloadHistoryAction")));
                    break;
                case 'R' :
                    toolbar.add(getButton(Swinger.getAction("removeCompletedAction")));
                    break;
                case 'S' :
                    toolbar.add(getButton(Swinger.getAction("removeCompletedAndDeletedAction")));
                    break;
                case 'T' :
                    toolbar.add(getButton(Swinger.getAction("removeInvalidLinksAction")));
                    break;
                case 'U' :
                    toolbar.add(getButton(Swinger.getAction("removeSelectedAction")));
                    break;
                case 'V' :
                    toolbar.add(getButton(Swinger.getAction("validateLinksAction")));
                    break;
                case 'W' :
                    initForceDownloadButton();
                    toolbar.add(forceDownloadButton);
                    break;
                case 'X' :
                    toolbar.add(getButton(Swinger.getAction("retryAllErrorAction")));
                    break;
                case 'Y' :
                    toolbar.add(getButton(Swinger.getAction("selectAllAction")));
                    break;
                case 'Z' :
                    toolbar.add(getButton(Swinger.getAction("invertSelectionAction")));
                    break;
                default:
                    break;
            }
        }
    }

    private void createToolbar() {
        toolbarPanel.add(toolbar);
        toolbar.setFocusable(false);
        toolbar.setFloatable(false);
        final Border border = toolbar.getBorder();
        Border innerBorder = BorderFactory.createEmptyBorder(2, 2, 1, 2);
        if (border != null)
            toolbar.setBorder(BorderFactory.createCompoundBorder(border, innerBorder));
        else
            toolbar.setBorder(innerBorder);
        addToolbarButtons();
        toolbar.add(Box.createGlue());
        searchField = new SearchField(context);
        new CompoundUndoManager(searchField);
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    final SearchItem searchItem = searchField.getSelectedItem();
                    if (searchItem != null) {
                        directorManager.getSearchManager().openBrowser(searchItem, searchField.getText());
                        Swinger.inputFocus(searchField);
                    }
                }
            }
        });

        ValueModel valueModel = BindUtils.getPrefsValueModel(UserProp.SEARCH_FIELD_TEXT, "");
        Bindings.bind(searchField, valueModel, false);
        //PropertyConnector.connectAndUpdate(valueModel, searchField, "text");

        valueModel = BindUtils.getPrefsValueModel(UserProp.SEARCH_FIELD_VISIBLE, UserProp.SEARCH_FIELD_VISIBLE_DEFAULT);
        PropertyConnector.connectAndUpdate(valueModel, searchField, "visible");

        //if (!searchField.getSearchItemList().isEmpty())
        searchField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (directorManager.getSearchManager().checkForDirChange()) {
                    directorManager.getSearchManager().loadSearchData();
                    searchField.setSearchItemList(directorManager.getSearchManager().getSearchItems());
                }
            }
        });

        toolbar.add(searchField);
        toolbar.add(Box.createHorizontalStrut(3));
//        toolbar.add(new ToolbarSeparator());
//        toolbar.add(getButton(Swinger.getAction("quit")));
        //    toolbar.add(Box.createGlue());
        AbstractButton btn = getButton(Swinger.getAction("paypalSupportAction"));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.putClientProperty("noChange", true);
        btn.setOpaque(false);
        btn.setRolloverEnabled(false);
        btn.setBackground(null);
        btn.setText(null);
        btn.setBorder(new EmptyBorder(0, 0, 0, 0));
        if (AppPrefs.getProperty(UserProp.SHOW_PAYPAL, UserProp.SHOW_PAYPAL_DEFAULT)) {
            toolbar.add(btn);
        }
        toolbar.add(Box.createHorizontalStrut(18));


        updateButtons(AppPrefs.getProperty(UserProp.SHOW_TEXT_TOOLBAR, UserProp.SHOW_TEXT_TOOLBAR_DEFAULT));


        checkPreferences();

//        this.labelWorkingProgress = new JXBusyLabel();
//        this.labelWorkingProgress.setName("labelWorkingProgress");
//        labelWorkingProgress.setBorder(BorderFactory.createEmptyBorder(2, 10, 2, 10));
//        setWorkingProgress(false);
//        toolbar.add(labelWorkingProgress);
    }

    void initManager() {
        searchField.setSearchItemList(directorManager.getSearchManager().getSearchItems());
    }

    private void checkPreferences() {
        AppPrefs.getPreferences().addPreferenceChangeListener(new PreferenceChangeListener() {
            public void preferenceChange(final PreferenceChangeEvent evt) {
                if (UserProp.SHOW_TEXT_TOOLBAR.equals(evt.getKey())) {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            updateButtons(Boolean.valueOf(evt.getNewValue()));
                        }
                    });
                }
            }
        });
    }

    private void updateButtons(boolean withText) {
        final Component[] components = toolbar.getComponents();
        Dimension dimension;

        if (withText) {
            toolbarPanel.setPreferredSize(new Dimension(400, 54));
            dimension = buttonDimensionWithText;
        } else {
            dimension = buttonWithoutWithoutTextDimension;
            toolbarPanel.setPreferredSize(new Dimension(400, 47));
        }
        for (Component c : components) {
            if (c instanceof AbstractButton) {
                AbstractButton button = (AbstractButton) c;
                if (button.getClientProperty("noChange") != null)
                    continue;
                button.setMinimumSize(dimension);
                button.setPreferredSize(dimension);
                button.setMaximumSize(dimension);
                if (withText) {
                    updateButtonText(button, button.getAction());
                } else {
                    button.setText(null);
                }

            }
        }
        toolbar.getParent().validate();
        toolbar.getParent().repaint();
    }


    private void setToolBarVisible(boolean visible) {
        toolbarPanel.setVisible(visible);
        //toolbar.setVisible(visible);
        //  AppPrefs.storeProperty(AppPrefs.SHOW_TOOLBAR, visible); //ulozeni uzivatelskeho nastaveni, ale jen do hashmapy
    }

    /**
     * Vraci hlavni panel toolbaru jako komponentu
     *
     * @return komponenta toolbar
     */
    public JComponent getComponent() {
        return toolbarPanel;
    }

    private AbstractButton getToggleButton(final Action action) {
        final JToggleButton button = new JToggleButton(action);
        return setButtonProperties(button, action);
    }

    private AbstractButton getButton(final Action action) {
        final JButton button = new JButton(action);
        return setButtonProperties(button, action);
    }


    private AbstractButton setButtonProperties(AbstractButton button, Action action) {
        button.setRolloverEnabled(true);
        button.setIconTextGap(0);
        final Object desc = action.getValue(Action.SHORT_DESCRIPTION);
        //updateButtonText(button, action);
        final Font font = button.getFont();
        button.setFont(font.deriveFont(fontSize));
        button.setVerticalTextPosition(JButton.BOTTOM);
        button.setHorizontalTextPosition(JButton.CENTER);
        button.setText(null);

        final Object keystroke = action.getValue(Action.ACCELERATOR_KEY);
        if (desc != null && keystroke != null) {
            button.setToolTipText(desc.toString() + " (" + SwingUtils.keyStroke2String((KeyStroke) keystroke) + ")");
        }

        button.setMnemonic(0);
        button.setFocusable(false);
        return button;
    }

    private void updateButtonText(AbstractButton button, Action action) {
        String s = (String) action.getValue(Action.NAME);
        if (s != null && s.endsWith("..."))
            s = s.substring(0, s.length() - 3);
        button.setText(s);
    }

//    private void setWorkingProgress(final boolean enabled) {
//        final JXFrame jxFrame = (JXFrame) (MainApp.getInstance(MainApp.class).getMainFrame());
//        jxFrame.setWaiting(enabled);
//        //labelWorkingProgress.setBusy(enabled);
//    }


    public SearchField getSearchField() {
        return searchField;
    }
}
