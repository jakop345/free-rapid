package cz.vity.freerapid.gui.managers.interfaces;

import cz.vity.freerapid.model.bean.DownloadFile;

import java.util.EventListener;
import java.util.List;

/**
 * @author Vity
 */
public interface UrlListDataListener extends EventListener {
    public void linksAdded(List<DownloadFile> list);
}
