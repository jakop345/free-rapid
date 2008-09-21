import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import com.jgoodies.forms.builder.*;
import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;
import org.jdesktop.swinghelper.buttonpanel.*;


public class NewLinksDialog extends JDialog {
	public NewLinksDialog(Frame owner) {
		super(owner);
		initComponents();
	}

	public NewLinksDialog(Dialog owner) {
		super(owner);
		initComponents();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
 
		ResourceBundle bundle = ResourceBundle.getBundle("NewLinksDialog");
		JPanel dialogPane = new JPanel();
		JPanel contentPanel = new JPanel();
		JLabel labelLinks = new JLabel();
		JScrollPane scrollPane1 = new JScrollPane();
		JEditorPane urlsArea = ComponentFactory.getURLsEditorPane();
		randomizeBox = new JCheckBox();
		JLabel labelSaveTo = new JLabel();
		comboPath = new JComboBox();
		btnSelectPath = new JButton();
		JLabel labelDescription = new JLabel();
		JScrollPane scrollPane2 = new JScrollPane();
		descriptionArea = ComponentFactory.getTextArea();
		JXButtonPanel buttonBar = new JXButtonPanel();
		btnPasteFromClipboard = new JButton();
		okButton = new JButton();
		btnStartPaused = new JButton();
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

				//---- labelLinks ----
				labelLinks.setText(bundle.getString("labelLinks.text"));
				labelLinks.setLabelFor(urlsArea);

				//======== scrollPane1 ========
				{

						//---- urlsArea ----
						urlsArea.setMinimumSize(new Dimension(24, 24));
						scrollPane1.setViewportView(urlsArea);
				}

				//---- randomizeBox ----
				randomizeBox.setText(bundle.getString("randomizeBox.text"));

				//---- labelSaveTo ----
				labelSaveTo.setText(bundle.getString("labelSaveTo.text"));
				labelSaveTo.setLabelFor(comboPath);

				//---- comboPath ----
				comboPath.setEditable(true);

				//---- btnSelectPath ----
				btnSelectPath.setText(bundle.getString("btnSelectPath.text"));

				//---- labelDescription ----
				labelDescription.setText(bundle.getString("labelDescription.text"));
				labelDescription.setLabelFor(descriptionArea);

				//======== scrollPane2 ========
				{
					scrollPane2.setViewportView(descriptionArea);
				}

				PanelBuilder contentPanelBuilder = new PanelBuilder(new FormLayout(
						"default, $lcgap, pref:grow, $lcgap, min",
						"default, $lgap, fill:pref:grow, 2*($lgap, default), $lgap, fill:[40dlu,pref,55dlu]:grow"), contentPanel);

				contentPanelBuilder.add(labelLinks,       cc.xy  (1, 1));
				contentPanelBuilder.add(scrollPane1,      cc.xywh(1, 3, 5, 1));
				contentPanelBuilder.add(randomizeBox,     cc.xy  (5, 5));
				contentPanelBuilder.add(labelSaveTo,      cc.xy  (1, 7));
				contentPanelBuilder.add(comboPath,        cc.xy  (3, 7));
				contentPanelBuilder.add(btnSelectPath,    cc.xy  (5, 7));
				contentPanelBuilder.add(labelDescription, cc.xy  (1, 9));
			contentPanelBuilder.add(scrollPane2,      cc.xywh(3, 9, 3, 1));
			}
			dialogPane.add(contentPanel, BorderLayout.CENTER);

			//======== buttonBar ========
			{
				buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));

				//---- btnPasteFromClipboard ----
				btnPasteFromClipboard.setText(bundle.getString("btnPasteFromClipboard.text"));

				//---- okButton ----
				okButton.setText(bundle.getString("okButton.text"));

				//---- btnStartPaused ----
				btnStartPaused.setText(bundle.getString("btnStartPaused.text"));

				//---- cancelButton ----
				cancelButton.setText(bundle.getString("cancelButton.text"));

				PanelBuilder buttonBarBuilder = new PanelBuilder(new FormLayout(
						"pref, $lcgap, default:grow, $ugap, 2*(pref, $lcgap), default",
						"fill:pref"), buttonBar);
				((FormLayout)buttonBar.getLayout()).setColumnGroups(new int[][] {{5, 7, 9}});

				buttonBarBuilder.add(btnPasteFromClipboard, cc.xy(1, 1));
				buttonBarBuilder.add(okButton,              cc.xy(5, 1));
				buttonBarBuilder.add(btnStartPaused,        cc.xy(7, 1));
				buttonBarBuilder.add(cancelButton,          cc.xy(9, 1));
			}
			dialogPane.add(buttonBar, BorderLayout.SOUTH);
		}
		contentPane.add(dialogPane, BorderLayout.CENTER);
		pack();
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables

		private JCheckBox randomizeBox;
		private JComboBox comboPath;
		private JButton btnSelectPath;
		private JTextArea descriptionArea;
		private JButton btnPasteFromClipboard;
		private JButton okButton;
		private JButton btnStartPaused;
		private JButton cancelButton;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
