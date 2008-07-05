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
		btnHelp = new JButton();
		btnCopyToClipboard = new JButton();
		btnSaveLog = new JButton();
		btnOK = new JButton();
		JPanel toolbarPanel = new JPanel();
		btnPrevError = ComponentFactory.getToolbarButton();
		btnNextError = ComponentFactory.getToolbarButton();
		separator1 = new JSeparator();
		btnOraErrorCode = ComponentFactory.getToolbarButton();
		separator2 = new JSeparator();
		btnSearch = ComponentFactory.getToolbarToggleButton();
		findBar = ComponentFactory.getToolbarFindBar(textArea);
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
					splitPane.setResizeWeight(0.01);
					splitPane.setPreferredSize(new Dimension(650, 400));
					splitPane.setName("splitPane");

					//======== scrollPane2 ========
					{
						scrollPane2.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
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

				//---- btnHelp ----
				btnHelp.setText(bundle.getString("btnHelp.text"));

				//---- btnCopyToClipboard ----
				btnCopyToClipboard.setText(bundle.getString("btnCopyToClipboard.text"));

				//---- btnSaveLog ----
				btnSaveLog.setText(bundle.getString("btnSaveLog.text"));

				//---- btnOK ----
				btnOK.setText(bundle.getString("btnOK.text"));

				PanelBuilder buttonBarBuilder = new PanelBuilder(new FormLayout(
					new ColumnSpec[] {
						FormFactory.BUTTON_COLSPEC,
						FormFactory.UNRELATED_GAP_COLSPEC,
						FormFactory.BUTTON_COLSPEC,
						FormFactory.RELATED_GAP_COLSPEC,
						FormFactory.BUTTON_COLSPEC,
						FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
						new ColumnSpec(ColumnSpec.FILL, Sizes.PREFERRED, FormSpec.DEFAULT_GROW),
						FormFactory.BUTTON_COLSPEC
					},
					RowSpec.decodeSpecs("pref")), buttonBar);

				buttonBarBuilder.add(btnHelp,            cc.xy(1, 1));
				buttonBarBuilder.add(btnCopyToClipboard, cc.xy(3, 1));
				buttonBarBuilder.add(btnSaveLog,         cc.xy(5, 1));
				buttonBarBuilder.add(btnOK,              cc.xy(8, 1));
			}
			dialogPane.add(buttonBar, BorderLayout.SOUTH);

			//======== toolbarPanel ========
			{
				toolbarPanel.setBorder(new EmptyBorder(0, 5, 3, 5));

				//---- btnPrevError ----
				btnPrevError.setPreferredSize(new Dimension(26, 23));
				btnPrevError.setName("btnPrevError");

				//---- btnNextError ----
				btnNextError.setPreferredSize(new Dimension(26, 23));
				btnNextError.setName("btnNextError");

				//---- btnOraErrorCode ----
				btnOraErrorCode.setPreferredSize(new Dimension(26, 23));
				btnOraErrorCode.setName("btnOraErrorCode");

				//---- btnSearch ----
				btnSearch.setPreferredSize(new Dimension(26, 23));
				btnSearch.setName("btnOraErrorCode");

				//======== findBar ========
				{
					findBar.setLayout(null);
				}

				PanelBuilder toolbarPanelBuilder = new PanelBuilder(new FormLayout(
					new ColumnSpec[] {
						FormFactory.DEFAULT_COLSPEC,
						FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
						FormFactory.DEFAULT_COLSPEC,
						FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
						FormFactory.DEFAULT_COLSPEC,
						FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
						FormFactory.DEFAULT_COLSPEC,
						FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
						FormFactory.DEFAULT_COLSPEC,
						FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
						FormFactory.DEFAULT_COLSPEC,
						FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
						FormFactory.DEFAULT_COLSPEC
					},
					RowSpec.decodeSpecs("default")), toolbarPanel);
				((FormLayout)toolbarPanel.getLayout()).setColumnGroups(new int[][] {{1, 3, 7, 11}});

				toolbarPanelBuilder.add(btnPrevError,    cc.xy( 1, 1));
				toolbarPanelBuilder.add(btnNextError,    cc.xy( 3, 1));
				toolbarPanelBuilder.add(separator1,      cc.xy( 5, 1));
				toolbarPanelBuilder.add(btnOraErrorCode, cc.xy( 7, 1));
				toolbarPanelBuilder.add(separator2,      cc.xy( 9, 1));
				toolbarPanelBuilder.add(btnSearch,       cc.xy(11, 1));
				toolbarPanelBuilder.add(findBar,         cc.xy(13, 1));
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
	private JButton btnHelp;
	private JButton btnCopyToClipboard;
	private JButton btnSaveLog;
	private JButton btnOK;
	private JButton btnPrevError;
	private JButton btnNextError;
	private JSeparator separator1;
	private JButton btnOraErrorCode;
	private JSeparator separator2;
	private JToggleButton btnSearch;
	private JPanel findBar;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
