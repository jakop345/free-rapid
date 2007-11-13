package net.wordrider.core.actions;

import net.wordrider.core.Lng;
import net.wordrider.core.managers.SupportedLanguage;
import net.wordrider.utilities.Swinger;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * @author Vity
 */
public final class ChangeLanguageAction extends CoreAction {
    private final static String CODE = "ChangeLanguageAction";
    private final String languageCode;


    public ChangeLanguageAction(final SupportedLanguage lang) {
        super(CODE + lang.getLanguageCode(), lang.getName(), lang.getMnemonic(), lang.getIcon());    //call to super
        this.languageCode = lang.getLanguageCode();
    }


    public final void actionPerformed(final ActionEvent e) {
        if (!languageCode.equals(Lng.getSelectedLanguageCode())) {
            Lng.setSelectedLanguageCode(languageCode);
            Lng.reloadLangProperties();
            Swinger.showInformationDialog(getMainFrame(), Lng.getLabel("ChangeLanguageAction.set"));
        } else
            ((JMenuItem) e.getSource()).setSelected(true);
    }
}
