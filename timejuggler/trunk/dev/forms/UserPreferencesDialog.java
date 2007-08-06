import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import com.jgoodies.forms.builder.*;
import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;
import com.l2fprod.common.swing.*;
import org.jdesktop.swingx.*;
/*
 * Created by JFormDesigner on Mon Aug 06 17:23:18 CEST 2007
 */



/**
 * @author Vity
 */
public class UserPreferencesDialog extends JDialog {
	public UserPreferencesDialog(Frame owner) {
		super(owner);
		initComponents();
	}

	public UserPreferencesDialog(Dialog owner) {
		super(owner);
		initComponents();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		// Generated using JFormDesigner Open Source Project license - unknown
		ResourceBundle bundle = ResourceBundle.getBun;
		JPanel dialogPane = new JPanel();
		JPanel contentPanel = new JPanel();
		toolbar = new JButtonBar();
		panelCard = new JPanel();
		JPanel panelGeneral = new JPanel();
		JPanel panelGeneralSettings = new JPanel();
		checkShowIconInSystemTray = new JCheckBox();
		JLabel labelDefaultEventLength = new JLabel();
		spinnerDefaultEventLength = new JSpinner();
		JLabel labelMinutes1 = new JLabel();
		JLabel labelDefaultSnoozeLength = new JLabel();
		spinnerlDefaultSnoozeLength = new JSpinner();
		JLabel labelMinutes2 = new JLabel();
		JPanel panelAppearance = new JPanel();
		JLabel labelLaF = new JLabel();
		comboLaF = new JComboBox();
		JLabel labelRequiresRestart = new JLabel();
		JLabel labelDateTextFormat = new JLabel();
		comboDateTextFormat = new JComboBox();
		JPanel panelAlarm = new JPanel();
		JPanel panelWhenAlarmGoesOff = new JPanel();
		JLabel labelPlaySound = new JLabel();
		fieldSoundPath = new JTextField();
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
		JPanel panelCategories = new JPanel();
		JPanel panelListCategories = new JPanel();
		JScrollPane scrollPaneCategories = new JScrollPane();
		tableCategories = new JXTable();
		btnCategoryAdd = new JButton();
		btnCategoryEdit = new JButton();
		btnCategoryRemove = new JButton();
		JPanel panelViews = new JPanel();
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
		setTitle(bundle.getString("this.title"));
		setName("this");
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

		//======== dialogPane ========
		{
			dialogPane.setName("dialogPane");
			dialogPane.setLayout(new BorderLayout());

			//======== contentPanel ========
			{
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
						panelGeneral.setName("panelGeneral");

						//======== panelGeneralSettings ========
						{
							panelGeneralSettings.setBorder(new TitledBorder(bundle.getString("panelGeneralSettings.border")));
							panelGeneralSettings.setName("panelGeneralSettings");

							//---- checkShowIconInSystemTray ----
							checkShowIconInSystemTray.setText(bundle.getString("checkShowIconInSystemTray.text"));
							checkShowIconInSystemTray.setName("checkShowIconInSystemTray");

							//---- labelDefaultEventLength ----
							labelDefaultEventLength.setText(bundle.getString("labelDefaultEventLength.text"));
							labelDefaultEventLength.setLabelFor(spinnerDefaultEventLength);
							labelDefaultEventLength.setName("labelDefaultEventLength");

							//---- spinnerDefaultEventLength ----
							spinnerDefaultEventLength.setModel(new SpinnerNumberModel(1, 1, 999, 1));
							spinnerDefaultEventLength.setName("spinnerDefaultEventLength");

							//---- labelMinutes1 ----
							labelMinutes1.setText(bundle.getString("labelMinutes1.text"));
							labelMinutes1.setName("labelMinutes1");

							//---- labelDefaultSnoozeLength ----
							labelDefaultSnoozeLength.setText(bundle.getString("labelDefaultSnoozeLength.text"));
							labelDefaultSnoozeLength.setLabelFor(spinnerlDefaultSnoozeLength);
							labelDefaultSnoozeLength.setName("labelDefaultSnoozeLength");

							//---- spinnerlDefaultSnoozeLength ----
							spinnerlDefaultSnoozeLength.setModel(new SpinnerNumberModel(1, 1, 999, 1));
							spinnerlDefaultSnoozeLength.setName("spinnerlDefaultSnoozeLength");

							//---- labelMinutes2 ----
							labelMinutes2.setText(bundle.getString("labelMinutes2.text"));
							labelMinutes2.setName("labelMinutes2");

							PanelBuilder panelGeneralSettingsBuilder = new PanelBuilder(new FormLayout(
								new ColumnSpec[] {
									new ColumnSpec(ColumnSpec.LEFT, Sizes.dluX(0), FormSpec.NO_GROW),
									FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
									FormFactory.DEFAULT_COLSPEC,
									FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
									FormFactory.DEFAULT_COLSPEC,
									FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
									FormFactory.DEFAULT_COLSPEC
								},
								new RowSpec[] {
									FormFactory.DEFAULT_ROWSPEC,
									FormFactory.UNRELATED_GAP_ROWSPEC,
									FormFactory.DEFAULT_ROWSPEC,
									FormFactory.LINE_GAP_ROWSPEC,
									FormFactory.DEFAULT_ROWSPEC
								}), panelGeneralSettings);

							panelGeneralSettingsBuilder.add(checkShowIconInSystemTray,   cc.xywh(3, 1, 3, 1));
							panelGeneralSettingsBuilder.add(labelDefaultEventLength,     cc.xy  (3, 3));
							panelGeneralSettingsBuilder.add(spinnerDefaultEventLength,   cc.xywh(5, 3, 1, 1, CellConstraints.FILL, CellConstraints.FILL));
							panelGeneralSettingsBuilder.add(labelMinutes1,               cc.xy  (7, 3));
							panelGeneralSettingsBuilder.add(labelDefaultSnoozeLength,    cc.xy  (3, 5));
							panelGeneralSettingsBuilder.add(spinnerlDefaultSnoozeLength, cc.xywh(5, 5, 1, 1, CellConstraints.FILL, CellConstraints.FILL));
							panelGeneralSettingsBuilder.add(labelMinutes2,               cc.xy  (7, 5));
						}

						//======== panelAppearance ========
						{
							panelAppearance.setBorder(new CompoundBorder(
								new TitledBorder(bundle.getString("panelAppearance.border")),
								Borders.DLU2_BORDER));
							panelAppearance.setName("panelAppearance");

							//---- labelLaF ----
							labelLaF.setText(bundle.getString("labelLaF.text"));
							labelLaF.setLabelFor(comboLaF);
							labelLaF.setName("labelLaF");

							//---- comboLaF ----
							comboLaF.setName("comboLaF");

							//---- labelRequiresRestart ----
							labelRequiresRestart.setText(bundle.getString("labelRequiresRestart.text"));
							labelRequiresRestart.setName("labelRequiresRestart");

							//---- labelDateTextFormat ----
							labelDateTextFormat.setText(bundle.getString("labelDateTextFormat.text"));
							labelDateTextFormat.setLabelFor(comboDateTextFormat);
							labelDateTextFormat.setName("labelDateTextFormat");

							//---- comboDateTextFormat ----
							comboDateTextFormat.setName("comboDateTextFormat");

							PanelBuilder panelAppearanceBuilder = new PanelBuilder(new FormLayout(
								new ColumnSpec[] {
									new ColumnSpec(ColumnSpec.LEFT, Sizes.dluX(0), FormSpec.NO_GROW),
									FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
									FormFactory.DEFAULT_COLSPEC,
									FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
									FormFactory.PREF_COLSPEC,
									FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
									FormFactory.DEFAULT_COLSPEC
								},
								new RowSpec[] {
									FormFactory.DEFAULT_ROWSPEC,
									FormFactory.NARROW_LINE_GAP_ROWSPEC,
									FormFactory.DEFAULT_ROWSPEC
								}), panelAppearance);

							panelAppearanceBuilder.add(labelLaF,             cc.xy(3, 1));
							panelAppearanceBuilder.add(comboLaF,             cc.xy(5, 1));
							panelAppearanceBuilder.add(labelRequiresRestart, cc.xy(7, 1));
							panelAppearanceBuilder.add(labelDateTextFormat,  cc.xy(3, 3));
							panelAppearanceBuilder.add(comboDateTextFormat,  cc.xy(5, 3));
						}

						PanelBuilder panelGeneralBuilder = new PanelBuilder(new FormLayout(
							ColumnSpec.decodeSpecs("default:grow"),
							new RowSpec[] {
								new RowSpec(RowSpec.FILL, Sizes.DEFAULT, FormSpec.NO_GROW),
								FormFactory.RELATED_GAP_ROWSPEC,
								new RowSpec(RowSpec.FILL, Sizes.DEFAULT, FormSpec.NO_GROW)
							}), panelGeneral);

						panelGeneralBuilder.add(panelGeneralSettings, cc.xy(1, 1));
						panelGeneralBuilder.add(panelAppearance,      cc.xy(1, 3));
					}
					panelCard.add(panelGeneral, "CARD1");

					//======== panelAlarm ========
					{
						panelAlarm.setBorder(Borders.TABBED_DIALOG_BORDER);
						panelAlarm.setName("panelAlarm");

						//======== panelWhenAlarmGoesOff ========
						{
							panelWhenAlarmGoesOff.setBorder(new TitledBorder(bundle.getString("panelWhenAlarmGoesOff.border")));
							panelWhenAlarmGoesOff.setName("panelWhenAlarmGoesOff");

							//---- labelPlaySound ----
							labelPlaySound.setText(bundle.getString("labelPlaySound.text"));
							labelPlaySound.setLabelFor(fieldSoundPath);
							labelPlaySound.setName("labelPlaySound");

							//---- fieldSoundPath ----
							fieldSoundPath.setName("fieldSoundPath");

							//---- btnUseDefaultSound ----
							btnUseDefaultSound.setText(bundle.getString("btnUseDefaultSound.text"));
							btnUseDefaultSound.setName("btnUseDefaultSound");

							//---- btnBrowse ----
							btnBrowse.setText(bundle.getString("btnBrowse.text"));
							btnBrowse.setName("btnBrowse");

							//---- btnPreview ----
							btnPreview.setText(bundle.getString("btnPreview.text"));
							btnPreview.setName("btnPreview");

							//---- checkShowAlarmBox ----
							checkShowAlarmBox.setText(bundle.getString("checkShowAlarmBox.text"));
							checkShowAlarmBox.setName("checkShowAlarmBox");

							//---- checkShowMissedAlarms ----
							checkShowMissedAlarms.setText(bundle.getString("checkShowMissedAlarms.text"));
							checkShowMissedAlarms.setName("checkShowMissedAlarms");

							PanelBuilder panelWhenAlarmGoesOffBuilder = new PanelBuilder(new FormLayout(
								new ColumnSpec[] {
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
								new RowSpec[] {
									FormFactory.DEFAULT_ROWSPEC,
									FormFactory.NARROW_LINE_GAP_ROWSPEC,
									FormFactory.DEFAULT_ROWSPEC,
									FormFactory.LINE_GAP_ROWSPEC,
									FormFactory.DEFAULT_ROWSPEC,
									FormFactory.DEFAULT_ROWSPEC
								}), panelWhenAlarmGoesOff);
							((FormLayout)panelWhenAlarmGoesOff.getLayout()).setColumnGroups(new int[][] {{9, 11}});

							panelWhenAlarmGoesOffBuilder.add(labelPlaySound,        cc.xy  ( 3, 1));
							panelWhenAlarmGoesOffBuilder.add(fieldSoundPath,        cc.xywh( 5, 1, 7, 1));
							panelWhenAlarmGoesOffBuilder.add(btnUseDefaultSound,    cc.xy  ( 7, 3));
							panelWhenAlarmGoesOffBuilder.add(btnBrowse,             cc.xy  ( 9, 3));
							panelWhenAlarmGoesOffBuilder.add(btnPreview,            cc.xy  (11, 3));
							panelWhenAlarmGoesOffBuilder.add(checkShowAlarmBox,     cc.xywh( 3, 5, 5, 1));
							panelWhenAlarmGoesOffBuilder.add(checkShowMissedAlarms, cc.xywh( 3, 6, 5, 1));
						}

						//======== panelAlarmDefaults ========
						{
							panelAlarmDefaults.setBorder(new TitledBorder(bundle.getString("panelAlarmDefaults.border")));
							panelAlarmDefaults.setName("panelAlarmDefaults");

							//---- labelDefaultAlarmSettingForEvent ----
							labelDefaultAlarmSettingForEvent.setText(bundle.getString("labelDefaultAlarmSettingForEvent.text"));
							labelDefaultAlarmSettingForEvent.setLabelFor(comboDefaultAlarmSettingForEvent);
							labelDefaultAlarmSettingForEvent.setName("labelDefaultAlarmSettingForEvent");

							//---- comboDefaultAlarmSettingForEvent ----
							comboDefaultAlarmSettingForEvent.setName("comboDefaultAlarmSettingForEvent");

							//---- labelDefaultAlarmSettingForTask ----
							labelDefaultAlarmSettingForTask.setText(bundle.getString("labelDefaultAlarmSettingForTask.text"));
							labelDefaultAlarmSettingForTask.setLabelFor(comboDefaultAlarmSettingForTask);
							labelDefaultAlarmSettingForTask.setName("labelDefaultAlarmSettingForTask");

							//---- comboDefaultAlarmSettingForTask ----
							comboDefaultAlarmSettingForTask.setName("comboDefaultAlarmSettingForTask");

							//---- labelDefaultTimeBeforeEvent ----
							labelDefaultTimeBeforeEvent.setText(bundle.getString("labelDefaultTimeBeforeEvent.text"));
							labelDefaultTimeBeforeEvent.setLabelFor(spinnerDefaultTimeBeforeEvent);
							labelDefaultTimeBeforeEvent.setName("labelDefaultTimeBeforeEvent");

							//---- spinnerDefaultTimeBeforeEvent ----
							spinnerDefaultTimeBeforeEvent.setModel(new SpinnerNumberModel(1, 1, 999, 1));
							spinnerDefaultTimeBeforeEvent.setName("spinnerDefaultTimeBeforeEvent");

							//---- comboTimeUnitEvent ----
							comboTimeUnitEvent.setName("comboTimeUnitEvent");

							//---- labelDefaultTimeBeforeTask ----
							labelDefaultTimeBeforeTask.setText(bundle.getString("labelDefaultTimeBeforeTask.text"));
							labelDefaultTimeBeforeTask.setLabelFor(spinnerDefaultTimeBeforeTask);
							labelDefaultTimeBeforeTask.setName("labelDefaultTimeBeforeTask");

							//---- spinnerDefaultTimeBeforeTask ----
							spinnerDefaultTimeBeforeTask.setModel(new SpinnerNumberModel(1, 1, 1, 1));
							spinnerDefaultTimeBeforeTask.setName("spinnerDefaultTimeBeforeTask");

							//---- comboTimeUnitTask ----
							comboTimeUnitTask.setName("comboTimeUnitTask");

							PanelBuilder panelAlarmDefaultsBuilder = new PanelBuilder(new FormLayout(
								new ColumnSpec[] {
									new ColumnSpec(ColumnSpec.LEFT, Sizes.dluX(0), FormSpec.NO_GROW),
									FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
									FormFactory.DEFAULT_COLSPEC,
									FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
									FormFactory.DEFAULT_COLSPEC,
									FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
									FormFactory.DEFAULT_COLSPEC
								},
								new RowSpec[] {
									FormFactory.DEFAULT_ROWSPEC,
									FormFactory.NARROW_LINE_GAP_ROWSPEC,
									FormFactory.DEFAULT_ROWSPEC,
									FormFactory.NARROW_LINE_GAP_ROWSPEC,
									FormFactory.DEFAULT_ROWSPEC,
									FormFactory.NARROW_LINE_GAP_ROWSPEC,
									FormFactory.DEFAULT_ROWSPEC
								}), panelAlarmDefaults);

							panelAlarmDefaultsBuilder.add(labelDefaultAlarmSettingForEvent, cc.xy  (3, 1));
							panelAlarmDefaultsBuilder.add(comboDefaultAlarmSettingForEvent, cc.xywh(5, 1, 3, 1));
							panelAlarmDefaultsBuilder.add(labelDefaultAlarmSettingForTask,  cc.xy  (3, 3));
							panelAlarmDefaultsBuilder.add(comboDefaultAlarmSettingForTask,  cc.xywh(5, 3, 3, 1));
							panelAlarmDefaultsBuilder.add(labelDefaultTimeBeforeEvent,      cc.xy  (3, 5));
							panelAlarmDefaultsBuilder.add(spinnerDefaultTimeBeforeEvent,    cc.xywh(5, 5, 1, 1, CellConstraints.DEFAULT, CellConstraints.FILL));
							panelAlarmDefaultsBuilder.add(comboTimeUnitEvent,               cc.xy  (7, 5));
							panelAlarmDefaultsBuilder.add(labelDefaultTimeBeforeTask,       cc.xy  (3, 7));
							panelAlarmDefaultsBuilder.add(spinnerDefaultTimeBeforeTask,     cc.xywh(5, 7, 1, 1, CellConstraints.DEFAULT, CellConstraints.FILL));
							panelAlarmDefaultsBuilder.add(comboTimeUnitTask,                cc.xy  (7, 7));
						}

						PanelBuilder panelAlarmBuilder = new PanelBuilder(new FormLayout(
							ColumnSpec.decodeSpecs("default:grow"),
							new RowSpec[] {
								FormFactory.DEFAULT_ROWSPEC,
								FormFactory.RELATED_GAP_ROWSPEC,
								FormFactory.DEFAULT_ROWSPEC
							}), panelAlarm);

						panelAlarmBuilder.add(panelWhenAlarmGoesOff, cc.xy(1, 1));
						panelAlarmBuilder.add(panelAlarmDefaults,    cc.xy(1, 3));
					}
					panelCard.add(panelAlarm, "CARD2");

					//======== panelCategories ========
					{
						panelCategories.setBorder(Borders.TABBED_DIALOG_BORDER);
						panelCategories.setName("panelCategories");

						//======== panelListCategories ========
						{
							panelListCategories.setBorder(new TitledBorder(bundle.getString("panelListCategories.border")));
							panelListCategories.setName("panelListCategories");

							//======== scrollPaneCategories ========
							{
								scrollPaneCategories.setName("scrollPaneCategories");

								//---- tableCategories ----
								tableCategories.setName("tableCategories");
								scrollPaneCategories.setViewportView(tableCategories);
							}

							//---- btnCategoryAdd ----
							btnCategoryAdd.setText(bundle.getString("btnCategoryAdd.text"));
							btnCategoryAdd.setName("btnCategoryAdd");

							//---- btnCategoryEdit ----
							btnCategoryEdit.setText(bundle.getString("btnCategoryEdit.text"));
							btnCategoryEdit.setName("btnCategoryEdit");

							//---- btnCategoryRemove ----
							btnCategoryRemove.setText(bundle.getString("btnCategoryRemove.text"));
							btnCategoryRemove.setName("btnCategoryRemove");

							PanelBuilder panelListCategoriesBuilder = new PanelBuilder(new FormLayout(
								new ColumnSpec[] {
									new ColumnSpec(ColumnSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW),
									FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
									FormFactory.DEFAULT_COLSPEC,
									FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
									FormFactory.DEFAULT_COLSPEC,
									FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
									FormFactory.DEFAULT_COLSPEC
								},
								new RowSpec[] {
									new RowSpec(Sizes.dluY(120)),
									FormFactory.LINE_GAP_ROWSPEC,
									FormFactory.DEFAULT_ROWSPEC
								}), panelListCategories);
							((FormLayout)panelListCategories.getLayout()).setColumnGroups(new int[][] {{3, 5, 7}});

							panelListCategoriesBuilder.add(scrollPaneCategories, cc.xywh(1, 1, 7, 1));
							panelListCategoriesBuilder.add(btnCategoryAdd,       cc.xy  (3, 3));
							panelListCategoriesBuilder.add(btnCategoryEdit,      cc.xy  (5, 3));
							panelListCategoriesBuilder.add(btnCategoryRemove,    cc.xy  (7, 3));
						}

						PanelBuilder panelCategoriesBuilder = new PanelBuilder(new FormLayout(
							"default:grow",
							"default"), panelCategories);

						panelCategoriesBuilder.add(panelListCategories, cc.xy(1, 1));
					}
					panelCard.add(panelCategories, "CARD3");

					//======== panelViews ========
					{
						panelViews.setBorder(Borders.TABBED_DIALOG_BORDER);
						panelViews.setName("panelViews");

						//======== panelDayAndWeekViews ========
						{
							panelDayAndWeekViews.setBorder(new TitledBorder(bundle.getString("panelDayAndWeekViews.border")));
							panelDayAndWeekViews.setName("panelDayAndWeekViews");

							//---- labelDayStartsAt ----
							labelDayStartsAt.setText(bundle.getString("labelDayStartsAt.text"));
							labelDayStartsAt.setLabelFor(comboDayStartsAt);
							labelDayStartsAt.setName("labelDayStartsAt");

							//---- comboDayStartsAt ----
							comboDayStartsAt.setName("comboDayStartsAt");

							//---- labelShow ----
							labelShow.setText(bundle.getString("labelShow.text"));
							labelShow.setLabelFor(comboShowHoursAtATime);
							labelShow.setName("labelShow");

							//---- comboShowHoursAtATime ----
							comboShowHoursAtATime.setName("comboShowHoursAtATime");

							//---- labelHoursAtATime ----
							labelHoursAtATime.setText(bundle.getString("labelHoursAtATime.text"));
							labelHoursAtATime.setName("labelHoursAtATime");

							//---- labelDayEndsAt ----
							labelDayEndsAt.setText(bundle.getString("labelDayEndsAt.text"));
							labelDayEndsAt.setLabelFor(comboDayEndsAt);
							labelDayEndsAt.setName("labelDayEndsAt");

							//---- comboDayEndsAt ----
							comboDayEndsAt.setName("comboDayEndsAt");

							PanelBuilder panelDayAndWeekViewsBuilder = new PanelBuilder(new FormLayout(
								new ColumnSpec[] {
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
								new RowSpec[] {
									FormFactory.DEFAULT_ROWSPEC,
									FormFactory.NARROW_LINE_GAP_ROWSPEC,
									FormFactory.DEFAULT_ROWSPEC
								}), panelDayAndWeekViews);

							panelDayAndWeekViewsBuilder.add(labelDayStartsAt,      cc.xy( 3, 1));
							panelDayAndWeekViewsBuilder.add(comboDayStartsAt,      cc.xy( 5, 1));
							panelDayAndWeekViewsBuilder.add(labelShow,             cc.xy( 7, 1));
							panelDayAndWeekViewsBuilder.add(comboShowHoursAtATime, cc.xy( 9, 1));
							panelDayAndWeekViewsBuilder.add(labelHoursAtATime,     cc.xy(11, 1));
							panelDayAndWeekViewsBuilder.add(labelDayEndsAt,        cc.xy( 3, 3));
							panelDayAndWeekViewsBuilder.add(comboDayEndsAt,        cc.xy( 5, 3));
						}

						//======== panelMultiWeekView ========
						{
							panelMultiWeekView.setBorder(new TitledBorder(bundle.getString("panelMultiWeekView.border")));
							panelMultiWeekView.setName("panelMultiWeekView");

							//---- labelDefaultWeeksToShow ----
							labelDefaultWeeksToShow.setText(bundle.getString("labelDefaultWeeksToShow.text"));
							labelDefaultWeeksToShow.setLabelFor(comboDefaultWeeksToShow);
							labelDefaultWeeksToShow.setName("labelDefaultWeeksToShow");

							//---- comboDefaultWeeksToShow ----
							comboDefaultWeeksToShow.setName("comboDefaultWeeksToShow");

							//---- labelPrevisouWeeksToShow ----
							labelPrevisouWeeksToShow.setText(bundle.getString("labelPrevisouWeeksToShow.text"));
							labelPrevisouWeeksToShow.setLabelFor(comboPreviousWeeksToShow);
							labelPrevisouWeeksToShow.setName("labelPrevisouWeeksToShow");

							//---- comboPreviousWeeksToShow ----
							comboPreviousWeeksToShow.setName("comboPreviousWeeksToShow");

							PanelBuilder panelMultiWeekViewBuilder = new PanelBuilder(new FormLayout(
								new ColumnSpec[] {
									new ColumnSpec("0px"),
									FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
									new ColumnSpec("max(pref;50dlu)"),
									FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
									new ColumnSpec(Sizes.dluX(34)),
									FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
									new ColumnSpec(Sizes.dluX(38)),
									FormFactory.DEFAULT_COLSPEC
								},
								new RowSpec[] {
									FormFactory.DEFAULT_ROWSPEC,
									FormFactory.NARROW_LINE_GAP_ROWSPEC,
									FormFactory.DEFAULT_ROWSPEC
								}), panelMultiWeekView);

							panelMultiWeekViewBuilder.add(labelDefaultWeeksToShow,  cc.xywh(3, 1, 5, 1, CellConstraints.FILL, CellConstraints.DEFAULT));
							panelMultiWeekViewBuilder.add(comboDefaultWeeksToShow,  cc.xy  (8, 1));
							panelMultiWeekViewBuilder.add(labelPrevisouWeeksToShow, cc.xywh(3, 3, 1, 1, CellConstraints.LEFT, CellConstraints.DEFAULT));
							panelMultiWeekViewBuilder.add(comboPreviousWeeksToShow, cc.xy  (5, 3));
						}

						PanelBuilder panelViewsBuilder = new PanelBuilder(new FormLayout(
							ColumnSpec.decodeSpecs("default:grow"),
							new RowSpec[] {
								FormFactory.DEFAULT_ROWSPEC,
								FormFactory.RELATED_GAP_ROWSPEC,
								FormFactory.DEFAULT_ROWSPEC
							}), panelViews);

						panelViewsBuilder.add(panelDayAndWeekViews, cc.xy(1, 1));
						panelViewsBuilder.add(panelMultiWeekView,   cc.xy(1, 3));
					}
					panelCard.add(panelViews, "CARD4");
				}
				contentPanel.add(panelCard, BorderLayout.CENTER);
			}
			dialogPane.add(contentPanel, BorderLayout.CENTER);

			//======== buttonBar ========
			{
				buttonBar.setBorder(Borders.BUTTON_BAR_GAP_BORDER);
				buttonBar.setName("buttonBar");

				//---- btnOK ----
				btnOK.setText(bundle.getString("btnOK.text"));
				btnOK.setName("btnOK");

				//---- btnCancel ----
				btnCancel.setText(bundle.getString("btnCancel.text"));
				btnCancel.setName("btnCancel");

				PanelBuilder buttonBarBuilder = new PanelBuilder(new FormLayout(
					new ColumnSpec[] {
						FormFactory.GLUE_COLSPEC,
						new ColumnSpec("max(pref;42dlu)"),
						FormFactory.RELATED_GAP_COLSPEC,
						FormFactory.PREF_COLSPEC
					},
					RowSpec.decodeSpecs("pref")), buttonBar);
				((FormLayout)buttonBar.getLayout()).setColumnGroups(new int[][] {{2, 4}});

				buttonBarBuilder.add(btnOK,     cc.xy(2, 1));
				buttonBarBuilder.add(btnCancel, cc.xy(4, 1));
			}
			dialogPane.add(buttonBar, BorderLayout.SOUTH);
		}
		contentPane.add(dialogPane, BorderLayout.CENTER);
		pack();
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	// Generated using JFormDesigner Open Source Project license - unknown
	private JButtonBar toolbar;
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
	private JXTable tableCategories;
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
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
