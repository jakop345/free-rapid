import java.awt.*;
import java.util.*;
import javax.swing.*;
import com.jgoodies.forms.builder.*;
import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;
import org.jdesktop.swingx.*;
/*
 * Created by JFormDesigner on Thu Jul 26 20:30:03 CEST 2007
 */



/**
 * @author SHOCKIE
 */
public class EventDialog extends JPanel {
	public EventDialog() {
		initComponents();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		// Generated using JFormDesigner Open Source Project license - unknown
		JPanel mainPanel = new JPanel();
		JLabel labelTitle = new JLabel();
		titleField = new JTextField();
		JLabel labelLocation = new JLabel();
		locationField = new JTextField();
		JLabel labelFrom = new JLabel();
		checkDate = new JCheckBox();
		dateFromPicker = new JXDatePicker();
		timeFromSpinner = new JSpinner();
		allDayCheckbox = new JCheckBox();
		JLabel labelTo = new JLabel();
		checkDueDate = new JCheckBox();
		dateToPicker = new JXDatePicker();
		timeToSpinner = new JSpinner();
		repeatCheckbox = new JCheckBox();
		btnSetPattern = new JButton();
		JPanel panelCalendar = new JPanel();
		labelCalendar = new JLabel();
		calendarCombo = new JComboBox();
		JLabel labelCategory = new JLabel();
		categoryCombo = new JComboBox();
		morePanel = new JPanel();
		JLabel labelDescription = new JLabel();
		scrollPaneDescription = new JScrollPane();
		descriptionArea = new JTextArea();
		JLabel labelAttendees = new JLabel();
		scrollPaneAttendees = new JScrollPane();
		attendeesArea = new JEditorPane();
		JLabel labelPrivacy = new JLabel();
		privacyCombo = new JComboBox();
		JLabel labelPriority = new JLabel();
		priorityCombo = new JComboBox();
		JLabel labelStatus = new JLabel();
		statusCombo = new JComboBox();
		JLabel labelAlarm = new JLabel();
		alarmCombo = new JComboBox();
		panelAlarm = new JPanel();
		alarmTimeSpinner = new JSpinner();
		alarmTimeUnitCombo = new JComboBox();
		alarmBeforeAfterCombo = new JComboBox();
		JPanel panelURL = new JPanel();
		JLabel labelURL = new JLabel();
		urlField = new JTextField();
		btnVisitURL = new JButton();
		panelStatus = new JPanel();
		JLabel labelStatus2 = new JLabel();
		statusTypeCombo = new JComboBox();
		completedDatePicker = new JXDatePicker();
		percentCompleteSpinner = new JSpinner();
		JLabel labelComplete = new JLabel();
		panelBtn = new JPanel();
		btnOK = new JButton();
		btnCancel = new JButton();
		btnLessMore = new JButton();
		CellConstraints cc = new CellConstraints();

		//======== mainPanel ========
		{
			mainPanel.setBorder(Borders.DIALOG_BORDER);
			mainPanel.setName("mainPanel");
			mainPanel.setLayout(new BorderLayout());

			//======== this ========
			{
				this.setName("this");

				//---- labelTitle ----
				labelTitle.setText( );
				labelTitle.setLabelFor(titleField);
				labelTitle.setName("labelTitle");

				//---- titleField ----
				titleField.setName("titleField");

				//---- labelLocation ----
				labelLocation.setText( );
				labelLocation.setLabelFor(locationField);
				labelLocation.setName("labelLocation");

				//---- locationField ----
				locationField.setName("locationField");

				//---- labelFrom ----
				labelFrom.setText( );
				labelFrom.setName("labelFrom");

				//---- checkDate ----
				checkDate.setToolTipText( );
				checkDate.setName("checkDate");

				//---- dateFromPicker ----
				dateFromPicker.setName("dateFromPicker");

				//---- timeFromSpinner ----
				timeFromSpinner.setName("timeFromSpinner");

				//---- allDayCheckbox ----
				allDayCheckbox.setText( );
				allDayCheckbox.setName("allDayCheckbox");

				//---- labelTo ----
				labelTo.setText( );
				labelTo.setName("labelTo");

				//---- checkDueDate ----
				checkDueDate.setToolTipText( );
				checkDueDate.setName("checkDueDate");

				//---- dateToPicker ----
				dateToPicker.setName("dateToPicker");

				//---- timeToSpinner ----
				timeToSpinner.setName("timeToSpinner");

				//---- repeatCheckbox ----
				repeatCheckbox.setText( );
				repeatCheckbox.setName("repeatCheckbox");

				//---- btnSetPattern ----
				btnSetPattern.setText( );
				btnSetPattern.setName("btnSetPattern");

				//======== panelCalendar ========
				{
					panelCalendar.setName("panelCalendar");

					//---- labelCalendar ----
					labelCalendar.setText( );
					labelCalendar.setLabelFor(calendarCombo);
					labelCalendar.setName("labelCalendar");

					//---- calendarCombo ----
					calendarCombo.setName("calendarCombo");

					//---- labelCategory ----
					labelCategory.setText( );
					labelCategory.setLabelFor(categoryCombo);
					labelCategory.setName("labelCategory");

					//---- categoryCombo ----
					categoryCombo.setName("categoryCombo");

					PanelBuilder panelCalendarBuilder = new PanelBuilder(new FormLayout(
						new ColumnSpec[] {
							new ColumnSpec(Sizes.dluX(45)),
							FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
							FormFactory.PREF_COLSPEC,
							new ColumnSpec(ColumnSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW),
							FormFactory.DEFAULT_COLSPEC,
							FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
							FormFactory.DEFAULT_COLSPEC
						},
						RowSpec.decodeSpecs("default")), panelCalendar);

					panelCalendarBuilder.add(labelCalendar, cc.xy(1, 1));
					panelCalendarBuilder.add(calendarCombo, cc.xy(3, 1));
					panelCalendarBuilder.add(labelCategory, cc.xy(5, 1));
					panelCalendarBuilder.add(categoryCombo, cc.xy(7, 1));
				}

				//======== morePanel ========
				{
					morePanel.setName("morePanel");

					//---- labelDescription ----
					labelDescription.setText( );
					labelDescription.setLabelFor(descriptionArea);
					labelDescription.setName("labelDescription");

					//======== scrollPaneDescription ========
					{
						scrollPaneDescription.setName("scrollPaneDescription");

						//---- descriptionArea ----
						descriptionArea.setRows(5);
						descriptionArea.setName("descriptionArea");
						scrollPaneDescription.setViewportView(descriptionArea);
					}

					//---- labelAttendees ----
					labelAttendees.setText( );
					labelAttendees.setLabelFor(attendeesArea);
					labelAttendees.setName("labelAttendees");

					//======== scrollPaneAttendees ========
					{
						scrollPaneAttendees.setName("scrollPaneAttendees");

						//---- attendeesArea ----
						attendeesArea.setName("attendeesArea");
						scrollPaneAttendees.setViewportView(attendeesArea);
					}

					//---- labelPrivacy ----
					labelPrivacy.setText( );
					labelPrivacy.setLabelFor(privacyCombo);
					labelPrivacy.setName("labelPrivacy");

					//---- privacyCombo ----
					privacyCombo.setName("privacyCombo");

					//---- labelPriority ----
					labelPriority.setText( );
					labelPriority.setLabelFor(priorityCombo);
					labelPriority.setName("labelPriority");

					//---- priorityCombo ----
					priorityCombo.setName("priorityCombo");

					//---- labelStatus ----
					labelStatus.setText( );
					labelStatus.setLabelFor(statusCombo);
					labelStatus.setName("labelStatus");

					//---- statusCombo ----
					statusCombo.setName("statusCombo");

					//---- labelAlarm ----
					labelAlarm.setText( );
					labelAlarm.setLabelFor(alarmCombo);
					labelAlarm.setName("labelAlarm");

					//---- alarmCombo ----
					alarmCombo.setName("alarmCombo");

					//======== panelAlarm ========
					{
						panelAlarm.setName("panelAlarm");

						//---- alarmTimeSpinner ----
						alarmTimeSpinner.setModel(new SpinnerNumberModel(1, 0, null, 1));
						alarmTimeSpinner.setName("alarmTimeSpinner");

						//---- alarmTimeUnitCombo ----
						alarmTimeUnitCombo.setName("alarmTimeUnitCombo");

						//---- alarmBeforeAfterCombo ----
						alarmBeforeAfterCombo.setName("alarmBeforeAfterCombo");

						PanelBuilder panelAlarmBuilder = new PanelBuilder(new FormLayout(
							new ColumnSpec[] {
								new ColumnSpec("max(pref;23dlu)"),
								FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
								FormFactory.DEFAULT_COLSPEC,
								FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
								FormFactory.DEFAULT_COLSPEC
							},
							RowSpec.decodeSpecs("default")), panelAlarm);

						panelAlarmBuilder.add(alarmTimeSpinner,      cc.xywh(1, 1, 1, 1, CellConstraints.DEFAULT, CellConstraints.FILL));
						panelAlarmBuilder.add(alarmTimeUnitCombo,    cc.xy  (3, 1));
						panelAlarmBuilder.add(alarmBeforeAfterCombo, cc.xy  (5, 1));
					}

					//======== panelURL ========
					{
						panelURL.setName("panelURL");

						//---- labelURL ----
						labelURL.setText( );
						labelURL.setLabelFor(urlField);
						labelURL.setName("labelURL");

						//---- urlField ----
						urlField.setName("urlField");

						//---- btnVisitURL ----
						btnVisitURL.setText( );
						btnVisitURL.setName("btnVisitURL");

						PanelBuilder panelURLBuilder = new PanelBuilder(new FormLayout(
							new ColumnSpec[] {
								new ColumnSpec(Sizes.dluX(45)),
								FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
								new ColumnSpec(ColumnSpec.FILL, Sizes.PREFERRED, FormSpec.DEFAULT_GROW),
								FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
								FormFactory.PREF_COLSPEC
							},
							RowSpec.decodeSpecs("default")), panelURL);

						panelURLBuilder.add(labelURL,    cc.xy  (1, 1));
						panelURLBuilder.add(urlField,    cc.xy  (3, 1));
						panelURLBuilder.add(btnVisitURL, cc.xywh(5, 1, 1, 1, CellConstraints.LEFT, CellConstraints.DEFAULT));
					}

					//======== panelStatus ========
					{
						panelStatus.setName("panelStatus");

						//---- labelStatus2 ----
						labelStatus2.setText( );
						labelStatus2.setName("labelStatus2");

						//---- statusTypeCombo ----
						statusTypeCombo.setName("statusTypeCombo");

						//---- completedDatePicker ----
						completedDatePicker.setName("completedDatePicker");

						//---- percentCompleteSpinner ----
						percentCompleteSpinner.setModel(new SpinnerNumberModel(100, 0, 100, 25));
						percentCompleteSpinner.setName("percentCompleteSpinner");

						//---- labelComplete ----
						labelComplete.setText( );
						labelComplete.setName("labelComplete");

						PanelBuilder panelStatusBuilder = new PanelBuilder(new FormLayout(
							new ColumnSpec[] {
								new ColumnSpec(Sizes.dluX(45)),
								FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
								FormFactory.PREF_COLSPEC,
								FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
								FormFactory.PREF_COLSPEC,
								FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
								new ColumnSpec("max(pref;20dlu)"),
								new ColumnSpec(ColumnSpec.LEFT, Sizes.DLUX4, FormSpec.NO_GROW),
								FormFactory.DEFAULT_COLSPEC
							},
							RowSpec.decodeSpecs("default")), panelStatus);

						panelStatusBuilder.add(labelStatus2,           cc.xy  (1, 1));
						panelStatusBuilder.add(statusTypeCombo,        cc.xy  (3, 1));
						panelStatusBuilder.add(completedDatePicker,    cc.xy  (5, 1));
						panelStatusBuilder.add(percentCompleteSpinner, cc.xywh(7, 1, 1, 1, CellConstraints.DEFAULT, CellConstraints.FILL));
						panelStatusBuilder.add(labelComplete,          cc.xy  (9, 1));
					}

					PanelBuilder morePanelBuilder = new PanelBuilder(new FormLayout(
						new ColumnSpec[] {
							new ColumnSpec(Sizes.dluX(45)),
							FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
							new ColumnSpec(ColumnSpec.FILL, Sizes.PREFERRED, FormSpec.DEFAULT_GROW),
							FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
							new ColumnSpec(Sizes.dluX(30)),
							FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
							FormFactory.PREF_COLSPEC
						},
						new RowSpec[] {
							new RowSpec(RowSpec.TOP, Sizes.PREFERRED, FormSpec.DEFAULT_GROW),
							FormFactory.LINE_GAP_ROWSPEC,
							FormFactory.DEFAULT_ROWSPEC,
							FormFactory.LINE_GAP_ROWSPEC,
							FormFactory.PREF_ROWSPEC,
							FormFactory.LINE_GAP_ROWSPEC,
							FormFactory.DEFAULT_ROWSPEC,
							FormFactory.LINE_GAP_ROWSPEC,
							FormFactory.DEFAULT_ROWSPEC,
							FormFactory.LINE_GAP_ROWSPEC,
							FormFactory.DEFAULT_ROWSPEC,
							FormFactory.LINE_GAP_ROWSPEC,
							FormFactory.DEFAULT_ROWSPEC,
							FormFactory.LINE_GAP_ROWSPEC,
							FormFactory.DEFAULT_ROWSPEC
						}), morePanel);

					morePanelBuilder.add(labelDescription,      cc.xywh(1,  1, 1, 1, CellConstraints.DEFAULT, CellConstraints.TOP    ));
					morePanelBuilder.add(scrollPaneDescription, cc.xywh(3,  1, 5, 1, CellConstraints.DEFAULT, CellConstraints.FILL   ));
					morePanelBuilder.add(labelAttendees,        cc.xy  (1,  3));
					morePanelBuilder.add(scrollPaneAttendees,   cc.xywh(3,  3, 1, 9));
					morePanelBuilder.add(labelPrivacy,          cc.xy  (5,  3));
					morePanelBuilder.add(privacyCombo,          cc.xy  (7,  3));
					morePanelBuilder.add(labelPriority,         cc.xy  (5,  5));
					morePanelBuilder.add(priorityCombo,         cc.xy  (7,  5));
					morePanelBuilder.add(labelStatus,           cc.xy  (5,  7));
					morePanelBuilder.add(statusCombo,           cc.xy  (7,  7));
					morePanelBuilder.add(labelAlarm,            cc.xy  (5,  9));
					morePanelBuilder.add(alarmCombo,            cc.xywh(7,  9, 1, 1, CellConstraints.LEFT   , CellConstraints.DEFAULT));
					morePanelBuilder.add(panelAlarm,            cc.xy  (7, 11));
					morePanelBuilder.add(panelURL,              cc.xywh(1, 13, 7, 1));
					morePanelBuilder.add(panelStatus,           cc.xywh(1, 15, 7, 1));
				}

				//======== panelBtn ========
				{
					panelBtn.setBorder(Borders.BUTTON_BAR_GAP_BORDER);
					panelBtn.setName("panelBtn");

					//---- btnOK ----
					btnOK.setText( );
					btnOK.setName("btnOK");

					//---- btnCancel ----
					btnCancel.setText( );
					btnCancel.setName("btnCancel");

					//---- btnLessMore ----
					btnLessMore.setText( );
					btnLessMore.setName("btnLessMore");

					PanelBuilder panelBtnBuilder = new PanelBuilder(new FormLayout(
						new ColumnSpec[] {
							new ColumnSpec(ColumnSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW),
							FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
							FormFactory.PREF_COLSPEC,
							FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
							FormFactory.PREF_COLSPEC,
							FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
							FormFactory.PREF_COLSPEC
						},
						RowSpec.decodeSpecs("default")), panelBtn);
					((FormLayout)panelBtn.getLayout()).setColumnGroups(new int[][] {{3, 5, 7}});

					panelBtnBuilder.add(btnOK,       cc.xy(3, 1));
					panelBtnBuilder.add(btnCancel,   cc.xy(5, 1));
					panelBtnBuilder.add(btnLessMore, cc.xy(7, 1));
				}

				PanelBuilder builder = new PanelBuilder(new FormLayout(
					new ColumnSpec[] {
						new ColumnSpec(Sizes.dluX(45)),
						FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
						FormFactory.PREF_COLSPEC,
						FormFactory.PREF_COLSPEC,
						FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
						new ColumnSpec("max(pref;35dlu)"),
						FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
						FormFactory.PREF_COLSPEC,
						FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
						FormFactory.PREF_COLSPEC,
						FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
						new ColumnSpec(ColumnSpec.FILL, Sizes.PREFERRED, FormSpec.DEFAULT_GROW)
					},
					new RowSpec[] {
						FormFactory.PREF_ROWSPEC,
						FormFactory.LINE_GAP_ROWSPEC,
						FormFactory.PREF_ROWSPEC,
						FormFactory.LINE_GAP_ROWSPEC,
						FormFactory.PREF_ROWSPEC,
						FormFactory.LINE_GAP_ROWSPEC,
						FormFactory.PREF_ROWSPEC,
						FormFactory.LINE_GAP_ROWSPEC,
						FormFactory.DEFAULT_ROWSPEC,
						FormFactory.LINE_GAP_ROWSPEC,
						new RowSpec(RowSpec.FILL, Sizes.PREFERRED, FormSpec.DEFAULT_GROW),
						FormFactory.LINE_GAP_ROWSPEC,
						FormFactory.DEFAULT_ROWSPEC
					}), this);

				builder.add(labelTitle,      cc.xy  ( 1,  1));
				builder.add(titleField,      cc.xywh( 3,  1, 10, 1, CellConstraints.FILL   , CellConstraints.DEFAULT));
				builder.add(labelLocation,   cc.xy  ( 1,  3));
				builder.add(locationField,   cc.xywh( 3,  3, 10, 1, CellConstraints.FILL   , CellConstraints.DEFAULT));
				builder.add(labelFrom,       cc.xy  ( 1,  5));
				builder.add(checkDate,       cc.xywh( 3,  5,  1, 1, CellConstraints.CENTER , CellConstraints.BOTTOM ));
				builder.add(dateFromPicker,  cc.xy  ( 4,  5));
				builder.add(timeFromSpinner, cc.xywh( 6,  5,  1, 1, CellConstraints.DEFAULT, CellConstraints.FILL   ));
				builder.add(allDayCheckbox,  cc.xy  ( 8,  5));
				builder.add(labelTo,         cc.xy  ( 1,  7));
				builder.add(checkDueDate,    cc.xywh( 3,  7,  1, 1, CellConstraints.CENTER , CellConstraints.CENTER ));
				builder.add(dateToPicker,    cc.xy  ( 4,  7));
				builder.add(timeToSpinner,   cc.xywh( 6,  7,  1, 1, CellConstraints.DEFAULT, CellConstraints.FILL   ));
				builder.add(repeatCheckbox,  cc.xy  ( 8,  7));
				builder.add(btnSetPattern,   cc.xy  (10,  7));
				builder.add(panelCalendar,   cc.xywh( 1,  9, 12, 1));
				builder.add(morePanel,       cc.xywh( 1, 11, 12, 1));
				builder.add(panelBtn,        cc.xywh( 1, 13, 12, 1));
			}
			mainPanel.add(this, BorderLayout.NORTH);
		}
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	// Generated using JFormDesigner Open Source Project license - unknown
	private JTextField titleField;
	private JTextField locationField;
	private JCheckBox checkDate;
	private JXDatePicker dateFromPicker;
	private JSpinner timeFromSpinner;
	private JCheckBox allDayCheckbox;
	private JCheckBox checkDueDate;
	private JXDatePicker dateToPicker;
	private JSpinner timeToSpinner;
	private JCheckBox repeatCheckbox;
	private JButton btnSetPattern;
	private JLabel labelCalendar;
	private JComboBox calendarCombo;
	private JComboBox categoryCombo;
	private JPanel morePanel;
	private JScrollPane scrollPaneDescription;
	private JTextArea descriptionArea;
	private JScrollPane scrollPaneAttendees;
	private JEditorPane attendeesArea;
	private JComboBox privacyCombo;
	private JComboBox priorityCombo;
	private JComboBox statusCombo;
	private JComboBox alarmCombo;
	private JPanel panelAlarm;
	private JSpinner alarmTimeSpinner;
	private JComboBox alarmTimeUnitCombo;
	private JComboBox alarmBeforeAfterCombo;
	private JTextField urlField;
	private JButton btnVisitURL;
	private JPanel panelStatus;
	private JComboBox statusTypeCombo;
	private JXDatePicker completedDatePicker;
	private JSpinner percentCompleteSpinner;
	private JPanel panelBtn;
	private JButton btnOK;
	private JButton btnCancel;
	private JButton btnLessMore;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
