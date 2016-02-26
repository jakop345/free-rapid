package cz.vity.freerapid.gui.dialogs;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.adapter.Bindings;
import com.jgoodies.common.swing.MnemonicUtils;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.*;
import cz.vity.freerapid.gui.managers.ManagerDirector;
import cz.vity.freerapid.gui.managers.ProxySetManager;
import cz.vity.freerapid.model.ProxySetModel;
import cz.vity.freerapid.model.bean.ProxySet;
import cz.vity.freerapid.swing.ComponentFactory;
import cz.vity.freerapid.swing.Swinger;
import cz.vity.freerapid.swing.components.EditorPaneProxyDetector;
import cz.vity.freerapid.utilities.LogUtils;
import org.jdesktop.swinghelper.buttonpanel.JXButtonPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Set;
import java.util.logging.Logger;

/**
 * @author tong2shot
 */
public class ProxySetManipDialog extends AppDialog {
    private final static Logger logger = Logger.getLogger(ProxySetManipDialog.class.getName());

    enum ManipType {
        ADD,
        EDIT
    }

    private final ManagerDirector director;
    private final ProxySetManager manager;
    private final ManipType manipType;
    private final ProxySet proxySet;
    private PresentationModel<ProxySet> model;

    public ProxySetManipDialog(JDialog owner, ManagerDirector director, ManipType manipType, ProxySet proxySet) throws HeadlessException {
        super(owner, true);
        this.director = director;
        this.manager = director.getProxySetManager();
        this.manipType = manipType;
        this.proxySet = proxySet;
        this.setName("ProxySetManipDialog");
        try {
            initComponents();
            build();
        } catch (Exception e) {
            LogUtils.processException(logger, e);
            doClose();
        }
    }

    @Override
    protected AbstractButton getBtnOK() {
        return btnOk;
    }

    @Override
    protected AbstractButton getBtnCancel() {
        return btnCancel;
    }

    private void build() {
        inject();
        buildModels();
        buildGUI();

        setAction(btnOk, "btnOkAction");
        setAction(btnCancel, "btnCancelAction");

        setTitle(getTitle() + " - " + (manipType == ManipType.ADD ? getResourceMap().getString("titleAdd")
                : getResourceMap().getString("titleEdit")));

        MnemonicUtils.configure(btnOk, (manipType == ManipType.ADD ? getResourceMap().getString("btnOkAddText")
                : getResourceMap().getString("btnOkEditText")));

        pack();
        setResizable(true);
        locateOnOpticalScreenCenter(this);
    }

    @SuppressWarnings("unchecked")
    private void buildModels() {
        if (manipType == ManipType.EDIT) {
            model = new PresentationModel<ProxySet>(proxySet);
            Bindings.bind(fldName, model.getBufferedModel("name"));
            proxyEditorPane.setProxies((Set) model.getBufferedModel("proxies").getValue());
        }
    }

    private void buildGUI() {
        //
    }

    @Override
    public void doClose() {
        super.doClose();
        if (model != null) {
            model.setBean(null);
        }
    }

    @org.jdesktop.application.Action
    public void btnOkAction() {
        if (!validateChanges())
            return;
        setResult(RESULT_OK);
        final String name = fldName.getText().trim();
        final Set<String> proxies = proxyEditorPane.getProxies();
        if (manipType == ManipType.ADD) {
            manager.addProxySetItem(new ProxySet(new ProxySetModel(name, proxies)));
        } else {
            if (model != null) {
                model.getBufferedModel("proxies").setValue(proxyEditorPane.getProxies());
                model.triggerCommit();
                manager.updateProxySetItem(model.getBean());
            }
        }
        doClose();
    }

    @org.jdesktop.application.Action
    public void btnCancelAction() {
        setResult(RESULT_CANCEL);
        if (manipType == ManipType.EDIT) {
            if (model != null) {
                model.triggerFlush();
            }
        }
        doClose();
    }

    private boolean validateChanges() {
        final String name = fldName.getText().trim();
        if (name.isEmpty()) {
            Swinger.showErrorMessage(getResourceMap(), "noNameMessage");
            Swinger.inputFocus(fldName);
            return false;
        }
        if ((manipType == ManipType.EDIT && !name.equals(model.getBean().getName()) && manager.isNameExists(name))
                || (manipType == ManipType.ADD && manager.isNameExists(name))) {
            Swinger.showErrorMessage(getResourceMap(), "nameExists", name);
            Swinger.inputFocus(fldName);
            return false;
        }
        final Set<String> proxies = proxyEditorPane.getProxies();
        return !isValidateListEmpty(proxies);
    }

    private boolean isValidateListEmpty(final Set<String> proxies) {
        if (proxies.isEmpty()) {
            Swinger.showErrorMessage(getResourceMap(), "noProxyMessage");
            Swinger.inputFocus(proxyEditorPane);
            return true;
        }
        return false;
    }

    @SuppressWarnings({"deprecation"})
    private void initComponents() {
        JPanel dialogPane = new JPanel();
        JPanel contentPanel = new JPanel();

        JLabel lblName = new JLabel();
        JLabel lblProxies = new JLabel();
        JScrollPane scrollPane1 = new JScrollPane();
        fldName = new JTextField();
        proxyEditorPane = ComponentFactory.getProxiesEditorPane();
        lblName.setName("lblName");
        lblProxies.setName("lblProxies");
        lblName.setLabelFor(fldName);
        lblProxies.setLabelFor(proxyEditorPane);

        JXButtonPanel buttonBar = new JXButtonPanel();
        btnOk = new JButton();
        btnCancel = new JButton();
        btnOk.setName("btnOk");
        btnCancel.setName("btnCancel");
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
                //======== scrollPane1 ========
                {
                    scrollPane1.setViewportView(proxyEditorPane);
                }

                PanelBuilder contentPanelBuilder = new PanelBuilder(new FormLayout(
                        new ColumnSpec[]{
                                FormSpecs.DEFAULT_COLSPEC,
                                FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
                                new ColumnSpec(ColumnSpec.FILL, Sizes.bounded(Sizes.PREFERRED, Sizes.dluX(140), Sizes.dluX(255)), FormSpec.DEFAULT_GROW)
                        },
                        new RowSpec[]{
                                FormSpecs.DEFAULT_ROWSPEC,
                                FormSpecs.UNRELATED_GAP_ROWSPEC,
                                FormSpecs.DEFAULT_ROWSPEC,
                                FormSpecs.LINE_GAP_ROWSPEC,
                                new RowSpec(RowSpec.FILL, Sizes.bounded(Sizes.PREFERRED, Sizes.dluY(40), Sizes.dluY(55)), FormSpec.DEFAULT_GROW)
                        }), contentPanel);
                contentPanelBuilder.add(lblName, cc.xy(1, 1));
                contentPanelBuilder.add(fldName, cc.xy(3, 1));
                contentPanelBuilder.add(lblProxies, cc.xy(1, 3));
                contentPanelBuilder.add(scrollPane1, cc.xyw(1, 5, 3));

            }
            dialogPane.add(contentPanel, BorderLayout.CENTER);

            //======== buttonBar ========
            {
                buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));

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

                buttonBarBuilder.add(btnOk, cc.xy(3, 1));
                buttonBarBuilder.add(btnCancel, cc.xy(5, 1));
            }
            dialogPane.add(buttonBar, BorderLayout.SOUTH);
        }
        contentPane.add(dialogPane, BorderLayout.CENTER);
    }

    private JTextField fldName;
    private EditorPaneProxyDetector proxyEditorPane;
    private JButton btnOk;
    private JButton btnCancel;
}
