package cz.vity.freerapid.gui.managers.exceptions;

import cz.vity.freerapid.model.bean.PluginMetaData;

/**
 * @author Ladislav Vitasek
 */
public class PluginIsNotEnabledException extends NotSupportedDownloadServiceException {
    private PluginMetaData disabledPlugin = null;

    public PluginIsNotEnabledException(PluginMetaData disabledPlugin) {
        super("Plugin " + disabledPlugin.getId() + " is not enabled");
        this.disabledPlugin = disabledPlugin;
    }

    public PluginMetaData getDisabledPlugin() {
        return disabledPlugin;
    }
}
