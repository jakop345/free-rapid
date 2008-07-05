import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.tree.*;
import com.jgoodies.forms.builder.*;
import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;
import org.jdesktop.swinghelper.buttonpanel.*;


public class ShowLogDialog extends JDialog {
	public ShowLogDialog(Frame owner) {
		super(owner);
		initComponents();
	}

	public ShowLogDialog(Dialog owner) {
		super(owner);
		initComponents();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		// Generated using JFormDesigner Open Source Project license - unknown
		ResourceBundle bundle = ResourceBundle.getBundle("ShowLogDialog");
		JPanel dialogPane = new JPanel();
		JPanel contentPanel = new JPanel();
		JSplitPane splitPane = new JSplitPane();
		JScrollPane scrollPane2 = new JScrollPane();
		errorTree = new JTree();
		JScrollPane scrollPane = new JScrollPane();
		textArea = ComponentFactory.getSQLArea();
		JXButtonPanel buttonBar = new JXButtonPanel();
		btnCopyToClipboard = new JButton();
		btnSaveLog = new JButton();
		btnOK = new JButton();
		JPanel toolbarPanel = new JPanel();
		btnPrevError = new JButton();
		btnNextError = new JButton();
		separator1 = new JSeparator();
		btnOraErrorCode = new JButton();
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
				contentPanel.setPreferredSize(new Dimension(530, 426));
				contentPanel.setLayout(new BorderLayout());

				//======== splitPane ========
				{
					splitPane.setOneTouchExpandable(true);
					splitPane.setResizeWeight(0.5);

					//======== scrollPane2 ========
					{
						scrollPane2.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

						//---- errorTree ----
						errorTree.setModel(new DefaultTreeModel(
							new DefaultMutableTreeNode("(root)") {
								{
									add(new DefaultMutableTreeNode("sdfsdf"));
									add(new DefaultMutableTreeNode("sdfs"));
									add(new DefaultMutableTreeNode("sdf"));
									add(new DefaultMutableTreeNode("sdf"));
									add(new DefaultMutableTreeNode("sd"));
									add(new DefaultMutableTreeNode("sdfs"));
									add(new DefaultMutableTreeNode("dfs"));
									add(new DefaultMutableTreeNode("df"));
									add(new DefaultMutableTreeNode("sdf"));
									add(new DefaultMutableTreeNode("sd"));
									add(new DefaultMutableTreeNode("fs"));
									add(new DefaultMutableTreeNode("dfs"));
									add(new DefaultMutableTreeNode("df"));
									add(new DefaultMutableTreeNode("sdf"));
									add(new DefaultMutableTreeNode("sdf"));
									add(new DefaultMutableTreeNode("s"));
									add(new DefaultMutableTreeNode("dfs"));
									add(new DefaultMutableTreeNode("df"));
									add(new DefaultMutableTreeNode("sdf"));
									add(new DefaultMutableTreeNode("s"));
									add(new DefaultMutableTreeNode("df"));
									add(new DefaultMutableTreeNode("sdf"));
									add(new DefaultMutableTreeNode("sd"));
									add(new DefaultMutableTreeNode("fs"));
									add(new DefaultMutableTreeNode("df"));
									add(new DefaultMutableTreeNode("sd"));
									add(new DefaultMutableTreeNode("fs"));
									add(new DefaultMutableTreeNode("df"));
									add(new DefaultMutableTreeNode("sdf"));
									add(new DefaultMutableTreeNode("s"));
									add(new DefaultMutableTreeNode("df"));
								}
							}));
						scrollPane2.setViewportView(errorTree);
					}
					splitPane.setLeftComponent(scrollPane2);

					//======== scrollPane ========
					{
						scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

						//---- textArea ----
						textArea.setEditable(false);
						scrollPane.setViewportView(textArea);
					}
					splitPane.setRightComponent(scrollPane);
				}
				contentPanel.add(splitPane, BorderLayout.CENTER);
			}
			dialogPane.add(contentPanel, BorderLayout.CENTER);

			//======== buttonBar ========
			{
				buttonBar.setBorder(Borders.BUTTON_BAR_GAP_BORDER);

				//---- btnCopyToClipboard ----
				btnCopyToClipboard.setText(bundle.getString("btnCopyToClipboard.text"));

				//---- btnSaveLog ----
				btnSaveLog.setText(bundle.getString("btnSaveLog.text"));

				//---- btnOK ----
				btnOK.setText(bundle.getString("btnOK.text"));

				PanelBuilder buttonBarBuilder = new PanelBuilder(new FormLayout(
					new ColumnSpec[] {
						FormFactory.BUTTON_COLSPEC,
						FormFactory.RELATED_GAP_COLSPEC,
						FormFactory.BUTTON_COLSPEC,
						FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
						new ColumnSpec(ColumnSpec.FILL, Sizes.PREFERRED, FormSpec.DEFAULT_GROW),
						FormFactory.BUTTON_COLSPEC
					},
					RowSpec.decodeSpecs("pref")), buttonBar);

				buttonBarBuilder.add(btnCopyToClipboard, cc.xy(1, 1));
				buttonBarBuilder.add(btnSaveLog,         cc.xy(3, 1));
				buttonBarBuilder.add(btnOK,              cc.xy(6, 1));
			}
			dialogPane.add(buttonBar, BorderLayout.SOUTH);

			//======== toolbarPanel ========
			{
				toolbarPanel.setBorder(new EmptyBorder(0, 5, 3, 5));

				//---- btnPrevError ----
				btnPrevError.setPreferredSize(new Dimension(26, 23));
				btnPrevError.setName(bundle.getString("btnPrevError.name"));

				//---- btnNextError ----
				btnNextError.setPreferredSize(new Dimension(26, 23));
				btnNextError.setName(bundle.getString("btnNextError.name"));

				//---- btnOraErrorCode ----
				btnOraErrorCode.setPreferredSize(new Dimension(26, 23));
				btnOraErrorCode.setName(bundle.getString("btnOraErrorCode.name"));

				PanelBuilder toolbarPanelBuilder = new PanelBuilder(new FormLayout(
					new ColumnSpec[] {
						FormFactory.DEFAULT_COLSPEC,
						FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
						FormFactory.DEFAULT_COLSPEC,
						FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
						FormFactory.DEFAULT_COLSPEC,
						FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
						FormFactory.DEFAULT_COLSPEC
					},
					RowSpec.decodeSpecs("default")), toolbarPanel);

				toolbarPanelBuilder.add(btnPrevError,    cc.xy(1, 1));
				toolbarPanelBuilder.add(btnNextError,    cc.xy(3, 1));
				toolbarPanelBuilder.add(separator1,      cc.xy(5, 1));
				toolbarPanelBuilder.add(btnOraErrorCode, cc.xy(7, 1));
			}
			dialogPane.add(toolbarPanel, BorderLayout.NORTH);
		}
		contentPane.add(dialogPane, BorderLayout.CENTER);
		pack();
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	// Generated using JFormDesigner Open Source Project license - unknown
	private JTree errorTree;
	private JEditorPane textArea;
	private JButton btnCopyToClipboard;
	private JButton btnSaveLog;
	private JButton btnOK;
	private JButton btnPrevError;
	private JButton btnNextError;
	private JSeparator separator1;
	private JButton btnOraErrorCode;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
