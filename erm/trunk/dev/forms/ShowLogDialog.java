import java.awt.*;
import java.util.*;
import javax.swing.*;
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
		JScrollPane scrollPane = new JScrollPane();
		textArea = ComponentFactory.getSQLArea();
		JXButtonPanel buttonBar = new JXButtonPanel();
		btnCopyToClipboard = new JButton();
		btnSaveLog = new JButton();
		btnOK = new JButton();
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
				contentPanel.setLayout(new BorderLayout());

				//======== scrollPane ========
				{
					scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

					//---- textArea ----
					textArea.setPreferredSize(new Dimension(450, 400));
					textArea.setEditable(false);
					scrollPane.setViewportView(textArea);
				}
				contentPanel.add(scrollPane, BorderLayout.CENTER);
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
		}
		contentPane.add(dialogPane, BorderLayout.CENTER);
		pack();
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	// Generated using JFormDesigner Open Source Project license - unknown
	private JEditorPane textArea;
	private JButton btnCopyToClipboard;
	private JButton btnSaveLog;
	private JButton btnOK;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
