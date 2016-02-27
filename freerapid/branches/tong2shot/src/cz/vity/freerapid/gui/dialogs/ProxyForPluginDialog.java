package cz.vity.freerapid.gui.dialogs;

import com.jgoodies.common.collect.ArrayListModel;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.*;
import cz.vity.freerapid.gui.managers.ManagerDirector;
import cz.vity.freerapid.gui.managers.ProxyForPluginManager;
import cz.vity.freerapid.model.bean.ProxyForPlugin;
import cz.vity.freerapid.swing.SwingUtils;
import cz.vity.freerapid.swing.Swinger;
import cz.vity.freerapid.swing.components.FindTableAction;
import cz.vity.freerapid.utilities.LogUtils;
import org.jdesktop.application.Action;
import org.jdesktop.swinghelper.buttonpanel.JXButtonPanel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.decorator.HighlighterFactory;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * @author tong2shot
 */
public class ProxyForPluginDialog extends AppDialog implements PropertyChangeListener, ListSelectionListener {
    private final static Logger logger = Logger.getLogger(ProxyForPluginDialog.class.getName());

    private static final String DATA_ADDED_PROPERTY = "dataAdded";
    private static final String DATA_MODIFIED_PROPERTY = "dataModified";
    private static final int COLUMN_ACTIVE = 0;
    private static final int COLUMN_PLUGIN = 1;
    private static final int COLUMN_SERVICES = 2;
    private static final int COLUMN_PROXY_SET = 3;
    private static final int MIN_FIRST_COLUMN_WIDTH = 26;

    private static final String SELECTED_ACTION_ENABLED_PROPERTY = "selectedEnabled";
    private boolean selectedEnabled;

    private final ManagerDirector director;
    private final ProxyForPluginManager manager;

    public ProxyForPluginDialog(JDialog owner, ManagerDirector director) throws HeadlessException {
        super(owner, true);
        this.director = director;
        this.manager = director.getProxyForPluginManager();
        this.setName("ProxyForPluginDialog");
        try {
            initComponents();
            build();
        } catch (Exception e) {
            LogUtils.processException(logger, e);
            doClose();
        }
    }

    @Override
    protected AbstractButton getBtnOK() {
        return btnClose;
    }

    @Override
    public void doClose() {
        manager.removePropertyChangeListener(DATA_ADDED_PROPERTY, this);
        CustomTableModel tableModel = (CustomTableModel) table.getModel();
        tableModel.model.removeListDataListener(tableModel);
        super.doClose();
    }

    private void build() {
        inject();
        buildModels();
        buildGUI();

        setAction(btnAdd, "btnAddAction");
        setAction(btnEdit, "btnEditAction");
        setAction(btnDelete, "btnDeleteAction");
        setAction(btnProxySet, "btnProxySetAction");
        setAction(btnClose, "btnCloseAction");

        updateActions();

        manager.addPropertyChangeListener(DATA_ADDED_PROPERTY, this);
        director.getProxySetManager().addPropertyChangeListener(DATA_MODIFIED_PROPERTY, new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                reloadModel();
            }
        });

        pack();
        setResizable(true);
        locateOnOpticalScreenCenter(this);
    }

    private void buildModels() {
        //
    }

    private void buildGUI() {
        initTable();
    }

    private void initTable() {
        table.setName("table");
        table.setModel(new CustomTableModel(new ArrayListModel<ProxyForPlugin>(manager.getItems()), getList("columns", 4)));
        table.setAutoCreateColumnsFromModel(false);
        table.setEditable(true);
        table.setHorizontalScrollEnabled(true);
        table.setColumnControlVisible(true);
        table.setSortable(true);
        table.setColumnMargin(10);
        table.setRolloverEnabled(true);
        table.setShowGrid(true, true);
        table.setColumnSelectionAllowed(false);
        table.createDefaultColumnsFromModel();

        table.getSelectionModel().addListSelectionListener(this);

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!table.hasFocus())
                    Swinger.inputFocus(table);
                if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() >= 2) {
                    btnEditAction();
                }
            }
        });

        Component comp = table.getTableHeader().getDefaultRenderer().getTableCellRendererComponent(
                table, getList("columns", 4)[COLUMN_ACTIVE], false, false, 0, 0);
        final int width = Math.max(comp.getPreferredSize().width + 2 * table.getColumnMargin(), MIN_FIRST_COLUMN_WIDTH);
        TableColumn tableColumn = Swinger.updateColumn(table, "Active", COLUMN_ACTIVE, width, width, null);
        tableColumn.setWidth(width);
        tableColumn.setMaxWidth(width);

        ColorHighlighter highlighter = new ColorHighlighter(new HighlightPredicate() {
            @Override
            public boolean isHighlighted(Component renderer, ComponentAdapter adapter) {
                return Boolean.FALSE.equals(adapter.getValue(COLUMN_ACTIVE));
            }
        }, HighlighterFactory.GENERIC_GRAY, Color.BLACK);
        table.addHighlighter(highlighter);

        table.setSortOrder(COLUMN_PLUGIN, SortOrder.ASCENDING);

        final InputMap tableInputMap = table.getInputMap();
        tableInputMap.put(SwingUtils.getShiftKeyStroke(KeyEvent.VK_HOME), "selectFirstRowExtendSelection");
        tableInputMap.put(SwingUtils.getShiftKeyStroke(KeyEvent.VK_END), "selectLastRowExtendSelection");

        new FindTableAction(Swinger.getResourceMap(), COLUMN_PLUGIN) {
            @Override
            protected Object getObject(int index, int column) {
                return table.getModel().getValueAt(index, column);
            }
        }.install(table);

        table.getParent().setPreferredSize(new Dimension(600, 200));
    }

    private void reloadModel() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (table.getModel() instanceof CustomTableModel) {
                    CustomTableModel tableModel = (CustomTableModel) table.getModel();
                    tableModel.model.removeListDataListener(tableModel);
                }
                int col = table.getSortedColumnIndex();
                SortOrder order = table.getSortOrder(col);
                table.setModel(new CustomTableModel(new ArrayListModel<ProxyForPlugin>(manager.getItems()), getList("columns", 4)));
                table.setSortOrder(col, order);
            }
        });
    }

    @org.jdesktop.application.Action
    public void btnAddAction() {
        final ProxyForPluginManipDialog dialog = new ProxyForPluginManipDialog(this, director, ProxyForPluginManipDialog.ManipType.ADD, null);
        getApp().show(dialog);
    }

    @org.jdesktop.application.Action(enabledProperty = SELECTED_ACTION_ENABLED_PROPERTY)
    public void btnEditAction() {
        final int[] indexes = getSelectedRows();
        if (indexes.length > 0) {
            int index = indexes[0];
            final ProxyForPlugin ProxyForPlugin = (ProxyForPlugin) table.getValueAt(table.convertRowIndexToModel(index), -1);
            final ProxyForPluginManipDialog dialog = new ProxyForPluginManipDialog(this, director, ProxyForPluginManipDialog.ManipType.EDIT, ProxyForPlugin);
            getApp().show(dialog);
            if (dialog.getModalResult() == RESULT_OK) {
                reloadModel();
            }
        }
    }

    @org.jdesktop.application.Action(enabledProperty = SELECTED_ACTION_ENABLED_PROPERTY)
    public void btnDeleteAction() {
        final int[] indexes = getSelectedRows();
        this.removeSelected(indexes);
    }

    @Action
    public void btnProxySetAction() {
        final ProxySetDialog proxySetDialog = new ProxySetDialog(this, director);
        getApp().show(proxySetDialog);
    }

    @org.jdesktop.application.Action
    public void btnCloseAction() {
        super.doClose();
    }

    @SuppressWarnings({"deprecation"})
    private void initComponents() {
        JPanel dialogPane = new JPanel();
        JPanel contentPanel = new JPanel();

        JScrollPane scrollPane1 = new JScrollPane();
        table = new JXTable();

        JXButtonPanel actionButtonPanel = new JXButtonPanel();
        btnAdd = new JButton();
        btnEdit = new JButton();
        btnDelete = new JButton();
        btnProxySet = new JButton();
        btnAdd.setName("btnAdd");
        btnEdit.setName("btnEdit");
        btnDelete.setName("btnDelete");
        btnProxySet.setName("btnProxySet");


        JXButtonPanel buttonBar = new JXButtonPanel();
        btnClose = new JButton();
        btnClose.setName("btnClose");
        CellConstraints cc = new CellConstraints();

        //======== this ========
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== dialogPane ========
        {
            dialogPane.setBorder(Borders.DIALOG);
            dialogPane.setLayout(new BorderLayout());

            //======== contentPanel ========
            {
                contentPanel.setBorder(new CompoundBorder(
                        new EmptyBorder(4, 4, 4, 4),
                        new EtchedBorder()));
                contentPanel.setLayout(new BorderLayout());

                //======== scrollPane1 ========
                {
                    scrollPane1.setViewportView(table);
                }
                contentPanel.add(scrollPane1, BorderLayout.CENTER);

                //======== actionButtonPanel ========
                {
                    actionButtonPanel.setBorder(new EmptyBorder(4, 4, 4, 4));

                    PanelBuilder actionButtonPanelBuilder = new PanelBuilder(new FormLayout(
                            new ColumnSpec[]{
                                    ColumnSpec.decode("max(pref;42dlu)"),
                                    FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
                                    FormSpecs.DEFAULT_COLSPEC,
                                    FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
                                    FormSpecs.DEFAULT_COLSPEC,
                                    new ColumnSpec(ColumnSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW),
                                    ColumnSpec.decode("max(pref;42dlu)"),
                            },
                            RowSpec.decodeSpecs("default")), actionButtonPanel);
                    ((FormLayout) actionButtonPanel.getLayout()).setColumnGroups(new int[][]{{1, 3, 5}});

                    actionButtonPanelBuilder.add(btnAdd, cc.xy(1, 1));
                    actionButtonPanelBuilder.add(btnEdit, cc.xy(3, 1));
                    actionButtonPanelBuilder.add(btnDelete, cc.xy(5, 1));
                    actionButtonPanelBuilder.add(btnProxySet, cc.xy(7, 1));
                }
                contentPanel.add(actionButtonPanel, BorderLayout.SOUTH);
            }
            dialogPane.add(contentPanel, BorderLayout.CENTER);

            //======== buttonBar ========
            {
                buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));

                PanelBuilder buttonBarBuilder = new PanelBuilder(new FormLayout(
                        new ColumnSpec[]{
                                new ColumnSpec(ColumnSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW),
                                FormSpecs.UNRELATED_GAP_COLSPEC,
                                ColumnSpec.decode("max(pref;42dlu)"),
                                FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
                                FormSpecs.DEFAULT_COLSPEC
                        },
                        RowSpec.decodeSpecs("fill:pref")), buttonBar);
                ((FormLayout) buttonBar.getLayout()).setColumnGroups(new int[][]{{3, 5}});

                buttonBarBuilder.add(btnClose, cc.xy(5, 1));
            }
            dialogPane.add(buttonBar, BorderLayout.SOUTH);
        }
        contentPane.add(dialogPane, BorderLayout.CENTER);
    }

    private ArrayListModel<ProxyForPlugin> getItems() {
        return ((CustomTableModel) table.getModel()).model;
    }

    private int[] getSelectedRows() {
        return Swinger.getSelectedRows(table);
    }

    public java.util.List<ProxyForPlugin> getSelectionToList(int[] selectedRows) {
        return selectionToList(selectedRows);
    }

    private java.util.List<ProxyForPlugin> selectionToList(int[] indexes) {
        java.util.List<ProxyForPlugin> list = new ArrayList<ProxyForPlugin>();
        final ArrayListModel<ProxyForPlugin> items = getItems();
        for (int index : indexes) {
            list.add(items.get(index));
        }
        return list;
    }

    private void updateActions() {
        final int[] indexes = getSelectedRows();
        setSelectedEnabled(indexes.length > 0);
    }

    public boolean isSelectedEnabled() {
        return this.selectedEnabled;
    }

    public void setSelectedEnabled(final boolean selectedEnabled) {
        boolean oldValue = this.selectedEnabled;
        this.selectedEnabled = selectedEnabled;
        firePropertyChange(SELECTED_ACTION_ENABLED_PROPERTY, oldValue, selectedEnabled);
    }

    private void removeSelected(final int[] indexes) {
        final int result = Swinger.getChoiceOKCancel("msgDeleteProxyForPluginConfirmation");
        if (result != Swinger.RESULT_OK)
            return;

        final ListSelectionModel selectionModel = table.getSelectionModel();
        selectionModel.setValueIsAdjusting(true);

        final ArrayListModel<ProxyForPlugin> items = getItems();
        final java.util.List<ProxyForPlugin> toRemoveList = getSelectionToList(indexes);
        manager.removeItems(toRemoveList);
        items.removeAll(toRemoveList);

        selectionModel.setValueIsAdjusting(false);
        final int min = getArrayMin(indexes);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                final int count = table.getRowCount();
                if (table.getRowCount() > 0) {
                    int index = Math.min(count - 1, min);
                    index = table.convertRowIndexToView(index);
                    selectionModel.addSelectionInterval(index, index);
                    scrollToVisible(true);
                }
            }
        });
        updateActions();
    }

    private void scrollToVisible(final boolean up) {
        final int[] rows = table.getSelectedRows();
        final int length = rows.length;
        if (length > 0)
            table.scrollRowToVisible((up) ? rows[0] : rows[length - 1]);
    }

    private int getArrayMin(int[] indexes) {
        int min = Integer.MAX_VALUE;
        for (int i : indexes) {
            if (min > i) {
                min = i;
            }
        }
        return min;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        reloadModel();
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) {
            return;
        }
        updateActions();
    }

    private class CustomTableModel extends AbstractTableModel implements ListDataListener {
        private final ArrayListModel<ProxyForPlugin> model;
        private final String[] columns;


        public CustomTableModel(ArrayListModel<ProxyForPlugin> model, String[] columns) {
            super();
            this.model = model;
            this.columns = columns;
            model.addListDataListener(this);
        }

        public int getRowCount() {
            return model.getSize();
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return COLUMN_ACTIVE == columnIndex;
        }

        @Override
        public String getColumnName(int column) {
            return this.columns[column];
        }

        public int getColumnCount() {
            return this.columns.length;
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            if (columnIndex == COLUMN_ACTIVE) {
                return Boolean.class;
            }
            return Object.class;
        }

        public Object getValueAt(int rowIndex, int columnIndex) {
            final ProxyForPlugin proxyForPlugin = model.get(rowIndex);
            switch (columnIndex) {
                case COLUMN_ACTIVE:
                    return proxyForPlugin.isEnabled();
                case COLUMN_PLUGIN:
                    return proxyForPlugin.getPluginId();
                case COLUMN_SERVICES:
                    return proxyForPlugin.getPluginServices();
                case COLUMN_PROXY_SET:
                    return proxyForPlugin.getProxySetName();
                case -1:
                    return proxyForPlugin;
                default:
                    assert false;
            }
            return proxyForPlugin;
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            final ProxyForPlugin data = model.get(rowIndex);
            if (columnIndex == COLUMN_ACTIVE) {
                data.setEnabled((Boolean) aValue);
                manager.updateProxyForPluginItem(data);
            }
            this.fireTableCellUpdated(rowIndex, columnIndex);
        }

        public void intervalAdded(ListDataEvent e) {
            fireTableRowsInserted(e.getIndex0(), e.getIndex1());
        }

        public void intervalRemoved(ListDataEvent e) {
            fireTableRowsDeleted(e.getIndex0(), e.getIndex1());
        }

        public void contentsChanged(ListDataEvent e) {
            fireTableRowsUpdated(e.getIndex0(), e.getIndex1());
        }
    }

    private JXTable table;
    private JButton btnAdd;
    private JButton btnEdit;
    private JButton btnDelete;
    private JButton btnProxySet;
    private JButton btnClose;
}
