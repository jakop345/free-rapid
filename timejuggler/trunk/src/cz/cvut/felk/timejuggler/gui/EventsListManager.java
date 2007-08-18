package cz.cvut.felk.timejuggler.gui;

import application.ApplicationContext;
import application.ResourceMap;
import com.jgoodies.binding.adapter.AbstractTableAdapter;
import com.jgoodies.binding.adapter.Bindings;
import com.jgoodies.binding.adapter.SingleListSelectionAdapter;
import com.jgoodies.binding.list.ArrayListModel;
import com.jgoodies.binding.list.SelectionInList;
import com.jgoodies.binding.value.DelayedReadValueModel;
import com.jgoodies.binding.value.ValueHolder;
import cz.cvut.felk.timejuggler.core.AppPrefs;
import cz.cvut.felk.timejuggler.core.MainApp;
import cz.cvut.felk.timejuggler.core.data.PersistencyLayerException;
import cz.cvut.felk.timejuggler.db.entity.Category;
import cz.cvut.felk.timejuggler.db.entity.interfaces.EventTaskEntity;
import cz.cvut.felk.timejuggler.db.entity.interfaces.VCalendarEntity;
import cz.cvut.felk.timejuggler.swing.ComponentFactory;
import cz.cvut.felk.timejuggler.swing.CustomLayoutConstraints;
import cz.cvut.felk.timejuggler.swing.Swinger;
import cz.cvut.felk.timejuggler.utilities.LogUtils;
import info.clearthought.layout.TableLayout;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.FilterPipeline;
import org.jdesktop.swingx.decorator.PatternFilter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * Sprava a vytvoreni seznamu udalosti
 * @author Vity
 */
public class EventsListManager {
    private final static Logger logger = Logger.getLogger(EventsListManager.class.getName());
    private final JPanel panelMain = new JPanel(new BorderLayout(2, 2));
    private final JPanel panelSearchbar = new JPanel();
    private SelectionInList<EventTaskEntity> inList;

    private static enum EventsFilters {
        ALL_EVENTS
    }

    /**
     * Zalozi, vytvorena komponenta je dostupna pres getComponent
     * @see getComponent
     */
    public EventsListManager(final ApplicationContext context) {
        final Action action = context.getActionMap().get("showSearchBar");
        action.addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                if (Action.SELECTED_KEY.equals(evt.getPropertyName())) {
                    setSearchBarVisible((Boolean) evt.getNewValue());
                }
            }

        });
        action.putValue(Action.SELECTED_KEY, AppPrefs.getProperty(AppPrefs.SHOW_SEARCHBAR, true));
    }

    private void setSearchBarVisible(boolean visible) {
        panelSearchbar.setVisible(visible);
//        final LayoutManager layoutManager = panelMain.getLayout();
//        layoutManager.layoutContainer(panelMain);
        //((TableLayout) layoutManager).invalidateLayout(panelMain);
        AppPrefs.storeProperty(AppPrefs.SHOW_SEARCHBAR, visible); //ulozeni uzivatelskeho nastaveni, ale jen do hashmapy
    }

    public JComponent getComponent() {
        final double f = TableLayout.FILL;
        final double p = TableLayout.PREFERRED;
        final TableLayout layoutSearchbar = new TableLayout(new double[]{p, p, f}, new double[]{f});
        panelSearchbar.setLayout(layoutSearchbar);
        layoutSearchbar.setHGap(10);
        layoutSearchbar.setVGap(2);

        panelMain.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(), BorderFactory.createEmptyBorder(4, 4, 0, 4)));
        final JLabel labelContain = new JLabel();
        labelContain.setName("labelContain");

        final MainApp app = MainApp.getInstance(MainApp.class);

        final JXTable table = new JXTable();
        table.setName("eventsTable");
        //inicializace categories tabulky
        ArrayListModel<EventTaskEntity> eventsListModel = new ArrayListModel<EventTaskEntity>();
        try {
            eventsListModel = app.getDataProvider().getEventsListModel();
        } catch (PersistencyLayerException e) {
            LogUtils.processException(logger, e);
        }

        inList = new SelectionInList<EventTaskEntity>((java.util.List<EventTaskEntity>) eventsListModel);

        final EventsTableModel tableModel = new EventsTableModel(inList);

        table.setModel(tableModel);
        table.setSortOrder(EventsTableModel.COLUMN_START_INDEX, org.jdesktop.swingx.decorator.SortOrder.ASCENDING);
        table.setSelectionModel(new SingleListSelectionAdapter(new JXTableSelectionConverter(inList.getSelectionIndexHolder(), table)));

        table.addMouseListener(new DoubleClickHandler());
        final ResourceMap rm = Swinger.getResourceMap();

        final JComboBox comboBox = ComponentFactory.getComboBox();
        bindCombobox(comboBox, AppPrefs.SELECTED_EVENTS_FILTER, EventsFilters.ALL_EVENTS.ordinal(), "eventFilter");

        final JTextField fieldFilter = ComponentFactory.getTextField();
        final MyPreferencesAdapter adapter = new MyPreferencesAdapter(AppPrefs.CONTAIN_EVENTS_FILTER, "");
        final DelayedReadValueModel delayedReadValueModel = new DelayedReadValueModel(adapter, 300, true);
        delayedReadValueModel.addValueChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                updateFilters(evt.getNewValue().toString(), table);
            }
        });


        Bindings.bind(fieldFilter, delayedReadValueModel);

        table.setColumnControlVisible(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        Swinger.updateColumn(table, rm.getString("tableEvents.column.start"), EventsTableModel.COLUMN_START_INDEX, 70);
        Swinger.updateColumn(table, rm.getString("tableEvents.column.title"), EventsTableModel.COLUMN_TITLE_INDEX, 100);
        Swinger.updateColumn(table, rm.getString("tableEvents.column.end"), EventsTableModel.COLUMN_END_INDEX, 70);
        Swinger.updateColumn(table, rm.getString("tableEvents.column.category"), EventsTableModel.COLUMN_CATEGORY_INDEX, 70);
        Swinger.updateColumn(table, rm.getString("tableEvents.column.location"), EventsTableModel.COLUMN_LOCATION_INDEX, 70);
        Swinger.updateColumn(table, rm.getString("tableEvents.column.status"), EventsTableModel.COLUMN_STATUS_INDEX, 50);
        Swinger.updateColumn(table, rm.getString("tableEvents.column.calendarName"), EventsTableModel.COLUMN_CALENDAR_NAME_INDEX, 70, null);

        table.packAll();
        initEventHandling();
        updateFilters(delayedReadValueModel.getString(), table);

        panelSearchbar.add(comboBox, new CustomLayoutConstraints(0, 0));
        panelSearchbar.add(labelContain, new CustomLayoutConstraints(1, 0));
        panelSearchbar.add(fieldFilter, new CustomLayoutConstraints(2, 0));

        panelMain.add(panelSearchbar, BorderLayout.NORTH);
        panelMain.add(new JScrollPane(table), BorderLayout.CENTER);

        return panelMain;
    }

    private void updateFilters(final String filterText, JXTable table) {
        if (filterText == null || filterText.isEmpty()) {
            logger.info("Setting no events filter");
            table.setFilters(null);
        } else {
            logger.info("Setting events filter");
            table.setFilters(new FilterPipeline(new AllPatternFilter(filterText, Pattern.CASE_INSENSITIVE, table.getColumnCount())));
        }
    }

    /**
     * Filtr pro redukci seznamu podle inputu. Prohledava pres vsechny sloupce (defaultne se prohledava jen pevne
     * urceny).
     */
    private static class AllPatternFilter extends PatternFilter {

        public AllPatternFilter(String string, int patternFlags, int col) {
            super(string, patternFlags, col);
        }

        @Override
        public boolean test(int row) {
            final int maxColumnIndex = getColumnIndex();
            for (int i = 0; i < maxColumnIndex; ++i) {
                if (adapter.isTestable(i)) {
                    Object value = getInputValue(row, i);
                    if (value != null) {
                        boolean matches = pattern.matcher(value.toString()).find();
                        if (matches)
                            return true;
                    }
                }
            }
            return false;
        }
    }


    /**
     * A mouse listener that edits the selected item on double click.
     */
    private static final class DoubleClickHandler extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2) {
                final Action action = Swinger.getAction("editEvent");
                if (action.isEnabled())
                    action.actionPerformed(new ActionEvent(e.getSource(), 0, "editEvent"));
            }
        }
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

    private void updateActionEnablement() {

    }

    /**
     * Initializes the event handling by just registering a handler that updates the Action enablement if the
     * albumSelection's 'selectionEmpty' property changes.
     */
    private void initEventHandling() {
        getEventSelection().addPropertyChangeListener(
                SelectionInList.PROPERTYNAME_SELECTION_EMPTY,
                new SelectionEmptyHandler());
        updateActionEnablement();
    }

    private SelectionInList<EventTaskEntity> getEventSelection() {
        return inList;
    }

    private EventTaskEntity getSelectedItem() {
        return inList.getSelection();
    }

    /**
     * Describes how to present an Event in a JTable.
     */
    private static final class EventsTableModel extends AbstractTableAdapter<EventTaskEntity> {
        private final static int COLUMN_START_INDEX = 0;
        private final static int COLUMN_TITLE_INDEX = 1;
        private final static int COLUMN_END_INDEX = 2;
        private final static int COLUMN_CATEGORY_INDEX = 3;
        private final static int COLUMN_LOCATION_INDEX = 4;
        private final static int COLUMN_STATUS_INDEX = 5;
        private final static int COLUMN_CALENDAR_NAME_INDEX = 6;

        private static final String[] COLUMNS = {"Start", "Title", "End", "Category", "Location", "Status", "Calendar Name"};

        private EventsTableModel(ListModel listModel) {
            super(listModel, COLUMNS);
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return false;
        }

        public Object getValueAt(int rowIndex, int columnIndex) {
            EventTaskEntity entity = getRow(rowIndex);
            if (entity == null)
                throw new IllegalStateException("entity in row cannot be null");
            switch (columnIndex) {
                case COLUMN_START_INDEX:
                    return entity.getStartDate();
                case COLUMN_TITLE_INDEX:
                    return entity.getSummary();
                case COLUMN_END_INDEX:
                    return entity.getEndDate();
                case COLUMN_CATEGORY_INDEX:
                    final java.util.List<Category> list = entity.getCategories();
                    if (list != null && !list.isEmpty())
                        return list.get(0).getName();
                    else return "";
                case COLUMN_LOCATION_INDEX:
                    return entity.getLocation();
                case COLUMN_STATUS_INDEX:
                    return entity.getStatus();
                case COLUMN_CALENDAR_NAME_INDEX:
                    final VCalendarEntity calendar = entity.getCalendar();
                    if (calendar != null)
                        return calendar.getName();
                    else return "";
                default:
                    throw new IllegalStateException("Unknown column");
            }
        }

    }

    private void bindCombobox(final JComboBox combobox, final String key, final Object defaultValue, final String propertyResourceMap) {
        final String[] stringList = getList(propertyResourceMap);
        if (stringList == null)
            throw new IllegalArgumentException("Property '" + propertyResourceMap + "' does not provide any string list from resource map.");
        bindCombobox(combobox, key, defaultValue, stringList);
    }

    private void bindCombobox(final JComboBox combobox, String key, final Object defaultValue, final String[] values) {
        if (values == null)
            throw new IllegalArgumentException("List of combobox values cannot be null!!");
        final MyPreferencesAdapter adapter = new MyPreferencesAdapter(key, defaultValue);
        final SelectionInList<String> inList = new SelectionInList<String>(values, new ValueHolder(values[(Integer) adapter.getValue()]), adapter);
        Bindings.bind(combobox, inList);
    }

    protected String[] getList(String key) {
        return (String[]) Swinger.getResourceMap().getObject(key + "_list", String[].class);
    }
}
