import java.awt.*;
import java.util.*;
import javax.swing.*;
import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;
import org.jdesktop.swinghelper.buttonpanel.*;


public class SelectConnectionDialog extends JDialog {
	public SelectConnectionDialog(Frame owner) {
		super(owner);
		initComponents();
	}

	public SelectConnectionDialog(Dialog owner) {
		super(owner);
		initComponents();
	}

	private void initComponents() {
	}

	

	private void initComponents() {
	}
	// Generated using JFormDesigner Open Source Project license - unknown
	ResourceBundle bundle = ResourceBundle.getBundle("ChooseConnectionDialog");
	dialogPane = new JPanel();
	contentPanel = new JPanel();
	label1 = new JLabel();
	comboBox1 = new JComboBox();
	button1 = new JButton();
	button2 = new JButton();
	labelDescription = new JLabel();
	buttonBar = new JXButtonPanel();
	button3 = new JButton();
	okButton = new JButton();
	cancelButton = new JButton();
	CellConstraints cc = new CellConstraints();

	//======== this ========
	Container contentPane = getContentPane();
	contentPane.setLayout(new BorderLayout());

	//======== dialogPane ========
	{
		dialogPane.setBorder(Borders.DIALOG_BORDER);
		dialogPane.setLayout(new BorderLayout());

		//======== contentPanel ========
		{
			contentPanel.setLayout(new FormLayout(
				new ColumnSpec[] {
					FormFactory.DEFAULT_COLSPEC,
					FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
					new ColumnSpec(ColumnSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW),
					FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
					FormFactory.DEFAULT_COLSPEC,
					FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
					FormFactory.DEFAULT_COLSPEC
				},
				new RowSpec[] {
					FormFactory.DEFAULT_ROWSPEC,
					FormFactory.LINE_GAP_ROWSPEC,
					new RowSpec("max(min;20dlu)")
				}));

			//---- label1 ----
			label1.setText(bundle.getString("label1.text"));
			contentPanel.add(label1, cc.xy(1, 1));
			contentPanel.add(comboBox1, cc.xy(3, 1));

			//---- button1 ----
			button1.setText(bundle.getString("button1.text"));
			contentPanel.add(button1, cc.xy(5, 1));

			//---- button2 ----
			button2.setText(bundle.getString("button2.text"));
			contentPanel.add(button2, cc.xy(7, 1));

			//---- labelDescription ----
			labelDescription.setText(bundle.getString("labelDescription.text"));
			contentPanel.add(labelDescription, cc.xywh(3, 3, 5, 1, CellConstraints.DEFAULT, CellConstraints.TOP));
		}
		dialogPane.add(contentPanel, BorderLayout.CENTER);

		//======== buttonBar ========
		{
			buttonBar.setBorder(Borders.BUTTON_BAR_GAP_BORDER);
			buttonBar.setCyclic(true);
			buttonBar.setLayout(new FormLayout(
				new ColumnSpec[] {
					FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
					FormFactory.DEFAULT_COLSPEC,
					FormFactory.GLUE_COLSPEC,
					FormFactory.BUTTON_COLSPEC,
					FormFactory.RELATED_GAP_COLSPEC,
					FormFactory.BUTTON_COLSPEC
				},
				RowSpec.decodeSpecs("pref")));
			((FormLayout)buttonBar.getLayout()).setColumnGroups(new int[][] {{2, 4, 6}});

			//---- button3 ----
			button3.setText(bundle.getString("button3.text"));
			buttonBar.add(button3, cc.xy(2, 1));

			//---- okButton ----
			okButton.setText(bundle.getString("okButton.text"));
			buttonBar.add(okButton, cc.xy(4, 1));

			//---- cancelButton ----
			cancelButton.setText(bundle.getString("cancelButton.text"));
			buttonBar.add(cancelButton, cc.xy(6, 1));
		}
		dialogPane.add(buttonBar, BorderLayout.SOUTH);
	}
	contentPane.add(dialogPane, BorderLayout.CENTER);
	pack();
	setLocationRelativeTo(getOwner());

	// Generated using JFormDesigner Open Source Project license - unknown
	private JPanel dialogPane;
	private JPanel contentPanel;
	private JLabel label1;
	private JComboBox comboBox1;
	private JButton button1;
	private JButton button2;
	private JLabel labelDescription;
	private JXButtonPanel buttonBar;
	private JButton button3;
	private JButton okButton;
	private JButton cancelButton;

	private void initComponents() {
	}
	// Generated using JFormDesigner Open Source Project license - unknown
	ResourceBundle bundle = ResourceBundle.getBundle("ChooseConnectionDialog");
	JPanel dialogPane = new JPanel();
	JPanel contentPanel = new JPanel();
	JLabel labelSelectConnection = new JLabel();
	comboConnections = new JComboBox();
	btnNewConnection = new JButton();
	btnEditConnection = new JButton();
	labelDescription = new JLabel();
	JXButtonPanel buttonBar = new JXButtonPanel();
	btnHelp = new JButton();
	okButton = new JButton();
	cancelButton = new JButton();
	CellConstraints cc = new CellConstraints();

	//======== this ========
	Container contentPane = getContentPane();
	contentPane.setLayout(new BorderLayout());

	//======== dialogPane ========
	{
		dialogPane.setBorder(Borders.DIALOG_BORDER);
		dialogPane.setLayout(new BorderLayout());

		//======== contentPanel ========
		{
			contentPanel.setLayout(new FormLayout(
				new ColumnSpec[] {
					FormFactory.DEFAULT_COLSPEC,
					FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
					new ColumnSpec("max(pref;70dlu):grow"),
					FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
					FormFactory.DEFAULT_COLSPEC,
					FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
					FormFactory.DEFAULT_COLSPEC
				},
				new RowSpec[] {
					FormFactory.DEFAULT_ROWSPEC,
					FormFactory.LINE_GAP_ROWSPEC,
					new RowSpec("max(min;15dlu)")
				}));

			//---- labelSelectConnection ----
			labelSelectConnection.setText(bundle.getString("labelSelectConnection.text"));
			labelSelectConnection.setLabelFor(comboConnections);
			contentPanel.add(labelSelectConnection, cc.xy(1, 1));
			contentPanel.add(comboConnections, cc.xy(3, 1));

			//---- btnNewConnection ----
			btnNewConnection.setText(bundle.getString("btnNewConnection.text"));
			contentPanel.add(btnNewConnection, cc.xy(5, 1));

			//---- btnEditConnection ----
			btnEditConnection.setText(bundle.getString("btnEditConnection.text"));
			contentPanel.add(btnEditConnection, cc.xy(7, 1));

			//---- labelDescription ----
			labelDescription.setText(bundle.getString("labelDescription.text"));
			contentPanel.add(labelDescription, cc.xywh(3, 3, 5, 1, CellConstraints.DEFAULT, CellConstraints.TOP));
		}
		dialogPane.add(contentPanel, BorderLayout.CENTER);

		//======== buttonBar ========
		{
			buttonBar.setBorder(Borders.BUTTON_BAR_GAP_BORDER);
			buttonBar.setCyclic(true);
			buttonBar.setLayout(new FormLayout(
				new ColumnSpec[] {
					FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
					FormFactory.DEFAULT_COLSPEC,
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

			//---- okButton ----
			okButton.setText(bundle.getString("okButton.text"));
			buttonBar.add(okButton, cc.xy(4, 1));

			//---- cancelButton ----
			cancelButton.setText(bundle.getString("cancelButton.text"));
			buttonBar.add(cancelButton, cc.xy(6, 1));
		}
		dialogPane.add(buttonBar, BorderLayout.SOUTH);
	}
	contentPane.add(dialogPane, BorderLayout.CENTER);
	pack();
	setLocationRelativeTo(getOwner());

	// Generated using JFormDesigner Open Source Project license - unknown
	private JComboBox comboConnections;
	private JButton btnNewConnection;
	private JButton btnEditConnection;
	private JLabel labelDescription;
	private JButton btnHelp;
	private JButton okButton;
	private JButton cancelButton;

	private void initComponents() {
	}
	// Generated using JFormDesigner Open Source Project license - unknown
	ResourceBundle bundle = ResourceBundle.getBundle("ChooseConnectionDialog");
	JPanel dialogPane = new JPanel();
	JPanel contentPanel = new JPanel();
	JLabel labelSelectConnection = new JLabel();
	comboConnections = new JComboBox();
	btnNewConnection = new JButton();
	btnEditConnection = new JButton();
	labelDescription = new JLabel();
	JXButtonPanel buttonBar = new JXButtonPanel();
	btnHelp = new JButton();
	okButton = new JButton();
	cancelButton = new JButton();
	CellConstraints cc = new CellConstraints();

	//======== this ========
	Container contentPane = getContentPane();
	contentPane.setLayout(new BorderLayout());

	//======== dialogPane ========
	{
		dialogPane.setBorder(Borders.DIALOG_BORDER);
		dialogPane.setLayout(new BorderLayout());

		//======== contentPanel ========
		{
			contentPanel.setLayout(new FormLayout(
				new ColumnSpec[] {
					FormFactory.DEFAULT_COLSPEC,
					FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
					new ColumnSpec("max(pref;70dlu):grow"),
					FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
					FormFactory.DEFAULT_COLSPEC,
					FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
					FormFactory.DEFAULT_COLSPEC
				},
				new RowSpec[] {
					FormFactory.DEFAULT_ROWSPEC,
					FormFactory.LINE_GAP_ROWSPEC,
					new RowSpec("max(min;15dlu)")
				}));
			((FormLayout)contentPanel.getLayout()).setColumnGroups(new int[][] {{5, 7}});

			//---- labelSelectConnection ----
			labelSelectConnection.setText(bundle.getString("labelSelectConnection.text"));
			labelSelectConnection.setLabelFor(comboConnections);
			contentPanel.add(labelSelectConnection, cc.xy(1, 1));
			contentPanel.add(comboConnections, cc.xy(3, 1));

			//---- btnNewConnection ----
			btnNewConnection.setText(bundle.getString("btnNewConnection.text"));
			contentPanel.add(btnNewConnection, cc.xy(5, 1));

			//---- btnEditConnection ----
			btnEditConnection.setText(bundle.getString("btnEditConnection.text"));
			contentPanel.add(btnEditConnection, cc.xy(7, 1));

			//---- labelDescription ----
			labelDescription.setText(bundle.getString("labelDescription.text"));
			contentPanel.add(labelDescription, cc.xywh(3, 3, 5, 1, CellConstraints.DEFAULT, CellConstraints.TOP));
		}
		dialogPane.add(contentPanel, BorderLayout.CENTER);

		//======== buttonBar ========
		{
			buttonBar.setBorder(Borders.BUTTON_BAR_GAP_BORDER);
			buttonBar.setCyclic(true);
			buttonBar.setLayout(new FormLayout(
				new ColumnSpec[] {
					FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
					FormFactory.DEFAULT_COLSPEC,
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

			//---- okButton ----
			okButton.setText(bundle.getString("okButton.text"));
			buttonBar.add(okButton, cc.xy(4, 1));

			//---- cancelButton ----
			cancelButton.setText(bundle.getString("cancelButton.text"));
			buttonBar.add(cancelButton, cc.xy(6, 1));
		}
		dialogPane.add(buttonBar, BorderLayout.SOUTH);
	}
	contentPane.add(dialogPane, BorderLayout.CENTER);
	pack();
	setLocationRelativeTo(getOwner());

	// Generated using JFormDesigner Open Source Project license - unknown
	private JComboBox comboConnections;
	private JButton btnNewConnection;
	private JButton btnEditConnection;
	private JLabel labelDescription;
	private JButton btnHelp;
	private JButton okButton;
	private JButton cancelButton;

	private void initComponents() {
	}
	// Generated using JFormDesigner Open Source Project license - unknown
	ResourceBundle bundle = ResourceBundle.getBundle("ChooseConnectionDialog");
	JPanel dialogPane = new JPanel();
	JPanel contentPanel = new JPanel();
	JLabel labelSelectConnection = new JLabel();
	comboConnections = new JComboBox();
	btnNewConnection = new JButton();
	btnEditConnection = new JButton();
	labelDescription = new JLabel();
	JXButtonPanel buttonBar = new JXButtonPanel();
	btnHelp = new JButton();
	okButton = new JButton();
	cancelButton = new JButton();
	CellConstraints cc = new CellConstraints();

	//======== this ========
	Container contentPane = getContentPane();
	contentPane.setLayout(new BorderLayout());

	//======== dialogPane ========
	{
		dialogPane.setBorder(Borders.DIALOG_BORDER);
		dialogPane.setLayout(new BorderLayout());

		//======== contentPanel ========
		{
			contentPanel.setLayout(new FormLayout(
				new ColumnSpec[] {
					FormFactory.DEFAULT_COLSPEC,
					FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
					new ColumnSpec("max(pref;70dlu):grow"),
					FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
					FormFactory.DEFAULT_COLSPEC,
					FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
					FormFactory.DEFAULT_COLSPEC
				},
				new RowSpec[] {
					FormFactory.DEFAULT_ROWSPEC,
					FormFactory.LINE_GAP_ROWSPEC,
					new RowSpec("max(min;15dlu)")
				}));
			((FormLayout)contentPanel.getLayout()).setColumnGroups(new int[][] {{5, 7}});

			//---- labelSelectConnection ----
			labelSelectConnection.setText(bundle.getString("labelSelectConnection.text"));
			labelSelectConnection.setLabelFor(comboConnections);
			contentPanel.add(labelSelectConnection, cc.xy(1, 1));
			contentPanel.add(comboConnections, cc.xy(3, 1));

			//---- btnNewConnection ----
			btnNewConnection.setText(bundle.getString("btnNewConnection.text"));
			contentPanel.add(btnNewConnection, cc.xy(5, 1));

			//---- btnEditConnection ----
			btnEditConnection.setText(bundle.getString("btnEditConnection.text"));
			contentPanel.add(btnEditConnection, cc.xy(7, 1));

			//---- labelDescription ----
			labelDescription.setText(bundle.getString("labelDescription.text"));
			contentPanel.add(labelDescription, cc.xywh(3, 3, 5, 1, CellConstraints.DEFAULT, CellConstraints.TOP));
		}
		dialogPane.add(contentPanel, BorderLayout.CENTER);

		//======== buttonBar ========
		{
			buttonBar.setBorder(Borders.BUTTON_BAR_GAP_BORDER);
			buttonBar.setCyclic(true);
			buttonBar.setLayout(new FormLayout(
				new ColumnSpec[] {
					FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
					FormFactory.DEFAULT_COLSPEC,
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

			//---- okButton ----
			okButton.setText(bundle.getString("okButton.text"));
			buttonBar.add(okButton, cc.xy(4, 1));

			//---- cancelButton ----
			cancelButton.setText(bundle.getString("cancelButton.text"));
			buttonBar.add(cancelButton, cc.xy(6, 1));
		}
		dialogPane.add(buttonBar, BorderLayout.SOUTH);
	}
	contentPane.add(dialogPane, BorderLayout.CENTER);
	pack();
	setLocationRelativeTo(getOwner());

	// Generated using JFormDesigner Open Source Project license - unknown
	private JComboBox comboConnections;
	private JButton btnNewConnection;
	private JButton btnEditConnection;
	private JLabel labelDescription;
	private JButton btnHelp;
	private JButton okButton;
	private JButton cancelButton;

	private void initComponents() {
	}
	// Generated using JFormDesigner Open Source Project license - unknown
	ResourceBundle bundle = ResourceBundle.getBundle("ChooseConnectionDialog");
	JPanel dialogPane = new JPanel();
	JPanel contentPanel = new JPanel();
	JLabel labelSelectConnection = new JLabel();
	comboConnections = new JComboBox();
	btnNewConnection = new JButton();
	btnEditConnection = new JButton();
	labelDescription = new JLabel();
	JXButtonPanel buttonBar = new JXButtonPanel();
	btnHelp = new JButton();
	okButton = new JButton();
	cancelButton = new JButton();
	CellConstraints cc = new CellConstraints();

	//======== this ========
	Container contentPane = getContentPane();
	contentPane.setLayout(new BorderLayout());

	//======== dialogPane ========
	{
		dialogPane.setBorder(Borders.DIALOG_BORDER);
		dialogPane.setLayout(new BorderLayout());

		//======== contentPanel ========
		{
			contentPanel.setLayout(new FormLayout(
				new ColumnSpec[] {
					FormFactory.DEFAULT_COLSPEC,
					FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
					new ColumnSpec("max(pref;70dlu):grow"),
					FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
					FormFactory.DEFAULT_COLSPEC,
					FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
					FormFactory.DEFAULT_COLSPEC
				},
				new RowSpec[] {
					FormFactory.DEFAULT_ROWSPEC,
					FormFactory.LINE_GAP_ROWSPEC,
					new RowSpec("max(min;15dlu)")
				}));
			((FormLayout)contentPanel.getLayout()).setColumnGroups(new int[][] {{5, 7}});

			//---- labelSelectConnection ----
			labelSelectConnection.setText(bundle.getString("labelSelectConnection.text"));
			labelSelectConnection.setLabelFor(comboConnections);
			contentPanel.add(labelSelectConnection, cc.xy(1, 1));
			contentPanel.add(comboConnections, cc.xy(3, 1));

			//---- btnNewConnection ----
			btnNewConnection.setText(bundle.getString("btnNewConnection.text"));
			contentPanel.add(btnNewConnection, cc.xy(5, 1));

			//---- btnEditConnection ----
			btnEditConnection.setText(bundle.getString("btnEditConnection.text"));
			contentPanel.add(btnEditConnection, cc.xy(7, 1));

			//---- labelDescription ----
			labelDescription.setText(bundle.getString("labelDescription.text"));
			contentPanel.add(labelDescription, cc.xywh(3, 3, 5, 1, CellConstraints.DEFAULT, CellConstraints.TOP));
		}
		dialogPane.add(contentPanel, BorderLayout.CENTER);

		//======== buttonBar ========
		{
			buttonBar.setBorder(Borders.BUTTON_BAR_GAP_BORDER);
			buttonBar.setCyclic(true);
			buttonBar.setLayout(new FormLayout(
				new ColumnSpec[] {
					FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
					FormFactory.DEFAULT_COLSPEC,
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

			//---- okButton ----
			okButton.setText(bundle.getString("okButton.text"));
			buttonBar.add(okButton, cc.xy(4, 1));

			//---- cancelButton ----
			cancelButton.setText(bundle.getString("cancelButton.text"));
			buttonBar.add(cancelButton, cc.xy(6, 1));
		}
		dialogPane.add(buttonBar, BorderLayout.SOUTH);
	}
	contentPane.add(dialogPane, BorderLayout.CENTER);
	pack();
	setLocationRelativeTo(getOwner());

	// Generated using JFormDesigner Open Source Project license - unknown
	private JComboBox comboConnections;
	private JButton btnNewConnection;
	private JButton btnEditConnection;
	private JLabel labelDescription;
	private JButton btnHelp;
	private JButton okButton;
	private JButton cancelButton;

	private void initComponents() {
	}
	// Generated using JFormDesigner Open Source Project license - unknown
	ResourceBundle bundle = ResourceBundle.getBundle("ChooseConnectionDialog");
	JPanel dialogPane = new JPanel();
	JPanel contentPanel = new JPanel();
	JLabel labelSelectConnection = new JLabel();
	comboConnections = new JComboBox();
	btnNewConnection = new JButton();
	btnEditConnection = new JButton();
	labelDescription = new JLabel();
	JXButtonPanel buttonBar = new JXButtonPanel();
	btnHelp = new JButton();
	okButton = new JButton();
	cancelButton = new JButton();
	CellConstraints cc = new CellConstraints();

	//======== this ========
	Container contentPane = getContentPane();
	contentPane.setLayout(new BorderLayout());

	//======== dialogPane ========
	{
		dialogPane.setBorder(Borders.DIALOG_BORDER);
		dialogPane.setLayout(new BorderLayout());

		//======== contentPanel ========
		{
			contentPanel.setLayout(new FormLayout(
				new ColumnSpec[] {
					FormFactory.DEFAULT_COLSPEC,
					FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
					new ColumnSpec("max(pref;70dlu):grow"),
					FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
					FormFactory.DEFAULT_COLSPEC,
					FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
					FormFactory.DEFAULT_COLSPEC
				},
				new RowSpec[] {
					FormFactory.DEFAULT_ROWSPEC,
					FormFactory.LINE_GAP_ROWSPEC,
					new RowSpec("max(min;15dlu)")
				}));
			((FormLayout)contentPanel.getLayout()).setColumnGroups(new int[][] {{5, 7}});

			//---- labelSelectConnection ----
			labelSelectConnection.setText(bundle.getString("labelSelectConnection.text"));
			labelSelectConnection.setLabelFor(comboConnections);
			contentPanel.add(labelSelectConnection, cc.xy(1, 1));
			contentPanel.add(comboConnections, cc.xy(3, 1));

			//---- btnNewConnection ----
			btnNewConnection.setText(bundle.getString("btnNewConnection.text"));
			contentPanel.add(btnNewConnection, cc.xy(5, 1));

			//---- btnEditConnection ----
			btnEditConnection.setText(bundle.getString("btnEditConnection.text"));
			contentPanel.add(btnEditConnection, cc.xy(7, 1));

			//---- labelDescription ----
			labelDescription.setText(bundle.getString("labelDescription.text"));
			contentPanel.add(labelDescription, cc.xywh(3, 3, 5, 1, CellConstraints.DEFAULT, CellConstraints.TOP));
		}
		dialogPane.add(contentPanel, BorderLayout.CENTER);

		//======== buttonBar ========
		{
			buttonBar.setBorder(Borders.BUTTON_BAR_GAP_BORDER);
			buttonBar.setCyclic(true);
			buttonBar.setLayout(new FormLayout(
				new ColumnSpec[] {
					FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
					FormFactory.DEFAULT_COLSPEC,
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

			//---- okButton ----
			okButton.setText(bundle.getString("okButton.text"));
			buttonBar.add(okButton, cc.xy(4, 1));

			//---- cancelButton ----
			cancelButton.setText(bundle.getString("cancelButton.text"));
			buttonBar.add(cancelButton, cc.xy(6, 1));
		}
		dialogPane.add(buttonBar, BorderLayout.SOUTH);
	}
	contentPane.add(dialogPane, BorderLayout.CENTER);
	pack();
	setLocationRelativeTo(getOwner());

	// Generated using JFormDesigner Open Source Project license - unknown
	private JComboBox comboConnections;
	private JButton btnNewConnection;
	private JButton btnEditConnection;
	private JLabel labelDescription;
	private JButton btnHelp;
	private JButton okButton;
	private JButton cancelButton;

	private void initComponents() {
	}
	// Generated using JFormDesigner Open Source Project license - unknown
	ResourceBundle bundle = ResourceBundle.getBundle("ChooseConnectionDialog");
	JPanel dialogPane = new JPanel();
	JPanel contentPanel = new JPanel();
	JLabel labelSelectConnection = new JLabel();
	comboConnections = new JComboBox();
	btnNewConnection = new JButton();
	btnEditConnection = new JButton();
	labelDescription = new JLabel();
	JXButtonPanel buttonBar = new JXButtonPanel();
	btnHelp = new JButton();
	okButton = new JButton();
	cancelButton = new JButton();
	CellConstraints cc = new CellConstraints();

	//======== this ========
	Container contentPane = getContentPane();
	contentPane.setLayout(new BorderLayout());

	//======== dialogPane ========
	{
		dialogPane.setBorder(Borders.DIALOG_BORDER);
		dialogPane.setLayout(new BorderLayout());

		//======== contentPanel ========
		{
			contentPanel.setLayout(new FormLayout(
				new ColumnSpec[] {
					FormFactory.DEFAULT_COLSPEC,
					FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
					new ColumnSpec("max(pref;70dlu):grow"),
					FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
					FormFactory.DEFAULT_COLSPEC,
					FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
					FormFactory.DEFAULT_COLSPEC
				},
				new RowSpec[] {
					FormFactory.DEFAULT_ROWSPEC,
					FormFactory.LINE_GAP_ROWSPEC,
					new RowSpec("max(min;15dlu)")
				}));
			((FormLayout)contentPanel.getLayout()).setColumnGroups(new int[][] {{5, 7}});

			//---- labelSelectConnection ----
			labelSelectConnection.setText(bundle.getString("labelSelectConnection.text"));
			labelSelectConnection.setLabelFor(comboConnections);
			contentPanel.add(labelSelectConnection, cc.xy(1, 1));
			contentPanel.add(comboConnections, cc.xy(3, 1));

			//---- btnNewConnection ----
			btnNewConnection.setText(bundle.getString("btnNewConnection.text"));
			contentPanel.add(btnNewConnection, cc.xy(5, 1));

			//---- btnEditConnection ----
			btnEditConnection.setText(bundle.getString("btnEditConnection.text"));
			contentPanel.add(btnEditConnection, cc.xy(7, 1));

			//---- labelDescription ----
			labelDescription.setText(bundle.getString("labelDescription.text"));
			contentPanel.add(labelDescription, cc.xywh(3, 3, 5, 1, CellConstraints.DEFAULT, CellConstraints.TOP));
		}
		dialogPane.add(contentPanel, BorderLayout.CENTER);

		//======== buttonBar ========
		{
			buttonBar.setBorder(Borders.BUTTON_BAR_GAP_BORDER);
			buttonBar.setCyclic(true);
			buttonBar.setLayout(new FormLayout(
				new ColumnSpec[] {
					FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
					FormFactory.DEFAULT_COLSPEC,
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

			//---- okButton ----
			okButton.setText(bundle.getString("okButton.text"));
			buttonBar.add(okButton, cc.xy(4, 1));

			//---- cancelButton ----
			cancelButton.setText(bundle.getString("cancelButton.text"));
			buttonBar.add(cancelButton, cc.xy(6, 1));
		}
		dialogPane.add(buttonBar, BorderLayout.SOUTH);
	}
	contentPane.add(dialogPane, BorderLayout.CENTER);
	pack();
	setLocationRelativeTo(getOwner());

	// Generated using JFormDesigner Open Source Project license - unknown
	private JComboBox comboConnections;
	private JButton btnNewConnection;
	private JButton btnEditConnection;
	private JLabel labelDescription;
	private JButton btnHelp;
	private JButton okButton;
	private JButton cancelButton;

	private void initComponents() {
	}
	// Generated using JFormDesigner Open Source Project license - unknown
	ResourceBundle bundle = ResourceBundle.getBundle("ChooseConnectionDialog");
	JPanel dialogPane = new JPanel();
	JPanel contentPanel = new JPanel();
	JLabel labelSelectConnection = new JLabel();
	comboConnections = new JComboBox();
	btnNewConnection = new JButton();
	btnEditConnection = new JButton();
	labelDescription = new JLabel();
	JXButtonPanel buttonBar = new JXButtonPanel();
	btnHelp = new JButton();
	okButton = new JButton();
	cancelButton = new JButton();
	CellConstraints cc = new CellConstraints();

	//======== this ========
	Container contentPane = getContentPane();
	contentPane.setLayout(new BorderLayout());

	//======== dialogPane ========
	{
		dialogPane.setBorder(Borders.DIALOG_BORDER);
		dialogPane.setLayout(new BorderLayout());

		//======== contentPanel ========
		{
			contentPanel.setLayout(new FormLayout(
				new ColumnSpec[] {
					FormFactory.DEFAULT_COLSPEC,
					FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
					new ColumnSpec("max(pref;70dlu):grow"),
					FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
					FormFactory.DEFAULT_COLSPEC,
					FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
					FormFactory.DEFAULT_COLSPEC
				},
				new RowSpec[] {
					FormFactory.DEFAULT_ROWSPEC,
					FormFactory.LINE_GAP_ROWSPEC,
					new RowSpec("max(min;15dlu)")
				}));
			((FormLayout)contentPanel.getLayout()).setColumnGroups(new int[][] {{5, 7}});

			//---- labelSelectConnection ----
			labelSelectConnection.setText(bundle.getString("labelSelectConnection.text"));
			labelSelectConnection.setLabelFor(comboConnections);
			contentPanel.add(labelSelectConnection, cc.xy(1, 1));
			contentPanel.add(comboConnections, cc.xy(3, 1));

			//---- btnNewConnection ----
			btnNewConnection.setText(bundle.getString("btnNewConnection.text"));
			contentPanel.add(btnNewConnection, cc.xy(5, 1));

			//---- btnEditConnection ----
			btnEditConnection.setText(bundle.getString("btnEditConnection.text"));
			contentPanel.add(btnEditConnection, cc.xy(7, 1));

			//---- labelDescription ----
			labelDescription.setText(bundle.getString("labelDescription.text"));
			contentPanel.add(labelDescription, cc.xywh(3, 3, 5, 1, CellConstraints.DEFAULT, CellConstraints.TOP));
		}
		dialogPane.add(contentPanel, BorderLayout.CENTER);

		//======== buttonBar ========
		{
			buttonBar.setBorder(Borders.BUTTON_BAR_GAP_BORDER);
			buttonBar.setCyclic(true);
			buttonBar.setLayout(new FormLayout(
				new ColumnSpec[] {
					FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
					FormFactory.DEFAULT_COLSPEC,
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

			//---- okButton ----
			okButton.setText(bundle.getString("okButton.text"));
			buttonBar.add(okButton, cc.xy(4, 1));

			//---- cancelButton ----
			cancelButton.setText(bundle.getString("cancelButton.text"));
			buttonBar.add(cancelButton, cc.xy(6, 1));
		}
		dialogPane.add(buttonBar, BorderLayout.SOUTH);
	}
	contentPane.add(dialogPane, BorderLayout.CENTER);
	pack();
	setLocationRelativeTo(getOwner());

	// Generated using JFormDesigner Open Source Project license - unknown
	private JComboBox comboConnections;
	private JButton btnNewConnection;
	private JButton btnEditConnection;
	private JLabel labelDescription;
	private JButton btnHelp;
	private JButton okButton;
	private JButton cancelButton;

	private void initComponents() {
	}
	// Generated using JFormDesigner Open Source Project license - unknown
	ResourceBundle bundle = ResourceBundle.getBundle("ChooseConnectionDialog");
	dialogPane = new JPanel();
	JPanel contentPanel = new JPanel();
	JLabel labelSelectConnection = new JLabel();
	comboConnections = new JComboBox();
	btnNewConnection = new JButton();
	btnEditConnection = new JButton();
	labelDescription = new JLabel();
	JXButtonPanel buttonBar = new JXButtonPanel();
	btnHelp = new JButton();
	okButton = new JButton();
	cancelButton = new JButton();
	CellConstraints cc = new CellConstraints();

	//======== this ========
	Container contentPane = getContentPane();
	contentPane.setLayout(new BorderLayout());

	//======== dialogPane ========
	{
		dialogPane.setBorder(Borders.DIALOG_BORDER);
		dialogPane.setLayout(new BorderLayout());

		//======== contentPanel ========
		{
			contentPanel.setLayout(new FormLayout(
				new ColumnSpec[] {
					FormFactory.DEFAULT_COLSPEC,
					FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
					new ColumnSpec("max(pref;70dlu):grow"),
					FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
					FormFactory.DEFAULT_COLSPEC,
					FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
					FormFactory.DEFAULT_COLSPEC
				},
				new RowSpec[] {
					FormFactory.DEFAULT_ROWSPEC,
					FormFactory.LINE_GAP_ROWSPEC,
					new RowSpec("max(min;15dlu)")
				}));
			((FormLayout)contentPanel.getLayout()).setColumnGroups(new int[][] {{5, 7}});

			//---- labelSelectConnection ----
			labelSelectConnection.setText(bundle.getString("labelSelectConnection.text"));
			labelSelectConnection.setLabelFor(comboConnections);
			contentPanel.add(labelSelectConnection, cc.xy(1, 1));
			contentPanel.add(comboConnections, cc.xy(3, 1));

			//---- btnNewConnection ----
			btnNewConnection.setText(bundle.getString("btnNewConnection.text"));
			contentPanel.add(btnNewConnection, cc.xy(5, 1));

			//---- btnEditConnection ----
			btnEditConnection.setText(bundle.getString("btnEditConnection.text"));
			contentPanel.add(btnEditConnection, cc.xy(7, 1));

			//---- labelDescription ----
			labelDescription.setText(bundle.getString("labelDescription.text"));
			contentPanel.add(labelDescription, cc.xywh(3, 3, 5, 1, CellConstraints.DEFAULT, CellConstraints.TOP));
		}
		dialogPane.add(contentPanel, BorderLayout.CENTER);

		//======== buttonBar ========
		{
			buttonBar.setBorder(Borders.BUTTON_BAR_GAP_BORDER);
			buttonBar.setCyclic(true);
			buttonBar.setLayout(new FormLayout(
				new ColumnSpec[] {
					FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
					FormFactory.DEFAULT_COLSPEC,
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

			//---- okButton ----
			okButton.setText(bundle.getString("okButton.text"));
			buttonBar.add(okButton, cc.xy(4, 1));

			//---- cancelButton ----
			cancelButton.setText(bundle.getString("cancelButton.text"));
			buttonBar.add(cancelButton, cc.xy(6, 1));
		}
		dialogPane.add(buttonBar, BorderLayout.SOUTH);
	}
	contentPane.add(dialogPane, BorderLayout.CENTER);
	pack();
	setLocationRelativeTo(getOwner());

	// Generated using JFormDesigner Open Source Project license - unknown
	private JPanel dialogPane;
	private JComboBox comboConnections;
	private JButton btnNewConnection;
	private JButton btnEditConnection;
	private JLabel labelDescription;
	private JButton btnHelp;
	private JButton okButton;
	private JButton cancelButton;

	private void initComponents() {
	}
	// Generated using JFormDesigner Open Source Project license - unknown
	ResourceBundle bundle = ResourceBundle.getBundle("ChooseConnectionDialog");
	dialogPane = new JPanel();
	JXButtonPanel buttonBar = new JXButtonPanel();
	btnHelp = new JButton();
	okButton = new JButton();
	cancelButton = new JButton();
	JPanel contentPanel2 = new JPanel();
	JLabel labelSelectConnection = new JLabel();
	comboConnections = new JComboBox();
	btnNewConnection = new JButton();
	btnEditConnection = new JButton();
	labelDescription = new JLabel();
	CellConstraints cc = new CellConstraints();

	//======== this ========
	Container contentPane = getContentPane();
	contentPane.setLayout(new BorderLayout());

	//======== dialogPane ========
	{
		dialogPane.setBorder(Borders.DIALOG_BORDER);
		dialogPane.setLayout(new BorderLayout());

		//======== buttonBar ========
		{
			buttonBar.setBorder(Borders.BUTTON_BAR_GAP_BORDER);
			buttonBar.setCyclic(true);
			buttonBar.setLayout(new FormLayout(
				new ColumnSpec[] {
					FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
					FormFactory.DEFAULT_COLSPEC,
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

			//---- okButton ----
			okButton.setText(bundle.getString("okButton.text"));
			buttonBar.add(okButton, cc.xy(4, 1));

			//---- cancelButton ----
			cancelButton.setText(bundle.getString("cancelButton.text"));
			buttonBar.add(cancelButton, cc.xy(6, 1));
		}
		dialogPane.add(buttonBar, BorderLayout.SOUTH);

		//======== contentPanel2 ========
		{
			contentPanel2.setLayout(new FormLayout(
				new ColumnSpec[] {
					FormFactory.DEFAULT_COLSPEC,
					FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
					new ColumnSpec("max(pref;70dlu):grow"),
					FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
					FormFactory.DEFAULT_COLSPEC,
					FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
					FormFactory.DEFAULT_COLSPEC
				},
				new RowSpec[] {
					FormFactory.DEFAULT_ROWSPEC,
					FormFactory.LINE_GAP_ROWSPEC,
					new RowSpec("max(min;15dlu)")
				}));
			((FormLayout)contentPanel2.getLayout()).setColumnGroups(new int[][] {{5, 7}});

			//---- labelSelectConnection ----
			labelSelectConnection.setText(bundle.getString("labelSelectConnection.text"));
			labelSelectConnection.setLabelFor(comboConnections);
			contentPanel2.add(labelSelectConnection, cc.xy(1, 1));
			contentPanel2.add(comboConnections, cc.xy(3, 1));

			//---- btnNewConnection ----
			btnNewConnection.setText(bundle.getString("btnNewConnection.text"));
			contentPanel2.add(btnNewConnection, cc.xy(5, 1));

			//---- btnEditConnection ----
			btnEditConnection.setText(bundle.getString("btnEditConnection.text"));
			contentPanel2.add(btnEditConnection, cc.xy(7, 1));

			//---- labelDescription ----
			labelDescription.setText(bundle.getString("labelDescription.text"));
			contentPanel2.add(labelDescription, cc.xywh(3, 3, 5, 1, CellConstraints.DEFAULT, CellConstraints.TOP));
		}
		dialogPane.add(contentPanel2, BorderLayout.NORTH);
	}
	contentPane.add(dialogPane, BorderLayout.CENTER);
	pack();
	setLocationRelativeTo(getOwner());

	// Generated using JFormDesigner Open Source Project license - unknown
	private JPanel dialogPane;
	private JButton btnHelp;
	private JButton okButton;
	private JButton cancelButton;
	private JComboBox comboConnections;
	private JButton btnNewConnection;
	private JButton btnEditConnection;
	private JLabel labelDescription;

	private void initComponents() {
	}
	// Generated using JFormDesigner Open Source Project license - unknown
	ResourceBundle bundle = ResourceBundle.getBundle("ChooseConnectionDialog");
	dialogPane = new JPanel();
	JXButtonPanel buttonBar = new JXButtonPanel();
	btnHelp = new JButton();
	okButton = new JButton();
	cancelButton = new JButton();
	JPanel contentPanel2 = new JPanel();
	JLabel labelSelectConnection = new JLabel();
	comboConnections = new JComboBox();
	btnNewConnection = new JButton();
	btnEditConnection = new JButton();
	labelDescription = new JLabel();
	CellConstraints cc = new CellConstraints();

	//======== this ========
	Container contentPane = getContentPane();
	contentPane.setLayout(new BorderLayout());

	//======== dialogPane ========
	{
		dialogPane.setBorder(Borders.DIALOG_BORDER);
		dialogPane.setLayout(new BorderLayout());

		//======== buttonBar ========
		{
			buttonBar.setBorder(Borders.BUTTON_BAR_GAP_BORDER);
			buttonBar.setCyclic(true);
			buttonBar.setLayout(new FormLayout(
				new ColumnSpec[] {
					FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
					FormFactory.DEFAULT_COLSPEC,
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

			//---- okButton ----
			okButton.setText(bundle.getString("okButton.text"));
			buttonBar.add(okButton, cc.xy(4, 1));

			//---- cancelButton ----
			cancelButton.setText(bundle.getString("cancelButton.text"));
			buttonBar.add(cancelButton, cc.xy(6, 1));
		}
		dialogPane.add(buttonBar, BorderLayout.SOUTH);

		//======== contentPanel2 ========
		{
			contentPanel2.setLayout(new FormLayout(
				new ColumnSpec[] {
					FormFactory.DEFAULT_COLSPEC,
					FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
					new ColumnSpec("max(pref;70dlu):grow"),
					FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
					FormFactory.DEFAULT_COLSPEC,
					FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
					FormFactory.DEFAULT_COLSPEC
				},
				new RowSpec[] {
					FormFactory.DEFAULT_ROWSPEC,
					FormFactory.LINE_GAP_ROWSPEC,
					new RowSpec("max(min;15dlu)")
				}));
			((FormLayout)contentPanel2.getLayout()).setColumnGroups(new int[][] {{5, 7}});

			//---- labelSelectConnection ----
			labelSelectConnection.setText(bundle.getString("labelSelectConnection.text"));
			labelSelectConnection.setLabelFor(comboConnections);
			contentPanel2.add(labelSelectConnection, cc.xy(1, 1));
			contentPanel2.add(comboConnections, cc.xy(3, 1));

			//---- btnNewConnection ----
			btnNewConnection.setText(bundle.getString("btnNewConnection.text"));
			contentPanel2.add(btnNewConnection, cc.xy(5, 1));

			//---- btnEditConnection ----
			btnEditConnection.setText(bundle.getString("btnEditConnection.text"));
			contentPanel2.add(btnEditConnection, cc.xy(7, 1));

			//---- labelDescription ----
			labelDescription.setText(bundle.getString("labelDescription.text"));
			contentPanel2.add(labelDescription, cc.xywh(3, 3, 5, 1, CellConstraints.DEFAULT, CellConstraints.TOP));
		}
		dialogPane.add(contentPanel2, BorderLayout.NORTH);
	}
	contentPane.add(dialogPane, BorderLayout.CENTER);
	pack();
	setLocationRelativeTo(getOwner());

	// Generated using JFormDesigner Open Source Project license - unknown
	private JPanel dialogPane;
	private JButton btnHelp;
	private JButton okButton;
	private JButton cancelButton;
	private JComboBox comboConnections;
	private JButton btnNewConnection;
	private JButton btnEditConnection;
	private JLabel labelDescription;

	private void initComponents() {
	}
	// Generated using JFormDesigner Open Source Project license - unknown
	ResourceBundle bundle = ResourceBundle.getBundle("ChooseConnectionDialog");
	dialogPane = new JPanel();
	JXButtonPanel buttonBar = new JXButtonPanel();
	btnHelp = new JButton();
	okButton = new JButton();
	cancelButton = new JButton();
	JPanel contentPanel2 = new JPanel();
	JLabel labelSelectConnection = new JLabel();
	comboConnections = new JComboBox();
	btnNewConnection = new JButton();
	btnEditConnection = new JButton();
	labelDescription = new JLabel();
	CellConstraints cc = new CellConstraints();

	//======== this ========
	Container contentPane = getContentPane();
	contentPane.setLayout(new BorderLayout());

	//======== dialogPane ========
	{
		dialogPane.setBorder(Borders.DIALOG_BORDER);
		dialogPane.setLayout(new BorderLayout());

		//======== buttonBar ========
		{
			buttonBar.setBorder(Borders.BUTTON_BAR_GAP_BORDER);
			buttonBar.setCyclic(true);
			buttonBar.setLayout(new FormLayout(
				new ColumnSpec[] {
					FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
					FormFactory.DEFAULT_COLSPEC,
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

			//---- okButton ----
			okButton.setText(bundle.getString("okButton.text"));
			buttonBar.add(okButton, cc.xy(4, 1));

			//---- cancelButton ----
			cancelButton.setText(bundle.getString("cancelButton.text"));
			buttonBar.add(cancelButton, cc.xy(6, 1));
		}
		dialogPane.add(buttonBar, BorderLayout.SOUTH);

		//======== contentPanel2 ========
		{
			contentPanel2.setLayout(new FormLayout(
				new ColumnSpec[] {
					FormFactory.DEFAULT_COLSPEC,
					FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
					new ColumnSpec("max(pref;70dlu):grow"),
					FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
					FormFactory.DEFAULT_COLSPEC,
					FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
					FormFactory.DEFAULT_COLSPEC
				},
				new RowSpec[] {
					FormFactory.DEFAULT_ROWSPEC,
					FormFactory.LINE_GAP_ROWSPEC,
					new RowSpec("max(min;15dlu)")
				}));
			((FormLayout)contentPanel2.getLayout()).setColumnGroups(new int[][] {{5, 7}});

			//---- labelSelectConnection ----
			labelSelectConnection.setText(bundle.getString("labelSelectConnection.text"));
			labelSelectConnection.setLabelFor(comboConnections);
			contentPanel2.add(labelSelectConnection, cc.xy(1, 1));
			contentPanel2.add(comboConnections, cc.xy(3, 1));

			//---- btnNewConnection ----
			btnNewConnection.setText(bundle.getString("btnNewConnection.text"));
			contentPanel2.add(btnNewConnection, cc.xy(5, 1));

			//---- btnEditConnection ----
			btnEditConnection.setText(bundle.getString("btnEditConnection.text"));
			contentPanel2.add(btnEditConnection, cc.xy(7, 1));

			//---- labelDescription ----
			labelDescription.setText(bundle.getString("labelDescription.text"));
			contentPanel2.add(labelDescription, cc.xywh(3, 3, 5, 1, CellConstraints.DEFAULT, CellConstraints.TOP));
		}
		dialogPane.add(contentPanel2, BorderLayout.NORTH);
	}
	contentPane.add(dialogPane, BorderLayout.CENTER);
	pack();
	setLocationRelativeTo(getOwner());

	// Generated using JFormDesigner Open Source Project license - unknown
	private JPanel dialogPane;
	private JButton btnHelp;
	private JButton okButton;
	private JButton cancelButton;
	private JComboBox comboConnections;
	private JButton btnNewConnection;
	private JButton btnEditConnection;
	private JLabel labelDescription;

	private void initComponents() {
	}
	// Generated using JFormDesigner Open Source Project license - unknown
	ResourceBundle bundle = ResourceBundle.getBundle("ChooseConnectionDialog");
	dialogPane = new JPanel();
	JXButtonPanel buttonBar = new JXButtonPanel();
	btnHelp = new JButton();
	okButton = new JButton();
	btnCancel = new JButton();
	JPanel contentPanel2 = new JPanel();
	JLabel labelSelectConnection = new JLabel();
	comboConnections = new JComboBox();
	btnNewConnection = new JButton();
	btnEditConnection = new JButton();
	labelDescription = new JLabel();
	CellConstraints cc = new CellConstraints();

	//======== this ========
	Container contentPane = getContentPane();
	contentPane.setLayout(new BorderLayout());

	//======== dialogPane ========
	{
		dialogPane.setBorder(Borders.DIALOG_BORDER);
		dialogPane.setLayout(new BorderLayout());

		//======== buttonBar ========
		{
			buttonBar.setBorder(Borders.BUTTON_BAR_GAP_BORDER);
			buttonBar.setCyclic(true);
			buttonBar.setLayout(new FormLayout(
				new ColumnSpec[] {
					FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
					FormFactory.DEFAULT_COLSPEC,
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

			//---- okButton ----
			okButton.setText(bundle.getString("okButton.text"));
			buttonBar.add(okButton, cc.xy(4, 1));

			//---- btnCancel ----
			btnCancel.setText(bundle.getString("btnCancel.text"));
			buttonBar.add(btnCancel, cc.xy(6, 1));
		}
		dialogPane.add(buttonBar, BorderLayout.SOUTH);

		//======== contentPanel2 ========
		{
			contentPanel2.setLayout(new FormLayout(
				new ColumnSpec[] {
					FormFactory.DEFAULT_COLSPEC,
					FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
					new ColumnSpec("max(pref;70dlu):grow"),
					FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
					FormFactory.DEFAULT_COLSPEC,
					FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
					FormFactory.DEFAULT_COLSPEC
				},
				new RowSpec[] {
					FormFactory.DEFAULT_ROWSPEC,
					FormFactory.LINE_GAP_ROWSPEC,
					new RowSpec("max(min;15dlu)")
				}));
			((FormLayout)contentPanel2.getLayout()).setColumnGroups(new int[][] {{5, 7}});

			//---- labelSelectConnection ----
			labelSelectConnection.setText(bundle.getString("labelSelectConnection.text"));
			labelSelectConnection.setLabelFor(comboConnections);
			contentPanel2.add(labelSelectConnection, cc.xy(1, 1));
			contentPanel2.add(comboConnections, cc.xy(3, 1));

			//---- btnNewConnection ----
			btnNewConnection.setText(bundle.getString("btnNewConnection.text"));
			contentPanel2.add(btnNewConnection, cc.xy(5, 1));

			//---- btnEditConnection ----
			btnEditConnection.setText(bundle.getString("btnEditConnection.text"));
			contentPanel2.add(btnEditConnection, cc.xy(7, 1));

			//---- labelDescription ----
			labelDescription.setText(bundle.getString("labelDescription.text"));
			contentPanel2.add(labelDescription, cc.xywh(3, 3, 5, 1, CellConstraints.DEFAULT, CellConstraints.TOP));
		}
		dialogPane.add(contentPanel2, BorderLayout.NORTH);
	}
	contentPane.add(dialogPane, BorderLayout.CENTER);
	pack();
	setLocationRelativeTo(getOwner());

	// Generated using JFormDesigner Open Source Project license - unknown
	private JPanel dialogPane;
	private JButton btnHelp;
	private JButton okButton;
	private JButton btnCancel;
	private JComboBox comboConnections;
	private JButton btnNewConnection;
	private JButton btnEditConnection;
	private JLabel labelDescription;

	private void initComponents() {
	}
	// Generated using JFormDesigner Open Source Project license - unknown
	ResourceBundle bundle = ResourceBundle.getBundle("ChooseConnectionDialog");
	dialogPane = new JPanel();
	JXButtonPanel buttonBar = new JXButtonPanel();
	btnHelp = new JButton();
	btnOK = new JButton();
	btnCancel = new JButton();
	JPanel contentPanel2 = new JPanel();
	JLabel labelSelectConnection = new JLabel();
	comboConnections = new JComboBox();
	btnNewConnection = new JButton();
	btnEditConnection = new JButton();
	labelDescription = new JLabel();
	CellConstraints cc = new CellConstraints();

	//======== this ========
	Container contentPane = getContentPane();
	contentPane.setLayout(new BorderLayout());

	//======== dialogPane ========
	{
		dialogPane.setBorder(Borders.DIALOG_BORDER);
		dialogPane.setLayout(new BorderLayout());

		//======== buttonBar ========
		{
			buttonBar.setBorder(Borders.BUTTON_BAR_GAP_BORDER);
			buttonBar.setCyclic(true);
			buttonBar.setLayout(new FormLayout(
				new ColumnSpec[] {
					FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
					FormFactory.DEFAULT_COLSPEC,
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

		//======== contentPanel2 ========
		{
			contentPanel2.setLayout(new FormLayout(
				new ColumnSpec[] {
					FormFactory.DEFAULT_COLSPEC,
					FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
					new ColumnSpec("max(pref;70dlu):grow"),
					FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
					FormFactory.DEFAULT_COLSPEC,
					FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
					FormFactory.DEFAULT_COLSPEC
				},
				new RowSpec[] {
					FormFactory.DEFAULT_ROWSPEC,
					FormFactory.LINE_GAP_ROWSPEC,
					new RowSpec("max(min;15dlu)")
				}));
			((FormLayout)contentPanel2.getLayout()).setColumnGroups(new int[][] {{5, 7}});

			//---- labelSelectConnection ----
			labelSelectConnection.setText(bundle.getString("labelSelectConnection.text"));
			labelSelectConnection.setLabelFor(comboConnections);
			contentPanel2.add(labelSelectConnection, cc.xy(1, 1));
			contentPanel2.add(comboConnections, cc.xy(3, 1));

			//---- btnNewConnection ----
			btnNewConnection.setText(bundle.getString("btnNewConnection.text"));
			contentPanel2.add(btnNewConnection, cc.xy(5, 1));

			//---- btnEditConnection ----
			btnEditConnection.setText(bundle.getString("btnEditConnection.text"));
			contentPanel2.add(btnEditConnection, cc.xy(7, 1));

			//---- labelDescription ----
			labelDescription.setText(bundle.getString("labelDescription.text"));
			contentPanel2.add(labelDescription, cc.xywh(3, 3, 5, 1, CellConstraints.DEFAULT, CellConstraints.TOP));
		}
		dialogPane.add(contentPanel2, BorderLayout.NORTH);
	}
	contentPane.add(dialogPane, BorderLayout.CENTER);
	pack();
	setLocationRelativeTo(getOwner());

	// Generated using JFormDesigner Open Source Project license - unknown
	private JPanel dialogPane;
	private JButton btnHelp;
	private JButton btnOK;
	private JButton btnCancel;
	private JComboBox comboConnections;
	private JButton btnNewConnection;
	private JButton btnEditConnection;
	private JLabel labelDescription;

	private void initComponents() {
	}
	// Generated using JFormDesigner Open Source Project license - unknown
	ResourceBundle bundle = ResourceBundle.getBundle("ChooseConnectionDialog");
	dialogPane = new JPanel();
	JXButtonPanel buttonBar = new JXButtonPanel();
	btnHelp = new JButton();
	btnOK = new JButton();
	btnCancel = new JButton();
	JPanel contentPanel2 = new JPanel();
	JLabel labelSelectConnection = new JLabel();
	comboConnections = new JComboBox();
	btnEditConnection = new JButton();
	labelDescription = new JLabel();
	CellConstraints cc = new CellConstraints();

	//======== this ========
	Container contentPane = getContentPane();
	contentPane.setLayout(new BorderLayout());

	//======== dialogPane ========
	{
		dialogPane.setBorder(Borders.DIALOG_BORDER);
		dialogPane.setLayout(new BorderLayout());

		//======== buttonBar ========
		{
			buttonBar.setBorder(Borders.BUTTON_BAR_GAP_BORDER);
			buttonBar.setCyclic(true);
			buttonBar.setLayout(new FormLayout(
				new ColumnSpec[] {
					FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
					FormFactory.DEFAULT_COLSPEC,
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

		//======== contentPanel2 ========
		{
			contentPanel2.setLayout(new FormLayout(
				new ColumnSpec[] {
					FormFactory.DEFAULT_COLSPEC,
					FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
					new ColumnSpec("max(pref;70dlu):grow"),
					FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
					FormFactory.DEFAULT_COLSPEC
				},
				new RowSpec[] {
					FormFactory.DEFAULT_ROWSPEC,
					FormFactory.LINE_GAP_ROWSPEC,
					new RowSpec("max(min;15dlu)")
				}));

			//---- labelSelectConnection ----
			labelSelectConnection.setText(bundle.getString("labelSelectConnection.text"));
			labelSelectConnection.setLabelFor(comboConnections);
			contentPanel2.add(labelSelectConnection, cc.xy(1, 1));
			contentPanel2.add(comboConnections, cc.xy(3, 1));

			//---- btnEditConnection ----
			btnEditConnection.setText(bundle.getString("btnEditConnection.text"));
			contentPanel2.add(btnEditConnection, cc.xy(5, 1));

			//---- labelDescription ----
			labelDescription.setText(bundle.getString("labelDescription.text"));
			contentPanel2.add(labelDescription, cc.xywh(3, 3, 3, 1, CellConstraints.DEFAULT, CellConstraints.TOP));
		}
		dialogPane.add(contentPanel2, BorderLayout.NORTH);
	}
	contentPane.add(dialogPane, BorderLayout.CENTER);
	pack();
	setLocationRelativeTo(getOwner());

	// Generated using JFormDesigner Open Source Project license - unknown
	private JPanel dialogPane;
	private JButton btnHelp;
	private JButton btnOK;
	private JButton btnCancel;
	private JComboBox comboConnections;
	private JButton btnEditConnection;
	private JLabel labelDescription;

	private void initComponents() {
	}
	// Generated using JFormDesigner Open Source Project license - unknown
	ResourceBundle bundle = ResourceBundle.getBundle("ChooseConnectionDialog");
	dialogPane = new JPanel();
	JXButtonPanel buttonBar = new JXButtonPanel();
	btnHelp = new JButton();
	btnOK = new JButton();
	btnCancel = new JButton();
	JPanel contentPanel2 = new JPanel();
	JLabel labelSelectConnection = new JLabel();
	comboConnections = new JComboBox();
	btnEditConnection = new JButton();
	labelDescription = new JLabel();
	CellConstraints cc = new CellConstraints();

	//======== this ========
	Container contentPane = getContentPane();
	contentPane.setLayout(new BorderLayout());

	//======== dialogPane ========
	{
		dialogPane.setBorder(Borders.DIALOG_BORDER);
		dialogPane.setLayout(new BorderLayout());

		//======== buttonBar ========
		{
			buttonBar.setBorder(Borders.BUTTON_BAR_GAP_BORDER);
			buttonBar.setCyclic(true);
			buttonBar.setLayout(new FormLayout(
				new ColumnSpec[] {
					FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
					FormFactory.DEFAULT_COLSPEC,
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

		//======== contentPanel2 ========
		{
			contentPanel2.setLayout(new FormLayout(
				new ColumnSpec[] {
					FormFactory.DEFAULT_COLSPEC,
					FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
					new ColumnSpec("max(pref;70dlu):grow"),
					FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
					FormFactory.DEFAULT_COLSPEC
				},
				new RowSpec[] {
					FormFactory.DEFAULT_ROWSPEC,
					FormFactory.LINE_GAP_ROWSPEC,
					new RowSpec("max(min;15dlu)")
				}));

			//---- labelSelectConnection ----
			labelSelectConnection.setText(bundle.getString("labelSelectConnection.text"));
			labelSelectConnection.setLabelFor(comboConnections);
			contentPanel2.add(labelSelectConnection, cc.xy(1, 1));
			contentPanel2.add(comboConnections, cc.xy(3, 1));

			//---- btnEditConnection ----
			btnEditConnection.setText(bundle.getString("btnEditConnection.text"));
			contentPanel2.add(btnEditConnection, cc.xy(5, 1));

			//---- labelDescription ----
			labelDescription.setText(bundle.getString("labelDescription.text"));
			contentPanel2.add(labelDescription, cc.xywh(3, 3, 3, 1, CellConstraints.DEFAULT, CellConstraints.TOP));
		}
		dialogPane.add(contentPanel2, BorderLayout.NORTH);
	}
	contentPane.add(dialogPane, BorderLayout.CENTER);
	pack();
	setLocationRelativeTo(getOwner());

	// Generated using JFormDesigner Open Source Project license - unknown
	private JPanel dialogPane;
	private JButton btnHelp;
	private JButton btnOK;
	private JButton btnCancel;
	private JComboBox comboConnections;
	private JButton btnEditConnection;
	private JLabel labelDescription;

	private void initComponents() {
	}
	// Generated using JFormDesigner Open Source Project license - unknown
	ResourceBundle bundle = ResourceBundle.getBundle("ChooseConnectionDialog");
	dialogPane = new JPanel();
	JXButtonPanel buttonBar = new JXButtonPanel();
	btnHelp = new JButton();
	btnOK = new JButton();
	btnCancel = new JButton();
	JPanel contentPanel = new JPanel();
	JLabel labelSelectConnection = new JLabel();
	comboConnections = new JComboBox();
	btnEditConnection = new JButton();
	labelDescription = new JLabel();
	CellConstraints cc = new CellConstraints();

	//======== this ========
	Container contentPane = getContentPane();
	contentPane.setLayout(new BorderLayout());

	//======== dialogPane ========
	{
		dialogPane.setBorder(Borders.DIALOG_BORDER);
		dialogPane.setLayout(new BorderLayout());

		//======== buttonBar ========
		{
			buttonBar.setBorder(Borders.BUTTON_BAR_GAP_BORDER);
			buttonBar.setCyclic(true);
			buttonBar.setLayout(new FormLayout(
				new ColumnSpec[] {
					FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
					FormFactory.DEFAULT_COLSPEC,
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

		//======== contentPanel ========
		{
			contentPanel.setLayout(new FormLayout(
				new ColumnSpec[] {
					FormFactory.DEFAULT_COLSPEC,
					FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
					new ColumnSpec("max(pref;70dlu):grow"),
					FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
					FormFactory.DEFAULT_COLSPEC
				},
				new RowSpec[] {
					FormFactory.DEFAULT_ROWSPEC,
					FormFactory.LINE_GAP_ROWSPEC,
					new RowSpec("max(min;15dlu)")
				}));

			//---- labelSelectConnection ----
			labelSelectConnection.setText(bundle.getString("labelSelectConnection.text"));
			labelSelectConnection.setLabelFor(comboConnections);
			contentPanel.add(labelSelectConnection, cc.xy(1, 1));
			contentPanel.add(comboConnections, cc.xy(3, 1));

			//---- btnEditConnection ----
			btnEditConnection.setText(bundle.getString("btnEditConnection.text"));
			contentPanel.add(btnEditConnection, cc.xy(5, 1));

			//---- labelDescription ----
			labelDescription.setText(bundle.getString("labelDescription.text"));
			contentPanel.add(labelDescription, cc.xywh(3, 3, 3, 1, CellConstraints.DEFAULT, CellConstraints.TOP));
		}
		dialogPane.add(contentPanel, BorderLayout.NORTH);
	}
	contentPane.add(dialogPane, BorderLayout.CENTER);
	pack();
	setLocationRelativeTo(getOwner());

	// Generated using JFormDesigner Open Source Project license - unknown
	private JPanel dialogPane;
	private JButton btnHelp;
	private JButton btnOK;
	private JButton btnCancel;
	private JComboBox comboConnections;
	private JButton btnEditConnection;
	private JLabel labelDescription;

	private void initComponents() {
	}
	// Generated using JFormDesigner Open Source Project license - unknown
	ResourceBundle bundle = ResourceBundle.getBundle("ChooseConnectionDialog");
	dialogPane = new JPanel();
	JXButtonPanel buttonBar = new JXButtonPanel();
	btnHelp = new JButton();
	btnOK = new JButton();
	btnCancel = new JButton();
	JPanel contentPanel = new JPanel();
	JLabel labelSelectConnection = new JLabel();
	comboConnections = new JComboBox();
	btnEditConnection = new JButton();
	labelDescription = new JLabel();
	CellConstraints cc = new CellConstraints();

	//======== this ========
	Container contentPane = getContentPane();
	contentPane.setLayout(new BorderLayout());

	//======== dialogPane ========
	{
		dialogPane.setBorder(Borders.DIALOG_BORDER);
		dialogPane.setLayout(new BorderLayout());

		//======== buttonBar ========
		{
			buttonBar.setBorder(Borders.BUTTON_BAR_GAP_BORDER);
			buttonBar.setCyclic(true);
			buttonBar.setLayout(new FormLayout(
				new ColumnSpec[] {
					FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
					FormFactory.DEFAULT_COLSPEC,
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

		//======== contentPanel ========
		{
			contentPanel.setLayout(new FormLayout(
				new ColumnSpec[] {
					FormFactory.DEFAULT_COLSPEC,
					FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
					new ColumnSpec("max(pref;70dlu):grow"),
					FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
					FormFactory.DEFAULT_COLSPEC
				},
				new RowSpec[] {
					FormFactory.DEFAULT_ROWSPEC,
					FormFactory.LINE_GAP_ROWSPEC,
					new RowSpec("max(min;15dlu)")
				}));

			//---- labelSelectConnection ----
			labelSelectConnection.setText(bundle.getString("labelSelectConnection.text"));
			labelSelectConnection.setLabelFor(comboConnections);
			contentPanel.add(labelSelectConnection, cc.xy(1, 1));
			contentPanel.add(comboConnections, cc.xy(3, 1));

			//---- btnEditConnection ----
			btnEditConnection.setText(bundle.getString("btnEditConnection.text"));
			contentPanel.add(btnEditConnection, cc.xy(5, 1));

			//---- labelDescription ----
			labelDescription.setText(bundle.getString("labelDescription.text"));
			contentPanel.add(labelDescription, cc.xywh(3, 3, 3, 1, CellConstraints.DEFAULT, CellConstraints.TOP));
		}
		dialogPane.add(contentPanel, BorderLayout.NORTH);
	}
	contentPane.add(dialogPane, BorderLayout.CENTER);
	pack();
	setLocationRelativeTo(getOwner());

	// Generated using JFormDesigner Open Source Project license - unknown
	private JPanel dialogPane;
	private JButton btnHelp;
	private JButton btnOK;
	private JButton btnCancel;
	private JComboBox comboConnections;
	private JButton btnEditConnection;
	private JLabel labelDescription;
}
