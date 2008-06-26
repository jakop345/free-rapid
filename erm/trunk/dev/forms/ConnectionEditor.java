import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;
import org.jdesktop.swinghelper.buttonpanel.*;
/*
 * Created by JFormDesigner on Mon Jun 23 10:13:15 CEST 2008
 */



/**
 * @author SHOCKIE
 */
public class ConnectionEditor extends JDialog {
	public ConnectionEditor(Frame owner) {
		super(owner);
		initComponents();
	}

	public ConnectionEditor(Dialog owner) {
		super(owner);
		initComponents();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		// Generated using JFormDesigner Open Source Project license - unknown
		ResourceBundle bundle = ResourceBundle.getBundle("ConnectionEditor");
		JPanel dialogPane = new JPanel();
		JPanel contentPanel = new JPanel();
		JPanel toolbar = new JPanel();
		btnConnectionAdd = new JButton();
		btnConnectionRemove = new JButton();
		separator1 = new JSeparator();
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
			dialogPane.setName(bundle.getString("dialogPane.name"));
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
						new ColumnSpec[] {
							FormFactory.PREF_COLSPEC,
							FormFactory.PREF_COLSPEC,
							FormFactory.PREF_COLSPEC,
							FormFactory.PREF_COLSPEC,
							FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
							new ColumnSpec(ColumnSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW)
						},
						RowSpec.decodeSpecs("pref:grow")));
					((FormLayout)toolbar.getLayout()).setColumnGroups(new int[][] {{1, 2, 4}});

					//---- btnConnectionAdd ----
					btnConnectionAdd.setPreferredSize(new Dimension(26, 23));
					toolbar.add(btnConnectionAdd, cc.xy(1, 1));

					//---- btnConnectionRemove ----
					btnConnectionRemove.setPreferredSize(new Dimension(26, 23));
					toolbar.add(btnConnectionRemove, cc.xy(2, 1));
					toolbar.add(separator1, cc.xy(3, 1));

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
							new ColumnSpec[] {
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
						panelParameters.setBorder(new CompoundBorder(
							new EmptyBorder(4, 4, 4, 4),
							new TitledBorder(null, "Connection parameters", TitledBorder.LEFT, TitledBorder.DEFAULT_POSITION)));
						panelParameters.setPreferredSize(new Dimension(400, 260));
						panelParameters.setVisible(false);
						panelParameters.setLayout(new FormLayout(
							new ColumnSpec[] {
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
							new RowSpec[] {
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
						((FormLayout)panelParameters.getLayout()).setRowGroups(new int[][] {{1, 11, 13}, {3, 7, 9}});

						//---- labelName ----
						labelName.setText(bundle.getString("labelName.text"));
						labelName.setLabelFor(nameField);
						panelParameters.add(labelName, cc.xy(3, 1));

						//---- nameField ----
						nameField.setPreferredSize(new Dimension(150, 20));
						panelParameters.add(nameField, cc.xy(5, 1));

						//---- labelDescription ----
						labelDescription.setText(bundle.getString("labelDescription.text"));
						labelDescription.setLabelFor(descriptionField);
						panelParameters.add(labelDescription, cc.xy(3, 3));
						panelParameters.add(descriptionField, cc.xywh(5, 3, 5, 1));

						//---- labelLibrary ----
						labelLibrary.setText(bundle.getString("labelLibrary.text"));
						labelLibrary.setLabelFor(libraryField);
						panelParameters.add(labelLibrary, cc.xy(3, 5));
						panelParameters.add(libraryField, cc.xywh(5, 5, 3, 1));

						//---- btnSelectLibrary ----
						btnSelectLibrary.setText(bundle.getString("btnSelectLibrary.text"));
						btnSelectLibrary.setPreferredSize(new Dimension(26, 23));
						panelParameters.add(btnSelectLibrary, cc.xy(9, 5));

						//---- labelDriver ----
						labelDriver.setText(bundle.getString("labelDriver.text"));
						labelDriver.setLabelFor(driverCombo);
						panelParameters.add(labelDriver, cc.xy(3, 7));
						panelParameters.add(driverCombo, cc.xywh(5, 7, 5, 1));

						//---- labelURL ----
						labelURL.setText(bundle.getString("labelURL.text"));
						labelURL.setLabelFor(urlField);
						panelParameters.add(labelURL, cc.xy(3, 9));
						panelParameters.add(urlField, cc.xywh(5, 9, 5, 1));

						//---- labelUser ----
						labelUser.setText(bundle.getString("labelUser.text"));
						labelUser.setLabelFor(userField);
						panelParameters.add(labelUser, cc.xy(3, 11));
						panelParameters.add(userField, cc.xy(5, 11));

						//---- labelPassword ----
						labelPassword.setText(bundle.getString("labelPassword.text"));
						labelPassword.setLabelFor(passwordField);
						panelParameters.add(labelPassword, cc.xy(3, 13));
						panelParameters.add(passwordField, cc.xy(5, 13));

						//---- labelWarning ----
						labelWarning.setText(bundle.getString("labelWarning.text"));
						labelWarning.setForeground(Color.red);
						panelParameters.add(labelWarning, cc.xywh(5, 15, 3, 1));

						//======== btnPanel2 ========
						{
							btnPanel2.setLayout(new FormLayout(
								new ColumnSpec[] {
									new ColumnSpec(ColumnSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW),
									FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
									FormFactory.DEFAULT_COLSPEC,
									FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
									FormFactory.DEFAULT_COLSPEC
								},
								RowSpec.decodeSpecs("default")));

							//---- btnTest ----
							btnTest.setText(bundle.getString("btnTest.text"));
							btnPanel2.add(btnTest, cc.xy(3, 1));

							//---- btnInfo ----
							btnInfo.setText(bundle.getString("btnInfo.text"));
							btnPanel2.add(btnInfo, cc.xy(5, 1));
						}
						panelParameters.add(btnPanel2, cc.xywh(3, 17, 7, 1));
					}
					splitPane.setRightComponent(panelParameters);
				}
				contentPanel.add(splitPane, cc.xy(1, 2));
			}
			dialogPane.add(contentPanel, BorderLayout.CENTER);

			//======== buttonBar ========
			{
				buttonBar.setBorder(Borders.BUTTON_BAR_GAP_BORDER);
				buttonBar.setCyclic(true);
				buttonBar.setLayout(new FormLayout(
					new ColumnSpec[] {
						FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
						FormFactory.BUTTON_COLSPEC,
						FormFactory.GLUE_COLSPEC,
						FormFactory.BUTTON_COLSPEC,
						FormFactory.RELATED_GAP_COLSPEC,
						FormFactory.BUTTON_COLSPEC
					},
					RowSpec.decodeSpecs("pref")));
				((FormLayout)buttonBar.getLayout()).setColumnGroups(new int[][] {{2, 4, 6}});

				//---- btnHelp ----
				btnHelp.setText(bundle.getString("btnHelp.text"));
				buttonBar.add(btnHelp, cc.xy(2, 1));

				//---- btnOK ----
				btnOK.setText(bundle.getString("btnOK.text"));
				buttonBar.add(btnOK, cc.xy(4, 1));

				//---- btnCancel ----
				btnCancel.setText(bundle.getString("btnCancel.text"));
				buttonBar.add(btnCancel, cc.xy(6, 1));
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
	private JButton btnConnectionAdd;
	private JButton btnConnectionRemove;
	private JSeparator separator1;
	private JButton btnConnectionCopy;
	private JList listConnections;
	private JPanel panelParameters;
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
	private JButton btnHelp;
	private JButton btnOK;
	private JButton btnCancel;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
