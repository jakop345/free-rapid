package cz.cvut.felk.timejuggler.gui;

import cz.cvut.felk.timejuggler.core.AppPrefs;
import cz.cvut.felk.timejuggler.swing.CustomLayoutConstraints;
import cz.cvut.felk.timejuggler.swing.Swinger;
import cz.cvut.felk.timejuggler.swing.renderers.HeaderIconRenderer;
import info.clearthought.layout.TableLayout;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.FilterPipeline;
import org.jdesktop.swingx.decorator.PatternFilter;
import org.jdesktop.swingx.table.TableColumnExt;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * Seznam tasku
 * @author Vity
 */
public class TaskListManager {
    /**
     * konstanta pro hodnotu nizke priority v modelu
     */
    private static final int LOW_PRIORITY = 1;
    /**
     * konstanta pro hodnotu vysoke priority v modelu
     */
    private static final int HIGH_PRIORITY = 3;

    /**
     * index sloupce vyuzite pro "DONE"
     */
    private static final int COLUMN_DONE_INDEX = 0;
    /**
     * index sloupce vyuzite pro "PRIORITY"
     */
    private static final int COLUMN_PRIORITY_INDEX = 1;

    /**
     * Vytvori komponentu
     * @return vraci panel
     */
    public JComponent getComponent() {
        final double f = TableLayout.FILL;
        final double p = TableLayout.PREFERRED;
        final TableLayout mgr = new TableLayout(new double[]{f}, new double[]{p, p, f});
        mgr.setHGap(10);
        final JPanel panelMain = new JPanel(mgr);
        panelMain.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(), BorderFactory.createEmptyBorder(4, 4, 0, 4)));
        final JLabel labelTask = new JLabel();
        labelTask.setName("labelTask");
        final JCheckBox checkTask = new JCheckBox();
        checkTask.setName("checkTask");
        final JXTable table = new JXTable();
        table.setName("taskJXTable");
        final DefaultTableModel tableModel = new DefaultTableModel(new Object[]{"Done", "Priority", "Completed", "Title", "Calendar Name"}, 0) {
            public Class<?> getColumnClass(int columnIndex) {
                final String name = this.getColumnName(columnIndex);
                if (name.equals("Done"))
                    return Boolean.class;
                if (name.equals("Priority"))
                    return Integer.class;
                return super.getColumnClass(columnIndex);
            }

            public boolean isCellEditable(int row, int column) {
                return true;
            }
        };
        tableModel.addRow(new Object[]{Boolean.TRUE, 1, "test", "testX", "Home"});
        tableModel.addRow(new Object[]{Boolean.TRUE, 2, "test", "testX", "Svatky"});
        tableModel.addRow(new Object[]{Boolean.FALSE, 3, "test", "testX", "testY"});
        tableModel.addRow(new Object[]{Boolean.TRUE, 2, "test", "testX", "testY"});
        tableModel.addRow(new Object[]{Boolean.TRUE, 1, "test", "testX", "testY"});


        table.setModel(tableModel);

        table.getColumnExt("Completed").setVisible(false);
        table.setColumnControlVisible(true);
        table.getColumnExt("Done").setHeaderRenderer(new HeaderIconRenderer(Swinger.getIconImage("iconCheckTask")));
        final TableColumnExt priorityColumn = table.getColumnExt("Priority");
        priorityColumn.setHeaderRenderer(new HeaderIconRenderer(Swinger.getIconImage("iconPriorityTask")));
        priorityColumn.setCellRenderer(new PriorityCellRenderer());
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setDefaultRenderer(Object.class, new DefaultStringCellRenderer());
        table.packAll();
        table.setPreferredSize(new Dimension(100, -1));

        panelMain.add(labelTask, new CustomLayoutConstraints(0, 0));
        panelMain.add(checkTask, new CustomLayoutConstraints(0, 1));
        panelMain.add(new JScrollPane(table), new CustomLayoutConstraints(0, 2));

        checkTask.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                updateFilters(((AbstractButton) e.getSource()).isSelected());
            }

            private void updateFilters(boolean hideCompletedTasks) {
                AppPrefs.storeProperty(AppPrefs.HIDE_COMPLETED_TASKS, hideCompletedTasks);
                if (!hideCompletedTasks)
                    table.setFilters(null);
                else
                    table.setFilters(new FilterPipeline(new HideCompletedTasksFilter()));
            }
        });

        checkTask.setSelected(AppPrefs.getProperty(AppPrefs.HIDE_COMPLETED_TASKS, false));
        return panelMain;

    }

    /**
     * Filtr pouzivany ke skryti DONE tasku
     */
    private static class HideCompletedTasksFilter extends PatternFilter {

        public HideCompletedTasksFilter() {
            super("", 0, 0);
        }

        @Override
        public boolean test(int row) {
            final int columnIndex = getColumnIndex();

            if (adapter.isTestable(columnIndex)) {
                Object value = getInputValue(row, columnIndex);
                if (value != null) {
                    return (value.equals(Boolean.FALSE));
                }
            }
            return false;
        }
    }

    /**
     * Renderer na bunku obsahujici Priority - kresli patricne ikony
     */
    private static final class PriorityCellRenderer extends DefaultTableCellRenderer {
        private final ImageIcon lowPriorityIcon = Swinger.getIconImage("lowPriorityIcon");
        private final ImageIcon higPriorityIcon = Swinger.getIconImage("highPriorityIcon");

        public final Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, "", isSelected, hasFocus, row, column);
            this.setHorizontalAlignment(CENTER);
            this.setHorizontalTextPosition(CENTER);
            switch ((Integer) value) {
                case LOW_PRIORITY:
                    this.setIcon(lowPriorityIcon);
                    break;
                case HIGH_PRIORITY:
                    this.setIcon(higPriorityIcon);
                    break;
                default:
                    this.setIcon(null);
            }

            return this;
        }
    }

    /**
     * Vychozi renderer pro Object.class - stringy budou preskrtnute pokud je Task DONE
     */
    private static final class DefaultStringCellRenderer extends DefaultTableCellRenderer {
        private static final String FORMAT_STRING = Swinger.getResourceMap().getString("doneTaskString");

        public final Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (value instanceof String) {
                final boolean isDone = (Boolean) table.getModel().getValueAt(table.convertRowIndexToModel(row), COLUMN_DONE_INDEX);
                final String s = String.format(FORMAT_STRING, value);
                return super.getTableCellRendererComponent(table, (isDone) ? s : value, isSelected, hasFocus, row, column);
            }
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }
    }

}
