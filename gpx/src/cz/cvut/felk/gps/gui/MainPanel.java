package cz.cvut.felk.gps.gui;

import com.jgoodies.binding.adapter.Bindings;
import com.jgoodies.binding.adapter.PreferencesAdapter;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.factories.DefaultComponentFactory;
import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.*;
import cz.cvut.felk.gps.core.AppPrefs;
import cz.cvut.felk.gps.core.tasks.ProcessTask;
import cz.cvut.felk.gps.gui.dialogs.filechooser.OpenSaveDialogFactory;
import cz.cvut.felk.gps.swing.ComponentFactory;
import cz.cvut.felk.gps.swing.Swinger;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.Task;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.prefs.Preferences;

/**
 * @author Vity
 */

public class MainPanel extends JPanel {

    public MainPanel() {
        Swinger.initActions(this);
        initComponents();
        buildModels();
    }

    private void buildModels() {
        final Preferences pref = AppPrefs.getPreferences();
        PreferencesAdapter adapter = new PreferencesAdapter(pref, AppPrefs.LAST_USED_KML_DIR, "");
        Bindings.bind(fieldKMLFile, adapter);
        adapter = new PreferencesAdapter(pref, AppPrefs.LAST_SELECTED_DIR, "");
        Bindings.bind(fieldGPXFolder, adapter);
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Open Source Project license - unknown

        DefaultComponentFactory compFactory = DefaultComponentFactory.getInstance();
        JPanel mainPanel = this;
        final ResourceMap map = Swinger.getResourceMap();
        final String title1Text = map.getString("title1.text");
        final String title2Text = map.getString("title2.text");
        JComponent title1 = compFactory.createSeparator(title1Text);
        fieldGPXFolder = ComponentFactory.getTextField();
        JButton btnBrowseGPX = new JButton(Swinger.getAction("btnBrowseGPXAction"));
        Component title2 = compFactory.createSeparator(title2Text);
        fieldKMLFile = ComponentFactory.getTextField();
        JButton btnBrowseKML = new JButton(Swinger.getAction("btnBrowseKMLAction"));
        JPanel panelProcess = new JPanel();
        JButton btnProcess = new JButton(Swinger.getAction("btnProcessAction"));
        CellConstraints cc = new CellConstraints();

        //======== MainPanel ========
        {
            mainPanel.setBorder(Borders.DLU4_BORDER);
            mainPanel.setName("MainPanel");

            //---- fieldGPXFolder ----
            fieldGPXFolder.setColumns(25);
            fieldGPXFolder.setName("fieldGPXFolder");

            btnBrowseGPX.setName("btnBrowseGPX");

            //---- fieldKMLFile ----
            fieldKMLFile.setColumns(25);
            fieldKMLFile.setName("fieldKMLFile");

            //---- btnBrowseKML ----
            btnBrowseKML.setName("btnBrowseKML");

            //======== panelProcess ========
            {
                panelProcess.setName("panelProcess");

                //---- btnProcess ----

                btnProcess.setFont(new Font("Dialog", Font.BOLD, 14));
                btnProcess.setName("btnProcess");

                PanelBuilder panelProcessBuilder = new PanelBuilder(new FormLayout(
                        "default",
                        "default"), panelProcess);

                panelProcessBuilder.add(btnProcess, cc.xywh(1, 1, 1, 1, CellConstraints.RIGHT, CellConstraints.DEFAULT));
            }

            PanelBuilder mainPanelBuilder = new PanelBuilder(new FormLayout(
                    new ColumnSpec[]{
                            new ColumnSpec(ColumnSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW),
                            FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                            FormFactory.DEFAULT_COLSPEC
                    },
                    new RowSpec[]{
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

            mainPanelBuilder.add(title1, cc.xywh(1, 1, 3, 1));
            mainPanelBuilder.add(fieldGPXFolder, cc.xy(1, 3));
            mainPanelBuilder.add(btnBrowseGPX, cc.xy(3, 3));
            mainPanelBuilder.add(title2, cc.xywh(1, 5, 3, 1));
            mainPanelBuilder.add(fieldKMLFile, cc.xy(1, 7));
            mainPanelBuilder.add(btnBrowseKML, cc.xy(3, 7));
            mainPanelBuilder.add(panelProcess, cc.xywh(1, 11, 3, 1, CellConstraints.RIGHT, CellConstraints.DEFAULT));
        }
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner Open Source Project license - unknown
    private JTextField fieldGPXFolder;
    private JTextField fieldKMLFile;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

    @org.jdesktop.application.Action(block = Task.BlockingScope.WINDOW)
    public Task btnProcessAction() {
        final File gpxFolder = new File(fieldGPXFolder.getText());
        if (!gpxFolder.isDirectory()) {
            Swinger.inputFocus(fieldGPXFolder);
            return null;
        }
        final File kmlFile = new File(fieldKMLFile.getText());
        if (!kmlFile.isFile()) {
            Swinger.inputFocus(fieldKMLFile);
            return null;
        }
        return new ProcessTask(gpxFolder, kmlFile);
    }

    @org.jdesktop.application.Action
    public void btnBrowseGPXAction() {
        final File[] files = OpenSaveDialogFactory.getChooseDirectoryDialog(fieldGPXFolder.getText());
        if (files.length > 0)
            fieldGPXFolder.setText(files[0].getAbsolutePath());
    }

    @org.jdesktop.application.Action
    public void btnBrowseKMLAction() {
        final File[] files = OpenSaveDialogFactory.getOpenKMLDialog(fieldKMLFile.getText());
        if (files.length > 0)
            fieldKMLFile.setText(files[0].getAbsolutePath());
    }

}