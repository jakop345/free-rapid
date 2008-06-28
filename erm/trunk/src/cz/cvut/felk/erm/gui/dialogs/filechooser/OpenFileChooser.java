package cz.cvut.felk.erm.gui.dialogs.filechooser;

import java.io.File;

/**
 * @author Ladislav Vitasek
 */
class OpenFileChooser extends JAppFileChooser {

    public OpenFileChooser(File currentDirectory) {
        super(currentDirectory);
        this.setDialogType(OPEN_DIALOG);
    }

    protected String getDialogName() {
        return "OpenFileChooser";
    }
}
