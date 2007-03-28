package net.wordrider.dialogs;

import net.wordrider.core.AppPrefs;
import net.wordrider.core.Lng;
import net.wordrider.core.swing.CompTitledPane;
import net.wordrider.utilities.Consts;
import net.wordrider.utilities.LogUtils;
import net.wordrider.utilities.Swinger;
import net.wordrider.utilities.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusListener;
import java.util.logging.Logger;

/**
 * @author Vity
 */
public final class ConnectDialog extends AppDialog {
    private final JTextField inputLoginName = new JTextField();
    private JCheckBox checkProxy = Swinger.getCheckBox("dialog.connect.checkProxy");
    private JButton btnCheck;
    private JButton btnCancel;
    private final JTextField inputHostName = new JTextField();
    private JCheckBox checkAuthentification;
    private final JTextField inputPortNumber = new JTextField();
    private JCheckBox checkSavePassword = new JCheckBox();
    private final JTextField inputPassword = new JPasswordField();
    private final CompTitledPane panelProxyPane = new CompTitledPane(checkProxy);
    private final JPanel panelProxy = panelProxyPane.getContentPane();
    private JLabel labelLoginName = Swinger.getLabel("dialog.connect.labelLoginName");
    private JLabel labelPassword = Swinger.getLabel("dialog.connect.labelPassword");
    private JLabel labelWarning = Swinger.getLabel("dialog.connect.labelWarning");
    private final static Logger logger = Logger.getLogger(ConnectDialog.class.getName());

    public ConnectDialog(final Frame owner) throws HeadlessException {
        super(owner, true);    //call to super

        try {
            init();
            initData();
            useHttpProxy(checkProxy.isSelected());
            useAuthentification(checkAuthentification.isSelected());
            this.setModal(true);
            this.setTitle(Lng.getLabel("dialog.connect.title", Consts.APPVERSION));
            this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            this.pack();
            //this.setSize(305, 235);
            Swinger.centerDialog(owner, this);
            this.setVisible(true);
        } catch (Exception e) {
            LogUtils.processException(logger, e);
        }
    }

    private void initData() {
        checkProxy.setSelected(AppPrefs.getProperty(AppPrefs.PROXY_USE, false));
        checkAuthentification.setSelected(AppPrefs.getProperty(AppPrefs.PROXY_LOGIN, false));
        inputHostName.setText(AppPrefs.getProperty(AppPrefs.PROXY_URL, ""));
        inputPortNumber.setText(AppPrefs.getProperty(AppPrefs.PROXY_PORT, ""));
        checkSavePassword.setSelected(AppPrefs.getProperty(AppPrefs.PROXY_SAVEPASSWORD, false));
        inputLoginName.setText(AppPrefs.getProperty(AppPrefs.PROXY_USERNAME, ""));
        inputPassword.setText(Utils.generateXorString(AppPrefs.getProperty(AppPrefs.PROXY_PASSWORD, "")));
    }

    private void doCancelButtonAction() {
        result = AppDialog.RESULT_CANCEL;
        doClose();
    }

    protected final AbstractButton getCancelButton() {
        return btnCancel;
    }

    protected final AbstractButton getOkButton() {
        return btnCheck;
    }

    private void btnCheck_actionPerformed() {

        if (checkProxy.isSelected()) {
            if (inputHostName.getText().length() == 0) {
                Swinger.showErrorDialog((Frame) this.getParent(), Lng.getLabel("dialog.connect.message.enterHostName"));
                Swinger.inputFocus(inputHostName);
                return;
            }
            if (inputPortNumber.getText().length() == 0) {
                Swinger.showErrorDialog((Frame) this.getParent(), Lng.getLabel("dialog.connect.message.enterPort"));
                Swinger.inputFocus(inputPortNumber);
                return;
            }
            if (checkAuthentification.isSelected()) {
                if (inputLoginName.getText().length() == 0) {
                    Swinger.showErrorDialog((Frame) this.getParent(), Lng.getLabel("dialog.connect.message.enterLogin"));
                    Swinger.inputFocus(inputLoginName);
                    return;
                }
            }

        }

        AppPrefs.storeProperty(AppPrefs.PROXY_USE, checkProxy.isSelected());
        AppPrefs.storeProperty(AppPrefs.PROXY_LOGIN, checkAuthentification.isSelected());
        AppPrefs.storeProperty(AppPrefs.PROXY_URL, inputHostName.getText());
        AppPrefs.storeProperty(AppPrefs.PROXY_PORT, inputPortNumber.getText());
        AppPrefs.storeProperty(AppPrefs.PROXY_SAVEPASSWORD, checkSavePassword.isSelected());
        AppPrefs.storeProperty(AppPrefs.PROXY_USERNAME, inputLoginName.getText());
        AppPrefs.storeProperty(AppPrefs.PROXY_PASSWORD, Utils.generateXorString(inputPassword.getText()));
        result = AppDialog.RESULT_OK;
        doClose();
    }

    private final class CheckAdapter implements java.awt.event.ActionListener {
        public final void actionPerformed(final ActionEvent e) {
            if (e.getSource().equals(checkProxy)) {
                useHttpProxy(checkProxy.isSelected());
            } else if (e.getSource().equals(checkAuthentification)) {
                useAuthentification(checkAuthentification.isSelected());
            }
        }
    }

    private void useAuthentification(final boolean use) {
        inputLoginName.setEnabled(use);
        inputPassword.setEnabled(use);
        checkSavePassword.setEnabled(use);
        labelLoginName.setEnabled(use);
        labelPassword.setEnabled(use);
        labelWarning.setEnabled(use);
        if (use)
            Swinger.inputFocus(inputLoginName);
    }

    private void useHttpProxy(final boolean use) {
        checkProxy.setSelected(use);
        final Component comp[] = panelProxy.getComponents();
        for (Component aComp : comp) {
            if (!comp.equals(checkProxy))
                aComp.setEnabled(use);
        }
        if (checkAuthentification.isSelected() && use)
            useAuthentification(true);
        else
            useAuthentification(false);
        if (use) {
            Swinger.inputFocus(inputHostName);
        }
        checkProxy.setEnabled(true);
    }

    private final class ActionButtonsAdapter implements ActionListener {
        public final void actionPerformed(final ActionEvent e) {
            if (e.getSource().equals(btnCheck))
                btnCheck_actionPerformed();
            else
                doCancelButtonAction();
        }
    }

    private void init() {
        final Container panelConnect = this.getContentPane();
        panelConnect.setLayout(new GridBagLayout());
        panelProxy.setLayout(new GridBagLayout());
        final ActionListener checkListener = new CheckAdapter();

        inputPortNumber.setDocument(new LimitedPlainDocument("[0-9]{0,6}"));

        Swinger.addKeyActions(inputHostName);
        Swinger.addKeyActions(inputLoginName);
        Swinger.addKeyActions(inputPassword);
        Swinger.addKeyActions(inputPortNumber);

        final int inputHeight = inputLoginName.getPreferredSize().height;
        final FocusListener focusListener = new Swinger.SelectAllOnFocusListener();
        inputHostName.addFocusListener(focusListener);
        inputPortNumber.addFocusListener(focusListener);
        inputLoginName.addFocusListener(focusListener);
        inputPassword.addFocusListener(focusListener);
        inputHostName.setPreferredSize(new Dimension(75, inputHeight));
        inputPortNumber.setPreferredSize(new Dimension(50, inputHeight));
        checkProxy.addActionListener(checkListener);
        //panelConnect.add(checkProxy, new GridBagConstraints(0, 0, 3, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.VERTICAL, new Insets(1, 1, 1, 1), 0, 0));

        //final Border titleBorder = new TitledBorder(BorderFactory.createLineBorder(Color.gray, 1), Lng.getLabel("dialog.connect.labelSettings"));
        //BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(4,4,4,4),BorderFactory.createEtchedBorder())
        //panelProxy.setBorder(new CompTitledBorder(checkProxy, ));

        final JLabel labelHostName = new JLabel(Lng.getLabel("dialog.connect.labelHostName"));
        labelHostName.setLabelFor(inputHostName);
        panelProxy.add(labelHostName, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1, 1, 1, 1), 0, 0));
        panelProxy.add(inputHostName, new GridBagConstraints(1, 0, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(1, 1, 1, 1), 0, 0));

        final JLabel labelPortNumber = new JLabel(Lng.getLabel("dialog.connect.labelPortNumber"));
        labelPortNumber.setLabelFor(inputPortNumber);
        panelProxy.add(labelPortNumber, new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1, 4, 1, 1), 0, 0));
        panelProxy.add(inputPortNumber, new GridBagConstraints(3, 0, 1, 1, 0.1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(1, 1, 1, 1), 0, 0));

        panelProxy.add(inputPassword, new GridBagConstraints(1, 3, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(1, 1, 1, 1), 0, 0));

        checkAuthentification = Swinger.getCheckBox("dialog.connect.checkAutentification");
        checkAuthentification.addActionListener(checkListener);
        panelProxy.add(checkAuthentification, new GridBagConstraints(0, 1, 3, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1, 1, 1, 1), 0, 0));

        labelLoginName.setLabelFor(inputLoginName);

        panelProxy.add(labelLoginName, new GridBagConstraints(0, 2, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1, 1, 1, 1), 0, 0));
        panelProxy.add(inputLoginName, new GridBagConstraints(1, 2, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(1, 1, 1, 1), 0, 0));

        labelPassword.setLabelFor(inputPassword);
        panelProxy.add(labelPassword, new GridBagConstraints(0, 3, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1, 1, 1, 1), 0, 2));

        checkSavePassword = Swinger.getCheckBox("dialog.connect.checkSavePassword");
        panelProxy.add(checkSavePassword, new GridBagConstraints(2, 3, 2, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1, 2, 1, 1), 0, 4));

        panelProxy.add(labelWarning, new GridBagConstraints(0, 4, 4, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1, 1, 1, 1), 0, 0));

        final JPanel btnPanel = new JPanel(new net.wordrider.dialogs.layouts.EqualsLayout(5));
        btnCheck = Swinger.getButton("dialog.connect.btnCheck");
        panelConnect.add(panelProxyPane, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(1, 1, 1, 1), 0, 0));
        panelConnect.add(btnPanel, new GridBagConstraints(0, 1, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 6), 0, 6));


        btnCancel = Swinger.getButton("dialog.connect.btnCancel");


        final Dimension buttonSize = new Dimension(85, 25);

        final ActionListener actionButtonListener = new ActionButtonsAdapter();
        btnCheck.addActionListener(actionButtonListener);
        btnCancel.addActionListener(actionButtonListener);

        btnCheck.setRolloverEnabled(false);
        btnCancel.setRolloverEnabled(false);

        btnCheck.setMinimumSize(buttonSize);
        btnCancel.setMinimumSize(buttonSize);

        btnPanel.add(btnCheck);
        btnPanel.add(btnCancel);

    }
}
