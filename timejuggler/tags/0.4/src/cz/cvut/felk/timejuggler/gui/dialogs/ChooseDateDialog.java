package cz.cvut.felk.timejuggler.gui.dialogs;

import com.jgoodies.binding.adapter.Bindings;
import com.jgoodies.binding.value.BufferedValueModel;
import com.jgoodies.binding.value.Trigger;
import com.jgoodies.binding.value.ValueHolder;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.*;
import cz.cvut.felk.timejuggler.swing.ComponentFactory;
import cz.cvut.felk.timejuggler.swing.Swinger;
import cz.cvut.felk.timejuggler.utilities.LogUtils;
import org.jdesktop.application.Action;
import org.jdesktop.swingx.JXDatePicker;

import javax.swing.*;
import java.awt.*;
import java.util.Date;
import java.util.logging.Logger;

/**
 * @author Vity
 */
public class ChooseDateDialog extends AppDialog {
    private final static Logger logger = Logger.getLogger(ChooseDateDialog.class.getName());

    //   private static final String PROPERTY_DATE = "value";
    private final ValueHolder dateModel;
    private Trigger trigger;
    private BufferedValueModel model;

    //TODO pridat ikonu pro dialog
    public ChooseDateDialog(Frame owner, ValueHolder dateModel) throws HeadlessException {
        super(owner, true);
        // dateModel.setIdentityCheckEnabled(true);
        this.dateModel = dateModel;
        this.setName("ChooseDateDialog");

        try {
            initComponents();
            build();
        } catch (Exception e) {
            LogUtils.processException(logger, e);
            doClose(); // pri otevirani vyjimce se dialog neotevre = fatalni chyba
        }
    }


    @Override
    protected AbstractButton getBtnCancel() {
        return btnCancel;
    }

    @Override
    protected AbstractButton getBtnOK() {
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

    private void buildGUI() {
    }

    private void setDefaultValues() {
        trigger.triggerFlush();
    }

    private void buildModels() {
        trigger = new Trigger();

        model = new BufferedValueModel(dateModel, trigger);
        //final JFormattedTextField formattedField = BasicComponentFactory.createDateField(model);
        //  formattedField.setFormatterFactory(new JXDatePickerFormatterFactory());
        Bindings.bind(fieldDate.getEditor(), model);
        //fieldDate.setEditor(formattedField);
        //fieldDate.setFormats(new String[]{"d.M.yyyy"});
        //final Action actionOK = getActionMap().get("okBtnAction");

//        final PropertyConnector connector1 = PropertyConnector.connect(model, BufferedValueModel.PROPERTYNAME_BUFFERING, actionOK, "enabled");
//        connector1.updateProperty2();
    }

    @Action
    public void okBtnAction() {
        if (!validateForm()) {
            return;
        }
        trigger.triggerCommit();
        setResult(RESULT_OK);
        doClose();
    }

    @Action
    public void cancelBtnAction() {
        trigger.triggerFlush();
        doClose();
    }

    @Override
    public void doClose() {
        if (model != null)
            model.release();
        super.doClose();
    }

    private void initComponents() {
        JPanel dialogPane = new JPanel();
        JPanel contentPanel = new JPanel();
        JLabel labelName = new JLabel();
        fieldDate = ComponentFactory.getDatePicker();
        //checkUseColor = new JCheckBox();
        //comboColor = ComponentFactory.getColorComboBox();
        JPanel buttonBar = new JPanel();
        btnOK = new JButton();
        btnCancel = new JButton();
        CellConstraints cc = new CellConstraints();

        //======== ChooseDateDialog ========
        {
            Container contentPane = this.getContentPane();
            contentPane.setLayout(new BorderLayout());

            //======== dialogPane ========
            {
                dialogPane.setBorder(Borders.DIALOG_BORDER);
                dialogPane.setName("dialogPane");
                dialogPane.setLayout(new BorderLayout());

                //======== contentPanel ========
                {
                    contentPanel.setName("contentPanel");

                    //---- labelName ----

                    labelName.setLabelFor(fieldDate);
                    labelName.setName("labelName");

                    //---- fieldName ----
                    fieldDate.setName("fieldDate");

                    //---- checkUseColor ----

                    //checkUseColor.setName("checkUseColor");

                    //---- comboColor ----
                    // comboColor.setName("comboColor");

                    PanelBuilder contentPanelBuilder = new PanelBuilder(new FormLayout(
                            new ColumnSpec[]{
                                    FormFactory.DEFAULT_COLSPEC,
                                    FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                    new ColumnSpec("max(pref;70dlu)")
                            },
                            new RowSpec[]{
                                    FormFactory.PREF_ROWSPEC,
                                    FormFactory.UNRELATED_GAP_ROWSPEC,
                                    FormFactory.DEFAULT_ROWSPEC,
                                    FormFactory.LINE_GAP_ROWSPEC,
                                    FormFactory.DEFAULT_ROWSPEC
                            }), contentPanel);

                    contentPanelBuilder.add(labelName, cc.xywh(1, 1, 1, 1, CellConstraints.RIGHT, CellConstraints.DEFAULT));
                    contentPanelBuilder.add(fieldDate, cc.xy(3, 1));
//                    contentPanelBuilder.add(checkUseColor, cc.xy(1, 3));
//                    contentPanelBuilder.add(comboColor, cc.xy(3, 3));
                }
                dialogPane.add(contentPanel, BorderLayout.CENTER);

                //======== buttonBar ========
                {
                    buttonBar.setBorder(Borders.BUTTON_BAR_GAP_BORDER);
                    buttonBar.setName("buttonBar");

                    //---- okButton ----

                    btnOK.setName("okButton");

                    //---- cancelButton ----
                    btnCancel.setName("cancelButton");

                    PanelBuilder buttonBarBuilder = new PanelBuilder(new FormLayout(
                            new ColumnSpec[]{
                                    new ColumnSpec(ColumnSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW),
                                    new ColumnSpec("max(pref;42dlu)"),
                                    FormFactory.RELATED_GAP_COLSPEC,
                                    FormFactory.PREF_COLSPEC
                            },
                            RowSpec.decodeSpecs("pref")), buttonBar);
                    ((FormLayout) buttonBar.getLayout()).setColumnGroups(new int[][]{{2, 4}});

                    buttonBarBuilder.add(btnOK, cc.xy(2, 1));
                    buttonBarBuilder.add(btnCancel, cc.xy(4, 1));
                }
                dialogPane.add(buttonBar, BorderLayout.SOUTH);
            }
            contentPane.add(dialogPane);
        }
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    private ActionMap getActionMap() {
        return Swinger.getActionMap(this.getClass(), this);
    }

    private boolean validateForm() {
        final Date value = (Date) model.getValue();
        return validateNonEmpty(fieldDate.getEditor(), value);
    }

    private boolean validateNonEmpty(JFormattedTextField editor, Date value) {
        return value != null;
    }


    private JXDatePicker fieldDate;

    private JButton btnOK;
    private JButton btnCancel;


}