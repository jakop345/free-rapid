import java.awt.*;
import java.util.*;
import javax.swing.*;
import com.jgoodies.forms.builder.*;
import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;
import org.jdesktop.swingx.*;
/*
 * Created by JFormDesigner on Wed Aug 01 18:23:29 CEST 2007
 */



/**
 * @author Vity
 */
public class RecurrenceDialog {
	public RecurrenceDialog() {
		initComponents();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		// Generated using JFormDesigner Open Source Project license - unknown
		ResourceBundle bundle = ResourceBundle.getBun;
		recurrenceDialog = new JDialog();
		dialogPane = new JPanel();
		JPanel contentPanel = new JPanel();
		labelOccurs = new JLabel();
		comboOccurs = new JComboBox();
		panelOccurence = new JPanel();
		JPanel panelDaily = new JPanel();
		JLabel labelEvery1 = new JLabel();
		spinnerDaily = new JSpinner();
		JLabel labelDays = new JLabel();
		JPanel panelWeekly = new JPanel();
		JLabel labelEvery2 = new JLabel();
		spinnerWeekly = new JSpinner();
		labelWeeksOn = new JLabel();
		JPanel panelDaysInWeek = new JPanel();
		checkMonday = new JCheckBox();
		checkFriday = new JCheckBox();
		checkTuesday = new JCheckBox();
		checkSaturday = new JCheckBox();
		checkWednesday = new JCheckBox();
		checkSunday = new JCheckBox();
		checkThursday = new JCheckBox();
		JPanel panelMonthly = new JPanel();
		JLabel labelEvery3 = new JLabel();
		spinnerMonthly = new JSpinner();
		labelMonthsOnthe = new JLabel();
		radio30thDay = new JRadioButton();
		radioFifthMonday = new JRadioButton();
		radioLastMonday = new JRadioButton();
		JPanel panelAnnually = new JPanel();
		JLabel labelEvery4 = new JLabel();
		spinnerAnnually = new JSpinner();
		JLabel labelYears = new JLabel();
		JPanel panelRepeat = new JPanel();
		radioRepeatForever = new JRadioButton();
		radioRepeatFor = new JRadioButton();
		spinnerRepeatFor = new JSpinner();
		JLabel labelOccurences = new JLabel();
		repeatUntil = new JRadioButton();
		dateUntil = new JXDatePicker();
		JPanel buttonBar = new JPanel();
		btnOK = new JButton();
		btnCancel = new JButton();
		CellConstraints cc = new CellConstraints();

		//======== recurrenceDialog ========
		{
			recurrenceDialog.setTitle(bundle.getString("recurrenceDialog.title"));
			recurrenceDialog.setName("recurrenceDialog");
			Container recurrenceDialogContentPane = recurrenceDialog.getContentPane();
			recurrenceDialogContentPane.setLayout(new BorderLayout());

			//======== dialogPane ========
			{
				dialogPane.setBorder(Borders.DIALOG_BORDER);
				dialogPane.setName("dialogPane");
				dialogPane.setLayout(new BorderLayout());

				//======== contentPanel ========
				{
					contentPanel.setName("contentPanel");

					//---- labelOccurs ----
					labelOccurs.setText(bundle.getString("labelOccurs.text"));
					labelOccurs.setLabelFor(comboOccurs);
					labelOccurs.setName("labelOccurs");

					//---- comboOccurs ----
					comboOccurs.setName("comboOccurs");

					//======== panelOccurence ========
					{
						panelOccurence.setName("panelOccurence");
						panelOccurence.setLayout(new CardLayout(2, 2));

						//======== panelDaily ========
						{
							panelDaily.setName("panelDaily");

							//---- labelEvery1 ----
							labelEvery1.setText(bundle.getString("labelEvery1.text"));
							labelEvery1.setLabelFor(spinnerDaily);
							labelEvery1.setName("labelEvery1");

							//---- spinnerDaily ----
							spinnerDaily.setModel(new SpinnerNumberModel(1, 1, 999, 1));
							spinnerDaily.setName("spinnerDaily");

							//---- labelDays ----
							labelDays.setText(bundle.getString("labelDays.text"));
							labelDays.setLabelFor(spinnerDaily);
							labelDays.setName("labelDays");

							PanelBuilder panelDailyBuilder = new PanelBuilder(new FormLayout(
								new ColumnSpec[] {
									FormFactory.DEFAULT_COLSPEC,
									FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
									FormFactory.DEFAULT_COLSPEC,
									FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
									new ColumnSpec(ColumnSpec.FILL, Sizes.PREFERRED, FormSpec.DEFAULT_GROW)
								},
								RowSpec.decodeSpecs("pref")), panelDaily);

							panelDailyBuilder.add(labelEvery1,  cc.xy  (1, 1));
							panelDailyBuilder.add(spinnerDaily, cc.xywh(3, 1, 1, 1, CellConstraints.FILL, CellConstraints.FILL));
							panelDailyBuilder.add(labelDays,    cc.xy  (5, 1));
						}
						panelOccurence.add(panelDaily, "card1");

						//======== panelWeekly ========
						{
							panelWeekly.setName("panelWeekly");

							//---- labelEvery2 ----
							labelEvery2.setText(bundle.getString("labelEvery2.text"));
							labelEvery2.setLabelFor(spinnerWeekly);
							labelEvery2.setName("labelEvery2");

							//---- spinnerWeekly ----
							spinnerWeekly.setModel(new SpinnerNumberModel(1, 1, 999, 1));
							spinnerWeekly.setName("spinnerWeekly");

							//---- labelWeeksOn ----
							labelWeeksOn.setText(bundle.getString("labelWeeksOn.text"));
							labelWeeksOn.setLabelFor(spinnerWeekly);
							labelWeeksOn.setName("labelWeeksOn");

							//======== panelDaysInWeek ========
							{
								panelDaysInWeek.setName("panelDaysInWeek");

								//---- checkMonday ----
								checkMonday.setText(bundle.getString("checkMonday.text"));
								checkMonday.setName("checkMonday");

								//---- checkFriday ----
								checkFriday.setText(bundle.getString("checkFriday.text"));
								checkFriday.setName("checkFriday");

								//---- checkTuesday ----
								checkTuesday.setText(bundle.getString("checkTuesday.text"));
								checkTuesday.setName("checkTuesday");

								//---- checkSaturday ----
								checkSaturday.setText(bundle.getString("checkSaturday.text"));
								checkSaturday.setName("checkSaturday");

								//---- checkWednesday ----
								checkWednesday.setText(bundle.getString("checkWednesday.text"));
								checkWednesday.setName("checkWednesday");

								//---- checkSunday ----
								checkSunday.setText(bundle.getString("checkSunday.text"));
								checkSunday.setName("checkSunday");

								//---- checkThursday ----
								checkThursday.setText(bundle.getString("checkThursday.text"));
								checkThursday.setName("checkThursday");

								PanelBuilder panelDaysInWeekBuilder = new PanelBuilder(new FormLayout(
									new ColumnSpec[] {
										new ColumnSpec(ColumnSpec.FILL, Sizes.PREFERRED, FormSpec.DEFAULT_GROW),
										FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
										new ColumnSpec(ColumnSpec.FILL, Sizes.PREFERRED, FormSpec.DEFAULT_GROW)
									},
									new RowSpec[] {
										FormFactory.DEFAULT_ROWSPEC,
										FormFactory.GLUE_ROWSPEC,
										FormFactory.DEFAULT_ROWSPEC,
										FormFactory.GLUE_ROWSPEC,
										FormFactory.DEFAULT_ROWSPEC,
										FormFactory.GLUE_ROWSPEC,
										FormFactory.DEFAULT_ROWSPEC
									}), panelDaysInWeek);

								panelDaysInWeekBuilder.add(checkMonday,    cc.xywh(1, 1, 2, 1));
								panelDaysInWeekBuilder.add(checkFriday,    cc.xy  (3, 1));
								panelDaysInWeekBuilder.add(checkTuesday,   cc.xywh(1, 3, 2, 1));
								panelDaysInWeekBuilder.add(checkSaturday,  cc.xy  (3, 3));
								panelDaysInWeekBuilder.add(checkWednesday, cc.xywh(1, 5, 2, 1, CellConstraints.FILL, CellConstraints.DEFAULT));
								panelDaysInWeekBuilder.add(checkSunday,    cc.xy  (3, 5));
								panelDaysInWeekBuilder.add(checkThursday,  cc.xy  (1, 7));
							}

							PanelBuilder panelWeeklyBuilder = new PanelBuilder(new FormLayout(
								new ColumnSpec[] {
									FormFactory.DEFAULT_COLSPEC,
									FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
									FormFactory.PREF_COLSPEC,
									FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
									new ColumnSpec(ColumnSpec.FILL, Sizes.PREFERRED, FormSpec.DEFAULT_GROW)
								},
								new RowSpec[] {
									FormFactory.DEFAULT_ROWSPEC,
									FormFactory.LINE_GAP_ROWSPEC,
									FormFactory.PREF_ROWSPEC
								}), panelWeekly);

							panelWeeklyBuilder.add(labelEvery2,     cc.xy  (1, 1));
							panelWeeklyBuilder.add(spinnerWeekly,   cc.xywh(3, 1, 1, 1, CellConstraints.FILL, CellConstraints.FILL));
							panelWeeklyBuilder.add(labelWeeksOn,    cc.xy  (5, 1));
							panelWeeklyBuilder.add(panelDaysInWeek, cc.xywh(1, 3, 5, 1));
						}
						panelOccurence.add(panelWeekly, "card2");

						//======== panelMonthly ========
						{
							panelMonthly.setName("panelMonthly");

							//---- labelEvery3 ----
							labelEvery3.setText(bundle.getString("labelEvery3.text"));
							labelEvery3.setLabelFor(spinnerMonthly);
							labelEvery3.setName("labelEvery3");

							//---- spinnerMonthly ----
							spinnerMonthly.setModel(new SpinnerNumberModel(1, 1, 999, 1));
							spinnerMonthly.setName("spinnerMonthly");

							//---- labelMonthsOnthe ----
							labelMonthsOnthe.setText(bundle.getString("labelMonthsOnthe.text"));
							labelMonthsOnthe.setLabelFor(spinnerMonthly);
							labelMonthsOnthe.setName("labelMonthsOnthe");

							//---- radio30thDay ----
							radio30thDay.setText(bundle.getString("radio30thDay.text"));
							radio30thDay.setName("radio30thDay");

							//---- radioFifthMonday ----
							radioFifthMonday.setText(bundle.getString("radioFifthMonday.text"));
							radioFifthMonday.setName("radioFifthMonday");

							//---- radioLastMonday ----
							radioLastMonday.setText(bundle.getString("radioLastMonday.text"));
							radioLastMonday.setName("radioLastMonday");

							PanelBuilder panelMonthlyBuilder = new PanelBuilder(new FormLayout(
								new ColumnSpec[] {
									FormFactory.PREF_COLSPEC,
									FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
									FormFactory.PREF_COLSPEC,
									FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
									new ColumnSpec(Sizes.dluX(59))
								},
								new RowSpec[] {
									FormFactory.PREF_ROWSPEC,
									FormFactory.LINE_GAP_ROWSPEC,
									FormFactory.DEFAULT_ROWSPEC,
									FormFactory.NARROW_LINE_GAP_ROWSPEC,
									FormFactory.DEFAULT_ROWSPEC,
									FormFactory.NARROW_LINE_GAP_ROWSPEC,
									FormFactory.DEFAULT_ROWSPEC
								}), panelMonthly);

							panelMonthlyBuilder.add(labelEvery3,      cc.xy  (1, 1));
							panelMonthlyBuilder.add(spinnerMonthly,   cc.xywh(3, 1, 1, 1, CellConstraints.FILL, CellConstraints.FILL));
							panelMonthlyBuilder.add(labelMonthsOnthe, cc.xy  (5, 1));
							panelMonthlyBuilder.add(radio30thDay,     cc.xywh(1, 3, 5, 1));
							panelMonthlyBuilder.add(radioFifthMonday, cc.xywh(1, 5, 5, 1));
							panelMonthlyBuilder.add(radioLastMonday,  cc.xywh(1, 7, 5, 1));
						}
						panelOccurence.add(panelMonthly, "card3");

						//======== panelAnnually ========
						{
							panelAnnually.setName("panelAnnually");

							//---- labelEvery4 ----
							labelEvery4.setText(bundle.getString("labelEvery4.text"));
							labelEvery4.setLabelFor(spinnerAnnually);
							labelEvery4.setName("labelEvery4");

							//---- spinnerAnnually ----
							spinnerAnnually.setModel(new SpinnerNumberModel(1, 1, 999, 1));
							spinnerAnnually.setName("spinnerAnnually");

							//---- labelYears ----
							labelYears.setText(bundle.getString("labelYears.text"));
							labelYears.setLabelFor(spinnerAnnually);
							labelYears.setName("labelYears");

							PanelBuilder panelAnnuallyBuilder = new PanelBuilder(new FormLayout(
								new ColumnSpec[] {
									FormFactory.DEFAULT_COLSPEC,
									FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
									FormFactory.PREF_COLSPEC,
									FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
									new ColumnSpec(ColumnSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW)
								},
								RowSpec.decodeSpecs("pref")), panelAnnually);

							panelAnnuallyBuilder.add(labelEvery4,     cc.xy  (1, 1));
							panelAnnuallyBuilder.add(spinnerAnnually, cc.xywh(3, 1, 1, 1, CellConstraints.FILL, CellConstraints.FILL));
							panelAnnuallyBuilder.add(labelYears,      cc.xy  (5, 1));
						}
						panelOccurence.add(panelAnnually, "card4");
					}

					//======== panelRepeat ========
					{
						panelRepeat.setName("panelRepeat");

						//---- radioRepeatForever ----
						radioRepeatForever.setText(bundle.getString("radioRepeatForever.text"));
						radioRepeatForever.setName("radioRepeatForever");

						//---- radioRepeatFor ----
						radioRepeatFor.setText(bundle.getString("radioRepeatFor.text"));
						radioRepeatFor.setName("radioRepeatFor");

						//---- spinnerRepeatFor ----
						spinnerRepeatFor.setModel(new SpinnerNumberModel(1, 1, 999, 1));
						spinnerRepeatFor.setName("spinnerRepeatFor");

						//---- labelOccurences ----
						labelOccurences.setText(bundle.getString("labelOccurences.text"));
						labelOccurences.setLabelFor(spinnerRepeatFor);
						labelOccurences.setName("labelOccurences");

						//---- repeatUntil ----
						repeatUntil.setText(bundle.getString("repeatUntil.text"));
						repeatUntil.setName("repeatUntil");

						//---- dateUntil ----
						dateUntil.setName("dateUntil");

						PanelBuilder panelRepeatBuilder = new PanelBuilder(new FormLayout(
							new ColumnSpec[] {
								FormFactory.PREF_COLSPEC,
								FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
								FormFactory.PREF_COLSPEC,
								FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
								new ColumnSpec(ColumnSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW)
							},
							new RowSpec[] {
								FormFactory.DEFAULT_ROWSPEC,
								FormFactory.NARROW_LINE_GAP_ROWSPEC,
								FormFactory.DEFAULT_ROWSPEC,
								FormFactory.NARROW_LINE_GAP_ROWSPEC,
								FormFactory.DEFAULT_ROWSPEC
							}), panelRepeat);

						panelRepeatBuilder.add(radioRepeatForever, cc.xy  (1, 1));
						panelRepeatBuilder.add(radioRepeatFor,     cc.xy  (1, 3));
						panelRepeatBuilder.add(spinnerRepeatFor,   cc.xywh(3, 3, 1, 1, CellConstraints.FILL, CellConstraints.FILL));
						panelRepeatBuilder.add(labelOccurences,    cc.xy  (5, 3));
						panelRepeatBuilder.add(repeatUntil,        cc.xy  (1, 5));
						panelRepeatBuilder.add(dateUntil,          cc.xywh(3, 5, 3, 1));
					}

					PanelBuilder contentPanelBuilder = new PanelBuilder(new FormLayout(
						new ColumnSpec[] {
							FormFactory.PREF_COLSPEC,
							FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
							new ColumnSpec("max(pref;50dlu)"),
							FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
							new ColumnSpec(ColumnSpec.FILL, Sizes.PREFERRED, FormSpec.DEFAULT_GROW)
						},
						new RowSpec[] {
							FormFactory.DEFAULT_ROWSPEC,
							FormFactory.LINE_GAP_ROWSPEC,
							new RowSpec(RowSpec.CENTER, Sizes.PREFERRED, FormSpec.DEFAULT_GROW),
							FormFactory.RELATED_GAP_ROWSPEC,
							FormFactory.DEFAULT_ROWSPEC
						}), contentPanel);

					contentPanelBuilder.add(labelOccurs,    cc.xy  (1, 1));
					contentPanelBuilder.add(comboOccurs,    cc.xywh(3, 1, 1, 1, CellConstraints.FILL, CellConstraints.DEFAULT));
					contentPanelBuilder.add(panelOccurence, cc.xywh(3, 3, 3, 2));
					contentPanelBuilder.add(panelRepeat,    cc.xywh(1, 5, 5, 1));
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
							FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
							new ColumnSpec(ColumnSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW),
							FormFactory.GLUE_COLSPEC,
							FormFactory.PREF_COLSPEC,
							FormFactory.RELATED_GAP_COLSPEC,
							new ColumnSpec("max(pref;42dlu)")
						},
						RowSpec.decodeSpecs("pref")), buttonBar);
					((FormLayout)buttonBar.getLayout()).setColumnGroups(new int[][] {{4, 6}});

					buttonBarBuilder.add(btnOK,     cc.xy(4, 1));
					buttonBarBuilder.add(btnCancel, cc.xy(6, 1));
				}
				dialogPane.add(buttonBar, BorderLayout.SOUTH);
			}
			recurrenceDialogContentPane.add(dialogPane, BorderLayout.CENTER);
			recurrenceDialog.pack();
			recurrenceDialog.setLocationRelativeTo(recurrenceDialog.getOwner());
		}

		//---- buttonGroupMonthly ----
		ButtonGroup buttonGroupMonthly = new ButtonGroup();
		buttonGroupMonthly.add(radio30thDay);
		buttonGroupMonthly.add(radioFifthMonday);
		buttonGroupMonthly.add(radioLastMonday);

		//---- buttonGroupRepeat ----
		ButtonGroup buttonGroupRepeat = new ButtonGroup();
		buttonGroupRepeat.add(radioRepeatForever);
		buttonGroupRepeat.add(radioRepeatFor);
		buttonGroupRepeat.add(repeatUntil);
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	// Generated using JFormDesigner Open Source Project license - unknown
	private JDialog recurrenceDialog;
	private JPanel dialogPane;
	private JLabel labelOccurs;
	private JComboBox comboOccurs;
	private JPanel panelOccurence;
	private JSpinner spinnerDaily;
	private JSpinner spinnerWeekly;
	private JLabel labelWeeksOn;
	private JCheckBox checkMonday;
	private JCheckBox checkFriday;
	private JCheckBox checkTuesday;
	private JCheckBox checkSaturday;
	private JCheckBox checkWednesday;
	private JCheckBox checkSunday;
	private JCheckBox checkThursday;
	private JSpinner spinnerMonthly;
	private JLabel labelMonthsOnthe;
	private JRadioButton radio30thDay;
	private JRadioButton radioFifthMonday;
	private JRadioButton radioLastMonday;
	private JSpinner spinnerAnnually;
	private JRadioButton radioRepeatForever;
	private JRadioButton radioRepeatFor;
	private JSpinner spinnerRepeatFor;
	private JRadioButton repeatUntil;
	private JXDatePicker dateUntil;
	private JButton btnOK;
	private JButton btnCancel;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
