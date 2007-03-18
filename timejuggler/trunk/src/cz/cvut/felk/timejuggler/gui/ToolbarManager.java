package cz.cvut.felk.timejuggler.gui;

import cz.cvut.felk.timejuggler.swing.ToolbarSeparator;
import cz.cvut.felk.timejuggler.core.AppPrefs;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

import application.ApplicationContext;

/**
 * Sprava toolbaru
 * Vytvari a ovlada cely toolbar
 * @author Vity
 */
public class ToolbarManager  {
    /**
     * hlavni panel v kterem je toolbar umisten
     */
    private final JPanel toolbarPanel = new JPanel(new BorderLayout());
    /**
     * preferovana velikost buttonu v toolbaru
     */
    private final static Dimension buttonDimension = new Dimension(47, 45);
    /**
     * velikost mezery mezi buttony ruzneho typu
     */
    private static final int STRUT_SIZE = 8;

    /**
     * samotny toolbar
     */
    private JToolBar toolbar = new JToolBar("Main Toolbar");

    /**
     * Konstruktor - naplni toolbar buttony
     */
    public ToolbarManager() {
        toolbarPanel.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 0));
        final Action action = ApplicationContext.getInstance().getActionMap().get("showToolbar");
        //odchyt udalosti z akce pro zmenu viditelnosti toolbaru
        action.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (Action.SELECTED_KEY.equals(evt.getPropertyName())) {
                    setToolBarVisible((Boolean) evt.getNewValue());
                }
            }
        });
        action.putValue(Action.SELECTED_KEY, AppPrefs.getProperty(AppPrefs.SHOW_TOOLBAR, true));
        createToolbar();
    }

    private void createToolbar() {
        toolbarPanel.add(toolbar);
        toolbarPanel.setPreferredSize(new Dimension(400, 56));
        toolbar.setFocusable(false);
        toolbar.setFloatable(false);
        toolbar.add(getButton(MenuManager.getAction("newEvent")));
        toolbar.add(getButton(MenuManager.getAction("newTask")));
        toolbar.add(Box.createHorizontalStrut(STRUT_SIZE));
        toolbar.add(getButton(MenuManager.getAction("editEventOrTask")));
        toolbar.add(getButton(MenuManager.getAction("deleteEventOrTask")));
        toolbar.add(new ToolbarSeparator());
        toolbar.add(getButton(MenuManager.getAction("goToday")));
        toolbar.add(new ToolbarSeparator());
        AbstractButton comp = getToggleButton(MenuManager.getAction("dayView"));
        final ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(comp);
        toolbar.add(comp);
        comp = getToggleButton(MenuManager.getAction("weekView"));
        buttonGroup.add(comp);
        toolbar.add(comp);
        comp = getToggleButton(MenuManager.getAction("multiWeekView"));
        buttonGroup.add(comp);
        toolbar.add(comp);
        comp = getToggleButton(MenuManager.getAction("monthView"));
        buttonGroup.add(comp);
        toolbar.add(comp);
    }

    /**
     * Vraci hlavni panel toolbaru jako komponentu
     * @return komponenta toolbar
     */
    public JComponent getComponent() {
        return toolbarPanel;
    }

    private static AbstractButton getToggleButton(final Action action) {
        final JToggleButton button = new JToggleButton(action);
        return setButtonProperties(button);
    }

    private static AbstractButton getButton(final Action action) {
        final JButton button = new JButton(action);
        return setButtonProperties(button);
    }

    private static AbstractButton setButtonProperties(AbstractButton button) {
//        final String text = button.getText();
//        if (text.endsWith("...")) { //little hack :-)
//            button.setText(text.substring(0, text.length() - 3));
//        }         
        button.setRolloverEnabled(true);
        button.setIconTextGap(-2);
        button.setVerticalTextPosition(JButton.BOTTOM);
        button.setHorizontalTextPosition(JButton.CENTER);
        button.setMinimumSize(buttonDimension);
        button.setPreferredSize(buttonDimension);
//        button.setMaximumSize(buttonDimension);
        button.setMnemonic(0);
        button.setFocusable(false);
        return button;
    }


    private void setToolBarVisible(boolean visible) {
        toolbarPanel.setVisible(visible);
        AppPrefs.storeProperty(AppPrefs.SHOW_TOOLBAR, visible); //ulozeni uzivatelskeho nastaveni, ale jen do hashmapy        
    }
}
