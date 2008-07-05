import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import com.jgoodies.forms.builder.*;
import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;


public class FindBar {
	public FindBar() {
		initComponents();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		// Generated using JFormDesigner Open Source Project license - unknown
		ResourceBundle bundle = ResourceBundle.getBundle("FindBar");
		findBar = new JPanel();
		searchField = ComponentFactory.getTextField();
		findPrevious = ComponentFactory.getToolbarButton();
		findNext = ComponentFactory.getToolbarButton();
		matchCheck = new JCheckBox();
		CellConstraints cc = new CellConstraints();

		//======== findBar ========
		{
			findBar.setBorder(new EmptyBorder(0, 3, 0, 0));
			findBar.setName("findBar");

			//---- searchField ----
			searchField.setPreferredSize(new Dimension(100, 21));
			searchField.setName("searchField");

			//---- findPrevious ----
			findPrevious.setName("findPrevious");
			findPrevious.setToolTipText(bundle.getString("findPrevious.toolTipText"));

			//---- findNext ----
			findNext.setName("findNext");
			findNext.setToolTipText(bundle.getString("findNext.toolTipText"));

			//---- matchCheck ----
			matchCheck.setText(bundle.getString("matchCheck.text"));

			PanelBuilder findBarBuilder = new PanelBuilder(new FormLayout(
				new ColumnSpec[] {
					new ColumnSpec("max(pref;60dlu):grow"),
					FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
					FormFactory.PREF_COLSPEC,
					FormFactory.DEFAULT_COLSPEC,
					FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
					FormFactory.DEFAULT_COLSPEC
				},
				RowSpec.decodeSpecs("pref")), findBar);
			((FormLayout)findBar.getLayout()).setColumnGroups(new int[][] {{3, 4}});

			findBarBuilder.add(searchField,  cc.xy(1, 1));
			findBarBuilder.add(findPrevious, cc.xy(3, 1));
			findBarBuilder.add(findNext,     cc.xy(4, 1));
			findBarBuilder.add(matchCheck,   cc.xy(6, 1));
		}
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	// Generated using JFormDesigner Open Source Project license - unknown
	private JPanel findBar;
	private JTextField searchField;
	private JButton findPrevious;
	private JButton findNext;
	private JCheckBox matchCheck;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
