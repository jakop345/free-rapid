import java.awt.*;
import java.util.*;
import javax.swing.*;
import com.jgoodies.forms.builder.*;
import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;
/*
 * Created by JFormDesigner on Sun Oct 21 14:57:51 CEST 2007
 */



/**
 * @author Vity
 */
public class mainPanel  {

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		// Generated using JFormDesigner Open Source Project license - unknown
		ResourceBundle bundle = ResourceBundle.getB;
		DefaultComponentFactory compFactory = DefaultComponentFactory.getInstance();
		JPanel mainPanel = new JPanel();
		JComponent title1 = compFactory.createSeparator(bundle.getString("title1.text"));
		fieldGPXFolder = new JTextField();
		btnBrowseGPX = new JButton();
		JComponent title2 = compFactory.createSeparator(bundle.getString("title2.text"));
		fieldKMLFile = new JTextField();
		btnBrowseKML = new JButton();
		panelProcess = new JPanel();
		btnProcess = new JButton();
		CellConstraints cc = new CellConstraints();

		//======== mainPanel ========
		{
			mainPanel.setBorder(Borders.DLU4_BORDER);
			mainPanel.setName("mainPanel");

			//---- title1 ----
			title1.setName("title1");

			//---- fieldGPXFolder ----
			fieldGPXFolder.setColumns(25);
			fieldGPXFolder.setName("fieldGPXFolder");

			//---- btnBrowseGPX ----
			btnBrowseGPX.setText(bundle.getString("btnBrowseGPX.text"));
			btnBrowseGPX.setName("btnBrowseGPX");

			//---- title2 ----
			title2.setName("title2");

			//---- fieldKMLFile ----
			fieldKMLFile.setColumns(25);
			fieldKMLFile.setName("fieldKMLFile");

			//---- btnBrowseKML ----
			btnBrowseKML.setText(bundle.getString("btnBrowseKML.text"));
			btnBrowseKML.setName("btnBrowseKML");

			//======== panelProcess ========
			{
				panelProcess.setName("panelProcess");

				//---- btnProcess ----
				btnProcess.setText(bundle.getString("btnProcess.text"));
				btnProcess.setFont(new Font("Dialog", Font.BOLD, 14));
				btnProcess.setName("btnProcess");

				PanelBuilder panelProcessBuilder = new PanelBuilder(new FormLayout(
					"default",
					"default"), panelProcess);

				panelProcessBuilder.add(btnProcess, cc.xywh(1, 1, 1, 1, CellConstraints.RIGHT, CellConstraints.DEFAULT));
			}

			PanelBuilder mainPanelBuilder = new PanelBuilder(new FormLayout(
				new ColumnSpec[] {
					new ColumnSpec(ColumnSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW),
					FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
					FormFactory.DEFAULT_COLSPEC
				},
				new RowSpec[] {
					FormFactory.DEFAULT_ROWSPEC,
					FormFactory.LINE_GAP_ROWSPEC,
					FormFactory.DEFAULT_ROWSPEC,
					FormFactory.NARROW_LINE_GAP_ROWSPEC,
					FormFactory.DEFAULT_ROWSPEC,
					FormFactory.LINE_GAP_ROWSPEC,
					FormFactory.DEFAULT_ROWSPEC,
					FormFactory.LINE_GAP_ROWSPEC,
					FormFactory.UNRELATED_GAP_ROWSPEC,
					FormFactory.LINE_GAP_ROWSPEC,
					FormFactory.DEFAULT_ROWSPEC,
					FormFactory.LINE_GAP_ROWSPEC,
					FormFactory.DEFAULT_ROWSPEC
				}), mainPanel);

			mainPanelBuilder.add(title1,         cc.xywh(1,  1, 3, 1));
			mainPanelBuilder.add(fieldGPXFolder, cc.xy  (1,  3));
			mainPanelBuilder.add(btnBrowseGPX,   cc.xy  (3,  3));
			mainPanelBuilder.add(title2,         cc.xywh(1,  5, 3, 1));
			mainPanelBuilder.add(fieldKMLFile,   cc.xy  (1,  7));
			mainPanelBuilder.add(btnBrowseKML,   cc.xy  (3,  7));
			mainPanelBuilder.add(panelProcess,   cc.xywh(1, 11, 3, 1, CellConstraints.RIGHT, CellConstraints.DEFAULT));
		}
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	// Generated using JFormDesigner Open Source Project license - unknown
	private JTextField fieldGPXFolder;
	private JButton btnBrowseGPX;
	private JTextField fieldKMLFile;
	private JButton btnBrowseKML;
	private JPanel panelProcess;
	private JButton btnProcess;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
