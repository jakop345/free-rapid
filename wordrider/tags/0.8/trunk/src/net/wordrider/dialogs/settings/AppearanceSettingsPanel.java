package net.wordrider.dialogs.settings;

import info.clearthought.layout.TableLayout;
import net.wordrider.core.AppPrefs;
import net.wordrider.core.Lng;
import net.wordrider.core.MainApp;
import net.wordrider.core.swing.CustomLayoutConstraints;
import net.wordrider.gui.LaF;
import net.wordrider.gui.LookAndFeels;
import net.wordrider.utilities.LogUtils;
import net.wordrider.utilities.Swinger;

import javax.swing.*;
import java.awt.*;
import java.util.Vector;
import java.util.logging.Logger;

/**
 * @author Vity
 */
final class AppearanceSettingsPanel extends SettingsPanel {
    private final static Logger logger = Logger.getLogger(AppearanceSettingsPanel.class.getName());

    public AppearanceSettingsPanel(final SettingsDialog dialog, final String labelCode) {
        super(dialog, labelCode);    //call to super
    }

    protected final void init() {
        //  this.setLayout(new GridBagLayout());

        final JLabel labelLookAndFeel = Swinger.getLabel("settings.lookandfeel");

        final Vector<LaF> lafsVector = LookAndFeels.getInstance().getAvailableLookAndFeels();
        final DefaultComboBoxModel comboModel = new DefaultComboBoxModel(lafsVector);
        final int lafIndex = lafsVector.indexOf(LookAndFeels.getInstance().getSelectedLaF());
        if (lafIndex != -1)
            comboModel.setSelectedItem(lafsVector.elementAt(lafIndex));
        final DefaultOptionsGroup defaultGroup = new DefaultOptionsGroup();
        final ComboBoxOption comboLAF = new ComboBoxOption(manager, comboModel, defaultGroup) {
            public void applyChange() {
                super.applyChange();    //call to super
                final boolean result = changeLookAndFeel((LaF) this.getComponent().getSelectedItem());
                if (!result) {
                    final int index = lafsVector.indexOf(LookAndFeels.getInstance().getSelectedLaF());
                    if (index != -1)
                        this.getComponent().setSelectedItem(lafsVector.elementAt(index));
                }
            }
        };

        final Dimension prefSize = comboLAF.getComponent().getPreferredSize();
        prefSize.height = 23;
        comboLAF.getComponent().setPreferredSize(prefSize);

        comboLAF.getComponent().setMaximumSize(new Dimension(80, 23));

        labelLookAndFeel.setLabelFor(comboLAF.getComponent());
        final ColorOptionsGroup group = new ColorOptionsGroup();
        final CheckBoxOption antialiasingOption = new CheckBoxOption(manager, "settings.antialiasing", Swinger.antialiasing, group) {
            public void applyChange() {
                super.applyChange();    //call to super
                Swinger.setAntialiasing(getComponent().isSelected());
            }
        };

        final CheckBoxOption checkDecoratedFrames = new CheckBoxOption(manager, "settings.decoratedFrames", AppPrefs.DECORATED_FRAMES, false, defaultGroup);
        final CheckBoxOption checkUseScrollable = new CheckBoxOption(manager, "settings.usescrollLayout", AppPrefs.SCROLL_LAYOUT, true, defaultGroup) {
            public void applyChange() {
                super.applyChange();
                MainApp.getInstance().getMainAppFrame().getManagerDirector().getAreaManager().applyTabLayout();
            }
        };

        // Create and set layout

        final double f = TableLayout.FILL;
        final double p = TableLayout.PREFERRED;
        final TableLayout mgr = new TableLayout(new double[]{p, f}, new double[]{p, p, p, p});
        mgr.setHGap(10);
        mgr.setVGap(2);
        this.setLayout(mgr);
        this.add(labelLookAndFeel, new CustomLayoutConstraints(0, 0));
        this.add(comboLAF.getComponent(), new CustomLayoutConstraints(1, 0, 1, 1, TableLayout.LEFT, TableLayout.LEFT));
        this.add(antialiasingOption.getComponent(), new CustomLayoutConstraints(0, 1, 2, 1));
        this.add(checkDecoratedFrames.getComponent(), new CustomLayoutConstraints(0, 2, 2, 1));
        this.add(checkUseScrollable.getComponent(), new CustomLayoutConstraints(0, 3, 2, 1));

//        this.add(labelLookAndFeel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
//                , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2, 4, 2, 4), 0, 0));
//        this.add(comboLAF, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
//                , GridBagConstraints.WEST, GridBagConstraints.VERTICAL, new Insets(2, 4, 2, 4), 25, 0));
//        this.add(antialiasingOption, new GridBagConstraints(0, 1, 2, 1, 0.0, 0.0
//                , GridBagConstraints.WEST, GridBagConstraints.VERTICAL, new Insets(2, 4, 2, 4), 0, 0));
//        this.add(checkDecoratedFrames, new GridBagConstraints(0, 2, 2, 1, 0.0, 0.0
//                , GridBagConstraints.WEST, GridBagConstraints.VERTICAL, new Insets(2, 4, 2, 4), 0, 0));
//        this.add(checkUseScrollable, new GridBagConstraints(0, 3, 2, 1, 0.0, 0.0
//                , GridBagConstraints.WEST, GridBagConstraints.VERTICAL, new Insets(2, 4, 2, 4), 0, 0));

    }

    private static boolean changeLookAndFeel(final LaF laf) {
        if (LookAndFeels.getInstance().getSelectedLaF().equals(laf))
            return true;
        boolean succesful = false;
        try {
            succesful = LookAndFeels.getInstance().loadLookAndFeel(laf, true);
            if (succesful)
                LookAndFeels.getInstance().storeSelectedLaF(laf);
        } catch (Exception ex) {
            LogUtils.processException(logger, ex);
        }
        if (succesful)
            try {
                Swinger.showInformationDialog(MainApp.getInstance().getMainAppFrame(), Lng.getLabel("ChangeLookAndFeelAction.set"));
            } catch (Exception e) {
                LogUtils.processException(logger, e);
            }
        else
            Swinger.showErrorDialog(MainApp.getInstance().getMainAppFrame(), Lng.getLabel("ChangeLookAndFeelAction.failed"));
        return succesful;
    }
}
