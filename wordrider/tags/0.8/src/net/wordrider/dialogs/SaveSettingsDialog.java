package net.wordrider.dialogs;

import net.wordrider.core.AppPrefs;
import net.wordrider.core.Lng;
import net.wordrider.dialogs.layouts.EqualsLayout;
import net.wordrider.files.ti68kformat.TIFileInfo;
import net.wordrider.files.ti68kformat.TIImageFileInfo;
import net.wordrider.files.ti68kformat.TITextFileInfo;
import net.wordrider.utilities.LogUtils;
import net.wordrider.utilities.Swinger;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusListener;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * @author Vity
 */
public final class SaveSettingsDialog extends AppDialog {
    private final JTextField inputFolder = new JTextField();
    private final JTextField inputVariable = new JTextField();
    private final JTextField inputComment = new JTextField();
    private final JButtonGroup buttonGroup = new JButtonGroup();
    private final JButtonGroup radioGroup = new JButtonGroup();

    private JRadioButton radioDontChange;
    private JRadioButton radioUseLast;
    private JRadioButton radioSameAsDocument;
    private JRadioButton radioSetNew;

    private JRadioButton radioStoreType1;
    private JRadioButton radioStoreType2;
    private JRadioButton radioStoreType3;

    private final JTextField inputPictureFolder = new JTextField();


    private final JComboBox comboOutputFormat = new JComboBox(new Object[]{"Hibview", "TxtRider/uView"});

    private JButton btnCancel;
    private JButton btnSave;
    private final JCheckBox checkSave = Swinger.getCheckBox("savesettings.pictures");
    private final JCheckBox checkInsertIntoDocument = Swinger.getCheckBox("savesettings.pictures.insert");

//    private final static int RESULT_SAVE = 0;
    //    private final static int RESULT_CANCEL = 1;
    //    private final int result = RESULT_SAVE;
    private TIFileInfo fileInfo;
    private final Frame frame;
    //    private static final String DEFAULT_FOLDER_NAME = "main";
    private final JPanel pictureTabPanel = new JPanel(new GridBagLayout());
    private final JPanel generalPanel = new JPanel(new GridBagLayout());
    private final JPanel outputFormatPanel = new JPanel(new GridBagLayout());
    private final JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
    public static final Pattern regexpFolderPattern = Pattern.compile("\\p{Alpha}\\w{0,7}");

    public static final int STATUS_TEXTFILE = 0;
    public static final int STATUS_IMAGE = 1;
    public static final int STATUS_IMAGE_IMAGEPROPERTIES = 2;
    private final int statusWindow;
    public static final Pattern regexpComment = Pattern.compile("^\\p{Alnum}[A-Za-z0-9_]{0,39}$");
    private final static Logger logger = Logger.getLogger(SaveSettingsDialog.class.getName());

    public SaveSettingsDialog(final Frame owner, final TIFileInfo fileInfo, final int statusWindow) {
        super(owner, true);
        this.frame = owner;
        this.statusWindow = statusWindow;
        try {
            init();
        } catch (Exception e) {
            LogUtils.processException(logger, e);
        }
        switch (statusWindow) {
            case STATUS_TEXTFILE:
                tabbedPane.addTab(Lng.getLabel("savesettings.tab.pictures"), pictureTabPanel);
                final TITextFileInfo info = ((TITextFileInfo) fileInfo);
                final int outputFormat = info.getOutputFormat();
                if (outputFormat == -1) {
                    comboOutputFormat.setSelectedIndex(AppPrefs.getProperty(AppPrefs.LAST_USED_OUTPUTFORMAT, 0));
                } else comboOutputFormat.setSelectedIndex(outputFormat);
                this.setTitle(Lng.getLabel("savesettings.title"));
                break;
            case STATUS_IMAGE:
                checkInsertIntoDocument.setSelected(AppPrefs.getProperty(AppPrefs.REMEMBER_INSERT_IMAGE, true));
                this.setTitle(Lng.getLabel("savesettings.title"));
                break;
            case STATUS_IMAGE_IMAGEPROPERTIES:
                this.setTitle(Lng.getLabel("savesettings.title2"));
                break;
            default:
                throw new IllegalArgumentException("Invalid argument status Window");
        }
        tabbedPane.setSelectedComponent(generalPanel);
        this.pack();
        initDialogContents(fileInfo);
        Swinger.centerDialog(owner, this);
        this.setModal(true);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setVisible(true);
    }


    private void initDialogContents(final TIFileInfo fileInfo) {
        Swinger.addKeyActions(inputFolder);
        Swinger.addKeyActions(inputVariable);
        Swinger.addKeyActions(inputComment);
        Swinger.addKeyActions(inputPictureFolder);
        if (fileInfo.getFolderName().length() == 0) {
            inputFolder.setText(AppPrefs.getProperty(AppPrefs.DEFAULT_FOLDER, "main"));
            inputComment.setText(AppPrefs.getProperty(AppPrefs.DEFAULT_COMMENT, "generated_by_WordRider"));
            inputVariable.setText(AppPrefs.getProperty(AppPrefs.DEFAULT_VARIABLE, "unknown"));
        } else {
            inputFolder.setText(fileInfo.getFolderName());
            inputComment.setText(fileInfo.getComment());
            inputVariable.setText(fileInfo.getVarName());
        }

        if (fileInfo instanceof TITextFileInfo) {
            inputPictureFolder.setText(((TITextFileInfo) fileInfo).getPictureFolder());
        }
        boolean instance = fileInfo instanceof TITextFileInfo;
        if (instance)
            switch (((TITextFileInfo) fileInfo).getPictureProcessingType()) {
                case TITextFileInfo.PICTURE_DONTSAVE:
                    checkSave.setSelected(false);
                    radioDontChange.setSelected(true);
                    break;
                case TITextFileInfo.PICTURE_USESAMEASFORDOCUMENT:
                    radioSameAsDocument.setSelected(true);
                    break;
                case TITextFileInfo.PICTURE_FOLDER_DONTCHANGE:
                    radioDontChange.setSelected(true);
                    break;
                case TITextFileInfo.PICTURE_FOLDER_USELAST:
                    radioUseLast.setSelected(true);
                    break;
                case TITextFileInfo.PICTURE_FOLDER_USEOWN:
                    radioSetNew.setSelected(true);
                    break;
                default:
                    break;
            }

        switch (fileInfo.getStoreType()) {
            case TITextFileInfo.STORE_RAM:
                radioStoreType1.setSelected(true);
                break;
            case TITextFileInfo.STORE_RAM_LOCKED:
                radioStoreType2.setSelected(true);
                break;
            case TITextFileInfo.STORE_ARCHIVE:
                radioStoreType3.setSelected(true);
                break;
            default:
                break;
        }
        if (instance)
            checkSave.setSelected(((TITextFileInfo) fileInfo).getPictureProcessingType() != TITextFileInfo.PICTURE_DONTSAVE);
        checkSave_actionPerformed();
        inputPictureFolder.setEnabled(radioSetNew.isSelected());
        Swinger.inputFocus(inputFolder);
    }

    public TIFileInfo getResult() {
//        if (result == RESULT_CANCEL)
//            return null;
//        else
        return fileInfo;
    }

    private void doCancelButtonAction() {
        doClose();
    }

    protected AbstractButton getCancelButton() {
        return btnCancel;
    }

    protected AbstractButton getOkButton() {
        return btnSave;
    }

    private boolean checkNameValidity(final JTextField field, final Component parentTab) {
        boolean result = true;
        final String text = field.getText();
        if ((text == null || text.equals("") && !field.equals(inputComment))) {
            Swinger.showErrorDialog(frame, Lng.getLabel("savesettings.mustbeset"));
            result = false;
        }
//        else if (digitStart && Character.isDigit(text.charAt(0))) {
//            Swinger.showErrorDialog(frame, Lng.getLabel("savesettings.startsdigit"));
//            result = false;
//        } else if (text.length() > maxLenght) {
//            Swinger.showErrorDialog(frame, Lng.getLabel("savesettings.toolong", String.valueOf(maxLenght)));
//            result = false;
//        }
        tabbedPane.setSelectedComponent(parentTab);
        if (!result)
            Swinger.inputFocus(field);
        return result;
    }

    /*
 dal jsem ulozit jako, napsal nejakej nazev promenny, odskranul ulozit s obrazky, ulozil (vcetne nazvu souboru), pak jsem dal znovu ulozit jako, zmenil jsem nazev promenny, zaskrtnul jsem ulozit s obrazky (a nic jsem nezaskrnutl o adresari s obrazkama) a pak jsem dal Save a to hodilo tuhle vyjimku
 */

    private void btnSave_actionPerformed() {
        if (!checkNameValidity(inputFolder, generalPanel) || !checkNameValidity(inputVariable, generalPanel) || !checkNameValidity(inputComment, generalPanel))
            return;
        TITextFileInfo info = null;
        switch (statusWindow) {
            case STATUS_TEXTFILE:
                fileInfo = new TITextFileInfo();
                info = (TITextFileInfo) fileInfo;
                final int selectedIndex = comboOutputFormat.getSelectedIndex();
                info.setOutputFormat(selectedIndex);
                AppPrefs.storeProperty(AppPrefs.LAST_USED_OUTPUTFORMAT, selectedIndex);
                break;
            case STATUS_IMAGE:
                fileInfo = new TIImageFileInfo();
                final boolean insert;
                ((TIImageFileInfo) fileInfo).setInsertIntoDocument(insert = checkInsertIntoDocument.isSelected());
                AppPrefs.storeProperty(AppPrefs.REMEMBER_INSERT_IMAGE, insert);
                break;
            case STATUS_IMAGE_IMAGEPROPERTIES:
                fileInfo = new TIFileInfo();
                break;
        }
        fileInfo.setComment(inputComment.getText());
        fileInfo.setFolderName(inputFolder.getText());
        fileInfo.setVarName(inputVariable.getText());
        final int processingType;
        if (!checkSave.isSelected())
            processingType = TITextFileInfo.PICTURE_DONTSAVE;
        else {
            final AbstractButton button = buttonGroup.getSelected();
            if (button.equals(radioDontChange))
                processingType = TITextFileInfo.PICTURE_FOLDER_DONTCHANGE;
            else if (button.equals(radioUseLast))
                processingType = TITextFileInfo.PICTURE_FOLDER_USELAST;
            else if (button.equals(radioSameAsDocument))
                processingType = TITextFileInfo.PICTURE_USESAMEASFORDOCUMENT;
            else if (button.equals(radioSetNew)) {
                if (!checkNameValidity(inputPictureFolder, pictureTabPanel))
                    return;
                processingType = TITextFileInfo.PICTURE_FOLDER_USEOWN;
                if (info != null) {
                    info.setPictureFolder(inputPictureFolder.getText());
                }
            } else
                processingType = TITextFileInfo.PICTURE_FOLDER_DONTCHANGE;
        }
        final AbstractButton source = radioGroup.getSelected();
        if (source.equals(radioStoreType1)) {
            fileInfo.setStoreType(TITextFileInfo.STORE_RAM, false);
        } else if (source.equals(radioStoreType2)) {
            fileInfo.setStoreType(TITextFileInfo.STORE_RAM_LOCKED, false);
        } else fileInfo.setStoreType(TITextFileInfo.STORE_ARCHIVE, false);
        if (info != null) {
            info.setPictureProcessingType(processingType);
        }
        doClose();
    }

    private void checkSave_actionPerformed() {
        final boolean isEnabled = checkSave.isSelected();
        radioDontChange.setEnabled(isEnabled);
        radioSetNew.setEnabled(isEnabled);
        radioUseLast.setEnabled(isEnabled);
        radioSameAsDocument.setEnabled(isEnabled);
    }


    private final class ActionButtonsAdapter implements java.awt.event.ActionListener {
        public final void actionPerformed(final ActionEvent e) {
            if (e.getSource().equals(btnSave))
                btnSave_actionPerformed();
            else
                doCancelButtonAction();
        }
    }

    private final class CheckSaveAdapter implements java.awt.event.ActionListener {
        public final void actionPerformed(final ActionEvent e) {
            checkSave_actionPerformed();
        }
    }


    private final class RadiosAdapter implements java.awt.event.ActionListener {
        public final void actionPerformed(final ActionEvent e) {
            final boolean setNew = (e.getSource().equals(radioSetNew));
            inputPictureFolder.setEnabled(setNew);
            if (setNew)
                Swinger.inputFocus(inputPictureFolder);
        }
    }

    private void init() {
        inputFolder.setDocument(new LimitedPlainDocument(regexpFolderPattern));
        inputVariable.setDocument(new LimitedPlainDocument(regexpFolderPattern));
        inputComment.setDocument(new LimitedPlainDocument(regexpComment));
        inputPictureFolder.setDocument(new LimitedPlainDocument(regexpFolderPattern));
        final FocusListener focusListener = new Swinger.SelectAllOnFocusListener();
        inputFolder.addFocusListener(focusListener);
        inputVariable.addFocusListener(focusListener);
        inputComment.addFocusListener(focusListener);
        inputPictureFolder.addFocusListener(focusListener);

        //inputVariable.setPreferredSize(new Dimension(70,-1));
        final Container mainPanel = this.getContentPane();
        final JPanel picturePanel = new JPanel(new GridBagLayout());
        mainPanel.setLayout(new GridBagLayout());
        final JLabel labelFolder = Swinger.getLabel("savesettings.input.folder");
        labelFolder.setLabelFor(inputFolder);
        final JLabel labelVariable = Swinger.getLabel("savesettings.input.variable");
        labelVariable.setLabelFor(inputVariable);
        final JLabel labelComment = Swinger.getLabel("savesettings.input.comment");
        labelComment.setLabelFor(inputComment);


        picturePanel.setEnabled(true);
        picturePanel.setBorder(new TitledBorder(BorderFactory.createLineBorder(Color.gray, 1), Lng.getLabel("savesettings.pictures.folder")));
        radioDontChange = Swinger.getRadio("savesettings.pictures.samesource");

        radioSameAsDocument = Swinger.getRadio("savesettings.pictures.samefolder");
        radioUseLast = Swinger.getRadio("savesettings.pictures.lastfolder");
        radioSetNew = Swinger.getRadio("savesettings.pictures.newfolder");

        radioStoreType1 = Swinger.getRadio("savesettings.storetype.ram");
        radioStoreType2 = Swinger.getRadio("savesettings.storetype.ramLocked");
        radioStoreType3 = Swinger.getRadio("savesettings.storetype.archive");
        final Border emptyBorder = BorderFactory.createEmptyBorder(3, 2, 3, 2);
        radioStoreType1.setBorder(emptyBorder);
        radioStoreType2.setBorder(emptyBorder);
        radioStoreType3.setBorder(emptyBorder);
        final ActionListener radioAction = new RadiosAdapter();
        radioSameAsDocument.addActionListener(radioAction);
        radioDontChange.addActionListener(radioAction);
        radioUseLast.addActionListener(radioAction);
        radioSetNew.addActionListener(radioAction);

        final JPanel btnPanel = new JPanel(new EqualsLayout(5));
        final JPanel storeTypePanel = new JPanel();
        storeTypePanel.setLayout(new BoxLayout(storeTypePanel, BoxLayout.X_AXIS));

        final Dimension buttonSize = new Dimension(85, 25);
        btnCancel = Swinger.getButton("savesettings.cancelBtn");
        btnCancel.setMinimumSize(buttonSize);

        btnSave = Swinger.getButton("savesettings.saveBtn");
        btnSave.setMinimumSize(buttonSize);

        final ActionListener actionButtonListener = new ActionButtonsAdapter();
        btnSave.addActionListener(actionButtonListener);
        btnCancel.addActionListener(actionButtonListener);
        checkSave.addActionListener(new CheckSaveAdapter());
        inputFolder.setPreferredSize(new Dimension(80, inputFolder.getPreferredSize().height));
        inputVariable.setPreferredSize(inputFolder.getPreferredSize());
        generalPanel.add(labelFolder, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
                , GridBagConstraints.WEST, GridBagConstraints.VERTICAL, new Insets(3, 4, 4, 0), 0, 0));
        generalPanel.add(labelComment, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
                , GridBagConstraints.WEST, GridBagConstraints.VERTICAL, new Insets(4, 4, 4, 0), 0, 0));
        generalPanel.add(inputFolder, new GridBagConstraints(1, 0, 1, 1, 0.8, 0.0
                , GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(4, 4, 4, 4), (statusWindow != STATUS_TEXTFILE) ? 20 : 0, 0));
        generalPanel.add(inputVariable, new GridBagConstraints(3, 0, 1, 1, 1.0, 0.0
                , GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, new Insets(4, 4, 4, 4), 0, 0));
        generalPanel.add(inputComment, new GridBagConstraints(1, 1, 3, 1, 1.0, 0.0
                , GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(4, 4, 4, 4), 0, 0));
        generalPanel.add(labelVariable, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0
                , GridBagConstraints.WEST, GridBagConstraints.VERTICAL, new Insets(4, 4, 4, 4), 0, 0));

        if (statusWindow == STATUS_TEXTFILE) {
            inputPictureFolder.setMaximumSize(inputFolder.getPreferredSize());
            picturePanel.add(radioDontChange, new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0
                    , GridBagConstraints.WEST, GridBagConstraints.VERTICAL, new Insets(0, 4, 0, 0), 0, 0));
            picturePanel.add(radioUseLast, new GridBagConstraints(0, 1, 2, 1, 0.0, 0.0
                    , GridBagConstraints.WEST, GridBagConstraints.VERTICAL, new Insets(0, 4, 2, 2), 0, 0));
            picturePanel.add(radioSameAsDocument, new GridBagConstraints(0, 2, 2, 1, 0.0, 0.0
                    , GridBagConstraints.WEST, GridBagConstraints.VERTICAL, new Insets(0, 4, 0, 0), 0, 0));
            picturePanel.add(radioSetNew, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
                    , GridBagConstraints.WEST, GridBagConstraints.VERTICAL, new Insets(0, 4, 0, 0), 0, 0));
            picturePanel.add(inputPictureFolder, new GridBagConstraints(1, 3, 1, 1, 1.0, 0.0
                    , GridBagConstraints.WEST, GridBagConstraints.VERTICAL, new Insets(0, 0, 4, 5), 80, 0));
            //, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 4, 5), 0, 0));
            pictureTabPanel.add(checkSave, new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0
                    , GridBagConstraints.WEST, GridBagConstraints.VERTICAL, new Insets(0, 10, 0, 0), 5, 0));

            final JLabel labelOutputFormat = Swinger.getLabel("savesettings.outputFormat");
            final Dimension prefSize = comboOutputFormat.getPreferredSize();
            prefSize.height = 23;
            comboOutputFormat.setPreferredSize(prefSize);
            labelComment.setLabelFor(comboOutputFormat);
            outputFormatPanel.add(labelOutputFormat, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
                    , GridBagConstraints.WEST, GridBagConstraints.VERTICAL, new Insets(3, 4, 4, 0), 0, 0));
            outputFormatPanel.add(comboOutputFormat, new GridBagConstraints(1, 0, 1, 1, 0.8, 0.0
                    , GridBagConstraints.WEST, GridBagConstraints.VERTICAL, new Insets(4, 4, 4, 4), 0, 0));

        }
        radioGroup.add(radioStoreType1);
        radioGroup.add(radioStoreType2);
        radioGroup.add(radioStoreType3);
        buttonGroup.add(radioDontChange);
        buttonGroup.add(radioUseLast);
        buttonGroup.add(radioSameAsDocument);
        buttonGroup.add(radioSetNew);
        storeTypePanel.setBorder(BorderFactory.createTitledBorder(Lng.getLabel("savesettings.storetype")));
        storeTypePanel.add(radioStoreType1);
        storeTypePanel.add(radioStoreType2);
        storeTypePanel.add(radioStoreType3);
        btnPanel.add(btnSave);
        btnPanel.add(btnCancel);
        //btnPanel.setPreferredSize(new Dimension(250, 30));

        pictureTabPanel.add(picturePanel, new GridBagConstraints(0, 1, 4, 1, 1.0, 1.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        generalPanel.add(storeTypePanel, new GridBagConstraints(0, 2, 4, 1, 1.0, 1.0
                , GridBagConstraints.NORTHEAST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 2, 0), 0, 0));
        if (statusWindow == STATUS_TEXTFILE) {
            generalPanel.add(outputFormatPanel, new GridBagConstraints(0, 3, 4, 1, 1.0, 1.0
                    , GridBagConstraints.NORTHEAST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 2, 0), 0, 0));
        }
        if (statusWindow == STATUS_IMAGE)
            generalPanel.add(checkInsertIntoDocument, new GridBagConstraints(0, 3, 4, 1, 0.0, 0.0
                    , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2, 4, 4, 5), 0, 0));
        mainPanel.add(tabbedPane, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0
                , GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(1, 2, 2, 2), 0, 0));
        mainPanel.add(btnPanel, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0
                , GridBagConstraints.SOUTH, GridBagConstraints.BOTH, new Insets(2, 2, 2, 6), 0, 2));

        tabbedPane.addTab(Lng.getLabel("savesettings.tab.general"), generalPanel);
    }

}
