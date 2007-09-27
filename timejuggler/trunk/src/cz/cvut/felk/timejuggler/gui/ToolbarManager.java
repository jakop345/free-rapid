package cz.cvut.felk.timejuggler.gui;

import application.Application;
import application.ApplicationContext;
import application.ResourceMap;
import application.TaskMonitor;
import cz.cvut.felk.timejuggler.swing.Swinger;
import cz.cvut.felk.timejuggler.swing.ToolbarSeparator;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Sprava toolbaru Vytvari a ovlada cely toolbar
 * @author Vity
 */
public class ToolbarManager {
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
    private JToolBar toolbar = new JToolBar("mainToolbar");
    private JLabel labelWorkingProgress;

    /**
     * Konstruktor - naplni toolbar buttony
     */

    public ToolbarManager(ApplicationContext context) {
        toolbarPanel.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 0));
        final Action action = context.getActionMap().get("showToolbar");
        action.putValue(Action.SELECTED_KEY, true); //defaultni hodnota
        //odchyt udalosti z akce pro zmenu viditelnosti toolbaru
        action.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (Action.SELECTED_KEY.equals(evt.getPropertyName())) {
                    setToolBarVisible((Boolean) evt.getNewValue());
                }
            }
        });
        createToolbar();
        final TaskMonitor taskMonitor = Application.getInstance().getContext().getTaskMonitor();
        taskMonitor.addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                final String propertyName = evt.getPropertyName();
                if ("done".equals(propertyName)) {
                    setWorkingProgress(false);
                } else if ("started".equals(propertyName)) {
                    setWorkingProgress(true);
                }
            }
        });
    }

    private void createToolbar() {
        toolbarPanel.add(toolbar);
        toolbarPanel.setPreferredSize(new Dimension(400, 56));
        toolbar.setFocusable(false);
        toolbar.setFloatable(false);
        toolbar.add(getButton(Swinger.getAction("newEvent")));
        toolbar.add(getButton(Swinger.getAction("newTask")));
        toolbar.add(Box.createHorizontalStrut(STRUT_SIZE));
        toolbar.add(getButton(Swinger.getAction("editEventOrTask")));
        toolbar.add(getButton(Swinger.getAction("deleteEventOrTask")));
        toolbar.add(new ToolbarSeparator());
        toolbar.add(getButton(Swinger.getAction("goToday")));
        toolbar.add(new ToolbarSeparator());
        AbstractButton comp = getToggleButton(Swinger.getAction("dayView"));
        final ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(comp);
        toolbar.add(comp);
        comp = getToggleButton(Swinger.getAction("weekView"));
        buttonGroup.add(comp);
        toolbar.add(comp);
        comp = getToggleButton(Swinger.getAction("multiWeekView"));
        buttonGroup.add(comp);
        toolbar.add(comp);
        comp = getToggleButton(Swinger.getAction("monthView"));
        buttonGroup.add(comp);
        toolbar.add(comp);
        toolbar.add(Box.createGlue());
        this.labelWorkingProgress = new JLabel();
        this.labelWorkingProgress.setName("labelWorkingProgress");
        labelWorkingProgress.setBorder(BorderFactory.createEmptyBorder(2, 10, 2, 10));
        setWorkingProgress(false);
        toolbar.add(labelWorkingProgress);
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
        //button.setPreferredSize(buttonDimension);
//        button.setMaximumSize(buttonDimension);
        button.setMnemonic(0);
        button.setFocusable(false);
        return button;
    }


    private void setWorkingProgress(final boolean enabled) {
        final ResourceMap map = Application.getInstance().getContext().getResourceMap();
        final String icon = (enabled) ? "iconWorking" : "iconNotWorking";
        labelWorkingProgress.setIcon(map.getIcon(icon));
        labelWorkingProgress.setEnabled(false);
    }

    private void setToolBarVisible(boolean visible) {
        toolbarPanel.setVisible(visible);
        //toolbar.setVisible(visible);
        //  AppPrefs.storeProperty(AppPrefs.SHOW_TOOLBAR, visible); //ulozeni uzivatelskeho nastaveni, ale jen do hashmapy
    }
}
