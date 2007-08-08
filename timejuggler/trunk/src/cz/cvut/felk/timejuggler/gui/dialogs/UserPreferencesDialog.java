package cz.cvut.felk.timejuggler.gui.dialogs;

import application.ResourceMap;
import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.adapter.Bindings;
import com.jgoodies.binding.adapter.SpinnerAdapterFactory;
import com.jgoodies.binding.beans.PropertyConnector;
import com.jgoodies.binding.list.SelectionInList;
import com.jgoodies.binding.value.Trigger;
import com.jgoodies.binding.value.ValueHolder;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.*;
import com.l2fprod.common.swing.JButtonBar;
import com.l2fprod.common.swing.plaf.blue.BlueishButtonBarUI;
import cz.cvut.felk.timejuggler.core.AppPrefs;
import cz.cvut.felk.timejuggler.core.MainApp;
import cz.cvut.felk.timejuggler.gui.MyPreferencesAdapter;
import cz.cvut.felk.timejuggler.gui.MyPresentationModel;
import cz.cvut.felk.timejuggler.swing.ComponentFactory;
import cz.cvut.felk.timejuggler.swing.Swinger;
import cz.cvut.felk.timejuggler.utilities.LogUtils;
import org.jdesktop.swingx.JXTable;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Date;
import java.util.logging.Logger;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;

/**
 * @author Vity
 */
public class UserPreferencesDialog extends AppDialog {
    private final static Logger logger = Logger.getLogger(UserPreferencesDialog.class.getName());
    private MyPresentationModel model;
    private static final String CARD_PROPERTY = "card";
    private ApplyPreferenceChangeListener prefListener;
    private JCheckBox checkPlaySound;

    private static enum Card {
        CARD1, CARD2, CARD3, CARD4
    }

    public UserPreferencesDialog(Frame owner) throws HeadlessException {
        super(owner, true);
        this.setName("UserPreferencesDialog");
        try {
            initComponents();
            build();
        } catch (Exception e) {
            LogUtils.processException(logger, e);
            doClose(); //dialog se pri fatalni chybe zavre
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

    private void build() {
        inject();
        buildGUI();
        buildModels();

        final ActionMap actionMap = getActionMap();
        btnOK.setAction(actionMap.get("okBtnAction"));
        btnCancel.setAction(actionMap.get("cancelBtnAction"));


        setDefaultValues();
        showCard(Card.valueOf(AppPrefs.getProperty(AppPrefs.USER_SETTINGS_SELECTED_CARD, Card.CARD1.toString())));
        pack();
        setResizable(false);
        locateOnOpticalScreenCenter(this);


    }

    private void buildGUI() {
        toolbar.setUI(new BlueishButtonBarUI());

        final ActionMap map = getActionMap();

        ButtonGroup group = new ButtonGroup();
        addButton(map.get("generalBtnAction"), Card.CARD1, group);
        addButton(map.get("alarmsBtnAction"), Card.CARD2, group);
        addButton(map.get("categoriesBtnAction"), Card.CARD3, group);
        addButton(map.get("viewsBtnAction"), Card.CARD4, group);

        this.btnCategoryEdit.setAction(map.get("btnCategoryEditAction"));
        this.btnCategoryAdd.setAction(map.get("btnCategoryAddAction"));
        this.btnCategoryRemove.setAction(map.get("btnCategoryRemoveAction"));

        this.btnBrowse.setAction(map.get("btnBrowseAction"));
        this.btnUseDefaultSound.setAction(map.get("btnUseDefaultSoundAction"));
        this.btnPreview.setAction(map.get("btnPreviewAction"));

    }


    private void addButton(Action action, final Card card, ButtonGroup group) {
        final JToggleButton button = new JToggleButton(action);
        final Dimension size = button.getPreferredSize();
        final Dimension dim = new Dimension(60, size.height);
        button.setMinimumSize(dim);
        button.setPreferredSize(dim);
        toolbar.add(button);
        button.putClientProperty(CARD_PROPERTY, card);
        group.add(button);
    }

    private void showCard(Card card) {
        final CardLayout cardLayout = (CardLayout) panelCard.getLayout();
        cardLayout.show(panelCard, card.toString());
        AppPrefs.storeProperty(AppPrefs.USER_SETTINGS_SELECTED_CARD, card.toString());
        final ActionMap map = getActionMap();
        Action action = null;
        switch (card) {
            case CARD1:
                action = map.get("generalBtnAction");
                break;
            case CARD2:
                action = map.get("alarmsBtnAction");
                break;
            case CARD3:
                action = map.get("categoriesBtnAction");
                break;
            case CARD4:
                action = map.get("viewsBtnAction");
                break;
            default:
                assert false;
                break;
        }
        if (action != null)
            action.putValue(Action.SELECTED_KEY, Boolean.TRUE);
    }


    private void buildModels() {
        model = new MyPresentationModel(null, new Trigger());
        prefListener = new ApplyPreferenceChangeListener();
        AppPrefs.getPreferences().addPreferenceChangeListener(prefListener);

        //spinnerDefaultTimeBeforeTask.setModel(new SpinnerNumberModel(1, 1, 1, 1));

        bindCheckbox(checkShowIconInSystemTray, AppPrefs.SHOW_TRAY, true);
        bindCheckbox(checkPlaySound, AppPrefs.PLAY_SOUND, true);
        bindCheckbox(checkShowAlarmBox, AppPrefs.SHOW_ALARM_BOX, true);
        bindCheckbox(checkShowMissedAlarms, AppPrefs.SHOW_MISSED_ALARMS, true);

        bindSpinner(spinnerDefaultEventLength, AppPrefs.DEFAULT_EVENT_LENGTH, 60, 1, 999, 60);
        bindSpinner(spinnerlDefaultSnoozeLength, AppPrefs.DEFAULT_SNOOZE_LENGTH, 60, 1, 999, 30);
        bindSpinner(spinnerDefaultTimeBeforeEvent, AppPrefs.DEFAULT_ALARM_TIME_BEFORE_EVENT, 15, 1, 999, 5);
        bindSpinner(spinnerDefaultTimeBeforeTask, AppPrefs.DEFAULT_ALARM_TIME_BEFORE_TASK, 15, 1, 999, 5);

        bindCombobox(comboDayEndsAt, AppPrefs.DAY_ENDS_AT, 0, "dayEndsAt");
        bindCombobox(comboDayStartsAt, AppPrefs.DAY_STARTS_AT, 0, "dayStartsAt");
        bindCombobox(comboDefaultAlarmSettingForEvent, AppPrefs.DEFAULT_ALARM_SETTING_FOR_EVENT, 0, "onOff");
        bindCombobox(comboDefaultAlarmSettingForTask, AppPrefs.DEFAULT_ALARM_SETTING_FOR_TASK, 0, "onOff");
        bindCombobox(comboDefaultWeeksToShow, AppPrefs.DEFAULT_WEEKS_TO_SHOW, 0, "defaultWeeksToShow");
        bindCombobox(comboPreviousWeeksToShow, AppPrefs.PREVIOUS_WEEKS_TO_SHOW, 0, "previousWeeksToShow");
        bindCombobox(comboShowHoursAtATime, AppPrefs.SHOW_HOURS_AT_A_TIME, 0, "showHoursAtATime");
        bindCombobox(comboTimeUnitEvent, AppPrefs.DEFAULT_ALARM_TIME_BEFORE_EVENT_TIMEUNIT, 0, "timeunit");
        bindCombobox(comboTimeUnitTask, AppPrefs.DEFAULT_ALARM_TIME_BEFORE_TASK_TIMEUNIT, 0, "timeunit");
        bindCombobox(comboDateTextFormat, AppPrefs.DATE_TEXT_FORMAT, AppPrefs.DEF_DATE_TEXT_FORMAT_LONG, getDateFormats());

        final Action actionOK = getActionMap().get("okBtnAction");
        final PropertyConnector connector = PropertyConnector.connect(model, PresentationModel.PROPERTYNAME_BUFFERING, actionOK, "enabled");
        connector.updateProperty2();

    }


    private void bindSpinner(JSpinner spinner, String key, int defaultValue, int minValue, int maxValue, int step) {
        spinner.setModel(SpinnerAdapterFactory.createNumberAdapter(
                model.getBufferedPreferences(key, defaultValue),
                defaultValue,   // defaultValue
                minValue,   // minValue
                maxValue, // maxValue
                step)); // step
    }

    private void bindCheckbox(final JCheckBox checkBox, final String key, final Object defaultValue) {
        Bindings.bind(checkBox, model.getBufferedPreferences(key, defaultValue));
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
        final SelectionInList<String> inList = new SelectionInList<String>(values, new ValueHolder(values[(Integer) adapter.getValue()]), model.getBufferedModel(adapter));
        Bindings.bind(combobox, inList);
    }


    private void setDefaultValues() {

    }

    private String[] getDateFormats() {
        final ResourceMap map = this.getResourceMap();
        final Date date = new Date();
        final String longFormat = map.getString("longFormatDate", date);
        final String shortFormat = map.getString("shortFormatDate", date);
        return new String[]{longFormat, shortFormat};
    }

    @application.Action
    public void okBtnAction() {
        model.triggerCommit();
        doClose();
    }

    @application.Action
    public void cancelBtnAction() {
        doClose();
    }

    @application.Action(selectedProperty = "generalBtnActionSelected")
    public void generalBtnAction(ActionEvent e) {
        showCard(e);
    }

    private void showCard(ActionEvent e) {
        showCard((Card) ((JComponent) e.getSource()).getClientProperty(CARD_PROPERTY));
    }

    public boolean isGeneralBtnActionSelected() {
        return true;
    }


    @application.Action
    public void alarmsBtnAction(ActionEvent e) {
        showCard(e);
    }

    @application.Action
    public void categoriesBtnAction(ActionEvent e) {
        showCard(e);
    }

    @application.Action
    public void viewsBtnAction(ActionEvent e) {
        showCard(e);
    }

    @application.Action
    public void btnCategoryAddAction() {

    }


    @application.Action
    public void btnCategoryRemoveAction() {

    }

    @application.Action
    public void btnCategoryEditAction() {

    }

    @application.Action
    public void btnUseDefaultSoundAction() {

    }

    @application.Action
    public void btnPreviewAction() {

    }

    @application.Action
    public void btnBrowseAction() {

    }


    @Override
    public void doClose() {
        AppPrefs.getPreferences().removePreferenceChangeListener(prefListener);
        if (model != null)
            model.release();
        super.doClose();
    }

    private ActionMap getActionMap() {
        return Swinger.getActionMap(this.getClass(), this);
    }


    private void initComponents() {

        JPanel dialogPane = new JPanel();
        JPanel contentPanel = new JPanel();
        JLabel labelMinutes1 = new JLabel();
        JLabel labelMinutes2 = new JLabel();
        toolbar = new JButtonBar(JButtonBar.HORIZONTAL);
        panelCard = new JPanel();
        panelGeneral = new JPanel();
        JPanel panelGeneralSettings = new JPanel();
        checkShowIconInSystemTray = new JCheckBox();
        JLabel labelDefaultEventLength = new JLabel();
        spinnerDefaultEventLength = new JSpinner();
        JLabel labelDefaultSnoozeLength = new JLabel();
        spinnerlDefaultSnoozeLength = new JSpinner();
        JPanel panelAppearance = new JPanel();
        JLabel labelLaF = new JLabel();
        comboLaF = new JComboBox();
        JLabel labelRequiresRestart = new JLabel();
        JLabel labelDateTextFormat = new JLabel();
        comboDateTextFormat = new JComboBox();
        panelAlarm = new JPanel();
        JPanel panelWhenAlarmGoesOff = new JPanel();
        checkPlaySound = new JCheckBox();
        fieldSoundPath = ComponentFactory.getTextField();
        btnUseDefaultSound = new JButton();
        btnBrowse = new JButton();
        btnPreview = new JButton();
        checkShowAlarmBox = new JCheckBox();
        checkShowMissedAlarms = new JCheckBox();
        JPanel panelAlarmDefaults = new JPanel();
        JLabel labelDefaultAlarmSettingForEvent = new JLabel();
        comboDefaultAlarmSettingForEvent = new JComboBox();
        JLabel labelDefaultAlarmSettingForTask = new JLabel();
        comboDefaultAlarmSettingForTask = new JComboBox();
        JLabel labelDefaultTimeBeforeEvent = new JLabel();
        spinnerDefaultTimeBeforeEvent = new JSpinner();
        comboTimeUnitEvent = new JComboBox();
        JLabel labelDefaultTimeBeforeTask = new JLabel();
        spinnerDefaultTimeBeforeTask = new JSpinner();
        comboTimeUnitTask = new JComboBox();
        panelCategories = new JPanel();
        JPanel panelListCategories = new JPanel();
        JScrollPane scrollPaneCategories = new JScrollPane();
        tableCategories = new JXTable();
        btnCategoryAdd = new JButton();
        btnCategoryEdit = new JButton();
        btnCategoryRemove = new JButton();
        panelViews = new JPanel();
        JPanel panelDayAndWeekViews = new JPanel();
        labelDayStartsAt = new JLabel();
        comboDayStartsAt = new JComboBox();
        JLabel labelShow = new JLabel();
        comboShowHoursAtATime = new JComboBox();
        JLabel labelHoursAtATime = new JLabel();
        JLabel labelDayEndsAt = new JLabel();
        comboDayEndsAt = new JComboBox();
        JPanel panelMultiWeekView = new JPanel();
        JLabel labelDefaultWeeksToShow = new JLabel();
        comboDefaultWeeksToShow = new JComboBox();
        JLabel labelPrevisouWeeksToShow = new JLabel();
        comboPreviousWeeksToShow = new JComboBox();
        buttonBar = new JPanel();
        btnOK = new JButton();
        btnCancel = new JButton();
        CellConstraints cc = new CellConstraints();

        //======== this ========
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

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
                    final ResourceMap resourceMap = getResourceMap();
                    {
                        panelGeneral.setBorder(Borders.TABBED_DIALOG_BORDER);
                        panelGeneral.setName("panelGeneral");

                        //======== panelGeneralSettings ========
                        {
                            panelGeneralSettings.setBorder(new TitledBorder(resourceMap.getString("panelGeneralSettings_border")));
                            panelGeneralSettings.setName("panelGeneralSettings");

                            //---- checkShowIconInSystemTray ----

                            checkShowIconInSystemTray.setName("checkShowIconInSystemTray");

                            labelMinutes1.setName("labelMinutes1");
                            labelMinutes2.setName("labelMinutes2");

                            //---- labelDefaultEventLength ----

                            labelDefaultEventLength.setLabelFor(spinnerDefaultEventLength);
                            labelDefaultEventLength.setName("labelDefaultEventLength");

                            //---- spinnerDefaultEventLength ----
                            spinnerDefaultEventLength.setName("spinnerDefaultEventLength");

                            //---- labelDefaultSnoozeLength ----

                            labelDefaultSnoozeLength.setLabelFor(spinnerlDefaultSnoozeLength);
                            labelDefaultSnoozeLength.setName("labelDefaultSnoozeLength");

                            //---- spinnerlDefaultSnoozeLength ----
                            spinnerlDefaultSnoozeLength.setName("spinnerlDefaultSnoozeLength");

                            PanelBuilder panelGeneralSettingsBuilder = new PanelBuilder(new FormLayout(
                                    new ColumnSpec[]{
                                            new ColumnSpec(ColumnSpec.LEFT, Sizes.dluX(0), FormSpec.NO_GROW),
                                            FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                            FormFactory.DEFAULT_COLSPEC,
                                            FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                            FormFactory.DEFAULT_COLSPEC,
                                            FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                            FormFactory.DEFAULT_COLSPEC
                                    },
                                    new RowSpec[]{
                                            FormFactory.DEFAULT_ROWSPEC,
                                            FormFactory.UNRELATED_GAP_ROWSPEC,
                                            FormFactory.DEFAULT_ROWSPEC,
                                            FormFactory.LINE_GAP_ROWSPEC,
                                            FormFactory.DEFAULT_ROWSPEC
                                    }), panelGeneralSettings);

                            panelGeneralSettingsBuilder.add(checkShowIconInSystemTray, cc.xywh(3, 1, 3, 1));
                            panelGeneralSettingsBuilder.add(labelDefaultEventLength, cc.xy(3, 3));
                            panelGeneralSettingsBuilder.add(spinnerDefaultEventLength, cc.xywh(5, 3, 1, 1, CellConstraints.FILL, CellConstraints.FILL));
                            panelGeneralSettingsBuilder.add(labelMinutes1, cc.xy(7, 3));
                            panelGeneralSettingsBuilder.add(labelDefaultSnoozeLength, cc.xy(3, 5));
                            panelGeneralSettingsBuilder.add(spinnerlDefaultSnoozeLength, cc.xywh(5, 5, 1, 1, CellConstraints.FILL, CellConstraints.FILL));
                            panelGeneralSettingsBuilder.add(labelMinutes2, cc.xy(7, 5));
                        }

                        //======== panelAppearance ========
                        {
                            panelAppearance.setBorder(new CompoundBorder(
                                    new TitledBorder(resourceMap.getString("panelAppearance_border")),
                                    Borders.DLU2_BORDER));
                            panelAppearance.setName("panelAppearance");

                            //---- labelLaF ----

                            labelLaF.setLabelFor(comboLaF);
                            labelLaF.setName("labelLaF");

                            //---- comboLaF ----
                            comboLaF.setName("comboLaF");

                            //---- labelRequiresRestart ----

                            labelRequiresRestart.setName("labelRequiresRestart");

                            //---- labelDateTextFormat ----

                            labelDateTextFormat.setLabelFor(comboDateTextFormat);
                            labelDateTextFormat.setName("labelDateTextFormat");

                            //---- comboDateTextFormat ----
                            comboDateTextFormat.setName("comboDateTextFormat");

                            PanelBuilder panelAppearanceBuilder = new PanelBuilder(new FormLayout(
                                    new ColumnSpec[]{
                                            new ColumnSpec(ColumnSpec.LEFT, Sizes.dluX(0), FormSpec.NO_GROW),
                                            FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                            FormFactory.DEFAULT_COLSPEC,
                                            FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                            FormFactory.PREF_COLSPEC,
                                            FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                            FormFactory.DEFAULT_COLSPEC
                                    },
                                    new RowSpec[]{
                                            FormFactory.DEFAULT_ROWSPEC,
                                            FormFactory.NARROW_LINE_GAP_ROWSPEC,
                                            FormFactory.DEFAULT_ROWSPEC
                                    }), panelAppearance);

                            panelAppearanceBuilder.add(labelLaF, cc.xy(3, 1));
                            panelAppearanceBuilder.add(comboLaF, cc.xy(5, 1));
                            panelAppearanceBuilder.add(labelRequiresRestart, cc.xy(7, 1));
                            panelAppearanceBuilder.add(labelDateTextFormat, cc.xy(3, 3));
                            panelAppearanceBuilder.add(comboDateTextFormat, cc.xy(5, 3));
                        }

                        PanelBuilder panelGeneralBuilder = new PanelBuilder(new FormLayout(
                                ColumnSpec.decodeSpecs("default:grow"),
                                new RowSpec[]{
                                        new RowSpec(RowSpec.FILL, Sizes.DEFAULT, FormSpec.NO_GROW),
                                        FormFactory.RELATED_GAP_ROWSPEC,
                                        new RowSpec(RowSpec.FILL, Sizes.DEFAULT, FormSpec.NO_GROW)
                                }), panelGeneral);

                        panelGeneralBuilder.add(panelGeneralSettings, cc.xy(1, 1));
                        panelGeneralBuilder.add(panelAppearance, cc.xy(1, 3));
                    }
                    panelCard.add(panelGeneral, Card.CARD1.toString());

                    //======== panelAlarm ========
                    {
                        panelAlarm.setBorder(Borders.TABBED_DIALOG_BORDER);
                        panelAlarm.setName("panelAlarm");

                        //======== panelWhenAlarmGoesOff ========
                        {
                            panelWhenAlarmGoesOff.setBorder(new TitledBorder(resourceMap.getString("panelWhenAlarmGoesOff_border")));
                            panelWhenAlarmGoesOff.setName("panelWhenAlarmGoesOff");

                            //---- labelPlaySound ----

                            checkPlaySound.setName("checkPlaySound");

                            //---- fieldSoundPath ----
                            fieldSoundPath.setName("fieldSoundPath");

                            //---- btnUseDefaultSound ----

                            btnUseDefaultSound.setName("btnUseDefaultSound");

                            //---- btnBrowse ----

                            btnBrowse.setName("btnBrowse");

                            //---- btnPreview ----

                            btnPreview.setName("btnPreview");

                            //---- checkShowAlarmBox ----

                            checkShowAlarmBox.setName("checkShowAlarmBox");

                            //---- checkShowMissedAlarms ----

                            checkShowMissedAlarms.setName("checkShowMissedAlarms");

                            PanelBuilder panelWhenAlarmGoesOffBuilder = new PanelBuilder(new FormLayout(
                                    new ColumnSpec[]{
                                            new ColumnSpec(ColumnSpec.LEFT, Sizes.dluX(0), FormSpec.NO_GROW),
                                            FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                            FormFactory.DEFAULT_COLSPEC,
                                            FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                            new ColumnSpec(ColumnSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW),
                                            FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                            FormFactory.DEFAULT_COLSPEC,
                                            FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                            FormFactory.DEFAULT_COLSPEC,
                                            FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                            FormFactory.DEFAULT_COLSPEC
                                    },
                                    new RowSpec[]{
                                            FormFactory.DEFAULT_ROWSPEC,
                                            FormFactory.NARROW_LINE_GAP_ROWSPEC,
                                            FormFactory.DEFAULT_ROWSPEC,
                                            FormFactory.LINE_GAP_ROWSPEC,
                                            FormFactory.DEFAULT_ROWSPEC,
                                            FormFactory.DEFAULT_ROWSPEC
                                    }), panelWhenAlarmGoesOff);
                            ((FormLayout) panelWhenAlarmGoesOff.getLayout()).setColumnGroups(new int[][]{{9, 11}});

                            panelWhenAlarmGoesOffBuilder.add(checkPlaySound, cc.xy(3, 1));
                            panelWhenAlarmGoesOffBuilder.add(fieldSoundPath, cc.xywh(5, 1, 7, 1));
                            panelWhenAlarmGoesOffBuilder.add(btnUseDefaultSound, cc.xy(7, 3));
                            panelWhenAlarmGoesOffBuilder.add(btnBrowse, cc.xy(9, 3));
                            panelWhenAlarmGoesOffBuilder.add(btnPreview, cc.xy(11, 3));
                            panelWhenAlarmGoesOffBuilder.add(checkShowAlarmBox, cc.xywh(3, 5, 5, 1));
                            panelWhenAlarmGoesOffBuilder.add(checkShowMissedAlarms, cc.xywh(3, 6, 5, 1));
                        }

                        //======== panelAlarmDefaults ========
                        {
                            panelAlarmDefaults.setBorder(new TitledBorder(resourceMap.getString("panelAlarmDefaults_border")));
                            panelAlarmDefaults.setName("panelAlarmDefaults");

                            //---- labelDefaultAlarmSettingForEvent ----

                            labelDefaultAlarmSettingForEvent.setLabelFor(comboDefaultAlarmSettingForEvent);
                            labelDefaultAlarmSettingForEvent.setName("labelDefaultAlarmSettingForEvent");

                            //---- comboDefaultAlarmSettingForEvent ----
                            comboDefaultAlarmSettingForEvent.setName("comboDefaultAlarmSettingForEvent");

                            //---- labelDefaultAlarmSettingForTask ----

                            labelDefaultAlarmSettingForTask.setLabelFor(comboDefaultAlarmSettingForTask);
                            labelDefaultAlarmSettingForTask.setName("labelDefaultAlarmSettingForTask");

                            //---- comboDefaultAlarmSettingForTask ----
                            comboDefaultAlarmSettingForTask.setName("comboDefaultAlarmSettingForTask");

                            //---- labelDefaultTimeBeforeEvent ----

                            labelDefaultTimeBeforeEvent.setLabelFor(spinnerDefaultTimeBeforeEvent);
                            labelDefaultTimeBeforeEvent.setName("labelDefaultTimeBeforeEvent");

                            //---- spinnerDefaultTimeBeforeEvent ----
                            spinnerDefaultTimeBeforeEvent.setName("spinnerDefaultTimeBeforeEvent");

                            //---- comboTimeUnitEvent ----
                            comboTimeUnitEvent.setName("comboTimeUnitEvent");

                            //---- labelDefaultTimeBeforeTask ----

                            labelDefaultTimeBeforeTask.setLabelFor(spinnerDefaultTimeBeforeTask);
                            labelDefaultTimeBeforeTask.setName("labelDefaultTimeBeforeTask");

                            //---- spinnerDefaultTimeBeforeTask ----

                            spinnerDefaultTimeBeforeTask.setName("spinnerDefaultTimeBeforeTask");

                            //---- comboTimeUnitTask ----
                            comboTimeUnitTask.setName("comboTimeUnitTask");

                            PanelBuilder panelAlarmDefaultsBuilder = new PanelBuilder(new FormLayout(
                                    new ColumnSpec[]{
                                            new ColumnSpec(ColumnSpec.LEFT, Sizes.dluX(0), FormSpec.NO_GROW),
                                            FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                            FormFactory.DEFAULT_COLSPEC,
                                            FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                            FormFactory.DEFAULT_COLSPEC,
                                            FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                            FormFactory.DEFAULT_COLSPEC
                                    },
                                    new RowSpec[]{
                                            FormFactory.DEFAULT_ROWSPEC,
                                            FormFactory.NARROW_LINE_GAP_ROWSPEC,
                                            FormFactory.DEFAULT_ROWSPEC,
                                            FormFactory.NARROW_LINE_GAP_ROWSPEC,
                                            FormFactory.DEFAULT_ROWSPEC,
                                            FormFactory.NARROW_LINE_GAP_ROWSPEC,
                                            FormFactory.DEFAULT_ROWSPEC
                                    }), panelAlarmDefaults);

                            panelAlarmDefaultsBuilder.add(labelDefaultAlarmSettingForEvent, cc.xy(3, 1));
                            panelAlarmDefaultsBuilder.add(comboDefaultAlarmSettingForEvent, cc.xywh(5, 1, 3, 1));
                            panelAlarmDefaultsBuilder.add(labelDefaultAlarmSettingForTask, cc.xy(3, 3));
                            panelAlarmDefaultsBuilder.add(comboDefaultAlarmSettingForTask, cc.xywh(5, 3, 3, 1));
                            panelAlarmDefaultsBuilder.add(labelDefaultTimeBeforeEvent, cc.xy(3, 5));
                            panelAlarmDefaultsBuilder.add(spinnerDefaultTimeBeforeEvent, cc.xywh(5, 5, 1, 1, CellConstraints.DEFAULT, CellConstraints.FILL));
                            panelAlarmDefaultsBuilder.add(comboTimeUnitEvent, cc.xy(7, 5));
                            panelAlarmDefaultsBuilder.add(labelDefaultTimeBeforeTask, cc.xy(3, 7));
                            panelAlarmDefaultsBuilder.add(spinnerDefaultTimeBeforeTask, cc.xywh(5, 7, 1, 1, CellConstraints.DEFAULT, CellConstraints.FILL));
                            panelAlarmDefaultsBuilder.add(comboTimeUnitTask, cc.xy(7, 7));
                        }

                        PanelBuilder panelAlarmBuilder = new PanelBuilder(new FormLayout(
                                ColumnSpec.decodeSpecs("default:grow"),
                                new RowSpec[]{
                                        FormFactory.DEFAULT_ROWSPEC,
                                        FormFactory.RELATED_GAP_ROWSPEC,
                                        FormFactory.DEFAULT_ROWSPEC
                                }), panelAlarm);

                        panelAlarmBuilder.add(panelWhenAlarmGoesOff, cc.xy(1, 1));
                        panelAlarmBuilder.add(panelAlarmDefaults, cc.xy(1, 3));
                    }
                    panelCard.add(panelAlarm, Card.CARD2.toString());

                    //======== panelCategories ========
                    {
                        panelCategories.setBorder(Borders.TABBED_DIALOG_BORDER);
                        panelCategories.setName("panelCategories");

                        //======== panelListCategories ========
                        {
                            panelListCategories.setBorder(new TitledBorder(resourceMap.getString("panelListCategories_border")));
                            panelListCategories.setName("panelListCategories");

                            //======== scrollPaneCategories ========
                            {
                                scrollPaneCategories.setName("scrollPaneCategories");

                                //---- tableCategories ----
                                tableCategories.setName("tableCategories");
                                scrollPaneCategories.setViewportView(tableCategories);
                            }

                            //---- btnCategoryAdd ----

                            btnCategoryAdd.setName("btnCategoryAdd");

                            //---- btnCategoryEdit ----

                            btnCategoryEdit.setName("btnCategoryEdit");

                            //---- btnCategoryRemove ----

                            btnCategoryRemove.setName("btnCategoryRemove");

                            PanelBuilder panelListCategoriesBuilder = new PanelBuilder(new FormLayout(
                                    new ColumnSpec[]{
                                            new ColumnSpec(ColumnSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW),
                                            FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                            FormFactory.DEFAULT_COLSPEC,
                                            FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                            FormFactory.DEFAULT_COLSPEC,
                                            FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                            FormFactory.DEFAULT_COLSPEC
                                    },
                                    new RowSpec[]{
                                            new RowSpec(Sizes.dluY(120)),
                                            FormFactory.LINE_GAP_ROWSPEC,
                                            FormFactory.DEFAULT_ROWSPEC
                                    }), panelListCategories);
                            ((FormLayout) panelListCategories.getLayout()).setColumnGroups(new int[][]{{3, 5, 7}});

                            panelListCategoriesBuilder.add(scrollPaneCategories, cc.xywh(1, 1, 7, 1));
                            panelListCategoriesBuilder.add(btnCategoryAdd, cc.xy(3, 3));
                            panelListCategoriesBuilder.add(btnCategoryEdit, cc.xy(5, 3));
                            panelListCategoriesBuilder.add(btnCategoryRemove, cc.xy(7, 3));
                        }

                        PanelBuilder panelCategoriesBuilder = new PanelBuilder(new FormLayout(
                                "default:grow",
                                "default"), panelCategories);

                        panelCategoriesBuilder.add(panelListCategories, cc.xy(1, 1));
                    }
                    panelCard.add(panelCategories, Card.CARD3.toString());

                    //======== panelViews ========
                    {
                        panelViews.setBorder(Borders.TABBED_DIALOG_BORDER);
                        panelViews.setName("panelViews");

                        //======== panelDayAndWeekViews ========
                        {
                            panelDayAndWeekViews.setBorder(new TitledBorder(resourceMap.getString("panelDayAndWeekViews_border")));
                            panelDayAndWeekViews.setName("panelDayAndWeekViews");

                            //---- labelDayStartsAt ----

                            labelDayStartsAt.setLabelFor(comboDayStartsAt);
                            labelDayStartsAt.setName("labelDayStartsAt");

                            //---- comboDayStartsAt ----
                            comboDayStartsAt.setName("comboDayStartsAt");

                            //---- labelShow ----

                            labelShow.setLabelFor(comboShowHoursAtATime);
                            labelShow.setName("labelShow");

                            //---- comboShowHoursAtATime ----
                            comboShowHoursAtATime.setName("comboShowHoursAtATime");

                            //---- labelHoursAtATime ----

                            labelHoursAtATime.setName("labelHoursAtATime");

                            //---- labelDayEndsAt ----

                            labelDayEndsAt.setLabelFor(comboDayEndsAt);
                            labelDayEndsAt.setName("labelDayEndsAt");

                            //---- comboDayEndsAt ----
                            comboDayEndsAt.setName("comboDayEndsAt");

                            PanelBuilder panelDayAndWeekViewsBuilder = new PanelBuilder(new FormLayout(
                                    new ColumnSpec[]{
                                            new ColumnSpec("left:0px"),
                                            FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                            FormFactory.DEFAULT_COLSPEC,
                                            FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                            FormFactory.DEFAULT_COLSPEC,
                                            new ColumnSpec(ColumnSpec.LEFT, Sizes.bounded(Sizes.DEFAULT, Sizes.dluX(35), Sizes.dluX(200)), FormSpec.NO_GROW),
                                            FormFactory.DEFAULT_COLSPEC,
                                            FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                            FormFactory.DEFAULT_COLSPEC,
                                            FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                            FormFactory.DEFAULT_COLSPEC
                                    },
                                    new RowSpec[]{
                                            FormFactory.DEFAULT_ROWSPEC,
                                            FormFactory.NARROW_LINE_GAP_ROWSPEC,
                                            FormFactory.DEFAULT_ROWSPEC
                                    }), panelDayAndWeekViews);

                            panelDayAndWeekViewsBuilder.add(labelDayStartsAt, cc.xy(3, 1));
                            panelDayAndWeekViewsBuilder.add(comboDayStartsAt, cc.xy(5, 1));
                            panelDayAndWeekViewsBuilder.add(labelShow, cc.xy(7, 1));
                            panelDayAndWeekViewsBuilder.add(comboShowHoursAtATime, cc.xy(9, 1));
                            panelDayAndWeekViewsBuilder.add(labelHoursAtATime, cc.xy(11, 1));
                            panelDayAndWeekViewsBuilder.add(labelDayEndsAt, cc.xy(3, 3));
                            panelDayAndWeekViewsBuilder.add(comboDayEndsAt, cc.xy(5, 3));
                        }

                        //======== panelMultiWeekView ========
                        {
                            panelMultiWeekView.setBorder(new TitledBorder(resourceMap.getString("panelMultiWeekView_border")));
                            panelMultiWeekView.setName("panelMultiWeekView");

                            //---- labelDefaultWeeksToShow ----

                            labelDefaultWeeksToShow.setLabelFor(comboDefaultWeeksToShow);
                            labelDefaultWeeksToShow.setName("labelDefaultWeeksToShow");

                            //---- comboDefaultWeeksToShow ----
                            comboDefaultWeeksToShow.setName("comboDefaultWeeksToShow");

                            //---- labelPrevisouWeeksToShow ----

                            labelPrevisouWeeksToShow.setLabelFor(comboPreviousWeeksToShow);
                            labelPrevisouWeeksToShow.setName("labelPrevisouWeeksToShow");

                            //---- comboPreviousWeeksToShow ----
                            comboPreviousWeeksToShow.setName("comboPreviousWeeksToShow");

                            PanelBuilder panelMultiWeekViewBuilder = new PanelBuilder(new FormLayout(
                                    new ColumnSpec[]{
                                            new ColumnSpec("0px"),
                                            FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                            new ColumnSpec("max(pref;50dlu)"),
                                            FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                            FormFactory.PREF_COLSPEC,
                                            FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                            new ColumnSpec(Sizes.dluX(38)),
                                            FormFactory.DEFAULT_COLSPEC
                                    },
                                    new RowSpec[]{
                                            FormFactory.DEFAULT_ROWSPEC,
                                            FormFactory.NARROW_LINE_GAP_ROWSPEC,
                                            FormFactory.DEFAULT_ROWSPEC
                                    }), panelMultiWeekView);


                            panelMultiWeekViewBuilder.add(labelDefaultWeeksToShow, cc.xywh(3, 1, 5, 1, CellConstraints.FILL, CellConstraints.DEFAULT));
                            panelMultiWeekViewBuilder.add(comboDefaultWeeksToShow, cc.xy(8, 1));
                            panelMultiWeekViewBuilder.add(labelPrevisouWeeksToShow, cc.xywh(3, 3, 1, 1, CellConstraints.LEFT, CellConstraints.DEFAULT));
                            panelMultiWeekViewBuilder.add(comboPreviousWeeksToShow, cc.xy(5, 3));
                        }

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
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner Open Source Project license - unknown
    private com.l2fprod.common.swing.JButtonBar toolbar;
    private JPanel panelCard;
    private JCheckBox checkShowIconInSystemTray;
    private JSpinner spinnerDefaultEventLength;
    private JSpinner spinnerlDefaultSnoozeLength;
    private JComboBox comboLaF;
    private JComboBox comboDateTextFormat;
    private JTextField fieldSoundPath;
    private JButton btnUseDefaultSound;
    private JButton btnBrowse;
    private JButton btnPreview;
    private JCheckBox checkShowAlarmBox;
    private JCheckBox checkShowMissedAlarms;
    private JComboBox comboDefaultAlarmSettingForEvent;
    private JComboBox comboDefaultAlarmSettingForTask;
    private JSpinner spinnerDefaultTimeBeforeEvent;
    private JComboBox comboTimeUnitEvent;
    private JSpinner spinnerDefaultTimeBeforeTask;
    private JComboBox comboTimeUnitTask;
    private JTable tableCategories;
    private JButton btnCategoryAdd;
    private JButton btnCategoryEdit;
    private JButton btnCategoryRemove;
    private JLabel labelDayStartsAt;
    private JComboBox comboDayStartsAt;
    private JComboBox comboShowHoursAtATime;
    private JComboBox comboDayEndsAt;
    private JComboBox comboDefaultWeeksToShow;
    private JComboBox comboPreviousWeeksToShow;
    private JPanel buttonBar;
    private JButton btnOK;
    private JButton btnCancel;

    private JPanel panelGeneral;
    private JPanel panelAlarm;
    private JPanel panelCategories;
    private JPanel panelViews;


    private static class ApplyPreferenceChangeListener implements PreferenceChangeListener {
        public void preferenceChange(PreferenceChangeEvent evt) {
            final MainApp app = MainApp.getInstance(MainApp.class);

            if (AppPrefs.SHOW_TRAY.equals(evt.getKey())) {
                app.getTrayIconSupport().setVisibleByDefault();
            }
        }
    }
}
