package net.wordrider.dialogs;


import javax.swing.filechooser.FileView;
import java.io.File;

final class ImageFileView extends FileView {
    public final String getName(final File f) {
        return null;
    }

    public final String getDescription(final File f) {
        return null;
    }

    public final Boolean isTraversable(final File f) {
        return null;
    }

//    public final String getTypeDescription(final File f) {
//        //        final String extension = Utils.getExtension(f);
//        final String type = null;
//
//        //        if (extension != null) {
//        //            if (extension.equals(Utils.jpeg) ||
//        //                extension.equals(Utils.jpg)) {
//        //                type = "JPEG Image";
//        //            }
//        //        }
//        return type;
//    }

//    public final Icon getIcon(final File f) {
//        //        final String extension = Utils.getExtension(f);
//        final Icon icon = null;
//
//        //        if (extension != null) {
//        //            if (extension.equals(Utils.jpeg) ||
//        //                extension.equals(Utils.jpg)) {
//        //                //icon = jpgIcon;
//        //                icon = null;
//        //            }
//        //        }
//        return icon;
//    }
}
