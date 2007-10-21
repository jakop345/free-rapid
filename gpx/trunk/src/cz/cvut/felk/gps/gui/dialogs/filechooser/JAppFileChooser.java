package cz.cvut.felk.gps.gui.dialogs.filechooser;

import org.jdesktop.appframework.swingx.SingleXFrameApplication;
import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationContext;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * @author Kleopatra
 * @author Vity
 */
class JAppFileChooser extends JFileChooser {

    public JAppFileChooser(File currentDirectory) {
        super(currentDirectory);
    }

    @Override
    protected JDialog createDialog(Component parent) throws HeadlessException {
        JDialog dialog = super.createDialog(parent);
        Application application = Application.getInstance(Application.class);
        ApplicationContext context = application.getContext();
        if (context.getApplication() instanceof SingleXFrameApplication) {
            dialog.setName("JAppFileChooser");
            ((SingleXFrameApplication) context.getApplication()).prepareDialog(dialog, false);
        }
        return dialog;
    }

}