package net.wordrider.dialogs.settings;

import info.clearthought.layout.TableLayout;
import net.wordrider.core.AppPrefs;
import net.wordrider.core.Lng;
import net.wordrider.core.swing.CustomLayoutConstraints;
import net.wordrider.utilities.Consts;

import javax.swing.*;
import java.awt.*;

/**
 * @author Vity
 */
final class GeneralSettingsPanel extends SettingsPanel {
    public GeneralSettingsPanel(final SettingsDialog dialog, final String labelCode) {
        super(dialog, labelCode);    //call to super
    }

    protected final void init() {
        //    this.setLayout(new GridBagLayout());
        final DefaultOptionsGroup defaultGroup = new DefaultOptionsGroup();

        final CheckBoxOption checkWindowPositionOption = new CheckBoxOption(manager, "settings.position", AppPrefs.WINDOWSPOSITION, false, defaultGroup);
        final CheckBoxOption checkOneInstanceOption = new CheckBoxOption(manager, "settings.oneinstance", AppPrefs.ONEINSTANCE, false, defaultGroup);
        final CheckBoxOption checkNewFileOption = new CheckBoxOption(manager, "settings.newfile", AppPrefs.NEW_FILE_AFTER_START, false, defaultGroup);
        final CheckBoxOption checkNewVersionOption = new CheckBoxOption(manager, "settings.newversion", AppPrefs.NEW_VERSION, false, defaultGroup);
        final CheckBoxOption checkSaveCharactersOption = new CheckBoxOption(manager, "settings.usedcharsSave", AppPrefs.USED_CHARS_SAVE, true, defaultGroup);
        final CheckBoxOption checkSaveInfoDialogOption = new CheckBoxOption(manager, "settings.infosave", AppPrefs.INFO_SUCCESFUL, true, defaultGroup);

        final SpinnerNumberModel spinnerModel = new SpinnerNumberModel();
        spinnerModel.setValue(new Integer(AppPrefs.getProperty(AppPrefs.MAX_RECENT_FILES, Consts.DEFAULT_RECENT_FILES_MAX_COUNT)));
        spinnerModel.setMaximum(20);
        spinnerModel.setMinimum(0);
        final SpinnerOption spinner = new SpinnerOption(manager, spinnerModel, defaultGroup) {
            public void applyChange() {
                super.applyChange();    //call to super
                AppPrefs.storeProperty(AppPrefs.MAX_RECENT_FILES, getComponent().getValue().toString());
            }
        };
        spinner.setDefaultValue(Consts.DEFAULT_TAB_SIZE);
        spinner.getComponent().setPreferredSize(new Dimension(60, 23));

        final JLabel labelRecents = new JLabel(Lng.getLabel("settings.recentcount"));
        labelRecents.setLabelFor(spinner.getComponent());

        final double f = TableLayout.FILL;
        final double p = TableLayout.PREFERRED;
        final TableLayout mgr = new TableLayout(new double[]{p, f}, new double[]{p, p, p, p, p, p, 2, p});
        mgr.setHGap(10);
        mgr.setVGap(2);
        this.setLayout(mgr);
        this.add(checkWindowPositionOption.getComponent(), new CustomLayoutConstraints(0, 0, 2, 1));
        this.add(checkOneInstanceOption.getComponent(), new CustomLayoutConstraints(0, 1, 2, 1));
        this.add(checkNewVersionOption.getComponent(), new CustomLayoutConstraints(0, 2, 2, 1));
        this.add(checkNewFileOption.getComponent(), new CustomLayoutConstraints(0, 3, 2, 1));
        this.add(checkSaveCharactersOption.getComponent(), new CustomLayoutConstraints(0, 4, 2, 1));
        this.add(checkSaveInfoDialogOption.getComponent(), new CustomLayoutConstraints(0, 5, 2, 1));
        this.add(labelRecents, new CustomLayoutConstraints(0, 7));
        this.add(spinner.getComponent(), new CustomLayoutConstraints(1, 7, 1, 1, TableLayout.LEFT, TableLayout.CENTER));

//        this.add(checkWindowPositionOption, new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0
//            , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2, 4, 2, 4), 0, 0));
//        this.add(checkOneInstanceOption, new GridBagConstraints(0, 1, 2, 1, 0.0, 0.0
//            , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2, 4, 2, 4), 0, 0));
//        this.add(checkNewVersionOption, new GridBagConstraints(0, 2, 2, 1, 0.0, 0.0
//            , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2, 4, 2, 4), 0, 0));
//        this.add(checkNewFileOption, new GridBagConstraints(0, 3, 2, 1, 0.0, 0.0
//            , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2, 4, 2, 4), 0, 0));
//        this.add(checkSaveCharactersOption, new GridBagConstraints(0, 4, 2, 1, 0.0, 0.0
//            , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2, 4, 2, 4), 0, 0));
//
//        this.add(checkSaveInfoDialogOption, new GridBagConstraints(0, 6, 2, 1, 0.0, 0.0
//            , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2, 4, 2, 4), 0, 0));
//        this.add(labelRecents, new GridBagConstraints(0, 7, 1, 1, 0.0, 0.0
//            , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2, 4, 4, 4), 0, 0));
//        this.add(spinner, new GridBagConstraints(1, 7, 1, 1, 0.0, 0.0
//            , GridBagConstraints.WEST, GridBagConstraints.VERTICAL, new Insets(2, 4, 4, 4), 0, 0));

        //      this.setPreferredSize(new Dimension(325, 260));
    }
}
