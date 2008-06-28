package cz.cvut.felk.erm.gui.dialogs.filechooser;

import java.io.File;

/**
 * @author Ladislav Vitasek
 */
class SaveFileChooser extends JAppFileChooser {

    public SaveFileChooser(File currentDirectory) {
        super(currentDirectory);
        this.setDialogType(SAVE_DIALOG);
    }

    protected String getDialogName() {
        return "SaveFileChooser";
    }

}