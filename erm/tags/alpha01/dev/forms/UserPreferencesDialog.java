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
 * @author Ladislav Vitasek
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
		ResourceBundle bundle = ResourceBundle.getBundle("UserPreferencesDialog");
		JPanel dialogPane = new JPanel();
		JPanel contentPanel = new JPanel();
		toolbar = new JButtonBar();
		panelCard = new JPanel();
		JPanel panelGeneral = new JPanel();
		JPanel panelGeneralSettings = new JPanel();
		checkShowIconInSystemTray = new JCheckBox();
		JPanel panelAppearance = new JPanel();
		JLabel labelLaF = new JLabel();
		comboLaF = new JComboBox();
		JLabel labelRequiresRestart = new JLabel();
		JPanel panelAlarm = new JPanel();
		JPanel panelWhenAlarmGoesOff = new JPanel();
		JPanel panelAlarmDefaults = new JPanel();
		JPanel panelViews = new JPanel();
		buttonBar = new JPanel();
		btnOK = new JButton();
		btnCancel = new JButton();
		CellConstraints cc = new CellConstraints();

		//======== this ========
		setTitle(bundle.getString("this.title"));
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

		//======== dialogPane ========
		{
			dialogPane.setLayout(new BorderLayout());

			//======== contentPanel ========
			{
				contentPanel.setLayout(new BorderLayout());

				//======== toolbar ========
				{
					toolbar.setBorder(LineBorder.createBlackLineBorder());
					toolbar.setLayout(null);
				}
				contentPanel.add(toolbar, BorderLayout.NORTH);

				//======== panelCard ========
				{
					panelCard.setLayout(new CardLayout());

					//======== panelGeneral ========
					{
						panelGeneral.setBorder(Borders.TABBED_DIALOG_BORDER);
						panelGeneral.setLayout(new FormLayout(
							ColumnSpec.decodeSpecs("default:grow"),
							new RowSpec[] {
								new RowSpec(RowSpec.FILL, Sizes.DEFAULT, FormSpec.NO_GROW),
								FormFactory.RELATED_GAP_ROWSPEC,
								new RowSpec(RowSpec.FILL, Sizes.DEFAULT, FormSpec.NO_GROW)
							}));

						//======== panelGeneralSettings ========
						{
							panelGeneralSettings.setBorder(new TitledBorder(null, bundle.getString("panelGeneralSettings.border"), TitledBorder.LEADING, TitledBorder.TOP));
							panelGeneralSettings.setLayout(new FormLayout(
								new ColumnSpec[] {
									new ColumnSpec(ColumnSpec.LEFT, Sizes.dluX(0), FormSpec.NO_GROW),
									FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
									FormFactory.DEFAULT_COLSPEC
								},
								RowSpec.decodeSpecs("default")));

							//---- checkShowIconInSystemTray ----
							checkShowIconInSystemTray.setText(bundle.getString("checkShowIconInSystemTray.text"));
							panelGeneralSettings.add(checkShowIconInSystemTray, cc.xy(3, 1));
						}
						panelGeneral.add(panelGeneralSettings, cc.xy(1, 1));

						//======== panelAppearance ========
						{
							panelAppearance.setBorder(new CompoundBorder(
								new TitledBorder(null, bundle.getString("panelAppearance.border"), TitledBorder.LEADING, TitledBorder.TOP),
								Borders.DLU2_BORDER));
							panelAppearance.setLayout(new FormLayout(
								new ColumnSpec[] {
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
							labelLaF.setText(bundle.getString("labelLaF.text"));
							labelLaF.setLabelFor(comboLaF);
							panelAppearance.add(labelLaF, cc.xy(3, 1));
							panelAppearance.add(comboLaF, cc.xy(5, 1));

							//---- labelRequiresRestart ----
							labelRequiresRestart.setText(bundle.getString("labelRequiresRestart.text"));
							panelAppearance.add(labelRequiresRestart, cc.xy(7, 1));
						}
						panelGeneral.add(panelAppearance, cc.xy(1, 3));
					}
					panelCard.add(panelGeneral, "CARD1");

					//======== panelAlarm ========
					{
						panelAlarm.setBorder(Borders.TABBED_DIALOG_BORDER);
						panelAlarm.setLayout(new FormLayout(
							ColumnSpec.decodeSpecs("default:grow"),
							new RowSpec[] {
								FormFactory.DEFAULT_ROWSPEC,
								FormFactory.RELATED_GAP_ROWSPEC,
								FormFactory.DEFAULT_ROWSPEC
							}));

						//======== panelWhenAlarmGoesOff ========
						{
							panelWhenAlarmGoesOff.setBorder(new TitledBorder(null, bundle.getString("panelWhenAlarmGoesOff.border"), TitledBorder.LEADING, TitledBorder.TOP));
							panelWhenAlarmGoesOff.setLayout(new FormLayout(
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
								}));
							((FormLayout)panelWhenAlarmGoesOff.getLayout()).setColumnGroups(new int[][] {{9, 11}});
						}
						panelAlarm.add(panelWhenAlarmGoesOff, cc.xy(1, 1));

						//======== panelAlarmDefaults ========
						{
							panelAlarmDefaults.setBorder(new TitledBorder(null, bundle.getString("panelAlarmDefaults.border"), TitledBorder.LEADING, TitledBorder.TOP));
							panelAlarmDefaults.setLayout(new FormLayout(
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
								}));
						}
						panelAlarm.add(panelAlarmDefaults, cc.xy(1, 3));
					}
					panelCard.add(panelAlarm, "CARD2");

					//======== panelViews ========
					{
						panelViews.setBorder(Borders.TABBED_DIALOG_BORDER);
						panelViews.setLayout(new FormLayout(
							ColumnSpec.decodeSpecs("default:grow"),
							new RowSpec[] {
								FormFactory.DEFAULT_ROWSPEC,
								FormFactory.RELATED_GAP_ROWSPEC,
								FormFactory.DEFAULT_ROWSPEC
							}));
					}
					panelCard.add(panelViews, "CARD4");
				}
				contentPanel.add(panelCard, BorderLayout.CENTER);
			}
			dialogPane.add(contentPanel, BorderLayout.CENTER);

			//======== buttonBar ========
			{
				buttonBar.setBorder(Borders.BUTTON_BAR_GAP_BORDER);
				buttonBar.setLayout(new FormLayout(
					new ColumnSpec[] {
						FormFactory.GLUE_COLSPEC,
						new ColumnSpec("max(pref;42dlu)"),
						FormFactory.RELATED_GAP_COLSPEC,
						FormFactory.PREF_COLSPEC
					},
					RowSpec.decodeSpecs("pref")));
				((FormLayout)buttonBar.getLayout()).setColumnGroups(new int[][] {{2, 4}});

				//---- btnOK ----
				btnOK.setText(bundle.getString("btnOK.text"));
				buttonBar.add(btnOK, cc.xy(2, 1));

				//---- btnCancel ----
				btnCancel.setText(bundle.getString("btnCancel.text"));
				buttonBar.add(btnCancel, cc.xy(4, 1));
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
	private JComboBox comboLaF;
	private JPanel buttonBar;
	private JButton btnOK;
	private JButton btnCancel;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
