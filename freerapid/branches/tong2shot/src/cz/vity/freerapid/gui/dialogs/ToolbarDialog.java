package cz.vity.freerapid.gui.dialogs;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.*;
import cz.vity.freerapid.core.AppPrefs;
import cz.vity.freerapid.core.MainApp;
import cz.vity.freerapid.core.UserProp;
import cz.vity.freerapid.gui.managers.ToolbarManager;
import cz.vity.freerapid.utilities.LogUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.logging.Logger;

/**
 * @author birchie
 */
public class ToolbarDialog extends AppDialog {
    private final static Logger logger = Logger.getLogger(ToolbarDialog.class.getName());

    private String defaultToolbarString;
    private String currentToolbarString;
    private String newToolbarString;
    private DefaultListModel toolbarModel;
    private DefaultListModel functionModel;

    private ToolbarManager toolbarManager;

    private boolean toolbarListSelected = false;
    private static final String TOOLBAR_LIST_SELECTED_ACTION_ENABLED_PROPERTY = "toolbarListSelected";
    private boolean functionListSelected = false;
    private static final String FUNCTION_LIST_SELECTED_ACTION_ENABLED_PROPERTY = "functionListSelected";

    public ToolbarDialog(JFrame owner) throws Exception {
        super(owner, true);
        defaultToolbarString = UserProp.CUSTOM_TOOLBAR_BUTTONS_DEFAULT.toUpperCase();
        currentToolbarString = AppPrefs.getProperty(UserProp.CUSTOM_TOOLBAR_BUTTONS, UserProp.CUSTOM_TOOLBAR_BUTTONS_DEFAULT).toUpperCase();
        newToolbarString = currentToolbarString;
        this.setName("ToolbarDialog");
        toolbarManager = MainApp.getInstance(MainApp.class).getManagerDirector().getToolbarManager();
        try {
            initComponents();
            build();
        } catch (Exception e) {
            LogUtils.processException(logger, e);
            doClose();
            throw e;
        }
    }

    private void build() {
        inject();
        loadFunctionModel();
        loadToolbarModel();
        setAction(okButton, "okBtnAction");
        setAction(cancelButton, "cancelBtnAction");
        setAction(defaultButton, "defaultBtnAction");
        setAction(resetButton, "resetBtnAction");

        setAction(addButton, "addBtnAction");
        setAction(removeButton, "removeBtnAction");
        setAction(upButton, "upBtnAction");
        setAction(downButton, "downBtnAction");

        toolbarList.getSelectionModel().addListSelectionListener(new ToolbarListSelectionListener());
        functionsList.getSelectionModel().addListSelectionListener(new ToolbarListSelectionListener());
    }

    void loadFunctionModel() {
        functionModel = new DefaultListModel();

        for (String btn : toolbarManager.getToolbarButtonList()) {
            functionModel.addElement(btn);
        }
        functionsList.setModel(functionModel);
    }

    void loadToolbarModel() {
        toolbarModel = new DefaultListModel();
        for (char btn : newToolbarString.toCharArray()) {
            toolbarModel.addElement(""+btn);
        }
        toolbarModel.addElement(" ");    // blank line at end of list
        toolbarList.setModel(toolbarModel);
    }

        @Override
    protected AbstractButton getBtnOK() {
        return okButton;
    }

    @Override
    protected AbstractButton getBtnCancel() {
        return cancelButton;
    }

    @org.jdesktop.application.Action
    public void okBtnAction() {
        determineToolbarString();
        AppPrefs.storeProperty(UserProp.CUSTOM_TOOLBAR_BUTTONS, newToolbarString);
        setResult(RESULT_OK);
        toolbarManager.reloadToolbar();
        doClose();
    }

    @org.jdesktop.application.Action
    public void cancelBtnAction() {
        setResult(RESULT_CANCEL);
        doClose();
    }

    @org.jdesktop.application.Action
    public void defaultBtnAction() {
        newToolbarString = defaultToolbarString;
        loadToolbarModel();
    }

    @org.jdesktop.application.Action
    public void resetBtnAction() {
        newToolbarString = currentToolbarString;
        loadToolbarModel();
    }

    @org.jdesktop.application.Action(enabledProperty = FUNCTION_LIST_SELECTED_ACTION_ENABLED_PROPERTY)
    public void addBtnAction() {
        String btn = (String) functionsList.getSelectedValue();
        if (btn != null) {
            int index = toolbarList.getSelectedIndex();
            if (index >= 0) {
                toolbarModel.add(index, btn);
                toolbarList.setSelectedIndex(index);
            } else {
                toolbarModel.add(toolbarModel.size()-1, btn);
                toolbarList.setSelectedIndex(toolbarModel.size()-2);
            }
        }
    }

    @org.jdesktop.application.Action(enabledProperty = TOOLBAR_LIST_SELECTED_ACTION_ENABLED_PROPERTY)
    public void removeBtnAction() {
        int index = toolbarList.getSelectedIndex();
        if ((index >= 0) &&  (index < toolbarModel.size()-1))
            toolbarModel.remove(index);
    }

    @org.jdesktop.application.Action(enabledProperty = TOOLBAR_LIST_SELECTED_ACTION_ENABLED_PROPERTY)
    public void upBtnAction() {
        String btn = (String) toolbarList.getSelectedValue();
        if (btn != null) {
            int index = toolbarList.getSelectedIndex();
            if ((index > 0) && (index < toolbarModel.size()-1)) {
                toolbarModel.remove(index);
                toolbarModel.add(index-1, btn);
                toolbarList.setSelectedIndex(index - 1);
            }
        }
    }

    @org.jdesktop.application.Action(enabledProperty = TOOLBAR_LIST_SELECTED_ACTION_ENABLED_PROPERTY)
    public void downBtnAction() {
        String btn = (String) toolbarList.getSelectedValue();
        if (btn != null) {
            int index = toolbarList.getSelectedIndex();
            if (index+1 < toolbarModel.size()-1) {
                toolbarModel.remove(index);
                toolbarModel.add(index + 1, btn);
                toolbarList.setSelectedIndex(index + 1);
            }
        }
    }

    private void determineToolbarString() {
        String out = "";
        for (int ii = 0; ii < functionModel.size(); ii++) {
            try {
                out += (String) toolbarModel.get(ii);
            } catch (Exception x) { /**/ }
        }
        newToolbarString = out.trim();
    }


    private void initComponents() {
        JPanel dialogPane = new JPanel();
        JPanel contentPanel = new JPanel();
        JPanel toolbarPanel = new JPanel();
        JPanel movementPanel = new JPanel();
        JPanel functionsPanel = new JPanel();
        JPanel buttonPanel = new JPanel();

        JLabel toolbarListLabel = new JLabel();
        toolbarList = new JList();
        JScrollPane toolbarScrollPane = new JScrollPane();

        JLabel functionsListLabel = new JLabel();
        functionsList = new JList();
        JScrollPane functionsScrollPane = new JScrollPane();

        addButton = new JButton();
        removeButton = new JButton();
        upButton = new JButton();
        downButton = new JButton();

        defaultButton = new JButton();
        resetButton = new JButton();
        okButton = new JButton();
        cancelButton = new JButton();

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
                //======== toolbarPanel ========
                {
                    toolbarPanel.setLayout(new BoxLayout(toolbarPanel, BoxLayout.PAGE_AXIS));

                    toolbarListLabel.setName("toolbarListLabel");
                    toolbarScrollPane.setViewportView(toolbarList);
                    toolbarScrollPane.setPreferredSize(new Dimension(150, 150));

                    toolbarList.setLayoutOrientation(JList.VERTICAL);
                    toolbarList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                    toolbarList.setCellRenderer(new ToolbarListCellRenderer());

                    toolbarPanel.add(toolbarListLabel);
                    toolbarPanel.add(toolbarScrollPane);
                }
                //======== movementPanel ========
                {
                    movementPanel.setBorder(new EmptyBorder(30, 10, 0, 10));

                    PanelBuilder movementPanelBuilder = new PanelBuilder(new FormLayout(
                            new ColumnSpec[]{
                                    FormSpecs.DEFAULT_COLSPEC
                            },
                            new RowSpec[]{
                                    FormSpecs.DEFAULT_ROWSPEC,
                                    FormSpecs.LINE_GAP_ROWSPEC,
                                    FormSpecs.DEFAULT_ROWSPEC,
                                    FormSpecs.LINE_GAP_ROWSPEC,
                                    FormSpecs.DEFAULT_ROWSPEC,
                                    FormSpecs.LINE_GAP_ROWSPEC,
                                    FormSpecs.DEFAULT_ROWSPEC,
                                    FormSpecs.LINE_GAP_ROWSPEC,
                                    FormSpecs.DEFAULT_ROWSPEC
                            }), movementPanel);

                    movementPanelBuilder.add(addButton, cc.xy(1, 1));
                    movementPanelBuilder.add(removeButton, cc.xy(1, 3));
                    movementPanelBuilder.add(Box.createRigidArea(new Dimension(30,30)), cc.xy(1, 5));
                    movementPanelBuilder.add(upButton, cc.xy(1, 7));
                    movementPanelBuilder.add(downButton, cc.xy(1, 9));

                }
                //======== functionsPanel ========
                {
                    functionsPanel.setLayout(new BoxLayout(functionsPanel, BoxLayout.PAGE_AXIS));

                    functionsListLabel.setName("functionsListLabel");
                    functionsScrollPane.setViewportView(functionsList);
                    functionsScrollPane.setPreferredSize(new Dimension(150, 150));

                    functionsList.setLayoutOrientation(JList.VERTICAL);
                    functionsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                    functionsList.setCellRenderer(new ToolbarListCellRenderer());

                    functionsPanel.add(functionsListLabel);
                    functionsPanel.add(functionsScrollPane);
                }

                PanelBuilder contentPanelBuilder = new PanelBuilder(new FormLayout(
                        new ColumnSpec[]{
                                new ColumnSpec(ColumnSpec.FILL, Sizes.PREFERRED, FormSpec.DEFAULT_GROW),
                                FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
                                FormSpecs.PREF_COLSPEC,
                                FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
                                new ColumnSpec(ColumnSpec.FILL, Sizes.PREFERRED, FormSpec.DEFAULT_GROW)
                        },
                        new RowSpec[]{
                                new RowSpec(RowSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW)
                        }), contentPanel);

                contentPanelBuilder.add(toolbarPanel, cc.xy(1, 1));
                contentPanelBuilder.add(movementPanel, cc.xy(3, 1));
                contentPanelBuilder.add(functionsPanel, cc.xy(5, 1));
            }
            //======== buttonPanel ========
            {
                buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
                buttonPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

                defaultButton.setName("defaultButton");
                resetButton.setName("resetButton");
                okButton.setName("okButton");
                cancelButton.setName("cancelButton");

                buttonPanel.add(defaultButton);
                buttonPanel.add(resetButton);
                buttonPanel.add(Box.createHorizontalGlue());
                buttonPanel.add(okButton);
                buttonPanel.add(cancelButton);
            }

            dialogPane.add(contentPanel, BorderLayout.CENTER);
            dialogPane.add(buttonPanel, BorderLayout.SOUTH);
        }
        contentPane.add(dialogPane, BorderLayout.CENTER);
    }


    private class ToolbarListCellRenderer extends DefaultListCellRenderer  {
        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            if (value instanceof String) {
                String btn = (String) value;
                JLabel label = (JLabel) super.getListCellRendererComponent(list, toolbarManager.getToolbarButtonName(btn), index, isSelected, cellHasFocus);
                label.setIcon(toolbarManager.getToolbarButtonSmallIcon(btn));
                return label;
            }
            return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        }
    }

    private class ToolbarListSelectionListener implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            setToolbarListSelected((toolbarList.getSelectedIndex() >= 0) && (toolbarList.getSelectedIndex() < toolbarModel.size()-1));
            setFunctionListSelected((functionsList.getSelectedIndex() >= 0) && (functionsList.getSelectedIndex() < functionModel.size() - 1));
        }
    }

    public boolean isToolbarListSelected() {
        return toolbarListSelected;
    }
    public void setToolbarListSelected(boolean enabled) {
        boolean oldValue = toolbarListSelected;
        toolbarListSelected = enabled;
        firePropertyChange(TOOLBAR_LIST_SELECTED_ACTION_ENABLED_PROPERTY, oldValue, toolbarListSelected);
    }

    public boolean isFunctionListSelected() {
        return functionListSelected;
    }
    public void setFunctionListSelected(boolean enabled) {
        boolean oldValue = functionListSelected;
        functionListSelected = enabled;
        firePropertyChange(FUNCTION_LIST_SELECTED_ACTION_ENABLED_PROPERTY, oldValue, functionListSelected);
    }


    private JList toolbarList;
    private JList functionsList;

    private JButton addButton;
    private JButton removeButton;
    private JButton upButton;
    private JButton downButton;

    private JButton defaultButton;
    private JButton resetButton;
    private JButton okButton;
    private JButton cancelButton;
}
