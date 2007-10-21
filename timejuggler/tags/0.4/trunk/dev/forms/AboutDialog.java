import java.awt.*;
import java.util.*;
import javax.swing.*;
import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;
import org.jdesktop.swingx.*;
/*
 * Created by JFormDesigner on Sat Aug 04 18:30:19 CEST 2007
 */



/**
 * @author Vity
 */
public class AboutDialog {
	public AboutDialog(Frame owner) {
		super(owner);
		initComponents();
	}

	public AboutDialog(Dialog owner) {
		super(owner);
		initComponents();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		// Generated using JFormDesigner Open Source Project license - unknown
		ResourceBundle bundle = ResourceBundle.getBun;
		AboutDialog = new JDialog();
		JPanel dialogPane = new JPanel();
		JPanel contentPanel = new JPanel();
		infoLabel = new JLabel();
		JPanel buttonBar = new JPanel();
		btnOK = new JButton();
		CellConstraints cc = new CellConstraints();

		//======== AboutDialog ========
		{
			AboutDialog.setName("AboutDialog");
			Container AboutDialogContentPane = AboutDialog.getContentPane();
			AboutDialogContentPane.setLayout(new BorderLayout());

			//======== dialogPane ========
			{
				dialogPane.setBorder(Borders.DIALOG_BORDER);
				dialogPane.setName("dialogPane");
				dialogPane.setLayout(new BorderLayout());

				//======== contentPanel ========
				{
					contentPanel.setName("contentPanel");
					contentPanel.setLayout(new BorderLayout());

					//---- infoLabel ----
					infoLabel.setName("infoLabel");
					contentPanel.add(infoLabel, BorderLayout.CENTER);
				}
				dialogPane.add(contentPanel, BorderLayout.CENTER);

				//======== buttonBar ========
				{
					buttonBar.setBorder(Borders.BUTTON_BAR_GAP_BORDER);
					buttonBar.setName("buttonBar");

					//---- btnOK ----
					btnOK.setText(bundle.getString("okButton.text"));
					btnOK.setName("btnOK");

					PanelBuilder buttonBarBuilder = new PanelBuilder(new FormLayout(
						"default:grow, max(pref;50dlu), default:grow",
						"pref"), buttonBar);

					buttonBarBuilder.add(btnOK, cc.xy(2, 1));
				}
				dialogPane.add(buttonBar, BorderLayout.SOUTH);
			}
			AboutDialogContentPane.add(dialogPane, BorderLayout.CENTER);
			AboutDialog.pack();
			AboutDialog.setLocationRelativeTo(AboutDialog.getOwner());
		}
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	// Generated using JFormDesigner Open Source Project license - unknown
	private JDialog AboutDialog;
	private JLabel infoLabel;
	private JButton btnOK;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
