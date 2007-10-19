package cz.cvut.felk.timejuggler.gui;

import com.jgoodies.binding.adapter.Bindings;
import com.jgoodies.binding.adapter.SingleListSelectionAdapter;
import com.jgoodies.binding.beans.PropertyConnector;
import com.jgoodies.binding.list.ArrayListModel;
import com.jgoodies.binding.list.SelectionInList;
import cz.cvut.felk.timejuggler.core.MainApp;
import cz.cvut.felk.timejuggler.core.data.DataProvider;
import cz.cvut.felk.timejuggler.core.data.PersistencyLayerException;
import cz.cvut.felk.timejuggler.db.entity.interfaces.VCalendarEntity;
import cz.cvut.felk.timejuggler.swing.ComponentFactory;
import cz.cvut.felk.timejuggler.swing.CustomLayoutConstraints;
import cz.cvut.felk.timejuggler.swing.Swinger;
import cz.cvut.felk.timejuggler.swing.renderers.CheckRenderer;
import cz.cvut.felk.timejuggler.utilities.LogUtils;
import info.clearthought.layout.TableLayout;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.swingx.JXList;
import org.jdesktop.swingx.calendar.JXMonthView;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Comparator;
import java.util.Date;
import java.util.logging.Logger;

/**
 * Sprava a vytvoreni maleho kalendare
 * @author Vity
 */
public class SmallCalendarManager {
    private final static Logger logger = Logger.getLogger(SmallCalendarManager.class.getName());
    /**
     * Polozky dat pro seznam v kalendari
     */
    private JXList checkedList;
    private JPopupMenu popupMenu;
    private SelectionInList<VCalendarEntity> inCalendarsList;

    public SmallCalendarManager() {
    }

    public JComponent getComponent() {

        final JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setName("mainTabbedPane");
        final ResourceMap rm = Swinger.getResourceMap();
        tabbedPane.addTab(rm.getString("tabbedPaneSmallCalendar.date.title"), null, getSmallCalendarPanel());
        tabbedPane.addTab(rm.getString("tabbedPaneSmallCalendar.calendar.title"), null, getCalendarList());
        tabbedPane.setBorder(null);
        return tabbedPane;
    }

    private Component getSmallCalendarPanel() {
        final double f = TableLayout.FILL;
        final double p = TableLayout.PREFERRED;
        final TableLayout mgr = new TableLayout(new double[]{f}, new double[]{f, p, f});
        mgr.setHGap(10);
        final JPanel panel = new JPanel(mgr);
        final EnhancedJXMonthView view = new EnhancedJXMonthView();

        panel.add(view, new CustomLayoutConstraints(0, 1));
        final MainApp app = MainApp.getInstance(MainApp.class);
        final DataProvider dataProvider = app.getDataProvider();
        PropertyConnector connector = PropertyConnector.connect(dataProvider.getCurrentDateHolder(), "value", view, "selectedDate");
        connector.updateProperty2();
        return panel;
    }

    private Component getCalendarList() {

        MainApp app = MainApp.getInstance(MainApp.class);
        ListModel listModel;
        try {
            listModel = app.getDataProvider().getCalendarsListModel();
        } catch (PersistencyLayerException e) {
            LogUtils.processException(logger, e);
            listModel = new ArrayListModel();
        }
        updateEventTaskActionEnablement(listModel.getSize());
        listModel.addListDataListener(new CalendarsListListener());
        //final SelectionInList<VCalendarEntity> inList = new SelectionInList<VCalendarEntity>(new SortedListModel(listModel, SortedListModel.SortOrder.ASCENDING, new CalendarComparator()));
        inCalendarsList = new SelectionInList<VCalendarEntity>(listModel);

        //checkedList = BasicComponentFactory.createList(inList, new CheckListRenderer());
        checkedList = new JXList();
        //Bindings.bind(checkedList, inCalendarsList);

        checkedList.setModel(inCalendarsList);
        //nelze pouzit binding, protoze pouzivame sortovani, nesedely by indexy na jxlistu, proto vyuzijeme konvertor
        checkedList.setSelectionModel(
                new SingleListSelectionAdapter(new JXListSelectionConverter(
                        inCalendarsList.getSelectionIndexHolder(), checkedList)));

        Bindings.addComponentPropertyHandler(checkedList, inCalendarsList.getSelectionHolder());

//        checkedList.addMouseListener(new DoubleClickHandler());
        checkedList.setCellRenderer(new CheckListRenderer());
        checkedList.setFilterEnabled(true);

        checkedList.addKeyListener(new MyKeyAdapter());
        checkedList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        checkedList.setBorder(new EmptyBorder(2, 4, 0, 0));
        checkedList.setComparator(new CalendarComparator());
        checkedList.setSortOrder(org.jdesktop.swingx.decorator.SortOrder.ASCENDING);

        popupMenu = new JPopupMenu("Calendar popup");

        final Object[] popmenuActionNames = {
                "newCalendar",
                "editCalendar",
                "deleteCalendar",
                "---",
                "importCalendar",
                "exportCalendar"
        };

        MenuManager.processMenu(popupMenu, "Calendar popup", popmenuActionNames);
        checkedList.add(popupMenu);
        checkedList.addMouseListener(new CalendarsListMouseListener());
        initActionHandling();
        return new JScrollPane(checkedList);
    }

    private static final class CheckListRenderer extends CheckRenderer {
        public final Component getListCellRendererComponent(final JList list, final Object value, final int index, final boolean isSelected, final boolean cellHasFocus) {
            final VCalendarEntity contentData = (VCalendarEntity) value;
            check.setSelected(contentData.isActive());
            return super.getListCellRendererComponent(list, contentData.getName(), index, isSelected, cellHasFocus, null);    //call to super
        }

    }

    private void updateCheckedStateToDB(VCalendarEntity calendarEntity) {
        MainApp app = MainApp.getInstance(MainApp.class);
        try {
            app.getDataProvider().updateCalendarActive(calendarEntity);
        } catch (PersistencyLayerException e) {
            LogUtils.processException(logger, e);
            Swinger.showErrorDialog("errorCalendarUpdate", e);
        }
    }

    private final class MyKeyAdapter extends KeyAdapter {
        public final void keyPressed(final KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                toggleChecked(checkedList.getSelectedIndex());
            }
        }
    }

    private final static class CalendarComparator implements Comparator<VCalendarEntity> {

        public int compare(VCalendarEntity o1, VCalendarEntity o2) {
            return o1.getName().compareTo(o2.getName());
        }
    }

    class CalendarsListMouseListener extends MouseAdapter {

        public void mouseClicked(final MouseEvent e) {
//            if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2) {
//                Swinger.getAction("editCalendar").actionPerformed(null);
//                return;
//            }
            if (SwingUtilities.isLeftMouseButton(e) && e.getX() < 20) {
                final int index = checkedList.locationToIndex(e.getPoint());
                toggleChecked(index);

                e.consume();
            }
        }

        public void mousePressed(MouseEvent e) {
            maybeShowPopup(e);
        }

        public void mouseReleased(MouseEvent e) {
            maybeShowPopup(e);
        }

        private void maybeShowPopup(MouseEvent e) {
            if (e.isPopupTrigger() && popupMenu != null) {
                popupMenu.show(e.getComponent(), e.getX(), e.getY());
                e.consume();
            }
        }
    }


    private void toggleChecked(final int index) {
        if (index < 0)
            throw new IllegalArgumentException("Index must be >=0");
        //final Rectangle rect = checkedList.getCellBounds(index, index);

        final VCalendarEntity data = (VCalendarEntity) checkedList.getModel().getElementAt(index);
        data.setActive(!data.isActive());
        updateCheckedStateToDB(data);
        checkedList.setSelectedIndex(index);
        //checkedList.repaint(rect);
    }

    /**
     * Initializes the event handling by just registering a handler that updates the Action enablement if the
     * albumSelection's 'selectionEmpty' property changes.
     */
    private void initActionHandling() {
        inCalendarsList.addPropertyChangeListener(
                SelectionInList.PROPERTYNAME_SELECTION_EMPTY,
                new SelectionEmptyHandler());
        updateActionEnablement();
    }

    private void updateActionEnablement() {
        boolean hasSelection = inCalendarsList.hasSelection();
        Swinger.getAction("editCalendar").setEnabled(hasSelection);
        Swinger.getAction("deleteCalendar").setEnabled(hasSelection);
    }

    /**
     * Enables or disables this model's Actions when it is notified about a change in the <em>selectionEmpty</em>
     * property of the SelectionInList.
     */
    private final class SelectionEmptyHandler implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            updateActionEnablement();
        }
    }


    private class CalendarsListListener implements ListDataListener {
        public void intervalAdded(ListDataEvent e) {
            sizeChanged(e);
        }

        public void intervalRemoved(ListDataEvent e) {
            sizeChanged(e);
        }

        private void sizeChanged(ListDataEvent e) {
            final ListModel listModel = (ListModel) e.getSource();
            final int size = listModel.getSize();
            updateEventTaskActionEnablement(size);
        }

        public void contentsChanged(ListDataEvent e) {

        }
    }

    private void updateEventTaskActionEnablement(final int size) {
        Swinger.getAction("newEvent").setEnabled(size > 0);
        Swinger.getAction("newTask").setEnabled(size > 0);
    }

    public SelectionInList<VCalendarEntity> getInCalendarsList() {
        return inCalendarsList;
    }


    public static class EnhancedJXMonthView extends JXMonthView {
        public EnhancedJXMonthView() {
            super();
            this.setBorder(null);
            this.setShowingWeekNumber(true);
            this.setShowLeadingDates(true);
            this.setShowTrailingDates(true);
            this.setPreferredCols(1);
            this.setPreferredRows(1);
            this.setUnselectableDates(new long[0]);
            this.setTraversable(true);
            //this.setFirstDisplayedDate();
            ComponentFactory.setMonthViewStyle(this);
        }

        @Override
        public void setSelectedDate(Date newDate) {
            super.setSelectedDate(newDate);
            super.setFirstDisplayedDate(newDate.getTime());
        }
    }


}
