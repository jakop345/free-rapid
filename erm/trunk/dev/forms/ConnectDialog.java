import java.awt.*;
import java.util.*;
import javax.swing.*;
import com.jgoodies.forms.builder.*;
import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;
/*
 * Created by JFormDesigner on Mon Sep 24 21:04:26 CEST 2007
 */



/**
 * @author Ladislav Vitasek
 */
public class ConnectDialog extends JDialog {
	public ConnectDialog(Frame owner) {
		super(owner);
		initComponents();
	}

	public ConnectDialog(Dialog owner) {
		super(owner);
		initComponents();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		// Generated using JFormDesigner Open Source Project license - unknown
		ResourceBundle bundle = ResourceBundle.getBun;
		dialogPane = new JPanel();
		JPanel contentPanel = new JPanel();
		JLabel labelHostName = new JLabel();
		fieldHostName = new JTextField();
		JLabel labelPortNumber = new JLabel();
		fieldPort = new JTextField();
		checkAuthentification = new JCheckBox();
		labelLoginName = new JLabel();
		fieldUserName = new JTextField();
		labelPassword = new JLabel();
		fieldPassword = new JPasswordField();
		checkStorePassword = new JCheckBox();
		labelWarning = new JLabel();
		JPanel buttonBar = new JPanel();
		btnOk = new JButton();
		btnCancel = new JButton();
		CellConstraints cc = new CellConstraints();

		//======== this ========
		setTitle(bundle.getString("this.title"));
		setName("this");
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

		//======== dialogPane ========
		{
			dialogPane.setBorder(Borders.DIALOG_BORDER);
			dialogPane.setName("dialogPane");
			dialogPane.setLayout(new BorderLayout());

			//======== contentPanel ========
			{
				contentPanel.setName("contentPanel");

				//---- labelHostName ----
				labelHostName.setText(bundle.getString("labelHostName.text"));
				labelHostName.setLabelFor(fieldHostName);
				labelHostName.setName("labelHostName");

				//---- fieldHostName ----
				fieldHostName.setColumns(8);
				fieldHostName.setName("fieldHostName");

				//---- labelPortNumber ----
				labelPortNumber.setText(bundle.getString("labelPortNumber.text"));
				labelPortNumber.setLabelFor(fieldPort);
				labelPortNumber.setName("labelPortNumber");

				//---- fieldPort ----
				fieldPort.setColumns(6);
				fieldPort.setName("fieldPort");

				//---- checkAuthentification ----
				checkAuthentification.setText(bundle.getString("checkAuthentification.text"));
				checkAuthentification.setName("checkAuthentification");

				//---- labelLoginName ----
				labelLoginName.setText(bundle.getString("labelLoginName.text"));
				labelLoginName.setLabelFor(fieldUserName);
				labelLoginName.setName("labelLoginName");

				//---- fieldUserName ----
				fieldUserName.setName("fieldUserName");

				//---- labelPassword ----
				labelPassword.setText(bundle.getString("labelPassword.text"));
				labelPassword.setLabelFor(fieldPassword);
				labelPassword.setName("labelPassword");

				//---- fieldPassword ----
				fieldPassword.setName("fieldPassword");

				//---- checkStorePassword ----
				checkStorePassword.setText(bundle.getString("checkStorePassword.text"));
				checkStorePassword.setName("checkStorePassword");

				//---- labelWarning ----
				labelWarning.setText(bundle.getString("labelWarning.text"));
				labelWarning.setName("labelWarning");

				PanelBuilder contentPanelBuilder = new PanelBuilder(new FormLayout(
					new ColumnSpec[] {
						FormFactory.PREF_COLSPEC,
						FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
						new ColumnSpec(ColumnSpec.FILL, Sizes.PREFERRED, FormSpec.DEFAULT_GROW),
						FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
						FormFactory.DEFAULT_COLSPEC,
						FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
						FormFactory.DEFAULT_COLSPEC
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
						FormFactory.DEFAULT_ROWSPEC
					}), contentPanel);

				contentPanelBuilder.add(labelHostName,         cc.xy  (1, 1));
				contentPanelBuilder.add(fieldHostName,         cc.xy  (3, 1));
				contentPanelBuilder.add(labelPortNumber,       cc.xy  (5, 1));
				contentPanelBuilder.add(fieldPort,             cc.xy  (7, 1));
				contentPanelBuilder.add(checkAuthentification, new CellConstraints(1, 3, 3, 1, CellConstraints.DEFAULT, CellConstraints.DEFAULT, new Insets( 0, 7, 0, 0)));
				contentPanelBuilder.add(labelLoginName,        cc.xy  (1, 5));
				contentPanelBuilder.add(fieldUserName,         cc.xy  (3, 5));
				contentPanelBuilder.add(labelPassword,         cc.xy  (1, 7));
				contentPanelBuilder.add(fieldPassword,         cc.xy  (3, 7));
				contentPanelBuilder.add(checkStorePassword,    cc.xywh(5, 7, 3, 1));
				contentPanelBuilder.add(labelWarning,          cc.xywh(1, 9, 7, 1));
			}
			dialogPane.add(contentPanel, BorderLayout.CENTER);

			//======== buttonBar ========
			{
				buttonBar.setBorder(Borders.BUTTON_BAR_GAP_BORDER);
				buttonBar.setName("buttonBar");

				//---- btnOk ----
				btnOk.setText(bundle.getString("btnOk.text"));
				btnOk.setName("btnOk");

				//---- btnCancel ----
				btnCancel.setText(bundle.getString("btnCancel.text"));
				btnCancel.setName("btnCancel");

				PanelBuilder buttonBarBuilder = new PanelBuilder(new FormLayout(
					new ColumnSpec[] {
						FormFactory.GLUE_COLSPEC,
						FormFactory.BUTTON_COLSPEC,
						FormFactory.RELATED_GAP_COLSPEC,
						FormFactory.BUTTON_COLSPEC
					},
					RowSpec.decodeSpecs("pref")), buttonBar);
				((FormLayout)buttonBar.getLayout()).setColumnGroups(new int[][] {{2, 4}});

				buttonBarBuilder.add(btnOk,     cc.xy(2, 1));
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
	private JPanel dialogPane;
	private JTextField fieldHostName;
	private JTextField fieldPort;
	private JCheckBox checkAuthentification;
	private JLabel labelLoginName;
	private JTextField fieldUserName;
	private JLabel labelPassword;
	private JPasswordField fieldPassword;
	private JCheckBox checkStorePassword;
	private JLabel labelWarning;
	private JButton btnOk;
	private JButton btnCancel;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
