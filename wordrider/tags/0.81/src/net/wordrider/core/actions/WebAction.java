package net.wordrider.core.actions;

import net.wordrider.utilities.BrowserControl;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * @author Vity
 */
public class WebAction extends CoreAction{
    private String url;


    public WebAction(String url) {
        this.url = url;
    }

    protected WebAction(final String actionCode, final KeyStroke keyStroke, final String smallIcon, final String url) {
        super(actionCode, keyStroke, smallIcon);
        if (url == null)
            throw new IllegalArgumentException("URL argument cannot be null");
        this.url = url;
    }


    public void actionPerformed(ActionEvent e) {
        BrowserControl.showURL(url);
    }
}
