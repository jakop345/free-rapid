package cz.cvut.felk.erm.gui.plugintools;

import cz.cvut.felk.erm.gui.managers.PluginTool;
import cz.cvut.felk.erm.gui.managers.interfaces.IFileInstance;

import javax.swing.*;
import java.awt.*;
import java.util.logging.Logger;

/**
 * @author Vity
 */
public final class PluginTool2 extends PluginTool {
    private final static Logger logger = Logger.getLogger(NavigatorTool.class.getName());


    public PluginTool2() {
        super();
        final Container contentPane = getContentPane();
        contentPane.add(new JScrollPane(new JTable()), BorderLayout.CENTER);

        // Add a label above List to describe what is being shown

        getComponent().setPreferredSize(new Dimension(250, 300));
    }

    public final String getName() {
        return "PluginTool2";
    }

    public String getTabName() {
        return getName();
    }

    public final Icon getIcon() {
        return null;
    }

    public final String getTip() {
        return "Tooltip_2";
    }

    public void activate() {
        //setFileInstance(AreaManager.getInstance().getActiveInstance());
        logger.info("PluginTool1 activated");
    }

    public void deactivate() {
        setFileInstance(null);
        logger.info("PluginTool1 deactivated");
    }

    public void updateData() {
//        if (editor != null && listModel != null)
//            listModel.updateDataList();
    }

    public void setFileInstance(final IFileInstance instance) {
//        if (instance != null)
//            setEditor(instance.getRiderArea());
//        else
//            setEditor(null);
//        getContentPane().setEnabled(editor != null);
    }


    public boolean closeSoft() {
        return true;  //implement - call to super class
    }

    public void closeHard() {
        //implement - call to super class
    }
}