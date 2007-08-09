package cz.cvut.felk.timejuggler.gui;

import application.ResourceMap;
import com.jgoodies.binding.adapter.Bindings;
import com.jgoodies.binding.list.SelectionInList;
import cz.cvut.felk.timejuggler.core.MainApp;
import cz.cvut.felk.timejuggler.db.entity.VCalendar;
import cz.cvut.felk.timejuggler.swing.CustomLayoutConstraints;
import cz.cvut.felk.timejuggler.swing.Swinger;
import cz.cvut.felk.timejuggler.swing.renderers.CheckRenderer;
import info.clearthought.layout.TableLayout;
import org.jdesktop.swingx.JXList;
import org.jdesktop.swingx.calendar.JXMonthView;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Comparator;

/**
 * Sprava a vytvoreni maleho kalendare
 * @author Vity
 */
public class SmallCalendarManager {
    /**
     * Polozky dat pro seznam v kalendari
     */
//    private List<ContentData<String>> listData; //TODO sjednotit s globalnimi daty
    private JXList checkedList;

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
        final JXMonthView view = new JXMonthView();
        view.setBorder(null);
        view.setShowingWeekNumber(true);
        view.setShowLeadingDates(true);
        view.setShowTrailingDates(true);
        view.setPreferredCols(1);
        view.setPreferredRows(1);
        view.setUnselectableDates(new long[0]);
        view.setTraversable(true);
        panel.add(view, new CustomLayoutConstraints(0, 1));
        return panel;
    }

    private Component getCalendarList() {

        MainApp app = MainApp.getInstance(MainApp.class);
        final ListModel listModel = app.getDataProvider().getCalendarsListModel();
        //final SelectionInList<VCalendar> inList = new SelectionInList<VCalendar>(new SortedListModel(listModel, SortedListModel.SortOrder.ASCENDING, new CalendarComparator()));
        final SelectionInList<VCalendar> inList = new SelectionInList<VCalendar>(listModel);
        //checkedList = BasicComponentFactory.createList(inList, new CheckListRenderer());
        checkedList = new JXList();
        Bindings.bind(checkedList, inList);
        checkedList.setCellRenderer(new CheckListRenderer());
        checkedList.setFilterEnabled(true);

        checkedList.addKeyListener(new MyKeyAdapter());
        checkedList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        checkedList.setBorder(new EmptyBorder(2, 4, 0, 0));
        checkedList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(final MouseEvent e) {
                toggleChecked(checkedList.locationToIndex(e.getPoint()));
            }
        });
        checkedList.setComparator(new CalendarComparator());
        checkedList.setSortOrder(org.jdesktop.swingx.decorator.SortOrder.ASCENDING);

        return new JScrollPane(checkedList);
    }

    private static final class CheckListRenderer extends CheckRenderer {
        public final Component getListCellRendererComponent(final JList list, final Object value, final int index, final boolean isSelected, final boolean cellHasFocus) {
            final VCalendar contentData = ((VCalendar) value);
            check.setSelected(false);
            return super.getListCellRendererComponent(list, contentData.getName(), index, isSelected, cellHasFocus, null);    //call to super
        }

    }

    private void toggleChecked(final int index) {
        if (index < 0)
            throw new IllegalArgumentException("Index must be >=0");
        final VCalendar data = (VCalendar) checkedList.getModel().getElementAt(index);
        //data.checked = !data.checked;//TODO podpora pro checked
        final Rectangle rect = checkedList.getCellBounds(index, index);
        checkedList.repaint(rect);
    }

    private final class MyKeyAdapter extends KeyAdapter {
        public final void keyPressed(final KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                toggleChecked(checkedList.getSelectedIndex());
            }
        }
    }

    private final static class CalendarComparator implements Comparator<VCalendar> {

        public int compare(VCalendar o1, VCalendar o2) {
            return o1.getName().compareTo(o2.getName());
        }
    }


}
