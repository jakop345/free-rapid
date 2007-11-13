package net.wordrider.area;

import net.wordrider.core.Lng;
import net.wordrider.files.ti68kformat.TIFileInfo;
import net.wordrider.files.ti68kformat.TIImageDecoder;
import net.wordrider.utilities.LogUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.image.ImageProducer;
import java.io.File;
import java.util.logging.Logger;

/**
 * @author Vity
 */
public final class AreaImage extends ImageIcon implements Cloneable {
    private final boolean isTiImage;
    private TIFileInfo info;
    private final static Logger logger = Logger.getLogger(AreaImage.class.getName());

    public AreaImage(final Image image) {
        super(image);    //call to super
        this.isTiImage = image.getSource() instanceof TIImageDecoder;

        if (isTiImage) {
            this.info = getImageDecoder().getFileInfo();
            updateDescription();
        } else {
            logger.warning("Not supported image!");
        }

    }

    public AreaImage(final Image image, final TIFileInfo info) {
        super(image);
        isTiImage = false;
        this.info = info;
        updateDescription();
    }

    private void updateDescription() {
        final String comment = (isTiImage) ? info.getComment() : Lng.getLabel("editor.areaImage.fnf");
        StringBuilder desc = new StringBuilder(60);
        desc.append("<html><b>").append(Lng.getLabel("editor.areaImage.folder")).append(" </b>").append(info.getFolderName()).append("<br><b>").append(Lng.getLabel("editor.areaImage.variable"));
        desc.append(" </b>").append(info.getVarName());
        if (comment.length() > 0)
            desc.append("<br><i>").append(comment).append(" </i>");
        desc.append("</html>");
        setDescription(desc.toString());
    }

    public final TIFileInfo getTIFileInfo() {
        return info;
    }

    public final void setTIFileInfo(final TIFileInfo info) {
        if (isTiImage) {
            getImageDecoder().setFileInfo(info);
        }
        this.info = info;
        updateDescription();
    }

    private TIImageDecoder getImageDecoder() {
        if (isTiImage) {
            final ImageProducer source = this.getImage().getSource();
            return (TIImageDecoder) source;
        } else return null;
    }

    public final String getImagePath() {
        final File file = getOriginalFile();
        if (file != null)
            return file.getParent();
        return "";
    }

    public final Object clone() {
        try {
            return super.clone();    //call to super
        } catch (CloneNotSupportedException e) {
            LogUtils.processException(logger, e);
            return null;
        }
    }

    public final File getOriginalFile() {
        if (isTiImage) {
            return getImageDecoder().getOriginalFile();
        }
        return null;
    }

    public final void setOriginalFile(final File file) {
        if (isTiImage) {
            getImageDecoder().setOriginalFile(file);
        }
    }

    public final String toString() {
        return "&P" + info.getFolderName() + "/" + info.getVarName();
    }
}
