package net.wordrider.core.actions;

import net.wordrider.core.Lng;
import net.wordrider.gui.LaF;
import net.wordrider.gui.LookAndFeels;
import net.wordrider.utilities.LogUtils;
import net.wordrider.utilities.Swinger;

import java.awt.event.ActionEvent;
import java.util.logging.Logger;

/**
 * @author Vity
 */
final class ChangeLookAndFeelAction extends CoreAction {
    private final static String CODE = "ChangeLookAndFeelAction";
    private final LaF laf;
    private final static Logger logger = Logger.getLogger(ChangeLookAndFeelAction.class.getName());

    public ChangeLookAndFeelAction(final LaF laf) {
        super(CODE, laf.getName(), (int) '\0', null);
        this.laf = laf;
    }

    public final void actionPerformed(final ActionEvent e) {
        boolean succesful;
        try {
            succesful = LookAndFeels.getInstance().loadLookAndFeel(laf, true);
            LookAndFeels.getInstance().storeSelectedLaF(laf);

        } catch (Exception ex) {
            LogUtils.processException(logger, ex);
            succesful = false;
        }
        if (succesful)
            Swinger.showInformationDialog(getMainFrame(), Lng.getLabel("ChangeLookAndFeelAction.set"));
        else
            Swinger.showErrorDialog(getMainFrame(), Lng.getLabel("ChangeLookAndFeelAction.failed"));
    }
}
