import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import com.jgoodies.forms.builder.*;
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

				//======== toolbar ========
				{
					toolbar.setBorder(new EmptyBorder(5, 5, 0, 5));
					toolbar.setOpaque(false);

					//---- btnConnectionAdd ----
					btnConnectionAdd.setPreferredSize(new Dimension(26, 23));
					btnConnectionAdd.setName(bundle.getString("btnConnectionAdd.name"));

					//---- btnConnectionRemove ----
					btnConnectionRemove.setPreferredSize(new Dimension(26, 23));
					btnConnectionRemove.setName(bundle.getString("btnConnectionRemove.name"));

					//---- btnConnectionCopy ----
					btnConnectionCopy.setPreferredSize(new Dimension(26, 23));
					btnConnectionCopy.setName(bundle.getString("btnConnectionCopy.name"));

					PanelBuilder toolbarBuilder = new PanelBuilder(new FormLayout(
						new ColumnSpec[] {
							FormFactory.PREF_COLSPEC,
							FormFactory.PREF_COLSPEC,
							FormFactory.PREF_COLSPEC,
							FormFactory.PREF_COLSPEC,
							FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
							new ColumnSpec(ColumnSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW)
						},
						RowSpec.decodeSpecs("pref:grow")), toolbar);
					((FormLayout)toolbar.getLayout()).setColumnGroups(new int[][] {{1, 2, 4}});

					toolbarBuilder.add(btnConnectionAdd,    cc.xy(1, 1));
					toolbarBuilder.add(btnConnectionRemove, cc.xy(2, 1));
					toolbarBuilder.add(separator1,          cc.xy(3, 1));
					toolbarBuilder.add(btnConnectionCopy,   cc.xy(4, 1));
				}

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

						//======== scrollPane1 ========
						{

							//---- listConnections ----
							listConnections.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
							listConnections.setBorder(new EmptyBorder(2, 2, 2, 2));
							scrollPane1.setViewportView(listConnections);
						}

						PanelBuilder splitLeftPanelBuilder = new PanelBuilder(new FormLayout(
							new ColumnSpec[] {
								new ColumnSpec(ColumnSpec.FILL, Sizes.bounded(Sizes.PREFERRED, Sizes.dluX(100), Sizes.dluX(130)), FormSpec.DEFAULT_GROW)
							},
							RowSpec.decodeSpecs("fill:default:grow")), splitLeftPanel);

						splitLeftPanelBuilder.add(scrollPane1, cc.xy(1, 1));
					}
					splitPane.setLeftComponent(splitLeftPanel);

					//======== panelParameters ========
					{
						panelParameters.setBorder(new CompoundBorder(
							new EmptyBorder(4, 4, 4, 4),
							new TitledBorder(null, "Connection parameters", TitledBorder.LEFT, TitledBorder.DEFAULT_POSITION)));
						panelParameters.setPreferredSize(new Dimension(400, 260));
						panelParameters.setVisible(false);

						//---- labelName ----
						labelName.setText(bundle.getString("labelName.text"));
						labelName.setLabelFor(nameField);

						//---- nameField ----
						nameField.setPreferredSize(new Dimension(150, 20));

						//---- labelDescription ----
						labelDescription.setText(bundle.getString("labelDescription.text"));
						labelDescription.setLabelFor(descriptionField);

						//---- labelLibrary ----
						labelLibrary.setText(bundle.getString("labelLibrary.text"));
						labelLibrary.setLabelFor(libraryField);

						//---- btnSelectLibrary ----
						btnSelectLibrary.setText(bundle.getString("btnSelectLibrary.text"));
						btnSelectLibrary.setPreferredSize(new Dimension(26, 23));

						//---- labelDriver ----
						labelDriver.setText(bundle.getString("labelDriver.text"));
						labelDriver.setLabelFor(driverCombo);

						//---- labelURL ----
						labelURL.setText(bundle.getString("labelURL.text"));
						labelURL.setLabelFor(urlField);

						//---- labelUser ----
						labelUser.setText(bundle.getString("labelUser.text"));
						labelUser.setLabelFor(userField);

						//---- labelPassword ----
						labelPassword.setText(bundle.getString("labelPassword.text"));
						labelPassword.setLabelFor(passwordField);

						//---- labelWarning ----
						labelWarning.setText(bundle.getString("labelWarning.text"));
						labelWarning.setForeground(Color.red);

						//======== btnPanel2 ========
						{

							//---- btnTest ----
							btnTest.setText(bundle.getString("btnTest.text"));

							//---- btnInfo ----
							btnInfo.setText(bundle.getString("btnInfo.text"));

							PanelBuilder btnPanel2Builder = new PanelBuilder(new FormLayout(
								new ColumnSpec[] {
									new ColumnSpec(ColumnSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW),
									FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
									FormFactory.DEFAULT_COLSPEC,
									FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
									FormFactory.DEFAULT_COLSPEC
								},
								RowSpec.decodeSpecs("default")), btnPanel2);

							btnPanel2Builder.add(btnTest, cc.xy(3, 1));
							btnPanel2Builder.add(btnInfo, cc.xy(5, 1));
						}

						PanelBuilder panelParametersBuilder = new PanelBuilder(new FormLayout(
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
							}), panelParameters);
						((FormLayout)panelParameters.getLayout()).setRowGroups(new int[][] {{1, 11, 13}, {3, 7, 9}});

						panelParametersBuilder.add(labelName,        cc.xy  (3,  1));
						panelParametersBuilder.add(nameField,        cc.xy  (5,  1));
						panelParametersBuilder.add(labelDescription, cc.xy  (3,  3));
						panelParametersBuilder.add(descriptionField, cc.xywh(5,  3, 5, 1));
						panelParametersBuilder.add(labelLibrary,     cc.xy  (3,  5));
						panelParametersBuilder.add(libraryField,     cc.xywh(5,  5, 3, 1));
						panelParametersBuilder.add(btnSelectLibrary, cc.xy  (9,  5));
						panelParametersBuilder.add(labelDriver,      cc.xy  (3,  7));
						panelParametersBuilder.add(driverCombo,      cc.xywh(5,  7, 5, 1));
						panelParametersBuilder.add(labelURL,         cc.xy  (3,  9));
						panelParametersBuilder.add(urlField,         cc.xywh(5,  9, 5, 1));
						panelParametersBuilder.add(labelUser,        cc.xy  (3, 11));
						panelParametersBuilder.add(userField,        cc.xy  (5, 11));
						panelParametersBuilder.add(labelPassword,    cc.xy  (3, 13));
						panelParametersBuilder.add(passwordField,    cc.xy  (5, 13));
						panelParametersBuilder.add(labelWarning,     cc.xywh(5, 15, 3, 1));
						panelParametersBuilder.add(btnPanel2,        cc.xywh(3, 17, 7, 1));
					}
					splitPane.setRightComponent(panelParameters);
				}

				PanelBuilder contentPanelBuilder = new PanelBuilder(new FormLayout(
					"default:grow",
					"default, fill:default:grow"), contentPanel);

				contentPanelBuilder.add(toolbar,   cc.xy(1, 1));
				contentPanelBuilder.add(splitPane, cc.xy(1, 2));
			}
			dialogPane.add(contentPanel, BorderLayout.CENTER);

			//======== buttonBar ========
			{
				buttonBar.setBorder(Borders.BUTTON_BAR_GAP_BORDER);
				buttonBar.setCyclic(true);

				//---- btnHelp ----
				btnHelp.setText(bundle.getString("btnHelp.text"));

				//---- btnOK ----
				btnOK.setText(bundle.getString("btnOK.text"));

				//---- btnCancel ----
				btnCancel.setText(bundle.getString("btnCancel.text"));

				PanelBuilder buttonBarBuilder = new PanelBuilder(new FormLayout(
					new ColumnSpec[] {
						FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
						FormFactory.BUTTON_COLSPEC,
						FormFactory.GLUE_COLSPEC,
						FormFactory.BUTTON_COLSPEC,
						FormFactory.RELATED_GAP_COLSPEC,
						FormFactory.BUTTON_COLSPEC
					},
					RowSpec.decodeSpecs("pref")), buttonBar);
				((FormLayout)buttonBar.getLayout()).setColumnGroups(new int[][] {{2, 4, 6}});

				buttonBarBuilder.add(btnHelp,   cc.xy(2, 1));
				buttonBarBuilder.add(btnOK,     cc.xy(4, 1));
				buttonBarBuilder.add(btnCancel, cc.xy(6, 1));
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
