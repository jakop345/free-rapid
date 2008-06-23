import java.awt.*;
import java.util.*;
import javax.swing.*;
import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;
import org.jdesktop.swinghelper.buttonpanel.*;
/*
 * Created by JFormDesigner on Mon Apr 21 22:33:57 CEST 2008
 */



/**
 * @author SHOCKIE
 */
public class CloseDialog  {

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		// Generated using JFormDesigner Open Source Project license - unknown
		ResourceBundle bundle = ResourceBundle.getBundle("CloseDialog");
		CloseDialog = new JDialog();
		JPanel dialogPane = new JPanel();
		JPanel contentPanel = new JPanel();
		JScrollPane scrollPane1 = new JScrollPane();
		list = new JList();
		JCheckBox checkSort = new JCheckBox();
		JXButtonPanel buttonBar = new JXButtonPanel();
		JButton btnSelectAll = new JButton();
		JButton btnSelectNone = new JButton();
		btnOk = new JButton();
		btnCancel = new JButton();
		CellConstraints cc = new CellConstraints();

		//======== CloseDialog ========
		{
			CloseDialog.setTitle(bundle.getString("CloseDialog.title"));
			Container CloseDialogContentPane = CloseDialog.getContentPane();
			CloseDialogContentPane.setLayout(new BorderLayout());

			//======== dialogPane ========
			{
				dialogPane.setBorder(Borders.DIALOG_BORDER);
				dialogPane.setLayout(new BorderLayout());

				//======== contentPanel ========
				{
					contentPanel.setLayout(new FormLayout(
						new ColumnSpec[] {
							new ColumnSpec(ColumnSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW),
							FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
							new ColumnSpec(ColumnSpec.FILL, Sizes.dluX(121), FormSpec.DEFAULT_GROW)
						},
						new RowSpec[] {
							new RowSpec(RowSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW),
							FormFactory.LINE_GAP_ROWSPEC,
							FormFactory.DEFAULT_ROWSPEC
						}));

					//======== scrollPane1 ========
					{
						scrollPane1.setViewportView(list);
					}
					contentPanel.add(scrollPane1, cc.xywh(1, 1, 3, 1));

					//---- checkSort ----
					checkSort.setText(bundle.getString("checkSort.text"));
					contentPanel.add(checkSort, cc.xywh(1, 3, 3, 1));
				}
				dialogPane.add(contentPanel, BorderLayout.CENTER);

				//======== buttonBar ========
				{
					buttonBar.setBorder(Borders.BUTTON_BAR_GAP_BORDER);
					buttonBar.setLayout(new FormLayout(
						new ColumnSpec[] {
							FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
							FormFactory.DEFAULT_COLSPEC,
							FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
							FormFactory.DEFAULT_COLSPEC,
							new ColumnSpec("max(min;30dlu):grow"),
							FormFactory.BUTTON_COLSPEC,
							FormFactory.RELATED_GAP_COLSPEC,
							FormFactory.BUTTON_COLSPEC
						},
						RowSpec.decodeSpecs("pref")));
					((FormLayout)buttonBar.getLayout()).setColumnGroups(new int[][] {{2, 4}, {6, 8}});

					//---- btnSelectAll ----
					btnSelectAll.setText(bundle.getString("btnSelectAll.text"));
					buttonBar.add(btnSelectAll, cc.xy(2, 1));

					//---- btnSelectNone ----
					btnSelectNone.setText(bundle.getString("btnSelectNone.text"));
					buttonBar.add(btnSelectNone, cc.xy(4, 1));

					//---- btnOk ----
					btnOk.setText(bundle.getString("btnOk.text"));
					buttonBar.add(btnOk, cc.xy(6, 1));

					//---- btnCancel ----
					btnCancel.setText(bundle.getString("btnCancel.text"));
					buttonBar.add(btnCancel, cc.xy(8, 1));
				}
				dialogPane.add(buttonBar, BorderLayout.SOUTH);
			}
			CloseDialogContentPane.add(dialogPane, BorderLayout.CENTER);
			CloseDialog.pack();
			CloseDialog.setLocationRelativeTo(CloseDialog.getOwner());
		}
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	// Generated using JFormDesigner Open Source Project license - unknown
	private JDialog CloseDialog;
	private JList list;
	private JButton btnOk;
	private JButton btnCancel;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
