package net.wordrider.dialogs.settings;

import info.clearthought.layout.TableLayout;
import net.wordrider.core.AppPrefs;
import net.wordrider.core.Lng;
import net.wordrider.core.MainApp;
import net.wordrider.core.actions.SendToCalcAction;
import net.wordrider.core.swing.CustomLayoutConstraints;
import net.wordrider.core.swing.URLMouseClickAdapter;
import net.wordrider.dialogs.JButtonGroup;
import net.wordrider.utilities.Consts;
import net.wordrider.utilities.Swinger;
import net.wordrider.utilities.Utils;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.plaf.FileChooserUI;
import javax.swing.plaf.basic.BasicFileChooserUI;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * @author Vity
 */
final class SendMethodSettingsPanel extends SettingsPanel {
    public SendMethodSettingsPanel(final SettingsDialog dialog, final String labelCode) {
        super(dialog, labelCode);    //call to super
    }

    protected final void init() {
        final DefaultOptionsGroup defaultGroup = new DefaultOptionsGroup();

        final int methodSelected = SendToCalcAction.getMethodSelected();

        final String defaultPathTiConnect = SendToCalcAction.getDefaultPath(SendToCalcAction.TI_CONNECT);
        final JTextFieldOption tiConnectPathField = new JTextFieldOption(manager, null, AppPrefs.TICONNECT_PATH, defaultPathTiConnect, defaultGroup);
        final String defaultPathTiLP = SendToCalcAction.getDefaultPath(SendToCalcAction.TILP);
        final JTextFieldOption tilpPathField = new JTextFieldOption(manager, null, AppPrefs.TILP_PATH, defaultPathTiLP, defaultGroup);
        final JTextFieldOption tilpParametersField = new JTextFieldOption(manager, null, AppPrefs.TILP_PARAMETERS, "", defaultGroup);

        final JTextField inputTIPath = tiConnectPathField.getComponent();

        final JButton btnTIConnectPath = new JButton("\u2026");
        final Dimension btnDimension = new Dimension(20, inputTIPath.getPreferredSize().height);
        btnTIConnectPath.setPreferredSize(btnDimension);
        final JButton btnTiLPPath = new JButton("\u2026");
        btnTiLPPath.setPreferredSize(btnDimension);

        final SimpleFileFilter tiConnectFilter = new SimpleFileFilter(new String[]{"TISendTo.exe"}, "TISendTo.exe (TI Connect)");
        final SimpleFileFilter tilpFilter = new SimpleFileFilter(Utils.isWindows() ? new String[]{"tilp2.exe", "tilp.exe"} : new String[]{"tilp2", "tilp"}, Utils.isWindows() ? "tilp2.exe, tilp.exe (TiLP)" : "tilp2, tilp (TiLP)");

        btnTIConnectPath.addActionListener(new SelFileAction(inputTIPath, tiConnectFilter));
        btnTiLPPath.addActionListener(new SelFileAction(tilpPathField.getComponent(), tilpFilter));


        final RadioOption radioTIConnect = new RadioOption(manager, "settings.radioTIConnect", AppPrefs.CALC_SEND_METHOD, SendToCalcAction.TI_CONNECT, methodSelected, defaultGroup);
        final RadioOption radioTiLP = new RadioOption(manager, "settings.radioTiLP", AppPrefs.CALC_SEND_METHOD, SendToCalcAction.TILP, methodSelected, defaultGroup) {
            protected void updateValue() {
                super.updateValue();
                final boolean enabled = getComponent().isSelected();
                inputTIPath.setEnabled(!enabled);
                tilpPathField.getComponent().setEnabled(enabled);
                tilpParametersField.getComponent().setEnabled(enabled);
                btnTIConnectPath.setEnabled(!enabled);
                btnTiLPPath.setEnabled(enabled);
            }
        };
        if (!Utils.isWindows()) {
            radioTIConnect.getComponent().setEnabled(false);
            inputTIPath.setEnabled(false);
            btnTIConnectPath.setEnabled(false);
        }
        inputTIPath.setPreferredSize(new Dimension(300, inputTIPath.getPreferredSize().height));

        final JButtonGroup group = new JButtonGroup();
        group.add(radioTIConnect.getComponent());
        group.add(radioTiLP.getComponent());

        final JLabel tiConnectPathLabel = Swinger.getLabel("settings.filePathTIConnect");
        tiConnectPathLabel.setLabelFor(inputTIPath);
        final JLabel tilpPathLabel = Swinger.getLabel("settings.filePathTiLP");
        tilpPathLabel.setLabelFor(tilpPathField.getComponent());
        final JLabel tilpParametersLabel = Swinger.getLabel("settings.TiLPParams");
        tilpParametersLabel.setLabelFor(tilpParametersField.getComponent());

        final JLabel moreInfoParamsLabel = Swinger.getLabel("settings.moreInfoParams");
        moreInfoParamsLabel.setToolTipText(Lng.getLabel("settings.moreInfoParams.hint"));
        moreInfoParamsLabel.setForeground(Color.BLUE);
        moreInfoParamsLabel.addMouseListener(new URLMouseClickAdapter(Consts.WEBURL_HELP_PARAMS));
        moreInfoParamsLabel.setFocusable(true);


        final double f = TableLayout.FILL;
        final double p = TableLayout.PREFERRED;
        final TableLayout mgr = new TableLayout(new double[]{20, f, 2, p, p, 2}, new double[]{p, p, p, p, p, p, p, p});
        mgr.setVGap(2);
        this.setLayout(mgr);

        this.add(radioTIConnect.getComponent(), new CustomLayoutConstraints(0, 0, 4, 1));
        this.add(tiConnectPathLabel, new CustomLayoutConstraints(1, 1));
        this.add(inputTIPath, new CustomLayoutConstraints(1, 2));
        this.add(btnTIConnectPath, new CustomLayoutConstraints(3, 2));
        this.add(radioTiLP.getComponent(), new CustomLayoutConstraints(0, 3, 4, 1));
        this.add(tilpPathLabel, new CustomLayoutConstraints(1, 4));
        this.add(tilpPathField.getComponent(), new CustomLayoutConstraints(1, 5));
        this.add(btnTiLPPath, new CustomLayoutConstraints(3, 5));
        this.add(tilpParametersLabel, new CustomLayoutConstraints(1, 6));
        this.add(tilpParametersField.getComponent(), new CustomLayoutConstraints(1, 7));
        this.add(moreInfoParamsLabel, new CustomLayoutConstraints(3, 7, 2, 1));
    }


    private static class SelFileAction implements ActionListener {
        private JTextComponent field;
        private SimpleFileFilter filter;

        public SelFileAction(JTextComponent field, SimpleFileFilter filter) {

            this.field = field;
            this.filter = filter;
        }


        public void actionPerformed(ActionEvent e) {
            showFileChooser();
        }

        private void showFileChooser() {
            final JFileChooser fileDialog = new JFileChooser(field.getText());
            fileDialog.setFileFilter(filter);
            fileDialog.setDialogTitle(Lng.getLabel("settings.filechooser"));
            fileDialog.setMultiSelectionEnabled(false);
            fileDialog.setApproveButtonText(Lng.getLabel("settings.filechooserBtnSelect"));
            fileDialog.setApproveButtonMnemonic(Lng.getMnemonic("settings.filechooserBtnSelect"));
            fileDialog.setAcceptAllFileFilterUsed(false);
            final FileChooserUI chooserUI = fileDialog.getUI();
            if (chooserUI instanceof BasicFileChooserUI)
                ((BasicFileChooserUI) chooserUI).setFileName(filter.getFileNames()[0]);

            final int result = fileDialog.showOpenDialog(MainApp.getInstance().getMainAppFrame());
            if (result == JFileChooser.APPROVE_OPTION) {
                field.setText(fileDialog.getSelectedFile().getAbsolutePath());
            }
            Swinger.inputFocus(field);
        }

    }

    private static class SimpleFileFilter extends FileFilter {
        private String description;
        private String[] fileNames;


        public SimpleFileFilter(final String[] fileNames, final String description) {
            this.description = description;
            this.fileNames = fileNames;
        }

        public boolean accept(File f) {
            if (f.isDirectory())
                return true;

            final String fileName = f.getName();
            if (fileName != null)
                for (int i = 0; i < fileNames.length; ++i) {
                    if (fileName.equalsIgnoreCase(this.fileNames[i])) {
                        return true;
                    }
                }
            return false;
        }

        public String getDescription() {
            return description;
        }


        public String[] getFileNames() {
            return fileNames;
        }
    }

}
