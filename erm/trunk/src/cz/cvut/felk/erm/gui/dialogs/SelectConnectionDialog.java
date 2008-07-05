package cz.cvut.felk.erm.gui.dialogs;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.adapter.Bindings;
import com.jgoodies.binding.beans.PropertyConnector;
import com.jgoodies.binding.list.ArrayListModel;
import com.jgoodies.binding.list.SelectionInList;
import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;
import cz.cvut.felk.erm.binding.NullToBooleanConverter;
import cz.cvut.felk.erm.core.AppPrefs;
import cz.cvut.felk.erm.core.UserProp;
import cz.cvut.felk.erm.db.DBConnection;
import cz.cvut.felk.erm.gui.actions.FileActions;
import cz.cvut.felk.erm.gui.managers.ManagerDirector;
import cz.cvut.felk.erm.swing.ComponentFactory;
import cz.cvut.felk.erm.swing.Swinger;
import org.jdesktop.swinghelper.buttonpanel.JXButtonPanel;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Ladislav Vitasek
 */
public class SelectConnectionDialog extends AppDialog {
    private final static Logger logger = Logger.getLogger(SelectConnectionDialog.class.getName());
    private PresentationModel<DBConnection> model;
    private ArrayListModel<DBConnection> connListModel;
    private DBConnection selectedConnection = null;

    public SelectConnectionDialog(Frame owner) throws Exception {
        super(owner, true);

        this.setName("SelectConnectionDialog");
        try {
            initComponents();
            build();
        } catch (Exception e) {
            doClose(); //dialog se pri fatalni chybe zavre
            throw e;
        }
    }


    @org.jdesktop.application.Action
    public void okBtnAction() {
        setSelectedConnection((DBConnection) comboConnections.getSelectedItem());
        setResult(RESULT_OK);
        doClose();
    }


    public List<DBConnection> getConnList() {
        return connListModel;
    }

    @org.jdesktop.application.Action
    public void btnCancelActionAction() {
        doClose();
    }


    @org.jdesktop.application.Action
    public void btnCancelAction() {
        doClose();
    }


    @org.jdesktop.application.Action
    public void btnEditConnectionAction() throws Exception {
        final ManagerDirector managerDirector = getApp().getManagerDirector();
        final FileActions fileActions = managerDirector.getMenuManager().getFileActions();

        final int result = fileActions.updateConnectionsDialog(model.getBean());
        if (result == RESULT_OK) {
            connListModel.clear();
            connListModel.addAll(fileActions.loadDBConnections(managerDirector.getConnectionManager()));
            final String id = AppPrefs.getProperty(UserProp.CONN_EDITOR_LAST_SEL_CONNECTION, "");
            final DBConnection dbConnection = getDBConnectionByID(id, connListModel);
            comboConnections.setSelectedItem(dbConnection);
        }
    }

    @Override
    public void doClose() {
        if (model != null) {
            final DBConnection bean = model.getBean();
            if (bean != null)
                AppPrefs.storeProperty(UserProp.CONN_CHOOSE_LAST_SEL_CONNECTION, bean.getId());
            model.setBean(null);
            model.release();
        }
        super.doClose();
    }

    @Override
    protected AbstractButton getBtnCancel() {
        return btnCancel;
    }

    @Override
    protected AbstractButton getBtnOK() {
        return btnOK;
    }


    private void build() throws CloneNotSupportedException {
        inject();
        buildGUI();

        final ActionMap map = getActionMap();
        btnOK.setAction(map.get("okBtnAction"));
        btnCancel.setAction(map.get("btnCancelAction"));
        btnEditConnection.setAction(map.get("btnEditConnectionAction"));
        setContextHelp(btnHelp, "http://seznam.cz");

        buildModels();

        setDefaultValues();

        pack();
        setResizable(true);
        locateOnOpticalScreenCenter(this);
    }

    private void setDefaultValues() {
        final String id = AppPrefs.getProperty(UserProp.CONN_CHOOSE_LAST_SEL_CONNECTION, "");
        final DBConnection selectedConnection = getDBConnectionByID(id, connListModel);

        if (selectedConnection == null && comboConnections.getModel().getSize() > 0)
            comboConnections.setSelectedIndex(0);
        else {
            comboConnections.setSelectedItem(selectedConnection);
            if (selectedConnection != null && selectedConnection.isValid())
                Swinger.inputFocus(btnOK);
        }

    }

    private DBConnection getDBConnectionByID(String id, List<DBConnection> connections) {
        for (DBConnection conn : connections) {
            if (id.equals(conn.getId())) {
                logger.info("Found last selected connection with an ID " + id);
                return conn;
            }
        }
        return null;
    }

    private void buildGUI() {
        final ConnectionEditorDialog.DBConnectionCellRenderer renderer = new ConnectionEditorDialog.DBConnectionCellRenderer(Swinger.getResourceMap(ConnectionEditorDialog.class));
        comboConnections.setRenderer(renderer);

    }


    private void buildModels() throws CloneNotSupportedException {
        model = new PresentationModel<DBConnection>((ValueModel) null);

        bindBasicComponents();
    }


    private void bindBasicComponents() {
        final ManagerDirector managerDirector = getApp().getManagerDirector();
        final FileActions fileActions = managerDirector.getMenuManager().getFileActions();
        final List<DBConnection> dbConnectionList = fileActions.loadDBConnections(managerDirector.getConnectionManager());
        connListModel = new ArrayListModel<DBConnection>(dbConnectionList);
        final SelectionInList<DBConnection> connsSelectionInList = new SelectionInList<DBConnection>((ListModel) connListModel, model.getBeanChannel());
        Bindings.bind(this.comboConnections, connsSelectionInList);

        //final PropertyAdapter<SelectionInList<DBConnection>> isSelectionEmptyAdapter = new PropertyAdapter<SelectionInList<DBConnection>>(connsSelectionInList, SelectionInList.PROPERTYNAME_SELECTION_EMPTY, true);
        //final ValueModel emptyAdapter = ConverterFactory.createBooleanNegator(isSelectionEmptyAdapter);

        //PropertyConnector.connectAndUpdate(emptyAdapter, btnEditConnection.getAction(), "enabled");        
        PropertyConnector.connectAndUpdate(model.getModel(DBConnection.DESC_PROPERTY), labelDescription, "text");
        PropertyConnector.connectAndUpdate(new NullToBooleanConverter(model.getModel("valid")), btnOK.getAction(), "enabled");
    }


    private void initComponents() {
        // Generated using JFormDesigner Open Source Project license - unknown
        //ResourceBundle bundle = ResourceBundle.getBundle("ChooseConnectionDialog");
        JPanel dialogPane = new JPanel();
        JXButtonPanel buttonBar = new JXButtonPanel();
        btnHelp = new JButton();
        btnOK = new JButton();
        btnCancel = new JButton();
        JPanel contentPanel = new JPanel();
        JLabel labelSelectConnection = new JLabel();
        comboConnections = new JComboBox();
        btnEditConnection = new JButton();
        labelDescription = new JLabel();
        CellConstraints cc = new CellConstraints();

        //======== this ========
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== dialogPane ========
        {
            dialogPane.setBorder(Borders.DIALOG_BORDER);
            dialogPane.setLayout(new BorderLayout());

            //======== buttonBar ========
            {
                buttonBar.setBorder(Borders.BUTTON_BAR_GAP_BORDER);
                buttonBar.setCyclic(true);
                buttonBar.setLayout(new FormLayout(
                        new ColumnSpec[]{
                                FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                FormFactory.DEFAULT_COLSPEC,
                                FormFactory.GLUE_COLSPEC,
                                ComponentFactory.BUTTON_COLSPEC,
                                FormFactory.RELATED_GAP_COLSPEC,
                                ComponentFactory.BUTTON_COLSPEC
                        },
                        RowSpec.decodeSpecs("pref")));
                ((FormLayout) buttonBar.getLayout()).setColumnGroups(new int[][]{{2, 4, 6}});

                //---- btnHelp ----
                btnHelp.setName("btnHelp");
                buttonBar.add(btnHelp, cc.xy(2, 1));

                //---- btnOK ----
                btnOK.setName("btnOK");
                buttonBar.add(btnOK, cc.xy(4, 1));

                //---- btnCancel ----
                btnCancel.setName("btnCancel");
                buttonBar.add(btnCancel, cc.xy(6, 1));
            }
            dialogPane.add(buttonBar, BorderLayout.SOUTH);

            //======== contentPanel ========
            {
                contentPanel.setLayout(new FormLayout(
                        new ColumnSpec[]{
                                FormFactory.DEFAULT_COLSPEC,
                                FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                new ColumnSpec("max(pref;70dlu):grow"),
                                FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                FormFactory.DEFAULT_COLSPEC
                        },
                        new RowSpec[]{
                                FormFactory.DEFAULT_ROWSPEC,
                                FormFactory.LINE_GAP_ROWSPEC,
                                new RowSpec("max(min;15dlu)")
                        }));

                //---- labelSelectConnection ----
                labelSelectConnection.setName("labelSelectConnection");
                labelSelectConnection.setLabelFor(comboConnections);
                contentPanel.add(labelSelectConnection, cc.xy(1, 1));
                contentPanel.add(comboConnections, cc.xy(3, 1));

                //---- btnEditConnection ----
                btnEditConnection.setName("btnEditConnection");
                contentPanel.add(btnEditConnection, cc.xy(5, 1));

                //---- labelDescription ----
                labelDescription.setName("labelDescription");
                contentPanel.add(labelDescription, cc.xywh(3, 3, 3, 1, CellConstraints.DEFAULT, CellConstraints.TOP));
            }
            dialogPane.add(contentPanel, BorderLayout.NORTH);
        }
        contentPane.add(dialogPane, BorderLayout.CENTER);
    }

    private JComboBox comboConnections;

    private JButton btnEditConnection;
    private JLabel labelDescription;
    private JButton btnHelp;
    private JButton btnOK;
    private JButton btnCancel;

    public void setSelectedConnection(DBConnection selectedConnection) {
        this.selectedConnection = selectedConnection;
    }

    public DBConnection getSelectedConnection() {
        return selectedConnection;
    }
}
