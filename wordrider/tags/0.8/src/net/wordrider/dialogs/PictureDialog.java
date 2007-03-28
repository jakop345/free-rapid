package net.wordrider.dialogs;

import net.wordrider.core.Lng;
import net.wordrider.utilities.Utils;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * @author Vity
 */
public final class PictureDialog extends JFileChooser {
    private final ImagePreview imagePreview = new ImagePreview(this);

    public PictureDialog() {
        super();
        init();
    }

    public PictureDialog(final File currentDirectory) {
        super(currentDirectory);    //call to super
        init();
    }

    public final int showInsertDialog(final Component parent) {
        return super.showDialog(parent, Lng.getLabel("picturedialog.insertBtn"));    //call to super
    }

    private void addFilter(final String[] extensions, final String labelCode) {
        this.addChoosableFileFilter(new RiderFileFilter(extensions, labelCode));
    }

    private void init() {
        this.setAcceptAllFileFilterUsed(false);
        addFilter(new String[]{"89i"}, "picturedialog.ti89files");
        addFilter(new String[]{"9xi"}, "picturedialog.ti9xfiles");
        addFilter(new String[]{"92i"}, "picturedialog.ti92files");
        addFilter(new String[]{"bmp"}, "picturedialog.files");
        addFilter(new String[]{"jpeg", "jpg"}, "picturedialog.files");
        addFilter(new String[]{"gif"}, "picturedialog.files");
        addFilter(new String[]{"png"}, "picturedialog.files");
        if (Utils.isJVMVersion(1.5)) {
            addFilter(new String[]{"wbmp"}, "picturedialog.files");
            addFilter(new String[]{"89i", "9xi", "92i", "bmp", "jpeg", "jpg", "gif", "png", "wbmp"}, "picturedialog.allsupported");
        } else
            addFilter(new String[]{"89i", "9xi", "92i", "bmp", "jpeg", "jpg", "gif", "png"}, "picturedialog.allsupported");
        this.setDialogTitle(Lng.getLabel("picturedialog.title"));
//        final String [] st = ImageIO.getReaderFormatNames();
//        for (int i = 0; i < st.length; ++i)
//            System.out.println(st[i]);
        //Add custom icons for file types.
        this.setFileView(new ImageFileView());
        //Add the preview pane.
        this.setAccessory(imagePreview);
    }

    public void freeResources() {
        imagePreview.freeResources();
    }
}
