package cz.cvut.felk.timejuggler.gui;

import application.ResourceMap;
import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.list.SelectionInList;
import cz.cvut.felk.timejuggler.core.MainApp;
import cz.cvut.felk.timejuggler.db.entity.VCalendar;
import cz.cvut.felk.timejuggler.swing.CustomLayoutConstraints;
import cz.cvut.felk.timejuggler.swing.Swinger;
import cz.cvut.felk.timejuggler.swing.renderers.CheckRenderer;
import info.clearthought.layout.TableLayout;
import org.jdesktop.swingx.calendar.JXMonthView;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Sprava a vytvoreni maleho kalendare
 * @author Vity
 */
public class SmallCalendarManager {
    /**
     * Polozky dat pro seznam v kalendari
     */
//    private List<ContentData<String>> listData; //TODO sjednotit s globalnimi daty
    private JList checkedList;

    public SmallCalendarManager() {
    }

    public JComponent getComponent() {

        final JTabbedPane tabbedPane = new JTabbedPane();
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
//        listData = new ArrayList<ContentData<String>>();
//        listData.add(new ContentData<String>("Hlavni kalendar", true));
//        listData.add(new ContentData<String>("Svatky", false));
//        final AbstractListModel listModel = new AbstractListModel() {
//            public int getSize() {
//                return listData.size();
//            }
//
//            public Object getElementAt(int index) {
//                return listData.get(index);
//            }
//        };

        MainApp app = MainApp.getInstance(MainApp.class);
        final ListModel listModel = app.getDataProvider().getCalendars();
        checkedList = BasicComponentFactory.createList(new SelectionInList<VCalendar>(listModel), new CheckListRenderer());
        checkedList.addKeyListener(new MyKeyAdapter());
        checkedList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        checkedList.setBorder(new EmptyBorder(2, 4, 0, 0));
        checkedList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(final MouseEvent e) {
                toggleChecked(checkedList.locationToIndex(e.getPoint()));
            }
        });
        return new JScrollPane(checkedList);
    }

//    @SuppressWarnings({"unchecked"})
//    private class ContentData<C extends Comparable> implements Comparable {
//        private C value;
//        private boolean checked;
//
//        public ContentData(C item, boolean checked) {
//            this.value = item;
//            this.checked = checked;
//        }
//
//        public int compareTo(Object o) {
//            return value.compareTo(((ContentData) o).value);
//        }
//
//        public boolean isChecked() {
//            return checked;
//        }
//
//
//        public C getValue() {
//            return value;
//        }
//    }

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


}
