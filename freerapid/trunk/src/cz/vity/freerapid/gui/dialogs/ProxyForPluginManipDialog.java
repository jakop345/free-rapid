package cz.vity.freerapid.gui.dialogs;

import com.jgoodies.common.collect.ArrayListModel;
import com.jgoodies.common.swing.MnemonicUtils;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.*;
import cz.vity.freerapid.gui.managers.ManagerDirector;
import cz.vity.freerapid.gui.managers.ProxyForPluginManager;
import cz.vity.freerapid.model.PluginMetaData;
import cz.vity.freerapid.model.ProxyForPlugin;
import cz.vity.freerapid.model.ProxySet;
import cz.vity.freerapid.swing.Swinger;
import cz.vity.freerapid.swing.components.FindTableAction;
import cz.vity.freerapid.utilities.LogUtils;
import org.jdesktop.swinghelper.buttonpanel.JXButtonPanel;
import org.jdesktop.swingx.JXTable;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Vector;
import java.util.logging.Logger;

/**
 * @author tong2shot
 */
public class ProxyForPluginManipDialog extends AppDialog implements ListSelectionListener {
    private final static Logger logger = Logger.getLogger(ProxyForPluginManipDialog.class.getName());

    enum ManipType {
        ADD,
        EDIT
    }

    private static final int COLUMN_ID = 0;
    private static final int COLUMN_SERVICES = 1;
    private static final String SELECTED_ACTION_ENABLED_PROPERTY = "selectedEnabled";
    private boolean selectedEnabled;

    private final ManagerDirector director;
    private final ProxyForPluginManager manager;
    private final ProxyForPlugin proxyForPlugin;
    private final ManipType manipType;

    public ProxyForPluginManipDialog(JDialog owner, ManagerDirector director, ManipType manipType, ProxyForPlugin proxyForPlugin) throws HeadlessException {
        super(owner, true);
        this.director = director;
        this.manager = director.getProxyForPluginManager();
        this.proxyForPlugin = proxyForPlugin;
        this.manipType = manipType;
        this.setName("ProxyForPluginManipDialog");
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
        return btnOk;
    }

    @Override
    protected AbstractButton getBtnCancel() {
        return btnCancel;
    }

    private void build() {
        inject();
        buildModels();
        buildGUI();

        setAction(btnOk, "btnOkAction");
        setAction(btnCancel, "btnCancelAction");

        setTitle(getTitle() + " - " + (manipType == ManipType.ADD ? getResourceMap().getString("titleAdd")
                : getResourceMap().getString("titleEdit")));

        MnemonicUtils.configure(btnOk, (manipType == ManipType.ADD ? getResourceMap().getString("btnOkAddText")
                : getResourceMap().getString("btnOkEditText")));

        pack();
        setResizable(true);
        locateOnOpticalScreenCenter(this);
    }

    @SuppressWarnings("unchecked")
    private void buildModels() {
        //
    }

    private void buildGUI() {
        initTable();
        java.util.List<ProxySet> items = new LinkedList<ProxySet>(director.getProxySetManager().getItems());
        Collections.sort(items, new Comparator<ProxySet>() {
            @Override
            public int compare(ProxySet o1, ProxySet o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        cbbProxySet.setModel(new DefaultComboBoxModel<ProxySet>(new Vector<ProxySet>(items)));
        cbbProxySet.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateActions();
            }
        });

        if (manipType == ManipType.EDIT) {
            int selectedIndex = -1;
            String pluginName = proxyForPlugin.getPluginId();
            ArrayListModel<PluginMetaData> tableItems = getItems();
            for (int i = 0; i < tableItems.size(); i++) {
                PluginMetaData plugin = tableItems.get(i);
                if (pluginName.equals(plugin.getId())) {
                    selectedIndex = pluginTable.convertRowIndexToView(i);
                    break;
                }
            }
            pluginTable.getSelectionModel().setSelectionInterval(selectedIndex, selectedIndex);
            pluginTable.scrollRowToVisible(selectedIndex);

            selectedIndex = -1;
            String proxySetName = proxyForPlugin.getProxySetName();
            ComboBoxModel<ProxySet> comboBoxModel = cbbProxySet.getModel();
            for (int i = 0; i < comboBoxModel.getSize(); i++) {
                ProxySet proxySet = comboBoxModel.getElementAt(i);
                if (proxySetName.equals(proxySet.getName())) {
                    selectedIndex = i;
                    break;
                }
            }
            cbbProxySet.setSelectedIndex(selectedIndex);
        }
    }

    private void initTable() {
        final ArrayListModel<PluginMetaData> plugins = new ArrayListModel<PluginMetaData>(director.getPluginsManager().getSupportedPlugins());
        pluginTable.setName("pluginTable");
        pluginTable.setModel(new CustomTableModel(plugins, getList("columns", 2)));
        pluginTable.setAutoCreateColumnsFromModel(false);
        pluginTable.setEditable(false);
        pluginTable.setColumnControlVisible(true);
        pluginTable.setSortable(true);
        pluginTable.setColumnMargin(10);
        pluginTable.setRolloverEnabled(true);
        pluginTable.setShowGrid(true, true);
        pluginTable.setColumnSelectionAllowed(false);
        pluginTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        pluginTable.createDefaultColumnsFromModel();

        pluginTable.getSelectionModel().addListSelectionListener(this);

        pluginTable.setSortOrder(COLUMN_ID, SortOrder.ASCENDING);

        new FindTableAction(Swinger.getResourceMap(), COLUMN_ID) {
            @Override
            protected Object getObject(int index, int column) {
                return pluginTable.getModel().getValueAt(index, column);
            }
        }.install(pluginTable);

        pluginTable.getParent().setPreferredSize(new Dimension(600, 400));
    }

    @Override
    public void doClose() {
        super.doClose();
    }

    @org.jdesktop.application.Action(enabledProperty = SELECTED_ACTION_ENABLED_PROPERTY)
    public void btnOkAction() {
        if (!validateChanges())
            return;
        setResult(RESULT_OK);
        final int[] indexes = getSelectedRows();
        final int index = indexes[0];
        final PluginMetaData pluginMetaData = getItems().get(index);
        final ProxySet proxySet = cbbProxySet.getItemAt(cbbProxySet.getSelectedIndex());
        final Long proxySetDbId = proxySet.getIdentificator();
        final String pluginName = pluginMetaData.getId();
        if (manipType == ManipType.ADD) {
            manager.addProxyForPluginItem(new ProxyForPlugin(pluginName, proxySetDbId));
        } else {
            proxyForPlugin.setPluginId(pluginName);
            proxyForPlugin.setProxySetId(proxySetDbId);
            manager.updateProxyForPluginItem(proxyForPlugin);
        }
        doClose();
    }

    @org.jdesktop.application.Action
    public void btnCancelAction() {
        setResult(RESULT_CANCEL);
        doClose();
    }

    private boolean validateChanges() {
        if (getSelectedRows().length <= 0) {
            Swinger.showErrorMessage(getResourceMap(), "noPluginSelected");
            Swinger.inputFocus(pluginTable);
            return false;
        }

        final int[] indexes = getSelectedRows();
        final int index = indexes[0];
        final PluginMetaData pluginMetaData = getItems().get(index);
        final String name = pluginMetaData.getId();
        if ((manipType == ManipType.EDIT && !name.equals(proxyForPlugin.getPluginId()) && manager.isPluginExists(name))
                || (manipType == ManipType.ADD && manager.isPluginExists(name))) {
            Swinger.showErrorMessage(getResourceMap(), "pluginExists", name);
            Swinger.inputFocus(pluginTable);
            return false;
        }

        if (cbbProxySet.getSelectedIndex() == -1) {
            Swinger.showErrorMessage(getResourceMap(), "noProxySetSelected");
            Swinger.inputFocus(cbbProxySet);
            return false;
        }
        return true;
    }

    @SuppressWarnings({"deprecation"})
    private void initComponents() {
        JPanel dialogPane = new JPanel();
        JPanel contentPanel = new JPanel();

        JLabel lblProxySet = new JLabel();
        JScrollPane scrollPane1 = new JScrollPane();
        pluginTable = new JXTable();
        cbbProxySet = new JComboBox<ProxySet>();
        lblProxySet.setName("lblProxySet");
        lblProxySet.setLabelFor(cbbProxySet);

        JXButtonPanel buttonBar = new JXButtonPanel();
        btnOk = new JButton();
        btnCancel = new JButton();
        btnOk.setName("btnOk");
        btnCancel.setName("btnCancel");
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
                //======== scrollPane1 ========
                {
                    scrollPane1.setViewportView(pluginTable);
                }

                PanelBuilder contentPanelBuilder = new PanelBuilder(new FormLayout(
                        new ColumnSpec[]{
                                FormSpecs.DEFAULT_COLSPEC,
                                FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
                                new ColumnSpec(ColumnSpec.FILL, Sizes.bounded(Sizes.PREFERRED, Sizes.dluX(140), Sizes.dluX(255)), FormSpec.DEFAULT_GROW)
                        },
                        new RowSpec[]{
                                new RowSpec(RowSpec.FILL, Sizes.bounded(Sizes.PREFERRED, Sizes.dluY(140), Sizes.dluY(255)), FormSpec.DEFAULT_GROW),
                                FormSpecs.UNRELATED_GAP_ROWSPEC,
                                FormSpecs.DEFAULT_ROWSPEC,
                        }), contentPanel);
                contentPanelBuilder.add(scrollPane1, cc.xyw(1, 1, 3));
                contentPanelBuilder.add(lblProxySet, cc.xy(1, 3));
                contentPanelBuilder.add(cbbProxySet, cc.xy(3, 3));

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

                buttonBarBuilder.add(btnOk, cc.xy(3, 1));
                buttonBarBuilder.add(btnCancel, cc.xy(5, 1));
            }
            dialogPane.add(buttonBar, BorderLayout.SOUTH);
        }
        contentPane.add(dialogPane, BorderLayout.CENTER);
    }

    private ArrayListModel<PluginMetaData> getItems() {
        return ((CustomTableModel) pluginTable.getModel()).model;
    }

    private int[] getSelectedRows() {
        return Swinger.getSelectedRows(pluginTable);
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

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) {
            return;
        }
        updateActions();
    }


    private static class CustomTableModel extends AbstractTableModel {
        private final ArrayListModel<PluginMetaData> model;
        private final String[] columns;


        public CustomTableModel(ArrayListModel<PluginMetaData> model, String[] columns) {
            super();
            this.model = model;
            this.columns = columns;
        }

        public int getRowCount() {
            return model.getSize();
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return false;
        }

        @Override
        public String getColumnName(int column) {
            return this.columns[column];
        }

        public int getColumnCount() {
            return this.columns.length;
        }

        public Object getValueAt(int rowIndex, int columnIndex) {
            final PluginMetaData plugin = model.get(rowIndex);
            switch (columnIndex) {
                case COLUMN_ID:
                    return plugin.getId();
                case COLUMN_SERVICES:
                    return plugin.getServices();
                case -1:
                    return plugin;
                default:
                    assert false;
            }
            return plugin;
        }
    }

    private JXTable pluginTable;
    private JComboBox<ProxySet> cbbProxySet;
    private JButton btnOk;
    private JButton btnCancel;
}
