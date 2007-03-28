package net.wordrider.core.actions;

import net.wordrider.core.AppPrefs;
import net.wordrider.core.Lng;
import net.wordrider.core.managers.AreaManager;
import net.wordrider.core.managers.FileInstance;
import net.wordrider.dialogs.CloseDialog;
import net.wordrider.utilities.Consts;
import net.wordrider.utilities.LogUtils;
import net.wordrider.utilities.Swinger;
import net.wordrider.utilities.Utils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Vity
 */
public final class SendToCalcAction extends CoreAction {
    private static final SendToCalcAction instance = new SendToCalcAction();
    private static final String CODE = "SendToCalcAction";

    public static final int TI_CONNECT = 1;
    public static final int TILP = 2;
    private final static Logger logger = Logger.getLogger(SendToCalcAction.class.getName());

    private SendToCalcAction() {
        super(CODE, KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK | InputEvent.ALT_MASK), "ico_send.gif");
    }

    public static int getMethodSelected() {
        final int i = AppPrefs.getProperty(AppPrefs.CALC_SEND_METHOD, Utils.isWindows() ? TI_CONNECT : TILP);
        if (i == TI_CONNECT && !Utils.isWindows())
            return TILP;
        else return i;
    }

    public static String getDefaultPath(final int methodSelected) {
        if (methodSelected == TI_CONNECT) {
            return AppPrefs.getProperty(AppPrefs.TICONNECT_PATH, Consts.TI_CONNECT_PATH);
        } else {
            return AppPrefs.getProperty(AppPrefs.TILP_PATH, Utils.isWindows() ? Consts.TILP_WINDOWS : Consts.TILP_LINUX);
        }
    }


    private static File testMethodSelected(final int methodSelected) {
        //final int methodSelected = getMethodSelected();
        final File file = new File(getDefaultPath(methodSelected));
        if (file.exists() && file.isFile())
            return file;
        return null;
    }

    public static SendToCalcAction getInstance() {
        return instance;
    }

    private static boolean sendOpened(int selectedMethod, File runFile) {
        final AreaManager areaManager = AreaManager.getInstance();
        final Collection<FileInstance> fileInstances = areaManager.getOpenedInstances();
        if (fileInstances.isEmpty())
            return true;
        final JFrame frame = getMainFrame();
        final CloseDialog<FileInstance> dialog = new CloseDialog<FileInstance>(frame, fileInstances, areaManager.getActiveInstance());
        final Collection<FileInstance> selectedList = dialog.getReturnList();
        if (selectedList.isEmpty())
            return true;
        final Collection<FileInstance> modifiedList = areaManager.getModifiedInstances();
        final Collection<File> sendList = new LinkedList<File>();
        final boolean sendWithPictures = AppPrefs.getProperty(AppPrefs.SEND_WITH_PICTURES, true);
        for (FileInstance instance : selectedList) {
            if (!instance.hasAssignedFile() || modifiedList.contains(instance)) {
                final int result = Swinger.getButtonsOption(frame, new String[]{Lng.getLabel("message.button.save"), Lng.getLabel("message.button.skiptThisFile"),
                Lng.getLabel(Swinger.MESSAGE_BTN_CANCEL_CODE)}, Lng.getLabel("message.confirm.fileNotSaved", instance.getName()));
                if (result == Swinger.RESULT_YES) {
                    if (!SaveFileAction.save(false))
                        return false;
                } else if (result != Swinger.RESULT_NO) //cancel
                    return false;
                else continue;
            }
            if (sendWithPictures)
                sendList.addAll(instance.getRiderArea().getAllPictureFilePaths());
            sendList.add(instance.getFile());
        }
        sendFiles(selectedMethod, runFile, sendList);
        return true;
    }

    private static void sendFiles(int selectedMethod, File runFile, Collection<File> sendList) {
        final List<String> commands = new ArrayList<String>();
        commands.add(runFile.getAbsolutePath());
        if (selectedMethod == TILP) {
            final String s = AppPrefs.getProperty(AppPrefs.TILP_PARAMETERS, "");
            if (s.length() > 0) {
                commands.add(s);
            }
        }
        final boolean isWindows = Utils.isWindows();
        for (File file : sendList) {
            if (file.exists() && file.isFile()) {
                final String path = file.getAbsolutePath();
                commands.add(isWindows ? "\"" + path + "\"" : path);
            }
        }
        final String[] cmdList = new String[commands.size()];
        commands.toArray(cmdList);
        final StringBuilder params = new StringBuilder();
        for (String cmd : cmdList) {
            params.append(cmd);
            params.append(' ');
        }
        final String command = params.toString();
        logger.info("Sending files to calc command " + command);
        try {
            Runtime.getRuntime().exec(cmdList);
        } catch (IOException e) {
            LogUtils.processException(logger, e);
            Swinger.showErrorDialog(getMainFrame(), Lng.getLabel("message.error.externProgram", command));
        }
    }


    public final void actionPerformed(final ActionEvent e) {
        int selectedMethod = getMethodSelected();
        File runFile = testMethodSelected(selectedMethod);
        if (runFile == null) {
            Swinger.showErrorDialog(getMainFrame(), Lng.getLabel("message.error.externProgramMissing"));
            ShowUserSettings.getInstance().setUpSendMethod();
            selectedMethod = getMethodSelected();
            runFile = testMethodSelected(selectedMethod);
        }

        if (runFile == null)
            return;
        sendOpened(selectedMethod, runFile);
    }

}
