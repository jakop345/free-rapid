package cz.cvut.felk.erm.gui.dialogs;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.adapter.Bindings;
import com.jgoodies.binding.beans.PropertyAdapter;
import com.jgoodies.binding.beans.PropertyConnector;
import com.jgoodies.binding.list.ArrayListModel;
import com.jgoodies.binding.list.SelectionInList;
import com.jgoodies.binding.value.AbstractValueModel;
import com.jgoodies.binding.value.ConverterFactory;
import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.*;
import cz.cvut.felk.erm.core.AppPrefs;
import cz.cvut.felk.erm.core.UserProp;
import cz.cvut.felk.erm.core.tasks.CoreTask;
import cz.cvut.felk.erm.db.DBConnection;
import cz.cvut.felk.erm.db.DatabaseDriverManager;
import cz.cvut.felk.erm.db.tasks.DBTestConnectionTask;
import cz.cvut.felk.erm.gui.dialogs.filechooser.OpenSaveDialogFactory;
import cz.cvut.felk.erm.swing.ComponentFactory;
import cz.cvut.felk.erm.swing.Swinger;
import cz.cvut.felk.erm.utilities.Utils;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.Task;
import org.jdesktop.swinghelper.buttonpanel.JXButtonPanel;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Ladislav Vitasek
 */
public class ConnectionEditorDialog extends AppDialog {
    private final static Logger logger = Logger.getLogger(ConnectionEditorDialog.class.getName());
    private PresentationModel<DBConnection> model;
    private JList listConnections;
    private ArrayListModel<String> driversListModel = new ArrayListModel<String>(3);
    private ArrayListModel<DBConnection> connListModel;
    private final List<DBConnection> connections;
    private DBConnection selectedConnection = null;


    public ConnectionEditorDialog(Frame owner, List<DBConnection> connections) throws Exception {
        this(owner, connections, null);
    }

    public ConnectionEditorDialog(Frame owner, List<DBConnection> connections, DBConnection defaultSelected) throws Exception {
        super(owner, true);
        this.connections = connections;
        this.selectedConnection = defaultSelected;

        this.setName("ConnectionEditorDialog");
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


    private String getNewNameForConnection() {
        final ResourceMap map = getResourceMap();

        final String label = "defaultConnectionName";
        for (int i = 1; i < 30000; ++i) {
            boolean found = false;
            for (DBConnection conn : connListModel) {
                if ((map.getString(label, i).equals(conn.getName()))) {
                    found = true;
                    break;
                }
            }
            if (!found)
                return map.getString(label, i);
        }
        return map.getString(label);
    }

    @org.jdesktop.application.Action
    public void btnConnectionAddAction() {
        final DBConnection conn = new DBConnection(true);
        conn.setName(getNewNameForConnection());
        connListModel.add(conn);
        listConnections.setSelectedValue(conn, true);
        Swinger.inputFocus(listConnections);
    }

    @org.jdesktop.application.Action
    public void btnConnectionRemoveAction() {
        if (listConnections.isSelectionEmpty())
            return;
        final DBConnection selected = (DBConnection) listConnections.getSelectedValue();
        int index = listConnections.getSelectedIndex();
        connListModel.remove(selected);
        index = Math.min(connListModel.size() - 1, index);
        if (index != -1)
            listConnections.setSelectedIndex(index);
        Swinger.inputFocus(listConnections);
    }

    @org.jdesktop.application.Action
    public void btnConnectionCopyAction() {
        if (listConnections.isSelectionEmpty())
            return;
        final DBConnection selected = (DBConnection) listConnections.getSelectedValue();
        final DBConnection copy = selected.doCopy();
        String name = selected.getName();
        if (name == null)
            name = "";
        copy.generateNewId();
        final String copyString = getResourceMap().getString("defaultConnectionNameCopy");
        if (!name.endsWith(copyString))
            copy.setName(name + " " + copyString);
        connListModel.add(copy);
        this.listConnections.setSelectedValue(copy, true);
        Swinger.inputFocus(listConnections);
    }

    @org.jdesktop.application.Action(block = Task.BlockingScope.WINDOW)
    public Task btnInfoAction() {
        final DBConnection connection = (DBConnection) listConnections.getSelectedValue();
        if (!validateForm())
            return null;
        return new DBTestConnectionTask(connection, true);
    }

    @org.jdesktop.application.Action(block = Task.BlockingScope.WINDOW)
    public Task btnTestAction() {
        final DBConnection connection = (DBConnection) listConnections.getSelectedValue();
        if (!validateForm())
            return null;
        return new DBTestConnectionTask(connection, false);
    }

    @org.jdesktop.application.Action(block = Task.BlockingScope.WINDOW)
    public void btnSelectLibraryAction() {
        final File[] files = OpenSaveDialogFactory.getChooseJARorZIPFileDialog();
        if (files.length > 0) {
            libraryField.setText(files[0].getPath());
            Swinger.inputFocus((JComponent) driverCombo);
        }
    }

    private boolean validateForm() {
        final DBConnection bean = model.getBean();
        if (!Utils.hasValue(bean.getDriver())) {
            Swinger.showErrorMessage(getResourceMap(), "validate");
            Swinger.inputFocus(driverCombo);
            return false;
        }
        if (!Utils.hasValue(bean.getUrl())) {
            Swinger.showErrorMessage(getResourceMap(), "validate");
            Swinger.inputFocus(urlField);
            return false;
        }
        if (!Utils.hasValue(bean.getUser())) {
            Swinger.showErrorMessage(getResourceMap(), "validate");
            Swinger.inputFocus(userField);
            return false;
        }
        return true;
    }


    @org.jdesktop.application.Action
    public void btnCancelAction() {
        doClose();
    }


    @Override
    public void doClose() {
        final DBConnection bean = model.getBean();
        if (bean != null)
            AppPrefs.storeProperty(UserProp.CONN_EDITOR_LAST_SEL_CONNECTION, bean.getId());
        super.doClose();
        model.setBean(null);
        if (model != null) {
            model.release();
        }
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

        setAction(btnOK, ("okBtnAction"));
        setAction(btnCancel, ("btnCancelAction"));
        setAction(btnConnectionAdd, ("btnConnectionAddAction"));
        setAction(btnConnectionRemove, ("btnConnectionRemoveAction"));
        setAction(btnConnectionCopy, ("btnConnectionCopyAction"));
        setAction(btnTest, ("btnTestAction"));
        setAction(btnSelectLibrary, ("btnSelectLibraryAction"));
        setAction(btnInfo, ("btnInfoAction"));
        setAction(btnHelp, ("contextDialogHelpAction"));

        buildModels();

        setDefaultValues();


        pack();
        setResizable(true);
        locateOnOpticalScreenCenter(this);
    }

    private void setDefaultValues() {
        final boolean makeEdit = selectedConnection != null;
        if (selectedConnection == null) {
            final String id = AppPrefs.getProperty(UserProp.CONN_EDITOR_LAST_SEL_CONNECTION, "");
            for (DBConnection conn : connections) {
                if (id.equals(conn.getId())) {
                    logger.info("Found last selected connection with an ID " + id);
                    this.selectedConnection = conn;
                    break;
                }
            }
        }
        if (this.selectedConnection == null && listConnections.getModel().getSize() > 0)
            listConnections.setSelectedIndex(0);
        else {
            listConnections.setSelectedValue(this.selectedConnection, true);
            if (makeEdit)
                activateEditing();
        }
    }

    private void buildGUI() {

        libraryField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                updateDriverList();
            }

            public void removeUpdate(DocumentEvent e) {
                updateDriverList();
            }

            public void changedUpdate(DocumentEvent e) {
                updateDriverList();
            }
        });
        listConnections.setCellRenderer(new DBConnectionCellRenderer(getResourceMap()));
        listConnections.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listConnections.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() >= 2)
                    activateEditing();
            }
        });
        listConnections.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER)
                    activateEditing();
            }
        });
        setContextHelp(btnHelp, "http://www.seznam.cz");
    }

    private void activateEditing() {
        Swinger.inputFocus(nameField);
    }

    private void updateDriverList() {
        final DBConnection bean = model.getBean();
        if (bean == null)
            return;
        final String path = bean.getDriverLibrary();
        final File file = new File(path);
        if (!(file.exists() && file.isFile())) {
            driversListModel.clear();
            return;
        }
        final Task<String[], Void> task = new CoreTask<String[], Void>(getApp(), getResourceMap(), null) {


            protected String[] doInBackground() throws Exception {
                logger.info("Browsing JAR " + path);
                message("taskBrowsingJar");
                return DatabaseDriverManager.getInstance().loadDriverClasses(path);
            }

            @Override
            protected void succeeded(final String[] result) {
                if (result.length == 0) {
                    Swinger.showErrorMessage(this.getResourceMap(), "errormessage_noDriverFound");
                    return;
                }
                final Object value = model.getBean().getDriver();
                driversListModel = new ArrayListModel<String>(Arrays.asList(result));
                Bindings.bind(driverCombo, new SelectionInList<String>((ListModel) driversListModel, model.getModel(DBConnection.DRIVER_PROPERTY)));
//                driversListModel.clear();
//
//                boolean changed = model.isChanged();
//                dontMakeUpdates = true;
//                driversListModel.clear();
//                driversListModel.addAll(Arrays.asList(result));
//                dontMakeUpdates = false;
//                if (!changed && model.isChanged())
//                    model.resetChanged();
//
                if (value != null) {
                    driverCombo.setSelectedItem(value);
                } else {
                    driverCombo.setSelectedIndex(0);
                }
            }

            @Override
            protected void failed(Throwable cause) {
                Swinger.showErrorDialog(getResourceMap(), "errormessage_taskBrowsingJarFailed", cause);
            }
        };
        getApp().getContext().getTaskService().execute(task);
    }

    private void buildModels() throws CloneNotSupportedException {


        model = new PresentationModel<DBConnection>((ValueModel) null);

        bindBasicComponents();

        final ActionMap map = getActionMap();
        final javax.swing.Action actionOK = map.get("okBtnAction");
        assert actionOK != null;

        listConnections.getModel().addListDataListener(new ListDataListener() {
            public void intervalAdded(ListDataEvent e) {
                contentsChanged(e);
            }

            public void intervalRemoved(ListDataEvent e) {
                contentsChanged(e);
            }

            public void contentsChanged(ListDataEvent e) {
                actionOK.setEnabled(true);
            }
        });
        model.addBeanPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                actionOK.setEnabled(true);
                final String s = evt.getPropertyName();
                if (DBConnection.TESTED_PROPERTY.equals(s)) {
                    listConnections.repaint();
                    return;
                }
                if (DBConnection.DRIVER_PROPERTY.equals(s) || DBConnection.URL_PROPERTY.equals(s) || DBConnection.USER_PROPERTY.equals(s) || DBConnection.PASSWORD_PROPERTY.equals(s)) {
                    model.getBean().setTested(false);
                    listConnections.repaint();//valid/invalid
                }
            }
        });
        actionOK.setEnabled(false);
//        model.resetChanged();
//        PropertyConnector connector = PropertyConnector.connect(model, PresentationModel.PROPERTYNAME_CHANGED, actionOK, "enabled");
//        connector.updateProperty2();
    }

//    private ActionMap getActionMap() {
//        return Swinger.getActionMap(this.getClass(), this);
//    }


    private void bindBasicComponents() {

        final AbstractValueModel namePropertyModel = model.getModel(DBConnection.NAME_PROPERTY);
        Bindings.bind(this.nameField, namePropertyModel);

        namePropertyModel.addValueChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                listConnections.repaint();
            }
        });

        Bindings.bind(this.descriptionField, model.getModel(DBConnection.DESC_PROPERTY));
        Bindings.bind(this.libraryField, model.getModel(DBConnection.DRIVER_LIBRARY_PROPERTY));

        Bindings.bind(this.driverCombo, new SelectionInList<String>((ListModel) driversListModel, model.getModel(DBConnection.DRIVER_PROPERTY)));
        //PropertyConnector.connectAndUpdate(model.getModel(ConnectionSettings.DRIVER_PROPERTY), driverCombo, "");
        //driverCombo.setSelectedItem();

        Bindings.bind(this.urlField, model.getModel(DBConnection.URL_PROPERTY));
        Bindings.bind(this.passwordField, model.getModel(DBConnection.PASSWORD_PROPERTY));
        Bindings.bind(this.userField, model.getModel(DBConnection.USER_PROPERTY));


        connListModel = new ArrayListModel<DBConnection>(doDeepCopy(connections));
        final SelectionInList<DBConnection> connsSelectionInList = new SelectionInList<DBConnection>((ListModel) connListModel, model.getBeanChannel());
        Bindings.bind(this.listConnections, connsSelectionInList);

        final PropertyAdapter<SelectionInList<DBConnection>> isSelectionEmptyAdapter = new PropertyAdapter<SelectionInList<DBConnection>>(connsSelectionInList, SelectionInList.PROPERTYNAME_SELECTION_EMPTY, true);
        final ValueModel emptyAdapter = ConverterFactory.createBooleanNegator(isSelectionEmptyAdapter);
        PropertyConnector.connectAndUpdate(emptyAdapter, btnConnectionRemove.getAction(), "enabled");
        PropertyConnector.connectAndUpdate(emptyAdapter, btnConnectionCopy.getAction(), "enabled");
        PropertyConnector.connectAndUpdate(emptyAdapter, panelParameters, "visible");
    }

    private List<DBConnection> doDeepCopy(List<DBConnection> connections) {
        final List<DBConnection> result = new ArrayList<DBConnection>();
        for (DBConnection conn : connections) {
            result.add(conn.doCopy());
        }
        return result;
    }

    /**
     * final TitledBorder border = new TitledBorder(null, getResourceMap().getString("titleBorder.connectionParameters"), TitledBorder.LEFT, TitledBorder.DEFAULT_POSITION);
     * panelParameters.setBorder(new CompoundBorder(
     * new EmptyBorder(4, 4, 4, 4),
     * border));
     */

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Open Source Project license - unknown
        //ResourceBundle bundle = ResourceBundle.getBundle("ConnectionEditor");
        JPanel dialogPane = new JPanel();
        JPanel contentPanel = new JPanel();
        JPanel toolbar = new JPanel();
        btnConnectionAdd = new JButton();
        btnConnectionRemove = new JButton();
        //JSeparator separator = new JSeparator();
        btnConnectionCopy = new JButton();
        JSplitPane splitPane = new JSplitPane();
        JPanel splitLeftPanel = new JPanel();
        JScrollPane scrollPane1 = new JScrollPane();
        listConnections = new JList();
        panelParameters = new JPanel();
        JLabel labelName = new JLabel();
        nameField = ComponentFactory.getTextField();
        JLabel labelDescription = new JLabel();
        descriptionField = ComponentFactory.getTextField();
        JLabel labelLibrary = new JLabel();
        libraryField = ComponentFactory.getTextField();
        btnSelectLibrary = new JButton();
        JLabel labelDriver = new JLabel();
        driverCombo = new JComboBox();
        JLabel labelURL = new JLabel();
        urlField = ComponentFactory.getTextField();
        JLabel labelUser = new JLabel();
        userField = ComponentFactory.getTextField();
        JLabel labelPassword = new JLabel();
        passwordField = ComponentFactory.getPasswordField();
        JLabel labelWarning = new JLabel();
        JXButtonPanel btnPanel2 = new JXButtonPanel();
        btnTest = new JButton();
        btnInfo = new JButton();
        JXButtonPanel buttonBar = new JXButtonPanel();
        btnHelp = new JButton();
        btnOK = new JButton();
        btnCancel = new JButton();
        CellConstraints cc = new CellConstraints();

        //======== this ========
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== dialogPane ========
        {
            dialogPane.setBorder(Borders.DIALOG_BORDER);
            //dialogPane.setName(bundle.getString("dialogPane.name"));
            dialogPane.setLayout(new BorderLayout());

            //======== contentPanel ========
            {
                contentPanel.setLayout(new FormLayout(
                        "default:grow",
                        "default, fill:default:grow"));

                //======== toolbar ========
                {
                    toolbar.setBorder(new EmptyBorder(5, 5, 0, 5));
                    toolbar.setOpaque(false);
                    toolbar.setLayout(new FormLayout(
                            new ColumnSpec[]{
                                    FormFactory.PREF_COLSPEC,
                                    FormFactory.PREF_COLSPEC,
                                    FormFactory.PREF_COLSPEC,
                                    FormFactory.PREF_COLSPEC,
                                    FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                    new ColumnSpec(ColumnSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW)
                            },
                            RowSpec.decodeSpecs("pref:grow")));
                    ((FormLayout) toolbar.getLayout()).setColumnGroups(new int[][]{{1, 2, 4}});

                    //---- btnConnectionAdd ----
                    btnConnectionAdd.setPreferredSize(new Dimension(26, 23));
                    toolbar.add(btnConnectionAdd, cc.xy(1, 1));

                    //---- btnConnectionRemove ----
                    btnConnectionRemove.setPreferredSize(new Dimension(26, 23));
                    toolbar.add(btnConnectionRemove, cc.xy(2, 1));

                    toolbar.add(Box.createHorizontalStrut(3), cc.xy(3, 1));

                    //---- btnConnectionCopy ----
                    btnConnectionCopy.setPreferredSize(new Dimension(26, 23));
                    toolbar.add(btnConnectionCopy, cc.xy(4, 1));
                }
                contentPanel.add(toolbar, cc.xy(1, 1));

                //======== splitPane ========
                {
                    splitPane.setResizeWeight(0.0010);
                    splitPane.setBorder(new EmptyBorder(5, 5, 5, 0));
                    splitPane.setDividerLocation(150);
                    splitPane.setDividerSize(7);

                    //======== splitLeftPanel ========
                    {
                        splitLeftPanel.setMinimumSize(new Dimension(102, 24));
                        splitLeftPanel.setPreferredSize(new Dimension(102, 116));
                        splitLeftPanel.setLayout(new FormLayout(
                                new ColumnSpec[]{
                                        new ColumnSpec(ColumnSpec.FILL, Sizes.bounded(Sizes.PREFERRED, Sizes.dluX(100), Sizes.dluX(130)), FormSpec.DEFAULT_GROW)
                                },
                                RowSpec.decodeSpecs("fill:default:grow")));

                        //======== scrollPane1 ========
                        {

                            //---- listConnections ----
                            listConnections.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                            listConnections.setBorder(new EmptyBorder(2, 2, 2, 2));
                            scrollPane1.setViewportView(listConnections);
                        }
                        splitLeftPanel.add(scrollPane1, cc.xy(1, 1));
                    }
                    splitPane.setLeftComponent(splitLeftPanel);

                    //======== panelParameters ========
                    {
                        final TitledBorder border = new TitledBorder(null, getResourceMap().getString("titleBorder.connectionParameters"), TitledBorder.LEFT, TitledBorder.DEFAULT_POSITION);
                        panelParameters.setBorder(new CompoundBorder(
                                new EmptyBorder(4, 4, 4, 4),
                                border));
                        panelParameters.setPreferredSize(new Dimension(400, 260));
                        panelParameters.setLayout(new FormLayout(
                                new ColumnSpec[]{
                                        FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                        FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                        FormFactory.DEFAULT_COLSPEC,
                                        FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                        FormFactory.DEFAULT_COLSPEC,
                                        FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                        new ColumnSpec(ColumnSpec.FILL, Sizes.PREFERRED, FormSpec.DEFAULT_GROW),
                                        FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                        FormFactory.PREF_COLSPEC,
                                        FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                        FormFactory.LABEL_COMPONENT_GAP_COLSPEC
                                },
                                new RowSpec[]{
                                        FormFactory.DEFAULT_ROWSPEC,
                                        FormFactory.LINE_GAP_ROWSPEC,
                                        FormFactory.DEFAULT_ROWSPEC,
                                        FormFactory.LINE_GAP_ROWSPEC,
                                        FormFactory.DEFAULT_ROWSPEC,
                                        FormFactory.LINE_GAP_ROWSPEC,
                                        FormFactory.DEFAULT_ROWSPEC,
                                        FormFactory.LINE_GAP_ROWSPEC,
                                        FormFactory.DEFAULT_ROWSPEC,
                                        FormFactory.LINE_GAP_ROWSPEC,
                                        FormFactory.DEFAULT_ROWSPEC,
                                        FormFactory.LINE_GAP_ROWSPEC,
                                        FormFactory.DEFAULT_ROWSPEC,
                                        FormFactory.LINE_GAP_ROWSPEC,
                                        FormFactory.DEFAULT_ROWSPEC,
                                        FormFactory.PARAGRAPH_GAP_ROWSPEC,
                                        FormFactory.DEFAULT_ROWSPEC,
                                        FormFactory.LINE_GAP_ROWSPEC,
                                        FormFactory.LINE_GAP_ROWSPEC
                                }));
                        //((FormLayout) panelParameters.getLayout()).setRowGroups(new int[][]{{1, 11, 13}, {3, 7, 9}});

                        //---- labelName ----
                        labelName.setName("labelName");
                        labelName.setLabelFor(nameField);
                        panelParameters.add(labelName, cc.xy(3, 1));

                        //---- nameField ----
                        nameField.setPreferredSize(new Dimension(150, 20));
                        panelParameters.add(nameField, cc.xy(5, 1));

                        //---- labelDescription ----
                        labelDescription.setName("labelDescription");
                        labelDescription.setLabelFor(descriptionField);
                        panelParameters.add(labelDescription, cc.xy(3, 3));
                        panelParameters.add(descriptionField, cc.xywh(5, 3, 5, 1));

                        //---- labelLibrary ----
                        labelLibrary.setName("labelLibrary");
                        labelLibrary.setLabelFor(libraryField);
                        panelParameters.add(labelLibrary, cc.xy(3, 5));
                        panelParameters.add(libraryField, cc.xywh(5, 5, 3, 1));

                        //---- btnSelectLibrary ----
                        btnSelectLibrary.setName("btnSelectLibrary");
                        btnSelectLibrary.setPreferredSize(new Dimension(26, 23));
                        panelParameters.add(btnSelectLibrary, cc.xy(9, 5));

                        //---- labelDriver ----
                        labelDriver.setName("labelDriver");
                        labelDriver.setLabelFor(driverCombo);
                        panelParameters.add(labelDriver, cc.xy(3, 7));
                        panelParameters.add(driverCombo, cc.xywh(5, 7, 5, 1));

                        //---- labelURL ----
                        labelURL.setName("labelURL");
                        labelURL.setLabelFor(urlField);
                        panelParameters.add(labelURL, cc.xy(3, 9));
                        panelParameters.add(urlField, cc.xywh(5, 9, 5, 1));

                        //---- labelUser ----
                        labelUser.setName("labelUser");
                        labelUser.setLabelFor(userField);
                        panelParameters.add(labelUser, cc.xy(3, 11));
                        panelParameters.add(userField, cc.xy(5, 11));

                        //---- labelPassword ----
                        labelPassword.setName("labelPassword");
                        labelPassword.setLabelFor(passwordField);
                        panelParameters.add(labelPassword, cc.xy(3, 13));
                        panelParameters.add(passwordField, cc.xy(5, 13));

                        //---- labelWarning ----
                        labelWarning.setName("labelWarning");
                        labelWarning.setForeground(Color.red);
                        panelParameters.add(labelWarning, cc.xywh(5, 15, 3, 1));

                        //======== btnPanel2 ========
                        {
                            btnPanel2.setLayout(new FormLayout(
                                    new ColumnSpec[]{
                                            new ColumnSpec(ColumnSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW),
                                            FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                            FormFactory.DEFAULT_COLSPEC,
                                            FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                            FormFactory.DEFAULT_COLSPEC
                                    },
                                    RowSpec.decodeSpecs("default")));

                            //---- btnTest ----
                            btnTest.setName("btnTest");
                            btnPanel2.add(btnTest, cc.xy(3, 1));

                            //---- btnInfo ----
                            btnInfo.setName("btnInfo");
                            btnPanel2.add(btnInfo, cc.xy(5, 1));
                        }
                        panelParameters.add(btnPanel2, cc.xywh(3, 17, 7, 1));
                    }
                    final JPanel panel = new JPanel(new BorderLayout());
                    panel.add(panelParameters);
                    panel.setMinimumSize(panelParameters.getPreferredSize());
                    splitPane.setRightComponent(panel);
                }
                contentPanel.add(splitPane, cc.xy(1, 2));
            }
            dialogPane.add(contentPanel, BorderLayout.CENTER);

            //======== buttonBar ========
            {
                buttonBar.setBorder(Borders.BUTTON_BAR_GAP_BORDER);
                buttonBar.setCyclic(true);
                buttonBar.setLayout(new FormLayout(
                        new ColumnSpec[]{
                                FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                ComponentFactory.BUTTON_COLSPEC,
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
        }
        contentPane.add(dialogPane, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }


    static class DBConnectionCellRenderer extends DefaultListCellRenderer {
        private final ResourceMap map;

        public DBConnectionCellRenderer(ResourceMap map) {
            this.map = map;
        }

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            final Component comp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            DBConnection conn = (DBConnection) value;
            final String s = conn.getName();
            this.setText((s == null) ? "" : s);
            String icon;
            if (conn.isTested()) {
                icon = "dbConnectionTested";
            } else {
                if (conn.isValid()) {
                    icon = "dbConnectionValid";
                } else {
                    icon = "dbConnectionInvalid";
                    conn.isValid();
                }
            }
            this.setIcon(map.getIcon(icon));
            this.setToolTipText(conn.getDescription());
            return comp;
        }
    }

    private JButton btnConnectionAdd;
    private JButton btnConnectionRemove;
    private JButton btnConnectionCopy;
    private JButton btnHelp;
    private JTextField nameField;
    private JTextField descriptionField;
    private JTextField libraryField;
    private JButton btnSelectLibrary;
    private JComboBox driverCombo;
    private JTextField urlField;
    private JTextField userField;
    private JPasswordField passwordField;
    private JButton btnTest;
    private JButton btnInfo;
    private JButton btnOK;
    private JButton btnCancel;
    private JPanel panelParameters;


}