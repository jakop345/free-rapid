package net.wordrider.dialogs.settings;

import info.clearthought.layout.TableLayout;
import net.wordrider.area.RiderArea;
import net.wordrider.core.AppPrefs;
import net.wordrider.core.Lng;
import net.wordrider.core.managers.AreaManager;
import net.wordrider.core.swing.CustomLayoutConstraints;
import net.wordrider.utilities.Consts;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * @author Vity
 */
final class EditorSettingsPanel extends SettingsPanel {
    public EditorSettingsPanel(final SettingsDialog dialog, final String label) {
        super(dialog, label);    //call to super
    }

    protected final void init() {
        //  this.setLayout(new GridBagLayout());

        final DefaultOptionsGroup defaultGroup = new DefaultOptionsGroup();

        final CheckBoxOption checkDragNDrop = new CheckBoxOption(manager, "settings.dragndrop", AppPrefs.DRAG_AND_DROP, true, defaultGroup);
        final CheckBoxOption checkHighlightLine = new CheckBoxOption(manager, "settings.highlightline", AppPrefs.HIGHLIGHT_LINE, true, defaultGroup) {
            public void applyChange() {
                super.applyChange();
                AreaManager.getInstance().updateHighlightCurrentLine();
            }
        };
        final CheckBoxOption checkBracketMatching = new CheckBoxOption(manager, "settings.bracketMatching", AppPrefs.MATCH_BRACKETS, true, defaultGroup) {
            public void applyChange() {
                super.applyChange();
                AreaManager.getInstance().updateBracketMatching();
            }
        };
        final CheckBoxOption checkBracketMatchingMath = new CheckBoxOption(manager, "settings.bracketMatchMath", AppPrefs.MATCH_BRACKET_MATHONLY, true, defaultGroup);
        checkBracketMatching.getComponent().addItemListener(new ItemListener() {
            public void itemStateChanged(final ItemEvent e) {
                checkBracketMatchingMath.getComponent().setEnabled(((AbstractButton) e.getItem()).isSelected());
            }
        });
        checkBracketMatchingMath.getComponent().setEnabled(checkBracketMatching.getComponent().isSelected());
        final CheckBoxOption checkAltKeysMenu = new CheckBoxOption(manager, "settings.altmenu", AppPrefs.ALT_KEY_FOR_MENU, false, defaultGroup);

        final JLabel labelWidth = new JLabel(Lng.getLabel("settings.emulation"));
        final JLabel labelTabWidth = new JLabel(Lng.getLabel("settings.tabsize"));

        final Object[] widthsTypes = {new ViewWidth(RiderArea.FULLVIEWBORDER), new ViewWidth(RiderArea.TI89VIEWBORDER), new ViewWidth(RiderArea.TI92VIEWBORDER)};
        final DefaultComboBoxModel comboModel = new DefaultComboBoxModel(widthsTypes);
        comboModel.setSelectedItem(new ViewWidth(AppPrefs.getProperty(AppPrefs.USE_EMULATION_CODE, RiderArea.FULLVIEWBORDER)));

        final ComboBoxOption combo = new ComboBoxOption(manager, comboModel, defaultGroup) {
            public void applyChange() {
                super.applyChange();    //call to super
                AppPrefs.storeProperty(AppPrefs.USE_EMULATION_CODE, ((ViewWidth) this.getComponent().getSelectedItem()).getViewWidthValue());
            }
        };
        final Dimension prefSize = combo.getComponent().getPreferredSize();
        prefSize.height = 23;
        combo.getComponent().setPreferredSize(prefSize);
        combo.getComponent().setMaximumSize(new Dimension(80, 23));
        labelWidth.setLabelFor(combo.getComponent());
        final SpinnerNumberModel spinnerModel = new SpinnerNumberModel();
        spinnerModel.setValue(new Integer(AppPrefs.getProperty(AppPrefs.TABSIZE, Consts.DEFAULT_TAB_SIZE)));
        spinnerModel.setMaximum(20);
        spinnerModel.setMinimum(0);
        final SpinnerOption spinner = new SpinnerOption(manager, spinnerModel, defaultGroup) {
            public void applyChange() {
                super.applyChange();    //call to super
                AppPrefs.storeProperty(AppPrefs.TABSIZE, getComponent().getValue().toString());
            }
        };
        spinner.setDefaultValue(Consts.DEFAULT_TAB_SIZE);
        spinner.getComponent().setPreferredSize(new Dimension(60, 23));
        labelTabWidth.setLabelFor(spinner.getComponent());

        checkBracketMatchingMath.getComponent().setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(0, 25, 0, 0), checkBracketMatchingMath.getComponent().getBorder()));

        final double f = TableLayout.FILL;
        final double p = TableLayout.PREFERRED;
        final TableLayout mgr = new TableLayout(new double[]{p, f}, new double[]{p, p, p, p, p, p, 2, p});
        mgr.setHGap(10);
        mgr.setVGap(2);
        this.setLayout(mgr);
        this.add(checkDragNDrop.getComponent(), new CustomLayoutConstraints(0, 0, 2, 1));
        this.add(checkHighlightLine.getComponent(), new CustomLayoutConstraints(0, 1, 2, 1));
        this.add(checkBracketMatching.getComponent(), new CustomLayoutConstraints(0, 2, 2, 1));
        this.add(checkBracketMatchingMath.getComponent(), new CustomLayoutConstraints(0, 3, 2, 1));
        this.add(checkAltKeysMenu.getComponent(), new CustomLayoutConstraints(0, 4, 2, 1));
        this.add(labelWidth, new CustomLayoutConstraints(0, 5));
        this.add(combo.getComponent(), new CustomLayoutConstraints(1, 5, 1, 1, TableLayout.FULL, TableLayout.LEFT));
        this.add(labelTabWidth, new CustomLayoutConstraints(0, 7));
        this.add(spinner.getComponent(), new CustomLayoutConstraints(1, 7, 1, 1, TableLayout.LEFT, TableLayout.LEFT));

    }

    private static final class ViewWidth {
        private final Integer value;
        private final String label;

        public ViewWidth(final int viewWidthValue) {
            value = viewWidthValue;
            switch (viewWidthValue) {
                case RiderArea.TI89VIEWBORDER:
                    label = Lng.getLabel("settings.emulation.ti89");
                    break;
                case RiderArea.TI92VIEWBORDER:
                    label = Lng.getLabel("settings.emulation.ti92");
                    break;
                case RiderArea.FULLVIEWBORDER:
                    label = Lng.getLabel("settings.emulation.fullview");
                    break;
                default:
                    label = Lng.getLabel("not found!");
                    break;
            }
        }

        public final boolean equals(final Object obj) {
            return obj instanceof ViewWidth && value.equals(((ViewWidth) obj).getViewWidthValue());
        }

        public final Integer getViewWidthValue() {
            return value;
        }

        public final String toString() {
            return label;
        }
    }

}
