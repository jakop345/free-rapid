package net.wordrider.core.managers;

import net.wordrider.core.managers.interfaces.IInformedTab;
import net.wordrider.core.managers.interfaces.IRiderManager;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.Collection;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Map;

/**
 * @author Vity
 */
@SuppressWarnings({"SuspiciousNameCombination"})
abstract class TabManager<C extends IInformedTab> implements IRiderManager {
    final JTabbedPane tabbedPane;

    private static int anIDCounter;
    /**
     * LASTOPENFOLDER_KEY - component value - id *
     */
    final Map<Component, Object> runningTabs = new Hashtable<Component, Object>(4);
    /**
     * LASTOPENFOLDER_KEY - id value - fileinstance *
     */
    final Map<Object, C> runningInstancesIDs = new Hashtable<Object, C>(4);
    Object activeInstanceID;

    public TabManager() {
        this(JTabbedPane.TOP);
    }

    public TabManager(final int tabPlacement) {
        tabbedPane = createTabbedPane(tabPlacement);
        //InputMap map = tabbedPane.getInputMap();
        //tabbedPane.setInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW, map);
        //tabbedPane.setFocusable(false);
        //tabbedPane.setF
        //   tabbedPane.setMinimumSize(new Dimension(200,50));
        tabbedPane.getModel().addChangeListener(new TabListener());
        tabbedPane.setOpaque(false);
        //tabbedPane.setVisible(false);
    }

    public final C getActiveInstance() {
        return (activeInstanceID != null) ? runningInstancesIDs.get(activeInstanceID) : null;
    }

    public final Component getManagerComponent() {
        return tabbedPane;
    }

    final Integer registerNewOne(final C informedPlugin) {
        return registerNewOne(informedPlugin, true);
    }

    final Integer registerNewOne(final C informedPlugin, final boolean activate) {
        final Integer id = nextID();
        runningInstancesIDs.put(id, informedPlugin);
        final Component managerComponent = informedPlugin.getComponent();
        runningTabs.put(managerComponent, id);
        addTab(informedPlugin, managerComponent);
        if (activate)
            tabbedPane.setSelectedComponent(managerComponent);
        return id;
    }

    private void addTab(final C informedPlugin, final Component managerComponent) {
        final int tabPlacement = tabbedPane.getTabPlacement();
        switch (tabPlacement) {
            case JTabbedPane.LEFT:
            case JTabbedPane.RIGHT:
                tabbedPane.addTab(null, new VerticalTextIcon(informedPlugin.getTabName(), informedPlugin.getIcon(), tabPlacement == JTabbedPane.RIGHT), managerComponent, informedPlugin.getTip());
                return;
            default:
                tabbedPane.addTab(informedPlugin.getTabName(), informedPlugin.getIcon(), managerComponent, informedPlugin.getTip());
        }
    }

    void deactivateInstance(final Object anID) {
        runningInstancesIDs.get(anID).deactivate();
    }

    public void lookAndFeelChanged() {
        final Object textIconGap = UIManager.get("TabbedPane.textIconGap");
        final Insets tabInsets = UIManager.getInsets("TabbedPane.tabInsets");
        UIManager.put("TabbedPane.textIconGap", 1);
        //System.out.println("insets2" + tabInsets);
        UIManager.put("TabbedPane.tabInsets", new Insets(tabInsets.left, tabInsets.top, tabInsets.right, tabInsets.bottom));
        //UIManager.put("TabbedPane.tabInsets", new Insets(1, tabInsets.top, 1, tabInsets.bottom));
        tabbedPane.updateUI();
        //LookAndFeels.updateComponentTreeUI(tabbedPane);
        UIManager.put("TabbedPane.textIconGap", textIconGap);
        UIManager.put("TabbedPane.tabInsets", tabInsets);
    }

    Collection<C> runningInstances() {
        return runningInstancesIDs.values();
    }

    public final Collection<C> getOpenedInstances() {
        return new LinkedList<C>(runningInstances());
    }

    private final class TabListener implements ChangeListener {
        public final void stateChanged(final ChangeEvent e) {
            final Component selectedComponent = tabbedPane.getSelectedComponent();
            if (selectedComponent != null) {
                final Object anID = runningTabs.get(selectedComponent);
                if (anID != null && !anID.equals(activeInstanceID)) {
                    activateInstance(anID);
                } else {
                    if (activeInstanceID != null) {
                        deactivateInstance(activeInstanceID);
                        activeInstanceID = null;
                        //MainApp.getMainApp().getMainAppFrame().setTitle();
                    }
                }
            } else
                activeInstanceID = null;
        }
    }

    //    private void activateInstance(final Integer anID) {
    //        activateInstance(anID, getActiveInstance());
    //    }


    void activateInstance(final Object anID) {
        //deactivating active
        if (activeInstanceID != null && runningInstancesIDs.containsKey(activeInstanceID))
            deactivateInstance(activeInstanceID);
        //activating new
        activeInstanceID = anID;
        runningInstancesIDs.get(anID).activate();
    }
    //  MainApp.getMainApp().getMainAppFrame().setTitle();

    final void closeSoft(final Object anID, final boolean removeTab) {
        if (anID == null || !runningInstancesIDs.containsKey(anID))
            return;

        boolean result = false;
        try {
            result = runningInstancesIDs.get(anID).closeSoft();
        } catch (Throwable throwable) {
            System.err.println(throwable);
        }
        if (result && removeTab)
            removeInstance(anID);
    }

    final void closeHard(final Object anID) {
        if (anID == null || !runningInstancesIDs.containsKey(anID))
            return;
        removeInstance(anID);
    }

    private synchronized void removeInstance(final Object anID) {
        final IInformedTab informedTab =
                runningInstancesIDs.get(anID);
        runningTabs.remove(informedTab.getComponent());
        //order!
        if (runningTabs.isEmpty())
            deactivateInstance(anID);
        runningInstancesIDs.remove(anID);
        tabbedPane.remove(informedTab.getComponent());
        tabbedPane.validate();
        activeInstanceID = null;
        final Component selectedComponent = tabbedPane.getSelectedComponent();
        if (selectedComponent != null)
            activateInstance(runningTabs.get(selectedComponent));
    }

    private JTabbedPane createTabbedPane(final int tabPlacement) {
        switch (tabPlacement) {
            case JTabbedPane.LEFT:
            case JTabbedPane.RIGHT:
                final Object textIconGap = UIManager.get("TabbedPane.textIconGap");
                final Insets tabInsets = UIManager.getInsets("TabbedPane.tabInsets");
                UIManager.put("TabbedPane.textIconGap", 1);
                //System.out.println("insets" + tabInsets);
                UIManager.put("TabbedPane.tabInsets", new Insets(tabInsets.left, tabInsets.top, tabInsets.right, tabInsets.bottom));
                //UIManager.put("TabbedPane.tabInsets", new Insets(1, -5, 1, -5));
                final JTabbedPane tabPane = new JTabbedPane(tabPlacement, JTabbedPane.SCROLL_TAB_LAYOUT);
                UIManager.put("TabbedPane.textIconGap", textIconGap);
                UIManager.put("TabbedPane.tabInsets", tabInsets);
                return tabPane;
            default:
                return new JTabbedPane(tabPlacement, JTabbedPane.SCROLL_TAB_LAYOUT);
        }
    }

    public void closeSoftAllInstances(final boolean removeTabs) {
        for (Object o : new LinkedList<Object>(runningInstancesIDs.keySet())) {
            closeSoft(o, removeTabs);
        }
    }


    private static synchronized Integer nextID() {
        return ++anIDCounter;
    }

    public final void getPrevTab() {
        final int tabCount = runningTabs.size();
        if (tabCount > 1) {
            int selectedIndex = tabbedPane.getSelectedIndex();
            if (selectedIndex - 1 < 0)
                selectedIndex = tabCount;
            tabbedPane.setSelectedIndex(selectedIndex - 1);
        }
    }


    public final void getNextTab() {
        final int tabCount = runningTabs.size();
        if (tabCount > 1) {
            int selectedIndex = tabbedPane.getSelectedIndex();
            if (selectedIndex + 1 == tabCount)
                selectedIndex = -1;
            tabbedPane.setSelectedIndex(selectedIndex + 1);
        }
    }
}
