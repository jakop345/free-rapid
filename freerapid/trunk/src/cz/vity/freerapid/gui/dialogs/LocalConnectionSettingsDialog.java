package cz.vity.freerapid.gui.dialogs;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.adapter.Bindings;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.*;
import cz.vity.freerapid.model.DownloadFile;
import cz.vity.freerapid.model.LocalConnectionSettingsType;
import cz.vity.freerapid.swing.Swinger;
import cz.vity.freerapid.utilities.LogUtils;
import org.jdesktop.swinghelper.buttonpanel.JXButtonPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author tong2shot
 */
public class LocalConnectionSettingsDialog extends AppDialog {

    private final static Logger logger = Logger.getLogger(LocalConnectionSettingsDialog.class.getName());

    private final static Pattern PROXY_REGEX_PATTERN = Pattern.compile("((\\w*)(:(.*?))?@)?(.*?):(\\d{2,5})");
    private final static Pattern SOCKS_REGEX_PATTERN = Pattern.compile("(?i)^(\\$SOCKS\\$|SOCKS\\:)");

    private PresentationModel<DownloadFile> model;

    public LocalConnectionSettingsDialog(Frame owner, PresentationModel<DownloadFile> model) {
        super(owner, false);
        this.model = model;
        this.setName("LocalConnectionSettingsDialog");
        try {
            initComponents();
            build();
        } catch (Exception e) {
            LogUtils.processException(logger, e);
            doClose();
        }
    }

    @Override
    protected AbstractButton getBtnCancel() {
        return cancelButton;
    }

    @Override
    protected AbstractButton getBtnOK() {
        return okButton;
    }

    @org.jdesktop.application.Action
    public void cancelBtnAction() {
        model.getBufferedModel("localConnectionSettingsType").setValue(model.getBean().getLocalConnectionSettingsType());
        model.getBufferedModel("localProxy").setValue(model.getBean().getLocalProxy());
        setResult(RESULT_CANCEL);
        doClose();
    }

    @org.jdesktop.application.Action
    public void okBtnAction() {
        if (!validateChanges())
            return;
        setResult(RESULT_OK);
        doClose();
    }

    @Override
    public void doClose() {
        super.doClose();
    }

    private void build() {
        inject();
        buildModels();
        buildGUI();
        setAction(okButton, "okBtnAction");
        setAction(cancelButton, "cancelBtnAction");
    }

    private void buildModels() {
        Bindings.bind(rbApplication, model.getBufferedModel("localConnectionSettingsType"), LocalConnectionSettingsType.APPLICATION);
        Bindings.bind(rbDirect, model.getBufferedModel("localConnectionSettingsType"), LocalConnectionSettingsType.DIRECT);
        Bindings.bind(rbLocalProxy, model.getBufferedModel("localConnectionSettingsType"), LocalConnectionSettingsType.LOCAL_PROXY);
        Bindings.bind(fldLocalProxy, model.getBufferedModel("localProxy"));
    }

    private void buildGUI() {
        rbApplication.setActionCommand(rbApplication.getName());
        rbDirect.setActionCommand(rbDirect.getName());
        rbLocalProxy.setActionCommand(rbLocalProxy.getName());
        ActionListener rbActionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean isLocalProxy = e.getActionCommand().equals(rbLocalProxy.getActionCommand());
                fldLocalProxy.setEnabled(isLocalProxy);
                fldLocalProxy.setEditable(isLocalProxy);
            }
        };
        rbApplication.addActionListener(rbActionListener);
        rbDirect.addActionListener(rbActionListener);
        rbLocalProxy.addActionListener(rbActionListener);

        boolean isLocalProxy = rbLocalProxy.isSelected();
        fldLocalProxy.setEnabled(isLocalProxy);
        fldLocalProxy.setEditable(isLocalProxy);
    }


    private boolean validateChanges() {
        if (rbLocalProxy.isSelected() && !validateProxy(fldLocalProxy.getText())) {
            Swinger.showErrorMessage(this.getResourceMap(), "invalidProxyMessage");
            Swinger.inputFocus(fldLocalProxy);
            fldLocalProxy.selectAll();
            return false;
        }
        return true;
    }

    private boolean validateProxy(String strProxy) {
        final Matcher matcherSocks = SOCKS_REGEX_PATTERN.matcher(strProxy);
        if (matcherSocks.find()) {
            strProxy = strProxy.substring(matcherSocks.group(1).length());
        }
        final Matcher matcher = PROXY_REGEX_PATTERN.matcher(strProxy);
        return matcher.matches();
    }

    @SuppressWarnings({"deprecation"})
    private void initComponents() {
        JPanel dialogPane = new JPanel();
        JPanel contentPanel = new JPanel();

        rbApplication = new JRadioButton();
        rbDirect = new JRadioButton();
        rbLocalProxy = new JRadioButton();
        fldLocalProxy = new JTextField();
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(rbApplication);
        buttonGroup.add(rbDirect);
        buttonGroup.add(rbLocalProxy);
        JLabel lblProxyDesc = new JLabel();

        JXButtonPanel buttonBar = new JXButtonPanel();
        okButton = new JButton();
        cancelButton = new JButton();
        CellConstraints cc = new CellConstraints();

        //======== this ========
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== dialogPane ========
        {
            dialogPane.setBorder(Borders.DIALOG);
            dialogPane.setLayout(new BorderLayout());

            //======== contentPanel ========
            {
                //---- rbApplication ----
                rbApplication.setName("rbApplication");

                //---- rbDirect ----
                rbDirect.setName("rbDirect");

                //---- rbLocalProxy ----
                rbLocalProxy.setName("rbLocalProxy");

                //---- fldLocalProxy ----
                fldLocalProxy.setName("fldLocalProxy");

                //---- lblProxyDesc ----
                lblProxyDesc.setName("lblProxyDesc");

                PanelBuilder contentPanelBuilder = new PanelBuilder(new FormLayout(
                        new ColumnSpec[]{
                                FormSpecs.DEFAULT_COLSPEC,
                        },
                        new RowSpec[]{
                                FormSpecs.DEFAULT_ROWSPEC,
                                FormSpecs.LINE_GAP_ROWSPEC,
                                FormSpecs.DEFAULT_ROWSPEC,
                                FormSpecs.LINE_GAP_ROWSPEC,
                                FormSpecs.DEFAULT_ROWSPEC,
                                FormSpecs.LINE_GAP_ROWSPEC,
                                FormSpecs.DEFAULT_ROWSPEC,
                                FormSpecs.LINE_GAP_ROWSPEC,
                                FormSpecs.DEFAULT_ROWSPEC,
                                FormSpecs.LINE_GAP_ROWSPEC,
                                FormSpecs.DEFAULT_ROWSPEC
                        }), contentPanel);

                contentPanelBuilder.add(rbApplication, cc.xy(1, 1));
                contentPanelBuilder.add(rbDirect, cc.xy(1, 3));
                contentPanelBuilder.add(rbLocalProxy, cc.xy(1, 5));
                contentPanelBuilder.add(fldLocalProxy, cc.xy(1, 7));
                contentPanelBuilder.add(lblProxyDesc, cc.xy(1, 9));
            }
            dialogPane.add(contentPanel, BorderLayout.CENTER);

            //======== buttonBar ========
            {
                buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));

                //---- okButton ----
                okButton.setName("okButton");

                //---- cancelButton ----
                cancelButton.setName("cancelButton");

                PanelBuilder buttonBarBuilder = new PanelBuilder(new FormLayout(
                        new ColumnSpec[]{
                                new ColumnSpec(ColumnSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW),
                                FormSpecs.UNRELATED_GAP_COLSPEC,
                                ColumnSpec.decode("max(pref;42dlu)"),
                                FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
                                FormSpecs.DEFAULT_COLSPEC
                        },
                        RowSpec.decodeSpecs("fill:pref")), buttonBar);
                ((FormLayout) buttonBar.getLayout()).setColumnGroups(new int[][]{{3, 5}});

                buttonBarBuilder.add(okButton, cc.xy(3, 1));
                buttonBarBuilder.add(cancelButton, cc.xy(5, 1));
            }
            dialogPane.add(buttonBar, BorderLayout.SOUTH);
        }
        contentPane.add(dialogPane, BorderLayout.CENTER);
    }

    private JRadioButton rbApplication;
    private JRadioButton rbDirect;
    private JRadioButton rbLocalProxy;
    private JTextField fldLocalProxy;
    private JButton okButton;
    private JButton cancelButton;
}
