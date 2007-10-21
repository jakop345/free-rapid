import java.awt.*;
import java.util.*;
import javax.swing.*;
import com.jgoodies.forms.builder.*;
import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;
/*
 * Created by JFormDesigner on Thu Aug 02 18:42:28 CEST 2007
 */



/**
 * @author Vity
 */
public class CategoryDialog  {

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		// Generated using JFormDesigner Open Source Project license - unknown
		ResourceBundle bundle = ResourceBundle.getBun;
		CategoryDialog = new JDialog();
		dialogPane = new JPanel();
		contentPanel = new JPanel();
		labelName = new JLabel();
		fieldName = new JTextField();
		checkUseColor = new JCheckBox();
		comboColor = new JComboBox();
		JPanel buttonBar = new JPanel();
		btnOK = new JButton();
		cancelButton = new JButton();
		CellConstraints cc = new CellConstraints();

		//======== CategoryDialog ========
		{
			CategoryDialog.setTitle(bundle.getString("CategoryDialog.title"));
			CategoryDialog.setName("CategoryDialog");
			Container CategoryDialogContentPane = CategoryDialog.getContentPane();
			CategoryDialogContentPane.setLayout(new BorderLayout());

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
					fieldName.setColumns(10);
					fieldName.setName("fieldName");

					//---- checkUseColor ----
					checkUseColor.setText(bundle.getString("checkUseColor.text"));
					checkUseColor.setName("checkUseColor");

					//---- comboColor ----
					comboColor.setName("comboColor");

					PanelBuilder contentPanelBuilder = new PanelBuilder(new FormLayout(
						new ColumnSpec[] {
							FormFactory.DEFAULT_COLSPEC,
							FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
							new ColumnSpec("max(pref;55dlu)")
						},
						new RowSpec[] {
							FormFactory.PREF_ROWSPEC,
							FormFactory.UNRELATED_GAP_ROWSPEC,
							FormFactory.DEFAULT_ROWSPEC,
							FormFactory.LINE_GAP_ROWSPEC,
							FormFactory.DEFAULT_ROWSPEC
						}), contentPanel);

					contentPanelBuilder.add(labelName,     cc.xywh(1, 1, 1, 1, CellConstraints.RIGHT, CellConstraints.DEFAULT));
					contentPanelBuilder.add(fieldName,     cc.xy  (3, 1));
					contentPanelBuilder.add(checkUseColor, cc.xy  (1, 3));
					contentPanelBuilder.add(comboColor,    cc.xy  (3, 3));
				}
				dialogPane.add(contentPanel, BorderLayout.CENTER);

				//======== buttonBar ========
				{
					buttonBar.setBorder(Borders.BUTTON_BAR_GAP_BORDER);
					buttonBar.setName("buttonBar");

					//---- btnOK ----
					btnOK.setText(bundle.getString("btnOK.text"));
					btnOK.setName("btnOK");

					//---- cancelButton ----
					cancelButton.setText(bundle.getString("cancelButton.text"));
					cancelButton.setName("cancelButton");

					PanelBuilder buttonBarBuilder = new PanelBuilder(new FormLayout(
						new ColumnSpec[] {
							new ColumnSpec(ColumnSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW),
							new ColumnSpec("max(pref;42dlu)"),
							FormFactory.RELATED_GAP_COLSPEC,
							FormFactory.PREF_COLSPEC
						},
						RowSpec.decodeSpecs("pref")), buttonBar);
					((FormLayout)buttonBar.getLayout()).setColumnGroups(new int[][] {{2, 4}});

					buttonBarBuilder.add(btnOK,        cc.xy(2, 1));
					buttonBarBuilder.add(cancelButton, cc.xy(4, 1));
				}
				dialogPane.add(buttonBar, BorderLayout.SOUTH);
			}
			CategoryDialogContentPane.add(dialogPane, BorderLayout.CENTER);
			CategoryDialog.pack();
			CategoryDialog.setLocationRelativeTo(CategoryDialog.getOwner());
		}
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	// Generated using JFormDesigner Open Source Project license - unknown
	private JDialog CategoryDialog;
	private JPanel dialogPane;
	private JPanel contentPanel;
	private JLabel labelName;
	private JTextField fieldName;
	private JCheckBox checkUseColor;
	private JComboBox comboColor;
	private JButton btnOK;
	private JButton cancelButton;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
