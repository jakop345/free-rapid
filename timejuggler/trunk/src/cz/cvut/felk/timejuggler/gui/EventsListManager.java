package cz.cvut.felk.timejuggler.gui;

import application.ResourceMap;
import application.ApplicationContext;
import cz.cvut.felk.timejuggler.swing.CustomLayoutConstraints;
import cz.cvut.felk.timejuggler.swing.Swinger;
import cz.cvut.felk.timejuggler.core.AppPrefs;
import info.clearthought.layout.TableLayout;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.FilterPipeline;
import org.jdesktop.swingx.decorator.PatternFilter;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.BadLocationException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.awt.*;

/**
 * Sprava a vytvoreni seznamu udalosti
 * @author Vity
 */
public class EventsListManager {
    private final static Logger logger = Logger.getLogger(EventsListManager.class.getName());
    private final JPanel panelMain = new JPanel(new BorderLayout(2, 2));
    private final JPanel panelSearchbar = new JPanel();

    /**
     * Zalozi, vytvorena komponenta je dostupna pres getComponent
     * @see getComponent
     */
    public EventsListManager() {
        final Action action = ApplicationContext.getInstance().getActionMap().get("showSearchBar");
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

        final ResourceMap rm = Swinger.getResourceMap();
        final Integer eventFilterCount = rm.getInteger("eventFilterCount");
        final Object[] comboValues = new Object[eventFilterCount];
        for (int i = 0; i < eventFilterCount; ++i)
            comboValues[i] = rm.getString("eventFilter" + i);
        final DefaultTableModel tableModel = new DefaultTableModel(new Object[]{"Start", "Title", "End", "Category", "Location", "Status", "Calendar Name"}, 10);
        final JXTable table = new JXTable(tableModel);


        final JComboBox comboBox = new JComboBox(comboValues);
        final JTextField fieldFilter = new JTextField();
        fieldFilter.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                doFilter(e);
            }

            public void removeUpdate(DocumentEvent e) {
                doFilter(e);
            }

            public void changedUpdate(DocumentEvent e) {
                doFilter(e);
            }

            private void doFilter(DocumentEvent e) {
                try {
                    final String filterText = e.getDocument().getText(0, e.getDocument().getLength());
                    table.setFilters(new FilterPipeline(new AllPatternFilter(filterText, 0, table.getColumnCount())));
                } catch (BadLocationException ex) {
                    logger.log(Level.SEVERE, ex.getMessage(), ex);
                }
            }

        });

        table.setColumnControlVisible(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.packAll();

        panelSearchbar.add(comboBox, new CustomLayoutConstraints(0, 0));
        panelSearchbar.add(labelContain, new CustomLayoutConstraints(1, 0));
        panelSearchbar.add(fieldFilter, new CustomLayoutConstraints(2, 0));

        panelMain.add(panelSearchbar, BorderLayout.NORTH);
        panelMain.add(new JScrollPane(table), BorderLayout.CENTER);

        return panelMain;
    }

    /**
     * Filtr pro redukci seznamu podle inputu.
     * Prohledava pres vsechny sloupce (defaultne se prohledava jen pevne urceny).
     */
    private static class AllPatternFilter extends PatternFilter {

        public AllPatternFilter(String string, int i, int i1) {
            super(string, i, i1);
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
}
