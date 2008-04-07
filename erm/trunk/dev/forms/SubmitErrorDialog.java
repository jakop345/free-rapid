import java.awt.*;
import java.util.*;
import javax.swing.*;
import com.jgoodies.forms.builder.*;
import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;
/*
 * Created by JFormDesigner on Mon Sep 24 21:38:08 CEST 2007
 */



/**
 * @author Ladislav Vitasek
 */
public class SubmitErrorDialog extends JDialog {
	public SubmitErrorDialog(Frame owner) {
		super(owner);
		initComponents();
	}

	public SubmitErrorDialog(Dialog owner) {
		super(owner);
		initComponents();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		// Generated using JFormDesigner Open Source Project license - unknown
		ResourceBundle bundle = ResourceBundle.getBun;
		dialogPane = new JPanel();
		JPanel contentPanel = new JPanel();
		JLabel labelName = new JLabel();
		fieldName = new JTextField();
		JLabel labelEmail = new JLabel();
		fieldEmail = new JTextField();
		JLabel labelComment = new JLabel();
		JScrollPane scrollPane1 = new JScrollPane();
		commentTextArea = new JTextArea();
		JLabel labelDescribeInfo = new JLabel();
		JPanel buttonBar = new JPanel();
		btnConnection = new JButton();
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

				//---- labelName ----
				labelName.setText(bundle.getString("labelName.text"));
				labelName.setLabelFor(fieldName);
				labelName.setName("labelName");

				//---- fieldName ----
				fieldName.setColumns(15);
				fieldName.setName("fieldName");

				//---- labelEmail ----
				labelEmail.setText(bundle.getString("labelEmail.text"));
				labelEmail.setLabelFor(fieldEmail);
				labelEmail.setName("labelEmail");

				//---- fieldEmail ----
				fieldEmail.setColumns(10);
				fieldEmail.setName("fieldEmail");

				//---- labelComment ----
				labelComment.setText(bundle.getString("labelComment.text"));
				labelComment.setLabelFor(commentTextArea);
				labelComment.setName("labelComment");

				//======== scrollPane1 ========
				{
					scrollPane1.setName("scrollPane1");

					//---- commentTextArea ----
					commentTextArea.setRows(10);
					commentTextArea.setLineWrap(true);
					commentTextArea.setName("commentTextArea");
					scrollPane1.setViewportView(commentTextArea);
				}

				//---- labelDescribeInfo ----
				labelDescribeInfo.setText(bundle.getString("labelDescribeInfo.text"));
				labelDescribeInfo.setName("labelDescribeInfo");

				PanelBuilder contentPanelBuilder = new PanelBuilder(new FormLayout(
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
						new RowSpec(RowSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW),
						FormFactory.LINE_GAP_ROWSPEC,
						FormFactory.DEFAULT_ROWSPEC
					}), contentPanel);

				contentPanelBuilder.add(labelName,         cc.xy  (1, 1));
				contentPanelBuilder.add(fieldName,         cc.xy  (3, 1));
				contentPanelBuilder.add(labelEmail,        cc.xy  (5, 1));
				contentPanelBuilder.add(fieldEmail,        cc.xy  (7, 1));
				contentPanelBuilder.add(labelComment,      cc.xywh(1, 3, 1, 1, CellConstraints.DEFAULT, CellConstraints.TOP));
				contentPanelBuilder.add(scrollPane1,       cc.xywh(3, 3, 5, 1));
				contentPanelBuilder.add(labelDescribeInfo, cc.xywh(3, 5, 5, 1));
			}
			dialogPane.add(contentPanel, BorderLayout.CENTER);

			//======== buttonBar ========
			{
				buttonBar.setBorder(Borders.BUTTON_BAR_GAP_BORDER);
				buttonBar.setName("buttonBar");

				//---- btnConnection ----
				btnConnection.setText(bundle.getString("btnConnection.text"));
				btnConnection.setName("btnConnection");

				//---- btnOk ----
				btnOk.setText(bundle.getString("btnOk.text"));
				btnOk.setName("btnOk");

				//---- btnCancel ----
				btnCancel.setText(bundle.getString("btnCancel.text"));
				btnCancel.setName("btnCancel");

				PanelBuilder buttonBarBuilder = new PanelBuilder(new FormLayout(
					new ColumnSpec[] {
						FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
						FormFactory.DEFAULT_COLSPEC,
						FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
						new ColumnSpec("max(min;10dlu):grow"),
						FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
						FormFactory.BUTTON_COLSPEC,
						FormFactory.RELATED_GAP_COLSPEC,
						new ColumnSpec("max(pref;42dlu)")
					},
					RowSpec.decodeSpecs("pref")), buttonBar);

				buttonBarBuilder.add(btnConnection, cc.xywh(2, 1, 1, 1, CellConstraints.LEFT, CellConstraints.DEFAULT));
				buttonBarBuilder.add(btnOk,         cc.xy  (6, 1));
				buttonBarBuilder.add(btnCancel,     cc.xy  (8, 1));
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
	private JTextField fieldName;
	private JTextField fieldEmail;
	private JTextArea commentTextArea;
	private JButton btnConnection;
	private JButton btnOk;
	private JButton btnCancel;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
