package net.wordrider.dialogs.settings;

import info.clearthought.layout.TableLayout;
import net.wordrider.area.RiderStyles;
import net.wordrider.core.AppPrefs;
import net.wordrider.core.Lng;
import net.wordrider.core.MainApp;
import net.wordrider.core.managers.TitleManager;
import net.wordrider.core.swing.CustomLayoutConstraints;
import net.wordrider.dialogs.LimitedPlainDocument;
import net.wordrider.dialogs.SaveSettingsDialog;
import net.wordrider.utilities.Consts;
import net.wordrider.utilities.Swinger;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;

/**
 * @author Vity
 */
final class MiscSettingsPanel extends SettingsPanel {
    public MiscSettingsPanel(final SettingsDialog dialog, final String labelCode) {
        super(dialog, labelCode);    //call to super
    }

    protected final void init() {
        final DefaultOptionsGroup defaultGroup = new DefaultOptionsGroup();

        final CheckBoxOption checkImageFormatDialog = new CheckBoxOption(manager, "settings.imageFormatDialog", AppPrefs.SHOW_IMAGEFORMAT, true, defaultGroup);
        final CheckBoxOption checkInputFormat = new CheckBoxOption(manager, "settings.inputFormatDialog", AppPrefs.SHOWINPUTFORMAT, true, defaultGroup);
        final CheckBoxOption checkRenameImageDialog = new CheckBoxOption(manager, "settings.renameImage", AppPrefs.RENAME_IMAGE_AUTOMATICALLY, false, defaultGroup);

        final double f = TableLayout.FILL;
        final double p = TableLayout.PREFERRED;
        final TableLayout titleLayout = new TableLayout(new double[]{p, p, p, f}, new double[]{p});
        titleLayout.setVGap(0);
        titleLayout.setHGap(4);
        final JPanel titlePanel = new JPanel(titleLayout);

        final Object[] titleFormats = {new TitleStyle(TitleManager.TITLE_FILENAME), new TitleStyle(TitleManager.VARIABLE_NAME), new TitleStyle(TitleManager.FOLDER_VARIABLE_NAME), new TitleStyle(TitleManager.FILE_PATH)};
        final DefaultComboBoxModel comboModel = new DefaultComboBoxModel(titleFormats);
        comboModel.setSelectedItem(new TitleStyle(AppPrefs.getProperty(AppPrefs.FRAME_TITLE_TYPE, TitleManager.TITLE_FILENAME)));
        final ComboBoxOption combo = new ComboBoxOption(manager, comboModel, defaultGroup) {

            public void applyChange() {
                super.applyChange();    //call to super
                AppPrefs.storeProperty(AppPrefs.FRAME_TITLE_TYPE, ((TitleStyle) this.getComponent().getSelectedItem()).getValue());
                MainApp.getInstance().getMainAppFrame().getManagerDirector().getTitleChanger().updateTitle();
            }
        };

        final CheckBoxOption checkFrameTitle = new CheckBoxOption(manager, "settings.checkFrameTitle", AppPrefs.FRAME_TITLE, true, defaultGroup) {

            @Override
            public void itemStateChanged(final ItemEvent e) {
                super.itemStateChanged(e);
                combo.getComponent().setEnabled(this.getComponent().isSelected());
            }

            @Override
            public void applyChange() {
                super.applyChange();
                MainApp.getInstance().getMainAppFrame().getManagerDirector().getTitleChanger().updateTitle();
            }
        };

        final JLabel labelFrameTitle = Swinger.getLabel("settings.labelFrameTitle");
        titlePanel.add(checkFrameTitle.getComponent(), new CustomLayoutConstraints(0, 0));
        titlePanel.add(combo.getComponent(), new CustomLayoutConstraints(1, 0));
        titlePanel.add(labelFrameTitle, new CustomLayoutConstraints(2, 0));

        setFrameComboEnabled(combo);

        final Document folderDocument = new LimitedPlainDocument(SaveSettingsDialog.regexpFolderPattern);
        final Document variableDocument = new LimitedPlainDocument(SaveSettingsDialog.regexpFolderPattern);
        final Document commentDocument = new LimitedPlainDocument(SaveSettingsDialog.regexpComment);
        final JTextField inputFolder = new JTextFieldOption(manager, folderDocument, AppPrefs.DEFAULT_FOLDER, Consts.DEFAULT_FOLDERNAME, defaultGroup).getComponent();
        final JTextField inputVariable = new JTextFieldOption(manager, variableDocument, AppPrefs.DEFAULT_VARIABLE, Consts.DEFAULT_VARNAME, defaultGroup).getComponent();
        final JTextField inputComment = new JTextFieldOption(manager, commentDocument, AppPrefs.DEFAULT_COMMENT, "generated_by_Wordrider", defaultGroup).getComponent();
        final FocusListener listener = new Swinger.SelectAllOnFocusListener();
        inputFolder.addFocusListener(listener);
        inputVariable.addFocusListener(listener);
        inputComment.addFocusListener(listener);


        final JLabel labelFolder = Swinger.getLabel("settings.input.folder");
        labelFolder.setLabelFor(inputFolder);
        final JLabel labelVariable = Swinger.getLabel("settings.input.variable");
        labelVariable.setLabelFor(inputVariable);
        final JLabel labelComment = Swinger.getLabel("settings.input.comment");
        labelComment.setLabelFor(inputComment);
        final JPanel generalPanel = new JPanel(new GridBagLayout());
        generalPanel.setBorder(BorderFactory.createCompoundBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), Lng.getLabel("settings.defaultValuesTitle")), BorderFactory.createEmptyBorder(2, 4, 0, 4)));
        inputFolder.setPreferredSize(new Dimension(80, inputFolder.getPreferredSize().height));
        inputVariable.setPreferredSize(inputFolder.getPreferredSize());

        generalPanel.add(labelFolder, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
                , GridBagConstraints.WEST, GridBagConstraints.VERTICAL, new Insets(3, 4, 4, 0), 0, 0));
        generalPanel.add(labelComment, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
                , GridBagConstraints.WEST, GridBagConstraints.VERTICAL, new Insets(4, 4, 4, 0), 0, 0));
        generalPanel.add(inputFolder, new GridBagConstraints(1, 0, 1, 1, 0.8, 0.0
                , GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(4, 4, 4, 4), 0, 0));
        generalPanel.add(inputVariable, new GridBagConstraints(3, 0, 1, 1, 1.0, 0.0
                , GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, new Insets(4, 4, 4, 4), 0, 0));
        generalPanel.add(inputComment, new GridBagConstraints(1, 1, 3, 1, 1.0, 0.0
                , GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(4, 4, 4, 4), 0, 0));
        generalPanel.add(labelVariable, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0
                , GridBagConstraints.WEST, GridBagConstraints.VERTICAL, new Insets(4, 4, 4, 4), 0, 0));

        TableLayout notefolioLayout = new TableLayout(new double[]{p, p, f}, new double[]{p, p});
        notefolioLayout.setVGap(0);
        notefolioLayout.setHGap(4);
        final JPanel notefolioPanel = new JPanel(notefolioLayout);
        notefolioPanel.setBorder(BorderFactory.createCompoundBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), Lng.getLabel("settings.notefolioImportTitle")), BorderFactory.createEmptyBorder(2, 4, 0, 4)));

        final Object[] widthsTypes = {new NoteSep(RiderStyles.SINGLE_LINE), new NoteSep(RiderStyles.DOUBLE_LINE), new NoteSep(RiderStyles.EMPTY_LINE)};
        final DefaultComboBoxModel comboNoteModel = new DefaultComboBoxModel(widthsTypes);
        comboNoteModel.setSelectedItem(new NoteSep(AppPrefs.getProperty(AppPrefs.NOTEFOLIO_SEPARATOR, RiderStyles.DOUBLE_LINE)));

        final ComboBoxOption comboNote = new ComboBoxOption(manager, comboNoteModel, defaultGroup) {
            public void applyChange() {
                super.applyChange();    //call to super
                AppPrefs.storeProperty(AppPrefs.NOTEFOLIO_SEPARATOR, ((NoteSep) this.getComponent().getSelectedItem()).getValue());
            }
        };

        final JLabel labelNotefolio = Swinger.getLabel("settings.notefolio.label");
        labelFolder.setLabelFor(comboNote.getComponent());


        final CheckBoxOption checkPutBreakpoints = new CheckBoxOption(manager, "settings.putBreakpoints", AppPrefs.NOTEFOLIO_BREAKPOINT, true, defaultGroup);

        notefolioPanel.add(labelNotefolio, new CustomLayoutConstraints(0, 0));
        notefolioPanel.add(comboNote.getComponent(), new CustomLayoutConstraints(1, 0, 1, 1, TableLayout.LEFT, TableLayout.CENTER));
        notefolioPanel.add(checkPutBreakpoints.getComponent(), new CustomLayoutConstraints(0, 1, 3, 1));

        TableLayout mgr = new TableLayout(new double[]{f}, new double[]{p, p, p, p, p, p, f});
        mgr.setVGap(2);
        this.setLayout(mgr);

        this.add(checkInputFormat.getComponent(), new CustomLayoutConstraints(0, 0));
        this.add(checkImageFormatDialog.getComponent(), new CustomLayoutConstraints(0, 1));
        this.add(checkRenameImageDialog.getComponent(), new CustomLayoutConstraints(0, 2));
        this.add(titlePanel, new CustomLayoutConstraints(0, 4));
        this.add(generalPanel, new CustomLayoutConstraints(0, 5));
        this.add(notefolioPanel, new CustomLayoutConstraints(0, 6));

    }

    private void setFrameComboEnabled(ComboBoxOption combo) {
        combo.getComponent().setEnabled(AppPrefs.getProperty(AppPrefs.FRAME_TITLE, true));
    }


    private static final class NoteSep extends ComboModelItem {
        public NoteSep(final int value) {
            super(value);
        }

        String getLabelByValue(int separator) {
            switch (separator) {
                case RiderStyles.SINGLE_LINE:
                    return Lng.getLabel("settings.singleLine");
                case RiderStyles.DOUBLE_LINE:
                    return Lng.getLabel("settings.doubleLine");
                case RiderStyles.EMPTY_LINE:
                    return Lng.getLabel("settings.emptyLine");
                default:
                    assert false;
                    return "not found";
            }
        }
    }

    private static final class TitleStyle extends ComboModelItem {
        public TitleStyle(final int value) {
            super(value);
        }

        String getLabelByValue(int separator) {
            switch (separator) {
                case TitleManager.TITLE_FILENAME:
                    return Lng.getLabel("settings.frame.title");
                case TitleManager.VARIABLE_NAME:
                    return Lng.getLabel("settings.frame.variable");
                case TitleManager.FOLDER_VARIABLE_NAME:
                    return Lng.getLabel("settings.frame.foldervar");
                case TitleManager.FILE_PATH:
                    return Lng.getLabel("settings.frame.filepath");
                default:
                    assert false;
                    return "not found";
            }
        }
    }


}
