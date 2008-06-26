package cz.cvut.felk.erm.gui.dialogs;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.adapter.Bindings;
import com.jgoodies.binding.adapter.SpinnerAdapterFactory;
import com.jgoodies.binding.beans.PropertyConnector;
import com.jgoodies.binding.list.ArrayListModel;
import com.jgoodies.binding.list.SelectionInList;
import com.jgoodies.binding.value.Trigger;
import com.jgoodies.binding.value.ValueHolder;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.*;
import com.l2fprod.common.swing.JButtonBar;
import com.l2fprod.common.swing.plaf.blue.BlueishButtonBarUI;
import cz.cvut.felk.erm.core.AppPrefs;
import cz.cvut.felk.erm.core.FWProp;
import cz.cvut.felk.erm.core.MainApp;
import cz.cvut.felk.erm.gui.MyPreferencesAdapter;
import cz.cvut.felk.erm.gui.MyPresentationModel;
import cz.cvut.felk.erm.swing.LaF;
import cz.cvut.felk.erm.swing.LookAndFeels;
import cz.cvut.felk.erm.swing.Swinger;
import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.swinghelper.buttonpanel.JXButtonPanel;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;

/**
 * @author Ladislav Vitasek
 */
public class UserPreferencesDialog extends AppDialog {
    private final static Logger logger = Logger.getLogger(UserPreferencesDialog.class.getName());
    private MyPresentationModel model;
    private static final String CARD_PROPERTY = "card";
    private static final String LAF_PROPERTY = "lafFakeProperty";
    private ApplyPreferenceChangeListener prefListener = null;


    private static enum Card {
        CARD1, CARD2, CARD3, CARD4
    }

    public UserPreferencesDialog(Frame owner) throws Exception {
        super(owner, true);
        this.setName("UserPreferencesDialog");
        try {
            initComponents();
            build();
        } catch (Exception e) {
            doClose(); //dialog se pri fatalni chybe zavre
            throw e;
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
        buildModels();

        final ActionMap actionMap = getActionMap();
        btnOK.setAction(actionMap.get("okBtnAction"));
        btnCancel.setAction(actionMap.get("cancelBtnAction"));


        setDefaultValues();
        showCard(Card.valueOf(AppPrefs.getProperty(FWProp.USER_SETTINGS_SELECTED_CARD, Card.CARD1.toString())));
        pack();
        setResizable(true);
        locateOnOpticalScreenCenter(this);


    }

    private void buildGUI() {
        toolbar.setUI(new BlueishButtonBarUI());//nenechat to default?
        //  toolbar.setOrientation(JButtonBar.HORIZONTAL);
        //    toolbar.setUI(new BasicButtonBarUI());//nenechat to default?


        final ActionMap map = getActionMap();

        ButtonGroup group = new ButtonGroup();
        addButton(map.get("generalBtnAction"), Card.CARD1, group);
        addButton(map.get("viewsBtnAction"), Card.CARD4, group);

    }


    private void addButton(javax.swing.Action action, final Card card, ButtonGroup group) {
        final JToggleButton button = new JToggleButton(action);
        final Dimension size = button.getPreferredSize();
        final Dimension dim = new Dimension(60, size.height);
        button.setFont(button.getFont().deriveFont((float) 10));
        button.setForeground(Color.BLACK);
        button.setMinimumSize(dim);
        button.setPreferredSize(dim);
        button.setHorizontalTextPosition(JButton.CENTER);
        button.setVerticalTextPosition(JButton.BOTTOM);
        button.setOpaque(false);
        toolbar.add(button);
        button.putClientProperty(CARD_PROPERTY, card);
        group.add(button);
    }

    private void showCard(Card card) {
        assert card != null;
        final CardLayout cardLayout = (CardLayout) panelCard.getLayout();
        cardLayout.show(panelCard, card.toString());
        AppPrefs.storeProperty(FWProp.USER_SETTINGS_SELECTED_CARD, card.toString());
        String actionName;
        switch (card) {
            case CARD1:
                actionName = "generalBtnAction";
                break;
            case CARD4:
                actionName = "viewsBtnAction";
                break;
            default:
                assert false;
                return;
        }
        javax.swing.Action action = getActionMap().get(actionName);
        assert action != null;
        action.putValue(javax.swing.Action.SELECTED_KEY, Boolean.TRUE);
    }


    private void buildModels() throws CloneNotSupportedException {

        model = new MyPresentationModel(null, new Trigger());

        prefListener = new ApplyPreferenceChangeListener();
        AppPrefs.getPreferences().addPreferenceChangeListener(prefListener);
        bindBasicComponents();

        final ActionMap map = getActionMap();
        final javax.swing.Action actionOK = map.get("okBtnAction");
        PropertyConnector connector = PropertyConnector.connect(model, PresentationModel.PROPERTYNAME_BUFFERING, actionOK, "enabled");
        connector.updateProperty2();

    }

    private void bindBasicComponents() {

        bind(checkShowIconInSystemTray, FWProp.SHOW_TRAY, true);

        bindLaFCombobox();
    }

    private void bindLaFCombobox() {
        final LookAndFeels lafs = LookAndFeels.getInstance();
        final ListModel listModel = new ArrayListModel<LaF>(lafs.getAvailableLookAndFeels());
        final LookAndFeelAdapter adapter = new LookAndFeelAdapter(LAF_PROPERTY, lafs.getSelectedLaF());
        final SelectionInList<String> inList = new SelectionInList<String>(listModel, model.getBufferedModel(adapter));
        Bindings.bind(comboLaF, inList);
    }


    private void bind(JSpinner spinner, String key, int defaultValue, int minValue, int maxValue, int step) {
        spinner.setModel(SpinnerAdapterFactory.createNumberAdapter(
                model.getBufferedPreferences(key, defaultValue),
                defaultValue,   // defaultValue
                minValue,   // minValue
                maxValue, // maxValue
                step)); // step
    }

    private void bind(final JCheckBox checkBox, final String key, final Object defaultValue) {
        Bindings.bind(checkBox, model.getBufferedPreferences(key, defaultValue));
    }

    private void bind(final JTextField field, final String key, final Object defaultValue) {
        Bindings.bind(field, model.getBufferedPreferences(key, defaultValue), false);
    }

    private void bind(final JComboBox combobox, final String key, final Object defaultValue, final String propertyResourceMap) {
        final String[] stringList = getList(propertyResourceMap);
        if (stringList == null)
            throw new IllegalArgumentException("Property '" + propertyResourceMap + "' does not provide any string list from resource map.");
        bind(combobox, key, defaultValue, stringList);
    }

    private void bind(final JComboBox combobox, String key, final Object defaultValue, final String[] values) {
        if (values == null)
            throw new IllegalArgumentException("List of combobox values cannot be null!!");
        final MyPreferencesAdapter adapter = new MyPreferencesAdapter(key, defaultValue);
        final SelectionInList<String> inList = new SelectionInList<String>(values, new ValueHolder(values[(Integer) adapter.getValue()]), model.getBufferedModel(adapter));
        Bindings.bind(combobox, inList);
    }


    private void setDefaultValues() {

    }

    @Action
    public void okBtnAction() {
        model.triggerCommit();

        LaF laf = (LaF) comboLaF.getSelectedItem();
        LookAndFeels.getInstance().storeSelectedLaF(laf);

        doClose();
    }

    @Action
    public void cancelBtnAction() {
        doClose();
    }

    @Action(selectedProperty = "generalBtnActionSelected")
    public void generalBtnAction(ActionEvent e) {
        showCard(e);
    }

    private void showCard(ActionEvent e) {
        showCard((Card) ((JComponent) e.getSource()).getClientProperty(CARD_PROPERTY));
    }

    public boolean isGeneralBtnActionSelected() {
        return true;
    }

//    @Action
//    public void alarmsBtnAction(ActionEvent e) {
//        showCard(e);
//    }
//
//    @Action
//    public void categoriesBtnAction(ActionEvent e) {
//        showCard(e);
//    }

    @Action
    public void viewsBtnAction(ActionEvent e) {
        showCard(e);
    }


    @Override
    public void doClose() {
        AppPrefs.removeProperty(LAF_PROPERTY);
        logger.log(Level.FINE, "Closing UserPreferenceDialog.");
        try {
            synchronized (this) {
                if (prefListener != null) {
                    AppPrefs.getPreferences().removePreferenceChangeListener(prefListener);
                    prefListener = null;
                }
            }
            if (model != null)
                model.release();
        } finally {
            super.doClose();
        }
    }

    private ActionMap getActionMap() {
        return Swinger.getActionMap(this.getClass(), this);
    }


    private void initComponents() {

        JPanel dialogPane = new JPanel();
        JPanel contentPanel = new JPanel();
        toolbar = new JButtonBar(JButtonBar.HORIZONTAL);
        panelCard = new JPanel();
        panelGeneral = new JPanel();
        JPanel panelGeneralSettings = new JPanel();
        checkShowIconInSystemTray = new JCheckBox();

        JPanel panelAppearance = new JPanel();
        JLabel labelLaF = new JLabel();
        comboLaF = new JComboBox();
        JLabel labelRequiresRestart = new JLabel();


        panelViews = new JPanel();
        JPanel panelDayAndWeekViews = new JPanel();

        JPanel panelMultiWeekView = new JPanel();

        JXButtonPanel buttonBar = new JXButtonPanel();
        buttonBar.setCyclic(true);

        btnOK = new JButton();
        btnCancel = new JButton();
        CellConstraints cc = new CellConstraints();

        //======== this ========
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        final ResourceMap resourceMap = getResourceMap();

        //======== dialogPane ========
        {
            dialogPane.setBorder(Borders.DIALOG_BORDER);
            dialogPane.setName("dialogPane");
            dialogPane.setLayout(new BorderLayout());

            //======== contentPanel ========
            {
                //   contentPanel.setBorder(new MatteBorder(1, 1, 1, 1, Color.black));
                contentPanel.setName("contentPanel");
                contentPanel.setLayout(new BorderLayout());

                //======== toolbar ========
                {
                    toolbar.setBorder(LineBorder.createBlackLineBorder());
                    toolbar.setName("toolbar");
                    toolbar.setLayout(null);
                }
                contentPanel.add(toolbar, BorderLayout.NORTH);

                //======== panelCard ========
                {
                    panelCard.setName("panelCard");
                    panelCard.setLayout(new CardLayout());

                    //======== panelGeneral ========
                    {
                        panelGeneral.setBorder(Borders.TABBED_DIALOG_BORDER);
                        panelGeneral.setLayout(new FormLayout(
                                ColumnSpec.decodeSpecs("default:grow"),
                                new RowSpec[]{
                                        new RowSpec(RowSpec.FILL, Sizes.DEFAULT, FormSpec.NO_GROW),
                                        FormFactory.RELATED_GAP_ROWSPEC,
                                        new RowSpec(RowSpec.FILL, Sizes.DEFAULT, FormSpec.NO_GROW)
                                }));

                        //======== panelGeneralSettings ========
                        {
                            panelGeneralSettings.setBorder(new TitledBorder(null, resourceMap.getString("panelGeneralSettings.border"), TitledBorder.LEADING, TitledBorder.TOP));
                            panelGeneralSettings.setLayout(new FormLayout(
                                    new ColumnSpec[]{
                                            new ColumnSpec(ColumnSpec.LEFT, Sizes.dluX(0), FormSpec.NO_GROW),
                                            FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                            FormFactory.DEFAULT_COLSPEC
                                    },
                                    RowSpec.decodeSpecs("default")));

                            //---- checkShowIconInSystemTray ----
                            checkShowIconInSystemTray.setName("checkShowIconInSystemTray");
                            panelGeneralSettings.add(checkShowIconInSystemTray, cc.xy(3, 1));
                        }
                        panelGeneral.add(panelGeneralSettings, cc.xy(1, 1));

                        //======== panelAppearance ========
                        {
                            panelAppearance.setBorder(new CompoundBorder(
                                    new TitledBorder(null, resourceMap.getString("panelAppearance.border"), TitledBorder.LEADING, TitledBorder.TOP),
                                    Borders.DLU2_BORDER));
                            panelAppearance.setLayout(new FormLayout(
                                    new ColumnSpec[]{
                                            new ColumnSpec(ColumnSpec.LEFT, Sizes.dluX(0), FormSpec.NO_GROW),
                                            FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                            FormFactory.DEFAULT_COLSPEC,
                                            FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                            FormFactory.PREF_COLSPEC,
                                            FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                            FormFactory.DEFAULT_COLSPEC
                                    },
                                    RowSpec.decodeSpecs("default")));

                            //---- labelLaF ----
                            labelLaF.setName("labelLaF");
                            labelLaF.setLabelFor(comboLaF);
                            panelAppearance.add(labelLaF, cc.xy(3, 1));
                            panelAppearance.add(comboLaF, cc.xy(5, 1));

                            //---- labelRequiresRestart ----
                            labelRequiresRestart.setName("labelRequiresRestart");
                            panelAppearance.add(labelRequiresRestart, cc.xy(7, 1));
                        }
                        panelGeneral.add(panelAppearance, cc.xy(1, 3));
                    }
                    panelCard.add(panelGeneral, Card.CARD1.toString());

                    //======== panelViews ========
                    {
                        panelViews.setBorder(Borders.TABBED_DIALOG_BORDER);
                        panelViews.setName("panelViews");


                        PanelBuilder panelViewsBuilder = new PanelBuilder(new FormLayout(
                                ColumnSpec.decodeSpecs("default:grow"),
                                new RowSpec[]{
                                        FormFactory.DEFAULT_ROWSPEC,
                                        FormFactory.RELATED_GAP_ROWSPEC,
                                        FormFactory.DEFAULT_ROWSPEC
                                }), panelViews);

                        panelViewsBuilder.add(panelDayAndWeekViews, cc.xy(1, 1));
                        panelViewsBuilder.add(panelMultiWeekView, cc.xy(1, 3));
                    }
                    panelCard.add(panelViews, Card.CARD4.toString());
                }
                contentPanel.add(panelCard, BorderLayout.CENTER);
            }
            dialogPane.add(contentPanel, BorderLayout.CENTER);

            //======== buttonBar ========
            {
                buttonBar.setBorder(Borders.BUTTON_BAR_GAP_BORDER);
                buttonBar.setName("buttonBar");

                //---- btnOK ----

                btnOK.setName("btnOK");

                //---- btnCancel ----

                btnCancel.setName("btnCancel");

                PanelBuilder buttonBarBuilder = new PanelBuilder(new FormLayout(
                        new ColumnSpec[]{
                                FormFactory.GLUE_COLSPEC,
                                new ColumnSpec("max(pref;42dlu)"),
                                FormFactory.RELATED_GAP_COLSPEC,
                                FormFactory.PREF_COLSPEC
                        },
                        RowSpec.decodeSpecs("pref")), buttonBar);
                ((FormLayout) buttonBar.getLayout()).setColumnGroups(new int[][]{{2, 4}});

                buttonBarBuilder.add(btnOK, cc.xy(2, 1));
                buttonBarBuilder.add(btnCancel, cc.xy(4, 1));
            }
            dialogPane.add(buttonBar, BorderLayout.SOUTH);
        }
        contentPane.add(dialogPane, BorderLayout.CENTER);

    }

    private com.l2fprod.common.swing.JButtonBar toolbar;
    private JPanel panelCard;
    private JCheckBox checkShowIconInSystemTray;
    private JComboBox comboLaF;


    private JButton btnOK;
    private JButton btnCancel;

    private JPanel panelGeneral;
    private JPanel panelViews;


    private class ApplyPreferenceChangeListener implements PreferenceChangeListener {

        public void preferenceChange(PreferenceChangeEvent evt) {
            //pozor, interne se vola ve zvlastnim vlakne, nikoli na EDT threadu
            final MainApp app = MainApp.getInstance(MainApp.class);
            final String key = evt.getKey();
            if (FWProp.SHOW_TRAY.equals(key)) {
                app.getTrayIconSupport().setVisibleByDefault();
            }
//            else if (LAF_PROPERTY.equals(key)) {
//                boolean succesful;
////                final ResourceMap map = getResourceMap();
//                laf = (LaF) comboLaF.getSelectedItem();
//                try {
////                    succesful = LookAndFeels.getInstance().loadLookAndFeel(laf, true);
//                    succesful = true;
//                    synchronized (UserPreferencesDialog.this) {
//                        AppPrefs.getPreferences().removePreferenceChangeListener(this);
//
//                        AppPrefs.removeProperty(LAF_PROPERTY);
//                        AppPrefs.getPreferences().addPreferenceChangeListener(this);
//                    }
//                } catch (Exception ex) {
//                    LogUtils.processException(logger, ex);
//                    Swinger.showErrorDialog("changeLookAndFeelActionFailed", ex);
////                    succesful = false;
//                }
//                if (!succesful) {
//
//                } //else {
//                    Swinger.showInformationDialog(map.getString("message_changeLookAndFeelActionSet"));
//                }
        }
//        }
    }


}
