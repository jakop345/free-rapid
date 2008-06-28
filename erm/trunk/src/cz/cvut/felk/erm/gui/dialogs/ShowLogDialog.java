package cz.cvut.felk.erm.gui.dialogs;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.*;
import cz.cvut.felk.erm.core.tasks.CoreTask;
import cz.cvut.felk.erm.gui.dialogs.filechooser.OpenSaveDialogFactory;
import cz.cvut.felk.erm.swing.ComponentFactory;
import cz.cvut.felk.erm.swing.Swinger;
import cz.cvut.felk.erm.utilities.LogUtils;
import cz.cvut.felk.erm.utilities.Utils;
import org.jdesktop.application.Task;
import org.jdesktop.swinghelper.buttonpanel.JXButtonPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.io.FileWriter;
import java.util.logging.Logger;

/**
 * @author Ladislav Vitasek
 */
public class ShowLogDialog extends AppDialog implements ClipboardOwner {
    private final static Logger logger = Logger.getLogger(ShowLogDialog.class.getName());

    public ShowLogDialog(Frame owner) throws Exception {
        super(owner, true);

        this.setName("ShowLogDialog");
        try {
            initComponents();
            build();
        } catch (Exception e) {
            doClose(); //dialog se pri fatalni chybe zavre
            throw e;
        }
    }


    @org.jdesktop.application.Action
    public void okBtnAction() {
        setResult(RESULT_OK);
        doClose();
    }


    @org.jdesktop.application.Action
    public void btnCopyToClipboardAction() {
        StringSelection stringSelection = new StringSelection(textArea.getText());
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, this);
    }

    @org.jdesktop.application.Action(block = Task.BlockingScope.NONE)
    public Task btnSaveLogAction() {
        final File logDialog = OpenSaveDialogFactory.getSaveLogDialog();
        if (logDialog == null)
            return null;
        return new CoreTask(getApp()) {
            protected Object doInBackground() throws Exception {
                this.setUserCanCancel(false);
                message("message.savingFile");
                FileWriter writer = null;
                try {
                    writer = new FileWriter(logDialog);
                    writer.write(textArea.getText());
                } finally {
                    if (writer != null)
                        writer.close();
                }
                return null;
            }

            @Override
            protected void failed(Throwable cause) {
                LogUtils.processException(logger, cause);
                Swinger.showErrorMessage(getResourceMap(), "message.error.savingFileFailed", Utils.getExceptionMessage(cause));
            }
        };
    }

    @Override
    public void doClose() {
        super.doClose();
    }

    @Override
    protected AbstractButton getBtnCancel() {
        return btnOK;
    }

    @Override
    protected AbstractButton getBtnOK() {
        return btnOK;
    }

    public void setLog(String value) {
        textArea.setText(value);
        textArea.setCaretPosition(0);
    }

    private void build() throws CloneNotSupportedException {
        inject();
        buildGUI();

        final ActionMap map = getActionMap();
        btnOK.setAction(map.get("okBtnAction"));
        btnCopyToClipboard.setAction(map.get("btnCopyToClipboardAction"));
        btnSaveLog.setAction(map.get("btnSaveLogAction"));

        buildModels();

        setDefaultValues();

        pack();
        setResizable(true);
        locateOnOpticalScreenCenter(this);
    }

    private void setDefaultValues() {

    }

    private void buildGUI() {
        textArea.setFont(textArea.getFont().deriveFont(Font.PLAIN, 8));
//        Swinger.inputFocus(btnOK);
    }


    private void buildModels() throws CloneNotSupportedException {

        bindBasicComponents();
    }

    private ActionMap getActionMap() {
        return Swinger.getActionMap(this.getClass(), this);
    }


    private void bindBasicComponents() {

    }


    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Open Source Project license - unknown
        //ResourceBundle bundle = ResourceBundle.getBundle("ShowLogDialog");
        JPanel dialogPane = new JPanel();
        JPanel contentPanel = new JPanel();
        JScrollPane scrollPane = new JScrollPane();
        textArea = ComponentFactory.getSQLArea();
        JXButtonPanel buttonBar = new JXButtonPanel();
        btnCopyToClipboard = new JButton();
        btnSaveLog = new JButton();
        btnOK = new JButton();
        CellConstraints cc = new CellConstraints();

        //======== this ========
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== dialogPane ========
        {
            dialogPane.setBorder(Borders.DIALOG_BORDER);
            dialogPane.setLayout(new BorderLayout());

            //======== contentPanel ========
            {
                contentPanel.setLayout(new BorderLayout());

                //======== scrollPane ========
                {
                    scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

                    //---- textArea ----
                    textArea.setPreferredSize(new Dimension(450, 400));
                    textArea.setEditable(false);
                    scrollPane.setViewportView(textArea);
                }
                contentPanel.add(scrollPane, BorderLayout.CENTER);
            }
            dialogPane.add(contentPanel, BorderLayout.CENTER);

            //======== buttonBar ========
            {
                buttonBar.setBorder(Borders.BUTTON_BAR_GAP_BORDER);

                //---- btnCopyToClipboard ----
                btnCopyToClipboard.setName("btnCopyToClipboard");

                //---- btnSaveLog ----
                btnSaveLog.setName("btnSaveLog");

                //---- btnOK ----
                btnOK.setName("btnOK");

                PanelBuilder buttonBarBuilder = new PanelBuilder(new FormLayout(
                        new ColumnSpec[]{
                                ComponentFactory.BUTTON_COLSPEC,
                                FormFactory.RELATED_GAP_COLSPEC,
                                ComponentFactory.BUTTON_COLSPEC,
                                FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                new ColumnSpec(ColumnSpec.FILL, Sizes.PREFERRED, FormSpec.DEFAULT_GROW),
                                ComponentFactory.BUTTON_COLSPEC
                        },
                        RowSpec.decodeSpecs("pref")), buttonBar);

                buttonBarBuilder.add(btnCopyToClipboard, cc.xy(1, 1));
                buttonBarBuilder.add(btnSaveLog, cc.xy(3, 1));
                buttonBarBuilder.add(btnOK, cc.xy(6, 1));
            }
            dialogPane.add(buttonBar, BorderLayout.SOUTH);
        }
        contentPane.add(dialogPane, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner Open Source Project license - unknown
    private JEditorPane textArea;
    private JButton btnCopyToClipboard;
    private JButton btnSaveLog;
    private JButton btnOK;

    public void lostOwnership(Clipboard clipboard, Transferable contents) {

    }
}