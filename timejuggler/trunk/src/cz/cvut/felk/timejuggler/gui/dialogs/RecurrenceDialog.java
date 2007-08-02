package cz.cvut.felk.timejuggler.gui.dialogs;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.*;
import cz.cvut.felk.timejuggler.swing.ComponentFactory;
import cz.cvut.felk.timejuggler.swing.Swinger;
import cz.cvut.felk.timejuggler.utilities.LogUtils;
import org.jdesktop.swingx.JXDatePicker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

/**
 * @author Vity
 */
public class RecurrenceDialog extends AppDialog {
    private final static Logger logger = Logger.getLogger(RecurrenceDialog.class.getName());
    private static final String CARD1 = "card1";
    private static final String CARD2 = "card2";
    private static final String CARD3 = "card3";
    private static final String CARD4 = "card4";

    public RecurrenceDialog(Frame owner) throws HeadlessException {
        super(owner, true);
        this.setName("RecurrenceDialog");
        try {
            initComponents();
            build();
        } catch (Exception e) {
            LogUtils.processException(logger, e);
        }
    }

    @Override
    protected AbstractButton getCancelButton() {
        return btnCancel;
    }

    @Override
    protected AbstractButton getOkButton() {
        return btnOK;
    }

    private void build() {
        inject();
        buildGUI();
        buildModels();

        final ActionMap actionMap = getActionMap();
        btnOK.setAction(actionMap.get("okBtnAction"));
        btnCancel.setAction(actionMap.get("cancelBtnAction"));

        setDefaultValues();

        pack();
        setResizable(false);
        locateOnOpticalScreenCenter(this);
    }

    private void setDefaultValues() {
        radioRepeatForever.setSelected(true); //bude odstraneno
        updateRepeatRadios();
    }

    private void buildModels() {
        setComboModelFromResource(comboOccurs);
    }

    @application.Action
    public void okBtnAction() {
        doClose();
    }

    @application.Action
    public void cancelBtnAction() {
        doClose();
    }


    private void buildGUI() {
        ActionListener radioActionListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateRepeatRadios();
            }
        };
        radioRepeatFor.addActionListener(radioActionListener);
        radioRepeatForever.addActionListener(radioActionListener);
        radioRepeatUntil.addActionListener(radioActionListener);
        comboOccurs.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateCardPanel();
            }
        });
    }

    private void updateRepeatRadios() {
        dateUntil.setEnabled(radioRepeatUntil.isSelected());
        spinnerRepeatFor.setEnabled(radioRepeatFor.isSelected());
    }

    private void updateCardPanel() {
        String card;
        switch (comboOccurs.getSelectedIndex()) {
            case 1:
                card = CARD2;
                break;
            case 2:
                card = CARD3;
                break;
            case 3:
                card = CARD4;
                break;
            default:
                card = CARD1;
                break;
        }
        final CardLayout cardLayout = (CardLayout) panelOccurence.getLayout();
        cardLayout.show(panelOccurence, card);
    }

    private ActionMap getActionMap() {
        return Swinger.getActionMap(this.getClass(), this);
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Open Source Project license - unknown
        JPanel dialogPane = new JPanel();
        JPanel contentPanel = new JPanel();
        JLabel labelOccurs = new JLabel();
        comboOccurs = ComponentFactory.getComboBox();
        panelOccurence = new JPanel();
        JPanel panelDaily = new JPanel();
        JLabel labelEvery1 = new JLabel();
        spinnerDaily = new JSpinner();
        JLabel labelDays = new JLabel();
        JPanel panelWeekly = new JPanel();
        JLabel labelEvery2 = new JLabel();
        spinnerWeekly = new JSpinner();
        JLabel labelWeeksOn = new JLabel();
        JPanel panelDaysInWeek = new JPanel();
        checkMonday = new JCheckBox();
        checkFriday = new JCheckBox();
        checkTuesday = new JCheckBox();
        checkSaturday = new JCheckBox();
        checkWednesday = new JCheckBox();
        checkSunday = new JCheckBox();
        checkThursday = new JCheckBox();
        JPanel panelMonthly = new JPanel();
        JLabel labelEvery3 = new JLabel();
        spinnerMonthly = new JSpinner();
        JLabel labelMonthsOnthe = new JLabel();
        radio30thDay = new JRadioButton();
        radioFifthMonday = new JRadioButton();
        radioLastMonday = new JRadioButton();
        JPanel panelAnnually = new JPanel();
        JLabel labelEvery4 = new JLabel();
        spinnerAnnually = new JSpinner();
        JLabel labelYears = new JLabel();
        JPanel panelRepeat = new JPanel();
        radioRepeatForever = new JRadioButton();
        radioRepeatFor = new JRadioButton();
        spinnerRepeatFor = new JSpinner();
        JLabel labelOccurences = new JLabel();
        radioRepeatUntil = new JRadioButton();
        dateUntil = ComponentFactory.getDatePicker();
        JPanel buttonBar = new JPanel();
        btnOK = new JButton();
        btnCancel = new JButton();
        CellConstraints cc = new CellConstraints();

        //======== RecurrenceDialog ========
        {

            Container recurrenceDialogContentPane = this.getContentPane();
            recurrenceDialogContentPane.setLayout(new BorderLayout());

            //======== dialogPane ========
            //======== dialogPane ========
            {
                dialogPane.setBorder(Borders.DIALOG_BORDER);
                dialogPane.setName("dialogPane");
                dialogPane.setLayout(new BorderLayout());

                //======== contentPanel ========
                {
                    contentPanel.setName("contentPanel");

                    //---- labelOccurs ----

                    labelOccurs.setLabelFor(comboOccurs);
                    labelOccurs.setName("labelOccurs");

                    //---- comboOccurs ----
                    comboOccurs.setName("comboOccurs");

                    //======== panelOccurence ========
                    {
                        panelOccurence.setName("panelOccurence");
                        panelOccurence.setLayout(new CardLayout(2, 2));

                        //======== panelDaily ========
                        {
                            panelDaily.setName("panelDaily");

                            //---- labelEvery1 ----

                            labelEvery1.setLabelFor(spinnerDaily);
                            labelEvery1.setName("labelEvery1");

                            //---- spinnerDaily ----
                            spinnerDaily.setModel(new SpinnerNumberModel(1, 1, 999, 1));
                            spinnerDaily.setName("spinnerDaily");

                            //---- labelDays ----

                            labelDays.setLabelFor(spinnerDaily);
                            labelDays.setName("labelDays");

                            PanelBuilder panelDailyBuilder = new PanelBuilder(new FormLayout(
                                    new ColumnSpec[]{
                                            FormFactory.DEFAULT_COLSPEC,
                                            FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                            FormFactory.DEFAULT_COLSPEC,
                                            FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                            new ColumnSpec(ColumnSpec.FILL, Sizes.PREFERRED, FormSpec.DEFAULT_GROW)
                                    },
                                    RowSpec.decodeSpecs("pref")), panelDaily);

                            panelDailyBuilder.add(labelEvery1, cc.xy(1, 1));
                            panelDailyBuilder.add(spinnerDaily, cc.xywh(3, 1, 1, 1, CellConstraints.FILL, CellConstraints.FILL));
                            panelDailyBuilder.add(labelDays, cc.xy(5, 1));
                        }
                        panelOccurence.add(panelDaily, CARD1);

                        //======== panelWeekly ========
                        {
                            panelWeekly.setName("panelWeekly");

                            //---- labelEvery2 ----

                            labelEvery2.setLabelFor(spinnerWeekly);
                            labelEvery2.setName("labelEvery2");

                            //---- spinnerWeekly ----
                            spinnerWeekly.setModel(new SpinnerNumberModel(1, 1, 999, 1));
                            spinnerWeekly.setName("spinnerWeekly");

                            //---- labelWeeksOn ----

                            labelWeeksOn.setLabelFor(spinnerWeekly);
                            labelWeeksOn.setName("labelWeeksOn");

                            //======== panelDaysInWeek ========
                            {
                                panelDaysInWeek.setName("panelDaysInWeek");

                                //---- checkMonday ----

                                checkMonday.setName("checkMonday");

                                //---- checkFriday ----

                                checkFriday.setName("checkFriday");

                                //---- checkTuesday ----

                                checkTuesday.setName("checkTuesday");

                                //---- checkSaturday ----

                                checkSaturday.setName("checkSaturday");

                                //---- checkWednesday ----

                                checkWednesday.setName("checkWednesday");

                                //---- checkSunday ----

                                checkSunday.setName("checkSunday");

                                //---- checkThursday ----

                                checkThursday.setName("checkThursday");

                                PanelBuilder panelDaysInWeekBuilder = new PanelBuilder(new FormLayout(
                                        new ColumnSpec[]{
                                                new ColumnSpec(ColumnSpec.FILL, Sizes.PREFERRED, FormSpec.DEFAULT_GROW),
                                                FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                                new ColumnSpec(ColumnSpec.FILL, Sizes.PREFERRED, FormSpec.DEFAULT_GROW)
                                        },
                                        new RowSpec[]{
                                                FormFactory.DEFAULT_ROWSPEC,
                                                FormFactory.GLUE_ROWSPEC,
                                                FormFactory.DEFAULT_ROWSPEC,
                                                FormFactory.GLUE_ROWSPEC,
                                                FormFactory.DEFAULT_ROWSPEC,
                                                FormFactory.GLUE_ROWSPEC,
                                                FormFactory.DEFAULT_ROWSPEC
                                        }), panelDaysInWeek);

                                panelDaysInWeekBuilder.add(checkMonday, cc.xywh(1, 1, 2, 1));
                                panelDaysInWeekBuilder.add(checkFriday, cc.xy(3, 1));
                                panelDaysInWeekBuilder.add(checkTuesday, cc.xywh(1, 3, 2, 1));
                                panelDaysInWeekBuilder.add(checkSaturday, cc.xy(3, 3));
                                panelDaysInWeekBuilder.add(checkWednesday, cc.xywh(1, 5, 2, 1, CellConstraints.FILL, CellConstraints.DEFAULT));
                                panelDaysInWeekBuilder.add(checkSunday, cc.xy(3, 5));
                                panelDaysInWeekBuilder.add(checkThursday, cc.xy(1, 7));
                            }

                            PanelBuilder panelWeeklyBuilder = new PanelBuilder(new FormLayout(
                                    new ColumnSpec[]{
                                            FormFactory.DEFAULT_COLSPEC,
                                            FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                            FormFactory.PREF_COLSPEC,
                                            FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                            new ColumnSpec(ColumnSpec.FILL, Sizes.PREFERRED, FormSpec.DEFAULT_GROW)
                                    },
                                    new RowSpec[]{
                                            FormFactory.DEFAULT_ROWSPEC,
                                            FormFactory.LINE_GAP_ROWSPEC,
                                            FormFactory.PREF_ROWSPEC
                                    }), panelWeekly);

                            panelWeeklyBuilder.add(labelEvery2, cc.xy(1, 1));
                            panelWeeklyBuilder.add(spinnerWeekly, cc.xywh(3, 1, 1, 1, CellConstraints.FILL, CellConstraints.FILL));
                            panelWeeklyBuilder.add(labelWeeksOn, cc.xy(5, 1));
                            panelWeeklyBuilder.add(panelDaysInWeek, cc.xywh(1, 3, 5, 1));
                        }
                        panelOccurence.add(panelWeekly, CARD2);

                        //======== panelMonthly ========
                        {
                            panelMonthly.setName("panelMonthly");

                            //---- labelEvery3 ----

                            labelEvery3.setLabelFor(spinnerMonthly);
                            labelEvery3.setName("labelEvery3");

                            //---- spinnerMonthly ----
                            spinnerMonthly.setModel(new SpinnerNumberModel(1, 1, 999, 1));
                            spinnerMonthly.setName("spinnerMonthly");

                            //---- labelMonthsOnthe ----

                            labelMonthsOnthe.setLabelFor(spinnerMonthly);
                            labelMonthsOnthe.setName("labelMonthsOnthe");

                            //---- radio30thDay ----

                            radio30thDay.setName("radio30thDay");

                            //---- radioFifthMonday ----

                            radioFifthMonday.setName("radioFifthMonday");

                            //---- radioLastMonday ----

                            radioLastMonday.setName("radioLastMonday");

                            PanelBuilder panelMonthlyBuilder = new PanelBuilder(new FormLayout(
                                    new ColumnSpec[]{
                                            FormFactory.PREF_COLSPEC,
                                            FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                            FormFactory.PREF_COLSPEC,
                                            FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                            new ColumnSpec(Sizes.dluX(59))
                                    },
                                    new RowSpec[]{
                                            FormFactory.PREF_ROWSPEC,
                                            FormFactory.LINE_GAP_ROWSPEC,
                                            FormFactory.DEFAULT_ROWSPEC,
                                            FormFactory.NARROW_LINE_GAP_ROWSPEC,
                                            FormFactory.DEFAULT_ROWSPEC,
                                            FormFactory.NARROW_LINE_GAP_ROWSPEC,
                                            FormFactory.DEFAULT_ROWSPEC
                                    }), panelMonthly);

                            panelMonthlyBuilder.add(labelEvery3, cc.xy(1, 1));
                            panelMonthlyBuilder.add(spinnerMonthly, cc.xywh(3, 1, 1, 1, CellConstraints.FILL, CellConstraints.FILL));
                            panelMonthlyBuilder.add(labelMonthsOnthe, cc.xy(5, 1));
                            panelMonthlyBuilder.add(radio30thDay, cc.xywh(1, 3, 5, 1));
                            panelMonthlyBuilder.add(radioFifthMonday, cc.xywh(1, 5, 5, 1));
                            panelMonthlyBuilder.add(radioLastMonday, cc.xywh(1, 7, 5, 1));
                        }
                        panelOccurence.add(panelMonthly, CARD3);

                        //======== panelAnnually ========
                        {
                            panelAnnually.setName("panelAnnually");

                            //---- labelEvery4 ----

                            labelEvery4.setLabelFor(spinnerAnnually);
                            labelEvery4.setName("labelEvery4");

                            //---- spinnerAnnually ----
                            spinnerAnnually.setModel(new SpinnerNumberModel(1, 1, 999, 1));
                            spinnerAnnually.setName("spinnerAnnually");

                            //---- labelYears ----

                            labelYears.setLabelFor(spinnerAnnually);
                            labelYears.setName("labelYears");

                            PanelBuilder panelAnnuallyBuilder = new PanelBuilder(new FormLayout(
                                    new ColumnSpec[]{
                                            FormFactory.DEFAULT_COLSPEC,
                                            FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                            FormFactory.PREF_COLSPEC,
                                            FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                            new ColumnSpec(ColumnSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW)
                                    },
                                    RowSpec.decodeSpecs("pref")), panelAnnually);

                            panelAnnuallyBuilder.add(labelEvery4, cc.xy(1, 1));
                            panelAnnuallyBuilder.add(spinnerAnnually, cc.xywh(3, 1, 1, 1, CellConstraints.FILL, CellConstraints.FILL));
                            panelAnnuallyBuilder.add(labelYears, cc.xy(5, 1));
                        }
                        panelOccurence.add(panelAnnually, CARD4);
                    }

                    //======== panelRepeat ========
                    {
                        panelRepeat.setName("panelRepeat");

                        //---- radioRepeatForever ----

                        radioRepeatForever.setName("radioRepeatForever");

                        //---- radioRepeatFor ----

                        radioRepeatFor.setName("radioRepeatFor");

                        //---- spinnerRepeatFor ----
                        spinnerRepeatFor.setModel(new SpinnerNumberModel(1, 1, 999, 1));
                        spinnerRepeatFor.setName("spinnerRepeatFor");

                        //---- labelOccurences ----

                        labelOccurences.setLabelFor(spinnerRepeatFor);
                        labelOccurences.setName("labelOccurences");

                        //---- repeatUntil ----

                        radioRepeatUntil.setName("repeatUntil");

                        //---- dateUntil ----
                        dateUntil.setName("dateUntil");

                        PanelBuilder panelRepeatBuilder = new PanelBuilder(new FormLayout(
                                new ColumnSpec[]{
                                        FormFactory.PREF_COLSPEC,
                                        FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                        FormFactory.PREF_COLSPEC,
                                        FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                        new ColumnSpec(ColumnSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW)
                                },
                                new RowSpec[]{
                                        FormFactory.DEFAULT_ROWSPEC,
                                        FormFactory.NARROW_LINE_GAP_ROWSPEC,
                                        FormFactory.DEFAULT_ROWSPEC,
                                        FormFactory.NARROW_LINE_GAP_ROWSPEC,
                                        FormFactory.DEFAULT_ROWSPEC
                                }), panelRepeat);

                        panelRepeatBuilder.add(radioRepeatForever, cc.xy(1, 1));
                        panelRepeatBuilder.add(radioRepeatFor, cc.xy(1, 3));
                        panelRepeatBuilder.add(spinnerRepeatFor, cc.xywh(3, 3, 1, 1, CellConstraints.FILL, CellConstraints.FILL));
                        panelRepeatBuilder.add(labelOccurences, cc.xy(5, 3));
                        panelRepeatBuilder.add(radioRepeatUntil, cc.xy(1, 5));
                        panelRepeatBuilder.add(dateUntil, cc.xywh(3, 5, 3, 1));
                    }

                    PanelBuilder contentPanelBuilder = new PanelBuilder(new FormLayout(
                            new ColumnSpec[]{
                                    FormFactory.PREF_COLSPEC,
                                    FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                    new ColumnSpec("max(pref;50dlu)"),
                                    FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                    new ColumnSpec(ColumnSpec.FILL, Sizes.PREFERRED, FormSpec.DEFAULT_GROW)
                            },
                            new RowSpec[]{
                                    FormFactory.DEFAULT_ROWSPEC,
                                    FormFactory.LINE_GAP_ROWSPEC,
                                    new RowSpec(RowSpec.CENTER, Sizes.PREFERRED, FormSpec.DEFAULT_GROW),
                                    FormFactory.RELATED_GAP_ROWSPEC,
                                    FormFactory.DEFAULT_ROWSPEC
                            }), contentPanel);

                    contentPanelBuilder.add(labelOccurs, cc.xy(1, 1));
                    contentPanelBuilder.add(comboOccurs, cc.xywh(3, 1, 1, 1, CellConstraints.FILL, CellConstraints.DEFAULT));
                    contentPanelBuilder.add(panelOccurence, cc.xywh(3, 3, 3, 2));
                    contentPanelBuilder.add(panelRepeat, cc.xywh(1, 5, 5, 1));
                }
                dialogPane.add(contentPanel, BorderLayout.CENTER);

                //======== buttonBar ========
                {
                    buttonBar.setBorder(Borders.BUTTON_BAR_GAP_BORDER);
                    buttonBar.setName("buttonBar");

                    //---- btnOK ----

                    btnOK.setName("btnOK");

                    //---- btnCancel ----

                    btnCancel.setName("btnCancel");

                    PanelBuilder buttonBarBuilder = new PanelBuilder(new FormLayout(
                            new ColumnSpec[]{
                                    FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                    new ColumnSpec(ColumnSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW),
                                    FormFactory.GLUE_COLSPEC,
                                    FormFactory.PREF_COLSPEC,
                                    FormFactory.RELATED_GAP_COLSPEC,
                                    new ColumnSpec("max(pref;42dlu)")
                            },
                            RowSpec.decodeSpecs("pref")), buttonBar);
                    ((FormLayout) buttonBar.getLayout()).setColumnGroups(new int[][]{{4, 6}});

                    buttonBarBuilder.add(btnOK, cc.xy(4, 1));
                    buttonBarBuilder.add(btnCancel, cc.xy(6, 1));
                }
                dialogPane.add(buttonBar, BorderLayout.SOUTH);
            }
            this.add(dialogPane, BorderLayout.CENTER);
        }

        //---- buttonGroupMonthly ----
        ButtonGroup buttonGroupMonthly = new ButtonGroup();
        buttonGroupMonthly.add(radio30thDay);
        buttonGroupMonthly.add(radioFifthMonday);
        buttonGroupMonthly.add(radioLastMonday);

        //---- buttonGroupRepeat ----
        ButtonGroup buttonGroupRepeat = new ButtonGroup();
        buttonGroupRepeat.add(radioRepeatForever);
        buttonGroupRepeat.add(radioRepeatFor);
        buttonGroupRepeat.add(radioRepeatUntil);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    private JComboBox comboOccurs;
    private JPanel panelOccurence;
    private JSpinner spinnerDaily;
    private JSpinner spinnerWeekly;
    private JCheckBox checkMonday;
    private JCheckBox checkFriday;
    private JCheckBox checkTuesday;
    private JCheckBox checkSaturday;
    private JCheckBox checkWednesday;
    private JCheckBox checkSunday;
    private JCheckBox checkThursday;
    private JSpinner spinnerMonthly;
    private JRadioButton radio30thDay;
    private JRadioButton radioFifthMonday;
    private JRadioButton radioLastMonday;
    private JSpinner spinnerAnnually;
    private JRadioButton radioRepeatForever;
    private JRadioButton radioRepeatFor;
    private JSpinner spinnerRepeatFor;
    private JRadioButton radioRepeatUntil;
    private JXDatePicker dateUntil;
    private JButton btnOK;
    private JButton btnCancel;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
