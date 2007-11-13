package net.wordrider.dialogs;

import net.wordrider.core.AppPrefs;
import net.wordrider.core.Lng;
import net.wordrider.dialogs.layouts.EqualsLayout;
import net.wordrider.utilities.LogUtils;
import net.wordrider.utilities.Swinger;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

/**
 * @author Vity
 */
public final class ChooseFormatDialog extends AppDialog {

    public static final int CHOOSE_INPUT_FORMAT = 0;
    public static final int CHOOSE_OUTPUT_FORMAT = 1;
    private JRadioButton radioChoose1;
    private JRadioButton radioChoose2;

    private JButton btnCancel;
    private JButton btnSave;

    private final JCheckBox dontShowAgain = Swinger.getCheckBox("dialog.chooseFormat.dontshow");
    private int dialogType;
    private final static Logger logger = Logger.getLogger(ChooseFormatDialog.class.getName());

    public ChooseFormatDialog(final Frame owner, final int dialogType) {
        super(owner, true);
        this.dialogType = dialogType;
        try {
            init();
        } catch (Exception e) {
            LogUtils.processException(logger, e);
        }
        initDialogContents();
        this.pack();
        Swinger.centerDialog(owner, this);
        this.setModal(true);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);


        this.setVisible(true);
    }


    private void initDialogContents() {
        switch (dialogType) {
            case CHOOSE_INPUT_FORMAT:
                this.setTitle(Lng.getLabel("dialog.chooseFormat.title2"));
                final boolean isHibview = AppPrefs.getProperty(AppPrefs.TIINPUTTEXTFORMAT, true);
                radioChoose1.setSelected(isHibview);
                radioChoose2.setSelected(!isHibview);
                dontShowAgain.setSelected(!AppPrefs.getProperty(AppPrefs.SHOWINPUTFORMAT, true));
                break;
            case CHOOSE_OUTPUT_FORMAT:
                this.setTitle(Lng.getLabel("dialog.chooseFormat.title"));
                final boolean ti92ImageFormat = AppPrefs.getProperty(AppPrefs.TI92IMAGEFORMAT, false);
                radioChoose1.setSelected(!ti92ImageFormat);
                radioChoose2.setSelected(ti92ImageFormat);
                dontShowAgain.setSelected(!AppPrefs.getProperty(AppPrefs.SHOW_IMAGEFORMAT, true));
                break;
            default:
                throw new IllegalArgumentException("Not defined argument");
        }
    }

    public final int getResult() {
        return result;
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


    private void btnSave_actionPerformed() {
        switch (dialogType) {
            case CHOOSE_INPUT_FORMAT:
                AppPrefs.storeProperty(AppPrefs.SHOWINPUTFORMAT, !dontShowAgain.isSelected());
                AppPrefs.storeProperty(AppPrefs.TIINPUTTEXTFORMAT, radioChoose1.isSelected());
                break;
            case CHOOSE_OUTPUT_FORMAT:
                AppPrefs.storeProperty(AppPrefs.SHOW_IMAGEFORMAT, !dontShowAgain.isSelected());
                AppPrefs.storeProperty(AppPrefs.TI92IMAGEFORMAT, radioChoose2.isSelected());
                break;
            default:
                throw new IllegalArgumentException("Not defined argument");
        }
        result = RESULT_OK;
        doClose();
    }


    private final class ActionButtonsAdapter implements java.awt.event.ActionListener {
        public final void actionPerformed(final ActionEvent e) {
            if (e.getSource().equals(btnSave))
                btnSave_actionPerformed();
            else
                doCancelButtonAction();
        }
    }


    private void init() {

        final Container mainPanel = this.getContentPane();
        final JPanel radioPanel = new JPanel(new BorderLayout());

        mainPanel.setLayout(new GridBagLayout());
        radioPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.gray, 1), Lng.getLabel("dialog.chooseFormat.formats")));
        switch (dialogType) {
            case CHOOSE_INPUT_FORMAT:
                radioChoose1 = Swinger.getRadio("dialog.chooseFormat.hibview");
                radioChoose2 = Swinger.getRadio("dialog.chooseFormat.txtrider");
                break;
            case CHOOSE_OUTPUT_FORMAT:
                radioChoose1 = Swinger.getRadio("dialog.chooseFormat.ti89choice");
                radioChoose2 = Swinger.getRadio("dialog.chooseFormat.ti92choice");
                break;
            default:
                throw new IllegalArgumentException("Not defined argument");
        }


        final JPanel btnPanel = new JPanel(new EqualsLayout(5));
        btnPanel.setPreferredSize(new Dimension(250, 28));
        final Dimension buttonSize = new Dimension(85, 25);
        btnCancel = Swinger.getButton("dialog.chooseFormat.cancelBtn");
        btnCancel.setMinimumSize(buttonSize);

        btnSave = Swinger.getButton("dialog.chooseFormat.saveBtn");
        btnSave.setMinimumSize(buttonSize);


        final ActionListener actionButtonListener = new ActionButtonsAdapter();
        btnSave.addActionListener(actionButtonListener);
        btnCancel.addActionListener(actionButtonListener);
        radioPanel.add(radioChoose1, BorderLayout.NORTH);
        radioPanel.add(radioChoose2, BorderLayout.SOUTH);
        final Border radioBorder = BorderFactory.createEmptyBorder(2, 2, 2, 2);
        radioChoose1.setBorder(radioBorder);
        radioChoose2.setBorder(radioBorder);

//        radioPanel.add(radioChoose1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
//                , GridBagConstraints.WEST, GridBagConstraints.VERTICAL, new Insets(0, 4, 0, 0), 0, 0));
//        radioPanel.add(radioChoose2, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
//                , GridBagConstraints.WEST, GridBagConstraints.VERTICAL, new Insets(0, 4, 2, 2), 0, 0));
        final JButtonGroup group = new JButtonGroup();
        group.add(radioChoose1);
        group.add(radioChoose2);
        mainPanel.add(radioPanel, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(4, 4, 4, 4), 0, 0));
        mainPanel.add(dontShowAgain, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 0), 0, 0));
        btnPanel.add(btnSave);
        btnPanel.add(btnCancel);
        mainPanel.add(btnPanel, new GridBagConstraints(0, 2, 1, 1, 1.0, 1.0
                , GridBagConstraints.SOUTH, GridBagConstraints.BOTH, new Insets(2, 2, 2, 6), 0, 2));
    }

}
